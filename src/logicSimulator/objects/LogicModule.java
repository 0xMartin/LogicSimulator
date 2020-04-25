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
package logicSimulator.objects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.ComputeCore;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Model;
import logicSimulator.objects.wiring.Input;
import logicSimulator.objects.wiring.Output;

/**
 *
 * @author Martin
 */
public class LogicModule extends WorkSpaceObject {

    //name of module
    private final String moduleName;

    //in opening use 'lastModelName' to reload again
    private transient List<WorkSpaceObject> logicModel;

    public LogicModule(Point position, String name) {
        super(position);
        super.setModel(new Model());
        this.logicModel = new ArrayList<>();
        this.moduleName = name;
    }

    public List<WorkSpaceObject> getLogicModel() {
        return this.logicModel;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    @Override
    public void restore() {
        this.logicModel = new ArrayList<>();
    }

    /**
     * Set logic model for this module (not copy must be referenc)
     *
     * @param logicModel New logic model
     */
    public void setLogicModel(List<WorkSpaceObject> logicModel) {
        this.logicModel = logicModel;
        refreshIOPins();
    }

    /**
     * Clone module
     *
     * @param module Module
     */
    public void cloneModule(LogicModule module) {
        //clone graphics model
        super.getModel().clone(module.getModel());
        
        //clone logic model
        this.logicModel = Tools.cloneWObjects(module.getLogicModel());
        
        //reconect logic model, because are duplicating object not conections -> must reconnect again
        ComputeCore.CircuitHandler.refreshConnectivity(this.logicModel);
        
        //set cloned logic model
        setLogicModel(this.logicModel);      
    }

    /**
     * Refresh io pins of this module
     */
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
                    break;
                }
            }
        }
    }

    @Override
    public boolean compute() {
        if (this.logicModel != null) {
            try {
                int changes = ComputeCore.compute(this.logicModel);
                return changes != 0;
            } catch (Exception ex) {
            }
        }
        return false;
    }

    @Override
    public boolean error() {
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        LogicModule lm = new LogicModule(Tools.copy(super.getPosition()), this.moduleName);

        //clone model
        lm.getModel().clone(super.getModel());

        //clone logic model data
        lm.logicModel = Tools.cloneWObjects(this.logicModel);
        
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
        return lm;
    }

}
