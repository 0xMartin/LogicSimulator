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
public class BitSet implements WorkSpaceObject, Serializable {

    private final Point position;

    private int[] indexes;

    private final Model model;

    private boolean selected = false;

    public BitSet(Point position, int[] indexes, int bits_out) {
        this.position = position;
        this.indexes = indexes;
        this.model = new Model(
                new Line[]{
                    new Line(new Point(0, -20), new Point(0, 0)),
                    new Line(new Point(0, 0), new Point(10, 10)),
                    new Line(new Point(0, 0), new Point(-10, 10)),
                    new Line(new Point(0, 20), new Point(10, 10)),
                    new Line(new Point(0, 20), new Point(-10, 10)),
                    //arrow
                    new Line(new Point(0, 0), new Point(-4, -4)),
                    new Line(new Point(0, 0), new Point(4, -4))
                },
                null, null
        );
        //pins
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.INPUT, 1, "IN", new Point.Double(0, -20))
        );
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.OUTPUT, bits_out, "OUT", new Point.Double(0, 20))
        );
        setWriteOnlyBits();
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
        Propertie[] p = new Propertie[this.indexes.length + 2];
        p[0] = new Propertie("Input bits", this.indexes.length);
        p[1] = new Propertie("Output bits", this.model.getIOPins().get(1).getValue().length);
        for (int i = 0; i < this.indexes.length; i++) {
            p[2 + i] = new Propertie("Bit " + i, this.indexes[i]);
        }
        return p;
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Input bits":
                    int[] v = new int[propt.getValueInt()];
                    for (int i = 0; i < v.length && i < this.indexes.length; i++) {
                        v[i] = this.indexes[i];
                    }
                    this.indexes = v;
                    this.model.getIOPins().get(1).changeBitWidth(propt.getValueInt());
                    break;
                case "Output bits":
                    this.model.getIOPins().get(1).changeBitWidth(propt.getValueInt());
                    break;
                default:
                    String postfix = propt.getName().substring(4);
                    int id = Integer.parseInt(postfix);
                    if (id >= 0 && id < this.indexes.length) {
                        this.indexes[id] = propt.getValueInt();
                    }
                    break;
            }
            setWriteOnlyBits();
        } catch (NumberFormatException ex) {
        }
    }

    private void setWriteOnlyBits() {
        IOPin pin = this.model.getIOPins().get(1);
        pin.writeOnly = new boolean[pin.getValue().length];
        for (int i : this.indexes) {
            pin.writeOnly[i] = true;
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
        //compute final value
        boolean[] in = this.model.getIOPins().get(0).getValue();
        boolean[] val = new boolean[this.model.getIOPins().get(1).getValue().length];
        //reconect
        for (int i = 0; i < this.indexes.length; i++) {
            if (this.indexes[i] >= 0 && this.indexes[i] < val.length) {
                val[this.indexes[i]] = in[i];
            }
        }
        //return
        return this.model.getIOPins().get(1).setValue(val);
    }

    public WorkSpaceObject cloneObject() {
        int[] av = new int[this.indexes.length];
        System.arraycopy(this.indexes, 0, av, 0, av.length);
        BitSet ret = new BitSet(
                new Point(this.position.x, this.position.y),
                av,
                this.model.getIOPins().get(1).getValue().length
        );
        ret.getModel().clone(this.model);
        return ret;
    }

    @Override
    public boolean error() {
        return this.indexes.length != this.model.getIOPins().get(0).getValue().length;
    }

}
