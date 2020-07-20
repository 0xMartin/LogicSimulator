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
package logicSimulator;

import logicSimulator.data.PropertieReader;
import java.io.File;
import java.util.ArrayList;
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

    /**
     * Display project file in main window
     *
     * @param pf Project File
     */
    public void displayFile(ProjectFile pf) {
        if (this.core == null) {
            return;
        }
        if (this.core.getLSComponents() == null) {
            return;
        }
        for (LSComponent comp : this.core.getLSComponents()) {
            if (comp instanceof MainWindow) {
                ((MainWindow) comp).getPFDockingPanel().displayProjectFile(pf);
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

    public void setName(String name) {
        if (name != null) {
            if (name.length() != 0) {
                this.name = name;
            }
        }
    }

    /**
     * Get location of project
     * @return 
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Set location of porject
     * @param file 
     */
    public void setFile(File file) {
        this.file = file;
    }

    private final List<WorkSpaceObject> refComponent = new ArrayList<>();

    /**
     * References on components (components in toolbar)
     * @return List<WorkSpaceObject>
     */
    public List<WorkSpaceObject> getRefComponents() {
        return this.refComponent;
    }
    
}
