/*
 * Logic simlator
 * Author: Martin Krcma
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
