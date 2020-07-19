/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import logicSimulator.Tools;

/**
 * Karnaugh map
 *
 * @author Martin
 */
public class KarnaughMapComponent extends JPanel implements MouseListener {

    private static final int CELL_SIZE = 40;

    private final List<Point[]> segments = new ArrayList<>();

    private int input;

    private boolean[] map;

    private final Color[] colors = new Color[]{
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.CYAN,
        Color.PINK,
        Color.MAGENTA
    };

    public KarnaughMapComponent() {
        this.setNumberOfInputs(2);
        super.addMouseListener(this);
    }

    public void setNumberOfInputs(int inputs) {
        this.input = inputs;
        this.map = new boolean[(int) Math.pow(2, inputs)];
    }

    public boolean[] getMap() {
        return this.map;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        Tools.setHighQuality(g2);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, super.getWidth(), super.getHeight());

        int cW = super.getWidth() / 2;
        int cH = super.getHeight() / 2;

        int celsW = (int) Math.pow(2, this.input / 2) + (this.input % 2 != 0 ? 1 : 0),
                celsH = (int) Math.pow(2, this.input / 2);

        g2.setColor(Color.BLACK);

        String str;
        for (int i = 0; i < celsW; ++i) {
            for (int j = 0; j < celsH; ++j) {
                //draw bit values
                if (j == 0) {
                    str = Tools.getGreyCode(i, this.input / 2 + (this.input % 2 != 0 ? 1 : 0));
                    g2.drawString(str,
                            (int) ((i - celsW / 2f + 0.5f) * this.CELL_SIZE + cW
                            - g2.getFontMetrics().stringWidth(str) / 2),
                            (int) ((j - celsH / 2f - 0.2f) * this.CELL_SIZE + cH
                            + Tools.centerYString(g2.getFontMetrics())));
                }
                if (i == 0) {
                    str = Tools.getGreyCode(j, this.input / 2);
                    g2.drawString(str,
                            (int) ((i - celsW / 2f - 0.2f) * this.CELL_SIZE + cW
                            - g2.getFontMetrics().stringWidth(str)),
                            (int) ((j - celsH / 2f + 0.5f) * this.CELL_SIZE + cH
                            + Tools.centerYString(g2.getFontMetrics())));
                }
                //draw cell rect
                g2.drawRect(
                        (int) ((i - celsW / 2f) * this.CELL_SIZE + cW),
                        (int) ((j - celsH / 2f) * this.CELL_SIZE + cH),
                        this.CELL_SIZE,
                        this.CELL_SIZE
                );
            }
        }

        //draw segments
        g2.setComposite(AlphaComposite.SrcOver.derive(0.4f));
        int cIndex = 0;
        for (Point[] seg : this.segments) {
            g2.setColor(this.colors[cIndex]);
            cIndex = cIndex + 1 >= this.colors.length ? 0 : cIndex + 1;

            Point last = null;
            for (Point p : seg) {
                if (last != null) {
                    g2.fillRoundRect(
                            (int) ((last.x - celsW / 2f + 0.15f) * this.CELL_SIZE + cW),
                            (int) ((last.y - celsH / 2f + 0.15f) * this.CELL_SIZE + cH),
                            (int) (Math.abs(p.x - last.x + 0.7f) * this.CELL_SIZE),
                            (int) (Math.abs(p.y - last.y + 0.7f) * this.CELL_SIZE),
                            8, 8
                    );
                    last = null;
                } else {
                    last = p;
                }
            }
        }

        //draw value in map
        g2.setColor(Color.BLACK);
        g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));
        for (int i = 0; i < celsW; ++i) {
            for (int j = 0; j < celsH; ++j) {
                g2.drawString(this.map[i + j * celsW] ? "1" : "0",
                        (int) ((i - celsW / 2f + 0.5f) * this.CELL_SIZE + cW
                        - g2.getFontMetrics().stringWidth("0") / 2),
                        (int) ((j - celsH / 2f + 0.5f) * this.CELL_SIZE + cH
                        + Tools.centerYString(g2.getFontMetrics())));
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        //change bit of map

        int cW = super.getWidth() / 2;
        int cH = super.getHeight() / 2;

        int celsW = (int) Math.pow(2, this.input / 2) + (this.input % 2 != 0 ? 1 : 0),
                celsH = (int) Math.pow(2, this.input / 2);

        for (int i = 0; i < celsW; ++i) {
            for (int j = 0; j < celsH; ++j) {
                Rectangle rect = new Rectangle((int) ((i - celsW / 2f) * this.CELL_SIZE + cW),
                        (int) ((j - celsH / 2f) * this.CELL_SIZE + cH),
                        this.CELL_SIZE,
                        this.CELL_SIZE);
                if (rect.contains(e.getPoint())) {
                    this.map[i + j * celsW] = !this.map[i + j * celsW];
                }
            }
        }

        findSegments();

        super.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private final Point[] patterns = new Point[]{
        //16
        new Point(16, 16),
        new Point(16, 8),
        new Point(8, 16),
        new Point(16, 4),
        new Point(4, 16),
        new Point(16, 2),
        new Point(2, 16),
        new Point(16, 1),
        new Point(1, 16),
        //8
        new Point(8, 8),
        new Point(8, 4),
        new Point(4, 8),
        new Point(8, 2),
        new Point(2, 8),
        new Point(8, 1),
        new Point(1, 8),
        //4
        new Point(4, 4),
        new Point(4, 2),
        new Point(2, 4),
        new Point(1, 4),
        new Point(4, 1),
        //2
        new Point(2, 2),
        new Point(2, 1),
        new Point(1, 2),
        //1
        new Point(1, 1)
    };

    public void findSegments() {
        this.segments.clear();

        //compute number of cels
        int celsW = (int) Math.pow(2, this.input / 2) + (this.input % 2 != 0 ? 1 : 0),
                celsH = (int) Math.pow(2, this.input / 2);

        //compute max size of pattern
        double wP = 0, hP = 0;
        int val;
        for (int i = 0; i < 10; ++i) {
            val = (int) Math.pow(2, i);
            if (val >= celsW) {
                wP = val > celsW ? Math.pow(2, i - 1) : Math.pow(2, i);
                break;
            }
        }
        for (int i = 0; i < 10; ++i) {
            val = (int) Math.pow(2, i);
            if (val >= celsH) {
                hP = val > celsW ? Math.pow(2, i - 1) : Math.pow(2, i);
                break;
            }
        }

        //find all ones
        List<MPoint> oneList = new ArrayList<>();
        for (int i = 0; i < celsW; ++i) {
            for (int j = 0; j < celsH; ++j) {
                if (this.map[i + j * celsW]) {
                    oneList.add(new MPoint(i, j));
                }
            }
        }

        for (int i = 0; i < this.patterns.length; ++i) {
            if (findAllSquerPatterns(oneList, this.patterns[i].x, this.patterns[i].y, celsW, celsH) == 0) {
                break;
            }
        }

    }

    private class MPoint {

        Point pt;
        boolean included = false;

        public MPoint(int x, int y) {
            this.pt = new Point(x, y);
        }
    }

    private int findAllSquerPatterns(List<MPoint> oneList, int pW, int pH, int mW, int mH) {

        final Point v1 = new Point(), v2 = new Point();

        oneList.stream().forEach((pt) -> {
            Point corner = squerFinder(pt, oneList, pW, pH, mW, mH);
            if (corner != null) {
                if (corner.x < pt.pt.x) {
                    v1.x = pt.pt.x;
                    v1.y = corner.y;
                    v2.x = 0;
                    v2.y = pt.pt.y;
                    this.segments.add(new Point[]{pt.pt, v1, v2, corner});
                } else if (corner.y < pt.pt.y) {
                    v1.x = corner.x;
                    v1.y = pt.pt.y;
                    v2.x = pt.pt.x;
                    v2.y = 0;
                    this.segments.add(new Point[]{pt.pt, v1, v2, corner});
                } else {
                    this.segments.add(new Point[]{pt.pt, corner});
                }
            }
        });

        int remaining = 0;
        remaining = oneList.stream()
                .filter((pt) -> (!pt.included))
                .map((_item) -> 1)
                .reduce(remaining, Integer::sum);

        return remaining;
    }

    /**
     * Find squer in map
     *
     * @param p1 Left top corner of squer
     * @param pts Positions of all ones in map
     * @param width Width of patterm
     * @param height Height of pattern
     * @param mapW Width of map
     * @param mapH Height of map
     * @return buttom right corner of squer
     */
    private Point squerFinder(MPoint p1, List<MPoint> pts, int width, int height, int mapW, int mapH) {
        final Point vP = new Point();

        List<MPoint> includeRemover = new ArrayList<>();

        boolean allCelsOnInclude = true;

        for (int i = 0; i < width; ++i) {
            LOOP:
            for (int j = 0; j < height; ++j) {

                vP.x = p1.pt.x + i;
                vP.y = p1.pt.y + j;

                while (vP.x >= mapW) {
                    vP.x -= mapW;
                }

                while (vP.y >= mapH) {
                    vP.y -= mapH;
                }

                for (MPoint pt : pts) {
                    if (pt.pt.x == vP.x && pt.pt.y == vP.y) {
                        if (!pt.included) {
                            allCelsOnInclude = false;
                            includeRemover.add(pt);
                        }
                        pt.included = true;
                        continue LOOP;
                    }
                }

                //remove all includes
                includeRemover.stream().forEach((pt) -> {
                    pt.included = false;
                });

                return null;
            }
        }

        if (allCelsOnInclude) {
            return null;
        }

        Point corner = new Point(p1.pt.x + width - 1, p1.pt.y + height - 1);

        while (corner.x >= mapW) {
            corner.x -= mapW;
        }

        while (corner.y >= mapH) {
            corner.y -= mapH;
        }

        return corner;
    }

    public String buildLogicExpression() {
        StringBuilder strB = new StringBuilder();

        
        
        return strB.toString();
    }

}
