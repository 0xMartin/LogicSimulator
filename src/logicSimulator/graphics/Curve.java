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
import java.awt.geom.QuadCurve2D;
import java.io.Serializable;

/*
 * Logic simlator
 * Author: Martin Krcma
 */
/**
 *
 * @author Martin
 */
public class Curve implements Serializable, GraphicsObject {

    public Point.Double.Double p1, p2, control;

    /**
     * Create line from two double points (references)
     *
     * @param x1 X position of point 1
     * @param y1 Y position of point 1
     * @param xc X position of control point
     * @param yc Y position of control point
     * @param x2 X position of point 2
     * @param y2 Y position of point 2
     */
    public Curve(int x1, int y1, int xc, int yc, int x2, int y2) {
        this.p1 = new Point.Double(x1, y1);
        this.p2 = new Point.Double(x2, y2);
        this.control = new Point.Double(xc, yc);
    }

    public Curve(Point.Double p1, Point.Double control, Point.Double p2) {
        this.p1 = p1;
        this.control = control;
        this.p2 = p2;
    }

    @Override
    public void draw(Graphics2D g2, int xOff, int yOff) {
        QuadCurve2D.Double curve = new QuadCurve2D.Double(
                p1.x + xOff,
                p1.y + yOff,
                control.x + xOff,
                control.y + yOff,
                p2.x + xOff,
                p2.y + yOff
        );
        g2.draw(curve);
    }

    @Override
    public Curve cloneObject() {
        Curve c = new Curve(Tools.copy(this.p1), Tools.copy(this.control), Tools.copy(this.p2));
        return c;
    }

    @Override
    public Point.Double[] getPoints() {
        return new Point.Double[]{this.p1, this.p2, this.control};
    }

}
