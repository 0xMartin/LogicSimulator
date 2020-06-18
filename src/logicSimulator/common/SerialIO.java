/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

/**
 *
 * @author Martin
 */
public interface SerialIO {
   
    
    public String getID();
    
    public String getType();
    
    public byte getSetValue();
    
    public byte getResetValue();
    
    public void set();
    
    public void reset();
    
    public boolean getValue();
    
}
