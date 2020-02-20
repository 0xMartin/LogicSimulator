/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.wiring;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
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
public class Output extends WorkSpaceObject {

    private String label = "";

    //this be will be visible only outside of module
    private IOPin output;

    public Output(Point position, int bits) {
        super(position);

        //model
        Model model = new Model(
                new GraphicsObject[]{
                    new Line(new Point.Double(0.0, -14.0), new Point.Double(0.0, 14.0)),
                    new Line(new Point.Double(0.0, 14.0), new Point.Double(14.0, 0.0)),
                    new Line(new Point.Double(0.0, 14.0), new Point.Double(-14.0, 0.0))
                }
        );
        //pins
        this.output = new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0, 0));
        model.getIOPins().add(new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(0.0, -14.0)));

        super.setModel(model);
    }

    public IOPin getOutput() {
        return this.output;
    }

    public void setOutput(IOPin pin) {
        this.output = pin;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        boolean stat = super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());
        if (stat) //draw label
        {
            g2.setColor(Colors.OBJECT);
            g2.setFont(Fonts.LABEL);
            //draw label
            g2.drawString(
                    this.label,
                    (int) (super.getPosition().x + super.getModel().getBoundsMax().x + 9),
                    (int) (super.getPosition().y + super.getModel().getBoundsMin().y
                    + (super.getModel().getAngle() == 1 ? 0 : super.getModel().getHeight() / 2))
            );
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", super.getPins().get(0).getValue().length, Propertie.Type.BITS),
            new Propertie("Label", this.label)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    int i = propt.getValueInt();
                    i = Math.max(i, 1);
                    i = Math.min(i, 128);
                    super.getPins().get(0).changeBitWidth(i);
                    break;
                case "Label":
                    this.label = propt.getValueString();
                    this.output.setLabel(this.label);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean compute() {
        IOPin pin = super.getPins().get(0);
        return this.output.setValue(pin.getValue());
    }

    public WorkSpaceObject cloneObject() {
        Output ret = new Output(
                new Point(super.getPosition().x, super.getPosition().y),
                this.output.getValue().length
        );
        ret.output = this.output.cloneObject();
        ret.label = this.label;
        ret.getModel().clone(super.getModel());
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

}
