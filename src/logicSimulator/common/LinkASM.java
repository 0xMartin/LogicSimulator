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
