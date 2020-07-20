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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileFilter;
import logicSimulator.common.LinkASM;
import logicSimulator.objects.complex.MCU;
import logicSimulator.objects.output.VectorScreen;
import logicSimulator.ui.SystemResources;

/**
 *
 * @author Martin
 */
public class TraslatorConfig extends javax.swing.JFrame {

    private final List<LinkASM> translator;

    private final DefaultListModel model = new DefaultListModel();

    private final JFileChooser chooser = new JFileChooser();

    /**
     * Creates new form Linker
     *
     * @param translator
     */
    public TraslatorConfig(List<LinkASM> translator) {
        this.translator = translator;
        initComponents();
        refreshLinkerView();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        super.setLocation(
                (screen.width - super.getWidth()) / 2,
                (screen.height - super.getHeight()) / 2
        );
    }

    private void refreshLinkerView() {
        this.model.clear();
        int index = 0;
        for (LinkASM link : this.translator) {
            this.model.add(index++, link);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuEdit = new javax.swing.JPopupMenu();
        jMenuItemEdit = new javax.swing.JMenuItem();
        jMenuItemDelete = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>(this.model);
        jPanel1 = new javax.swing.JPanel();
        jButtonAdd2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldMnemonic = new javax.swing.JTextField();
        jTextFieldHex = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaComment = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemImportLocal = new javax.swing.JMenuItem();
        jMenuItemImport = new javax.swing.JMenuItem();
        jMenuItemExport = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemClearAll = new javax.swing.JMenuItem();

        jMenuItemEdit.setText("Edit");
        jMenuItemEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditActionPerformed(evt);
            }
        });
        jPopupMenuEdit.add(jMenuItemEdit);

        jMenuItemDelete.setText("Delete");
        jMenuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteActionPerformed(evt);
            }
        });
        jPopupMenuEdit.add(jMenuItemDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Translator Configuration");
        setIconImage(SystemResources.TOOLBAR_TRASLATOR_CONFIG.getImage());

        jList1.setCellRenderer(new LinkerListRenderer());
        jList1.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jList1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Link", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 12))); // NOI18N

        jButtonAdd2.setFont(new java.awt.Font("Consolas", 1, 13)); // NOI18N
        jButtonAdd2.setText("Add");
        jButtonAdd2.setToolTipText("Add new link");
        jButtonAdd2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdd2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Consolas", 1, 13)); // NOI18N
        jLabel2.setText("Hex:");

        jLabel1.setFont(new java.awt.Font("Consolas", 1, 13)); // NOI18N
        jLabel1.setText("Mnemonic:");

        jTextFieldMnemonic.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jTextFieldMnemonic.setForeground(new java.awt.Color(0, 0, 153));
        jTextFieldMnemonic.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldMnemonicKeyReleased(evt);
            }
        });

        jTextFieldHex.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jTextFieldHex.setForeground(new java.awt.Color(0, 153, 0));
        jTextFieldHex.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldHexKeyReleased(evt);
            }
        });

        jTextAreaComment.setColumns(20);
        jTextAreaComment.setFont(new java.awt.Font("Consolas", 3, 12)); // NOI18N
        jTextAreaComment.setLineWrap(true);
        jTextAreaComment.setRows(5);
        jTextAreaComment.setToolTipText("Comment");
        jTextAreaComment.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextAreaComment);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldMnemonic)
                            .addComponent(jTextFieldHex, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButtonAdd2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextFieldMnemonic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldHex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jMenu1.setText("File");

        jMenuItemImportLocal.setText("Import local");
        jMenuItemImportLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportLocalActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemImportLocal);

        jMenuItemImport.setText("Import from file");
        jMenuItemImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemImport);

        jMenuItemExport.setText("Export to file");
        jMenuItemExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExport);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItemClearAll.setText("Clear all");
        jMenuItemClearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemClearAllActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemClearAll);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAdd2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdd2ActionPerformed
        try {
            addToTranslator(
                    new LinkASM(
                            this.jTextFieldMnemonic.getText(),
                            this.jTextFieldHex.getText(),
                            this.jTextAreaComment.getText()
                    )
            );
            this.refreshLinkerView();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButtonAdd2ActionPerformed

    private int selected_list_item = -1;

    private void jList1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MousePressed
        int selected = this.jList1.getSelectedIndex();
        if (this.selected_list_item == selected) {
            //show edit menu
            this.jPopupMenuEdit.show(this, evt.getX(), evt.getY() - this.jScrollPane1.getVerticalScrollBar().getValue());
        }

        //selected index in list with linkers
        this.selected_list_item = selected;
    }//GEN-LAST:event_jList1MousePressed

    private void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteActionPerformed
        //remove selected item from list and traslator list form HexEditorArea
        try {
            this.translator.remove(this.selected_list_item);
            this.refreshLinkerView();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jMenuItemDeleteActionPerformed

    private void jMenuItemEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditActionPerformed
        //edit
        int index = this.jList1.getSelectedIndex();
        if (index >= 0 && index < this.translator.size()) {
            LinkASM link = this.translator.get(index);
            this.jTextFieldMnemonic.setText(link.Mnemonic);
            this.jTextFieldHex.setText("0x" + link.Hex);
            this.jTextAreaComment.setText(link.Comment);
        }
    }//GEN-LAST:event_jMenuItemEditActionPerformed

    private void jMenuItemImportLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportLocalActionPerformed
        //import local 
        JComboBox<String> list = new JComboBox<>(new String[]{"VECTOR SCREEN", "MCU"});
        JTextField prefix = new JTextField();

        JOptionPane.showMessageDialog(this, new Object[]{"Instruction set:", list, "PInstruction prefix:", prefix},
                "Import traslator", JOptionPane.PLAIN_MESSAGE);
        switch ((String) list.getSelectedItem()) {
            case "VECTOR SCREEN":
                for (LinkASM link : VectorScreen.getInstructions()) {
                    link.Mnemonic = prefix.getText() + link.Mnemonic;
                    link.Mnemonic = link.Mnemonic.toLowerCase();
                    addToTranslator(link);
                }
                refreshLinkerView();
                break;
            case "MCU":
                for (LinkASM link : MCU.getInstructions()) {
                    link.Mnemonic = prefix.getText() + link.Mnemonic;
                    link.Mnemonic = link.Mnemonic.toLowerCase();
                    addToTranslator(link);
                }
                refreshLinkerView();
                break;
        }
    }//GEN-LAST:event_jMenuItemImportLocalActionPerformed

    private void jMenuItemImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportActionPerformed
        //import translator list from file
        this.chooser.setDialogTitle("Import");
        this.chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.toString().endsWith(".tl");
            }

            @Override
            public String getDescription() {
                return "Traslator list";
            }
        });
        if (this.chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                FileInputStream fis = new FileInputStream(this.chooser.getSelectedFile());
                ObjectInputStream ois = new ObjectInputStream(fis);
                //read object
                List<LinkASM> trans = (List<LinkASM>) ois.readObject();
                //copy data
                this.translator.clear();
                trans.stream().forEach((link) -> {
                    this.translator.add(link);
                });
                //refresh
                this.refreshLinkerView();
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "File not found", "Error", JOptionPane.PLAIN_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "File cant import file", "Error", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItemImportActionPerformed

    private void jMenuItemExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportActionPerformed
        //export translator list
        this.chooser.setDialogTitle("Export");
        if (this.chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String file = this.chooser.getSelectedFile().toString();
                file = file.endsWith(".tl") ? file : file + ".tl";
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(this.translator);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Cant save file", "Error", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItemExportActionPerformed

    private void jMenuItemClearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearAllActionPerformed
        //clear all
        this.translator.clear();
        this.refreshLinkerView();
    }//GEN-LAST:event_jMenuItemClearAllActionPerformed

    private void jTextFieldHexKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldHexKeyReleased
        if (!this.jTextFieldHex.getText().startsWith("0x")) {
            this.jTextFieldHex.setText("0x" + this.jTextFieldHex.getText());
        }
    }//GEN-LAST:event_jTextFieldHexKeyReleased

    private void jTextFieldMnemonicKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldMnemonicKeyReleased
        try {
            if (this.jTextFieldMnemonic.getText().startsWith("0x")) {
                this.jTextFieldMnemonic.setText(this.jTextFieldHex.getText().substring(2));
            }
            if (this.jTextFieldMnemonic.getText().charAt(0) == '1'
                    || this.jTextFieldMnemonic.getText().charAt(0) == '0'
                    || this.jTextFieldMnemonic.getText().charAt(0) == '\'') {
                this.jTextFieldMnemonic.setText(this.jTextFieldMnemonic.getText().substring(1));
            }
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jTextFieldMnemonicKeyReleased

    /**
     * Add link to translator list if this link is inside than replace it
     *
     * @param link
     */
    private void addToTranslator(LinkASM link) {
        //remove if is inside
        for (int i = 0; i < this.translator.size(); i++) {
            if (this.translator.get(i).Mnemonic.equals(link.Mnemonic)) {
                //remove last
                this.translator.remove(i);
                //replace by firstadd to list
                this.translator.add(i, link);
                return;
            }
        }
        //add as new
        this.translator.add(link);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemClearAll;
    private javax.swing.JMenuItem jMenuItemDelete;
    private javax.swing.JMenuItem jMenuItemEdit;
    private javax.swing.JMenuItem jMenuItemExport;
    private javax.swing.JMenuItem jMenuItemImport;
    private javax.swing.JMenuItem jMenuItemImportLocal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenuEdit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaComment;
    private javax.swing.JTextField jTextFieldHex;
    private javax.swing.JTextField jTextFieldMnemonic;
    // End of variables declaration//GEN-END:variables

    private class LinkerListRenderer<E> extends JPanel implements ListCellRenderer<E> {

        @Override
        public Component getListCellRendererComponent(
                JList<? extends E> list, E value, int index,
                boolean isSelected, boolean cellHasFocus) {

            super.setLayout(new BorderLayout());
            super.setOpaque(true);
            super.removeAll();

            if (value instanceof LinkASM) {
                LinkASM link = (LinkASM) value;
                //color of bg
                String bg = index % 2 == 0 ? "#F0F0F0" : "#D0D0D0";
                bg = isSelected ? "#D0D0FF" : bg;
                //insert line breakers
                String comment = "";
                for (int i = 0, c = 0; i < link.Comment.length(); i++, c++) {
                    comment += link.Comment.charAt(i);
                    if (c >= 50 && link.Comment.charAt(i) == ' ') {
                        comment += "<br>";
                        c = -1;
                    }
                }
                //label
                JLabel label = new JLabel(
                        "<html><table bgcolor=\"" + bg + "\"><tbody>"
                        + "<tr>"
                        + "<td><font color=BLUE>" + link.Mnemonic + "</font> >> <font color=GREEN>0x" + link.Hex + "</font></td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td><i><font color=BLACK>" + comment + "</font></i></td>"
                        + "</tr>"
                        + "</tbody></table></html>");
                label.setFont(list.getFont());
                super.add(label, BorderLayout.CENTER);
            }

            return this;
        }

    }

}
