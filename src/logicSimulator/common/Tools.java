/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import logicSimulator.ui.Colors;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import logicSimulator.LSComponent;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Project;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.wiring.BitGet;
import logicSimulator.objects.control.Button;
import logicSimulator.objects.displays.Bulp;
import logicSimulator.objects.gates.And;
import logicSimulator.objects.gates.Buffer;
import logicSimulator.objects.gates.Nand;
import logicSimulator.objects.gates.Nor;
import logicSimulator.objects.gates.Not;
import logicSimulator.objects.gates.Nxor;
import logicSimulator.objects.gates.Or;
import logicSimulator.objects.gates.Xor;
import logicSimulator.objects.wiring.BitSet;
import logicSimulator.objects.wiring.Input;
import logicSimulator.objects.wiring.Wire;

/**
 *
 * @author Martin
 */
public class Tools {

    /**
     * Is object in list ?
     *
     * @param list List with objects
     * @param obj Object
     * @return
     */
    public static boolean isInLfist(List<Object> list, Object obj) {
        if (list == null || obj == null) {
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }
        return list.stream().anyMatch((element) -> (element == obj));
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

    /**
     * Clone WorkSpaceObject
     *
     * @param obj Object ot clone
     * @return
     * @throws CloneNotSupportedException
     */
    public static WorkSpaceObject clone(WorkSpaceObject obj) throws CloneNotSupportedException {
        if (obj instanceof Not) {
            //NOT
            return ((Not) obj).cloneObject();
        } else if (obj instanceof Buffer) {
            //BUFFER
            return ((Buffer) obj).cloneObject();
        } else if (obj instanceof And) {
            //AND
            return ((And) obj).cloneObject();
        } else if (obj instanceof Nand) {
            //NAND
            return ((Nand) obj).cloneObject();
        } else if (obj instanceof Or) {
            //OR
            return ((Or) obj).cloneObject();
        } else if (obj instanceof Nor) {
            //NOR
            return ((Nor) obj).cloneObject();
        } else if (obj instanceof Xor) {
            //XOR
            return ((Xor) obj).cloneObject();
        } else if (obj instanceof Nxor) {
            //NXOR
            return ((Nxor) obj).cloneObject();
        } else if (obj instanceof Wire) {
            //WIRE
            return ((Wire) obj).cloneObject();
        } else if (obj instanceof Button) {
            //BUTTON
            return ((Button) obj).cloneObject();
        } else if (obj instanceof Bulp) {
            //BULP
            return ((Bulp) obj).cloneObject();
        } else if (obj instanceof BitGet) {
            //BIT GET
            return ((BitGet) obj).cloneObject();
        } else if (obj instanceof BitSet) {
            //BIT SET
            return ((BitSet) obj).cloneObject();
        }else if (obj instanceof Input) {
            //INPUT
            return ((Input) obj).cloneObject();
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
        for (IOPin pin : obj.getPins()) {
            Point.Double p = pin.getPosition();
            if (Math.abs(Math.pow(o.x + p.x - cursor.x, 2)
                    + Math.pow(o.y + p.y - cursor.y, 2)) < 25) {
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
                if (Tools.dist(l.p1, cursor) < 5) {
                    return l.p1;
                }
                if (Tools.dist(l.p2, cursor) < 5) {
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
     * @param cursor Cursor position
     * @return
     */
    public static boolean isOnLine(WorkSpaceObject w, Point cursor) {
        if (w instanceof Wire) {
            int x = cursor.x;
            int y = cursor.y;
            for (Line l : ((Wire) w).getPath()) {
                double start, end;
                if (l.p1.x - l.p2.x == 0) {
                    start = Math.min(l.p1.y, l.p2.y) - LogicSimulatorCore.WORK_SPACE_STEP / 2;
                    end = Math.max(l.p1.y, l.p2.y) + LogicSimulatorCore.WORK_SPACE_STEP / 2;
                    if (y >= start && y <= end && Math.abs(l.p1.x - x) < 10) {
                        return true;
                    }
                } else {
                    start = Math.min(l.p1.x, l.p2.x) - LogicSimulatorCore.WORK_SPACE_STEP / 2;
                    end = Math.max(l.p1.x, l.p2.x) + LogicSimulatorCore.WORK_SPACE_STEP / 2;
                    if (x >= start && x <= end && Math.abs(l.p1.y - y) < 10) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        });
        //pins 2
        w2.getPins().stream().forEach((pin) -> {
            w.getPins().add(pin);
        });
        return w;
    }

    /**
     * Connect all gates with wires and all wires with wires
     *
     * @param objects Workspace object list
     */
    public static void connectAllObject(List<WorkSpaceObject> objects) {

        //unconnect all pins
        objects.stream().forEach((WorkSpaceObject obj) -> {
            if (obj instanceof Wire) {
                obj.getPins().clear();
            } else {
                if (obj != null) {
                    if (obj.getPins() != null) {
                        obj.getPins().stream().forEach((pin) -> {
                            pin.setWire(null);
                        });
                    }
                }
            }
        });

        //connect wires with wires
        L:
        for (int i = 0; i < objects.size(); i++) {
            WorkSpaceObject obj1 = objects.get(i);
            if (obj1 instanceof Wire) {
                for (WorkSpaceObject obj2 : objects) {
                    if (obj1 == obj2) {
                        continue;
                    }
                    if (obj2 instanceof Wire) {
                        for (Line l : ((Wire) obj2).getPath()) {
                            if (Tools.isOnLine(obj1, Tools.ptToInt(l.p1))) {
                                objects.remove(obj1);
                                objects.remove(obj2);
                                objects.add(Tools.connectWires((Wire) obj1, (Wire) obj2));
                                i = - 1;
                                continue L;
                            } else if (Tools.isOnLine(obj1, Tools.ptToInt(l.p2))) {
                                objects.remove(obj1);
                                objects.remove(obj2);
                                objects.add(Tools.connectWires((Wire) obj1, (Wire) obj2));
                                i = - 1;
                                continue L;
                            }
                        }
                    }
                }
            }
        }

        //connect pins with wires
        objects.stream().forEach(obj -> {
            if (obj != null) {
                if (!(obj instanceof Wire)) {
                    if (obj.getPins() != null) {
                        obj.getPins().stream().forEach((pin) -> {
                            for (WorkSpaceObject obj2 : objects) {
                                if (obj == obj2) {
                                    continue;
                                }
                                if (obj2 instanceof Wire) {
                                    Point p = new Point(
                                            (int) (obj.getPosition().x + pin.getPosition().x),
                                            (int) (obj.getPosition().y + pin.getPosition().y)
                                    );
                                    if (Tools.isOnLine(obj2, p)) {
                                        //add pin to wire pin list because interset with 
                                        pin.setWire((Wire) obj2);
                                        obj2.getPins().add(pin);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Test if (double) point p is on end of wire path point p must be in line
     *
     * @param lines Wire path
     * @param p Int point
     * @return
     */
    public static boolean endOfWire(List<Line> lines, Point.Double p) {
        /*
        if p is on position of some point of line from path then
        if this point is once time in this path then is end of line
         */
        int count = 0;
        for (Line line : lines) {
            //if point p is same as point in path then increment count (int) var
            //toleranc is sqrt(4) px
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
     * Convert boolean[] to string
     *
     * @param bin boolean[]
     * @return
     */
    public static String binToString(boolean[] bin) {
        String ret = "";
        for (boolean b : bin) {
            ret += b ? '1' : '0' + " ";
        }
        return ret;
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
     * Get io pin from list be id
     *
     * @param pins List with io pins
     * @param id Id of pin we are looking for
     * @return
     */
    public static IOPin getIOPin(List<IOPin> pins, String id) {
        for (IOPin pin : pins) {
            if (pin.ID.equals(id)) {
                return pin;
            }
        }
        return null;
    }

    /**
     * Rotate point around [0, 0]
     *
     * @param p Double point
     * @param angle Angle
     */
    public static void rotatePoint(Point.Double p, double angle) {
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
        try {
            //find center
            Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
            Point max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
            objects.stream().forEach((obj) -> {
                int width = 0, height = 0;
                if (obj.getModel() != null) {
                    width = obj.getModel().getWidth() / 2;
                    height = obj.getModel().getHeight() / 2;
                }
                min.x = Math.min(min.x, obj.getPosition().x - width);
                min.y = Math.min(min.y, obj.getPosition().y - height);
                max.x = Math.max(max.x, obj.getPosition().x + width);
                max.y = Math.max(max.y, obj.getPosition().y + height);
            });
            Point center = new Point(
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
                                //point 1
                                line.p1.x -= center.x;
                                line.p1.y -= center.y;
                                Tools.rotatePoint(line.p1, a);
                                line.p1.x += center.x;
                                line.p1.y += center.y;
                                //point 2
                                line.p2.x -= center.x;
                                line.p2.y -= center.y;
                                Tools.rotatePoint(line.p2, a);
                                line.p2.x += center.x;
                                line.p2.y += center.y;
                                //step
                                Tools.step(line.p1, LogicSimulatorCore.WORK_SPACE_STEP);
                                Tools.step(line.p2, LogicSimulatorCore.WORK_SPACE_STEP);
                            });
                        }
                    } else if (obj.getModel() != null) {
                        obj.getModel().rotate(angle);
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
        for (WorkSpaceObject obj : objects) {
            if (obj == null) {
                continue;
            }
            if (!obj.isSelected()) {
                continue;
            }
            ret.add(obj);
        }
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
        for (LSComponent comp : core.getLSComponents()) {
            if (comp instanceof Project) {
                core.getLSComponents().remove(comp);
            }
        }
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

}
