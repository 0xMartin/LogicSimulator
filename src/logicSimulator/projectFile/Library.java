/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.projectFile;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.LineBorder;
import logicSimulator.ExceptionLogger;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.PFHandler;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.Tools;
import logicSimulator.data.FileIO;
import logicSimulator.data.IOProject;
import logicSimulator.ui.LabelHQ;
import logicSimulator.ui.LibraryInfoPanel;
import logicSimulator.ui.LibraryModuleInfo;

/**
 *
 * @author Martin
 */
public class Library extends ProjectFile {

    private final LibHandler handler;

    //info about lib
    private transient String author, date, description;

    public Library(String name, Project project) {
        super(project);
        super.setName(name);
        super.setLayout(new BorderLayout());

        this.handler = new LibHandler();
        this.handler.setName(name);
        super.add(this.handler, BorderLayout.CENTER);
    }

    @Override
    public PFHandler getHandler() {
        return this.handler;
    }

    @Override
    public void backUpData(String projectDirectoryPath) throws Exception {

    }

    @Override
    public void restoreData(File file) throws Exception {
        loadLib();
    }

    public void loadLib() throws Exception {

        //unzip all project file from lib file and each add to the project
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(
                new File(super.getProject().getFile().getAbsoluteFile().getParentFile()
                        + "/" + this.getName() + "." + LogicSimulatorCore.LIBRARY)))) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                //unzip single file
                File newFile = new File(zipEntry.getName());
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zipEntry = zis.getNextEntry();

                //load project file from new file
                try {
                    if (newFile.getName().endsWith(LogicSimulatorCore.WORKSPACE_FILE_TYPE)) {
                        //workspace ################################################
                        ProjectFile pf = new WorkSpace(Tools.fileName(newFile.getName()), super.getProject());
                        pf.isLibFile = true;
                        pf.libName = this.getName();
                        pf.restoreData(newFile);
                        super.getProject().getProjectFiles().add(pf);

                    } else if (newFile.getName().endsWith(LogicSimulatorCore.MODULE_FILE_TYPE)) {
                        //module ################################################
                        ProjectFile pf = new ModuleEditor(Tools.fileName(newFile.getName()), super.getProject(), null);
                        pf.isLibFile = true;
                        pf.libName = this.getName();
                        pf.restoreData(newFile);
                        super.getProject().getProjectFiles().add(pf);

                    } else if (newFile.getName().equals("info.txt")) {
                        //load info about library
                        String[] lines = FileIO.readText(newFile).split("\n");
                        this.author = lines[0];
                        this.date = lines[1];
                        this.description = "";
                        for (int i = 2; i < lines.length; ++i) {
                            this.description += lines[i] + '\n';
                        }
                    }
                } catch (Exception ex) {
                    ExceptionLogger.getInstance().logException(ex);
                }

                //delete file
                newFile.delete();
            }

            zis.closeEntry();
        }

        //link logic models (workspaces) with modules
        for (ProjectFile pf : super.getProject().getProjectFiles()) {
            if (pf instanceof ModuleEditor) {
                //get name of logic model
                ModuleEditor mEditor = ((ModuleEditor) pf);
                String lmName = mEditor.getLogicModelName();
                //find workspace
                for (ProjectFile pf2 : super.getProject().getProjectFiles()) {
                    if (pf == pf2) {
                        continue;
                    }
                    if (pf2 instanceof WorkSpace) {
                        if (lmName.equals(((WorkSpace) pf2).getName())) {
                            //set pf2 workspace for module editor
                            ((ModuleEditor) pf).setWorkSpace((WorkSpace) pf2);
                            break;
                        }
                    }
                }
            }
        }

        //rebuild UI
        this.handler.rebuild();
    }

    private class LibHandler extends JPanel implements
            PFHandler, MouseWheelListener, MouseListener {

        public LibHandler() {
            super.setLayout(new BorderLayout());
        }

        public void rebuild() {
            super.removeAll();

            final Font f = new Font("tahoma", Font.BOLD, 14);

            //count number of modules in library
            int libModuleCount = 0;
            for (ProjectFile pf : getProject().getProjectFiles()) {
                if (pf instanceof WorkSpace) {
                    //only lib files
                    if (pf.isLibFile) {
                        if (pf.libName.equals(this.getName())) {
                            libModuleCount++;
                        }
                    }
                }
            }

            //panel for modules of this lib
            JPanel body = new JPanel(new GridLayout(1 + libModuleCount, 1));

            //info
            LibraryInfoPanel lIP = new LibraryInfoPanel();
            lIP.jLabelAuthor.setText(author);
            lIP.jLabelDate.setText(date);
            lIP.jLabelLibName.setText(getName());
            lIP.jTextArea.setText(description);
            body.add(lIP);

            //add view for each module
            LibraryModuleInfo lmi;
            for (ProjectFile pf : getProject().getProjectFiles()) {
                if (pf instanceof ModuleEditor) {
                    //only lib files
                    if (pf.isLibFile) {
                        if (pf.libName.equals(this.getName())) {

                            lmi = new LibraryModuleInfo();
                            lmi.jLabelName.setText(pf.getName());
                            lmi.jEditorPaneDocumentation.setText(((ModuleEditor) pf).getDocumentation());
                            //image of circuit
                            BufferedImage img = Tools.createImage(
                                    ((ModuleEditor) pf).getModule().getLogicModel(), 450, 350);
                            lmi.jLabelCircuitImage.setIcon(new ImageIcon(img));

                            body.add(lmi);
                        }
                    }
                }
            }

            super.add(new JScrollPane(body), BorderLayout.CENTER);
        }

        @Override
        public void zoom(int ration) {

        }

        @Override
        public Point getCursorPosition() {
            return null;
        }

        @Override
        public void repaintPF() {
            this.repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

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
