#include <wiringPi.h>
#include <stdio.h>

#define ObstaclePin      0

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !\n");
		return 1; 
	}

	pinMode(ObstaclePin, INPUT);

	while(1){
		if(digitalRead(ObstaclePin) == 0){
			delay(25);
		    if(digitalRead(ObstaclePin) == 0){
				printf("Detected Barrier !\n");
			}
		}
	}

	return 0;
}

