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

/**
 *
 * @author Martin
 */
public class Propertie {

    //name of propertie
    private final String name;

    //string value of propertie
    private String value;

    //numeric value of propertie
    private int number;

    //is propertie of numeric type ?
    private boolean numeric;

    //type of propertie
    private Type type = Type.UNKNOWN;

    public enum Type {
        UNKNOWN, //unknown type of propertie
        COLOR, //color
        LOGIC, //true/false
        BITS, //number of bits  (1 - 32)
        INPUTS, //number of bits (2 - 17)
        FONT;   //font of text     
    }

    /**
     * Propertie
     *
     * @param name Name of propertie
     * @param value Value of propertie (String)
     */
    public Propertie(String name, String value) {
        this.name = name;
        this.value = value;
        this.numeric = false;
    }

    /**
     * Propertie
     *
     * @param name Name of propertie
     * @param number Value of propertie (Integer)
     */
    public Propertie(String name, int number) {
        this.name = name;
        this.number = number;
        this.numeric = true;
    }

    /**
     * Propertie
     *
     * @param name Name of propertie
     * @param value Value of propertie (String)
     * @param type Type of propertie
     */
    public Propertie(String name, String value, Type type) {
        this.name = name;
        this.value = value;
        this.numeric = false;
        this.type = type;
    }

    /**
     * Propertie
     *
     * @param name Name of propertie
     * @param number Value of propertie (Integer)
     * @param type Type of propertie
     */
    public Propertie(String name, int number, Type type) {
        this.name = name;
        this.number = number;
        this.numeric = true;
        this.type = type;
    }

    /**
     * Set type for propetie
     *
     * @param type New Type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Get type of propetie
     *
     * @return
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Get name
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get String value of propertie
     *
     * @return
     */
    public String getValueString() {
        if (this.numeric) {
            return this.number + "";
        } else {
            return this.value;
        }
    }

    /**
     * Get integer value of propertie
     *
     * @return
     * @throws NumberFormatException
     */
    public int getValueInt() throws NumberFormatException {
        if (this.numeric) {
            return this.number;
        }
        return Integer.parseInt(this.value);
    }

    /**
     * Set value (String)
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
        this.numeric = false;
    }

    /**
     * Set value (Integer)
     *
     * @param value
     */
    public void setValue(int value) {
        this.number = value;
        this.numeric = true;
    }

}
