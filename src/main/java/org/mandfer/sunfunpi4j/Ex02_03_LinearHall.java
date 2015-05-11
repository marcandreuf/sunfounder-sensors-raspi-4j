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
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Blink led on GPIO 0
 *
 * @author marcandreuf
 */
public class Ex02_03_LinearHall extends BaseSketch {    

    private short intensity;
    
    private GpioPinDigitalOutput ADC_CS;
    private GpioPinDigitalOutput ADC_CLK;
    private GpioPinDigitalMultipurpose ADC_DIO;
    
    /**
     * In this example we use type 'short'. In JAVA the type 'byte' is 
     * signed and the values go from -128 to 127. There is no unsigned 'byte'
     * type in JAVA. The next numeric type is 'short', 
     * which is a 16-bit integer.  
     * 
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
     * @param gpio controller 
     */
    public Ex02_03_LinearHall(GpioController gpio){
        super(gpio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Ex02_03_LinearHall sketch = new Ex02_03_LinearHall( GpioFactory.getInstance());
        sketch.run(args);
    }    
    
    public short getIntensity(){
        return intensity;
    }
    
    @Override
    protected void setup() {
        wiringPiSetup();
        
        ADC_CS = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
        ADC_CLK = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        ADC_DIO = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_02, PinMode.DIGITAL_OUTPUT);
        logger.debug("Linear Hall ready!");        
    }

    @Override
    protected void loop(String[] args) {
        short analogVal;
        do{            
            ADC_DIO.setMode(PinMode.DIGITAL_OUTPUT);            
            analogVal = get_ADC_Result();
            intensity = (short) (210 - analogVal);
            logger.debug("Current intensity of magnetic field: "+Integer.valueOf(intensity));
            delay(500);            
        }while(isNotInterrupted);
    }
    
    private short get_ADC_Result(){        
        short dat1=0, dat2=0;
        
        // Start converstaion        
        ADC_CS.low();
	
	// MUX Start bit to setup MUX address (Multiplexer configuration)
        ADC_CLK.low();
        ADC_DIO.high(); delayMicrosendos(2);  // Start bit
        ADC_CLK.high(); delayMicrosendos(2);         

	// MUX SGL/-DIF git to setup Sigle-Ended channel type
	ADC_CLK.low();
        ADC_DIO.high(); delayMicrosendos(2);
        ADC_CLK.high(); delayMicrosendos(2);
        
        // MUX ODD/SIGN bit to setup analog input in Channel #0
        ADC_CLK.low();
        ADC_DIO.low(); delayMicrosendos(2); 
        ADC_CLK.high();        
        
        // Keep the clock going to settle the MUX address
        delayMicrosendos(2);
        ADC_CLK.low();
        delayMicrosendos(2);

	// Read MSB byte
	ADC_DIO.setMode(PinMode.DIGITAL_INPUT);        
        for(byte i=0; i<8; i++){
            ADC_CLK.high(); delayMicrosendos(2);
            ADC_CLK.low(); delayMicrosendos(2);
            dat1 = (short) ((dat1 << 1) | ADC_DIO.getState().getValue());
        }
        // Read LSB byte
        for(byte i=0; i<8; i++){
            dat2 = (short) (dat2 | (ADC_DIO.getState().getValue() << i));
            ADC_CLK.high(); delayMicrosendos(2);
            ADC_CLK.low(); delayMicrosendos(2);
        }
        
        // End of conversation.
        ADC_CS.high();
        
        //If valid reading MSF == LSF
        return dat1==dat2 ? dat1 : 0;        
    }

}
