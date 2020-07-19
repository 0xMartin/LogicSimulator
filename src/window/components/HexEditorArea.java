/* 
 * Copyright (C) 2020 Martin Krcma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package window.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import logicSimulator.Convert;
import logicSimulator.Tools;
import logicSimulator.common.LinkASM;

public class HexEditorArea extends JScrollPane {

    /**
     * text area for line indexing and text pane for code editing
     */
    private final JTextArea number;
    private final JTextPane code;

    //list with start and end indexes of all onstructionos in program
    private final List<Point> commandPositions = new ArrayList<>();

    //font
    public String fontName = "Consolas";
    public int fontSize = 16;

    //program offset
    public int programOffset = 0;

    //list for code translating
    private final List<LinkASM> translator = new ArrayList<>();

    //coloring AttributeSets
    private final StyleContext sc = StyleContext.getDefaultStyleContext();

    private final AttributeSet asetCommand = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);

    private final AttributeSet asetAlias = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED);

    private final AttributeSet asetMark = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.DARK_GRAY);

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
            refreshLineNumbers();
            refreshCurrentInstruction(this, offset);
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
            colorizeWords();
            refreshLineNumbers();
            refreshCurrentInstruction(this, offs - 1);
        }

    };

    //current word
    private String currentInstruction = "";
    //if is true than show list with instructions
    private boolean showInstructions = false;

    /**
     * Set current writed word
     *
     * @param doc StyledDocument
     * @param offset Caret offset
     */
    private void refreshCurrentInstruction(StyledDocument doc, int offset) {
        try {
            String text = doc.getText(0, doc.getLength());
            currentInstruction = "";
            for (int i = offset; i >= 0; i--) {
                char c = text.charAt(i);
                if (c == ' ' || c == '\n' || c == '\r') {
                    break;
                }
                currentInstruction = c + currentInstruction;
            }
        } catch (BadLocationException ex) {
        }
    }

    public HexEditorArea() {
        //textarea with number of line
        this.number = new JTextArea(" 1 ");
        this.number.setForeground(Color.BLUE);
        this.number.setBackground(Color.white);
        this.number.setEditable(false);

        JScrollPane scrollPane = this;

        //text area with code
        this.code = new JTextPane(this.doc) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.white);
                g.fillRect(0, 0, super.getWidth(), super.getHeight());

                Graphics2D g2 = (Graphics2D) g;
                Tools.setHighQuality(g2);
                
                //highlight curent line
                try {
                    Rectangle rect = super.modelToView(getCaretPosition());
                    if (rect != null) {
                        g2.setColor(new Color(230, 230, 250));
                        g2.fillRect(0, rect.y, super.getWidth(), rect.height);
                    }
                } catch (BadLocationException e) {
                }

                //paint component
                super.paintComponent(g2);

                //vertical line divider
                g2.setColor(Color.BLACK);
                g2.drawLine(0, 0, 0, super.getHeight());

                //paint instruction list (ctrl + space)
                if (showInstructions) {
                    try {
                        Rectangle rect = super.modelToView(getCaretPosition());

                        boolean reverse = false;
                        
                        //if is so low
                        if (rect.y - scrollPane.getVerticalScrollBar().getValue() > 0.5 * scrollPane.getHeight()) {
                            reverse = true;
                        }

                        List<String> view = new ArrayList<>();
                        String w = currentInstruction.toLowerCase();
                        //add all similar instructions to list
                        translator.stream()
                                .filter((link) -> (link.Mnemonic.startsWith(w) && !w.equals(link.Mnemonic)))
                                .forEachOrdered((link) -> {
                                    view.add(link.Mnemonic + " >> 0x" + link.Hex);
                                });
                        //draw all items
                        for (int i = 0; i < view.size(); ++i) {
                            //bg
                            g2.setColor(i % 2 == 0 ? new Color(190, 190, 220) : new Color(160, 160, 190));
                            g2.fillRect(0, rect.y + (reverse ? - 10 : 7) + (reverse ? -1 : 1) * (i + 1) * rect.height, 350, rect.height);
                            //text
                            g2.setColor(i % 2 == 0 ? Color.black : Color.white);
                            g2.drawString(view.get(i), 5, rect.y + (reverse ? rect.height + 4 : 4) + (reverse ? -1 : 1) * (i + 2) * rect.height);
                        }
                    } catch (BadLocationException e) {
                    }
                }
            }

            @Override
            public void repaint(long tm, int x, int y, int width, int height) {
                super.repaint(tm, 0, 0, getWidth(), getHeight());
            }
        };
        this.code.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        //show available instructions (ctrl + space)
                        if (evt.isControlDown()) {
                            showInstructions = true;
                        }
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        //hide available instructions
                        showInstructions = false;
                        break;
                }
            }
        });
        this.code.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //hide available instructions
                showInstructions = false;
            }
        });
        this.code.setOpaque(false);

        this.refreshFont(this.fontSize);
        JPanel body = new JPanel();
        body.setLayout(new BorderLayout());
        body.add(this.number, BorderLayout.WEST);
        body.add(this.code, BorderLayout.CENTER);
        super.setViewportView(body);
    }

    /**
     * Return code text pane
     *
     * @return
     */
    public JTextPane getCodePanel() {
        return this.code;
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

    /**
     * Translate and get program in hex
     *
     * @return
     */
    public List<Byte> getHexData() {
        List<Byte> hexData = new ArrayList<>();

        this.commandPositions.clear();
        int index = 0;

        //replace all linkers
        String[] lines = this.code.getText().split("\n");
        for (String line : lines) {

            //remove \r symbol
            line = line.replace("\r", "");
            //get words
            String[] words = Tools.removeEndWhiteSpaces(line).toLowerCase().replace("\r", "").split(" ");

            boolean comment = false;

            word_loop:
            for (String word : words) {

                index += word.length() + 1;

                if (comment) {
                    continue;
                }

                //if word is only one whitespace than continue to next word
                if (word.length() == 1 && word.charAt(0) == ' ' || word.length() == 0) {
                    continue;
                }

                if (word.endsWith(":") && !word.startsWith("'")) {
                    continue;
                }

                //link ASM
                for (LinkASM link : this.translator) {
                    if (link.Mnemonic.equals(word)) {
                        hexData.add(Convert.hexToByte(link.Hex));
                        //add positon of command in text to list
                        this.commandPositions.add(new Point(index - word.length(), index));
                        continue word_loop;
                    }
                }

                //jump mark
                for (JMPMark mark : this.jmpMarks) {
                    if (mark.name.equals(word)) {
                        byte[] address = Convert.intToTwoBytes(mark.address);
                        hexData.add(address[0]);
                        hexData.add(address[1]);
                        //add positon of command in text to list
                        this.commandPositions.add(new Point(index - word.length(), index));
                        this.commandPositions.add(new Point(index - word.length(), index));
                        continue word_loop;
                    }
                }

                //link not found
                if (!word.contains("//")) {
                    if (word.startsWith("0x")) {
                        //value is in hex fromat
                        hexData.add(Convert.hexToByte(word.substring(2)));
                        //add positon of command in text to list
                        this.commandPositions.add(new Point(index - word.length(), index));
                    } else if (word.startsWith("'")) {
                        //value is char format (for loading chars)
                        for (int i = 1; i < word.length(); i++) {
                            int dec = (int) word.charAt(i);
                            hexData.add(Convert.hexToByte(Integer.toString(dec, 16)));
                            //add positon of command in text to list
                            this.commandPositions.add(new Point(index - word.length() + i, index - word.length() + i + 1));
                        }
                    } else {
                        //value is in bin format
                        hexData.add(Convert.binToByte(word));
                        //add positon of command in text to list
                        this.commandPositions.add(new Point(index - word.length(), index));
                    }
                } else {
                    comment = true;
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
    }

    /**
     * Get text (code)
     *
     * @return
     */
    public String getText() {
        return this.code.getText();
    }

    /**
     * Colorize words
     */
    private void colorizeWords() {
        //find jump marks
        findJumpMarks(this.code.getText());

        //remova all chracter atributes (set deafult "black")
        String text = this.code.getText();
        this.doc.setCharacterAttributes(0, text.length(), asetBlack, true);
        //for each line of code
        String[] lines = text.split("\n");
        int index = 0;
        for (String line : lines) {
            String[] words = Tools.removeEndWhiteSpaces(line).toLowerCase().replace("\r", "").split(" ");
            boolean comment = false;
            for (String word : words) {
                if (!comment) {
                    if (translator.stream().anyMatch((l) -> (l.Mnemonic.equals(word)))) {
                        if (translator.stream().anyMatch((l) -> (l.Mnemonic.equals(word)) && l.isFunction)) {
                            //function
                            this.doc.setCharacterAttributes(index, word.length() + 1, asetCommand, true);
                        } else {
                            //alias
                            this.doc.setCharacterAttributes(index, word.length() + 1, asetAlias, true);
                        }
                    } else if (word.endsWith(":") && !word.startsWith("'")) {
                        //hex format of value
                        this.doc.setCharacterAttributes(index, word.length() + 1, asetMark, true);
                    } else if (word.startsWith("0x")) {
                        //hex format of value
                        this.doc.setCharacterAttributes(index, word.length() + 1, asetValueHex, true);
                    } else if (word.contains("//")) {
                        int start_c = index + word.indexOf("//");
                        int end_c = index + line.length();
                        this.doc.setCharacterAttributes(start_c, end_c - start_c + 1, asetComment, true);
                        comment = true;
                    } else if (word.startsWith("'")) {
                        this.doc.setCharacterAttributes(index, word.length() + 1, asetValueBin, true);
                        comment = true;
                    } else if (word.matches("[01]*")) {
                        //binary format of value
                        this.doc.setCharacterAttributes(index, word.length() + 1, asetValueBin, true);
                    } else {
                        for (JMPMark mark : this.jmpMarks) {
                            if (mark.name.equals(word)) {
                                //binary format of value
                                this.doc.setCharacterAttributes(index, word.length() + 1, asetAlias, true);
                                break;
                            }
                        }
                    }
                }
                index += word.length() + 1;
            }
        }
    }

    /**
     * Highlight command in code pane
     *
     * @param address address of highlighted command
     */
    public void highlight(int address) {
        Highlighter highlighter = this.code.getHighlighter();
        //remove all highlight
        highlighter.removeAllHighlights();

        //find start and end index of command in program
        Point p;
        if (address < this.commandPositions.size() && address >= 0) {
            p = this.commandPositions.get(address);
        } else {
            return;
        }

        //highlight
        try {
            highlighter.addHighlight(p.x - 1, p.y - 1, new DefaultHighlightPainter(Color.orange));
        } catch (BadLocationException ex) {
        }
    }

    private class JMPMark {

        public String name;
        public int address;

        public JMPMark(String name, int address) {
            this.name = name;
            this.address = address;
        }
    }

    private final List<JMPMark> jmpMarks = new ArrayList<>();

    private void findJumpMarks(String program) {
        this.jmpMarks.clear();

        int instructionAddress = this.programOffset;

        //find jump marks
        String[] lines = program.split("\n");
        for (String line : lines) {
            String[] words = Tools.removeEndWhiteSpaces(line).toLowerCase().replace("\r", "").split(" ");
            boolean comment = false;
            for (String word : words) {
                if (word.length() != 0 && !comment) {
                    if (word.endsWith(":") && !word.startsWith("'")) {
                        this.jmpMarks.add(new JMPMark(
                                word.substring(0, word.length() - 1),
                                instructionAddress
                        ));
                    } else if (word.contains("//")) {
                        comment = true;
                    }
                }
            }
        }

        //for each jmp mark assigns address
        for (String line : lines) {
            String[] words = Tools.removeEndWhiteSpaces(line).toLowerCase().replace("\r", "").split(" ");
            boolean comment = false;
            for (String word : words) {
                if (word.length() != 0 && !comment) {
                    if (word.endsWith(":") && !word.startsWith("'")) {
                        String str = word.substring(0, word.length() - 1);
                        for (JMPMark jmpMark : this.jmpMarks) {
                            if (jmpMark.name.equals(str)) {
                                jmpMark.address = instructionAddress;
                                //System.out.println(jmpMark.name + " -> " + jmpMark.address);
                                break;
                            }
                        }
                    } else if (word.contains("//")) {
                        comment = true;
                    } else if (this.jmpMarks.stream().anyMatch((mark) -> (mark.name.equals(word)))) {
                        instructionAddress += 2;
                    } else {
                        ++instructionAddress;
                    }
                }
            }
        }

    }

}
