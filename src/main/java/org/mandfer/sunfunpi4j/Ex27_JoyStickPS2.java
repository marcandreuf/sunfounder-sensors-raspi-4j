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
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author marcandreuf
 */
public class Ex27_JoyStickPS2 extends ADC_Base {    
   
    private GpioPinDigitalInput joyStick_Z;
    
    /**
     * @param gpio controller 
     */
    public Ex27_JoyStickPS2(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex27_JoyStickPS2 sketch = new Ex27_JoyStickPS2(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        super.setup(args);
        joyStick_Z = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03);
        joyStick_Z.setPullResistance(PinPullResistance.PULL_UP);
        logger.debug("Joystick ready!");        
    }

    @Override
    protected void loop(String[] args) {
        int tmp;
        int channelX=0, channelY=1;
        short xVal=0, yVal=0, zVal=0;
        do{
	    tmp = 0;
            xVal = get_ADC_Result(channelX);
            if(xVal == 0){
                tmp = 1; //up	
            }
            if(xVal == 255){
                tmp = 2; //down
            }

            yVal = get_ADC_Result(channelY);
            if(yVal == 0){
                tmp = 3; //right
            }
            if(yVal == 255){
                tmp = 4; //left
            }

            zVal = (short) joyStick_Z.getState().getValue();
            if(zVal == 0){
                logger.debug("Button is pressed!");
            }
            
            switch(tmp){
		case 0: logger.debug("neutral"); break;
                case 1: logger.debug("up"); break;
                case 2: logger.debug("down"); break;
                case 3: logger.debug("right"); break;
                case 4: logger.debug("left"); break;
                default: break;
            }
            
        }while(isNotInterrupted);
    }
}
