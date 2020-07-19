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
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.LinkASM;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.common.SerialDataDriver;
import logicSimulator.ui.SystemResources;

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
     * data - input data for one pixel, clear - clear display, clk - clock
     * synchronization impuls for writing data to screen buffer
     */
    private IOPin data, clear, clk;

    private transient BufferedImage image, buffer;

    private transient Graphics2D bufferG;

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
        initScreen();
        initSerilaDataDriver();
    }

    /**
     * Init visible screen image and buffer image
     */
    private void initScreen() {
        //create image for drawing
        this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        //buffet
        this.buffer = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        this.bufferG = (Graphics2D) this.buffer.getGraphics();
        Tools.setHighQuality(this.bufferG);
    }

    private void rebuildModel() {
        //model
        int w_halft = (int) (width / 2d);
        int h_halft = (int) (height / 2d);
        int offset = LogicSimulatorCore.WORK_SPACE_STEP - w_halft % LogicSimulatorCore.WORK_SPACE_STEP;
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-w_halft - offset, -h_halft, w_halft - offset, -h_halft));
        GOList.add(new Line(-w_halft - offset, h_halft, w_halft - offset, h_halft));
        GOList.add(new Line(-w_halft - offset, h_halft, -w_halft - offset, -h_halft));
        GOList.add(new Line(w_halft - offset, h_halft, w_halft - offset, -h_halft));

        //io pins
        double h = h_halft % LogicSimulatorCore.WORK_SPACE_STEP - h_halft;
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
        model.computeSize();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        //render model
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
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new VectorScreen(Tools.copy(super.getPosition()), this.width, this.height);
    }

    @Override
    public boolean error() {
        return false;
    }

    /**
     * Return all command for vecter screen
     *
     * @return
     */
    public static final LinkASM[] getInstructions() {
        //list with all instruction for this component
        return new LinkASM[]{
            //instructions
            new LinkASM("POINT", "0x1", "Create and add point to buffer, 2x 16bit, One value -> [2 x 8bit : 1. (8 bit) lower weight, 2. (8 bit) higher weight]", true),
            new LinkASM("DTB", "0x2", "Draw object to the back buffer 1x object_type", true),
            new LinkASM("COLOR", "0x3", "Set color of drawing [1x 8bit : RRRGGGBB]", true),
            new LinkASM("DRAW", "0x4", "Draw all", true),
            new LinkASM("CLR", "0x5", "Clear buffer screen", true),
            new LinkASM("LDSTR", "0x6", "Load string [1x 8bit : length of string], string stay in memory, if you want to load new than you must use CLRSTR", true),
            new LinkASM("CLRSTR", "0x7", "Clear string", true),
            new LinkASM("STRSIZE", "0x8", "Set size of text", true),
            new LinkASM("IMGID", "0x9", "Set ID of image 1x 8bit", true),
            new LinkASM("SCALE", "0xA", "Change scale 1x 8bit (scale = hex_value / 50f)", true),
            //objects
            new LinkASM("LINE", "0x0", "Syntax: OBJ LINE, 2x point", false),
            new LinkASM("CIRCLE", "0x1", "Syntax: OBJ CIRCLE, 2x point [1. center, 2. width and height]", false),
            new LinkASM("FILL_CIRCLE", "0x2", "Syntax: OBJ FILL_CIRCLE, 2x point [1. center, 2. width and height]", false),
            new LinkASM("POLYGON", "0x3", "Syntax: OBJ POLYGON, min 3 points", false),
            new LinkASM("FILL_POLYGON", "0x4", "Syntax: OBJ FILL_POLYGON, min 3 points", false),
            new LinkASM("STRING", "0x5", "Syntax: OBJ STRING, Draw string, in buffer must be position of string and chars", false),
            new LinkASM("IMAGE", "0x6", "Syntax: OBJ IMAGE", false)
        };
    }

    @Override
    public void restore() {
        //init screen
        this.initScreen();
        //init serial data driver
        this.sdd = new SerialDataDriver();
        this.initSerilaDataDriver();
    }

    private boolean r_edge = false;

    @Override
    public boolean compute() {
        //clear
        if (this.clear.getValue()[0]) {
            
            //clear screnn buffer
            this.bufferG.setColor(Color.white);
            this.bufferG.clearRect(0, 0, this.width, this.height);
            
            //draw buffer
            this.image.setData(this.buffer.getData());
            
            //clear hadler data
            this.points.clear();
            this.newPoint = null;
            this.str = null;
            this.sdd.getBitBuffer().clear();
            this.sdd.setValuesCount(0);
            
            //scale
            this.scale = 1f;
            
            return true;
        }

        //clk
        if (this.clk.getValue()[0]) {
            if (!this.r_edge) {
                //hadle action
                boolean draw = this.sdd.handleValue(this.data.getValue());
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
    private transient List<Point> points = new ArrayList<>();

    private transient Point newPoint = null;

    private transient String str = null;

    private transient int imgID = 0;

    private transient float scale = 1f;

    private transient SerialDataDriver sdd = new SerialDataDriver();

    private void initSerilaDataDriver() {
        this.points = new ArrayList<>();

        //POINT
        this.sdd.addInstructionListener(1, 4, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 1, (ActionEvent e) -> {
            switch (e.getID()) {
                case 1:
                    //set Y value
                    this.newPoint.y = this.sdd.getBitBufferValue();
                    //clear buffer
                    this.sdd.getBitBuffer().clear();
                    //clear buffer
                    //this.sdd.getBitBuffer().clear();
                    break;
                case 3:
                    //create new point and add to list
                    this.newPoint = new Point(0, 0);
                    this.points.add(this.newPoint);
                    //set X value
                    this.newPoint.x = this.sdd.getBitBufferValue();
                    //clear buffer
                    this.sdd.getBitBuffer().clear();
                    break;
            }
        });

        //OBJ
        this.sdd.addInstructionListener(2, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 2, (ActionEvent e) -> {
            this.bufferG.scale(this.scale, this.scale);
            switch (this.sdd.getBitBufferValue()) {
                case 0:
                    //draw line
                    if (this.points.size() > 1) {
                        Point p1 = this.points.get(0);
                        Point p2 = this.points.get(1);
                        this.bufferG.drawLine(p1.x, p1.y, p2.x, p2.y);
                        this.points.clear();
                    }
                    break;
                case 1:
                    //circle
                    if (this.points.size() > 1) {
                        Point p1 = this.points.get(0);
                        Point p2 = this.points.get(1);
                        this.bufferG.drawArc(p1.x, p1.y, p2.x, p2.y, 0, 360);
                        this.points.clear();
                    }
                    break;
                case 2:
                    //fill circle
                    if (this.points.size() > 1) {
                        Point p1 = this.points.get(0);
                        Point p2 = this.points.get(1);
                        this.bufferG.fillArc(p1.x, p1.y, p2.x, p2.y, 0, 360);
                        this.points.clear();
                    }
                    break;
                case 3:
                    //polygon
                    if (this.points.size() > 2) {
                        int[] xArr = new int[this.points.size()], yArr = new int[this.points.size()];
                        for (int i = 0; i < this.points.size(); i++) {
                            xArr[i] = this.points.get(i).x;
                            yArr[i] = this.points.get(i).y;
                        }
                        this.bufferG.drawPolygon(xArr, yArr, this.points.size());
                        this.points.clear();
                    }
                    break;
                case 4:
                    //fill polygon
                    if (this.points.size() > 2) {
                        int[] xArr = new int[this.points.size()], yArr = new int[this.points.size()];
                        for (int i = 0; i < this.points.size(); i++) {
                            xArr[i] = this.points.get(i).x;
                            yArr[i] = this.points.get(i).y;
                        }
                        this.bufferG.fillPolygon(xArr, yArr, this.points.size());
                        this.points.clear();
                    }
                    break;
                case 5:
                    //draw string
                    if (this.points.size() > 0 && this.str != null) {
                        Point p1 = this.points.get(0);
                        this.bufferG.drawString(this.str, p1.x, p1.y);
                        this.points.clear();
                    }
                    break;
                case 6:
                    //img
                    if (this.imgID >= 0 && this.imgID < SystemResources.IMG_RES.size()) {
                        this.bufferG.drawImage(SystemResources.IMG_RES.get(this.imgID),
                                this.points.get(0).x, this.points.get(0).y, null);
                        this.points.clear();
                    }
                    break;
            }
            this.bufferG.scale(1f / this.scale, 1f / this.scale);

            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //COLOR
        this.sdd.addInstructionListener(3, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 3, (ActionEvent e) -> {
            //set color
            this.bufferG.setColor(Tools.get8BitColor(this.sdd.getBitBufferValue()));
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //DRAW
        this.sdd.addInstructionListener(4, 0, (ActionEvent e) -> {
            //draw image
            this.image.setData(this.buffer.getData());
        });

        //CLR
        this.sdd.addInstructionListener(5, 0, (ActionEvent e) -> {
            //clear buffer
            this.bufferG.clearRect(0, 0, (int) (this.width / this.scale),
                    (int) (this.height / this.scale));
        });

        //LDSTR
        this.sdd.addInstructionListener(6, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 6, (ActionEvent e) -> {
            //set length of string
            if (this.str == null) {
                this.str = "";
                this.sdd.setValuesCount(this.sdd.getBitBufferValue() + 1);
            } else {
                this.str += (char) this.sdd.getBitBufferValue();
            }
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //CLRSTR
        this.sdd.addInstructionListener(7, 0, (ActionEvent e) -> {
            this.str = null;
        });

        //TXTSIZE
        this.sdd.addInstructionListener(8, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 8, (ActionEvent e) -> {
            //set text size
            this.bufferG.setFont(this.bufferG.getFont().deriveFont((float) this.sdd.getBitBufferValue()));
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //IMGID
        this.sdd.addInstructionListener(9, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 9, (ActionEvent e) -> {
            //load img id
            this.imgID = this.sdd.getBitBufferValue();
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

        //SCALE
        this.sdd.addInstructionListener(10, 1, null);
        this.sdd.addValueListener(SerialDataDriver.SOME_VALUE, 10, (ActionEvent e) -> {
            this.scale = this.sdd.getBitBufferValue() / 50f;
            //clear buffer
            this.sdd.getBitBuffer().clear();
        });

    }

}
