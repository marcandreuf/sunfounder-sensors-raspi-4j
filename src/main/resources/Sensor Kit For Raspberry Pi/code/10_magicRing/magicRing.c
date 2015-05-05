#include <wiringPi.h>
#include <stdio.h>

#define SwitchPin         0
#define LedPin            1

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}
	
	pinMode(SwitchPin, INPUT);
	pinMode(LedPin,  OUTPUT);

	while(1){
		if(digitalRead(SwitchPin) == LOW){
			printf("on !\n");
			digitalWrite(LedPin, HIGH);     //led on
		}	
		else{
			printf("off !\n");
			digitalWrite(LedPin, LOW);		
		}
	}

	return 0;
}

