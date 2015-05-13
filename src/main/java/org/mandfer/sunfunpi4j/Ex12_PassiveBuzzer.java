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
import com.pi4j.wiringpi.SoftTone;

/**
 *
 * @author marcandreuf
 */
public class Ex12_PassiveBuzzer extends BaseSketch {    
   
    private static final int BUZPIN = 0;
    
    private static final int CL1 = 131;
    private static final int CL2 = 147;
    private static final int CL3 = 165;
    private static final int CL4 = 175;
    private static final int CL5 = 196;
    private static final int CL6 = 221;
    private static final int CL7 = 248;

    private static final int CM1 = 262;
    private static final int CM2 = 294;
    private static final int CM3 = 330;
    private static final int CM4 = 350;
    private static final int CM5 = 393;
    private static final int CM6 = 441;
    private static final int CM7 = 495;

    private static final int CH1 = 525;
    private static final int CH2 = 589;
    private static final int CH3 = 661;
    private static final int CH4 = 700;
    private static final int CH5 = 786;
    private static final int CH6 = 882;
    private static final int CH7 = 990;
    
    private final int[] song_1 = {CM3,CM5,CM6,CM3,CM2,CM3,CM5,CM6,CH1,CM6,CM5,CM1,CM3,CM2,
  		     CM2,CM3,CM5,CM2,CM3,CM3,CL6,CL6,CL6,CM1,CM2,CM3,CM2,CL7,
		     CL6,CM1,CL5};
    private final int[] beat_1 = {1,1,3,1,1,3,1,1,1,1,1,1,1,1,3,1,1,3,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,3};
    
    private final int[] song_2 = {CM1,CM1,CM1,CL5,CM3,CM3,CM3,CM1,CM1,CM3,CM5,CM5,CM4,CM3,CM2,
		     CM2,CM3,CM4,CM4,CM3,CM2,CM3,CM1,CM1,CM3,CM2,CL5,CL7,CM2,CM1};
    private final int[] beat_2 = {1,1,1,3,1,1,1,3,1,1,1,1,1,1,3,1,1,1,2,1,1,1,3,1,1,1,3,3,2,3};
    
    /**
     * Example using pi4j SoftTone class.
     * 
     * http://pi4j.com/apidocs/com/pi4j/wiringpi/SoftTone.html
     * 
     * @param gpio controller 
     */
    public Ex12_PassiveBuzzer(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex12_PassiveBuzzer sketch = new Ex12_PassiveBuzzer(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup() {
        wiringPiSetup();
        if(SoftTone.softToneCreate(BUZPIN)==-1){
            logger.error("Setup softTone failed !");
            throw new ExceptionInInitializerError("Setup softTone failed !");
        }
        logger.debug("Buzzer sensor ready !");
    }

    @Override
    protected void loop(String[] args) {
        do{
            logger.info("Music is being played...");
            for(int i=0; i<song_1.length/4; i++){
                SoftTone.softToneWrite(BUZPIN, song_1[i]);
                delay(beat_1[i] * 500);
            }
            for(int i=0; i<song_2.length/4; i++){
                SoftTone.softToneWrite(BUZPIN, song_2[i]);
                delay(beat_2[i] * 500);
            }            
        }while(isNotInterrupted);
        SoftTone.softToneStop(BUZPIN);
    }
}
