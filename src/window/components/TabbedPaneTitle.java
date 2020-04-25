/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import logicSimulator.PFViewer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.projectFile.DocumentationEditor;
import logicSimulator.ProjectFile;
import logicSimulator.Tools;
import logicSimulator.projectFile.HexEditor;

/**
 * Title of tabbed pane (close button, name, image of project file, docking
 * button)
 *
 * @author Martin
 */
public class TabbedPaneTitle extends JPanel {

    private final JLabel title;

    private final String defaultTitle;

    /**
     * Set UI for title as unselected "normal"
     */
    public final void unSelectedUI() {
        this.title.setText(this.defaultTitle + "   ");
        this.title.setForeground(Color.black);
    }

    /**
     * Set UI for title as selected
     */
    public final void selectedUI() {
        this.title.setText("[" + this.defaultTitle + "]  ");
        this.title.setForeground(Color.red);
    }

    public TabbedPaneTitle(PFViewer tp, Component component) {
        super.setOpaque(false);

        this.defaultTitle = tp.getTitleAt(tp.indexOfComponent(component));

        //label
        this.title = new JLabel(" ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Tools.setHighQuality(g2);
                super.paintComponent(g);
            }
        };

        this.title.setFont(new Font(
                this.title.getFont().getFamily(),
                Font.BOLD,
                this.title.getFont().getSize()
        ));

        this.unSelectedUI();

        //icon
        if (component instanceof WorkSpace) {
            this.title.setIcon(logicSimulator.ui.SystemResources.LWF_ICON);
        } else if (component instanceof ModuleEditor) {
            this.title.setIcon(logicSimulator.ui.SystemResources.MF_ICON);
        }else if (component instanceof HexEditor) {
            this.title.setIcon(logicSimulator.ui.SystemResources.HEF_ICON);
        }else if (component instanceof DocumentationEditor) {
            this.title.setIcon(logicSimulator.ui.SystemResources.DF_ICON);
        }

        //set font as bold
        this.title.setFont(new Font(
                this.title.getFont().getFamily(),
                Font.BOLD,
                this.title.getFont().getSize()
        ));

        this.add(this.title);

        //button
        JButton button = new JButton(" × ");
        button.setForeground(Color.GRAY);
        button.setBackground(Color.RED);
        button.setContentAreaFilled(false);
        button.setBorder(null);
        button.setOpaque(false);
        this.add(button);
        button.addActionListener((ActionEvent evt) -> {
            //remove component from tabbed pane and set as closed
            if (component != null) {
                ProjectFile pf = (ProjectFile) component;
                pf.getPFMode().OPENED = false;
                pf.getPFMode().VISIBLE = false;
                tp.remove(component);
                tp.getDockingPanel().refreshLayout();
            }
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setForeground(Color.WHITE);
                button.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setForeground(Color.GRAY);
                button.setOpaque(false);
            }
        });
    }

    /**
     * Set title of tabbed pane
     *
     * @param title Title
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }

}
