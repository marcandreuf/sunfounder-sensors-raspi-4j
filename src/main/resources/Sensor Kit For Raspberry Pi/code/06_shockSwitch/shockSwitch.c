#include <wiringPi.h>
#include <stdio.h>

#define ShockPin      0
#define LedPin        1

int main(void)
{
	int count;

	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}
	
	pinMode(ShockPin,  INPUT);
	pinMode(LedPin,   OUTPUT);

	while(1){
		if(digitalRead(ShockPin) == LOW){
			delay(10);
			if(digitalRead(ShockPin) == LOW){
				count++;
				printf("Detected shaking ! count = %d\n", count);
				digitalWrite(LedPin, !digitalRead(LedPin));			
			}
		}	
	}

	return 0;
}

