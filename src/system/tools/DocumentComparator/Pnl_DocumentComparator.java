/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package system.tools.DocumentComparator;

import eoc.widgets.PObject;
import java.io.File;
import java.util.Arrays;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_DocumentComparator extends PObject {

    /**
     * Creates new form Pnl_directoryComparator
     */
    public Pnl_DocumentComparator() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        btn_nacitajDBzaznamy = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree_DB = new javax.swing.JTree();

        jPanel1.setName("jPanel1"); // NOI18N

        jButton1.setText("Compare directories");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btn_nacitajDBzaznamy.setText("Načítať záznamy z databázy");
        btn_nacitajDBzaznamy.setName("btn_nacitajDBzaznamy"); // NOI18N
        btn_nacitajDBzaznamy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_nacitajDBzaznamyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_nacitajDBzaznamy)
                .addContainerGap(379, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(btn_nacitajDBzaznamy))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTree_DB.setName("jTree_DB"); // NOI18N
        jScrollPane1.setViewportView(jTree_DB);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        File f = new File("C:\\BlokyNew");
        listFilesForFolder(f);
        System.out.println("END OF DIRECTORY-COMPARING");

    }//GEN-LAST:event_jButton1ActionPerformed

    private void btn_nacitajDBzaznamyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_nacitajDBzaznamyActionPerformed
//        TreeModel model = new FileTreeModel(new File(System.getProperty("user.dir")));
//        JTree tree = new JTree(model);
        String sql = "SELECT TOP 5 filename FROM LIBDOCS ORDER BY id";
        Object[][] oQry = krn.SQLQ_getQueryResultSetAsArray(krn.getDBcnWork().getConn(), sql, false);
        //v tejto slucke, alebo po nej distribuovat spravy do panelu a udrzat prepojenie
        for (Object[] oo: oQry) {
            System.out.println("REDIGG:" + oo[0].toString());
            String s = oo[0].toString();
            s = s.replace("\\", "/");
            String[] ss = s.split("/");
            System.out.println("REDIGGoo:" + Arrays.deepToString(ss));
            
        }
                
    }//GEN-LAST:event_btn_nacitajDBzaznamyActionPerformed

    public void listFilesForFolder(final File folder) {
    String sql;
    // test suborov adresarov z Bloky voci databaze (tabulka LIBDOCS)
    for (final File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
            listFilesForFolder(fileEntry);
        } else {
            //if (fileEntry.getName().equalsIgnoreCase("thumbs.db")) continue;
            sql = "select count(*) as numRecs FROM LIBDOCS WHERE docname = '" + fileEntry.getName() + "'";
            Object o = krn.SQLQ_getQueryValue(MyCn.getConn(), sql, "numRecs");
            Integer i = Integer.parseInt(o.toString());
            if (i == 0) System.out.println(i + " --- " + fileEntry.getName());
            if (i > 1) {
                System.out.println(i + " --- " + fileEntry.getName());
                // vylistuju sa vsetky nazvy suborov aj s adresarmi
//                     --> [id, docname, tablename, reckey, filename
//                             , filebinary, vytvoril, dtvytv, dtzmeny, kto, platny, remote_id, doc_id, destination, cat_id, dtschvalenia, dtodoslania, dtotvorenia, dtsynchtoisa, dtnotify, parent_docname, stav_signatus, lib_id, filetext, APPCODE, ZAS_ID, REP_NAME, STRANY]
//     --> [38292, 168 obj.001 oprava vch. dverí 09012015.pdf, vcs, 110, null, p:\168 - Bitunková 7-9-11\2015\168 obj.001 oprava vch. dverí 09012015.pdf, null, ana, 2015-02-03 23:01:52.964, 2015-02-03 23:01:52.964, ana, 1, null, null, 2, 68, null, null, null, null, null, null, null, null, null, null, null, null, null]
//     --> [46259, 168 obj.001 oprava vch. dverí 09012015.pdf, vcs, 112, null, p:\170 - G.Bethlena 1-3-5-7\168 - Bitunková 7-9-11\2015\168 obj.001 oprava vch. dverí 09012015.pdf, null, ana, 2016-04-15 23:03:02.315, 2016-04-15 23:03:02.315, ana, 1, null, null, 2, 1358, null, null, null, null, null, null, null, null, null, null, null, null, null]

                sql = "select id, tablename, reckey, filename FROM LIBDOCS WHERE docname = '" + fileEntry.getName() + "'";
                Object oo[][] = krn.SQLQ_getQueryResultSetAsArray(MyCn.getConn(), sql, false);
                for (Object[] row:oo) {
                    String sFile = row[3].toString().trim();
                    sFile = sFile.replace("p:", "c:\\BlokyNew");
                    File fTest = new File(sFile);
                 //   reckey picu pomoze. Treba vyhladat VCS !
                   System.out.println(fTest.exists() + "     --> " + Arrays.deepToString(row));
                   // mazanie zaznamu v DB o neexistujucom dokumente
                   if (!fTest.exists()) {
                       sql = "DELETE FROM LIBDOCS WHERE id = " + row[0];
                       System.out.println("DELETING RECORD FOR: " + sFile + "\nSQL: " + sql);
                       //krn.SQL_callSqlStatement(MyCn, sql, true);
                       System.out.println("ID " + row[0] + " DELETED akoze, inak nie :-)");
                   }
                } // for (Object[] row:oo)
            } // if (i > 1)
        }
    }
 }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_nacitajDBzaznamy;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree_DB;
    // End of variables declaration//GEN-END:variables
}
