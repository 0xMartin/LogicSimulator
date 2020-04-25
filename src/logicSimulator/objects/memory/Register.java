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
import logicSimulator.ui.Colors;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class Register extends WorkSpaceObject {

    private final IOPin dataIn, dataOut, clk, reset, shift_l, shift_r;

    private int bits;

    public Register(Point position, int bits) {
        super(position);
        this.bits = bits;

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-28, -42, -28, 42));
        GOList.add(new Line(-28, -42, 28, -42));
        GOList.add(new Line(28, -42, 28, 42));
        GOList.add(new Line(-28, 42, 28, 42));

        //pins
        this.shift_r = new IOPin(IOPin.MODE.INPUT, 1, "SHIFT RIGHT", new Point.Double(-28.0, 28.0));
        this.shift_l = new IOPin(IOPin.MODE.INPUT, 1, "SHIFT LEFT", new Point.Double(-28.0, 14.0));
        this.reset = new IOPin(IOPin.MODE.INPUT, 1, "RESET", new Point.Double(-28.0, 0.0));
        this.clk = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-28.0, -14.0));
        this.dataIn = new IOPin(IOPin.MODE.INPUT, bits, "DATA IN", new Point.Double(-28.0, -28.0));
        this.dataOut = new IOPin(IOPin.MODE.OUTPUT, bits, "DATA OUT", new Point.Double(28.0, 0.0));
        model.getIOPins().add(this.shift_r);
        model.getIOPins().add(this.shift_l);
        model.getIOPins().add(this.reset);
        model.getIOPins().add(this.clk);
        model.getIOPins().add(this.dataIn);
        model.getIOPins().add(this.dataOut);

        super.setModel(model);
        model.computeSize();
        model.disableRotation();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            //draw title
            g2.setFont(Fonts.BIG);
            g2.setColor(Colors.TEXT);
            g2.drawString(
                    "REG",
                    pos.x - g2.getFontMetrics().stringWidth("REG") / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) - 10
            );
            //size
            g2.drawString(
                    this.bits + "bit",
                    pos.x - g2.getFontMetrics().stringWidth(this.bits + "bit") / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) + 10
            );
        }
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
                    this.dataIn.changeBitWidth(this.bits);
                    this.dataOut.changeBitWidth(this.bits);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    private boolean r_edge = true;

    @Override
    public boolean compute() {
        if (this.clk.getValue()[0]) {
            if (this.r_edge) {

                //output buffer
                boolean[] dataOutBits = this.dataOut.getValue();

                //reset
                if (this.reset.getValue()[0]) {
                    for (int i = 0; i < dataOutBits.length; i++) {
                        dataOutBits[i] = false;
                    }
                    return true;
                }

                //input buffer
                boolean[] dataInBits = this.dataIn.getValue();

                //shifting
                boolean left = this.shift_l.getValue()[0];
                boolean right = this.shift_r.getValue()[0];

                //set
                for (int i = 0; i < this.bits; i++) {
                    int index = i;
                    index += left ? 1 : 0;
                    index += right ? -1 : 0;
                    index = index < 0 ? this.bits - 1 : index;
                    index = index == this.bits ? 0 : index;
                    dataOutBits[index] = dataInBits[i];
                }

                this.r_edge = false;
                return true;
            }
        } else {
            this.r_edge = true;
        }
        return false;
    }

    @Override
    public boolean error() {
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new Register(
                Tools.copy(super.getPosition()), this.bits
        );
    }

}
