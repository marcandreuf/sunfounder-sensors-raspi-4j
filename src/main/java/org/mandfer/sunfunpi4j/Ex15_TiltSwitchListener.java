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
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import static org.mandfer.sunfunpi4j.BaseSketch.wiringPiSetup;

/**
 *
 * @author marcandreuf
 */
public class Ex15_TiltSwitchListener extends BaseSketch {    
    private GpioPinDigitalInput tiltSwitch;
    private GpioPinDigitalOutput ledPin;
    
    /**
     * @param gpio controller 
     */
    public Ex15_TiltSwitchListener(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex15_TiltSwitchListener sketch = new Ex15_TiltSwitchListener(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        wiringPiSetup();
        ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);        
        tiltSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);        
        tiltSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                logger.debug("Tilt state: "+event.getState());
                if(event.getState().isHigh()){
                    ledPin.high();
                }else{
                    ledPin.low();
                }
            }            
        });
        logger.debug("Tilt switch sensor ready!"); 
    }

    @Override
    protected void loop(String[] args) {
        try {
            countDownLatchEndSketch.await();
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
