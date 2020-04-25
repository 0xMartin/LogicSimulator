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

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.accessibility.Accessible;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author Martin
 */
public class FontChooser extends JComboBox implements KeyListener {

    public static Font[] FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

    private boolean stringMode;

    public FontChooser(String initFont, boolean stringMode) {
        this(stringMode);
        super.setSelectedItem(initFont);
    }

    @Override
    public Object getSelectedItem() {
        return this.stringMode ? this.getSelectedFontName() : super.getSelectedItem();
    }

    public FontChooser(boolean stringMode) {
        this.stringMode = stringMode;
        super.addKeyListener(this);

        //set model
        if (this.stringMode) {
            //string mode
            String[] fNames = new String[FontChooser.FONTS.length];
            for (int i = 0; i < FontChooser.FONTS.length; i++) {
                fNames[i] = FontChooser.FONTS[i].getFontName();
            }
            super.setModel(new DefaultComboBoxModel<>(fNames));
        } else {
            //font mode
            super.setModel(new DefaultComboBoxModel<>(FontChooser.FONTS));
        }

        //longest font for PrototypeDisplayValue
        Font longest = FontChooser.FONTS[0];
        for (Font font : FontChooser.FONTS) {
            if (font.getFontName().length() > longest.getFontName().length()) {
                longest = font;
            }
        }
        super.setPrototypeDisplayValue(longest);

        //rendered
        super.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                JLabel label = new JLabel();
                if (stringMode) {
                    //string mode
                    String fontName = value.toString();
                    label.setFont(new Font(fontName, Font.PLAIN, 12));
                    label.setText(fontName);
                } else {
                    //font mode
                    Font font = (Font) value;
                    label.setFont(new Font(font.getName(), Font.PLAIN, 12));
                    label.setText(font.getFontName());
                }
                return label;
            }
        });

        Accessible a = super.getUI().getAccessibleChild(this, 0);
        if (a instanceof javax.swing.plaf.basic.ComboPopup) {
            JList popupList = ((javax.swing.plaf.basic.ComboPopup) a).getList();
            popupList.setPrototypeCellValue(super.getPrototypeDisplayValue());
        }
    }

    public String getSelectedFontName() {
        return this.stringMode ? super.getSelectedItem().toString() : ((Font) super.getSelectedItem()).getFontName();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        for (Font f : FontChooser.FONTS) {
            if (f.getFontName().charAt(0) == c) {
                super.setSelectedItem(f);
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
