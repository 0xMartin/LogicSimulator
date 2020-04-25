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
package logicSimulator.objects.aritmetic;

import java.awt.Point;
import logicSimulator.Tools;
import logicSimulator.objects.IOPin;

/**
 *
 * @author Martin
 */
public class BitSub extends BitAdd {

    private final IOPin BIN, BOUT;
    
    public BitSub(Point position, int bits) {
        super(position, bits);
        super.title = "-";
        this.BIN = super.CIN;
        this.BIN.setLabel("BIN");
        this.BOUT = super.COUT;
        this.BOUT.setLabel("BOUT");
    }

    @Override
    public boolean compute() {
        boolean[] vA = super.A.getValue();
        boolean[] vB = super.B.getValue();

        boolean[] ret = new boolean[super.bits];
        boolean borrow = this.BIN.getValue()[0];
        for (int i = 0; i < super.bits; i++) {
            ret[i] = vA[i] ^ vB[i] ^ borrow;
            //borrow out
            borrow = !vA[i] && vB[i] || borrow && (!vA[i] || vB[i]);
        }

        this.BOUT.getValue()[0] = borrow;
        return super.OUT.setValue(ret) || borrow;
    }

    @Override
    public void restore() {
        this.title = "-";
    }
    
    @Override
    public BitSub cloneObject() {
        return new BitSub(Tools.copy(super.getPosition()), super.bits);
    }

    @Override
    public boolean error() {
        return false;
    }

}
