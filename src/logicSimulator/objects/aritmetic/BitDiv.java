/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.aritmetic;

import java.awt.Point;
import logicSimulator.Convert;
import logicSimulator.Tools;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class BitDiv extends BitAdd {

    public BitDiv(Point position, int bits) {
        super(position, bits);
        super.title = "÷";
    }

    @Override
    public boolean compute() {
        long a = Convert.bitsToLong(super.A.getValue());
        long b = Convert.bitsToLong(super.B.getValue());
        
        long c = b == 0 ? 0 : a / b;

        boolean[] ret = new boolean[super.bits];
        for (int i = 0; i < this.bits; i++) {
            ret[i] = (c >> i & 0x1) == 1;
        }

        return this.OUT.setValue(ret);
    }

    @Override
    public void restore() {
        this.title = "÷";
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", super.bits, Propertie.Type.BITS)
        };
    }

    @Override
    public void changePropertie(Propertie propt) {
        try {
            switch (propt.getName()) {
                case "Bits":
                    super.bits = propt.getValueInt();
                    super.getModel().getIOPins().stream().forEach((pin) -> {
                        pin.changeBitWidth(super.bits);
                    });
                    break;
            }
        } catch (NumberFormatException ex) {
        }
    }

    @Override
    public BitDiv cloneObject() {
        return new BitDiv(Tools.copy(super.getPosition()), super.bits);
    }

    @Override
    public boolean error() {
        return false;
    }

}
