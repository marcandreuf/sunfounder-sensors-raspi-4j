#include <wiringPi.h>
#include <stdio.h>

#define KnockPin      0
#define LedPin        1

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}
	
	pinMode(KnockPin, INPUT);
	pinMode(LedPin,  OUTPUT);

	while(1){
		if(digitalRead(KnockPin) == LOW){
			printf("Detected knocking!\n");
			//digitalWrite(LedPin, LOW);     //led on
			digitalWrite(LedPin, !digitalRead(LedPin));     //led on
		}
	}

	return 0;
}

