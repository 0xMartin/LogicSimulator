/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator.projectFile;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.PFHandler;
import logicSimulator.PFMode;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.Settings;
import logicSimulator.Tools;
import logicSimulator.common.LogicModule;
import logicSimulator.ui.Colors;
import logicSimulator.common.IOPin;
import logicSimulator.common.Circle;
import logicSimulator.common.Line;
import logicSimulator.common.Curve;
import logicSimulator.common.GraphicsObject;
import window.components.ProjectFileToolbar;

/**
 * Allows logic module model editing
 *
 * @author Martin
 */
public class ModuleEditor extends JPanel implements ProjectFile {

    //project
    private final Project project;

    //logic module
    private final LogicModule module;

    //workspace (logic model)
    private WorkSpace workSpace;

    private final PFMode pfMode = new PFMode(false, false, true);

    //handler
    private final EditorHandler editorHandler;

    public ModuleEditor(String name, Project project, LogicModule module) {
        //default
        this.module = module;
        this.project = project;
        this.setName(name);
        this.setBorder(null);
        this.setLayout(new BorderLayout());

        //toolbar
        this.add(new ProjectFileToolbar(this), BorderLayout.NORTH);

        //editor handler
        this.editorHandler = new EditorHandler();
        this.add(this.editorHandler, BorderLayout.CENTER);
    }

    /**
     * Get editor handler
     *
     * @return
     */
    public EditorHandler getEditorHandler() {
        return this.editorHandler;
    }

    /**
     * Set workspace for this logic module
     *
     * @param workspace
     */
    public void setWorkSpace(WorkSpace workspace) {
        this.workSpace = workspace;
    }

    /**
     * Get workspace
     *
     * @return
     */
    public WorkSpace getWorkSpace() {
        return this.workSpace;
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
    public PFMode getPFMode() {
        return this.pfMode;
    }

    @Override
    public Component getComp() {
        return this;
    }

    @Override
    public PFHandler getHandler() {
        return this.editorHandler;
    }

    @Override
    public Project getProject() {
        return this.project;
    }

    @Override
    public void selectInProject() {
        if (this.project != null) {
            this.project.setSelectedFile(this);
        }
    }

    public void selecteAllGraphicsObjects() {
        List<Point.Double> pts = Tools.getPoints(module.getModel());
        this.editorHandler.select.clear();
        pts.stream().forEach((pt -> {
            this.editorHandler.select.add(pt);
        }));
    }

    /**
     * Handler for logic module editor (rendering, model editing, linking logic
     * model, ...)
     */
    private class EditorHandler extends JPanel implements PFHandler,
            MouseListener, MouseMotionListener {

        //menu for model editor
        private final JPopupMenu menu;

        public EditorHandler() {
            super();

            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (module != null) {
                        module.refreshIOPins();
                    }
                }
            });

            //init menu
            this.menu = new JPopupMenu();
            this.add(this.menu);
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
                chooseLogicModel();
            };
            item.addActionListener(action);
            this.registerKeyboardAction(
                    action, KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            //set model 
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
                Point.Double p = new Point.Double(
                        this.pressed.x - this.getWidth() / 2,
                        this.pressed.y - this.getHeight() / 2
                );
                Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP);
                this.line = new Line(p, Tools.copy(p));
                module.getModel().graphicsObjects = Tools.addGraphicsObjects(
                        module.getModel().graphicsObjects, new GraphicsObject[]{this.line});
                this.repaint();
            };
            item.addActionListener(action);

            //Add circle
            item = new JMenuItem("Add circle");
            m.add(item);
            action = (ActionEvent evt) -> {
                Point.Double p = new Point.Double(
                        this.pressed.x - this.getWidth() / 2,
                        this.pressed.y - this.getHeight() / 2
                );
                this.circle = new Circle(p, LogicSimulatorCore.WORK_SPACE_STEP);
                module.getModel().graphicsObjects = Tools.addGraphicsObjects(
                        module.getModel().graphicsObjects, new GraphicsObject[]{this.circle});
                this.repaint();
            };
            item.addActionListener(action);

            //Add curve
            item = new JMenuItem("Add curve");
            m.add(item);
            action = (ActionEvent evt) -> {
                Point.Double p = new Point.Double(
                        this.pressed.x - this.getWidth() / 2,
                        this.pressed.y - this.getHeight() / 2
                );
                Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP);
                this.curve = new Curve(p, Tools.copy(p), Tools.copy(p));
                module.getModel().graphicsObjects = Tools.addGraphicsObjects(
                        module.getModel().graphicsObjects, new GraphicsObject[]{this.curve});
                this.repaint();
            };
            item.addActionListener(action);

            //Add rect
            item = new JMenuItem("Add rectangle");
            m.add(item);
            action = (ActionEvent evt) -> {
                Point.Double p = new Point.Double(
                        this.pressed.x - this.getWidth() / 2,
                        this.pressed.y - this.getHeight() / 2
                );
                int a = LogicSimulatorCore.WORK_SPACE_STEP * 3;
                GraphicsObject[] rect = new GraphicsObject[]{
                    new Line(new Point.Double(p.x, p.y), new Point.Double(p.x, p.y + a)),
                    new Line(new Point.Double(p.x, p.y), new Point.Double(p.x + a, p.y)),
                    new Line(new Point.Double(p.x + a, p.y), new Point.Double(p.x + a, p.y + a)),
                    new Line(new Point.Double(p.x, p.y + a), new Point.Double(p.x + a, p.y + a))
                };
                for (GraphicsObject go : rect) {
                    this.GOBuffer.add(go);
                }
            };
            item.addActionListener(action);

            //Add text
            item = new JMenuItem("Add text");
            m.add(item);
            action = (ActionEvent evt) -> {

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
                module.getModel().graphicsObjects = null;
                this.repaint();
            };
            item.addActionListener(action);

            //select all
            item = new JMenuItem("Select all");
            m.add(item);
            action = (ActionEvent evt) -> {
                if (module != null) {
                    List<Point.Double> pts = Tools.getPoints(module.getModel());
                    this.select.clear();
                    pts.stream().forEach((pt) -> {
                        this.select.add(pt);
                    });
                }
            };
            item.addActionListener(action);

            //delete
            item = new JMenuItem("Delete");
            m.add(item);
            action = (ActionEvent evt) -> {
                //delete
                this.select.stream().forEach((pt) -> {
                    removeGraphicsObjectFromModel(pt);
                });
                //repaint
                this.repaint();
            };
            item.addActionListener(action);
            this.registerKeyboardAction(
                    action, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            //copy
            item = new JMenuItem("Copy");
            m.add(item);
            action = (ActionEvent evt) -> {
                copySelectedGraphicsObjects();
            };
            item.addActionListener(action);
            this.registerKeyboardAction(
                    action, KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            //copy
            item = new JMenuItem("Paste");
            m.add(item);
            action = (ActionEvent evt) -> {
                //clear selection
                this.select.clear();
                //move copy of data from copy vector to GO buffer
                this.copy_vector.stream().forEach((go) -> {
                    GOBuffer.add(go.cloneObject());
                });
            };
            item.addActionListener(action);
            this.registerKeyboardAction(
                    action, KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            item = new JMenuItem("Generate java model");
            m.add(item);
            action = (ActionEvent evt) -> {
                Tools.generateJavaModel(module.getModel());
            };
            item.addActionListener(action);
        }

        /**
         * Choose logic model (workspace) for module
         */
        public void chooseLogicModel() {
            //load all workspaces from project
            List<WorkSpace> works = new ArrayList<>();
            project.getProjectFiles().stream().forEach((pf) -> {
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
                        module.setLogicModel(works.get(i));
                        workSpace = works.get(i);
                    }
                }
            }
        }

        @Override
        public void repaintPF(){
            this.repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponents(g);

            Graphics2D g2 = (Graphics2D) g;

            //set high rendering quality (if is allowed in core)
            if (Settings.HIGH_RENDER_QUALITY) {
                Tools.setHighQuality(g2);
            }

            g2.setColor(Colors.ME_BACKGROUND);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());

            //center of screen
            int cW = this.getWidth() / 2;
            int cH = this.getHeight() / 2;

            //find start [x, y] for grid
            g2.setColor(Colors.ME_GRID);
            int startX = this.getWidth() / 2;
            int startY = this.getHeight() / 2;
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
                go.draw(g2, this.getWidth() / 2, this.getHeight() / 2);
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
                g2.drawLine(this.cursor.x, 0, this.cursor.x, this.getHeight());
                g2.drawLine(0, this.cursor.y, this.getWidth(), this.cursor.y);
            }

            //draw cross mouse press pointer
            if (this.pressed != null) {
                g2.setColor(Colors.ME_GRID);
                g2.drawLine(this.pressed.x, - 10 + this.pressed.y, this.pressed.x, 10 + this.pressed.y);
                g2.drawLine(this.pressed.x - 10, this.pressed.y, this.pressed.x + 10, this.pressed.y);
            }

            //draw model size
            if (module != null) {
                if (module.getModel() != null) {
                    g2.setColor(Colors.ME_GRID);
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                    for (int i = -1; i <= 1; i += 2) {
                        g2.drawLine(
                                this.getWidth() / 2 + i * module.getModel().getWidth() / 2,
                                0,
                                this.getWidth() / 2 + i * module.getModel().getWidth() / 2,
                                this.getHeight()
                        );
                        g2.drawLine(
                                0,
                                this.getHeight() / 2 + i * module.getModel().getHeight() / 2,
                                this.getWidth(),
                                this.getHeight() / 2 + i * module.getModel().getHeight() / 2
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
                int w = this.last.x - this.pressed.x;
                int h = this.last.y - this.pressed.y;
                g2.fillRect(
                        this.pressed.x + (w < 0 ? w : 0),
                        this.pressed.y + (h < 0 ? h : 0),
                        Math.abs(this.last.x - this.pressed.x),
                        Math.abs(this.last.y - this.pressed.y)
                );
                g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));
                g2.drawRect(
                        this.pressed.x + (w < 0 ? w : 0),
                        this.pressed.y + (h < 0 ? h : 0),
                        Math.abs(this.last.x - this.pressed.x),
                        Math.abs(this.last.y - this.pressed.y)
                );
            }

            //draw points of model
            if (module != null) {
                if (module.getModel() != null) {
                    List<Point.Double> pts = Tools.getPoints(module.getModel());
                    for (int i = 0; i < pts.size(); i++) {
                        Point.Double p = pts.get(i);
                        if (p != null) {
                            if (this.select.stream().anyMatch((pt) -> (p == pt))) {
                                g2.setColor(logicSimulator.ui.Colors.SELECT_RECT);
                            } else {
                                g2.setColor(Color.BLUE);
                            }
                            g2.fillRect(
                                    (int) (p.x - 2) + this.getWidth() / 2,
                                    (int) (p.y - 2) + this.getHeight() / 2,
                                    4,
                                    4
                            );
                        }
                    }
                }
            }

            //module info table
            String lm = "";
            int co = 0, in = 0, out = 0;
            if (workSpace != null) {
                lm = workSpace.getName();
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
            g2.fillRect(10, 8, 200, 65);
            g2.setColor(Colors.ME_MODEL);
            if (workSpace != null) {
                g2.drawRect(10, 8, 200, 65);
            }
            g2.drawString("Logic model: " + lm, 20, 20);
            g2.drawString("Complexity: " + co, 20, 35);
            g2.drawString("Inputs: " + in, 20, 50);
            g2.drawString("Output: " + out, 20, 65);

            //info table
            if (this.cursor != null && this.pressed != null) {
                g2.setColor(Colors.ME_BACKGROUND);
                g2.fillRect(10, this.getHeight() - 40, 100, 40);
                g2.setColor(Colors.ME_GRID);
                g2.drawString(
                        "Dx: " + (float) (this.cursor.x - this.pressed.x) / LogicSimulatorCore.WORK_SPACE_STEP,
                        20,
                        this.getHeight() - 10
                );
                g2.drawString(
                        "Dy: " + (float) (this.cursor.y - this.pressed.y) / LogicSimulatorCore.WORK_SPACE_STEP,
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
                //add buffer to model
                module.getModel().graphicsObjects = Tools.addGraphicsObjects(
                        module.getModel().graphicsObjects, buf
                );
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

            //start moving with points
            if (this.line == null && this.circle == null && this.curve == null) {
                Point c = evt.getPoint();
                c.x -= this.getWidth() / 2;
                c.y -= this.getHeight() / 2;
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
                this.menu.show(this, evt.getX(), evt.getY());
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
                        pt.x += evt.getX() - last.x;
                        pt.y += evt.getY() - last.y;
                    }
                });
            }

            this.last = evt.getPoint();
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
                        pt.x += evt.getX() - last.x;
                        pt.y += evt.getY() - last.y;
                    }
                }
            });

            //last positon of cursor
            this.last = evt.getPoint();

            this.repaint();
        }

        /**
         * This select all point in select rectangle
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

            List<Point.Double> pts = Tools.getPoints(module.getModel());

            pts.stream().forEach((pt) -> {
                if (Tools.isInBounds(min, max, Tools.ptToInt(pt))) {
                    this.select.add(pt);
                }
            });
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
                module.getModel().graphicsObjects = Tools.removeGraphicsObject(
                        module.getModel().graphicsObjects, go);
            }
        }

        /**
         * Get graphics owner of point p
         *
         * @param p Point.Double
         * @return
         */
        private GraphicsObject getOwner(Point.Double p) {
            if (module.getModel().graphicsObjects != null) {
                for (GraphicsObject go : module.getModel().graphicsObjects) {
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
        public void zoom(int ration) {
            //not suported
        }

        @Override
        public Point getCursorPosition() {
            return this.cursor;
        }

    }

}
