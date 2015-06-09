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
    
    /*
    * Based on calculation from 
    * http://home.roboticlab.eu/en/examples/sensor/photoresistor
    * 
    * SWAP GND AND +5V CONNEXIONS to setup the resistor as per the link above !
    *
    */

    @Override
    protected void loop(String[] args) {
        short analogVal;
        double voltageReading;
        double resistance;
        double illuminance;
                
        do {
            analogVal = get_ADC_Result();
            logger.debug("ADC value: "+analogVal);
           
            // U = Uref * (ADC / 256)
            voltageReading = (analogVal / 256d) * 5d;
            logger.debug("Voltage: "+Double.toString(voltageReading));
            
            //With a R2 of 10k and Uref, voltage reference at 5V
            // R = (R2*Uref)/U2-R2
            resistance = (10d*5d)/voltageReading-10d;
            logger.debug("Resistance: "+resistance);
                        
            illuminance = 255.84 * Math.pow(resistance, -10d/9d);
            logger.debug("Illuminance in LUX: "+illuminance+"\n\n");
                                    
            delayMilliseconds(3000);
        } while (isNotInterrupted);
    }
}
