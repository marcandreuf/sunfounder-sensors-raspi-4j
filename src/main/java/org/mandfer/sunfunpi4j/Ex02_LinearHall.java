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
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Blink led on GPIO 0
 *
 * @author marcandreuf
 */
public class Ex02_LinearHall extends BaseSketch {    

    private GpioPinDigitalOutput ADC_CS;
    private GpioPinDigitalOutput ADC_CLK;
    private GpioPinDigitalOutput ADC_DIO;
    
    /**
     * In this example we use type 'char'. In JAVA the type 'byte' is 
     * signed and the values go from -128 to 127. There is no unsigned 'byte'
     * type in JAVA. The next type char, which is a single 16-bit positive integer.  
     * 
     * @param gpio 
     */
    public Ex02_LinearHall(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex02_LinearHall sketch = new Ex02_LinearHall( GpioFactory.getInstance());
        sketch.run(args);
    }    
    
    @Override
    protected void setup() {
        wiringPiSetup();
        
        ADC_CS = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
        ADC_CLK = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        ADC_DIO = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_02, PinMode.DIGITAL_OUTPUT);
        logger.debug("Linear Hall ready!");        
    }

    @Override
    protected void loop(String[] args) {
        char analogVal;
        char mag;
        
        do{            
            ADC_DIO.setMode(PinMode.DIGITAL_OUTPUT);
            
            analogVal = get_ADC_Result();
            mag = (char) (210 - analogVal);
            logger.debug("Current intensity of magnetic field: "+mag);
            delay(500);            
        }while(isNotInterrupted);
    }
    
    private char get_ADC_Result(){        
        char dat1=0, dat2=0;
        
        ADC_CS.low();
        ADC_CLK.low();
        ADC_DIO.high(); delayMicrosendos(2);
        ADC_CLK.high(); delayMicrosendos(2);        
        
        ADC_CLK.low();
        ADC_DIO.high(); delayMicrosendos(2);
        ADC_CLK.high(); delayMicrosendos(2);
        
        ADC_CLK.low();
        ADC_DIO.high(); delayMicrosendos(2);
        ADC_CLK.high();
        ADC_DIO.high(); delayMicrosendos(2);
        ADC_CLK.low();
        ADC_DIO.high(); delayMicrosendos(2);
        
        for(byte i=0; i<8; i++){
            ADC_CLK.high(); delayMicrosendos(2);
            ADC_CLK.low(); delayMicrosendos(2);
            
            ADC_DIO.setMode(PinMode.DIGITAL_INPUT);
            dat1 = (char) ((dat1 << 1) | ADC_DIO.getState().getValue());
        }
        
        for(byte i=0; i<8; i++){
            dat2 = (char) (dat2 | (ADC_DIO.getState().getValue() << i));
            ADC_CLK.high(); delayMicrosendos(2);
            ADC_CLK.low(); delayMicrosendos(2);
        }
        
        ADC_CS.high();
        
        return dat1==dat2 ? dat1 : 0;        
    }

}






































