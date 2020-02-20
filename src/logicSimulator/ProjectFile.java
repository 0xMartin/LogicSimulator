/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.Component;

/**
 * Project file (Workspace: logic circuits; ModuleEditor: Editing of module)
 *
 * @author Martin
 */
public interface ProjectFile {

    /**
     * Get project file mode
     *
     * @return
     */
    public PFMode getPFMode();


    /**
     * Get component if exist
     *
     * @return
     */
    public Component getComp();

    /**
     * Get handler of project file
     *
     * @return
     */
    public PFHandler getHandler();

    /**
     * Set visibility
     *
     * @param val true -> then project file is visible on tabbed pane (is
     * selected on tabbed pane)
     */
    public void setVisible(boolean val);

    /**
     * Is visible (selected) in tabbed pane
     *
     * @return
     */
    public boolean isVisible();

    /**
     * Get project
     *
     * @return
     */
    public Project getProject();

    /**
     * Select project file in project
     */
    public void selectInProject();

}
