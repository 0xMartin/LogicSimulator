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
package logicSimulator.objects;

import java.awt.BasicStroke;
import logicSimulator.ui.Colors;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import logicSimulator.ExceptionLogger;
import logicSimulator.objects.wiring.Wire;
import logicSimulator.ui.Fonts;

/**
 * Pin for logic gates
 *
 * @author Martin
 */
public class IOPin implements Serializable {

    //position of this pin
    private final Point.Double position;

    //pin mode: INPUT, OUTPUT or IO
    public static enum MODE implements Serializable {
        INPUT, //input can be used only for bit receiving
        OUTPUT, //output is used for sending bits to another pins using wire connection
        IO, //this is input and output pin
        LINKER;     //for wire connecting using bridge
    }

    //label of this pin
    private String label = "";

    //if is true than render label of pin
    public boolean VisibleLabel = false;

    //hover "same as in hmtl" -> display round oval around pin
    public boolean VisbileHover = false;

    //draw clk symbol, can use if pin is on left or right side of component
    public boolean drawClkSymbol = false;

    //mode of pin
    public final IOPin.MODE mode;

    //value of pin and last value
    private boolean[] value, lastVal;

    //wire connected to this io pin
    protected Wire connectedWire;

    /**
     * If this is null than output pin write its value for all connected input
     * pins, if is not null than value will be writed only if in "writeOnly" is
     * on same position true
     */
    public boolean[] writeOnly = null;

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
     * Set label of pin
     *
     * @return String
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Get label of pin
     *
     * @param label String
     */
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

    /**
     * Get position of pin (reference)
     *
     * @return Point.Double
     */
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

        /**
         * newValue and value must have same length !!DATABITS length of pin
         * automaticli adjust to incoming value!!
         */
        /*
        if (this.value.length != newValue.length) {
            this.value = new boolean[newValue.length];
            this.lastVal = new boolean[newValue.length];
        }
         */
        //copy bits from newValue to value buffer
        System.arraycopy(newValue, 0, this.value, 0, Math.min(newValue.length, value.length));

        //value changed ?
        boolean changed = false;
        for (int i = 0; i < this.value.length; i++) {
            if (this.value[i] != this.lastVal[i]) {
                changed = true;
                break;
            }
        }

        //copy to all bits from value to last value buffer (only if are different)
        if (changed) {
            System.arraycopy(this.value, 0, this.lastVal, 0, lastVal.length);
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
                        });
            } catch (Exception ex) {
                ExceptionLogger.getInstance().logException(ex);
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

    /**
     * Render pin
     *
     * @param g2 Graphics context
     * @param xoff X offset
     * @param yoff Y offset
     */
    public void render(Graphics2D g2, int xoff, int yoff) {

        //draw pin
        g2.setColor(this.value.length == 1 ? Colors.IOPIN : Colors.IOPIN_BUS);
        int x = (int) (this.position.x + xoff);
        int y = (int) (this.position.y + yoff);
        g2.drawOval(x - 2, y - 2, 4, 4);

        //draw clk symbol
        if (this.drawClkSymbol) {
            int side = this.position.x < 0 ? 1 : -1;
            g2.setColor(Colors.OBJECT);
            g2.drawLine(x + side, y + 6 * side, x + 8 * side, y);
            g2.drawLine(x + side, y - 6 * side, x + 8 * side, y);
        }

        //draw hover oval
        if (this.VisbileHover) {
            g2.setColor(Colors.SELECT_RECT);
            g2.drawOval((int) this.position.x + xoff - 5, (int) this.position.y + yoff - 5, 10, 10);
        }

        //draw label of pin
        String lab = this.label + " : " + this.value.length;
        if (this.VisibleLabel) {
            /**
             * Off set for label: if is on left side of model the draw it more
             * on left side in ride side is it same but on right, center line of
             * model is X=0
             *
             */
            g2.setFont(Fonts.SMALL);
            int labelOffsetX = this.position.x < 0 ? - 9 - g2.getFontMetrics().stringWidth(lab) : 9;
            int labelOffsetY = this.position.y > 0 ? g2.getFontMetrics().getHeight() + 14 : -5;
            int xp = (int) (this.position.x + xoff + labelOffsetX);
            int yp = (int) (this.position.y + yoff + labelOffsetY - 9);

            //draw rectangle       
            g2.setColor(Colors.WIRE_1);
            g2.fillRoundRect(xp - 3, yp - g2.getFontMetrics().getHeight(),
                    g2.getFontMetrics().stringWidth(lab) + 6,
                    g2.getFontMetrics().getHeight() + 6, 3, 3
            );
            g2.setColor(Colors.TEXT);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(xp - 3, yp - g2.getFontMetrics().getHeight(),
                    g2.getFontMetrics().stringWidth(lab) + 6,
                    g2.getFontMetrics().getHeight() + 6, 3, 3
            );

            //draw label
            g2.drawString(lab, xp, yp);
        }
    }

    /**
     * Clone this pin
     *
     * @return IOPin
     */
    public IOPin cloneObject() {
        IOPin ret = new IOPin(
                this.mode,
                this.value.length,
                this.label,
                new Point.Double(this.position.x, this.position.y)
        );
        ret.label = this.label;
        ret.drawClkSymbol = this.drawClkSymbol;
        if (this.writeOnly != null) {
            ret.writeOnly = new boolean[this.writeOnly.length];
            System.arraycopy(this.writeOnly, 0, ret.writeOnly, 0, this.writeOnly.length);
        }
        return ret;
    }

    /**
     * Change width of pin buffer
     *
     * @param bits New bit width of bin buffer
     */
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

    /**
     * Get connected wire
     *
     * @return Wire
     */
    public Wire getWire() {
        return this.connectedWire;
    }

}
