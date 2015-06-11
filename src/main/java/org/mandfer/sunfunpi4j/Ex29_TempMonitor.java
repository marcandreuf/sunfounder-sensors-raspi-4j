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
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author marcandreuf
 */
public class Ex29_TempMonitor extends BaseSketch {

    private GpioPinDigitalOutput ledRed; // Pin 0
    private GpioPinDigitalOutput ledGreen;  // Pin 1
    private GpioPinDigitalOutput ledBlue; // Pin 2
    private GpioPinDigitalOutput beep; // Pin 3
    private String device_fileName; // Argument to point 28-000xxxx
    int low, high;

    /**
     * @param gpio controller
     */
    public Ex29_TempMonitor(GpioController gpio) {
        super(gpio);
    }

    public static void main(String[] args) throws InterruptedException {
        Ex29_TempMonitor sketch = new Ex29_TempMonitor(GpioFactory.getInstance());
        sketch.run(args);
    }

    @Override
    protected void setup(String[] args) {
        if (args.length != 4) {
            device_fileName = args[0];
            low = Integer.parseInt(args[1]);
            high = Integer.parseInt(args[2]);
            if (low >= high) {
                throw new RuntimeException(
                        "Arguments error, lower limit "
                        + "should be less than upper limit. \n");
            }
            logger.debug("Temperature monitor ready!");
        } else {
            throw new RuntimeException(
                    "Please provide arguments \n"
                    + " [ds18b20 Filename] [lower limit] [upper limit] \n"
                    + "Find ds18b20 device file names at "
                    + Ex16_Ds18b20.W1_DEVICES_PATH);
        }
        wiringPiSetup();
        ledRed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
        ledGreen = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        ledBlue = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
        beep = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
        logger.info("Temperature monitor ready!");
    }

    @Override
    protected void loop(String[] args) {
        double temp;
        do {
            temp = tempRead();

            logger.info("The lower limit of temperature : %d\n", low);
            logger.info("The upper limit of temperature : %d\n", high);
            logger.info("Current temperature : %0.3f\n", temp);

            if (temp < low) {
                ledBlue.high();
                ledRed.low();
                ledGreen.low();
                for (int i = 0; i < 3; i++) {
                    beepCtrl(500);
                }
            }
            if (temp >= low && temp < high) {
                ledBlue.low();
                ledRed.low();
                ledGreen.high();
            }
            if (temp >= high) {
                ledBlue.low();
                ledRed.high();
                ledGreen.low();
                for (int i = 0; i < 3; i++) {
                    beepCtrl(100);
                }
            }
        } while (isNotInterrupted);
    }

    private void beepCtrl(int delay) {
        beep.low();
        delayMilliseconds(delay);
        beep.high();
        delayMilliseconds(delay);
    }

    private double tempRead() {
        return Ex16_Ds18b20.readTempFromFile(device_fileName);
    }
}
