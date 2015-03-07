#include "stdafx.h"
#include "motor_driver.h"


MOTOR_STATE	CMotorDriver::drive_state;
MOTOR_STATE	CMotorDriver::turn_state;
int		CMotorDriver::drive_current_pulse;
int		CMotorDriver::turn_current_pulse;
int		CMotorDriver::drive_target_pulse;
int		CMotorDriver::turn_target_pulse;
int		CMotorDriver::drive_change_rate;
int		CMotorDriver::turn_change_rate;


void CMotorDriver::Setup()
{
    drive_current_pulse = 0;
    turn_current_pulse = 0;
    drive_target_pulse = 0;
    turn_target_pulse = 0;
    drive_change_rate = 8;
    turn_change_rate = 80;
    
    Set_Drive_State(MOTOR_STOP);
    Set_Turn_State(MOTOR_STOP);
    Setup_GPIO();
    Setup_Timer();
}


void CMotorDriver::Setup_GPIO()
{
    // A0 and A1 pins are Timer 2's channel 1 and channel 2 output pins.
    RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOA, ENABLE);
    
    GPIO_InitTypeDef init;
    init.GPIO_Mode = GPIO_Mode_AF;
    init.GPIO_OType = GPIO_OType_PP;
    init.GPIO_Speed = GPIO_Speed_2MHz;
    init.GPIO_Pin = MOTOR_PIN_DRIVE_ENABLE | MOTOR_PIN_TURN_ENABLE;
    init.GPIO_PuPd = GPIO_PuPd_NOPULL;
    
    GPIO_PinAFConfig(GPIOA, GPIO_PinSource0, GPIO_AF_TIM2);
    GPIO_PinAFConfig(GPIOA, GPIO_PinSource1, GPIO_AF_TIM2);
    GPIO_Init(GPIOA, &init);
    
    
    RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOB, ENABLE);
    init.GPIO_Mode = GPIO_Mode_OUT;
    init.GPIO_OType = GPIO_OType_PP;
    init.GPIO_Pin = MOTOR_PIN_DRIVE_IN_0 | MOTOR_PIN_DRIVE_IN_1 | MOTOR_PIN_TURN_IN_0 | MOTOR_PIN_TURN_IN_1;
    init.GPIO_PuPd = GPIO_PuPd_NOPULL;
    init.GPIO_Speed = GPIO_Speed_2MHz;
    GPIO_Init(GPIOB, &init);
}


void CMotorDriver::Setup_Timer()
{
    RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM2, ENABLE);
    
    TIM_TimeBaseInitTypeDef base;
    base.TIM_ClockDivision = TIM_CKD_DIV1;
    base.TIM_Period = TIMER_RESET_VALUE;
    base.TIM_CounterMode = TIM_CounterMode_Up;
    base.TIM_Prescaler = Get_Prescaler(100000);
    TIM_TimeBaseInit(TIM2, &base);
    
    TIM_OCInitTypeDef oc;
    oc.TIM_OutputState = TIM_OutputState_Enable;
    oc.TIM_OCMode = TIM_OCMode_PWM1;
    oc.TIM_OCPolarity = TIM_OCPolarity_High;
    oc.TIM_Pulse = 0;
    
    TIM_OC1Init(TIM2, &oc);
    TIM_OC1PreloadConfig(TIM2, TIM_OCPreload_Enable);
    TIM_OC2Init(TIM2, &oc);
    TIM_OC2PreloadConfig(TIM2, TIM_OCPreload_Enable);
    
    TIM_GenerateEvent(TIM2, TIM_EventSource_Update);
    
    TIM_Cmd(TIM2, ENABLE);
    
    TIM_ITConfig(TIM2, TIM_IT_Update, ENABLE);
    NVIC_EnableIRQ(TIM2_IRQn);
    NVIC_SetPriority(TIM2_IRQn, 2);
}


uint16_t CMotorDriver::Get_Prescaler(uint32_t freq)
{
    return ((SystemCoreClock / freq) - 1);
}


void CMotorDriver::Set_Motor_Direction(MOTOR_STATE state, uint16_t GPIO_0, uint16_t GPIO_1)
{
    BitAction p0;
    BitAction p1;
    
    switch (state)
    {
    case MOTOR_STOP:
    case MOTOR_BRAKE:
	p0 = Bit_RESET;
	p1 = Bit_RESET;
	break;
	
    case MOTOR_FORWARD:
	p0 = Bit_SET;
	p1 = Bit_RESET;
	break;
	
    case MOTOR_REVERSE:
	p0 = Bit_RESET;
	p1 = Bit_SET;
	break;
    }
    
    GPIO_WriteBit(GPIOB, GPIO_0, p0);
    GPIO_WriteBit(GPIOB, GPIO_1, p1);
}


void CMotorDriver::Set_Drive_State(MOTOR_STATE state)
{
    Set_Motor_Direction(state, MOTOR_PIN_DRIVE_IN_0, MOTOR_PIN_DRIVE_IN_1);
    if (state == MOTOR_STOP)
    {
	drive_target_pulse = 0;
	drive_current_pulse = 0;
    }
    drive_state = state;
}


void CMotorDriver::Set_Turn_State(MOTOR_STATE state)
{
    Set_Motor_Direction(state, MOTOR_PIN_TURN_IN_0, MOTOR_PIN_TURN_IN_1);
    if (state == MOTOR_STOP)
    {
	turn_target_pulse = 0;
	turn_current_pulse = 0;
    }
    turn_state = state;
}


void CMotorDriver::Set_Drive_Pulse(int pulse)
{
    drive_target_pulse = pulse;
}


void CMotorDriver::Set_Turn_Pulse(int pulse)
{
    turn_current_pulse = pulse;
}

void CMotorDriver::Set_Drive_Change_Rate(int rate)
{
    drive_change_rate = rate;
}


void CMotorDriver::Set_Turn_Change_Rate(int rate)
{
    turn_change_rate = rate;
}


void CMotorDriver::Update()
{
    drive_current_pulse += drive_change_rate * (((drive_target_pulse - drive_current_pulse) > 0) ? 1 : -1);
    if (drive_current_pulse < 0)
	drive_current_pulse = 0;
    else if (drive_current_pulse > TIMER_RESET_VALUE)
	drive_current_pulse = TIMER_RESET_VALUE;
    
//    turn_current_pulse += turn_change_rate * (((turn_target_pulse - turn_current_pulse) > 0) ? 1 : -1);
//    if (turn_current_pulse < 0)
//	turn_current_pulse = 0;
//    else if (turn_current_pulse > TIMER_RESET_VALUE)
//	turn_current_pulse = TIMER_RESET_VALUE;
    
    TIM_SetCompare1(TIM2, (uint32_t)turn_current_pulse);
    TIM_SetCompare2(TIM2, (uint32_t)drive_target_pulse);
    
    if (drive_state == MOTOR_FORWARD || drive_state == MOTOR_REVERSE)
    {
	drive_target_pulse += drive_change_rate;
	if (drive_target_pulse > DRIVE_MOTOR_MAX)
	    drive_target_pulse = DRIVE_MOTOR_MAX;
    }
}


MOTOR_STATE CMotorDriver::Get_Drive_State()
{
    return drive_state;
}


MOTOR_STATE CMotorDriver::Get_Turn_State()
{
    return turn_state;
}