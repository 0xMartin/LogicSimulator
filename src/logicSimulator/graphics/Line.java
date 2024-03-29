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
package logicSimulator.graphics;

import logicSimulator.Tools;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

/*
 * Logic simlator
 * Author: Martin Krcma
 */
/**
 *
 * @author Martin
 */
public class Line implements Serializable, GraphicsObject {

    public Point.Double p1, p2;
    //graphics node
    public boolean n1 = false, n2 = false;

    /**
     * Create line from two double points (references)
     * @param x1 X position of point 1
     * @param y1 Y position of point 1 
     * @param x2 X position of point 2
     * @param y2 Y position of point 2
     */
    public Line(int x1, int y1, int x2, int y2) {
        this.p1 = new Point.Double(x1, y1);
        this.p2 = new Point.Double(x2, y2);
    }
    
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

    @Override
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

    @Override
    public Line cloneObject() {
        Line l = new Line(Tools.copy(this.p1), Tools.copy(this.p2));
        l.n1 = this.n1;
        l.n2 = this.n2;
        return l;
    }
    
    @Override
    public Point.Double[] getPoints() {
        return new Point.Double[]{this.p1, this.p2};
    }

}
