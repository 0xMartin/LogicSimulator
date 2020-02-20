/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.displays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.GraphicsObject;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class RasterScreen extends WorkSpaceObject {

    /**
     * Width and height of screen (number of pixels) and size of one pixel
     */
    private int width, height, pixelSize;

    /**
     * Color for 0 and 1 value in pixels
     */
    private Color OFF = Color.BLACK, ON = Color.WHITE;

    /**
     * Pixels
     */
    private boolean[] pixels;

    /**
     * data - input data for one pixel, clear - clear display, clk - clock
     * synchronization impuls for writing data to screen buffer
     */
    private IOPin data, clear, clk, row, col;

    /**
     * Create raster screen
     *
     * @param position Position of screen
     * @param width width of screen (number of pixels)
     * @param height height of screen (number of pixels)
     * @param pixelSize Size of onr pixel
     */
    public RasterScreen(Point position, int width, int height, int pixelSize) {
        super(position);
        this.width = width;
        this.height = height;
        this.pixelSize = pixelSize;
        rebuildModel();
    }

    private void rebuildModel() {
        this.pixels = new boolean[this.width * this.height];

        //model
        double w_halft = pixelSize * width / 2d;
        double h_halft = pixelSize * height / 2d;
        double offset = LogicSimulatorCore.WORK_SPACE_STEP - w_halft % LogicSimulatorCore.WORK_SPACE_STEP;
        Model model = new Model(
                new GraphicsObject[]{
                    new Line(new Point.Double(-w_halft - offset, -h_halft), new Point.Double(w_halft - offset, -h_halft)),
                    new Line(new Point.Double(-w_halft - offset, h_halft), new Point.Double(w_halft - offset, h_halft)),
                    new Line(new Point.Double(-w_halft - offset, h_halft), new Point.Double(-w_halft - offset, -h_halft)),
                    new Line(new Point.Double(w_halft - offset, h_halft), new Point.Double(w_halft - offset, -h_halft))
                }
        );
        double h = h_halft % LogicSimulatorCore.WORK_SPACE_STEP - h_halft;
        //io pins
        this.clear = new IOPin(
                IOPin.MODE.INPUT, 1, "CLEAR",
                new Point.Double(-w_halft - offset, h)
        );
        this.data = new IOPin(
                IOPin.MODE.INPUT, width, "DATA",
                new Point.Double(-w_halft - offset, h + LogicSimulatorCore.WORK_SPACE_STEP)
        );
        this.clk = new IOPin(
                IOPin.MODE.INPUT, 1, "CLK",
                new Point.Double(-w_halft - offset, h + LogicSimulatorCore.WORK_SPACE_STEP * 2)
        );
        this.row = new IOPin(
                IOPin.MODE.INPUT, Tools.binLength(this.height - 1), "ROW",
                new Point.Double(-w_halft - offset, h + LogicSimulatorCore.WORK_SPACE_STEP * 3)
        );
        this.col = new IOPin(
                IOPin.MODE.INPUT, Tools.binLength(this.width - 1), "COLL",
                new Point.Double(-w_halft - offset, h + LogicSimulatorCore.WORK_SPACE_STEP * 4)
        );
        model.getIOPins().add(this.clear);
        model.getIOPins().add(this.data);
        model.getIOPins().add(this.clk);
        model.getIOPins().add(this.row);
        model.getIOPins().add(this.col);
        model.disableRotation();

        super.setModel(model);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        boolean stat = super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());
        if (stat) {
            double w_halft = pixelSize * width / 2d;
            double h_halft = pixelSize * height / 2d;
            int off = (int) (LogicSimulatorCore.WORK_SPACE_STEP - w_halft % LogicSimulatorCore.WORK_SPACE_STEP);
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    g2.setColor(this.pixels[i + j * this.width] ? this.ON : this.OFF);
                    g2.fillRect(
                            i * this.pixelSize + super.getPosition().x - (int) w_halft - off,
                            j * this.pixelSize + super.getPosition().y - (int) h_halft,
                            this.pixelSize,
                            this.pixelSize
                    );
                }
            }
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Width", this.width, Propertie.Type.BITS),
            new Propertie("Height", this.height, Propertie.Type.BITS),
            new Propertie("PixelSize", this.pixelSize)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Width":
                    this.width = propt.getValueInt();
                    break;
                case "Height":
                    this.height = propt.getValueInt();
                    break;
                case "PixelSize":
                    this.pixelSize = propt.getValueInt();
                    break;
            }
            rebuildModel();
        } catch (Exception ex) {
        }
    }

    private boolean cl = false;

    @Override
    public boolean compute() {
        //clear
        if (this.clear.getValue()[0]) {
            for (int i = 0; i < this.pixels.length; i++) {
                this.pixels[i] = false;
            }
            return true;
        }
        //write data (on row of screen)
        if (this.clk.getValue()[0]) {
            if (!this.cl) {
                int row_index = Tools.binToDec(this.row.getValue());
                int col_index = Tools.binToDec(this.col.getValue());
                if (row_index < this.height && col_index < this.width) {
                    this.pixels[col_index + row_index * this.width] = this.data.getValue()[0];
                }
                this.cl = true;
                return true;
            }
        } else {
            this.cl = false;
        }
        return false;
    }

    public WorkSpaceObject cloneObject() {
        return new RasterScreen(Tools.copy(super.getPosition()), this.width, this.height, this.pixelSize);
    }

    @Override
    public boolean error() {
        return false;
    }

}
