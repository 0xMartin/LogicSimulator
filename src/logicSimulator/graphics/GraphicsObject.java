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

import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Martin
 */
public interface GraphicsObject {
    
    /**
     * Draw graphics object
     * @param g2 2D Graphics context
     * @param xOffset X offset of rendering
     * @param yOffset Y offest of rendering
     */
    public void draw(Graphics2D g2, int xOffset, int yOffset);
    
    /**
     * Clone object
     * @return 
     */
    public GraphicsObject cloneObject();
    
    /**
     * Get all points of this graphics object
     * @return 
     */
    public Point.Double[] getPoints();
    
}
