package logicSimulator.common;

import logicSimulator.Tools;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Logic simlator
 * Author: Martin Krcma
 */
/**
 *
 * @author Martin
 */
public class Line implements Serializable {

    public Point.Double p1, p2;
    //graphics node
    public boolean n1 = false, n2 = false;

    /**
     * Create line from two double points (references)
     * @param p1 Point p1
     * @param p2 Point p2
     */
    public Line(Point.Double p1, Point.Double p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * Create line from two points (values)
     * @param p1 Point p1
     * @param p2 Point p2
     */
    public Line(Point p1, Point p2) {
        this.p1 = new Point.Double(p1.x, p1.y);
        this.p2 = new Point.Double(p2.x, p2.y);
    }

    
    public void streightGroup(Line l) {
        if (l == null) {
            this.p1 = new Point.Double(p1.x, p1.y);
        } else {
            this.p1 = l.p2;
        }
    }

    /**
     * This make chage only in one axis x or y (line stays straight)
     *
     * @param x X position of cursor
     * @param y Y position of cursor
     */
    public void setEndStreight(int x, int y) {
        if (p1.x - p2.x == p1.y - p2.y) {
            //can choose direct
            if (Math.abs(p1.x - x) > Math.abs(p1.y - y)) {
                this.p2.x = x;
                this.p2.y = this.p1.y;
            } else {
                this.p2.y = y;
                this.p2.x = this.p1.x;
            }
        } else {
            if (this.p1.y - this.p2.y == 0) {
                this.p2.x = x;
                this.p2.y = this.p1.y;
            } else {
                this.p2.y = y;
                this.p2.x = this.p1.x;
            }
        }
    }

    public void draw(Graphics2D g2, int xOff, int yOff) {
        //line
        g2.drawLine(
                (int) (p1.x + xOff),
                (int) (p1.y + yOff),
                (int) (p2.x + xOff),
                (int) (p2.y + yOff)
        );
        //draw nodes
        if (this.n1) {
            g2.fillOval(
                    (int) (p1.x + xOff - 4),
                    (int) (p1.y + yOff - 4),
                    8, 8);
        }
        if (this.n2) {
            g2.fillOval(
                    (int) (p2.x + xOff - 4),
                    (int) (p2.y + yOff - 4),
                    8, 8);
        }
    }

    public double length() {
        return Math.sqrt(Math.pow(this.p1.x - this.p2.x, 2) + Math.pow(this.p1.y - this.p2.y, 2));
    }

    public Line cloneObject() {
        Line l = new Line(Tools.copy(this.p1), Tools.copy(this.p2));
        l.n1 = this.n1;
        l.n2 = this.n2;
        return l;
    }
    
    public List<Point.Double> getPoints(){
        List<Point.Double> list = new ArrayList<>();
        list.add(this.p1);
        list.add(this.p2);
        return list;
    }

}
