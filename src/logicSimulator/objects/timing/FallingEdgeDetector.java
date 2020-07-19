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
package logicSimulator.objects.timing;

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
public class FallingEdgeDetector extends WorkSpaceObject {

    private final IOPin in, out;

    public FallingEdgeDetector(Point position, int bits) {
        super(position);
        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);

        //body
        GOList.add(new Line(-28, -14, -28, 14));
        GOList.add(new Line(-28, -14, 28, -14));
        GOList.add(new Line(28, -14, 28, 14));
        GOList.add(new Line(-28, 14, 28, 14));

        //symbol
        GOList.add(new Line(-5, -10, 5, 10));
        GOList.add(new Line(0, 0, 2, -6));
        GOList.add(new Line(0, 0, -6, -2));
        GOList.add(new Line(-5, -10, -21, -10));
        GOList.add(new Line(5, 10, 21, 10));

        //pins
        this.in = new IOPin(IOPin.MODE.INPUT, bits, "IN", new Point.Double(-28, 0));
        this.out = new IOPin(IOPin.MODE.OUTPUT, bits, "OUT", new Point.Double(28, 0));
        model.getIOPins().add(this.in);
        model.getIOPins().add(this.out);

        this.lastVal = new boolean[bits];

        super.setModel(model);
        model.computeSize();
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", super.getPins().get(0).getValue().length, Propertie.Type.BITS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.in.changeBitWidth(propt.getValueInt());
                    this.out.changeBitWidth(propt.getValueInt());
                    this.lastVal = new boolean[propt.getValueInt()];
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    //last values for edge detection
    private boolean[] lastVal;

    @Override
    public boolean compute() {
        boolean changed = false;

        //falling edge detection
        boolean[] outBuf = this.out.getValue();
        for (int i = 0; i < this.lastVal.length && i < outBuf.length
                && i < this.in.getValue().length; ++i) {
            if (this.lastVal[i] && !this.in.getValue()[i]) {
                outBuf[i] = true;
                changed = true;
            } else {
                outBuf[i] = false;
            }
        }

        //store current bits to last value
        for (int i = 0; i < this.lastVal.length; ++i) {
            this.lastVal[i] = this.in.getValue()[i];
        }

        return changed;
    }

    @Override
    public FallingEdgeDetector cloneObject() {
        FallingEdgeDetector ret = new FallingEdgeDetector(
                Tools.copy(super.getPosition()), super.getPins().get(0).getValue().length);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

}
