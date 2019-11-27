/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import logicSimulator.ui.Colors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import logicSimulator.LogicSimulatorCore;

/**
 *
 * @author Martin
 */
public class Model implements Serializable {

    private int width = 0, height = 0;

    private final List<IOPin> pins;

    private Color color = Colors.GATE;

    /**
     * angle of model
     */
    private int angle = 0;

    public int getAngle() {
        return this.angle;
    }

    public Line[] lines;
    public Circle[] circles;
    public Curve[] curves;

    public Point.Double[] points;

    public Model(Line[] lines, Circle[] circles, Curve[] curves) {
        this.lines = lines;
        this.circles = circles;
        this.curves = curves;
        this.pins = new ArrayList<>();
        computeSize();
    }

    public List<IOPin> getIOPins() {
        return this.pins;
    }

    /**
     * Compute size of this model
     */
    public void computeSize() {
        Point.Double max = new Point.Double(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Point.Double min = new Point.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
        //find max and min for x and y axis
        if (this.lines != null) {
            for (Line l : this.lines) {
                //max
                max.x = Math.max(max.x, l.p1.x);
                max.x = Math.max(max.x, l.p2.x);
                max.y = Math.max(max.y, l.p1.y);
                max.y = Math.max(max.y, l.p2.y);
                //min
                min.x = Math.min(min.x, l.p1.x);
                min.x = Math.min(min.x, l.p2.x);
                min.y = Math.min(min.y, l.p1.y);
                min.y = Math.min(min.y, l.p2.y);
            }
        }
        //find max and min for x and y axis
        if (this.circles != null) {
            for (Circle c : this.circles) {
                //max
                max.x = Math.max(max.x, c.p1.x + c.radius);
                max.y = Math.max(max.y, c.p1.y + c.radius);
                //min
                min.x = Math.min(min.x, c.p1.x - c.radius);
                min.y = Math.min(min.y, c.p1.y - c.radius);
            }
        }
        //compute width and height
        this.width = (int) (max.x - min.x);
        this.height = (int) (max.y - min.y);
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
            pin.render(g2, new Point(x, y));
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
        //points
        if (this.points != null) {
            for (Point.Double p : this.points) {
                Tools.rotatePoint(p, A);
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
        if (x >= mPosition.x - this.width / 2
                && x <= mPosition.x + this.width / 2) {
            if (y >= mPosition.y - this.height / 2
                    && y <= mPosition.y + this.height / 2) {
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
        //copy points
        Point.Double[] points_copy = null;
        if (this.points != null) {
            points_copy = new Point.Double[this.points.length];
            for (int i = 0; i < this.points.length; i++) {
                points_copy[i] = Tools.copy(this.points[i]);
            }
        }
        //model 
        Model ret = new Model(lines_copy, circles_copy, curves_copy);
        ret.points = points_copy;
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
     * @param offset Rendereing offset (Only for render gates thet are in visible area)
     * @param screen Sreen size
     * @param select Is selecte ? -> draw select
     */
    public void renderModel(Graphics2D g2, Point pos, Point offset, Dimension screen, boolean select) {
        if (Tools.isInRange(
                pos.x - offset.x, pos.y - offset.y, screen,
                Math.max(this.width, this.height)
        )) {
            //draw select rect
            if (select) {
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.setColor(Colors.SELECT_RECT);
                g2.drawRect(
                        pos.x - this.width / 2 - 10,
                        pos.y - this.height / 2 - 10,
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
        }
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
        this.points = m.points;
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
                //get point
                ret.addAll(Arrays.asList(this.points));
                return ret;
            } catch (Exception ex) {
                i++;
            }
        }
        return ret;
    }

}
