/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system;

import eoc.IEOC_DB_driver;
import eoc.database.DBconnection;
import java.sql.Connection;
import java.util.Map;

/**
 *
 * @author rvanya
 */
public class Welcome_dialog_2DB extends eoc.widgets.DObject implements IWelcome {
    private static Connection myWorkCn;
    private static Connection myOLAPCn;
    private static Connection myWWWCn;
    private boolean bMode = true; // simple(true)/advanced(false)
   //private final java.awt.Frame parent;
    /**
     * Creates new form EaS_welcome_dial
     */
//    public Welcome_dialog_full(java.awt.Frame parnt, boolean modal, boolean bSimple) {
   public Welcome_dialog_2DB(java.awt.Frame parnt, boolean modal,
               boolean bSimple, Map<String, String>[] hmFrmValues) {
        //parent = parnt;
        super(parnt, modal);
        bMode = bSimple;
        initComponents();
        
       // krn.OutPrintln("");
////        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
////            krn.OutPrintln("STack elenet: " +  ste);
////        }
//        eaS_welcome_pnl1.setParentContainer(this);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        eaS_welcome_pnl1 = new system.Welcome_panel_full();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Vítajte v systéme EaSys - verzia V2 - Beta ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(eaS_welcome_pnl1, javax.swing.GroupLayout.DEFAULT_SIZE, 908, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(eaS_welcome_pnl1, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Welcome_dialog_2DB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Welcome_dialog_2DB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Welcome_dialog_2DB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Welcome_dialog_2DB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* vytvorenie a zobrazenie dialog-boxu */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                system.Welcome_dialog_2DB dialog = 
                    new system.Welcome_dialog_2DB(new javax.swing.JFrame(), true /* modal */, false /* simple/advanced */, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    @Override
    public String initialize(Kernel krnl, DBconnection cnX) {
        super.initialize(krnl, cnX); //To change body of generated methods, choose Tools | Templates.
        eaS_welcome_pnl1.setMode(bMode);
        eaS_welcome_pnl1.setParentContainer(this);
        eaS_welcome_pnl1.initialize(krnl, cnX);
        return "";
    }
    
    
    public String setKrn(Kernel krnl) {
        krn = krnl;
        eaS_welcome_pnl1.setKrn(krn);
        this.setTitle("Vítajte v systéme EaSys - verzia: " + krn.getAppVersion());

        return "";
        
    }
    public String gtt() {
        
        return "GTT";
    }
    public DBconnection[] getDBconnections() {
        DBconnection[] cnx = {null,null,null};
//        krn.OutPrintln("dial-CCCA");
        this.setVisible(true);
        cnx = eaS_welcome_pnl1.getDedicatedDBConnections();
//        krn.OutPrintln("EaS_welcome_dial-con1-isNull=" + (cnx[0]==null));
//        krn.OutPrintln("EaS_welcome_dial-con2-isNull=" + (cnx[1]==null));
//        krn.OutPrintln("dial-CCCB");
        return cnx;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private system.Welcome_panel_full eaS_welcome_pnl1;
    // End of variables declaration//GEN-END:variables

    @Override
    public Map<String, String>[] getFrameValues() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    public IEOC_DB_driver[] getDBdrivers() {
        IEOC_DB_driver[] dnx = {null,null,null};
//        krn.OutPrintln("dial-CCCA");
        this.setVisible(true);
        dnx = eaS_welcome_pnl1.getDedicatedDBdrivers(false /* autoCommit */);
//        krn.OutPrintln("EaS_welcome_dial-con1-isNull=" + (cnx[0]==null));
//        krn.OutPrintln("EaS_welcome_dial-con2-isNull=" + (cnx[1]==null));
//        krn.OutPrintln("dial-CCCB");
        return dnx;
    }
}