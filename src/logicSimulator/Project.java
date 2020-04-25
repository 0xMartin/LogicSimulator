/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import logicSimulator.data.PropertieReader;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import logicSimulator.common.CopyObjectVector;
import window.MainWindow;

/**
 *
 * @author Martin
 */
public class Project implements LSComponent {

    /**
     * Project file ".phl" location
     */
    private File file;

    private LogicSimulatorCore core;

    //selected project file
    private ProjectFile selectedFile = null;

    //all project files
    private final List<ProjectFile> projectFiles;

    //copy
    public CopyObjectVector copyObjects = null;

    //name of project
    private String name;

    //true -> edit mode activated (for circuit editing, ...), false -> control mode
    public boolean editMode = true;

    public Project(String name) {
        this.name = name;
        this.projectFiles = new LinkedList<>();
    }

    /**
     * Get selected project file
     *
     * @return
     */
    public ProjectFile getSelectedFile() {
        return this.selectedFile;
    }

    /**
     * Select project file
     *
     * @param projectFile ProjectFile
     */
    public void setSelectedFile(ProjectFile projectFile) {
        this.selectedFile = projectFile;      
    }

    /**
     * Add object to propertie editor
     *
     * @param obj WorkSpaceObject
     */
    public void editPropt(WorkSpaceObject obj) {
        if (this.core == null) {
            return;
        }
        if (this.core.getLSComponents() == null) {
            return;
        }
        for (LSComponent comp : this.core.getLSComponents()) {
            if (comp instanceof MainWindow) {
                ((MainWindow) comp).editProperties(obj);
                break;
            }
        }
    }

    @Override
    public void init(LogicSimulatorCore core, PropertieReader propt) throws Exception {
        this.core = core;

    }

    @Override
    public void run() {
    }

    @Override
    public void stop() {
    }

    /**
     * Return all project for this project
     *
     * @return
     */
    public List<ProjectFile> getProjectFiles() {
        return this.projectFiles;
    }

    public void rename(String newName) {
        //to do
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
