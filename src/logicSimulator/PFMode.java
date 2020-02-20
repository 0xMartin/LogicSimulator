/*
 * Logic simlator
 * Author: Martin Krcma
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
