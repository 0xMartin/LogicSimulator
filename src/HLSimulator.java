/*
 * Logic simlator
 * Author: Martin Krcma
 */

import logicSimulator.ui.SystemResources;
import data.PropertieReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import logicSimulator.ComputeCore;
import logicSimulator.LogicSimulatorCore;
import window.MainWindow;
import logicSimulator.LSComponent;
import logicSimulator.Project;
import logicSimulator.Tools;
import window.ProjectWizard;

/**
 *
 * @author Martin
 */
public class HLSimulator extends SystemResources implements LogicSimulatorCore {

    //all main components
    private final List<LSComponent> components;

    public static Splash splash;

    public HLSimulator() throws IOException {
        super();
        this.components = new ArrayList<>();
        //Settings.HIGH_RENDER_QUALITY = true;
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
                    new PropertieReader(PROPT_PROJECTS, PropertieReader.ID.PROJECT)
                };
                
                //init
                logicSimulator.init(proptList);
                
                //run
                logicSimulator.run();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE, null);
                Logger.getLogger(HLSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

}
