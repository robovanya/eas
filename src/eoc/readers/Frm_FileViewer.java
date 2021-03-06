/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DefaultCaret;
import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Frm_FileViewer extends javax.swing.JFrame {

    File    fUserOutFile;
    long    lastFileLength = 0;  // posledna nacitana dlzka suboru
    Integer lastReadedLine = 0;  // posledny nacitany riadok
    Integer lastSeparatorNumber = 0; // posledné číslo rozdelovača
     
    // vlakno na cyklicke citanie/kontrolu vypisovaneho suboru (kazdu sekundu)
    Thread tCycle = new Thread("EaSys_LogFileController") {
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                skontrolujSubor();
            } catch (InterruptedException ie) {
            }
        }
    }
};

    /**
     * Creates new form jfrm_Log
     */
    public Frm_FileViewer() {
        initComponents();
        DefaultCaret caret = (DefaultCaret) jTextArea1.getCaret();
        caret.setUpdatePolicy(ALWAYS_UPDATE);        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pm_tools = new javax.swing.JPopupMenu();
        mi_appendSeparator = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mi_clearContents = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        mi_appendSeparator.setText("Pridať rozdelovač");
        mi_appendSeparator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_appendSeparatorActionPerformed(evt);
            }
        });
        pm_tools.add(mi_appendSeparator);
        pm_tools.add(jSeparator1);

        mi_clearContents.setText("Mazať obsah výpisov");
        mi_clearContents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_clearContentsActionPerformed(evt);
            }
        });
        pm_tools.add(mi_clearContents);

        setTitle("Easys - System log (Kernel-messages)");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setComponentPopupMenu(pm_tools);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mi_appendSeparatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_appendSeparatorActionPerformed
        lastSeparatorNumber++;
        jTextArea1.append("\n## >> " + lastSeparatorNumber + " << ##\n"); 
    }//GEN-LAST:event_mi_appendSeparatorActionPerformed

    private void mi_clearContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_clearContentsActionPerformed
        jTextArea1.setText("");
        lastSeparatorNumber = 0;
    }//GEN-LAST:event_mi_clearContentsActionPerformed

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
            java.util.logging.Logger.getLogger(Frm_FileViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frm_FileViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frm_FileViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frm_FileViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frm_FileViewer().setVisible(true);
            }
        });
    }

    public void skontrolujSubor() {
        long currFileLength = fUserOutFile.length();
        if (currFileLength != lastFileLength) {
             try {
                 readFile(fUserOutFile);
                 lastFileLength = currFileLength; 
             } catch (IOException ex) {
                 Logger.getLogger(Frm_FileViewer.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
    }
    
    public void readFile(File f)  throws FileNotFoundException, IOException {
         FileReader inputFile = new FileReader(f);

          //Vytvorenie objektu pre citanie obsahu suboru
          BufferedReader bufferReader = new BufferedReader(inputFile);

          //Variable to hold the one line data
          String line;
          int countCurReadedLines = 0;
//          int countCurReadedLines = 0;
          if (lastReadedLine == 0)
             jTextArea1.setText(""); // maze sa povodny obsah
          // Read file line by line and print on the console
          while ((line = bufferReader.readLine()) != null) {
            countCurReadedLines++;
            if (countCurReadedLines > lastReadedLine) {
                lastReadedLine++;
                jTextArea1.append(line + "\n"); 
                System.out.println(line);
            }
          }
          //Close the buffer reader
          bufferReader.close();
    }
    
    public void showFile(File f)  throws FileNotFoundException, IOException {
         fUserOutFile = f;
         lastFileLength = fUserOutFile.length();
         readFile(fUserOutFile);
         if (tCycle != null && !tCycle.isAlive()) tCycle.start();
    }

    @Override
    public synchronized void setState(int state) {
         try {
             showFile(fUserOutFile);
         } catch (IOException ex) {
             Logger.getLogger(Frm_FileViewer.class.getName()).log(Level.SEVERE, null, ex);
         }
        super.setState(state); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JMenuItem mi_appendSeparator;
    private javax.swing.JMenuItem mi_clearContents;
    private javax.swing.JPopupMenu pm_tools;
    // End of variables declaration//GEN-END:variables
}
