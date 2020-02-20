/*
 * Logic simlator
 * Author: Martin Krcma
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
