#include <wiringPi.h>
#include <stdio.h>

#define FlamePin 0

void myISR(void)
{
	printf("Detected Flame !\n");
}

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !\n");
		return 1; 
	}

	if(wiringPiISR(FlamePin, INT_EDGE_FALLING, &myISR)){
		printf("setup interrupt failed !\n");	
		return 1; 
	}

	while(1){
		;
	}

	return 0;
}

