/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import java.io.Serializable;

/**
 *
 * @author Martin
 */
public class LinkASM implements Serializable {
 
    //Mnemonic of link and before transation "hex" 
    public String Mnemonic, Hex, Comment;
    
    public LinkASM(String Mnemonic, String Hex) {
        init(Mnemonic, Hex, "");
    }

    public LinkASM(String Mnemonic, String Hex, String Comment) {
        init(Mnemonic, Hex, Comment);
    }

    
    private void init(String Mnemonic, String Hex, String Comment) {
        this.Comment = Comment;
        this.Mnemonic = Mnemonic.toLowerCase();
        if (Hex.startsWith("0x")) {
            //value is in hex fromat
            this.Hex = Hex.substring(2);

        } else {
            //value is in bin format
            this.Hex = Integer.toString(Integer.parseInt(Mnemonic, 2), 16);
        }
    }

}
