/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.gates;

import logicSimulator.common.Propertie;
import logicSimulator.common.IOPin;
import java.awt.Point;
import logicSimulator.common.Circle;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.GraphicsObject;

/**
 *
 * @author Martin
 */
public class Not extends WorkSpaceObject {

    public Not(Point position, int bits) {
        super(position);

        //create gate model
        Model model = new Model(
                new GraphicsObject[]{
                    new Line(new Point.Double(0.0, 14.0), new Point.Double(-9.0, -7.0)),
                    new Line(new Point.Double(-9.0, -7.0), new Point.Double(9.0, -7.0)),
                    new Line(new Point.Double(9.0, -7.0), new Point.Double(0.0, 14.0)),
                    new Circle(new Point.Double(0.0, 21.0), 6),
                    new Line(new Point.Double(0.0, -14.0), new Point.Double(0.0, -7.0))
                }
        );

        //pins
        model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(0.0, -14.0)));
        model.getIOPins().add(new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0.0, 28.0)));

        super.setModel(model);
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie(
            "Bits",
            Tools.getLast(super.getPins()).getValue().length,
            Propertie.Type.BITS
            )
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    super.getPins().forEach((pin) -> {
                        pin.changeBitWidth(propt.getValueInt());
                    });
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    public Not cloneObject() {
        Not ret = new Not(
                Tools.copy(super.getPosition()),
                Tools.getLast(super.getPins()).getValue().length
        );
        ret.getModel().clone(super.getModel());
        return ret;
    }

    @Override
    public boolean compute() {
        //read value
        boolean[] in = super.getModel().getIOPins().get(0).getValue();
        //not fuction
        boolean[] out = new boolean[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = !in[i];
        }
        //write value
        return Tools.getLast(super.getPins()).setValue(out);
    }

}
