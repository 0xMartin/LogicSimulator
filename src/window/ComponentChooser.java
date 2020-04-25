/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import logicSimulator.ui.SystemResources;
import javax.swing.JList;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.projectFile.ModuleEditor;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.WorkSpaceObject;
import logicSimulator.Tools;
import logicSimulator.objects.LogicModule;
import logicSimulator.common.Model;
import logicSimulator.objects.aritmetic.BitAdd;
import logicSimulator.objects.aritmetic.BitDiv;
import logicSimulator.objects.aritmetic.BitMul;
import logicSimulator.objects.aritmetic.BitSub;
import logicSimulator.objects.aritmetic.MagnitudeComparator;
import logicSimulator.objects.wiring.BitGet;
import logicSimulator.objects.input.Button;
import logicSimulator.objects.input.Clock;
import logicSimulator.objects.input.RandomGenerator;
import logicSimulator.objects.output.Bulp;
import logicSimulator.objects.output.RasterScreen;
import logicSimulator.objects.output.VectorScreen;
import logicSimulator.objects.gate.And;
import logicSimulator.objects.gate.Buffer;
import logicSimulator.objects.gate.ControledBuffer;
import logicSimulator.objects.gate.ControledNot;
import logicSimulator.objects.gate.DMUX;
import logicSimulator.objects.gate.MUX;
import logicSimulator.objects.gate.Nand;
import logicSimulator.objects.gate.Nor;
import logicSimulator.objects.gate.Not;
import logicSimulator.objects.gate.Nxor;
import logicSimulator.objects.gate.Or;
import logicSimulator.objects.gate.Xor;
import logicSimulator.objects.input.KeyBoard;
import logicSimulator.objects.memory.Counter;
import logicSimulator.objects.memory.DFlipFlop;
import logicSimulator.objects.memory.FIFO;
import logicSimulator.objects.memory.JKFlipFlop;
import logicSimulator.objects.memory.LIFO;
import logicSimulator.objects.memory.ROMRAM;
import logicSimulator.objects.memory.RSFlipFlop;
import logicSimulator.objects.memory.RWMRAM;
import logicSimulator.objects.memory.RWMSAM;
import logicSimulator.objects.memory.Register;
import logicSimulator.objects.memory.TFlipFlop;
import logicSimulator.objects.output.SevenSeg;
import logicSimulator.objects.output.TextScreen;
import logicSimulator.objects.wiring.BitSet;
import logicSimulator.objects.wiring.Bridge;
import logicSimulator.objects.wiring.Constant;
import logicSimulator.objects.wiring.Input;
import logicSimulator.objects.wiring.Output;
import window.components.PropertieEditor;

/**
 *
 * @author Martin
 */
public class ComponentChooser extends javax.swing.JFrame {

    //default components    
    public static final String[] GATE = {
        "Buffer",
        "Controled buffer",
        "Not",
        "Controled not",
        "Or",
        "Nor",
        "And",
        "Nand",
        "Xor",
        "Nxor",
        "MUX",
        "DMUX"
    };
    public static final String[] OUTPUT = {
        "Bulp",
        "7 seg",
        "Raster screen",
        "Vector screen",
        "Text screen"
    };
    public static final String[] INPUT = {
        "Button",
        "Keyboard",
        "Clock",
        "Random generator"
    };
    public static final String[] WIRING = {
        "Constant",
        "Bit get",
        "Bit set",
        "Input",
        "Output",
        "Bridge"
    };
    public static final String[] MEMORY = {
        "ROM RAM",
        "RWM SAM",
        "RWM RAM",
        "Counter",
        "RS flip flop",
        "JK flip flop",
        "D flip flop",
        "T flip flop",
        "Register",
        "LIFO",
        "FIFO"
    };
    public static final String[] ARITMETIC = {
        "Magnitude comparator",
        "Add",
        "Sub",
        "Mul",
        "Div"
    };
    public static String[] MODULES = null;
    public static final List<LogicModule> modules = new ArrayList<>();

    private void reloadList() {
        String[] list = null;
        switch (this.jComboBox1.getSelectedItem().toString()) {
            case "Gate":
                list = ComponentChooser.GATE;
                break;
            case "Output":
                list = ComponentChooser.OUTPUT;
                break;
            case "Input":
                list = ComponentChooser.INPUT;
                break;
            case "Wiring":
                list = ComponentChooser.WIRING;
                break;
            case "Memory":
                list = ComponentChooser.MEMORY;
                break;
            case "Aritmetic":
                list = ComponentChooser.ARITMETIC;
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
        ((PropertieEditor) this.jTableProperties).onPropertieChange((ActionEvent e) -> {
            this.jPanelView.repaint();
        });
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

                    //model error
                    if(m.getWidth() < 0 || m.getHeight() < 0){
                        g2.setColor(Color.RED);
                        g2.setStroke(new BasicStroke(3));
                        g2.drawLine(0, 0, jPanelView.getWidth(), jPanelView.getHeight());
                        g2.drawLine(jPanelView.getWidth(), 0, 0, jPanelView.getHeight());
                    }

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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Gate", "Input", "Output", "Wiring", "Memory", "Aritmetic", "Modules" }));
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
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
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
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
        List<WorkSpaceObject> list = new ArrayList<>();
        WorkSpaceObject obj = this.selectedObject.cloneObject();
        if (obj != null) {
            obj.getPosition().x = LogicSimulatorCore.OBJECT_NULL_POSITION;
            list.add(obj);
            //add selected component to workspace
            ProjectFile pf = this.project.getSelectedFile();
            if (pf instanceof WorkSpace) {
                ((WorkSpace) pf).addNewObjects(list);
            }
            dispose();
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
            Point m1 = getBestMatch(ComponentChooser.GATE, this.jTextFieldFind.getText());
            Point m2 = getBestMatch(ComponentChooser.INPUT, this.jTextFieldFind.getText());
            Point m3 = getBestMatch(ComponentChooser.OUTPUT, this.jTextFieldFind.getText());
            Point m4 = getBestMatch(ComponentChooser.WIRING, this.jTextFieldFind.getText());
            Point m5 = getBestMatch(ComponentChooser.MEMORY, this.jTextFieldFind.getText());
            Point m6 = getBestMatch(ComponentChooser.ARITMETIC, this.jTextFieldFind.getText());
            Point m7 = getBestMatch(ComponentChooser.MODULES, this.jTextFieldFind.getText());
            int[] dat = new int[]{m1.y, m2.y, m3.y, m4.y, m5.y, m6.y, m7.y};
            int[] indexList = new int[]{m1.x, m2.x, m3.x, m4.x, m5.x, m6.x, m7.x};
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
        if (list == null) {
            return new Point(0, -1);
        }
        find = find.toLowerCase();
        Point ret = new Point(0, 0);
        for (int i = 0; i < list.length; i++) {
            String listItem = list[i].toLowerCase();
            int match = 0;
            for (int j = 0; match < find.length() && j < list[i].length(); j++) {
                if (find.charAt(match) == listItem.charAt(j)) {
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
        this.selectedObject = selectComponent(item);
        if (this.selectedObject == null) {
            this.jPanelView.repaint();
            this.jLabelType.setText("Type:");
            this.jLabelName.setText("Name:");
            this.jLabelSize.setText("Size:");
        } else {
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
                    } catch (URISyntaxException ex) {
                    }
                    //display html
                    this.jEditorPaneInfo.setText(total);
                } else {
                    this.jEditorPaneInfo.setText("");
                }
            } catch (IOException ex) {
            }
        }
    }

    public WorkSpaceObject selectComponent(String id) {
        switch (id) {
            //gate
            case "Buffer":
                return new Buffer(new Point(0, 0), 1);
            case "Not":
                return new Not(new Point(0, 0), 1);
            case "Or":
                return new Or(new Point(0, 0), 1, 2);
            case "Nor":
                return new Nor(new Point(0, 0), 1, 2);
            case "And":
                return new And(new Point(0, 0), 1, 2);
            case "Nand":
                return new Nand(new Point(0, 0), 1, 2);
            case "Xor":
                return new Xor(new Point(0, 0), 1, 2);
            case "Nxor":
                return new Nxor(new Point(0, 0), 1, 2);
            case "MUX":
                return new MUX(new Point(0, 0), 1, 2);
            case "DMUX":
                return new DMUX(new Point(0, 0), 1, 2);
            case "Controled buffer":
                return new ControledBuffer(new Point(0, 0), 1);
            case "Controled not":
                return new ControledNot(new Point(0, 0), 1);
            //input
            case "Button":
                return new Button(new Point(0, 0), 1);
            case "Clock":
                return new Clock(new Point(0, 0), 1);
            case "Keyboard":
                return new KeyBoard(new Point(0, 0));
            case "Random generator":
                return new RandomGenerator(new Point(0, 0), 1);
            //output
            case "Bulp":
                return new Bulp(new Point(0, 0), 1);
            case "Raster screen":
                return new RasterScreen(new Point(0, 0), 10, 10, 10);
            case "Vector screen":
                return new VectorScreen(new Point(0, 0), 240, 240);
            case "Text screen":
                return new TextScreen(new Point(0, 0));
            case "7 seg":
                return new SevenSeg(new Point(0, 0));
            //wiring
            case "Bit get":
                return new BitGet(new Point(0, 0), new boolean[1]);
            case "Bit set":
                return new BitSet(new Point(0, 0), new int[1]);
            case "Input":
                return new Input(new Point(0, 0), 1);
            case "Output":
                return new Output(new Point(0, 0), 1);
            case "Bridge":
                return new Bridge(new Point(0, 0), "A");
            case "Constant":
                return new Constant(new Point(0, 0));
            //memory
            case "ROM RAM":
                return new ROMRAM(new Point(0, 0), 8, "Mem" + Tools.randomNumber(5));
            case "RWM SAM":
                return new RWMSAM(new Point(0, 0), 8, "Mem" + Tools.randomNumber(5));
            case "RWM RAM":
                return new RWMRAM(new Point(0, 0), 8, "Mem" + Tools.randomNumber(5));
            case "Counter":
                return new Counter(new Point(0, 0), 8);
            case "RS flip flop":
                return new RSFlipFlop(new Point(0, 0));
            case "JK flip flop":
                return new JKFlipFlop(new Point(0, 0));
            case "D flip flop":
                return new DFlipFlop(new Point(0, 0));
            case "T flip flop":
                return new TFlipFlop(new Point(0, 0));
            case "Register":
                return new Register(new Point(0, 0), 8);
            case "LIFO":
                return new LIFO(new Point(0, 0), 8);
            case "FIFO":
                return new FIFO(new Point(0, 0), 8);
            //aritmetic
            case "Magnitude comparator":
                return new MagnitudeComparator(new Point(0, 0), 1);
            case "Add":
                return new BitAdd(new Point(0, 0), 1);
            case "Sub":
                return new BitSub(new Point(0, 0), 1);
            case "Mul":
                return new BitMul(new Point(0, 0), 1);
            case "Div":
                return new BitDiv(new Point(0, 0), 1);
            //modules
            default:
                if (ComponentChooser.MODULES != null) {
                    for (int i = 0; i < ComponentChooser.MODULES.length; i++) {
                        if (ComponentChooser.MODULES[i].equals(id)) {
                            return ComponentChooser.modules.get(i).cloneObject();
                        }
                    }
                }
                break;
        }
        return null;
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
