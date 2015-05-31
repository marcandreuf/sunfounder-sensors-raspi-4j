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
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 *
 * @author marcandreuf
 */
public class Ex17_RotaryEncoder extends BaseSketch {    
   
    private GpioPinDigitalInput swPin;
    private GpioPinDigitalInput roAPin;
    private GpioPinDigitalInput roBPin;
    private int globalCounter = 0;
    private boolean flag = false;
    private PinState lastRoBStatus;
    private PinState currentRoBStatus=PinState.LOW;
    
    
    /**
     * @param gpio controller 
     */
    public Ex17_RotaryEncoder(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex17_RotaryEncoder sketch = new Ex17_RotaryEncoder(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        swPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);
        roAPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);
        roBPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02);
        swPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
                globalCounter = 0;
            }
        });
        logger.debug("Sketch ready!");        
    }

    @Override
    protected void loop(String[] args) {
        do{
            rotaryDeal();
            logger.debug("counter :"+globalCounter);
        }while(isNotInterrupted);
    }

    private void rotaryDeal() {
       lastRoBStatus = roBPin.getState();
            
        while(roAPin.isLow()){
            currentRoBStatus = roBPin.getState();
            flag=true;
        }
            
        if(flag){
            flag = false;
            if(lastRoBStatus==PinState.LOW && currentRoBStatus==PinState.HIGH){
                globalCounter ++;
            }
            if(lastRoBStatus==PinState.HIGH && currentRoBStatus==PinState.LOW){
                globalCounter --;
            }
        }
    }
}
