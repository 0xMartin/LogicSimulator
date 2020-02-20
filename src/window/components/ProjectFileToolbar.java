/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.ProjectFile;
import logicSimulator.Tools;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.Text;
import logicSimulator.objects.wiring.Wire;
import logicSimulator.projectFile.HEXEditor;
import logicSimulator.ui.ButtonHQ;
import logicSimulator.ui.SystemResources;
import window.Linker;

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

    /**
     * Init toolbar
     */
    private void initToolbar() {
        if (this.projectFile instanceof WorkSpace) {
            initWorkspaceToolbar();
        } else if (this.projectFile instanceof ModuleEditor) {
            initModuleEditorToolbar();
        } else if (this.projectFile instanceof HEXEditor) {
            initHEXEditorToolbar();
        }
    }

    private String right_string = "";

    /**
     * Set text on right side of toolbar
     *
     * @param text
     */
    public void setRightString(String text) {
        this.right_string = text;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.right_string.length() != 0) {
            Graphics2D g2 = (Graphics2D) g;
            Tools.setHighQuality(g2);
            g2.setColor(Color.black);
            g2.setFont(new Font("consolas", Font.PLAIN, 14));
            g2.drawString(
                    this.right_string,
                    this.getWidth() - 10 - g.getFontMetrics().stringWidth(this.right_string),
                    this.getHeight() / 2 + Tools.centerYString(g.getFontMetrics())
            );
        }
    }

    private void initWorkspaceToolbar() {
        JButton b;

        //zoom in
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Zoom in");
        b.setIcon(SystemResources.TOOLBAR_ZOOM_IN);
        b.addActionListener((ActionEvent e) -> {
            WorkSpace w = (WorkSpace) this.projectFile;
            w.getHandler().zoom(-1);
        });

        //zoom out
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Zoom out");
        b.setIcon(SystemResources.TOOLBAR_ZOOM_OUT);
        b.addActionListener((ActionEvent e) -> {
            WorkSpace w = (WorkSpace) this.projectFile;
            w.getHandler().zoom(1);
        });

        //delete
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Delete");
        b.setIcon(SystemResources.TOOLBAR_DELETE);
        b.addActionListener((ActionEvent e) -> {
            //delete all wire in selection
            WorkSpace w = (WorkSpace) this.projectFile;
            w.deleteSelectedObjects();
            w.repaint();
        });

        //rotate
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Rotate");
        b.setIcon(SystemResources.TOOLBAR_ROTATE);
        b.addActionListener((ActionEvent e) -> {
            //delete all wire in selection
            WorkSpace w = (WorkSpace) this.projectFile;
            w.rotateSelectedObject();
            w.repaint();
        });

        //delete wire
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Delete all wires");
        b.setIcon(SystemResources.TOOLBAR_DELETE_WIRE);
        b.addActionListener((ActionEvent e) -> {
            //delete all wire in selection
            WorkSpace w = (WorkSpace) this.projectFile;
            for (int i = 0; i < w.getObjects().size(); i++) {
                WorkSpaceObject obj = w.getObjects().get(i);
                if (obj != null) {
                    if (obj.isSelected()) {
                        if (obj instanceof Wire) {
                            w.getObjects().remove(i);
                            i = -1;
                        }
                    }
                }
            }
            w.repaint();
        });

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
        b.addActionListener((ActionEvent e) -> {
            WorkSpace w = (WorkSpace) this.projectFile;
            List<WorkSpaceObject> list = new ArrayList<>();
            list.add(new Text(
                    "Text",
                    w.getHandler().getCursorPosition(),
                    new Font("tahoma", Font.PLAIN, 12)
            ));
            w.addNewObjects(list);
        });

    }

    private void initModuleEditorToolbar() {
        JButton b;

        //line
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Add Line");
        b.setIcon(SystemResources.TOOLBAR_LINE);
        b.addActionListener((ActionEvent e) -> {

        });

        //circle
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Add circle");
        b.setIcon(SystemResources.TOOLBAR_CIRCLE);
        b.addActionListener((ActionEvent e) -> {

        });

        //curve
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Add curve");
        b.setIcon(SystemResources.TOOLBAR_CURVE);
        b.addActionListener((ActionEvent e) -> {

        });

        //rectangle
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Add rectangle");
        b.setIcon(SystemResources.TOOLBAR_RECTANGLE);
        b.addActionListener((ActionEvent e) -> {

        });

        //text
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Add text");
        b.setIcon(SystemResources.TOOLBAR_TXT);
        b.addActionListener((ActionEvent e) -> {

        });

        //select all
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Select all");
        b.setIcon(SystemResources.TOOLBAR_SELECT_ALL);
        b.addActionListener((ActionEvent e) -> {
            ModuleEditor m = (ModuleEditor) this.projectFile;
            m.selecteAllGraphicsObjects();
            m.repaint();
        });

        //delete
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Delete");
        b.setIcon(SystemResources.TOOLBAR_DELETE);
        b.addActionListener((ActionEvent e) -> {
            ModuleEditor m = (ModuleEditor) this.projectFile;
            m.getModule().getModel().graphicsObjects = null;
            m.getHandler().repaintPF();
        });

    }

    private void initHEXEditorToolbar() {
        JButton b;

        //link
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("");
        b.setIcon(SystemResources.TOOLBAR_DELETE);
        b.addActionListener((ActionEvent e) -> {
            Linker linker = new Linker(
                    ((HexEditorArea) this.projectFile.getHandler()).getTranslator()
            );
            linker.setLocationRelativeTo(this.projectFile.getComp());
            linker.setAlwaysOnTop(true);
            linker.setVisible(true);
        });

        //clear
        b = new ButtonHQ();
        b.setOpaque(false);
        this.add(b);
        b.setToolTipText("Clear");
        b.setIcon(SystemResources.TOOLBAR_DELETE);
        b.addActionListener((ActionEvent e) -> {

        });
    }

}
