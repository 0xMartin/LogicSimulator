/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import logicSimulator.ui.SystemResources;
import javax.swing.JList;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.ModuleEditor;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.WorkSpace;
import logicSimulator.WorkSpaceObject;
import logicSimulator.Tools;
import logicSimulator.common.LogicModule;
import logicSimulator.common.Model;
import logicSimulator.objects.wiring.BitGet;
import logicSimulator.objects.control.Button;
import logicSimulator.objects.displays.Bulp;
import logicSimulator.objects.gates.And;
import logicSimulator.objects.gates.Buffer;
import logicSimulator.objects.gates.Nand;
import logicSimulator.objects.gates.Nor;
import logicSimulator.objects.gates.Not;
import logicSimulator.objects.gates.Nxor;
import logicSimulator.objects.gates.Or;
import logicSimulator.objects.gates.Xor;
import logicSimulator.objects.wiring.BitSet;
import logicSimulator.objects.wiring.Bridge;
import logicSimulator.objects.wiring.Input;
import logicSimulator.objects.wiring.Output;
import window.components.PropertieEditor;

/**
 *
 * @author Martin
 */
public class ComponentChooser extends javax.swing.JFrame {

    //default components    
    public static final String[] GATE = {"BUFFER", "NOT", "OR", "NOR", "AND", "NAND", "XOR", "NXOR"};
    public static final String[] DISPLAY = {"BULP", "7 SEG", "DISPLAY"};
    public static final String[] CONTROL = {"BUTTON", "POTENTIOMETER"};
    public static final String[] WIRING = {"BIT GET", "BIT SET", "INPUT", "OUTPUT", "BRIDGE"};
    public static String[] MODULES = null;
    public static final List<LogicModule> modules = new ArrayList<>();

    private void reloadList() {
        String[] list = null;
        switch (this.jComboBox1.getSelectedItem().toString()) {
            case "Gate":
                list = ComponentChooser.GATE;
                break;
            case "Display":
                list = ComponentChooser.DISPLAY;
                break;
            case "Control":
                list = ComponentChooser.CONTROL;
                break;
            case "Wiring":
                list = ComponentChooser.WIRING;
                break;
            case "Modules":
                //get all modules from project
                ComponentChooser.modules.clear();
                this.project.getProjectFiles().stream().forEach((pf) -> {
                    if (pf instanceof ModuleEditor) {
                        LogicModule m = ((ModuleEditor) pf).getModule();
                        if (m != null) {
                            ComponentChooser.modules.add(m);
                        }
                    }
                });
                //get names of each module
                int index = 0;
                ComponentChooser.MODULES = new String[ComponentChooser.modules.size()];
                for (int i = 0; i < this.project.getProjectFiles().size(); i++) {
                    ProjectFile pf = this.project.getProjectFiles().get(i);
                    if (pf instanceof ModuleEditor) {
                        ComponentChooser.MODULES[index++] = ((ModuleEditor) pf).getName();
                    }
                }
                list = ComponentChooser.MODULES;
                break;
        }
        if (list != null) {
            this.model.clear();
            for (String item : list) {
                this.model.addElement(item);
            }
        }
    }

    /**
     * Select object
     */
    private WorkSpaceObject selectedObject = null;

    public WorkSpaceObject getSelectedComponent() {
        return this.selectedObject;
    }

    private Project project;

    private final MainWindow mWindow;

    private final DefaultListModel<String> model;

    /**
     * Creates new form ComponentChooser
     *
     * @param mWindow MainWindow
     */
    public ComponentChooser(MainWindow mWindow) {
        this.model = new DefaultListModel<>();
        this.mWindow = mWindow;
        initComponents();
        if (Toolkit.getDefaultToolkit().isAlwaysOnTopSupported()) {
            this.setAlwaysOnTop(true);
        }
    }

    /**
     * Set porject
     *
     * @param project
     */
    public void setProject(Project project) {
        if (project != null) {
            this.project = project;
        }
    }

    /**
     * Choose and place new component to selcted workspace (from project)
     */
    public void chooseComponent() {
        //show
        this.setVisible(true);
        this.setLocation(
                this.mWindow.getX() + (this.mWindow.getWidth() - this.getWidth()) / 2,
                this.mWindow.getY() + (this.mWindow.getHeight() - this.getHeight()) / 2
        );
        this.jSplitPane1.setDividerLocation(0.3f);
        //reload list
        reloadList();
        //show first component
        this.jList1.setSelectedIndex(0);
        showSelectedComponent();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanelLeft = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldFind = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanelRight = new javax.swing.JPanel();
        jButtonPlace = new javax.swing.JButton();
        jPanelView = new javax.swing.JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                /**
                * Draw image of component
                */
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);

                g2.clearRect(0, 0, jPanelView.getWidth(), jPanelView.getHeight());
                if(selectedObject != null){
                    Model m = selectedObject.getModel();
                    int max  = Math.max(
                        m.getWidth(),
                        m.getHeight()
                    ) + 30;
                    float f = jPanelView.getWidth() / (float) max;
                    g2.scale(f, f);
                    selectedObject.getPosition().x = (int)((jPanelView.getWidth() - m.getBoundsMax().x - m.getBoundsMin().x)/ (2f * f));
                    selectedObject.getPosition().y = (int)((jPanelView.getHeight() - m.getBoundsMax().y - m.getBoundsMin().y) / (2f * f));
                    selectedObject.getPosition().x -= (int)((m.getBoundsMax().x + m.getBoundsMin().x) / (2f * f));
                    selectedObject.getPosition().y -= (int)((m.getBoundsMax().y + m.getBoundsMin().y) / (2f * f));
                    selectedObject.render(
                        g2,
                        new Point(0, 0),
                        jPanelView.getSize()
                    );
                }
            }
        };
        jLabelType = new javax.swing.JLabel();
        jLabelName = new javax.swing.JLabel();
        jLabelSize = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPaneInfo = new javax.swing.JEditorPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableProperties = new PropertieEditor();
        jButtonDefault = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Component chooser");
        setIconImage(SystemResources.ICON);
        setType(java.awt.Window.Type.UTILITY);

        jSplitPane1.setDividerLocation(300);

        jLabel1.setText("Find:");

        jTextFieldFind.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldFindKeyReleased(evt);
            }
        });

        jList1.setModel(model);
        jList1.setCellRenderer(new ComponentListCellRenderer());
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jList1MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Gate", "Control", "Display", "Wiring", "Modules" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLeftLayout = new javax.swing.GroupLayout(jPanelLeft);
        jPanelLeft.setLayout(jPanelLeftLayout);
        jPanelLeftLayout.setHorizontalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(jPanelLeftLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldFind))
                    .addComponent(jComboBox1, 0, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelLeftLayout.setVerticalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanelLeft);

        jButtonPlace.setText("Place");
        jButtonPlace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPlaceActionPerformed(evt);
            }
        });

        jPanelView.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanelViewLayout = new javax.swing.GroupLayout(jPanelView);
        jPanelView.setLayout(jPanelViewLayout);
        jPanelViewLayout.setHorizontalGroup(
            jPanelViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );
        jPanelViewLayout.setVerticalGroup(
            jPanelViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );

        jLabelType.setText("Type:");

        jLabelName.setText("Name:");

        jLabelSize.setText("Size:");

        jEditorPaneInfo.setEditable(false);
        jEditorPaneInfo.setContentType("text/html"); // NOI18N
        jScrollPane2.setViewportView(jEditorPaneInfo);

        jTableProperties.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Propertie", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableProperties.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTablePropertiesMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jTableProperties);

        jButtonDefault.setText("Add to toolbar");
        jButtonDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDefaultActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .addComponent(jLabelType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanelView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelRightLayout.createSequentialGroup()
                        .addComponent(jButtonDefault, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPlace, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRightLayout.createSequentialGroup()
                        .addComponent(jPanelView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonPlace)
                            .addComponent(jButtonDefault)))
                    .addGroup(jPanelRightLayout.createSequentialGroup()
                        .addComponent(jLabelType)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanelRight);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        reloadList();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButtonPlaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPlaceActionPerformed
        if (this.project == null) {
            dispose();
            return;
        }
        try {
            List<WorkSpaceObject> list = new ArrayList<>();
            WorkSpaceObject obj = Tools.clone(this.selectedObject);
            if (obj != null) {
                obj.getPosition().x = LogicSimulatorCore.OBJECT_NULL_POSITION;
                list.add(obj);
                //add selected component to workspace
                ProjectFile pf = this.project.getSelectedFile();
                if (pf instanceof WorkSpace) {
                    ((WorkSpace) pf).addNewObjects(list);
                }
                //add to last added component combobox
                this.mWindow.addComponentToLastAdded(obj);
                dispose();
            }
        } catch (CloneNotSupportedException ex) {
        }
    }//GEN-LAST:event_jButtonPlaceActionPerformed

    /**
     * Select and display component
     *
     * @param evt
     */
    private void jList1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseReleased
        showSelectedComponent();
    }//GEN-LAST:event_jList1MouseReleased

    private void jTextFieldFindKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFindKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.jTextFieldFind.setText(this.jTextFieldFind.getText().toUpperCase());
            Point m0 = getBestMatch(ComponentChooser.MODULES, this.jTextFieldFind.getText());
            Point m1 = getBestMatch(ComponentChooser.GATE, this.jTextFieldFind.getText());
            Point m2 = getBestMatch(ComponentChooser.CONTROL, this.jTextFieldFind.getText());
            Point m3 = getBestMatch(ComponentChooser.DISPLAY, this.jTextFieldFind.getText());
            Point m4 = getBestMatch(ComponentChooser.WIRING, this.jTextFieldFind.getText());
            int[] dat = new int[]{m0.y, m1.y, m2.y, m3.y, m4.y};
            int[] indexList = new int[]{m0.x, m1.x, m2.x, m3.x, m4.x};
            //find best
            int max = dat[0];
            int index = 0;
            for (int i = 1; i < dat.length; i++) {
                if (dat[i] > max) {
                    max = dat[i];
                    index = i;
                }
            }
            //open in list and select
            this.jComboBox1.setSelectedIndex(index);
            //reload list
            reloadList();
            //select component
            this.jList1.setSelectedIndex(indexList[index]);
            showSelectedComponent();
        }
    }//GEN-LAST:event_jTextFieldFindKeyReleased

    private void jTablePropertiesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablePropertiesMouseReleased
        this.jPanelView.repaint();
    }//GEN-LAST:event_jTablePropertiesMouseReleased

    private void jButtonDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDefaultActionPerformed
        //add selected component to toolbar
        this.mWindow.addComponentToToolbar(this.selectedObject);
    }//GEN-LAST:event_jButtonDefaultActionPerformed

    /**
     * Get best match item
     *
     * @param list String list
     * @param find Find
     * @return [index, number of matching chars in right order]
     */
    public Point getBestMatch(String[] list, String find) {
        Point ret = new Point(0, 0);
        for (int i = 0; i < list.length; i++) {
            int match = 0;
            for (int j = 0; match < find.length() && j < list[i].length(); j++) {
                if (find.charAt(match) == list[i].charAt(j)) {
                    match++;
                }
            }
            if (match > ret.y) {
                ret.x = i;
                ret.y = match;
            }
        }
        return ret;
    }

    private void showSelectedComponent() {
        String item = this.jList1.getSelectedValue();
        if (item == null) {
            return;
        }
        //select
        selectComponent(item);
        //display component info
        this.jPanelView.repaint();
        this.jLabelType.setText("Type: " + this.jComboBox1.getSelectedItem().toString());
        this.jLabelName.setText("Name: " + item);
        this.jLabelSize.setText("Size: "
                + this.selectedObject.getModel().getWidth() / LogicSimulatorCore.WORK_SPACE_STEP
                + ", " + this.selectedObject.getModel().getHeight() / LogicSimulatorCore.WORK_SPACE_STEP);
        //edit propt
        ((PropertieEditor) this.jTableProperties).edit(this.selectedObject);
        //info html
        try {
            InputStream is = getClass().getResourceAsStream("/src/doc/" + item.toLowerCase() + ".html");
            if (is != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is)
                );
                String total = "", line;
                while ((line = reader.readLine()) != null) {
                    total += line;
                }
                //replace image ref
                try {
                    total = total.replaceAll(
                            "IMG_REF",
                            this.getClass().getResource("/src/doc/" + item.toLowerCase() + ".gif").toURI().toString()
                    );
                } catch (Exception ex) {
                }
                //display html
                this.jEditorPaneInfo.setText(total);
            } else {
                this.jEditorPaneInfo.setText("");
            }
        } catch (Exception ex) {
        }
    }

    private void selectComponent(String id) {
        switch (id) {
            //gate
            case "BUFFER":
                this.selectedObject = new Buffer(new Point(0, 0), 1);
                break;
            case "NOT":
                this.selectedObject = new Not(new Point(0, 0), 1);
                break;
            case "OR":
                this.selectedObject = new Or(new Point(0, 0), 1, 2);
                break;
            case "NOR":
                this.selectedObject = new Nor(new Point(0, 0), 1, 2);
                break;
            case "AND":
                this.selectedObject = new And(new Point(0, 0), 1, 2);
                break;
            case "NAND":
                this.selectedObject = new Nand(new Point(0, 0), 1, 2);
                break;
            case "XOR":
                this.selectedObject = new Xor(new Point(0, 0), 1, 2);
                break;
            case "NXOR":
                this.selectedObject = new Nxor(new Point(0, 0), 1, 2);
                break;
            //control
            case "BUTTON":
                this.selectedObject = new Button(new Point(0, 0), 1);
                break;
            //display
            case "BULP":
                this.selectedObject = new Bulp(new Point(0, 0), 1);
                break;
            //wiring
            case "BIT GET":
                this.selectedObject = new BitGet(new Point(0, 0), new boolean[1]);
                break;
            case "BIT SET":
                this.selectedObject = new BitSet(new Point(0, 0), new int[1]);
                break;
            case "INPUT":
                this.selectedObject = new Input(new Point(0, 0), 1);
                break;
            case "OUTPUT":
                this.selectedObject = new Output(new Point(0, 0), 1);
                break;
            case "BRIDGE":
                this.selectedObject = new Bridge(new Point(0, 0), "A");
                break;
            //modules
            default:
                if (ComponentChooser.MODULES != null) {
                    for (int i = 0; i < ComponentChooser.MODULES.length; i++) {
                        if (ComponentChooser.MODULES[i].equals(id)) {
                            this.selectedObject = ComponentChooser.modules.get(i).cloneObject();
                            break;
                        }
                    }
                }
                break;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDefault;
    private javax.swing.JButton jButtonPlace;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JEditorPane jEditorPaneInfo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelSize;
    private javax.swing.JLabel jLabelType;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelView;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTableProperties;
    private javax.swing.JTextField jTextFieldFind;
    // End of variables declaration//GEN-END:variables

    private class ComponentListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                this.setBackground(list.getSelectionBackground());
                this.setForeground(list.getSelectionForeground());
            } else {
                this.setBackground(index % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
            }
            return this;
        }

    }

}
