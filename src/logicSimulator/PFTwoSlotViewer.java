/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import window.components.TabbedPaneTitle;

/**
 * DockingPanel for two docking tabbed panes
 *
 * @author Martin
 */
public class PFTwoSlotViewer extends JPanel {

    /**
     * two tabbed panes for project files (left is primary => if is visible only
     * on of them tham will always visible "left")*
     */
    private PFViewer left, right;

    private JSplitPane spliter = null;

    public PFTwoSlotViewer() {
        this.left = new PFViewer(this);
        this.right = new PFViewer(this);
    }

    /**
     * Get left tabbed pane
     *
     * @return
     */
    public PFViewer getLeft() {
        return this.left;
    }

    /**
     * Get right tabbed pane
     *
     * @return
     */
    public PFViewer getRight() {
        return this.right;
    }

    /**
     * Is DockableTabbedPane on left side in this docking panel
     *
     * @param dtp DockableTabbedPane
     * @return
     */
    public boolean isOnLeftSide(PFViewer dtp) {
        return dtp == this.left;
    }

    /**
     * Refresh layout of docking panels in parent panel
     */
    public void refreshLayout() {
        this.removeAll();

        if (this.left.getTabCount() > 0 && this.right.getTabCount() > 0) {
            //both must be displayed (spliter)
            if (this.spliter == null) {
                this.spliter = new JSplitPane();
                this.spliter.setLeftComponent(this.left);
                this.spliter.setRightComponent(this.right);
                this.spliter.setSize(this.getWidth(), this.getHeight());
                this.spliter.setDividerLocation(0.5f);
            }
            this.add(this.spliter);
        } else {
            this.spliter = null;
            //only one displayed

            /**
             * swap tabbed panes if left is emtpy and right have some component
             * inside (left is primary and must be visible)
             */
            if (this.left.getTabCount() == 0 && this.right.getTabCount() > 0) {
                this.swapDockableTabbedPanels();
            }

            if (this.left.getTabCount() > 0) {
                //this.left.setSize(this.getWidth(), this.getHeight());
                this.add(this.left);
            }
        }

        this.revalidate();

        this.repaint();
    }

    /**
     * Swap dockable tabbed panels in pane (j split)
     */
    public void swapDockableTabbedPanels() {
        PFViewer temp = this.left;
        this.left = this.right;
        this.right = temp;

        for (Component c : this.left.getComponents()) {
            if (c instanceof ProjectFile) {
                ProjectFile pf = (ProjectFile) c;
                if (pf.getPFMode().OPENED) {
                    pf.getPFMode().LEFT_SIDE = true;
                }
            }
        }

        for (Component c : this.right.getComponents()) {
            if (c instanceof ProjectFile) {
                ProjectFile pf = (ProjectFile) c;
                if (pf.getPFMode().OPENED) {
                    pf.getPFMode().LEFT_SIDE = false;
                }
            }
        }
    }

    /**
     * Add or display new project file to tabbed panel (!!not add to project
     * data list with all project files !!)
     *
     * @param pf Workspace
     */
    public void displayProjectFile(ProjectFile pf) {
        pf.getPFMode().OPENED = true;

        //find project file in tabbedpane if it isnt inside then add
        boolean addProjectFile = true;

        //primary add project file to the left side
        PFViewer tp = pf.getPFMode().LEFT_SIDE ? this.left : this.right;

        //try find in some tabbed pane (if is inside then only select and mov on the top)
        //finding of project file in left
        for (Component c : this.left.getComponents()) {
            if (c == pf) {
                addProjectFile = false;
                tp = this.left;
                break;
            }
        }
        //finding of project file in right if 
        for (Component c : this.right.getComponents()) {
            if (c == pf) {
                addProjectFile = false;
                tp = this.right;
                break;
            }
        }

        //add this project file
        if (addProjectFile) {
            //add project file
            tp.add(pf.getComp().getName(), pf.getComp());
            //add TabbedPaneTitle
            tp.setTabComponentAt(
                    tp.getTabCount() - 1,
                    new TabbedPaneTitle(tp, pf.getComp())
            );
        }

        //select project file in tabbed pane
        tp.setSelectedComponent(pf.getComp());
    }

    /**
     * Remove project file fro one of tabbed panes
     *
     * @param comp
     */
    public void removePF(Component comp) {
        JTabbedPane tp = null;

        for (Component c : this.left.getComponents()) {
            if (c == comp) {
                tp = this.left;
                break;
            }
        }

        for (Component c : this.right.getComponents()) {
            if (c == comp) {
                tp = this.right;
                break;
            }
        }

        if (tp != null) {
            tp.remove(comp);
            this.refreshLayout();
        }

    }

    /**
     * Get all tab component from both (left + right) panes
     *
     * @return
     */
    public List<Component> getAllTabComponent() {
        List<Component> comps = new ArrayList<>();
        comps.addAll(Arrays.asList(this.left.getComponents()));
        comps.addAll(Arrays.asList(this.right.getComponents()));
        return comps;
    }

    /**
     * Refresh all names of both tabbed panes
     */
    public void refreshAllNames() {
        refreshAllNames(this.left);
        refreshAllNames(this.right);
    }

    /**
     * Refresh all names of tabbed pane
     *
     * @param pftp ProjectFileTabbedPane
     */
    private void refreshAllNames(PFViewer pftp) {
        for (Component c : pftp.getComponents()) {
            if (c == null) {
                continue;
            }
            //get index of component c
            int index = pftp.indexOfComponent(c);
            if (index >= 0) {
                //get tab component
                Component title = pftp.getTabComponentAt(index);
                if (title instanceof TabbedPaneTitle) {
                    //chage title
                    ((TabbedPaneTitle) title).setTitle(c.getName());
                }
            }
        }
    }

}
