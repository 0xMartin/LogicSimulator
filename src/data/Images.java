/*
 * Logic simlator
 * Author: Martin Krcma
 */
package data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Martin
 */
public class Images {

    public BufferedImage PROJECT_WIZARD_BG;

    public Images() throws IOException {
        this.PROJECT_WIZARD_BG = ImageIO.read(this.getClass().getResource("/src/img/projectWizardBg.png"));
    }

}
