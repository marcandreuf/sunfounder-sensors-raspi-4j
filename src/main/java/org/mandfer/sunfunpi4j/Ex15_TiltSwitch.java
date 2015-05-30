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
import com.pi4j.io.gpio.RaspiPin;
import static org.mandfer.sunfunpi4j.BaseSketch.wiringPiSetup;

/**
 *
 * @author marcandreuf
 */
public class Ex15_TiltSwitch extends BaseSketch {    
    private GpioPinDigitalInput tiltSwitch;
    private GpioPinDigitalOutput ledPin;
    
    /**
     * @param gpio controller 
     */
    public Ex15_TiltSwitch(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex15_TiltSwitch sketch = new Ex15_TiltSwitch(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        wiringPiSetup();
        tiltSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00);
        ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        logger.debug("Tilt switch sensor ready!"); 
    }

    @Override
    protected void loop(String[] args) {
        do{
            if(tiltSwitch.isHigh()){
                logger.debug("led on !");
                ledPin.high();
            }else{
                logger.debug("led off !");
                ledPin.low();
            }
            delay(500);
            ledPin.low();
        }while(isNotInterrupted);
    }
}
