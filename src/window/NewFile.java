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
package window;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import logicSimulator.Tools;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

    private String selected = "Workspace";

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
        this.update();
    }

    private void update() {
        //info
        String fileType = "";
        ImageIcon img = null;
        try {
            switch (this.selected) {
                case "Workspace":
                    img = new ImageIcon(this.getClass().getResource("/src/img/workspace-preview.gif"));
                    fileType = "." + logicSimulator.LogicSimulatorCore.WORKSPACE_FILE_TYPE;
                    break;
                case "Module":
                    img = new ImageIcon(this.getClass().getResource("/src/img/module-preview.gif"));
                    fileType = "." + logicSimulator.LogicSimulatorCore.MODULE_FILE_TYPE;
                    break;
                case "HEX file":
                    img = new ImageIcon(this.getClass().getResource("/src/img/hexFile-preview.gif"));
                    fileType = "." + logicSimulator.LogicSimulatorCore.HEX_FILE_TYPE;
                    break;
                case "Documentation":
                    img = new ImageIcon(this.getClass().getResource("/src/img/documentation-preview.gif"));
                    fileType = "." + logicSimulator.LogicSimulatorCore.DOCUMENTATION_FILE_TYPE;
                    break;
            }
        } catch (Exception ex) {
        }
        //display location
        this.jLabelLocation.setText(
                this.project.getFile().getAbsoluteFile().getParent()
                + "\\" + this.jTextFieldName.getText() + fileType
        );
        //project info
        this.jLabelProject.setText(this.project.getName());
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

        jLabelGif = new javax.swing.JLabel(){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Tools.setHighQuality(g2);
                super.paintComponent((Graphics2D) g);
            }
        };
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jLabelLocation = new javax.swing.JLabel();
        jLabelProject = new javax.swing.JLabel();
        jButtonAdd = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New file");
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        jLabelGif.setBackground(new java.awt.Color(255, 255, 255));
        jLabelGif.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelGif.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabelGif.setOpaque(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Name:");

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Location:");

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Project:");

        jTextFieldName.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTextFieldName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNameKeyReleased(evt);
            }
        });

        jLabelLocation.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabelLocation.setForeground(new java.awt.Color(0, 0, 0));
        jLabelLocation.setText(" ");

        jLabelProject.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabelProject.setForeground(new java.awt.Color(0, 0, 0));
        jLabelProject.setText(" ");

        jButtonAdd.setBackground(new java.awt.Color(255, 255, 255));
        jButtonAdd.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonAdd.setForeground(new java.awt.Color(0, 0, 0));
        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(75, 110, 175));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Workspace");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonsActionPerformed(evt);
            }
        });

        jButton2.setText("Module");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonsActionPerformed(evt);
            }
        });

        jButton3.setText("HEX file");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonsActionPerformed(evt);
            }
        });

        jButton4.setText("Documentation");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelProject, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextFieldName)
                                .addGap(106, 106, 106)
                                .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAdd))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabelLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabelProject))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelGif, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabelGif, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNameKeyReleased
        if (evt.getKeyChar() == '.' || evt.getKeyChar() == ' ' || evt.getKeyChar() == '=') {
            this.jTextFieldName.setText(this.lastName);
        }
        this.update();
        this.lastName = this.jTextFieldName.getText();
    }//GEN-LAST:event_jTextFieldNameKeyReleased

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        if (this.lastName.length() == 0) {
            return;
        }

        //add file
        ProjectFile pf = null;
        Class<?> fileType = null;
        switch (this.selected) {
            case "Workspace":
                pf = new WorkSpace(this.lastName, this.project);
                fileType = WorkSpace.class;
                break;
            case "Module":
                pf = new ModuleEditor(this.lastName, this.project, new LogicModule(new Point(0, 0), this.lastName));
                fileType = ModuleEditor.class;
                break;
            case "HEX file":
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
                        if (fileType.isInstance(pf2)) {
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

    private void jButtonsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonsActionPerformed
        if (evt.getSource() == this.jButton1) {
            buttonActivation(this.jButton1, true);
            buttonActivation(this.jButton2, false);
            buttonActivation(this.jButton3, false);
            buttonActivation(this.jButton4, false);
        } else if (evt.getSource() == this.jButton2) {
            buttonActivation(this.jButton1, false);
            buttonActivation(this.jButton2, true);
            buttonActivation(this.jButton3, false);
            buttonActivation(this.jButton4, false);
        } else if (evt.getSource() == this.jButton3) {
            buttonActivation(this.jButton1, false);
            buttonActivation(this.jButton2, false);
            buttonActivation(this.jButton3, true);
            buttonActivation(this.jButton4, false);
        } else {
            buttonActivation(this.jButton1, false);
            buttonActivation(this.jButton2, false);
            buttonActivation(this.jButton3, false);
            buttonActivation(this.jButton4, true);
        }

        this.selected = ((JButton) evt.getSource()).getText();

        update();
    }//GEN-LAST:event_jButtonsActionPerformed

    private void buttonActivation(JButton button, boolean state) {
        button.setBackground(state ? new Color(75, 110, 175) : Color.WHITE);
        button.setForeground(state ? Color.WHITE : Color.BLACK);
    }

    private String lastName = "";

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelGif;
    private javax.swing.JLabel jLabelLocation;
    private javax.swing.JLabel jLabelProject;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextFieldName;
    // End of variables declaration//GEN-END:variables
}
