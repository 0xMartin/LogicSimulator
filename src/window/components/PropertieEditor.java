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
package window.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
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
        super.getTableHeader().setReorderingAllowed(false);
        super.setCellSelectionEnabled(false);
        super.setRowHeight(30);
        super.getTableHeader().setDefaultRenderer(new PropertieEditor.Renderer());
        super.setDefaultRenderer(String.class, new PropertieEditor.Renderer());
    }

    private static class Renderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel l;
            if (value instanceof String) {
                l = new JLabel(value.toString());
            } else {
                l = (JLabel) value;
            }
            if (l != null) {
                l.setBorder(new EmptyBorder(0,10,0,0));
                l.setFont(new Font("tahoma", Font.BOLD, 12));
                l.setOpaque(true);
                l.setBackground(row % 2 == 0 ? new Color(220, 220, 220) : new Color(255, 255, 255));
                //l.setForeground(row % 2 == 0 ? new Color(255, 255, 255) : new Color(200, 200, 200));
                return l;
            } else {
                return new JLabel(" ");
            }
        }
    }

    //list with editor for all components in cells of table
    private final HashMap<Integer, TableCellEditor> editors = new HashMap<>();

    private final List<Propertie> lastData = new ArrayList<>();

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
        this.object = obj;
        readPropts();
    }

    /**
     * Change propertie of object
     *
     * @param prot Propertie
     */
    private void changePropt(Propertie prot) {
        if (this.object == null || prot == null) {
            return;
        }
        this.object.changePropertie(prot);
    }

    /**
     * Read and show all propts of object
     */
    private void readPropts() {
        DefaultTableModel model = (DefaultTableModel) this.getModel();
        model.getDataVector().removeAllElements();
        if (super.getCellEditor() != null) {
            super.getCellEditor().cancelCellEditing();
        }
        this.editors.clear();
        this.lastData.clear();

        if (this.object == null) {
            repaint();
            return;
        }

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
                        c1.setColorChangedListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), c1.getText()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c1));
                        break;
                    case LOGIC:
                        //true false type
                        JComboBox c2 = new JComboBox(new String[]{"True", "False"});
                        c2.setSelectedItem(propt.getValueString());
                        c2.addActionListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), propt.getValueString()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c2));
                        break;
                    case BITS:
                        //bit type (user choose how many bits have input of some component, ...)
                        Integer[] bits = new Integer[32];
                        for (int i = 1; i <= 32; i++) {
                            bits[i - 1] = i;
                        }
                        JComboBox c3 = new JComboBox(bits);
                        c3.setSelectedItem(propt.getValueInt());
                        c3.addActionListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), propt.getValueInt()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c3));
                        break;
                    case INPUTS:
                        //bit type (user choose how many bits have input of some component, ...)
                        Integer[] inputs = new Integer[16];
                        for (int i = 2; i <= 17; i++) {
                            inputs[i - 2] = i;
                        }
                        JComboBox c4 = new JComboBox(inputs);
                        c4.setSelectedItem(propt.getValueInt());
                        c4.addActionListener((ActionEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), propt.getValueInt()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c4));
                        break;
                    case FONT:
                        FontChooser c5 = new FontChooser(propt.getValueString(), true);
                        c5.addItemListener((ItemEvent e) -> {
                            this.valuesChanged();
                        });
                        model.addRow(new Object[]{propt.getName(), propt.getValueString()});
                        this.editors.put(this.getRowCount() - 1, new DefaultCellEditor(c5));
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
                String val = this.editors.get(row).getCellEditorValue().toString();
                for (Propertie p : this.lastData) {
                    if (name.equals(p.getName())) {
                        if (!p.getValueString().equals(val)) {
                            //change propertie in last and in object
                            p.setValue(val);
                            this.changePropt(new Propertie(name, val));
                            change = true;
                            break;
                        }
                    }
                }
            }

            //if some propertie changed then read all propts, ...
            if (change) {
                if (this.action != null) {
                    this.action.actionPerformed(new ActionEvent(this, 0, ""));
                    //read propts again
                    this.readPropts();
                }
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
