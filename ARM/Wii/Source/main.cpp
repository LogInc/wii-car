#include "stdafx.h"
#include "uart.h"
#include "leds.h"
#include "bluetooth.h"
#include "motor_driver.h"
#include "main.h"


CUart* g_pUart2 = 0;
CUart* g_pUart3 = 0;

CBluetooth bluetooth;

CUart uart;

//int main()
//{
//	//Configure_SystemTimer();
//	Configure_LEDs();
//	uart.Initialize(9600, USART2);
//	uart.Enable_Interrupt(4);
//	uart.Start();
//	g_pUart2 = &uart;
//	
//	char buffer[64];
//	while (1)
//	{
//		if (uart.Get_String(buffer, 64, '\n', -1) == FUNCTION_SUCCESS)
//		{
//			uart.Put_String(buffer);
//		}
//	}
//}



int main()
{
    Configure_SystemTimer();
    Configure_LEDs();
    Led(LED_HEADLIGHTS, 1);
    CMotorDriver::Setup();
    
    //Led(LED_RUNNING, 1);
    
    Bluetooth_Machine();
    
    
    while (1)
    {
		char buffer[32];
		//char c;
		if (bluetooth.Receive(buffer, 10, '\n', -1) != FUNCTION_SUCCESS)
		//if (bluetooth.Get_Char(&c, -1) != FUNCTION_SUCCESS)
		{
			CMotorDriver::Set_Drive_State(MOTOR_STOP);
			CMotorDriver::Set_Turn_State(MOTOR_STOP);
			Led(LED_BLUETOOTH_CONNECTED, 0);
			Led(LED_BLUETOOTH_AVAILABLE, 0);
			Bluetooth_Machine();
		}
		else
		{
			//bluetooth.Clear_Buffer();
			Update(buffer);
			//Led_Toggle(LED_RUNNING);
			bluetooth.Transmit(buffer);
		}
    }
}


static void Configure_SystemTimer()
{
    // Generates interrupt every 1 ms.
    SysTick_CLKSourceConfig(SysTick_CLKSource_HCLK);
    SysTick_Config(SystemCoreClock/1000);
	NVIC_SetPriority(SysTick_IRQn, 0);
}


static void Configure_Bluetooth()
{
    g_pUart2 = 0;
    
    FUNCTION_STATUS status = bluetooth.Initialize(USART2, 9600);
    Error_Indicator(status);
    if (status != FUNCTION_SUCCESS)
    {
		return;
    }
    
    g_pUart2 = bluetooth.GetUART();
    bluetooth.Start();
}


static void Verify_Bluetooth_Presence()
{
    //bluetooth.Reset_Mode(1);
    //Delay(BLUETOOTH_RESET_COOLDOWN);
    //bluetooth.Reset_Mode(0);
    
    Delay(BLUETOOTH_BOOT_COOLDOWN);
    
    FUNCTION_STATUS status = bluetooth.Device_Available();
    Error_Indicator(status);
}


static void Bluetooth_Machine()
{
    while (1)
    {
		FUNCTION_STATUS status;
		BLUETOOTH_STATE state = bluetooth.GetState();
		switch (state)
		{
		case BLUETOOTH_UNINITIALIZED:
			Led(LED_BLUETOOTH_AVAILABLE, 0);
			Led(LED_BLUETOOTH_CONNECTED, 0);
			Configure_Bluetooth();
			break;
			
		case BLUETOOTH_INITIALIZED:
		case BLUETOOTH_NOTFOUND:
		case BLUETOOTH_ERRORSTATE:
			Led(LED_BLUETOOTH_AVAILABLE, 0);
			Led(LED_BLUETOOTH_CONNECTED, 0);
			Verify_Bluetooth_Presence();
			break;
			
		case BLUETOOTH_PRESENT:
			status = bluetooth.Connected();
			Error_Indicator(status);
			if (status != FUNCTION_SUCCESS)
			{
				Led(LED_BLUETOOTH_AVAILABLE, 1);
				Led(LED_BLUETOOTH_CONNECTED, 0);
			}
			break;
			
		case BLUETOOTH_CONNECTED:
			Led(LED_BLUETOOTH_AVAILABLE, 0);
			Led(LED_BLUETOOTH_CONNECTED, 1);
			return;
		}
		Delay(500);
    }
}


static void Update(char state[])
{
	/*
    char c = *state;
    
    switch (c)
    {
    case 'A':
		CMotorDriver::Set_Drive_State(MOTOR_FORWARD);
		break;
		
    case 'B':
		CMotorDriver::Set_Drive_State(MOTOR_REVERSE);
		break;
		
    case 'C':
		CMotorDriver::Set_Drive_State(MOTOR_STOP);
		break;
		
    case 'D':
		CMotorDriver::Set_Turn_State(MOTOR_LEFT);
		CMotorDriver::Set_Turn_Pulse(500);
		break;
		
    case 'E':
		CMotorDriver::Set_Turn_State(MOTOR_LEFT);
		CMotorDriver::Set_Turn_Pulse(999);
		break;
		
    case 'F':
		CMotorDriver::Set_Turn_State(MOTOR_RIGHT);
		CMotorDriver::Set_Turn_Pulse(500);
		break;
		
    case 'G':
		CMotorDriver::Set_Turn_State(MOTOR_RIGHT);
		CMotorDriver::Set_Turn_Pulse(999);
		break;
		
    case 'H':
		CMotorDriver::Set_Turn_State(MOTOR_STOP);
		//CMotorDriver::Set_Turn_Pulse(999);
		break;
		
    case 'I':
		Led(LED_HEADLIGHTS, 1);
		break;
		
    case 'J':
		Led(LED_HEADLIGHTS, 0);
		break;
    }
	*/
    
	MOTOR_STATE drive = Make_Motor_State(state[STATE_DRIVE_INDEX]);
	MOTOR_STATE turn = Make_Motor_State(state[STATE_TURN_INDEX]);
	if (CMotorDriver::Get_Drive_State() != drive)
		CMotorDriver::Set_Drive_State(drive);
	
	if (CMotorDriver::Get_Turn_State() != turn)
	 CMotorDriver::Set_Turn_State(Make_Motor_State(state[STATE_TURN_INDEX]));
	
	CMotorDriver::Set_Turn_Pulse((999 * (state[STATE_TURN_MAGNITUDE_INDEX] - '0')) / NUM_TURN_MOTOR_STATES);
	Led(LED_HEADLIGHTS, 1 - (state[STATE_HEADLIGHTS_INDEX] - '0'));
}


static MOTOR_STATE Make_Motor_State(char state)
{
    switch (state)
    {
    case MOTOR_CMD_FORWARD:
		return MOTOR_FORWARD;
		
    case MOTOR_CMD_REVERSE:
		return MOTOR_REVERSE;
		
    case MOTOR_CMD_STOP:
		return MOTOR_STOP;
		
    case MOTOR_CMD_BRAKE:
		return MOTOR_BRAKE;
		
    case MOTOR_CMD_LEFT:
		return MOTOR_LEFT;
		
    case MOTOR_CMD_RIGHT:
		return MOTOR_RIGHT;
		
    default:
		return MOTOR_STOP;
    }
}


static void Error_Indicator(FUNCTION_STATUS status)
{
    Led(LED_ERROR, 0);
    switch (status)
    {	
    case FUNCTION_FAIL_ERROR:
		Led(LED_ERROR, 1);
		break;
		
    case FUNCTION_FAIL_UNEXPECTED_RESULT:
		Led_Toggle(LED_ERROR);
		Delay(LED_ERROR_BLINK_RATE);
		Led_Toggle(LED_ERROR);
		Delay(LED_ERROR_BLINK_RATE);
		Led_Toggle(LED_ERROR);
		break;
		
    case FUNCTION_FAIL_TIMEOUT:
		for (int i=0; i<5; i++)
		{
			Led_Toggle(LED_ERROR);
			Delay(LED_ERROR_BLINK_RATE);
		}
    }
}