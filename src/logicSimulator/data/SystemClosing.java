/*
 * Logic simlator
 * Author: Martin Krcma
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
