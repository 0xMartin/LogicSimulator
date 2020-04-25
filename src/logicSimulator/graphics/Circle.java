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
