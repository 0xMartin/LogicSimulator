/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.Point;
import java.util.List;
import logicSimulator.graphics.Line;
import logicSimulator.objects.IOPin;
import logicSimulator.objects.wiring.Bridge;
import logicSimulator.objects.wiring.Wire;

/**
 * Conecting wires with components, deleting useless wires, connect wires using
 * bridges, simplifying wires and next necessarily operations with circuit
 *
 * @author Martin
 */
public class CircuitHandler {

    /**
     * Connect all gates with wires, all wires with wires, briges with bridges,
     * separet splited path of wires, delete useless point of all wire paths,
     * drop all point-point references in wire pathes
     *
     * @param objects Workspace object list
     */
    public static void refreshConnectivity(List<WorkSpaceObject> objects) {

        //unconnect all pins
        deleteAllPinConnections(objects);

        //splite wire path on wires with one line of this path
        splitWireOnSingleLineWires(objects);

        //connect wires with wires
        Object1For:
        for (int i = 0; i < objects.size(); i++) {
            WorkSpaceObject obj1 = objects.get(i);
            if (obj1 instanceof Wire) {
                for (WorkSpaceObject obj2 : objects) {
                    if (obj1 == obj2) {
                        continue;
                    }
                    if (obj2 instanceof Wire) {
                        for (Line l : ((Wire) obj2).getPath()) {
                            if (Tools.isOnLine((Wire) obj1, l.p1, null) != null) {
                                objects.remove(obj1);
                                objects.remove(obj2);
                                objects.add(Tools.connectWires((Wire) obj1, (Wire) obj2));
                                i = - 1;
                                continue Object1For;
                            } else if (Tools.isOnLine((Wire) obj1, l.p2, null) != null) {
                                objects.remove(obj1);
                                objects.remove(obj2);
                                objects.add(Tools.connectWires((Wire) obj1, (Wire) obj2));
                                i = - 1;
                                continue Object1For;
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
                            objects.stream()
                                    .filter((obj2) -> !(obj == obj2))
                                    .filter((obj2) -> (obj2 instanceof Wire))
                                    .forEachOrdered((obj2) -> {
                                        Point.Double p = new Point.Double(
                                                obj.getPosition().x + pin.getPosition().x,
                                                obj.getPosition().y + pin.getPosition().y
                                        );
                                        if (Tools.isOnLine((Wire) obj2, p, null) != null) {
                                            //add pin to wire pin list because interset with
                                            pin.setWire((Wire) obj2);
                                            obj2.getPins().add(pin);
                                        }
                                    });
                        });
                    }
                }
            }
        });

        //connect all bridges with same id (connect theirs wires)
        connectBridges(objects);

        //simplifi wire
        simplifiWires(objects);

        //group all lines of all wire pathes
        groupWirePathLines(objects);

        //set nodes visibility for all points of lines
        objects.stream()
                .filter((obj) -> (obj instanceof Wire))
                .forEachOrdered((wire) -> {
                    ((Wire) wire).getPath().stream().forEach((line) -> {
                        //set node vibility for node 1 and 2 of line
                        //node 1
                        if (Tools.endOfPath(((Wire) wire).getPath(), line.p1)) {
                            line.n1 = Tools.isOnLine((Wire) wire, line.p1, line) != null;
                        } else {
                            line.n1 = false;
                        }
                        //node 2
                        if (Tools.endOfPath(((Wire) wire).getPath(), line.p2)) {
                            line.n2 = Tools.isOnLine((Wire) wire, line.p2, line) != null;
                        } else {
                            line.n2 = false;
                        }
                    });
                });

        /**
         * all wires with HIGH(1) value or if they are BUS(-1) and no connected
         * output pins go to the LOW
         */
        objects.stream()
                .filter((obj) -> (obj instanceof Wire))
                .forEachOrdered((wire) -> {
                    //in high
                    if (((Wire) wire).value == 1 || ((Wire) wire).value == -1) {
                        boolean outExist = false;
                        for (IOPin pin : ((Wire) wire).getPins()) {
                            if (pin.mode == IOPin.MODE.OUTPUT) {
                                outExist = true;
                                break;
                            }
                        }
                        //set low is output pin not connected to the wire
                        if (!outExist) {
                            //set value of wire
                            ((Wire) wire).value = 0;
                            //write low on all connected input pins
                            for (IOPin pin : ((Wire) wire).getPins()) {
                                if (pin.mode == IOPin.MODE.INPUT) {
                                    pin.setValue(false);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Splite wire path on wires with one streight line of this path
     *
     * @param objects List with objects
     */
    public static void splitWireOnSingleLineWires(List<WorkSpaceObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            //is wire ?
            if (objects.get(i) instanceof Wire) {
                Wire w = (Wire) objects.get(i);
                //if wire path is from more then one line
                if (w.getPath().size() > 1) {
                    //add new wires for each line of path
                    w.getPath().stream().forEach((line) -> {
                        Wire subWire = new Wire();
                        //copy line selection
                        Line lCopy = line.cloneObject();
                        if (w.getSelectedLines().stream().anyMatch((o1) -> (o1 == line))) {
                            subWire.getSelectedLines().add(lCopy);
                        }
                        //add one line of wire path to one line wire
                        subWire.value = w.value;
                        subWire.getPath().add(lCopy);
                        objects.add(subWire);
                    });
                    //remove wire
                    objects.remove(i);
                    i -= 1;
                }
            }
        }
    }

    /**
     * Group all lines in wire => if point from line 1 and point from line 2 are
     * on same position (+-4px) then point from line 1 will be referenc on point
     * from line 2
     *
     * @param objects list with objects
     */
    public static void groupWirePathLines(List<WorkSpaceObject> objects) {
        objects.stream()
                .filter((obj1) -> (obj1 instanceof Wire))
                .forEachOrdered((wire) -> {
                    //find intersect
                    objects.stream().forEach((obj) -> {
                        ((Wire) wire).getPath().stream().forEach((line1) -> {
                            //wire
                            if (obj instanceof Wire) {
                                ((Wire) obj).getPath().stream().forEach((line2) -> {
                                    if (Tools.dist(line1.p1, line2.p1) < 4) {
                                        line1.p1 = line2.p1;
                                    } else if (Tools.dist(line1.p1, line2.p2) < 4) {
                                        line1.p1 = line2.p2;
                                    }
                                    if (Tools.dist(line1.p2, line2.p1) < 4) {
                                        line1.p2 = line2.p1;
                                    } else if (Tools.dist(line1.p2, line2.p2) < 4) {
                                        line1.p2 = line2.p2;
                                    }
                                });
                            }
                        });
                    });
                });
    }

    /**
     * Simplifi all wires => if one long streigh line is created from many short
     * lines than of all make on long line
     *
     * @param objects list with objects
     */
    public static void simplifiWires(List<WorkSpaceObject> objects) {
        objects.stream()
                .filter((obj) -> (obj instanceof Wire))
                .forEachOrdered((wire) -> {
                    //line 1
                    Line1For:
                    for (int i = 0; i < ((Wire) wire).getPath().size(); i++) {
                        Line l1 = ((Wire) wire).getPath().get(i);
                        boolean vertical1 = l1.p1.x == l1.p2.x;
                        //line 2
                        for (Line l2 : ((Wire) wire).getPath()) {
                            if (l1 == l2) {
                                continue;
                            }
                            boolean vertical2 = l2.p1.x == l2.p2.x;
                            //if line make straight combination
                            if (vertical1 == vertical2) {
                                boolean added = false;
                                if (vertical1) {
                                    if (l1.p1.x == l2.p1.x) {
                                        if (Tools.intervalIntersect(l1.p1.y, l1.p2.y, l2.p1.y, l2.p2.y)) {
                                            //find start and end point of line
                                            Point.Double p1 = new Point.Double(l1.p1.x, 0);
                                            Point.Double p2 = new Point.Double(l1.p1.x, 0);
                                            p1.y = Tools.min(new double[]{l1.p1.y, l1.p2.y, l2.p1.y, l2.p2.y});
                                            p2.y = Tools.max(new double[]{l1.p1.y, l1.p2.y, l2.p1.y, l2.p2.y});
                                            //remplace by longer line
                                            ((Wire) wire).getPath().add(new Line(p1, p2));
                                            added = true;
                                        }
                                    }
                                } else {
                                    if (l1.p1.y == l2.p1.y) {
                                        if (Tools.intervalIntersect(l1.p1.x, l1.p2.x, l2.p1.x, l2.p2.x)) {
                                            //find start and end point of line
                                            Point.Double p1 = new Point.Double(0, l1.p1.y);
                                            Point.Double p2 = new Point.Double(0, l1.p1.y);
                                            p1.x = Tools.min(new double[]{l1.p1.x, l1.p2.x, l2.p1.x, l2.p2.x});
                                            p2.x = Tools.max(new double[]{l1.p1.x, l1.p2.x, l2.p1.x, l2.p2.x});
                                            //remplace by longer line
                                            ((Wire) wire).getPath().add(new Line(p1, p2));
                                            added = true;
                                        }
                                    }
                                }
                                //if linew was added then remove both lines and continue from begin of path
                                if (added) {
                                    ((Wire) wire).getPath().remove(l1);
                                    ((Wire) wire).getPath().remove(l2);
                                    i = -1;
                                    continue Line1For;
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Delete all pin connection with wires
     *
     * @param objects list with objects
     */
    public static void deleteAllPinConnections(List<WorkSpaceObject> objects) {
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
    }

    /**
     * Connect all bridges with same id from object list (connecte wires with
     * then are connected)
     *
     * @param objects list with objects
     */
    public static void connectBridges(List<WorkSpaceObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) instanceof Bridge) {
                Bridge b1 = (Bridge) objects.get(i);
                //for bridge b1 find all briges with same id and connect them
                boolean connected = false;
                for (int j = 0; j < objects.size(); j++) {
                    if (objects.get(j) instanceof Bridge) {
                        Bridge b2 = (Bridge) objects.get(j);
                        if (b1.getID().equals(b2.getID())) {
                            //id of b1 and b2 are same
                            IOPin p1 = b1.getPins().get(0);
                            IOPin p2 = b2.getPins().get(0);
                            //if one of connected wire is null then continue
                            if (p1.getWire() == null || p2.getWire() == null) {
                                continue;
                            }
                            //wire of both bridges must be different
                            if (p1.getWire() == p2.getWire()) {
                                continue;
                            }
                            objects.remove(p1.getWire());
                            objects.remove(p2.getWire());
                            //connect wires
                            Wire wJoin = Tools.connectWires(p1.getWire(), p2.getWire());
                            objects.add(wJoin);
                            connected = true;
                            //next loop must go from 0 index because some object are added and removed
                            j = -1;
                        }
                    }
                }
                if (connected) {
                    i -= 1;
                }
            }
        }
    }
}
