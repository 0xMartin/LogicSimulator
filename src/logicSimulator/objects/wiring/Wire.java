/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.wiring;

import logicSimulator.ui.Colors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.common.Tools;

/**
 * Wire connects io pints
 *
 * @author Martin
 */
public class Wire implements WorkSpaceObject, Serializable {

    private final List<IOPin> pins;

    private final LinkedList<Line> path;

    private boolean select = false;

    @Override
    public boolean compute() {
        return false;
    }

    @Override
    public boolean error() {
        return false;
    }

    public enum WValue {
        LOW, HIGH, BUS;
    }

    private WValue value = WValue.LOW;

    public Wire() {
        this.pins = new ArrayList<>();
        this.path = new LinkedList<>();
    }

    public LinkedList<Line> getPath() {
        return this.path;
    }

    public void setBinaryValue(WValue val) {
        this.value = val;
    }

    @Override
    public Point getPosition() {
        if (this.path.isEmpty()) {
            return null;
        }
        return new Point((int) this.path.get(0).p1.x, (int) this.path.get(0).p1.y);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(0, 0);
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        switch (this.value) {
            case LOW:
                g2.setColor(Colors.WIRE_0);
                break;
            case HIGH:
                g2.setColor(Colors.WIRE_1);
                break;
            case BUS:
                g2.setColor(Colors.WIRE_BUS);
                break;
        }
        g2.setStroke(new BasicStroke(3));
        this.path.stream().forEach(line -> {
            //draw line
            line.draw(g2, 0, 0);
            //draw select
            if (this.select) {
                Color c = g2.getColor();
                g2.setColor(Colors.SELECT_RECT);
                g2.fillRect((int) (line.p1.x) - 4, (int) (line.p1.y) - 4, 7, 7);
                g2.fillRect((int) (line.p2.x) - 4, (int) (line.p2.y) - 4, 7, 7);
                g2.setColor(c);
            }
            /*
            //display number of connected pins
            Point p = new Point(
                    (int) (line.p1.x - (line.p1.x - line.p2.x) / 2),
                    (int) (line.p1.y - (line.p1.y - line.p2.y) / 2)
            );
            g2.drawString(": " + this.pins.size(), p.x + 15, p.y + 15);
             */
        });
    }

    @Override
    public Propertie[] getProperties() {
        return null;
    }

    @Override
    public void changePropertie(Propertie propt) {

    }

    @Override
    public List<IOPin> getPins() {
        return this.pins;
    }

    @Override
    public boolean select(Point position) {
        if (Tools.isOnLine(this, position)) {
            this.select = true;
            return true;
        }
        this.select = false;
        return false;
    }

    @Override
    public void unSelect() {
        this.select = false;
    }

    @Override
    public boolean isSelected() {
        return this.select;
    }

    @Override
    public Model getModel() {
        return null;
    }

    public Wire cloneObject() {
        Wire w = new Wire();
        //copy lines
        this.path.stream().forEach((line) -> {
            w.getPath().add(line.cloneObject());
        });
        return w;
    }

}
