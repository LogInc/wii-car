#ifndef __WII_BLUETOOTH_H__
#define __WII_BLUETOOTH_H__

#include "uart.h"

#define BLUETOOTH_PIN_KEY	(GPIO_Pin_10)
#define BLUETOOTH_PIN_RESET	(GPIO_Pin_9)

#define BLUETOOTH_TIMEOUT	(500)

enum BLUETOOTH_STATE
{
    BLUETOOTH_UNINITIALIZED,
    BLUETOOTH_INITIALIZED,
    BLUETOOTH_PRESENT,
    BLUETOOTH_NOTFOUND,
    BLUETOOTH_ERRORSTATE,
    BLUETOOTH_CONNECTED,
};

class CBluetooth
{
private:
    CUart		m_uart;
    BLUETOOTH_STATE	m_state;
    
public:
    FUNCTION_STATUS	Initialize(USART_TypeDef* pUart, uint32_t baud);
    void		Start();
    void		Stop();
    void		AT_Mode(uint8_t at);
    void		Reset_Mode(uint8_t reset);
    
public:
    FUNCTION_STATUS	AT_Command(const char* command, char* out, uint8_t size);
    FUNCTION_STATUS	Device_Available();
    FUNCTION_STATUS	Connected();
    void		Clear_Buffer();
    void		Transmit(char* buffer);
    FUNCTION_STATUS	Receive(char* buffer, uint8_t size, char terminator='\0', uint32_t timeout=-1);
    FUNCTION_STATUS	Get_Char(char* buffer, uint32_t timeout=-1);
    
public:
    CUart*		GetUART();
    BLUETOOTH_STATE	GetState();
    
public:
    CBluetooth();
};

#endif