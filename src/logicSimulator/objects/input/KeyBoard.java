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
package logicSimulator.objects.input;

import java.awt.Component;
import logicSimulator.common.ClickAction;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.Project;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Colors;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class KeyBoard extends WorkSpaceObject implements ClickAction {

    private transient boolean active = false;

    private final IOPin CLK, CLR, OUT, AVAILABLE;

    private transient LinkedList<Character> buffer = new LinkedList<>();

    private transient KeyListener keyListener;

    public KeyBoard(Point position) {
        super(position);
        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-28, -21, -28, 21));
        GOList.add(new Line(28, -21, 28, 21));
        GOList.add(new Line(-28, -21, 28, -21));
        GOList.add(new Line(-28, 21, 28, 21));

        //output pin
        this.CLK = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-28, -14));
        this.CLK.drawClkSymbol = true;
        this.CLR = new IOPin(IOPin.MODE.INPUT, 1, "CLEAR", new Point.Double(-28, 14));
        this.AVAILABLE = new IOPin(IOPin.MODE.OUTPUT, 1, "AVAILABLE", new Point.Double(28, 14));
        this.OUT = new IOPin(IOPin.MODE.OUTPUT, 8, "OUTPUT", new Point.Double(28, -14));
        model.getIOPins().add(this.CLK);
        model.getIOPins().add(this.CLR);
        model.getIOPins().add(this.AVAILABLE);
        model.getIOPins().add(this.OUT);

        super.setModel(model);
        model.disableRotation();
        model.computeSize();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        //draw keyborad on/of status and char on the top of lifo stack buffer
        if (stat) {
            //stat
            g2.setColor(this.active ? Colors.WIRE_1 : Colors.WIRE_0);
            g2.fillRect(-12 + pos.x, -12 + pos.y, 24, 24);
            g2.setColor(Colors.OBJECT);
            //top char
            g2.setFont(Fonts.MEDIUM);
            if (!this.buffer.isEmpty()) {
                try {
                    String top = (char) this.buffer.getFirst() + "";
                    g2.drawString(top,
                            pos.x - g2.getFontMetrics().stringWidth(top) / 2,
                            pos.y + Tools.centerYString(g2.getFontMetrics())
                    );
                } catch (Exception ex) {
                }
            }
            //char count
            g2.setFont(Fonts.SMALL);
            String count = this.buffer.size() + "";
            g2.drawString(count,
                    pos.x + 20 - g2.getFontMetrics().stringWidth(count) / 2,
                    pos.y + 15
            );
        }
    }

    @Override
    public void restore() {
        this.active = false;
        //restore char buffer
        this.buffer = new LinkedList<>();
    }

    @Override
    public Propertie[] getProperties() {
        return null;
    }

    @Override
    public void changePropertie(Propertie propt) {

    }

    private boolean r_edge = true;

    @Override
    public boolean compute() {
        //clear
        if (this.CLR.getValue()[0]) {
            this.buffer.clear();
        }
        //get char from keyboard buffer
        if (this.CLK.getValue()[0]) {
            if (this.r_edge) {
                this.r_edge = false;

                //is available some char
                this.AVAILABLE.getValue()[0] = !this.buffer.isEmpty();

                if (!this.buffer.isEmpty()) {
                    //get first char from buffer
                    char c = this.buffer.getFirst();
                    this.buffer.removeFirst();
                    //write bin value of char to 8 bit bus
                    return this.OUT.setValue(Convert.charToBits(c));
                } else {
                    return this.OUT.setValue(false);
                }
            }
        } else {
            this.r_edge = true;
        }
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new KeyBoard(Tools.copy(super.getPosition()));
    }

    @Override
    public void changeValue(Point cursor, Component parent, Project project) {
        //if user click on clock -> change value
        if (super.getModel().intersect(cursor, super.getPosition())) {
            //active listening
            this.active = !this.active;
            //remove last keylistener
            if (this.keyListener != null) {
                parent.removeKeyListener(this.keyListener);
            }
            if (this.active) {
                //set key listener
                this.keyListener = new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (c >= 32 && c < 126 || c == '\n' || c == '\b') {
                            //add char to buffer and repaint
                            buffer.add(c);
                            parent.repaint();
                            //is available some char
                            AVAILABLE.getValue()[0] = !buffer.isEmpty();
                        }
                    }
                };
                parent.addKeyListener(this.keyListener);
            }
        }
    }

    @Override
    public boolean error() {
        return false;
    }

}
