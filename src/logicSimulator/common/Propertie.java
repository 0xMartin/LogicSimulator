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

    private final String name;
    private String value;
    private int number;
    private boolean numeric;

    public Propertie(String name, String value) {
        this.name = name;
        this.value = value;
        this.numeric = false;
    }

    public Propertie(String name, int number) {
        this.name = name;
        this.number = number;
        this.numeric = true;
    }

    public String getName() {
        return this.name;
    }

    public String getValueString() {
        if (this.numeric) {
            return this.number + "";
        } else {
            return this.value;
        }
    }

    public int getValueInt() throws NumberFormatException {
        if (this.numeric) {
            return this.number;
        }
        return Integer.parseInt(this.value);
    }

    public void setValue(String value) {
        this.value = value;
        this.numeric = false;
    }
    
    public void setValue(int value) {
        this.number = value;
        this.numeric = true;
    }

}
