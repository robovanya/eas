/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package system.tools.uni_cis;

import eoc.database.DBconnection;
import system.Kernel;
import java.sql.Connection;

/**
 *
 * @author rvanya
 */
public class dbt_uni_cis extends eoc.xinterface.XTable {
     private String  sTyp_ciselnika;
     private Integer id_ciselnika;
    /**
     * Creates new form dbt_bytovy_fond_vcs
     */
    public dbt_uni_cis() {
        super();
        initComponents();
    }

       @Override
   public String initialize(Kernel kr, DBconnection cX ) {
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
      this.setMasterTable("eas_uni_cis");
      this.setMasterKey("id_eas_uni_cis","INTEGER");
      //this.setDisplayedFields("id_eas_usrgrp,c_group^Skupina,c_popis^Popis,c_private_data,c_poznamka");
      System.out.println(">>>>>>XXX.>>>>> KEJKUURVA:" + id_ciselnika);
//      this.setDisplayedFields("ID,c_skratka^ŠPZvozidla,c_naz_pol_cis^Typ vozidla,c_popis^Popis");
      this.setQueryBase("select * from eas_uni_cis");
      setAppWhere("id_eas_uni_cis_def = " + id_ciselnika);
      // this.setAppWhere("1=0"); // prazdne query
      // this.setAppWhere("id_eas_users < 50000");
      this.setiNumFetchedRows(-9999);
      super.initialize(kr,cX);
      this.setTableEditable(false);
      return "";
   }
/*
    public void set_uni_cis_Keys(String sUniCisTyp, Integer idUniCisDef) {
        typ_ciselnika = sUniCisTyp;
        id_ciselnika  = idUniCisDef;
      this.setAppWhere("id_eas_uni_cis_def = " + id_ciselnika);
      this.rebuildQuery();
    }
*/  
    public void set_uni_cis_Keys(String sUniCisTyp, Integer idUniCisDef) {
        sTyp_ciselnika = sUniCisTyp;
        id_ciselnika  = idUniCisDef;
        System.out.println("SETTING UNICISKEYS TO:" + sUniCisTyp + " " + idUniCisDef);
        String sql = "SELECT * FROM eas_uni_cis_def WHERE id_eas_uni_cis_def = " + id_ciselnika;
        Object[][] rsQry = krn.SQLQ_getQueryResultSetAsArray(MyCn.getConn(), sql, true);
        String skrLabel = "";
        String polLabel = "";
        String nazLabel = "";
        Object[] h = rsQry[0];
        Object[] d = rsQry[1];
        for (int i = 0; i < h.length; i++) {
            if (h[i].equals("c_skrat_pol_label")) skrLabel = d[i].toString();
            if (h[i].equals("c_naz_pol_label"))   polLabel = d[i].toString();
            if (h[i].equals("c_popis_pol_label")) nazLabel = d[i].toString();
////            for (int u = 0; u < o.length; u++) {
                //// System.out.print(o[u] + " ");
///            }
            // System.out.println();
        }
//      String dispFlds = "id_eas_uni_cis,c_skratka^" + skrLabel + 
      String dispFlds = "c_skratka^" + skrLabel + 
                        ",c_naz_pol_cis^" + polLabel +
                        ",c_popis^" + nazLabel;  
      //// System.out.println("@dispFlds_ISSSSS:" + dispFlds);
      setDisplayedFields(dispFlds);
      setAppWhere("id_eas_uni_cis_def = " + id_ciselnika);
       rebuildQuery();
       buildTblFromResultSetStandard();
       reopen_Query();
       myTable.revalidate();
       myTable.repaint();
    }

    @Override
    public String afterInitialize() {
      //// System.out.println("AFFTERRINITTI: " + id_ciselnika);
        super.afterInitialize(); //To change body of generated methods, choose Tools | Templates.
       rebuildQuery();
       buildTblFromResultSetStandard();
       reopen_Query();
       myTable.revalidate();
       myTable.repaint();
        return "";
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
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
