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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Fonts;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class LIFO extends WorkSpaceObject {

    //byte array = data of memory
    protected final LinkedList<Integer> stack;

    //pins
    protected final IOPin dataIn, dataOut, clear, clk, push_pop;

    protected int bits;

    /**
     * Create rom memory 8 bits per cell
     *
     * @param position Position of object
     * @param bits Bits
     */
    public LIFO(Point position, int bits) {
        super(position);
        this.bits = bits;
        this.stack = new LinkedList<>();

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-56, -42, -56, 42));
        GOList.add(new Line(-56, -42, 56, -42));
        GOList.add(new Line(56, -42, 56, 42));
        GOList.add(new Line(-56, 42, 56, 42));

        //pins
        this.dataIn = new IOPin(IOPin.MODE.INPUT, this.bits, "DATA IN", new Point.Double(-56.0, -28.0));
        this.clear = new IOPin(IOPin.MODE.INPUT, 1, "CLEAR", new Point.Double(-56.0, -14.0));
        this.clk = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-56.0, 14.0));
        this.clk.drawClkSymbol = true;
        this.push_pop = new IOPin(IOPin.MODE.INPUT, 1, "PUSH/POP", new Point.Double(-56.0, 28.0));
        this.dataOut = new IOPin(IOPin.MODE.OUTPUT, this.bits, "DATA OUT", new Point.Double(56.0, 0.0));
        model.getIOPins().add(this.dataOut);
        model.getIOPins().add(this.dataIn);
        model.getIOPins().add(this.push_pop);
        model.getIOPins().add(this.clear);
        model.getIOPins().add(this.clk);

        super.setModel(model);
        model.computeSize();
        model.disableRotation();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            //values
            g2.setColor(Colors.OBJECT);
            g2.drawRect(pos.x - 15, pos.y - 12, 30, 50);
            g2.setColor(Colors.TEXT);
            String val;
            for (int i = 0; i < 4; i++) {
                if (this.stack.size() - i - 1 >= 0) {
                    g2.setFont(i == 0 ? Fonts.SMALL.deriveFont(Font.BOLD) : Fonts.SMALL);
                    val = Convert.intToHex(this.stack.get(this.stack.size() - i - 1));
                    g2.drawString(val,
                            pos.x - g2.getFontMetrics().stringWidth(val) / 2,
                            pos.y + 19 + (1 - i) * g2.getFontMetrics().getAscent() + Tools.centerYString(g2.getFontMetrics()));
                }
            }
            //name
            g2.setFont(Fonts.BIG);
            g2.drawString("LIFO",
                    pos.x - g2.getFontMetrics().stringWidth("LIFO") / 2,
                    pos.y - 20
            );
            g2.setFont(Fonts.MEDIUM);
            //pin names
            g2.setFont(Fonts.SMALL);
            g2.drawString("Pu/Po",
                    pos.x - 52,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) + 28
            );
            g2.drawString("D",
                    pos.x - 52,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) - 28
            );
            g2.drawString("CLR",
                    pos.x - 52,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) - 14
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
                    this.stack.clear();
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    private boolean r_edge = false;

    @Override
    public boolean compute() {
        //clear
        if (this.clear.getValue()[0]) {
            boolean repaint = !this.stack.isEmpty();
            this.stack.clear();
            return this.dataOut.setValue(false) || repaint;
        }
        //clk action
        if (this.clk.getValue()[0]) {
            if (!this.r_edge) {
                this.r_edge = true;

                if (this.push_pop.getValue()[0]) {
                    //push data to stack
                    this.stack.add(Convert.bitsToInt(this.dataIn.getValue()));
                } else {
                    //pop data from stack
                    boolean[] out = this.dataOut.getValue();
                    if (!this.stack.isEmpty()) {
                        //get value from stack
                        Integer last = this.stack.getLast();
                        this.stack.removeLast();
                        boolean[] stackVal = Convert.intToBits(last, (short) this.bits);
                        //copy stack value to out value
                        System.arraycopy(stackVal, 0, out, 0, this.bits);
                    } else {
                        for(int i = 0; i < out.length; i++){
                            out[i] = false;
                        }
                    }
                }

            }
            
            return true;
        } else {
            this.r_edge = false;
        }
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new LIFO(
                Tools.copy(super.getPosition()),
                this.bits
        );
    }

    @Override
    public boolean error() {
        return false;
    }

}
