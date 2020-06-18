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
package logicSimulator.graphics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.Serializable;
import logicSimulator.Tools;

/**
 *
 * @author Martin
 */
public class GString implements Serializable, GraphicsObject {

    //string
    private String str;

    //rotation of string
    private int rotation = 0;

    //font of string
    private transient Font font;

    //position of string
    public final Point2D.Double position;

    //half width and height of characters
    private int height;

    public GString(Point.Double pos, int height, String str) {
        this.height = height;
        this.position = pos;
        this.font = logicSimulator.ui.Fonts.MEDIUM.deriveFont((float) height);
        this.str = str;
    }

    /**
     * Set string
     *
     * @param str String
     */
    public void setString(String str) {
        this.str = str;
    }

    /**
     * Get string
     *
     * @return String
     */
    public String getString() {
        return this.str;
    }

    /**
     * Add key code (char or digit or space)
     *
     * @param evt KeyEvent
     */
    public void addKey(KeyEvent evt) {
        if (Character.isAlphabetic(evt.getKeyChar())
                || Character.isDigit(evt.getKeyChar())
                || evt.getKeyCode() == KeyEvent.VK_SPACE) {
            //add char or digit
            this.str += evt.getKeyChar();
        } else if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            //remove one character
            this.str = this.str.substring(0, this.str.length() - 1);
        }
    }

    /**
     * Set rotation of string
     *
     * @param rot (int) rotation: 0 - 0, 1 - 90, 2 - 180, 3 - 270, ...
     */
    public void setRotation(int rot) {
        this.rotation = rot;
    }

    /**
     * Set height of characters in string
     *
     * @param height Height of character (4-200)
     */
    public void setHeight(int height) {
        if (height > 5 && height < 100) {
            this.height = height;
            this.font = logicSimulator.ui.Fonts.MEDIUM.deriveFont((float) height);
        }
    }
    
    /**
     * Get font height
     * @return (int)
     */
    public int getHeight() {
        return this.height;
    }

    @Override
    public void draw(Graphics2D g2, int xOffset, int yOffset) {
        if (this.font == null) {
            this.font = logicSimulator.ui.Fonts.MEDIUM.deriveFont((float) height);
        }

        Font temp = g2.getFont();
        g2.setFont(this.font);

        int x = (int) this.position.x + xOffset;
        int y = (int) this.position.y + yOffset + Tools.centerYString(g2.getFontMetrics());

        g2.rotate(this.rotation * Math.PI / 2, x, y);
        g2.drawString(this.str, x, y);
        g2.rotate(-this.rotation * Math.PI / 2, x, y);

        g2.setFont(temp);
    }

    @Override
    public GraphicsObject cloneObject() {
        return new GString(
                Tools.copy(this.position),
                this.height,
                this.str
        );
    }

    @Override
    public Point2D.Double[] getPoints() {
        return new Point2D.Double[]{this.position};
    }

}
