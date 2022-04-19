/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package easys.system.prog.uzivatelia;

//import easys.system.skupiny_uzivatelov.*;
import easys.*;
import eoc.database.DBconnection;
import java.sql.Connection;
import javax.swing.JTable;

/**
 *sQueryBase
 * @author rvanya
 */
public class DBT_eas_UserGrps extends eoc.xinterface.XTable {

    public DBT_eas_UserGrps() {
         initComponents();
    }
   @Override
   public String initialize(system.Kernel kr, DBconnection cX) {
      setMyObjectID(this);
      this.setConn(cX);
      this.setKrn(kr);
      this.setXTableType("DBTABLE");
      this.setMyTable(jTable1);
      this.setMyScrollPane(jScrollPane1);
      this.setsExternalTable("eas.eas_users");
      this.setsExternalKeyInExtTable("id_eas_users","INTEGER");
      this.setExternalKeyInMyTable("id_eas_users","INTEGER");
      this.setMasterTable("eas.eas_x_grpusr");
      this.setMasterKey("id_eas_x_grpusr","INTEGER");
      this.setDisplayedFields("id_eas_x_grpusr,c_group");
      this.setQueryBase("select * from eas.eas_x_grpusr");
//      this.setiNumFetchedRows(60);
      this.setiNumFetchedRows(-9999);
      super.initialize(kr, cX);
      this.setTableEditable(false);
      
      return "";
   }

   public JTable getTable(){
      return jTable1;
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
        jTable1 = new javax.swing.JTable();

        setMyScrollPane(jScrollPane1);
        setMyTable(jTable1);
        setPreferredSize(new java.awt.Dimension(300, 64));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 64));

        jTable1.setAutoCreateColumnsFromModel(false);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setDropMode(javax.swing.DropMode.ON);
        jTable1.setFillsViewportHeight(true);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
