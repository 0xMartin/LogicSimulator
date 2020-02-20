package window.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import logicSimulator.Tools;

public class HexEditorArea extends JScrollPane {

    private final JTextArea number, hex;
    private final JTextPane code;

    public String fontName = "Consolas";
    public int fontSize = 16;

    private final List<Integer> errorSigns = new ArrayList<>();

    private final List<LinkASM> translator = new ArrayList<>();

    //coloring AttributeSets
    private final StyleContext sc = StyleContext.getDefaultStyleContext();
    private final AttributeSet asetCommand = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
    private final AttributeSet asetValueHex = sc.addAttribute(
            sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true),
            StyleConstants.Foreground, Color.GREEN.darker()
    );
    private final AttributeSet asetValueBin = sc.addAttribute(
            sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true),
            StyleConstants.Foreground, Color.MAGENTA
    );
    private final AttributeSet asetComment = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.GRAY);
    private final AttributeSet asetBlack = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

    private final StyledDocument doc = new DefaultStyledDocument() {
        @Override
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offset, str, a);
            colorizeWords();
        }

        public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
            colorizeWords();
        }

    };

    public HexEditorArea() {
        super();

        translator.add(new LinkASM("ADD", "0x01"));
        translator.add(new LinkASM("MOV", "0x02"));
        translator.add(new LinkASM("RMV", "0x03"));
        translator.add(new LinkASM("JMP", "0x04"));
        translator.add(new LinkASM("EQ", "0x05"));

        //textarea with number of line
        this.number = new JTextArea(" 1 ");
        this.number.setForeground(Color.BLUE);
        this.number.setBackground(Color.white);
        this.number.setEditable(false);

        //text area with code
        this.code = new JTextPane(this.doc) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                //vertical line divider
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, 0, 0, super.getHeight());
                //sigs
                try {
                    g2.setColor(Color.RED);
                    int height = g2.getFontMetrics().getHeight();
                    errorSigns.stream().forEach((sign) -> {
                        g2.fillRect(0, sign * height, 3, height);
                    });
                } catch (Exception ex) {
                }
            }
        };
        this.code.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                translate();
            }
        });
        this.doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                codeChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                codeChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        this.code.setBackground(Color.white);

        this.hex = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                //vertical line divider
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, 0, 0, super.getHeight());
            }
        };
        this.hex.setForeground(Color.GRAY);
        this.hex.setEditable(false);

        this.refreshFont(this.fontSize);

        JPanel body = new JPanel();
        body.setLayout(new BorderLayout());

        body.add(this.number, BorderLayout.WEST);
        body.add(this.code, BorderLayout.CENTER);
        body.add(this.hex, BorderLayout.EAST);

        super.setViewportView(body);
    }

    private void codeChanged() {
        //refresh number
        refreshLineNumbers();

        //find errors + higlight
        try {
            String[] lines = this.code.getText().split("\n");
            this.errorSigns.clear();

            int index = 0;
            for (String line : lines) {
                String[] words = line.replace("\r", "").toLowerCase().split(" ");

                boolean error = false;
                for (String word : words) {
                    if (word.contains("//")) {
                        break;
                    }
                    if (!word.startsWith("0x")
                            && !this.translator.stream().anyMatch((o) -> (o.oldStr.equals(word)))
                            && !word.matches("[01]*")) {
                        error = true;
                        break;
                    }
                }

                if (error) {
                    this.errorSigns.add(index);
                }

                index++;
            }
        } catch (Exception ex) {
        }

    }

    /**
     * Set font for hex editor area
     *
     * @param size Set font size
     */
    public void refreshFont(int size) {
        this.fontSize = size;
        this.number.setFont(new Font(this.fontName, Font.PLAIN, size));
        this.code.setFont(new Font(this.fontName, Font.PLAIN, size));
        this.hex.setFont(new Font(this.fontName, Font.PLAIN, size));
    }

    private void refreshLineNumbers() {
        //number
        int count_code = this.doc.getDefaultRootElement().getElementCount();
        int count_number = this.number.getLineCount();

        if (count_code != count_number) {
            if (count_code > count_number) {
                //add numbers
                while (count_code != this.number.getLineCount()) {
                    this.number.append(
                            "\n " + (this.number.getLineCount() + 1) + ' '
                    );
                }
            } else {
                //remove numbers
                Document d = this.number.getDocument();
                while (count_code != this.number.getLineCount()) {
                    int lastLineBreak = this.number.getText().lastIndexOf('\n');
                    try {
                        d.remove(lastLineBreak, this.number.getText().length() - lastLineBreak);
                    } catch (BadLocationException ex) {
                    }
                }
            }
        }
    }

    private void translate() {
        this.hex.setText("");
        String[] lines = this.code.getText().split("\n");
        for (String line : lines) {
            String[] words = line.toLowerCase().split(" ");

            WORD_LOOP:
            for (String word : words) {
                try {
                    String prefix = "  0x"
                            + Tools.convertToNumber(this.hex.getLineCount() - 1, 16);
                    for (LinkASM link : this.translator) {
                        if (link.oldStr.equals(word)) {
                            this.hex.append(prefix + " : " + link.newStr + "  \n");
                            continue WORD_LOOP;
                        }
                    }
                    //link not found
                    if (!word.contains("//")) {
                        if (word.startsWith("0x")) {
                            //value is in hex fromat
                            this.hex.append(
                                    prefix + " : "
                                    + word.substring(2).replace("\r", "") + "  \n"
                            );

                        } else {
                            //value is in bin format
                            this.hex.append(
                                    prefix + " : "
                                    + Integer.toString(Integer.parseInt(word.replace("\r", ""), 2), 16) + "  \n"
                            );
                        }
                    } else {
                        //comment -> next commands on line
                        break;
                    }
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * Get translated code
     *
     * @return
     */
    public List<String> getHexData() {
        List<String> hexData = new ArrayList<>();
        //replace all linkers
        String[] lines = this.code.getText().split("\n");
        for (String line : lines) {
            String[] words = line.toLowerCase().split(" ");
            word_loop:
            for (String word : words) {
                //if word is only one whitespace than continue to next word
                if (word.length() == 1 && word.charAt(0) == ' ') {
                    continue;
                }
                for (LinkASM link : this.translator) {
                    if (link.oldStr.equals(word)) {
                        hexData.add(link.newStr);
                        continue word_loop;
                    }
                }
                //link not found
                if (!word.contains("//")) {
                    if (word.startsWith("0x")) {
                        //value is in hex fromat
                        hexData.add(word.substring(2).replace("\r", ""));

                    } else {
                        //value is in bin format
                        hexData.add(Integer.toString(Integer.parseInt(word.replace("\r", ""), 2), 16));
                    }
                } else {
                    //comment -> next commands on line
                    break;
                }
            }
        }

        return hexData;
    }

    /**
     * Get translator
     *
     * @return
     */
    public List<LinkASM> getTranslator() {
        return this.translator;
    }

    /**
     * Set text (code)
     *
     * @param text New code
     */
    public void setText(String text) {
        this.code.setText(text);
        translate();
    }

    /**
     * Get text (code)
     *
     */
    public String getText() {
        return this.code.getText();
    }

    /**
     * Colorize words
     */
    private void colorizeWords() {
        //remova all chracter atributes (set deafult "black")
        String text = this.code.getText();
        this.doc.setCharacterAttributes(0, text.length(), asetBlack, true);
        //for each line of code
        String[] lines = text.split("\n");
        int index = 0;
        for (String line : lines) {
            String[] words = line.toLowerCase().replace("\r", "").split(" ");
            boolean comment = false;
            for (String word : words) {
                if (!comment) {
                    if (translator.stream().anyMatch((l) -> (l.oldStr.equals(word)))) {
                        //function
                        this.doc.setCharacterAttributes(index, word.length(), asetCommand, true);
                    } else if (word.startsWith("0x")) {
                        //hex format of value
                        this.doc.setCharacterAttributes(index, word.length() + 1, asetValueHex, true);
                    } else if (word.contains("//")) {
                        int start_c = index + word.indexOf("//");
                        int end_c = index + line.length();
                        this.doc.setCharacterAttributes(start_c, end_c - start_c, asetComment, true);
                        comment = true;

                    } else if (word.matches("[01]*")) {
                        //binary format of value
                        this.doc.setCharacterAttributes(index, word.length(), asetValueBin, true);
                    }
                }
                index += word.length() + 1;
            }
        }
    }

    public static class LinkASM implements Serializable {

        public String oldStr, newStr;

        public LinkASM(String oldStr, String newStr) {
            this.oldStr = oldStr.toLowerCase();
            if (newStr.startsWith("0x")) {
                //value is in hex fromat
                this.newStr = newStr.substring(2);

            } else {
                //value is in bin format
                this.newStr = Integer.toString(Integer.parseInt(newStr, 2), 16);
            }
        }

    }

}
