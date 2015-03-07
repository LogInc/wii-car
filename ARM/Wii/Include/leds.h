#ifndef __WII_LED_H__
#define __WII_LED_H__


#define	LED_RUNNING			0
#define LED_ERROR			1
#define LED_BLUETOOTH_AVAILABLE		2
#define LED_BLUETOOTH_CONNECTED		3
#define LED_HEADLIGHTS			4


void Configure_LEDs();
void Led(uint8_t i, uint8_t state);
void Led_Toggle(uint8_t i);

#endif