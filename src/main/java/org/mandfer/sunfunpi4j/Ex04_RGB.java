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
import java.awt.Color;

/**
 *
 * @author marcandreuf
 */
public class Ex04_RGB extends RGB_Base {
    
    /**
     * @param gpio controller 
     */
    public Ex04_RGB(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex04_RGB sketch = new Ex04_RGB(GpioFactory.getInstance());
        sketch.run(args);
    }    
    
    @Override
    protected void setup(String[] args) {
        wiringPiSetup();
        ledInit(LEDPINRED, LEDPINGREEN, LEDPINBLUE);
        logger.debug("Rgb ready!");        
    }

    @Override
    protected void loop(String[] args) {
        do{ 
            ledColorSet(Color.RED);
            delayMilliseconds(500);
            ledColorSet(Color.GREEN);
            delayMilliseconds(500);
            ledColorSet(Color.BLUE);
            delayMilliseconds(500);
            ledColorSet(Color.YELLOW);
            delayMilliseconds(500);
            ledColorSet(Color.PINK);
            delayMilliseconds(500);
            ledColorSet(0xc0,0xff,0x3e);
            delayMilliseconds(500);
            ledColorSet(0x94,0x00,0xd3);
            delayMilliseconds(500);
            ledColorSet(0x76,0xee,0x00);
            delayMilliseconds(500);
            ledColorSet(0x00,0xc5,0xcd);	
            delayMilliseconds(500);            
        }while(isNotInterrupted);
        turnLedOff();
    }

}
