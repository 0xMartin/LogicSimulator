/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import logicSimulator.ui.Colors;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import logicSimulator.objects.wiring.Wire;
import logicSimulator.ui.Fonts;

/**
 * Pin for logic gates
 *
 * @author Martin
 */
public class IOPin implements Serializable {

    //if is true than render label of pin
    public boolean VisibleName = false;

    //pin mode: INPUT, OUTPUT or IO
    public static enum MODE implements Serializable {
        INPUT, //input can be used only for bit receiving
        OUTPUT, //output is used for sending bits to another pins using wire connection
        IO, //this is input and output pin
        LINKER;     //for wire connecting using bridge
    }

    //label
    private String label = "";

    //mode of pin
    public final IOPin.MODE mode;

    //value of pin and last value
    private boolean[] value, lastVal;

    //wire connected to this io pin
    protected Wire connectedWire;

    //position of this pin
    private final Point.Double position;

    /**
     * If this is null than output pin write its value for all connected input
     * pins, if is not null than value will be writed only if in "writeOnly" is
     * on same position true
     */
    public boolean[] writeOnly = null;

    public String ID;

    //if is true then value of this io pin was changed 
    private boolean valueRecieved = false;

    /**
     * Create io pin
     *
     * @param mode Mode of pin input or output
     * @param bits Number of bits (for bus)
     * @param label Label of pin
     * @param position Position of pin (in model)
     */
    public IOPin(IOPin.MODE mode, int bits, String label, Point.Double position) {
        this.mode = mode;
        this.value = new boolean[bits];
        this.lastVal = new boolean[bits];
        this.label = label;
        this.position = position;
    }

    /**
     * Return true if value is recieved (then status will set to false)
     *
     * @return
     */
    public boolean valueRecieved() {
        boolean b = this.valueRecieved;
        this.valueRecieved = false;
        return b;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Connect IOPin to new wire, last will be disconnected
     *
     * @param wire
     */
    public void setWire(Wire wire) {
        this.connectedWire = wire;
    }

    public Point.Double getPosition() {
        return this.position;
    }

    /**
     * Read logic value from this io pin
     *
     * @return
     */
    public boolean[] getValue() {
        if (this.mode == MODE.INPUT) {
            if (this.connectedWire == null) {
                for (int i = 0; i < this.value.length; i++) {
                    this.value[i] = false;
                }
            }
        }
        return this.value;
    }

    /**
     * Set new logic value (only for output mode), all input iopins on same wire
     * will be updated on same value
     *
     * @param newValue New logic value
     * @return Return true if value was changed
     */
    public boolean setValue(boolean[] newValue) {

        //newValue and value must have same length !!DATABITS length of pin automaticli adjust to incoming value!!
        if (this.value.length != newValue.length) {
            this.value = new boolean[newValue.length];
            this.lastVal = new boolean[newValue.length];
        }

        //copy from newValue to value
        System.arraycopy(newValue, 0, this.value, 0, value.length);

        //value changed ?
        boolean changed = false;
        for (int i = 0; i < this.value.length; i++) {
            if (this.value[i] != this.lastVal[i]) {
                changed = true;
                break;
            }
        }

        //copy to last value (only for change detection)
        if (changed) {
            System.arraycopy(newValue, 0, this.lastVal, 0, lastVal.length);
        }

        //return 
        return changed;
    }

    /**
     * Write value on wire, all input and io pins values will be changed
     */
    public void writeValue() {
        if (this.mode != IOPin.MODE.OUTPUT || this.value.length == 0) {
            return;
        }
        //update values for all input and common pins 
        if (this.connectedWire != null) {
            //set value status of wire
            this.connectedWire.value = this.value.length > 1 ? -1 : (this.value[0] ? 1 : 0);
            //send value
            try {
                this.connectedWire.getPins().stream()
                        .filter((pin) -> !(pin == this))
                        .filter((pin) -> !(pin.mode == IOPin.MODE.OUTPUT))
                        .forEachOrdered((pin) -> {
                            //input pin
                            if (this.writeOnly == null) {
                                //send all
                                pin.setValue(this.value);
                            } else {
                                //send only selected bits
                                boolean[] val = pin.getValue();
                                //change only avaiable bits
                                for (int i = 0; i < this.writeOnly.length && i < val.length; i++) {
                                    if (this.writeOnly[i]) {
                                        val[i] = this.value[i];
                                    }
                                }
                            }
                            //set status "valueRecieved" on true (used for bridges)
                            pin.valueRecieved = true;
                        });
            } catch (Exception ex) {
                //call again (this call if some object connect or unconnect from wire in time of writing)
                writeValue();
            }
        }

    }

    /**
     * Set new value for all bits
     *
     * @param val New value
     * @return if true then value changed
     */
    public boolean setValue(boolean val) {
        //write to value
        for (int j = 0; j < this.value.length; j++) {
            this.value[j] = val;
        }
        //find differece
        boolean changed = false;
        for (int i = 0; i < this.value.length; i++) {
            if (this.value[i] != this.lastVal[i]) {
                changed = true;
                break;
            }
        }
        //write to last value
        if (changed) {
            for (int j = 0; j < this.lastVal.length; j++) {
                this.lastVal[j] = val;
            }
        }

        return changed;
    }

    public void render(Graphics2D g2, int xoff, int yoff) {
        //draw pin
        g2.setColor(Colors.IOPIN);
        g2.drawOval(
                (int) (this.position.x + xoff - 3),
                (int) (this.position.y + yoff - 3),
                5, 5
        );
        //draw label

        if (this.label.length() != 0 && this.VisibleName) {
            /**
             * Off set for label: if is on left side of model the draw it more
             * on left side in ride side is it same but on right, center line of
             * model is X=0
             *
             */
            g2.setFont(Fonts.IOPIN);
            g2.setColor(Colors.TEXT);
            int labelOffset = this.position.x < 0 ? - 9 - g2.getFontMetrics().stringWidth(this.label) : 9;
            //draw
            g2.drawString(
                    this.label,
                    (int) (this.position.x + xoff + labelOffset),
                    (int) (this.position.y + yoff - 9)
            );
        }
    }

    public IOPin cloneObject() {
        IOPin ret = new IOPin(
                this.mode,
                this.value.length,
                this.label,
                new Point.Double(this.position.x, this.position.y)
        );
        ret.label = this.label;
        if (this.writeOnly != null) {
            ret.writeOnly = new boolean[this.writeOnly.length];
            System.arraycopy(this.writeOnly, 0, ret.writeOnly, 0, this.writeOnly.length);
        }
        return ret;
    }

    public void changeBitWidth(int bits) {
        //resize last value
        this.lastVal = new boolean[bits];
        //resize value
        if (this.value == null) {
            this.value = new boolean[bits];
        } else {
            boolean[] b = new boolean[bits];
            for (int i = 0; i < this.value.length && i < b.length; i++) {
                b[i] = this.value[i];
            }
            this.value = b;
        }
    }

    public Wire getWire() {
        return this.connectedWire;
    }

}
