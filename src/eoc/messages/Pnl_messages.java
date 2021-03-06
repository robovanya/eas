/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.messages;

import eoc.IEOC_Pnl_messages;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import system.cron.Crond;

/**
 *
 * @author rvanya
 */
public class Pnl_messages extends javax.swing.JPanel implements IEOC_Pnl_messages {
//    ArrayList<eoc.messages.Pnl_message> EOC_messages = new ArrayList<>();
    private Dimension dSplitDim;
    private boolean bMinimized = false;
    private int lastDividerLocation;
    private JSplitPane mySplitPnl;
    ////private ArrayList<Crond.MessageRecord> alMessageRecords; // Eoc_message,Pnl_message,.....
    private MessageContainer msgContainer;
   /**
    * Creates new form EOC_Pnl_messages
    */
    
   public Pnl_messages() {
      initComponents();
   }
   
   /**
    * This method is called from within the constructor to initialize the form. WARNING:
    * Do NOT modify this code. The content of this method is always regenerated by the
    * Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 255, 204));
        setAutoscrolls(true);

        jScrollPane1.setBackground(new java.awt.Color(255, 102, 204));
        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 544, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/RIGHT_U.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (!bMinimized) {
            mySplitPnl = (JSplitPane) this.getParent();
            dSplitDim = mySplitPnl.getSize();
            //JSplitPane myMasterSplitPnl = (JSplitPane) mySplitPnl.getParent();
            lastDividerLocation = mySplitPnl.getDividerLocation();
            mySplitPnl.setDividerLocation(dSplitDim.width - 80);
            mySplitPnl.repaint();
            mySplitPnl.revalidate();
            //myMasterSplitPnl.repaint();
            //myMasterSplitPnl.revalidate();
            bMinimized = true;
            //jButton1.setText("<");
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img//LEFT_U.png"))); // NOI18N
        }
        else {
            mySplitPnl.setDividerLocation(lastDividerLocation);
//            jButton1.setText(">");
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img//RIGHT_U.png"))); // NOI18N
            bMinimized = false;
            
        }
        //JOptionPane.showMessageDialog(null,  this.getParent().toString() + "\n\n" 
        //        + this.getParent().getSize().toString() + "\n\n"
        //        + mySplitPnl.getDividerLocation());
    }//GEN-LAST:event_jButton1ActionPerformed

    @Override
   public void AddNewMessage(eoc.messages.Pnl_message oMsg) {
        jPanel1.setLayout(new BoxLayout(jPanel1,BoxLayout.Y_AXIS ));
        jPanel1.add(oMsg, BorderLayout.NORTH);
        oMsg.setVisible(true);
        this.validate();
        // posunutie oblasti sprav na spodok - t.j. poslednu spravu
        JScrollBar vertical = jScrollPane1.getVerticalScrollBar();
        vertical.setValue( vertical.getMaximum()); 

                
       
   }

   public void setMessageContainer(MessageContainer msgCntr) {
        if (msgContainer != null ) return;
        msgContainer = msgCntr;       
        for (MessageRecord mr:msgContainer.getMessageRecors()) {
            Pnl_message oMsg = new eoc.messages.Pnl_message(mr.message);
            AddNewMessage(oMsg);
            mr.message.listEntries("", true);
        }
        

   }
  /* 
   public void setMessageArrayList(ArrayList al) {
        alMessageRecords = al;
        System.out.println("SEETINGARRAYLISTTMESSAGERECORSSSS MESGLISTIS::\n");
        for (MessageRecord mr:alMessageRecords) {
            Pnl_message oMsg = new eoc.messages.Pnl_message(mr.message);
            AddMessage(oMsg);
            mr.message.listEntries("", true);
        }
    }
   */ 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
