#include <wiringPi.h>
#include <stdio.h>

#define   SIG   0
#define   LED   1

int main(void)
{
	if(wiringPiSetup() == -1){
		printf("setup wiringPi failed !");
		return 1; 
	}
	
	pinMode(SIG, INPUT);
	pinMode(LED, OUTPUT);

	while(1){
		if(0 == digitalRead(SIG)){
			digitalWrite(LED, 1);	
		}		
		else{
			digitalWrite(LED, 0);	
		}
	}

	return 0;
}
