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

    //if is true than this file dont be saved
    public transient boolean isLibFile = false;
    //name of lib
    public transient String libName = "";
    
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
