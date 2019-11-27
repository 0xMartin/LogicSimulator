/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Project;
import logicSimulator.WorkSpace;
import logicSimulator.common.Tools;

/**
 *
 * @author Martin
 */
public class ProjectTreeView extends JTree {

    private Project project;

    private LogicSimulatorCore core;

    public ProjectTreeView() {
        super();
    }

    public void init(LogicSimulatorCore core, Project project) {
        //project
        this.project = project;
        //set core
        this.core = core;
        //set renderer
        this.setCellRenderer(new ProjectTreeView.Renderer());
        update();
    }

    public void update() {
        //work spaces
        List<WorkSpace> works = this.project.getWorkSpaces();

        //buil data for tree builder
        List<String[]> data = new ArrayList<>();

        //project files
        int size = works.isEmpty() ? 1 : works.size();
        String[] folder = new String[size + 1];
        data.add(folder);
        folder[0] = "Project Files";
        if (works.isEmpty()) {
            folder[1] = "";
        } else {
            for (int i = 0; i < works.size(); i++) {
                folder[i + 1] = works.get(i).getName();
            }
        }
        //modules
        data.add(new String[]{"Modules", ""});
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
            if (!leaf) {
                return this;
            }
            JLabel l = new JLabel(value.toString());
            l.setFont(tree.getFont());
            l.setForeground(tree.getForeground());
            //l.setIcon();
            return l;
        }

    }

}
