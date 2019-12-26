package logicSimulator.common;

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
public class Circle implements Serializable {

    public Point.Double p1;
    public int radius;

    public Circle(Point.Double p1, int radius) {
        this.p1 = p1;
        this.radius = radius;
    }

    public void draw(Graphics2D g2, int xOff, int yOff) {
        //line
        g2.drawOval((int) (this.p1.x + xOff) - this.radius, (int) (this.p1.y + yOff) - this.radius, this.radius * 2, this.radius * 2);
    }

    public Circle cloneObject() {
        Circle c = new Circle(Tools.copy(this.p1), this.radius);
        return c;
    }

}
