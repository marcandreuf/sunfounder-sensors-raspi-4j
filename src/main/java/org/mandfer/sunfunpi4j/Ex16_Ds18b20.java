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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Detect de w1 device in the RaspberryPi.
 * http://www.reuk.co.uk/DS18B20-Temperature-Sensor-with-Raspberry-Pi.htm
 * 
 * @author marcandreuf
 */
public class Ex16_Ds18b20 extends BaseSketch {    
   
    public final static String W1_DEVICES_PATH = "/sys/bus/w1/devices/";
    public final static String W1_SLAVE = "/w1_slave";
    private String device_fileName; // Argument to point 28-000xxxx
    
    
    
    /**
     * @param gpio controller 
     */
    public Ex16_Ds18b20(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex16_Ds18b20 sketch = new Ex16_Ds18b20(GpioFactory.getInstance());        
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        if(args.length == 1){
            device_fileName = args[0];            
            logger.debug("Ds18b20 sensor ready!");        
        }else{
            throw new RuntimeException(
                    "Please provide a device file name from "+W1_DEVICES_PATH);
        }
    }

    @Override
    protected void loop(String[] args) {
        double temp;
        do{
            temp = readTempFromFile(W1_DEVICES_PATH+device_fileName+W1_SLAVE);
            logger.debug("Temperature is "+temp);
        }while(isNotInterrupted);
    }

    public static double readTempFromFile(String sampleFile) {
        int iniPos, endPos;
        String strTemp, strTempIdentifier = "t=";
        double tvalue = 0;
        List<String> lines;
        try {
            Path path = FileSystems.getDefault().getPath(sampleFile);
            lines = Files.readAllLines(path, Charset.defaultCharset());
            for(String line : lines){
                if(line.contains(strTempIdentifier)){
                    iniPos = line.indexOf(strTempIdentifier)+2;
                    endPos = line.length();
                    strTemp = line.substring(iniPos, endPos);
                    tvalue = Double.parseDouble(strTemp) / 1000;
                }
            }        
        } catch (IOException ex) {
            logger.error("Error while reading file "+sampleFile, ex);
        }
        return tvalue;
    }
}
