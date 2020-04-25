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

import java.awt.Color;

/**
 *
 * @author Martin
 */
public class Colors {

    //workspace
    public static Color GRID = Color.LIGHT_GRAY;
    public static Color BACKGROUND = Color.WHITE;
    public static Color ERROR = Color.RED;
    //edit
    public static Color SELECT_RECT = Color.RED;
    public static Color SELECT_RECT2 = Color.BLUE;
    //objects
    public static Color OBJECT = Color.BLACK;
    public static Color IOPIN = Color.BLUE;
    public static Color IOPIN_BUS = new Color(153, 0, 153);
    public static Color WIRE_1 = new Color(0, 200, 0);
    public static Color WIRE_0 = new Color(70, 120, 70);
    public static Color WIRE_BUS = Color.BLACK;
    public static Color TEXT = Color.BLACK;

    //module editor
    public static Color ME_GRID = Color.GRAY;
    public static Color ME_BACKGROUND = Color.WHITE;
    public static Color ME_MODEL = Color.BLACK;
    public static Color ME_DRAG = Color.GRAY;
    public static Color ME_CURSORCROSS = Color.BLUE;
    public static Color ME_CENTER = Color.RED;

    //UI
    public static Color COMPONENT_FOREGROUND = Color.WHITE;
    public static Color COMPONENT_BACKGROUND = new Color(40, 40, 40, 130);
    public static Color COMPONENT_SELECTBG = new Color(40, 40, 40, 170);

}
