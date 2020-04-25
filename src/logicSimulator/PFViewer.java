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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
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
        super.addMouseListener(this);
        super.addMouseMotionListener(this);
        //when is tab changed then must change value of "VISIBLE" boolean in PFMode
        super.addChangeListener((ChangeEvent e) -> {
            //set visible true for selected component
            ProjectFile selectedPF = (ProjectFile) this.getSelectedComponent();
            selectedPF.getPFMode().VISIBLE = true;
            //set visble false for all remaining components
            for (Component c : super.getComponents()) {
                if (c instanceof ProjectFile) {
                    ProjectFile pf = (ProjectFile) c;
                    if (pf != selectedPF) {
                        pf.getPFMode().VISIBLE = false;
                    }
                }
            }
        });
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
