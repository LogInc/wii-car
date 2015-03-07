#include "stdafx.h"
#include "uart.h"
#include "motor_driver.h"

extern CUart* g_pUart2;
extern CUart* g_pUart3;

static uint32_t timer = 0;
uint32_t	counter = 0;

#ifdef __cplusplus
extern "C"
{
#endif
    
    void SysTick_Handler()
    {
	counter++;
	if (timer)
	    timer--;
    }
    
    
    void USART2_IRQHandler()
    {
	if (g_pUart2)
	    g_pUart2->Handle_Interrupt();
    }
    
    
    void USART3_IRQHandler()
    {
	if (g_pUart3)
	    g_pUart3->Handle_Interrupt();
    }
    
    
    void TIM2_IRQHandler()
    {
	TIM_ClearITPendingBit(TIM2, TIM_IT_Update);
	CMotorDriver::Update();
    }
    
    
    void Delay(uint32_t ms)
    {
	timer = ms;
	while (timer);
    }
    
    uint32_t Get_System_Counter()
    {
	return counter;
    }
    
#ifdef __cplusplus
}
#endif