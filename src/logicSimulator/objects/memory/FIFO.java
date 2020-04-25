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
import logicSimulator.Convert;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.ui.Fonts;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class FIFO extends LIFO {

    /**
     * Create rom memory 8 bits per cell
     *
     * @param position Position of object
     * @param bits Bits
     */
    public FIFO(Point position, int bits) {
        super(position, bits);    
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
                if (i < super.stack.size()) {
                    g2.setFont(i == 0 ? Fonts.SMALL.deriveFont(Font.BOLD) : Fonts.SMALL);
                    val = Convert.intToHex(super.stack.get(i));
                    g2.drawString(val,
                            pos.x - g2.getFontMetrics().stringWidth(val) / 2,
                            pos.y + 19 + (1 - i) * g2.getFontMetrics().getAscent() + Tools.centerYString(g2.getFontMetrics()));
                }
            }
            //name
            g2.setFont(Fonts.BIG);
            g2.drawString("FIFO",
                    pos.x - g2.getFontMetrics().stringWidth("FIFO") / 2,
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
                        Integer last = this.stack.getFirst();
                        this.stack.removeFirst();
                        boolean[] stackVal = Convert.intToBits(last, (short) this.bits);
                        //copy stack value to out value
                        System.arraycopy(stackVal, 0, out, 0, this.bits);
                    } else {
                        for (int i = 0; i < out.length; i++) {
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
        return new FIFO(
                Tools.copy(super.getPosition()),
                super.bits
        );
    }

}
