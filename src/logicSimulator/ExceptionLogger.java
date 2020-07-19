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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin
 */
public class ExceptionLogger {

    private static ExceptionLogger el;

    private BufferedWriter writer;

    private ExceptionLogger() {
        try {
            this.writer = new BufferedWriter(new FileWriter(new File("error.txt"), true));
        } catch (IOException ex) {
        }
    }

    public void logException(Exception ex) {
        Logger.getLogger(ExceptionLogger.class.getName()).log(Level.SEVERE, null, ex);
        try {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();

            try {
                this.writer.write(LogicSimulatorCore.getDate("HH:mm:ss - dd.MM.yyyy"));
            } catch (Exception ex1) {
            }
            this.writer.newLine();
            this.writer.write(exceptionAsString);
            this.writer.newLine();
        } catch (Exception ex1) {
        }
    }

    public void closeFile() throws IOException {
        this.writer.flush();
        this.writer.close();
    }

    public static ExceptionLogger getInstance() {
        if (ExceptionLogger.el == null) {
            ExceptionLogger.el = new ExceptionLogger();
        }
        return ExceptionLogger.el;
    }

}
