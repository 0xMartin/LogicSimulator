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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class HexIO {

    private final File file;

    public HexIO(File file) {
        this.file = file;
    }

    /**
     * Write program to file
     *
     * @param data Data of memory
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void write(byte[] data) throws FileNotFoundException, IOException {
        String f = this.file.toString();
        try (OutputStream os = new FileOutputStream(f.endsWith(".hex") ? f : f + ".hex")) {
            for (byte cell : data) {
                os.write(cell);
            }
            os.close();
        }
    }

    /**
     * Read bytes from file
     *
     * @return List with bytes
     * @throws FileNotFoundException
     * @throws IOException
     */
    public byte[] read() throws FileNotFoundException, IOException {
        byte[] data;
        try (InputStream os = new FileInputStream(this.file.toString())) {
            data = os.readAllBytes();
            os.close();
        }
        return data;
    }

}
