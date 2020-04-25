/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicSimulator.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class PropertieReader {

    public static enum ID {
        WINDOW, COMPUTING, PROJECT, PLUGINS, SETTINGS;
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
    public List<Propertie> readFile() throws Exception {
        List<Propertie> ret = new ArrayList<>();
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
