/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JButton;
import logicSimulator.Tools;

/**
 * JButton with better graphics
 *
 * @author Martin
 */
public class ButtonHQ extends JButton implements MouseListener {

    private int borderWidth = 0;

    /**
     * Create button with better rendering quality
     */
    public ButtonHQ() {
        super.addMouseListener(this);
        super.setPreferredSize(new Dimension(26, 26));
    }

    /**
     * Create button with better rendering quality
     *
     * @param icon Icon
     */
    public ButtonHQ(Icon icon) {
        super(icon);
        super.addMouseListener(this);
        super.setPreferredSize(new Dimension(26, 26));
    }

    /**
     * Set border width
     *
     * @param width Width of border
     */
    public void setBorderWidth(int width) {
        this.borderWidth = width;
        boolean opague = this.borderWidth <= 0;
        this.setOpaque(opague);
        this.setContentAreaFilled(opague);
        this.setFocusPainted(opague);
        this.setBorderPainted(opague);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Tools.setHighQuality(g2);

        if (this.borderWidth > 0) {
            g2.setStroke(new BasicStroke(this.borderWidth));
            int w = super.getWidth() - this.borderWidth * 2;
            int h = super.getHeight() - this.borderWidth * 2;
            //draw hover effect
            if (this.hover) {
                g2.setColor(new Color(40, 90, 220, 60));
                g2.fillRoundRect(
                        this.borderWidth,
                        this.borderWidth,
                        w, h,
                        w / 4, h / 4
                );
            }
            //draw component
            super.paintComponent((Graphics2D) g);
            //draw border
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(
                    this.borderWidth,
                    this.borderWidth,
                    w, h,
                    w / 4, h / 4
            );
        } else {
            //draw component without round board
            super.paintComponent((Graphics2D) g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private boolean hover = false;

    @Override
    public void mouseEntered(MouseEvent e) {
        this.hover = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.hover = false;
    }

}
