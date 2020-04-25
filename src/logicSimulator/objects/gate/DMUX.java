/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.gate;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.graphics.Circle;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.graphics.Line;
import logicSimulator.objects.IOPin;
import logicSimulator.ui.Colors;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class DMUX extends WorkSpaceObject {

    private IOPin SELECT, INPUT;

    private transient IOPin LAST_OUT;

    private int inputs, bits;

    public DMUX(Point position, int bits, int inputs) {
        super(position);
        super.setModel(new Model());
        this.inputs = Math.max(2, inputs);
        this.bits = bits;
        buildModel(super.getModel(), this.inputs, this.bits);
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        //model
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());

        //draw text
        if (stat) {
            int offX = 0, offY = 0;
            switch (super.getModel().getAngle()) {
                case 0:
                    offX = 7;
                    break;
                case 1:
                    offY = 7;
                    break;
                case 2:
                    offX = -7;
                    break;
                case 3:
                    offY = -7;
                    break;
            }
            g2.setColor(Colors.TEXT);
            g2.setFont(Fonts.MEDIUM);
            //title
            g2.drawString("DMX",
                    pos.x + offX - g2.getFontMetrics().stringWidth("DMX") / 2,
                    pos.y + offY + Tools.centerYString(g2.getFontMetrics())
            );
            //select bits
            g2.setFont(Fonts.SMALL);
            int b = this.SELECT.getValue().length;
            g2.drawString(b + " bit",
                    pos.x + offX - g2.getFontMetrics().stringWidth(b + " bit") / 2,
                    pos.y + offY + Tools.centerYString(g2.getFontMetrics()) + 12
            );
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.bits, Propertie.Type.BITS),
            new Propertie("Outputs", this.inputs, Propertie.Type.INPUTS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.bits = propt.getValueInt();
                    super.getModel().getIOPins().stream().forEach((pin) -> {
                        pin.changeBitWidth(this.bits);
                    });
                    break;
                case "Outputs":
                    this.inputs = propt.getValueInt();
                    buildModel(super.getModel(), this.inputs, this.bits);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean compute() {
        //get address of select
        int address = Convert.bitsToInt(this.SELECT.getValue());

        List<IOPin> pins = super.getModel().getIOPins();
        if (address >= 0 && address < pins.size() - 2) {
            //get value of input
            boolean[] in = this.INPUT.getValue();
            //clear low to last pin
            if (this.LAST_OUT != null) {
                this.LAST_OUT.setValue(false);
            }
            this.LAST_OUT = pins.get(address);
            //write input value to selected output
            return pins.get(address).setValue(in);
        } else {
            if (this.LAST_OUT != null) {
                this.LAST_OUT.setValue(false);
            }
        }

        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        DMUX ret = new DMUX(Tools.copy(super.getPosition()), this.bits, this.inputs);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

    /**
     * Create model for MUX
     *
     * @param model Last model of and gate
     * @param inputs Number of inputs 2 - 32
     * @param bits Bit width all pins
     * @return Output pin of and gate
     */
    private void buildModel(Model model, int inputs, int bits) {
        List<GraphicsObject> GOList = model.getGraphicsObjects();
        List<IOPin> pins = model.getIOPins();

        int angle = model.getAngle();
        model.resetAngle();

        //clear
        pins.clear();
        GOList.clear();

        //default model
        int height = (inputs / 2 - 1) * 14 + 32;
        GOList.add(new Line(28, -height, 28, height));
        GOList.add(new Line(-14, -height + 14, -14, height - 14));
        GOList.add(new Line(28, -height, -14, -height + 14));
        GOList.add(new Line(28, height, -14, height - 14));
        //dot for indication of first output
        GOList.add(new Circle(22, -(inputs / 2) * 14, 2));

        //pins
        for (int i = -inputs / 2; i <= inputs / 2; i++) {
            if (i == 0 && inputs % 2 != 0 || i != 0) {
                pins.add(new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(28, i * 14)));
            }
        }

        //selector
        this.SELECT = new IOPin(IOPin.MODE.INPUT, Tools.binLength(inputs - 1), "SELECT", new Point.Double(14, height - 7));
        pins.add(this.SELECT);

        //output
        this.INPUT = new IOPin(IOPin.MODE.INPUT, bits, "INPUT", new Point.Double(-14, 0));
        pins.add(this.INPUT);

        //rotate model to position of last model and inside of rotate compute size of model
        model.rotate(angle);
    }

}
