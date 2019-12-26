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
     * Is project file opened (in some window)
     *
     * @return
     */
    public boolean isOpened();

    /**
     * Set open status (true -> component is opened, false -> component is
     * close)
     *
     * @param open
     */
    public void setOpened(boolean open);

    /**
     * Get component if exist
     *
     * @return
     */
    public Component getComp();

}
