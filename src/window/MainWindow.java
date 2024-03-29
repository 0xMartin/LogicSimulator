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

import logicSimulator.data.IOProject;
import logicSimulator.data.PropertieReader;
import logicSimulator.data.SystemClosing;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import logicSimulator.CircuitHandler;
import logicSimulator.ComputeCore;
import logicSimulator.ExceptionLogger;
import logicSimulator.LSComponent;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.Tools;
import logicSimulator.projectFile.WorkSpace;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Propertie;
import logicSimulator.ui.ButtonHQ;
import logicSimulator.ui.MenuItemHQ;
import logicSimulator.ui.SystemResources;
import logicSimulator.PFTwoSlotViewer;
import logicSimulator.projectFile.Library;
import logicSimulator.ui.Colors;
import window.components.NumberChooser;
import window.components.ProjectTreeView;
import window.components.PropertieEditor;

/**
 *
 * @author Martin
 */
public class MainWindow extends JFrame implements LSComponent {

    //main system core
    private LogicSimulatorCore core;

    //propts of this window
    private PropertieReader propt;

    //computing core
    private ComputeCore comuteCore;

    //project
    private Project project;

    //utilities
    private final ComponentChooser componentChooser;
    private final SerialConfiguration serialConfig;
    private final ColorSettings colorSettings;
    private final Tutorial tutorial;
    private final About about;
    private final KarnaughMap kMap;
    private final ObjectsHandler objectsHandler;
    private final GlobalSettings globalSettings;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {

        //init all from components
        initComponents();

        //maximize window
        super.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        //create instances of utils
        this.componentChooser = new ComponentChooser(this);
        this.serialConfig = new SerialConfiguration(this);
        this.colorSettings = new ColorSettings(this);
        this.tutorial = new Tutorial(this);
        this.about = new About(this);
        this.kMap = new KarnaughMap(this);
        this.objectsHandler = new ObjectsHandler(this);
        this.globalSettings = new GlobalSettings(this);

        //propertie editor event
        ((PropertieEditor) this.jTableProperties).onPropertieChange((ActionEvent evt) -> {
            if (this.project.getSelectedFile() instanceof WorkSpace) {
                WorkSpace w = (WorkSpace) this.project.getSelectedFile();
                //on propertie change reconect all object in selected workspace
                CircuitHandler.refreshConnectivity(w.getObjects());
                //rapaint
                w.getHandler().repaintPF();
            }
        });

        //value changed listener ups controler (for compute core)
        ((NumberChooser) this.ups).setValueChangedEvent((ActionEvent e) -> {
            this.comuteCore.setUPS(((NumberChooser) this.ups).getValue());
        });

        //edit mode
        this.jPanelBody.registerKeyboardAction(
                (ActionEvent e) -> {
                    editMode();
                }, KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        //contro mode
        this.jPanelBody.registerKeyboardAction(
                (ActionEvent e) -> {
                    controlMode();
                }, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelBody = new javax.swing.JPanel();
        jToolBarMain = new javax.swing.JToolBar();
        jButtonEdit = new ButtonHQ();
        jButtonControl = new ButtonHQ();
        jToolBarFile = new javax.swing.JToolBar();
        jButtonNewFile = new ButtonHQ();
        jButtonNewProject = new ButtonHQ();
        jButtonOpenProject = new ButtonHQ();
        jButtonSaveProject = new ButtonHQ();
        jToolBarEdit = new javax.swing.JToolBar();
        jButtonUndo = new ButtonHQ();
        jButtonRedo = new ButtonHQ();
        jButtonSettings = new ButtonHQ();
        jToolBarSimulating = new javax.swing.JToolBar();
        jButtonRun = new ButtonHQ();
        jButtonStop = new ButtonHQ();
        jButtonStep = new ButtonHQ();
        ups = new NumberChooser(" UPS",100, 10, 1, 1000000);
        jToolBarOther = new javax.swing.JToolBar();
        jButtonHelp = new ButtonHQ();
        jToolBarComponents = new javax.swing.JToolBar();
        jSplitPaneBody = new javax.swing.JSplitPane();
        jSplitPaneLeft = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeProject = new ProjectTreeView();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableProperties = new PropertieEditor();
        pfdockingPanel = new PFTwoSlotViewer();
        jToolBar1 = new javax.swing.JToolBar();
        jLabelSimulation = new javax.swing.JLabel();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNewFileProject = new MenuItemHQ();
        jMenuItemNewFile = new MenuItemHQ();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemOpenProject = new MenuItemHQ();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSaveProject = new MenuItemHQ();
        jMenuItemSaveProjectAs = new MenuItemHQ();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExport1 = new MenuItemHQ();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemEndo = new MenuItemHQ();
        jMenuItemRedo = new MenuItemHQ();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFindText = new MenuItemHQ();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFindObject = new MenuItemHQ();
        jMenuItemPlaceComponent = new MenuItemHQ();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemRename = new MenuItemHQ();
        jMenuItemMove = new MenuItemHQ();
        jMenuItemAddLibrary = new MenuItemHQ();
        jMenuItemSettings1 = new MenuItemHQ();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemRun = new MenuItemHQ();
        jMenuItemStop = new MenuItemHQ();
        jMenuItemStep = new MenuItemHQ();
        jCheckBoxMenuItemShowUpdates = new javax.swing.JCheckBoxMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemSettingsGlobal = new MenuItemHQ();
        jMenuItemColors = new MenuItemHQ();
        jMenuItemCheckForUpdates = new MenuItemHQ();
        jMenu4 = new javax.swing.JMenu();
        jMenuItemPrint = new MenuItemHQ();
        jMenuItemPrintScreen = new MenuItemHQ();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPrint1 = new MenuItemHQ();
        jMenuItemPrint2 = new MenuItemHQ();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItemTutorials = new MenuItemHQ();
        jMenuItemAbout = new MenuItemHQ();
        jMenuItemReportBugs = new MenuItemHQ();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HL simulator");
        setIconImage(SystemResources.ICON);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jToolBarMain.setBorder(null);
        jToolBarMain.setFloatable(false);

        jButtonEdit.setIcon(SystemResources.TOOLBAR_CURCOR);
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

        jButtonControl.setIcon(SystemResources.TOOLBAR_CLICKER);
        jButtonControl.setToolTipText("Control");
        jButtonControl.setBorder(null);
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

        jToolBarFile.setRollover(true);
        jToolBarFile.setName("File"); // NOI18N

        jButtonNewFile.setIcon(SystemResources.TOOLBAR_NEWFILE);
        jButtonNewFile.setToolTipText("New file");
        jButtonNewFile.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jButtonNewFile.setFocusable(false);
        jButtonNewFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewFile.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonNewFile.setOpaque(false);
        jButtonNewFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewFileActionPerformed(evt);
            }
        });
        jToolBarFile.add(jButtonNewFile);

        jButtonNewProject.setIcon(SystemResources.TOOLBAR_NEWPROJECT);
        jButtonNewProject.setToolTipText("New project");
        jButtonNewProject.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jButtonNewProject.setFocusable(false);
        jButtonNewProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewProject.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonNewProject.setOpaque(false);
        jButtonNewProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNewProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewProjectActionPerformed(evt);
            }
        });
        jToolBarFile.add(jButtonNewProject);

        jButtonOpenProject.setIcon(SystemResources.TOOLBAR_OPENPROJECT);
        jButtonOpenProject.setToolTipText("Open project");
        jButtonOpenProject.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jButtonOpenProject.setFocusable(false);
        jButtonOpenProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpenProject.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonOpenProject.setOpaque(false);
        jButtonOpenProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOpenProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenProjectActionPerformed(evt);
            }
        });
        jToolBarFile.add(jButtonOpenProject);

        jButtonSaveProject.setIcon(SystemResources.TOOLBAR_SAVE);
        jButtonSaveProject.setToolTipText("Save");
        jButtonSaveProject.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jButtonSaveProject.setFocusable(false);
        jButtonSaveProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSaveProject.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonSaveProject.setOpaque(false);
        jButtonSaveProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveProjectActionPerformed(evt);
            }
        });
        jToolBarFile.add(jButtonSaveProject);

        jToolBarMain.add(jToolBarFile);

        jToolBarEdit.setRollover(true);
        jToolBarEdit.setName("Edit"); // NOI18N

        jButtonUndo.setIcon(SystemResources.TOOLBAR_UNDO);
        jButtonUndo.setToolTipText("Edit");
        jButtonUndo.setFocusable(false);
        jButtonUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonUndo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonUndo.setOpaque(false);
        jButtonUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUndoActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonUndo);

        jButtonRedo.setIcon(SystemResources.TOOLBAR_REDO);
        jButtonRedo.setToolTipText("Redo");
        jButtonRedo.setFocusable(false);
        jButtonRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRedo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonRedo.setOpaque(false);
        jButtonRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRedoActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonRedo);

        jButtonSettings.setIcon(SystemResources.TOOLBAR_SETTINGS);
        jButtonSettings.setToolTipText("Settings");
        jButtonSettings.setFocusable(false);
        jButtonSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSettings.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonSettings.setOpaque(false);
        jButtonSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSettingsActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonSettings);

        jToolBarMain.add(jToolBarEdit);

        jToolBarSimulating.setRollover(true);
        jToolBarSimulating.setName("Simulating"); // NOI18N

        jButtonRun.setIcon(SystemResources.TOOLBAR_RUN);
        jButtonRun.setToolTipText("Start");
        jButtonRun.setFocusable(false);
        jButtonRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRun.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonRun.setOpaque(false);
        jButtonRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunActionPerformed(evt);
            }
        });
        jToolBarSimulating.add(jButtonRun);

        jButtonStop.setIcon(SystemResources.TOOLBAR_STOP);
        jButtonStop.setToolTipText("Stop");
        jButtonStop.setEnabled(false);
        jButtonStop.setFocusable(false);
        jButtonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStop.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonStop.setOpaque(false);
        jButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });
        jToolBarSimulating.add(jButtonStop);

        jButtonStep.setIcon(SystemResources.TOOLBAR_STEP);
        jButtonStep.setToolTipText("Step");
        jButtonStep.setFocusable(false);
        jButtonStep.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStep.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonStep.setOpaque(false);
        jButtonStep.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepActionPerformed(evt);
            }
        });
        jToolBarSimulating.add(jButtonStep);

        ups.setForeground(new java.awt.Color(0, 0, 0));
        ups.setText("UPS");
        ups.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ups.setMaximumSize(new java.awt.Dimension(75, 60));
        ups.setMinimumSize(new java.awt.Dimension(75, 0));
        ups.setPreferredSize(new java.awt.Dimension(25, 20));
        jToolBarSimulating.add(ups);

        jToolBarMain.add(jToolBarSimulating);

        jToolBarOther.setRollover(true);
        jToolBarOther.setName("Other"); // NOI18N

        jButtonHelp.setIcon(SystemResources.TOOLBAR_HELP);
        jButtonHelp.setToolTipText("Start");
        jButtonHelp.setFocusable(false);
        jButtonHelp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonHelp.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonHelp.setOpaque(false);
        jButtonHelp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });
        jToolBarOther.add(jButtonHelp);

        jToolBarMain.add(jToolBarOther);

        jToolBarComponents.setRollover(true);
        jToolBarComponents.setName("Components"); // NOI18N
        jToolBarMain.add(jToolBarComponents);

        jSplitPaneBody.setBorder(null);
        jSplitPaneBody.setDividerSize(5);

        jSplitPaneLeft.setBorder(null);
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
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
        );

        jSplitPaneLeft.setRightComponent(jPanel1);

        jSplitPaneBody.setLeftComponent(jSplitPaneLeft);

        javax.swing.GroupLayout pfdockingPanelLayout = new javax.swing.GroupLayout(pfdockingPanel);
        pfdockingPanel.setLayout(pfdockingPanelLayout);
        pfdockingPanelLayout.setHorizontalGroup(
            pfdockingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 958, Short.MAX_VALUE)
        );
        pfdockingPanelLayout.setVerticalGroup(
            pfdockingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        jSplitPaneBody.setRightComponent(pfdockingPanel);
        pfdockingPanel.setLayout(new GridLayout());

        javax.swing.GroupLayout jPanelBodyLayout = new javax.swing.GroupLayout(jPanelBody);
        jPanelBody.setLayout(jPanelBodyLayout);
        jPanelBodyLayout.setHorizontalGroup(
            jPanelBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBarMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPaneBody)
        );
        jPanelBodyLayout.setVerticalGroup(
            jPanelBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBodyLayout.createSequentialGroup()
                .addComponent(jToolBarMain, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSplitPaneBody, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
        );

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setFloatable(false);

        jLabelSimulation.setText(" UPS:-");
        jToolBar1.add(jLabelSimulation);

        jMenuFile.setText("File");

        jMenuItemNewFileProject.setIcon(SystemResources.TOOLBAR_NEWPROJECT);
        jMenuItemNewFileProject.setText("New project");
        jMenuItemNewFileProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewFileProjectActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNewFileProject);

        jMenuItemNewFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNewFile.setIcon(SystemResources.TOOLBAR_NEWFILE);
        jMenuItemNewFile.setText("New file");
        jMenuItemNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewFileActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNewFile);
        jMenuFile.add(jSeparator5);

        jMenuItemOpenProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpenProject.setIcon(SystemResources.TOOLBAR_OPENPROJECT);
        jMenuItemOpenProject.setText("Open project");
        jMenuItemOpenProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenProjectActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpenProject);
        jMenuFile.add(jSeparator3);

        jMenuItemSaveProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSaveProject.setIcon(SystemResources.TOOLBAR_SAVE);
        jMenuItemSaveProject.setText("Save project");
        jMenuItemSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveProjectActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveProject);

        jMenuItemSaveProjectAs.setIcon(SystemResources.TOOLBAR_SAVE);
        jMenuItemSaveProjectAs.setText("Save project as...");
        jMenuItemSaveProjectAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveProjectAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveProjectAs);
        jMenuFile.add(jSeparator4);

        jMenuItemExport1.setText("Export as library");
        jMenuItemExport1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExport1ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExport1);

        jMenuBarMain.add(jMenuFile);

        jMenuEdit.setText("Edit");

        jMenuItemEndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemEndo.setIcon(SystemResources.TOOLBAR_UNDO);
        jMenuItemEndo.setText("Endo");
        jMenuItemEndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEndoActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemEndo);

        jMenuItemRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemRedo.setIcon(SystemResources.TOOLBAR_REDO);
        jMenuItemRedo.setText("Redo");
        jMenuItemRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRedoActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemRedo);
        jMenuEdit.add(jSeparator7);

        jMenuItemFindText.setText("Find text");
        jMenuItemFindText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFindTextActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemFindText);
        jMenuEdit.add(jSeparator6);

        jMenuItemFindObject.setText("Find object");
        jMenuItemFindObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFindObjectActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemFindObject);

        jMenuItemPlaceComponent.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemPlaceComponent.setText("Component chooser");
        jMenuItemPlaceComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPlaceComponentActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemPlaceComponent);

        jMenuBarMain.add(jMenuEdit);

        jMenu1.setText("Project");

        jMenuItemRename.setText("Rename");
        jMenuItemRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRenameActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemRename);

        jMenuItemMove.setText("Move");
        jMenuItemMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMoveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemMove);

        jMenuItemAddLibrary.setText("Add library");
        jMenuItemAddLibrary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddLibraryActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemAddLibrary);

        jMenuItemSettings1.setIcon(SystemResources.TOOLBAR_SETTINGS);
        jMenuItemSettings1.setText("Serial port configuration");
        jMenuItemSettings1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSettings1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSettings1);

        jMenuItem2.setText("Add image resource");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBarMain.add(jMenu1);

        jMenu2.setText("Simulation");

        jMenuItemRun.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItemRun.setIcon(SystemResources.TOOLBAR_RUN);
        jMenuItemRun.setText("Run");
        jMenuItemRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRunActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemRun);

        jMenuItemStop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        jMenuItemStop.setIcon(SystemResources.TOOLBAR_STOP);
        jMenuItemStop.setText("Stop");
        jMenuItemStop.setEnabled(false);
        jMenuItemStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStopActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemStop);

        jMenuItemStep.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        jMenuItemStep.setIcon(SystemResources.TOOLBAR_STEP);
        jMenuItemStep.setText("Step");
        jMenuItemStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStepActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemStep);

        jCheckBoxMenuItemShowUpdates.setText("Show updates");
        jCheckBoxMenuItemShowUpdates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemShowUpdatesActionPerformed(evt);
            }
        });
        jMenu2.add(jCheckBoxMenuItemShowUpdates);

        jMenuBarMain.add(jMenu2);

        jMenu3.setText("Global");

        jMenuItemSettingsGlobal.setIcon(SystemResources.TOOLBAR_SETTINGS);
        jMenuItemSettingsGlobal.setText("Settings");
        jMenuItemSettingsGlobal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSettingsGlobalActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemSettingsGlobal);

        jMenuItemColors.setText("Colors");
        jMenuItemColors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemColorsActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemColors);

        jMenuItemCheckForUpdates.setText("Check for updates");
        jMenu3.add(jMenuItemCheckForUpdates);

        jMenuBarMain.add(jMenu3);

        jMenu4.setText("Tools");

        jMenuItemPrint.setText("Print");
        jMenu4.add(jMenuItemPrint);

        jMenuItemPrintScreen.setText("PrintScreen");
        jMenu4.add(jMenuItemPrintScreen);
        jMenu4.add(jSeparator8);

        jMenuItemPrint1.setText("IO  analysis");
        jMenu4.add(jMenuItemPrint1);

        jMenuItemPrint2.setText("Objects handler");
        jMenuItemPrint2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPrint2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemPrint2);

        jMenu5.setText("Utilities");

        jMenuItem1.setText("Grapher");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem1);

        jMenuItem3.setText("Karnaugh map");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem3);

        jMenu4.add(jMenu5);

        jMenuBarMain.add(jMenu4);

        jMenu6.setText("Help");

        jMenuItemTutorials.setText("Tutorials");
        jMenuItemTutorials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTutorialsActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemTutorials);

        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemAbout);

        jMenuItemReportBugs.setText("Report bugs");
        jMenu6.add(jMenuItemReportBugs);

        jMenuBarMain.add(jMenu6);

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
        this.jSplitPaneLeft.setDividerLocation(0.6f);

        //add all opened project
        this.project.getProjectFiles().stream().forEach((pf) -> {
            if (pf.getPFMode().OPENED) {
                ((PFTwoSlotViewer) this.pfdockingPanel).displayProjectFile(pf);
            }
        });

        //display visible project file (select in tab)
        this.project.getProjectFiles().stream().forEach((pf) -> {
            if (pf.getPFMode().VISIBLE) {
                ((PFTwoSlotViewer) this.pfdockingPanel).displayProjectFile(pf);
            }
        });
        
        if (this.project.getProjectFiles().size() == 1) {
            ((PFTwoSlotViewer) this.pfdockingPanel).displayProjectFile(this.project.getProjectFiles().get(0));
        }

        //refresh layout of tabbed panels
        ((PFTwoSlotViewer) this.pfdockingPanel).refreshLayout();
    }//GEN-LAST:event_formWindowOpened

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        editMode();
    }//GEN-LAST:event_jButtonEditActionPerformed
    
    private void editMode() {
        this.project.editMode = true;
        this.jButtonEdit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.jButtonControl.setBorder(null);
        //change cursor
        this.project.getProjectFiles().stream().forEach((w) -> {
            w.getComp().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        });
    }

    private void jButtonControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonControlActionPerformed
        controlMode();
    }//GEN-LAST:event_jButtonControlActionPerformed
    
    private void controlMode() {
        this.project.editMode = false;
        this.jButtonControl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.jButtonEdit.setBorder(null);
        if (this.project.getSelectedFile() instanceof WorkSpace) {
            ((WorkSpace) this.project.getSelectedFile()).unselectAllObjects(null);
        }
        //change cursor
        this.project.getProjectFiles().stream().forEach((w) -> {
            w.getComp().setCursor(new Cursor(Cursor.HAND_CURSOR));
        });
    }

    private void jMenuItemOpenProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenProjectActionPerformed
        openProjectWizzard(ProjectWizard.Mode.OPEN);
    }//GEN-LAST:event_jMenuItemOpenProjectActionPerformed

    private void jMenuItemPlaceComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPlaceComponentActionPerformed
        if (this.project.editMode) {
            //show component chooser
            ProjectFile pf = this.project.getSelectedFile();
            if (pf instanceof WorkSpace) {
                this.componentChooser.chooseComponent((WorkSpace) pf);
                ((WorkSpace) pf).unselectAllObjects(null);
            }
        }
    }//GEN-LAST:event_jMenuItemPlaceComponentActionPerformed

    private void jMenuItemSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveProjectActionPerformed
        //save project
        saveProject(this.project.getFile());
    }//GEN-LAST:event_jMenuItemSaveProjectActionPerformed

    private void jMenuItemNewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewFileActionPerformed
        (new NewFile(this, this.project)).setVisible(true);
    }//GEN-LAST:event_jMenuItemNewFileActionPerformed

    private void jButtonNewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewFileActionPerformed
        (new NewFile(this, this.project)).setVisible(true);
    }//GEN-LAST:event_jButtonNewFileActionPerformed

    private void jButtonNewProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewProjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonNewProjectActionPerformed

    private void jButtonOpenProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenProjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonOpenProjectActionPerformed

    private void jButtonUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUndoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonUndoActionPerformed

    private void jButtonRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRedoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonRedoActionPerformed

    private void jButtonSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSettingsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonSettingsActionPerformed

    private void jButtonSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveProjectActionPerformed
        //save project
        saveProject(this.project.getFile());
    }//GEN-LAST:event_jButtonSaveProjectActionPerformed

    private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunActionPerformed
        runSimulation();
    }//GEN-LAST:event_jButtonRunActionPerformed

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
        stopSimulation();
    }//GEN-LAST:event_jButtonStopActionPerformed

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonHelpActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //save project ?
        int opt = JOptionPane.showConfirmDialog(this, "Do you want to save this projects?",
                "Save project", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (opt == JOptionPane.YES_OPTION) {
            saveProject(this.project.getFile());
        }

        //close system
        SystemClosing sc = new SystemClosing(this.core);
        sc.saveProperties();
        
        sc.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepActionPerformed
        //circuit simulating step by step
        ProjectFile pf = this.project.getSelectedFile();
        this.comuteCore.setWorkSpace((WorkSpace) pf);
        this.comuteCore.step();
    }//GEN-LAST:event_jButtonStepActionPerformed

    private void jMenuItemNewFileProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewFileProjectActionPerformed
        openProjectWizzard(ProjectWizard.Mode.NEW);
    }//GEN-LAST:event_jMenuItemNewFileProjectActionPerformed

    private void jMenuItemEndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEndoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemEndoActionPerformed

    private void jMenuItemRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRedoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemRedoActionPerformed

    private void jMenuItemFindTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFindTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemFindTextActionPerformed

    private void jMenuItemFindObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFindObjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemFindObjectActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        this.about.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemSaveProjectAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveProjectAsActionPerformed
        //save opened project as
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(this.project.getName() + "." + LogicSimulatorCore.PROJECT_FILE_TYPE));
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getPath().endsWith(LogicSimulatorCore.PROJECT_FILE_TYPE) || f.isDirectory();
            }
            
            @Override
            public String getDescription() {
                return "High low simulator project";
            }
        });
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            /**
             * Get path of new location of project and if project file doesnt
             * end with its agree postfix then add it on the end of path
             */
            String path = chooser.getSelectedFile().getPath();
            path += path.endsWith(LogicSimulatorCore.PROJECT_FILE_TYPE) ? "" : '.'
                    + LogicSimulatorCore.PROJECT_FILE_TYPE;
            //save project
            this.saveProject(new File(path));
        }
    }//GEN-LAST:event_jMenuItemSaveProjectAsActionPerformed

    private void jMenuItemStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStepActionPerformed
        //simulate one step
        this.comuteCore.step();
    }//GEN-LAST:event_jMenuItemStepActionPerformed

    private void jMenuItemStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStopActionPerformed
        //stop simulation
        stopSimulation();
    }//GEN-LAST:event_jMenuItemStopActionPerformed

    private void jMenuItemRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRunActionPerformed
        //run simulation
        runSimulation();
    }//GEN-LAST:event_jMenuItemRunActionPerformed

    private void jCheckBoxMenuItemShowUpdatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemShowUpdatesActionPerformed
        if (this.jCheckBoxMenuItemShowUpdates.isSelected()) {
            //add ups display to the toolbar
            JLabel l = new JLabel("0");
            l.setMinimumSize(new Dimension(60, 0));
            l.setMaximumSize(new Dimension(60, 60));
            l.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
            l.setOpaque(true);
            l.setFont(l.getFont().deriveFont(12f));
            l.setForeground(Color.white);
            l.setName("updates");
            this.jToolBarSimulating.add(l);
        } else {
            //remove ups from tool bar
            for (Component c : this.jToolBarSimulating.getComponents()) {
                if (c.getName().equals("updates")) {
                    this.jToolBarSimulating.remove(c);
                    break;
                }
            }
        }
    }//GEN-LAST:event_jCheckBoxMenuItemShowUpdatesActionPerformed

    private void jMenuItemSettings1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettings1ActionPerformed
        this.serialConfig.showComponent();
    }//GEN-LAST:event_jMenuItemSettings1ActionPerformed

    private void jMenuItemColorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemColorsActionPerformed
        this.colorSettings.showComponent();
    }//GEN-LAST:event_jMenuItemColorsActionPerformed

    private void jMenuItemRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRenameActionPerformed
        JTextField newName = new JTextField(this.project.getName());
        int opt = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Enter new name of current project:", newName},
                "Rename project",
                JOptionPane.YES_NO_OPTION
        );
        
        if (opt == JOptionPane.YES_OPTION) {
            //change name of project
            this.project.setName(newName.getText());

            //update tree view of this project
            ((ProjectTreeView) this.jTreeProject).updateProjectTree();
        }
    }//GEN-LAST:event_jMenuItemRenameActionPerformed

    private void jMenuItemMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMoveActionPerformed
        //save opened project as
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(this.project.getName() + "." + LogicSimulatorCore.PROJECT_FILE_TYPE));
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getPath().endsWith(LogicSimulatorCore.PROJECT_FILE_TYPE) || f.isDirectory();
            }
            
            @Override
            public String getDescription() {
                return "High low simulator project";
            }
        });
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            /**
             * Get path of new location of project and if project file doesnt
             * end with its agree postfix then add it on the end of path
             */
            String path = chooser.getSelectedFile().getPath();
            path += path.endsWith(LogicSimulatorCore.PROJECT_FILE_TYPE) ? "" : '.'
                    + LogicSimulatorCore.PROJECT_FILE_TYPE;

            //delete current project
            IOProject pIO = new IOProject(this.project);
            pIO.deleteAll();

            //move project
            try {
                IOProject io = new IOProject(this.project);
                io.save(new File(path));
                JOptionPane.showMessageDialog(this, "Project successfully moved", "Project",
                        JOptionPane.INFORMATION_MESSAGE, null);
            } catch (Exception ex) {
                ExceptionLogger.getInstance().logException(ex);
                JOptionPane.showMessageDialog(this, "Project could not be moved", "Error",
                        JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }//GEN-LAST:event_jMenuItemMoveActionPerformed

    private void jMenuItemExport1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExport1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getPath().endsWith("." + LogicSimulatorCore.LIBRARY) || f.isDirectory();
            }
            
            @Override
            public String getDescription() {
                return "Circuit library";
            }
        });

        //show save dialog
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            IOProject pIO = new IOProject(this.project);
            try {
                //get description from user about library
                JTextField author = new JTextField();
                JTextArea area = new JTextArea(30, 50);
                JOptionPane.showMessageDialog(this, new Object[]{"Author:", author,
                    "Description: ", new JScrollPane(area)},
                        "Library description", JOptionPane.QUESTION_MESSAGE);

                //export library
                pIO.exportAsLibrary(chooser.getSelectedFile(),
                        "Author: " + author.getText() + "\nDate: "
                        + LogicSimulatorCore.getDate("dd. MM. yyyy") + "\n" + area.getText());
                JOptionPane.showMessageDialog(this, "Project successfully exported",
                        "Project", JOptionPane.INFORMATION_MESSAGE, null);
            } catch (IOException ex) {
                ExceptionLogger.getInstance().logException(ex);
            }
        }
    }//GEN-LAST:event_jMenuItemExport1ActionPerformed

    private void jMenuItemAddLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddLibraryActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getPath().endsWith("." + LogicSimulatorCore.LIBRARY) || f.isDirectory();
            }
            
            @Override
            public String getDescription() {
                return "Circuit library";
            }
        });

        //show save dialog
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            //add selected library to current project
            Library lib = new Library(Tools.fileName(chooser.getSelectedFile().getName()), this.project);
            this.project.getProjectFiles().add(lib);

            //move lib to the project directory
            try {
                Files.copy(chooser.getSelectedFile().toPath(),
                        new File(this.project.getFile().getAbsoluteFile().getParent()
                                + "/" + chooser.getSelectedFile().getName()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ExceptionLogger.getInstance().logException(ex);
            }

            //load lib
            try {
                lib.loadLib();
            } catch (Exception ex) {
                ExceptionLogger.getInstance().logException(ex);
            }

            //refresh tree view
            ((ProjectTreeView) this.jTreeProject).updateProjectTree();
        }
    }//GEN-LAST:event_jMenuItemAddLibraryActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        ProjectFile pf = this.project.getSelectedFile();
        
        if (pf instanceof WorkSpace) {
            Grapher grapher = new Grapher((WorkSpace) pf, this.comuteCore);
            grapher.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "You can not open grapher for this file",
                    "Grapher", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        File imgDir = new File(this.project.getFile().getAbsoluteFile().getParentFile() + "/img");

        //create dir with images
        if (!imgDir.exists()) {
            imgDir.mkdir();
        }

        //choose and insert copy of selected image to the project resource directory
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(this.project.getName() + "." + LogicSimulatorCore.PROJECT_FILE_TYPE));
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getPath().endsWith(".png") || f.getPath().endsWith(".jpg") || f.isDirectory();
            }
            
            @Override
            public String getDescription() {
                return "Image";
            }
        });
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] imgs = imgDir.listFiles();
            int index = 0;
            for (File f : imgs) {
                try {
                    String str1 = f.getName().split("_")[1];
                    index = Math.max(index, Integer.parseInt(str1.split("\\.")[0]));
                } catch (NumberFormatException ex) {
                }
            }

            //copy img to project img res dir
            try {
                Files.copy(chooser.getSelectedFile().toPath(),
                        new File(imgDir.toString() + "/img_" + (index + 1) + "."
                                + Tools.fileType(chooser.getSelectedFile().toString())).toPath());
            } catch (IOException ex) {
                ExceptionLogger.getInstance().logException(ex);
            }

            //reload img res
            try {
                SystemResources.reloadImageResources(this.project);
            } catch (IOException ex) {
                ExceptionLogger.getInstance().logException(ex);
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItemTutorialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTutorialsActionPerformed
        this.tutorial.setVisible(true);
    }//GEN-LAST:event_jMenuItemTutorialsActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        this.kMap.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItemPrint2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPrint2ActionPerformed
        this.objectsHandler.setVisible(true);
    }//GEN-LAST:event_jMenuItemPrint2ActionPerformed

    private void jMenuItemSettingsGlobalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettingsGlobalActionPerformed
        this.globalSettings.setVisible(true);
    }//GEN-LAST:event_jMenuItemSettingsGlobalActionPerformed

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
        this.propt = propt;

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
        ((ProjectTreeView) this.jTreeProject).init(this, this.project);

        //compute core 1 sec event
        this.comuteCore.set1SecListener((ActionEvent e) -> {
            String[] data = e.getActionCommand().split(";");
            for (String d : data) {
                String[] arr = d.split("=");
                switch (arr[0]) {
                    case "UPS":
                        //show updates per second and name of simulate circuit
                        if (this.comuteCore.getWorkspace() != null) {
                            this.jLabelSimulation.setText(" UPS: " + arr[1]
                                    + " - circuit: [" + this.comuteCore.getWorkspace().getName() + "]");
                        }
                        break;
                    case "UPDATES":
                        //show how many object update
                        if (this.jCheckBoxMenuItemShowUpdates.isSelected()) {
                            //selected project file be workspace
                            if (this.project.getSelectedFile() instanceof WorkSpace) {
                                WorkSpace w = (WorkSpace) this.project.getSelectedFile();
                                //find label updates
                                for (Component c : this.jToolBarSimulating.getComponents()) {
                                    if (c == null) {
                                        continue;
                                    }
                                    if (c.getName() == null) {
                                        continue;
                                    }
                                    if (!c.getName().equals("updates")) {
                                        continue;
                                    }
                                    //get background color for label
                                    int val = Integer.parseInt(arr[1]);
                                    float hue = 0.85f * (float) val / (w.getObjects().size()
                                            * this.comuteCore.getCTL().getTicksPerSecond());
                                    hue = hue > 0.85f ? 0.85f : hue;
                                    Color bg = Color.getHSBColor(hue, 0.7f, 0.7f);
                                    JLabel l = ((JLabel) c);
                                    l.setBackground(bg);
                                    String txt = " " + (val > 999 ? String.format("%.1f", (float) val / 1000f) + "k" : val);
                                    l.setText(txt);
                                    break;
                                }
                            }
                        }
                        break;
                }
            }
        });

        //compute core workspace closed during running simulation
        this.comuteCore.setWorkSpaceClosedListener((ActionEvent e) -> {
            this.stopSimulation();
        });

        //init utils
        this.componentChooser.setProject(this.project);
        this.serialConfig.init(core);
        this.kMap.setProject(this.project);
        this.objectsHandler.setProject(this.project);

        //properties #############################################
        try {
            List<Propertie> propts = propt.readFile();
            propts.stream().forEach((p -> {
                switch (p.getName()) {
                    //color
                    case "GRID":
                        Colors.GRID = new Color(p.getValueInt());
                        break;
                    case "BACKGROUND":
                        Colors.BACKGROUND = new Color(p.getValueInt());
                        break;
                    case "ERROR":
                        Colors.ERROR = new Color(p.getValueInt());
                        break;
                    case "SELECT_RECT":
                        Colors.SELECT_RECT = new Color(p.getValueInt());
                        break;
                    case "SELECT_RECT2":
                        Colors.SELECT_RECT2 = new Color(p.getValueInt());
                        break;
                    case "OBJECT":
                        Colors.OBJECT = new Color(p.getValueInt());
                        break;
                    case "IOPIN":
                        Colors.IOPIN = new Color(p.getValueInt());
                        break;
                    case "IOPIN_BUS":
                        Colors.IOPIN_BUS = new Color(p.getValueInt());
                        break;
                    case "WIRE_1":
                        Colors.WIRE_1 = new Color(p.getValueInt());
                        break;
                    case "WIRE_0":
                        Colors.WIRE_0 = new Color(p.getValueInt());
                        break;
                    case "WIRE_BUS":
                        Colors.WIRE_BUS = new Color(p.getValueInt());
                        break;
                    case "TEXT":
                        Colors.TEXT = new Color(p.getValueInt());
                        break;
                    case "ME_DRAG":
                        Colors.ME_DRAG = new Color(p.getValueInt());
                        break;
                    case "ME_CURSORCROSS":
                        Colors.ME_CURSORCROSS = new Color(p.getValueInt());
                        break;
                    case "ME_CENTER":
                        Colors.ME_CENTER = new Color(p.getValueInt());
                        break;
                    case "GR_BACKGROUND":
                        Colors.GR_BACKGROUND = new Color(p.getValueInt());
                        break;
                    case "GR_AXES":
                        Colors.GR_AXES = new Color(p.getValueInt());
                        break;
                    case "GR_GRAPHLINE":
                        Colors.GR_GRAPHLINE = new Color(p.getValueInt());
                        break;
                }
            }));
        } catch (Exception ex) {
            ExceptionLogger.getInstance().logException(ex);
        }

        //ref components
        this.project.getRefComponents().stream().forEach((obj) -> {
            this.addComponentToToolbar(
                    obj.cloneObject()
            );
        });

        //set value for ups controler (value can be changed in compute core from propertie file)
        ((NumberChooser) this.ups).setValue(this.comuteCore.getCTL().getTicksPerSecond());
    }
    
    @Override
    public void run() {
        this.setVisible(true);
    }
    
    @Override
    public void stop() {
        dispose();
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
     * Update project file tree view
     */
    public void updateProjectView() {
        //update project file tree
        ((ProjectTreeView) this.jTreeProject).updateProjectTree();
    }

    /**
     * Rename file of opened project
     *
     * @param comp Component that you want to rename
     */
    public void renameFileOfProject(ProjectFile comp) {
        if (comp == null) {
            return;
        }

        //last name
        String lastName = comp.getComp().getName();

        //get new name from input dialog
        String name = (String) JOptionPane.showInputDialog(
                this,
                "Write new name of this file",
                "Rename",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                comp.getComp().getName()
        );
        //constraints
        if (name == null) {
            return;
        }
        if (name.length() == 0) {
            return;
        }
        //for workspace
        if (comp instanceof WorkSpace) {
            for (ProjectFile pf : this.project.getProjectFiles()) {
                if (pf.getComp() != null) {
                    if (pf.getComp().getName().equals(name)) {
                        return;
                    }
                }
            }
            //rename workspace
            ((WorkSpace) comp).setName(name);
            //update tree
            ((ProjectTreeView) this.jTreeProject).updateProjectTree();
        }

        //refresh names for all displayed components
        ((PFTwoSlotViewer) this.pfdockingPanel).refreshAllNames();

        //rename file
        IOProject io = new IOProject(this.project);
        String fileType = "";
        if (comp instanceof WorkSpace) {
            fileType = "." + LogicSimulatorCore.WORKSPACE_FILE_TYPE;
        }
        io.renameFile(lastName + fileType, comp.getComp().getName() + fileType);
    }

    /**
     * Delete file from project
     *
     * @param comp Project file
     */
    public void deleteFile(ProjectFile comp) {
        if (comp == null) {
            return;
        }
        
        int n = JOptionPane.showConfirmDialog(
                this,
                "Do you really want to delete [" + comp.getComp().getName() + "]",
                "Delete",
                JOptionPane.YES_NO_OPTION);
        
        if (n != JOptionPane.YES_OPTION) {
            return;
        }

        //remove deleted project file from tabbedPane
        ((PFTwoSlotViewer) this.pfdockingPanel).removePF(comp.getComp());

        //remove deleted project file from the project
        this.project.getProjectFiles().remove(comp);

        //update tree
        ((ProjectTreeView) this.jTreeProject).updateProjectTree();

        //delete file from disk and from project link list
        IOProject io = new IOProject(this.project);
        io.deleteFile(comp.getComp().getName() + "." + Tools.getFileType(comp));

        //only for lib
        if (comp instanceof Library) {
            //remove each modul of deleted library
            for (int i = 0; i < this.project.getProjectFiles().size(); ++i) {
                ProjectFile pf = this.project.getProjectFiles().get(i);
                if (pf.isLibFile) {
                    if (pf.libName.equals(((Library) comp).getName())) {
                        this.project.getProjectFiles().remove(i);
                        --i;
                    }
                }
            }

            //remove lib from project directory
            (new File(this.project.getFile().getAbsoluteFile().getParent() + "/"
                    + comp.getName() + "." + LogicSimulatorCore.LIBRARY)).delete();
        }
    }

    /**
     * Save current opened project
     *
     * @param location File - location of project file (linker)
     */
    private void saveProject(File location) {
        try {
            IOProject io = new IOProject(this.project);
            io.save(location);
            JOptionPane.showMessageDialog(this, "Project successfully saved", "Project", JOptionPane.INFORMATION_MESSAGE, null);
        } catch (Exception ex) {
            ExceptionLogger.getInstance().logException(ex);
            JOptionPane.showMessageDialog(this, "Project could not be saved", "Error", JOptionPane.ERROR_MESSAGE, null);
        }
    }

    /**
     * Add reference of component to toolbar
     *
     * @param obj Component (Workspace object)
     */
    public void addComponentToToolbar(WorkSpaceObject obj) {
        if (obj != null) {
            
            String componentName = Tools.getComponentName(obj);
            //commponent can be only one in toolbar
            for (Component c : this.jToolBarComponents.getComponents()) {
                if (c instanceof JButton) {
                    String toolTip = ((JButton) c).getToolTipText();
                    if (toolTip != null) {
                        if (toolTip.equals(componentName)) {
                            return;
                        }
                    }
                }
            }

            //button
            ButtonHQ b = new ButtonHQ();
            b.setToolTipText(componentName);
            //event
            b.addActionListener((ActionEvent evt) -> {
                ProjectFile pf = this.project.getSelectedFile();
                if (pf instanceof WorkSpace) {
                    if (this.project.editMode) {
                        List<WorkSpaceObject> list = new ArrayList<>();
                        obj.getPosition().x = LogicSimulatorCore.OBJECT_NULL_POSITION;
                        list.add(obj.cloneObject());
                        ((WorkSpace) pf).addNewObjects(list);
                    }
                }
            });
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == 3) {
                        //remove object from toolbar
                        jToolBarComponents.remove(b);
                        jToolBarComponents.revalidate();
                        jToolBarComponents.repaint();
                        //remove object from list
                        String objName = Tools.getComponentName(obj);
                        for (WorkSpaceObject obj2 : project.getRefComponents()) {
                            if (objName.equals(Tools.getComponentName(obj2))) {
                                project.getRefComponents().remove(obj2);
                                break;
                            }
                        }
                    }
                }
            });

            //create image of selected object and set this image for button        
            b.setIcon(new ImageIcon(Tools.createImage(obj, new Dimension(20, 20), 20, Math.PI / 4)));

            //add to toolbar
            this.jToolBarComponents.add(b);

            //add to ref list in project
            String objName = Tools.getComponentName(obj);
            if (this.project.getRefComponents().stream()
                    .allMatch((obj2) -> (!objName.equals(Tools.getComponentName(obj2))))) {
                this.project.getRefComponents().add(obj);
            }
            
            this.jToolBarComponents.revalidate();
            this.jToolBarComponents.repaint();
        }
    }

    /**
     * Get all component references that are in component toolbar
     *
     * @return List with references on component
     */
    public List<String> getRefComponents() {
        List<String> list = new ArrayList<>();
        for (Component c : this.jToolBarComponents.getComponents()) {
            if (c instanceof JButton) {
                list.add(((JButton) c).getToolTipText());
            }
        }
        return list;
    }

    /**
     * Run simulation
     */
    private void runSimulation() {
        ProjectFile pf = this.project.getSelectedFile();
        if (pf != null) {
            if (pf instanceof WorkSpace) {
                //run simulation
                this.comuteCore.setWorkSpace((WorkSpace) pf);
                this.comuteCore.startComputing();
                //buttons
                this.jButtonRun.setEnabled(false);
                this.jButtonStop.setEnabled(true);
                this.jButtonStep.setEnabled(false);
                //menu items
                this.jMenuItemRun.setEnabled(false);
                this.jMenuItemStop.setEnabled(true);
                this.jMenuItemStep.setEnabled(false);
                
                return;
            }
        }

        //show message
        JOptionPane.showMessageDialog(
                this,
                "Unable to run simulating for this file",
                "Error",
                JOptionPane.ERROR_MESSAGE,
                null
        );
    }

    /**
     * Stop simulation
     */
    private void stopSimulation() {
        this.comuteCore.stopComputing();
        //buttins
        this.jButtonRun.setEnabled(true);
        this.jButtonStop.setEnabled(false);
        this.jButtonStep.setEnabled(true);
        //menu items
        this.jMenuItemRun.setEnabled(true);
        this.jMenuItemStop.setEnabled(false);
        this.jMenuItemStep.setEnabled(true);
        //label
        this.jLabelSimulation.setText(" UPS:-");
    }

    /**
     * Return project file docking panel for two
     *
     * @return
     */
    public PFTwoSlotViewer getPFDockingPanel() {
        return (PFTwoSlotViewer) this.pfdockingPanel;
    }
    
    private void openProjectWizzard(ProjectWizard.Mode mode) {
        for (LSComponent comp : this.core.getLSComponents()) {
            if (comp instanceof ProjectWizard) {

                //get project path
                File f = this.project.getFile();

                //open some new project
                ((ProjectWizard) comp).setMode(mode);
                ((ProjectWizard) comp).setVisible(true);

                //if projects is same as before than return
                File f2 = null;
                for (LSComponent comp2 : this.core.getLSComponents()) {
                    if (comp2 instanceof Project) {
                        f2 = ((Project) comp2).getFile();
                        break;
                    }
                }
                if (f2 == null) {
                    return;
                }
                if (f.toString().equals(f2.toString())) {
                    return;
                }

                //save project
                if (JOptionPane.showConfirmDialog(this, "Do you want to save this projects?",
                        "Save project", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    saveProject(this.project.getFile());
                }

                //close this window
                this.dispose();

                //init window
                LSComponent component = new MainWindow();
                try {
                    component.init(
                            this.core,
                            this.propt
                    );
                    this.core.getLSComponents().add(component);
                    component.run();
                } catch (Exception ex) {
                    ExceptionLogger.getInstance().logException(ex);
                }
                
                break;
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonControl;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonNewFile;
    private javax.swing.JButton jButtonNewProject;
    private javax.swing.JButton jButtonOpenProject;
    private javax.swing.JButton jButtonRedo;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JButton jButtonSaveProject;
    private javax.swing.JButton jButtonSettings;
    private javax.swing.JButton jButtonStep;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JButton jButtonUndo;
    public javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowUpdates;
    private javax.swing.JLabel jLabelSimulation;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemAddLibrary;
    private javax.swing.JMenuItem jMenuItemCheckForUpdates;
    private javax.swing.JMenuItem jMenuItemColors;
    private javax.swing.JMenuItem jMenuItemEndo;
    private javax.swing.JMenuItem jMenuItemExport1;
    private javax.swing.JMenuItem jMenuItemFindObject;
    private javax.swing.JMenuItem jMenuItemFindText;
    private javax.swing.JMenuItem jMenuItemMove;
    private javax.swing.JMenuItem jMenuItemNewFile;
    private javax.swing.JMenuItem jMenuItemNewFileProject;
    private javax.swing.JMenuItem jMenuItemOpenProject;
    private javax.swing.JMenuItem jMenuItemPlaceComponent;
    private javax.swing.JMenuItem jMenuItemPrint;
    private javax.swing.JMenuItem jMenuItemPrint1;
    private javax.swing.JMenuItem jMenuItemPrint2;
    private javax.swing.JMenuItem jMenuItemPrintScreen;
    private javax.swing.JMenuItem jMenuItemRedo;
    private javax.swing.JMenuItem jMenuItemRename;
    private javax.swing.JMenuItem jMenuItemReportBugs;
    private javax.swing.JMenuItem jMenuItemRun;
    private javax.swing.JMenuItem jMenuItemSaveProject;
    private javax.swing.JMenuItem jMenuItemSaveProjectAs;
    private javax.swing.JMenuItem jMenuItemSettings1;
    private javax.swing.JMenuItem jMenuItemSettingsGlobal;
    private javax.swing.JMenuItem jMenuItemStep;
    private javax.swing.JMenuItem jMenuItemStop;
    private javax.swing.JMenuItem jMenuItemTutorials;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelBody;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JSplitPane jSplitPaneBody;
    private javax.swing.JSplitPane jSplitPaneLeft;
    private javax.swing.JTable jTableProperties;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBarComponents;
    private javax.swing.JToolBar jToolBarEdit;
    private javax.swing.JToolBar jToolBarFile;
    private javax.swing.JToolBar jToolBarMain;
    private javax.swing.JToolBar jToolBarOther;
    private javax.swing.JToolBar jToolBarSimulating;
    private javax.swing.JTree jTreeProject;
    private javax.swing.JPanel pfdockingPanel;
    private javax.swing.JLabel ups;
    // End of variables declaration//GEN-END:variables
}
