/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.wiring;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import logicSimulator.WorkSpaceObject;
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
public class Output implements WorkSpaceObject, Serializable {

    private final Point position;

    private final Model model;

    private boolean selected = false;

    private String label = "";

    //this be will be visible only outside of module
    private IOPin output;

    public IOPin getOutput() {
        return this.output;
    }

    public void setOutput(IOPin pin) {
        this.output = pin;
    }

    public Output(Point position, int bits) {
        this.position = position;
        //model
        this.model = new Model(
                new Line[]{
                    new Line(new Point(0, -20), new Point(0, 20)),
                    new Line(new Point(-15, 5), new Point(0, 20)),
                    new Line(new Point(15, 5), new Point(0, 20))
                },
                null, null
        );
        //pins
        this.output = new IOPin(IOPin.MODE.OUTPUT, bits, "", new Point.Double(0, 0));
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(0, -20))
        );
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(this.model.getWidth(), this.model.getHeight());
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        boolean stat = this.model.renderModel(g2, this.position, offset, screen, this.selected);
        if (stat) //draw label
        {
            g2.setColor(Colors.GATE);
            g2.setFont(Fonts.LABEL);
            //draw label
            g2.drawString(
                    this.label,
                    (int) (this.position.x + this.model.getBoundsMax().x + 9),
                    (int) (this.position.y + this.model.getBoundsMin().y
                    + (this.model.getAngle() == 1 ? 0 : this.model.getHeight() / 2))
            );
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.model.getIOPins().get(0).getValue().length),
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
                    this.model.getIOPins().get(0).changeBitWidth(i);
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
    public List<IOPin> getPins() {
        return this.model.getIOPins();
    }

    @Override
    public boolean select(Point position) {
        if (this.model.intersect(position, this.position)) {
            this.selected = true;
            return true;
        }
        this.selected = false;
        return false;
    }

    @Override
    public void unSelect() {
        this.selected = false;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public boolean compute() {
        IOPin pin = this.model.getIOPins().get(0);
        return this.output.setValue(pin.getValue());
    }

    public WorkSpaceObject cloneObject() {
        Output ret = new Output(
                new Point(this.position.x, this.position.y),
                this.output.getValue().length
        );
        ret.output = this.output.cloneObject();
        ret.label = this.label;
        ret.getModel().clone(this.model);
        return ret;
    }

    @Override
    public boolean error() {
        return false;
    }

}
