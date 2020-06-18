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
package logicSimulator.objects.input;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.graphics.GraphicsObject;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Propertie;
import logicSimulator.common.SerialIO;
import logicSimulator.graphics.GString;
import logicSimulator.ui.Fonts;

/**
 *
 * @author Martin
 */
public class SerialOutputTrigger extends WorkSpaceObject implements SerialIO {
    
    //id of input trigger
    private final GString ID;

    //output pin
    private final IOPin pin;

    //set and reset byte
    private byte setByte = 0b00000001, resetByte = 0b00000000;

    public SerialOutputTrigger(Point position, String id) {
        super(position);

        //g string id
        this.ID = new GString(new Point2D.Double(
                25, 0),
                Fonts.MEDIUM.getSize(), id);

        //model
        List<GraphicsObject> GOList = new ArrayList<>();
        Model model = new Model(GOList);
        GOList.add(new Line(-14, -14, 0, 0));
        GOList.add(new Line(-14, 14, 0, 0));
        GOList.add(new Line(0, -14, 14, 0));
        GOList.add(new Line(0, 14, 14, 0));
        GOList.add(new Line(-14, 0, 0, 0));
        GOList.add(this.ID);

        //output pin
        this.pin = new IOPin(IOPin.MODE.INPUT, 1, "TRIGGER IN", new Point.Double(-14, 0));
        model.getIOPins().add(this.pin);

        super.setModel(model);
        model.computeSize();
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("ID", this.ID.getString()),
            new Propertie("SET BYTE", this.setByte),
            new Propertie("RESET BYTE", this.resetByte)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        switch (propt.getName()) {
            case "ID":
                this.ID.setString(propt.getValueString());
                this.ID.position.x = -25 - this.ID.getString().length() * Fonts.MEDIUM.getSize() / 2;
                break;
            case "SET BYTE":
                break;
            case "RESET BYTE":
                break;
        }
    }

    @Override
    public boolean compute() {
        return false;
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new SerialOutputTrigger(Tools.copy(super.getPosition()), this.ID.getString());
    }

    @Override
    public String getID() {
        return this.ID.getString();
    }

    @Override
    public String getType() {
        return "Output Trigger";
    }

    @Override
    public byte getSetValue() {
        return this.setByte;
    }

    @Override
    public byte getResetValue() {
        return this.resetByte;
    }

    @Override
    public void set() {

    }

    @Override
    public void reset() {

    }

    @Override
    public boolean getValue() {
        return this.pin.getValue()[0];
    }

}
