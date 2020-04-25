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
package logicSimulator.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class Text extends WorkSpaceObject {

    private final List<String> text;

    private final Dimension size;

    private Font font;

    private boolean textChange = true;

    private Color color = Color.BLACK;

    public Text(String text, Point position, Font font) {
        super(position);

        this.text = new ArrayList<>();
        if (text.length() != 0) {
            this.text.add(text);
        }
        this.size = new Dimension(0, 0);
        this.font = font;
    }

    private void computeSize(Graphics2D g2) {
        int width = 0;
        for (int i = 0; i < this.text.size(); i++) {
            width = (int) Math.max(g2.getFontMetrics().stringWidth(this.text.get(i)), width);
        }
        this.size.width = width;
        this.size.height = g2.getFontMetrics().getHeight() * this.text.size();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        //set text font
        g2.setFont(this.font);
        //recompute size of this text object if si neded
        if (this.textChange) {
            computeSize(g2);
            this.textChange = false;
        }
        //if is on visible area of screen then render text
        Point pos = super.getPosition();
        if (Tools.isInRange(
                pos.x - offset.x, pos.y - offset.y, screen,
                Math.max(this.size.width, this.size.height)
        )) {
            //draw each line of text
            g2.setColor(this.color);
            for (int i = 0; i < this.text.size(); i++) {
                g2.drawString(
                        this.text.get(i),
                        pos.x - this.size.width / 2,
                        pos.y + g2.getFontMetrics().getHeight() * (i + 1) - this.size.height / 2
                );
            }
            //draw select rect
            if (super.isSelected()) {
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.setColor(Colors.SELECT_RECT);
                g2.drawRect(
                        pos.x - 10 - this.size.width / 2,
                        pos.y - 10 - this.size.height / 2,
                        this.size.width + 20,
                        this.size.height + 20
                );
            }
        }
    }

    @Override
    public Propertie[] getProperties() {
        Propertie[] p = new Propertie[this.text.size() + 4];
        //font
        p[0] = new Propertie("Font", this.font.getName(), Propertie.Type.FONT);
        //size
        p[1] = new Propertie("Size", this.font.getSize());
        //color
        p[2] = new Propertie("Color", this.color.getRGB(), Propertie.Type.COLOR);
        //all lines
        for (int i = 0; i < this.text.size(); i++) {
            String line = this.text.get(i);
            p[i + 3] = new Propertie("Line " + (i + 1), line == null ? "" : line);
        }
        p[this.text.size() + 3] = new Propertie("Add", "");
        return p;
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            if (propt.getName().equals("Font")) {
                //change font
                this.font = new Font(
                        propt.getValueString(),
                        Font.PLAIN,
                        this.font.getSize()
                );
            } else if (propt.getName().equals("Size")) {
                //change size
                int s = Math.max(propt.getValueInt(), 4);
                this.font = this.font.deriveFont((float) s);
            } else if (propt.getName().equals("Add")) {
                //add new line
                if (propt.getValueString().length() != 0) {
                    this.text.add(propt.getValueString());
                }
            } else if (propt.getName().startsWith("Line")) {
                //get index of changed line
                int index = Integer.parseInt(propt.getName().substring(5)) - 1;
                String last = this.text.get(index);
                this.text.set(index, propt.getValueString());
                //set some text when all lines of label are empty
                boolean setSomeText = true;
                for (String line : this.text) {
                    if (line.length() != 0) {
                        setSomeText = false;
                        break;
                    }
                }
                if (setSomeText) {
                    this.text.set(0, last);
                }
            } else if (propt.getName().equals("Color")) {
                //color of text
                this.color = new Color(Integer.parseInt(propt.getValueString()));
            }
            this.textChange = true;
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean select(Point cursor) {
        Point pos = super.getPosition();
        if (pos == null || cursor == null) {
            return false;
        }
        int hw = this.size.width / 2;
        int hh = this.size.height / 2;
        if (cursor.x >= pos.x - hw
                && cursor.y >= pos.y - hh
                && cursor.x <= pos.x + this.size.width - hw
                && cursor.y <= pos.y + this.size.height - hh) {
            super.select();
            return true;
        }
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        Text copy = new Text(
                "",
                Tools.copy(super.getPosition()),
                new Font(this.font.getFontName(), this.font.getStyle(), this.font.getSize())
        );
        this.text.stream().forEach((line) -> {
            copy.text.add(line);
        });
        return copy;
    }

}
