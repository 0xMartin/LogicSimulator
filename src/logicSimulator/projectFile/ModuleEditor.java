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
package logicSimulator.projectFile;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import logicSimulator.CircuitHandler;
import logicSimulator.ComputeCore;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.PFHandler;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.Settings;
import logicSimulator.Tools;
import logicSimulator.data.FileIO;
import logicSimulator.objects.LogicModule;
import logicSimulator.ui.Colors;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Circle;
import logicSimulator.graphics.Line;
import logicSimulator.graphics.Curve;
import logicSimulator.graphics.GString;
import logicSimulator.graphics.GraphicsObject;

/**
 * Allows logic module model editing
 *
 * @author Martin
 */
public class ModuleEditor extends ProjectFile {

    //logic module
    private LogicModule module;

    private String changeTime = "";

    private String logicModelName;

    private final ProjectFileToolbar toolbar;

    //menu for model editor
    private JPopupMenu menu;

    //handler
    private final EditorHandler editorHandler;

    public ModuleEditor(String name, Project project, LogicModule module) {
        super(project);
        //default
        this.module = module;
        super.setName(name);
        super.setBorder(null);
        super.setLayout(new BorderLayout());

        //toolbar
        this.toolbar = new ProjectFileToolbar(this);
        super.add(this.toolbar, BorderLayout.NORTH);

        //editor handler
        this.editorHandler = new EditorHandler(this);
        super.add(this.editorHandler, BorderLayout.CENTER);

        //init menu
        initMenu();
    }

    private void initMenu() {
        //init menu
        this.menu = new JPopupMenu();
        this.editorHandler.add(this.menu);
        JMenu m;
        JMenuItem item;
        ActionListener action;

        //logic model menu
        m = new JMenu("Logic model");
        this.menu.add(m);

        //set model 
        item = new JMenuItem("Set model");
        m.add(item);
        action = (ActionEvent evt) -> {
            this.editorHandler.chooseLogicModel();
        };
        item.addActionListener(action);
        this.editorHandler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK), JComponent.WHEN_FOCUSED
        );

        //remove model 
        item = new JMenuItem("Remove model");
        m.add(item);
        action = (ActionEvent evt) -> {

        };
        item.addActionListener(action);

        //show model
        item = new JMenuItem("Show model");
        m.add(item);
        action = (ActionEvent evt) -> {

        };
        item.addActionListener(action);

        //graphics model menu
        m = new JMenu("Graphic model");
        this.menu.add(m);

        //Add line
        item = new JMenuItem("Add line");
        m.add(item);
        action = (ActionEvent evt) -> {
            addLine();
        };
        item.addActionListener(action);

        //Add circle
        item = new JMenuItem("Add circle");
        m.add(item);
        action = (ActionEvent evt) -> {
            addCircle();
        };
        item.addActionListener(action);

        //Add curve
        item = new JMenuItem("Add curve");
        m.add(item);
        action = (ActionEvent evt) -> {
            addCurve();
        };
        item.addActionListener(action);

        //Add rect
        item = new JMenuItem("Add rectangle");
        m.add(item);
        action = (ActionEvent evt) -> {
            addRect();
        };
        item.addActionListener(action);

        //Add text
        item = new JMenuItem("Add string");
        m.add(item);
        action = (ActionEvent evt) -> {
            addText();
        };
        item.addActionListener(action);

        //Edit
        m = new JMenu("Edit");
        this.menu.add(m);

        //Rotate
        item = new JMenuItem("Rotate");
        m.add(item);
        action = (ActionEvent evt) -> {

        };
        item.addActionListener(action);

        //Clear graphics model
        item = new JMenuItem("Clear graphics model");
        m.add(item);
        action = (ActionEvent evt) -> {
            module.getModel().getGraphicsObjects().clear();
            this.repaint();
        };
        item.addActionListener(action);

        //select all
        item = new JMenuItem("Select all");
        m.add(item);
        action = (ActionEvent evt) -> {
            selectAllGO();
        };
        item.addActionListener(action);

        //delete
        item = new JMenuItem("Delete");
        m.add(item);
        action = (ActionEvent evt) -> {
            //delete
            this.editorHandler.select.stream().forEach((pt) -> {
                this.editorHandler.removeGraphicsObjectFromModel(pt);
            });
            //repaint
            this.repaint();
        };
        item.addActionListener(action);
        this.editorHandler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED
        );

        //copy
        item = new JMenuItem("Copy");
        m.add(item);
        action = (ActionEvent evt) -> {
            this.editorHandler.copySelectedGraphicsObjects();
        };
        item.addActionListener(action);
        this.editorHandler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK), JComponent.WHEN_FOCUSED
        );

        //paste
        item = new JMenuItem("Paste");
        m.add(item);
        action = (ActionEvent evt) -> {
            //clear selection
            this.editorHandler.select.clear();

            //center of mass
            int ptCount = 0;
            Point.Double center = new Point.Double(0d, 0d);

            //move copy of data from copy vector to GO buffer
            for (GraphicsObject go : this.editorHandler.copy_vector) {
                //add go object
                this.editorHandler.GOBuffer.add(go.cloneObject());
                //sum positions
                for (Point.Double pt : go.cloneObject().getPoints()) {
                    center.x += pt.x;
                    center.y += pt.y;
                    ++ptCount;
                }
            }

            center.x /= ptCount;
            center.y /= ptCount;

            //center all objects to cursor
            this.editorHandler.GOBuffer.stream().forEach((go) -> {
                for (Point.Double pt : go.getPoints()) {
                    pt.x += (this.editorHandler.cursor.x - this.editorHandler.getWidth() / 2 - center.x)
                            / this.editorHandler.scale;
                    pt.y += (this.editorHandler.cursor.y - this.editorHandler.getHeight() / 2 - center.y)
                            / this.editorHandler.scale;
                }
            });
        };
        item.addActionListener(action);
        this.editorHandler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), JComponent.WHEN_FOCUSED
        );

        //change height of string font
        item = new JMenuItem("Change string font height");
        m.add(item);
        action = (ActionEvent evt) -> {

            //get all selected strings and compute avg height
            float avgHeight = 0;
            List<GString> strs = new ArrayList<>();
            for (Point.Double pt : this.editorHandler.select) {
                GraphicsObject go = this.editorHandler.getOwner(pt);
                if (go instanceof GString) {
                    strs.add((GString) go);
                    avgHeight += ((GString) go).getHeight();
                }
            }

            if (strs.isEmpty()) {
                return;
            }

            avgHeight /= strs.size();

            //set up size
            JTextField fS = new JTextField(Math.round(avgHeight) + "");
            int n = JOptionPane.showConfirmDialog(
                    this, new Object[]{"Height of string font:", fS},
                    "Change string font height",
                    JOptionPane.OK_CANCEL_OPTION);

            //change size
            if (n == JOptionPane.YES_OPTION) {
                try {
                    int height = Integer.parseInt(fS.getText());

                    //change size of each string
                    strs.stream().forEach((str) -> {
                        str.setHeight(height);
                    });
                } catch (NumberFormatException ex) {
                }
            }
        };
        item.addActionListener(action);

    }

    @Override
    public void backUpData(String projectDirectoryPath) throws Exception {
        FileIO.writeObject(
                new File(projectDirectoryPath + this.getName() + "." + LogicSimulatorCore.MODULE_FILE_TYPE),
                new Object[]{this.module, this.logicModelName}
        );
    }

    @Override
    public void restoreData(File file) throws Exception {
        //read LogicModule from file
        Object[] data = (Object[]) FileIO.readObject(file);
        this.module = (LogicModule) data[0];
        this.logicModelName = (String) data[1];
    }

    public ProjectFileToolbar getToolBar() {
        return this.toolbar;
    }

    /**
     * Set workspace for this logic module
     *
     * @param workspace
     */
    public void setWorkSpace(WorkSpace workspace) {
        //set logic model
        this.module.setLogicModel(workspace.getObjects());
        //get name of workspace (logic model)
        this.logicModelName = workspace.getName() == null ? "" : workspace.getName();
        //time of last change
        this.changeTime = LogicSimulatorCore.getDate("HH:mm:ss - dd.MM.yyyy");

        //for each child of this module set new workspace
        applyChanges();
    }

    /**
     * Get name of current logic model
     *
     * @return
     */
    public String getLogicModelName() {
        return this.logicModelName;
    }

    /**
     * Get logic module
     *
     * @return
     */
    public LogicModule getModule() {
        return this.module;
    }

    @Override
    public PFHandler getHandler() {
        return this.editorHandler;
    }

    public void selecteAllGraphicsObjects() {
        List<Point.Double> pts = Tools.getPoints(module.getModel());
        this.editorHandler.select.clear();
        pts.stream().forEach((pt -> {
            this.editorHandler.select.add(pt);
        }));
    }

    /**
     * Apply all changes for all childs of this module in project
     */
    private boolean changed = false;

    public void applyChanges() {
        Project p = super.getProject();
        if (p != null) {
            if (p.getProjectFiles() != null) {
                //find out childs of this module in each workspaces of projects
                p.getProjectFiles().stream()
                        .filter((pf) -> (pf instanceof WorkSpace))
                        .forEachOrdered((pf) -> {
                            //workspace
                            WorkSpace w = (WorkSpace) pf;
                            changed = false;
                            w.getObjects().stream()
                                    .filter((obj) -> (obj instanceof LogicModule))
                                    .forEachOrdered((obj) -> {
                                        LogicModule child = (LogicModule) obj;
                                        //child of module finded
                                        if (child.getModuleName().equals(this.module.getModuleName())) {
                                            //clone
                                            child.cloneModule(this.module);
                                            changed = true;
                                        }
                                    });
                            //if some module changed than must refresh connectivity
                            if (changed) {
                                CircuitHandler.refreshConnectivity(w.getObjects());
                            }
                        });
                //change time
                this.changeTime = LogicSimulatorCore.getDate("HH:mm:ss - dd.MM.yyyy");
                this.editorHandler.repaint();
            }
        }
    }

    /**
     * Add line to current editing model
     */
    public void addLine() {
        Point.Double p = new Point.Double(
                this.editorHandler.pressed.x - this.editorHandler.getWidth() / 2,
                this.editorHandler.pressed.y - this.editorHandler.getHeight() / 2
        );
        Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP);
        this.editorHandler.line = new Line(p, Tools.copy(p));
        this.module.getModel().getGraphicsObjects().add(this.editorHandler.line);
        this.editorHandler.repaint();
    }

    /**
     * Add circle to current editing model
     */
    public void addCircle() {
        Point.Double p = new Point.Double(
                this.editorHandler.cursor.x - this.editorHandler.getWidth() / 2,
                this.editorHandler.cursor.y - this.editorHandler.getHeight() / 2
        );
        this.editorHandler.circle = new Circle(p, LogicSimulatorCore.WORK_SPACE_STEP);
        module.getModel().getGraphicsObjects().add(this.editorHandler.circle);
        this.editorHandler.repaint();
    }

    /**
     * Add rect to current editing model
     */
    public void addRect() {
        Point.Double p = new Point.Double(
                this.editorHandler.cursor.x - this.editorHandler.getWidth() / 2,
                this.editorHandler.cursor.y - this.editorHandler.getHeight() / 2
        );
        int a = LogicSimulatorCore.WORK_SPACE_STEP * 3;
        GraphicsObject[] rect = new GraphicsObject[]{
            new Line(new Point.Double(p.x, p.y), new Point.Double(p.x, p.y + a)),
            new Line(new Point.Double(p.x, p.y), new Point.Double(p.x + a, p.y)),
            new Line(new Point.Double(p.x + a, p.y), new Point.Double(p.x + a, p.y + a)),
            new Line(new Point.Double(p.x, p.y + a), new Point.Double(p.x + a, p.y + a))
        };
        this.editorHandler.GOBuffer.addAll(Arrays.asList(rect));
    }

    /**
     * Add curve to current editing model
     */
    public void addCurve() {
        Point.Double p = new Point.Double(
                this.editorHandler.pressed.x - this.editorHandler.getWidth() / 2,
                this.editorHandler.pressed.y - this.editorHandler.getHeight() / 2
        );
        Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP);
        this.editorHandler.curve = new Curve(p, Tools.copy(p), Tools.copy(p));
        module.getModel().getGraphicsObjects().add(this.editorHandler.curve);
        this.repaint();
    }

    /**
     * Add text to current editing model
     */
    public void addText() {
        Point.Double p = new Point.Double(
                this.editorHandler.cursor.x - this.editorHandler.getWidth() / 2,
                this.editorHandler.cursor.y - this.editorHandler.getHeight() / 2
        );
        GString str = new GString(p, 20, "Ahoj");
        this.editorHandler.GOBuffer.add(str);
    }

    /**
     * Select all graphics object of current editing model
     */
    public void selectAllGO() {
        if (module != null) {
            List<Point.Double> pts = Tools.getPoints(module.getModel());
            this.editorHandler.select.clear();
            pts.stream().forEach((pt) -> {
                this.editorHandler.select.add(pt);
            });
        }
    }

    /**
     * Handler for logic module editor (rendering, model editing, linking logic
     * model, ...)
     */
    private class EditorHandler extends JPanel implements PFHandler,
            MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

        private final ModuleEditor owner;

        private float scale = 1.0f;

        /**
         * Create handler for module editor
         *
         * @param owner Owner of this handler
         */
        public EditorHandler(ModuleEditor owner) {
            this.owner = owner;
            super.addMouseListener(this);
            super.addMouseMotionListener(this);
            super.addMouseWheelListener(this);
            super.addKeyListener(this);
            super.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (module != null) {
                        module.refreshIOPins();
                    }
                }
            });

            //paint zoom value on toolbar
            this.zoom(0);
        }

        @Override
        public void zoom(int ration) {
            if (ration != 0) {
                this.scale -= 0.1f * ration;
                this.scale = this.scale < 1.0f ? 1.0f : this.scale;
                this.scale = this.scale > 3f ? 3f : this.scale;
                //repaint
                this.repaint();
            }
            //set zoom info
            this.owner.getToolBar().setRightString(String.format("%.2f", this.scale * 100) + "%");
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            //zoom out or in
            zoom(e.getWheelRotation());
        }

        /**
         * Choose logic model (workspace) for module
         */
        public void chooseLogicModel() {
            //load all workspaces from project
            List<WorkSpace> works = new ArrayList<>();
            this.owner.getProject().getProjectFiles().stream().forEach((pf) -> {
                if (pf instanceof WorkSpace) {
                    works.add((WorkSpace) pf);
                }
            });
            //no workspaces
            if (works.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "In project isnt any workspace that can be used as logic model for this module",
                        "Error", JOptionPane.ERROR_MESSAGE, null);
                return;
            }
            //create list with all names of projects
            String[] names = new String[works.size()];
            for (int i = 0; i < works.size(); i++) {
                names[i] = works.get(i).getName();
            }
            //choose workspace
            String input = (String) JOptionPane.showInputDialog(
                    this,
                    "Choose workspace as logic model for this module",
                    "Set logic model",
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    names,
                    names[0]
            );
            //set model (find in array)
            if (input != null) {
                for (int i = 0; i < works.size(); i++) {
                    if (input.equals(works.get(i).getName())) {
                        //set
                        setWorkSpace(works.get(i));
                    }
                }
            }
        }

        @Override
        public void repaintPF() {
            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponents(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.scale(this.scale, this.scale);

            //set high rendering quality (if is allowed in core)
            if (Settings.HIGH_RENDER_QUALITY) {
                Tools.setHighQuality(g2);
            }

            g2.setColor(Colors.ME_BACKGROUND);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());

            //center of screen
            int cW = (int) (this.getWidth() / 2 / this.scale);
            int cH = (int) (this.getHeight() / 2 / this.scale);

            //find start [x, y] for grid
            g2.setColor(Colors.ME_GRID);
            int startX = (int) (this.getWidth() / 2 / this.scale);
            int startY = (int) (this.getHeight() / 2 / this.scale);
            while (startX > 0) {
                startX -= LogicSimulatorCore.WORK_SPACE_STEP;
            }
            while (startY > 0) {
                startY -= LogicSimulatorCore.WORK_SPACE_STEP;
            }
            //draw grid
            g2.setColor(Colors.GRID);
            for (int x = startX; x < this.getWidth(); x += LogicSimulatorCore.WORK_SPACE_STEP) {
                for (int y = startY; y < this.getHeight(); y += LogicSimulatorCore.WORK_SPACE_STEP) {
                    g2.fillRect(x - 1, y - 1, 2, 2);
                }
            }

            //draw model of module
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Colors.ME_MODEL);
            if (module.getModel() != null) {
                module.getModel().render(g2, cW, cH);
            }

            //draw go buffer
            g2.setColor(Colors.ME_DRAG);
            this.GOBuffer.stream().forEach((go) -> {
                go.draw(g2, cW, cH);
            });
            g2.setStroke(new BasicStroke(1));

            g2.setComposite(AlphaComposite.SrcOver.derive(0.4f));

            //draw center cross
            g2.setColor(Colors.ME_CENTER);
            g2.drawLine(cW - 10, cH, cW + 10, cH);
            g2.drawLine(cW, cH - 10, cW, cH + 10);

            //draw cross cursor pointer
            if (this.cursor != null) {
                g2.setColor(Colors.ME_CURSORCROSS);
                g2.drawLine(
                        (int) (this.cursor.x / this.scale),
                        0,
                        (int) (this.cursor.x / this.scale),
                        this.getHeight()
                );
                g2.drawLine(
                        0,
                        (int) (this.cursor.y / this.scale),
                        this.getWidth(),
                        (int) (this.cursor.y / this.scale)
                );
            }

            //draw cross mouse press pointer
            if (this.pressed != null) {
                g2.setColor(Colors.ME_GRID);
                g2.drawLine(
                        (int) (this.pressed.x / this.scale),
                        (int) (this.pressed.y / this.scale - 10),
                        (int) (this.pressed.x / this.scale),
                        (int) (this.pressed.y / this.scale + 10)
                );
                g2.drawLine(
                        (int) (this.pressed.x / this.scale - 10),
                        (int) (this.pressed.y / this.scale),
                        (int) (this.pressed.x / this.scale + 10),
                        (int) (this.pressed.y / this.scale)
                );
            }

            //draw model size
            if (module != null) {
                if (module.getModel() != null) {
                    g2.setColor(Colors.ME_CENTER);
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                    for (int i = -1; i <= 1; i += 2) {
                        g2.drawLine(
                                cW + i * module.getModel().getWidth() / 2,
                                0,
                                cW + i * module.getModel().getWidth() / 2,
                                (int) (this.getHeight() / this.scale)
                        );
                        g2.drawLine(
                                0,
                                cH + i * module.getModel().getHeight() / 2,
                                (int) (this.getWidth() / this.scale),
                                cH + i * module.getModel().getHeight() / 2
                        );
                    }
                    g2.setStroke(new BasicStroke(1));
                }
            }

            g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));

            //selecte rect
            if (this.isPressed && this.line == null
                    && this.circle == null && this.curve == null && !this.movingWithPoints) {
                g2.setColor(logicSimulator.ui.Colors.SELECT_RECT2);
                g2.setStroke(new BasicStroke(1));
                g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
                int w = (int) ((this.last.x - this.pressed.x) / this.scale);
                int h = (int) ((this.last.y - this.pressed.y) / this.scale);
                g2.fillRect(
                        (int) ((this.pressed.x / this.scale + (w < 0 ? w : 0))),
                        (int) ((this.pressed.y / this.scale + (h < 0 ? h : 0))),
                        (int) (Math.abs(w)),
                        (int) (Math.abs(h))
                );
                g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));
                g2.drawRect(
                        (int) ((this.pressed.x / this.scale + (w < 0 ? w : 0))),
                        (int) ((this.pressed.y / this.scale + (h < 0 ? h : 0))),
                        (int) (Math.abs(w)),
                        (int) (Math.abs(h))
                );
            }

            //draw points of model
            if (module != null) {
                if (module.getModel() != null) {
                    List<Point.Double> pts = Tools.getPoints(module.getModel());
                    for (int i = 0; i < pts.size(); i++) {
                        Point.Double p = pts.get(i);
                        if (p != null) {
                            IOPin pin = isPointPinPosition(p);
                            if (this.select.stream().anyMatch((pt) -> (p == pt))) {
                                g2.setColor(logicSimulator.ui.Colors.SELECT_RECT);
                            } else {
                                g2.setColor(pin != null ? Color.BLUE : Color.GRAY);
                            }
                            g2.fillRect(
                                    (int) (p.x - 2 + cW),
                                    (int) (p.y - 2 + cH),
                                    4,
                                    4
                            );
                            if (pin != null) {
                                g2.setColor(Color.blue);
                                int xoff = pin.getPosition().x < 0
                                        ? -g2.getFontMetrics().stringWidth(pin.getLabel()) - 10 : 10;
                                g2.drawString(
                                        pin.getLabel(),
                                        (int) (p.x + xoff + cW),
                                        (int) (p.y + cH)
                                );
                            }
                        }
                    }
                }
            }

            g2.scale(1f / this.scale, 1f / this.scale);

            //module info table
            int co = 0, in = 0, out = 0;
            if (module != null) {
                co = module.getLogicModel().size();
                for (IOPin p : module.getPins()) {
                    if (p.mode == IOPin.MODE.INPUT) {
                        in++;
                    } else if (p.mode == IOPin.MODE.OUTPUT) {
                        out++;
                    }
                }
            }
            g2.setColor(Colors.ME_BACKGROUND);
            g2.fillRect(10, 8, 200, 80);
            g2.setColor(Colors.ME_MODEL);
            g2.drawRect(10, 8, 200, 80);

            g2.drawString("Logic model: " + logicModelName, 20, 20);
            g2.drawString("Complexity: " + co, 20, 35);
            g2.drawString("Inputs: " + in, 20, 50);
            g2.drawString("Output: " + out, 20, 65);
            g2.drawString("Last change: " + changeTime, 20, 80);

            //info table
            if (this.cursor != null && this.pressed != null) {
                g2.setColor(Colors.ME_BACKGROUND);
                g2.fillRect(10, this.getHeight() - 40, 100, 40);
                g2.setColor(Colors.ME_GRID);
                g2.drawString(
                        "Dx: " + String.format("%.03f", (float) (this.cursor.x - this.pressed.x) / LogicSimulatorCore.WORK_SPACE_STEP),
                        20,
                        this.getHeight() - 10
                );
                g2.drawString(
                        "Dy: " + String.format("%.03f", (float) (this.cursor.y - this.pressed.y) / LogicSimulatorCore.WORK_SPACE_STEP),
                        20,
                        this.getHeight() - 20
                );
            }
        }

        /**
         * Handler
         * ####################################################################
         */
        private Point cursor, pressed;

        private boolean isPressed = false;

        private Line line = null;
        private Circle circle = null;
        private Curve curve = null;
        private boolean circleRadius = false, curveC = false;

        //selected points (for moving, deleting)
        private final List<Point.Double> select = new ArrayList<>();

        //copy
        private final List<GraphicsObject> copy_vector = new ArrayList<>();

        //buffer for new added graphics objects
        private final List<GraphicsObject> GOBuffer = new ArrayList<>();

        @Override
        public void mouseClicked(MouseEvent evt) {

        }

        @Override
        public void mousePressed(MouseEvent evt) {
            //select this project file in project
            this.owner.selectInProject();

            this.isPressed = true;
            this.pressed = evt.getPoint();

            this.requestFocus();

            //place GO buffer
            if (!this.GOBuffer.isEmpty()) {
                GraphicsObject[] buf = new GraphicsObject[this.GOBuffer.size()];
                int index = 0;
                for (GraphicsObject go : this.GOBuffer) {
                    buf[index++] = go;
                    for (Point.Double pt : go.getPoints()) {
                        Tools.step(pt, LogicSimulatorCore.WORK_SPACE_STEP / 2);
                    }
                }
                //add all elements of buffer to model
                module.getModel().getGraphicsObjects().addAll(Arrays.asList(buf));
                this.GOBuffer.clear();
            }

            Point.Double p = new Point.Double(
                    this.cursor.x - this.getWidth() / 2,
                    this.cursor.y - this.getHeight() / 2
            );

            //place line
            if (this.line != null) {
                Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP / 2);
                /**
                 * Line cant start and end in same point => if this is false
                 * then place end point of line in grid and line pointer set to
                 * null
                 */
                if (!Tools.equal(p, line.p1)) {
                    this.line.p2 = p;
                    this.line = null;
                }

                this.repaint();
            }

            //place circle
            if (this.circle != null) {
                if (!this.circleRadius) {
                    Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP / 2);
                    this.circle.p1 = p;
                    //on next step changing circle radius
                    this.circleRadius = true;
                } else {
                    this.circleRadius = false;
                    this.circle = null;
                }
            }

            //place curve
            if (this.curve != null) {
                if (!this.curveC) {
                    Tools.step(this.curve.p2, LogicSimulatorCore.WORK_SPACE_STEP);
                    //on next step changing circle radius
                    this.curveC = true;
                } else {
                    Tools.step(this.curve.control, LogicSimulatorCore.WORK_SPACE_STEP / 4);
                    this.curveC = false;
                    this.curve = null;
                }
            }

            //start moving with points (set boolean movingWithPoints on true)
            if (this.line == null && this.circle == null && this.curve == null) {
                Point c = Tools.copy(this.pressed);
                c.x -= this.getWidth() / 2;
                c.y -= this.getHeight() / 2;
                c = Tools.divide(c, this.scale);
                //moving with colection of selected point can start only if user press in some of them
                for (Point.Double pt : this.select) {
                    if (Tools.dist(pt, c) < 9) {
                        this.movingWithPoints = true;
                        break;
                    }
                }
            }

            this.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            this.isPressed = false;

            if (evt.getButton() != 1) {
                //show popum menu
                this.owner.menu.show(this, evt.getX(), evt.getY());
                return;
            }

            //all points place to grid system
            this.select.stream().forEach((pt) -> {
                Tools.step(pt, LogicSimulatorCore.WORK_SPACE_STEP / 2);
            });

            //select all points
            if (!this.movingWithPoints) {
                selectPointsInRect(!evt.isControlDown());
            }

            //reset vars
            this.movingWithPoints = false;

            //recompute size
            module.getModel().computeSize();

            this.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent evt) {

        }

        @Override
        public void mouseExited(MouseEvent evt) {

        }

        private Point last = null;

        private boolean movingWithPoints = false;

        @Override
        public void mouseDragged(MouseEvent evt) {
            this.cursor = evt.getPoint();

            this.repaint();

            //moving with selected points
            if (this.movingWithPoints) {
                this.select.stream().forEach((pt) -> {
                    if (last != null) {
                        pt.x += (evt.getX() - last.x) / this.scale;
                        pt.y += (evt.getY() - last.y) / this.scale;
                    }
                });
            }

            this.last = Tools.copy(this.cursor);
        }

        @Override
        public void mouseMoved(MouseEvent evt) {
            this.cursor = evt.getPoint();

            //line move with end point
            if (this.line != null) {
                this.line.p2.x = this.cursor.x - this.getWidth() / 2;
                this.line.p2.y = this.cursor.y - this.getHeight() / 2;
                Tools.step(this.line.p2, LogicSimulatorCore.WORK_SPACE_STEP / 2);
            }

            //move with circle and changing radius
            if (this.circle != null) {
                if (this.circleRadius) {
                    //radius
                    Point p = new Point(
                            this.cursor.x - this.getWidth() / 2,
                            this.cursor.y - this.getHeight() / 2
                    );
                    this.circle.radius = (int) Tools.dist(this.circle.p1, p);
                    this.circle.radius = Tools.step(this.circle.radius, LogicSimulatorCore.WORK_SPACE_STEP / 4);
                } else {
                    //move
                    this.circle.p1.x = this.cursor.x - this.getWidth() / 2;
                    this.circle.p1.y = this.cursor.y - this.getHeight() / 2;
                    Tools.step(this.circle.p1, LogicSimulatorCore.WORK_SPACE_STEP / 2);
                }
            }

            //move with curve end point and control point
            if (this.curve != null) {
                if (this.curveC) {
                    this.curve.control.x = this.cursor.x - this.getWidth() / 2;
                    this.curve.control.y = this.cursor.y - this.getHeight() / 2;
                    Tools.step(this.curve.control, LogicSimulatorCore.WORK_SPACE_STEP / 4);
                } else {
                    this.curve.p2.x = this.cursor.x - this.getWidth() / 2;
                    this.curve.p2.y = this.cursor.y - this.getHeight() / 2;
                    Tools.step(this.curve.p2, LogicSimulatorCore.WORK_SPACE_STEP / 2);
                }
            }

            //move with gobuffer  objects
            this.GOBuffer.stream().forEach((go) -> {
                if (last != null) {
                    for (Point.Double pt : go.getPoints()) {
                        pt.x += (evt.getX() - last.x) / this.scale;
                        pt.y += (evt.getY() - last.y) / this.scale;
                    }
                }
            });

            //last positon of cursor
            this.last = Tools.copy(this.cursor);

            this.repaint();
        }

        /**
         * This select all point of object model in select rectangle
         */
        private void selectPointsInRect(boolean clearLastSelection) {
            if (clearLastSelection) {
                this.select.clear();
            }

            if (this.pressed == null || module.getModel() == null) {
                return;
            }

            //select by mouse click
            if (this.last == null) {
                this.pressed.x -= 5;
                this.pressed.y -= 5;
                this.last = new Point(this.pressed.x + 10, this.pressed.y + 10);
            }

            //bounds of select rect
            Point min = new Point(
                    Math.min(this.pressed.x, this.last.x) - this.getWidth() / 2,
                    Math.min(this.pressed.y, this.last.y) - this.getHeight() / 2
            ), max = new Point(
                    Math.max(this.pressed.x, this.last.x) - this.getWidth() / 2,
                    Math.max(this.pressed.y, this.last.y) - this.getHeight() / 2
            );

            min = Tools.divide(min, this.scale);
            max = Tools.divide(max, this.scale);

            List<Point.Double> pts = Tools.getPoints(module.getModel());

            for (Point.Double pt : pts) {
                if (Tools.isInBounds(min, max, Tools.ptToInt(pt))) {
                    this.select.add(pt);
                }
            }
        }

        /**
         * Remove graphics model from model
         *
         * @param p Point of object
         */
        private void removeGraphicsObjectFromModel(Point.Double p) {
            //try remove graphics object
            GraphicsObject go = getOwner(p);
            if (go != null) {
                module.getModel().getGraphicsObjects().remove(go);
            }
        }

        /**
         * Get graphics owner of point p
         *
         * @param p Point.Double
         * @return
         */
        private GraphicsObject getOwner(Point.Double p) {
            if (module.getModel().getGraphicsObjects() != null) {
                for (GraphicsObject go : module.getModel().getGraphicsObjects()) {
                    if (go == null) {
                        continue;
                    }
                    for (Point.Double pt : go.getPoints()) {
                        if (pt == p) {
                            return go;
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Copy selected graphics objects and store them to the copy vector
         * before pasting past copy objects again
         */
        private void copySelectedGraphicsObjects() {
            //clear vector
            this.copy_vector.clear();
            //copy
            this.select.stream().forEach((pt -> {
                GraphicsObject go = getOwner(pt);
                if (go != null) {
                    this.copy_vector.add(go.cloneObject());
                }
            }));
        }

        @Override
        public Point getCursorPosition() {
            return this.cursor;
        }

        private IOPin isPointPinPosition(Point2D.Double p) {
            for (IOPin pin : module.getModel().getIOPins()) {
                if (pin.getPosition() == p) {
                    return pin;
                }
            }
            return null;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            //none
        }

        @Override
        public void keyPressed(KeyEvent e) {

            //add new pressed char to each selected string                
            this.select.stream().forEach((pt) -> {
                GraphicsObject go = this.getOwner(pt);
                if (go instanceof GString) {
                    ((GString) go).addKey(e);
                }
            });

            this.repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //none
        }

    }

}
