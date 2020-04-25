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
public class JKFlipFlop extends RSFlipFlop {

    private final IOPin CLK;

    public JKFlipFlop(Point position) {
        super(position);
        super.title = "JK";
        super.SET.setLabel("J");
        super.RESET.setLabel("K");
        this.CLK = new IOPin(IOPin.MODE.INPUT, 1, "CLK", new Point.Double(-28.0, 0));
        this.CLK.drawClkSymbol = true;
        super.getModel().getIOPins().add(this.CLK);
    }

    private boolean r_edge = true;

    @Override
    public boolean compute() {
        if (this.CLK.getValue()[0]) {
            if (this.r_edge) {
                boolean[] setBits = super.SET.getValue();
                boolean[] resetBits = super.RESET.getValue();
                boolean[] out = super.OUT.getValue();
                //set and reset bits
                if (setBits[0] && resetBits[0]) {
                    out[0] = !out[0];
                } else if (setBits[0]) {
                    out[0] = true;
                } else if (resetBits[0]) {
                    out[0] = false;
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
        this.title = "JK";
    }

    @Override
    public WorkSpaceObject cloneObject() {
        return new JKFlipFlop(
                Tools.copy(super.getPosition())
        );
    }

}
