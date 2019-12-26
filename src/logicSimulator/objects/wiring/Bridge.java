/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.wiring;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Tools;
import logicSimulator.common.Line;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.ui.Fonts;
import logicSimulator.ui.Colors;

/**
 *
 * @author Martin
 */
public class Bridge implements WorkSpaceObject, Serializable {

    private final Point position;

    private Model model;

    private boolean selected = false;

    //bridge idetificator
    private String id;

    private boolean idChanged = true;

    public Bridge(Point position, String id) {
        this.position = position;
        this.id = id;
        buildModel(20);
    }

    private void buildModel(int txtLength) {
        txtLength += 10;
        //orientation of model
        int angle = this.model == null ? 0 : this.model.getAngle();
        this.model = new Model(
                new Line[]{
                    //arrow
                    new Line(new Point.Double(0, 0), new Point.Double(-7, 7)),
                    new Line(new Point.Double(0, 0), new Point.Double(-7, -7)),
                    new Line(new Point.Double(-7, 7), new Point.Double(-7, 10)),
                    new Line(new Point.Double(-7, -7), new Point.Double(-7, -10)),
                    //body
                    new Line(new Point.Double(-7, -10), new Point.Double(-7 - txtLength, -10)),
                    new Line(new Point.Double(-7, 10), new Point.Double(-7 - txtLength, 10)),
                    new Line(new Point.Double(-7 - txtLength, 10), new Point.Double(-7 - txtLength, -10))
                },
                null, null);
        this.model.getIOPins().add(new IOPin(IOPin.MODE.LINKER, 1, "", new Point.Double(0, 0)));
        //rotate model
        this.model.rotate(angle);
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
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
        g2.setFont(Fonts.STATUS);
        //rebuild model
        if (this.idChanged) {
            this.idChanged = false;
            buildModel(g2.getFontMetrics().stringWidth(this.id));
        }
        //model
        this.model.renderModel(g2, this.position, offset, screen, this.selected);
        //draw id
        g2.setColor(Colors.GATE);
        int x = this.position.x;
        int y = this.position.y;
        int a = this.model.getAngle();
        switch (a) {
            case 0:
                x -= 13 + g2.getFontMetrics().stringWidth(this.id);
                break;
            case 1:
                x -= g2.getFontMetrics().stringWidth(this.id) / 2;
                y -= 7 + (this.model.getHeight() - 7) / 2;
                break;
            case 2:
                x += 13;
                break;
            case 3:
                x -= g2.getFontMetrics().stringWidth(this.id) / 2;
                y += 7 + (this.model.getHeight() - 7) / 2;
                break;
        }
        if (a == 1 || a == 3) {
            g2.rotate(-Math.PI / 2, this.position.x + 3, y - 2);
        }
        g2.drawString(
                this.id,
                x, y + Tools.centerYString(g2.getFontMetrics())
        );
        if (a == 1 || a == 3) {
            g2.rotate(Math.PI / 2, this.position.x + 3, y - 2);
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

    @Override
    public boolean error() {
        return false;
    }

    public WorkSpaceObject cloneObject() {
        Bridge bridge = new Bridge(Tools.copy(this.position), this.id);
        bridge.getModel().clone(this.model);
        return bridge;
    }

}
