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
public class BitSet extends WorkSpaceObject {

    private int[] indexes;

    public BitSet(Point position, int[] indexes) {
        super(position);
        this.indexes = indexes;

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(0, 0, -7, 7));
        GOList.add(new Line(-7, 7, 0, 14));
        GOList.add(new Line(0, 14, 7, 7));
        GOList.add(new Line(7, 7, 0, 0));
        GOList.add(new Line(0, 0, 0, -14));
        GOList.add(new Line(0, 0, 7, -7));
        GOList.add(new Line(0, 0, -7, -7));

        //pins
        model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, 1, "IN", new Point.Double(0.0, -14.0)));
        model.getIOPins().add(new IOPin(IOPin.MODE.OUTPUT, Tools.max(this.indexes) + 1, "OUT", new Point.Double(0.0, 14.0)));

        super.setModel(model);
        model.computeSize();

        setWriteOnlyBits();
    }

    @Override
    public Propertie[] getProperties() {
        Propertie[] p = new Propertie[this.indexes.length + 1];
        p[0] = new Propertie("Input bits", this.indexes.length, Propertie.Type.BITS);
        for (int i = 0; i < this.indexes.length; i++) {
            p[1 + i] = new Propertie("Bit " + i, this.indexes[i]);
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
                    super.getPins().get(0).changeBitWidth(propt.getValueInt());
                    break;
                default:
                    String postfix = propt.getName().substring(4);
                    int id = Integer.parseInt(postfix);
                    if (id >= 0 && id < this.indexes.length) {
                        this.indexes[id] = propt.getValueInt();
                    }
                    break;
            }
            //change bit width of output pin
            super.getPins().get(1).changeBitWidth(Tools.max(this.indexes) + 1);
            //refresh write only bits
            setWriteOnlyBits();
        } catch (NumberFormatException ex) {
        }
    }

    private void setWriteOnlyBits() {
        IOPin pin = super.getPins().get(1);
        pin.writeOnly = new boolean[pin.getValue().length];
        for (int i : this.indexes) {
            pin.writeOnly[i] = true;
        }
    }

    @Override
    public boolean compute() {
        //compute final value
        boolean[] in = super.getPins().get(0).getValue();
        boolean[] val = new boolean[super.getPins().get(1).getValue().length];
        //reconect
        for (int i = 0; i < this.indexes.length; i++) {
            if (this.indexes[i] >= 0 && this.indexes[i] < val.length) {
                val[this.indexes[i]] = in[i];
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
        int[] av = new int[this.indexes.length];
        System.arraycopy(this.indexes, 0, av, 0, av.length);
        BitSet ret = new BitSet(Tools.copy(super.getPosition()), av);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

}
