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
import logicSimulator.Tools;
import logicSimulator.graphics.Line;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Fonts;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class Bridge extends WorkSpaceObject {

    //bridge idetificator
    private String id;

    private boolean idChanged = true;

    public Bridge(Point position, String id) {
        super(position);

        this.id = id;
        buildModel(20);
    }

    private void buildModel(int txtLength) {
        txtLength += 10;
        //orientation of model
        int angle = super.getModel() == null ? 0 : super.getModel().getAngle();

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(0, 0, -7, 7));
        GOList.add(new Line(0, 0, -7, -7));
        GOList.add(new Line(-7, 7, -7, 10));
        GOList.add(new Line(-7, -7, -7, -10));
        GOList.add(new Line(-7, -10, -7 - txtLength, -10));
        GOList.add(new Line(-7, 10, -7 - txtLength, 10));
        GOList.add(new Line(-7 - txtLength, 10, -7 - txtLength, -10));

        //pin
        model.getIOPins().add(new IOPin(IOPin.MODE.LINKER, 1, "", new Point.Double(0, 0)));

        //rotate model
        model.rotate(angle);

        super.setModel(model);
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        g2.setFont(Fonts.BIG);
        //rebuild model
        if (this.idChanged) {
            this.idChanged = false;
            buildModel(g2.getFontMetrics().stringWidth(this.id));
        }
        //model
        super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());
        //draw id
        g2.setColor(Colors.OBJECT);
        int x = super.getPosition().x;
        int y = super.getPosition().y;
        int a = super.getModel().getAngle();
        switch (a) {
            case 0:
                x -= 13 + g2.getFontMetrics().stringWidth(this.id);
                break;
            case 1:
                x -= g2.getFontMetrics().stringWidth(this.id) / 2;
                y -= 7 + (super.getModel().getHeight() - 7) / 2;
                break;
            case 2:
                x += 13;
                break;
            case 3:
                x -= g2.getFontMetrics().stringWidth(this.id) / 2;
                y += 7 + (super.getModel().getHeight() - 7) / 2;
                break;
        }
        if (a == 1 || a == 3) {
            g2.rotate(-Math.PI / 2, super.getPosition().x + 3, y - 2);
        }
        g2.drawString(
                this.id,
                x, y + Tools.centerYString(g2.getFontMetrics())
        );
        if (a == 1 || a == 3) {
            g2.rotate(Math.PI / 2, super.getPosition().x + 3, y - 2);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("ID", this.id)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "ID":
                    this.id = propt.getValueString();
                    this.idChanged = true;
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public WorkSpaceObject cloneObject() {
        Bridge ret = new Bridge(Tools.copy(super.getPosition()), this.id);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

}
