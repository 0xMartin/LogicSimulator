/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.aritmetic;

import java.awt.Point;
import logicSimulator.Tools;
import logicSimulator.objects.IOPin;

/**
 *
 * @author Martin
 */
public class BitSub extends BitAdd {

    private final IOPin BIN, BOUT;
    
    public BitSub(Point position, int bits) {
        super(position, bits);
        super.title = "-";
        this.BIN = super.CIN;
        this.BIN.setLabel("BIN");
        this.BOUT = super.COUT;
        this.BOUT.setLabel("BOUT");
    }

    @Override
    public boolean compute() {
        boolean[] vA = super.A.getValue();
        boolean[] vB = super.B.getValue();

        boolean[] ret = new boolean[super.bits];
        boolean borrow = this.BIN.getValue()[0];
        for (int i = 0; i < super.bits; i++) {
            ret[i] = vA[i] ^ vB[i] ^ borrow;
            //borrow out
            borrow = !vA[i] && vB[i] || borrow && (!vA[i] || vB[i]);
        }

        this.BOUT.getValue()[0] = borrow;
        return super.OUT.setValue(ret) || borrow;
    }

    @Override
    public void restore() {
        this.title = "-";
    }
    
    @Override
    public BitSub cloneObject() {
        return new BitSub(Tools.copy(super.getPosition()), super.bits);
    }

    @Override
    public boolean error() {
        return false;
    }

}
