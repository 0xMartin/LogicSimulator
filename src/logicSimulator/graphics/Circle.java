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
public class Circle implements Serializable, GraphicsObject {

    public Point.Double p1;
    public int radius;

    /**
     * Create line from two double points (references)
     *
     * @param x1 X position of center
     * @param y1 Y position of center
     * @param radius Radius of circle
     */
    public Circle(int x1, int y1, int radius) {
        this.p1 = new Point.Double(x1, y1);
        this.radius = radius;
    }

    public Circle(Point.Double p1, int radius) {
        this.p1 = p1;
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g2, int xOff, int yOff) {
        //line
        g2.drawOval((int) (this.p1.x + xOff) - this.radius, (int) (this.p1.y + yOff) - this.radius, this.radius * 2, this.radius * 2);
    }

    @Override
    public Circle cloneObject() {
        Circle c = new Circle(Tools.copy(this.p1), this.radius);
        return c;
    }

    @Override
    public Point.Double[] getPoints() {
        return new Point.Double[]{this.p1};
    }

}
