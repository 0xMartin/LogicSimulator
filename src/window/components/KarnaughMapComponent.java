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
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Propertie;
import logicSimulator.graphics.Line;
import logicSimulator.objects.IOPin;
import logicSimulator.objects.gate.And;
import logicSimulator.objects.gate.Not;
import logicSimulator.objects.gate.Buffer;
import logicSimulator.objects.gate.Or;
import logicSimulator.objects.input.Button;
import logicSimulator.objects.output.Bulp;
import logicSimulator.objects.wiring.Wire;

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
        this.segments.clear();
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

        int celsW = (int) Math.pow(2, this.input / 2) + (this.input % 2 != 0 ? 2 : 0),
                celsH = (int) Math.pow(2, this.input / 2);

        g2.setColor(Color.BLACK);

        //title
        String str = "";
        int offset = 0;
        for (int i = 0; i < (this.input / 2 + (this.input % 2 != 0 ? 1 : 0)); ++i) {
            str += (char) ('A' + i);
            ++offset;
        }
        g2.drawString(str, cW - g2.getFontMetrics().stringWidth(str) / 2,
                cH - ((celsH + 1) * KarnaughMapComponent.CELL_SIZE) / 2);
        str = "";
        for (int i = 0; i < this.input / 2; ++i) {
            str += (char) ('A' + i + offset);
        }
        g2.drawString(str, cW - g2.getFontMetrics().stringWidth(str) - 5
                - ((celsW + 1) * KarnaughMapComponent.CELL_SIZE) / 2,
                cH + Tools.centerYString(g2.getFontMetrics()));

        //table
        for (int i = 0; i < celsW; ++i) {
            for (int j = 0; j < celsH; ++j) {
                //draw bit values
                if (j == 0) {
                    str = Tools.getGreyCode(i, this.input / 2 + (this.input % 2 != 0 ? 1 : 0));
                    g2.drawString(str,
                            (int) ((i - celsW / 2f + 0.5f) * KarnaughMapComponent.CELL_SIZE + cW
                            - g2.getFontMetrics().stringWidth(str) / 2),
                            (int) ((j - celsH / 2f - 0.2f) * KarnaughMapComponent.CELL_SIZE + cH
                            + Tools.centerYString(g2.getFontMetrics())));
                }
                if (i == 0) {
                    str = Tools.getGreyCode(j, this.input / 2);
                    g2.drawString(str,
                            (int) ((i - celsW / 2f - 0.2f) * KarnaughMapComponent.CELL_SIZE + cW
                            - g2.getFontMetrics().stringWidth(str)),
                            (int) ((j - celsH / 2f + 0.5f) * KarnaughMapComponent.CELL_SIZE + cH
                            + Tools.centerYString(g2.getFontMetrics())));
                }
                //draw cell rect
                g2.drawRect(
                        (int) ((i - celsW / 2f) * KarnaughMapComponent.CELL_SIZE + cW),
                        (int) ((j - celsH / 2f) * KarnaughMapComponent.CELL_SIZE + cH),
                        KarnaughMapComponent.CELL_SIZE,
                        KarnaughMapComponent.CELL_SIZE
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
                            (int) ((last.x - celsW / 2f + 0.15f) * KarnaughMapComponent.CELL_SIZE + cW),
                            (int) ((last.y - celsH / 2f + 0.15f) * KarnaughMapComponent.CELL_SIZE + cH),
                            (int) (Math.abs(p.x - last.x + 0.7f) * KarnaughMapComponent.CELL_SIZE),
                            (int) (Math.abs(p.y - last.y + 0.7f) * KarnaughMapComponent.CELL_SIZE),
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
                        (int) ((i - celsW / 2f + 0.5f) * KarnaughMapComponent.CELL_SIZE + cW
                        - g2.getFontMetrics().stringWidth("0") / 2),
                        (int) ((j - celsH / 2f + 0.5f) * KarnaughMapComponent.CELL_SIZE + cH
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

        int celsW = (int) Math.pow(2, this.input / 2) + (this.input % 2 != 0 ? 2 : 0),
                celsH = (int) Math.pow(2, this.input / 2);

        for (int i = 0; i < celsW; ++i) {
            for (int j = 0; j < celsH; ++j) {
                Rectangle rect = new Rectangle((int) ((i - celsW / 2f) * KarnaughMapComponent.CELL_SIZE + cW),
                        (int) ((j - celsH / 2f) * KarnaughMapComponent.CELL_SIZE + cH),
                        KarnaughMapComponent.CELL_SIZE,
                        KarnaughMapComponent.CELL_SIZE);
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
        int celsW = (int) Math.pow(2, this.input / 2) + (this.input % 2 != 0 ? 2 : 0),
                celsH = (int) Math.pow(2, this.input / 2);

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
        final StringBuilder expression = new StringBuilder();
        final List<String> grays = new ArrayList<>();

        this.segments.stream().forEach((seg) -> {
            Point[] pts = getAllPointsOfSegment(seg);

            grays.clear();
            for (Point pt : pts) {
                grays.add(Tools.getGreyCode(pt.x, this.input / 2 + (this.input % 2 != 0 ? 1 : 0))
                        + Tools.getGreyCode(pt.y, this.input / 2));
            }

            if (!grays.isEmpty()) {
                String compared = grays.get(0);
                boolean[] exSegBits = new boolean[compared.length()];

                for (int i = 0; i < exSegBits.length; ++i) {
                    exSegBits[i] = true;
                }

                for (int i = 1; i < grays.size(); ++i) {
                    for (int j = 0; j < compared.length(); ++j) {
                        if (compared.charAt(j) != grays.get(i).charAt(j)) {
                            exSegBits[j] = false;
                        }
                    }
                }

                String expressionSegment = "";
                for (int i = 0; i < exSegBits.length; ++i) {
                    if (exSegBits[i]) {
                        expressionSegment += compared.charAt(i) == '0' ? "!" + (char) ('A' + i) : "" + (char) ('A' + i);
                    }
                }

                expression.append(expressionSegment).append("+");
            }
        });

        String out = expression.toString();

        return out.substring(0, out.length() - 1);
    }

    private Point[] getAllPointsOfSegment(Point[] seg) {

        if (seg.length == 8) {
            return new Point[]{seg[0], seg[2], seg[4], seg[6]};
        } else if (seg.length == 4) {
            Point[] pts1 = getAllPointsOfSegment(new Point[]{seg[0], seg[1]});
            Point[] pts2 = getAllPointsOfSegment(new Point[]{seg[2], seg[3]});
            Point[] pts = new Point[pts1.length + pts2.length];
            int index = 0;
            for (int i = 0; i < pts1.length; ++i) {
                pts[index++] = pts1[i];
            }
            for (int i = 0; i < pts2.length; ++i) {
                pts[index++] = pts2[i];
            }
            return pts;
        }

        //star and end corner
        Point p1 = seg[0];
        Point p2 = seg[seg.length - 1];

        //width and height of rect
        int width = Math.abs(p1.x - p2.x) + 1;
        int height = Math.abs(p1.y - p2.y) + 1;

        Point[] pts = new Point[width * height];

        int index = 0;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                pts[index++] = new Point(p1.x + i, p1.y + j);
            }
        }

        return pts;
    }

    /**
     * Build circuit from expression
     *
     * @param logicExpression Expression
     * @return List<WorkSpaceObject>
     */
    public List<WorkSpaceObject> buildCircuit(String logicExpression) {
        List<WorkSpaceObject> circuit = new ArrayList<>();

        //split expression on and segments
        final String[] segs = logicExpression.split("\\+");

        int step = logicSimulator.LogicSimulatorCore.WORK_SPACE_STEP;

        //used components
        Wire w;
        Button b;
        Not not;

        //virtual cursor
        final Point vCursor = new Point(0, 0);
        final Point wPoint = new Point();

        //build inputs nets
        final LinkedList<Integer> nets = new LinkedList<>();

        for (int i = 0; i < this.input; ++i) {
            b = new Button(Tools.copy(vCursor), 1);
            b.changePropertie(new Propertie("Label", (char) ('A' + i) + ""));
            b.getModel().rotate(1);
            circuit.add(b);
            nets.add(vCursor.x);

            vCursor.x += 3 * step;
        }

        vCursor.x += 6 * step;
        vCursor.y += step * 4;

        //build ands
        final List<WorkSpaceObject> segComponents = new ArrayList<>();
        ExVar[] exVars;
        for (String segment : segs) {
            //parse segment from string to ExVar format
            exVars = parseSegmentExVar(segment);

            List<IOPin> pins = null;
            WorkSpaceObject obj;
            if (exVars.length == 1) {
                //buffer / not
                obj = exVars[0].not ? new Not(Tools.copy(vCursor), 1) : new Buffer(Tools.copy(vCursor), 1);
                exVars[0].not = false;
            } else {
                //add and
                obj = new And(Tools.copy(vCursor), 1, exVars.length);
            }
            circuit.add(obj);
            segComponents.add(obj);
            pins = obj.getModel().getIOPins();
            obj.getModel().rotate(3);

            if (pins == null) {
                continue;
            }

            //connect each input of added and
            for (int i = 0; i < exVars.length; ++i) {
                wPoint.x = (int) (pins.get(i).getPosition().x + obj.getPosition().x);
                wPoint.y = (int) (pins.get(i).getPosition().y + obj.getPosition().y);

                if (exVars[i].not) {
                    //wire with not
                    not = new Not(new Point(nets.getLast() + step * 2, wPoint.y), 1);
                    not.getModel().rotate(3);
                    circuit.add(not);

                    List<IOPin> notPins = not.getModel().getIOPins();

                    w = new Wire();
                    w.getPath().add(new Line(
                            new Point(nets.get(exVars[i].index), wPoint.y),
                            new Point((int) (notPins.get(0).getPosition().x + not.getPosition().x), wPoint.y)
                    ));
                    circuit.add(w);

                    w = new Wire();
                    w.getPath().add(new Line(
                            new Point((int) (notPins.get(1).getPosition().x + not.getPosition().x), wPoint.y),
                            Tools.copy(wPoint)
                    ));
                    circuit.add(w);
                } else {
                    //only wire
                    w = new Wire();
                    w.getPath().add(new Line(
                            new Point(nets.get(exVars[i].index), wPoint.y),
                            Tools.copy(wPoint)
                    ));
                    circuit.add(w);
                }
            }

            vCursor.y += step * (obj.getModel().getHeight() / step + 1);
        }

        //nets
        int circHeight = vCursor.y;
        vCursor.x = 0;
        vCursor.y = 0;
        for (int i = 0; i < nets.size(); ++i) {
            w = new Wire();
            w.getPath().add(new Line(Tools.copy(vCursor), new Point(vCursor.x, vCursor.y + circHeight)));
            circuit.add(w);
            vCursor.x += 3 * step;
        }

        //only one segment component
        if (segComponents.size() == 1) {
            WorkSpaceObject obj = segComponents.get(0);

            Point.Double out = null;
            for (IOPin pin : obj.getPins()) {
                if (pin.mode == IOPin.MODE.OUTPUT) {
                    out = pin.getPosition();
                }
            }

            //add output bulp
            Wire w1 = new Wire();
            w1.getPath().add(new Line(
                    new Point((int) (out.x + obj.getPosition().x),
                            (int) (out.y + obj.getPosition().y)),
                    new Point((int) (out.x + obj.getPosition().x + step * 3),
                            (int) (out.y + obj.getPosition().y))
            ));
            circuit.add(w1);
            Bulp bulp = new Bulp(new Point((int) (out.x + obj.getPosition().x + step * 3),
                    (int) (out.y + obj.getPosition().y)), 1);
            circuit.add(bulp);

            //select all components (for copy vector)
            circuit.stream().forEach((obj2) -> {
                obj2.select();
            });

            return circuit;
        }

        //create or gate
        vCursor.x += step * (10 + segs.length);
        Or or = new Or(new Point(vCursor.x, (int) segComponents.get(0).getPosition().y + step * (segs.length / 2)),
                1, segs.length);
        or.getModel().rotate(3);
        circuit.add(or);

        //create list with or inputs (sorted be y position)
        List<Point> orPins = new ArrayList<>();
        or.getPins().stream().forEach((pin) -> {
            if (pin.mode == IOPin.MODE.INPUT) {
                orPins.add(new Point((int) pin.getPosition().x, (int) pin.getPosition().y));
            } else {
                //add output bulp
                Wire w1 = new Wire();
                w1.getPath().add(new Line(
                        new Point((int) (pin.getPosition().x + or.getPosition().x),
                                (int) (pin.getPosition().y + or.getPosition().y)),
                        new Point((int) (pin.getPosition().x + or.getPosition().x + step * 3),
                                (int) (pin.getPosition().y + or.getPosition().y))
                ));
                circuit.add(w1);
                Bulp bulp = new Bulp(new Point((int) (pin.getPosition().x + or.getPosition().x + step * 3),
                        (int) (pin.getPosition().y + or.getPosition().y)), 1);
                circuit.add(bulp);
            }
        });
        orPins.sort((Point o1, Point o2) -> {
            if (o1.y > o2.y) {
                return 1;
            } else if (o1.y < o2.y) {
                return -1;
            } else {
                return 0;
            }
        });

        //connect all ands with or
        for (int i = 0; i < segComponents.size(); ++i) {
            WorkSpaceObject obj = segComponents.get(i);
            IOPin out = obj.getPins().get(obj.getPins().size() - 1);

            //wire
            w = new Wire();
            if (i == 0) {
                //streight
                w.getPath().add(new Line(
                        new Point((int) (out.getPosition().x + obj.getPosition().x),
                                (int) (out.getPosition().y + obj.getPosition().y)),
                        new Point((int) (orPins.get(i).x + or.getPosition().x),
                                (int) (orPins.get(i).y + or.getPosition().y))
                ));
            } else {
                //complex
                w.getPath().add(new Line(
                        new Point((int) (out.getPosition().x + obj.getPosition().x),
                                (int) (out.getPosition().y + obj.getPosition().y)),
                        new Point((int) (out.getPosition().x + obj.getPosition().x + step * i),
                                (int) (out.getPosition().y + obj.getPosition().y))
                ));
                w.getPath().add(new Line(
                        new Point((int) (out.getPosition().x + obj.getPosition().x + step * i),
                                (int) (out.getPosition().y + obj.getPosition().y)),
                        new Point((int) (out.getPosition().x + obj.getPosition().x + step * i),
                                (int) (orPins.get(i).y + or.getPosition().y))
                ));
                w.getPath().add(new Line(
                        new Point((int) (out.getPosition().x + obj.getPosition().x + step * i),
                                (int) (orPins.get(i).y + or.getPosition().y)),
                        new Point((int) (orPins.get(i).x + or.getPosition().x),
                                (int) (orPins.get(i).y + or.getPosition().y))
                ));
            }
            circuit.add(w);
        }

        //select all components (for copy vector)
        circuit.stream().forEach((obj) -> {
            obj.select();
        });

        return circuit;
    }

    private class ExVar {

        //A -> 0, B -> 1, ...
        final int index;
        boolean not;

        public ExVar(int index, boolean not) {
            this.index = index;
            this.not = not;
        }
    }

    private ExVar[] parseSegmentExVar(String segment) {
        int varCount = 0;
        for (int i = 0; i < segment.length(); ++i) {
            varCount += segment.charAt(i) != '!' ? 1 : 0;
        }

        ExVar[] vars = new ExVar[varCount];

        int index = 0;
        boolean not = false;
        for (int i = 0; i < segment.length(); ++i) {
            if (segment.charAt(i) == '!') {
                not = true;
            } else {
                vars[index++] = new ExVar(segment.charAt(i) - (int) 'A', not);
                not = false;
            }
        }

        return vars;
    }

}
