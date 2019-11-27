/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.timing;

/**
 *
 * @author Martin
 */
public class ConstantTimeLoop {

    //ticks per on second
    private int ticksPerSecond;

    //ns for one tick
    private double perTick;

    //last time for tickCheck()
    private double last = System.nanoTime();
    //tick count for tickCheck()
    private double ticks = 0;

    /**
     * Constant time loop this hold constant looping
     *
     * @param ticksPerSecond Ticks per one second
     */
    public ConstantTimeLoop(int ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
        updatePerTick();
    }

    public void setTicksPerSecond(int value) {
        this.ticksPerSecond = value;
        updatePerTick();
    }

    public int getTicksPerSecond() {
        return this.ticksPerSecond;
    }

    private void updatePerTick() {
        this.perTick = this.ticksPerSecond / 1e9;
    }

    public void reset() {
        this.last = System.nanoTime();
        this.ticks = 0;
    }

    /**
     * Stop = false -> running
     */
    private boolean stop = true;
    
    public boolean isStoped(){
        return this.stop;
    }
    
    /**
     * Stop loop
     */
    public void stop() {
        this.stop = true;
    }
    
    /**
     * Run the loop
     */
    public void run(){
        this.stop = false;
    }

    /**
     * If is true then do one loop tick
     *
     * @return
     */
    public boolean tickCheck() {
        if(this.stop){
            return false;
        }
        double now = System.nanoTime();
        this.ticks += (now - this.last) * this.perTick;
        this.last = now;
        if (this.ticks >= 1.0) {
            this.ticks--;
            return true;
        }
        return false;
    }

}
