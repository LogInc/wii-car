#include "stdafx.h"
#include "leds.h"


void Configure_LEDs()
{
    RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOD, ENABLE);
    
    GPIO_InitTypeDef init;
    init.GPIO_Mode = GPIO_Mode_OUT;
    init.GPIO_OType = GPIO_OType_PP;
    init.GPIO_Pin = GPIO_Pin_11 | GPIO_Pin_12 | GPIO_Pin_13 | GPIO_Pin_14 | GPIO_Pin_15;
    init.GPIO_Speed = GPIO_Speed_2MHz;
    GPIO_Init(GPIOD, &init);
}


void Led(uint8_t i, uint8_t state)
{
    uint32_t pin;
    
    switch (i)
    {
    case LED_RUNNING:
	pin = GPIO_Pin_12;
	break;
	
    case LED_ERROR:
	pin = GPIO_Pin_13;
	break;
	
    case LED_BLUETOOTH_AVAILABLE:
	pin = GPIO_Pin_14;
	break;
	
    case LED_BLUETOOTH_CONNECTED:
	pin = GPIO_Pin_15;
	break;
	
    case LED_HEADLIGHTS:
	pin = GPIO_Pin_11;
	break;
	
    default:
	return;
    }
    
    GPIO_WriteBit(GPIOD, pin, state ? Bit_SET : Bit_RESET);
}

void Led_Toggle(uint8_t i)
{
    uint32_t pin;
    
    switch (i)
    {
    case LED_RUNNING:
	pin = GPIO_Pin_12;
	break;
	
    case LED_ERROR:
	pin = GPIO_Pin_13;
	break;
	
    case LED_BLUETOOTH_AVAILABLE:
	pin = GPIO_Pin_14;
	break;
	
    case LED_BLUETOOTH_CONNECTED:
	pin = GPIO_Pin_15;
	break;
	
    case LED_HEADLIGHTS:
	pin = GPIO_Pin_11;
	break;
    default:
	return;
    }
    
    GPIO_ToggleBits(GPIOD, pin);
}