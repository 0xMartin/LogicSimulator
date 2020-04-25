/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.output;

import logicSimulator.ui.Colors;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.Tools;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class Bulp extends WorkSpaceObject {

    private int bits;

    private String label = "";

    private Point.Double[] bulps;

    public Bulp(Point position, int bits) {
        super(position);

        buildModel(bits, 0);
    }

    private void buildModel(int bits, int angle) {
        this.bits = bits;
        //model
        int h_width = Math.min(8, bits) * 10;
        int h_height = ((bits - 1) / 8 + 1) * 10;
        int x_off = h_width % 14;
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-h_width + x_off, -h_height, h_width + x_off, -h_height));
        GOList.add(new Line(-h_width + x_off, h_height, h_width + x_off, h_height));
        GOList.add(new Line(-h_width + x_off, -h_height, -h_width + x_off, h_height));
        GOList.add(new Line(h_width + x_off, -h_height, h_width + x_off, h_height));
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
        model.getIOPins().add(
                new IOPin(IOPin.MODE.INPUT, bits, "", new Point.Double(-h_width + x_off, 0))
        );
        //rotate model
        model.rotate(angle);
        //size
        model.computeSize();

        super.setModel(model);
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        Model m = super.getModel();

        if (Tools.isInRange(
                pos.x - offset.x, pos.y - offset.y, screen,
                Math.max(m.getWidth(), m.getHeight())
        )) {
            //read value from input pin
            boolean[] val = m.getIOPins().get(0).getValue();

            m.preRender(g2);

            //draw buttons for each bit
            if (this.bits == val.length) {
                boolean b = m.getAngle() % 2 != 0;
                g2.setFont(Fonts.BIG);
                for (int index = 0; index < this.bits; index++) {
                    g2.setColor(val[index] ? Colors.WIRE_1 : Colors.WIRE_0);

                    //rotate points around center of objects if position of 1 and 3
                    Point.Double p = Tools.copy(this.bulps[index]);
                    if (m.getAngle() % 2 != 0) {
                        Tools.rotatePoint(p, Math.PI / 2d);
                        p.x -= 20;
                    }

                    //get position for each button
                    int x = (int) (pos.x + p.x);
                    int y = (int) (pos.y + p.y);

                    //offset
                    int a = m.getAngle();
                    if (a == 2 || a == 3) {
                        int off = (28 - 8 * Math.min(this.bits, 8));
                        while (off < 0) {
                            off += 28;
                        }
                        if (a == 2) {
                            x -= off;
                        } else {
                            y -= off;
                        }
                    }

                    //draw bit status
                    g2.fillArc(x, y, 20, 20, 0, 360);
                    g2.setColor(Colors.OBJECT);
                    g2.drawString(
                            val[index] ? "1" : "0",
                            (int) (x + 10 - g2.getFontMetrics().stringWidth("0") / 2),
                            (int) (y + 10 + Tools.centerYString(g2.getFontMetrics()))
                    );

                    //draw 0 and end bit 7 or lowes
                    if (val.length > 1) {
                        if (index == 0 || index == Math.min(this.bits - 1, 7)) {
                            g2.setFont(Fonts.SMALL);
                            g2.drawString(
                                    index + "",
                                    x + (b ? 28 : 14) - g2.getFontMetrics().stringWidth(index + "") / 2,
                                    y + (b ? 14 : -8)
                            );
                            g2.setFont(Fonts.BIG);
                        }
                    }
                }
                m.errorTag(false);
            } else {
                //error
                m.errorTag(true);
            }

            //draw label
            g2.setFont(Fonts.MEDIUM);
            g2.drawString(
                    this.label,
                    (int) (pos.x + m.getBoundsMax().x + 9),
                    (int) (pos.y + m.getBoundsMin().y
                    + (m.getAngle() == 2 ? 0 : m.getHeight() / 2))
            );

            //render model
            super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());

            super.getModel().postRender(g2);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.bits, Propertie.Type.BITS),
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
                    buildModel(i, super.getModel().getAngle());
                    break;
                case "Label":
                    this.label = propt.getValueString();
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public WorkSpaceObject cloneObject() {
        Bulp ret = new Bulp(Tools.copy(super.getPosition()), this.bits);
        ret.getModel().clone(super.getModel());
        ret.label = this.label;
        return ret;
    }

}
