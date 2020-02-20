/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        BITS, //number of bits
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
