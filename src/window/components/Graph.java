/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Martin
 */
public class Graph extends JPanel {

    public List<Point.Double> POINTS = new ArrayList<>();

    public Graph() {
        this.setBackground(Color.WHITE);
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                double w = width;
                double d = getWidth() / (getWidth() - w);
                POINTS.stream().forEach((pts) -> {
                    pts.x = pts.x * d;
                });
            }
        });
    }

    public void move(double x, double y) {
        //move with all points
        this.POINTS.stream().forEach((pts) -> {
            pts.x += x;
            pts.y += y;
        });
        //romove all point that are out of rendering
        for (int i = 0; i < this.POINTS.size(); i++) {
            if (this.POINTS.get(i).x < -10d) {
                this.POINTS.remove(i);
                i = -1;
            }
        }
    }

    private int width;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.width = this.getWidth();

        Graphics2D g2 = (Graphics2D) g;

        //render hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

        //find min and max
        double MIN = Double.MAX_VALUE, MAX = Double.MIN_VALUE;
        for (Point.Double pts : this.POINTS) {
            MIN = Math.min(MIN, pts.y);
            MAX = Math.max(MAX, pts.y);
        }

        //draw all
        g2.clearRect(0, 0, this.getWidth(), this.getHeight());
        //grids
        g2.setColor(Color.GRAY);
        for (int i = 0; i < this.getWidth(); i += this.getWidth() / 5) {
            g2.drawLine(i, 0, i, this.getHeight());
        }
        g2.drawLine(0, (int) (this.getHeight() * 0.1f), this.getWidth(), (int) (this.getHeight() * 0.1f));
        g2.drawLine(0, (int) (this.getHeight() * 0.9f), this.getWidth(), (int) (this.getHeight() * 0.9f));
        g2.drawLine(0, (int) (this.getHeight() * 0.5f), this.getWidth(), (int) (this.getHeight() * 0.5f));
        //draw graph
        g2.setColor(Color.BLUE);
        Point.Double last = null;
        for (Point.Double pts : this.POINTS) {
            double y = (pts.y - MIN) / MAX * this.getHeight() * 0.8d + 0.1d * this.getHeight();
            if (last != null) {
                g2.drawLine((int) last.x, (int) (this.getHeight() - last.y), (int) pts.x, (int) (this.getHeight() - y));
            }
            last = new Point.Double(pts.x, y);
        }
        g2.setColor(Color.GRAY);
        if (MIN != Double.MAX_VALUE && MAX != Double.MIN_VALUE) {
            g2.drawString(String.format("%.2f", MIN), 5, (int) (this.getHeight() * 0.9f) - 2);
            g2.drawString(String.format("%.2f", MAX), 5, (int) (this.getHeight() * 0.1f) - 2);
        }
    }

}
