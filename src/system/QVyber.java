/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system;

import eoc.database.DBconnection;
import eoc.dbdata.ColumnDefinition;
import eoc.xinterface.XDBtableColumn;
import eoc.xinterface.XTableModel;
import system.desktop.Desktop;
import system.Kernel;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rvanya
 */
public class QVyber extends eoc.widgets.DObject {

   private Desktop     dsk;
   private eoc.IEOC_VisualObject questor_frm;
   private Component   questor_fld;
   // ziadana navratova hodnota s moze skladat z dvoch casti, delenych ^
   // pricom prva cast moze skladat z casti delenych ciarkou,
   // druha cast MUSI BIT nazov jedneho stlca 
   // (nieco, ako primary_key vyberu, z hladiska objektu-ziadatela)
   // Vsetky casti MUSIA byt nazvami stlpcov z tabulky vyberu
   private String[] sRetValCols;
   private String   sRequiedPrimaryKey;
   private String   sRetVal = "";
   private String   sCurrQry = "";
   private Object /* eoc.IEOC_VisualObject */ methodSource;
////   Hashtable<String, Method> htCalcMethods = new Hashtable<String, Method>();
    public  XTableModel    myTableModel; // = new DefaultTableModel();
    public DefaultTableColumnModel myTableColumnModel; // = new DefaultTableColumnModel();
    ColumnDefinition[] columnDefinitions;
    private String[] sColumnLabels;         // nazvy stlpcov, prazdny nazov==hidden
    private Object[][] data;

    public QVyber(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // Vytvorenie ovladaca pre stlacenie klaves manipulujuce s tabulkou
        // (Up,Down,PgUp,PgDown,Ctrl+Home,Ctrl+End)
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        myTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                   System.out.println("QVYBER-DOBLECLSCLS???===" + e.getClickCount());
                if (e.getClickCount() == 2) {
      sRetVal = packRetVal();
      endVyber(true /* schovat okno */);
                     e.consume();
                    return;
                }
            }    
            });            
        
        myTable.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { // nespracovava sa
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //ctrl+a
                if (e.getKeyCode() == 65 && e.isControlDown()) {
                   // mazanie nepotrebnych vektor sipok, ked existuje
                     e.consume();
//                    _evt_valueChanged();  // Vykonny kod pre spracovanie udalosti valueChanged
                    return;
                }
                //ctrl+i
                if (e.getKeyCode() == 73 && e.isControlDown()) {
                   // mazanie nepotrebnych vektor sipok, ked existuje
  //                  clearOrdering();
                    return;
                }
                // klavesy - Ctrl+j
                if (e.getKeyCode() == 74 && e.isControlDown()) {
                    krn.Message(myObjectID, "I", 
                        "(Dúfam teda, že by ste to sám nenapísali inak :-))\n\n" 
                        + sCurrQry // + "\n\nCACHE_SIZE:" + 
                       // (iNumFetchedRows == -9999 ? "ALL" : iNumFetchedRows)
                            , "Aktuálny dotaz (ctrl+j)");
//                  SystemClipboard.copy(sCurrQry);
                    Clipboard cbr = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection ss = new StringSelection(sCurrQry);
                    cbr.setContents(ss,ss);
                    //e.consume();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                   //goToPrev("");
//             currSelectedRow = myTable.getSelectedRow() - 1;
//           _evt_valueChanged();  // Vykonny kod pre spracovanie udalosti valueChanged
                   // e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_UP) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    //goToNext("");
//            currSelectedRow = myTable.getSelectedRow() + 1;
//            _evt_valueChanged();  // Vykonny kod pre spracovanie udalosti valueChanged
                   // e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_DOWN){
                /* // tieto su presmerovavane spat
                */
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ) {
//                    _evt_vk_page_down();
//                    e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && ...

                if (e.getKeyCode() == KeyEvent.VK_PAGE_UP ) {
//                    _evt_vk_page_up();
//                    e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_PAGE_UP && riadok ==0){
                
                if (e.getKeyCode() == KeyEvent.VK_HOME && e.isControlDown()) {
//                    goToFirst("");
//                    e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_PAGE_UP && riadok ==0){
                if (e.getKeyCode() == KeyEvent.VK_END && e.isControlDown()) {
//                    goToLast("");
//                    e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_PAGE_UP && riadok ==0){
                // klavesy - Ctrl+i

            } // public void keyPressed(KeyEvent e) {

            @Override
            public void keyReleased(KeyEvent e) { // nespracovava sa
            }
        }); // .addKeyListener
        
        this.setAlwaysOnTop(true);
    }

   /**
    * Creates new form EOC_vyber
    */
   /*
   public EOC_vyber() {
      initComponents();
      this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE );
      setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
 }
   */
   
    @Override
    public String initialize(Kernel krnl, DBconnection cX) {
       super.initialize(krnl, cX);
       krn = krnl;
       MyCn = cX;
       dsk = krn.getDsk();
       dsk.setEnabled(false);
       bInitialized = true;
//XTB       xTB_vyber1.setParentContainer(this);
       return "";
    }

   /**
    * This method is called from within the constructor to initialize the form. WARNING:
    * Do NOT modify this code. The content of this method is always regenerated by the
    * Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_OK = new javax.swing.JButton();
        btn_Zrus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        myTable = new javax.swing.JTable();

        setTitle("Výber");
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btn_OK.setText("OK");
        btn_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_OKActionPerformed(evt);
            }
        });

        btn_Zrus.setText("Zruš");
        btn_Zrus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ZrusActionPerformed(evt);
            }
        });

        myTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(myTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(207, Short.MAX_VALUE)
                .addComponent(btn_OK)
                .addGap(35, 35, 35)
                .addComponent(btn_Zrus)
                .addContainerGap(207, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Zrus)
                    .addComponent(btn_OK))
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(561, 453));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

   private void btn_ZrusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ZrusActionPerformed
      sRetVal = "<CANCELED>";
      endVyber(true /* schovat okno */);
      
      this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
   }//GEN-LAST:event_btn_ZrusActionPerformed

   private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
      //// krn.krnMsg("formWindowClosed"); 
      dsk.setEnabled(true);
   }//GEN-LAST:event_formWindowClosed

   
   private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      dsk.setEnabled(true);
      // ked ma sRetVal hodnotu, bola nastavena buttonmi OK alebo Zrus
      if (sRetVal.equals("")) {
         sRetVal = "<CANCELED>";
         endVyber(false);
      }
   }//GEN-LAST:event_formWindowClosing
   /*
   private String[] sRetValCols;
   private String   sRequiedPrimaryKey;
   private String   sRetVal = "";
   */
   
   private void btn_OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_OKActionPerformed
       ////krn.krnMsg("EKSN_PERFORMED");
      // krn.krnMsg("SETTING-sRetVal TO: " + sRetVal + " --> RUN1NING endVyber(true)");
      sRetVal = packRetVal();
      endVyber(true /* schovat okno */);
   }//GEN-LAST:event_btn_OKActionPerformed

   public void endVyber(boolean schovatOkno) {
      if (schovatOkno)  {
         this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
      }
      //krn.krnMsg("VYBRALA SA HODNOTA: " + sRetVal);
   }
           
   public String createQVyberForMe(eoc.IEOC_VisualObject vFromViewer, Component comp, 
                              String sQry,String retCols, /* String sWhere, String sOrder,*/
                              String sTitle,String sHeaders) { 
      String sFirstRetvalPart;
      sFirstRetvalPart = system.FnEaS.sEntry(1, retCols, "^");
      if (system.FnEaS.iNumEntries(retCols,"^") > 1) 
          sRequiedPrimaryKey = system.FnEaS.sEntry(2, retCols, "^").trim() ;
      else 
          sRequiedPrimaryKey = "";
      
      sRetValCols = sFirstRetvalPart.split(",");
      questor_frm = vFromViewer;
      questor_fld = comp;
      sCurrQry    = sQry;
      sColumnLabels = sHeaders.split(",");
      myTableModel = new XTableModel();
      myTable.setModel(myTableModel);
      myTableColumnModel = (DefaultTableColumnModel) myTable.getColumnModel();
      ColumnDefinition[] colDefinitions = null;
       try {
          try (ResultSet rs = krn.SQLQ_getQueryResultSet(MyCn.getConn(), sQry)) {
              colDefinitions = krn.SQLQ_getResultSetAsColumnDefinitionArray(MyCn.getConn(), rs);
              data = krn.SQLQ_getResultSetAsArray(rs, false);
              System.out.println("colDefinitions.length:::" + colDefinitions.length);
          }
           if (colDefinitions.length > 0) {
               System.out.println(colDefinitions.length);
           }
       } catch (SQLException ex) {
           Logger.getLogger(QVyber.class.getName()).log(Level.SEVERE, null, ex);
       }
       buildTblFromResultSetQuery(colDefinitions);
      if (sTitle.length() > 0)
          this.setTitle(sTitle);
      System.out.println("BCMC:" + myTableColumnModel.getColumnCount() + "\n" +
                         "BTMC:" + myTableModel.getColumnCount()); 
      int ii = 0;
      for (Object[] row: data) {
          ii++;
          if (ii < 10) System.out.println("DATAL:" + row.length);
          ((DefaultTableModel) myTableModel).addRow(row);
      }
      this.setVisible(true);
      return sRetVal;
   }


    public void buildTblFromResultSetQuery(ColumnDefinition[] colDefinitions) {
        //QQQ TO BUDE SKLADANIE metadat pre stlpce/columns
        //QQQ a skladanie pola udajov
          /* ColDefinition format
         * 
         * dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
         * numdecimals^formatstring^tooltyp
         * 
         * Ked sa tablename Zacina s !@, jedna sa o specialny stlpec,
         * napriklad: FUN@- metodu treba volat @CALCULATED, !@@DBQUERY, ...
         * ************************************************************************/
            if (colDefinitions==null) {
                krn.Message("E", "myXQuery.getrsMetaData() - XQuery_ERROR", "NULL-ResultSet.metadata");
                return;
            }
        columnDefinitions = colDefinitions;

        List<XDBtableColumn> alColumns = new ArrayList<>();
        deleteTableStructure(); // mazanie pripadnych default stlpcov
      System.out.println("CMC:" + myTableColumnModel.getColumnCount() + "\n" +
                         "TMC:" + myTableModel.getColumnCount()); 
        myTable.setAutoCreateColumnsFromModel(false);
        String colDefString;   // Column definition format string
        String colName;  // meno stlpca resultsetu
        int colPositionInDispFlds = -1; // pozicia stlpca v sDisplayedFields
        String colLabel = ""; // Nadpis stlpca
        Boolean bHidden = false; // schovany stlpec
            krn.debugOut(this, 5, "buildTblFromResultSet()BEGIN -> "
                    + "myTableColumnModel.getColumnCount()="
                    + myTableColumnModel.getColumnCount()
                    + " myTable.getColumnCount()=" + myTable.getColumnCount()
                    + " myTable.getModel().getColumnCount()="
                    + myTable.getModel().getColumnCount());
            
            for_block:
            for (int i = 1; i <= columnDefinitions.length; i++) {
                ColumnDefinition colDef = columnDefinitions[i - 1];
                colName = colDef.getFieldName(); // rsMetaData.getColumnName(i);
                //////krn.OutPrintln("CREATING COLUMN: " + colName);
                bHidden = false;
                // vsetky stlpce sa beru z databazovej tabulky
                colLabel = "";
                if ((sColumnLabels!=null) && sColumnLabels.length > 0) {
                    if (sColumnLabels.length - 1 >= i) colLabel = sColumnLabels[i-1];
                }                    
                // istota je gulomet :-)
                if (colLabel.equals("")) colLabel = colName;
                //      dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
                //      numdecimals^formatstring^tooltyp^HIDDEN
                colDefString = "^" + colDef.getTableName(); // sMasterTable; // dbname sa zatial ignoruje getDBname();
                colDefString = colDefString + "^" + colDef.getFieldName(); // colName;
                colDefString = colDefString + "^" + colDef.getDataType(); // "String"; // krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "DataType");
                colDefString = colDefString + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "DefaultValue");
                colDefString = colDefString + "^" + colLabel;
                colDefString = colDefString + "^" + colDef.getFieldLength(); // 100; // krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Length");
                colDefString = colDefString + "^" + colDef.getNumDecimals(); // 2;  // krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Decimals");
                colDefString = colDefString + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "formatstring");
                colDefString = colDefString + "^"; // krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Comment");
                /////krn.OutPrintln("CREATING COLUMN: " + colName + " WITH LABEL: " + colLabel);
                XDBtableColumn aktCol = 
                        createColumn(colDefString, colPositionInDispFlds, bHidden);
                aktCol.setModelIndex(i-1);
                alColumns.add(aktCol);
            } // for (int i = 1; i <= numColumns; i++)

            /*
                 System.out.println("SDFDEFS:" + sDisplayedFieldDefinitions 
                                   + "\nSDFF:" + sDisplayedFields
                                   + "\nSDFL:" + sDisplayedFieldLabels
                                   );
                 */
            /*
            if ((sDisplayedFields!=null) && sDisplayedFields.length > 0) {
            int iNumEnt = sDisplayedFields.length;
            for (int i = 0; i < iNumEnt; i++) {
                colName = sDisplayedFields[i];
                
                if (colName.startsWith("FUN@")) {
                    colPositionInDispFlds = i;
    
                    colLabel = sDisplayedFieldLabels[i];
                    if (colLabel==null || colLabel.equals("")) {
                        colLabel = colName;
                    }
                /////System.out.println(">COOOLABL: " + colLabel + " formLabels:" + sDisplayedFieldLabels);
                    ////   nu a v noci zalozis prve fungujuce stlpce, robikam
                    colDef = "!@@FUNCTION^" + sMasterTable; // dbname sa zatial ignoruje getDBname();
                    colDef = colDef + "^" + colName;
                    colDef = colDef + "^STRING"; //+ krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "DataType");
                    colDef = colDef + "^!@@FUN"; // + krn.getDBcolumnInfo(sMasterTable, colName, "DefaultValue");
//               colDef = colDef + "^" + colName; // + krn.getDBcolumnInfo(sMasterTable, colName, "FieldLabel");
                    colDef = colDef + "^" + colLabel; // + colLabel;
                    colDef = colDef + "^18"; // + krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Length");
                    colDef = colDef + "^3"; // + krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Decimals");
                    colDef = colDef + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "formatstring");
                    colDef = colDef + "^Toto je koment k stlpci " + colName; // + krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Comment");
                    XDBtableColumn aktCol = createColumn(colDef, colPositionInDispFlds, false);
                    alColumns.add(aktCol);
                }
            }
            }
            */
            // !!!!
            // 2013-11-22 - Prvy pokus o calculated-stlpec
            /*
                colDef = "!@@FUNCTION^" + sMasterTable; // dbname sa zatial ignoruje getDBname();
                colDef = colDef + "^FUN@PolkaIDu";
                colDef = colDef + "^DECIMAL"; //+ krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "DataType");
                colDef = colDef + "^!@@FUN"; // + krn.getDBcolumnInfo(sMasterTable, colName, "DefaultValue");
//               colDef = colDef + "^" + colName; // + krn.getDBcolumnInfo(sMasterTable, colName, "FieldLabel");
                colDef = colDef + "^PolkaIDu"; // + colLabel;
                colDef = colDef + "^18"; // + krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Length");
                colDef = colDef + "^3"; // + krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Decimals");
                colDef = colDef + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "formatstring");
                colDef = colDef + "^Toto je koment k stlpci PolkaIDu"; // + krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Comment");
                createColumn(colDef, false);
                iNumTblColumns++;
            */

            // 2013-5-27 - KUUURVA -- Nasledujuci riadok nam chybal,
            // + jeden riadok v CreateColumn() !  
            ///// 2014-8-18 myTblModel.setColumnCount(myTableColumnModel.getColumnCount());
            // column list sorting
            // tu sa urci spravne poradie pre vsetky stlpce do alColumns,
            //a vytvori sa utriedeny alSortedColumns
            //s je bud sDislpayedFields.length alebo iNumRsColumns.length
            //int s = sDisplayedFields.length;
            //int s = (((sDisplayedFields!=null) && sDisplayedFields.length > 0)
            //        ? sDisplayedFields.length : iNumDisplayedColumns);
            /*
            if ((sDisplayedFields!=null) && sDisplayedFields.length > 0) {
                int s = sDisplayedFields.length;
                for (int i = 0; i < alColumns.size();i++) {
                    TableColumn aktCol = alColumns.get(i);
                    //// System.out.println(aktCol.getFieldLabel()+ " " + aktCol.getModelIndex());
                    if (aktCol.getModelIndex() == -1) {
                        aktCol.setModelIndex(s);
                        //// System.out.println(aktCol.getFieldLabel()+ " update to:" + aktCol.getModelIndex());
                        s++;
                    }
                }
            }
            */
            // pridanie komparatora pre triedenie objektov typu XDBtableColumn
            Collections.sort(alColumns, new Comparator<XDBtableColumn>() {
                 @Override public int compare(XDBtableColumn p1, XDBtableColumn p2) {
                 return p1.getModelIndex() - p2.getModelIndex(); // Ascending
               }
               });
            //// System.out.println("after_sortingg:");
            for (int i = 0; i < alColumns.size();i++) {
                XDBtableColumn aktCol = alColumns.get(i);
                //// System.out.println(aktCol.getFieldLabel()+ " " + aktCol.getModelIndex());
            }            
      System.out.println("1CMC:" + myTableColumnModel.getColumnCount() + "\n" +
                         "1TMC:" + myTableModel.getColumnCount()); 
            deleteTableStructure();
      System.out.println("1bCMC:" + myTableColumnModel.getColumnCount() + "\n" +
                         "1bTMC:" + myTableModel.getColumnCount()); 
            for (int i = 0; i < alColumns.size();i++) {
                XDBtableColumn aktCol = alColumns.get(i);
                myTableModel.addColumn(aktCol);
               myTableColumnModel.addColumn(aktCol);
            }            
      System.out.println("2CMC:" + myTableColumnModel.getColumnCount() + "\n" +
                         "2TMC:" + myTableModel.getColumnCount()); 
            //for (int i = 1; i <= myTblModel.getColumnCount(); i++) {
            //     myTableColumnModel.addColumn(myTable.getColumn(i));
            //}
            myTable.getTableHeader().revalidate();
            myTable.getTableHeader().repaint();
            myTable.revalidate();
            myTable.repaint();

    } // public void buildTblFromResultSet() {
   
   private String packRetVal() {
       sRetVal = "";
       /* original-kod z vyber-objektu
       for(int i=0; i<sRetValCols.length; i++) {
           System.out.println("packRetValLLLfor: " + sRetValCols[i]);
           sRetVal = sRetVal + "," 
                   + xTB_vyber1.getScreenValue(sRetValCols[i]);
       }
       if (sRetVal.startsWith(",")) sRetVal = sRetVal.substring(1);
       if (!sRequiedPrimaryKey.equals(""))
           sRetVal = sRetVal + "^" 
                   + xTB_vyber1.getScreenValue(sRequiedPrimaryKey);
       */
       for(int i=0; i<sRetValCols.length; i++) {
           System.out.println("packRetValLLLfor: " + sRetValCols[i]);
           sRetVal = sRetVal + "," 
                   + getScreenValue(sRetValCols[i]);
       }
       if (sRetVal.startsWith(",")) sRetVal = sRetVal.substring(1);
       if (!sRequiedPrimaryKey.equals(""))
           sRetVal = sRetVal + "^" 
                   + getScreenValue(sRequiedPrimaryKey);
       if (krn.getVerboseLevel() < 6)
           krn.Message("QVyber.packRetVal() returning: " + sRetVal);
       
       return sRetVal;
       
   };
   
    // prevzate z XTable
    public String getScreenValue(String objName) {
       System.out.println("XTable.getScreenValue()for: " + objName + "atRoww:" + myTable.getSelectedRow()
       + "myTable.getModel().getcolcontt::" + myTable.getModel().getColumnCount());
       Object o = myTable.getModel().getValueAt(myTable.getSelectedRow(),
                        getTblColumnIndexBydbFieldName(myTable, objName));
       String s;
       s = o.toString();
       //if (o instanceof Integer) s = Integer.pa .parseInt(o.toString());
       
//       return (String) myTable.getModel().getValueAt(myTable.getSelectedRow(),
//                        getTblColumnIndexBydbFieldName(myTable, objName));
       return s;
    }

    // vrati poradove cislo stlpca v columnmodelu tabulky podla jeho nazvu
    // prevzate z XTable
    public int getTblColumnIndexBydbFieldName(JTable table, String name) {
        for (int i = 0; i < myTableColumnModel.getColumnCount(); i++) {
            XDBtableColumn tc = (XDBtableColumn) myTableColumnModel.getColumn(i);
            if (tc.getdbFieldName().toUpperCase().equals(name.toUpperCase())) {
//               krn.OutPrintln("getTblColumnIndexBydbFieldName-"
//                    + name + " " + tc.getModelIndex() + " - " + i );
                // QQQ - osetrit zmenu poradia v objekte tc ???
/////            System.out.println("Tesssting:" + name.toUpperCase() +" on " + tc.getdbFieldName().toUpperCase() + " returposition:" + tc.getModelIndex());
                return tc.getModelIndex();
            }
 ////           else
 ////           System.out.println("Tesssting:" + name.toUpperCase() +" on " + tc.getdbFieldName().toUpperCase());
        }
        return -1;
    }

    
    public String getMetodNamesFromColDef (String cd) {
       cd = cd.trim();
       System.out.println("Getting mtdn from: " + cd);
       String cdfs[] = cd.split("~");
       String mtn = "";
       String mn;
       for (String s: cdfs) {
           mn = s.trim();
           if (mn.startsWith("FUN@")) {
               mtn = mtn + "," + FnEaS.sEntry(1,mn.substring(3),"^");
           }
           //// else System.out.println("MIAFASZEZ?:" + mn);
       }
       if (mtn.length() > 0) mtn = mtn.substring(1); // odrezanie prevej ciarky
       System.out.println("getMetodNamesFromColDefRETURN:" + mtn);
       return mtn;
   }
/*   
    public void put_htMethods(eoc.IEOC_VisualObject findInObject ,String mtdNames,
            Hashtable<String, Method> ht, boolean bClear) {
        if (bClear) ht.clear();
        
    }
*/
   
    @Override
    public String receiveMessage(eoc.EOC_message eocMsg) {

        // musi byt ako prva instrukcia v metode !!!
        if (eocMsg==null) return FnEaS.nullEocMessageResponse(myObjectID);
        
        /////krn.krnMsg("receivingdblclick_message = " + sMessage);
        String receive_status = null;
        try {
             receive_status = 
                super.receiveMessage(eocMsg); 
        }
        catch (Exception ex) {
            // ked metoda neexistuje, spracuje sa to nizsie
       }
       if (eocMsg.getMessage().equalsIgnoreCase("MOUSE_DOUBLE_CLICKED")) {
           btn_OK.doClick();
         /* 
        krn.krnMsg("receivingdblclick_message >>>> ACTIOOOON-01 --> " + btn_OK.getX() + " - " + btn_OK.getY());
           ActionEvent aevt =
               new ActionEvent((Object)btn_OK, ActionEvent.ACTION_PERFORMED, "MOUSE_CLICKED");
        EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
        EventQueue qn = new EventQueue();
        synchronized(q) {
           q.postEvent(aevt);
           enableEvents(AWTEvent.MOUSE_EVENT_MASK);
           MouseEvent me;
           me = new MouseEvent((Component)btn_OK, MouseEvent.MOUSE_CLICKED, 
                            1, MouseEvent.BUTTON1, btn_OK.getX() + 1, btn_OK.getY() + 1, 1, false);
           q.postEvent(me);
           q.push(qn);
           
        }
        */
        ////krn.krnMsg("receivingdblclick_message >>>> ACTIOOOON-02");
       }
       return "";
    }
   public String getRetVal() {
      sRetVal = packRetVal();
      return sRetVal;
   }
    public void setMethodSource(Object mtdSrc) {
        methodSource = mtdSrc;
    }
    
    // mazanie pripadnych default stlpcov z ColumnModel-u
    private void deleteTableStructure() {
        while (myTableColumnModel.getColumnCount() > 0) {
          //  krn.debugOut(this, 4, "Removing ColumnModel column:"
              //      + myTableColumnModel.getColumn(0).getHeaderValue());
            myTableColumnModel.removeColumn(myTableColumnModel.getColumn(0));
        }
        // mazanie pripadnych default stlpcov z tabulky (z DBTableModel-u)
        while (myTable.getColumnCount() > 0) {
        //    krn.debugOut(this, 4, "Removing Table column:"
         //           + myTable.getColumn(0).getHeaderValue());
            myTable.removeColumn(myTable.getColumn(0));
        }
        // mazanie riadkov zo starej tabulky
        while (myTableModel.getRowCount() > 0) {
            myTableModel.removeRow(0);
        }
        myTableModel.setColumnCount(0);
    }

    public XDBtableColumn createColumn(String ColDefinition, int colposition, boolean bHidden) {
        XDBtableColumn myColumn = null;
            /* ColDefinition format:
             * dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
             * numdecimals^formatstring^tooltyp^HIDDEN
             * 
             * Ked sa tablename Zacina s !@@, jedna sa o specialny stlpec,
             * napriklad: !@@FUNCTION, !@@CALCULATED, !@@DBQUERY, ...
             * 
             * PRIKLAD VOLANIA METODY: createColumn("^^blaaablaaa^integer^4^BLABLA^...
             * 20^^>9.99^Toto je tuuultipp pre stlpec BLABLA");
             * ************************************************************************/
            int numEntr = FnEaS.iNumEntries(ColDefinition, "^");
            if (numEntr < 7) {
                krn.Message(this, "e", "Definícia stlpca musí mať aspoň 7 častí,"
                        + " delených znakom ^.\n\n" + ColDefinition,
                        "Chybná definície stľpca tabuľky");
                return null;
            }
            myColumn = new XDBtableColumn(krn, MyCn);
            myColumn.initialize(ColDefinition);
            ////System.out.println("COLPOSS:" + myColumn.getdbFieldName() + " === " + colposition);
            myColumn.setModelIndex(colposition);
            myColumn.setHeaderValue(myColumn.getFieldLabel());

            myColumn.setMyTable(myTable);
            // Zobudenie/ozivenie stlpca (pridaju sa rendereri a podobne)
            if (bHidden) {
                 myColumn.setHidden(bHidden);
                 myColumn.setMinWidth(0);
                 myColumn.setMaxWidth(0);
                 myColumn.setWidth(0);
                 myColumn.setResizable(false);
            }

            myTableModel.addColumn(myColumn);
        return myColumn;
    }

    

    /**
  * @param args the command line arguments
  */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_OK;
    private javax.swing.JButton btn_Zrus;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable myTable;
    // End of variables declaration//GEN-END:variables
}
