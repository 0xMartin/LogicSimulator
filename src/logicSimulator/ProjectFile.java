/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JPanel;

/**
 * Project file (Workspace: logic circuits; ModuleEditor: Editing of module)
 *
 * @author Martin
 */
public abstract class ProjectFile extends JPanel {

    //project 
    private final Project project;

    //project file mode
    private final PFMode pfMode = new PFMode(false, false, true);

    public ProjectFile(Project project) {
        this.project = project;
    }

    /**
     * Get project file mode
     *
     * @return
     */
    public PFMode getPFMode() {
        return this.pfMode;
    }

    /**
     * Get component if exist
     *
     * @return
     */
    public Component getComp() {
        return this;
    }

    /**
     * Get handler of project file
     *
     * @return
     */
    public abstract PFHandler getHandler();

    /**
     * Get project
     *
     * @return
     */
    public Project getProject() {
        return this.project;
    }

    private ActionListener selectListener;

    /**
     * Listener perform afte selection of this project file
     *
     * @param listener ActionListener
     */
    public void setSelectListener(ActionListener listener) {
        this.selectListener = listener;
    }

    /**
     * Select project file in project
     */
    public void selectInProject() {
        if (this.project != null) {
            this.project.setSelectedFile(this);
            if (this.selectListener != null) {
                this.selectListener.actionPerformed(new ActionEvent(this, 0, ""));
            }
        }
    }

    /**
     * Get serializable object[] that can be saved to file, if system read data
     * from file again in next program run that data can be restored
     *
     * @param projectDirectoryPath Path of the project directory
     * @throws java.io.FileNotFoundException
     */
    public abstract void backUpData(String projectDirectoryPath) throws Exception;

    /**
     * Restore data
     *
     * @param file File with data
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public abstract void restoreData(File file) throws Exception;

}
