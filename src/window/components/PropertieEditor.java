/*
 * Logic simlator
 * Author: Martin Krcma
 */
package window.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import logicSimulator.WorkSpaceObject;
import logicSimulator.common.Propertie;

/**
 *
 * @author Martin
 */
public class PropertieEditor extends JTable {

    /**
     *
     */
    private WorkSpaceObject object;

    /**
     * Event for on propertie change
     */
    private ActionListener action;

    /**
     * Event for propertie change
     *
     * @param action Action listener
     */
    public void onPropertieChange(ActionListener action) {
        this.action = action;
    }

    public PropertieEditor() {
        super();
        this.getTableHeader().setReorderingAllowed(false);
    }

    //list with editor for all components in cells of table
    private final HashMap<Integer, TableCellEditor> editors = new HashMap<>();

    private List<Propertie> lastData = new ArrayList<>();

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        int modelColumn = convertColumnIndexToModel(column);
        if (modelColumn == 1) {
            return (TableCellEditor) editors.get(row);
        } else {
            return super.getCellEditor(row, column);
        }
    }

    /**
     * Set WorkSpaceObject for editing
     *
     * @param obj WorkSpaceObject
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
     * @param prot Propertie
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
            repaint();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) this.getModel();

        //clear
        model.getDataVector().removeAllElements();
        this.editors.clear();
        this.lastData.clear();

        //read and display all properties
        Propertie[] propts = this.object.getProperties();
        if (propts == null) {
            repaint();
            return;
        }
        for (Propertie propt : propts) {
            if (propt != null) {
                this.lastData.add(propt);
                switch (propt.getType()) {
                    case COLOR:
                        //color type
                        Color c = new Color(Integer.parseInt(propt.getValueString()));
                        ColorBox c1 = new ColorBox(c);
                        c1.addActionListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), propt.getValueString()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c1));
                        break;
                    case LOGIC:
                        //true false type
                        JComboBox c2 = new JComboBox(new String[]{"True", "False"});
                        c2.addActionListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), propt.getValueString()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c2));
                        break;
                    case BITS:
                        //bit type (user choose how many bits have input of some component, ...)
                        Integer[] bits = new Integer[64];
                        for (int i = 1; i <= 64; i++) {
                            bits[i - 1] = i;
                        }
                        JComboBox c3 = new JComboBox(bits);
                        c3.addActionListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), c3.getItemAt(propt.getValueInt() - 1)});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c3));
                        break;
                    case FONT:
                        JTextField c4 = new JTextField(propt.getValueString());
                        c4.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mousePressed(MouseEvent e) {
                                try {
                                    String[] arr = c4.getText().replaceAll("\\s+", "").split(",");
                                    Font f = FontChooser.showDialog(null, new Font(arr[0], Font.PLAIN, Integer.parseInt(arr[1])));
                                    if (f != null) {
                                        c4.setText(f.getPSName() + ", " + f.getSize());
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        });
                        c4.addActionListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), c4.getText()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c4));
                        break;
                    default:
                        //unknown -> textfield
                        JTextField t = new JTextField(propt.getValueString());
                        t.addActionListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), t.getText()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(t));
                        break;
                }
            }
        }

        //cell renderer
        this.getColumnModel().getColumn(1).setCellRenderer(new TableRenderer());
        this.repaint();
    }

    /**
     * Invoke this after some value change
     */
    private void valuesChanged() {
        try {
            boolean change = false;

            DefaultTableModel model = (DefaultTableModel) this.getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                String name = model.getValueAt(row, 0).toString();
                String val = model.getValueAt(row, 1).toString();
                if (this.lastData.stream()
                        .anyMatch((p) -> (name.equals(p.getName()) && !p.getValueString().equals(val)))) {
                    //change propertie
                    this.changePropt(new Propertie((String) model.getValueAt(row, 0), val));
                    change = true;
                }
            }

            //if some propertie changed then read all propts, ...
            if (change) {
                if (this.action != null) {
                    this.action.actionPerformed(new ActionEvent(this, 0, ""));
                }
                //read all properite
                readPropts();
                //unselect all
                this.getSelectionModel().clearSelection();
            }
        } catch (Exception ex) {
        }
    }

    private class TableRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            if (column == 1) {
                TableCellEditor editor = editors.get(row);
                if (editor != null) {
                    Component c = editors.get(row).getTableCellEditorComponent(table, value, isSelected, row, column);
                    if (c != null) {
                        return c;
                    }
                }
            }
            return new JLabel(value.toString());
        }

    }

}
