package org.markadr;

/**
 *
 * @author Mark de Reeper
 */
public interface RotaryEncoderListener {
    
    void up(long encoderValue);
    void down(long encoderValue);
}
