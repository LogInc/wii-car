#include "stdafx.h"
#include "uart.h"


FUNCTION_STATUS CUart::Initialize(uint32_t baud, USART_TypeDef* pUart)
{
    // Common GPIO parameters.
    GPIO_InitTypeDef gpio_init;
    gpio_init.GPIO_Mode = GPIO_Mode_AF;
    gpio_init.GPIO_OType = GPIO_OType_PP;
    gpio_init.GPIO_PuPd = GPIO_PuPd_NOPULL;
    gpio_init.GPIO_Speed = GPIO_Speed_2MHz;
    
    if (pUart == USART2)
    {
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOA, ENABLE);
	
	gpio_init.GPIO_Pin = GPIO_Pin_2 | GPIO_Pin_3;
	GPIO_PinAFConfig(GPIOA, GPIO_PinSource2, GPIO_AF_USART2);
	GPIO_PinAFConfig(GPIOA, GPIO_PinSource3, GPIO_AF_USART2);
	GPIO_Init(GPIOA, &gpio_init);
    }
    else if (pUart == USART3)
    {
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOD, ENABLE);
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOB, ENABLE);
	
	GPIO_PinAFConfig(GPIOD, GPIO_PinSource8, GPIO_AF_USART3);
	GPIO_PinAFConfig(GPIOB, GPIO_PinSource11, GPIO_AF_USART3);
	gpio_init.GPIO_Pin = GPIO_Pin_8;
	GPIO_Init(GPIOD, &gpio_init);
	gpio_init.GPIO_Pin = GPIO_Pin_11;
	GPIO_Init(GPIOB, &gpio_init);
    }
    else
    {
	return FUNCTION_FAIL_ERROR;
    }
    
    m_pUart = pUart;
    
    RCC_APB1PeriphClockCmd(RCC_APB1Periph_USART2, ENABLE);
    USART_InitTypeDef uart_init;
    uart_init.USART_BaudRate = baud;
    uart_init.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
    uart_init.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
    uart_init.USART_Parity = USART_Parity_No;
    uart_init.USART_StopBits = USART_StopBits_1;
    uart_init.USART_WordLength = USART_WordLength_8b;
    
    USART_Init(m_pUart, &uart_init);
    
   // m_iReady = 1;
    m_iBufferStart = 0;
    m_iBufferNext = 0;
    m_iBufferEnd = 0;
    
    return FUNCTION_SUCCESS;
}


void CUart::Enable_Interrupt(uint8_t it_priority)
{
    USART_ITConfig(m_pUart, USART_IT_RXNE, ENABLE);
    USART_ITConfig(m_pUart, USART_IT_TC, ENABLE);
    USART_ClearITPendingBit(m_pUart, USART_IT_TC);
    
    if (m_pUart == USART2)
    {
	NVIC_EnableIRQ(USART2_IRQn);
	NVIC_SetPriority(USART2_IRQn, it_priority);
    }
    else
    {
	NVIC_EnableIRQ(USART3_IRQn);
	NVIC_SetPriority(USART3_IRQn, it_priority);
    }
}


void CUart::Start()
{
    USART_Cmd(m_pUart, ENABLE);
}


void CUart::Stop()
{
    USART_Cmd(m_pUart, DISABLE);
}


void CUart::Clear_Buffer()
{
    m_iBufferNext = 0;
    m_iBufferStart = 0;
    m_iBufferEnd = 0;
}


void CUart::Put_Char(const char c)
{
    //while (!m_iReady);
    //m_iReady = 0;
    
    while (!USART_GetFlagStatus(m_pUart, USART_FLAG_TXE));
    USART_SendData(m_pUart, c);
}


void CUart::Put_String(const char* s)
{
    while (*s)
    {
	Put_Char(*(s++));
    }
}


FUNCTION_STATUS CUart::Get_Char(char* out, uint32_t timeout)
{
    uint32_t ticks = Get_System_Counter();
    
    // Wait till we have a character to read.
//    while (!m_iReady)
//    {
//	if ((Get_System_Counter() - ticks) >= timeout)
//	    return FUNCTION_FAIL_TIMEOUT;
//    }
//    
//    m_iReady = 0;
//    
    ticks = Get_System_Counter();
    while ((m_iBufferEnd - m_iBufferStart) == 0)
    {
	if ((Get_System_Counter() - ticks) >= timeout)
	{
	    //m_iReady = 1;
	    return FUNCTION_FAIL_TIMEOUT;
	}
    }
    
    // Read the character from the circular buffer.
    *out = m_cBuffer[m_iBufferStart];
    m_iBufferStart++;
    m_iBufferStart &= BUFFER_SIZE_MASK;
    
    return FUNCTION_SUCCESS;
}


FUNCTION_STATUS CUart::Get_String(char* buffer, uint8_t size, char terminator, uint32_t timeout)
{
    char c;
    uint8_t i = 0;
    
    // We need to store the null terminator as well.
    size--;
    do
    {
	FUNCTION_STATUS status = Get_Char(&c, timeout);
	if (status != FUNCTION_SUCCESS)
	    return status;
	
	buffer[i++] = c;
    }
    while ((c != terminator) && (i < size));
    buffer[i] = '\0';
    
    return FUNCTION_SUCCESS;
}


void CUart::Handle_Interrupt()
{
    // Transmission Complete.
    if (USART_GetITStatus(m_pUart, USART_IT_TC))
    {
	USART_ClearITPendingBit(m_pUart, USART_IT_TC);
	//m_iReady = 1;
    }
    // Receive buffer not empty.
    // The received byte should be stored immediately since it will be
    // overwritten when the next byte arrives.
    if (USART_GetITStatus(m_pUart, USART_IT_RXNE))
    {
	// Read the byte from the receive buffer.
	m_cBuffer[m_iBufferNext] = (char)USART_ReceiveData(m_pUart);
	
	// The byte is stored in a circular buffer.
	m_iBufferNext++;
	m_iBufferNext &= BUFFER_SIZE_MASK;
	m_iBufferEnd++;
	m_iBufferEnd &= BUFFER_SIZE_MASK;
	
	USART_ClearITPendingBit(m_pUart, USART_IT_RXNE);
	// The character is now ready to be read.
	//m_iReady = 1;
    }
}