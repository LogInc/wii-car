#ifndef __WII_MOTOR_DRIVER_H__
#define __WII_MOTOR_DRIVER_H__

#define MOTOR_PIN_DRIVE_ENABLE	GPIO_Pin_0
#define MOTOR_PIN_TURN_ENABLE	GPIO_Pin_1
#define MOTOR_PIN_DRIVE_IN_0	GPIO_Pin_14
#define MOTOR_PIN_DRIVE_IN_1	GPIO_Pin_13
#define MOTOR_PIN_TURN_IN_0	GPIO_Pin_12
#define MOTOR_PIN_TURN_IN_1	GPIO_Pin_15
#define TIMER_RESET_VALUE	(999)
#define DRIVE_MOTOR_MAX		(799)

enum MOTOR_STATE
{
    MOTOR_STOP		= 0,
    MOTOR_FORWARD	= 1,
    MOTOR_REVERSE	= 2,
    MOTOR_LEFT		= 1,
    MOTOR_RIGHT		= 2,
    MOTOR_BRAKE		= 3,
};


class CMotorDriver
{
private:
    static MOTOR_STATE	drive_state;
    static MOTOR_STATE	turn_state;
    static int		drive_current_pulse;
    static int		turn_current_pulse;
    static int		drive_target_pulse;
    static int		turn_target_pulse;
    static int		drive_change_rate;
    static int		turn_change_rate;
    
public:
    static void Setup();
    static void Set_Drive_State(MOTOR_STATE state);
    static void Set_Turn_State(MOTOR_STATE state);
    static void Set_Drive_Pulse(int pulse);
    static void Set_Turn_Pulse(int pulse);
    static void Set_Drive_Change_Rate(int rate);
    static void Set_Turn_Change_Rate(int rate);
    static void Update();
    
public:
    static MOTOR_STATE Get_Drive_State();
    static MOTOR_STATE Get_Turn_State();
    
private:
    static void		Set_Motor_Direction(MOTOR_STATE state, uint16_t GPIO_0, uint16_t GPIO_1);
    static void 	Setup_GPIO();
    static void		Setup_Timer();
    static uint16_t	Get_Prescaler(uint32_t freq);
};

#endif