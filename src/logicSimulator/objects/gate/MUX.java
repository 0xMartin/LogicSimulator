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
public class MUX extends WorkSpaceObject {

    private IOPin SELECT, OUT;

    private int inputs, bits;

    public MUX(Point position, int bits, int inputs) {
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
            g2.drawString("MUX",
                    pos.x + offX - g2.getFontMetrics().stringWidth("MUX") / 2,
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
            new Propertie("Inputs", this.inputs, Propertie.Type.INPUTS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.bits = propt.getValueInt();
                    super.getModel().getIOPins().stream().forEach((pin)->{
                        pin.changeBitWidth(this.bits);
                    });
                    break;
                case "Inputs":
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
            //get value of selected input pin
            boolean[] in = pins.get(address).getValue();
            //set value to out put pin
            return this.OUT.setValue(in);
        }else{
            return this.OUT.setValue(false);
        }
    }

    @Override
    public WorkSpaceObject cloneObject() {
        MUX ret = new MUX(Tools.copy(super.getPosition()), this.bits, this.inputs);
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
        GOList.add(new Line(-14, -height, -14, height));
        GOList.add(new Line(28, -height + 14, 28, height - 14));
        GOList.add(new Line(-14, -height, 28, -height + 14));
        GOList.add(new Line(-14, height, 28, height - 14));
        //dot for indication of first output
        GOList.add(new Circle(-8, -(inputs / 2) * 14, 2));

        //pins
        for (int i = -inputs / 2; i <= inputs / 2; i++) {
            if (i == 0 && inputs % 2 != 0 || i != 0) {
                pins.add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(-14, i * 14)));
            }
        }

        //selector
        this.SELECT = new IOPin(IOPin.MODE.INPUT, Tools.binLength(inputs - 1), "SELECT", new Point.Double(0, height - 7));
        pins.add(this.SELECT);

        //output
        this.OUT = new IOPin(IOPin.MODE.OUTPUT, bits, "OUT", new Point.Double(28, 0));
        pins.add(this.OUT);

        //rotate model to position of last model and inside of rotate compute size of model
        model.rotate(angle);
    }

}
