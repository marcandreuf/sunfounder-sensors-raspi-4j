#include <wiringPi.h>
#include <stdio.h>

#define   SIG   0

void myISR(void)
{
	printf("led on !\n");		
}

int main(void)
{
	if(wiringPiSetup() == -1){
		printf("setup wiringPi failed !");
		return 1; 
	}

	if(wiringPiISR(SIG, INT_EDGE_FALLING, &myISR) == -1){
		printf("setup ISR failed !");
		return 1;	
	}

	while(1);

	return 0;
}
