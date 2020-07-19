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

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logicSimulator.ExceptionLogger;
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
                ExceptionLogger.getInstance().logException(ex);
            }
        });
        writer.flush();
        writer.close();
    }

}
