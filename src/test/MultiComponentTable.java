/* (swing1.1beta3) */
//package jp.gr.java_conf.tame.swing.examples;
package test;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.EventObject;

/**
 * @version 1.0 11/09/98
 */

class ComboString {
  String str;
  ComboString(String str) {
    this.str = str;
  }
  public String toString() {
    return str;
  }
}

class MultiRenderer extends DefaultTableCellRenderer {
  JCheckBox checkBox = new JCheckBox();

  public Component getTableCellRendererComponent(
                     JTable table, Object value,
                     boolean isSelected, boolean hasFocus,
                     int row, int column) {
    if (value instanceof Boolean) {                    // Boolean
      checkBox.setSelected(((Boolean)value).booleanValue());
      checkBox.setHorizontalAlignment(JLabel.CENTER);
      return checkBox;
    }
    String str = (value == null) ? "" : value.toString();
    return super.getTableCellRendererComponent(
         table,str,isSelected,hasFocus,row,column);
  }
}

class MultiEditor implements TableCellEditor {
  private final static int      COMBO = 0;
  private final static int    BOOLEAN = 1;
  private final static int     STRING = 2;
  private final static int NUM_EDITOR = 3;
  DefaultCellEditor[] cellEditors;
  JComboBox<String> comboBox;
  int flg;

  public MultiEditor() {
    cellEditors = new DefaultCellEditor[NUM_EDITOR];
    comboBox = new JComboBox<String>();
    comboBox.addItem("true");
    comboBox.addItem("false");
    cellEditors[COMBO]   = new DefaultCellEditor(comboBox);
    JCheckBox checkBox   = new JCheckBox();
    //checkBox.setOpaque( true );
    checkBox.setHorizontalAlignment(JLabel.CENTER);
    cellEditors[BOOLEAN] = new DefaultCellEditor(checkBox);
    JTextField textField = new JTextField();
    cellEditors[STRING]  = new DefaultCellEditor(textField);
    flg = NUM_EDITOR;      // nobody
  }

  public Component getTableCellEditorComponent(JTable table, Object value,
              boolean isSelected, int row, int column) {
    if (value instanceof ComboString) {                       // ComboString
      flg = COMBO;
      String str = (value == null) ? "" : value.toString();
      return cellEditors[COMBO].getTableCellEditorComponent(
                       table, str,   isSelected, row, column);
    } else if (value instanceof Boolean) {                    // Boolean
      flg = BOOLEAN;
      return cellEditors[BOOLEAN].getTableCellEditorComponent(
                       table, value, isSelected, row, column);
    } else if (value instanceof String) {                     // String
      flg = STRING;
      return cellEditors[STRING].getTableCellEditorComponent(
                       table, value, isSelected, row, column);
    }
    return null;
  }

  public Object getCellEditorValue() {
    switch (flg) {
      case   COMBO:
        String str = (String)comboBox.getSelectedItem();
        return new ComboString(str);
      case BOOLEAN:
      case  STRING:
        return cellEditors[flg].getCellEditorValue();
      default:         return null;
    }
  }

  public Component getComponent() {
    return cellEditors[flg].getComponent();
  }
  public boolean stopCellEditing() {
    return cellEditors[flg].stopCellEditing();
  }
  public void cancelCellEditing() {
    cellEditors[flg].cancelCellEditing();
  }
  public boolean isCellEditable(EventObject anEvent) {
      return true;
    //return cellEditors[flg].isCellEditable(anEvent);
  }
  public boolean shouldSelectCell(EventObject anEvent) {
    return cellEditors[flg].shouldSelectCell(anEvent);
  }
  public void addCellEditorListener(CellEditorListener l) {
    cellEditors[flg].addCellEditorListener(l);
  }
  public void removeCellEditorListener(CellEditorListener l) {
    cellEditors[flg].removeCellEditorListener(l);
  }
  public void setClickCountToStart(int n) {
    cellEditors[flg].setClickCountToStart(n);
  }
  public int getClickCountToStart() {
    return cellEditors[flg].getClickCountToStart();
  }
}

public class MultiComponentTable extends JFrame {

  public MultiComponentTable(){
    super("MultiComponent Table");
    
    DefaultTableModel dm = new DefaultTableModel() {
      public boolean isCellEditable(int row, int column) {
        if (column == 0) {
          return true;
        }
        return false;
      }
    };
    dm.setDataVector(
      new Object[][]{
        {new ComboString("true") ,"ComboString","JLabel"   ,"JComboBox"},
        {new ComboString("false"),"ComboString","JLabel"   ,"JComboBox"},
        {new Boolean(true)       ,"Boolean"    ,"JCheckBox","JCheckBox"},
        {new Boolean(false)      ,"Boolean"    ,"JCheckBox","JCheckBox"},
        {"true"                  ,"String"     ,"JLabel"   ,"JTextField"},
        {"false"                 ,"String"     ,"JLabel"   ,"JTextField"}},
      new Object[]{"Component","Data","Renderer","Editor"});

    JTable table = new JTable(dm);
    table.getColumn("Component").setCellRenderer(
      new MultiRenderer());
    table.getColumn("Component").setCellEditor(
      new MultiEditor());

    JScrollPane scroll = new JScrollPane(table);
    getContentPane().add( scroll );
    setSize( 400, 160 );
    setVisible(true);
  }

  public static void main(String[] args) {
    MultiComponentTable frame = new MultiComponentTable();
    frame.addWindowListener( new WindowAdapter() {
      public void windowClosing( WindowEvent e ) {
        System.exit(0);
      }
    });
  }
}

