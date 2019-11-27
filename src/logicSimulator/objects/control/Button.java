/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.control;

import logicSimulator.ui.Colors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.common.Tools;

/**
 *
 * @author Martin
 */
public class Button implements WorkSpaceObject, Serializable {

    private final Point position;

    private boolean[] value;

    private Model model;

    private boolean selected = false;

    public Button(Point position, int bits) {
        this.position = position;
        this.value = new boolean[bits];
        buildModel(bits, 0);
    }

    public void buildModel(int bits, int angle) {
        //output vals []
        this.value = new boolean[bits];
        //model
        int len = bits * 10;
        int offy = len % 20 == 0 ? -10 : 0;
        this.model = new Model(
                new Line[]{
                    new Line(new Point(-10, -len + offy), new Point(10, -len + offy)),
                    new Line(new Point(10, -len + offy), new Point(10, len + offy)),
                    new Line(new Point(10, len + offy), new Point(-10, len + offy)),
                    new Line(new Point(-10, len + offy), new Point(-10, -len + offy)),
                    new Line(new Point(0, len + offy), new Point(0, len + offy + 6))
                },
                null, null
        );
        //buttons
        this.model.points = new Point.Double[bits];
        for (int i = 0; i < bits; i++) {
            this.model.points[i] = new Point.Double(-10, -len + i * 20 + offy);
        }
        //output pin
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.OUTPUT, bits, "A", new Point.Double(0, len + 10 + offy))
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
                        this.position.x - this.model.getWidth() / 2 - 10,
                        this.position.y - this.model.getHeight() / 2 - 10,
                        this.model.getWidth() + 20,
                        this.model.getHeight() + 20
                );
            }
            g2.setStroke(new BasicStroke(2));
            //draw buttons for each bit
            Font f = g2.getFont();
            for (int i = 0; i < this.value.length; i++) {
                g2.setColor(this.value[i] ? Colors.WIRE_1 : Colors.WIRE_0);
                int x = (int) (this.position.x + this.model.points[i].x);
                int y = (int) (this.position.y + this.model.points[i].y);
                switch (this.model.getAngle()) {
                    case 1:
                        x -= 20;
                        break;
                    case 2:
                        x -= 20;
                        y -= 20;
                        break;
                    case 3:
                        y -= 20;
                        break;
                }
                g2.fillRect(x, y, 20, 20);
                g2.setColor(Color.BLACK);
                g2.drawString(
                        this.value[i] ? "1" : "0",
                        (int) (x + 10 - g2.getFontMetrics().stringWidth("0") / 2),
                        (int) (y + 13)
                );
                g2.setFont(g2.getFont().deriveFont(6f));
                g2.drawString(
                        i + "",
                        (int) (x + 14 - g2.getFontMetrics().stringWidth(i + "") / 2),
                        (int) (y + 18)
                );
                g2.setFont(f);
            }
            //render model
            this.model.render(g2, this.position.x, this.position.y);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.value.length)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    buildModel(propt.getValueInt(), this.model.getAngle());
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
    public boolean select(Point position) {
        if (this.model.intersect(position, this.position)) {
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
        for (int i = 0; i < this.model.points.length; i++) {
            int x = (int) (this.position.x + this.model.points[i].x);
            int y = (int) (this.position.y + this.model.points[i].y);
            switch (this.model.getAngle()) {
                case 1:
                    x -= 20;
                    break;
                case 2:
                    x -= 20;
                    y -= 20;
                    break;
                case 3:
                    y -= 20;
                    break;
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
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

}
