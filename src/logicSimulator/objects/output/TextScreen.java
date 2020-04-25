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
package logicSimulator.objects.output;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.Project;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.ClickAction;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.graphics.Line;
import logicSimulator.objects.IOPin;
import logicSimulator.ui.Colors;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class TextScreen extends WorkSpaceObject implements ClickAction {

    //foreground
    private Color foreground = Color.BLACK;

    //font
    private Font font = Fonts.MEDIUM;

    //control pins
    private final IOPin IN, CLK, CLEAR;

    //text rendering offset
    private transient int text_y_off = 0;

    //text
    private transient String text = "";

    public TextScreen(Point position) {
        super(position);

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        List<IOPin> pins = model.getIOPins();
        GOList.add(new Line(-112, -84, -112, 84));
        GOList.add(new Line(-112, -84, 112, -84));
        GOList.add(new Line(112, -84, 112, 84));
        GOList.add(new Line(-112, 84, 112, 84));
        GOList.add(new Line(-98, 84, -98, -84));

        //pins
        this.IN = new IOPin(IOPin.MODE.INPUT, 8, "INPUT", new Point.Double(-112.0, -70.0));
        this.CLK = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-112.0, -56.0));
        this.CLK.drawClkSymbol = true;
        this.CLEAR = new IOPin(IOPin.MODE.INPUT, 8, "CLEAR", new Point.Double(-112.0, -42.0));
        pins.add(this.IN);
        pins.add(this.CLK);
        pins.add(this.CLEAR);

        super.setModel(model);
        model.computeSize();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            //draw text
            try {
                g2.setFont(this.font);
                g2.setColor(this.foreground);
                String[] lines = this.text.split("\n");
                //if offset is to heigher
                if (this.text_y_off >= lines.length) {
                    this.text_y_off = lines.length - 1;
                }
                //draw all lines on screen
                for (int i = this.text_y_off; i < lines.length; i++) {
                    String buf = lines[i];
                    while (g2.getFontMetrics().stringWidth(buf) > 206) {
                        buf = buf.substring(0, buf.length() - 1);
                    }
                    int yoff = g2.getFontMetrics().getHeight() * (1 + i - this.text_y_off);
                    if (yoff > 160) {
                        //out of screen
                        break;
                    }
                    g2.drawString(buf, pos.x - 95, pos.y - 82 + yoff);
                }

                //down button
                g2.setColor(Colors.WIRE_0);
                g2.fillRect(-111 + pos.x, 71 + pos.y, 12, 12);
                g2.setColor(Colors.OBJECT);
                g2.drawLine(-109 + pos.x, 75 + pos.y, -105 + pos.x, 80 + pos.y);
                g2.drawLine(-101 + pos.x, 75 + pos.y, -105 + pos.x, 80 + pos.y);

                //up button
                g2.setColor(Colors.WIRE_0);
                g2.fillRect(-111 + pos.x, 51 + pos.y, 12, 12);
                g2.setColor(Colors.OBJECT);
                g2.drawLine(-109 + pos.x, 60 + pos.y, -105 + pos.x, 55 + pos.y);
                g2.drawLine(-101 + pos.x, 60 + pos.y, -105 + pos.x, 55 + pos.y);

                //position bar
                g2.drawRect(-109 + pos.x, -10 + pos.y, 8, 55);
                int height = Math.max(8, (int) (55f / lines.length));
                g2.fillRect(
                        -109 + pos.x,
                        (int) (-10 + pos.y + ((float) this.text_y_off / lines.length * (55 - height))),
                        8, height
                );
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void restore() {
        this.text = "";
        this.text_y_off = 0;
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Foreground", this.foreground.getRGB(), Propertie.Type.COLOR),
            new Propertie("Font", this.font.getName(), Propertie.Type.FONT),
            new Propertie("Size", this.font.getSize())
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Foreground":
                    this.foreground = new Color(propt.getValueInt());
                    break;
                case "Font":
                    //change font
                    this.font = new Font(
                            propt.getValueString(),
                            Font.PLAIN,
                            this.font.getSize()
                    );
                    break;
                case "Size":
                    //change size
                    int s = Math.max(propt.getValueInt(), 4);
                    this.font = this.font.deriveFont((float) s);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    private boolean r_edge = true;

    @Override
    public boolean compute() {
        //clear
        if (this.CLEAR.getValue()[0]) {
            boolean repaint = this.text.length() != 0;
            this.text = "";
            return repaint;
        }
        //add char
        if (this.CLK.getValue()[0]) {
            if (this.r_edge) {
                this.r_edge = false;
                //get input data
                boolean[] input = this.IN.getValue();
                //is not null
                boolean notNull = false;
                for (int i = 0; i < input.length; i++) {
                    if (input[i]) {
                        notNull = true;
                        break;
                    }
                }
                //add to text
                if (notNull) {
                    char c = (char) Convert.bitsToByte(input);
                    //backspace
                    if (c == '\b') {
                        if (this.text.length() != 0) {
                            this.text = this.text.substring(0, this.text.length() - 1);
                        }
                    } else {
                        //add char to text
                        this.text += c;
                    }
                }
            }
        } else {
            this.r_edge = true;
        }
        return false;
    }

    @Override
    public TextScreen cloneObject() {
        return new TextScreen(Tools.copy(super.getPosition()));
    }

    @Override
    public boolean error() {
        return false;
    }

    @Override
    public void changeValue(Point cursor, Component parent, Project project) {
        Point pos = super.getPosition();
        if (pos.x - 111 <= cursor.x && cursor.x < pos.x - 99) {
            //up button
            if (pos.y + 51 <= cursor.y && cursor.y < pos.y + 63) {
                this.text_y_off = Math.max(0, this.text_y_off - 1);
            }
            //down button
            if (pos.y + 71 <= cursor.y && cursor.y < pos.y + 83) {
                int line_count = this.text.split("\n").length;
                this.text_y_off = Math.min(line_count - 1, this.text_y_off + 1);
            }
        }
    }

}
