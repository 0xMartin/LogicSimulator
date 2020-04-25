package logicSimulator.common;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class WrappingFlowLayout extends FlowLayout {

    public WrappingFlowLayout(int align) {
        super(align);
    }

    @Override
    public Dimension minimumLayoutSize(Container container) {
        return computeMinSize(container);
    }

    @Override
    public Dimension preferredLayoutSize(Container container) {
        return computeSize(container);
    }

    private Dimension computeMinSize(Container target) {
        synchronized (target.getTreeLock()) {
            int minWidth = Integer.MAX_VALUE;
            int minHeight = Integer.MAX_VALUE;

            for (int i = 0; i < target.getComponentCount(); i++) {
                Component c = target.getComponent(i);
                if (c.isVisible()) {
                    Dimension d = c.getPreferredSize();
                    minWidth = Math.min(minWidth, d.width);
                    minHeight = Math.min(minHeight, d.height);
                }
            }

            if (minWidth != Integer.MAX_VALUE && minHeight != Integer.MIN_VALUE) {
                return new Dimension(minWidth, minHeight);
            } else {
                return new Dimension(0, 0);
            }
        }
    }

    private Dimension computeSize(Container container) {
        synchronized (container.getTreeLock()) {
            int width = container.getWidth();
            int hGap = getHgap();
            int vGap = getVgap();

            if (width == 0) {
                width = Integer.MAX_VALUE;
            }

            int reqdWidth = 0;
            int maxwidth = width - hGap * 2;
            int x = 0, y = vGap;
            int rowHeight = 0;

            for (int i = 0; i < container.getComponentCount(); i++) {
                Component c = container.getComponent(i);
                if (c.isVisible()) {
                    Dimension d = c.getPreferredSize();
                    if ((x == 0) || (x + d.width <= maxwidth)) {
                        if (x > 0) {
                            x += hGap;
                        }
                        x += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    } else {
                        x = d.width;
                        y += vGap + rowHeight;
                        rowHeight = d.height;
                    }
                    reqdWidth = Math.max(reqdWidth, x);
                }
            }

            y += rowHeight;
            return new Dimension(reqdWidth, y + 3);
        }
    }

}
