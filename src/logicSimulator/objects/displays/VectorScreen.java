/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.displays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
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
public class VectorScreen extends WorkSpaceObject {

    /**
     * Width and height of screen (number of pixels) and size of one pixel
     */
    private int width, height;

    /**
     * Pixels and buffer
     */
    private int[] pixels, buffer;

    /**
     * data - input data for one pixel, clear - clear display, clk - clock
     * synchronization impuls for writing data to screen buffer
     */
    private IOPin data, clear, clk;

    private BufferedImage image;

    public void clearData() {
        this.image = null;
        this.pixels = null;
        this.buffer = null;
    }

    /**
     * Create vector screen
     *
     * command: 000 - none, 001 - add X (2x 8 bit), 010 - add Y (2x 8 bit), 011
     * - graphics object type, 100 - color (1x 8 bit), 101 - draw all, 110 -
     * clear
     *
     * graphics object: 000 - line, 001 - circle, 010 - fill circle, 011 -
     * polygon, 100 - fill polyon
     *
     * @param position Position of screen
     * @param width width of screen (number of pixels)
     * @param height height of screen (number of pixels)
     */
    public VectorScreen(Point position, int width, int height) {
        super(position);
        this.width = width;
        this.height = height;
        rebuildModel();
        initGraphicsBuffers();
    }

    private void initGraphicsBuffers() {
        //create image for drawing
        this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        //pixels
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        this.buffer = new int[this.width * this.height];
    }

    private void rebuildModel() {
        //model
        double w_halft = width / 2d;
        double h_halft = height / 2d;
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
                IOPin.MODE.INPUT, 8, "DATA",
                new Point.Double(-w_halft - offset, h + LogicSimulatorCore.WORK_SPACE_STEP)
        );
        this.clk = new IOPin(
                IOPin.MODE.INPUT, 1, "CLK",
                new Point.Double(-w_halft - offset, h + LogicSimulatorCore.WORK_SPACE_STEP * 2)
        );

        model.getIOPins().add(this.clear);
        model.getIOPins().add(this.data);
        model.getIOPins().add(this.clk);
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
            //offset for image
            double w_halft = width / 2d;
            double h_halft = height / 2d;
            int off = (int) (LogicSimulatorCore.WORK_SPACE_STEP - w_halft % LogicSimulatorCore.WORK_SPACE_STEP);
            //draw image
            if (this.image == null) {
                g2.setColor(Color.black);
                g2.fillRect(
                        super.getPosition().x - (int) w_halft - off,
                        super.getPosition().y - (int) h_halft,
                        this.width, this.height
                );
            } else {
                g2.drawImage(
                        this.image,
                        super.getPosition().x - (int) w_halft - off,
                        super.getPosition().y - (int) h_halft,
                        null
                );
            }
        }
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Width", this.width, Propertie.Type.BITS),
            new Propertie("Height", this.height, Propertie.Type.BITS)
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
            }
            rebuildModel();
        } catch (Exception ex) {
        }
    }

    public WorkSpaceObject cloneObject() {
        return new VectorScreen(Tools.copy(super.getPosition()), this.width, this.height);
    }

    @Override
    public boolean error() {
        return false;
    }

    private boolean r_edge = false;

    @Override
    public boolean compute() {
        //clear
        if (this.clear.getValue()[0]) {

            //if buffer is null than init all buffers (it can be null after load from saved project)
            if (this.buffer == null) {
                initGraphicsBuffers();
            }

            for (int i = 0; i < this.pixels.length; i++) {
                this.pixels[i] = -16777216;     //black color
                this.buffer[i] = -16777216;     //black color
                this.pointsBuffer.clear();
                this.value = 0;
                this.color = Color.WHITE;
            }
            return true;
        }
        //clk
        if (this.clk.getValue()[0]) {
            if (!this.r_edge) {
                boolean draw = handleData();
                this.r_edge = true;
                if (draw) {
                    return true;
                }
            }
        } else {
            this.r_edge = false;
        }
        return false;
    }

    /**
     * handle for data receiving and vector object drawing
     * ########################################################################
     */
    private static enum Command {
        ADDX, ADDY, OBJ, COLOR;
    }

    //actual command
    private Command com = null;

    //actual loaded value -> bacuse data in put is only for 8 bits (255 in dec) and that is not enought
    private final List<Boolean> valueBuffer = new ArrayList<>();

    //point bufffer for points
    private final List<Point> pointsBuffer = new ArrayList<>();

    private Point point = null;

    private Color color = Color.WHITE;

    private int value = 0;

    /**
     * Handle input data, call on clock rising edge
     */
    private boolean handleData() {
        //if buffer is null than init all buffers (it can be null after load from saved project)
        if (this.buffer == null) {
            initGraphicsBuffers();
        }

        if (this.value > 0) {
            this.value--;
            //value
            if (this.com != null) {
                switch (this.com) {
                    case ADDX:
                    case ADDY:
                        //add all input dataa bits to value buffer
                        for (boolean b : this.data.getValue()) {
                            this.valueBuffer.add(b);
                        }
                        break;
                    case COLOR:
                        this.color = Tools.get8BitColor(this.data.getValue());
                        break;
                    case OBJ:
                        //choose object, create it and add to the graphicsObject
                        int code = Tools.binToDec(this.data.getValue());
                        switch (code) {
                            case 0:
                                //line
                                if (this.pointsBuffer.size() > 1) {
                                    Tools.drawLine(
                                            this.buffer,
                                            this.width,
                                            this.height,
                                            this.pointsBuffer.get(0),
                                            this.pointsBuffer.get(1),
                                            this.color.getRGB()
                                    );
                                }
                                break;
                            case 1:
                                //circle

                                break;
                            case 2:
                                //fill circle

                                break;
                            case 3:
                                //polygon
                                if (this.pointsBuffer.size() > 2) {
                                    Tools.drawPolygon(
                                            this.buffer,
                                            this.width,
                                            this.height,
                                            this.pointsBuffer,
                                            this.color.getRGB()
                                    );
                                }
                                break;
                            case 4:
                                //fill polygon

                                break;
                        }
                        //clear point buffer
                        if (code >= 0 && code <= 4) {
                            this.pointsBuffer.clear();
                        }
                        break;
                }
            }
        } else {
            //command
            switch (Tools.binToDec(this.data.getValue())) {
                case 1:
                    //if point exist than add to point buffer
                    addPoint();
                    //create new point
                    this.point = new Point(0, 0);
                    this.com = Command.ADDX;
                    //wait for value (2x 8bit)
                    this.value = 2;
                    break;
                case 2:
                    //decode valueBuffer and put it to the x of point
                    this.point.x = Tools.binToDec(this.valueBuffer);
                    this.valueBuffer.clear();
                    this.com = Command.ADDY;
                    //wait for value (2x 8bit)
                    this.value = 2;
                    break;
                case 3:
                    //if point exist than add to point buffer
                    addPoint();
                    //choose object type
                    this.com = Command.OBJ;
                    //wait for value (1x 8bit)
                    this.value = 1;
                    break;
                case 4:
                    //color
                    this.com = Command.COLOR;
                    //wait for value (1x 8bit)
                    this.value = 1;
                    break;
                case 5:
                    //draw
                    System.arraycopy(this.buffer, 0, this.pixels, 0, this.buffer.length);
                    return true;
                case 6:
                    //clear
                    for (int i = 0; i < this.pixels.length; i++) {
                        this.pixels[i] = 0;
                        this.buffer[i] = 0;
                    }
                    this.point = null;
                    this.pointsBuffer.clear();
                    break;

            }
        }
        return false;
    }

    private void addPoint() {
        if (this.point != null) {
            //decode valueBuffer and put it to the y of point
            this.point.y = Tools.binToDec(this.valueBuffer);
            this.valueBuffer.clear();
            this.pointsBuffer.add(this.point);
            this.point = null;
        }
    }

}
