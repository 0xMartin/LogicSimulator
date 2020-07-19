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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import logicSimulator.projectFile.HexEditor;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.Tools;
import logicSimulator.projectFile.DocumentationEditor;
import logicSimulator.projectFile.Library;
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

    private DefaultTreeModel model;

    private DefaultMutableTreeNode workspaces, modules, hexFiles, documentations, libraries;

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
        //create popup menu
        createMenu();

        //build default tree structure
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.project.getName());
        this.model = new DefaultTreeModel(root, true);

        this.workspaces = new DefaultMutableTreeNode("Workspaces");
        root.add(this.workspaces);
        this.modules = new DefaultMutableTreeNode("Modules");
        root.add(this.modules);
        this.hexFiles = new DefaultMutableTreeNode("HEX files");
        root.add(this.hexFiles);
        this.documentations = new DefaultMutableTreeNode("Documentations");
        root.add(this.documentations);
        this.libraries = new DefaultMutableTreeNode("Libraries");
        root.add(this.libraries);

        super.setModel(model);

        //update tree
        updateProjectTree();
    }

    /**
     * Find object in tree model
     *
     * @param model Default tree model
     * @param parent Parent object
     * @param name Name of wanted node
     * @return
     */
    private Object findNode(DefaultTreeModel model, Object parent, String name) {
        int count = model.getChildCount(parent);
        Object node;
        for (int i = 0; i < count; ++i) {
            node = model.getChild(parent, i);
            if (name.equals(node.toString())) {
                return node;
            } else {
                Object ret = findNode(model, node, name);
                if (ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    private void addPFtoTree(ProjectFile pf) {
        if (pf == null) {
            return;
        }

        //get name of folder
        DefaultMutableTreeNode node = null;
        if (pf instanceof WorkSpace) {
            node = this.workspaces;
        } else if (pf instanceof ModuleEditor) {
            node = this.modules;
        } else if (pf instanceof HexEditor) {
            node = this.hexFiles;
        } else if (pf instanceof DocumentationEditor) {
            node = this.documentations;
        } else if (pf instanceof Library) {
            node = this.libraries;
        }

        //add pf to folder
        if (node != null) {
            this.model.insertNodeInto(
                    new DefaultMutableTreeNode(pf.getName() + "." + Tools.getFileType(pf), false),
                    node,
                    node.getChildCount()
            );
        }
    }

    private final List<String> lastPF = new ArrayList<>();

    public void updateProjectTree() {
        if (this.model == null) {
            return;
        }

        //add new files
        List<String> currentPF = new ArrayList<>();
        this.project.getProjectFiles().stream().forEach((pf) -> {
            if (!pf.isLibFile) {
                currentPF.add(pf.getName() + "." + Tools.getFileType(pf));

                //add pf to tree
                Object obj = findNode(model, model.getRoot(), pf.getName() + "." + Tools.getFileType(pf));
                if (obj == null) {
                    addPFtoTree(pf);
                }
            }
        });

        //remove files
        this.lastPF.stream().forEach((pfName) -> {
            if (currentPF.stream().allMatch((pfName2) -> (!pfName.equals(pfName2)))) {
                Object obj = findNode(this.model, this.model.getRoot(), pfName);
                if (obj != null) {
                    this.model.removeNodeFromParent((DefaultMutableTreeNode) obj);
                }
            }
        });

        //refresh tree
        this.lastPF.clear();
        this.project.getProjectFiles().stream().forEach((pf) -> {
            if (!pf.isLibFile) {
                this.lastPF.add(pf.getName() + "." + Tools.getFileType(pf));
            }
        });

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
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            String name = value.toString();
            if (name.endsWith(LogicSimulatorCore.WORKSPACE_FILE_TYPE)) {
                //logic worksapce file
                this.setIcon(SystemResources.LWF_ICON);
            } else if (name.endsWith(LogicSimulatorCore.MODULE_FILE_TYPE)) {
                //module file
                this.setIcon(SystemResources.MF_ICON);
            } else if (name.endsWith(LogicSimulatorCore.HEX_FILE_TYPE)) {
                //hex file
                this.setIcon(SystemResources.HEF_ICON);
            } else if (name.endsWith(LogicSimulatorCore.DOCUMENTATION_FILE_TYPE)) {
                //documentation
                this.setIcon(SystemResources.DF_ICON);
            } else if (name.endsWith(LogicSimulatorCore.LIB_FILE_TYPE)) {
                //library
                this.setIcon(SystemResources.LIB_ICON);
            } else if (name.length() == 0) {
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

        //open file
        item = new JMenuItem("Open");
        item.addActionListener((ActionEvent evt) -> {
            displayFile();
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
