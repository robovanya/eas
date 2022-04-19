/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.xls;

import eoc.widgets.PObject;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_XLS_reader extends PObject {
  private File inputFile;
  private String cellFilter[] = {"A","B","C","D","E","F","G","H","I","J","K",
                                 "L","M","N","O","P","Q","R","S","T","U"};
  /*
  private String columns[] = {"C_TECHEM","P_MIESTN","OST","POLOHA","C_SPRAVCA",
                              "MENO","ULICA","TYP_SPOTR","TYP_PRISTR","C_PRISTR",
                              "MIESTN","STAR_ODP","STAR_ODP_D","STAR_ODP_T",
                              "NOVY_ODP","NOVY_ODP_D","NOVY_ODP_T","TYP_ODP",
                              "TYP_ODH","KOEFICIENT","SPOTREBA"};
  */
  FileFilter filter = 
     new FileNameExtensionFilter("Súbor Microsoft EXCEL", new String[] {"xls"});

  private int colERRORS; // index stlpca ERRORS v modeli
  private int colMARKED; // index stlpca MARKED v modeli
  private int colPROBLEM; // index stlpca PROBLEM (obsahuke chybu) v modeli
  private int colUPDATES; // index stlpca UPDATES (oprava datumu pre AZ-AZ) v modeli
  private int colUPDATED; // index stlpca UPDATED (oprava datumu pre AZ-AZ) v modeli

  // EXCEL premenne
  private Workbook         readWb;
  private WritableWorkbook writeWb;
  private Sheet            readSheet;
  private WritableSheet    writeSheet;
  private WorkbookSettings wbSettings; // = new WorkbookSettings();

  private MyTableModel_1 tbmodelXLS;
  WritableCellFormat dateErrCellFormat =  //new WritableCellFormat();
      new jxl.write.WritableCellFormat (new jxl.write.DateFormat("dd.MM.yyyy"));

  WritableCellFormat dateUpdCellFormat =  //new WritableCellFormat();
      new jxl.write.WritableCellFormat (new jxl.write.DateFormat("dd.MM.yyyy"));

  class MyTableModel_1 extends DefaultTableModel {
 
    @Override
      public Class getColumnClass(int col) {
        // stlpce s desatinym cislom
        if (col == 11 || col == 14 || col == 19 || col == 20)       
            return Double.class;
        else 
        if (col == 12 || col == 15)       
            return DateTime.class;
        else return String.class;  // ostatne stlpce su textove
     }
  } // Class getColumnClass(int col)
    
  public int getColIdx(String colName) {
      int colIdx = -1;
      for (int i = 0; i < jTable_XLS.getColumnCount(); i++) {
           if (jTable_XLS.getColumnName(i).equalsIgnoreCase(colName)) {
               colIdx = i;
               break;
           }
      }
      return colIdx;
  }
    
  public class MyRenderer extends DefaultTableCellRenderer { 

      public Component getTableCellRendererComponent(JTable table, Object value,
                  boolean isSelected, boolean hasFocus, int row, int column) { 
           Component c = super.getTableCellRendererComponent
                       (table, value, isSelected, hasFocus, row, column); 

           if (colERRORS == -1) return c;
           
           Object oERRORS = table.getValueAt(row, colERRORS);
           String cERRORS = (oERRORS==null ? "" : oERRORS.toString());
           
           if(cERRORS.length() > 0) {
        
               if (cERRORS.toUpperCase().contains(" " 
                          + jTable_XLS.getColumnName(column).toUpperCase())) 
                   c.setBackground(new java.awt.Color(255, 0, 0)); 
               else {
                   if (!isSelected) 
                       c.setBackground(table.getBackground()); 
                   else
                       c.setBackground(table.getSelectionBackground());
               }
           }    
           else { 
               if (!isSelected) 
                   c.setBackground(table.getBackground()); 
               else
                   c.setBackground(table.getSelectionBackground());
          }
          return c; 
      } // public Component getTableCellRendererComponent

} // public class MyRenderer extends DefaultTableCellRenderer
    
    /**
     * Creates new form KoncorocnyImport
     */
    public Pnl_XLS_reader() {
      try {
          dateErrCellFormat.setBackground(Colour.ROSE);
          dateErrCellFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
          dateUpdCellFormat.setBackground(Colour.YELLOW2);
          dateUpdCellFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM_DASHED);
      } catch (WriteException ex) {
          Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
      }
        tbmodelXLS = new MyTableModel_1();
        initComponents();
        //updateTblModel(tbmodelXLS,columns);
        tbmodelXLS.addColumn("ERRORS");
        tbmodelXLS.addColumn("MARKED");
        tbmodelXLS.addColumn("PROBLEM");
        tbmodelXLS.addColumn("UPDATES");
        tbmodelXLS.addColumn("UPDATED");
        jTable_XLS.setModel(tbmodelXLS);
        colERRORS  = getColIdx("ERRORS");
        colMARKED  = getColIdx("MARKED");
        colPROBLEM = getColIdx("PROBLEM");
        colUPDATES = getColIdx("UPDATES");
        colUPDATED = getColIdx("UPDATED");
        jTable_XLS.setDefaultRenderer(String.class, new MyRenderer());
        jTable_XLS.setDefaultRenderer(Number.class, new MyRenderer());
        jTable_XLS.setDefaultRenderer(Date.class, new MyRenderer());
        jTable_XLS.setDefaultRenderer(DateTime.class, new MyRenderer());
        jTable_XLS.setModel(tbmodelXLS);

        repaint();
        
        jFileChooser1.setFileFilter(filter);
        jFileChooser1.addChoosableFileFilter(filter);
        jFileChooser1.setCurrentDirectory(new File("C:\\Techem_20160301new"));
    }

    private void updateTblModel(DefaultTableModel tblModel, String columns[]) {
        for (int i = 0; i < columns.length; i++) {
            tblModel.addColumn(columns[i]);
        } 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Dlg_FileChooser = new javax.swing.JDialog();
        jFileChooser1 = new javax.swing.JFileChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_XLS = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTF_inputFile = new javax.swing.JTextField();
        jButton_Load = new javax.swing.JButton();
        jButton_XLS_File = new javax.swing.JButton();
        jTF_outputFile = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton_Test = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        javax.swing.GroupLayout Dlg_FileChooserLayout = new javax.swing.GroupLayout(Dlg_FileChooser.getContentPane());
        Dlg_FileChooser.getContentPane().setLayout(Dlg_FileChooserLayout);
        Dlg_FileChooserLayout.setHorizontalGroup(
            Dlg_FileChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 783, Short.MAX_VALUE)
        );
        Dlg_FileChooserLayout.setVerticalGroup(
            Dlg_FileChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
        );

        jFileChooser1.setMinimumSize(new java.awt.Dimension(725, 545));
        jFileChooser1.setPreferredSize(new java.awt.Dimension(725, 545));

        jTable_XLS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "p_Miestn", "c_Spravca", "c_pristr", "miestn", "stary_odp_d", "star_odp_t", "star_odp", "novy_odp_d", "novy_odp_t", "novy_odp"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable_XLS.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_XLS.setFillsViewportHeight(true);
        jTable_XLS.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jTable_XLS);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Vstupný EXCEL súbor:");

        jTF_inputFile.setEnabled(false);
        jTF_inputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_inputFileActionPerformed(evt);
            }
        });

        jButton_Load.setText("Načítaj");
        jButton_Load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_LoadActionPerformed(evt);
            }
        });

        jButton_XLS_File.setText("Výber XLS súboru");
        jButton_XLS_File.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_XLS_FileActionPerformed(evt);
            }
        });

        jTF_outputFile.setEnabled(false);
        jTF_outputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_outputFileActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Výstupný EXCEL súbor:");

        jButton_Test.setText("Test");
        jButton_Test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_TestActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Potrebné typy testov:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTF_outputFile, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .addComponent(jTF_inputFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton_XLS_File, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_Load, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton_Test, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTF_inputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_Load)
                    .addComponent(jButton_XLS_File))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTF_outputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Test))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_TestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_TestActionPerformed
        wbSettings = new WorkbookSettings();
        //    wws.setEncoding( "Cp1250" );
        wbSettings.setEncoding( "Cp852" ); // ZATIAL SA NA TO VYKAKAT
        try {

            readWb = Workbook.getWorkbook(inputFile);
            String outputName = inputFile.getAbsolutePath();
            outputName = outputName.replace(".xls", "_tested.xls");
            try {
                writeWb = Workbook.createWorkbook (new File(outputName), readWb);
            }
            catch (Exception ex) {
                krn.Message("E", "Výstupný súbor " + outputName + " sa nedá otvoriť pre zápis.\n\n"
                    + "Chyba:\n" + ex.getMessage(), "Problém pri otvorení výstupného súboru");
                return;
            }
            readWb.close();
            // zistim prvu stranu XLS suboru
            writeSheet = writeWb.getSheet(0);
            addExtendedColumns();
            for (int row = 0; row < tbmodelXLS.getRowCount(); row++) {
                if (row % 50 == 0)
                System.out.println("TESTING ROW: " + row);
                
//                if (jCB_TypyOdpoctov.isSelected()) NOVY_ODP_T_test(row);
//                if (jCB_BaseTest.isSelected())    baseTest(row);
 //               if (jCB_AZ_EZ_test.isSelected())  AZ_EZ_test(row);
  //              if (jCB_Stupacka.isSelected())    P_MIESTN_test(row);
            }
            mark_errors(); // oznacenie vsetkych riadkov bytov,
            // pri ktorych sa vyskytol nejaky problem
            writeWb.write();
            writeWb.close();
            jTable_XLS.repaint();
            Desktop dt = Desktop.getDesktop();
            dt.open(new File(outputName));
        } catch (WriteException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton_TestActionPerformed

    
    private void jTF_outputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_outputFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTF_outputFileActionPerformed

    private void jButton_XLS_FileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_XLS_FileActionPerformed
        jFileChooser1.showDialog(Dlg_FileChooser, "Výber súboru XLS");
        inputFile = jFileChooser1.getSelectedFile();
        if (inputFile!=null) {
            jTF_inputFile.setText(inputFile.getAbsolutePath());
            String outputName = inputFile.getAbsolutePath();
            jTF_outputFile.setText(outputName.replace(".xls", "_tested.xls"));
        }
    }//GEN-LAST:event_jButton_XLS_FileActionPerformed

    private void jButton_LoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_LoadActionPerformed
        try {
            createTableModelFromXLS(inputFile, cellFilter, tbmodelXLS);
            /////readXLStoTable(inputFile, cellFilter, tbmodelXLS);
            jTable_XLS.revalidate();
            jTable_XLS.repaint();
        } catch (IOException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton_LoadActionPerformed

    private void jTF_inputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_inputFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTF_inputFileActionPerformed

    // oznacenie udojov problemovych bytov
    private void mark_errors() {
        String badByty  = "";
        String badBloky = "";
        for (int row = 0; row < tbmodelXLS.getRowCount(); row++) {
            Object oERR = jTable_XLS.getValueAt(row, colERRORS);
            String currERR = "";
            if (oERR!=null) currERR = oERR.toString();
            if (currERR.length() > 0) {
                String currByt = jTable_XLS.getValueAt(row, 4).toString().substring(0,8);
                if (!badByty.contains("," + currByt))
                    badByty = badByty +  "," + currByt;
            }
        }    
        // nepotrebne byloky - podla Kiss-úr a
        badBloky = badBloky + ",013,045,181";
        int xlsRow;
        // oznacenie riadkov bytov s vyskytom chyb, a nepotrebnych bytov
        for (int row = 0; row < tbmodelXLS.getRowCount(); row++) {
               
            String currByt = jTable_XLS.getValueAt(row, 4).toString().substring(0,8);
            if (badByty.contains("," + currByt)
                || badBloky.contains("," + currByt.substring(0, 3))) {
                xlsRow = row + 1 /* hlavicka */;        
                WritableCell cMARK = writeSheet.getWritableCell(colMARKED, xlsRow);
                jTable_XLS.setValueAt("*", row, colMARKED);
                Label lbl;
                if (cMARK.getType() == CellType.LABEL) {
                    lbl = (Label) cMARK; 
                    lbl.setString("*");
                }
                else {
                    lbl = new Label(colMARKED, xlsRow, "*");
                    cMARK = (WritableCell) lbl;
                    try {
                        writeSheet.addCell(cMARK);
                    } catch (WriteException ex) {
                        Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
       }
       System.out.println("BAD_BYTY:" + badByty);
    }  // private void mark_errors()
        

    
    
    String clearBeginnerZeros(String s) {
        while (s.startsWith("0")) {
            s = s.substring(1);
        }
        return s;
    }
    

    private void addExtendedColumns() {
        System.out.println("ADDING EXTENDED COLUMNS:" + colERRORS + " " + colMARKED);
        WritableCell cERR = writeSheet.getWritableCell(colERRORS, 0);
        Label lbl;
        lbl = new Label(colERRORS, 0, "ERRORS");
        cERR = (WritableCell) lbl;
        try {
            writeSheet.addCell(cERR);
        } catch (WriteException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        }
        WritableCell cMARK = writeSheet.getWritableCell(colMARKED, 0);
        lbl = new Label(colMARKED, 0, "MARKED");
        cMARK = (WritableCell) lbl;
        try {
            writeSheet.addCell(cMARK);
        } catch (WriteException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        }

        WritableCell cPROB = writeSheet.getWritableCell(colPROBLEM, 0);
        lbl = new Label(colPROBLEM, 0, "PROBLEM");
        cPROB = (WritableCell) lbl;
        try {
            writeSheet.addCell(cPROB);
        } catch (WriteException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        WritableCell cUPDS = writeSheet.getWritableCell(colUPDATES, 0);
        lbl = new Label(colUPDATES, 0, "UPDATES");
        cUPDS = (WritableCell) lbl;
        try {
            writeSheet.addCell(cUPDS);
        } catch (WriteException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        WritableCell cUPD = writeSheet.getWritableCell(colUPDATED, 0);
        lbl = new Label(colUPDATED, 0, "UPDATED");
        cUPD = (WritableCell) lbl;
        try {
            writeSheet.addCell(cUPD);
        } catch (WriteException ex) {
            Logger.getLogger(Pnl_XLS_reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

private void addExtendedColumn(int column, String cLabel) {
    
}    
public void createTableModelFromXLS(File inputFile,String cellFilter[],DefaultTableModel tbmodel)
           throws IOException, BiffException  {
    Kernel.staticMsg("createTableModelFromXLS");
    tbmodel.setRowCount(0);
    File inputWorkbookFile = inputFile;
    wbSettings = new WorkbookSettings();
    wbSettings.setEncoding( "cp1250" );
    boolean showDataType = true;
    try {
      readWb = Workbook.getWorkbook (inputWorkbookFile, wbSettings);
      // zistim prvu stranu XLS suboru
      readSheet = readWb.getSheet(0);
      // Hodnoty, ktore potrebujeme
      DecimalFormat df = new DecimalFormat("0.00");
      Double spolu = new Double(0);
      int rowCount = 0;
      for (int i = 2; i <= readSheet.getRows(); i++) {
          Object[] oRowData = new Object[cellFilter.length];   
          for (int j = 0; j < cellFilter.length; j++) {
              Cell myCell = readSheet.getCell(cellFilter[j] + i);
              CellType type = myCell.getType();

              if (type == CellType.LABEL) {
                  oRowData[j] = new String(myCell.getContents());
              }
              else if (type == CellType.NUMBER) {
                  oRowData[j] = new Double(myCell.getContents().replace(',','.'));
                  spolu = spolu + (Double) oRowData[j];
              }
              else if (type == CellType.DATE) {
                  oRowData[j] = new String(myCell.getContents());
              }
              else {
                  oRowData[j] = new String(myCell.getContents());
              }
        }
        tbmodel.addRow(oRowData);
        rowCount++;
      }
      readWb.close();
      System.out.println("Spolu v " + inputFile.getName() + "  -> riadokov: " + rowCount + " suma spolu: " + spolu);
      
    } catch (BiffException e) {
      e.printStackTrace();
    }
  } // public void readXLS(File inputFile, DefaultTableModel tbmodel)

    
public void readXLStoTable(File inputFile,String cellFilter[],DefaultTableModel tbmodel)
           throws IOException, BiffException  {
    tbmodel.setRowCount(0);
    File inputWorkbookFile = inputFile;
    wbSettings = new WorkbookSettings();
    wbSettings.setEncoding( "cp1250" );
    boolean showDataType = true;
    try {
      readWb = Workbook.getWorkbook (inputWorkbookFile, wbSettings);
      // zistim prvu stranu XLS suboru
      readSheet = readWb.getSheet(0);
      // Hodnoty, ktore potrebujeme
      DecimalFormat df = new DecimalFormat("0.00");
      Double spolu = new Double(0);
      int rowCount = 0;
      for (int i = 2; i <= readSheet.getRows(); i++) {
          Object[] oRowData = new Object[cellFilter.length];   
          for (int j = 0; j < cellFilter.length; j++) {
              Cell myCell = readSheet.getCell(cellFilter[j] + i);
              CellType type = myCell.getType();

              if (type == CellType.LABEL) {
                  oRowData[j] = new String(myCell.getContents());
              }
              else if (type == CellType.NUMBER) {
                  oRowData[j] = new Double(myCell.getContents().replace(',','.'));
                  spolu = spolu + (Double) oRowData[j];
              }
              else if (type == CellType.DATE) {
                  oRowData[j] = new String(myCell.getContents());
              }
              else {
                  oRowData[j] = new String(myCell.getContents());
              }
        }
        tbmodel.addRow(oRowData);
        rowCount++;
      }
      readWb.close();
      System.out.println("Spolu v " + inputFile.getName() + "  -> riadokov: " + rowCount + " suma spolu: " + spolu);
      
    } catch (BiffException e) {
      e.printStackTrace();
    }
  } // public void readXLS(File inputFile, DefaultTableModel tbmodel)

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog Dlg_FileChooser;
    private javax.swing.JButton jButton_Load;
    private javax.swing.JButton jButton_Test;
    private javax.swing.JButton jButton_XLS_File;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTF_inputFile;
    private javax.swing.JTextField jTF_outputFile;
    private javax.swing.JTable jTable_XLS;
    // End of variables declaration//GEN-END:variables
}
