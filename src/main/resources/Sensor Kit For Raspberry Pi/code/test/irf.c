#include <wiringPi.h>
#include <stdio.h>

#define LedPin    0
#define Irf       1

int cnt;

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}

	pinMode(Irf, INPUT);
	pinMode(LedPin, OUTPUT);

	while(1){
		if(0 == digitalRead(Irf)){
			delay(16);
			if(0 == digitalRead(Irf)){
				while(!digitalRead(Irf));
				digitalWrite(LedPin, !digitalRead(LedPin));
				cnt ++;
				printf("Button is pressed, cnt = %d\n", cnt);	
			}
		}
	}

	return 0;
}
