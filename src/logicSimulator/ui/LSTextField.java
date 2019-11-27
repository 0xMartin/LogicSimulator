/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JTextField;

/**
 *
 * @author Martin
 */
public class LSTextField extends JTextField {

    private boolean caretBlink = false;

    public LSTextField() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        //render hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        
        g2.setFont(this.getFont());

        //draw bg
        g2.setColor(Colors.COMPONENT_BACKGROUND);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2.setColor(Colors.COMPONENT_FOREGROUND);
        g2.drawRect(0, 0, this.getWidth(), this.getHeight());

        //text matrics
        g2.setFont(this.getFont());
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(this.getText(), g2);

        if (this.isFocusOwner()) {
            int c_off = (int) ((this.getHeight() - r.getHeight()) / 2);
            //caret blinking
            this.caretBlink = !this.caretBlink;
            //caret
            if (this.caretBlink) {
                g2.setColor(Color.WHITE);
                int x = 3 + g2.getFontMetrics().stringWidth(this.getText().substring(0, this.getCaretPosition()));
                g2.drawLine(x, c_off, x, (int) r.getHeight() + c_off);
            }
            //select
            g2.setColor(Colors.COMPONENT_SELECTBG);
            int ss = 3 + g2.getFontMetrics().stringWidth(
                    this.getText().substring(0, this.getSelectionStart())
            ) - this.getScrollOffset();
            int se = 3 + g2.getFontMetrics().stringWidth(
                    this.getText().substring(0, this.getSelectionEnd())
            ) - this.getScrollOffset();
            g2.fillRect(ss, c_off, se - ss, (int) r.getHeight() + c_off);
        }

        //draw text
        g2.setColor(Colors.COMPONENT_FOREGROUND);
        int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
        g2.drawString(this.getText(), 3 - this.getScrollOffset(), y);
    }

}
