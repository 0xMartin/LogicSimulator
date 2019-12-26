/*
 * Logic simlator
 * Author: Martin Krcma
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
import logicSimulator.LogicSimulatorCore;
import logicSimulator.ModuleEditor;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.WorkSpace;
import logicSimulator.Tools;
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
        int count_project = 0, count_module = 0;
        for (ProjectFile pf : projectFiles) {
            if (pf instanceof WorkSpace) {
                count_project++;
            } else if (pf instanceof ModuleEditor) {
                count_module++;
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
        //libraries
        data.add(new String[]{"Libraries", ""});
        //documentation
        data.add(new String[]{"Documentation", ""});

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
            if (name.endsWith(".lwf")) {
                //logic worksapce file
                this.setIcon(SystemResources.LWF_ICON);
            } else if (name.endsWith(".mf")) {
                //module file
                this.setIcon(SystemResources.MF_ICON);
            } else if (name.length() == 0){
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
            this.window.displayProjectFile(this.selected);
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

        //> add Analitics
        menu = new JMenu("Analitics");
        this.menu.add(menu);
        //Input-Output analitics
        item = new JMenuItem("Input-Output analitics");
        item.addActionListener((ActionEvent evt) -> {

        });
        menu.add(item);
        //map
        item = new JMenuItem("Map");
        item.addActionListener((ActionEvent evt) -> {

        });
        menu.add(item);
        //Status graphs
        item = new JMenuItem("Status graphs");
        item.addActionListener((ActionEvent evt) -> {

        });
        menu.add(item);
        //Circuit info
        item = new JMenuItem("Circuit info");
        item.addActionListener((ActionEvent evt) -> {

        });
        menu.add(item);

        //> add Analitics
        menu = new JMenu("Tools");
        this.menu.add(menu);
        //print
        item = new JMenuItem("Print");
        item.addActionListener((ActionEvent evt) -> {

        });
        menu.add(item);
        //print screen
        item = new JMenuItem("Printscreen");
        item.addActionListener((ActionEvent evt) -> {

        });
        menu.add(item);
        //clone
        item = new JMenuItem("Clone");
        item.addActionListener((ActionEvent evt) -> {

        });
        menu.add(item);

    }

}
