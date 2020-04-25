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
public class BitMul extends BitAdd {

    public BitMul(Point position, int bits) {
        super(position, bits);
        super.title = "×";
    }

    @Override
    public boolean compute() {
        long a = Convert.bitsToLong(super.A.getValue());
        long b = Convert.bitsToLong(super.B.getValue());

        long c = a * b;

        boolean[] ret = new boolean[super.bits];
        boolean[] cout = new boolean[super.bits];

        for (int i = 0; i < this.bits; i++) {
            ret[i] = (c >> i & 0x1) == 1;
        }
        for (int i = 0; i < this.bits; i++) {
            cout[i] = (c >> (i + this.bits) & 0x1) == 1;
        }

        return super.OUT.setValue(ret) || this.COUT.setValue(cout);
    }

    @Override
    public void restore() {
        this.title = "×";
    }

    @Override
    public Propertie[] getProperties() {
        return new Propertie[]{
            new Propertie("Bits", this.bits, Propertie.Type.BITS)
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
    public BitMul cloneObject() {
        return new BitMul(Tools.copy(super.getPosition()), super.bits);
    }

    @Override
    public boolean error() {
        return false;
    }

}
