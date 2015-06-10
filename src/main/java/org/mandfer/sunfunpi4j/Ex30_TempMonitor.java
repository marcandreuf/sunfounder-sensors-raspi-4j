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
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 *
 * @author marcandreuf
 */
public class Ex30_TempMonitor extends ADC_Base {    
   
    private GpioPinDigitalOutput ledRed; // Pin 2
    private GpioPinDigitalOutput ledGreen;  // Pin 4
    private GpioPinDigitalOutput ledBlue; // Pin 5
    private GpioPinDigitalInput joyStick_Z; // Pin 6
    private GpioPinDigitalOutput beep; // Pin 8
    private String device_fileName; // Argument to point 28-000xxxx 
    private PinState sys_state;
    
    /**
     * @param gpio controller 
     */
    public Ex30_TempMonitor(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex30_TempMonitor sketch = new Ex30_TempMonitor(GpioFactory.getInstance());
        sketch.run(args);
    }
    
    @Override
    protected void setup(String[] args) {
        super.setup(args);
        if(args.length == 1){
            device_fileName = args[0];            
            logger.debug("Temperature monitor ready!");
        }else{
            throw new RuntimeException(
                    "Please provide a device file name from "
                            +Ex16_Ds18b20.W1_DEVICES_PATH);
        }
        ledRed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
        ledGreen = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        ledBlue = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
        beep = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08);
        joyStick_Z = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_UP);
        joyStick_Z.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
                sys_state = PinState.LOW;
                logger.debug("interrupt occur !");
            }
        });
        sys_state = PinState.HIGH;
                
    }

    @Override
    protected void loop(String[] args) {
        int low=26, high=30, joyStick=0;
        double temp;
        
        logger.info("System is running...");        
        do{         
            joyStick = get_joyStick_state();
                switch(joyStick){
                case 1 : ++low;  break; 
                case 2 : --low;  break;
                case 3 : --high; break;
                case 4 : ++high; break;
                default: break;
            }
            if(low < high){
                logger.info("The lower limit of temperature : %d\n", low);
                logger.info("The upper limit of temperature : %d\n", high);                
            }   
            temp = tempRead();
            logger.info("Current temperature : %0.3f\n", temp);            
            if(temp < low){
                ledBlue.high();
                ledRed.low();
                ledGreen.low();
                for(int i=0; i<3; i++){
                    beepCtrl(400);
                }                
            }
            if(temp >= low && temp < high){
                ledBlue.low();
                ledRed.low();
                ledGreen.high();
            }
            if(temp >= high){
                ledBlue.low();
                ledRed.high();
                ledGreen.low();
                for(int i=0; i<3; i++){
                    beepCtrl(80);
                }
            }
            if(sys_state.isLow()){
                ledRed.low();
                ledBlue.low();
                ledGreen.low();
                beep_off();
                logger.info("System will be off...");
                setSketchInterruption(); // Set to finish the loop.
            }
        }while(isNotInterrupted);
    }
    
    private void beep_on(){
        beep.low();
    }

    private void beep_off(){
        beep.high();
    }
    
    private void beepCtrl(int delay){
        beep_on();
        delayMilliseconds(delay);
        beep_off();
        delayMilliseconds(delay);
    }
    
    private double tempRead(){
        return Ex16_Ds18b20.readTempFromFile(device_fileName);
    }
    
    private int get_joyStick_state(){
        int channelX = 0;
        int channelY = 1;
        int retValue = 0;
        short adcValue;
        
        adcValue = get_ADC_Result(channelX);
        if(adcValue == 255){
            retValue = 1;
        }
        if(adcValue == 0){
            retValue = 2;
        }
        
        adcValue = get_ADC_Result(channelY);
        if(adcValue == 255){
            retValue = 4;
        }
        if(adcValue == 0){
            retValue = 3;
        }       
        
        return retValue;
    }
}
