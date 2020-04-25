/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.objects.memory;

import java.awt.Point;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.IOPin;

/**
 *
 * @author Martin
 */
public class TFlipFlop extends RSFlipFlop {

    private final IOPin CLK, T;

    public TFlipFlop(Point position) {
        super(position);
        super.title = "T";
        this.T = this.SET;
        this.T.setLabel("T");
        this.CLK = this.RESET;
        this.CLK.setLabel("");
        this.CLK.drawClkSymbol = true;
    }

    private boolean r_edge = true;

    @Override
    public boolean compute() {
        if (this.CLK.getValue()[0]) {
            if (this.r_edge) {
                boolean[] dBits = super.SET.getValue();
                boolean[] out = super.OUT.getValue();
                //output change value if T is high
                if (dBits[0]) {
                    out[0] = !out[0];
                }
                super.N_OUT.getValue()[0] = !out[0];
                this.r_edge = false;
                return true;
            }
        } else {
            this.r_edge = true;
        }
        return false;
    }

    @Override
    public void restore() {
        this.title = "T";
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new TFlipFlop(
                Tools.copy(super.getPosition())
        );
    }

}
