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
package window.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JWindow;
import logicSimulator.Tools;
import logicSimulator.ui.Colors;

/**
 * Button for docking (right left, left, right)
 *
 * @author Martin
 */
public class DockingButton extends JWindow {

    public static enum Type {
        LEFT_RIGHT, LEFT, RIGHT, CENTER;
    }

    private static final int BTNSIZE = 40;

    private Type type;

    public DockingButton(Component parent, Type type) {
        super();
        this.setAlwaysOnTop(true);
        this.setSize(new Dimension(DockingButton.BTNSIZE * 3 + 30, DockingButton.BTNSIZE + 10));
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(parent);
        this.type = type;
    }

    /**
     * Set type of button
     *
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Tools.setHighQuality(g2);
        int w = this.getWidth() / 2;
        int h = this.getHeight() / 2;
        switch (this.type) {
            case LEFT:
                left(g2, -BTNSIZE - 10 + w, h);
                center(g2, w, h);
                break;
            case RIGHT:
                right(g2, BTNSIZE + 10 + w, h);
                center(g2, w, h);
                break;
            case LEFT_RIGHT:
                right(g2, BTNSIZE + 10 + w, h);
                center(g2, w, h);
                left(g2, -BTNSIZE - 10 + w, h);
                break;
            case CENTER:
                center(g2, w, h);
                break;
        }
    }

    /**
     * Draw left arrow
     *
     * @param g2 Graphics 2D
     * @param x x position (center)
     * @param y y position (center)
     */
    private void left(Graphics2D g2, int x, int y) {
        g2.setColor(this.left ? Color.blue : Color.gray);
        g2.fillRoundRect(
                x - DockingButton.BTNSIZE / 2,
                y - DockingButton.BTNSIZE / 2,
                DockingButton.BTNSIZE / 2,
                DockingButton.BTNSIZE,
                DockingButton.BTNSIZE / 5,
                DockingButton.BTNSIZE / 5
        );
        g2.setColor(Colors.OBJECT);
        g2.fillPolygon(
                new int[]{
                    x + DockingButton.BTNSIZE / 2,
                    x + DockingButton.BTNSIZE / 4,
                    x + DockingButton.BTNSIZE / 2
                },
                new int[]{
                    y + DockingButton.BTNSIZE / 4,
                    y,
                    y - DockingButton.BTNSIZE / 4
                },
                3
        );
        g2.drawRoundRect(
                x - DockingButton.BTNSIZE / 2,
                y - DockingButton.BTNSIZE / 2,
                DockingButton.BTNSIZE,
                DockingButton.BTNSIZE,
                DockingButton.BTNSIZE / 5,
                DockingButton.BTNSIZE / 5
        );
    }

    /**
     * Draw right arrow
     *
     * @param g2 Graphics 2D
     * @param x x position (center)
     * @param y y position (center)
     */
    private void right(Graphics2D g2, int x, int y) {
        g2.setColor(this.right ? Color.blue : Color.gray);
        g2.fillRoundRect(
                x,
                y - DockingButton.BTNSIZE / 2,
                DockingButton.BTNSIZE / 2,
                DockingButton.BTNSIZE,
                DockingButton.BTNSIZE / 5,
                DockingButton.BTNSIZE / 5
        );
        g2.setColor(Colors.OBJECT);
        g2.fillPolygon(
                new int[]{
                    x - DockingButton.BTNSIZE / 2,
                    x - DockingButton.BTNSIZE / 4,
                    x - DockingButton.BTNSIZE / 2
                },
                new int[]{
                    y + DockingButton.BTNSIZE / 4,
                    y,
                    y - DockingButton.BTNSIZE / 4
                },
                3
        );
        g2.drawRoundRect(
                x - DockingButton.BTNSIZE / 2,
                y - DockingButton.BTNSIZE / 2,
                DockingButton.BTNSIZE,
                DockingButton.BTNSIZE,
                DockingButton.BTNSIZE / 5,
                DockingButton.BTNSIZE / 5
        );
    }

    /**
     * Draw center
     *
     * @param g2 Graphics 2D
     * @param x x position (center)
     * @param y y position (center)
     */
    private void center(Graphics2D g2, int x, int y) {
        g2.setColor(this.center ? Color.blue : Color.gray);
        g2.fillRoundRect(
                (int) (x - DockingButton.BTNSIZE * 0.4),
                (int) (y - DockingButton.BTNSIZE * 0.4),
                (int) (DockingButton.BTNSIZE * 0.8),
                (int) (DockingButton.BTNSIZE * 0.8),
                DockingButton.BTNSIZE / 5,
                DockingButton.BTNSIZE / 5);
        g2.setColor(Colors.OBJECT);
        g2.drawRoundRect(
                x - DockingButton.BTNSIZE / 2,
                y - DockingButton.BTNSIZE / 2,
                DockingButton.BTNSIZE,
                DockingButton.BTNSIZE,
                DockingButton.BTNSIZE / 5,
                DockingButton.BTNSIZE / 5
        );
    }

    private boolean left = false, center = true, right = false;

    /**
     * Intersect with buttons
     *
     * @param c Cursor position on screen
     */
    int i = 0;

    public void intersect(Point c) {
        if (this.i < 10) {
            this.i++;
        } else {
            this.i = 0;
            int x = this.getLocation().x;
            int y = this.getLocation().y;
            if (c.y > y && c.y < y + this.getHeight()) {
                //left
                this.left = c.x > x && c.x < x + this.getWidth() / 3;
                //right
                this.right = c.x > x + 2 * this.getWidth() / 3 && c.x < x + this.getWidth();
                //center
                this.center = !this.left && !this.right || this.type == Type.CENTER;
                this.repaint();
            }
        }
    }

    /**
     * Get selecte button: -1 left, 0 center, 1 right
     *
     * @return (int)
     */
    public int getButton() {
        if (this.right) {
            return 1;
        } else if (this.left) {
            return -1;
        }
        return 0;
    }

    /**
     * Reset selection of button
     */
    public void resetSelect() {
        this.left = false;
        this.center = true;
        this.right = false;
    }

}
