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
package window.components;

import logicSimulator.ui.SystemResources;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import logicSimulator.projectFile.HexEditor;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.Tools;
import logicSimulator.projectFile.DocumentationEditor;
import window.MainWindow;
import window.NewFile;

/**
 *
 * @author Martin
 */
public class ProjectTreeView extends JTree implements MouseListener {

    private Project project;

    private MainWindow window;

    private JPopupMenu menu;

    public ProjectTreeView() {
        super();
        super.addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Tools.setHighQuality(g2);
        super.paintComponent((Graphics2D) g);
    }

    public void init(MainWindow window, Project project) {
        //project
        this.project = project;
        //load MainWindow
        this.window = window;
        //set renderer
        this.setCellRenderer(new ProjectTreeView.Renderer());
        //update tree
        updateProjectTree();
        //create popup menu
        createMenu();
    }

    public void updateProjectTree() {
        //work spaces
        List<ProjectFile> projectFiles = this.project.getProjectFiles();

        //count number of items in sections
        int count_project = 0, count_module = 0, count_hex = 0, count_doc = 0;
        for (ProjectFile pf : projectFiles) {
            if (pf instanceof WorkSpace) {
                count_project++;
            } else if (pf instanceof ModuleEditor) {
                count_module++;
            } else if (pf instanceof HexEditor) {
                count_hex++;
            }else if (pf instanceof DocumentationEditor) {
                count_doc++;
            }
        }

        //buil data for tree builder
        List<String[]> data = new ArrayList<>();

        //project files
        String[] folder = new String[Math.max(count_project + 1, 2)];
        data.add(folder);
        folder[0] = "Workspaces";
        if (count_project == 0) {
            folder[1] = "";
        } else {
            int index = 0;
            for (int i = 0; i < projectFiles.size() && index < count_project; i++) {
                if (projectFiles.get(i) instanceof WorkSpace) {
                    folder[(index++) + 1] = projectFiles.get(i).getComp().getName()
                            + "." + LogicSimulatorCore.WORKSPACE_FILE_TYPE;
                }
            }
        }
        
        //modules
        folder = new String[Math.max(count_module + 1, 2)];
        data.add(folder);
        folder[0] = "Modules";
        if (count_module == 0) {
            folder[1] = "";
        } else {
            int index = 0;
            for (int i = 0; i < projectFiles.size() && index < count_module; i++) {
                if (projectFiles.get(i) instanceof ModuleEditor) {
                    folder[(index++) + 1] = projectFiles.get(i).getComp().getName()
                            + "." + LogicSimulatorCore.MODULE_FILE_TYPE;
                }
            }
        }
        
        //hex files
        folder = new String[Math.max(count_hex + 1, 2)];
        data.add(folder);
        folder[0] = "HEX files";
        if (count_hex == 0) {
            folder[1] = "";
        } else {
            int index = 0;
            for (int i = 0; i < projectFiles.size() && index < count_hex; i++) {
                if (projectFiles.get(i) instanceof HexEditor) {
                    folder[(index++) + 1] = projectFiles.get(i).getComp().getName()
                            + "." + LogicSimulatorCore.HEX_FILE_TYPE;
                }
            }
        }
        
        //libraries
        data.add(new String[]{"Libraries", ""});
        
        //documentation files
        folder = new String[Math.max(count_doc + 1, 2)];
        data.add(folder);
        folder[0] = "Documentations";
        if (count_hex == 0) {
            folder[1] = "";
        } else {
            int index = 0;
            for (int i = 0; i < projectFiles.size() && index < count_hex; i++) {
                if (projectFiles.get(i) instanceof DocumentationEditor) {
                    folder[(index++) + 1] = projectFiles.get(i).getComp().getName()
                            + "." + LogicSimulatorCore.DOCUMENTATION_FILE_TYPE;
                }
            }
        }
        
        //build component list  
        this.setModel(Tools.buildTreeModel(data, this.project.getName()));
    }

    public WorkSpace getWorkSpace() {
        return null;
    }

    /**
     * Tree renderer
     */
    private class Renderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            String name = value.toString();
            if (name.endsWith(LogicSimulatorCore.WORKSPACE_FILE_TYPE)) {
                //logic worksapce file
                this.setIcon(SystemResources.LWF_ICON);
            } else if (name.endsWith(LogicSimulatorCore.MODULE_FILE_TYPE)) {
                //module file
                this.setIcon(SystemResources.MF_ICON);
            } else if (name.endsWith(LogicSimulatorCore.HEX_FILE_TYPE)) {
                //module file
                this.setIcon(SystemResources.HEF_ICON);
            }  else if (name.endsWith(LogicSimulatorCore.DOCUMENTATION_FILE_TYPE)) {
                //module file
                this.setIcon(SystemResources.DF_ICON);
            }  else if (name.length() == 0){
                //module file
                this.setIcon(SystemResources.PACKAGE_ICON);
            }

            return this;
        }

    }

    @Override
    public void mouseClicked(MouseEvent evt) {

    }

    private ProjectFile selected = null;

    @Override
    public void mousePressed(MouseEvent evt) {
        //on rigth button press show popup menu
        if (evt.getButton() == 3) {
            this.menu.show(this, evt.getX(), evt.getY());
        } else {
            //when user clicks on the "empty surface"
            if (this.getRowForLocation(evt.getX(), evt.getY()) == -1) {
                selected = null;
                this.clearSelection();
            }
            //select item
            TreePath path = this.getSelectionPath();
            if (this.getSelectionPath() != null) {
                if (path.getPath().length == 3) {
                    //select project file
                    for (ProjectFile pf : this.project.getProjectFiles()) {
                        String name = pf.getComp().getName() + "." + Tools.getFileType(pf);
                        //compare    
                        if (name.equals(path.getPathComponent(2).toString())) {
                            if (this.selected == pf) {
                                //display selelected file in main window
                                displayFile();
                            }
                            this.selected = pf;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void displayFile() {
        if (this.selected != null) {
            this.window.getPFDockingPanel().displayProjectFile(this.selected);
            this.window.getPFDockingPanel().refreshLayout();
        }
    }

    @Override
    public void mouseReleased(MouseEvent evt) {

    }

    @Override
    public void mouseEntered(MouseEvent evt) {

    }

    @Override
    public void mouseExited(MouseEvent evt) {

    }

    private void createMenu() {
        this.menu = new JPopupMenu();
        JMenu menu;
        JMenuItem item;
        //new file
        item = new JMenuItem("Add new file");
        item.addActionListener((ActionEvent evt) -> {
            (new NewFile(this.window, this.project)).setVisible(true);
        });
        this.menu.add(item);

        //> add edit menu
        menu = new JMenu("Edit");
        this.menu.add(menu);
        //rename file
        item = new JMenuItem("Rename");
        item.addActionListener((ActionEvent evt) -> {
            //rename file
            this.window.renameFileOfProject(this.selected);
        });
        menu.add(item);
        //delete file
        item = new JMenuItem("Delete");
        item.addActionListener((ActionEvent evt) -> {
            //rename file
            this.window.deleteFile(this.selected);
        });
        menu.add(item);

    }

}
