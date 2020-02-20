/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.gates;

import logicSimulator.common.Propertie;
import logicSimulator.common.IOPin;
import java.awt.Point;
import logicSimulator.common.Circle;
import logicSimulator.common.Curve;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.GraphicsObject;

/**
 *
 * @author Martin
 */
public class Nor extends WorkSpaceObject {

    private int inputs;

    /**
     * Create and gate
     *
     * @param position Position on workspace
     * @param bits Bits per wire
     * @param inputs Number of inputs (2<->5)
     */
    public Nor(Point position, int bits, int inputs) {
        super(position);

        this.inputs = inputs;

        //create gate model
        Model model = new Model(
                new GraphicsObject[]{
                    new Curve(new Point.Double(-21.0, -14.0), new Point.Double(0.0, 0.0), new Point.Double(21.0, -14.0)),
                    new Curve(new Point.Double(-21.0, -14.0), new Point.Double(-21.0, 14.0), new Point.Double(0.0, 28.0)),
                    new Curve(new Point.Double(21.0, -14.0), new Point.Double(21.0, 14.0), new Point.Double(0.0, 28.0)),
                    new Line(new Point.Double(-14.0, -12.0), new Point.Double(-14.0, -28.0)),
                    new Line(new Point.Double(14.0, -12.0), new Point.Double(14.0, -28.0)),
                    new Circle(new Point.Double(0.0, 35.0), 6)
                }
        );

        model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(-14.0, -28.0)));
        model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(14.0, -28.0)));
        model.getIOPins().add(new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0.0, 42.0)));

        super.setModel(model);
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie(
            "Bits",
            Tools.getLast(super.getPins()).getValue().length,
            Propertie.Type.BITS
            ),
            new Propertie("Inputs", this.inputs)
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
                case "Inputs":
                    this.inputs = propt.getValueInt();
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    public Nor cloneObject() {
        Nor ret = new Nor(
                Tools.copy(super.getPosition()),
                Tools.getLast(super.getPins()).getValue().length,
                this.inputs
        );
        ret.getModel().clone(super.getModel());
        return ret;
    }

    @Override
    public boolean compute() {
        //read value
        boolean[] in = super.getPins().get(0).getValue();
        //and fuction
        boolean[] out = new boolean[in.length];
        for (int i = 0; i < in.length; i++) {
            boolean result = false;
            for (IOPin pin : super.getPins()) {
                if (pin.mode == IOPin.MODE.INPUT) {
                    if (pin.getValue()[i]) {
                        result = true;
                        break;
                    }
                }
            }
            out[i] = !result;
        }
        //write value
        return Tools.getLast(super.getPins()).setValue(out);
    }

}
