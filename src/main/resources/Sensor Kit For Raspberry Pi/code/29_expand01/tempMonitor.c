#include <wiringPi.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <stdlib.h>
#include <stdio.h>

#define     LedRed    0
#define     LedGreen  1
#define     LedBlue   2

#define       Beep    3

#define     BUFSIZE   128

typedef unsigned char uchar;
typedef unsigned int  uint;

void beepInit(void)
{
	pinMode(Beep, OUTPUT);	
}

void beepCtrl(int t)
{
	digitalWrite(Beep, LOW);
	delay(t);
	digitalWrite(Beep, HIGH);	
	delay(t);
}

float tempRead(void)
{
	float temp;
	int i, j;
    int fd;
	int ret;

	char buf[BUFSIZE];
	char tempBuf[5];
	
	fd = open("/sys/bus/w1/devices/28-00000495db35/w1_slave", O_RDONLY);

	if(-1 == fd){
		perror("open device file error");
		return 1;
	}

	while(1){
		ret = read(fd, buf, BUFSIZE);
		if(0 == ret){
			break;	
		}
		if(-1 == ret){
			if(errno == EINTR){
				continue;	
			}
			perror("read()");
			close(fd);
			return 1;
		}
	}

	for(i=0;i<sizeof(buf);i++){
		if(buf[i] == 't'){
			for(j=0;j<sizeof(tempBuf);j++){
				tempBuf[j] = buf[i+2+j]; 	
			}
		}	
	}

	temp = (float)atoi(tempBuf) / 1000;

	close(fd);

	return temp;
}

void ledInit(void)
{
	pinMode(LedRed,   OUTPUT);	
	pinMode(LedGreen, OUTPUT);	
	pinMode(LedBlue,  OUTPUT);	
}

/* */
void ledCtrl(int n, int state)
{
	digitalWrite(n, state);
}

int main(int argc, char *argv[])
{
	int i;
	int low, high;
	float temp;

	if(argc != 3){
		printf("Usage : ./a.out [temperature lower limit] [upper limit]\n");
		printf("For example : ./a.out 29 31\n");
		return 0;	
	}

	low = atoi(argv[1]);
	high = atoi(argv[2]);

	if(low >= high){
		printf("Parameters error, lower limit should be less than upper limit\n");
		return 0;
	}

	if(wiringPiSetup() == -1){ //when initialize wiring failed,print messageto screen
		printf("setup wiringPi failed !");
		return 1; 
	}
	
	ledInit();
	beepInit();


	while(1){
		temp = tempRead();
		printf("The lower limit of temperature : %d\n", low);
		printf("The upper limit of temperature : %d\n", high);
		printf("Current temperature : %0.3f\n", temp);
		if(temp < low){
			ledCtrl(LedBlue, HIGH);
			ledCtrl(LedRed, LOW);
			ledCtrl(LedGreen, LOW);
			for(i = 0;i < 3; i++){
				beepCtrl(500);
			}
		}
		if(temp >= low && temp < high){
			ledCtrl(LedBlue, LOW);
			ledCtrl(LedRed, LOW);
			ledCtrl(LedGreen, HIGH);
		}
		if(temp >= high){
			ledCtrl(LedBlue, LOW);
			ledCtrl(LedRed, HIGH);
			ledCtrl(LedGreen, LOW);
			for(i = 0;i < 3; i++){
				beepCtrl(100);
			}
		}
	}	

	return 0;
}
