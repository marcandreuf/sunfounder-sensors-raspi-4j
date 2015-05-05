#include <wiringPi.h>
#include <stdio.h>

#define IrPin    0

int main(void)
{
	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}

	pinMode(IrPin, OUTPUT);

	while(1){
		digitalWrite(IrPin, HIGH);
		delay(500);
		digitalWrite(IrPin, LOW);
		delay(500);
	}

	return 0;
}
