/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class PropertieEditor extends JTable implements MouseListener, KeyListener {

    private WorkSpaceObject object;

    private ActionListener action;

    private final HashMap<String, String> map;

    public void onPropertieChange(ActionListener action) {
        this.action = action;
    }

    public PropertieEditor() {
        super();
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.map = new HashMap<>();
    }

    /**
     * Set WorkSpaceObject for editing
     *
     * @param obj
     */
    public void edit(WorkSpaceObject obj) {
        if (obj == null) {
            this.object = null;
            DefaultTableModel model = (DefaultTableModel) this.getModel();
            model.getDataVector().clear();
            model.setRowCount(0);
            repaint();
        } else {
            this.object = obj;
            readPropts();
        }
    }

    /**
     * Change propertie of object
     *
     * @param prot
     */
    private void changePropt(Propertie prot) {
        if (this.object == null) {
            return;
        }
        this.object.changePropertie(prot);
    }

    /**
     * Read and show all propts of object
     */
    private void readPropts() {
        if (this.object == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) this.getModel();
        //clear
        model.getDataVector().clear();
        //read and display all properties
        Propertie[] propts = this.object.getProperties();
        if (propts == null) {
            return;
        }
        for (Propertie propt : propts) {
            model.addRow(new Object[]{propt.getName(), propt.getValueString()});
            this.map.put(propt.getName(), propt.getValueString());
        }
    }

    private void valuesChanged() {
        try {
            DefaultTableModel model = (DefaultTableModel) this.getModel();
            boolean change = false;
            //find changes
            for (int i = 0; i < model.getRowCount(); i++) {
                String last = this.map.get((String) model.getValueAt(i, 0));
                if (!last.equals(model.getValueAt(i, 1))) {
                    //change propertie
                    this.changePropt(
                            new Propertie(
                                    (String) model.getValueAt(i, 0),
                                    (String) model.getValueAt(i, 1)
                            )
                    );
                    change = true;
                }
            }
            if (this.action != null && change) {
                this.action.actionPerformed(new ActionEvent(this, 0, ""));
            }
            readPropts();
        } catch (Exception ex) {
        }
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        valuesChanged();
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
    }

    @Override
    public void mousePressed(MouseEvent evt) {
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
    }

    @Override
    public void mouseExited(MouseEvent evt) {
    }

    @Override
    public void keyTyped(KeyEvent evt) {
    }

    @Override
    public void keyPressed(KeyEvent evt) {

    }

    @Override
    public void keyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.valuesChanged();
        }
    }

}
