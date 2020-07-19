/* 
 * Copyright (C) 2020 Martin Krcma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package logicSimulator;

import logicSimulator.projectFile.WorkSpace;
import logicSimulator.data.PropertieReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.common.Propertie;
import logicSimulator.objects.wiring.Wire;
import logicSimulator.timing.ConstantTimeLoop;

/**
 * Compute core Support logic circuit simulating, step by step simuating,
 * dynamic changing of timinig
 *
 * @author Martin
 */
public class ComputeCore implements LSComponent {

    private WorkSpace work;

    //rendering thread
    private Thread thread;

    //loop for constant timing
    private ConstantTimeLoop ctl;

    //opened project
    private Project project;

    //current ticks
    private int CURRENT_TICKS = 0;

    /**
     * current updates, and last updates (last is used for detectig change and
     * on change repaint workspace)
     */
    private int CURRENT_UPDATES = 0, LAST_UPDATES = 0;

    //1 sec listener, workspace closed
    private ActionListener invoke1Sec, workClosed;
    
    //invoke every compute core tick
    private List<ActionListener> onTick = new ArrayList<>();

    /**
     * Set workspace for simulation
     *
     * @param work
     */
    public void setWorkSpace(WorkSpace work) {
        this.work = work;
    }

    public WorkSpace getWorkspace() {
        return this.work;
    }

    /**
     * Open project
     *
     * @param prj Project
     */
    public void openProject(Project prj) {
        this.project = prj;
    }

    /**
     * Add one second action listener (call one time per second when simulation
     * run)
     *
     * @param action ActionListener
     */
    public void set1SecListener(ActionListener action) {
        this.invoke1Sec = action;
    }

    /**
     * Listener invoke action when workspace is closed duruning running simulation
     *
     * @param action ActionListener
     */
    public void setWorkSpaceClosedListener(ActionListener action) {
        this.workClosed = action;
    }
    
    /**
     * Listener is invoked when compute core do one computing iteration
     * @param action ActionListener
     */
    public void addOnTickListener(ActionListener action) {
        this.onTick.add(action);
    }
    
    /**
     * Remove on tick listener
     * @param action ActionListener
     */
    public void removeOnTickListener(ActionListener action) {
        this.onTick.remove(action);
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

        //properties
        try {
            List<Propertie> propts = propt.readFile();
            propts.stream().forEach((p -> {
                try {
                    switch (p.getName()) {
                        case "RPS":
                            this.ctl.setTicksPerSecond(p.getValueInt());
                            break;
                    }
                } catch (NumberFormatException ex) {
                    ExceptionLogger.getInstance().logException(ex);
                }
            }));
        } catch (Exception ex) {
            ExceptionLogger.getInstance().logException(ex);
        }
    }

    /**
     * Run compute core thread
     */
    @Override
    public void run() {
        this.thread.start();
    }

    private boolean abort = false;

    @Override
    public void stop() {
        this.abort = true;
    }

    /**
     * Get constat time loop for this computing core
     *
     * @return
     */
    public ConstantTimeLoop getCTL() {
        return this.ctl;
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

    private boolean step = false;

    /**
     * Compute one step, computing must be stoped
     */
    public void step() {
        this.step = true;
    }

    private long last = System.nanoTime();

    /**
     * Analytics
     */
    private void analytics() {
        this.CURRENT_TICKS++;
        if (System.nanoTime() - this.last >= 1e9) {
            this.last = System.nanoTime();
            //send invoke listener
            if (this.invoke1Sec != null) {
                ActionEvent e = new ActionEvent(
                        this,
                        0,
                        "UPS=" + this.CURRENT_TICKS + ";" //updates pre second
                        + "UPDATES=" + this.CURRENT_UPDATES //how many object update
                );
                this.invoke1Sec.actionPerformed(e);
            }
            //reset diagnostic variables
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
            if (this.abort) {
                break;
            }
            try {

                if (this.ctl.isStoped()) {
                    Thread.sleep(100);
                }

                if (this.ctl.tickCheck() || this.step) {
                    //analytics
                    analytics();

                    if (this.work == null) {
                        continue;
                    }

                    //workspace must be opened
                    if (!this.work.getPFMode().OPENED) {
                        //invoke workspace closed aciton
                        this.workClosed.actionPerformed(new ActionEvent(this, 0, ""));
                        continue;
                    }

                    if (this.work.getObjects().isEmpty()) {
                        continue;
                    }

                    //compute
                    this.CURRENT_UPDATES += ComputeCore.compute(this.work.getObjects());

                    //repaint selected workspace if something change
                    if (this.CURRENT_UPDATES != this.LAST_UPDATES || this.step) {
                        this.step = false;
                        this.work.getHandler().repaintPF();
                    }
                    this.LAST_UPDATES = this.CURRENT_UPDATES;
                    
                    //invoke tick listeners
                    this.onTick.stream().forEach((tick) -> {
                        tick.actionPerformed(new ActionEvent(this, 0, ""));
                    });
                }
            } catch (Exception ex) {
                ExceptionLogger.getInstance().logException(ex);
            }
        }
    }

    /**
     * First compute logic function of all gates / objects and then in next step
     * all output pins write theirs values on wires
     *
     * @param objects WorkSpace objects
     * @return Number of object that changes thers stats
     * @throws java.lang.Exception
     */
    public static int compute(List<WorkSpaceObject> objects) throws Exception {
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

}
