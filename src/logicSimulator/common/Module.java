/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.wiring.Input;

/**
 *
 * @author Martin
 */
public class Module implements WorkSpaceObject {

    private boolean select = false;

    private final Point position;

    private final Model model;

    private final List<WorkSpaceObject> objects = new ArrayList<>();

    public Module(int x, int y) {
        this.model = new Model(null, null, null);
        this.position = new Point(x, y);
    }

    public List<WorkSpaceObject> getObjects() {
        return this.objects;
    }

    public void addObjects(List<WorkSpaceObject> objects) {
        objects.stream().forEach((obj) -> {
            try {
                objects.add(Tools.clone(obj));
            } catch (CloneNotSupportedException ex) {
            }
        });
    }

    private void refreshIOPins() {
        List<IOPin> module_pins = new ArrayList<>();
        this.objects.stream().forEach((obj) -> {
            if (obj instanceof Input) {
                module_pins.add(obj);
            }
        });
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
        this.model.renderModel(g2, this.position, offset, screen, true);
    }

    @Override
    public Propertie[] getProperties() {
        return null;
    }

    @Override
    public void changePropertie(Propertie arg0) {

    }

    @Override
    public boolean select(Point cursor) {
        if (this.model.intersect(cursor, this.position)) {
            this.select = true;
            return true;
        }
        return false;
    }

    @Override
    public void unSelect() {
        this.select = false;
    }

    @Override
    public boolean isSelected() {
        return this.select;
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

    @Override
    public List<IOPin> getPins() {
        return this.model.getIOPins();
    }

}
