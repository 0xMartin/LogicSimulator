/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.ComputeCore;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Tools;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.wiring.Input;
import logicSimulator.objects.wiring.Output;

/**
 *
 * @author Martin
 */
public class LogicModule extends WorkSpaceObject {

    //name of workspace (logic model for this module) is used only for data saving
    private String lastModelName = "";

    //this must be set on null before project saving because this is not serializable
    //in opening use 'lastModelName' to reload again
    private List<WorkSpaceObject> logicModel;

    public LogicModule(Point position) {
        super(position);

        super.setModel(new Model(new GraphicsObject[0]));

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
        if (super.getModel() == null) {
            return;
        }

        //cleare pin list of this model
        super.getPins().clear();
        //add module io pins for all inputs and outputs inside of module
        if (this.logicModel != null) {
            this.logicModel.stream().forEach((obj) -> {
                if (obj instanceof Input) {
                    //add input pin to module
                    super.getPins().add(
                            ((Input) obj).getInput()
                    );
                } else if (obj instanceof Output) {
                    //add output to module
                    super.getPins().add(
                            ((Output) obj).getOutput()
                    );
                }
            });
        }
        //move
        L1:
        for (int i = 0; i < super.getPins().size(); i++) {
            IOPin pin1 = super.getPins().get(i);
            Point.Double p1 = pin1.getPosition();
            if (p1 == null) {
                continue;
            }
            for (IOPin pin2 : super.getPins()) {
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

    public WorkSpaceObject cloneObject() {
        LogicModule lm = new LogicModule(Tools.copy(super.getPosition()));
        //clone model
        lm.getModel().clone(super.getModel());
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
