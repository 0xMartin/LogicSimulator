/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.projectFile.WorkSpace;
import java.awt.Color;
import java.awt.Component;
import logicSimulator.ui.Colors;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import logicSimulator.graphics.Circle;
import logicSimulator.graphics.Curve;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.objects.LogicModule;
import logicSimulator.common.Model;
import logicSimulator.objects.wiring.BitGet;
import logicSimulator.objects.input.Button;
import logicSimulator.objects.input.Clock;
import logicSimulator.objects.input.RandomGenerator;
import logicSimulator.objects.output.Bulp;
import logicSimulator.objects.output.RasterScreen;
import logicSimulator.objects.output.VectorScreen;
import logicSimulator.objects.gate.And;
import logicSimulator.objects.gate.Buffer;
import logicSimulator.objects.gate.DMUX;
import logicSimulator.objects.gate.MUX;
import logicSimulator.objects.gate.Nand;
import logicSimulator.objects.gate.Nor;
import logicSimulator.objects.gate.Not;
import logicSimulator.objects.gate.Nxor;
import logicSimulator.objects.gate.Or;
import logicSimulator.objects.gate.Xor;
import logicSimulator.objects.input.KeyBoard;
import logicSimulator.objects.memory.Counter;
import logicSimulator.objects.memory.DFlipFlop;
import logicSimulator.objects.memory.JKFlipFlop;
import logicSimulator.objects.memory.ROMRAM;
import logicSimulator.objects.memory.RSFlipFlop;
import logicSimulator.objects.memory.TFlipFlop;
import logicSimulator.objects.output.SevenSeg;
import logicSimulator.objects.output.TextScreen;
import logicSimulator.objects.wiring.BitSet;
import logicSimulator.objects.wiring.Bridge;
import logicSimulator.objects.wiring.Input;
import logicSimulator.objects.wiring.Output;
import logicSimulator.objects.wiring.Wire;

/**
 *
 * @author Martin
 */
public class Tools {

    /**
     * Convert decimal number to bin and get number digits of bin number: dec:
     * 16 -> length: 5
     *
     * @param dec Decimal number
     * @return
     */
    public static int binLength(int dec) {
        int length = 1;
        while (dec >= 2) {
            dec -= dec % 2 == 0 ? 0 : 1;
            length++;
            dec /= 2;
        }
        return length;
    }

    /**
     * Build tree model from input string data
     *
     * @param data List<String[]> Every string[] is one folder in tree, first
     * item in array is name of folder and next are items
     * @param rootName Name of root node
     * @return
     */
    public static DefaultTreeModel buildTreeModel(List<String[]> data, String rootName) {
        //root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        root.setUserObject(rootName);
        //build tree of default components from "tools"
        data.forEach((group) -> {
            DefaultMutableTreeNode groupNode = null;
            for (int i = 0; i < group.length; i++) {
                if (i == 0) {
                    groupNode = new DefaultMutableTreeNode(group[i]);
                    root.add(groupNode);
                } else {
                    groupNode.add(new DefaultMutableTreeNode(group[i]));
                }
            }
        });
        return new DefaultTreeModel(root);
    }

    /**
     * Resize image (BufferedImage)
     *
     * @param img BufferedImage (image to risize)
     * @param width Width of new image
     * @param height Height of new image
     * @return
     */
    public static BufferedImage resizeImage(BufferedImage img, int width, int height) {
        BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) ret.getGraphics();
        //render hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g2.scale((float) width / (float) img.getWidth(), (float) height / (float) img.getHeight());
        g2.drawImage(img, 0, 0, null);
        return ret;
    }

    /**
     * Test if point [x, y] is in 2D range
     *
     * @param x X position of point
     * @param y Y position of point
     * @param range 2D Range
     * @param offset Offest for bounds of both dimensions of range
     * @return
     */
    public static boolean isInRange(int x, int y, Dimension range, int offset) {
        return x + offset >= 0 && y + offset >= 0 && x - offset < range.width && y - offset < range.height;
    }

    /**
     * Copy point.int
     *
     * @param p Point
     * @return
     */
    public static Point copy(Point p) {
        return new Point(p.x, p.y);
    }

    /**
     * Copy point.double
     *
     * @param p Point
     * @return
     */
    public static Point.Double copy(Point.Double p) {
        return new Point.Double(p.x, p.y);
    }

    /**
     * Return max point, maximum x and y from list of points
     *
     * @param list List of points
     * @return
     */
    public static Point maxPoint(Point[] list) {
        Point max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Point p : list) {
            max.x = Math.max(max.x, p.x);
            max.y = Math.max(max.y, p.y);
        }
        return max;
    }

    /**
     * Return min point, minimum x and y from list of points
     *
     * @param list List of points
     * @return
     */
    public static Point minPoint(Point[] list) {
        Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (Point p : list) {
            min.x = Math.min(min.x, p.x);
            min.y = Math.min(min.y, p.y);
        }
        return min;
    }

    public static IOPin getOutput(List<IOPin> pins) {
        for (IOPin pin : pins) {
            if (pin.mode == IOPin.MODE.OUTPUT) {
                return pin;
            }
        }
        return null;
    }

    /**
     * Return name of component
     *
     * @param obj WorkSpaceObject (only component)
     * @return name
     */
    public static String getComponentName(WorkSpaceObject obj) {
        if (obj instanceof Not) {
            //NOT
            return "NOT";
        } else if (obj instanceof Buffer) {
            //BUFFER
            return "BUFFER";
        } else if (obj instanceof And) {
            //AND
            return "AND";
        } else if (obj instanceof Nand) {
            //NAND
            return "NAND";
        } else if (obj instanceof Or) {
            //OR
            return "OR";
        } else if (obj instanceof Nor) {
            //NOR
            return "NOR";
        } else if (obj instanceof Xor) {
            //XOR
            return "XOR";
        } else if (obj instanceof Nxor) {
            //NXOR
            return "NXOR";
        } else if (obj instanceof Button) {
            //BUTTON
            return "BUTTON";
        } else if (obj instanceof Bulp) {
            //BULP
            return "BULP";
        } else if (obj instanceof BitGet) {
            //BIT GET
            return "BIT GET";
        } else if (obj instanceof BitSet) {
            //BIT SET
            return "BIT SET";
        } else if (obj instanceof Input) {
            //INPUT
            return "INPUT";
        } else if (obj instanceof Output) {
            //OUTPUT
            return "OUTPUT";
        } else if (obj instanceof LogicModule) {
            //LOGIC MODUL
            return ((LogicModule) obj).getModuleName();
        } else if (obj instanceof Bridge) {
            //BRIDGE
            return "BRIDGE";
        } else if (obj instanceof Clock) {
            //BRIDGE
            return "CLOCK";
        } else if (obj instanceof RasterScreen) {
            //RASTER SCREEN
            return "RASTER SCREEN";
        } else if (obj instanceof RandomGenerator) {
            //RANDOM GENERATOR
            return "RANDOM GENERATOR";
        } else if (obj instanceof VectorScreen) {
            //VECTOR SCREEN
            return "VECTOR SCREEN";
        } else if (obj instanceof ROMRAM) {
            //ROM RAM
            return "ROM RAM";
        } else if (obj instanceof Counter) {
            //ROM RAM
            return "COUNTER";
        } else if (obj instanceof RSFlipFlop) {
            //RS FLIP FLOP
            return "RS FLIP FLOP";
        } else if (obj instanceof JKFlipFlop) {
            //JK FLIP FLOP
            return "JK FLIP FLOP";
        } else if (obj instanceof DFlipFlop) {
            //D FLIP FLOP
            return "D FLIP FLOP";
        } else if (obj instanceof TFlipFlop) {
            //T FLIP FLOP
            return "T FLIP FLOP";
        } else if (obj instanceof MUX) {
            //MUX
            return "MUX";
        } else if (obj instanceof DMUX) {
            //DMUX
            return "DMUX";
        } else if (obj instanceof SevenSeg) {
            //7 SEG
            return "7 SEG";
        } else if (obj instanceof KeyBoard) {
            //KEYBOARD
            return "KEYBOARD";
        } else if (obj instanceof TextScreen) {
            //TEXT SCREEN
            return "TEXT SCREEN";
        }
        return null;
    }

    /**
     * Test if cursor intersect with some pin of object
     *
     * @param cursor Cursr position
     * @param obj Workspaceobject
     * @return
     */
    public static IOPin intersectIOPin(Point cursor, WorkSpaceObject obj) {
        if (cursor == null) {
            return null;
        }
        Point o = obj.getPosition();
        if (o == null) {
            return null;
        }
        if (obj.getPins() == null) {
            return null;
        }
        for (IOPin pin : obj.getPins()) {
            Point.Double p = pin.getPosition();
            if (Math.abs(Math.pow(o.x + p.x - cursor.x, 2)
                    + Math.pow(o.y + p.y - cursor.y, 2)) < 20) {
                return pin;
            }
        }
        return null;
    }

    /**
     * Test if cursor intersect with some poin of line path
     *
     * @param cursor Cursor
     * @param obj Wire
     * @return
     */
    public static Point2D.Double intersectWirePoints(Point cursor, WorkSpaceObject obj) {
        Point.Double p = null;
        if (obj instanceof Wire) {
            Wire w = (Wire) obj;
            List<Line> path = w.getPath();
            for (Line l : path) {
                if (Tools.dist(l.p1, cursor) < 8) {
                    return l.p1;
                }
                if (Tools.dist(l.p2, cursor) < 8) {
                    return l.p2;
                }
            }
        }
        return p;
    }

    /**
     * Convert double point to int point
     *
     * @param p Double point
     * @return
     */
    public static Point ptToInt(Point.Double p) {
        return new Point((int) p.x, (int) p.y);
    }

    /**
     * Convert int point to double point
     *
     * @param p int point
     * @return
     */
    public static Point.Double ptToDouble(Point p) {
        return new Point.Double(p.x, p.y);
    }

    /**
     * Compute distace between two points
     *
     * @param p1 Double point
     * @param p2 Int point
     * @return
     */
    public static double dist(Point.Double p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * Compute distace between two points
     *
     * @param p1 Double point
     * @param p2 Double point
     * @return
     */
    public static double dist(Point.Double p1, Point.Double p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * Add two points
     *
     * @param p1 Double point
     * @param p2 Double point
     * @return
     */
    public static Point.Double add(Point.Double p1, Point.Double p2) {
        return new Point.Double(p1.x + p2.x, p1.y + p2.y);
    }

    /**
     * Add two point
     *
     * @param p1 Double point
     * @param p2 Int point
     * @return
     */
    public static Point.Double add(Point.Double p1, Point p2) {
        return new Point.Double(p1.x + p2.x, p1.y + p2.y);
    }

    /**
     * If positions of two points are same then return true
     *
     * @param p1 Int point
     * @param p2 Int point
     * @return
     */
    public static boolean equal(Point p1, Point p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    /**
     * If positions of two points are same then return true
     *
     * @param p1 Doulbe point
     * @param p2 Doulbe point
     * @return
     */
    public static boolean equal(Point.Double p1, Point.Double p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    /**
     * Is point in some line of wire path ? delta x or y must be 0
     *
     * @param w Wire
     * @param point Point position
     * @param ignore this line will be ignored
     * @return
     */
    public static Line isOnLine(Wire w, Point.Double point, Line ignore) {
        double x = point.x;
        double y = point.y;
        for (Line l : w.getPath()) {
            if (ignore == l) {
                continue;
            }
            if (Math.abs(l.p1.x - l.p2.x) < 1d) {
                //x1 = x2 (tolerance 1 px)
                double start = Math.min(l.p1.y, l.p2.y) - LogicSimulatorCore.WORK_SPACE_STEP / 2;
                double end = Math.max(l.p1.y, l.p2.y) + LogicSimulatorCore.WORK_SPACE_STEP / 2;
                if (y >= start && y <= end && Math.abs(l.p1.x - x) < 2) {
                    return l;
                }
            } else {
                //y1 = y2
                double start = Math.min(l.p1.x, l.p2.x) - LogicSimulatorCore.WORK_SPACE_STEP / 2;
                double end = Math.max(l.p1.x, l.p2.x) + LogicSimulatorCore.WORK_SPACE_STEP / 2;
                if (x >= start && x <= end && Math.abs(l.p1.y - y) < 2) {
                    return l;
                }
            }
        }
        return null;
    }

    /**
     * Connect two wires and return final wire
     *
     * @param w1 Wire 1
     * @param w2 Wire 2
     * @return
     */
    public static Wire connectWires(Wire w1, Wire w2) {
        Wire w = new Wire();
        //path 1
        w1.getPath().stream().forEach((line) -> {
            w.getPath().add(line);
        });
        //path 2
        w2.getPath().stream().forEach((line) -> {
            w.getPath().add(line);
        });
        //pins 1
        w1.getPins().stream().forEach((pin) -> {
            w.getPins().add(pin);
            pin.setWire(w);
        });
        //pins 2
        w2.getPins().stream().forEach((pin) -> {
            w.getPins().add(pin);
            pin.setWire(w);
        });
        //selected 1
        w1.getSelectedLines().stream().forEach((line) -> {
            w.getSelectedLines().add(line);
        });
        //selected 2
        w2.getSelectedLines().stream().forEach((line) -> {
            w.getSelectedLines().add(line);
        });
        //value of wire (color)
        w.value = w1.value + w2.value;
        w.value = Math.max(w.value, -1);
        w.value = Math.min(w.value, 1);
        return w;
    }

    /**
     * Return number of points (that are in lines of wire path) that have same
     * position as point p
     *
     * @param wire Wire
     * @param p Point p
     * @return
     */
    public static int countWirePathPoints(Wire wire, Point.Double p) {
        int count = 0;
        for (Line line : wire.getPath()) {
            if (dist(line.p1, p) < 4) {
                count++;
            } else if (dist(line.p2, p) < 4) {
                count++;
            }
        }
        return count;
    }

    /**
     * Intersect of lines
     *
     * @param l1p1 Point 1 of line 1
     * @param l1p2 Point 2 of line 1
     * @param l2p1 Point 1 of line 2
     * @param l2p2 Point 2 of line 2
     * @return
     */
    public static boolean intervalIntersect(double l1p1, double l1p2, double l2p1, double l2p2) {
        //find min and max for
        double min1 = Math.min(l1p1, l1p2);
        double max1 = Math.max(l1p1, l1p2);
        double min2 = Math.min(l2p1, l2p2);
        double max2 = Math.max(l2p1, l2p2);

        return max1 >= min2 && min1 < max2;
    }

    /**
     * Test if (double) point p is on end of wire path point p must be in line
     *
     * @param lines Wire path
     * @param p Int point
     * @return
     */
    public static boolean endOfPath(List<Line> lines, Point.Double p) {
        /*
        if p is on position of some point of line from path then
        if this point is once time in this path then is end of line
         */
        int count = 0;
        for (Line line : lines) {
            //if point p is same as point in path then increment count (int) var
            //tolerance is sqrt(4) px
            if (Tools.dist(line.p1, p) < 4) {
                count++;
            } else if (Tools.dist(line.p2, p) < 4) {
                count++;
            }
        }
        //return result
        return count == 1;
    }

    /**
     * Test if all pin of same mode have same bit width
     *
     * @param pins Pins
     * @param mode Pin mode
     * @return
     */
    public static boolean sameBitWidth(List<IOPin> pins, IOPin.MODE mode) {
        int last = -1;
        for (IOPin pin : pins) {
            if (pin.mode == mode || mode == IOPin.MODE.IO) {
                if (last != -1) {
                    if (pin.getValue().length != last) {
                        return false;
                    }
                }
                last = pin.getValue().length;
            }
        }
        return true;
    }

    /**
     * Draw error sing
     *
     * @param g2 Graphics context
     * @param x X position of sign
     * @param y Y position of sign
     * @param height Height of sign
     */
    public static void drawError(Graphics2D g2, int x, int y, int height) {
        g2.setColor(Colors.ERROR);
        y += height / 2;
        //!
        g2.drawLine(
                x,
                y - height,
                x,
                (int) (y - height * 0.3f));
        g2.fillOval(x - 1, y - 1, 3, 3);
        //triangle
        g2.drawLine(
                (int) (x - height * 0.9),
                (int) (y + height * 0.3),
                (int) (x + height * 0.9),
                (int) (y + height * 0.3)
        );
        g2.drawLine(
                (int) (x - height * 0.9),
                (int) (y + height * 0.3),
                x,
                (int) (y - height * 1.6)
        );
        g2.drawLine(
                (int) (x + height * 0.9),
                (int) (y + height * 0.3),
                x,
                (int) (y - height * 1.6)
        );
    }

    /**
     * Rotate point around [0, 0]
     *
     * @param p Double point
     * @param angle Angle
     */
    public static void rotatePoint(Point.Double p, double angle) {
        if (angle == 0d) {
            return;
        }
        double x = p.x;
        double y = p.y;
        p.x = x * Math.cos(angle) - y * Math.sin(angle);
        p.y = x * Math.sin(angle) + y * Math.cos(angle);
    }

    /**
     * Rotate object pattern
     *
     * @param objects List with objects
     * @param angle
     */
    public static void rotate(List<WorkSpaceObject> objects, int angle) {

        /**
         * this list provide that every point of wire path change position only
         * once time
         */
        List<Point.Double> pointPathMap = new ArrayList<>();

        try {
            //find center
            Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
            Point max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
            objects.stream().forEach((obj) -> {
                int width = 0, height = 0;
                if (obj.getSize() != null) {
                    width = obj.getSize().width / 2;
                    height = obj.getSize().height / 2;
                }
                min.x = Math.min(min.x, obj.getPosition().x - width);
                min.y = Math.min(min.y, obj.getPosition().y - height);
                max.x = Math.max(max.x, obj.getPosition().x + width);
                max.y = Math.max(max.y, obj.getPosition().y + height);
            });
            Point.Double center = new Point.Double(
                    min.x + (max.x - min.x) / 2,
                    min.y + (max.y - min.y) / 2
            );

            //rotate with all objects
            double a = (double) angle * Math.PI / 2d;
            objects.stream().forEach((obj) -> {
                if (obj != null) {
                    if (obj instanceof Wire) {
                        if (((Wire) obj).getPath() != null) {
                            ((Wire) obj).getPath().forEach((line) -> {
                                for (Point.Double p : line.getPoints()) {
                                    //point musnt be in point map
                                    if (pointPathMap.stream().allMatch((pm) -> (pm != p))) {
                                        p.x -= center.x;
                                        p.y -= center.y;
                                        Tools.rotatePoint(p, a);
                                        p.x += center.x;
                                        p.y += center.y;
                                        //addd pt to map
                                        pointPathMap.add(p);
                                    }
                                }
                                //step
                                Tools.step(line.p1, LogicSimulatorCore.WORK_SPACE_STEP);
                                Tools.step(line.p2, LogicSimulatorCore.WORK_SPACE_STEP);
                            });
                        }
                    } else {
                        //rotate model
                        if (obj.getModel() != null) {
                            obj.getModel().rotate(angle);
                        }
                        //rotate posizion around center of patern rotation
                        double x = obj.getPosition().x - center.x;
                        double y = obj.getPosition().y - center.y;
                        obj.getPosition().x = (int) (x * Math.cos(a) - y * Math.sin(a) + center.x);
                        obj.getPosition().y = (int) (x * Math.sin(a) + y * Math.cos(a) + center.y);
                        Tools.step(obj.getPosition(), LogicSimulatorCore.WORK_SPACE_STEP);
                    }
                }
            });
        } catch (Exception ex) {
        }

    }

    /**
     * Step function -> point [x, y] will be in grid
     *
     * @param p Int point
     * @param step Gird step
     */
    public static void step(Point p, int step) {
        p.x = step(p.x, step);
        p.y = step(p.y, step);
    }

    /**
     * Step function -> point [x, y] will be in grid
     *
     * @param p Double point
     * @param step Gird step
     */
    public static void step(Point.Double p, int step) {
        p.x = step((int) p.x, step);
        p.y = step((int) p.y, step);
    }

    /**
     * Return new X value and that value will be divisible by number step
     *
     * @param x X value
     * @param step Step value
     * @return
     */
    public static int step(int x, int step) {
        int ret;
        int sign = (int) Math.signum(x);
        x = Math.abs(x);
        if (x % step < step / 2) {
            ret = x - x % step;
        } else {
            ret = x + (step - x % step);
        }
        return sign * ret;
    }

    public static List<WorkSpaceObject> getSelected(List<WorkSpaceObject> objects) {
        List<WorkSpaceObject> ret = new ArrayList<>();
        objects.stream()
                .filter((obj) -> !(obj == null))
                .filter((obj) -> !(!obj.isSelected()))
                .forEachOrdered((obj) -> {
                    ret.add(obj);
                });
        return ret;
    }

    /**
     * Check if cursor clik on some object from list, if click the return true
     *
     * @param objects List with objects
     * @param cursor Cursor position
     * @return
     */
    public static boolean intersectObject(List<WorkSpaceObject> objects, Point cursor) {
        for (WorkSpaceObject obj : objects) {
            if (obj == null) {
                continue;
            }
            if (obj.isSelected()) {
                continue;
            }
            if (obj.select(cursor)) {
                obj.unSelect();
                return true;
            }
        }
        return false;
    }

    /**
     * Remove all projects from core
     *
     * @param core LS core
     */
    public static void removeProject(LogicSimulatorCore core) {
        core.getLSComponents().stream()
                .filter((comp) -> (comp instanceof Project))
                .forEachOrdered((comp) -> {
                    core.getLSComponents().remove(comp);
                });
    }

    /**
     * Get name of file without file type
     *
     * @param file Name of file -> "name.type"
     * @return
     */
    public static String fileName(String file) {
        if (file == null) {
            return null;
        }
        //get position of last '.'
        int pos = file.lastIndexOf(".");
        // If there wasn't any '.' just return the string as is
        if (pos == -1) {
            return file;
        }
        //otherwise return the string, up to the dot
        return file.substring(0, pos);
    }

    /**
     * Return file type
     *
     * @param file File name
     * @return
     */
    public static String fileType(String file) {
        if (file == null) {
            return null;
        }
        //get position of last '.'
        int pos = file.lastIndexOf(".");
        // If there wasn't any '.' just return the string as is
        if (pos == -1) {
            return file;
        }
        return file.substring(pos + 1, file.length());
    }

    /**
     * Get project file type
     *
     * @param pf Project file
     * @return
     */
    public static String getFileType(ProjectFile pf) {
        if (pf instanceof WorkSpace) {
            return LogicSimulatorCore.WORKSPACE_FILE_TYPE;
        } else if (pf instanceof ModuleEditor) {
            return LogicSimulatorCore.MODULE_FILE_TYPE;
        }
        return "";
    }

    /**
     * Divide x and y of point
     *
     * @param p Point (int)
     * @param divider Divider (float)
     * @return
     */
    public static Point divide(Point p, float divider) {
        return new Point((int) (p.x / divider), (int) (p.y / divider));
    }

    /**
     * Find max value
     *
     * @param list int[] list
     * @return Max val
     */
    public static int max(int[] list) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < list.length; i++) {
            max = Math.max(list[i], max);
        }
        return max;
    }

    /**
     * Find max value
     *
     * @param list int[] list
     * @return Max val
     */
    public static double max(double[] list) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < list.length; i++) {
            max = Math.max(list[i], max);
        }
        return max;
    }

    /**
     * Find max value
     *
     * @param list int[] list
     * @return Max val
     */
    public static double min(double[] list) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < list.length; i++) {
            min = Math.min(list[i], min);
        }
        return min;
    }

    /**
     * Test if point p is in bounds
     *
     * @param p1 Corner 1
     * @param p2 Corner 2
     * @param p Point
     * @return
     */
    public static boolean isInBounds(Point.Double p1, Point.Double p2, Point.Double p) {
        //min -> left up corner of bound rect max -> right down corner of bound rect
        Point.Double max = new Point.Double(Double.MIN_VALUE, Double.MIN_VALUE);
        Point.Double min = new Point.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        //max
        max.x = Math.max(max.x, p1.x);
        max.x = Math.max(max.x, p2.x);
        max.y = Math.max(max.y, p1.y);
        max.y = Math.max(max.y, p2.y);
        //min
        min.x = Math.min(min.x, p1.x);
        min.x = Math.min(min.x, p2.x);
        min.y = Math.min(min.y, p1.y);
        min.y = Math.min(min.y, p2.y);
        //is in bounds
        return p.x >= min.x && p.y >= min.y && p.x <= max.x && p.y <= max.y;
    }

    /**
     * Test if point p is in bounds
     *
     * @param min Must be up left corner of select rect
     * @param max Must be down right corner of select rect
     * @param p Point
     * @return
     */
    public static boolean isInBounds(Point min, Point max, Point p) {
        //is in bounds
        return p.x >= min.x && p.y >= min.y && p.x <= max.x && p.y <= max.y;
    }

    /**
     * Compute y offset to center string in y axis
     *
     * @param m FontMetrics
     * @return y off (y + yoff -> string will be centered on y)
     */
    public static int centerYString(FontMetrics m) {
        return -m.getHeight() / 2 + m.getAscent();
    }

    /**
     * Set high quality of rendering for graphics2D "g2"
     *
     * @param g2 Graphics2D
     */
    public static void setHighQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
    }

    /**
     * Create image of workspace object
     *
     * @param obj WorkSpaceObject
     * @param size size of image
     * @param angle Rotation
     * @return
     */
    public static BufferedImage createImage(WorkSpaceObject obj, int size, double angle) {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.createGraphics();
        Tools.setHighQuality(g2);
        float scale = (float) size / (float) Math.max(
                obj.getSize().width,
                obj.getSize().height
        );
        g2.scale(scale, scale);
        obj.getPosition().x = (int) (size / 2f / scale);
        obj.getPosition().y = (int) (size / 2f / scale);
        g2.rotate(Math.PI / 4, size / 2f / (float) scale, size / 2f / (float) scale);
        obj.render(
                g2,
                new Point(0, 0),
                new Dimension(size, size)
        );
        return img;
    }

    /**
     * Get all points of model
     *
     * @param model Model
     * @return
     */
    public static List<Point.Double> getPoints(Model model) {
        List<Point.Double> pts = new ArrayList<>();
        //for graphics objects
        if (model.getGraphicsObjects() != null) {
            model.getGraphicsObjects().stream()
                    .filter((go) -> !(go == null))
                    .forEachOrdered((go) -> {
                        pts.addAll(Arrays.asList(go.getPoints()));
                    });
        }
        //for io pins
        if (model.getIOPins() != null) {
            model.getIOPins().stream().forEach((pin) -> {
                pts.addAll(Arrays.asList(pin.getPosition()));
            });
        }
        return pts;
    }

    public static void generateJavaModel(Model model) {
        String out = "this.model = new Model(\nnew GraphicsObject[]{";
        for (GraphicsObject go : model.getGraphicsObjects()) {
            if (go instanceof Line) {
                Line l = (Line) go;
                out += "\n   new Line(new Point.Double(" + l.p1.x + ", " + l.p1.y + "), new Point.Double(" + l.p2.x + ", " + l.p2.y + ")),";
            } else if (go instanceof Circle) {
                Circle c = (Circle) go;
                out += "\n   new Circle(new Point.Double(" + c.p1.x + ", " + c.p1.y + "), " + c.radius + "),";
            } else if (go instanceof Curve) {
                Curve c = (Curve) go;
                out += "\n   new Curve(new Point.Double(" + c.p1.x + ", " + c.p1.y + "), new Point.Double(" + c.control.x + ", " + c.control.y + "), new Point.Double(" + c.p2.x + ", " + c.p2.y + ")),";
            }
        }
        out += "\n}\n);\n\n";
        //io pin
        for (IOPin pin : model.getIOPins()) {
            String mx = pin.mode == IOPin.MODE.INPUT ? "INPUT" : "OUTPUT";
            out += "this.model.getIOPins().add(new IOPin(IOPin.MODE." + mx + ", bits, \"\", new Point.Double(" + pin.getPosition().x + ", " + pin.getPosition().y + ")));\n";
        }
        JOptionPane.showMessageDialog(null, new JTextArea(out));
    }

    public static String randomNumber(int lenght) {
        String ret = "";
        for (int i = 0; i < lenght; i++) {
            ret += (int) (Math.random() * 10);
        }
        return ret;
    }

    /**
     * Get 8 bit color
     *
     * @param val Value
     * @return Color
     */
    public static Color get8BitColor(int val) {
        return new Color(
                ((val >> 5) & 0x7) * 36,
                ((val >> 2) & 0x7) * 36,
                (val & 0x3) * 85
        );
    }

    /**
     * Get component with equeal name as arg: "name"
     *
     * @param components List with components
     * @param name Name of searched component
     * @return
     */
    public static Component getComponent(Component[] components, String name) {
        for (Component c : components) {
            if (c.getName() != null) {
                if (c.getName().equals(name)) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Clone all objects of array
     * @param objects Array with objects
     * @return 
     */
    public static List<WorkSpaceObject> cloneWObjects(List<WorkSpaceObject> objects) {
        List<WorkSpaceObject> ret = new ArrayList<>();
        objects.stream().forEach((obj) -> {
            ret.add(obj.cloneObject());
        });
        return ret;
    }

}
