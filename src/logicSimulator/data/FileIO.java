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
package logicSimulator.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Martin
 */
public class FileIO {

    /**
     * Write object to file
     * @param file File
     * @param data Object
     * @throws IOException 
     */
    public static void writeObject(File file, Object data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream objectOut = new ObjectOutputStream(fos)) {
            objectOut.writeObject(data);
            objectOut.flush();
        }
    }

    /**
     * Read object from file
     * @param file File
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public static Object readObject(File file) throws IOException, ClassNotFoundException {
        Object obj;
        try (FileInputStream fis = new FileInputStream(file);
                ObjectInputStream objectIn = new ObjectInputStream(fis)) {
            obj = objectIn.readObject();
        }
        return obj;
    }

    /**
     * Write text to file
     * @param file File for text
     * @param text Text
     * @throws IOException 
     */
    public static void writeText(File file, String text) throws IOException {
        try (FileWriter fw = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fw)) {
            writer.write(text);
            writer.flush();
        }
    }

    /**
     * Read text from file
     * @param file File with text
     * @return 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public static String readText(File file) throws IOException, ClassNotFoundException {
        String text = "";
        try (FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr)) {
            String line;
            while((line = reader.readLine()) != null){
                text += line + '\n';
            }
        }
        return text;
    }

}
