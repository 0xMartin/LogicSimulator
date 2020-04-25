/* 
 * Copyright (C) 2020 Martin Krcma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
public class Xor extends WorkSpaceObject {

    private int inputs, bits;

    private IOPin OUT;

    /**
     * Create and gate
     *
     * @param position Position on workspace
     * @param bits Bits per wire
     * @param inputs Number of inputs (2<->5)
     */
    public Xor(Point position, int bits, int inputs) {
        super(position);
        this.bits = bits;
        this.inputs = Math.max(inputs, 2);
        super.setModel(new Model());
        this.OUT = Xor.createModel(super.getModel(), this.inputs, this.bits, false);
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
                    this.OUT = Xor.createModel(super.getModel(), this.inputs, this.bits, false);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public Xor cloneObject() {
        Xor ret = new Xor(Tools.copy(super.getPosition()), this.bits, this.inputs);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean compute() {
        //and fuction
        boolean[] out = new boolean[this.bits];
        for (int i = 0; i < this.bits; i++) {
            int count = 0;
            for (IOPin pin : super.getPins()) {
                if (pin.mode == IOPin.MODE.INPUT) {
                    if (pin.getValue()[i]) {
                        count++;
                    }
                }
            }
            out[i] = count == 1;
        }
        //write value
        return this.OUT.setValue(out);
    }

    /**
     * Create model for XOR/NXOR gate
     *
     * @param model Last model of and gate
     * @param inputs Number of inputs
     * @param bits Bit width all pins
     * @param outNegation True -> Genrate model for OR gate / False -> generate
     * model for NOR
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
        GOList.add(new Curve(-21, -25, 0, -14, 21, -25));
        GOList.add(new Curve(-21, -20, 0, -9, 21, -20));
        GOList.add(new Curve(-21, -20, -21, 0, 0, 14));
        GOList.add(new Curve(21, -20, 21, 0, 0, 14));
        if (outNegation) {
            GOList.add(new Circle(0, 21, 6));
        } else {
            GOList.add(new Line(0, 14, 0, 26));
        }
        //side input pin extenders
        if (inputs > 3) {
            int width = (inputs - 2) / 2 * 14 + 14;
            GOList.add(new Line(21, -25, width, -25));
            GOList.add(new Line(-21, -25, -width, -25));
        }

        //extended input
        //mid pin
        if (inputs % 2 != 0) {
            pinList.add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(0, -28.0)));
            GOList.add(new Line(0, -26, 0, -20));
        }
        //side pins
        int count = inputs / 2;
        for (int i = -count; i <= count; i += (i == -1 ? 2 : 1)) {
            pinList.add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(14 * i, -28.0)));
            GOList.add(new Line(14 * i, -26, 14 * i, Math.abs(i) == 1 ? -23 : -25));
        }

        //output
        IOPin out = new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0.0, 28.0));
        pinList.add(out);

        //rotate model to position of last model and inside of rotate compute size of model
        model.rotate(angle);

        return out;
    }

}
