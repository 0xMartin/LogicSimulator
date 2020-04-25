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
package window.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JColorChooser;
import javax.swing.JTextField;

/**
 *
 * @author Martin
 */
public class ColorBox extends JTextField {

    private ActionListener colorChanged;
    
    public ColorBox(Color color) {
        super.setBackground(color);
        super.setForeground(color);
        super.setOpaque(true);
        super.setEditable(false);
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Color c = JColorChooser.showDialog(null, "Color", getBackground());
                if (c != null) {
                    setForeground(c);
                    setBackground(c);
                    setCaretColor(c);
                    setText(c.getRGB() + "");
                    if(colorChanged != null){
                        colorChanged.actionPerformed(new ActionEvent(this, 0, ""));
                    }
                }
            }
        });
    }

    public Color getColor() {
        return this.getBackground();
    }
    
    public void setColorChangedListener(ActionListener listener){
        this.colorChanged = listener;
    }

}
