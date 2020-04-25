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
package logicSimulator.common;

import java.awt.Component;
import java.awt.Point;
import logicSimulator.Project;

/**
 *
 * @author Martin
 */
public interface ClickAction {

    /**
     * Change value of object with click action
     *
     * @param cursor Position of cursor
     * @param parent Parent component for dialogs
     * @param project Project
     */
    public void changeValue(Point cursor, Component parent, Project project);

}
