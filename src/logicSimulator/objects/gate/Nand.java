/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.gate;

import logicSimulator.common.Propertie;
import logicSimulator.objects.IOPin;
import java.awt.Point;
import logicSimulator.common.Model;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;

/**
 *
 * @author Martin
 */
public class Nand extends WorkSpaceObject {

    protected int inputs, bits;

    protected IOPin OUT;

    /**
     * Create Nand gate
     *
     * @param position Position on workspace
     * @param bits Bits per wire
     * @param inputs Number of inputs
     */
    public Nand(Point position, int bits, int inputs) {
        super(position);
        this.bits = bits;
        this.inputs = Math.max(inputs, 2);
        super.setModel(new Model());
        this.OUT = And.createModel(super.getModel(), this.inputs, this.bits, true);
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.bits, Propertie.Type.BITS),
            new Propertie("Inputs", this.inputs, Propertie.Type.INPUTS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    this.bits = propt.getValueInt();
                    super.getPins().forEach((pin) -> {
                        pin.changeBitWidth(this.bits);
                    });
                    break;
                case "Inputs":
                    this.inputs = propt.getValueInt();
                    this.OUT = And.createModel(super.getModel(), this.inputs, this.bits, true);
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public Nand cloneObject() {
        Nand ret = new Nand(Tools.copy(super.getPosition()), this.bits, this.inputs);
        ret.getModel().rotate(super.getModel().getAngle());
        return ret;
    }

    @Override
    public boolean compute() {
        //and fuction
        boolean[] out = new boolean[this.bits];
        for (int i = 0; i < this.bits; i++) {
            boolean result = true;
            for (IOPin pin : super.getPins()) {
                if (pin.mode == IOPin.MODE.INPUT) {
                    if (pin.getValue()[i]) {
                        result = false;
                        break;
                    }
                }
            }
            out[i] = result;
        }
        //write value
        return this.OUT.setValue(out);
    }

}
