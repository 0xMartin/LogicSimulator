/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.gate;

import logicSimulator.common.Propertie;
import logicSimulator.objects.IOPin;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.Circle;
import logicSimulator.graphics.GraphicsObject;

/**
 *
 * @author Martin
 */
public class Not extends WorkSpaceObject {

    private final IOPin IN, OUT;

    private int bits;

    public Not(Point position, int bits) {
        super(position);
        this.bits = bits;

        //create gate model
        List<GraphicsObject> GOList = new ArrayList<>();
        GOList.add(new Line(0, 14, -9, -7));
        GOList.add(new Line(-9, -7, 9, -7));
        GOList.add(new Line(9, -7, 0, 14));
        GOList.add(new Circle(0, 21, 6));
        GOList.add(new Line(0, -12, 0, -7));

        Model model = new Model(GOList);

        //io pins
        this.IN = new IOPin(IOPin.MODE.INPUT, this.bits, "", new Point.Double(0.0, -14.0));
        this.OUT = new IOPin(IOPin.MODE.OUTPUT, this.bits, "", new Point.Double(0.0, 28.0));
        model.getIOPins().add(this.IN);
        model.getIOPins().add(this.OUT);

        super.setModel(model);
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.bits, Propertie.Type.BITS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.bits = propt.getValueInt();
                    super.getPins().forEach((pin) -> {
                        pin.changeBitWidth(this.bits);
                    });
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public Not cloneObject() {
        Not ret = new Not(Tools.copy(super.getPosition()), this.bits);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean compute() {
        //read value
        boolean[] in = this.IN.getValue();
        //not fuction
        boolean[] out = new boolean[this.bits];
        for (int i = 0; i < this.bits; i++) {
            out[i] = !in[i];
        }
        //write value
        return this.OUT.setValue(out);
    }

}
