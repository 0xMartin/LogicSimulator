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
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.Tools;

/**
 * Wire connects io pints
 *
 * @author Martin
 */
public class Wire implements WorkSpaceObject, Serializable {

    //io pins connect to the wire
    private final List<IOPin> pins;

    //path of this wire (only for graphics visualiation)
    private final List<Line> path;

    private final List<Line> selectedLines = new ArrayList<>();

    /**
     * Value the was be writed on this wire (0=low,1=high,-1=bus(this is
     * automatic))
     */
    public int value = 0;

    public Wire() {
        this.pins = new ArrayList<>();
        this.path = new ArrayList<>();
    }

    /**
     * Return selected line of this wire path
     *
     * @return Line
     */
    public List<Line> getSelectedLines() {
        return this.selectedLines;
    }

    @Override
    public boolean compute() {
        //get color (high low or bus color)
        boolean existOutPin = false;
        for (int i = 0; i < this.pins.size(); i++) {
            if (this.pins.get(i).mode == IOPin.MODE.OUTPUT
                    || this.pins.get(i).mode == IOPin.MODE.IO) {
                existOutPin = true;
                if (this.pins.get(i).getValue().length > 1) {
                    this.value = -1;
                    break;
                }
            }
        }
        //set false for all input pin in this wire because outpin not exist (not connected) on this wire
        if (!existOutPin) {
            this.value = 0;
            boolean changed = false;
            for (IOPin pin : this.pins) {
                changed = pin.setValue(false);
            }
            return changed;
        }
        return false;
    }

    @Override
    public boolean error() {
        return false;
    }

    public List<Line> getPath() {
        return this.path;
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
        //set color
        switch (this.value) {
            case 0:
                g2.setColor(Colors.WIRE_0);
                break;
            case 1:
                g2.setColor(Colors.WIRE_1);
                break;
            case -1:
                g2.setColor(Colors.WIRE_BUS);
                break;
        }
        g2.setStroke(new BasicStroke(3));
        this.path.stream().forEach(line -> {
            //draw line
            line.draw(g2, 0, 0);
            //draw select
            boolean isIn = false;
            for (Line l : this.selectedLines) {
                if (l == line) {
                    isIn = true;
                    break;
                }
            }
            if (isIn) {
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
        Line l = Tools.isOnLine(this, position);
        if (l != null) {
            if (!this.selectedLines.stream().anyMatch((o1) -> (o1 == l))) {
                this.selectedLines.add(l);
            }
            return true;
        }
        return false;
    }

    @Override
    public void unSelect() {
        this.selectedLines.clear();
    }

    @Override
    public boolean isSelected() {
        return this.selectedLines.size() > 0;
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
