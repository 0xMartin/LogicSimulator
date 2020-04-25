/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.wiring;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class BitGet extends WorkSpaceObject {

    private boolean[] avaiable;

    public BitGet(Point position, boolean[] avaiable) {
        super(position);
        this.avaiable = avaiable;

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(0, 0, -7, -7));
        GOList.add(new Line(0, -14, -7, -7));
        GOList.add(new Line(0, -14, 7, -7));
        GOList.add(new Line(7, -7, 0, 0));
        GOList.add(new Line(0, 0, 0, 14));
        GOList.add(new Line(0, 14, 7, 7));
        GOList.add(new Line(0, 14, -7, 7));

        //pins
        model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, 1, "IN", new Point.Double(0.0, -14.0)));
        model.getIOPins().add(new IOPin(IOPin.MODE.OUTPUT, 1, "OUT", new Point.Double(0.0, 14.0)));

        super.setModel(model);
        model.computeSize();

        changeOutputWidth();
    }

    @Override
    public Propertie[] getProperties() {
        Propertie[] p = new Propertie[this.avaiable.length + 1];
        p[0] = new Propertie("Input bits", this.avaiable.length, Propertie.Type.BITS);
        for (int i = 0; i < this.avaiable.length; i++) {
            p[1 + i] = new Propertie(
                    "Bit " + i,
                    this.avaiable[i] ? "True" : "False",
                    Propertie.Type.LOGIC
            );
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
                        this.avaiable[id] = propt.getValueString().equals("True");
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
        super.getPins().get(1).changeBitWidth(out_length);
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
        boolean[] in = super.getPins().get(0).getValue();
        for (int i = 0, j = 0; i < in.length && j < val.length; i++) {
            if (this.avaiable[i]) {
                val[j++] = in[i];
            }
        }
        //return
        return super.getPins().get(1).setValue(val);
    }

    @Override
    public boolean error() {
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        boolean[] av = new boolean[this.avaiable.length];
        System.arraycopy(this.avaiable, 0, av, 0, av.length);
        BitGet ret = new BitGet(Tools.copy(super.getPosition()), av);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

}
