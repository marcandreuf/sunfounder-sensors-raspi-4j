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
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author marcandreuf
 */
public class Ex20_DHT11 extends BaseSketch {    
   
    private final int MAXTIMINGS = 85;
    private GpioPinDigitalMultipurpose dhtPin;
    private int[] dht11_dat = {0,0,0,0,0};
   
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
        dhtPin = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_01, PinMode.DIGITAL_OUTPUT);
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
        PinState lastState = PinState.HIGH;
        short counter = 0;
        short j=0, i;
        float f; // fahrenheit
        
        // Required ???
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;
        
        // Pull pin down for 18 milliseconds
        dhtPin.setMode(PinMode.DIGITAL_OUTPUT);
        dhtPin.low();
        delayMilliseconds(18);
        
        // Pull pin up for 40 microseconds
        dhtPin.high();
        delayMicrosendos(40);
        
        // Prepare to read from the pin
        dhtPin.setMode(PinMode.DIGITAL_INPUT);
        
        // Detect change and read data
        for(i=0; i<MAXTIMINGS; i++){
            counter = 0;
            while(dhtPin.getState() == lastState){
                counter++;
                delayMicrosendos(1);
                if(counter == 255){
                    break;
                }
            }
            lastState = dhtPin.getState();
            
            if(counter == 255) break;
            
            // Ignore first 3 transitions
            if( (i>=4) && (i%2==0)){
                // shove each bit into the storage bytes
                dht11_dat[j/8] <<= 1;
                if(counter > 16){
                    dht11_dat[j/8] |= 1;
                }
                j++;
            }
        }
        
        // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte
	// print it out if data is good
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
