/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import data.PropertieReader;

/**
 * Logic simulator component
 * @author Martin
 */
public interface LSComponent {
 
    public void init(LogicSimulatorCore core, PropertieReader propt) throws Exception;
 
    public void run();

    public void stop();
    
}
