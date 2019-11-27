package logicSimulator.common;

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
public class Curve implements Serializable {

    public Point.Double.Double p1, p2, control;

    public Curve(Point.Double p1, Point.Double control, Point.Double p2) {
        this.p1 = p1;
        this.control = control;
        this.p2 = p2;
    }

    public void draw(Graphics2D g2, int xOff, int yOff) {
        QuadCurve2D.Double curve = new QuadCurve2D.Double(p1.x + xOff, p1.y + yOff, control.x + xOff, control.y + yOff, p2.x + xOff, p2.y + yOff);
        g2.draw(curve);
    }

    public Curve cloneObject() {
        Curve c = new Curve(Tools.copy(this.p1), Tools.copy(this.control), Tools.copy(this.p2));
        return c;
    }

}
