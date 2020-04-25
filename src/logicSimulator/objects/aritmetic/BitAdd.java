/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.aritmetic;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.graphics.Line;
import logicSimulator.objects.IOPin;
import logicSimulator.ui.Colors;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class BitAdd extends WorkSpaceObject {

    protected final IOPin A, B, CIN, OUT, COUT;

    protected int bits;

    protected transient String title = "+";

    public BitAdd(Point position, int bits) {
        super(position);

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-28, -28, -28, 28));
        GOList.add(new Line(-28, -28, 28, -28));
        GOList.add(new Line(28, -28, 28, 28));
        GOList.add(new Line(-28, 28, 28, 28));

        //pins
        this.A = new IOPin(IOPin.MODE.INPUT, 1, "A", new Point.Double(-28.0, -14.0));
        this.B = new IOPin(IOPin.MODE.INPUT, 1, "B", new Point.Double(-28.0, 14.0));
        this.OUT = new IOPin(IOPin.MODE.OUTPUT, 1, "OUT", new Point.Double(28.0, 0));
        this.CIN = new IOPin(IOPin.MODE.INPUT, 1, "CIN", new Point.Double(0, -28.0));
        this.COUT = new IOPin(IOPin.MODE.OUTPUT, 1, "COUT", new Point.Double(0, 28.0));
        model.getIOPins().add(this.A);
        model.getIOPins().add(this.B);
        model.getIOPins().add(this.OUT);
        model.getIOPins().add(this.CIN);
        model.getIOPins().add(this.COUT);

        super.setModel(model);
        model.computeSize();
        model.disableRotation();

    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        if (stat) {
            g2.setColor(Colors.TEXT);
            g2.setFont(Fonts.SMALL);
            g2.drawString(this.CIN.getLabel(),
                    pos.x - g2.getFontMetrics().stringWidth(this.CIN.getLabel()) / 2,
                    -19 + pos.y + Tools.centerYString(g2.getFontMetrics())
            );
            g2.drawString(this.COUT.getLabel(),
                    pos.x - g2.getFontMetrics().stringWidth(this.COUT.getLabel()) / 2,
                    19 + pos.y + Tools.centerYString(g2.getFontMetrics())
            );
            g2.setFont(Fonts.BIG);
            g2.drawString(this.title,
                    pos.x - g2.getFontMetrics().stringWidth(this.title) / 2,
                    pos.y + Tools.centerYString(g2.getFontMetrics())
            );
        }
    }

    @Override
    public void restore() {
        this.title = "+";
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.bits, Propertie.Type.BITS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.bits = propt.getValueInt();
                    this.A.changeBitWidth(this.bits);
                    this.B.changeBitWidth(this.bits);
                    this.OUT.changeBitWidth(this.bits);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public boolean compute() {
        boolean[] vA = this.A.getValue();
        boolean[] vB = this.B.getValue();

        boolean[] ret = new boolean[this.bits];
        boolean carry = this.CIN.getValue()[0];
        for (int i = 0; i < this.bits; i++) {
            //Y = A xor B xor Carray
            ret[i] = vA[i] ^ vB[i] ^ carry;
            //Y = A and (B or Carry) or B and carry
            carry = vA[i] && (vB[i] || carry) || (vB[i] && carry);
        }

        this.COUT.getValue()[0] = carry;
        return this.OUT.setValue(ret) || carry;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new BitAdd(Tools.copy(super.getPosition()), this.bits);
    }

    @Override
    public boolean error() {
        return false;
    }

}
