package window.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;
import logicSimulator.Tools;

public class Icon20 extends ImageIcon {

    public Icon20(URL location) {
        super(location);

        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = (Graphics2D) img.getGraphics();
        Tools.setHighQuality(g2);

        g2.drawImage(getImage(), 0, 0, 20, 20, null);

        setImage(img);
    }

    @Override
    public int getIconHeight() {
        return 20;
    }

    @Override
    public int getIconWidth() {
        return 20;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(getImage(), x, y, c);
    }
}
