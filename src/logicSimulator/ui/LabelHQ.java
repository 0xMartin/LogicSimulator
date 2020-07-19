/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JLabel;
import logicSimulator.Tools;

/**
 *
 * @author Martin
 */
public class LabelHQ extends JLabel {

    public LabelHQ(){
        super();
    }
    
    public LabelHQ(Icon icon) {
        super(icon);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Tools.setHighQuality(g2);
        super.paintComponent(g);
    }

}
