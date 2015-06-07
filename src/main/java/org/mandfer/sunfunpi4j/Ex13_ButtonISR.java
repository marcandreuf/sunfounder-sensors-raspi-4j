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
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioInterruptCallback;
import static org.mandfer.sunfunpi4j.BaseSketch.logger;
import static org.mandfer.sunfunpi4j.BaseSketch.wiringPiSetup;

/**
 *
 * @author marcandreuf
 */
public class Ex13_ButtonISR extends BaseSketch {    

    private static final int btnPin = 0;
    private static final int ledPin = 1;
    
    public static void main(String[] args) throws InterruptedException {
        Ex13_ButtonISR sketch = new Ex13_ButtonISR(GpioFactory.getInstance());
        sketch.run(args);
    }

    
    /**
     * @param gpio controller 
     */
    public Ex13_ButtonISR(GpioController gpio){
        super(gpio);
    }
    
    @Override
    protected void setup(String[] args) {
        gpioIsrSetup();
        logger.debug("Button ISR ready.");
    }
        
    
    private static volatile long lastTime = System.currentTimeMillis();;
    private static void gpioIsrSetup() {
        wiringPiSetup();
        
        Gpio.pinMode(btnPin, Gpio.INPUT);
        Gpio.pullUpDnControl(btnPin, Gpio.PUD_UP);
        
        Gpio.pinMode(ledPin, Gpio.OUTPUT);
        Gpio.pullUpDnControl(ledPin, Gpio.PUD_DOWN);
        Gpio.digitalWrite(ledPin, false);
        
        Gpio.wiringPiISR(btnPin, Gpio.INT_EDGE_FALLING, new GpioInterruptCallback() {
            private final long debounceTime = 200;
            @Override
            public void callback(int pin) {
                Gpio.delay(10);
                long currentTime = System.currentTimeMillis();
                if(currentTime > lastTime+debounceTime){
                    Gpio.digitalWrite(ledPin, Gpio.digitalRead(ledPin)==0?1:0);                
                    logger.debug("GPIO PIN 0 detected. Led state: "+Gpio.digitalRead(ledPin));
                }else{
                    logger.debug("Discard event "+currentTime);
                }              
                lastTime=currentTime;
            }
        });
    }
    
    
    @Override
    protected void loop(String[] args) {
        try {
            countDownLatchEndSketch.await();
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }
        Gpio.digitalWrite(ledPin, 0);
    }
    
    
}
