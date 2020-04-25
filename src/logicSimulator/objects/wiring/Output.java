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
package logicSimulator.objects.wiring;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Colors;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class Output extends WorkSpaceObject {

    private String label = "";

    //this be will be visible only outside of module
    private IOPin output;

    public Output(Point position, int bits) {
        super(position);

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(0, -14, 0, 14));
        GOList.add(new Line(0, 14, 14, 0));
        GOList.add(new Line(0, 14, -14, 0));

        //pins
        this.output = new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0, 0));
        model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(0.0, -14.0)));

        super.setModel(model);
        model.computeSize();
    }

    public IOPin getOutput() {
        return this.output;
    }

    public void setOutput(IOPin pin) {
        this.output = pin;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        boolean stat = super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());
        if (stat) //draw label
        {
            g2.setColor(Colors.OBJECT);
            g2.setFont(Fonts.MEDIUM);
            //draw label
            g2.drawString(
                    this.label,
                    (int) (super.getPosition().x + super.getModel().getBoundsMax().x + 9),
                    (int) (super.getPosition().y + super.getModel().getBoundsMin().y
                    + (super.getModel().getAngle() == 1 ? 0 : super.getModel().getHeight() / 2))
            );
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", super.getPins().get(0).getValue().length, Propertie.Type.BITS),
            new Propertie("Label", this.label)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    int i = propt.getValueInt();
                    i = Math.max(i, 1);
                    i = Math.min(i, 128);
                    super.getPins().get(0).changeBitWidth(i);
                    break;
                case "Label":
                    this.label = propt.getValueString();
                    this.output.setLabel(this.label);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean compute() {
        IOPin pin = super.getPins().get(0);
        return this.output.setValue(pin.getValue());
    }

    @Override
    public WorkSpaceObject cloneObject() {
        Output ret = new Output(
                new Point(super.getPosition().x, super.getPosition().y),
                this.output.getValue().length
        );
        ret.output = this.output.cloneObject();
        ret.label = this.label;
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

}
