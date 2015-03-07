#include "stm32f4xx.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_tim.h"
#include "stm32f4xx_usart.h"
#include "stdlib.h"
#include "string.h"


enum FUNCTION_STATUS
{
    FUNCTION_SUCCESS,
    FUNCTION_FAIL_ERROR,
    FUNCTION_FAIL_UNEXPECTED_RESULT,
    FUNCTION_FAIL_TIMEOUT,
};

#ifdef __cplusplus
extern "C"
{
#endif
    
	void		Delay(uint32_t ms);
	uint32_t	Get_System_Counter();

#ifdef __cplusplus
}
#endif