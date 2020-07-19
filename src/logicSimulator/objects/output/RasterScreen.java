/* 
 * Copyright (C) 2020 Martin Krcma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package logicSimulator.objects.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
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
    private transient boolean[] pixels;

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
        //model
        int w_halft = (int) (pixelSize * width / 2d);
        int h_halft = (int) (pixelSize * height / 2d);
        int offset = LogicSimulatorCore.WORK_SPACE_STEP - w_halft % LogicSimulatorCore.WORK_SPACE_STEP;
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-w_halft - offset, -h_halft, w_halft - offset, -h_halft));
        GOList.add(new Line(-w_halft - offset, h_halft, w_halft - offset, h_halft));
        GOList.add(new Line(-w_halft - offset, h_halft, -w_halft - offset, -h_halft));
        GOList.add(new Line(w_halft - offset, h_halft, w_halft - offset, -h_halft));
        double h = h_halft % LogicSimulatorCore.WORK_SPACE_STEP - h_halft;

        //io pins
        this.clear = new IOPin(
                IOPin.MODE.INPUT, 1, "CLEAR",
                new Point.Double(-w_halft - offset, h)
        );
        this.data = new IOPin(
                IOPin.MODE.INPUT, 1, "DATA",
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
        model.getIOPins().add(this.clk);
        model.getIOPins().add(this.row);
        model.getIOPins().add(this.col);
        model.getIOPins().add(this.data);

        super.setModel(model);
        model.disableRotation();
        model.computeSize();
        
        this.pixels = new boolean[this.width * this.height];
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        //draw model
        boolean stat = super.getModel().renderModel(g2, super.getPosition(), offset, screen, super.isSelected());

        if (stat) {
            try {
                //if pixels buffer is null than create new
                if (this.pixels == null) {
                    this.pixels = new boolean[this.width * this.height];
                }
                //draw pixels
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
            } catch (Exception ex) {
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
            int wl = this.width, hl = this.height, ps = this.pixelSize;
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
            if (wl != this.width || hl != this.height || ps != this.pixelSize) {
                rebuildModel();
            }
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
                int row_index = Convert.bitsToInt(this.row.getValue());
                int col_index = Convert.bitsToInt(this.col.getValue());
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

    @Override
    public WorkSpaceObject cloneObject() {
        return new RasterScreen(Tools.copy(super.getPosition()), this.width, this.height, this.pixelSize);
    }

    @Override
    public boolean error() {
        return false;
    }

}
