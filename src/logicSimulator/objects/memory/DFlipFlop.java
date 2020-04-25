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
package logicSimulator.objects.memory;

import java.awt.Point;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.IOPin;

/**
 *
 * @author Martin
 */
public class DFlipFlop extends RSFlipFlop {

    private final IOPin CLK, D;

    public DFlipFlop(Point position) {
        super(position);
        super.title = "D";
        this.D = this.SET;
        this.D.setLabel("D");
        this.CLK = this.RESET;
        this.CLK.setLabel("");
        this.CLK.drawClkSymbol = true;
    }

    private boolean r_edge = true;

    @Override
    public boolean compute() {
        if (this.CLK.getValue()[0]) {
            if (this.r_edge) {
                boolean[] dBits = super.SET.getValue();
                boolean[] out = super.OUT.getValue();
                //set value of input "D" to output
                out[0] = dBits[0];
                super.N_OUT.getValue()[0] = !out[0];
                this.r_edge = false;
                return true;
            }
        } else {
            this.r_edge = true;
        }
        return false;
    }

    @Override
    public void restore() {
        this.title = "D";
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new DFlipFlop(
                Tools.copy(super.getPosition())
        );
    }

}
