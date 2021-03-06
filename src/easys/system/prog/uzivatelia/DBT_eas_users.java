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
public class DBT_eas_users extends eoc.xinterface.XTable {

    public DBT_eas_users() {
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
      // Sybase
      //this.setMasterTable("osoby");
      //this.setMasterKey("id");
      //this.setMasterKeyDataType("INTEGER");
      //this.setDisplayedFields("id,meno,priez,rodmeno,dtnarod,rodcislo");
      //this.setQueryBase("select * from osoby");
       
      // Postgres
      
      //this.setsExternalTable("eas_usrgrp");
      // this.setsExternalKey("c_group","STRING");
      //this.setsExternalKeyInExtTable("c_group","INTEGER");
      //this.setExternalKeyInMyTable("c_group","INTEGER");
//      this.setMasterTable("eas.eas_users");
      this.setMasterTable("eas.eas_users");
      this.setMasterKey("id_eas_users","INTEGER");
      this.setDisplayedFields("id_eas_users,c_user^Užívateľské meno,"
              /* + "c_group^Skupina," toto by malo byt calculted fields */ 
              + "c_popis^Celé meno,c_poznamka^Poznámka");
      this.setQueryBase("select * from eas.eas_users");
//      this.setiNumFetchedRows(60);
      this.setiNumFetchedRows(-9999);
      super.initialize(kr, cX);
      
      return "";
   }
//jeb na to vanko, uz ti to nepali. toto by malo totiz bez problemov fungovat
   @Override
    public String getCurrentObjPerms() {
       if (getCurrentRowStatus().equals("noRowAvailable")) return "N";
       // OLD STYLE
       //String s = this.getValueBydbFieldName("c_user").toString();
       //System.out.println("SSSSS: " + s + " WRAPPA: " + krn.getWrapperAppName() 
       //+ " SYSSAD: " + this.getValueBydbFieldName("b_sysadmin").toString());
       //if (s.equals("EASYS") || s.toLowerCase().equals(krn.getWrapperAppName().toLowerCase())) {
       String s = this.getValueBydbFieldName("b_sysadmin").toString();
       if (Integer.parseInt(s) > 0) // sysadmin ucet sa nesmie menit (okrem pwd)
           return "NP";
       else {
           return "NUCDP";
       }
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
