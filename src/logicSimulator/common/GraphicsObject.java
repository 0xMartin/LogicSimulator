/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Martin
 */
public interface GraphicsObject {
    
    /**
     * Draw graphics object
     * @param g2 2D Graphics context
     * @param xOffset X offset of rendering
     * @param yOffset Y offest of rendering
     */
    public void draw(Graphics2D g2, int xOffset, int yOffset);
    
    /**
     * Clone object
     * @return 
     */
    public GraphicsObject cloneObject();
    
    /**
     * Get all points of this graphics object
     * @return 
     */
    public Point.Double[] getPoints();
    
}
