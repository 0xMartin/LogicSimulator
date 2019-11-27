/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.ui;

import java.awt.Color;

/**
 *
 * @author Martin
 */
public class Colors {

    //workspace
    public static Color GRID = Color.GRAY;
    public static Color BACKGROUND = Color.WHITE;
    public static Color ERROR = Color.RED;

    //edit
    public static Color SELECT_RECT = Color.RED;
    public static Color SELECT_RECT2 = Color.BLUE;
    public static Color DRAG_OBJ = Color.GRAY;

    //objects
    public static Color GATE = Color.BLACK;
    public static Color IOPIN = Color.BLUE;
    public static Color WIRE_1 = new Color(0, 200, 0);
    public static Color WIRE_0 = new Color(70, 120, 70);
    public static Color WIRE_BUS = Color.BLACK;
    public static Color TEXT = Color.CYAN;
    
    //UI
    public static Color COMPONENT_FOREGROUND = Color.WHITE;
    public static Color COMPONENT_BACKGROUND = new Color(40, 40, 40, 130);
    public static Color COMPONENT_SELECTBG = new Color(40, 40, 40, 170);

}
