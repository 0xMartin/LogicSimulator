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
package logicSimulator.ui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import logicSimulator.Tools;

/**
 *
 * @author Martin
 */
public class LSButton extends JButton implements MouseListener {

    public LSButton() {
        super();
        this.addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        //render hints
        Tools.setHighQuality(g2);

        //draw bg
        g2.setColor(this.cursorIN ? Colors.COMPONENT_SELECTBG : Colors.COMPONENT_BACKGROUND);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        //text
        g2.setFont(this.getFont());
        g2.setColor(Colors.COMPONENT_FOREGROUND);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(this.getText(), g2);
        int x = (this.getWidth() - (int) r.getWidth()) / 2;
        int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
        g2.drawString(this.getText(), x, y);

        //image
        if (this.getIcon() != null) {
            BufferedImage bf1 = new BufferedImage(
                    this.getIcon().getIconWidth(),
                    this.getIcon().getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );
            this.getIcon().paintIcon(null, bf1.getGraphics(), 0, 0);
            BufferedImage img = Tools.resizeImage(
                    bf1,
                    (int) (this.getHeight() * 0.8f),
                    (int) (this.getHeight() * 0.8f)
            );
            g2.drawImage(
                    img,
                    x - 8 - (int) (this.getHeight() * 0.8f),
                    (int) (this.getHeight() * 0.1f),
                    this
            );
        }
    }

    private boolean cursorIN = false;

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        this.cursorIN = true;
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        this.cursorIN = false;
    }

}
