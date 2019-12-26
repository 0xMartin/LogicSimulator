/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
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
import logicSimulator.common.LogicModule;
import logicSimulator.ui.Colors;
import logicSimulator.common.IOPin;
import logicSimulator.common.Circle;
import logicSimulator.common.Line;
import logicSimulator.common.Curve;

/**
 *
 * @author Martin
 */
public class ModuleEditor extends JPanel implements ProjectFile, MouseListener, MouseMotionListener {

    private final Project project;

    private final LogicModule module;

    private WorkSpace workSpace;

    public boolean OPENED = false;

    private final JPopupMenu menu;

    public ModuleEditor(String name, Project project, LogicModule module) {
        this.module = module;
        this.project = project;
        this.setName(name);
        this.setBorder(null);
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
        this.menu.registerKeyboardAction(
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
            Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP / 2);
            this.line = new Line(p, Tools.copy(p));
            this.module.getModel().lines = Tools.addLine(
                    this.module.getModel().lines, this.line);
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
            this.circle = new Circle(p, LogicSimulatorCore.WORK_SPACE_STEP / 2);
            this.module.getModel().circles = Tools.addCircle(
                    this.module.getModel().circles, this.circle);
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
            Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP / 2);
            this.curve = new Curve(p, Tools.copy(p), Tools.copy(p));
            this.module.getModel().curves = Tools.addCurve(
                    this.module.getModel().curves, this.curve);
            this.repaint();
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

        };
        item.addActionListener(action);
        //select all
        item = new JMenuItem("Select all");
        m.add(item);
        action = (ActionEvent evt) -> {

        };
        item.addActionListener(action);
        //delete
        item = new JMenuItem("Delete");
        m.add(item);
        action = (ActionEvent evt) -> {
            select = null;
        };
        item.addActionListener(action);
        this.menu.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public void chooseLogicModel() {
        //load all workspaces from project
        List<WorkSpace> works = new ArrayList<>();
        this.project.getProjectFiles().stream().forEach((pf) -> {
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
                    this.module.setLogicModel(works.get(i));
                    this.workSpace = works.get(i);
                }
            }
        }
    }

    public void setWorkSpace(WorkSpace workspace) {
        this.workSpace = workspace;
    }

    public WorkSpace getWorkSpace() {
        return this.workSpace;
    }

    public LogicModule getModule() {
        return this.module;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);

        Graphics2D g2 = (Graphics2D) g;

        //set high rendering quality (if is allowed in core)
        if (Settings.HIGH_RENDER_QUALITY) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
        }

        g2.setColor(Colors.BACKGROUND);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        //center of screen
        int cW = this.getWidth() / 2;
        int cH = this.getHeight() / 2;

        //find start [x, y] for grid
        g2.setColor(Colors.GRID);
        int startX = this.getWidth() / 2;
        int startY = this.getHeight() / 2;
        while (startX > 0) {
            startX -= LogicSimulatorCore.WORK_SPACE_STEP;
        }
        while (startY > 0) {
            startY -= LogicSimulatorCore.WORK_SPACE_STEP;
        }
        //draw grid
        for (int x = startX; x < this.getWidth(); x += LogicSimulatorCore.WORK_SPACE_STEP) {
            for (int y = startY; y < this.getHeight(); y += LogicSimulatorCore.WORK_SPACE_STEP) {
                g2.fillRect(x - 1, y - 1, 2, 2);
            }
        }

        //draw model of module
        g2.setStroke(new BasicStroke(2));
        this.module.getModel().render(g2, cW, cH);
        g2.setStroke(new BasicStroke(1));

        //draw center cross
        g2.setColor(Color.red);
        g2.drawLine(cW - 10, cH, cW + 10, cH);
        g2.drawLine(cW, cH - 10, cW, cH + 10);

        //draw cross cursor pointer
        if (this.cursor != null) {
            g2.setColor(Color.blue);
            g2.drawLine(this.cursor.x, 0, this.cursor.x, this.getHeight());
            g2.drawLine(0, this.cursor.y, this.getWidth(), this.cursor.y);
        }

        //draw cross press pointer
        if (this.pressed != null) {
            g2.setColor(Color.gray);
            g2.drawLine(this.pressed.x, -10 + this.pressed.y, this.pressed.x, 10 + this.pressed.y);
            g2.drawLine(this.pressed.x - 10, this.pressed.y, this.pressed.x + 10, this.pressed.y);
        }

        //draw model size
        if (this.module != null) {
            if (this.module.getModel() != null) {
                g2.setColor(Color.gray);
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                for (int i = -1; i <= 1; i += 2) {
                    g2.drawLine(
                            this.getWidth() / 2 + i * this.module.getModel().getWidth() / 2,
                            0,
                            this.getWidth() / 2 + i * this.module.getModel().getWidth() / 2,
                            this.getHeight()
                    );
                    g2.drawLine(
                            0,
                            this.getHeight() / 2 + i * this.module.getModel().getHeight() / 2,
                            this.getWidth(),
                            this.getHeight() / 2 + i * this.module.getModel().getHeight() / 2
                    );
                }
                g2.setStroke(new BasicStroke(1));
            }
        }

        //module info table
        String lm = "";
        int co = 0, in = 0, out = 0;
        if (this.workSpace != null) {
            lm = this.workSpace.getName();
            co = this.module.getLogicModel().size();
            for (IOPin p : this.module.getPins()) {
                if (p.mode == IOPin.MODE.INPUT) {
                    in++;
                } else if (p.mode == IOPin.MODE.OUTPUT) {
                    out++;
                }
            }
        }
        g2.setColor(Color.white);
        g2.fillRect(10, 8, 200, 65);
        g2.setColor(Color.black);
        if (this.workSpace != null) {
            g2.drawRect(10, 8, 200, 65);
        }
        g2.drawString("Logic model: " + lm, 20, 20);
        g2.drawString("Complexity: " + co, 20, 35);
        g2.drawString("Inputs: " + in, 20, 50);
        g2.drawString("Output: " + out, 20, 65);

        //info table
        if (this.cursor != null && this.pressed != null) {
            g2.setColor(Color.white);
            g2.fillRect(10, this.getHeight() - 40, 100, 40);
            g2.setColor(Color.black);
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

    //events
    private Point cursor, pressed;

    private Line line = null;
    private Circle circle = null;
    private Curve curve = null;
    private boolean circleRadius = false, curveC = false;

    private Object select = null;
    private final List<Point.Double> ioPinsPositions = new ArrayList<>();

    @Override
    public void mouseClicked(MouseEvent evt) {

    }

    @Override
    public void mousePressed(MouseEvent evt) {
        this.pressed = evt.getPoint();

        if (!this.isFocusOwner()) {
            this.requestFocus();
        }

        this.repaint();

        Point.Double p = new Point.Double(
                this.cursor.x - this.getWidth() / 2,
                this.cursor.y - this.getHeight() / 2
        );

        //place line
        if (this.line != null) {
            Tools.step(p, LogicSimulatorCore.WORK_SPACE_STEP / 2);
            /**
             * Line cant start and end in same point => if this is false then
             * place end point of line in grid and line pointer set to null
             */
            if (!Tools.equal(p, line.p1)) {
                this.line.p2 = p;
                this.line = null;
            }
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

        //recompute size
        this.module.getModel().computeSize();

        //select pin
        if (this.module.getPins() != null) {
            for (IOPin pin : this.module.getPins()) {
                if (Tools.dist(p, pin.getPosition()) < 9) {
                    this.ioPinsPositions.add(pin.getPosition());
                    break;
                }
            }
        }

        if (this.module.getModel().lines != null) {
            boolean trySelectLine = true;
            //select point fof line
            for (Line l : this.module.getModel().lines) {
                if (Tools.dist(l.p1, p) < 3) {
                    this.ioPinsPositions.add(l.p1);
                    trySelectLine = false;
                    break;
                }
                if (Tools.dist(l.p2, p) < 3) {
                    this.ioPinsPositions.add(l.p2);
                    trySelectLine = false;
                    break;
                }
            }
            //select line
            if (trySelectLine) {
                this.select = Tools.lineItersect(this.module.getModel().lines, p);
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        if (evt.getButton() != 1) {
            //show popum menu
            this.menu.show(this, evt.getX(), evt.getY());
            return;
        }

        //all io pins place to grid system
        this.ioPinsPositions.stream().forEach((pinPosition) -> {
            Tools.step(pinPosition, LogicSimulatorCore.WORK_SPACE_STEP);
        });

        //reset vars
        last = null;
        this.ioPinsPositions.clear();

        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent evt) {

    }

    @Override
    public void mouseExited(MouseEvent evt) {

    }

    private Point last = null;

    @Override
    public void mouseDragged(MouseEvent evt) {
        this.cursor = evt.getPoint();
        this.repaint();

        //move with selected io pins
        this.ioPinsPositions.stream().forEach((pinPosition) -> {
            if (last != null) {
                pinPosition.x += evt.getX() - last.x;
                pinPosition.y += evt.getY() - last.y;
            }
        });
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

        this.repaint();
    }

    @Override
    public boolean isOpened() {
        return this.OPENED;
    }

    @Override
    public void setOpened(boolean open) {
        this.OPENED = open;
    }

    @Override
    public Component getComp() {
        return this;
    }

}
