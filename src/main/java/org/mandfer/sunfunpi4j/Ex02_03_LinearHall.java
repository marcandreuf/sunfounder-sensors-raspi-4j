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
import com.pi4j.io.gpio.PinMode;

/**
 *
 * In this example we use type 'short'. In JAVA the type 'byte' is signed and
 * the values go from -128 to 127. There is no unsigned 'byte' type in JAVA. The
 * next numeric type is 'short', which is a 16-bit integer. 
 * Documentation about the Hall effect and the ADC0832CCN
 * 
 * Hall effect sensing and application by Honeywell
 * http://sensing.honeywell.com/index.php?ci_id=47847
 * 
 * Hall effect sensor 44E datasheet
 * http://www.allegromicro.com/~/media/Files/Datasheets/A3141-2-3-4-Datasheet.ashx
 * 
 * ADC0832 datasheet
 * http://pdf1.alldatasheet.com/datasheet-pdf/view/158145/NSC/ADC0832CCN.html
 *
 * @author marcandreuf
 */
public class Ex02_03_LinearHall extends ADC_Base {
    private short intensity;

    /**
     * @param gpio controller
     */
    public Ex02_03_LinearHall(GpioController gpio) {
        super(gpio);
    }

    public static void main(String[] args) throws InterruptedException {
        Ex02_03_LinearHall sketch = new Ex02_03_LinearHall(GpioFactory.getInstance());
        sketch.run(args);
    }

    @Override
    protected void setup() {
        super.setup();
        logger.debug("Linear Hall sensor ready!");
    }

    @Override
    protected void loop(String[] args) {
        short analogVal;
        do {
            ADC_DIO.setMode(PinMode.DIGITAL_OUTPUT);
            analogVal = get_ADC_Result();
            intensity = (short) (210 - analogVal);
            logger.debug("Current intensity of magnetic field: " + Integer.valueOf(intensity));
            delay(500);
        } while (isNotInterrupted);
    }
    
    public short getIntensity() {
        return intensity;
    }
}
