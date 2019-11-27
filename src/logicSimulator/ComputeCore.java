/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import data.PropertieReader;
import java.awt.Point;
import javax.swing.JLabel;
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

    private LogicSimulatorCore core;

    //rendering thread
    private Thread thread;

    private ConstantTimeLoop ctl;

    private Project project;

    private int CURRENT_TICKS = 0;
    private int CURRENT_UPDATES = 0, LAST_UPDATES = 0;

    public void openProject(Project prj) {
        this.project = prj;
    }

    @Override
    public void init(LogicSimulatorCore core, PropertieReader propt) throws Exception {
        //default
        this.core = core;
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
                if (this.ctl.isStoped()) {
                    Thread.sleep(100);
                }
                if (this.ctl.tickCheck() || this.step) {
                    //tick loging
                    if (!this.step) {
                        diagnostic();
                    }
                    this.step = false;
                    /**
                     * Update all First compute logic function of all gates /
                     * objects and then in next step all output pins write
                     * theirs values on wires
                     */
                    WorkSpace work = this.project.getSelectedWorkspace();
                    if (work.getObjects() == null) {
                        continue;
                    }
                    //compute all objects
                    for (int index = 0; index < work.getObjects().size(); index++) {
                        WorkSpaceObject obj = work.getObjects().get(index);

                        if (obj == null) {
                            continue;
                        }
                        if (obj instanceof Wire) {
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
                            this.CURRENT_UPDATES += changed ? 1 : 0;
                        }

                    }
                    //output pins write their values of wires
                    for (int index = 0; index < work.getObjects().size(); index++) {
                        WorkSpaceObject obj = work.getObjects().get(index);
                        //write
                        if (obj != null) {
                            if (!(obj instanceof Wire)) {
                                if (obj.getModel().getIOPins() != null) {
                                    obj.getModel().getIOPins().forEach((pin) -> {
                                        pin.writeValue();
                                    });
                                }
                            }
                        }
                    }
                    //repaint selected workspace if something change
                    if (this.CURRENT_UPDATES != this.LAST_UPDATES) {
                        this.project.getSelectedWorkspace().getHandler().repaint();
                    }
                    this.LAST_UPDATES = this.CURRENT_UPDATES;
                }
            } catch (Exception ex) {
            }
        }
    }

}
