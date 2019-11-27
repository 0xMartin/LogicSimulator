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
public class BitGet implements WorkSpaceObject, Serializable {

    private final Point position;

    private boolean[] avaiable;

    private final Model model;

    private boolean selected = false;

    public BitGet(Point position, boolean[] avaiable) {
        this.position = position;
        this.avaiable = avaiable;
        //model
        this.model = new Model(
                new Line[]{
                    new Line(new Point(0, 20), new Point(0, 0)),
                    new Line(new Point(0, 0), new Point(10, -10)),
                    new Line(new Point(0, 0), new Point(-10, -10)),
                    new Line(new Point(0, -20), new Point(10, -10)),
                    new Line(new Point(0, -20), new Point(-10, -10)),
                    //arrow
                    new Line(new Point(0, 15), new Point(-4, 11)),
                    new Line(new Point(0, 15), new Point(4, 11))
                },
                null, null
        );
        //pins
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.INPUT, 1, "IN", new Point.Double(0, -20))
        );
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.OUTPUT, 1, "OUT", new Point.Double(0, 20))
        );
        changeOutputWidth();
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
        Propertie[] p = new Propertie[this.avaiable.length + 1];
        p[0] = new Propertie("Input bits", this.avaiable.length);
        for (int i = 0; i < this.avaiable.length; i++) {
            p[1 + i] = new Propertie("Bit " + i, this.avaiable[i] ? "1" : "0");
        }
        return p;
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Input bits":
                    boolean[] b = new boolean[propt.getValueInt()];
                    for (int i = 0; i < b.length && i < this.avaiable.length; i++) {
                        b[i] = this.avaiable[i];
                    }
                    this.avaiable = b;
                    break;
                default:
                    String postfix = propt.getName().substring(4);
                    int id = Integer.parseInt(postfix);
                    if (id >= 0 && id < this.avaiable.length) {
                        this.avaiable[id] = propt.getValueInt() == 1;
                    }
                    break;
            }
            changeOutputWidth();
        } catch (NumberFormatException ex) {
        }
    }

    private void changeOutputWidth() {
        //count length of output value
        int out_length = 0;
        for (int i = 0; i < this.avaiable.length; i++) {
            if (this.avaiable[i]) {
                out_length++;
            }
        }
        this.model.getIOPins().get(1).changeBitWidth(out_length);
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
        //count length of output value
        int out_length = 0;
        for (int i = 0; i < this.avaiable.length; i++) {
            if (this.avaiable[i]) {
                out_length++;
            }
        }
        //output value
        boolean[] val = new boolean[out_length];
        //compute final value
        boolean[] in = this.model.getIOPins().get(0).getValue();
        for (int i = 0, j = 0; i < in.length && j < val.length; i++) {
            if (this.avaiable[i]) {
                val[j++] = in[i];
            }
        }
        //return
        return this.model.getIOPins().get(1).setValue(val);
    }

    public WorkSpaceObject cloneObject() {
        boolean[] av = new boolean[this.avaiable.length];
        System.arraycopy(this.avaiable, 0, av, 0, av.length);
        BitGet ret = new BitGet(new Point(this.position.x, this.position.y), av);
        ret.getModel().clone(this.model);
        return ret;
    }

    @Override
    public boolean error() {
        return this.avaiable.length != this.model.getIOPins().get(0).getValue().length;
    }

}
