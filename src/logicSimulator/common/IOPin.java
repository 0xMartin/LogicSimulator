/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import logicSimulator.ui.Colors;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import logicSimulator.objects.wiring.Wire;

/**
 * Pin for logic gates
 *
 * @author Martin
 */
public class IOPin implements Serializable {

    //pin mode: INPUT or OUTPUT
    public static enum MODE implements Serializable{
        INPUT, OUTPUT, IO;
    }

    public final IOPin.MODE mode;

    private boolean[] value, lastVal;

    protected Wire connectedWire;

    public String ID;

    private final Point.Double position;

    public boolean[] writeOnly = null;

    /**
     * Create io pin
     *
     * @param mode Mode of pin input or output
     * @param bits Number of bits (for bus)
     * @param id ID of pin
     * @param position Position of pin (in model)
     */
    public IOPin(IOPin.MODE mode, int bits, String id, Point.Double position) {
        this.mode = mode;
        this.value = new boolean[bits];
        this.lastVal = new boolean[bits];
        this.ID = id;
        this.position = position;
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

        //newValue and value must have same length !!DATABITS length of input pin automaticli adjust to incoming value!!
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
        System.arraycopy(newValue, 0, this.lastVal, 0, lastVal.length);

        //return 
        return changed;
    }

    /**
     * Write value on wire, all input pins values will be changeg
     */
    public void writeValue() {
        //update all input pins values
        if (this.mode != IOPin.MODE.OUTPUT) {
            return;
        }
        if (this.connectedWire != null) {
            //set value mode of wire
            this.connectedWire.setBinaryValue(
                    this.value.length == 1
                            ? (this.value[0] ? Wire.WValue.HIGH : Wire.WValue.LOW)
                            : Wire.WValue.BUS
            );
            //send value
            this.connectedWire.getPins().stream().forEach(pin -> {
                //write change only on input pin
                if (pin.mode == IOPin.MODE.INPUT) {
                    if (this.writeOnly == null) {
                        //send all
                        pin.setValue(this.value);
                    } else {
                        boolean[] val = pin.getValue();
                        //change only avaiable bits
                        for (int i = 0; i < this.writeOnly.length; i++) {
                            if (this.writeOnly[i]) {
                                val[i] = this.value[i];
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Set new value for all bits
     *
     * @param val New value
     */
    public void setValue(boolean val) {
        for (int j = 0; j < this.value.length; j++) {
            this.value[j] = val;
        }
        for (int j = 0; j < this.lastVal.length; j++) {
            this.lastVal[j] = val;
        }
    }

    public void render(Graphics2D g2, Point offSet) {
        Color c = g2.getColor();
        g2.setColor(Colors.IOPIN);
        g2.drawOval(
                (int) (this.position.x + offSet.x - 3),
                (int) (this.position.y + offSet.y - 3),
                5, 5
        );
        g2.setColor(c);
    }

    public IOPin cloneObject() {
        IOPin ret = new IOPin(
                this.mode,
                this.value.length,
                this.ID,
                new Point.Double(this.position.x, this.position.y)
        );
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
