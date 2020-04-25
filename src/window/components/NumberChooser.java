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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import logicSimulator.Tools;

/**
 *
 * @author Martin
 */
public class NumberChooser extends JLabel implements MouseListener, MouseMotionListener {

    private int value, step, min, max;

    private String prefix;

    private JWindow ruler;

    private boolean hex = false;

    public NumberChooser(String prefix, int value, int step, int min, int max) {
        super(prefix + "    " + value);
        this.value = value;
        this.step = step;
        this.prefix = prefix;
        this.min = min;
        this.max = max;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        //init popup
        updateRuler();
    }

    private void updateRuler() {
        this.ruler = new JWindow() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.RED);
                Tools.setHighQuality(g2);
                g2.setStroke(new BasicStroke(2));
                for (int x = 0; x <= step * 20; x += step) {
                    g2.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            1.0f - (float) Math.abs(step * 10f - x) / (float) (step * 10f) * 0.8f
                    )
                    );
                    g2.drawLine(x, 0, x, 10);
                }
            }
        };
        this.ruler.setAlwaysOnTop(true);
        this.ruler.setSize(new Dimension(this.step * 20, 10));
        this.ruler.setBackground(new Color(0, 0, 0, 0));
    }

    public void setHexDisplayFormat(boolean b) {
        this.hex = b;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public float getStep() {
        return this.step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int val = (this.value + this.offset);
         
        //draw
        super.setText(this.prefix + "    " + (this.hex ? "0x" + Integer.toString(val, 16) : val));
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        Tools.setHighQuality(g2);

        //arrow
        int x = g2.getFontMetrics().stringWidth(this.prefix) + 4;
        int h = g2.getFontMetrics().getAscent() - 2;
        Polygon p = new Polygon(
                new int[]{x, x + h, x},
                new int[]{
                    this.getHeight() / 2 - h / 2,
                    this.getHeight() / 2,
                    this.getHeight() / 2 + h / 2
                },
                3
        );
        g2.setColor(this.pressed ? Color.RED : Color.GRAY);
        g2.fillPolygon(p);
    }

    private boolean pressed = false;

    private Point p;
    private int offset = 0;

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    //action listener for value changed
    private ActionListener valueChanged;

    /**
     * Set value changed listener
     *
     * @param valueChanged ActionListener
     */
    public void setValueChangedEvent(ActionListener valueChanged) {
        this.valueChanged = valueChanged;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!super.isEnabled()) {
            return;
        }
        if (e.getButton() == 3) {
            try {
                this.value = Integer.parseInt(
                        JOptionPane.showInputDialog(
                                this,
                                "Type a new ups",
                                "UPS",
                                JOptionPane.QUESTION_MESSAGE
                        )
                );
                this.value = Math.max(this.value, this.min);
                this.value = Math.min(this.value, this.max);
                //value changed event
                if (this.valueChanged != null) {
                    this.valueChanged.actionPerformed(new ActionEvent(this, 0, ""));
                }
            } catch (Exception ex) {
            }
        } else {
            this.pressed = true;
            this.p = e.getPoint();
            //show ruler
            int x = this.getX() - this.step * 10 + this.getWidth() / 2;
            int y = this.getY() + this.getHeight();
            Container c = this.getParent();
            do {
                x += c.getX();
                y += c.getY();
            } while ((c = c.getParent()) != null);
            this.ruler.setLocation(x, y);
            this.ruler.setVisible(true);
        }
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.pressed = false;
        this.value += this.offset;
        this.value = Math.max(this.value, this.min);
        this.value = Math.min(this.value, this.max);
        this.offset = 0;
        this.ruler.setVisible(false);
        this.repaint();
        //value changed event
        if (this.valueChanged != null) {
            this.valueChanged.actionPerformed(new ActionEvent(this, 0, ""));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.pressed) {
            //difference beetwen points
            int diff = (e.getX() - this.p.x) / this.step;

            if (Math.abs(diff) <= 4) {
                this.offset = diff;
            } else if (Math.abs(diff) <= 8) {
                this.offset = (int) (Math.signum(diff) * (Math.abs(diff) - 4) * 10);
            } else if (Math.abs(diff) <= 10) {
                this.offset = (int) (Math.signum(diff) * (Math.abs(diff) - 8) * 50);
            }
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
