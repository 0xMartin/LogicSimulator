/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.Point;

public interface PFHandler {

    /**
     * Zoom rendering of component
     *
     * @param ration (int) if ration is positive than zoom out if is negative
     * than zoom in
     */
    public void zoom(int ration);

    /**
     * Return position of cursor on component
     *
     * @return
     */
    public Point getCursorPosition();
    
    public void repaintPF();
    
}
