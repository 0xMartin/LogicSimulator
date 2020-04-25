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

import java.util.ArrayList;
import java.util.List;
import logicSimulator.ComputeCore;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.common.Propertie;
import window.MainWindow;

/**
 *
 * @author Martin
 */
public class SystemClosing {

    private MainWindow window;

    private ComputeCore cc;

    /**
     * System closing
     *
     * @param core System core
     */
    public SystemClosing(LogicSimulatorCore core) {
        core.getLSComponents().stream().forEach((comp) -> {
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
            List<String> l1 = this.window.getRefComponents();
            l1.forEach((c) -> {
                propts.add(new Propertie("RefComponent", c));
            });
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

}
