/**
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Marc Andreu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package org.mandfer.sunfunpi4j;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import static java.lang.Math.log;

/**
 *
 * @author marcandreuf
 */
public class Ex11_01_AnalogTempSensor extends ADC_Base {
    /**
     * @param gpio controller
     */
    public Ex11_01_AnalogTempSensor(GpioController gpio) {
        super(gpio);
    }

    public static void main(String[] args) throws InterruptedException {
        Ex11_01_AnalogTempSensor sketch = new Ex11_01_AnalogTempSensor(GpioFactory.getInstance());
        sketch.run(args);
    }

    @Override
    protected void setup() {
        super.setup();
        logger.debug("Analog temp sensor ready!");
    }

    @Override
    protected void loop(String[] args) {        
        do {
            double resistance = getTempfromThermister(get_ADC_Result());
            logger.debug("Resistor value: "+resistance);
            //logger.debug("Current temperature: "+temp);
            delay(1000);
        } while (isNotInterrupted);
    }
    
    
    private double getTempfromThermister(int rawADC) {
        int KY_013Resistor = 10000;
        double resisADC = ((256.0 / (double)rawADC)-1)*KY_013Resistor; 
        
        
        double temp = log(((2560000/(double)rawADC) - 10000));
        temp = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * temp * temp ))* temp );
        return temp - 273.15;// Convert Kelvin to Celcius
        
        //double farenh = 4050.00 / Math.log(resisADC/(10000.00*Math.exp(-4050.00/298.00)));
        //return farenh - 273.15;
    }
}
