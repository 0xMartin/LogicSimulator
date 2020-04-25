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
package logicSimulator.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.Tools;

/**
 * SerialDataDriver is usefull for comenponent that require serial data access
 * (one input is used for instruction and values)
 *
 * @author Martin
 */
public class SerialDataDriver {

    public static int SOME_VALUE = -1;

    //number of values that come after instruction (if is 0 than next data is command)
    private int VALUE_COUNT = 0;

    //id of curent instruction
    private int CURENT_INSTRUCTION_ID = -1;

    //list with actions
    private final List<SDDAction> actions = new ArrayList<>();

    /**
     * All data that come from input are added to this buffer, is used if you
     * hava 8 bit bus and you need to load 16 bit value (you load it in two
     * clock ticks)
     *
     * @return Boolean[]
     */
    private final List<Boolean> bitBuffer = new ArrayList<>();

    /**
     * Get bit buffer
     *
     * @return List<Boolean>
     */
    public List<Boolean> getBitBuffer() {
        return this.bitBuffer;
    }

    /**
     * Set number of values
     *
     * @param count Values count
     */
    public void setValuesCount(int count) {
        this.VALUE_COUNT = count;
    }

    /**
     * Get value of buffer in decimal format
     *
     * @return int
     */
    public int getBitBufferValue() {
        int dec = 0;
        for (int i = 0; i < this.bitBuffer.size(); i++) {
            if (this.bitBuffer.get(i)) {
                dec += Math.pow(2, i);
            }
        }
        return dec;
    }

    /**
     * Handle incoming data
     *
     * @param bits Array with bits
     * @return True -> some action was performed
     */
    public boolean handleValue(boolean[] bits) {
        //value in dec format of input data
        int decValue = Convert.bitsToInt(bits);

        //perform action (only one)
        for (SDDAction action : this.actions) {
            //dec value must be same as trig value of action or trig is set as some value
            if (action.trigValue == decValue || action.trigValue == SerialDataDriver.SOME_VALUE) {
                if (VALUE_COUNT == 0) {
                    //instruction
                    if (action instanceof InstructionAction) {
                        this.CURENT_INSTRUCTION_ID = action.trigValue;
                        this.VALUE_COUNT = ((InstructionAction) action).numberOfValues;
                        //perform
                        if (action.listener != null) {
                            action.listener.actionPerformed(new ActionEvent(this, 0, ""));
                        }
                        return true;
                    }
                } else {
                    //value
                    if (action instanceof ValueAction) {
                        if (((ValueAction) action).insTrigValue == this.CURENT_INSTRUCTION_ID) {
                            //add incoming data bits to bit buffer
                            for (boolean bit : bits) {
                                this.bitBuffer.add(bit);
                            }
                            //perform
                            if (action.listener != null) {
                                //id of action event = VALUE_COUNT;
                                action.listener.actionPerformed(new ActionEvent(this, this.VALUE_COUNT, ""));
                            }
                            //decrease number of waiting values
                            this.VALUE_COUNT--;
                            return true;
                        }
                    }
                }
            }
        }
        //if some action was performed
        return false;
    }

    /**
     * Instruction action is performed if handled data is equal to "trigValue"
     *
     * @param listener Action listener
     * @param trigValue Value that trig this action (this value is idetificator
     * for instruction, value listener use this ID)
     * @param numberOfValues Number of values that must be handled after
     * instruction
     */
    public void addInstructionListener(int trigValue, int numberOfValues, ActionListener listener) {
        this.actions.add(new InstructionAction(listener, trigValue, numberOfValues));
    }

    /**
     * Value action activate if instruction with "insTrigValue" was handled and
     * value for value listener is trigged (action event: ID = curent index of
     * value)
     *
     * @param listener Action listener
     * @param trigValue Value that trig this action
     * @param insTrigValue Trig value of instrucion that is connected with this
     * value action
     */
    public void addValueListener(int trigValue, int insTrigValue, ActionListener listener) {
        this.actions.add(new ValueAction(listener, trigValue, insTrigValue));
    }

    /**
     * Serial data driver action
     */
    private class SDDAction {

        public final ActionListener listener;
        public final int trigValue;

        public SDDAction(ActionListener listener, int trigValue) {
            this.listener = listener;
            this.trigValue = trigValue;
        }
    }

    /**
     * Action for instruction
     */
    private class InstructionAction extends SDDAction {

        public final int numberOfValues;

        /**
         * Instruction action is performed if handled data is equal to
         * "trigValue"
         *
         * @param listener Action listener
         * @param trigValue Value that trig this action
         * @param numberOfValues Number of values that must be handled after
         * instruction
         */
        public InstructionAction(ActionListener listener, int trigValue, int numberOfValues) {
            super(listener, trigValue);
            this.numberOfValues = numberOfValues;
        }

    }

    /**
     * Action for value
     */
    private class ValueAction extends SDDAction {

        public final int insTrigValue;

        /**
         * Value action activate if instruction with "instructionID" was handled
         * and value for activation of this action in hadled
         *
         * @param listener Action listener
         * @param trigValue Value that trig this action
         * @param insTrigValue Trig value of instrucion that is connected with
         * this value action
         */
        public ValueAction(ActionListener listener, int trigValue, int insTrigValue) {
            super(listener, trigValue);
            this.insTrigValue = insTrigValue;
        }
    }

}
