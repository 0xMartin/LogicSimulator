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
package logicSimulator.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JMenuItem;
import logicSimulator.Tools;

/**
 * JButton with better graphics
 * @author Martin
 */
public class MenuItemHQ extends JMenuItem {

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Tools.setHighQuality(g2);
        super.paintComponent((Graphics2D) g);
    }

}
