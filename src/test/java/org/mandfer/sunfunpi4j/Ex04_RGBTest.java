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

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mandfer.categories.FastTest;
import org.mandfer.categories.SlowTest;
import static org.mandfer.sunfunpi4j.RGB_Base.LEDPINBLUE;
import static org.mandfer.sunfunpi4j.RGB_Base.LEDPINGREEN;
import static org.mandfer.sunfunpi4j.RGB_Base.LEDPINRED;
import static org.mockito.Mockito.atLeast;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author marcandreuf
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Gpio.class, SoftPwm.class})
public class Ex04_RGBTest extends BaseSketchTest{
 
    private Ex04_RGB sketch;
    
    @Before
    public void setUp(){
        PowerMockito.mockStatic(Gpio.class);
        PowerMockito.mockStatic(SoftPwm.class);        
        sketch = new Ex04_RGB(mocked_gpioController);
    }
    
    @Test
    @Category(FastTest.class)
    public void verifySetup(){
        sketch.setup(NO_ARGS);
        
        PowerMockito.verifyStatic();
        Gpio.wiringPiSetup();
        
        PowerMockito.verifyStatic();
        SoftPwm.softPwmCreate(LEDPINRED, 0, 100);
        PowerMockito.verifyStatic();
        SoftPwm.softPwmCreate(LEDPINGREEN, 0, 100);
        PowerMockito.verifyStatic();
        SoftPwm.softPwmCreate(LEDPINBLUE, 0, 100);        
    }    
    
    @Test
    @Category(SlowTest.class)
    public void turnOffLedBeforeFinishSketch() throws InterruptedException{
        sketch.setup(NO_ARGS);
        sketch.setSketchInterruption();
        sketch.loop();
        
        PowerMockito.verifyStatic(atLeast(1));
        SoftPwm.softPwmWrite(LEDPINRED, 0);
        PowerMockito.verifyStatic(atLeast(1));
        SoftPwm.softPwmWrite(LEDPINGREEN, 0);
        PowerMockito.verifyStatic(atLeast(1));
        SoftPwm.softPwmWrite(LEDPINBLUE, 0);
    }

}
