/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;
import logicSimulator.Tools;

/**
 * JButton with better graphics
 * @author Martin
 */
public class ButtonHQ extends JButton {

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Tools.setHighQuality(g2);
        super.paintComponent((Graphics2D) g);
    }

}
