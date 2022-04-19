/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.xinterface;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author rvanya
 */
public class XMetaDataEditor extends eoc.widgets.PObject {
     private String             metaScheme;
     private String             propertyDelimiter;
     private String             propertyCutter;
     private DefaultTableModel  metaTblModel;
     private CustomCellRenderer renderer;        
     private boolean bEditing   = false;
     private boolean bNewRecord = false;
    /**
     * Creates new form XMetaDataEditor
     */
     private class MyTableModel extends DefaultTableModel {

      @Override
      public boolean isCellEditable(int row, int column){  
          return bEditing;  
      }
     }
    public XMetaDataEditor() {
        initComponents();
        metaTblModel = new MyTableModel();
        metaTblModel.addColumn("Skratka");
        metaTblModel.addColumn("Popis");
        metaTblModel.addColumn("Hodnota");
        metaTable.setModel(metaTblModel);
        metaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        renderer = new CustomCellRenderer();
        metaTable.setDefaultRenderer(Object.class, renderer);
        metaTable.revalidate();
        metaTable.repaint();
    }
    
   private class CustomCellRenderer extends DefaultTableCellRenderer {

  /* (non-Javadoc)
   * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
   */
  public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column) {

   Component rendererComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
     row, column);

   //Set foreground color
   ////rendererComp.setForeground(Color.red);
   //Set background color
   Color clrProperty = Color.getHSBColor((float) 0.72,(float) 0.1,(float) 0.9);
   Color clrDisabled = Color.getHSBColor((float) 0.72,(float) 0.05,(float) 0.93);
   Color clrNotSet   = Color.getHSBColor((float) 0.15,(float) 0.5,(float) 0.9);
   if (column < 2) {
       if (table.getSelectedRow()==row)
           rendererComp.setBackground(table.getSelectionBackground());
       else 
           rendererComp.setBackground(clrProperty);
       rendererComp.setForeground(Color.black);
   }
   else {
       if (bEditing) {
           rendererComp.setBackground(Color.WHITE);
           if (value.toString().equals("< nezadane >")) 
               table.setValueAt("", row, column);
           if (bNewRecord)
               table.setValueAt("", row, column);
       }
       else {
           if (table.getSelectedRow()==row)
               rendererComp.setBackground(table.getSelectionBackground());
           else { 
               rendererComp.setBackground(
                   value.toString().equals("< nezadane >") ? clrNotSet : clrDisabled);
               rendererComp.setForeground(
                   value.toString().equals("< nezadane >") ? Color.blue : Color.black);
          }
       }
   }
   return rendererComp ;
  }
}  
    
public void setScheme(String scheme) {
    metaScheme = scheme;
    // priklad schemy:
    // #|typ_voz|Typ vozidla|character#spotreba|Spotreba|numeric#stav_tach|Stav tachometra|num#rocnik|Ročník|integer
    //metaTblModel = new DefaultTableModel();
    metaTblModel = (DefaultTableModel) metaTable.getModel();
    propertyDelimiter     = scheme.substring(0, 1).trim();
    propertyCutter        = scheme.substring(1, 2).trim();
    String properties     = scheme.substring(2).trim();
    String propertyList[] = properties.split(propertyDelimiter);
//System.out.println("DELIM:" + propertyDelimiter + " CUTTER:" + propertyCutterDelimiter
//                   + " PROPERTIES:" + properties);
    //metaTblModel.removeRow(WIDTH);
    for (String prop : propertyList) {
        
//        System.out.println("PROPERTYY:" + prop);
        String cuttedProp[] = prop.split(propertyCutter);
        if (cuttedProp.length < 2) break; // nema vlastnosti
        // clearing value
        cuttedProp[2] = ""; // TU BY MAL BYT OBJEKT PODLA TYPU
//        metaTblModel.addColumn("Skkratka");
        metaTblModel.addRow(cuttedProp);
//        for (String valju : cuttedProp) {
//            System.out.println("VALJUU:" + valju);
//        }
    }
    metaTable.setModel(metaTblModel);
}
    
public void setEditing(boolean bEdt, boolean bNew) {
    bEditing   = bEdt;
    bNewRecord = bNew;
    metaTable.repaint();
}

/**
 * Zápis meta-udajov do tabulky
 * @param mtdSch
 * @return 
 */
public boolean putMetaData(String mtData) {
    //System.out.println("puttiNNGG: " + mtData);
    if (mtData == null) { // mazanie hodnot metadat
        for (int row = 0; row < metaTblModel.getRowCount(); row++) {
            metaTblModel.setValueAt("", row, 2);
        }
        return true;  
    }
    propertyDelimiter     = mtData.substring(0, 1).trim();
    propertyCutter        = mtData.substring(1, 2).trim();
    String properties     = mtData.substring(2).trim();
    String propertyList[] = properties.split(propertyDelimiter);
    String propSkrat;
    String propValue;
    ////System.out.println("proopertyListIS:" + propertyList.length);
    prop_block:
    for (String prop : propertyList) {
        /////System.out.println("propBASMEKKKK: " + prop + "");
        String cuttedProp[] = prop.split(propertyCutter);
        propSkrat = cuttedProp[0];
        if (cuttedProp.length > 1)
            propValue = cuttedProp[1];
        else
            propValue = "< nezadane >";
        /////System.out.println("FINDIING:" + propSkrat + " FOR " + propValue);
        for (int row = 0; row < metaTblModel.getRowCount(); row++) {
            if (metaTblModel.getValueAt(row, 0).toString().equals(propSkrat)) {
                /////System.out.println("SETTTTING:" + propSkrat + " TO " + propValue);
                metaTblModel.setValueAt(propValue, row, 2);
                break;
            }
        }
    }
    
    return true;
}

/**
 * 
 * Vrátí metaDatatový reťazec,obsahujúci aktuálne údaje tabuľky
 * @return 
 */
public String getMetaData() {
    if (metaTable.getCellEditor() != null)
        metaTable.getCellEditor().stopCellEditing();
    if (metaTable.getSelectedRow() >= 0)
        metaTable.setRowSelectionInterval(metaTable.getSelectedRow(), metaTable.getSelectedRow());
    String mtDt = propertyDelimiter + propertyCutter; // prve dve znaky delimiterov
    for (int row = 0; row < metaTblModel.getRowCount(); row++) {
        //zapisuje sa len skratka polozky a hodnota
        mtDt = mtDt + metaTable.getValueAt(row, 0).toString();
        String s  = metaTable.getValueAt(row, 2).toString();
        if (s.equals("< nezadane >")) s = "";
        mtDt = mtDt + propertyCutter + s; // metaTable.getValueAt(row, 2).toString();
        if (row  <  metaTable.getRowCount() - 1) 
            mtDt = mtDt + propertyDelimiter;
    }
////    System.out.println("RERTMTD_mtDt:" + mtDt);
    return mtDt;
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        metaTable = new javax.swing.JTable();

        metaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Skratka", "Popis", "Hodnota"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        metaTable.setColumnSelectionAllowed(true);
        metaTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(metaTable);
        metaTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (metaTable.getColumnModel().getColumnCount() > 0) {
            metaTable.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable metaTable;
    // End of variables declaration//GEN-END:variables
}
