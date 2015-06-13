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
import org.mandfer.dht11.DHT11SensorReader;

/**
 *
 * 
 * 
 * @author marcandreuf
 */
public class Ex20_DHT11_Native extends BaseSketch {    

    private DHT11SensorReader sensor;
   
    /**
     * @param gpio controller 
     */
    public Ex20_DHT11_Native(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex20_DHT11_Native sketch = new Ex20_DHT11_Native(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        try{
            sensor = new DHT11SensorReader();
        }catch(Throwable t){
            String errMsg = "\n " 
              + " Please make sure to install de sensor native library. \n"
              + " Run the JNI script at src/main/java with the command \n "
              + " sudo sh jniDHT11SensorReaderBuilder.sh \n";
            throw new RuntimeException(errMsg);
        }
        logger.debug("DHT11 sensor with native library ready!");        
    }

    @Override
    protected void loop(String[] args) {
        String msg;
        do{
            float[] readData = sensor.readData();
            msg = String.format("temp: %.1f, hum %.1f",readData[0], readData[1]);
            logger.debug(msg);
            delayMilliseconds(1500);
        }while(isNotInterrupted);
    }
    
}
