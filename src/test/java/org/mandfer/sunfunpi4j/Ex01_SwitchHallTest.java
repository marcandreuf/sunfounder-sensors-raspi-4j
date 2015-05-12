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

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mandfer.categories.FastTest;
import org.mandfer.categories.SlowTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 *
 * @author marcandreuf
 */
public class Ex01_SwitchHallTest extends BaseSketchTest{
    
    private GpioPinDigitalInput mocked_hallPin;
    private GpioPinDigitalOutput mocked_ledPin;
    private Ex01_SwitchHall sketch;
    
    @Before
    public void setUp(){
        mocked_hallPin = mock(GpioPinDigitalInput.class);
        mocked_ledPin = mock(GpioPinDigitalOutput.class);
        sketch = new Ex01_SwitchHall(mocked_gpioController);
    }
    
    @Test
    @Category(FastTest.class)
    public void verifySetup(){
        sketch.setup();
        
        verify(mocked_gpioController).provisionDigitalInputPin(RaspiPin.GPIO_00);
        verify(mocked_gpioController).provisionDigitalOutputPin(RaspiPin.GPIO_01);
        verifyNoMoreInteractions(mocked_gpioController);
    }
    
    @Test
    @Category(SlowTest.class)
    public void testNotMagneticFieldInteraction() throws InterruptedException {
        prepareMockedPins();                
        when(mocked_hallPin.isLow()).thenReturn(false);
        
        sketch.setup();
        sketch.setSketchInterruption();
        sketch.loop();
        
        verify(mocked_ledPin).setState(PinState.LOW);
        verify(mocked_hallPin).isLow();
        verifyNoMoreInteractions(mocked_hallPin, mocked_ledPin);
    }
    private void prepareMockedPins() {
        when(mocked_gpioController.provisionDigitalInputPin(RaspiPin.GPIO_00)).thenReturn(mocked_hallPin);
        when(mocked_gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01)).thenReturn(mocked_ledPin);
    }
    
    
    @Test
    @Category(SlowTest.class)
    public void testMagneticFieldInteraction() throws InterruptedException {
        prepareMockedPins();
                
        when(mocked_hallPin.isLow()).thenReturn(true, true, false);
        
        sketch.setup();
        sketch.setSketchInterruption();
        sketch.loop();
        
        verify(mocked_ledPin).setState(PinState.LOW);
        verify(mocked_hallPin, times(3)).isLow();
        verify(mocked_ledPin).setState(PinState.HIGH);
        verifyNoMoreInteractions(mocked_hallPin, mocked_ledPin);
    }   

}
