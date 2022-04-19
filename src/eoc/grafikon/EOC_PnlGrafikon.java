/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.grafikon;

import eoc.database.DBconnection;
import system.Kernel;
import eoc.dbdata.DBtableColumn;
import eoc.dbdata.ColumnDefinition;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 *
 * @author rvanya
 */
public class EOC_PnlGrafikon extends eoc.widgets.PObject implements IEOC_graf_obj {
  ArrayList<EOC_graf> grafList;
  boolean bRendering = false;

    /**
     * Creates new form EOC_Pgraphicon
     */
    public EOC_PnlGrafikon() {
        initComponents();
    }
    
    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        setMyObjectID(this);
        String initialize = super.initialize(kr, cX);
        eOC_BrwGrafikon1.setKrn(kr);
        eOC_BrwGrafikon1.setConn(cX);
        eOC_BrwGrafikon1.setContainer(this);
        eOC_BrwGrafikon1.setGrafCombo(jCB_graf);
        eOC_BrwGrafikon1.setDimensionCombo(jCB_dimension);
        eOC_BrwGrafikon1.initialize(kr, cX);
        if (!initialize.equals("")) { return initialize; } 
        return "";
    } // initialize
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        eOC_BrwGrafikon1 = new eoc.grafikon.EOC_BrwGrafikon();
        jCB_graf = new javax.swing.JComboBox<String>();
        jCB_dimension = new javax.swing.JComboBox<String>();
        lblRendering = new javax.swing.JLabel();
        testPanel = new javax.swing.JPanel();
        jButton_udrzVytahov = new javax.swing.JButton();
        jButton_udrzUdrzbari = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton_udrzKonstPrvky = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        jCB_graf.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCB_graf.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCB_grafItemStateChanged(evt);
            }
        });
        jCB_graf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_grafActionPerformed(evt);
            }
        });

        jCB_dimension.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCB_dimension.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCB_dimensionItemStateChanged(evt);
            }
        });
        jCB_dimension.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_dimensionActionPerformed(evt);
            }
        });

        lblRendering.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/led_mdl_act.png"))); // NOI18N
        lblRendering.setText("jLabel1");

        jButton_udrzVytahov.setText("addGraf_udrzVytahov()");
        jButton_udrzVytahov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_udrzVytahovActionPerformed(evt);
            }
        });

        jButton_udrzUdrzbari.setText("addGraf_udrzUdrzbari()");
        jButton_udrzUdrzbari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_udrzUdrzbariActionPerformed(evt);
            }
        });

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton_udrzKonstPrvky.setText("addGraf_udrzKonstPrvky()");
        jButton_udrzKonstPrvky.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_udrzKonstPrvkyActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane5.setViewportView(jTextArea1);

        javax.swing.GroupLayout testPanelLayout = new javax.swing.GroupLayout(testPanel);
        testPanel.setLayout(testPanelLayout);
        testPanelLayout.setHorizontalGroup(
            testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton_udrzKonstPrvky, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_udrzUdrzbari, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_udrzVytahov, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5))
        );
        testPanelLayout.setVerticalGroup(
            testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, testPanelLayout.createSequentialGroup()
                .addGroup(testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(testPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton_udrzVytahov)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_udrzUdrzbari)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_udrzKonstPrvky)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(eOC_BrwGrafikon1, javax.swing.GroupLayout.DEFAULT_SIZE, 1029, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(testPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCB_graf, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCB_dimension, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRendering, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCB_dimension, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCB_graf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblRendering, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(eOC_BrwGrafikon1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(testPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
// co: "Col_1,Col_2,Col_n", from: "{owner.}tbl", where:"kluc=hodnota", order by:"<fraza>"
// prvy stlpec je spojovaci key/kluc na dalsiu datovu-dimenziu 
// (na obrazovke bude 'hidden')
// !!!!! bez id-klucov by to mohlo fungovat=>> 2D DB ukazat ako 3D DB
// t.j. vzdy vidim pochopitelny kluc, a viem co bude nasledovat na nizsej urovni.
    private void jButton_udrzVytahovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_udrzVytahovActionPerformed
        try {
            // vytvorenie riadkov hlavicky z databazy
            ResultSet rsSt = krn.SQLQ_getQueryResultSet(MyCn.getConn(), 
                  "select id,VCHOD,ULICA from VCHOD order by VCHOD");
            Object[][] ooHeaderColumnValues = krn.SQLQ_getQueryResultSetAsArray(MyCn.getConn(), 
                  "select id,VCHOD,ULICA from VCHOD order by VCHOD",false);
            ColumnDefinition[] hdrColDefs =
                krn.SQLQ_getResultSetAsColumnDefinitionArray(MyCn.getConn(), rsSt); 
            DBtableColumn[] hdrColumns 
                    = new DBtableColumn[hdrColDefs.length];
            for ( int i=0; i < hdrColDefs.length; i++ ) {
                hdrColumns[i] = hdrColDefs[i].transformToEOC_DBtableColumn();
            }
            /* Vytvorenie graf-objektu s identifikacnymi udajmi
             * a stlpcami udajov hlaviciek graf-riadkov
             *******************************************************/
            EOC_graf myGraf = new EOC_graf(krn, MyCn,
                    "udrzbaVytahov", // name
                    "Údržba výťahov", // label
                    "Zadelenie údržbárov k výťahom", // tooltip/popis
                    false, // isDimension
                    null, // parentGraf
                    hdrColumns,
                    ooHeaderColumnValues, 
                    "prac",
                    "id", // primaryKey 
                    true // bHiddenPrimaryKey
                    );
            if ((myGraf != null)) {
                if (eOC_BrwGrafikon1.addGraf(myGraf)) {
                // inicializacia noveho grafikonu
                 myGraf.initialize();
            }   
                /* grafikon automaticky prida vytvoreny objekt do grafList-u
                 * nacitanie a spracovanie noveho listu grafikonov
                 **********************************************************/
                grafList = eOC_BrwGrafikon1.getGrafList();
                int gfIdx  = 0;
                int idxCnt = -1;
                // pridanie novych grafikonov do vyberu (vymeni sa cely list)
                while (jCB_graf.getItemCount() > 0) {
                    jCB_graf.removeItemAt(jCB_graf.getItemCount() - 1);
                } 
                
                for (EOC_graf grf : grafList) {
                    bRendering = true;
                    jCB_graf.addItem(grf.getcGrafLabel());
                    idxCnt++;
                    if (grf == myGraf) gfIdx = idxCnt;
                }   
              
            // vytvorenie niekolkych pokusnych dimenzii pre pridavany graf
            for (int i = 1; i < 2; i++)
            { 
                EOC_Gdimension gdm = myGraf.createFakeDimension(i,"AAZadelenie údržbárovAA");
                myGraf.addDimension(gdm);
                gdm.setDimensionCombo(jCB_dimension);
                gdm.initialize(); 
                //krn.OutPrintln("adding_dim_ITEM: " + gdm.getcGrafLabel());
            }
            myGraf.selectDimension(0);
           
            myGraf.makeSelected();
            // pridany graf je vzdy posledny (aspon zatial :-))
            bRendering = false;
            jCB_graf.setSelectedIndex(gfIdx);
        };
        } catch (SQLException ex) {
            bRendering = false;
            Logger.getLogger(EOC_PnlGrafikon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton_udrzVytahovActionPerformed

    private void jButton_udrzKonstPrvkyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_udrzKonstPrvkyActionPerformed
        try {
            // vytvorenie riadkov hlavicky z databazy
            ResultSet rsSt = krn.SQLQ_getQueryResultSet(MyCn.getConn(), 
                  "select id,VCHOD,ULICA from VCHOD order by VCHOD");
            Object[][] ooHeaderColumnValues = krn.SQLQ_getQueryResultSetAsArray(MyCn.getConn(), 
                  "select id,VCHOD,ULICA from VCHOD order by VCHOD",false);
            ColumnDefinition[] hdrColDefs =
                krn.SQLQ_getResultSetAsColumnDefinitionArray(MyCn.getConn(), rsSt); 
            DBtableColumn[] hdrColumns 
                    = new DBtableColumn[hdrColDefs.length];
            for ( int i=0; i < hdrColDefs.length; i++ ) {
                hdrColumns[i] = hdrColDefs[i].transformToEOC_DBtableColumn();
            }
            /* Vytvorenie graf-objektu s identifikacnymi udajmi
             * a stlpcami udajov hlaviciek graf-riadkov
             *******************************************************/
            EOC_graf myGraf = new EOC_graf(krn, MyCn,
                    "konstrukcnePrvky", // name
                    "Údržba konštrukčných prvkov", // label
                    "Plánovanie údržby konštrukčných prvkov", // tooltip/popis
                    false, // isDimension
                    null, // parentGraf
                    hdrColumns,
                    ooHeaderColumnValues, 
                    "prac",
                    "id", // primaryKey 
                    true // bHiddenPrimaryKey
                    );
            if ((myGraf != null)) {
                if (eOC_BrwGrafikon1.addGraf(myGraf)) {
                // inicializacia noveho grafikonu
                 myGraf.initialize();
            }   
                /* grafikon automaticky prida vytvoreny objekt do grafList-u
                 * nacitanie a spracovanie noveho listu grafikonov
                 **********************************************************/
                grafList = eOC_BrwGrafikon1.getGrafList();
                int gfIdx  = 0;
                int idxCnt = -1;
                // pridanie novych grafikonov do vyberu (vymeni sa cely list)
                while (jCB_graf.getItemCount() > 0) {
                    jCB_graf.removeItemAt(jCB_graf.getItemCount() - 1);
                } 
                
                for (EOC_graf grf : grafList) {
                    bRendering = true;
                    jCB_graf.addItem(grf.getcGrafLabel());
                    idxCnt++;
                    if (grf == myGraf) gfIdx = idxCnt;
                }   
                
            // vytvorenie niekolkych pokusnych dimenzii pre pridavany graf
            for (int i = 1; i < 50; i++)
            { 
                EOC_Gdimension gdm = myGraf.createFakeDimension(i,"AAZadelenie údržbárovAA");
                myGraf.addDimension(gdm);
                gdm.setDimensionCombo(jCB_dimension);
                gdm.initialize( /* krn, MyCn */ ); 
                //krn.OutPrintln("adding_dim_ITEM: " + gdm.getcGrafLabel());
            }
            myGraf.selectDimension(0);
            myGraf.makeSelected();
            // pridany graf je vzdy posledny (aspon zatial :-))
            bRendering = false;
            jCB_graf.setSelectedIndex(gfIdx);
        };
        } catch (SQLException ex) {
            bRendering = false;
            Logger.getLogger(EOC_PnlGrafikon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton_udrzKonstPrvkyActionPerformed

    private void jButton_udrzUdrzbariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_udrzUdrzbariActionPerformed
        try {
            bRendering = true;
            // vytvorenie riadkov hlavicky z databazy
            ResultSet rsSt = krn.SQLQ_getQueryResultSet(MyCn.getConn(), 
                  "select id, menopr, priezpr from prac where ubf = 1 and visible = 1 order by priezpr, menopr");
            Object[][] ooHeaderColumnValues = krn.SQLQ_getQueryResultSetAsArray(MyCn.getConn(), 
                  "select id, menopr, priezpr from prac where ubf = 1 and visible = 1 order by priezpr, menopr",false);
            ColumnDefinition[] hdrColDefs =
                krn.SQLQ_getResultSetAsColumnDefinitionArray(MyCn.getConn(), rsSt); 
            DBtableColumn[] hdrColumns 
                    = new DBtableColumn[hdrColDefs.length];
            for ( int i=0; i < hdrColDefs.length; i++ ) {
                hdrColumns[i] = hdrColDefs[i].transformToEOC_DBtableColumn();
            }
            /* Vytvorenie graf-objektu s identifikacnymi udajmi
             * a stlpcami udajov hlaviciek graf-riadkov
             *******************************************************/
            EOC_graf myGraf = new EOC_graf(krn, MyCn,
                    "udrzbaVytahari", // name
                    "Zadelenie údržbárov", // label
                    "Zadelovanie údržbárov k výťahom", // tooltip/popis
                    false, // isDimension
                    null, // parentGraf
                    hdrColumns,
                    ooHeaderColumnValues, 
                    "prac",
                    "id", // primaryKey 
                    true // bHiddenPrimaryKey
                    );
            if ((myGraf != null)) {
                if (eOC_BrwGrafikon1.addGraf(myGraf)) {
                // inicializacia noveho grafikonu
                 myGraf.initialize();
            }
                
                //totto problem bude tu
                /* grafikon automaticky prida vytvoreny objekt do grafList-u
                 * nacitanie a spracovanie noveho listu grafikonov
                 **********************************************************/
                grafList = eOC_BrwGrafikon1.getGrafList();
                int gfIdx  = 0;
                int idxCnt = -1;
                // pridanie novych grafikonov do vyberu (vymeni sa cely list)
                while (jCB_graf.getItemCount() > 0) {
                    jCB_graf.removeItemAt(jCB_graf.getItemCount() - 1);
                } 
                
                for (EOC_graf grf : grafList) {
                    bRendering = true;
                    jCB_graf.addItem(grf.getcGrafLabel());
                    idxCnt++;
                    if (grf == myGraf) gfIdx = idxCnt;
                }   
//      dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
//      numdecimals^formatstring^tooltyp^HIDDEN
            // eoc.dbdata.DBtableColumn keyCol = new DBtableColumn(krn,MyCn,
            //    "^^id^integer^null^Id^5^0^#######^Interný identifikátor riadku^true");
            eoc.dbdata.DBtableColumn keyCol = 
                eOC_BrwGrafikon1.currentGraf.getPrimaryKeyColumn();                    
            
            eoc.dbdata.DBtableColumn[] dbc = myGraf.getTimeDimensionColumns(2016, 2, keyCol);
            for (int e = 0; e < dbc.length; e++) {
                dbc[e].setCellRenderer(new DimensionCellRenderer());;
            }
            Object oData[][] = null;
            // vytvorenie niekolkych pokusnych dimenzii pre pridavany graf
            for (int i = 1; i < 5; i++)
            { 
                EOC_Gdimension gdm = 
// do 2015-6-8  myGraf.createFakeDimension(i,"AAZadelenie údržbárovAA");
                    new EOC_Gdimension(krn, MyCn,
                       "planovacPracUkonov","Plánovanie pracovných úkonov",
                       "Pridelovanie pracovných úkolov jednotlivým údržbárom",
                       true /* isDimension */, myGraf, dbc, oData, "prac",
                       "id", true /* hiddenKey */);
                myGraf.addDimension(gdm);
                gdm.setDimensionCombo(jCB_dimension);
                gdm.initialize( /* krn, MyCn */ ); 
                gdm.createFakeRows();
                //krn.OutPrintln("adding_dim_ITEM: " + gdm.getcGrafLabel());
            }
//            myGraf.selectDimension(0);
            myGraf.selectDimension("planovacPracUkonov");
            myGraf.makeSelected();
            // pridany graf je vzdy posledny (aspon zatial :-))
            bRendering = false;
            jCB_graf.setSelectedIndex(gfIdx);
        };
        } catch (SQLException ex) {
            bRendering = false;
            Logger.getLogger(EOC_PnlGrafikon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
          Logger.getLogger(EOC_PnlGrafikon.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButton_udrzUdrzbariActionPerformed

    private void jCB_grafItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCB_grafItemStateChanged
        //krn.OutPrintln("jCB_grafItemStateChangedevt.getStateChange()>>> " + evt.getStateChange());
        if (evt.getStateChange() != 1) return;
        int selIdx;
        selIdx = jCB_graf.getSelectedIndex();
        if (selIdx < 0) 
            ((JComboBox) evt.getSource()).setSelectedIndex(0);
        else {
            eOC_BrwGrafikon1.selectGrafikon(selIdx);
        }
    }//GEN-LAST:event_jCB_grafItemStateChanged

    private void jCB_grafActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_grafActionPerformed
        // TODO add your handling code here:
        //krn.OutPrintln("jCB_grafActionPerformedevt.getStateChange()>>> " 
        //        + evt.getActionCommand() + " " + bRendering);
    }//GEN-LAST:event_jCB_grafActionPerformed

    private void jCB_dimensionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCB_dimensionItemStateChanged
        //krn.OutPrintln("jCB_dimensionItemStateChangedevt.getStateChange()>>> " + evt.getStateChange());
        if (evt.getStateChange() != 1) return;
        if (bRendering) return;
        int selIdx;
        selIdx = jCB_dimension.getSelectedIndex();
        if (selIdx < 0) 
            ((JComboBox) evt.getSource()).setSelectedIndex(0);
        else {
 ////////////           eOC_BrwGrafikon1.getSelectedGrafikon().makeSelected();
            eOC_BrwGrafikon1.getSelectedGrafikon().selectDimension(selIdx);
        }
    }//GEN-LAST:event_jCB_dimensionItemStateChanged

    private void jCB_dimensionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_dimensionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCB_dimensionActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private eoc.grafikon.EOC_BrwGrafikon eOC_BrwGrafikon1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_udrzKonstPrvky;
    private javax.swing.JButton jButton_udrzUdrzbari;
    private javax.swing.JButton jButton_udrzVytahov;
    private javax.swing.JComboBox<String> jCB_dimension;
    private javax.swing.JComboBox<String> jCB_graf;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblRendering;
    private javax.swing.JPanel testPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setRendering(boolean renderingState, String propagateMode,
                             Object caller, boolean ignoreCaller) {
        bRendering = renderingState;
        /////krn.OutPrintln("setRendering==" + bRendering);
        if (bRendering) {
            lblRendering.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/easys/res/img/led_mdl_err.png")));
        }
        else {
            lblRendering.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/easys/res/img/led_mdl_act.png")));
        }
        lblRendering.revalidate();

        // posiela signal iba graf-browseru, ked je populateMode DOWN alebo ALL
        if (ignoreCaller && ((Object) eOC_BrwGrafikon1 == caller)) return;

        if (propagateMode.equals("DOWN") || propagateMode.equals("ALL"))
           eOC_BrwGrafikon1.setRendering(renderingState, "DOWN", this, true); 
    }
    
    public void hideTestPanel() {
        testPanel.setVisible(false);
    }

    public void viewTestPanel() {
        testPanel.setVisible(true);
    }
}