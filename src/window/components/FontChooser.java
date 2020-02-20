/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author Martin
 */
public class FontChooser extends JDialog {

    private Font font = null;

    private final Font[] fonts;

    private int fSize = 16;

    private boolean choose = false;

    public FontChooser(Frame frame) {
        super(frame, true);
        this.setTitle("Font chooser");
        this.setLayout(new BorderLayout());
        this.fonts = FontChooser.getAllFonts();
        this.setSize(500, 300);
        this.setAlwaysOnTop(true);
    }

    private void initComponents() {
        //font priview
        JTextField fontPriview = new JTextField("Font priview");
        fontPriview.setBorder(null);
        fontPriview.setOpaque(false);
        fontPriview.setHorizontalAlignment(JTextField.CENTER);

        //list with all fonts
        String[] fNames = new String[this.fonts.length];
        for (int i = 0; i < this.fonts.length; i++) {
            fNames[i] = this.fonts[i].getName();
        }
        JList list = new JList(fNames);
        list.addListSelectionListener((ListSelectionEvent e) -> {
            //find font by name in list and if match than set font as "font" seleted
            String fName = list.getSelectedValue().toString();
            for (Font f : this.fonts) {
                if (f.getName().toLowerCase().equals(fName.toLowerCase())) {
                    this.font = f;
                    this.font = this.font.deriveFont((float) this.fSize);
                    fontPriview.setFont(this.font);
                    break;
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        this.add(scrollPane, BorderLayout.LINE_START);

        //set default font
        if (this.font == null) {
            list.setSelectedIndex(0);
        } else {
            int index = 0;
            for (Font f : this.fonts) {
                if (f.getName().toLowerCase().equals(this.font.getName().toLowerCase())) {
                    list.setSelectedIndex(index);
                    break;
                }
                index++;
            }
        }

        //button
        JButton b = new JButton("OK");
        b.addActionListener((ActionEvent e) -> {
            this.setVisible(false);
            this.choose = true;
        });
        this.add(b, BorderLayout.PAGE_END);

        //left panel
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        //size of font
        JTextField size = new JTextField(this.fSize + "");
        size.addActionListener((ActionEvent e) -> {
            try {
                this.fSize = Integer.parseInt(size.getText());
                this.font = this.font.deriveFont((float) this.fSize);
                fontPriview.setFont(this.font);
            } catch (Exception ex) {
            }
        });
        p2.add(fontPriview, BorderLayout.CENTER);
        p2.add(size, BorderLayout.PAGE_START);

        this.add(p2, BorderLayout.CENTER);
    }

    public Font selectedFont() {
        return this.font;
    }

    public static Font showDialog(Frame frame, Font font) {
        FontChooser fc = new FontChooser(frame);
        if (font != null) {
            fc.font = font;
            fc.fSize = font.getSize();
        }
        fc.initComponents();

        if (frame == null) {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            fc.setLocation(
                    (screen.width - fc.getWidth()) / 2,
                    (screen.height - fc.getHeight()) / 2
            );
        }

        fc.setVisible(true);
        return fc.choose ? fc.selectedFont() : null;
    }

    public static Font[] getAllFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (ge == null) {
            return null;
        } else {
            return ge.getAllFonts();
        }
    }

}
