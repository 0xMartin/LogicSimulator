/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.control;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.GraphicsObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Colors;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class RandomGenerator extends WorkSpaceObject {

    private IOPin change, rndOut;

    public RandomGenerator(Point position, int bits) {
        super(position);

        Model model = new Model(
                new GraphicsObject[]{
                    new Line(new Point.Double(-14.0, -14.0), new Point.Double(-14.0, 14.0)),
                    new Line(new Point.Double(-14.0, -14.0), new Point.Double(14.0, -14.0)),
                    new Line(new Point.Double(14.0, -14.0), new Point.Double(14.0, 14.0)),
                    new Line(new Point.Double(-14.0, 14.0), new Point.Double(14.0, 14.0)),}
        );
        this.change = new IOPin(IOPin.MODE.INPUT, bits, "CHANGE", new Point.Double(0, -14));
        this.rndOut = new IOPin(IOPin.MODE.OUTPUT, bits, "RANDOM", new Point.Double(0, 14));
        model.getIOPins().add(this.rndOut);
        model.getIOPins().add(this.change);

        super.setModel(model);
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            g2.setFont(Fonts.STATUS);
            g2.setColor(Colors.OBJECT);
            g2.drawString(
                    "?",
                    super.getPosition().x - g2.getFontMetrics().stringWidth("?") / 2,
                    super.getPosition().y + Tools.centerYString(g2.getFontMetrics())
            );
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", super.getPins().get(0).getValue().length, Propertie.Type.BITS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.rndOut.changeBitWidth(propt.getValueInt());
                    break;
            }
        } catch (Exception ex) {
        }
    }

    private boolean r_edge = false;

    @Override
    public boolean compute() {
        if (this.change.getValue()[0]) {
            if (!this.r_edge) {
                //change all bits of io pin value (using random function)
                for (int i = 0; i < this.rndOut.getValue().length; i++) {
                    this.rndOut.getValue()[i] = Math.random() > 0.5d;
                }
                this.r_edge = true;
                return true;
            }
        } else {
            this.r_edge = false;
        }
        return false;
    }

    public WorkSpaceObject cloneObject() {
        return new RandomGenerator(Tools.copy(super.getPosition()), super.getPins().get(0).getValue().length);
    }
    
    @Override
    public boolean error() {
        return false;
    }


}
