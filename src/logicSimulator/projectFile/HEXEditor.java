/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.projectFile;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import logicSimulator.PFHandler;
import logicSimulator.PFMode;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import window.components.HexEditorArea;
import window.components.ProjectFileToolbar;

/**
 * ASM editor for programs
 *
 * @author Martin
 */
public class HEXEditor extends JPanel implements ProjectFile {

    //project
    private final Project project;

    //project file mode
    private final PFMode pfMode = new PFMode(false, false, true);

    private final ProjectFileToolbar toolbar;

    //handler
    private final HEXHandler handler;

    public HEXEditor(String name, Project p) {
        this.project = p;

        this.setLayout(new BorderLayout());
        this.setName(name);

        this.toolbar = new ProjectFileToolbar(this);
        this.add(this.toolbar, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBorder(null);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.handler = new HEXHandler();
        jScrollPane.setViewportView(this.handler);
    }

    @Override
    public PFMode getPFMode() {
        return this.pfMode;
    }

    @Override
    public Component getComp() {
        return this;
    }

    @Override
    public PFHandler getHandler() {
        return this.handler;
    }

    @Override
    public Project getProject() {
        return this.project;
    }

    @Override
    public void selectInProject() {
        if (this.project != null) {
            this.project.setSelectedFile(this);
        }
    }

    /**
     * Return text that are in main text pane
     *
     * @return
     */
    public String getText() {
        return this.handler.getText();
    }

    /**
     * Get binary data for memory
     *
     * @return
     * @throws Exception
     */
    public List<String> getHexData() throws Exception {
        return this.handler.getHexData();
    }

    public void setText(String text) {
        this.handler.setText(text);
    }

    /**
     * Get data for save
     *
     * @return
     */
    public Object[] getSaveData() {
        return new Object[]{this.handler.getText(), this.handler.getTranslator()};
    }

    public void setData(Object[] data) {
        this.handler.setText((String) data[0]);
        this.handler.getTranslator().clear();
        List<HexEditorArea.LinkASM> trans = (List<HexEditorArea.LinkASM>) data[1];
        trans.stream().forEach((l) -> {
            this.handler.getTranslator().add(l);
        });
    }

    private class HEXHandler extends HexEditorArea implements PFHandler, MouseWheelListener {

        private int font_size = 16;

        public HEXHandler() {
            super();
            this.addMouseWheelListener(this);
        }

        @Override
        public void repaintPF() {
            this.repaint();
        }

        @Override
        public void zoom(int ration) {
            int step = 1;
            if (this.font_size > 25) {
                step = 2;
            } else if (this.font_size > 65) {
                step = 3;
            }
            this.font_size -= ration * step;
            this.font_size = Math.min(100, this.font_size);
            this.font_size = Math.max(5, this.font_size);
            super.refreshFont(this.font_size);
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

    }

}
