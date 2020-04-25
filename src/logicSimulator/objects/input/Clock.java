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
package logicSimulator.objects.input;

import java.awt.Component;
import logicSimulator.common.ClickAction;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Project;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class Clock extends WorkSpaceObject implements ClickAction {

    private boolean run = false;

    private int delay = 0;

    public Clock(Point position, int bits) {
        super(position);
        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-14, -14, -14, 14));
        GOList.add(new Line(-14, -14, 14, -14));
        GOList.add(new Line(14, -14, 14, 14));
        GOList.add(new Line(-14, 14, 14, 14));
        
        //output pin
        model.getIOPins().add(new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0, 14)));

        super.setModel(model);
        model.computeSize();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            g2.setColor(this.run ? Colors.WIRE_1 : Colors.WIRE_0);
            g2.fillRect(-12 + pos.x, -12 + pos.y, 24, 24);
            g2.setColor(Colors.OBJECT);
            g2.drawLine(-8 + pos.x, -8 + pos.y, pos.x, -8 + pos.y);
            g2.drawLine(pos.x, -8 + pos.y, pos.x, 8 + pos.y);
            g2.drawLine(pos.x, 8 + pos.y, 8 + pos.x, 8 + pos.y);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", super.getPins().get(0).getValue().length, Propertie.Type.BITS),
            new Propertie("Delay", this.delay)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    super.getPins().get(0).changeBitWidth(propt.getValueInt());
                    break;
                case "Delay":
                    this.delay = Math.max(propt.getValueInt(), 0);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    private int time = 0;

    @Override
    public boolean compute() {
        if (this.run) {
            if (this.time < this.delay) {
                this.time++;
            } else {
                this.time = 0;

                boolean[] out = super.getModel().getIOPins().get(0).getValue();
                if (out != null) {
                    boolean v = !out[0];
                    for (int i = 0; i < out.length; i++) {
                        out[i] = v;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new Clock(Tools.copy(super.getPosition()), super.getPins().get(0).getValue().length);
    }

    @Override
    public void changeValue(Point cursor, Component parent, Project project) {
        //if user click on clock -> change value
        if (super.getModel().intersect(cursor, super.getPosition())) {
            //run
            this.run = !this.run;
            //set false value
            super.getModel().getIOPins().get(0).setValue(false);
        }
    }

}
