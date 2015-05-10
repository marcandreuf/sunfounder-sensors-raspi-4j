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

import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mandfer.categories.FastTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author marc
 */
@Category(FastTest.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({Gpio.class, TimeUnit.class})
public class Ex02_LinearHallTest extends BaseSketchTest{
    
    private GpioPinDigitalOutput mocked_ADC_CS;
    private GpioPinDigitalOutput mocked_ADC_CLK;
    private GpioPinDigitalMultipurpose mocked_ADC_DIO;    
    private Ex02_LinearHall sketch;
    
    @Before
    public void setUp(){
        PowerMockito.mockStatic(Gpio.class);
        PowerMockito.mockStatic(TimeUnit.class);
        
        mocked_ADC_CS = mock(GpioPinDigitalOutput.class);
        mocked_ADC_CLK = mock(GpioPinDigitalOutput.class);
        mocked_ADC_DIO = mock(GpioPinDigitalMultipurpose.class);
        sketch = new Ex02_LinearHall(mocked_gpioController);
    }
    
    @Test
    public void verifySetup(){
        sketch.setup();
        
        PowerMockito.verifyStatic();
        Gpio.wiringPiSetup();
        verify(mocked_gpioController).provisionDigitalOutputPin(RaspiPin.GPIO_00);
        verify(mocked_gpioController).provisionDigitalOutputPin(RaspiPin.GPIO_01);
        verify(mocked_gpioController).provisionDigitalMultipurposePin(
                                        RaspiPin.GPIO_02, PinMode.DIGITAL_OUTPUT);
        verifyNoMoreInteractions(mocked_gpioController);
    }
    
    @Test
    public void testNotMagneticFieldInteraction() throws InterruptedException {
        prepareMockedPins();
        when(mocked_ADC_DIO.getState()).thenReturn(PinState.LOW);        
                
        sketch.setup();
        sketch.setSketchInterruption();
        sketch.loop();
        
        verify(mocked_ADC_DIO).setMode(PinMode.DIGITAL_OUTPUT);
        verify(mocked_ADC_CS).low();
        verify(mocked_ADC_DIO, times(5)).high();
        verify(mocked_ADC_CLK, times(19)).high();
        verify(mocked_ADC_CLK, times(20)).low();
        verify(mocked_ADC_DIO).setMode(PinMode.DIGITAL_INPUT);
        verify(mocked_ADC_CS).high();
    }
    
    private void prepareMockedPins() {
        when(mocked_gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00))
          .thenReturn(mocked_ADC_CS);
        when(mocked_gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01))
          .thenReturn(mocked_ADC_CLK);
        when(mocked_gpioController.provisionDigitalMultipurposePin(RaspiPin.GPIO_02, PinMode.DIGITAL_OUTPUT))
          .thenReturn(mocked_ADC_DIO);
    }
    
    
//    @Test
//    public void testMagneticFieldInteraction() throws InterruptedException {
//        prepareMockedPins();
//                
//        when(mocked_hallPin.isLow()).thenReturn(true, true, false);
//        
//        sketch.setup();
//        sketch.setSketchInterruption();
//        sketch.loop();
//        
//        verify(mocked_ledPin).setState(PinState.LOW);
//        verify(mocked_hallPin, times(3)).isLow();
//        verify(mocked_ledPin).setState(PinState.HIGH);
//        verifyNoMoreInteractions(mocked_hallPin, mocked_ledPin);
//    }   

}
