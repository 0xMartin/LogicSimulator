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
package logicSimulator.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.ComputeCore;
import logicSimulator.ExceptionLogger;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Colors;
import window.MainWindow;

/**
 *
 * @author Martin
 */
public class SystemClosing {

    private final LogicSimulatorCore core;

    private MainWindow window;

    private ComputeCore cc;

    /**
     * System closing
     *
     * @param core System core
     */
    public SystemClosing(LogicSimulatorCore core) {
        this.core = core;
        this.core.getLSComponents().stream().forEach((comp) -> {
            if (comp instanceof MainWindow) {
                this.window = (MainWindow) comp;
            } else if (comp instanceof ComputeCore) {
                this.cc = (ComputeCore) comp;
            }
        });
    }

    /**
     * Save all properties
     */
    public void saveProperties() {
        //window properties
        PropertieWriter writer = new PropertieWriter(LogicSimulatorCore.PROPT_WINDOW);
        List<Propertie> propts = new ArrayList<>();
        try {
            //ref components
            List<String> l1 = this.window.getRefComponents();
            l1.forEach((c) -> {
                propts.add(new Propertie("RefComponent", c));
            });
            //colors
            propts.add(new Propertie("GRID", Colors.GRID.getRGB()));
            propts.add(new Propertie("BACKGROUND", Colors.BACKGROUND.getRGB()));
            propts.add(new Propertie("ERROR", Colors.ERROR.getRGB()));
            propts.add(new Propertie("SELECT_RECT", Colors.SELECT_RECT.getRGB()));
            propts.add(new Propertie("SELECT_RECT2", Colors.SELECT_RECT2.getRGB()));
            propts.add(new Propertie("OBJECT", Colors.OBJECT.getRGB()));
            propts.add(new Propertie("IOPIN", Colors.IOPIN.getRGB()));
            propts.add(new Propertie("IOPIN_BUS", Colors.IOPIN_BUS.getRGB()));
            propts.add(new Propertie("WIRE_1", Colors.WIRE_1.getRGB()));
            propts.add(new Propertie("WIRE_0", Colors.WIRE_0.getRGB()));
            propts.add(new Propertie("WIRE_BUS", Colors.WIRE_BUS.getRGB()));
            propts.add(new Propertie("TEXT", Colors.TEXT.getRGB()));
            propts.add(new Propertie("ME_DRAG", Colors.ME_DRAG.getRGB()));
            propts.add(new Propertie("ME_CURSORCROSS", Colors.ME_CURSORCROSS.getRGB()));
            propts.add(new Propertie("ME_CENTER", Colors.ME_CENTER.getRGB()));
            propts.add(new Propertie("GR_BACKGROUND", Colors.GR_BACKGROUND.getRGB()));
            propts.add(new Propertie("GR_AXES", Colors.GR_AXES.getRGB()));
            propts.add(new Propertie("GR_GRAPHLINE", Colors.GR_GRAPHLINE.getRGB()));
            
            writer.writeFile(propts);
        } catch (Exception ex) {
        }

        //computing
        writer = new PropertieWriter(LogicSimulatorCore.PROPT_COMPUTING);
        propts.clear();
        try {
            propts.add(new Propertie("RPS", this.cc.getCTL().getTicksPerSecond()));
            writer.writeFile(propts);
        } catch (Exception ex) {
        }
    }

    public void dispose() {
        //exit program
        this.core.getLSComponents().stream().forEach((ls) -> {
            ls.stop();
        });

        try {
            //close exception logger file
            ExceptionLogger.getInstance().closeFile();
        } catch (IOException ex) {
        }
    }

}
