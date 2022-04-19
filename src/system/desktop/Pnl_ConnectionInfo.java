package system.desktop;

import eoc.database.DBconnection;
import system.Kernel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

public class Pnl_ConnectionInfo extends javax.swing.JPanel {

    public Kernel krn;    
    
    public Pnl_ConnectionInfo() {
        initComponents();
        String[] E;
        E = new String[] {"a","b","c"} ;
        DefaultComboBoxModel<String> dfm;
        dfm = new DefaultComboBoxModel<>(E);
        cb_ConnectionInfo.setModel(dfm);

    }
    
    public Kernel getKrn() {
        return krn;
    }

    public void setKrn(Kernel krn) {
        this.krn = krn;
        try {
            evtDBconnectionsChanged();
        } catch (SQLException ex) {
            Logger.getLogger(Pnl_ConnectionInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton4 = new javax.swing.JButton();
        cb_ConnectionInfo = new javax.swing.JComboBox<String>();

        jButton4.setText("jButton4");

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cb_ConnectionInfo.setPrototypeDisplayValue("");
        cb_ConnectionInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_ConnectionInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cb_ConnectionInfo, 0, 301, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cb_ConnectionInfo)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cb_ConnectionInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_ConnectionInfoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_ConnectionInfoActionPerformed

    public void evtDBconnectionsChanged() throws SQLException {
       cb_ConnectionInfo.removeAllItems();
       //cb_ConnectionInfo.getModel().
       DBconnection dbc;
       dbc = krn.getDBconn("Work");
       if (dbc!=null)
           cb_ConnectionInfo.addItem((String) "Work: " + dbc.getUsr() + " - " +dbc.getDatabaseName());
       else 
           cb_ConnectionInfo.addItem((String) "Work: <none>");
       dbc = krn.getDBconn("OLAP");
       if (dbc!=null)
           cb_ConnectionInfo.addItem((String) "OLAP: " + dbc.getUsr() + " - " +dbc.getDatabaseName());
       dbc = krn.getDBconn("WWW");
       if (dbc!=null)
           cb_ConnectionInfo.addItem((String) "WWW:  " + dbc.getUsr() + " - " +dbc.getDatabaseName());
    }

    
    public void setText(String t){
        cb_ConnectionInfo.removeAllItems();
        cb_ConnectionInfo.addItem(t);
        //txt_ConnectionInfo.setText(t);
    }
    
    public String getText(){
        String t = "";
        for (int i = 0; i < cb_ConnectionInfo.getItemCount(); i++) {
            t = t + "#" + cb_ConnectionInfo.getItemAt(i);
        }
        if (t.startsWith("#")) t = t.substring(1); // odrezanie prveho znaku #
        return t;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cb_ConnectionInfo;
    private javax.swing.JButton jButton4;
    // End of variables declaration//GEN-END:variables
}
