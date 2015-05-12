/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mandfer.sunfunpi4j;

import com.pi4j.io.gpio.GpioController;
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
    public static final int LEDPINYELLOW = 1;
    public static final int LEDPINGREEN = 1;
    public static final int LEDPINBLUE = 2;
    
    
    protected void ledInit(int ledPinA, int ledPinB) {
        SoftPwm.softPwmCreate(ledPinA, 0, 100);
        SoftPwm.softPwmCreate(ledPinB, 0, 100);        
    }
    
    protected void ledInit(int ledPinA, int ledPinB, int ledPinC) {
        ledInit(ledPinA, ledPinB);
        SoftPwm.softPwmCreate(ledPinC, 0, 100);
    } 
    
    protected void ledColorSet(Color color) {
        ledColorSet(color.getRed(), color.getGreen(), color.getBlue());
    }

    protected void ledColorSet(int colorA, int colorB) {
        SoftPwm.softPwmWrite(LEDPINRED, colorA);
        SoftPwm.softPwmWrite(LEDPINGREEN, colorB);
    }
    
    protected void ledColorSet(int colorA, int colorB, int colorC) {
        ledColorSet(colorA, colorB);
        SoftPwm.softPwmWrite(LEDPINBLUE, colorC);
    }
    
    protected void turnLedOff(){
        ledColorSet(Color.BLACK);
    }
}
