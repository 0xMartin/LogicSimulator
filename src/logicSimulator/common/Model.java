/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import logicSimulator.Tools;
import logicSimulator.ui.Colors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.LogicSimulatorCore;

/**
 *
 * @author Martin
 */
public class Model implements Serializable {

    //width and height of model
    private int width = 0, height = 0;

    //model bounds
    private Point.Double min, max;

    private final List<IOPin> pins;

    private Color color = Colors.GATE;

    /**
     * angle of model
     */
    private int angle = 0;

    /**
     * Orientation of model [0, 1, 2, 3] 0 - start position
     *
     * @return
     */
    public int getAngle() {
        return this.angle;
    }

    public Line[] lines;
    public Circle[] circles;
    public Curve[] curves;

    public Model(Line[] lines, Circle[] circles, Curve[] curves) {
        this.lines = lines;
        this.circles = circles;
        this.curves = curves;
        this.pins = new ArrayList<>();
        computeSize();
    }

    /**
     * Get all io pins
     *
     * @return
     */
    public List<IOPin> getIOPins() {
        return this.pins;
    }

    /**
     * Return up left corner of model bounds
     *
     * @return
     */
    public Point.Double getBoundsMin() {
        return this.min;
    }

    /**
     * Return down right corner of model
     *
     * @return
     */
    public Point.Double getBoundsMax() {
        return this.max;
    }

    /**
     * Compute size of this model
     */
    public void computeSize() {
        this.max = new Point.Double(Integer.MIN_VALUE, Integer.MIN_VALUE);
        this.min = new Point.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
        //find max and min for x and y axis
        if (this.lines != null) {
            for (Line l : this.lines) {
                //max
                this.max.x = Math.max(this.max.x, l.p1.x);
                this.max.x = Math.max(this.max.x, l.p2.x);
                this.max.y = Math.max(this.max.y, l.p1.y);
                this.max.y = Math.max(this.max.y, l.p2.y);
                //min
                this.min.x = Math.min(this.min.x, l.p1.x);
                this.min.x = Math.min(this.min.x, l.p2.x);
                this.min.y = Math.min(this.min.y, l.p1.y);
                this.min.y = Math.min(this.min.y, l.p2.y);
            }
        }
        //find max and min for x and y axis
        if (this.circles != null) {
            for (Circle c : this.circles) {
                //max
                this.max.x = Math.max(this.max.x, c.p1.x + c.radius);
                this.max.y = Math.max(this.max.y, c.p1.y + c.radius);
                //min
                this.min.x = Math.min(this.min.x, c.p1.x - c.radius);
                this.min.y = Math.min(this.min.y, c.p1.y - c.radius);
            }
        }
        //find max and min for x and y axis
        if (this.curves != null) {
            for (Curve c : this.curves) {
                //max
                this.max.x = Math.max(this.max.x, c.p1.x);
                this.max.x = Math.max(this.max.x, c.p2.x);
                this.max.y = Math.max(this.max.y, c.p1.y);
                this.max.y = Math.max(this.max.y, c.p2.y);
                //min
                this.min.x = Math.min(this.min.x, c.p1.x);
                this.min.x = Math.min(this.min.x, c.p2.x);
                this.min.y = Math.min(this.min.y, c.p1.y);
                this.min.y = Math.min(this.min.y, c.p2.y);
            }
        }
        //compute width and height
        this.width = (int) (this.max.x - this.min.x);
        this.height = (int) (this.max.y - this.min.y);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void render(Graphics2D g2, int x, int y) {
        //color
        g2.setColor(this.color);
        //draw all lines
        if (this.lines != null) {
            for (Line l : this.lines) {
                l.draw(g2, x, y);
            }
        }
        //draw all circles
        if (this.circles != null) {
            for (Circle c : this.circles) {
                c.draw(g2, x, y);
            }
        }
        //draw all curves
        if (this.curves != null) {
            for (Curve c : this.curves) {
                c.draw(g2, x, y);
            }
        }
        //render pints
        this.pins.stream().forEach((pin) -> {
            pin.render(g2, x, y);
        });
    }

    /**
     * Rotate model around center [0, 0] 1 angle = 90 degres
     *
     * @param angle
     */
    public void rotate(int angle) {
        this.angle += angle;
        while (this.angle < 0) {
            this.angle += 4;
        }
        while (this.angle > 3) {
            this.angle -= 4;
        }
        double A = (double) angle * Math.PI / 2d;
        //line
        if (this.lines != null) {
            for (Line l : this.lines) {
                Tools.rotatePoint(l.p1, A);
                Tools.rotatePoint(l.p2, A);
            }
        }
        //circle
        if (this.circles != null) {
            for (Circle c : this.circles) {
                Tools.rotatePoint(c.p1, A);
            }
        }
        //curve
        if (this.curves != null) {
            for (Curve c : this.curves) {
                Tools.rotatePoint(c.p1, A);
                Tools.rotatePoint(c.p2, A);
                Tools.rotatePoint(c.control, A);
            }
        }
        //pins
        this.pins.stream().forEach((pin) -> {
            Tools.rotatePoint(pin.getPosition(), A);
            pin.getPosition().x = Tools.step((int) pin.getPosition().x, LogicSimulatorCore.WORK_SPACE_STEP);
            pin.getPosition().y = Tools.step((int) pin.getPosition().y, LogicSimulatorCore.WORK_SPACE_STEP);
        });
        //compute model size
        computeSize();
    }

    /**
     * Intersect ?
     *
     * @param cursor Position of cursor
     * @param mPosition Model position
     * @return boolean
     */
    public boolean intersect(Point cursor, Point mPosition) {
        int x = cursor.x;
        int y = cursor.y;
        if (x >= mPosition.x + this.min.x
                && x <= mPosition.x + this.max.x) {
            if (y >= mPosition.y + this.min.y
                    && y <= mPosition.y + this.max.y) {
                return true;
            }
        }
        return false;
    }

    public Model cloneObject() {
        //copy lines
        Line[] lines_copy = null;
        if (this.lines != null) {
            lines_copy = new Line[this.lines.length];
            for (int i = 0; i < this.lines.length; i++) {
                lines_copy[i] = this.lines[i].cloneObject();
            }
        }
        //copy circles
        Circle[] circles_copy = null;
        if (this.circles != null) {
            circles_copy = new Circle[this.circles.length];
            for (int i = 0; i < this.circles.length; i++) {
                circles_copy[i] = this.circles[i].cloneObject();
            }
        }
        //copy curves
        Curve[] curves_copy = null;
        if (this.curves != null) {
            curves_copy = new Curve[this.curves.length];
            for (int i = 0; i < this.curves.length; i++) {
                curves_copy[i] = this.curves[i].cloneObject();
            }
        }
        //model 
        Model ret = new Model(lines_copy, circles_copy, curves_copy);
        //pins
        this.pins.forEach((pin) -> {
            ret.getIOPins().add(pin.cloneObject());
        });
        //out position
        ret.angle = this.angle;
        return ret;
    }

    /**
     * Render model
     *
     * @param g2 Graphics2D
     * @param pos Position of object
     * @param offset Rendereing offset (Only for render gates thet are in
     * visible area)
     * @param screen Sreen size
     * @param select Is selecte ? -> draw select
     * @return Return true if model was rendered
     */
    public boolean renderModel(Graphics2D g2, Point pos, Point offset, Dimension screen, boolean select) {
        if (Tools.isInRange(
                pos.x - offset.x, pos.y - offset.y, screen,
                Math.max(this.width, this.height)
        )) {
            //draw select rect
            if (select) {
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.setColor(Colors.SELECT_RECT);
                g2.drawRect(
                        (int) (pos.x + this.min.x - 10),
                        (int) (pos.y + this.min.y - 10),
                        this.width + 20,
                        this.height + 20
                );
            }
            g2.setStroke(new BasicStroke(2));
            //color
            g2.setColor(this.color);
            //render model
            this.render(g2, pos.x, pos.y);
            //render error 
            if (this.error) {
                Tools.drawError(g2, pos.x, pos.y, Math.max(this.width, this.height) / 3);
            }
            return true;
        }
        return false;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public Color getColor() {
        return this.color;
    }

    private boolean error = false;

    public void errorTag(boolean error) {
        this.error = error;
    }

    public void clone(Model model) {
        Model m = model.cloneObject();
        this.lines = m.lines;
        this.circles = m.circles;
        this.curves = m.curves;
        this.pins.clear();
        this.angle = m.angle;
        m.pins.stream().forEach((p) -> {
            this.pins.add(p);
        });
        computeSize();
    }

    /**
     * Return all points of this model
     *
     * @return
     */
    public List<Point.Double> points() {
        List<Point.Double> ret = new ArrayList<>();
        int i = 0;
        //try 10 times
        while (i < 10) {
            try {
                //get line points
                for (Line line : this.lines) {
                    ret.add(line.p1);
                    ret.add(line.p2);
                }
                //get cirlces points
                for (Circle circe : this.circles) {
                    ret.add(circe.p1);
                }
                //get curves points
                for (Curve curve : this.curves) {
                    ret.add(curve.p1);
                    ret.add(curve.control);
                    ret.add(curve.p2);
                }
                return ret;
            } catch (Exception ex) {
                i++;
            }
        }
        return ret;
    }

}
