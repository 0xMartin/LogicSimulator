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

import logicSimulator.common.ClickAction;
import logicSimulator.ui.Colors;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Project;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.Tools;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class Button extends WorkSpaceObject implements ClickAction {

    private boolean[] value;

    private Point.Double[] buttons;

    private String label = "";

    public Button(Point position, int bits) {
        super(position);
        this.value = new boolean[bits];
        buildModel(bits, 0);
    }

    private void buildModel(int bits, int angle) {
        //output vals []
        this.value = new boolean[bits];
        //model
        int h_width = Math.min(8, bits) * 10;
        int h_height = ((bits - 1) / 8 + 1) * 10;
        int x_off = 14 - h_width % 14;
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-h_width + x_off, -h_height, h_width + x_off, -h_height));
        GOList.add(new Line(-h_width + x_off, h_height, h_width + x_off, h_height));
        GOList.add(new Line(-h_width + x_off, -h_height, -h_width + x_off, h_height));
        GOList.add(new Line(h_width + x_off, -h_height, h_width + x_off, h_height));
        //buttons
        this.buttons = new Point.Double[bits];
        int x = 0, y = 0;
        for (int i = 0; i < bits; i++) {
            this.buttons[i] = new Point.Double(
                    h_width - x * 20 + x_off - 20,
                    -h_height + y * 20
            );
            x++;
            if (x > 7) {
                x = 0;
                y++;
            }
        }
        //output pin
        model.getIOPins().add(
                new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(h_width + x_off, 0))
        );
        //rotate model
        model.rotate(angle);
        //size
        model.computeSize();

        super.setModel(model);
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        if (Tools.isInRange(
                pos.x - offset.x, pos.y - offset.y, screen,
                Math.max(super.getModel().getWidth(), super.getModel().getHeight())
        )) {

            //model of this button
            Model m = super.getModel();

            //draw select rect
            if (super.isSelected()) {
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.setColor(Colors.SELECT_RECT);
                g2.drawRect(
                        (int) (pos.x + super.getModel().getBoundsMin().x - 10),
                        (int) (pos.y + super.getModel().getBoundsMin().y - 10),
                        m.getWidth() + 20,
                        m.getHeight() + 20
                );
            }

            m.preRender(g2);

            //draw buttons for each bit
            g2.setStroke(new BasicStroke(2));
            boolean b = m.getAngle() % 2 != 0;
            g2.setFont(Fonts.BIG);
            for (int index = 0; index < this.value.length; index++) {
                g2.setColor(this.value[index] ? Colors.WIRE_1 : Colors.WIRE_0);

                //rotate points around center of objects if position of 1 and 3
                Point.Double p = Tools.copy(this.buttons[index]);
                if (b) {
                    Tools.rotatePoint(p, Math.PI / 2d);
                    p.x -= 20;
                }

                //get position for each button
                int x = (int) (pos.x + p.x);
                int y = (int) (pos.y + p.y);

                //offset
                int a = m.getAngle();
                if (a == 2 || a == 3) {
                    int off = 8 * Math.min(this.value.length, 8);
                    while (off > 28) {
                        off -= 28;
                    }
                    if (a == 2) {
                        x -= off;
                    } else {
                        y -= off;
                    }
                }

                //draw button
                g2.fillRect(x, y, 20, 20);

                //draw value of bit
                g2.setColor(Colors.OBJECT);
                g2.drawString(
                        this.value[index] ? "1" : "0",
                        (int) (x + 10 - g2.getFontMetrics().stringWidth("0") / 2),
                        (int) (y + 10 + Tools.centerYString(g2.getFontMetrics()))
                );

                //draw 0 and end bit 7 or lowes
                if (this.value.length > 1) {
                    if (index == 0 || index == Math.min(this.value.length - 1, 7)) {
                        g2.setFont(Fonts.SMALL);
                        g2.drawString(
                                index + "",
                                x + (b ? 28 : 14) - g2.getFontMetrics().stringWidth(index + "") / 2,
                                y + (b ? 14 : -8)
                        );
                        g2.setFont(Fonts.BIG);
                    }
                }
            }

            //draw label
            g2.setFont(Fonts.MEDIUM);
            g2.drawString(
                    this.label,
                    (int) (pos.x + m.getBoundsMin().x
                    - g2.getFontMetrics().stringWidth(this.label) - 9),
                    (int) (pos.y + m.getBoundsMin().y
                    + (super.getModel().getAngle() == 2 ? 0 : m.getHeight() / 2))
            );

            //render model
            m.render(g2, pos.x, pos.y);

            m.postRender(g2);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.value.length, Propertie.Type.BITS),
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
                    buildModel(i, super.getModel().getAngle());
                    break;
                case "Label":
                    this.label = propt.getValueString();
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean compute() {
        boolean[] out = new boolean[this.value.length];
        System.arraycopy(this.value, 0, out, 0, this.value.length);
        return super.getPins().get(0).setValue(out);
    }

    /**
     * Chang value of button (logic: 0 -> logic: 1)
     *
     * @param cursor Cursor position
     */
    @Override
    public void changeValue(Point cursor, Component parent, Project project) {
        //if model orientation is 0 or 1 then use index otherwise use length - index
        //change value
        for (int i = 0; i < this.buttons.length; i++) {
            //index of button
            Point.Double p = Tools.copy(this.buttons[i]);

            //rotate all buttons if button array is rotated up or down
            if (super.getModel().getAngle() % 2 != 0) {
                Tools.rotatePoint(p, Math.PI / 2d);
                p.x -= 20;
            }

            int x = (int) (super.getPosition().x + p.x);
            int y = (int) (super.getPosition().y + p.y);

            //offset
            int a = super.getModel().getAngle();
            if (a == 2 || a == 3) {
                int off = 8 * Math.min(this.value.length, 8);
                while (off > 28) {
                    off -= 28;
                }
                if (a == 2) {
                    x -= off;
                } else {
                    y -= off;
                }
            }

            if (cursor.x >= x && cursor.x <= x + 20 && cursor.y >= y && cursor.y <= y + 20) {
                this.value[i] = !this.value[i];
                break;
            }
        }
    }

    @Override
    public WorkSpaceObject cloneObject() {
        Button ret = new Button(Tools.copy(super.getPosition()), this.value.length);
        ret.getModel().rotate(super.getModel().getAngle());
        ret.label = this.label;
        return ret;
    }

}
