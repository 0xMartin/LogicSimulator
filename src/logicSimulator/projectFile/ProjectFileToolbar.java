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
package logicSimulator.projectFile;

import java.awt.BorderLayout;
import logicSimulator.projectFile.documentStyleAction.SubscriptAction;
import logicSimulator.projectFile.documentStyleAction.SuperscriptAction;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.FontFamilyAction;
import javax.swing.text.StyledEditorKit.FontSizeAction;
import javax.swing.text.StyledEditorKit.ForegroundAction;
import logicSimulator.ProjectFile;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.objects.Text;
import logicSimulator.objects.memory.ROMRAM;
import logicSimulator.objects.wiring.Wire;
import logicSimulator.common.WrappingFlowLayout;
import logicSimulator.projectFile.documentStyleAction.BackgroundColorAction;
import logicSimulator.projectFile.documentStyleAction.StrikeThroughAction;
import logicSimulator.ui.ButtonHQ;
import logicSimulator.ui.SystemResources;
import window.TraslatorConfig;
import window.components.ColorBox;
import window.components.FontChooser;
import window.components.HexEditorArea;
import window.components.NumberChooser;

/**
 * Toolbar for project file
 *
 * @author Martin
 */
public class ProjectFileToolbar extends JToolBar {

    private final ProjectFile projectFile;

    /**
     * Create instace of toolbar for project file, supported files: workspace,
     * model editor, hex editor, documentation editor
     *
     * @param pf
     */
    public ProjectFileToolbar(ProjectFile pf) {
        super.setFloatable(false);
        super.setLayout(new WrappingFlowLayout(FlowLayout.LEFT));
        super.setBackground(Color.white);
        super.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        this.projectFile = pf;
        initToolbar();
    }

    /**
     * Init toolbar
     */
    private void initToolbar() {
        if (this.projectFile instanceof WorkSpace) {
            initWorkspaceToolbar();
        } else if (this.projectFile instanceof ModuleEditor) {
            initModuleEditorToolbar();
        } else if (this.projectFile instanceof HexEditor) {
            initHEXEditorToolbar();
        } else if (this.projectFile instanceof DocumentationEditor) {
            initDocEditorToolbar();
        }
    }

    //text on the right side of toolbar (use for displaing value of zoom)
    private String right_string = "";

    /**
     * Set text on right side of toolbar
     *
     * @param text
     */
    public void setRightString(String text) {
        this.right_string = text;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.right_string.length() != 0) {
            Graphics2D g2 = (Graphics2D) g;
            Tools.setHighQuality(g2);
            g2.setColor(Color.black);
            g2.setFont(new Font("consolas", Font.PLAIN, 14));
            g2.drawString(
                    this.right_string,
                    this.getWidth() - 10 - g.getFontMetrics().stringWidth(this.right_string),
                    this.getHeight() / 2 + Tools.centerYString(g.getFontMetrics())
            );
        }
    }

    /**
     * Init toolbar for workspace
     */
    private void initWorkspaceToolbar() {
        ButtonHQ b;

        //zoom in
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Zoom in");
        b.setIcon(SystemResources.TOOLBAR_ZOOM_IN);
        b.addActionListener((ActionEvent e) -> {
            WorkSpace w = (WorkSpace) this.projectFile;
            w.getHandler().zoom(-1);
        });
        this.add(b);

        //zoom out
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Zoom out");
        b.setIcon(SystemResources.TOOLBAR_ZOOM_OUT);
        b.addActionListener((ActionEvent e) -> {
            WorkSpace w = (WorkSpace) this.projectFile;
            w.getHandler().zoom(1);
        });
        this.add(b);

        //delete
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Delete");
        b.setIcon(SystemResources.TOOLBAR_DELETE);
        b.addActionListener((ActionEvent e) -> {
            //delete all wire in selection
            WorkSpace w = (WorkSpace) this.projectFile;
            w.deleteSelectedObjects();
            w.repaint();
        });
        this.add(b);

        //rotate
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Rotate");
        b.setIcon(SystemResources.TOOLBAR_ROTATE);
        b.addActionListener((ActionEvent e) -> {
            //delete all wire in selection
            WorkSpace w = (WorkSpace) this.projectFile;
            w.rotateSelectedObject();
            w.repaint();
        });
        this.add(b);

        //delete wire
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Delete all wires");
        b.setIcon(SystemResources.TOOLBAR_DELETE_WIRE);
        b.addActionListener((ActionEvent e) -> {
            //delete all wire in selection
            WorkSpace w = (WorkSpace) this.projectFile;
            for (int i = 0; i < w.getObjects().size(); i++) {
                WorkSpaceObject obj = w.getObjects().get(i);
                if (obj != null) {
                    if (obj.isSelected()) {
                        if (obj instanceof Wire) {
                            w.getObjects().remove(i);
                            i = -1;
                        }
                    }
                }
            }
            w.repaint();
        });
        this.add(b);

        //align vertical
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Vertical Align");
        b.setIcon(SystemResources.TOOLBAR_ALIGN_VERTICAL);
        this.add(b);

        //align horisontal
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Horisontal Align");
        b.setIcon(SystemResources.TOOLBAR_ALIGN_HORISONTAL);
        this.add(b);

        //text
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Add text");
        b.setIcon(SystemResources.TOOLBAR_TXT);
        b.addActionListener((ActionEvent e) -> {
            WorkSpace w = (WorkSpace) this.projectFile;
            List<WorkSpaceObject> list = new ArrayList<>();
            list.add(new Text(
                    "Text",
                    w.getHandler().getCursorPosition(),
                    new Font("tahoma", Font.PLAIN, 12)
            ));
            w.addNewObjects(list);
        });
        this.add(b);

    }

    /**
     * Init toolbar for Module editor
     */
    private void initModuleEditorToolbar() {
        ButtonHQ b;

        //apply
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Apply the changes");
        b.setIcon(SystemResources.TOOLBAR_APPLY);
        b.addActionListener((ActionEvent e) -> {
            ((ModuleEditor)this.projectFile).applyChanges();
        });
        this.add(b);
        
        //line
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Add Line");
        b.setIcon(SystemResources.TOOLBAR_LINE);
        b.addActionListener((ActionEvent e) -> {

        });
        this.add(b);

        //circle
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Add circle");
        b.setIcon(SystemResources.TOOLBAR_CIRCLE);
        b.addActionListener((ActionEvent e) -> {

        });
        this.add(b);

        //curve
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Add curve");
        b.setIcon(SystemResources.TOOLBAR_CURVE);
        b.addActionListener((ActionEvent e) -> {

        });
        this.add(b);

        //rectangle
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Add rectangle");
        b.setIcon(SystemResources.TOOLBAR_RECTANGLE);
        b.addActionListener((ActionEvent e) -> {

        });
        this.add(b);

        //text
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Add text");
        b.setIcon(SystemResources.TOOLBAR_TXT);
        b.addActionListener((ActionEvent e) -> {

        });
        this.add(b);

        //select all
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Select all");
        b.setIcon(SystemResources.TOOLBAR_SELECT_ALL);
        b.addActionListener((ActionEvent e) -> {
            ModuleEditor m = (ModuleEditor) this.projectFile;
            m.selecteAllGraphicsObjects();
            m.repaint();
        });
        this.add(b);

        //delete
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setToolTipText("Delete");
        b.setIcon(SystemResources.TOOLBAR_DELETE);
        b.addActionListener((ActionEvent e) -> {
            ModuleEditor m = (ModuleEditor) this.projectFile;
            m.getModule().getModel().getGraphicsObjects().clear();
            m.getHandler().repaintPF();
        });
        this.add(b);

    }

    /**
     * Inti toobar for hex editor
     */
    private void initHEXEditorToolbar() {
        ButtonHQ b;

        //translator
        b = new ButtonHQ();
        b.setBorderWidth(1);
        this.add(b);
        b.setToolTipText("Translator Configuration");
        b.setIcon(SystemResources.TOOLBAR_TRASLATOR_CONFIG);
        b.addActionListener((ActionEvent e) -> {
            TraslatorConfig transConf = new TraslatorConfig(
                    ((HexEditorArea) this.projectFile.getHandler()).getTranslator()
            );
            transConf.setVisible(true);
            transConf.setTitle("Translator Configuration : [" + this.projectFile.getComp().getName() + "]");
        });

        //conver dec to hex
        b = new ButtonHQ();
        b.setBorderWidth(1);
        this.add(b);
        b.setToolTipText("Dec to hex");
        b.setIcon(SystemResources.TOOLBAR_TO_HEX);
        b.addActionListener((ActionEvent e) -> {
            try {
                HexEditorArea ha = (HexEditorArea) this.projectFile.getHandler();
                //selected string as decimal number
                String dec = ha.getCodePanel().getSelectedText();
                //convert decimal number to hex number
                String hex = Integer.toString(Integer.parseInt(dec, 10), 16);
                Document d = ha.getCodePanel().getDocument();
                int start = ha.getCodePanel().getSelectionStart();
                try {
                    //remove selected dec number and insert this number in hex format
                    d.remove(start, dec.length());
                    d.insertString(start, "0x" + hex, null);
                } catch (BadLocationException ex) {
                }
            } catch (NumberFormatException ex) {
            }
        });

        //comment
        b = new ButtonHQ();
        b.setBorderWidth(1);
        this.add(b);
        b.setToolTipText("Comment");
        b.setIcon(SystemResources.TOOLBAR_COMMENT);
        b.addActionListener((ActionEvent e) -> {
            HexEditorArea ha = (HexEditorArea) this.projectFile.getHandler();
            //start + end of select
            int start = ha.getCodePanel().getSelectionStart();
            int end = ha.getCodePanel().getSelectionEnd();

            Document doc = ha.getCodePanel().getDocument();
            try {
                //move start position to left side of row (last line breaker symbol)
                String all = doc.getText(0, doc.getLength());
                while (start > 0) {
                    if (all.charAt(start) == '\n') {
                        break;
                    }
                    --start;
                }
                //after every "\n" place "//" in selected text
                int off = 1;
                String selectedText = doc.getText(start, end - start);
                for (int i = 0; i < selectedText.length(); i++) {
                    //place comment symbol after \n or on begining of code
                    if (selectedText.charAt(i) == '\n' || (start == 0 && i == 0)) {
                        doc.insertString(start + i + off, "//", null);
                        off += 2;
                    }
                }
            } catch (BadLocationException ex) {
            }
        });

        //uncomment
        b = new ButtonHQ();
        b.setBorderWidth(1);
        this.add(b);
        b.setToolTipText("Uncomment");
        b.setIcon(SystemResources.TOOLBAR_UNCOMMENT);
        b.addActionListener((ActionEvent e) -> {
            HexEditorArea ha = (HexEditorArea) this.projectFile.getHandler();
            //start + end of select
            int start = ha.getCodePanel().getSelectionStart();
            int end = ha.getCodePanel().getSelectionEnd();

            Document doc = ha.getCodePanel().getDocument();
            try {
                //move start position to left side of row (last line breaker symbol)
                String all = doc.getText(0, doc.getLength());
                while (start > 0) {
                    if (all.charAt(start) == '\n') {
                        break;
                    }
                    --start;
                }
                //after every "\n" place "//" in selected text
                int off = 1;
                String selectedText = doc.getText(start, end - start);
                for (int i = 0; i < selectedText.length(); i++) {
                    //place comment symbol after \n or on begining of code
                    if (selectedText.charAt(i) == '\n' || (start == 0 && i == 0)) {
                        if (selectedText.charAt(i + 1) == '/' && selectedText.charAt(i + 2) == '/') {
                            doc.remove(start + i + off, 2);
                            off -= 2;
                        }
                    }
                }
            } catch (BadLocationException ex) {
            }
        });

        //Upload
        b = new ButtonHQ();
        b.setBorderWidth(1);
        this.add(b);
        b.setToolTipText("Upload");
        b.setIcon(SystemResources.TOOLBAR_UPLOAD);
        b.addActionListener((ActionEvent e) -> {
            //upload
            ((HexEditor) this.projectFile).uploadCodeToMemory();
        });

        //list with memory IDs
        JComboBox memory = new JComboBox();
        memory.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                //last selected id;
                String last = (String) memory.getSelectedItem();
                //list with all memory IDs
                List<String> ids = new ArrayList<>();
                projectFile.getProject().getProjectFiles().stream()
                        .filter((pf) -> (pf instanceof WorkSpace))
                        .forEachOrdered((pf) -> {
                            //in each workspace find all rom ram
                            ((WorkSpace) pf).getObjects().stream()
                                    .filter((obj) -> (obj instanceof ROMRAM))
                                    .forEachOrdered((obj) -> {
                                        //add ID of memory to list
                                        String id = ((ROMRAM) obj).getID();
                                        ids.add(pf.getComp().getName() + ":" + id);
                                    });
                        });
                memory.setModel(new DefaultComboBoxModel(ids.toArray()));
                //select id if last time was selected
                for (int i = 0; i < memory.getItemCount(); i++) {
                    if (((String) memory.getItemAt(i)).equals(last)) {
                        memory.setSelectedIndex(i);
                        break;
                    }
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (memory.getSelectedItem() == null) {
                    if (memory.getItemCount() == 1) {
                        memory.setSelectedIndex(0);
                    }
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        memory.setPreferredSize(new Dimension(130, 26));
        memory.setToolTipText("Selected memory");
        memory.setName("memID");

        //debug checkbox
        JCheckBox debug = new JCheckBox("Debug", false);

        memory.addActionListener((ActionEvent e) -> {
            //memory selected from list
            ((HexEditor) this.projectFile).setMemoryID((String) memory.getSelectedItem());
            debug.setSelected(false);
        });
        this.add(memory);

        //offset
        NumberChooser nc = new NumberChooser(" Offset: ", 0, 10, 0, Integer.MAX_VALUE);
        nc.setHexDisplayFormat(true);
        nc.setName("offset");
        nc.setToolTipText("Offset of uploaded code");
        nc.setValueChangedEvent((ActionEvent e) -> {
            ((HexEditor) this.projectFile).setCodeOffset(nc.getValue());
            debug.setSelected(false);
        });
        this.add(nc);

        //clear
        JCheckBox clear = new JCheckBox("Clear", true);
        clear.setOpaque(false);
        clear.setName("clear");
        clear.setToolTipText("Clear memory before uploading");
        clear.addActionListener((ActionEvent e) -> {
            ((HexEditor) this.projectFile).clearCodeBefore(clear.isSelected());
        });
        this.add(clear);

        //enable Highlighting of current accessed instruction
        debug.setOpaque(false);
        debug.setToolTipText("Highlight of current accessed instruction");
        debug.addActionListener((ActionEvent e) -> {
            HexEditor hex = (HexEditor) this.projectFile;
            hex.setMemoryID((String) memory.getSelectedItem());
            boolean unselect = !hex.enableDebug(debug.isSelected());
            //unselect because can enable debug for hex editor (program in memory != program in hexeditor)
            if (unselect) {
                debug.setSelected(false);
            }
        });
        this.add(debug);

    }

    /**
     * Toolbar for document editor
     */
    private void initDocEditorToolbar() {
        ButtonHQ b;

        //font
        FontChooser fc = new FontChooser(false);
        fc.setMaximumRowCount(15);
        fc.setPreferredSize(new Dimension(125, 25));
        this.add(fc);
        fc.addActionListener((var e) -> {
            FontFamilyAction action = new StyledEditorKit.FontFamilyAction(
                    fc.getSelectedFontName(),
                    fc.getSelectedFontName()
            );
            action.actionPerformed(new ActionEvent(projectFile.getHandler(), 0, ""));
        });

        //size
        JSpinner size = new JSpinner(new SpinnerNumberModel(12, 4, 200, 1));
        size.setPreferredSize(new Dimension(50, 25));
        size.addChangeListener((ChangeEvent e) -> {
            FontSizeAction action = new StyledEditorKit.FontSizeAction(
                    "",
                    (int) size.getValue()
            );
            action.actionPerformed(new ActionEvent(this.projectFile.getHandler(), 0, ""));
        });
        this.add(size);

        //Foreground
        ColorBox cb1 = new ColorBox(Color.black);
        cb1.setColorChangedListener((ActionEvent e) -> {
            ForegroundAction action = new ForegroundAction(
                    "",
                    cb1.getColor()
            );
            action.actionPerformed(new ActionEvent(this.projectFile.getHandler(), 0, ""));
        });
        cb1.setToolTipText("Foreground");
        JPanel p = new JPanel(new BorderLayout());
        p.setToolTipText("Foreground");
        p.setPreferredSize(new Dimension(35, 25));
        p.add(new JLabel("<html><b>F</b></html>", SwingConstants.CENTER), BorderLayout.PAGE_START);
        p.add(cb1, BorderLayout.CENTER);
        this.add(p);

        //Background
        ColorBox cb2 = new ColorBox(Color.black);
        cb2.setPreferredSize(new Dimension(35, 25));
        cb2.setColorChangedListener((ActionEvent e) -> {
            BackgroundColorAction action = new BackgroundColorAction(cb2.getColor());
            action.actionPerformed(new ActionEvent(this.projectFile.getHandler(), 0, ""));
        });
        cb2.setToolTipText("Background");
        p = new JPanel(new BorderLayout());
        p.setToolTipText("Background");
        p.setPreferredSize(new Dimension(35, 25));
        p.add(new JLabel("<html><b>B</b></html>", SwingConstants.CENTER), BorderLayout.PAGE_START);
        p.add(cb2, BorderLayout.CENTER);
        this.add(p);

        //bold
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new StyledEditorKit.BoldAction());
        b.setText("<html><b>B</b></html>");
        b.setToolTipText("Bold");
        this.add(b);

        //italic
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new StyledEditorKit.ItalicAction());
        b.setText("<html><i>I</i></html>");
        b.setToolTipText("Italic");
        this.add(b);

        //under line
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new StyledEditorKit.UnderlineAction());
        b.setText("<html><u>U</u></html>");
        b.setToolTipText("Underline");
        this.add(b);

        //align left
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new StyledEditorKit.AlignmentAction("", StyleConstants.ALIGN_LEFT));
        b.setIcon(SystemResources.TOOLBAR_ALIGN_LEFT);
        b.setToolTipText("Align left");
        this.add(b);

        //align center
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new StyledEditorKit.AlignmentAction("", StyleConstants.ALIGN_CENTER));
        b.setIcon(SystemResources.TOOLBAR_ALIGN_CENTER);
        b.setToolTipText("Align center");
        this.add(b);

        //align right
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new StyledEditorKit.AlignmentAction("", StyleConstants.ALIGN_RIGHT));
        b.setIcon(SystemResources.TOOLBAR_ALIGN_RIGHT);
        b.setToolTipText("Align right");
        this.add(b);

        //align JUSTIFIED
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new StyledEditorKit.AlignmentAction("", StyleConstants.ALIGN_JUSTIFIED));
        b.setIcon(SystemResources.TOOLBAR_ALIGN_JUSTIFIED);
        b.setToolTipText("Align center");
        this.add(b);

        //table
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.addActionListener((ActionEvent e) -> {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JTextField row = new JTextField();
            panel.add(new JLabel("Rows: "));
            panel.add(row);
            JTextField col = new JTextField();
            panel.add(new JLabel("Cols: "));
            panel.add(col);
            int stat = JOptionPane.showConfirmDialog(this.projectFile, panel, "Create table", JOptionPane.OK_CANCEL_OPTION);
            if (stat == JOptionPane.OK_OPTION) {
                try {
                    ((DocumentationEditor) this.projectFile).insertTable(
                            Integer.parseInt(row.getText()),
                            Integer.parseInt(col.getText())
                    );
                } catch (NumberFormatException ex) {
                }
            }
        });
        b.setIcon(SystemResources.TOOLBAR_TABLE);
        b.setToolTipText("Create table");
        this.add(b);

        //horisontal line
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.addActionListener((ActionEvent e) -> {
            ((DocumentationEditor) this.projectFile).insertHR();
        });
        b.setIcon(SystemResources.TOOLBAR_HORISONTAL_LINE);
        b.setToolTipText("Horisontal line");
        this.add(b);

        //bullet
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.addActionListener((ActionEvent e) -> {
            ((DocumentationEditor) this.projectFile).insertBull();
        });
        b.setText("<html>&bull</html>");
        b.setToolTipText("Insert bullet");
        this.add(b);

        //Sub script
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new SubscriptAction());
        b.setText("<html>x<sub>2</sub></html>");
        b.setToolTipText("Sub script");
        this.add(b);

        //Super script
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new SuperscriptAction());
        b.setText("<html>x<sup>2</sup></html>");
        b.setToolTipText("Super script");
        this.add(b);

        //StrikeThrough
        b = new ButtonHQ();
        b.setBorderWidth(1);
        b.setAction(new StrikeThroughAction());
        b.setText("<html><strike>text</strike></html>");
        b.setToolTipText("Strike throught");
        this.add(b);
    }

}
