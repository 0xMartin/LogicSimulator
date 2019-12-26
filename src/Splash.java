
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JWindow;
import logicSimulator.Tools;

/*
 * Logic simlator
 * Author: Martin Krcma
 */

/**
 *
 * @author Martin
 */
public class Splash extends JWindow {

    private final BufferedImage img;
    
    public Splash(String res, int width, int height) throws IOException {
                
        BufferedImage iDef = ImageIO.read(this.getClass().getResource("/src/img/splash.png"));
        this.img = Tools.resizeImage(iDef, width, height);
        
        this.setSize(this.img.getWidth(null), this.img.getHeight(null));
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (screen.width - this.img.getWidth(null)) / 2,
                (screen.height - this.img.getHeight(null)) / 2
        );   
        this.setBackground(new Color(0,0,0,0));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Tools.setHighQuality(g2);
        g2.drawImage(this.img, 0, 0, this);
    }

}
