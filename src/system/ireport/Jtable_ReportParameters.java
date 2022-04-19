/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package system.ireport;

import eoc.DateMaskFormatter;
import eoc.EOC_date;
import eoc.widgets.DTvariable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Jtable_ReportParameters  extends JTable {
    Kernel krn;
    private Class editingClass;
    private final MyTableCellRenderer myTableCellRenderer;
    private final MyTableHeaderRenderer myTableHeaderRenderer;
    private final MyTableModel myTblModel;
    private final JTable myTable;
    DateCellEditor dateEditor;
    DateCellRenderer dateRenderer;
    DateMaskFormatter dmf;
    private DTvariable.DateSupporter dateSupporter;
    

    public Jtable_ReportParameters(Kernel kr) {
        super();
        myTable = this;
        krn = kr;
        //currDTvar = new DTvariable();
        myTableCellRenderer   = new MyTableCellRenderer();
        myTableHeaderRenderer = new MyTableHeaderRenderer();
        dmf = new DateMaskFormatter(krn); 
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myTblModel = new MyTableModel(); 
        myTblModel.addColumn("Parameter");
        myTblModel.addColumn("Hodnota");
        myTblModel.addColumn("Popis");
        setModel(myTblModel);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(myTableHeaderRenderer);
        dateEditor   = new DateCellEditor(krn);
        dateRenderer = new DateCellRenderer(krn);
        myTable.setDefaultEditor(eoc.EOC_date.class, dateEditor);
        myTable.setDefaultRenderer(eoc.EOC_date.class, dateRenderer);
        DTvariable dtv = new DTvariable();  
        dateSupporter = dtv.getDateSupporter();
        
        /*
        TableRowSorter sorter = new TableRowSorter(myModel);
        //sorter.
        setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        */

    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        editingClass = null;
        int modelColumn = convertColumnIndexToModel(column);
        if (modelColumn == 1) {
            Class rowClass = getModel().getValueAt(row, modelColumn).getClass();
            return getDefaultRenderer(rowClass);
        } 
        else {
            return myTableCellRenderer;
        }
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        editingClass = null;
        //int modelColumn = convertColumnIndexToModel(column);
        if (column == 1) {
            editingClass = getModel().getValueAt(row, column).getClass();
            return getDefaultEditor(editingClass);
        } else {
            return super.getCellEditor(row, column);
        }
    }


    @Override
    public boolean isCellEditable(int row, int col) {
           return (col == 1);
    }
    
    @Override
    public Class getColumnClass(int column) {

        Class cl;
        if (column == 1)
           cl = editingClass != null ? editingClass : super.getColumnClass(column);
        else 
           cl = String.class;
        System.out.println("RETT___COL____CLASSS: " + cl.getSimpleName());
        return cl;
    }

    public class MyTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus,int row,int col) {

            Component c = null;
            Class rowClass = getModel().getValueAt(row, col).getClass();
            
                if (col == 0) {
                    c = super.getTableCellRendererComponent
                               (table, value, isSelected, hasFocus, row, col);
                    if (row != table.getSelectedRow())
                        c.setBackground(new Color(212, 235, 255));
                    else
                        c.setBackground(table.getSelectionBackground());
                    c.setFont(c.getFont().deriveFont(Font.BOLD, 14));
                    if (c instanceof JLabel) {
                        Border border = BorderFactory.createBevelBorder(0);
                        ((JLabel) c).setBorder(border);
                    }
                }
                if (col == 2) {
                    c = super.getTableCellRendererComponent
                               (table, value, isSelected, hasFocus, row, col);
                    if (row != table.getSelectedRow())
                        c.setBackground(new Color(212, 235, 255));
                    else
                        c.setBackground(table.getSelectionBackground());
                    c.setFont(c.getFont().deriveFont(Font.BOLD, 14));
                }

                if (col == 1) {
                    if ((rowClass == GregorianCalendar.class)
                         || (rowClass == Date.class)
                         || (rowClass == EOC_date.class)) {
                        c = dateRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);            
                    }
                    else {
                            c = super.getTableCellRendererComponent
                                       (table, value, isSelected, hasFocus, row, col);
                    }                    
                        if (row != table.getSelectedRow())
                            c.setBackground(new Color(212, 235, 255));
                        else
                            c.setBackground(table.getSelectionBackground());
                }
               return c;
           }
    }

   // CLASSES
    public class MyTableHeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus,int row,int col) {

            Component c = super.getTableCellRendererComponent
                               (table, value, isSelected, hasFocus, row, col);
            
            c.setBackground(new Color(152, 175, 199));
            c.setFont(c.getFont().deriveFont(Font.BOLD, 16));
            if (c instanceof JLabel) {
                Border border = BorderFactory.createBevelBorder(0);
                ((JLabel) c).setBorder(border);
            }
            return c;
           }
    }

    class DateCellRenderer extends DefaultTableCellRenderer {
        Kernel krn;
        //DTvariable currTF;
        public DateCellRenderer(Kernel kr) {
            krn = kr;
        }

        @Override
       public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

               //DTvariable tf = new DTvariable();  
              // currTF = tf;
           /*
               String myValue = value.toString();
               // currTF.setValue(myValue);
               // currTF.setGenericDataType("Date");
               
               DTvariable currDTvar = new DTvariable();
               currDTvar.setValue(myValue);
               currDTvar.setGenericDataType("Date");
               */
            Component c = new JFormattedTextField();

                    ; //super.getTableCellRendererComponent
                               //(table, value, isSelected, hasFocus, row, col);

               
               
            //   System.out.println(">>>>>> DAAATERNDRFOR: " + value.toString());
            //   DateMaskFormatter dmf1 = new DateMaskFormatter(krn);
            //   DateMaskFormatter dmf2 = new DateMaskFormatter(krn);
            //   DateMaskFormatter dmf3 = new DateMaskFormatter(krn);
               
              // myValue
               ((JFormattedTextField) c).setValue(value);
               DefaultFormatterFactory aff = new DefaultFormatterFactory(dmf,dmf,dmf);
               ((JFormattedTextField) c).setFormatterFactory(aff);
               ((JFormattedTextField) c).setEditable(false);
                    if (row != table.getSelectedRow())
                        ((JFormattedTextField) c).setBackground(Color.WHITE);
                    else
                        ((JFormattedTextField) c).setBackground(table.getSelectionBackground());
               c.setFont(c.getFont().deriveFont(Font.PLAIN));
               /*
               if (!hasFocus)   
                   currDTvar.setBackground(Color.GREEN);
               else 
                   currDTvar.setBackground(Color.YELLOW);
               */
               return c;
        }
    }

    
    class DateCellEditor extends DefaultCellEditor {
        boolean editListenerAdded = false;       
        Kernel krn;
        //DTvariable currDTvar;
        JFormattedTextField dtv;
        int editedRow;
        // EOC_date myValue;
        // String myValueStr;
        public DateCellEditor(Kernel kr) {
            super(new JFormattedTextField());
            krn = kr;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//               DTvariable dtv = new DTvariable();  
                 dtv = new JFormattedTextField();  
            dtv.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    JFormattedTextField myObjID = ((JFormattedTextField) e.getComponent());
                    if (e.getKeyCode() == 32) {
                       
//                       System.out.println("KYKODE: " + e.getKeyCode() + " atPOSSS:" + myObjID.getCaret().getDot());
                       int dotPos = myObjID.getCaret().getDot();
                       if (dotPos == 0 || dotPos == 1) {
                          String s = dateSupporter.getDayStr();
                           try {                                  
                               myObjID.getDocument().insertString(0,s, null);
                               myObjID.getCaret().setDot(3);
                           } catch (BadLocationException ex) {
                               Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                       if (dotPos == 3 || dotPos == 4) {
                          String s = dateSupporter.getMonthStr();
                           try {                                  
                               myObjID.getDocument().insertString(3,s, null);
                               myObjID.getCaret().setDot(6);
                           } catch (BadLocationException ex) {
                               Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                       if (dotPos >= 6) {
                          String s = dateSupporter.getYearStr();
                           try {                                  
                               myObjID.getDocument().insertString(6,s, null);
                               myObjID.getCaret().setDot(10);
                           } catch (BadLocationException ex) {
                               Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                    }
                    else {
                        /*
                        try { 
                            Kernel.staticMsg("AAA");
                            myObjID.getDocument().insertString(myObjID.getCaret().getDot(), String.valueOf(e.getKeyChar()), null);
                            Kernel.staticMsg("BBB");
                        } catch (BadLocationException ex) {
                            Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        */
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
  //             currDTvar = dtv;
//               currDTvar = new DTvariable();
//               currDTvar.setGenericDataType("Date");
           // Component c = new JFormattedTextField(); //super.getTableCellRendererComponent

               editedRow = row;
               //EOC_date myValue = (EOC_date) value;
               //String myValueStr = value.toString();
          //     System.out.println("VALJU_getTableCellEditorComponent::::" + myValueStr);
//               DateMaskFormatter dmf1 = new DateMaskFormatter(krn);
//               DateMaskFormatter dmf2 = new DateMaskFormatter(krn);
//               DateMaskFormatter dmf3 = new DateMaskFormatter(krn);
               
               dtv.setValue((EOC_date) value);
               //System.out.println("VALJU_getTableCellEditorComponent:1::" + currDTvar.getText());
               DefaultFormatterFactory aff = new DefaultFormatterFactory(dmf,dmf,dmf);
               dtv.setFormatterFactory(aff);
               dtv.setEditable(true);
               dtv.setFont(dtv.getFont().deriveFont(Font.ITALIC));
               //currDTvar.setBackground(Color.ORANGE);
               ////System.out.println("VALJU_getTableCellEditorComponent:2::" + currDTvar.getText());
               return dtv;
        }

        @Override
        public Object getCellEditorValue() {
            return dtv.getValue();
        }

        @Override
        public boolean isCellEditable(EventObject e) {

////       if (super.isCellEditable(e)) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) e;
                return me.getClickCount() >= 2;
            }
            if (e instanceof KeyEvent) {
                KeyEvent ke = (KeyEvent) e;
                return ke.getKeyCode() == KeyEvent.VK_F2;
            }
////        }
        return false;            
            ////return true;
            /////throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true; // mozes
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean stopCellEditing() {
            //System.out.println("STOPCELEDITT: " + currDTvar.getText() + " VALL:" + currDTvar.getValue().toString());
//              currDTvar.setValue(currDTvar.getText());
              eoc.EOC_date edt = (eoc.EOC_date) myTblModel.getValueAt(editedRow, 1);
              edt.setValue(dtv.getText());
            System.out.println("\nSTOPCELEDITT: " + dtv.getText() + " VALL:" + dtv.getValue().toString()
                           + "\n  AT POSITON: " + editedRow /* myTable.getSelectedRow() */ + "/" + 1 + " EDTT:" + edt.toString() + "\n\n");
              myTblModel.setValueAt(edt, editedRow /* myTable.getSelectedRow() */, 1);
             // dtv.setEditable(false);
              myTable.editingStopped(null);
              myTblModel.fireTableCellUpdated(editedRow, 1);
              return true;
        }

        @Override
        public void cancelCellEditing() {   
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            if (editListenerAdded) return;
            editListenerAdded = true;
            this.addCellEditorListener(l);
            return;
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            if (!editListenerAdded) return;
            editListenerAdded = false;
            if (l==null) return;
            this.removeCellEditorListener(l);
        }
    }

    class MyTableModel extends DefaultTableModel {
        MyTableModel(){
            super();
        }
    }
    


    
    
}
