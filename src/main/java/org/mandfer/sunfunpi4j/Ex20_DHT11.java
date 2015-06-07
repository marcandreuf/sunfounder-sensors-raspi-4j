/**
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Marc Andreu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.mandfer.sunfunpi4j;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import static com.pi4j.wiringpi.Gpio.HIGH;
import static com.pi4j.wiringpi.Gpio.INPUT;
import static com.pi4j.wiringpi.Gpio.LOW;
import static com.pi4j.wiringpi.Gpio.OUTPUT;
import static com.pi4j.wiringpi.Gpio.delayMicroseconds;
import static com.pi4j.wiringpi.Gpio.digitalRead;
import static com.pi4j.wiringpi.Gpio.digitalWrite;
import static com.pi4j.wiringpi.Gpio.pinMode;

/**
 *
 * Looks like this sensor does not work in JAVA due to timing precision. 
 * 
 * It should work using this tutorial from Aadafruit 
 * 
 * https://learn.adafruit.com/downloads/pdf/dht-humidity-sensing-on-raspberry-pi-with-gdocs-logging.pdf
 * 
 * TODO: Make a direct call to the C library underneath Python.
 * Optionally call the DHT11.c from java and parse the values.
 * 
 * TODO: Implement JNI integration of the c library
 * http://en.wikipedia.org/wiki/Java_Native_Interface#Examples
 * https://bitbucket.org/Temdegon/greenhouse
 * check in sensors folder
 * 
 * 
 * @author marcandreuf
 */
public class Ex20_DHT11 extends BaseSketch {    
    private final int dhtPin = 1;
    private final int MAXTIMINGS = 85;
    //private GpioPinDigitalMultipurpose dhtPin;
    private final int[] dht11_dat = {0,0,0,0,0};
   
    /**
     * @param gpio controller 
     */
    public Ex20_DHT11(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex20_DHT11 sketch = new Ex20_DHT11(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        wiringPiSetup();
        //dhtPin = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_01, PinMode.DIGITAL_OUTPUT);
        logger.debug("DHT11 sensor ready!");        
    }

    @Override
    protected void loop(String[] args) {
        do{
            read_dht11_dat();
            delayMilliseconds(1000);
        }while(isNotInterrupted);
    }

    private void read_dht11_dat() {
        int lastState = HIGH;
        int currentState;
        int counter = 0;
        int j=0, i;
        float f; // fahrenheit
        
        // Required ???
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;
        
        pinMode(dhtPin, OUTPUT);
        
//        digitalWrite(dhtPin, HIGH);
//        delayMilliseconds(500);

        // Pull pin down for 18 milliseconds
        digitalWrite(dhtPin, LOW);
        delayMilliseconds(20);
        
        // Pull pin up for 40 microseconds
        digitalWrite(dhtPin, HIGH);
        delayMicrosendos(40);
        
        // Prepare to read from the pin
        pinMode(dhtPin, INPUT);
                
        delayMicroseconds(10);
        // Detect change and read data
        for(i=0; i<MAXTIMINGS; i++){
            counter = 0;
            while(digitalRead(dhtPin) == lastState){
                counter++;
                delayMicrosendos(1);
                if(counter == 255){
                    break;
                }
            }
            lastState = digitalRead(dhtPin);
                        
            if(counter == 255){
                //logger.debug("counted 255 break 2");
                break;
            }
            
            // Ignore first 3 transitions            
            if( (i>=4) && (i%2==0)){
                logger.debug("shove bits "+counter);
                // shove each bit into the storage bytes
                dht11_dat[j/8] <<= 1;
                if(counter > 16){
                    dht11_dat[j/8] |= 1;
                }
                j++;
            }else{
                logger.debug("Ignore transition");
            }
        }
        
        // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte
	// print it out if data is good
        logger.debug("j: "+j);
       if ((j >= 40) && (dht11_dat[4] == ((dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3]) & 0xFF)) ) {
		f = (float) (dht11_dat[2] * 9f / 5f + 32f);
		logger.debug("Humidity = "+dht11_dat[0]+"."+dht11_dat[1]
                      +". Temperature = "+dht11_dat[2]+"."+dht11_dat[3]+" *C ("+f+" *F)");
	}
	else
	{
		logger.debug("Data not good, skip");
	}
    }
}
