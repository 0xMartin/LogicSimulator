/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.control;

import logicSimulator.ui.Colors;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.Tools;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class Button implements WorkSpaceObject, Serializable {

    private final Point position;

    private boolean[] value;

    private Model model;

    private boolean selected = false;

    private Point.Double[] buttons;

    private String label = "";

    public Button(Point position, int bits) {
        this.position = position;
        this.value = new boolean[bits];
        buildModel(bits, 0);
    }

    public void buildModel(int bits, int angle) {
        //output vals []
        this.value = new boolean[bits];
        //model
        int h_width = Math.min(8, bits) * 10;
        int h_height = ((bits - 1) / 8 + 1) * 10;
        int x_off = h_width % 20 == 0 ? 0 : 10;
        this.model = new Model(
                new Line[]{
                    new Line(new Point(-h_width + x_off, -h_height), new Point(h_width + x_off, -h_height)),
                    new Line(new Point(-h_width + x_off, h_height), new Point(h_width + x_off, h_height)),
                    new Line(new Point(-h_width + x_off, -h_height), new Point(-h_width + x_off, h_height)),
                    new Line(new Point(h_width + x_off, -h_height), new Point(h_width + x_off, h_height))
                },
                null, null
        );
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
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(h_width + x_off, 0))
        );
        //rotate model
        this.model.rotate(angle);
        //size
        this.model.computeSize();
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(this.model.getWidth(), this.model.getHeight());
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        if (Tools.isInRange(
                this.position.x - offset.x, this.position.y - offset.y, screen,
                Math.max(this.model.getWidth(), this.model.getHeight())
        )) {
            //draw select rect
            if (this.selected) {
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.setColor(Colors.SELECT_RECT);
                g2.drawRect(
                        (int) (this.position.x + this.model.getBoundsMin().x - 10),
                        (int) (this.position.y + this.model.getBoundsMin().y - 10),
                        this.model.getWidth() + 20,
                        this.model.getHeight() + 20
                );
            }
            g2.setStroke(new BasicStroke(2));
            //draw buttons for each bit
            boolean b = this.model.getAngle() % 2 != 0;
            g2.setFont(Fonts.STATUS);
            for (int index = 0; index < this.value.length; index++) {
                g2.setColor(this.value[index] ? Colors.WIRE_1 : Colors.WIRE_0);
                //rotate points around center of objects if position of 1 and 3
                Point.Double p = Tools.copy(this.buttons[index]);
                if (this.model.getAngle() % 2 != 0) {
                    Tools.rotatePoint(p, Math.PI / 2d);
                    p.x -= 20;
                }
                //get position for each button
                int x = (int) (this.position.x + p.x);
                int y = (int) (this.position.y + p.y);
                //offset (if width is 1, 3, 5, 7)
                if (Math.min(this.value.length, 8) % 2 != 0) {
                    switch (this.model.getAngle()) {
                        case 2:
                            x -= 20;
                            break;
                        case 3:
                            y -= 20;
                            break;
                    }
                }
                //draw button
                g2.fillRect(x, y, 20, 20);
                //draw value of bit
                g2.setColor(Colors.GATE);
                g2.drawString(
                        this.value[index] ? "1" : "0",
                        (int) (x + 10 - g2.getFontMetrics().stringWidth("0") / 2),
                        (int) (y + 10 + Tools.centerYString(g2.getFontMetrics()))
                );
                //draw 0 and end bit 7 or lowes
                if (index == 0 || index == Math.min(this.value.length - 1, 7)) {
                    g2.setFont(Fonts.IOPIN);
                    g2.drawString(
                            index + "",
                            x + (b ? 28 : 14) - g2.getFontMetrics().stringWidth(index + "") / 2,
                            y + (b ? 14 : -8)
                    );
                    g2.setFont(Fonts.STATUS);
                }
            }
            g2.setFont(Fonts.LABEL);
            //draw label
            g2.drawString(
                    this.label,
                    (int) (this.position.x + this.model.getBoundsMin().x
                    - g2.getFontMetrics().stringWidth(this.label) - 9),
                    (int) (this.position.y + this.model.getBoundsMin().y
                    + (this.model.getAngle() == 2 ? 0 : this.model.getHeight() / 2))
            );
            //render model
            this.model.render(g2, this.position.x, this.position.y);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.value.length),
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
                    buildModel(i, this.model.getAngle());
                    break;
                case "Label":
                    this.label = propt.getValueString();
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public List<IOPin> getPins() {
        return this.model.getIOPins();
    }

    @Override
    public boolean select(Point cursor) {
        if (this.model.intersect(cursor, this.position)) {
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
        return this.model;
    }

    @Override
    public boolean compute() {
        boolean[] out = new boolean[this.value.length];
        for (int i = 0; i < this.value.length; i++) {
            out[i] = this.value[i];
        }
        return this.model.getIOPins().get(0).setValue(out);
    }

    /**
     * Chang value of button (logic: 0 -> logic: 1)
     *
     * @param cursor Cursor position
     */
    public void changeValue(Point cursor) {
        //if model orientation is 0 or 1 then use index otherwise use length - index
        //change value
        for (int i = 0; i < this.buttons.length; i++) {
            //index of button
            Point.Double p = Tools.copy(this.buttons[i]);
            //rotate all buttons if button array is rotated up or down
            if (this.model.getAngle() % 2 != 0) {
                Tools.rotatePoint(p, Math.PI / 2d);
                p.x -= 20;
            }
            int x = (int) (this.position.x + p.x);
            int y = (int) (this.position.y + p.y);
            //offset (if width is 1, 3, 5, 7)
            if (Math.min(this.value.length, 8) % 2 != 0) {
                switch (this.model.getAngle()) {
                    case 2:
                        x -= 20;
                        break;
                    case 3:
                        y -= 20;
                        break;
                }
            }
            if (cursor.x >= x && cursor.x <= x + 20 && cursor.y >= y && cursor.y <= y + 20) {
                this.value[i] = !this.value[i];
                break;
            }
        }
    }

    public WorkSpaceObject cloneObject() {
        Button ret = new Button(new Point(this.position.x, this.position.y), this.value.length);
        ret.getModel().clone(this.model);
        ret.label = this.label;
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

}
