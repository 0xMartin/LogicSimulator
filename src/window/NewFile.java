/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import logicSimulator.Tools;
import javax.swing.ImageIcon;
import logicSimulator.projectFile.HexEditor;
import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.objects.LogicModule;
import logicSimulator.projectFile.DocumentationEditor;

/**
 *
 * @author Martin
 */
public class NewFile extends javax.swing.JFrame {

    private Project project = null;

    private MainWindow window = null;

    /**
     * Creates new form NewFile
     *
     * @param window Main window
     * @param project Project
     */
    public NewFile(MainWindow window, Project project) {
        initComponents();
        //load project and MainWindow
        this.window = window;
        this.project = project;
        //center location
        super.setLocationRelativeTo(window);
        this.jListFiles.setSelectedIndex(0);
        this.update();
    }

    private void update() {
        //info
        String fileType = "";
        ImageIcon img = null;
        try {
            switch (this.jListFiles.getSelectedValue()) {
                case "Workspace":
                    img = new ImageIcon(this.getClass().getResource("/src/img/workspace_animation.gif"));
                    fileType = "." + logicSimulator.LogicSimulatorCore.WORKSPACE_FILE_TYPE;
                    break;
                case "Logic module":
                    img = null;
                    fileType = "." + logicSimulator.LogicSimulatorCore.MODULE_FILE_TYPE;
                    break;
                case "HEX editor":
                    img = null;
                    fileType = "." + logicSimulator.LogicSimulatorCore.HEX_FILE_TYPE;
                    break;
                case "Documentation":
                    img = null;
                    fileType = "." + logicSimulator.LogicSimulatorCore.HEX_FILE_TYPE;
                    break;
            }
        } catch (Exception ex) {
        }
        //display location
        this.jLabelLocation.setText(
                "Location: " + this.project.getFile().getAbsoluteFile().getParent()
                + "\\" + this.jTextFieldName.getText() + fileType
        );
        //project info
        this.jLabelProject.setText(
                "Project: " + this.project.getName()
        );
        //set image
        this.jLabelGif.setIcon(img);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonAdd = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jLabelGif = new javax.swing.JLabel(){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Tools.setHighQuality(g2);
                super.paintComponent((Graphics2D) g);
            }
        };
        jScrollPane1 = new javax.swing.JScrollPane();
        jListFiles = new javax.swing.JList<>();
        jLabelLocation = new javax.swing.JLabel();
        jLabelProject = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New file");
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jLabel2.setText("Name:");

        jTextFieldName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNameKeyReleased(evt);
            }
        });

        jLabelGif.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Files"));

        jListFiles.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Workspace", "Logic module", "Script module", "HEX editor", "Documentation" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jListFiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListFilesMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jListFiles);

        jLabelLocation.setText("Location:");

        jLabelProject.setText("Project:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelGif, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jTextFieldName))
                            .addComponent(jLabelLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelProject, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonAdd))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelLocation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelProject)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelGif, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        if (this.lastName.length() == 0) {
            return;
        }

        //add file
        ProjectFile pf = null;
        Class<?> fileType = null;
        switch (this.jListFiles.getSelectedValue()) {
            case "Workspace":
                pf = new WorkSpace(this.lastName, this.project);
                fileType = WorkSpace.class;
                break;
            case "Logic module":
                pf = new ModuleEditor(this.lastName, this.project, new LogicModule(new Point(0, 0), this.lastName));
                fileType = ModuleEditor.class;
                break;
            case "HEX editor":
                pf = new HexEditor(this.lastName, this.project);
                fileType = HexEditor.class;
                break;
            case "Documentation":
                pf = new DocumentationEditor(this.lastName, this.project);
                fileType = DocumentationEditor.class;
                break;
        }

        if (pf != null) {

            //if name exist then return
            for (ProjectFile pf2 : this.project.getProjectFiles()) {
                try {
                    if (pf2.getComp().getName().endsWith(this.lastName)) {
                        if(fileType.isInstance(pf2)){
                            return;
                        }
                    }
                } catch (Exception ex) {
                }
            }

            //add file to project add display it
            this.window.getPFDockingPanel().displayProjectFile(pf);
            this.project.getProjectFiles().add(pf);
            this.window.updateProjectView();

            //refresh layout of docking pane
            this.window.getPFDockingPanel().refreshLayout();
            
            //dispose this window
            dispose();
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jListFilesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListFilesMouseReleased
        this.update();
    }//GEN-LAST:event_jListFilesMouseReleased

    private void jTextFieldNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNameKeyReleased
        if (evt.getKeyChar() == '.' || evt.getKeyChar() == ' ' || evt.getKeyChar() == '=') {
            this.jTextFieldName.setText(this.lastName);
        }
        this.update();
        this.lastName = this.jTextFieldName.getText();
    }//GEN-LAST:event_jTextFieldNameKeyReleased

    private String lastName = "";

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelGif;
    private javax.swing.JLabel jLabelLocation;
    private javax.swing.JLabel jLabelProject;
    private javax.swing.JList<String> jListFiles;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldName;
    // End of variables declaration//GEN-END:variables
}
