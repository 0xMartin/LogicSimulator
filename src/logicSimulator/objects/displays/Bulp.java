/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.displays;

import logicSimulator.ui.Colors;
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
import logicSimulator.Tools;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class Bulp implements WorkSpaceObject, Serializable {

    private final Point position;

    private Model model;

    private int bits;

    private String label = "";

    private boolean selected = false;

    private Point.Double[] bulps;

    public Bulp(Point position, int bits) {
        this.position = position;
        this.buildModel(bits, 0);
    }

    public void buildModel(int bits, int angle) {
        this.bits = bits;
        //model
        int h_width = Math.min(8, bits) * 10;
        int h_height = ((bits - 1) / 8 + 1) * 10;
        int x_off = h_width % 20 == 0 ? 0 : 10;
        this.model = new Model(
                new Line[]{
                    new Line(new Point(-h_width + x_off, -h_height), new Point(h_width + x_off, -h_height)),
                    new Line(new Point(-h_width + x_off, h_height), new Point(h_width + x_off, h_height)),
                    new Line(new Point(-h_width + x_off, -h_height), new Point(-h_width + x_off, h_height)),
                    new Line(new Point(h_width + x_off, -h_height), new Point(h_width + x_off, h_height))
                },
                null, null
        );
        //buttons
        this.bulps = new Point.Double[bits];
        int x = 0, y = 0;
        for (int i = 0; i < bits; i++) {
            this.bulps[i] = new Point.Double(
                    h_width - x * 20 + x_off - 20,
                    -h_height + y * 20
            );
            x++;
            if (x > 7) {
                x = 0;
                y++;
            }
        }
        //output pin
        this.model.getIOPins().add(
                new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(-h_width + x_off, 0))
        );
        //rotate model
        this.model.rotate(angle);
        //size
        this.model.computeSize();
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
        if (Tools.isInRange(
                this.position.x - offset.x, this.position.y - offset.y, screen,
                Math.max(this.model.getWidth(), this.model.getHeight())
        )) {
            //read value from input pin
            boolean[] val = this.model.getIOPins().get(0).getValue();
            //draw
            //draw buttons for each bit
            if (this.bits == val.length) {
                boolean b = this.model.getAngle() % 2 != 0;
                g2.setFont(Fonts.STATUS);
                for (int index = 0; index < this.bits; index++) {
                    g2.setColor(val[index] ? Colors.WIRE_1 : Colors.WIRE_0);
                    //rotate points around center of objects if position of 1 and 3
                    Point.Double p = Tools.copy(this.bulps[index]);
                    if (this.model.getAngle() % 2 != 0) {
                        Tools.rotatePoint(p, Math.PI / 2d);
                        p.x -= 20;
                    }
                    //get position for each button
                    int x = (int) (this.position.x + p.x);
                    int y = (int) (this.position.y + p.y);
                    //offset (if width is 1, 3, 5, 7)
                    if (Math.min(this.bits, 8) % 2 != 0) {
                        switch (this.model.getAngle()) {
                            case 2:
                                x -= 20;
                                break;
                            case 3:
                                y -= 20;
                                break;
                        }
                    }
                    //draw bit status
                    g2.fillArc(x, y, 20, 20, 0, 360);
                    g2.setColor(Colors.GATE);
                    g2.drawString(
                            val[index] ? "1" : "0",
                            (int) (x + 10 - g2.getFontMetrics().stringWidth("0") / 2),
                            (int) (y + 10 + Tools.centerYString(g2.getFontMetrics()))
                    );
                    //draw 0 and end bit 7 or lowes
                    if (index == 0 || index == Math.min(this.bits - 1, 7)) {
                        g2.setFont(Fonts.IOPIN);
                        g2.drawString(
                                index + "",
                                x + (b ? 28 : 14) - g2.getFontMetrics().stringWidth(index + "") / 2,
                                y + (b ? 14 : -8)
                        );
                        g2.setFont(Fonts.STATUS);
                    }
                }
                this.model.errorTag(false);
            } else {
                //error
                this.model.errorTag(true);
            }
            g2.setFont(Fonts.LABEL);
            //draw label
            g2.drawString(
                    this.label,
                    (int) (this.position.x + this.model.getBoundsMax().x + 9),
                    (int) (this.position.y + this.model.getBoundsMin().y
                    + (this.model.getAngle() == 2 ? 0 : this.model.getHeight() / 2))
            );
            //render model
            this.model.renderModel(g2, this.position, offset, screen, this.selected);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.bits),
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
                    buildModel(i, this.model.getAngle());
                    break;
                case "Label":
                    this.label = propt.getValueString();
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean select(Point cursor) {
        if (this.model.intersect(cursor, this.position)) {
            this.selected = true;
            return true;
        }
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
        return false;
    }

    public WorkSpaceObject cloneObject() {
        Bulp ret = new Bulp(new Point(this.position.x, this.position.y), this.bits);
        ret.getModel().clone(this.model);
        ret.label = this.label;
        return ret;
    }

    @Override
    public boolean error() {
        return this.bits != this.model.getIOPins().get(0).getValue().length;
    }

    @Override
    public List<IOPin> getPins() {
        return this.model.getIOPins();
    }

}
