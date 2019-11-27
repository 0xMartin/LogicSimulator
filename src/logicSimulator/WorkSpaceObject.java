/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import logicSimulator.common.Propertie;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import logicSimulator.common.Model;
import logicSimulator.common.IOPin;

/**
 *
 * @author Martin
 */
public interface WorkSpaceObject {

    //center position of object
    public Point getPosition();

    /**
     * Get size
     * @return 
     */
    public Dimension getSize();

    /**
     * Render workspace object
     * @param g2 Graphics
     * @param offset
     * @param screen Screen size
     */
    public void render(Graphics2D g2, Point offset, Dimension screen);

    /**
     * Get properties
     * @return 
     */
    public Propertie[] getProperties();

    /**
     * Change propertie
     * @param propt 
     */
    public void changePropertie(Propertie propt);

    /**
     * Select object
     * @param cursor Cursor position
     * @return Return true if was selected
     */
    public boolean select(Point cursor);

    /**
     * Unselect object
     */
    public void unSelect();
    
    /**
     * Is object selected
     * @return 
     */
    public boolean isSelected();
    
    /**
     * Get model of object
     * @return 
     */
    public Model getModel();

    /**
     * Return true if value was changed
     * @return 
     */
    public boolean compute();
    
    /**
     * true -> error
     * @return 
     */
    public boolean error();
    
    /**
     * Get all iopins of this model
     * @return 
     */
    public List<IOPin> getPins();
    
}
