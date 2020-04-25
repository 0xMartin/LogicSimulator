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
public class JKFlipFlop extends RSFlipFlop {

    private final IOPin CLK;

    public JKFlipFlop(Point position) {
        super(position);
        super.title = "JK";
        super.SET.setLabel("J");
        super.RESET.setLabel("K");
        this.CLK = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-28.0, 0));
        this.CLK.drawClkSymbol = true;
        super.getModel().getIOPins().add(this.CLK);
    }

    private boolean r_edge = true;

    @Override
    public boolean compute() {
        if (this.CLK.getValue()[0]) {
            if (this.r_edge) {
                boolean[] setBits = super.SET.getValue();
                boolean[] resetBits = super.RESET.getValue();
                boolean[] out = super.OUT.getValue();
                //set and reset bits
                if (setBits[0] && resetBits[0]) {
                    out[0] = !out[0];
                } else if (setBits[0]) {
                    out[0] = true;
                } else if (resetBits[0]) {
                    out[0] = false;
                }
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
        this.title = "JK";
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new JKFlipFlop(
                Tools.copy(super.getPosition())
        );
    }

}
