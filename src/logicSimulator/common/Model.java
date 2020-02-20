/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.common;

import java.awt.AlphaComposite;
import logicSimulator.Tools;
import logicSimulator.ui.Colors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import logicSimulator.LogicSimulatorCore;

/**
 *
 * @author Martin
 */
public class Model implements Serializable {

    //width and height of model
    private int width = 0, height = 0;

    //model bounds
    private Point.Double min, max;

    //input and output pins
    private final List<IOPin> pins;

    //color of model
    private Color color = Colors.OBJECT;

    //angle of model
    private int angle = 0;

    private boolean drag = false;

    //true -> user can rotate model
    public boolean rotation = true;

    //graphics objects
    public GraphicsObject[] graphicsObjects;

    public Model(GraphicsObject[] graphicsObjects) {
        this.graphicsObjects = graphicsObjects;
        this.pins = new ArrayList<>();
        computeSize();
    }

    /**
     * Dispble rotation, user cant rotate model
     */
    public void disableRotation() {
        this.rotation = false;
    }

    /**
     * Allow rotation, user can rotate model
     */
    public void allowRotation() {
        this.rotation = true;
    }

    /**
     * Orientation of model [0, 1, 2, 3] 0 - start position
     *
     * @return
     */
    public int getAngle() {
        return this.angle;
    }

    /**
     * Get all io pins
     *
     * @return
     */
    public List<IOPin> getIOPins() {
        return this.pins;
    }

    /**
     * Return up left corner of model bounds
     *
     * @return
     */
    public Point.Double getBoundsMin() {
        return this.min;
    }

    /**
     * Return down right corner of model
     *
     * @return
     */
    public Point.Double getBoundsMax() {
        return this.max;
    }

    /**
     * Compute size of this model
     */
    public void computeSize() {
        this.max = new Point.Double(Integer.MIN_VALUE, Integer.MIN_VALUE);
        this.min = new Point.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
        //find max and min for x and y axis
        if (this.graphicsObjects != null) {
            for (GraphicsObject go : this.graphicsObjects) {
                for (Point.Double pt : go.getPoints()) {
                    this.max.x = Math.max(this.max.x, pt.x);
                    this.max.y = Math.max(this.max.y, pt.y);
                    this.min.x = Math.min(this.min.x, pt.x);
                    this.min.y = Math.min(this.min.y, pt.y);
                }
            }
        }
        //compute width and height
        this.width = (int) (this.max.x - this.min.x);
        this.height = (int) (this.max.y - this.min.y);
    }

    /**
     * Get width of model
     *
     * @return
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get height of model
     *
     * @return
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Rotate model around center [0, 0] 1 angle = 90 degres
     *
     * @param angle
     */
    public void rotate(int angle) {
        if (!this.rotation) {
            return;
        }

        this.angle += angle;
        while (this.angle < 0) {
            this.angle += 4;
        }
        while (this.angle > 3) {
            this.angle -= 4;
        }
        double A = (double) angle * Math.PI / 2d;
        //line
        if (this.graphicsObjects != null) {
            for (GraphicsObject go : this.graphicsObjects) {
                for (Point.Double pt : go.getPoints()) {
                    Tools.rotatePoint(pt, A);
                }
            }
        }
        //pins
        this.pins.stream().forEach((pin) -> {
            Tools.rotatePoint(pin.getPosition(), A);
            pin.getPosition().x = Tools.step((int) pin.getPosition().x, LogicSimulatorCore.WORK_SPACE_STEP);
            pin.getPosition().y = Tools.step((int) pin.getPosition().y, LogicSimulatorCore.WORK_SPACE_STEP);
        });
        //compute model size
        computeSize();
    }

    /**
     * Intersect ?
     *
     * @param cursor Position of cursor
     * @param mPosition Model position
     * @return boolean
     */
    public boolean intersect(Point cursor, Point mPosition) {
        if (mPosition == null || cursor == null) {
            return false;
        }
        int x = cursor.x;
        int y = cursor.y;
        if (x >= mPosition.x + this.min.x
                && x <= mPosition.x + this.max.x) {
            if (y >= mPosition.y + this.min.y
                    && y <= mPosition.y + this.max.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set drag for model
     *
     * @param value
     */
    public void setDrag(boolean value) {
        this.drag = value;
    }

    /**
     * Clone model
     *
     * @return
     */
    public Model cloneObject() {

        //copy all graphics objects
        GraphicsObject[] go_copy = null;
        if (this.graphicsObjects != null) {
            go_copy = new GraphicsObject[this.graphicsObjects.length];
            for (int i = 0; i < go_copy.length; i++) {
                go_copy[i] = this.graphicsObjects[i].cloneObject();
            }
        }

        //model 
        Model ret = new Model(go_copy);

        //pins
        this.pins.forEach((pin) -> {
            ret.getIOPins().add(pin.cloneObject());
        });

        //out position
        ret.angle = this.angle;

        return ret;
    }

    /**
     * Render model
     *
     * @param g2 Graphics2D
     * @param x x offset
     * @param y y offset
     */
    public void render(Graphics2D g2, int x, int y) {
        //color
        g2.setColor(this.color);

        //draw all lines
        if (this.graphicsObjects != null) {
            for (GraphicsObject go : this.graphicsObjects) {
                go.draw(g2, x, y);
            }
        }
        //render pints
        this.pins.stream().forEach((pin) -> {
            pin.render(g2, x, y);
        });

    }

    /**
     * Render model + if model is out of screen then dont render model + draw
     * select rect
     *
     * @param g2 Graphics2D
     * @param pos Position of object
     * @param offset Rendereing offset (Only for render gates thet are in
     * visible area)
     * @param screen Sreen size
     * @param select Is selecte ? -> draw select
     * @return Return true if model was rendered
     */
    public boolean renderModel(Graphics2D g2, Point pos, Point offset, Dimension screen, boolean select) {
        if (this.graphicsObjects == null) {
            return false;
        }
        if (Tools.isInRange(
                pos.x - offset.x, pos.y - offset.y, screen,
                Math.max(this.width, this.height)
        )) {

            //draw select rect
            if (select) {
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.setColor(Colors.SELECT_RECT);
                g2.drawRect(
                        (int) (pos.x + this.min.x - 10),
                        (int) (pos.y + this.min.y - 10),
                        this.width + 20,
                        this.height + 20
                );
            }

            preRender(g2);

            g2.setStroke(new BasicStroke(2));
            g2.setColor(this.color);

            //render model
            this.render(g2, pos.x, pos.y);

            //render error 
            if (this.error) {
                Tools.drawError(g2, pos.x, pos.y, Math.max(this.width, this.height) / 3);
            }

            postRender(g2);

            return true;
        }
        return false;
    }

    /**
     * Pre render - graphics configurations before object rendering
     *
     * @param g2 Graphics
     */
    public void preRender(Graphics2D g2) {
        if (this.drag) {
            g2.setComposite(AlphaComposite.SrcOver.derive(0.4f));
        }
    }

    /**
     * Post render - graphics configurations after object rendering
     *
     * @param g2 Graphics
     */
    public void postRender(Graphics2D g2) {
        if (this.drag) {
            g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));
        }
    }

    /**
     * Set color of model
     *
     * @param c Color
     */
    public void setColor(Color c) {
        this.color = c;
    }

    /**
     * Get color of model
     *
     * @return
     */
    public Color getColor() {
        return this.color;
    }

    //may display error image on model ?
    private boolean error = false;

    /**
     * Set error tag
     *
     * @param error true -> display error image on model
     */
    public void errorTag(boolean error) {
        this.error = error;
    }

    /**
     * Clone model and this model will be reference on cloned model
     *
     * @param model
     */
    public void clone(Model model) {
        Model m = model.cloneObject();
        this.graphicsObjects = m.graphicsObjects;
        this.pins.clear();
        this.angle = m.angle;
        m.pins.stream().forEach((p) -> {
            this.pins.add(p);
        });
        computeSize();
    }

    /**
     * Return all points of this model
     *
     * @return
     */
    public List<Point.Double> points() {
        List<Point.Double> ret = new ArrayList<>();
        int i = 0;
        //try 10 times
        while (i < 10) {
            try {
                //get line points
                if (this.graphicsObjects != null) {
                    for (GraphicsObject go : this.graphicsObjects) {
                        ret.addAll(Arrays.asList(go.getPoints()));
                    }
                }
                return ret;
            } catch (Exception ex) {
                i++;
            }
        }
        return ret;
    }

}
