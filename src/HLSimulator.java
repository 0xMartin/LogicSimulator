/*
 * Logic simlator
 * Author: Martin Krcma
 */

import logicSimulator.ui.SystemResources;
import logicSimulator.data.PropertieReader;
import java.util.ArrayList;
import java.util.List;
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
import window.ProjectWizard;

/**
 *
 * @author Martin
 */
public class HLSimulator extends SystemResources implements LogicSimulatorCore {

    //all main components
    private final List<LSComponent> components;

    public static Splash splash;

    public HLSimulator() throws Exception {
        super();
        this.components = new ArrayList<>();
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
        this.components.add(component);
        //hide splash screen
        this.splash.setVisible(false);
        this.splash.dispose();
        //open or create project
        ((ProjectWizard) component).setVisible(true);

        //if project is null
        boolean projectNotFound = true;
        for (LSComponent c : this.components) {
            if (c instanceof Project) {
                projectNotFound = false;
                break;
            }
        }
        if (projectNotFound) {
            System.exit(0);
            return;
        }

        //compute core
        component = new ComputeCore();
        component.init(
                this,
                PropertieReader.getWithID(proptFiles, PropertieReader.ID.COMPUTING)
        );
        this.components.add(component);

        //init window
        component = new MainWindow();
        component.init(
                this,
                PropertieReader.getWithID(proptFiles, PropertieReader.ID.WINDOW)
        );
        this.components.add(component);

        //load settings from file
    }

    /**
     * Run all components
     */
    public void run() {
        //run all
        this.components.forEach((lsObj) -> {
            lsObj.run();
        });
    }

    @Override
    public List<LSComponent> getLSComponents() {
        return this.components;
    }

    @Override
    public void sendMessage(MessageType type, String message) {

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
                HLSimulator.splash = new Splash("/src/img/splash.png", 900, 450);
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
