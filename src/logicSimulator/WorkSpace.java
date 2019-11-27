/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import logicSimulator.ui.Colors;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import logicSimulator.common.CopyObjectVector;
import logicSimulator.common.IOPin;
import logicSimulator.common.Line;
import logicSimulator.common.Model;
import logicSimulator.common.Tools;
import logicSimulator.objects.control.Button;
import logicSimulator.objects.wiring.Wire;

/**
 * In workspace are all object (logic gates, notes, labels, controls, ...)
 *
 * @author Martin
 */
public class WorkSpace extends JScrollPane implements ChangeListener {

    //rendering panel 
    private final WorkspaceHandler handler;

    //name of workspace
    private final String name;

    //all object in workspace
    private final List<WorkSpaceObject> objects;

    //new added object
    private List<WorkSpaceObject> newObj;

    private final Project project;

    public WorkSpace(String name, Project p) {
        super();

        this.project = p;
        this.name = name;
        this.objects = new ArrayList<>();

        //rendering panel
        this.handler = new WorkspaceHandler(this);
        this.setViewportView(this.handler);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.getViewport().addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        //change rendering offset
        this.handler.offset.x = this.getHorizontalScrollBar().getValue();
        this.handler.offset.y = this.getVerticalScrollBar().getValue();
    }

    public WorkspaceHandler getHandler() {
        return this.handler;
    }

    public List<WorkSpaceObject> getObjects() {
        return this.objects;
    }

    public String getName() {
        return this.name;
    }

    public void update(ComputeCore cc) {

    }

    public float getScale() {
        return this.handler.scale;
    }

    public void setScale(float scale) {
        if (scale <= 0) {
            return;
        }
        this.handler.scale = scale;
    }

    public void resizeWorkSpace() {
        //calculate new size of render panel
        Dimension size = this.getSize();
        this.objects.forEach((obj) -> {
            if (obj != null) {
                Point p = obj.getPosition();
                Dimension d = obj.getSize();
                if (p.x + d.width + 100 > size.width) {
                    size.width = p.x + d.width + 100;
                }
                if (p.y + d.height + 100 > size.height) {
                    size.height = p.y + d.height + 100;
                }
            }
        });
        //resize canvas
        this.handler.setPreferredSize(size);
        this.handler.revalidate();
    }

    /**
     * Place new object on the workspace new object will stick on cursor and
     * then user must click on some place on workspace
     *
     * @param newObject
     */
    public void addNewObjects(List<WorkSpaceObject> newObject) {
        if (newObject != null) {
            //all add to list
            newObject.stream().forEach((obj) -> {
                if (obj != null) {
                    if (obj.getModel() != null) {
                        obj.getModel().setColor(Colors.DRAG_OBJ);
                    }
                    this.objects.add(obj);
                }
            });
            //set to new object list (pointer)
            this.newObj = newObject;
        }
    }

    /**
     * Unselest all objects in workspaces
     *
     * @param ignore
     */
    public void unselectAllObjects(WorkSpaceObject ignore) {
        objects.forEach((obj) -> {
            if (ignore != obj) {
                obj.unSelect();
            }
        });
        this.handler.repaint();
    }

    /**
     * Select all object in workspace
     */
    public void selectAllObjects() {
        objects.forEach((obj) -> {
            obj.select(obj.getPosition());
        });
    }

    /**
     * Select all object in area q
     *
     * @param start Start corner
     * @param end End corne
     */
    public void select(Point start, Point end) {
        //find max and min
        Point maxS = Tools.maxPoint(new Point[]{start, end});
        Point minS = Tools.minPoint(new Point[]{start, end});
        //select all
        this.objects.forEach((obj) -> {
            if (obj.getPosition() != null) {
                int x = obj.getPosition().x;
                int y = obj.getPosition().y;
                if (x >= minS.x && x <= maxS.x) {
                    if (y >= minS.y && y <= maxS.y) {
                        obj.select(obj.getPosition());
                    }
                }
            }
        });
    }

    //handler for workspace (rendering and object editing)
    //#############################################################
    public class WorkspaceHandler extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

        private final WorkSpace wspace;

        //drawing scale
        private float scale = 1.0f;

        private final Point offset = new Point(0, 0);

        public WorkspaceHandler(WorkSpace wspace) {
            super();
            this.wspace = wspace;
            this.setBackground(Colors.BACKGROUND);
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.addMouseWheelListener(this);
            this.addKeyListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            //default
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.scale(this.scale, this.scale);

            //render hints
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);

            Dimension size = this.wspace.getSize();

            //clear all
            g.clearRect(offset.x, offset.x, size.width, size.height);
            //draw grid
            g2.setColor(Colors.GRID);
            for (int x = offset.x - offset.x % LogicSimulatorCore.WORK_SPACE_STEP;
                    x < offset.x + size.width;
                    x += LogicSimulatorCore.WORK_SPACE_STEP) {
                for (int y = offset.y - offset.y % LogicSimulatorCore.WORK_SPACE_STEP;
                        y < offset.y + size.height;
                        y += LogicSimulatorCore.WORK_SPACE_STEP) {
                    g2.fillRect(x, y, 2, 2);
                }
            }
            //render all wires
            objects.forEach((var obj) -> {
                try {
                    if (obj instanceof Wire) {
                        obj.render(g2, offset, size);
                    }
                } catch (Exception ex) {
                    //ignore
                }
            });
            //render all objects on the workspace (without wires)
            objects.forEach((var obj) -> {
                try {
                    if (!(obj instanceof Wire)) {
                        obj.render(g2, offset, size);
                    }
                } catch (Exception ex) {
                    //ignore
                }
            });
            //select rect
            if (this.selectStart != null && this.selectEnd != null) {
                g2.setColor(Colors.SELECT_RECT2);
                int w = this.selectEnd.x - this.selectStart.x;
                int h = this.selectEnd.y - this.selectStart.y;
                g2.drawRect(
                        this.selectStart.x + (w < 0 ? w : 0),
                        this.selectStart.y + (h < 0 ? h : 0),
                        Math.abs(this.selectEnd.x - this.selectStart.x),
                        Math.abs(this.selectEnd.y - this.selectStart.y)
                );
                g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
                g2.fillRect(
                        this.selectStart.x + (w < 0 ? w : 0),
                        this.selectStart.y + (h < 0 ? h : 0),
                        Math.abs(this.selectEnd.x - this.selectStart.x),
                        Math.abs(this.selectEnd.y - this.selectStart.y)
                );
                g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));
            }
        }

        /**
         * Events ####################################################
         */
        //last cursor position, an
        private Point last = null, cursor = null;

        //select rect
        private Point selectStart, selectEnd;

        //copy
        private CopyObjectVector copy = null;

        //last selecte object
        private WorkSpaceObject lastSelected;

        //draged
        private boolean draged = false;

        //new line
        private Line wireLine1, wireLine2;
        private Wire wire;

        @Override
        public void mouseClicked(MouseEvent arg0) {
            //control mode
            if (!project.editMode) {
                for (WorkSpaceObject obj : objects) {
                    if (obj.getModel() != null) {
                        if (obj.getModel().intersect(arg0.getPoint(), obj.getPosition())) {
                            if (obj instanceof Button) {
                                //change value of button
                                ((Button) obj).changeValue(arg0.getPoint());
                            }
                            break;
                        }
                    }
                }
                //repaint
                this.repaint();
                return;
            }

            //edit mode
            for (WorkSpaceObject obj : objects) {
                if (obj instanceof Wire) {
                    if (obj.select(arg0.getPoint())) {
                        break;
                    }
                }
            }
            //repaint
            this.repaint();
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
            //control mode
            if (!project.editMode) {
                return;
            }
            //edit mode
            /**
             * Relocate new object first must by object selected from list (by
             * click)
             */
            if (newObj != null) {
                newObj.stream().forEach((obj) -> {
                    if (obj != null) {
                        if (obj.getPosition() != null) {
                            if (obj.getPosition().x == LogicSimulatorCore.OBJECT_NULL_POSITION) {
                                obj.getPosition().x = arg0.getPoint().x;
                                obj.getPosition().y = arg0.getPoint().y;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            //control mode
            if (!project.editMode) {
                return;
            }
            //edit mode
            Point p = arg0.getPoint();
            //moving with new added object
            if (newObj != null) {
                if (this.last != null) {
                    newObj.stream().forEach((obj) -> {
                        if (obj instanceof Wire) {
                            ((Wire) obj).getPath().stream().forEach((line) -> {
                                line.p1.x += p.x - last.x;
                                line.p1.y += p.y - last.y;
                                line.p2.x += p.x - last.x;
                                line.p2.y += p.y - last.y;
                            });
                        } else {
                            if (obj != null) {
                                obj.getPosition().x += p.x - last.x;
                                obj.getPosition().y += p.y - last.y;
                            }
                        }
                    });
                }
                this.last = p;
            }
            this.cursor = p;
            //repaint
            this.repaint();
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            //control mode
            if (!project.editMode) {
                return;
            }
            this.requestFocus();

            //edit mode
            Point p = arg0.getPoint();

            //drop new add object
            newObj = null;

            //place new line of wire
            boolean wireEvt = false;
            for (WorkSpaceObject obj : objects) {
                if (obj instanceof Wire) {
                    //wire
                    if (Tools.isOnLine(obj, this.cursor)) {
                        Point lp = new Point(
                                Tools.step(this.cursor.x, LogicSimulatorCore.WORK_SPACE_STEP),
                                Tools.step(this.cursor.y, LogicSimulatorCore.WORK_SPACE_STEP)
                        );
                        this.wire = new Wire();
                        //create two lines and both place to new wire path
                        this.wireLine1 = new Line(lp, lp);
                        this.wireLine2 = new Line(lp, lp);
                        //make streight group with first wire
                        this.wireLine2.streightGroup(this.wireLine1);
                        this.wire.getPath().add(this.wireLine1);
                        this.wire.getPath().add(this.wireLine2);
                        objects.add(this.wire);
                        wireEvt = true;
                        break;
                    }
                } else {
                    //gate
                    if (Tools.intersectIOPin(this.cursor, obj) != null) {
                        this.wire = new Wire();
                        Point lp = new Point(
                                Tools.step(this.cursor.x, LogicSimulatorCore.WORK_SPACE_STEP),
                                Tools.step(this.cursor.y, LogicSimulatorCore.WORK_SPACE_STEP)
                        );
                        this.wireLine1 = new Line(lp, lp);
                        this.wireLine2 = new Line(lp, lp);
                        this.wireLine2.streightGroup(this.wireLine1);
                        this.wire.getPath().add(this.wireLine1);
                        this.wire.getPath().add(this.wireLine2);
                        objects.add(this.wire);
                        wireEvt = true;
                        break;
                    }
                }
            }

            //select obejct (one by click)
            boolean selected = false;
            if (!wireEvt) {
                //select new
                for (WorkSpaceObject obj : objects) {
                    //object cant be wire, wire select on mouse click
                    if (!(obj instanceof Wire)) {
                        if (obj != null) {
                            if (obj.select(p)) {
                                //start edit propt of this selected object
                                project.editPropt(obj);
                                //store to last selected
                                this.lastSelected = obj;
                                selected = true;
                            }
                        }
                    }
                }
                //select start for select rectangle
                if (!selected) {
                    this.selectStart = p;
                }
            }

            if (!selected) {
                project.editPropt(null);
                //unselecting
                unselectAllObjects(null);
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            //control mode
            if (!project.editMode) {
                return;
            }

            //edit mode
            //unselect all object without last selected if control is up
            if (!arg0.isControlDown() && this.lastSelected != null && !draged) {
                unselectAllObjects(this.lastSelected);
            }

            //remove lines from wire if length of line is 0
            if (this.wireLine1 != null) {
                //drop line 1 
                if (this.wireLine1.length() <= LogicSimulatorCore.WORK_SPACE_STEP / 2) {
                    this.wire.getPath().remove(this.wireLine1);
                    this.wireLine1 = null;
                }
            }
            if (this.wireLine2 != null) {
                //drop group
                this.wireLine2.streightGroup(null);
                //drop line 2
                if (this.wireLine2.length() <= LogicSimulatorCore.WORK_SPACE_STEP / 2) {
                    this.wire.getPath().remove(this.wireLine2);
                    this.wireLine2 = null;
                }
            }
            if (this.wire != null) {
                if (this.wire.getPath().isEmpty()) {
                    objects.remove(this.wire);
                }
            }

            //place new nodes for wire line 1
            if (this.wireLine1 != null) {
                objects.stream().forEach((obj) -> {
                    if (obj != wire) {
                        //point of line is on line and it insnt in end of wire path -> place node
                        if (Tools.isOnLine(obj, Tools.ptToInt(this.wireLine1.p1))) {
                            if (!Tools.endOfWire(((Wire) obj).getPath(), this.wireLine1.p1)) {
                                this.wireLine1.n1 = true;
                            }
                        }
                        if (Tools.isOnLine(obj, Tools.ptToInt(this.wireLine1.p2))) {
                            if (!Tools.endOfWire(((Wire) obj).getPath(), this.wireLine1.p2)) {
                                this.wireLine1.n2 = true;
                            }
                        }
                    }
                });
            }

            //place new nodes for wire line 2
            if (this.wireLine2 != null) {
                objects.stream().forEach((obj) -> {
                    if (obj != wire) {
                        if (Tools.isOnLine(obj, Tools.ptToInt(this.wireLine2.p1))) {
                            if (!Tools.endOfWire(((Wire) obj).getPath(), this.wireLine2.p1)) {
                                this.wireLine2.n1 = true;
                            }
                        }
                        if (Tools.isOnLine(obj, Tools.ptToInt(this.wireLine2.p2))) {
                            if (!Tools.endOfWire(((Wire) obj).getPath(), this.wireLine2.p2)) {
                                this.wireLine2.n2 = true;
                            }
                        }
                    }
                });
            }

            //for all gates set default color
            for (WorkSpaceObject obj : objects) {
                if (obj == null) {
                    continue;
                }
                if (obj.getModel() != null) {
                    obj.getModel().setColor(Colors.GATE);
                }
            }

            //place on grid all selected objects
            for (WorkSpaceObject obj : objects) {
                if (obj == null) {
                    continue;
                }
                if (obj.getPosition() == null) {
                    continue;
                }
                if (!obj.isSelected()) {
                    continue;
                }
                if (obj instanceof Wire) {
                    //wire
                    ((Wire) obj).getPath().stream().forEach((line) -> {
                        line.p1.x = Tools.step((int) line.p1.x, LogicSimulatorCore.WORK_SPACE_STEP);
                        line.p1.y = Tools.step((int) line.p1.y, LogicSimulatorCore.WORK_SPACE_STEP);
                        line.p2.x = Tools.step((int) line.p2.x, LogicSimulatorCore.WORK_SPACE_STEP);
                        line.p2.y = Tools.step((int) line.p2.y, LogicSimulatorCore.WORK_SPACE_STEP);
                    });
                } else {
                    //object
                    obj.getPosition().x = Tools.step(obj.getPosition().x, LogicSimulatorCore.WORK_SPACE_STEP);
                    obj.getPosition().y = Tools.step(obj.getPosition().y, LogicSimulatorCore.WORK_SPACE_STEP);
                }
            }

            //select rect
            if (this.selectStart != null && this.selectEnd != null) {
                this.wspace.select(this.selectStart, this.selectEnd);
            }

            //reset vars
            this.last = null;
            this.selectStart = null;
            this.selectEnd = null;
            this.wireLine1 = null;
            this.wireLine2 = null;
            this.lastSelected = null;
            this.draged = false;

            //connect all pins with wires and wires with wires
            Tools.connectAllObject(objects);

            //resize workspace
            resizeWorkSpace();

            //repaint
            this.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent arg0) {
            //control mode
            if (!project.editMode) {
                return;
            }
            //edit mode
            this.draged = true;
            Point p = arg0.getPoint();
            //wire lines editing
            if (this.wireLine1 != null) {
                this.wireLine1.setEndStreight(
                        Tools.step(p.x, LogicSimulatorCore.WORK_SPACE_STEP),
                        Tools.step(p.y, LogicSimulatorCore.WORK_SPACE_STEP)
                );
            }
            if (this.wireLine2 != null) {
                this.wireLine2.setEndStreight(
                        Tools.step(p.x, LogicSimulatorCore.WORK_SPACE_STEP),
                        Tools.step(p.y, LogicSimulatorCore.WORK_SPACE_STEP)
                );
            }
            //moving with all selected objects
            for (WorkSpaceObject obj : objects) {
                if (obj == null) {
                    continue;
                }
                if (!obj.isSelected()) {
                    continue;
                }
                //move
                if (last != null) {
                    //wire
                    if (obj instanceof Wire) {
                        List<Line> lines = ((Wire) obj).getPath();
                        for (Line l : lines) {
                            l.p1.x += p.x - this.last.x;
                            l.p1.y += p.y - this.last.y;
                            l.p2.x += p.x - this.last.x;
                            l.p2.y += p.y - this.last.y;
                        }
                    } else {
                        //normal object
                        obj.getPosition().x += p.x - this.last.x;
                        obj.getPosition().y += p.y - this.last.y;
                    }
                } else {
                    if (obj.getModel() != null) {
                        obj.getModel().setColor(Colors.DRAG_OBJ);
                    }
                }
                //write false value to wire for all bits of "OUTPUT" pin because pin can be unconected from wire
                for (IOPin pin : obj.getPins()) {
                    if (pin.mode == IOPin.MODE.OUTPUT) {
                        if (pin.getWire() != null) {
                            pin.setValue(false);
                            pin.writeValue();
                            pin.setWire(null);
                        }
                    }
                }
            }
            //select rectangle
            if (this.selectStart != null) {
                this.selectEnd = p;
            }
            this.last = p;
            //repaint
            this.repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent arg0) {

        }

        @Override
        public void keyTyped(KeyEvent arg0) {

        }

        @Override
        public void keyPressed(KeyEvent arg0) {
            //control mode
            if (!project.editMode) {
                return;
            }
            //edit mode
            switch (arg0.getKeyCode()) {
                case KeyEvent.VK_A:
                    //select all
                    if (arg0.isControlDown()) {
                        selectAllObjects();
                    }
                    break;
                case KeyEvent.VK_C:
                    //select all
                    if (arg0.isControlDown()) {
                        //copy all selected objects
                        this.copy = new CopyObjectVector(objects, this.cursor);
                    }
                    break;
                case KeyEvent.VK_V:
                    //select all
                    if (arg0.isControlDown()) {
                        //paste copy
                        if (this.copy != null) {
                            List<WorkSpaceObject> objs = this.copy.getObjects();
                            objs.stream().forEach((obj) -> {
                                if (obj != null) {
                                    if (obj instanceof Wire) {
                                        ((Wire) obj).getPath().stream().forEach((line) -> {
                                            line.p1.x += this.cursor.x - this.copy.cursor.x;
                                            line.p1.y += this.cursor.y - this.copy.cursor.y;
                                            line.p2.x += this.cursor.x - this.copy.cursor.x;
                                            line.p2.y += this.cursor.y - this.copy.cursor.y;
                                        });
                                    } else {
                                        obj.getPosition().x += this.cursor.x - this.copy.cursor.x;
                                        obj.getPosition().y += this.cursor.y - this.copy.cursor.y;
                                    }
                                }
                            });
                            addNewObjects(objs);
                        }
                    }
                    break;
                case KeyEvent.VK_R:
                    //rotate
                    if (arg0.isControlDown()) {
                        //find selected objects
                        List<WorkSpaceObject> selected = Tools.getSelected(objects);
                        //rotate selected object
                        if (selected.size() == 1) {
                            for (WorkSpaceObject obj : selected) {
                                Model m = obj.getModel();
                                if (m != null) {
                                    //rotate
                                    m.rotate(1);
                                    //write false to connected wires (if object rotate then output pins will unconect from wire)
                                    for (IOPin pin : m.getIOPins()) {
                                        if (pin.mode == IOPin.MODE.OUTPUT) {
                                            pin.setValue(false);
                                            pin.writeValue();
                                        }
                                    }
                                }
                            }
                        } else {
                            Tools.rotate(selected, 1);
                        }
                    }
                    //connect all pins with wires and wires with wires
                    Tools.connectAllObject(objects);
                    break;
                case KeyEvent.VK_DELETE:
                    //delete all selected objects
                    for (int i = 0; i < objects.size(); i++) {
                        WorkSpaceObject obj = objects.get(i);
                        if (obj == null) {
                            continue;
                        }
                        if (obj.isSelected()) {
                            objects.remove(obj);
                            //write false to connected wires
                            if (obj.getPins() != null) {
                                obj.getPins().stream().forEach(pin -> {
                                    if (pin.mode == IOPin.MODE.OUTPUT) {
                                        pin.setValue(false);
                                        pin.writeValue();
                                    }
                                });
                            }
                            i = -1;
                        }
                    }
                    break;
            }
            //repaint
            this.repaint();
        }

        @Override
        public void keyReleased(KeyEvent arg0) {

        }

    }

}
