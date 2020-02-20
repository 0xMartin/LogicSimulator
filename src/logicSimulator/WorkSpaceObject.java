/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import logicSimulator.common.IOPin;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class WorkSpaceObject implements Serializable {

    //position of object
    private final Point position;

    //model of object
    private Model model;

    //if is true than object is selected
    private boolean select = false;

    public WorkSpaceObject(Point position) {
        this.position = position;
    }

    /**
     * Set model for this gate
     *
     * @param model New model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Get (center) position of object
     *
     * @return
     */
    public Point getPosition() {
        return this.position;
    }

    /**
     * Get size of object, model of object must exist
     *
     * @return
     */
    public Dimension getSize() {
        if (this.model == null) {
            return new Dimension(0, 0);
        } else {
            return new Dimension(this.model.getWidth(), this.model.getHeight());
        }
    }

    /**
     * Render object
     *
     * @param g2 Graphics 2D
     * @param offset Position offset (for detection if objecte is out of Render
     * panel)
     * @param screen Render panel size
     */
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        this.model.renderModel(g2, this.position, offset, screen, this.select);
    }

    /**
     * Get properties of object
     *
     * @return
     */
    public Propertie[] getProperties() {
        return null;
    }

    /**
     * Change propertie of object
     *
     * @param propt
     */
    public void changePropertie(Propertie propt) {
    }

    /**
     * Selecte objecte if cursor is on object
     *
     * @param cursor Cursor position
     * @return True -> object was selected
     */
    public boolean select(Point cursor) {
        if (this.model.intersect(cursor, this.position)) {
            this.select = true;
            return true;
        }
        return false;
    }

    /**
     * Select object
     *
     */
    public void select() {
        this.select = true;
    }

    /**
     * Unselect object
     *
     */
    public void unSelect() {
        this.select = false;
    }

    /**
     * Is object selected
     *
     * @return
     */
    public boolean isSelected() {
        return this.select;
    }

    /**
     * Get graphics model of component
     *
     * @return
     */
    public Model getModel() {
        return this.model;
    }

    /**
     * Compute function of this component and write results to output pins (not
     * write date to wireF)
     *
     * @return
     */
    public boolean compute() {
        return false;
    }

    /**
     * Return true if is some error in computing
     *
     * @return
     */
    public boolean error() {
        return !Tools.sameBitWidth(this.model.getIOPins(), IOPin.MODE.IO);
    }

    /**
     * Get io pins of component
     *
     * @return
     */
    public List<IOPin> getPins() {
        if (this.model != null) {
            return this.model.getIOPins();
        } else {
            return null;
        }
    }

}
