#include <wiringPi.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <stdlib.h>
#include <stdio.h>

#define     LedRed    3
#define     LedGreen  4
#define     LedBlue   5

#define     ADC_CS    0
#define     ADC_CLK   1
#define     ADC_DIO   2

#define  JoyStick_Z   6

#define        Beep   8

#define     BUFSIZE   128

typedef unsigned char uchar;
typedef unsigned int  uint;

static int sys_state = 1; 

void beepInit(void)
{
	pinMode(Beep, OUTPUT);	
}

void beep_on(void)
{
	digitalWrite(Beep, LOW);	
}

void beep_off(void)
{	
	digitalWrite(Beep, HIGH);	
}

void beepCtrl(int t)
{
	beep_on();
	delay(t);
	beep_off();
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

uchar get_ADC_Result(uchar xyVal)
{
	//10:CH0
	//11:CH1
	uchar i;
	uchar dat1=0, dat2=0;

	digitalWrite(ADC_CS, 0);

	digitalWrite(ADC_CLK,0);
	digitalWrite(ADC_DIO,1);	delayMicroseconds(2);
	digitalWrite(ADC_CLK,1);	delayMicroseconds(2);
	digitalWrite(ADC_CLK,0);

	digitalWrite(ADC_DIO,1);    delayMicroseconds(2); //CH0 10
	digitalWrite(ADC_CLK,1);	delayMicroseconds(2);
	digitalWrite(ADC_CLK,0);

	if(xyVal == 'x'){
		digitalWrite(ADC_DIO,0);	delayMicroseconds(2); //CH0 0
	}
	if(xyVal == 'y'){
		digitalWrite(ADC_DIO,1);	delayMicroseconds(2); //CH1 1
	}
	digitalWrite(ADC_CLK,1);	
	digitalWrite(ADC_DIO,1);    delayMicroseconds(2);
	digitalWrite(ADC_CLK,0);	
	digitalWrite(ADC_DIO,1);    delayMicroseconds(2);
	
	for(i=0;i<8;i++)
	{
		digitalWrite(ADC_CLK,1);	delayMicroseconds(2);
		digitalWrite(ADC_CLK,0);    delayMicroseconds(2);

		pinMode(ADC_DIO, INPUT);
		dat1=dat1<<1 | digitalRead(ADC_DIO);
	}
	
	for(i=0;i<8;i++)
	{
		dat2 = dat2 | ((uchar)(digitalRead(ADC_DIO))<<i);
		digitalWrite(ADC_CLK,1); 	delayMicroseconds(2);
		digitalWrite(ADC_CLK,0);    delayMicroseconds(2);
	}

	digitalWrite(ADC_CS,1);

	pinMode(ADC_DIO, OUTPUT);
	
	return(dat1==dat2) ? dat1 : 0;
}

void joyStick_z_ISR(void)
{
	sys_state = 0;
	printf("interrupt occur !\n");
}

uchar get_joyStick_state(void)
{
	uchar tmp = 0;

	uchar xVal = 0, yVal = 0, zVal = 0;
	
	pinMode(ADC_DIO, OUTPUT);

	xVal = get_ADC_Result('x');
	if(xVal == 255){
		tmp = 1;
	}
	if(xVal == 0){
		tmp = 2;
	}

	yVal = get_ADC_Result('y');
	if(yVal == 255){
		tmp = 4;
	}
	if(yVal == 0){
		tmp = 3;
	}
	
	return tmp;
}

int main(void)
{
	int i;
	uchar joyStick = 0;
	float temp;
	uchar low = 26, high = 30;

	if(wiringPiSetup() == -1){
		printf("setup wiringPi failed !");
		return 1; 
	}
	
	pullUpDnControl(JoyStick_Z, PUD_UP);
	wiringPiISR(JoyStick_Z, INT_EDGE_FALLING, &joyStick_z_ISR);

	pinMode(ADC_CS,  OUTPUT);
	pinMode(ADC_CLK, OUTPUT);
	pinMode(ADC_DIO, OUTPUT);

	ledInit();
	beepInit();
	
	printf("System is running...\n");

	while(1){
		flag:
		joyStick = get_joyStick_state();

		switch(joyStick){
			case 1 : ++low;  break; 
			case 2 : --low;  break;
			case 3 : --high; break;
			case 4 : ++high; break;
			default: break;
		}
		
		if(low >= high){
			printf("Error, lower limit should be less than upper limit\n");
			goto flag;
		}

		printf("The lower limit of temperature : %d\n", low);
		printf("The upper limit of temperature : %d\n", high);
		
		temp = tempRead();
		
		printf("Current temperature : %0.3f\n", temp);
		
		if(temp < low){
			ledCtrl(LedBlue, HIGH);
			ledCtrl(LedRed, LOW);
			ledCtrl(LedGreen, LOW);
			for(i = 0;i < 3; i++){
				beepCtrl(400);
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
				beepCtrl(80);
			}
		}

		if(sys_state == 0){
			ledCtrl(LedRed, LOW);
			ledCtrl(LedGreen, LOW);
			ledCtrl(LedBlue, LOW);
			beep_off();
			printf("System will be off...\n");
			break;
		}
	}

	return 0;
}
