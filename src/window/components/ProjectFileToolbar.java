/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import logicSimulator.ProjectFile;
import logicSimulator.WorkSpace;
import logicSimulator.ui.ButtonHQ;
import logicSimulator.ui.SystemResources;

/**
 *
 * @author Martin
 */
public class ProjectFileToolbar extends JToolBar {

    private final ProjectFile projectFile;

    public ProjectFileToolbar(ProjectFile pf) {
        this.setFloatable(false);
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        this.projectFile = pf;
        initToolbar();
    }

    private void initToolbar() {
        JButton b;

        if (this.projectFile instanceof WorkSpace) {
            //delete
            b = new ButtonHQ();
            b.setOpaque(false);
            this.add(b);
            b.setToolTipText("Delete");
            b.setIcon(SystemResources.TOOLBAR_DELETE);
            //rotate
            b = new ButtonHQ();
            b.setOpaque(false);
            this.add(b);
            b.setToolTipText("Rotate");
            b.setIcon(SystemResources.TOOLBAR_ROTATE);
            //delete wire
            b = new ButtonHQ();
            b.setOpaque(false);
            this.add(b);
            b.setToolTipText("Delete all wires");
            b.setIcon(SystemResources.TOOLBAR_DELETE_WIRE);
            //align vertical
            b = new ButtonHQ();
            b.setOpaque(false);
            this.add(b);
            b.setToolTipText("Vertical Align");
            b.setIcon(SystemResources.TOOLBAR_ALIGN_VERTICAL);
            //align horisontal
            b = new ButtonHQ();
            b.setOpaque(false);
            this.add(b);
            b.setToolTipText("Horisontal Align");
            b.setIcon(SystemResources.TOOLBAR_ALIGN_HORISONTAL);
            //text
            b = new ButtonHQ();
            b.setOpaque(false);
            this.add(b);
            b.setToolTipText("Add text");
            b.setIcon(SystemResources.TOOLBAR_TXT);
        }
    }

}
