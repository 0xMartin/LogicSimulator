/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.projectFile;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.PFHandler;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.LinkASM;
import logicSimulator.data.FileIO;
import logicSimulator.objects.memory.ROMRAM;
import window.components.HexEditorArea;
import window.components.NumberChooser;

/**
 * ASM editor for programs
 *
 * @author Martin
 */
public class HexEditor extends ProjectFile {

    //toolbar
    private final ProjectFileToolbar toolbar;

    //uploading configuration
    private String memID = null;
    private int offset = 0;
    private boolean clear = true;
    //memory selected for program uploading
    private ROMRAM memory = null;

    //handler
    private final HEXHandler handler;

    public HexEditor(String name, Project project) {
        super(project);
        super.setLayout(new BorderLayout());
        super.setName(name);

        //toolbar
        this.toolbar = new ProjectFileToolbar(this);
        super.add(this.toolbar, BorderLayout.NORTH);

        //scroll pane for hex editor area
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBorder(null);
        super.add(jScrollPane, BorderLayout.CENTER);

        //handler for hex editor
        this.handler = new HEXHandler(this);
        jScrollPane.setViewportView(this.handler);

    }

    @Override
    public void backUpData(String projectDirectoryPath) throws Exception {
        FileIO.writeObject(
                new File(projectDirectoryPath + this.getName() + "." + LogicSimulatorCore.HEX_FILE_TYPE),
                new Object[]{
                    this.handler.getText(),
                    this.handler.getTranslator(),
                    this.memID,
                    this.offset,
                    this.clear
                }
        );
    }

    @Override
    public void restoreData(File file) throws Exception {
        //read text and translator
        Object[] data = (Object[]) FileIO.readObject(file);

        //translator
        this.handler.getTranslator().clear();
        List<LinkASM> trans = (List<LinkASM>) data[1];
        trans.stream().forEach((link) -> {
            this.handler.getTranslator().add(link);
        });

        //text
        this.handler.setText((String) data[0]);

        //memID
        this.memID = (String) data[2];
        JComboBox cb = (JComboBox) Tools.getComponent(this.toolbar.getComponents(), "memID");
        cb.setModel(new DefaultComboBoxModel(new String[]{this.memID}));
        cb.setSelectedIndex(0);

        //offset
        this.offset = (int) data[3];
        ((NumberChooser) Tools.getComponent(this.toolbar.getComponents(), "offset")).setValue(this.offset);

        //clear
        this.clear = (boolean) data[4];
        ((JCheckBox) Tools.getComponent(this.toolbar.getComponents(), "clear")).setSelected(this.clear);
    }

    @Override
    public PFHandler getHandler() {
        return this.handler;
    }

    /**
     * Get offset of program
     *
     * @return (int)
     */
    public int getProgramOffset() {
        return this.offset;
    }

    /**
     * May clear program after uploading new program
     *
     * @return
     */
    public boolean clearAfterUpload() {
        return this.clear;
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
    public List<Byte> getHexData() throws Exception {
        return this.handler.getHexData();
    }

    public void setText(String text) {
        this.handler.setText(text);
    }

    /**
     * Set id of memory (for code uploading)
     *
     * @param id New memory id
     */
    public void setMemoryID(String id) {
        this.memID = id;
    }

    /**
     * Set offset for uploading code
     *
     * @param off Offset
     */
    public void setCodeOffset(int off) {
        this.offset = off;
    }

    /**
     * If is true than clear memory before uploading
     *
     * @param clear
     */
    public void clearCodeBefore(boolean clear) {
        this.clear = clear;
    }

    /**
     * If is true than highlight current accessed instruction
     *
     * @param enable true -> enable
     * @return
     */
    public boolean enableDebug(boolean enable) {
        //find memory in project
        findMemoryInProject();
        if (this.memory != null) {
            if (enable) {
                //test if program in memory is same as in hexeditor
                List<Byte> program = this.handler.getHexData();
                byte[] data = this.memory.getData();
                int index = 0;
                for (int bitAddress = this.offset; bitAddress < data.length; bitAddress++) {

                    //get bit cluster = one instruction/data
                    byte cluster = program.get(index++);

                    //cluster byte is not same as cell from memory data
                    if (data[bitAddress] != cluster) {
                        /**
                         * difference finded => cant debug program (debug work
                         * but is wrong debug program in editor wtih differnt
                         * program in memory)
                         */
                        JOptionPane.showMessageDialog(this, "Can't debug this program because in memory is different  program");
                        return false;
                    }

                    //if all hex data are tested then break
                    if (index == program.size()) {
                        break;
                    }
                }

                //set listener that call after memory acess
                this.memory.setOnMemoryAccessListener((ActionEvent e) -> {
                    this.handler.highlight(e.getID() - this.offset);
                });
            } else {
                //set listener as null
                this.memory.setOnMemoryAccessListener(null);
                //remove all highlight
                this.handler.highlight(-1);
            }
        }
        return true;
    }

    /**
     * Upload code from editor to memory
     *
     */
    public void uploadCodeToMemory() {
        //find memory in project
        findMemoryInProject();
        //upload
        if (this.memory != null) {
            try {
                //load data from hex editor to memory
                List<Byte> program = getHexData();
                this.memory.loadData(program, this.offset, this.clear);
                //message
                JOptionPane.showMessageDialog(
                        this, "Hex code successfully uploaded to [" + this.memID + "]\n"
                        + "Program size: " + program.size() + " B ["
                        + String.format("%.2f", (program.size() / (float) this.memory.getData().length) * 100) + "%]\n"
                        + "Memory max capacity: " + this.memory.getData().length + " B",
                        "Upload complete",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Can't upload program");
                Logger.getLogger(HexEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Find memory in project and get referens on it (for program uploading)
     */
    private void findMemoryInProject() {
        if (this.memID == null) {
            return;
        }
        //get id of memory and workspace name
        String arr[] = this.memID.split(":");
        if (arr.length != 2) {
            return;
        }
        String id_m = arr[1];
        String id_w = arr[0];

        //find rom in project with "memID"
        for (ProjectFile pf : super.getProject().getProjectFiles()) {
            if (pf.getComp().getName().equals(id_w)) {
                if (pf instanceof WorkSpace) {
                    //finding of ROM RAM in workspace, must have same id as "memID" 
                    for (WorkSpaceObject obj : ((WorkSpace) pf).getObjects()) {
                        if (obj instanceof ROMRAM) {
                            ROMRAM rom = (ROMRAM) obj;
                            //upload data
                            if (rom.getID().endsWith(id_m)) {
                                //remove listener of memory (listener is for highlighting actual accessed command in hexeditorarea)
                                if (this.memory != rom) {
                                    if (this.memory != null) {
                                        this.memory.setOnMemoryAccessListener(null);
                                    }
                                }
                                //set new memory
                                this.memory = rom;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private class HEXHandler extends HexEditorArea implements
            PFHandler, MouseWheelListener, MouseListener {

        private int font_size = 16;

        private final HexEditor owner;

        public HEXHandler(HexEditor owner) {
            this.owner = owner;
            super.addMouseWheelListener(this);
            super.getCodePanel().addMouseListener(this);
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
