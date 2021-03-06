/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system;

import eoc.IEOC_DB_driver;
import eoc.database.DBconnection;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rvanya
 */
public class Welcome_dialog_simple extends eoc.widgets.DObject implements IWelcome {
    
    private static Connection myWorkCn;
    private static Connection myOLAPCn;
    private static Connection myWWWCn;
    private boolean bMode = true; // simple(true)/advanced(false)
    Map<String, String>[] hmFrameValues;
   //private final java.awt.Frame parent;
    /**
     * Creates new form EaS_welcome_dial
     */
    public Welcome_dialog_simple(java.awt.Frame parnt, boolean modal,
               boolean bSimple, Map<String, String>[] hmFrmValues) {
        //parent = parnt;
        super(parnt, modal);
        bMode = bSimple;
        hmFrameValues = hmFrmValues;
        initComponents();
//        krn.OutPrintln("");
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

        eaS_welcome_pnl1 = new system.Welcome_panel_simple();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Vítajte v systéme EaSys - verzia V2 - Beta ");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(eaS_welcome_pnl1, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(eaS_welcome_pnl1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        destroy();
    }//GEN-LAST:event_formWindowClosed

    /**
     * @param args the command line arguments
     */
    
// tu bol main()
    
    @Override
    public String initialize(Kernel krnl, DBconnection cnX) {
        super.initialize(krnl, cnX); //To change body of generated methods, choose Tools | Templates.
        eaS_welcome_pnl1.setMode(bMode);
        eaS_welcome_pnl1.setParentContainer(this);
        eaS_welcome_pnl1.initialize(krnl, cnX);
        eaS_welcome_pnl1.setReturnMap(hmFrameValues);
        return "";
    }
    
    
    public String setKrn(Kernel krnl) {
        krn = krnl;
        eaS_welcome_pnl1.setKrn(krn);
        this.setTitle("Vítajte v systéme EaSys - verzia: " + krn.getAppVersion());

        return "";
        
    }

    public DBconnection[] getDBconnections() {
        DBconnection[] cnx = {null,null,null};
//        krn.OutPrintln("dial-CCCA");
        this.setVisible(true);
        cnx = eaS_welcome_pnl1.getDedicatedDBConnections(true);
//        krn.OutPrintln("EaS_welcome_dial-con1-isNull=" + (cnx[0]==null));
//        krn.OutPrintln("EaS_welcome_dial-con2-isNull=" + (cnx[1]==null));
//        krn.OutPrintln("dial-CCCB");
        return cnx;
    }

    @Override
    public String destroy() {
//        System.out.println("PRCONTANERR: a" );
        super.setVisible(false);
        //this.dispose();
        
//        System.out.println("PRCONTANERR: B" );
        return "";
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private system.Welcome_panel_simple eaS_welcome_pnl1;
    // End of variables declaration//GEN-END:variables
/*
    @Override
    public Map<String, String>[] getFrameValues() {
        @SuppressWarnings("unchecked")
        Map<String, String>[] fvl = new Map[3];
        //fvl = {{null,null},{null,null},{null,null}};
        fvl = eaS_welcome_pnl1.getFrameValues();
        return fvl;
    }
    */

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
