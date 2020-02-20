/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

/**
 *
 * @author Martin
 */
public interface Memory {

    /**
     * Get memory data array
     *
     * @return boolean[]
     */
    public boolean[] getData();

    /**
     * Get width of on cell in memory
     *
     * @return
     */
    public int getBitWidth();

}
