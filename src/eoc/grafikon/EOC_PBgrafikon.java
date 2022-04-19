/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.grafikon;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 *
 * @author rvanya
 */
public class EOC_PBgrafikon extends /* javax.swing.JPanel */ eoc.widgets.PObject {
  Object[][] rowHeaderTableData;
  Object[][] dimensionTableData;
  Object[] rowHeaderTableColumns;
  Object[] dimensionTableColumns;
  DefaultTableModel rowHeaderTableModel;
  DefaultTableModel dimensionTableModel;  
  DefaultTableColumnModel rowHeaderColumnModel;  
  DefaultTableColumnModel dimensionColumnModel;  
  JTableHeader rowHeaderTableHeader;
  JTableHeader dimensionTableHeader;
  
    public EOC_PBgrafikon(system.Kernel krn) {
        initComponents();
    }
  public void addDimensionHeaders(/*Object[][] yDim,*/ String[] sLabels) {
      dimensionTable.setAutoCreateColumnsFromModel(false);
      dimensionTableModel = (DefaultTableModel) dimensionTable.getModel();
      dimensionColumnModel = (DefaultTableColumnModel) dimensionTable.getColumnModel();
      dimensionTableHeader = dimensionTable.getTableHeader();
        // mazanie default stlpcov z tabulky (z TableModel-u)
        while (dimensionColumnModel.getColumnCount() > 0) {
            dimensionColumnModel.removeColumn(dimensionColumnModel.getColumn(0));
        }
        while (dimensionTable.getColumnCount() > 0) {
            dimensionTable.removeColumn(dimensionTable.getColumn(0));
        }
        for (int i = 0; i < sLabels.length; i++) {
            TableColumn t = new TableColumn();
            t.setModelIndex(i);
            t.setHeaderValue(sLabels[i]);
            dimensionColumnModel.addColumn(t);
            dimensionTableModel.addColumn(t);
        }
       // mazanie udajov zabulky
       while (dimensionTableModel.getRowCount() > 0) {
           dimensionTableModel.removeRow(0);
       }
        dimensionTableHeader.setColumnModel(dimensionColumnModel);
        dimensionTable.revalidate();
  } // public void addDimensionHeaders(/*Object[][] yDim,*/ String[] sLabels) {

      public void addRowHeadHeaders(Object[][] yDim, String[] sLabels) {
        rowHeaderTable.setAutoCreateColumnsFromModel(false);
        rowHeaderColumnModel = (DefaultTableColumnModel) rowHeaderTable.getColumnModel();
        rowHeaderTableModel = (DefaultTableModel) rowHeaderTable.getModel();
        rowHeaderTableHeader = rowHeaderTable.getTableHeader();
        // mazanie default stlpcov z ColumnModel-u
        while (rowHeaderColumnModel.getColumnCount() > 0) {
            rowHeaderColumnModel.removeColumn(rowHeaderColumnModel.getColumn(0));
        }
        // mazanie default stlpcov z tabulky (z TableModel-u)
        while (rowHeaderTable.getColumnCount() > 0) {
            rowHeaderTable.removeColumn(rowHeaderTable.getColumn(0));
        }
 
        krn.OutPrintln("Createing " +  sLabels.length + " row headers for rows: " + yDim.length);
        rowHeaderColumnModel = (DefaultTableColumnModel) rowHeaderTable.getColumnModel();
        //rowHeaderTableHeader = rowHeaderTable.getTableHeader();
        // prvy stlpec je 
        for (int i = 0; i < sLabels.length; i++) {
 //           String s = sLabels[i];
 //           krn.OutPrintln("Cretovalbysomssss:" + s);
            TableColumn t = new TableColumn();
            t.setModelIndex(i);
            t.setHeaderValue(sLabels[i]);
            rowHeaderColumnModel.addColumn(t);
            rowHeaderTableModel.addColumn(t);
          ////  krn.OutPrintln("TBCNTXNW_" + i + " >> rowHeaderColumnModel.getColumnCount()==" + rowHeaderColumnModel.getColumnCount()
          ////  + " >> rowHeaderTableModel.getColumnCount()==" + rowHeaderTableModel.getColumnCount());
        }
/*        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setPreferredSize(new Dimension(300, 20));
        rowHeaderTableHeader.setDefaultRenderer(renderer);
*/        
        rowHeaderTableHeader.setColumnModel(rowHeaderColumnModel);
        rowHeaderTable.validate();
        rowHeaderTable.repaint();
        
        krn.OutPrintln("TBCNT_ALL=" + rowHeaderColumnModel.getColumnCount()
        + " in tblemodel:" + rowHeaderTableModel.getColumnCount()
        + " in tble:" + rowHeaderTable.getColumnCount()
        );
       // mazanie udajov zabulky
       while (rowHeaderTableModel.getRowCount() > 0) {
           rowHeaderTableModel.removeRow(0);
       }
       for (int y=0; y < yDim.length; y++) {
           Object[] o = yDim[y];
           //krn.OutPrintln("yDim ==" + o[0] + "," + o[1] + "," + o[2]);
           rowHeaderTableModel.addRow(o);
       }
       /*
       */
       //rowHeaderTable.set
       //rowHeaderTable.setColumnModel(rowHeaderColumnModel);
       //rowHeaderTable.setModel(rowHeaderTableModel);
      //rowHeaderTable.setSize(300,300);
      //rowHeaderViewport.setPreferredSize(rowHeaderTable.getPreferredSize());
       //rowHeaderTable.setPreferredSize(new Dimension(225,30));
       krn.OutPrintln("rowHeaderTable.getPreferredSize() > " + rowHeaderTable.getPreferredSize());
      krn.OutPrintln("rFINALLYrowHeaderTableModel.getRowCount()==" + rowHeaderTableModel.getRowCount());
      ///  rowHeaderTable. .validate();
        ///rowHeaderTableModel.addColumn(myObjectID);
        /*for(int i = 1; i<=sLabels.length; i++) {
            TableColumn t = new TableColumn(sLabels[i]);
            tbm.addColumn(t);
        }*/
      //  headerTable.setModel(myTableColumnModel);
/*        
            for (int i = 0;i < yDim.length;i++) {
                Object[] oDim = yDim[i];
                for (int ii = 0;ii < oDim.length;ii++) {
                    System.out.print(oDim[ii] + " ");
                }
                krn.OutPrintln("");
            }
*/
  } //  public void addRowHeadHeaders(Object[][] yDim, String[] sLabels) {

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rowHeaderScrollPane = new javax.swing.JScrollPane();
        rowHeaderTable = new javax.swing.JTable();
        dimensionScrollPane = new javax.swing.JScrollPane();
        dimensionTable = new javax.swing.JTable();
        headerPanel = new eoc.grafikon.Pnl_GrafikonHeader();

        rowHeaderScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rowHeaderScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        rowHeaderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        rowHeaderTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        rowHeaderTable.setAutoscrolls(false);
        rowHeaderScrollPane.setViewportView(rowHeaderTable);

        dimensionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        dimensionTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        dimensionScrollPane.setViewportView(dimensionTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rowHeaderScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(dimensionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE))
                    .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dimensionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(rowHeaderScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane dimensionScrollPane;
    private javax.swing.JTable dimensionTable;
    private eoc.grafikon.Pnl_GrafikonHeader headerPanel;
    private javax.swing.JScrollPane rowHeaderScrollPane;
    private javax.swing.JTable rowHeaderTable;
    // End of variables declaration//GEN-END:variables
}
