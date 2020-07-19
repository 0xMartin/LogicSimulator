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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import logicSimulator.ExceptionLogger;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.PFHandler;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.Tools;
import logicSimulator.data.FileIO;

/**
 * ASM editor for programs
 *
 * @author Martin
 */
public class DocumentationEditor extends ProjectFile {

    //toolbar
    private final ProjectFileToolbar toolbar;

    //handler
    private final DocumentHandler handler;

    public DocumentationEditor(String name, Project project) {
        super(project);
        super.setLayout(new BorderLayout());
        super.setName(name);

        //scroll pane for hex editor area
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBorder(null);
        super.add(jScrollPane, BorderLayout.CENTER);

        //handler for hex editor
        this.handler = new DocumentHandler(this);
        jScrollPane.setViewportView(this.handler);

        //toolbar
        this.toolbar = new ProjectFileToolbar(this);
        super.add(this.toolbar, BorderLayout.NORTH);
    }

    @Override
    public void backUpData(String projectDirectoryPath) throws Exception {
        FileIO.writeText(
                new File(projectDirectoryPath + this.getName() + "." + LogicSimulatorCore.DOCUMENTATION_FILE_TYPE),
                this.getText()
        );
    }

    @Override
    public void restoreData(File file) throws Exception {
        this.setText(FileIO.readText(file));
    }

    @Override
    public PFHandler getHandler() {
        return this.handler;
    }

    /**
     * Set html code
     * @param text New html code
     */
    public void setText(String text) {
        this.handler.setText(text);
    }

    /**
     * Get Html code from document
     * @return 
     */
    public String getText() {
        return this.handler.getText();
    }

    /**
     * Insert table to document
     * @param rows Number of rows
     * @param cols Number of cols
     */
    public void insertTable(int rows, int cols) {
        String table = "<table style=\"background: #111111\" align=\"center\">";
        for (int r = 0; r < rows; r++) {
            table += "<tr>";
            for (int c = 0; c < cols; c++) {
                String bg = r % 2 == 0 ? "#eeeeee" : "#cccccc";
                table += r == 0 ? "<th style=\"background: #000000; color: #ffffff\">A</th>"
                        : "<td style=\"background:" + bg + "\">A</td>";
            }
            table += "</tr>";
        }
        table += "</table>";

        this.handler.insertHTML(table);
    }

    /**
     * Insert <hr> to document
     */
    public void insertHR() {
        this.handler.insertHTML("<hr>");
    }

    /**
     * Insert bullet to document
     */
    public void insertBull() {
        this.handler.insertHTML("&bull");
    }

    private static class DocumentHandler extends JEditorPane implements
            PFHandler, MouseWheelListener, MouseListener {

        private final DocumentationEditor owner;

        private final HTMLEditorKit kit;
        private final HTMLDocument doc;

        public DocumentHandler(DocumentationEditor owner) {
            super("text/html", "");
            this.owner = owner;
            this.kit = (HTMLEditorKit) super.getEditorKit();
            this.doc = (HTMLDocument) super.getDocument();
            super.addMouseWheelListener(this);
            super.addMouseListener(this);
            this.zoom(0);
        }

        /**
         * Insert html to document
         * @param htmlText 
         */
        public void insertHTML(String htmlText) {
            try {
                this.kit.insertHTML(this.doc, this.getCaretPosition(), htmlText, 0, 0, null);
            } catch (BadLocationException | IOException ex) {
                ExceptionLogger.getInstance().logException(ex);
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Tools.setHighQuality(g2);
            super.paintComponent(g2);
        }

        @Override
        public void repaintPF() {
            this.repaint();
        }

        @Override
        public void zoom(int ration) {

        }

        @Override
        public Point getCursorPosition() {
            return null;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.isControlDown()) {
                zoom(e.getWheelRotation());
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //select this project file in project
            this.owner.selectInProject();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

    }

}
