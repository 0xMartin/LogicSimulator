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
import java.util.ArrayList;
import java.util.List;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;

/**
 *
 * @author Martin
 */
public class Buffer extends WorkSpaceObject {

    protected final IOPin IN, OUT;

    protected int bits;

    public Buffer(Point position, int bits) {
        super(position);

        this.bits = bits;

        //create gate model
        List<GraphicsObject> GOList = new ArrayList<>();
        GOList.add(new Line(0, 14, -9, -7));
        GOList.add(new Line(-9, -7, 9, -7));
        GOList.add(new Line(9, -7, 0, 14));
        GOList.add(new Line(0, 14, 0, 26));
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
    public Buffer cloneObject() {
        Buffer ret = new Buffer(Tools.copy(super.getPosition()), this.bits);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean compute() {
        //read value
        boolean[] in = this.IN.getValue();
        //buffer fuction
        boolean[] out = new boolean[in.length];
        System.arraycopy(in, 0, out, 0, in.length);
        //write value
        return this.OUT.setValue(out);
    }

}
