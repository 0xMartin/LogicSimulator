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
public class ROMRAM extends WorkSpaceObject implements ClickAction, Memory {

    //number of address bits
    private int addressBits;

    //current address
    private int currentAddress = 0;

    //byte array = data of memory
    private byte[] data;

    //pins
    private final IOPin dataOut, address, clk;

    //id for memory
    private String ID = "";

    /**
     * Create rom memory 8 bits per cell
     *
     * @param position Position of object
     * @param addressBits Number of address bits
     * @param ID ID of memory
     */
    public ROMRAM(Point position, int addressBits, String ID) {
        super(position);
        this.addressBits = Math.max(1, addressBits);
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
        this.address = new IOPin(IOPin.MODE.INPUT, addressBits, "ADDRESS", new Point.Double(-56.0, -28.0));
        this.clk = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-56.0, 28.0));
        this.clk.drawClkSymbol = true;
        this.dataOut = new IOPin(IOPin.MODE.OUTPUT, 8, "DATA", new Point.Double(56.0, 0.0));
        model.getIOPins().add(this.dataOut);
        model.getIOPins().add(this.address);
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
            g2.setFont(Fonts.BIG);
            g2.setColor(Colors.TEXT);
            g2.drawString("ROM RAM",
                    pos.x - g2.getFontMetrics().stringWidth("ROM RAM") / 2,
                    pos.y - 20);
            g2.setFont(Fonts.MEDIUM);
            //size
            String s = this.dataOut.getValue().length + " x " + Math.pow(2, this.addressBits);
            g2.drawString(s,
                    pos.x - g2.getFontMetrics().stringWidth(s) / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics()));
            //address
            s = "0x" + Convert.intToHex(this.currentAddress);
            g2.drawString(s,
                    pos.x - g2.getFontMetrics().stringWidth(s) / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) + 15);
            //pin names
            g2.setFont(Fonts.SMALL);
            g2.drawString("A",
                    pos.x - 52,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) - 28);
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
            this.address.changeBitWidth(this.addressBits);
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
        if (this.clk.getValue()[0]) {
            if (!this.r_edge) {

                //current selected address
                this.currentAddress = Convert.bitsToInt(this.address.getValue());

                //write data of cell in memory to output
                boolean[] outBuffer = this.dataOut.getValue();
                if (this.currentAddress >= 0 && this.currentAddress < this.data.length) {
                    //get value from memory data cell
                    boolean[] cell = Convert.byteToBits(this.data[this.currentAddress]);
                    //copy value of cell to output boolean[] buffer
                    System.arraycopy(cell, 0, outBuffer, 0, outBuffer.length);
                }

                //call listener
                if (this.onMemoryAccess != null) {
                    this.onMemoryAccess.actionPerformed(new ActionEvent(this, this.currentAddress, ""));
                }

                this.r_edge = true;
                return true;
            }
        } else {
            this.r_edge = false;
        }
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new ROMRAM(
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
        editor.setTitle("ROM data editor : [" + this.ID + "]");
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    /**
     * Get id of rom ram memory
     *
     * @return
     */
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
