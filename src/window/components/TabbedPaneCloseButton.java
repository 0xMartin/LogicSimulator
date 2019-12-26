/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import logicSimulator.WorkSpace;
import logicSimulator.ModuleEditor;
import logicSimulator.ProjectFile;

/**
 * Add this button using command "setTabComponentAt" and the this button can be
 * able to remove (close) component from tabbedPane
 *
 * @author Martin
 */
public class TabbedPaneCloseButton extends JPanel {

    private final JLabel title;

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public TabbedPaneCloseButton(JTabbedPane tp, Component component) {
        this.setOpaque(false);
        this.title = new JLabel(
                tp.getTitleAt(tp.indexOfComponent(component)) + "  "
        );
        //set font as bold
        this.title.setFont(new Font(
                this.title.getFont().getFamily(),
                Font.BOLD,
                this.title.getFont().getSize()
        ));
        //set color
        if (component instanceof WorkSpace) {
            this.title.setForeground(Color.BLACK);
        } else if (component instanceof ModuleEditor) {
            this.title.setForeground(Color.RED);
        }
        this.add(title);
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
            tp.remove(component);
            if (component != null) {
                ((ProjectFile) component).setOpened(false);
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

}
