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
package logicSimulator;

import java.awt.Point;

public interface PFHandler {

    /**
     * Zoom rendering of component
     *
     * @param ration (int) if ration is positive than zoom out if is negative
     * than zoom in
     */
    public void zoom(int ration);

    /**
     * Return position of cursor on component
     *
     * @return
     */
    public Point getCursorPosition();
    
    /**
     * Repaint hanlder
     */
    public void repaintPF();
    
}
