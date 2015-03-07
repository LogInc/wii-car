#ifndef __WII_MAIN_H__
#define __WII_MAIN_H__

#define LED_RUNNING_BLINK_RATE		(1000)
#define LED_ERROR_BLINK_RATE		(500)
#define BLUETOOTH_RESET_COOLDOWN	(100)
#define BLUETOOTH_BOOT_COOLDOWN		(1000)

#define STATE_DRIVE_INDEX		0
#define STATE_TURN_INDEX		1
#define	STATE_TURN_MAGNITUDE_INDEX	2
#define	STATE_HEADLIGHTS_INDEX		3

#define MOTOR_CMD_FORWARD	'F'
#define MOTOR_CMD_REVERSE	'R'
#define MOTOR_CMD_STOP		'S'
#define MOTOR_CMD_BRAKE		'B'
#define MOTOR_CMD_LEFT		'L'
#define MOTOR_CMD_RIGHT		'W'

#define NUM_TURN_MOTOR_STATES	2


static void		Configure_SystemTimer();
static void		Configure_Bluetooth();
static void		Verify_Bluetooth_Presence();
static void		Bluetooth_Machine();
static void		Update(char state[]);
static MOTOR_STATE	Make_Motor_State(char state);
static void		Error_Indicator(FUNCTION_STATUS status);

#endif