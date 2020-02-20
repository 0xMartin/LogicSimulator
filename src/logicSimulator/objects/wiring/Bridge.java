/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.wiring;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import logicSimulator.Tools;
import logicSimulator.common.Line;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.GraphicsObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Fonts;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class Bridge extends WorkSpaceObject {

    //bridge idetificator
    private String id;

    private boolean idChanged = true;

    public Bridge(Point position, String id) {
        super(position);
        
        this.id = id;
        buildModel(20);
    }

    private void buildModel(int txtLength) {
        txtLength += 10;
        //orientation of model
        int angle = super.getModel() == null ? 0 : super.getModel().getAngle();
        Model model = new Model(
                new GraphicsObject[]{
                    //arrow
                    new Line(new Point.Double(0, 0), new Point.Double(-7, 7)),
                    new Line(new Point.Double(0, 0), new Point.Double(-7, -7)),
                    new Line(new Point.Double(-7, 7), new Point.Double(-7, 10)),
                    new Line(new Point.Double(-7, -7), new Point.Double(-7, -10)),
                    //body
                    new Line(new Point.Double(-7, -10), new Point.Double(-7 - txtLength, -10)),
                    new Line(new Point.Double(-7, 10), new Point.Double(-7 - txtLength, 10)),
                    new Line(new Point.Double(-7 - txtLength, 10), new Point.Double(-7 - txtLength, -10))
                }
        );
        model.getIOPins().add(new IOPin(IOPin.MODE.LINKER, 1, "", new Point.Double(0, 0)));
        //rotate model
        model.rotate(angle);
        
        super.setModel(model);
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        g2.setFont(Fonts.STATUS);
        //rebuild model
        if (this.idChanged) {
            this.idChanged = false;
            buildModel(g2.getFontMetrics().stringWidth(this.id));
        }
        //model
        super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());
        //draw id
        g2.setColor(Colors.OBJECT);
        int x = super.getPosition().x;
        int y = super.getPosition().y;
        int a = super.getModel().getAngle();
        switch (a) {
            case 0:
                x -= 13 + g2.getFontMetrics().stringWidth(this.id);
                break;
            case 1:
                x -= g2.getFontMetrics().stringWidth(this.id) / 2;
                y -= 7 + (super.getModel().getHeight() - 7) / 2;
                break;
            case 2:
                x += 13;
                break;
            case 3:
                x -= g2.getFontMetrics().stringWidth(this.id) / 2;
                y += 7 + (super.getModel().getHeight() - 7) / 2;
                break;
        }
        if (a == 1 || a == 3) {
            g2.rotate(-Math.PI / 2, super.getPosition().x + 3, y - 2);
        }
        g2.drawString(
                this.id,
                x, y + Tools.centerYString(g2.getFontMetrics())
        );
        if (a == 1 || a == 3) {
            g2.rotate(Math.PI / 2, super.getPosition().x + 3, y - 2);
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("ID", this.id)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "ID":
                    this.id = propt.getValueString();
                    this.idChanged = true;
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    public WorkSpaceObject cloneObject() {
        Bridge bridge = new Bridge(Tools.copy(super.getPosition()), this.id);
        bridge.getModel().clone(super.getModel());
        return bridge;
    }

}
