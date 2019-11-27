/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.wiring;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class Input implements WorkSpaceObject, Serializable {

    private final Point position;

    private final Model model;

    private boolean selected = false;

    public Input(Point position, int bits) {
        this.position = position;
        //model
        this.model = new Model(
                new Line[]{
                    new Line(new Point(0, -20), new Point(0, 20)),
                    new Line(new Point(-15, 5), new Point(0, 20)),
                    new Line(new Point(15, 5), new Point(0, 20))
                },
                null, null
        );
        //pins
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.OUTPUT, bits, "OUT", new Point.Double(0, 20))
        );
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(this.model.getWidth(), this.model.getHeight());
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        this.model.renderModel(g2, this.position, offset, screen, this.selected);
    }

    @Override
    public Propertie[] getProperties() {
        return null;
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {

        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public List<IOPin> getPins() {
        return this.model.getIOPins();
    }

    @Override
    public boolean select(Point position) {
        if (this.model.intersect(position, this.position)) {
            this.selected = true;
            return true;
        }
        this.selected = false;
        return false;
    }

    @Override
    public void unSelect() {
        this.selected = false;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public boolean compute() {
        return false;
    }

    public WorkSpaceObject cloneObject() {
        Input ret = new Input(
                new Point(this.position.x, this.position.y),
                this.model.getIOPins().get(0).getValue().length
        );
        ret.getModel().clone(this.model);
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

}
