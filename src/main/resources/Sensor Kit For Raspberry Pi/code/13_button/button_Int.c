#include <wiringPi.h>
#include <stdio.h>

#define BtnPin    0
#define LedPin    1

void myBtnISR(void)
{
	digitalWrite(LedPin, !digitalRead(LedPin));
	printf("Button is pressed\n");	
}

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}
	
	if(wiringPiISR(BtnPin, INT_EDGE_FALLING, myBtnISR)){
		printf("setup ISR failed !");
		return 1;
	}
	
	pinMode(LedPin, OUTPUT);

	while(1);
	
	return 0;
}
