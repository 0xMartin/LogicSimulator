/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class Text implements WorkSpaceObject, Serializable {

    private final List<String> text;

    private boolean selected = false;

    private final Dimension size;

    private final Point position;

    private Font font;

    private boolean textChange = true;

    public Text(String text, Point position, Font font) {
        this.text = new ArrayList<>();
        if (text.length() != 0) {
            this.text.add(text);
        }
        this.size = new Dimension(0, 0);
        this.position = position;
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
    public Point getPosition() {
        return this.position;
    }

    @Override
    public Dimension getSize() {
        return this.size;
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
        if (Tools.isInRange(
                this.position.x - offset.x, this.position.y - offset.y, screen,
                Math.max(this.size.width, this.size.height)
        )) {
            //draw each line of text
            for (int i = 0; i < this.text.size(); i++) {
                g2.drawString(
                        this.text.get(i),
                        this.position.x - this.size.width / 2,
                        this.position.y + g2.getFontMetrics().getHeight() * (i + 1) - this.size.height / 2
                );
            }
            //draw select rect
            if (this.selected) {
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.setColor(Colors.SELECT_RECT);
                g2.drawRect(
                        this.position.x - 10 - this.size.width / 2,
                        this.position.y - 10 - this.size.height / 2,
                        this.size.width + 20,
                        this.size.height + 20
                );
            }
        }
    }

    @Override
    public Propertie[] getProperties() {
        Propertie[] p = new Propertie[this.text.size() + 1];
        //font
        p[0] = new Propertie("Font", this.font.getName() + ", " + this.font.getSize());
        //all lines
        for (int i = 1; i < this.text.size() + 1; i++) {
            String line = this.text.get(i - 1);
            p[i] = new Propertie("Line " + i, line == null ? "" : line);
        }
        p[this.text.size()] = new Propertie("Add", "");
        return p;
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            if (propt.getName().equals("Font")) {
                //change font
                String[] ff = propt.getValueString().split(",");
                int fs = Integer.parseInt(ff[1].replaceAll("\\s+", ""));
                fs = Math.max(5, fs);
                fs = Math.min(100, fs);
                this.font = new Font(
                        ff[0].replaceAll("\\s+", ""),
                        Font.PLAIN,
                        fs
                );
            } else if (propt.getName().equals("Add")) {
                //add new line
                if (propt.getValueString().length() != 0) {
                    this.text.add(propt.getValueString());
                }
            } else if (propt.getName().startsWith("Line")) {
                //get index of changed line
                int index = Integer.parseInt(propt.getName().substring(5));
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
            }
            this.textChange = true;
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean select(Point cursor) {
        int hw = this.size.width / 2;
        int hh = this.size.height / 2;
        if (cursor.x >= this.position.x - hw
                && cursor.y >= this.position.y - hh
                && cursor.x <= this.position.x + this.size.width - hw
                && cursor.y <= this.position.y + this.size.height - hh) {
            this.selected = true;
            return true;
        }
        return false;
    }

    @Override
    public void unSelect() {
        this.selected = false;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public Model getModel() {
        return null;
    }

    @Override
    public boolean compute() {
        return false;
    }

    @Override
    public boolean error() {
        return false;
    }

    @Override
    public List<IOPin> getPins() {
        return null;
    }

    public WorkSpaceObject cloneObject() {
        Text copy = new Text(
                "",
                Tools.copy(this.position),
                new Font(this.font.getFontName(), this.font.getStyle(), this.font.getSize())
        );
        this.text.stream().forEach((line) -> {
            copy.text.add(line);
        });
        return copy;
    }

}
