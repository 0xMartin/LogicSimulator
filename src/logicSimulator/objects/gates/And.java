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
import logicSimulator.common.Curve;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Tools;

/**
 *
 * @author Martin
 */
public class And implements WorkSpaceObject, Serializable {

    private int inputs;

    private final Point position;

    private final Model model;

    private boolean select = false;

    /**
     * Create and gate
     *
     * @param position Position on workspace
     * @param bits Bits per wire
     * @param inputs Number of inputs (2<->5)
     */
    public And(Point position, int bits, int inputs) {
        //default
        this.position = position;
        this.inputs = inputs;
        //create gate model
        this.model = new Model(
                null,
                null,
                new Curve[]{
                    new Curve(new Point.Double(-24, -25), new Point.Double(-25, 28), new Point.Double(0, 25)),
                    new Curve(new Point.Double(24, -25), new Point.Double(25, 28), new Point.Double(0, 25))
                });
        this.buildModel(inputs, bits);
    }

    public void buildModel(int inputs, int bits) {
        switch (inputs) {
            case 2:
                this.model.lines = new Line[]{
                    new Line(new Point.Double(-24, -25), new Point.Double(24, -25)),
                    //pins in
                    new Line(new Point.Double(-20, -25), new Point.Double(-20, -37)),
                    new Line(new Point.Double(20, -25), new Point.Double(20, -37)),
                    //pin out
                    new Line(new Point.Double(0, 25), new Point.Double(0, 37))
                };
                //io pins
                this.model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, bits, "IN1", new Point.Double(-20, -40)));
                this.model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, bits, "IN2", new Point.Double(20, -40)));
                break;
        }
        //output pin
        this.model.getIOPins().add(new IOPin(IOPin.MODE.OUTPUT, bits, "OUT", new Point.Double(0, 40)));
        this.model.computeSize();
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
        this.model.renderModel(g2, this.position, offset, screen, this.select);
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", Tools.getIOPin(this.model.getIOPins(), "OUT").getValue().length),
            new Propertie("Inputs", this.inputs)
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
                case "Inputs":
                    this.inputs = propt.getValueInt();
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

    public And cloneObject() {
        And ret = new And(
                new Point(this.position.x, this.position.y),
                Tools.getIOPin(this.model.getIOPins(), "OUT").getValue().length,
                this.inputs
        );
        ret.getModel().clone(this.model);
        return ret;
    }

    @Override
    public boolean compute() {
        //read value
        int bits = this.model.getIOPins().get(0).getValue().length;
        //and fuction
        boolean[] out = new boolean[bits];
        for (int i = 0; i < bits; i++) {
            boolean result = true;
            for (IOPin pin : this.model.getIOPins()) {
                if (pin.mode == IOPin.MODE.INPUT) {
                    if (!pin.getValue()[i]) {
                        result = false;
                        break;
                    }
                }
            }
            out[i] = result;
        }

        //write value
        return this.model.getIOPins().get(this.model.getIOPins().size() - 1).setValue(out);
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
