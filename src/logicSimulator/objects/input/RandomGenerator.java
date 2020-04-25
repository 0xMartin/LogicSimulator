/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.input;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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
public class RandomGenerator extends WorkSpaceObject {

    private final IOPin change, rndOut;

    public RandomGenerator(Point position, int bits) {
        super(position);
        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-14, -14, -14, 14));
        GOList.add(new Line(-14, -14, 14, -14));
        GOList.add(new Line(14, -14, 14, 14));
        GOList.add(new Line(-14, 14, 14, 14));
        
        //pins
        this.change = new IOPin(IOPin.MODE.INPUT, bits, "CHANGE", new Point.Double(0, -14));
        this.rndOut = new IOPin(IOPin.MODE.OUTPUT, bits, "RANDOM", new Point.Double(0, 14));
        model.getIOPins().add(this.rndOut);
        model.getIOPins().add(this.change);

        super.setModel(model);
        model.computeSize();
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            g2.setFont(Fonts.BIG);
            g2.setColor(Colors.OBJECT);
            g2.drawString("?",
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
        } catch (NumberFormatException ex) {
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

    @Override
    public RandomGenerator cloneObject() {
        RandomGenerator ret = new RandomGenerator(Tools.copy(super.getPosition()), super.getPins().get(0).getValue().length);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

}
