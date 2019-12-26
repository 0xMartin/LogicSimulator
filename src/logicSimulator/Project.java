/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import data.PropertieReader;
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

    private File file;

    private LogicSimulatorCore core;

    private ProjectFile selectedFile = null;

    private final List<ProjectFile> projectFiles;

    //copy
    public CopyObjectVector copyObjects = null;

    private String name;
    public boolean editMode = true;

    public Project(String name) {
        this.name = name;
        this.projectFiles = new LinkedList<>();
    }

    public ProjectFile getSelectedFile() {
        return this.selectedFile;
    }

    public void setSelectedFile(ProjectFile projectFile) {
        this.selectedFile = projectFile;
    }

    /**
     * Add object to propertie editor
     *
     * @param obj
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
        //none
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
