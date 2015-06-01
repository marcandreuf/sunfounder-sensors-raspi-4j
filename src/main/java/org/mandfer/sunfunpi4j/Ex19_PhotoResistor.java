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

/**
 *
 * @author marcandreuf
 */
public class Ex19_PhotoResistor extends ADC_Base {    
    private short illum;
    /**
     * @param gpio controller 
     */
    public Ex19_PhotoResistor(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex19_PhotoResistor sketch = new Ex19_PhotoResistor(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        super.setup(args);
        logger.debug("Sketch ready!");        
    }
    
    //http://home.roboticlab.eu/en/examples/sensor/photoresistor

    @Override
    protected void loop(String[] args) {
        short analogVal;
        double voltage;
        double resistance;
        double illuminance;
        do {
            analogVal = get_ADC_Result();
            illum = (short) (210 - analogVal);
            logger.debug("Current illumination: " + Integer.valueOf(illum));
            
            // U = Uref * (ADC / 256)
            voltage = (float) ((analogVal / 256) * 5);
            logger.debug("Current voltage: "+voltage);
            
            //With a R2 of 10k
            // R = (R2*Uref)/U2-R2
            resistance = (10*5)/voltage-10;
            logger.debug("Resistance: "+resistance);
                        
            illuminance = 255.84 * Math.pow(resistance, -10/9);
            logger.debug("Light intensisty in LUX: "+illuminance);
                                    
            delayMilliseconds(1000);
        } while (isNotInterrupted);
    }
}
