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

/**
 *
 * @author Martin
 */
public class PFMode {

    public boolean OPENED = false;

    public boolean VISIBLE = false;

    public boolean LEFT_SIDE = true;

    public PFMode(boolean opened, boolean visible, boolean left_side) {
        this.OPENED = opened;
        this.VISIBLE = visible;
        this.LEFT_SIDE = left_side;
    }

}
