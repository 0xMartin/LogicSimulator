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
import logicSimulator.ProjectFile;
import logicSimulator.Tools;

/**
 * Title of tabbed pane (close button, name, image of project file, docking
 * button)
 *
 * @author Martin
 */
public class TabbedPaneTitle extends JPanel {

    private final JLabel title;

    public TabbedPaneTitle(PFViewer tp, Component component) {
        this.setOpaque(false);
        
        //label
        this.title = new JLabel(tp.getTitleAt(tp.indexOfComponent(component)) + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Tools.setHighQuality(g2);
                super.paintComponent(g);
            }
        };

        //icon
        if (component instanceof WorkSpace) {
            this.title.setIcon(logicSimulator.ui.SystemResources.LWF_ICON);
        } else if (component instanceof ModuleEditor) {
            this.title.setIcon(logicSimulator.ui.SystemResources.MF_ICON);
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
                ((ProjectFile) component).getPFMode().OPENED = false;
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
