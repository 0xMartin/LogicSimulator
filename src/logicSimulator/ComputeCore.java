/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import data.PropertieReader;
import java.awt.Point;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.objects.wiring.Bridge;
import logicSimulator.objects.wiring.Wire;
import logicSimulator.timing.ConstantTimeLoop;
import window.components.Graph;

/**
 * Compute core Support logic circuit simulating, step by step simuating,
 * dynamic changing of timinig
 *
 * @author Martin
 */
public class ComputeCore implements LSComponent {

    //rendering thread
    private Thread thread;

    private ConstantTimeLoop ctl;

    public boolean RUN = false;

    private Project project;

    private int CURRENT_TICKS = 0;
    private int CURRENT_UPDATES = 0, LAST_UPDATES = 0;

    public void openProject(Project prj) {
        this.project = prj;
    }

    @Override
    public void init(LogicSimulatorCore core, PropertieReader propt) throws Exception {
        //default
        this.ctl = new ConstantTimeLoop(50);

        //load project
        for (LSComponent obj : core.getLSComponents()) {
            if (obj instanceof Project) {
                this.project = (Project) obj;
                break;
            }
        }
        if (this.project == null) {
            throw new Exception("ComputeCore: Project not exist");
        }

        //thread
        this.thread = new Thread(() -> {
            update();
        });
        this.thread.setName("ComputeCore");
    }

    @Override
    public void run() {
        this.thread.start();
        this.ctl.run();
    }

    /**
     * Set update per second
     *
     * @param value
     */
    public void setUPS(int value) {
        this.ctl.setTicksPerSecond(value);
        this.ctl.reset();
    }

    /**
     * Set USP label
     */
    private JLabel upsLabel;
    private Graph upsGraph;

    public void setUPSDisplay(JLabel lab, Graph g) {
        this.upsLabel = lab;
        this.upsGraph = g;
    }

    /**
     * Stop computing
     */
    public void stopComputing() {
        this.ctl.stop();
    }

    /**
     * start computig
     */
    public void startComputing() {
        this.ctl.run();
    }

    /**
     * Compute one step, computing must be stoped
     */
    private boolean step = false;

    public void step() {
        this.step = true;
    }

    /**
     * Diagnostic
     */
    private long last = System.nanoTime();

    private void diagnostic() {
        this.CURRENT_TICKS++;
        if (System.nanoTime() - last >= 1e9) {
            this.last = System.nanoTime();
            //graph
            if (this.upsGraph != null) {
                this.upsGraph.POINTS.add(new Point.Double(this.upsGraph.getWidth(), this.CURRENT_UPDATES));
                this.upsGraph.move(-this.upsGraph.getWidth() / 50d, 0d);
                this.upsGraph.repaint();
            }
            //label
            if (this.upsLabel != null) {
                this.upsLabel.setText("Timing: " + this.CURRENT_TICKS + " ups");
            }
            this.CURRENT_TICKS = 0;
            this.CURRENT_UPDATES = 0;
        }
    }

    /**
     * Update all object in workspace Every gate / object make delay ->
     * posibility to make endge detection, delay ...
     */
    private void update() {
        while (true) {
            try {

                if (this.ctl.isStoped() || !this.RUN) {
                    Thread.sleep(100);
                }

                if (!this.RUN) {
                    continue;
                }

                if (this.ctl.tickCheck() || this.step) {
                    //diagnostic
                    diagnostic();
                    this.step = false;

                    //selected file of project must be instance of workspace
                    if (!(this.project.getSelectedFile() instanceof WorkSpace)) {
                        continue;
                    }

                    //get selected workspace
                    WorkSpace work = (WorkSpace) this.project.getSelectedFile();
                    if (work.getObjects() == null) {
                        continue;
                    }

                    //compute
                    this.CURRENT_UPDATES += ComputeCore.compute(work.getObjects());

                    //repaint selected workspace if something change
                    if (this.CURRENT_UPDATES != this.LAST_UPDATES) {
                        work.getHandler().repaint();
                    }
                    this.LAST_UPDATES = this.CURRENT_UPDATES;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ComputeCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * First compute logic function of all gates / objects and then in next step
     * all output pins write theirs values on wires
     *
     * @param objects WorkSpace objects
     * @return Number of object that changes thers stats
     */
    public static int compute(List<WorkSpaceObject> objects) {
        int current_updates = 0;
        //compute all objects
        for (int index = 0; index < objects.size(); index++) {
            WorkSpaceObject obj = objects.get(index);

            if (obj == null) {
                continue;
            }
            if (obj instanceof Wire) {
                if (obj.compute()) {
                    current_updates++;
                }
                continue;
            }
            if (obj.getModel() == null) {
                continue;
            }

            //test if all iopins have same bit width
            if (obj.error()) {
                obj.getModel().errorTag(true);
            } else {
                obj.getModel().errorTag(false);
                //compute object and 
                boolean changed = obj.compute();
                current_updates += changed ? 1 : 0;
            }

        }
        //output pins write their values of wires
        for (int index = 0; index < objects.size(); index++) {
            WorkSpaceObject obj = objects.get(index);
            //write
            if (obj != null) {
                if (!(obj instanceof Wire)) {
                    if (obj.getModel() != null) {
                        if (obj.getModel().getIOPins() != null) {
                            obj.getModel().getIOPins().forEach((pin) -> {
                                pin.writeValue();
                            });
                        }
                    }
                }
            }
        }
        return current_updates;
    }

    /**
     * Conecting wires with components, deleting useless wires, connect wires
     * using bridges, simplifying wires and next necessarily operations with
     * circuit
     */
    public static class CircuitHandler {

        /**
         * Connect all gates with wires, all wires with wires, briges with
         * bridges, separet splited path of wires, delete useless point of all
         * wire paths, drop all point-point references in wire pathes
         *
         * @param objects Workspace object list
         */
        public static void refreshConnectivity(List<WorkSpaceObject> objects) {

            //unconnect all pins
            deleteAllPinConnections(objects);

            //splite wire path on wires with one line of this path
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i) instanceof Wire) {
                    Wire w = (Wire) objects.get(i);
                    if (w.getPath().size() > 1) {
                        //add new wires for each line of path
                        w.getPath().stream().forEach((line) -> {
                            Wire subWire = new Wire();
                            if (w.getSelectedLines().stream().anyMatch((o1) -> (o1 == line))) {
                                subWire.getSelectedLines().add(line);
                            }
                            subWire.value = w.value;
                            subWire.getPath().add(line);
                            objects.add(subWire);
                        });
                        //remove wire
                        objects.remove(i);
                        i -= 1;
                    }
                }
            }

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
                                if (Tools.isOnLine(obj1, Tools.ptToInt(l.p1)) != null) {
                                    objects.remove(obj1);
                                    objects.remove(obj2);
                                    objects.add(Tools.connectWires((Wire) obj1, (Wire) obj2));
                                    i = - 1;
                                    continue Object1For;
                                } else if (Tools.isOnLine(obj1, Tools.ptToInt(l.p2)) != null) {
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
                                            Point p = new Point(
                                                    (int) (obj.getPosition().x + pin.getPosition().x),
                                                    (int) (obj.getPosition().y + pin.getPosition().y)
                                            );
                                            if (Tools.isOnLine(obj2, p) != null) {
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
                            if (Tools.endOfLine(((Wire) wire).getPath(), line.p1)) {
                                line.n1 = Tools.isOnLine(wire, Tools.ptToInt(line.p1)) != line;
                            } else {
                                line.n1 = false;
                            }
                            //node 2
                            if (Tools.endOfLine(((Wire) wire).getPath(), line.p2)) {
                                line.n2 = Tools.isOnLine(wire, Tools.ptToInt(line.p2)) != line;
                            } else {
                                line.n2 = false;
                            }
                        });
                    });

        }

        /**
         * Group all lines in wire
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
         * Simplifi all wires => if one long streigh line is created from many
         * short lines than of all make on long line
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
         * Connect all bridges with same id from object list (connecte wires
         * with then are connected)
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

}
