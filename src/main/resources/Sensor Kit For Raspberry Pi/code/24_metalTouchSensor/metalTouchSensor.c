#include <wiringPi.h>
#include <stdio.h>

#define TouchPin    0
#define LedPin      1

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}
	
	pinMode(TouchPin, INPUT);
	pinMode(LedPin,  OUTPUT);

	while(1){
		if(digitalRead(TouchPin) == HIGH){
			printf("touched\n");
			digitalWrite(LedPin, LOW);     //led on
			delay(100);
			digitalWrite(LedPin, HIGH);    //led off
		}	
	}

	return 0;
}

