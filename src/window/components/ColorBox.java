/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JColorChooser;
import javax.swing.JTextField;

/**
 *
 * @author Martin
 */
public class ColorBox extends JTextField {

    public ColorBox(Color color) {
        this.setBackground(color);
        this.setOpaque(true);
        this.setEditable(false);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Color c = JColorChooser.showDialog(null, "Color", getBackground());
                if (c != null) {
                    setForeground(c);
                    setBackground(c);
                    setCaretColor(c);
                    setText(c.getRGB() + "");
                }
            }
        });
    }

    public Color getColor() {
        return this.getBackground();
    }

}
