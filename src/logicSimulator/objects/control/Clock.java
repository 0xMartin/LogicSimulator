/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.control;

import java.awt.Component;
import logicSimulator.common.ClickAction;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import logicSimulator.Project;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.GraphicsObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class Clock extends WorkSpaceObject implements ClickAction {

    private boolean run = false;

    private int delay = 0;

    public Clock(Point position, int bits) {
        super(position);

        Model model = new Model(
                new GraphicsObject[]{
                    new Line(new Point.Double(-14.0, -14.0), new Point.Double(-14.0, 14.0)),
                    new Line(new Point.Double(-14.0, -14.0), new Point.Double(14.0, -14.0)),
                    new Line(new Point.Double(14.0, -14.0), new Point.Double(14.0, 14.0)),
                    new Line(new Point.Double(-14.0, 14.0), new Point.Double(14.0, 14.0)),}
        );
        model.getIOPins().add(new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0, 14)));

        super.setModel(model);
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            g2.setColor(this.run ? Colors.WIRE_1 : Colors.WIRE_0);
            g2.fillRect(-12 + pos.x, -12 + pos.y, 24, 24);
            g2.setColor(Colors.OBJECT);
            g2.drawLine(-8 + pos.x, -8 + pos.y, pos.x, -8 + pos.y);
            g2.drawLine(pos.x, -8 + pos.y, pos.x, 8 + pos.y);
            g2.drawLine(pos.x, 8 + pos.y, 8 + pos.x, 8 + pos.y);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", super.getPins().get(0).getValue().length, Propertie.Type.BITS),
            new Propertie("Delay", this.delay)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    super.getPins().get(0).changeBitWidth(propt.getValueInt());
                    break;
                case "Delay":
                    this.delay = Math.max(propt.getValueInt(), 0);
                    break;
            }
        } catch (Exception ex) {
        }
    }

    private int time = 0;

    @Override
    public boolean compute() {
        if (this.run) {
            if (this.time < this.delay) {
                this.time++;
            } else {
                this.time = 0;

                boolean[] out = super.getModel().getIOPins().get(0).getValue();
                if (out != null) {
                    boolean v = !out[0];
                    for (int i = 0; i < out.length; i++) {
                        out[i] = v;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public WorkSpaceObject cloneObject() {
        return new Clock(Tools.copy(super.getPosition()), super.getPins().get(0).getValue().length);
    }

    @Override
    public void changeValue(Point cursor, Component parent, Project project) {
        //if user click on clock -> change value
        if (super.getModel().intersect(cursor, super.getPosition())) {
            //run
            this.run = !this.run;
            //set false value for each value of output buffer
            boolean[] out = super.getModel().getIOPins().get(0).getValue();
            if (out != null) {
                boolean v = !out[0];
                for (int i = 0; i < out.length; i++) {
                    out[i] = false;
                }
            }
        }
    }

}
