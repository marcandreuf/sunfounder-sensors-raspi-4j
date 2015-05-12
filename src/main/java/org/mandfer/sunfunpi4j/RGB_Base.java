/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mandfer.sunfunpi4j;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.SoftPwm;
import java.awt.Color;

/**
 * Base class with RBG methods
 * 
 * @author marcandreuf
 */
public abstract class RGB_Base extends BaseSketch {
    
    public RGB_Base(GpioController gpio){
        super(gpio);
    }

    public static final int LEDPINRED = 0;
    public static final int LEDPINGREEN = 1;
    public static final int LEDPINBLUE = 2;
    
    
    protected void ledInit() {
        SoftPwm.softPwmCreate(LEDPINRED, 0, 100);
        SoftPwm.softPwmCreate(LEDPINGREEN, 0, 100);
        SoftPwm.softPwmCreate(LEDPINBLUE, 0, 100);
    } 
    
    protected void ledColorSet(Color color) {
        ledColorSet(color.getRed(), color.getGreen(), color.getBlue());
    }

    protected void ledColorSet(int... colorValue) {
        SoftPwm.softPwmWrite(LEDPINRED, colorValue[0]);
        SoftPwm.softPwmWrite(LEDPINGREEN, colorValue[1]);
        SoftPwm.softPwmWrite(LEDPINBLUE, colorValue[2]);
    }
    
    protected void turnLedOff(){
        ledColorSet(Color.BLACK);
    }
}
