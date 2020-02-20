/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JTabbedPane;
import logicSimulator.ProjectFile;
import window.components.DockingButton;
import window.components.TabbedPaneTitle;

/**
 * Dockable tabbed pane
 *
 * @author Martin
 */
public class PFViewer extends JTabbedPane implements
        MouseListener, MouseMotionListener {

    private final DockingButton dockingButton;

    //panel where are tabbed panes
    private final PFTwoSlotViewer parent;

    public PFViewer(PFTwoSlotViewer parent) {
        this.dockingButton = new DockingButton(this, DockingButton.Type.CENTER);
        this.parent = parent;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public PFTwoSlotViewer getDockingPanel() {
        return this.parent;
    }

    private Point pressed = null;
    private boolean showDocking = true;

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.pressed != null) {
            if (e.getY() - pressed.y > this.getHeight() / 3) {
                //display
                if (this.showDocking) {
                    this.dockingButton.resetSelect();
                    this.dockingButton.setVisible(true);
                    this.dockingButton.setLocationRelativeTo(this);
                    this.showDocking = false;
                }
                //intersect with button
                this.dockingButton.intersect(e.getLocationOnScreen());
            } else {
                this.dockingButton.setVisible(false);
                this.showDocking = true;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.pressed = e.getPoint();

        /**
         * init docking button type
         */
        //second dockable tabbed pane
        PFViewer dtp = (PFViewer) getNextDockableTabbedPane();

        //count of tabs
        int count = this.getTabCount();

        DockingButton.Type type = DockingButton.Type.CENTER;

        if (dtp.getTabCount() == 0) {
            if (count > 1) {
                //left or right
                type = DockingButton.Type.LEFT_RIGHT;
            }
        } else {
            if (this.parent.isOnLeftSide(this)) {
                //this is on left and second is on right side
                type = DockingButton.Type.RIGHT;
            } else {
                //this is on right and second is on left side
                type = DockingButton.Type.LEFT;
            }
        }

        //set docking button type
        this.dockingButton.setType(type);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.dockingButton.setVisible(false);
        if (this.dockingButton != null) {
            switch (this.dockingButton.getButton()) {
                case -1:
                    //move from right to left
                    if (this.parent.isOnLeftSide(this)) {
                        this.parent.swapDockableTabbedPanels();
                    }
                    moveTab();
                    break;
                case 1:
                    //move from left to right
                    if (!this.parent.isOnLeftSide(this)) {
                        this.parent.swapDockableTabbedPanels();
                    }
                    moveTab();
                    break;
            }
            //refresh layout
            this.parent.refreshLayout();
            //reset select
            this.dockingButton.resetSelect();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Move selected tab from this tabbed pane to next tabbed pane in pane (j
     * spliter)
     */
    private void moveTab() {
        Component c = this.getSelectedComponent();
        if (c != null) {
            this.remove(c);
            PFViewer tp = getNextDockableTabbedPane();
            tp.add(c.getName(), c);
            //add TabbedPaneTitle
            tp.setTabComponentAt(
                    tp.getTabCount() - 1,
                    new TabbedPaneTitle(tp, c)
            );
            tp.setSelectedComponent(c);

            ((ProjectFile) c).getPFMode().LEFT_SIDE = !((ProjectFile) c).getPFMode().LEFT_SIDE;
        }
    }

    private PFViewer getNextDockableTabbedPane() {
        if (this != this.parent.getLeft()) {
            return this.parent.getLeft();
        } else {
            return this.parent.getRight();
        }
    }

}
