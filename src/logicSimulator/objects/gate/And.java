/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.gate;

import logicSimulator.common.Propertie;
import logicSimulator.objects.IOPin;
import java.awt.Point;
import java.util.List;
import logicSimulator.common.Model;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.Circle;
import logicSimulator.graphics.Curve;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.graphics.Line;

/**
 *
 * @author Martin
 */
public class And extends WorkSpaceObject {

    protected int inputs, bits;

    protected IOPin OUT;

    /**
     * Create and gate
     *
     * @param position Position on workspace
     * @param bits Bits per wire
     * @param inputs Number of inputs
     */
    public And(Point position, int bits, int inputs) {
        super(position);
        this.bits = bits;
        this.inputs = Math.max(inputs, 2);
        super.setModel(new Model());
        this.OUT = And.createModel(super.getModel(), this.inputs, this.bits, false);
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.bits, Propertie.Type.BITS),
            new Propertie("Inputs", this.inputs, Propertie.Type.INPUTS)
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
                case "Inputs":
                    this.inputs = propt.getValueInt();
                    this.OUT = And.createModel(super.getModel(), this.inputs, this.bits, false);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public And cloneObject() {
        And ret = new And(Tools.copy(super.getPosition()), this.bits, this.inputs);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean compute() {
        //and fuction
        boolean[] out = new boolean[this.bits];
        for (int i = 0; i < this.bits; i++) {
            boolean result = true;
            for (IOPin pin : super.getPins()) {
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
        return this.OUT.setValue(out);
    }

    /**
     * Create model for AND/NAND gate
     *
     * @param model Last model of and gate
     * @param inputs Number of inputs
     * @param bits Bit width all pins
     * @param outNegation True -> Genrate model for and gate / False -> generate
     * model for nand
     * @return Output pin of and gate
     */
    public static IOPin createModel(Model model, int inputs, int bits, boolean outNegation) {
        List<GraphicsObject> GOList = model.getGraphicsObjects();
        List<IOPin> pinList = model.getIOPins();

        //angle
        int angle = model.getAngle();
        model.resetAngle();

        //clear
        pinList.clear();
        GOList.clear();

        //default model
        //"width"  = half width of line where is connected input pins
        int width = inputs <= 3 ? 21 : (inputs / 2 * 14);
        GOList.add(new Line(-width, -21, width, -21));
        GOList.add(new Curve(21, -21, 23, 15, 0, 14));
        GOList.add(new Curve(-21, -21, -23, 15, 0, 14));
        if (outNegation) {
            GOList.add(new Circle(0, 21, 6));
        } else {
            GOList.add(new Line(0, 26, 0, 14));
        }

        //extended input
        //mid pin
        if (inputs % 2 != 0) {
            pinList.add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(0, -28.0)));
            GOList.add(new Line(0, -26, 0, -21));
        }
        //side pins
        int count = inputs / 2;
        for (int i = -count; i <= count; i += (i == -1 ? 2 : 1)) {
            pinList.add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(14 * i, -28.0)));
            GOList.add(new Line(14 * i, -26, 14 * i, -21));
        }

        //output
        IOPin out = new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0.0, 28.0));
        pinList.add(out);

        //rotate model to position of last model and inside of rotate compute size of model
        model.rotate(angle);

        return out;
    }

}
