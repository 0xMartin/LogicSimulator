/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.gates;

import logicSimulator.common.Propertie;
import logicSimulator.common.IOPin;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Tools;

/**
 *
 * @author Martin
 */
public class Buffer implements WorkSpaceObject, Serializable {

    private final Point position;

    public static Dimension SIZE = new Dimension(90, 30);

    private final Model model;

    private boolean select = false;

    public Buffer(Point position, int bits) {
        //default
        this.position = position;
        //create gate model
        this.model = new Model(
                new Line[]{
                    //body
                    new Line(new Point.Double(0, 7), new Point.Double(10, -15)),
                    new Line(new Point.Double(0, 7), new Point.Double(-10, -15)),
                    new Line(new Point.Double(-10, -15), new Point.Double(10, -15)),
                    //pins
                    new Line(new Point.Double(0, -15), new Point.Double(0, -18)),
                    new Line(new Point.Double(0, 18), new Point.Double(0, 7))
                },
                null, null
        );
        this.model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, bits, "IN", new Point.Double(0, -20)));
        this.model.getIOPins().add(new IOPin(IOPin.MODE.OUTPUT, bits, "OUT", new Point.Double(0, 20)));
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public Dimension getSize() {
        return Buffer.SIZE;
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        this.model.renderModel(g2, this.position, offset, screen, this.select);
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", Tools.getIOPin(this.model.getIOPins(), "OUT").getValue().length)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.model.getIOPins().forEach((pin) -> {
                        pin.changeBitWidth(propt.getValueInt());
                    });
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean select(Point cursor) {
        if (this.model.intersect(cursor, this.position)) {
            this.select = true;
            return true;
        }
        return false;
    }

    @Override
    public void unSelect() {
        this.select = false;
    }

    @Override
    public boolean isSelected() {
        return this.select;
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    public Buffer cloneObject() {
        Buffer ret = new Buffer(
                new Point(this.position.x, this.position.y),
                Tools.getIOPin(this.model.getIOPins(), "OUT").getValue().length
        );
        ret.getModel().clone(this.model);
        return ret;
    }

    @Override
    public boolean compute() {
        //read value
        boolean[] in = this.model.getIOPins().get(0).getValue();
        //not fuction
        boolean[] out = new boolean[in.length];
        System.arraycopy(in, 0, out, 0, in.length);
        //write value
        return this.model.getIOPins().get(1).setValue(out);
    }

    @Override
    public boolean error() {
        return !Tools.sameBitWidth(this.model.getIOPins(), IOPin.MODE.IO);
    }

    @Override
    public List<IOPin> getPins() {
        return this.model.getIOPins();
    }

}
