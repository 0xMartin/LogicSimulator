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
import java.io.FileReader;
import java.util.LinkedList;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class PropertieReader {

    public static enum ID {
        WINDOW, COMPUTING, PROJECT, PLUGINS, SETTINGS, SERIAL_PORT;
    }

    private PropertieReader.ID id;

    private final String file;

    /**
     * Create propertie reader Syntax: name=value
     *
     * @param file Propertie file
     */
    public PropertieReader(String file) {
        this.file = file;
    }

    public PropertieReader(String file, PropertieReader.ID id) {
        this.file = file;
        this.id = id;
    }

    /**
     * Read propertie file and return all properties in list
     *
     * @return
     * @throws java.lang.Exception
     */
    public LinkedList<Propertie> readFile() throws Exception {
        LinkedList<Propertie> ret = new LinkedList<>();
        //read all propts and each add to the list
        BufferedReader reader = new BufferedReader(new FileReader(this.file));
        String line;
        String[] propt;
        while ((line = reader.readLine()) != null) {
            propt = line.split("=");
            try {
                ret.add(new Propertie(propt[0], propt[1]));
            } catch (Exception ex) {

            }
        }
        reader.close();
        return ret;
    }

    public PropertieReader.ID getID() {
        return this.id;
    }

    public static PropertieReader getWithID(PropertieReader[] list, PropertieReader.ID id) {
        if (list == null) {
            return null;
        }
        for (PropertieReader pr : list) {
            if (pr.getID() == id) {
                return pr;
            }
        }
        return null;
    }

}
