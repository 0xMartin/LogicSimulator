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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.graphics.Line;
import logicSimulator.objects.IOPin;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class MagnitudeComparator extends WorkSpaceObject {

    private final IOPin A, B, A_HIGHER, A_EQUAL, A_LOWER;

    private int bits;

    public MagnitudeComparator(Point position, int bits) {
        super(position);

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-28, -21, -28, 21));
        GOList.add(new Line(28, -21, 28, 21));
        GOList.add(new Line(-28, 21, 28, 21));
        GOList.add(new Line(-28, -21, 28, -21));
        //symbols
        //>
        GOList.add(new Line(23, -14, 18, -10));
        GOList.add(new Line(23, -14, 18, -18));
        //=
        GOList.add(new Line(23, 3, 18, 3));
        GOList.add(new Line(23, -3, 18, -3));
        //<
        GOList.add(new Line(18, 14, 23, 10));
        GOList.add(new Line(18, 14, 23, 18));
        //s
        GOList.add(new Line(-24, -14, -16, -14));
        GOList.add(new Line(-24, 14, -16, 14));
        GOList.add(new Line(-16, -14, -16, 14));
        GOList.add(new Line(-16, 0, 4, 0));
        GOList.add(new Line(-1, -4, 4, 0));
        GOList.add(new Line(-1, 4, 4, 0));

        //pins
        this.A = new IOPin(IOPin.MODE.INPUT, bits, "A", new Point.Double(-28.0, -14.0));
        this.B = new IOPin(IOPin.MODE.INPUT, bits, "B", new Point.Double(-28.0, 14.0));
        this.A_HIGHER = new IOPin(IOPin.MODE.OUTPUT, 1, "A > B", new Point.Double(28.0, -14.0));
        this.A_EQUAL = new IOPin(IOPin.MODE.OUTPUT, 1, "A = B", new Point.Double(28.0, 0));
        this.A_LOWER = new IOPin(IOPin.MODE.OUTPUT, 1, "A < B", new Point.Double(28.0, 14.0));
        model.getIOPins().add(this.A);
        model.getIOPins().add(this.B);
        model.getIOPins().add(this.A_HIGHER);
        model.getIOPins().add(this.A_EQUAL);
        model.getIOPins().add(this.A_LOWER);

        super.setModel(model);
        model.computeSize();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());
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
                    this.A.changeBitWidth(this.bits);
                    this.B.changeBitWidth(this.bits);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean compute() {
        boolean[] vA = this.A.getValue();
        boolean[] vB = this.B.getValue();

        /**
         * Comparing: if bit from number A is 1 then add one point to ticksA and
         * if on same position in number B is 0 than set ticks B on 0 "A beat
         * value of B"
         */
        short ticksA = 0, ticksB = 0;
        for (int i = 0; i < this.bits; i++) {
            if (vA[i]) {
                ticksA++;
                if (!vB[i]) {
                    ticksB = 0;
                }
            }
            if (vB[i]) {
                ticksB++;
                if (!vA[i]) {
                    ticksA = 0;
                }
            }
        }

        if (ticksA < ticksB) {
            this.A_HIGHER.setValue(false);
            this.A_EQUAL.setValue(false);
            return this.A_LOWER.setValue(true);
        } else if (ticksA > ticksB) {
            this.A_EQUAL.setValue(false);
            this.A_LOWER.setValue(false);
            return this.A_HIGHER.setValue(true);
        } else {
            this.A_HIGHER.setValue(false);
            this.A_LOWER.setValue(false);
            return this.A_EQUAL.setValue(true);
        }

    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new MagnitudeComparator(Tools.copy(super.getPosition()), this.bits);
    }

    @Override
    public boolean error() {
        return false;
    }

}
