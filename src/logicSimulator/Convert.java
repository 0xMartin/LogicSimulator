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
package logicSimulator;

/**
 *
 * @author Martin
 */
public class Convert {

    /**
     * Convert byte to array of 8 bits
     *
     * @param B Byte
     * @return Array with 8 bits => boolean[8]
     */
    public static boolean[] byteToBits(byte B) {
        return new boolean[]{
            (B & 0x1) == 1,
            (B >> 1 & 0x1) == 1,
            (B >> 2 & 0x1) == 1,
            (B >> 3 & 0x1) == 1,
            (B >> 4 & 0x1) == 1,
            (B >> 5 & 0x1) == 1,
            (B >> 6 & 0x1) == 1,
            (B >> 7 & 0x1) == 1
        };
    }

    public static boolean[] charToBits(char c) {
        return Convert.byteToBits((byte) c);
    }

    /**
     * Convert int to two bytes
     *
     * @param val int value
     * @return byte[0] - lower, byte[1] - heigher
     */
    public static byte[] intToTwoBytes(int val) {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) (val & 0xff);
        bytes[1] = (byte) (val >> 8 & 0xff);

        return bytes;
    }

    /**
     * Convert bit array to byte
     *
     * @param bits Array with bits
     * @return
     */
    public static byte bitsToByte(boolean[] bits) {
        byte B = (byte) 0x0;
        for (int i = 0; i < 8 && i < bits.length; i++) {
            if (bits[i]) {
                B += 1 << i;
            }
        }
        return B;
    }

    /**
     * Convert number in hex format to byte
     *
     * @param hex Hex number: from 0 to ff
     * @return Byte
     */
    public static byte hexToByte(String hex) {
        //hex to bin
        String bin = Integer.toString(Integer.parseInt(hex, 16), 2);

        //bin to byte
        return binToByte(bin);
    }

    /**
     * Convert number (String )in bin format to byte
     *
     * @param bin Binary number: from 0 to 1111 1111
     * @return Byte
     */
    public static byte binToByte(String bin) {
        byte B = 0x0;
        for (int i = Math.min(7, bin.length() - 1), index = 0; i >= 0; i--, index++) {
            if (bin.charAt(i) == '1') {
                B += 1 << index;
            }
        }
        return B;
    }

    /**
     * Convert number to hex digit (0 <-> Z)
     *
     * @param number decimal int
     * @return char
     */
    public static char toHexDigit(int number) {
        if (number < 10) {
            return (char) (number + 48);
        } else {
            return (char) (number + 55);
        }
    }

    /**
     * Convert to byte to hex
     *
     * @param B Byte
     * @return Hex number: from 0x0 to 0xff
     */
    public static String byteToHex(byte B) {
        String hex = "";
        int val = B & 0xFF;
        hex += Convert.toHexDigit(val >>> 4);
        hex += Convert.toHexDigit(val & 0x0F);
        return hex;
    }

    /**
     * Convert hex to array of bits (in binary format)
     *
     * @param hex Hex number (String)
     * @return Array of bits (boolean)
     */
    public static boolean[] hexToBitArray(String hex) {
        //from hex string to bin string
        String str = Integer.toString(Integer.parseInt(hex, 16), 2);

        //from bin string to bit array
        boolean[] bin = new boolean[str.length()];
        for (int i = 0; i < bin.length; i++) {
            bin[i] = str.charAt(str.length() - i - 1) == '1';
        }
        return bin;
    }

    /**
     * Convert bit array to hex string
     *
     * @param bin Array with bits (booleans)
     * @return
     */
    public static String bitsToHex(boolean[] bin) {
        String hex = "";

        int segment = 0;
        for (int i = 0, shift = 0; i < bin.length; i++, shift++) {
            //if boolean "bit" on "i" postion is true than to buffer add width from 4 bit segment
            if (bin[i]) {
                segment += 1 << shift;
            }
            //if 4 bits apply theyr width to segment the conver segment value to hex char
            if (shift == 3 || i + 1 == bin.length) {
                hex = toHexDigit(segment) + hex;
                segment = 0;
                shift = -1;
            }
        }

        return Convert.removeFirstZeros(hex);
    }

    /**
     * Convert int to hex string
     *
     * @param dec Decimal number 32 bits (int)
     * @return Hex number (String)
     */
    public static String intToHex(int dec) {
        String hex = "";

        int buffer = 0;
        for (int i = 0, shift = 0; i < 32; i++, shift++) {
            //if bit form 32 bit "dec" on "i" postion is 1 than to buffer add width from 4 bit segment
            if ((dec >> i & 0x1) == 1) {
                buffer += 1 << shift;
            }
            //if 4 bits apply theyr width to segment the conver segment value to hex char
            if (shift == 3) {
                hex = toHexDigit(buffer) + hex;
                buffer = 0;
                shift = -1;
            }
        }

        return Convert.removeFirstZeros(hex);
    }

    /**
     * Convert binary number to int, on 0 index of array is bin with lower value
     *
     * @param bin Binary number
     * @return 32 bits value (int)
     */
    public static int bitsToInt(boolean[] bin) {
        int dec = 0;
        for (int i = 0; i < bin.length && i < 32; i++) {
            if (bin[i]) {
                dec += 1 << i;
            }
        }
        return dec;
    }

    /**
     * Convert binary number to long, on 0 index of array is bin with lower
     * value
     *
     * @param bin Binary number
     * @return 64 bits value (long)
     */
    public static long bitsToLong(boolean[] bin) {
        long dec = 0;
        for (int i = 0; i < bin.length && i < 64; i++) {
            if (bin[i]) {
                dec += 1 << i;
            }
        }
        return dec;
    }

    /**
     * Convert 32 bit value (int) to bits (boolean[])
     *
     * @param number 32 bit number
     * @param length Number of bits
     * @return Bits
     */
    public static boolean[] intToBits(int number, short length) {
        boolean[] ret = new boolean[length];
        for (int i = 0; i < length; i++) {
            ret[i] = (number >> i & 0x1) == 1;
        }
        return ret;
    }

    /**
     * Remove first unnecessary zeros
     *
     * @param bin Number
     * @return
     */
    private static String removeFirstZeros(String bin) {
        String ret = "";
        boolean add = false;
        for (int i = 0; i < bin.length(); i++) {
            if (bin.charAt(i) != '0') {
                add = true;
            }
            if (add) {
                ret += bin.charAt(i);
            }
        }
        //if is empty than must be 0
        if (ret.length() == 0) {
            ret = "0";
        }
        return ret;
    }

}
