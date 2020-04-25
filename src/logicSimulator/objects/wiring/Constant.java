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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logicSimulator.Convert;
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
public class Constant extends WorkSpaceObject {

    private boolean[] value;

    private transient String hexValue = "0x0";

    private final IOPin OUT;

    public Constant(Point position) {
        super(position);
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(0, 0, -7, -7));
        GOList.add(new Line(0, -14, -7, -7));
        GOList.add(new Line(0, -14, 7, -7));
        GOList.add(new Line(0, 0, 7, -7));

        this.OUT = new IOPin(IOPin.MODE.OUTPUT, 1, "", new Point.Double(0, 0));
        model.getIOPins().add(this.OUT);

        super.setModel(model);
        model.computeSize();

        this.value = new boolean[]{false};
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            g2.setColor(Colors.TEXT);
            g2.setFont(Fonts.SMALL);
            int offx = 0, offy = 0;
            switch (super.getModel().getAngle()) {
                case 0:
                    offy = -25;
                    break;
                case 1:
                    offx = 7;
                    offy = -20;
                    break;
                case 2:
                    offy = 25;
                    break;
                case 3:
                    offx = -7;
                    offy = -20;
                    break;
            }
            g2.drawString(this.hexValue,
                    pos.x + offx - g2.getFontMetrics().stringWidth(this.hexValue) / 2,
                    pos.y + offy + Tools.centerYString(g2.getFontMetrics())
            );
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Value", "0x" + Convert.bitsToHex(this.value))
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Value":
                    this.value = Convert.hexToBitArray(propt.getValueString().substring(2));
                    this.OUT.changeBitWidth(this.value.length);
                    this.OUT.setValue(this.value);
                    this.hexValue = "0x" + Convert.bitsToHex(this.value);
                    break;
            }
        } catch (Exception ex) {
            Logger.getLogger(Constant.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void restore() {
        if (this.value != null) {
            this.hexValue = "0x" + Convert.bitsToHex(this.value);
        }
    }

    @Override
    public Constant cloneObject() {
        Constant ret = new Constant(Tools.copy(super.getPosition()));
        ret.value = new boolean[this.value.length];
        System.arraycopy(this.value, 0, ret.value, 0, this.value.length);
        return ret;
    }

}
