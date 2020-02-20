/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.memory;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.GraphicsObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Fonts;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class Counter extends WorkSpaceObject {

    private final IOPin clock, set, dataIn, dataOut, overflow;

    /**
     * Create counter
     *
     * @param position Position of object
     * @param bits How many bits
     */
    public Counter(Point position, int bits) {
        super(position);

        Model model = new Model(
                new GraphicsObject[]{
                    new Line(new Point.Double(-56.0, -28.0), new Point.Double(-56.0, 28.0)),
                    new Line(new Point.Double(-56.0, -28.0), new Point.Double(56.0, -28.0)),
                    new Line(new Point.Double(56.0, -28.0), new Point.Double(56.0, 28.0)),
                    new Line(new Point.Double(-56.0, 28.0), new Point.Double(56.0, 28.0)),}
        );

        this.dataIn = new IOPin(IOPin.MODE.INPUT, bits, "DATA IN", new Point.Double(-56.0, -14.0));
        this.clock = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-56.0, 0.0));
        this.set = new IOPin(IOPin.MODE.INPUT, 1, "SET", new Point.Double(-56.0, 14.0));
        this.dataOut = new IOPin(IOPin.MODE.OUTPUT, bits, "DATA OUT", new Point.Double(56.0, -14.0));
        this.overflow = new IOPin(IOPin.MODE.OUTPUT, 1, "OVERFLOW", new Point.Double(56.0, 14.0));

        model.getIOPins().add(this.dataIn);
        model.getIOPins().add(this.dataOut);
        model.getIOPins().add(this.clock);
        model.getIOPins().add(this.set);
        model.getIOPins().add(this.overflow);

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
                    "COUNTER",
                    pos.x - g2.getFontMetrics().stringWidth("COUNTER") / 2,
                    pos.y - 10
            );
            g2.setFont(Fonts.LABEL);
            String s = this.dataOut.getValue().length + " bit";
            g2.drawString(
                    s,
                    pos.x - g2.getFontMetrics().stringWidth(s) / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics())
            );
            s = "0x" + Tools.convertToNumber(Tools.binToDec(this.dataOut.getValue()), 16);;
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
            new Propertie("Bits", this.dataOut.getValue().length, Propertie.Type.BITS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.dataIn.changeBitWidth(propt.getValueInt());
                    this.dataOut.changeBitWidth(propt.getValueInt());
                    break;
            }
        } catch (Exception ex) {
        }
    }

    private boolean r_edge = false;

    @Override
    public boolean compute() {

        this.overflow.getValue()[0] = false;

        //set
        if (this.set.getValue()[0]) {
            boolean[] in = this.dataIn.getValue();
            if (in.length == this.dataOut.getValue().length) {
                this.dataOut.setValue(in);
            }
            //write 
            return true;
        }

        //clk
        if (this.clock.getValue()[0]) {
            if (!this.r_edge) {

                //add 1 to counter
                boolean carry = true;
                boolean[] counter_reg = this.dataOut.getValue();
                for (int i = 0; i < counter_reg.length; i++) {
                    if (carry) {
                        carry = counter_reg[i];
                        counter_reg[i] = !counter_reg[i];
                    }
                }
                
                //overflow
                if (carry) {
                    this.overflow.getValue()[0] = true;
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
        return new Counter(Tools.copy(super.getPosition()), super.getPins().get(0).getValue().length);
    }

    @Override
    public boolean error() {
        return false;
    }

}
