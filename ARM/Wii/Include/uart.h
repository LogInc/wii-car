#ifndef __WII_UART_H__
#define __WII_UART_H__

#define BUFFER_SIZE		(1024)
#define BUFFER_SIZE_MASK	(BUFFER_SIZE - 1)

class CUart
{
private:
    USART_TypeDef*	m_pUart;
    // During RX, ready indicates when we have a character ready to be read
    // from the circular buffer.
    //uint8_t		m_iReady;
    
    // Circular buffer to store the received data before it is read by
    // the user.
    char		m_cBuffer[BUFFER_SIZE];
    uint8_t		m_iBufferNext;
    uint8_t		m_iBufferStart;
    uint8_t		m_iBufferEnd;
    
public:
    FUNCTION_STATUS		Initialize(uint32_t baud, USART_TypeDef* pUart);
    void				Enable_Interrupt(uint8_t it_priority);
    void				Start();
    void				Stop();
    
public:
    void				Clear_Buffer();
    void				Put_Char(const char c);
    void				Put_String(const char* s);
    FUNCTION_STATUS		Get_Char(char* out, uint32_t timeout=-1);
    FUNCTION_STATUS		Get_String(char* buffer, uint8_t size, char terminator='\0', uint32_t timeout=-1);
    
public:
    void				Handle_Interrupt();
};

#endif