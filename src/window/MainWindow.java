/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window;

import data.IOProject;
import data.PropertieReader;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import logicSimulator.ComputeCore;
import logicSimulator.LSComponent;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Project;
import logicSimulator.WorkSpace;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Tools;
import window.components.Graph;
import window.components.ProjectTreeView;
import window.components.PropertieEditor;

/**
 *
 * @author Martin
 */
public class MainWindow extends JFrame implements LSComponent {

    //main system core
    private LogicSimulatorCore core;

    //computing core
    private ComputeCore comuteCore;

    //project
    private Project project;

    //thread
    private Thread thread;

    //utilities
    private final ComponentChooser componentChooser;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        //init all from components
        initComponents();
        //maximize window
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        //create instances of utils
        this.componentChooser = new ComponentChooser(this);
        //propertie editor event
        ((PropertieEditor) this.jTableProperties).onPropertieChange(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //on propertie change reconect all object in selected workspace
                Tools.connectAllObject(project.getSelectedWorkspace().getObjects());
                //rapaint
                project.getSelectedWorkspace().getHandler().repaint();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanelBody = new javax.swing.JPanel();
        jToolBarMain = new javax.swing.JToolBar();
        jButtonEdit = new javax.swing.JButton();
        jButtonControl = new javax.swing.JButton();
        jSplitPaneBody = new javax.swing.JSplitPane();
        jSplitPaneLeft = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeProject = new ProjectTreeView();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableProperties = new PropertieEditor();
        jSplitPaneRight = new javax.swing.JSplitPane();
        jTabbedPane = new javax.swing.JTabbedPane();
        jScrollPaneSimulation = new javax.swing.JScrollPane();
        jPanelSimulation3 = new javax.swing.JPanel();
        timingGraph = new Graph();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabelUPS = new javax.swing.JLabel();
        jSliderUPS = new javax.swing.JSlider();
        jButtonNextstep = new javax.swing.JButton();
        jButtonNextstep1 = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        jLabelZoom = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabelTiming = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        jProgressBarMemoryUsage = new javax.swing.JProgressBar();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuEdit = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HL simulator");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jToolBarMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBarMain.setFloatable(false);

        jButtonEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/src/img/def/cursor.png"))); // NOI18N
        jButtonEdit.setToolTipText("Edit");
        jButtonEdit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jButtonEdit.setContentAreaFilled(false);
        jButtonEdit.setFocusable(false);
        jButtonEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonEdit);

        jButtonControl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/src/img/def/clicker.png"))); // NOI18N
        jButtonControl.setToolTipText("Control");
        jButtonControl.setContentAreaFilled(false);
        jButtonControl.setFocusable(false);
        jButtonControl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonControl.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonControlActionPerformed(evt);
            }
        });
        jToolBarMain.add(jButtonControl);

        jSplitPaneBody.setDividerSize(5);

        jSplitPaneLeft.setDividerSize(5);
        jSplitPaneLeft.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneLeft.setMinimumSize(new java.awt.Dimension(200, 27));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Project explorer"));
        jScrollPane1.setViewportView(jTreeProject);

        jSplitPaneLeft.setTopComponent(jScrollPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Object properties"));

        jScrollPane3.setBorder(null);

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
        jScrollPane3.setViewportView(jTableProperties);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
        );

        jSplitPaneLeft.setRightComponent(jPanel1);

        jSplitPaneBody.setLeftComponent(jSplitPaneLeft);

        jSplitPaneRight.setDividerSize(5);

        jTabbedPane.setMinimumSize(new java.awt.Dimension(200, 4));
        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });
        jSplitPaneRight.setLeftComponent(jTabbedPane);

        jScrollPaneSimulation.setMinimumSize(new java.awt.Dimension(140, 7));
        jScrollPaneSimulation.setPreferredSize(new java.awt.Dimension(200, 558));

        jPanelSimulation3.setBorder(javax.swing.BorderFactory.createTitledBorder("Simulation"));
        jPanelSimulation3.setMinimumSize(new java.awt.Dimension(140, 300));
        jPanelSimulation3.setPreferredSize(new java.awt.Dimension(140, 555));

        timingGraph.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        timingGraph.setToolTipText("Graph of updates per second");

        javax.swing.GroupLayout timingGraphLayout = new javax.swing.GroupLayout(timingGraph);
        timingGraph.setLayout(timingGraphLayout);
        timingGraphLayout.setHorizontalGroup(
            timingGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        timingGraphLayout.setVerticalGroup(
            timingGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 203, Short.MAX_VALUE)
        );

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Auto run");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Manual");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jLabelUPS.setText("UPS: 50");

        jSliderUPS.setMajorTickSpacing(25);
        jSliderUPS.setMaximum(200);
        jSliderUPS.setMinimum(1);
        jSliderUPS.setPaintTicks(true);
        jSliderUPS.setToolTipText("Updates per second");
        jSliderUPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderUPSStateChanged(evt);
            }
        });
        jSliderUPS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderUPSMouseReleased(evt);
            }
        });

        jButtonNextstep.setText("Next step");
        jButtonNextstep.setEnabled(false);
        jButtonNextstep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextstepActionPerformed(evt);
            }
        });

        jButtonNextstep1.setText("Clear graph");
        jButtonNextstep1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextstep1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSimulation3Layout = new javax.swing.GroupLayout(jPanelSimulation3);
        jPanelSimulation3.setLayout(jPanelSimulation3Layout);
        jPanelSimulation3Layout.setHorizontalGroup(
            jPanelSimulation3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSimulation3Layout.createSequentialGroup()
                .addGroup(jPanelSimulation3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSimulation3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelSimulation3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSimulation3Layout.createSequentialGroup()
                                .addComponent(jLabelUPS, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSliderUPS, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                            .addGroup(jPanelSimulation3Layout.createSequentialGroup()
                                .addGroup(jPanelSimulation3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelSimulation3Layout.createSequentialGroup()
                                        .addComponent(jRadioButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton2))
                                    .addGroup(jPanelSimulation3Layout.createSequentialGroup()
                                        .addComponent(jButtonNextstep)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonNextstep1)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(timingGraph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelSimulation3Layout.setVerticalGroup(
            jPanelSimulation3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSimulation3Layout.createSequentialGroup()
                .addGroup(jPanelSimulation3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addGap(0, 0, 0)
                .addGroup(jPanelSimulation3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSliderUPS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelUPS, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanelSimulation3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNextstep)
                    .addComponent(jButtonNextstep1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timingGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(232, Short.MAX_VALUE))
        );

        jScrollPaneSimulation.setViewportView(jPanelSimulation3);

        jSplitPaneRight.setRightComponent(jScrollPaneSimulation);

        jSplitPaneBody.setRightComponent(jSplitPaneRight);

        javax.swing.GroupLayout jPanelBodyLayout = new javax.swing.GroupLayout(jPanelBody);
        jPanelBody.setLayout(jPanelBodyLayout);
        jPanelBodyLayout.setHorizontalGroup(
            jPanelBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBarMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPaneBody, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
        );
        jPanelBodyLayout.setVerticalGroup(
            jPanelBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBodyLayout.createSequentialGroup()
                .addComponent(jToolBarMain, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSplitPaneBody, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
        );

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setFloatable(false);

        jLabelZoom.setText("Zoom: 100%");
        jToolBar1.add(jLabelZoom);
        jToolBar1.add(jSeparator1);

        jLabelTiming.setText(" Timing:  50ups ");
        jToolBar1.add(jLabelTiming);
        jToolBar1.add(jSeparator2);

        jLabel2.setText(" Memory usage: ");
        jToolBar1.add(jLabel2);

        jProgressBarMemoryUsage.setMaximumSize(new java.awt.Dimension(200, 12));
        jProgressBarMemoryUsage.setStringPainted(true);
        jToolBar1.add(jProgressBarMemoryUsage);

        jMenuFile.setText("File");
        jMenuBarMain.add(jMenuFile);

        jMenuEdit.setText("Edit");
        jMenuBarMain.add(jMenuEdit);

        setJMenuBar(jMenuBarMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //windows opened
        this.jSplitPaneRight.setDividerLocation(0.8f);
        this.jSplitPaneLeft.setDividerLocation(0.6f);

        //display selected workspace
        WorkSpace w = this.project.getSelectedWorkspace();
        if (w != null) {
            this.displayWorkSpace(w);
        }
    }//GEN-LAST:event_formWindowOpened

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged
        //select current opened workspace
        this.project.setSelectedWorkspace(
                (WorkSpace) this.jTabbedPane.getSelectedComponent()
        );
    }//GEN-LAST:event_jTabbedPaneStateChanged

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        this.project.editMode = true;
        this.jButtonEdit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.jButtonControl.setBorder(null);
    }//GEN-LAST:event_jButtonEditActionPerformed

    private void jButtonControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonControlActionPerformed
        this.project.editMode = false;
        this.jButtonControl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.jButtonEdit.setBorder(null);
        this.project.getSelectedWorkspace().unselectAllObjects(null);
    }//GEN-LAST:event_jButtonControlActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        //auto run
        this.jSliderUPS.setEnabled(true);
        this.jButtonNextstep.setEnabled(false);
        this.comuteCore.startComputing();
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        //auto run
        this.jSliderUPS.setEnabled(false);
        this.jButtonNextstep.setEnabled(true);
        this.comuteCore.stopComputing();
        this.jLabelTiming.setText("Timing: - ");
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jSliderUPSStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderUPSStateChanged
        this.jLabelUPS.setText("UPS: " + this.jSliderUPS.getValue());
    }//GEN-LAST:event_jSliderUPSStateChanged

    private void jSliderUPSMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderUPSMouseReleased
        this.comuteCore.setUPS(this.jSliderUPS.getValue());
    }//GEN-LAST:event_jSliderUPSMouseReleased

    private void jButtonNextstepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextstepActionPerformed
        //compute step
        this.comuteCore.step();
    }//GEN-LAST:event_jButtonNextstepActionPerformed

    private void jButtonNextstep1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextstep1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonNextstep1ActionPerformed

    /**
     * Init this window, must exis compute core and project in Core
     *
     * @param core Logic simulator core
     * @param propt Propertie file
     * @throws Exception
     */
    @Override
    public void init(LogicSimulatorCore core, PropertieReader propt) throws Exception {
        //default
        this.core = core;

        //load compute core, project
        core.getLSComponents().forEach((obj) -> {
            if (obj instanceof ComputeCore) {
                this.comuteCore = (ComputeCore) obj;
            } else if (obj instanceof Project) {
                this.project = (Project) obj;
            }
        });
        if (this.comuteCore == null) {
            throw new Exception("MainWindow: ComputeCore not exist");
        }
        if (this.project == null) {
            throw new Exception("MainWindow: Project not exist");
        }

        //init project tree view
        ((ProjectTreeView) this.jTreeProject).init(this.core, this.project);

        //thread
        this.thread = new Thread(() -> {
            jProgressBarMemoryUsage.setMaximum(100);
            while (true) {
                Runtime r = Runtime.getRuntime();
                float memoryUsage = 1.0f - (float) r.freeMemory() / (float) r.totalMemory();
                jProgressBarMemoryUsage.setValue((int) (memoryUsage * 100f));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        });

        //compute core
        this.comuteCore.setUPSDisplay(this.jLabelTiming, (Graph) this.timingGraph);

        //init utils
        this.componentChooser.setProject(this.project);
    }

    /**
     * Edit propertie of object (obj)
     *
     * @param obj
     */
    public void editProperties(WorkSpaceObject obj) {
        ((PropertieEditor) this.jTableProperties).edit(obj);
    }

    /**
     * Add or display new workspace
     *
     * @param w Workspace
     */
    private void displayWorkSpace(WorkSpace w) {
        //find workspace in tabbedpane if it isnt inside then add
        boolean b = true;
        for (Component c : this.jTabbedPane.getComponents()) {
            if (c == w) {
                b = false;
                break;
            }
        }
        if (b) {
            //add to tabbed
            w.setBorder(null);
            this.jTabbedPane.add(w.getName(), w);
            //add lister
            w.getHandler().addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent arg0) {
                    switch (arg0.getKeyCode()) {
                        case KeyEvent.VK_W:
                            //show component chooser
                            if (arg0.isControlDown()) {
                                componentChooser.chooseComponent();
                                project.getSelectedWorkspace().unselectAllObjects(null);
                            }
                            break;
                        case KeyEvent.VK_S:
                            if (arg0.isControlDown()) {
                                saveProject();
                            }
                            break;
                    }
                }
            });
        }
        //select workspace
        this.jTabbedPane.setSelectedComponent(w);
    }

    private void saveProject() {
        try {
            IOProject io = new IOProject(this.project);
            io.save(this.project.getFile());
        } catch (Exception ex) {
            Logger.getLogger(ProjectWizard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Project could not be saved", "Error", JOptionPane.ERROR_MESSAGE, null);
        }
    }

    @Override
    public void run() {
        this.setVisible(true);
        this.thread.start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonControl;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonNextstep;
    private javax.swing.JButton jButtonNextstep1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelTiming;
    private javax.swing.JLabel jLabelUPS;
    private javax.swing.JLabel jLabelZoom;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelBody;
    private javax.swing.JPanel jPanelSimulation3;
    private javax.swing.JProgressBar jProgressBarMemoryUsage;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPaneSimulation;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSlider jSliderUPS;
    private javax.swing.JSplitPane jSplitPaneBody;
    private javax.swing.JSplitPane jSplitPaneLeft;
    private javax.swing.JSplitPane jSplitPaneRight;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableProperties;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBarMain;
    private javax.swing.JTree jTreeProject;
    private javax.swing.JPanel timingGraph;
    // End of variables declaration//GEN-END:variables
}
