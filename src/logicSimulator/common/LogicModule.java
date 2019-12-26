/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.ComputeCore;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Tools;
import logicSimulator.WorkSpace;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.wiring.Input;
import logicSimulator.objects.wiring.Output;

/**
 *
 * @author Martin
 */
public class LogicModule implements WorkSpaceObject, Serializable {

    private boolean select = false;

    private final Point position;

    private final Model model;

    //name of workspace (logic model for this module) is used only for data saving
    private String lastModelName = "";
    //this must be set on null before project saving because this is not serializable
    //in opening use 'lastModelName' to reload again
    private List<WorkSpaceObject> logicModel;

    public LogicModule(Point position) {
        this.model = new Model(null, null, null);
        this.position = position;
        this.logicModel = new ArrayList<>();
    }

    public List<WorkSpaceObject> getLogicModel() {
        return this.logicModel;
    }

    public String getNameOfLastLogicModel() {
        return this.lastModelName;
    }

    /**
     * Set logic model for this module (not copy must be referenc)
     *
     * @param workspace Logic model (Workspace with model)
     */
    public void setLogicModel(WorkSpace workspace) {
        this.logicModel = workspace.getObjects();
        //set name of workspace
        if (this.logicModel != null) {
            this.lastModelName = workspace.getName();
        }
        refreshIOPins();
    }

    public void refreshIOPins() {
        //cleare pin list of this model
        this.model.getIOPins().clear();
        //add module io pins for all inputs and outputs inside of module
        if (this.logicModel != null) {
            this.logicModel.stream().forEach((obj) -> {
                if (obj instanceof Input) {
                    //add input pin to module
                    this.model.getIOPins().add(
                            ((Input) obj).getInput()
                    );
                } else if (obj instanceof Output) {
                    //add output to module
                    this.model.getIOPins().add(
                            ((Output) obj).getOutput()
                    );
                }
            });
        }
        //move
        L1:
        for (int i = 0; i < this.model.getIOPins().size(); i++) {
            IOPin pin1 = this.model.getIOPins().get(i);
            Point.Double p1 = pin1.getPosition();
            if (p1 == null) {
                continue;
            }
            for (IOPin pin2 : this.model.getIOPins()) {
                Point.Double p2 = pin2.getPosition();
                if (pin1 == pin2 || p2 == null) {
                    continue;
                }
                if (Tools.dist(p1, p2) < 4) {
                    p1.y -= LogicSimulatorCore.WORK_SPACE_STEP;
                    Tools.step(p1, LogicSimulatorCore.WORK_SPACE_STEP);
                    i -= 1;
                    continue L1;
                }
            }
        }
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(this.model.getWidth(), this.model.getHeight());
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        this.model.renderModel(g2, this.position, offset, screen, this.select);
    }

    @Override
    public Propertie[] getProperties() {
        return null;
    }

    @Override
    public void changePropertie(Propertie arg0) {

    }

    @Override
    public boolean select(Point cursor) {
        if (this.model.intersect(cursor, this.position)) {
            this.select = true;
            return true;
        }
        return false;
    }

    @Override
    public void unSelect() {
        this.select = false;
    }

    @Override
    public boolean isSelected() {
        return this.select;
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public boolean compute() {
        if (this.logicModel != null) {
            int changes = ComputeCore.compute(this.logicModel);
            return changes != 0;
        }
        return false;
    }

    @Override
    public boolean error() {
        return false;
    }

    @Override
    public List<IOPin> getPins() {
        return this.model.getIOPins();
    }

    public WorkSpaceObject cloneObject() {
        LogicModule lm = new LogicModule(new Point(this.position.x, this.position.y));
        //clone model
        lm.getModel().clone(this.model);
        //clone logic model data
        this.logicModel.stream().forEach((obj) -> {
            try {
                lm.getLogicModel().add(Tools.clone(obj));
            } catch (CloneNotSupportedException ex) {
            }
        });
        //reconect logic model, because are duplicating object not conections -> must reconnect again
        ComputeCore.CircuitHandler.refreshConnectivity(lm.getLogicModel());
        //connect inputs and outputs with pins of this model
        lm.getLogicModel().stream().forEach((obj) -> {
            if (obj instanceof Input) {
                Input in = (Input) obj;
                for (IOPin p : lm.getModel().getIOPins()) {
                    if (p.getLabel().equals(in.getLabel())) {
                        in.setInput(p);
                        break;
                    }
                }
            } else if (obj instanceof Output) {
                Output out = (Output) obj;
                for (IOPin p : lm.getModel().getIOPins()) {
                    if (p.getLabel().equals(out.getLabel())) {
                        out.setOutput(p);
                        break;
                    }
                }
            }
        });
        //logic model name (name of workspace)
        lm.lastModelName = this.lastModelName;
        return lm;
    }

}
