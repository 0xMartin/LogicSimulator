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
import logicSimulator.data.PropertieReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import logicSimulator.ComputeCore;
import logicSimulator.LogicSimulatorCore;
import window.MainWindow;
import logicSimulator.LSComponent;
import logicSimulator.Project;
import logicSimulator.SerialPortDriver;
import window.ProjectWizard;

/**
 *
 * @author Martin
 */
public class HLSimulator extends LogicSimulatorCore {

    public static Splash splash;

    public HLSimulator() throws Exception {
        super();
    }

    /**
     * Create and init all components
     *
     * @param proptFiles Propertie files
     * @throws Exception
     */
    public void init(PropertieReader[] proptFiles) throws Exception {

        LSComponent component;

        //project wizard (choose or create project and then continue in initialisation)
        component = new ProjectWizard(null, true);
        component.init(
                this,
                PropertieReader.getWithID(proptFiles, PropertieReader.ID.PROJECT)
        );
        super.getLSComponents().add(component);
        //hide splash screen
        HLSimulator.splash.setVisible(false);
        HLSimulator.splash.dispose();
        //open or create project
        ((ProjectWizard) component).setVisible(true);

        //if project is null
        boolean projectNotFound = true;
        for (LSComponent c : this.getLSComponents()) {
            if (c instanceof Project) {
                projectNotFound = false;
                break;
            }
        }
        if (projectNotFound) {
            System.exit(0);
            return;
        }

        //init serial port driver
        component = new SerialPortDriver(128);
        component.init(
                this,
                PropertieReader.getWithID(proptFiles, PropertieReader.ID.SERIAL_PORT)
        );
        this.getLSComponents().add(component);

        //compute core
        component = new ComputeCore();
        component.init(
                this,
                PropertieReader.getWithID(proptFiles, PropertieReader.ID.COMPUTING)
        );
        this.getLSComponents().add(component);

        //init window
        component = new MainWindow();
        component.init(
                this,
                PropertieReader.getWithID(proptFiles, PropertieReader.ID.WINDOW)
        );
        this.getLSComponents().add(component);

        //load settings from file
    }

    /**
     * Run all components
     */
    public void run() {
        //run all
        this.getLSComponents().forEach((lsObj) -> {
            lsObj.run();
        });
    }

    /**
     * Main, rum logisim core
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //set LookAndFeel 
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equals("Windows")) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException
                | InstantiationException | IllegalAccessException e) {
        }
        SwingUtilities.invokeLater(() -> {
            try {

                //splash
                HLSimulator.splash = new Splash("/src/img/splash.png", 640, 360);
                HLSimulator.splash.setVisible(true);

                //HL simulator
                HLSimulator logicSimulator = new HLSimulator();

                //propertie files
                PropertieReader[] proptList = new PropertieReader[]{
                    new PropertieReader(
                    LogicSimulatorCore.PROPT_PROJECTS,
                    PropertieReader.ID.PROJECT
                    ),
                    new PropertieReader(
                    LogicSimulatorCore.PROPT_WINDOW,
                    PropertieReader.ID.WINDOW
                    ),
                    new PropertieReader(
                    LogicSimulatorCore.PROPT_COMPUTING,
                    PropertieReader.ID.COMPUTING
                    )
                };

                //init
                logicSimulator.init(proptList);

                //run
                logicSimulator.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ex.getStackTrace(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE,
                        null
                );
                HLSimulator.splash.setVisible(false);
                Logger.getLogger(HLSimulator.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(0);
            }
        });
    }

}
