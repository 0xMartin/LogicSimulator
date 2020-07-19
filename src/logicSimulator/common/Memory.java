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

import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author Martin
 */
public interface Memory {

    /**
     * Get memory data array
     *
     * @return byte[]
     */
    public byte[] getData();

    /**
     * Get id of rom ram memory
     *
     * @return
     */
    public String getID();

    /**
     * This listener is invoked when something access to some value inside
     *
     * @param listener ActionListener
     */
    public void setOnMemoryAccessListener(ActionListener listener);

    /**
     * Load data to memory
     * @param hex Hexadecimal program
     * @param offset Offset of program
     * @param clear Clear memory before uploading
     */
    public void uploadProgram(List<Byte> hex, int offset, boolean clear);
    
}
