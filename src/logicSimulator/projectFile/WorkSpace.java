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

import logicSimulator.ui.Colors;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import logicSimulator.CircuitHandler;
import logicSimulator.LogicSimulatorCore;
import logicSimulator.PFHandler;
import logicSimulator.Project;
import logicSimulator.ProjectFile;
import logicSimulator.Settings;
import logicSimulator.Tools;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.CopyObjectVector;
import logicSimulator.objects.IOPin;
import logicSimulator.graphics.Line;
import logicSimulator.common.Model;
import logicSimulator.common.ClickAction;
import logicSimulator.data.FileIO;
import logicSimulator.objects.LogicModule;
import logicSimulator.objects.wiring.Wire;

/**
 * In workspace are all object (logic gates, notes, labels, controls, ...) in
 * this project fie you can make your logic circuit
 *
 * @author Martin
 */
public class WorkSpace extends ProjectFile {

    //handler fro this workspace (rendering, events ...)
    private final WorkspaceHandler handler;

    //all object in workspace
    private final List<WorkSpaceObject> objects;

    //new added object (this objects are sticked on cursor until user press button)
    private List<WorkSpaceObject> newObj;

    //popup menu
    private JPopupMenu menu;

    private final ProjectFileToolbar toolbar;

    /**
     * WorkSpace
     *
     * @param name Name of file
     * @param project Project
     */
    public WorkSpace(String name, Project project) {
        //default
        super(project);
        this.objects = new ArrayList<>();
        super.setLayout(new BorderLayout());
        super.setName(name);

        //toolbar
        this.toolbar = new ProjectFileToolbar(this);
        super.add(this.toolbar, BorderLayout.NORTH);

        //Workspace Handler
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBorder(null);
        super.add(jScrollPane, BorderLayout.CENTER);
        this.handler = new WorkspaceHandler(this);
        jScrollPane.setViewportView(this.handler);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.getViewport().addChangeListener((ChangeEvent evt) -> {
            this.handler.offset.x = (int) (jScrollPane.getHorizontalScrollBar().getValue() / this.handler.scale);
            this.handler.offset.y = (int) (jScrollPane.getVerticalScrollBar().getValue() / this.handler.scale);
        });

        //resize workspace
        super.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeWorkSpace(handler.scale);
            }
        });

        //init popup menu
        if (this.handler != null) {
            initMenu();
        }
    }

    private void initMenu() {
        this.menu = new JPopupMenu();
        this.handler.add(this.menu);
        JMenuItem item;
        JMenu m;
        ActionListener action;

        //<edit menu>
        m = new JMenu("Edit");
        this.menu.add(m);

        //delete object
        item = new JMenuItem("Delete");
        m.add(item);
        action = (ActionEvent evt) -> {
            if (super.getProject().editMode) {
                this.deleteSelectedObjects();
                //repaint
                this.handler.repaint();
            }
        };
        item.addActionListener(action);
        this.handler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED
        );

        //copy object
        item = new JMenuItem("Copy");
        m.add(item);
        action = (ActionEvent evt) -> {
            if (super.getProject().editMode) {
                super.getProject().copyObjects = new CopyObjectVector(objects);
            }
        };
        item.addActionListener(action);
        this.handler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK), JComponent.WHEN_FOCUSED
        );

        //paste object
        item = new JMenuItem("Paste");
        m.add(item);
        action = (ActionEvent evt) -> {
            if (super.getProject().editMode) {
                this.pasteObject(this.handler.cursor);
            }
        };
        item.addActionListener(action);
        this.handler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), JComponent.WHEN_FOCUSED
        );

        //<modification>
        m = new JMenu("Modification");
        this.menu.add(m);
        //rotate selected object
        item = new JMenuItem("Rotate");
        m.add(item);
        action = (ActionEvent evt) -> {
            if (super.getProject().editMode) {
                this.rotateSelectedObject();
            }
        };
        item.addActionListener(action);
        this.handler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK), JComponent.WHEN_FOCUSED
        );

        //select all in workspace
        item = new JMenuItem("Select all");
        this.menu.add(item);
        action = (ActionEvent evt) -> {
            if (super.getProject().editMode) {
                this.selectAllObjects();
                this.handler.repaint();
            }
        };
        item.addActionListener(action);
        this.handler.registerKeyboardAction(
                action, KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK), JComponent.WHEN_FOCUSED
        );
    }

    @Override
    public void backUpData(String projectDirectoryPath) throws Exception {
        FileIO.writeObject(
                new File(projectDirectoryPath + this.getName() + "." + LogicSimulatorCore.WORKSPACE_FILE_TYPE),
                this.objects
        );
    }

    @Override
    public void restoreData(File file) throws Exception {
        //read list with WorkSpaceObjects
        List<WorkSpaceObject> dataList = (List<WorkSpaceObject>) FileIO.readObject(file);
        this.objects.clear();
        dataList.stream().forEach((obj) -> {
            obj.restore();
            this.objects.add(obj);
        });
    }

    @Override
    public PFHandler getHandler() {
        return this.handler;
    }

    /**
     * Get toolbar of workspace
     *
     * @return ProjectFileToolbar
     */
    public ProjectFileToolbar getToolBar() {
        return this.toolbar;
    }

    /**
     * Get object that are placed on the workspace
     *
     * @return
     */
    public List<WorkSpaceObject> getObjects() {
        return this.objects;
    }

    /**
     *
     * @param scale
     */
    public void setScale(float scale) {
        if (scale <= 0) {
            return;
        }
        this.handler.scale = scale;
    }

    /**
     * Resize workspace
     *
     * @param scale Rendering scale
     */
    public void resizeWorkSpace(float scale) {
        //calculate new size of render panel
        Dimension size = this.getSize();
        this.objects.forEach((obj) -> {
            if (obj != null) {
                Point p = obj.getPosition();
                Dimension d = obj.getSize();
                if (d != null && p != null) {
                    if (p.x + d.width + 100 > size.width) {
                        size.width = p.x + d.width + 100;
                    }
                    if (p.y + d.height + 100 > size.height) {
                        size.height = p.y + d.height + 100;
                    }
                }
            }
        });
        //scale
        size.width *= scale;
        size.height *= scale;
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
            for (WorkSpaceObject obj : newObject) {
                if (obj != null) {

                    //check if this is not logic module, when its than cant be child of this circuit
                    if (obj instanceof LogicModule) {
                        if (((LogicModule) obj).getModuleName().equals(super.getName())) {
                            continue;
                        }
                    }

                    //add object
                    if (obj.getModel() != null) {
                        obj.getModel().setDrag(true);
                    }
                    this.objects.add(obj);
                }
            }
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
        this.objects.stream()
                .filter((obj) -> (obj != null))
                .forEachOrdered((WorkSpaceObject obj) -> {
                    if (obj instanceof Wire) {
                        //wire -> must try select line by line
                        Wire w = (Wire) obj;
                        //for each line test for each grid point of this line if is in select rect 
                        w.getPath().stream().forEach((line) -> {
                            if (line.p1.x == line.p2.x) {
                                //vertical line
                                int size = (int) (line.p1.y - line.p2.y);
                                int sign = (int) Math.signum(size);
                                size = Math.abs(size) / LogicSimulatorCore.WORK_SPACE_STEP;
                                int y = (int) line.p2.y;
                                for (int i = 0; i <= size; i++) {
                                    //is this point in selecte rect
                                    if (Tools.isInBounds(minS, maxS, new Point((int) line.p1.x, y))) {
                                        w.getSelectedLines().add(line);
                                        break;
                                    }
                                    y += sign * LogicSimulatorCore.WORK_SPACE_STEP;
                                }
                            } else {
                                //horisontal line
                                int size = (int) (line.p1.x - line.p2.x);
                                int sign = (int) Math.signum(size);
                                size = Math.abs(size) / LogicSimulatorCore.WORK_SPACE_STEP;
                                int x = (int) line.p2.x;
                                for (int i = 0; i <= size; i++) {
                                    //is this point in selecte rect
                                    if (Tools.isInBounds(minS, maxS, new Point(x, (int) line.p1.y))) {
                                        w.getSelectedLines().add(line);
                                        break;
                                    }
                                    x += sign * LogicSimulatorCore.WORK_SPACE_STEP;
                                }
                            }
                        });
                    } else {
                        //normal object
                        if (obj.getPosition() != null) {
                            int x = obj.getPosition().x;
                            int y = obj.getPosition().y;
                            if (x >= minS.x && x <= maxS.x) {
                                if (y >= minS.y && y <= maxS.y) {
                                    obj.select(obj.getPosition());
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Delete all selected object on workpsace
     */
    public void deleteSelectedObjects() {
        //delete all selected objects
        for (int i = 0; i < this.objects.size(); i++) {
            WorkSpaceObject obj = this.objects.get(i);
            if (obj == null) {
                continue;
            }
            if (obj.isSelected()) {
                //remove selected object
                if (obj instanceof Wire) {
                    /**
                     * if si wire the remove only selected line of path, when
                     * path is empty then remove wire from list
                     */
                    Wire w = (Wire) obj;
                    //delete all selected lines of wire path
                    w.getSelectedLines().stream().forEach((line) -> {
                        w.getPath().remove(line);
                    });
                    w.getSelectedLines().clear();
                    //if wire havent got any line in path then remove it from list
                    if (w.getPath().isEmpty()) {
                        this.objects.remove(obj);
                        i = -1;
                    }
                } else {
                    //if is normal object then is posible to remove it from list
                    this.objects.remove(obj);
                    i = -1;
                }
                //write false to connected wires
                if (obj.getPins() != null) {
                    obj.getPins().stream().forEach(pin -> {
                        if (pin.mode == IOPin.MODE.OUTPUT) {
                            pin.setValue(false);
                            pin.writeValue();
                        }
                    });
                }
            }
        }
        //connect all object
        CircuitHandler.refreshConnectivity(this.objects);
        //repaint
        this.handler.repaint();
    }

    /**
     * Rotate selected object
     */
    public void rotateSelectedObject() {
        //rotate
        //find selected objects
        List<WorkSpaceObject> selected = Tools.getSelected(this.objects);
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
        //connect all pins with wires and wires with wires
        CircuitHandler.refreshConnectivity(this.objects);
        //repaint
        this.handler.repaint();
    }

    /**
     * Paste object to the workspace
     *
     * @param cursor Point
     */
    public void pasteObject(Point cursor) {
        //paste copy
        if (super.getProject().copyObjects != null) {
            List<WorkSpaceObject> objs = super.getProject().copyObjects.getObjects();
            objs.stream().forEach((obj) -> {
                if (obj != null) {
                    Point p = super.getProject().copyObjects.centerOfCopy;
                    if (obj instanceof Wire) {
                        ((Wire) obj).getPath().stream().forEach((line) -> {
                            line.p1.x += cursor.x - p.x;
                            line.p1.y += cursor.y - p.y;
                            line.p2.x += cursor.x - p.x;
                            line.p2.y += cursor.y - p.y;
                        });
                    } else {
                        obj.getPosition().x += cursor.x - p.x;
                        obj.getPosition().y += cursor.y - p.y;
                    }
                }
            });
            addNewObjects(objs);
            //repaint
            this.handler.repaint();
        }
    }

    /**
     * Handler for workspace (rendering and object editing)
     */
    private class WorkspaceHandler extends JPanel implements PFHandler,
            MouseListener, MouseMotionListener, MouseWheelListener {

        private final WorkSpace owner;

        /**
         * (drawing scale) render position = [real size] / scale event position
         * = [real size] * scale
         */
        private float scale = 1.0f;

        private final Point offset = new Point(0, 0);

        /**
         * Create handler for workspace
         *
         * @param owner Owner of this handler
         */
        public WorkspaceHandler(WorkSpace owner) {
            super();
            this.owner = owner;
            super.setBackground(Colors.BACKGROUND);
            super.addMouseListener(this);
            super.addMouseMotionListener(this);
            super.addMouseWheelListener(this);
            super.requestFocus();
            //paint zoom value on toolbar
            this.zoom(0);
        }

        /**
         * Get scale of rendering
         *
         * @return
         */
        public float getScale() {
            return this.scale;
        }

        @Override
        public void zoom(int ration) {
            if (ration != 0) {
                this.scale -= 0.1f * ration;
                this.scale = this.scale < 0.4f ? 0.4f : this.scale;
                this.scale = this.scale > 2f ? 2f : this.scale;
                //repaint
                this.repaint();
                //resize workspace
                resizeWorkSpace(this.scale);
            }
            //set zoom info
            this.owner.getToolBar().setRightString(String.format("%.2f", this.scale * 100) + "%");
        }

        @Override
        public Point getCursorPosition() {
            return this.cursor;
        }

        @Override
        public void repaintPF() {
            this.repaint();
        }

        /**
         * Paint all object, background ... on workspace
         *
         * @param g Graphics
         */
        @Override
        protected void paintComponent(Graphics g) {
            //default
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.scale(this.scale, this.scale);

            //set high rendering quality (if is allowed in core)
            if (Settings.HIGH_RENDER_QUALITY) {
                Tools.setHighQuality(g2);
            }

            Dimension size = new Dimension(
                    (int) (super.getWidth() / this.scale),
                    (int) (super.getHeight() / this.scale)
            );

            //clear all
            g2.setColor(Colors.BACKGROUND);
            g2.fillRect(
                    offset.x, offset.y,
                    (int) (size.width),
                    (int) (size.height)
            );

            //draw grid
            g2.setColor(Colors.GRID);
            for (int x = offset.x - offset.x % LogicSimulatorCore.WORK_SPACE_STEP;
                    x < offset.x + size.width;
                    x += LogicSimulatorCore.WORK_SPACE_STEP) {
                for (int y = offset.y - offset.y % LogicSimulatorCore.WORK_SPACE_STEP;
                        y < offset.y + size.height;
                        y += LogicSimulatorCore.WORK_SPACE_STEP) {
                    g2.fillRect(x - 1, y - 1, 2, 2);
                }
            }

            //render all wires
            objects.forEach((var obj) -> {
                if (obj instanceof Wire) {
                    obj.render(g2, offset, size);
                }
            });

            //render all objects on the workspace (without wires)
            objects.forEach((var obj) -> {
                if (obj != null) {
                    if (!(obj instanceof Wire)) {
                        obj.render(g2, offset, size);
                    }
                }
            });

            //select rect
            if (this.selectStart != null && this.selectEnd != null) {
                g2.setStroke(new BasicStroke(1));
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
        /**
         * last cursor position, and current cursor position (ther values are
         * dependent on handler scale)
         */
        private Point last = null, cursor = null;

        //select rect
        private Point selectStart = null, selectEnd = null;

        //draged
        private boolean draged = false;

        //new wire
        private Line wireLine1 = null, wireLine2 = null;
        private Wire wire = null;

        //selecte wire for wire stratching
        private Wire selecteWire = null;
        private Wire wConnecter = null;

        /**
         *
         * @param evt
         */
        @Override
        public void mouseClicked(MouseEvent evt) {

        }

        /**
         *
         * @param evt
         */
        @Override
        public void mouseExited(MouseEvent evt) {
        }

        /**
         *
         * @param evt
         */
        @Override
        public void mouseEntered(MouseEvent evt) {

            //control mode
            if (!this.owner.getProject().editMode) {
                this.repaint();
                return;
            }

            //edit mode
            /**
             * Relocate new object first must by object selected from list (by
             * click)
             */
            if (this.owner.newObj != null) {
                this.owner.newObj.stream().forEach((obj) -> {
                    if (obj != null) {
                        if (obj.getPosition() != null) {
                            if (obj.getPosition().x == LogicSimulatorCore.OBJECT_NULL_POSITION) {
                                obj.getPosition().x = (int) (evt.getX() / this.scale);
                                obj.getPosition().y = (int) (evt.getY() / this.scale);
                            }
                        }
                    }
                });
            }

            this.repaint();
        }

        private IOPin lastHoverPin = null;

        /**
         *
         * @param evt
         */
        @Override
        public void mouseMoved(MouseEvent evt) {

            //save scaled cursor position
            this.cursor = Tools.divide(evt.getPoint(), this.scale);

            //control mode
            if (!this.owner.getProject().editMode) {
                return;
            }

            //edit mode
            //moving with new added object
            if (this.owner.newObj != null) {
                if (this.last != null) {
                    this.owner.newObj.stream().forEach((obj) -> {
                        if (obj instanceof Wire) {
                            ((Wire) obj).getPath().stream().forEach((line) -> {
                                line.p1.x += this.cursor.x - last.x;
                                line.p1.y += this.cursor.y - last.y;
                                line.p2.x += this.cursor.x - last.x;
                                line.p2.y += this.cursor.y - last.y;
                            });
                        } else {
                            if (obj != null) {
                                if (obj.getPosition() != null) {
                                    obj.getPosition().x += this.cursor.x - last.x;
                                    obj.getPosition().y += this.cursor.y - last.y;
                                }
                            }
                        }
                    });
                }
                this.last = this.cursor;
            }

            //remove hover for last pin
            if (this.lastHoverPin != null) {
                this.lastHoverPin.VisbileHover = false;
                this.lastHoverPin.VisibleLabel = false;
                this.lastHoverPin = null;
            }
            //show name of pin (CTR down) + pin hover effect
            for (int i = 0; i < objects.size(); ++i) {
                WorkSpaceObject obj = objects.get(i);
                if (obj.getPins() != null && obj.getModel() != null) {
                    if (obj.getModel().intersect(this.cursor, obj.getPosition(), 7)) {
                        obj.getPins().forEach((pin) -> {
                            Point.Double p = new Point.Double(
                                    pin.getPosition().x + obj.getPosition().x,
                                    pin.getPosition().y + obj.getPosition().y);
                            //if distance between cursor and io pin is lower than half of step, than display label of this pin
                            pin.VisbileHover = Tools.dist(p, this.cursor) < LogicSimulatorCore.WORK_SPACE_STEP / 2;
                            pin.VisibleLabel = evt.isControlDown() && pin.VisbileHover;
                            this.lastHoverPin = pin.VisbileHover ? pin : this.lastHoverPin;
                            //if label is visible than move object on the top of list
                            if (pin.VisibleLabel) {
                                objects.remove(obj);
                                objects.add(obj);
                            }
                        });
                        break;
                    }
                }
            }

            //repaint
            this.repaint();
        }

        /**
         *
         * @param evt
         */
        @Override
        public void mousePressed(MouseEvent evt) {
            //request this workspace
            this.requestFocus();

            //select this project file in project
            this.owner.selectInProject();

            //pressed button must be left button
            if (evt.getButton() != 1) {
                return;
            }

            //control mode
            if (!this.owner.getProject().editMode) {
                for (WorkSpaceObject obj : objects) {
                    if (obj.getModel() != null) {
                        if (obj.getModel().intersect(this.cursor, obj.getPosition())) {
                            if (obj instanceof ClickAction) {
                                //change value of button
                                ((ClickAction) obj).changeValue(this.cursor, this, this.owner.getProject());
                            }
                            break;
                        }
                    }
                }
                //repaint
                this.repaint();
                return;
            }

            //drop null objects
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i) == null) {
                    objects.remove(i);
                    i = -1;
                }
            }

            //unselect all object without last selected if control is up
            if (!evt.isControlDown()) {
                /**
                 * if cursor click on some selected object then dont unselecting
                 * (for draging)
                 */
                WorkSpaceObject wasSelected = null;
                boolean unselect = true;
                for (WorkSpaceObject obj : objects) {
                    boolean selected = obj.isSelected();
                    if (obj.select(this.cursor)) {
                        if (!selected) {
                            obj.unSelect();
                        } else {
                            unselect = false;
                            wasSelected = obj;
                        }
                        break;
                    }
                }
                if (unselect) {
                    //unselect all
                    this.selecteWire = null;
                    unselectAllObjects(null);
                } else {
                    //click on selecte wire (prepere for wire stratching)
                    clickOnSelectedWire(wasSelected);
                    return;
                }
            }

            //connect new wire to iopin
            for (WorkSpaceObject obj : objects) {
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
                    //unselect
                    this.owner.getProject().editPropt(null);
                    return;
                }
            }

            //select object
            for (WorkSpaceObject obj : this.owner.objects) {
                //object cant be wire, wire select on mouse click
                if (!(obj instanceof Wire)) {
                    if (obj != null) {
                        if (obj.select(this.cursor)) {
                            //start edit propt of this selected object
                            this.owner.getProject().editPropt(obj);
                            return;
                        }
                    }
                }
            }

            //connect new wire to wire
            for (WorkSpaceObject obj : objects) {
                if (obj instanceof Wire) {
                    if (Tools.isOnLine((Wire) obj, Tools.ptToDouble(this.cursor), null) != null) {
                        //connect next line to wire
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
                        this.owner.objects.add(this.wire);
                        return;
                    }
                }
            }

            //select start for select rectangle if wast selected object and not placed new line of wire
            this.selectStart = this.cursor;
        }

        /**
         *
         * @param evt
         */
        @Override
        public void mouseReleased(MouseEvent evt) {
            //pressed button must be left button
            if (evt.getButton() != 1) {
                //show popup menu
                this.owner.menu.show(this, evt.getX(), evt.getY());
                return;
            }

            //control mode
            if (!this.owner.getProject().editMode) {
                return;
            }

            //edit mode
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
                    this.owner.objects.remove(this.wire);
                    this.wire = null;
                }
            }

            //remove wire connector with stratched wire
            if (this.wConnecter != null) {
                if (!this.wConnecter.getPath().isEmpty()) {
                    if (this.wConnecter.getPath().get(0).length() <= LogicSimulatorCore.WORK_SPACE_STEP / 2) {
                        objects.remove(this.wConnecter);
                    }
                }
            }
            this.wConnecter = null;

            //select wire if not be connected new wire
            if (this.wire == null) {
                for (WorkSpaceObject obj : objects) {
                    if (obj instanceof Wire) {
                        if (obj.select(this.cursor)) {
                            this.selecteWire = (Wire) obj;
                            break;
                        }
                    }
                }
            }

            //place new nodes for wire line 1
            if (this.wireLine1 != null) {
                this.owner.objects.stream().forEach((obj) -> {
                    if (obj != wire) {
                        if (obj instanceof Wire) {
                            //point of line is on line and it insnt in end of wire path -> place node
                            if (Tools.isOnLine((Wire) obj, this.wireLine1.p1, null) != null) {
                                if (!Tools.endOfPath(((Wire) obj).getPath(), this.wireLine1.p1)) {
                                    this.wireLine1.n1 = true;
                                }
                            }
                            if (Tools.isOnLine((Wire) obj, this.wireLine1.p2, null) != null) {
                                if (!Tools.endOfPath(((Wire) obj).getPath(), this.wireLine1.p2)) {
                                    this.wireLine1.n2 = true;
                                }
                            }
                        }
                    }
                });
            }

            //place new nodes for wire line 2
            if (this.wireLine2 != null) {
                this.owner.objects.stream().forEach((obj) -> {
                    if (obj != wire) {
                        if (obj instanceof Wire) {
                            if (Tools.isOnLine((Wire) obj, this.wireLine2.p1, null) != null) {
                                if (!Tools.endOfPath(((Wire) obj).getPath(), this.wireLine2.p1)) {
                                    this.wireLine2.n1 = true;
                                }
                            }
                            if (Tools.isOnLine(
                                    (Wire) obj, this.wireLine2.p2, null) != null) {
                                if (!Tools.endOfPath(((Wire) obj).getPath(), this.wireLine2.p2)) {
                                    this.wireLine2.n2 = true;
                                }
                            }
                        }
                    }
                });
            }

            //place on grid all selected objects
            this.owner.objects.stream()
                    .filter((obj) -> !(obj == null))
                    .filter((obj) -> !(obj.getPosition() == null))
                    .filter((obj) -> !(!obj.isSelected()))
                    .forEachOrdered((obj) -> {
                        if (obj != null) {
                            if (obj instanceof Wire) {
                                //wire
                                ((Wire) obj).getPath().stream().forEach((line) -> {
                                    Tools.step(line.p1, LogicSimulatorCore.WORK_SPACE_STEP);
                                    Tools.step(line.p2, LogicSimulatorCore.WORK_SPACE_STEP);
                                });
                            } else {
                                //object
                                Tools.step(obj.getPosition(), LogicSimulatorCore.WORK_SPACE_STEP);
                            }
                        }
                    });

            //all new add object place on grid when user placing them
            if (this.owner.newObj != null) {
                this.owner.newObj.stream()
                        .filter((obj) -> (obj != null))
                        .forEachOrdered((obj) -> {
                            if (obj instanceof Wire) {
                                //wire (for each point of path)
                                ((Wire) obj).getPath().stream().forEach((line) -> {
                                    Tools.step(line.p1, LogicSimulatorCore.WORK_SPACE_STEP);
                                    Tools.step(line.p2, LogicSimulatorCore.WORK_SPACE_STEP);
                                });
                            } else if (obj.getPosition() != null) {
                                //normal object
                                Tools.step(obj.getPosition(), LogicSimulatorCore.WORK_SPACE_STEP);
                            }
                        });
            }

            //set drag mode for all objects on false
            objects.stream()
                    .filter((obj) -> (obj != null))
                    .filter((obj) -> (obj.getModel() != null))
                    .forEachOrdered((obj) -> {
                        obj.getModel().setDrag(false);
                    });

            //select rect
            if (this.selectStart != null && this.selectEnd != null) {
                this.owner.select(this.selectStart, this.selectEnd);
            }

            //Refresh Connectivity -> when: some object dragged, wire added, new object added
            if (this.draged || this.wireLine1 != null || this.wireLine2 != null || this.owner.newObj != null) {
                CircuitHandler.refreshConnectivity(objects);
            }

            //reset vars
            this.owner.newObj = null;
            this.last = null;
            this.selectStart = null;
            this.selectEnd = null;
            this.wireLine1 = null;
            this.wireLine2 = null;
            this.draged = false;

            //resize workspace
            resizeWorkSpace(this.scale);

            //repaint
            this.repaint();
        }

        //this list is for storing point witch changed ther positions, in one time every point can be moven only once time
        private final List<Point.Double> movedPoints = new ArrayList<>();

        /**
         *
         * @param evt
         */
        @Override
        public void mouseDragged(MouseEvent evt) {

            //save scaled cursor position
            this.cursor = Tools.divide(evt.getPoint(), this.scale);

            //control mode
            if (!this.owner.getProject().editMode) {
                return;
            }

            //select rectangle
            if (this.selectStart != null) {
                this.selectEnd = this.cursor;
            }

            //if control is down than only selecting new object with select rect
            if (evt.isControlDown()) {
                this.last = this.cursor;
                //repaint
                this.repaint();
                return;
            }

            //edit mode
            //wire path scretching
            boolean pathStretched = false;
            if (this.selecteWire != null) {
                if (this.selecteWire instanceof Wire) {
                    Wire w = (Wire) this.selecteWire;
                    pathStretched = true;
                    if (this.last != null) {
                        w.getSelectedLines().stream().forEach((line) -> {
                            if (line.p1.y == line.p2.y) {
                                //vertical line (only horizontal moving allowed)
                                line.p1.y += this.cursor.y - this.last.y;
                                line.p2.y += this.cursor.y - this.last.y;
                            } else {
                                //horisontal line (only vertical moving allowed)
                                line.p1.x += this.cursor.x - this.last.x;
                                line.p2.x += this.cursor.x - this.last.x;
                            }
                        });
                        this.draged = true;
                    }
                }
            }

            //this do if path not be stretched
            if (!pathStretched) {
                //wire lines editing
                if (this.wireLine1 != null) {
                    this.wireLine1.setEndStreight(
                            Tools.step(this.cursor.x, LogicSimulatorCore.WORK_SPACE_STEP),
                            Tools.step(this.cursor.y, LogicSimulatorCore.WORK_SPACE_STEP)
                    );
                    this.draged = true;
                }
                if (this.wireLine2 != null) {
                    this.wireLine2.setEndStreight(
                            Tools.step(this.cursor.x, LogicSimulatorCore.WORK_SPACE_STEP),
                            Tools.step(this.cursor.y, LogicSimulatorCore.WORK_SPACE_STEP)
                    );
                    this.draged = true;
                }

                //moving with all selected objects
                if (!this.movedPoints.isEmpty()) {
                    //clear move points for every invoke
                    this.movedPoints.clear();
                }
                objects.stream()
                        .filter((obj) -> !(obj == null))
                        .filter((obj) -> obj.isSelected())
                        .forEachOrdered((obj) -> {
                            //move
                            if (this.last != null) {
                                //wire
                                if (obj instanceof Wire) {
                                    List<Line> lines = ((Wire) obj).getPath();
                                    lines.forEach((line) -> {
                                        for (Point.Double pt : line.getPoints()) {
                                            //every point can by moved only once time in this object stream
                                            if (!this.movedPoints.stream().anyMatch((element) -> (element == pt))) {
                                                this.movedPoints.add(pt);
                                                pt.x += this.cursor.x - this.last.x;
                                                pt.y += this.cursor.y - this.last.y;
                                            }
                                        }
                                    });
                                } else if (obj.getPosition() != null) {
                                    //normal object
                                    obj.getPosition().x += this.cursor.x - this.last.x;
                                    obj.getPosition().y += this.cursor.y - this.last.y;
                                }
                                this.draged = true;
                            } else {
                                if (obj.getModel() != null) {
                                    obj.getModel().setDrag(true);
                                }
                            }
                        });
            }

            this.last = this.cursor;

            //repaint
            this.repaint();
        }

        /**
         *
         * @param evt
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent evt) {
            //zoom out or in
            zoom(evt.getWheelRotation());
        }

        /**
         * After click user can upconnect new wire
         *
         * @param wasSelected
         */
        private void clickOnSelectedWire(WorkSpaceObject wasSelected) {
            if (wasSelected == this.selecteWire) {
                this.selecteWire.getSelectedLines().stream().forEach((line) -> {
                    //crete new wire that will connect "line" when it will straching
                    this.wConnecter = new Wire();

                    //test for both point of line
                    for (Point.Double pt : line.getPoints()) {
                        boolean add = false;
                        //with iopin
                        for (WorkSpaceObject obj : objects) {
                            if (obj == this.selecteWire) {
                                continue;
                            }
                            if (add) {
                                break;
                            }
                            if (obj.getPins() != null) {
                                for (IOPin pin : obj.getPins()) {
                                    Point.Double p = new Point.Double(
                                            pin.getPosition().x + obj.getPosition().x,
                                            pin.getPosition().y + obj.getPosition().y
                                    );
                                    if (Tools.dist(p, pt) < 4) {
                                        add = true;
                                        break;
                                    }
                                }
                            }
                        }
                        //add line
                        if (add) {
                            this.wConnecter.getPath().add(new Line(pt, Tools.copy(pt)));
                        }
                    }
                    //add new wire connector if its path is not empty
                    if (this.wConnecter.getPath().size() > 0) {
                        objects.add(this.wConnecter);
                    }
                });
            }
        }

    }

}
