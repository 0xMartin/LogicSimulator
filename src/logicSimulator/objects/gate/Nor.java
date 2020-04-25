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
import logicSimulator.common.Model;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;

/**
 *
 * @author Martin
 */
public class Nor extends WorkSpaceObject {

    private int inputs, bits;

    private IOPin OUT;

    /**
     * Create nor gate
     *
     * @param position Position on workspace
     * @param bits Bits per wire
     * @param inputs Number of inputs (2<->5)
     */
    public Nor(Point position, int bits, int inputs) {
        super(position);
        this.bits = bits;
        this.inputs = Math.max(inputs, 2);
        super.setModel(new Model());
        this.OUT = Or.createModel(super.getModel(), this.inputs, this.bits, true);
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
                    this.OUT = Or.createModel(super.getModel(), this.inputs, this.bits, true);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public Nor cloneObject() {
        Nor ret = new Nor(Tools.copy(super.getPosition()), this.bits, this.inputs);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean compute() {
        //and fuction
        boolean[] out = new boolean[this.bits];
        for (int i = 0; i < this.bits; i++) {
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
        return this.OUT.setValue(out);
    }

}
