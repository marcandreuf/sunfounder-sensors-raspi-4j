#include <wiringPi.h>
#include <stdio.h>

#define LightBreakPin     0
#define LedPin            1

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}
//	printf("linker LedPin : GPIO %d(wiringPi pin)\n",VoicePin); //when initialize wiring successfully,print message to screen
	
	pinMode(LightBreakPin, INPUT);
	pinMode(LedPin,  OUTPUT);

	while(1){
		if(digitalRead(LightBreakPin) == HIGH){
			printf("led on !\n");
			digitalWrite(LedPin, HIGH);     //led on
		}	
		else{
			printf("led off !\n");
			digitalWrite(LedPin, LOW);		
		}
	}

	return 0;
}

