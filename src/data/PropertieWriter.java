/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class PropertieWriter {

    private final String file;

    /**
     * Create propertie writer Syntax: name=value
     *
     * @param file Propertie file
     */
    public PropertieWriter(String file) {
        this.file = file;
    }

    /**
     * Write all properties to file
     *
     * @param propts Properties
     * @throws java.lang.Exception
     */
    public void writeFile(List<Propertie> propts) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.file));
        propts.stream().forEach((p) -> {
            try {
                writer.write(p.getName() + "=" + p.getValueString());
                writer.newLine();
            } catch (IOException ex) {
                Logger.getLogger(PropertieWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        writer.flush();
        writer.close();
    }

}
