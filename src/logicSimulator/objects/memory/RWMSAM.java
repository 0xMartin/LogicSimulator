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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.Project;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.ClickAction;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Memory;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Fonts;
import logicSimulator.ui.Colors;
import window.ROMMemoryEditor;

/**
 *
 * @author Martin
 */
public class RWMSAM extends WorkSpaceObject implements Memory, ClickAction {

    //number of address bits
    private int addressBits;

    //current address
    private int currentAddress = 0;

    //byte array = data of memory
    private byte[] data;

    //pins
    private final IOPin dataIn, dataOut, clear, clk, set, backward;

    private String ID;

    /**
     * Create rom memory 8 bits per cell
     *
     * @param position Position of object
     * @param addressBits Number of address bits
     * @param ID ID of memory
     */
    public RWMSAM(Point position, int addressBits, String ID) {
        super(position);
        this.addressBits = addressBits;
        this.ID = ID;
        this.data = new byte[(int) Math.pow(2, addressBits)];

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-56, -42, -56, 42));
        GOList.add(new Line(-56, -42, 56, -42));
        GOList.add(new Line(56, -42, 56, 42));
        GOList.add(new Line(-56, 42, 56, 42));

        //pins
        this.dataIn = new IOPin(IOPin.MODE.INPUT, 8, "DATA IN", new Point.Double(-56.0, -28.0));
        this.set = new IOPin(IOPin.MODE.INPUT, 1, "SET", new Point.Double(-56.0, -14.0));
        this.clk = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-56.0, 0.0));
        this.clk.drawClkSymbol = true;
        this.backward = new IOPin(IOPin.MODE.INPUT, 1, "BACKWARD/FORWARD", new Point.Double(-56.0, 14.0));
        this.clear = new IOPin(IOPin.MODE.INPUT, 1, "CLEAR", new Point.Double(-56.0, 28.0));
        this.dataOut = new IOPin(IOPin.MODE.OUTPUT, 8, "DATA OUT", new Point.Double(56.0, 0.0));
        model.getIOPins().add(this.dataOut);
        model.getIOPins().add(this.dataIn);
        model.getIOPins().add(this.clear);
        model.getIOPins().add(this.clk);
        model.getIOPins().add(this.backward);
        model.getIOPins().add(this.set);

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
            g2.drawRect(pos.x - 15, pos.y + 2, 30, 34);
            g2.setColor(Colors.TEXT);
            String val;
            for (int i = -1; i <= 1; i++) {
                int a = this.currentAddress + i;
                if (a < 0) {
                    a = this.data.length - 1;
                }
                if (a == this.data.length) {
                    a = 0;
                }
                g2.setFont(i == 0 ? Fonts.SMALL.deriveFont(Font.BOLD) : Fonts.SMALL);
                val = Convert.byteToHex(this.data[a]);
                g2.drawString(val,
                        pos.x - g2.getFontMetrics().stringWidth(val) / 2,
                        pos.y + 19 + i * g2.getFontMetrics().getAscent() + Tools.centerYString(g2.getFontMetrics()));
            }
            //name of memory
            g2.setFont(Fonts.BIG);
            g2.setColor(Colors.TEXT);
            g2.drawString("RWM SAM",
                    pos.x - g2.getFontMetrics().stringWidth("RWM SAM") / 2,
                    pos.y - 20);
            g2.setFont(Fonts.MEDIUM);
            //size
            String s = this.dataOut.getValue().length + " x " + Math.pow(2, this.addressBits);
            g2.drawString(s,
                    pos.x - g2.getFontMetrics().stringWidth(s) / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) - 10);
            //pin names
            g2.setFont(Fonts.SMALL);
            g2.drawString("D",
                    pos.x - 52,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) - 28);
            g2.drawString("B/F",
                    pos.x - 52,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) + 14);
            g2.drawString("S",
                    pos.x - 52,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) - 14);
            g2.drawString("CLR",
                    pos.x - 52,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) + 28);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("ID", this.ID),
            new Propertie("Address bits", this.addressBits, Propertie.Type.BITS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "ID":
                    this.ID = propt.getValueString();
                    break;
                case "Address bits":
                    this.addressBits = Math.max(propt.getValueInt(), 1);
                    break;
            }
            //resize byte array (data of memory)
            int L = (int) Math.pow(2, this.addressBits);
            if (L != this.data.length) {
                this.data = new byte[L];
            }
        } catch (NumberFormatException ex) {
        }
    }

    //this call after access to memory, get address of accessed cell (in action event id)
    private transient ActionListener onMemoryAccess = null;

    @Override
    public void setOnMemoryAccessListener(ActionListener listener) {
        this.onMemoryAccess = listener;
    }

    private boolean r_edge = false;

    @Override
    public boolean compute() {
        //clear
        if (this.clear.getValue()[0]) {
            this.currentAddress = 0;
            for (int i = 0; i < this.data.length; i++) {
                this.data[i] = 0x0;
            }
            return this.dataOut.setValue(false);
        }
        //clk actions
        if (this.clk.getValue()[0]) {
            if (!this.r_edge) {
                this.r_edge = true;

                //action
                if (this.set.getValue()[0]) {
                    //set value of cell
                    this.data[this.currentAddress] = Convert.bitsToByte(this.dataIn.getValue());
                } else {
                    //current address change
                    this.currentAddress += this.backward.getValue()[0] ? -1 : 1;
                    if (this.currentAddress == this.data.length) {
                        this.currentAddress = 0;
                    }
                    if (this.currentAddress < 0) {
                        this.currentAddress = this.data.length - 1;
                    }
                }

                //write data of cell in memory to output
                boolean[] ret = new boolean[8];
                //get value from memory data cell
                boolean[] cell = Convert.byteToBits(this.data[this.currentAddress]);
                //copy value of cell to output boolean[] buffer
                System.arraycopy(cell, 0, ret, 0, 8);

                //call listener
                if (this.onMemoryAccess != null) {
                    this.onMemoryAccess.actionPerformed(new ActionEvent(this, this.currentAddress, ""));
                }

                return this.dataOut.setValue(ret);
            }
        } else {
            this.r_edge = false;
        }
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new RWMSAM(
                Tools.copy(super.getPosition()),
                this.addressBits,
                this.ID
        );
    }

    @Override
    public boolean error() {
        return false;
    }

    @Override
    public void changeValue(Point cursor, Component parent, Project project) {
        //edit rom data
        ROMMemoryEditor editor = new ROMMemoryEditor(this, project);
        editor.setLocationRelativeTo(parent);
        editor.setVisible(true);
        editor.setTitle("RWM data editor : [" + this.ID + "]");
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public void uploadProgram(List<Byte> hex, int offset, boolean clear) {
        //clear
        if (clear) {
            for (int i = 0; i < this.data.length; i++) {
                this.data[i] = 0x0;
            }
        }

        int index = 0;
        for (int i = offset; i < this.data.length; i++) {
            //byte from list copy to data of memory
            this.data[i] = hex.get(index++);
            //if all hex data are loaded than break
            if (index == hex.size()) {
                break;
            }
        }
    }

}
