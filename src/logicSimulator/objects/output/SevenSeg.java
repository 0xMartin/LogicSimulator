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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import logicSimulator.Convert;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.graphics.Line;
import logicSimulator.objects.IOPin;

/**
 *
 * @author Martin
 */
public class SevenSeg extends WorkSpaceObject {
    
    private enum SEG {
        LEFT_U,
        RIGHT_U,
        LEFT_D,
        RIGHT_D,
        TOP,
        MID,
        BOT
    }

    //map with digits
    private static transient final SEG[][] DIGITMAP = new SEG[][]{
        {SEG.LEFT_U, SEG.RIGHT_U, SEG.LEFT_D, SEG.RIGHT_D, SEG.TOP, SEG.BOT}, //0
        {SEG.RIGHT_U, SEG.RIGHT_D}, //1
        {SEG.RIGHT_U, SEG.LEFT_D, SEG.TOP, SEG.MID, SEG.BOT}, //2
        {SEG.RIGHT_U, SEG.RIGHT_D, SEG.TOP, SEG.MID, SEG.BOT}, //3
        {SEG.LEFT_U, SEG.RIGHT_U, SEG.RIGHT_D, SEG.MID}, //4
        {SEG.LEFT_U, SEG.RIGHT_D, SEG.TOP, SEG.MID, SEG.BOT}, //5
        {SEG.LEFT_U, SEG.LEFT_D, SEG.RIGHT_D, SEG.TOP, SEG.MID, SEG.BOT}, //6
        {SEG.RIGHT_U, SEG.RIGHT_D, SEG.TOP}, //7
        {SEG.LEFT_U, SEG.RIGHT_U, SEG.LEFT_D, SEG.RIGHT_D, SEG.TOP, SEG.MID, SEG.BOT}, //8
        {SEG.LEFT_U, SEG.RIGHT_U, SEG.RIGHT_D, SEG.TOP, SEG.MID, SEG.BOT}, //9
        {SEG.LEFT_U, SEG.RIGHT_U, SEG.LEFT_D, SEG.RIGHT_D, SEG.TOP, SEG.MID}, //A
        {SEG.LEFT_U, SEG.LEFT_D, SEG.RIGHT_D, SEG.MID, SEG.BOT}, //b
        {SEG.LEFT_U, SEG.LEFT_D, SEG.TOP, SEG.BOT}, //c
        {SEG.RIGHT_U, SEG.LEFT_D, SEG.RIGHT_D, SEG.MID, SEG.BOT}, //d
        {SEG.LEFT_U, SEG.LEFT_D, SEG.TOP, SEG.MID, SEG.BOT}, //E
        {SEG.LEFT_U, SEG.LEFT_D, SEG.TOP, SEG.MID}, //F
    };
    
    private final IOPin INPUT;
    
    private int DIGIT = 0;
    
    private Color color = Color.RED;
    
    public SevenSeg(Point position) {
        super(position);

        //model
        Model model = new Model();
        List<GraphicsObject> GOList = model.getGraphicsObjects();
        GOList.add(new Line(-28, 42, -28, -42));
        GOList.add(new Line(28, 42, 28, -42));
        GOList.add(new Line(-28, -42, 28, -42));
        GOList.add(new Line(-28, 42, 28, 42));

        //pins
        this.INPUT = new IOPin(IOPin.MODE.INPUT, 4, "", new Point.Double(0.0, 42.0));
        model.getIOPins().add(this.INPUT);
        
        super.setModel(model);
        model.computeSize();
    }
    
    @Override
    public void render(Graphics2D g2, Point offset, Dimension screen) {
        Point pos = super.getPosition();
        boolean stat = super.getModel().renderModel(g2, pos, offset, screen, super.isSelected());
        //draw digit
        if (stat) {
            g2.setColor(this.color);
            g2.setStroke(new BasicStroke(4));
            if (this.DIGIT >= 0 && this.DIGIT < SevenSeg.DIGITMAP.length) {
                drawDigit(SevenSeg.DIGITMAP[this.DIGIT], g2, pos);
            }
        }
    }
    
    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Color", this.color.getRGB(), Propertie.Type.COLOR)
        };
    }
    
    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Color":
                    this.color = new Color(propt.getValueInt());
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }
    
    @Override
    public boolean compute() {
        int number = Convert.bitsToInt(super.getModel().getIOPins().get(0).getValue());
        if (number >= 0 && number <= 15) {
            this.DIGIT = number;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean error() {
        return false;
    }
    
    @Override
    public SevenSeg cloneObject() {
        SevenSeg ret = new SevenSeg(Tools.copy(super.getPosition()));
        ret.color = new Color(this.color.getRGB());
        return ret;
    }

    /**
     * Draw digit into display
     *
     * @param segs Map with SEGMETS that must draw
     * @param g2 Graphics2D
     * @param pos Center position of seven seg
     */
    private void drawDigit(SEG[] segs, Graphics2D g2, Point pos) {
        for (SEG s : segs) {
            switch (s) {
                case LEFT_U:
                    g2.drawLine(-20 + pos.x, -29 + pos.y, -20 + pos.x, -5 + pos.y);
                    break;
                case LEFT_D:
                    g2.drawLine(-20 + pos.x, 29 + pos.y, -20 + pos.x, 5 + pos.y);
                    break;
                case RIGHT_U:
                    g2.drawLine(20 + pos.x, -29 + pos.y, 20 + pos.x, -5 + pos.y);
                    break;
                case RIGHT_D:
                    g2.drawLine(20 + pos.x, 29 + pos.y, 20 + pos.x, 5 + pos.y);
                    break;
                case TOP:
                    g2.drawLine(-15 + pos.x, -34 + pos.y, 15 + pos.x, -34 + pos.y);
                    break;
                case MID:
                    g2.drawLine(-15 + pos.x, pos.y, 15 + pos.x, pos.y);
                    break;
                case BOT:
                    g2.drawLine(-15 + pos.x, 34 + pos.y, 15 + pos.x, 34 + pos.y);
                    break;
            }
        }
    }
    
}
