package eoc.messages;

import system.Kernel;
import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;



public class Pnl_StandardMessages extends javax.swing.JPanel {

    public Kernel krn;  
  //  JLabel  barLabel;
   // StandardProgressBar progBar;
    public Pnl_StandardMessages() {
        initComponents();
        barLabel.setVisible(false);
        this.doLayout();
        /* OLD CODE - 2017-03-21
       progBar = new eoc.messages.StandardProgressBar();
        barLabel = new JLabel(".......");
        barLabel.setBounds(1, 1, 76, 21);
        this.add(barLabel);
        this.add(progBar);
        barLabel.setVisible(true);
        barLabel.revalidate();
        barLabel.repaint();
        progBar.setVisible(true);
        progBar.revalidate();
        progBar.repaint();
//        tu sa nechce objavit Labeeeeeel
        this.invalidate();
        this.revalidate();
        this.doLayout();
        this.repaint();
                */
        //Thread t = new Thread(progBar);
        //t.start();
        //progBar.run();
//        initialize();
    }
    
    public Kernel getKrn() {
        return krn;
    }
    
    public void initialize() {
    }

    public void setKrn(Kernel krn) {
        this.krn = krn;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progBar = new eoc.messages.StandardProgressBar();
        barLabel = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setAlignmentX(4.0F);
        setMinimumSize(new java.awt.Dimension(0, 33));
        setPreferredSize(new java.awt.Dimension(352, 33));

        barLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barLabelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(barLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progBar, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progBar, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(barLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void barLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barLabelActionPerformed
        // TODO add your handling code here:
        Kernel.staticMsg("Nešťukaj do mňa. Aj tak ti nič nevykonám. :o)");
    }//GEN-LAST:event_barLabelActionPerformed
    public void setMessage(String lbl, String t){
        if ((lbl != null) && (!barLabel.getText().equals(lbl))) {
            setLabelText(lbl);
        }
         progBar.setMessage(t);
    }
    
    public void setActivity(String lbl, String cPopis, int akt, int max) {
        if ((lbl != null) && (!barLabel.getText().equals(lbl))) {
            setLabelText(lbl);
        }
        progBar.setActivity(cPopis, akt, max);
    }
    
    public void setActivity(int akt, int max) {
        progBar.setActivity(akt, max);
    }
    
    public void setLabelText(String lbl) {
        barLabel.setText(lbl);
        Graphics g = barLabel.getGraphics();
        FontMetrics met = g.getFontMetrics();
        int height = met.getHeight();
        int width = met.stringWidth(lbl);
        Rectangle r = barLabel.getBounds();
        r.width = width + 20;
        //Kernel.staticMsg("SETWIDTHH:" + (width + 20));
        barLabel.setBounds(r);
        barLabel.setVisible(!lbl.equals(""));
        barLabel.invalidate();
        barLabel.validate();
        barLabel.repaint();
        this.invalidate();
        this.validate();
        this.repaint();
        this.doLayout();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton barLabel;
    private eoc.messages.StandardProgressBar progBar;
    // End of variables declaration//GEN-END:variables
}
