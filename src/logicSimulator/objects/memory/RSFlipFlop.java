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
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.graphics.Line;
import logicSimulator.objects.IOPin;
import logicSimulator.ui.Colors;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class RSFlipFlop extends WorkSpaceObject {

    protected final IOPin SET, RESET, OUT, N_OUT;

    protected transient String title = "RS";

    public RSFlipFlop(Point position) {
        super(position);

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-28, -28, -28, 28));
        GOList.add(new Line(-28, -28, 28, -28));
        GOList.add(new Line(28, -28, 28, 28));
        GOList.add(new Line(-28, 28, 28, 28));

        //pins
        this.SET = new IOPin(IOPin.MODE.INPUT, 1, "SET", new Point.Double(-28.0, -14.0));
        this.RESET = new IOPin(IOPin.MODE.INPUT, 1, "RESET", new Point.Double(-28.0, 14.0));
        this.OUT = new IOPin(IOPin.MODE.OUTPUT, 1, "OUT", new Point.Double(28.0, -14.0));
        this.N_OUT = new IOPin(IOPin.MODE.OUTPUT, 1, "!OUT", new Point.Double(28.0, 14.0));
        this.N_OUT.setValue(true);
        model.getIOPins().add(this.SET);
        model.getIOPins().add(this.RESET);
        model.getIOPins().add(this.OUT);
        model.getIOPins().add(this.N_OUT);

        super.setModel(model);
        model.computeSize();
        model.disableRotation();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            //draw title
            g2.setFont(Fonts.BIG);
            g2.setColor(Colors.TEXT);
            g2.drawString(this.title,
                    pos.x - g2.getFontMetrics().stringWidth(this.title) / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) - 10
            );
            //draw pin title
            g2.setFont(Fonts.SMALL);
            if (this.SET.getLabel().length() != 0) {
                g2.drawString(this.SET.getLabel().charAt(0) + "",
                        pos.x - 18 - g2.getFontMetrics().stringWidth(this.title) / 2,
                        pos.y - 14 + Tools.centerYString(g2.getFontMetrics())
                );
            }
            if (this.RESET.getLabel().length() != 0) {
                g2.drawString(this.RESET.getLabel().charAt(0) + "",
                        pos.x - 18 - g2.getFontMetrics().stringWidth(this.title) / 2,
                        pos.y + 14 + Tools.centerYString(g2.getFontMetrics())
                );
            }
            //value
            g2.setFont(Fonts.MEDIUM);
            g2.setColor(this.OUT.getValue()[0] ? Colors.WIRE_1 : Colors.WIRE_0);
            g2.fillRect(pos.x - 10, pos.y, 20, 20);
            g2.setColor(Colors.TEXT);
            g2.drawString(
                    this.OUT.getValue()[0] ? "1" : "0",
                    pos.x - g2.getFontMetrics().stringWidth("1") / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics()) + 10
            );
        }
    }

    @Override
    public void restore() {
        this.title = "RS";
    }

    @Override
    public Propertie[] getProperties() {
        return null;
    }

    @Override
    public void changePropertie(Propertie propt) {

    }

    @Override
    public boolean compute() {
        boolean[] setBits = this.SET.getValue();
        boolean[] resetBits = this.RESET.getValue();
        boolean[] out = this.OUT.getValue();
        boolean change = false;
        //set bits
        if (setBits[0]) {
            out[0] = true;
            change = true;
        }
        //reset bits
        if (resetBits[0]) {
            out[0] = false;
            change = true;
        }
        this.N_OUT.getValue()[0] = !out[0];
        return change;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new RSFlipFlop(
                Tools.copy(super.getPosition())
        );
    }

    @Override
    public boolean error() {
        return false;
    }

}
