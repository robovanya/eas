/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system.cron;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ToolTipManager;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.utils.ToolTipUtils;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_cron extends javax.swing.JPanel {

   public Kernel krn;    
   private int curLayer = 0;
   private Integer numUnreadedMsgs = 0;
   private BalloonTip lblMsgsBalTip;
   
   public Pnl_cron() {
      initComponents();
     // jLayeredPane1.add(new Pnl_values(), 0);
     // jLayeredPane1.add(new Pnl_msg(), 1);
      lblMsgsTxt.setVisible(false);
      lblMsgsBalTip = new BalloonTip(lblMsgs, "Hlásenie nových správv");
      lblMsgsBalTip.setVisible(false);
      ///// lblMsgs.addMouseMotionListener( new TipActivate() );
      //lblMsgsBalTip.setStyle(net.java.balloontip.styles.ModernBalloonStyle.class);
   }
   /****
   class TipActivate implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            mouseMoved(e);
            ///// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            ToolTipManager theManager = ToolTipManager.sharedInstance();
            MouseEvent toolTipEvent = new MouseEvent( lblMsgsBalTip, MouseEvent.MOUSE_ENTERED,
                    System.currentTimeMillis(), 0, 0, 0, 0, false );
            theManager.mouseMoved( toolTipEvent );
                   lblMsgsBalTip.setVisible(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Pnl_cron.class.getName()).log(Level.SEVERE, null, ex);
            }
                   lblMsgsBalTip.setVisible(false);
            System.out.println( "ActionEvent launched!" );
        }
    }
    * ****/

   

       public Kernel getKrn() {
        return krn;
    }

    public void setKrn(Kernel krn) {
        this.krn = krn;
    }
    

   public void setClock(String s) {
      lbl_crn_time.setText(s);
   }
   /**
    * This method is called from within the constructor to initialize the form. WARNING:
    * Do NOT modify this code. The content of this method is always regenerated by the
    * Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblMsgs = new javax.swing.JLabel();
        lblChats = new javax.swing.JLabel();
        lblMsgsTxt = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lbl_crn_time = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblMsgs.setBackground(new java.awt.Color(204, 255, 204));
        lblMsgs.setOpaque(true);

        lblChats.setBackground(new java.awt.Color(255, 255, 204));
        lblChats.setOpaque(true);

        lblMsgsTxt.setBackground(new java.awt.Color(214, 214, 255));
        lblMsgsTxt.setOpaque(true);

        jButton1.setText("*");
        jButton1.setToolTipText("Prepnutie medzi režimom počítadiel a zobrazovača správ");
        jButton1.setDefaultCapable(false);
        jButton1.setIconTextGap(0);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lbl_crn_time.setBackground(new java.awt.Color(204, 204, 255));
        lbl_crn_time.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbl_crn_time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_crn_time.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        lbl_crn_time.setMaximumSize(new java.awt.Dimension(52, 14));
        lbl_crn_time.setMinimumSize(new java.awt.Dimension(52, 14));
        lbl_crn_time.setOpaque(true);
        lbl_crn_time.setPreferredSize(new java.awt.Dimension(52, 14));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMsgsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblChats, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMsgs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_crn_time, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMsgsTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(lbl_crn_time, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblChats, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMsgs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3))
            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    curLayer = ((curLayer==0)?1:0);
    if (curLayer == 0) {
//       pnlValues.setVisible(false);
//       pnlMsg.setVisible(true);
        lblMsgsTxt.setVisible(true);
        lblChats.setVisible(false);
        lblMsgs.setVisible(false);
    }
    else {
//       pnlMsg.setVisible(false);
//       pnlValues.setVisible(true);
        lblMsgsTxt.setVisible(false);
        lblChats.setVisible(true);
        lblMsgs.setVisible(true);
    }
    
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed
    
    public boolean addNewMessages(Integer numMsgs) {
       numUnreadedMsgs = numUnreadedMsgs + numMsgs; 
       lblMsgs.setText(numUnreadedMsgs.toString());
       String ttx = "Nové správy: " + numMsgs;
       lblMsgs.setToolTipText(ttx);
       lblMsgsBalTip.setTextContents(ttx);
       lblMsgs.setToolTipText(ttx);
       ToolTipUtils.toolTipToBalloon(lblMsgsBalTip);
       lblMsgsBalTip = new BalloonTip(lblMsgs, ttx);
       //lblMsgsBalTip.setCloseButton(null);
      // ToolTipUtils.balloonToToolTip(lblMsgsBalTip, 500, 500);
       lblMsgsBalTip.setVisible(true);
       try {
           Thread.sleep(1000);
       } catch (InterruptedException ex) {
           Logger.getLogger(Pnl_cron.class.getName()).log(Level.SEVERE, null, ex);
           return false;
       }
       lblMsgsBalTip.setVisible(false);
       //lblMsgs.dispatchEvent(new MouseEvent());
    //   lblMsgsBalTip.
     //  MouseEvent evt;
      // evt = new MouseEvent();
      // lblMsgs.dispatchEvent(null);
/*       
MouseEvent me = new MouseEvent(lblMsgs, 0, System.currentTimeMillis(), MouseEvent.MOUSE_DRAGGED, lblMsgs.getX() + 1, lblMsgs.getY() + 1, 0, false);
for(MouseMotionListener ml: lblMsgs.getMouseMotionListeners()){
    ml.mouseDragged(me);
}
*/
       
      // lblMsgsBalTip.setVisible(false);
       return true;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel lblChats;
    private javax.swing.JLabel lblMsgs;
    private javax.swing.JLabel lblMsgsTxt;
    private javax.swing.JLabel lbl_crn_time;
    // End of variables declaration//GEN-END:variables
}
