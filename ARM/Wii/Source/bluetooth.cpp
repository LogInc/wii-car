#include "stdafx.h"
#include "uart.h"
#include "leds.h"
#include "bluetooth.h"


CBluetooth::CBluetooth()
{
    m_state = BLUETOOTH_UNINITIALIZED;
}


FUNCTION_STATUS CBluetooth::Initialize(USART_TypeDef* pUart, uint32_t baud)
{
    FUNCTION_STATUS status = m_uart.Initialize(baud, pUart);
    if (status != FUNCTION_SUCCESS)
	return status;
    
    m_uart.Enable_Interrupt(1);
    
    RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOD, ENABLE);
    GPIO_InitTypeDef gpio_init;
    gpio_init.GPIO_Mode = GPIO_Mode_OUT;
    gpio_init.GPIO_OType = GPIO_OType_PP;
    gpio_init.GPIO_PuPd = GPIO_PuPd_NOPULL;
    gpio_init.GPIO_Speed = GPIO_Speed_2MHz;
    gpio_init.GPIO_Pin = BLUETOOTH_PIN_KEY | BLUETOOTH_PIN_RESET;
    GPIO_Init(GPIOD, &gpio_init);
    
    AT_Mode(0);
    Reset_Mode(0);
    
    m_state = BLUETOOTH_INITIALIZED;
    
    return FUNCTION_SUCCESS;
}


void CBluetooth::Start()
{
    m_uart.Start();
}


void CBluetooth::Stop()
{
    m_uart.Stop();
}


void CBluetooth::AT_Mode(uint8_t at)
{
    GPIO_WriteBit(GPIOD, BLUETOOTH_PIN_KEY, at ? Bit_SET : Bit_RESET);
}


void CBluetooth::Reset_Mode(uint8_t reset)
{
    GPIO_WriteBit(GPIOD, BLUETOOTH_PIN_RESET, reset ? Bit_RESET : Bit_SET);
}


FUNCTION_STATUS CBluetooth::AT_Command(const char* command, char* out, uint8_t size)
{
    m_uart.Clear_Buffer();
    
    // Delay is necessary here to wait for the bluetooth module to get into
    // mode.
    AT_Mode(1);
    Delay(10);
    
    m_uart.Put_String(command);
    
    FUNCTION_STATUS status = m_uart.Get_String(out, size, '\n', BLUETOOTH_TIMEOUT);
    
    // Necessary here too for some reason.
    Delay(10);
    AT_Mode(0);
    
    m_uart.Clear_Buffer();
    
    return status;
}


FUNCTION_STATUS CBluetooth::Device_Available()
{
    char buffer[32];
    FUNCTION_STATUS status = AT_Command("AT\r\n", buffer, 32);
    
    if (status != FUNCTION_SUCCESS)
    {
	m_state = BLUETOOTH_NOTFOUND;
	return status;
    }
    
    if (strcmp("OK\r\n", buffer) == 0)
    {
	m_state = BLUETOOTH_PRESENT;
	return FUNCTION_SUCCESS;
    }
    else
    {
	m_state = BLUETOOTH_ERRORSTATE;
	return FUNCTION_FAIL_UNEXPECTED_RESULT;
    }
}


FUNCTION_STATUS	CBluetooth::Connected()
{
    char buffer[32];
    FUNCTION_STATUS status = AT_Command("AT+STATE?\r\n", buffer, 32);
    
    if (status != FUNCTION_SUCCESS)
    {
	m_state = BLUETOOTH_NOTFOUND;
	return status;
    }
    
    if (strcmp("+STATE:CONNECTED\r\n", buffer) == 0)
    {
	m_state = BLUETOOTH_CONNECTED;
	return FUNCTION_SUCCESS;
    }
    else
    {
    	m_state = BLUETOOTH_PRESENT;
	return FUNCTION_FAIL_ERROR;
    }
}


void CBluetooth::Clear_Buffer()
{
    m_uart.Clear_Buffer();
}


void CBluetooth::Transmit(char* buffer)
{
    m_uart.Put_String(buffer);
}


FUNCTION_STATUS	CBluetooth::Receive(char* buffer, uint8_t size, char terminator, uint32_t timeout)
{
    FUNCTION_STATUS status = m_uart.Get_String(buffer, size, terminator, timeout);
    if (status != FUNCTION_SUCCESS)
    {
	m_state = BLUETOOTH_ERRORSTATE;
    }
    return status;
}


FUNCTION_STATUS CBluetooth::Get_Char(char* buffer, uint32_t timeout)
{
    FUNCTION_STATUS status = m_uart.Get_Char(buffer, timeout);
    if (status != FUNCTION_SUCCESS)
    {
	m_state = BLUETOOTH_ERRORSTATE;
    }
    
    return status;
}


CUart* CBluetooth::GetUART()
{
    return &m_uart;
}


BLUETOOTH_STATE CBluetooth::GetState()
{
    return m_state;
}