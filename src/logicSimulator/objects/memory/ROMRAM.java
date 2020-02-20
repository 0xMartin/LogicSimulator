/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.memory;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import logicSimulator.Project;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.ClickAction;
import logicSimulator.common.GraphicsObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Memory;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Fonts;
import logicSimulator.ui.Colors;
import window.RAMMemoryEditor;

/**
 *
 * @author Martin
 */
public class ROMRAM extends WorkSpaceObject implements ClickAction, Memory {

    private int adress_bits;

    //boolean array with data
    private boolean[] data;

    private IOPin dataOut, adress, clock;

    /**
     * Create rom memory
     *
     * @param position Position of object
     * @param bits Data bits
     * @param adress_bits Adress bits
     */
    public ROMRAM(Point position, int bits, int adress_bits) {
        super(position);

        this.adress_bits = adress_bits;

        this.data = new boolean[bits * (int) Math.pow(2, adress_bits)];

        Model model = new Model(
                new GraphicsObject[]{
                    new Line(new Point.Double(-56.0, -42.0), new Point.Double(-56.0, 42.0)),
                    new Line(new Point.Double(-56.0, -42.0), new Point.Double(56.0, -42.0)),
                    new Line(new Point.Double(56.0, -42.0), new Point.Double(56.0, 42.0)),
                    new Line(new Point.Double(-56.0, 42.0), new Point.Double(56.0, 42.0)),}
        );

        this.adress = new IOPin(IOPin.MODE.INPUT, adress_bits, "ADRESS", new Point.Double(-56.0, -28.0));
        this.clock = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-56.0, 28.0));
        this.dataOut = new IOPin(IOPin.MODE.OUTPUT, bits, "DATA", new Point.Double(56.0, 0.0));

        model.getIOPins().add(this.dataOut);
        model.getIOPins().add(this.adress);
        model.getIOPins().add(this.clock);

        super.setModel(model);
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            g2.setFont(Fonts.STATUS);
            g2.setColor(Colors.TEXT);
            g2.drawString(
                    "ROM RAM",
                    pos.x - g2.getFontMetrics().stringWidth("ROM RAM") / 2,
                    pos.y - 20
            );
            g2.setFont(Fonts.LABEL);
            //size
            String s = this.dataOut.getValue().length + " x " + Math.pow(2, this.adress_bits);
            g2.drawString(
                    s,
                    pos.x - g2.getFontMetrics().stringWidth(s) / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics())
            );
            //adress
            s = "0x" + Tools.convertToNumber(Tools.binToDec(this.adress.getValue()), 16);
            g2.drawString(
                    s,
                    pos.x - g2.getFontMetrics().stringWidth(s) / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) + 15
            );
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Data bits", super.getPins().get(0).getValue().length, Propertie.Type.BITS),
            new Propertie("Adress bits", this.adress_bits, Propertie.Type.BITS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Data bits":
                    this.dataOut.changeBitWidth(propt.getValueInt());
                    break;
                case "Adress bits":
                    this.adress_bits = Math.max(propt.getValueInt(), 0);
                    break;
            }
            //new length
            int L = this.dataOut.getValue().length * (int) Math.pow(2, this.adress_bits);
            if (L != this.data.length) {
                this.data = new boolean[L];
            }
        } catch (Exception ex) {
        }
    }

    private boolean r_edge = false;

    @Override
    public boolean compute() {
        if (this.clock.getValue()[0]) {
            if (!this.r_edge) {
                int adress_i = Tools.binToDec(this.adress.getValue()) * this.dataOut.getValue().length;
                boolean[] outBuffer = this.dataOut.getValue();
                if (adress_i >= 0 && adress_i + outBuffer.length < this.data.length) {
                    for (int i = 0; i < outBuffer.length; i++) {
                        outBuffer[i] = this.data[adress_i + i];
                    }
                }
                this.r_edge = true;
                return true;
            }
        } else {
            this.r_edge = false;
        }
        return false;
    }

    public WorkSpaceObject cloneObject() {
        return new ROMRAM(Tools.copy(super.getPosition()), this.dataOut.getValue().length, this.adress_bits);
    }

    @Override
    public boolean error() {
        return false;
    }

    @Override
    public void changeValue(Point cursor, Component parent, Project project) {
        //edit rom data
        RAMMemoryEditor editor = new RAMMemoryEditor(this, project);
        editor.setLocationRelativeTo(parent);
        editor.setAlwaysOnTop(true);
        editor.setVisible(true);
    }

    @Override
    public boolean[] getData() {
        return this.data;
    }

    @Override
    public int getBitWidth() {
        return this.dataOut.getValue().length;
    }

}
