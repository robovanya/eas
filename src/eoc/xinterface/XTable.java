/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.xinterface;

import eoc.EOC_message;
import eoc.IEOC_Object;
import eoc.database.DBconnection;
import system.FnEaS;
import eoc.dbdata.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import system.FnEaS.*;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import system.Kernel;
// generic datove typy: string,integer,decimal,date,boolean
// PRIKLADY KODU 
// table.setValueAt(value, rowIndex, getColumnByName(table, colName));
// table.getValueAt(rowIndex, getColumnByName(table, colName));
// VYTVORENIE NOVEHO STLPCOVEHO-OBJEKTU V TABULKE
// dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
// numdecimals^formatstring^tooltyp 
// createColumn("^^blaaablaaa^integer^4^blabla1111^20^^>9.99^...
// Toto je tuuultipp pre stlpec blabla1111");

/**
 * EOC_dbtable Zaklad/rodic pre objekty typu browser databazovych tabuliek
 * 2013-1-28
 *
 * @author rvanya
 */
public class XTable extends eoc.widgets.PObject {

    // Typ EOC_XTable objektu (DBTABLE,FREEFORM -- zatial sa podporuje len typ DBTABLE
    String sXTableType = "";

    // Query-object related variables
    eoc.xinterface.XQuery myXQuery;
    ResultSetMetaData rsMetaData;
    private ResultSet crs;  // pracovny rowset na preberanie udajov z XQuery
    private ResultSet xcrs; // pracovny rowset na preberanie jednej vety z XQuery

    // Pointery/casti children-objektu (potomka)
    private JScrollPane    myScrollPane;
    private JViewport      myViewport;
    public  JTable         myTable;
    public  XTableModel    myTblModel; // = new DefaultTableModel();
    JButton urCornerButton = new JButton("▲");
    JButton lrCornerButton = new JButton("▼");
    JButton llCornerButton = new JButton(".");
    public DefaultTableColumnModel myTableColumnModel; // = new DefaultTableColumnModel();
    /*
     * XQuery AND XTable specific variables 
     * ***********************************************************************/
    private String sExternalTable = ""; // cudzia riadiaca tabulka, na ktorom 
    // je dotaz browsera zavysly
    private String sExternalKeyInExtTable = "<NONE>"; // meno stlpca cudzej tabulky, 
            // v cudzej tabule, na ktorom je dotaz browsera zavysly
    private String sExternalKeyInMyTable = "<NONE>"; // meno stlpca cudzej tabulky,
            // v internej tabulke, na ktorom je dotaz browsera zavysly
    private String sExternalKeyDataType = ""; // datovy typ sExternalKey (cudzi kluc)
    // char,number,date,datetime,boolean,...
    private String sCurrentExternalKeyValue = ""; // aktualna hodnota vyhladavaneho kluca
    private String sOldCurrentExternalKeyValue = "";
    private Object owRowSource; // Zdroj, poskytujuci hodnotu sExternalKey
    private String sMasterTable = ""; // hlavna tabulka dotazu
    // master key
    private String sMasterKey = ""; // vyhladavany stlpec hlavnej tabulky dotazu
    // pri spravne navrhnutej DB to je ekvivalent
    // sExternalKey
    private String sMasterKeyDataType = ""; // datovy typ sMasterkey
    // char,number,date,datetime,boolean,...
////    private String sCurrentMasterKeyValue = ""; // aktualna hodnota vyhladavaneho kluca
    // premenne, suvisiace s aktualnym triediacim klucom tabulky
    private String sOrderVector = "asc"; // smer triedenia stlpeca hlavnej tabulky dotazu
    private String sOrderKey = ""; // triediaci stlpec hlavnej tabulky dotazu
    private String sOrderKeyDataType = ""; // datovy typ sOrderkey
                                           // char,number,date,datetime,boolean,...f
    private String[] sSlaveTables; // podriadene tabulky v dotaze
    private int iNumTblColumns; // pocet VSETKYCH(calculated,others,RSrow) 
                                // stlpcov tabulky 
    private int iNumRsColumns; // Pocet stlpcov tabulky, nacitane z resultSet-u 
                               // (VZDY SU PRED ostatnymi stlpcami v table-model-u!)
    private String sDisplayedFieldDefinitions = ""; // kompletny retazec definicii
    // Rozobrate kusky definicii stlpcov 
    private int      iNumDisplayedColumns;     // pocet potrebnych stlpcov
    private String[] sDisplayedFields;         // potrebne stlpce
    private String[] sDisplayedFieldLabels;    // Label-y potrebnych stlpcov
    private String[] sDisplayedFieldDataTypes; // Datove typy potrebnych stlpcov
    private int[]    iDisplayedFieldLengths;      
    private int[]    iDisplayedFieldDecimals;      
    // casti retazca query prikazu 
    private String sQueryBase;          // select * from eas_usrgrp
    private String sQryAppWhere;      // !eas_usrgrp.bDisabled
    private String sQryUsrWhere;      // eas_usrgrp.c_meno = 'jozika'
    private String sQueryFullStatement; // poskladany dotaz
    private String sQueryPrevStatement; // predosly poskladany dotaz
    private String sFullQuery = "";         // uzivatelom zadany cely dotaz
    // Stavy objektu 
    private boolean bIsQryOpened = false; // ci je dotaz otvoreny
    private boolean bIsQryEmpty = true;   // ci je dotaz otvoreny
    private boolean bOnFirstRow;        // ci je na prvom riadku DB tabulky
    private boolean bOnLastRow;         // ci je na poslednom riadku DB tabulky
    // premenne, riadiace caching tabulky v browseri
    private boolean bTableCaching = false;
    private int iNumFetchedRows = -9999; // fullFetch //30;
    // private int iNumCachedChunks = 0;  // uz nacitane davky riadkov z RS
    private int iNumMaxChunks = 3;     // default
//    private int iCurrTableRow = 0;  // prave spracovany row (valueChanged event)
    private int iNumCachedRows = 0;     // pocet celkovych pridanych stlpcov v tabulke
    private int iLastErasedRows = 0;     // pocet naposledy uvolnenych stlpcov z tabulky
    private int iLastCachedRows = 0;     // pocet naposledy pridanych stlpcov do tabulky
    private int iCurrVisibleRows = 0; // pocet momentalne viditelnych riadkov tabulky
    // evidovane hodnoty stlpca sMasterKey (moze byt aj HIDDEN)
    private String sFirstMasterKeyValue = ""; // hodnota na 1. riadku tabulky
    private String sCurrentMasterKeyValue = ""; // hodnota na akt.riadku tabulky
    private String sOldCurrentMasterKeyValue = ""; // predosla hod. na akt.riadku tabulky
    private String sTestedMasterKeyValue = ""; // hodnota na akt.riadku tabulky
    private String sLastMasterKeyValue = ""; // hodnota na posl.riadku tabulky
    // evidovane hodnoty stlpca sOrderKey (moze byt aj HIDDEN)
    private String sFirstOrderKeyValue = ""; // hodnota na 1. riadku tabulky
    private String sCurrentOrderKeyValue = ""; // hodnota na akt.riadku tabulky
    private String sLastOrderKeyValue = ""; // hodnota na posl.riadku tabulky
    private Object owOpposite; // objekt na druhej strane Link-u 
    private boolean bPom; // pomocna premenna
    // pozicia ukazovatela - nastavuje value_changed event
    private boolean bTblFirstRow; // browser je na prvom riadku DB-tabulky
    private boolean bTblLastRow; // browser je na poslednom riadku DB-tabulky
    private boolean bDbFirstRow; // browser je na prvom riadku DB-tabulky
    private boolean bDbLastRow; // browser je na poslednom riadku DB-tabulky
    private String sCurrentRowStatus = ""; // stav browsera podla pozicie riadku v dotaze
                                           // 
    // premenne pre odchytenie udalosti rowDisplay
    private int rowHeight = 16; // default je 16 pre prazdne JTable
    private int lastDisplayedRow = -1;  // posledny 
    // ZEBRA kodovanie    
    //private java.awt.Color rowColors[] = new java.awt.Color[2];
    private boolean drawStripes = true;
    
    private int    currSelectedRow = -1;  // vlastna rezia selektovania riadkov
                                          // ( -1 => tabulka je bez riadkov )
    private String oldMovingVector  = "DOWN";
    private String currMovingVector = "DOWN";
    ////private ColumnHeaderRenderer ColHdrRenderer;
    private Object methodSource;
    //private String lastFirstRequiredOrderKeyValue = "";
    ///private int   lastEqualFirstRequiredOrderKeyCounter = 0;
    String sCurrentReadVector; /* UP/DOWN - smer citania dalsich viet do tabulky*/
    boolean bCurrentIncludeKey; // vcetne riadku s FirstRequiredOrderKeyValue

    private EOC_message eocRowStatusMsg;
    
    private String localObjPerms;
    private EOC_message eocAfterInitGoToRowMsg;
    
    //private XDObject rowViewerXDObject;
    private class ColumnStateUpdateListener implements TableColumnModelListener {
    
   int lastFrom = -1;
   int lastTo = -1;
   
   private void verifyChange(int from, int to) {
      if ((from == to) && (from != lastFrom)/* || to != lastTo*/) {
          if (lastFrom != -1) { // nie je to skutocny posun stlpca iba prvy dotyk
              //krn.OutPrintln("Column moved !! - from pos:" + lastFrom + " to pos:" + to); 
              String s = "";
              //String tm = "";
              //String t = "";
              for (int i = 0; i < myTableColumnModel.getColumnCount(); i++) {
                  XDBtableColumn tc = (XDBtableColumn) myTableColumnModel.getColumn(i);
                  s = s + tc.getdbFieldName() + " ";
                  //tm = tm + myTblModel.getValueAt(iCurrentTableRow,i) + " ";
                  //t = t + myTable.getValueAt(iCurrentTableRow,i) + " ";
              }
              s = s + "  row=" + currSelectedRow;
              // krn.OutPrintln("Column sort: " + s + "\n" + tm + "\n" + t);
          }
          
         lastFrom = from;
         lastTo = to;
         ///////////////////////////////////////
         // Column order has changed!  Do something here
         ///////////////////////////////////////
      }
   }

   public void columnMoved(TableColumnModelEvent e) {
      // e.mausdragged ??? treba odchytit
      verifyChange(e.getFromIndex(), e.getToIndex());
   }

   public void columnAdded(TableColumnModelEvent e) {
      verifyChange(e.getFromIndex(), e.getToIndex());
   }

   public void columnRemoved(TableColumnModelEvent e) {
      verifyChange(e.getFromIndex(), e.getToIndex());
   }

   public void columnMarginChanged(ChangeEvent e) {}
   
   public void columnSelectionChanged(ListSelectionEvent e) {}

}
    
    public int getCurrSelectedRow() {
        return currSelectedRow;
    }
    
    public void setCurrSelectedRow(int iRow, String sFlag) {
          // sFlag sluzi na presnejsiu detekciu miesta volania metody (hodnoty typu A,B,..
          // v kode volajucej metody, pokial sa vola z viacerych pozicii.
          // Ked sa vola iba raz, staci zadat "", nazov volajucej metody sa aj tak 
          // vypisuje metodou krn.OutPrintln (......)
////koookotinaaa          if (currSelectedRow == iRow) { return; }
//        krn.OutPrintln(FnEaS.getCallerMethodName() + " -- Setting currSelectedRow to " + iRow);
          currSelectedRow = iRow;
    }
    
    @Override
    public String initialize(system.Kernel kr, DBconnection cX) {
        if (bInitialized) return "";
        super.initialize(kr, cX);
        
        if (myObjectID == null) { 
            setMyObjectID(this);
        }
       /// ColHdrRenderer = new ColumnHeaderRenderer(myTable);
        setEOC_objectType("dbtable");
        if (myXQuery==null) {
            myXQuery = new XQuery();
        }
        myXQuery.initialize(kr, cX); // inacializacia query-objektu
        // test platneho typu aktualneho objektu
        if (!sXTableType.equals("DBTABLE")) {
            krn.Message(this, "E", "Typ Xtable '" + sXTableType + "' nie je podporovaný."
                    + "\n\nNastavte typ Xtable objektu pred volaním metódy Initialize."
                    + "\n\nInicializácia Xtable objektu bolo odmietnuté!", "");
            return "XTableType value ERROR: Unsupported type: " + sXTableType;
        }
        // nastavenie triedenia dotazu
        myXQuery.setOrdering(sOrderKey, sOrderVector, sOrderKeyDataType);

        // QQQ - TU BY MAL BYT TEST OBSAHU ZVONKU NASTAVOVANYCH PREMENNYCH 

        // Konfiguracia vlastnosti tabulky
        // =============================================================
        // aby tableModel nevytvaral Column-y v ColumnModeli automaticky
        Font f = krn.getDefaultFont();
        myTable.setFont(f);
        Integer[] i = Kernel.getFontSize("M", f);
        int ii =  i[0];
        myTable.setRowHeight(ii + (myTable.getRowMargin() * 2) + 6);
        myTable.setAutoCreateColumnsFromModel(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        myTable.setAutoscrolls(false);

        // vytvorenie vlastneho tableModel-u
////        myTblModel = new eoc.dbdata.DBTableModel();
        myTblModel = new XTableModel();
        myTable.setModel(myTblModel);

        // vytvorenie vlastneho tableColumnModel-u
        myTableColumnModel = (DefaultTableColumnModel) myTable.getColumnModel();
        Component dsk = krn.getDsk();
        dsk.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Vycistenie tableModel-u a tableColumnModel-u
        // od nepotrebnych/default stlpcov
        //===============================================================
        // mazanie default stlpcov z ColumnModel-u
        while (myTableColumnModel.getColumnCount() > 0) {
         //   krn.debugOut(this, 5, "Removing ColumnModel column:"
         //           + myTableColumnModel.getColumn(0).getHeaderValue());
            myTableColumnModel.removeColumn(myTableColumnModel.getColumn(0));
        }
        // mazanie default stlpcov z tabulky (z DBTableModel-u)
        while (myTable.getColumnCount() > 0) {
         //   krn.debugOut(this, 5, "Removing Table column:"
        //            + myTable.getColumn(0).getHeaderValue());
            myTable.removeColumn(myTable.getColumn(0));
        }
        
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // nastavenie velkosti cache-ovania v query-objekte
        myXQuery.setiNumFetchedRows(iNumFetchedRows);
        // vytvorenie vlastneho dotazu
        this.rebuildQuery();
        // vytvorenie tabulky zo vzniknuteho dotazu
        if (sFullQuery.length() > 0) 
           this.buildTblFromResultSetQuery();
        else   
           this.buildTblFromResultSetStandard();
        // otvorenie vlastneho dotazu
        ////krn.OutPrintln("OPE_QRY 01");
        ////System.out.println("XTABLEEA");
        this.open_Query();
        ////System.out.println("XTABLEEB");
        bTableCaching = false;
        // skok na prvy riadok tabulky
        myTable.changeSelection(0, 0, false, false);
        setCurrSelectedRow(0,"");
        dsk.setCursor(Cursor.getDefaultCursor());

        // Vytvorenie odchytavaca udalosti vyberov v tabulke
        myTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            
        @Override
        public void valueChanged(ListSelectionEvent e) {
            
            boolean noConsume = bTableCaching 
            //| e.getValueIsAdjusting() 
            //| (e.getLastIndex() == currSelectedRow)
                    ;
            /* dolezity test, NEMAZAT !!!
            if (noConsume && krn.getEASobjectName((Component) myObjectID).equals("dbt_bytovy_fond_vcs"))
            krn.OutPrintln(FnEaS.getCallerMethodName() + " bTableCaching is " + bTableCaching + " !"
            + "\ne.getValueIsAdjusting()=" + e.getValueIsAdjusting()
            + " fi=" + e.getFirstIndex() + " li=" + e.getLastIndex()                    
            + " currSelectedRow=" + currSelectedRow                    
            + "\nnoConsume=" + noConsume + "  object ==" + krn.getEASobjectName((Component) myObjectID)
            );
            */
            if (noConsume) return;
            
            /* dolezity test, NEMAZAT !!!
            if (krn.getEASobjectName((Component) myObjectID).equals("dbt_bytovy_fond_vcs")) 
            //if (e.getLastIndex() == currSelectedRow) return;
            krn.OutPrintln(FnEaS.getCallerMethodName() + " bTableCaching is " + bTableCaching + " !"
            + "\ne.getValueIsAdjusting()=" + e.getValueIsAdjusting()
            + " fi=" + e.getFirstIndex() + " li=" + e.getLastIndex()                    
            + " currSelectedRow=" + currSelectedRow                    
            + "\nConsuming event.  object =="
            + krn.getEASobjectName((Component) myObjectID));
            */
            _valueChanged_event();  // Vykonny kod pre spracovanie udalosti valueChanged
           } // public void valueChanged(ListSelectionEvent e) {
        }); // .addListSelectionListener
        
        // odrolovanie aktualnej vety do viditelnej oblasti tabulky
        myTable.scrollRectToVisible(myTable.getCellRect(currSelectedRow , 0, true));

        // odstranenie default-triedenia
        myTable.setRowSorter(null);
        myTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
 //              System.out.println("XTable-DOBLECLSCLS???===" + e.getClickCount());
                if (e.getClickCount() > 1 /* == 2 */ ) {
                    rowDblClicked();
//                    System.out.println("XTable-DOBLECLSCLS");
                    e.consume();
                    return;
                }
            }    
            });            
        // Vytvorenie ovladaca vlastneho triedenia, ktory pouziva XQuery-caching
        myTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
 //                   System.out.println("DOBLECLSCLS???===" + e.getClickCount());
                if (e.getClickCount() == 2) {
 //                   System.out.println("DOBLECLSCLS");
                    e.consume();
                    return;
                }
                if (e.isConsumed()) return;
                /*
                krn.krnMsg("MouseClickedINHeader " + e.getClickCount()
                + "\n" + e.getID() + " " + e.isConsumed() 
                + " " + e.getModifiers()
                + "\n"+ e.getSource().toString());
                */
                boolean orderChanged = false;
                eoc.dbdata.DBtableColumn currTC;
                Character lastCh;
                int columnIndex = myTable.getTableHeader().columnAtPoint(e.getPoint());
               ///// bulo by treba odlozit label bez znaku, alebo column-objekt
                // aktualne kliknuty stlpec
                currTC = (DBtableColumn) myTable.getColumnModel().getColumn(columnIndex);
                /*  QQQ 2015-09-17 -
                 *  isIdxd docasne vypnute, aby neotravovalo - 
                 *  asi treba rozsirit o test, ci ma tabulka vela riadkov.
                 *  inak to je smiesne, ked sa pyta aj ked tabulka ma 3-4 riadky :-)
                 * 
                boolean isIdxd = krn.isIndexed(MyCn, currTC.getdbTableName(),currTC.getdbFieldName());
                // test, ci je stlpec bezpecne trieditelny bez vysokeho naroku 
                // na potrebny cas a serverovske prostriedky
                if (!isIdxd) {
                   if (!krn.krnQuest("WN", "Stľpec " + currTC.getdbTableName() + "."  
                           + currTC.getdbFieldName() + " nie je v databáze indexovaný !" 
                           + "\n\nTriedenie tabuľky môže byť pri veľkom počte viet "
                           + "časovo náročné .\n\nPOKRAČOVAŤ ?", "POZOR!")) {
                      return ;  
                   } 
                }
                */
                // QQQ vysvietit header stlpca, ak ho prave filtruje ???
                if (!currTC.isSortable()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                myTable.setColumnSelectionInterval(columnIndex, columnIndex);
                lastCh = currTC.getFieldLabel().charAt(currTC.getFieldLabel().length() - 1);

                deleteSortVectorIconFromColHeaders();
                
                if (lastCh != '▲' && lastCh != '▼') {
                    currTC.setfieldLabel(currTC.getFieldLabel() + " ▲");
                    currTC.setSortVector("asc"); // first sort ascending
                    orderChanged = true;
                }
                if (lastCh == '▲') {
                    // mazanie nepotrebnych vektor sipok, ked existuje
////                    deleteSortVectorIconFromColHeaders();
                    currTC.setfieldLabel(currTC.getFieldLabel().substring(0,
                    currTC.getFieldLabel().length() - 2) + " ▼");
                    currTC.setSortVector("desc"); // sort descending after ascending sort
                    orderChanged = true;
                } 
                if(lastCh == '▼'){
                    // mazanie nepotrebnych vektor sipok, ked existuje
/////                    deleteSortVectorIconFromColHeaders();
                    currTC.setfieldLabel(currTC.getFieldLabel().substring(0,
                           currTC.getFieldLabel().length() - 2) + " ▲");
                    myTblModel.setRowCount(0); // mazanie riadkov tabulky - 2016-03-09
                    currTC.setSortVector("asc"); // sort ascending after descending sort
                }
               /// krn.OutPrintln("setOrderingFrom_XTable.mouseClicked");
                /*
                myXQuery.invalidate();
                myXQuery.setOrdering(currTC.getdbFieldName(), currTC.getSortVector(),
                        currTC.getDataType() + " --> " +  currTC.getFieldLabel());
                */
                setOrdering(currTC.getdbFieldName(), currTC.getSortVector(),
                        currTC.getDataType() + " --> " +  currTC.getFieldLabel());
                myTableColumnModel.getColumn(columnIndex).setHeaderValue(currTC.getFieldLabel());

                e.consume(); // 2014-8-20 som zistil, ze udalost mouseclick 
                             // sa vykona na hlavicke stlpca dvakrat.
                             // Pred tym to fungovalo. Hm.
                             // Preto  to testujem odteraz na zaciatku triggra.
                revalidate();
                repaint();  // !!! bez tohoto sa neobjavi novy headervalue na obrazovke !
                
          }
        });

        // Vytvorenie ovladaca pre stlacenie klaves manipulujuce s tabulkou
        // (Up,Down,PgUp,PgDown,Ctrl+Home,Ctrl+End)
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
                    _valueChanged_event();  // Vykonny kod pre spracovanie udalosti valueChanged
                    return;
                }
                //ctrl+i
                if (e.getKeyCode() == 73 && e.isControlDown()) {
                   // mazanie nepotrebnych vektor sipok, ked existuje
                    clearOrdering();
                    return;
                }
                // klavesy - Ctrl+j
                if (e.getKeyCode() == 74 && e.isControlDown()) {
                    krn.Message(myObjectID, "I", 
                        "(Dúfam teda, že by ste to sám nenapísali inak :-))\n\n" 
                        + sQueryFullStatement + "\n\nCACHE_SIZE:" + 
                       (iNumFetchedRows == -9999 ? "ALL" : iNumFetchedRows)
                            , "Aktuálny dotaz (ctrl+j)");
                    //e.consume();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                   //goToPrev("");
             currSelectedRow = myTable.getSelectedRow() - 1;
           _valueChanged_event();  // Vykonny kod pre spracovanie udalosti valueChanged
                   // e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_UP) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    //goToNext("");
            currSelectedRow = myTable.getSelectedRow() + 1;
            _valueChanged_event();  // Vykonny kod pre spracovanie udalosti valueChanged
                   // e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_DOWN){
                /* // tieto su presmerovavane spat
                */
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ) {
                    _evt_vk_page_down();
                    e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && ...

                if (e.getKeyCode() == KeyEvent.VK_PAGE_UP ) {
                    _evt_vk_page_up();
                    e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_PAGE_UP && riadok ==0){
                
                if (e.getKeyCode() == KeyEvent.VK_HOME && e.isControlDown()) {
                    goToFirst(null);
                    e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_PAGE_UP && riadok ==0){
                if (e.getKeyCode() == KeyEvent.VK_END && e.isControlDown()) {
                    goToLast(null);
                    e.consume();
                } // if(e.getKeyCode() == KeyEvent.VK_PAGE_UP && riadok ==0){
                // klavesy - Ctrl+i

            } // public void keyPressed(KeyEvent e) {

            @Override
            public void keyReleased(KeyEvent e) { // nespracovava sa
            }
        }); // .addKeyListener
        
        myScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int akt = myScrollPane.getVerticalScrollBar().getValue()
                        + myScrollPane.getVerticalScrollBar().getModel().getExtent();

                if (bTableCaching || bIsQryEmpty || !bIsQryOpened) {
                    return;
                }
                
                if (akt == myScrollPane.getVerticalScrollBar().getMaximum()) {
                    //"!@@FIRST" je specialny znak pre updateTableModelUpOrDown()
                    // mozno treba pouzit  sLastOrderKeyValue
                    //krn.OutPrintln("adjustmentValueChanged->DOWN->sCurrentOrderKeyValue= " + getTblMaxOrderKeyVal());
////                    to vyzera ako v tomto roku komentovane  111
                   //060116 QQQQ updateTableModelUpOrDown(getTblMaxOrderKeyVal(), "DOWN", false);
                }
                if (akt == myScrollPane.getVerticalScrollBar().getMinimum()) {
                   //// krn.OutPrintln("adjustmentValueChanged->UP->sCurrentOrderKeyValue= " + getTblMinOrderKeyVal());
                    //060116 QQQQ updateTableModelUpOrDown(getTblMinOrderKeyVal(), "UP", false);
                }
                
            } // public void adjustmentValueChanged(AdjustmentEvent e) {
            
        }); // myScrollPane.getVerticalScrollBar() ...>>>

        // zratuvanie poctu viditelnych riadkov tabulky
        myScrollPane.addComponentListener ( new ComponentAdapter ()
        {
            public void componentResized ( ComponentEvent e )
            {
                Rectangle vr = myTable.getVisibleRect ();
                int visibleRows = 0;
                Dimension extentSize = myViewport.getExtentSize();     // JViewport  
                Rectangle viewRect = myViewport.getViewRect();         //         methods  
                Rectangle visibleRect = myViewport.getVisibleRect();   // JComponent method  
                Point position = myViewport.getViewPosition();         //  JViewport method  
                int toppRow = myTable.rowAtPoint(position);  
                visibleRows = extentSize.height/myTable.getRowHeight();  
                
                if (iCurrVisibleRows != visibleRows) {
                    krn.debugOut(myObjectID, 0, 
                       "Changing currently visible rows to: " + visibleRows);
                }
                iCurrVisibleRows = visibleRows;
            } // omponentResized ( ComponentEvent e )
        } // myScrollPane.addComponentListener
        );       

        myTblModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // NEMAZAT  -  zatial toto nepotrebujem, ale moze sa to este zist
           }
    });
        
        // odchytenie udalosti rowdDisplay (ked sa objavuje novy riadok na obrazovke)
        // zatial odstavene
         myViewport.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                /*
                krn.OutPrintln("viewPort::> " +  myViewport.getX() + "," + myViewport.getY()
                        + " -- " + myViewport.getBounds().toString() + "ext.size -- " + myViewport.getExtentSize()
                        + " first row=>" +  myTable.rowAtPoint(new Point(1,1))
                        + " last row=>" +  myTable.rowAtPoint(new Point(1,myViewport.getExtentSize().height))
                        + " last row=>" +  myTable.rowAtPoint(e..getPoint()))
                        + " \n" + e.toString()
                        );
                        * */
            }
             
            }
        );
         
        myTableColumnModel.addColumnModelListener(new ColumnStateUpdateListener());
        myScrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, urCornerButton);
        myScrollPane.setCorner(JScrollPane.LOWER_TRAILING_CORNER, lrCornerButton);
/////        myScrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, llCornerButton);
        lrCornerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               goToLast(null);
            }
        });
        urCornerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               goToFirst(null);
            }
        });
        bInitialized = true;
        ////krn.OutPrintln("OPE_QRY 02");
        open_Query();
        bTableCaching = false;
        currSelectedRow = 1;
        goToFirst(null);
        _valueChanged_event(); // vyvolanie udalosti nasilu po vypnuti tableCaching-u

        return "";
    } // initialize
    
    
    @Override
    public String afterInitialize() {
        super.afterInitialize();
        if (eocAfterInitGoToRowMsg!=null) {
            ////System.out.println("AFTGOTTOROW: " + eocAfterInitGoToRowMsg.getParameters()
            //// + "  INOBJ:" + FnEaS.sObjName(myObjectID) + " BEGIN");
            goToRow(eocAfterInitGoToRowMsg);
            /////System.out.println("AFTGOTTOROW: " + eocAfterInitGoToRowMsg.getParameters()
            ///// + "  INOBJ:" + FnEaS.sObjName(myObjectID) + " END");
            
        }
        AWTEvent evt;
        // aplikovanie udalosti na scroll-pane
        //evt = new java.awt.event.AdjustmentEvent(null, AdjustmentEvent.ADJUSTMENT_LAST, WIDTH, WIDTH)
        //    (source, event type, adjust type, event value)
        /*
        evt = new java.awt.event.AdjustmentEvent(null,      // source
                AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,   // event type
                AdjustmentEvent.TRACK, // adjust type
                AdjustmentEvent.
                );*/

                //        adjustmentEvent(AWTEvent.ADJUSTMENT_EVENT_MASK) {};
      //  myScrollPane.dispatchEvent(evt);
        myScrollPane.repaint();
        myScrollPane.revalidate();
            bTableCaching = false;
            myTable.changeSelection(0, 0, false, false); // namiesto bFocus==true
// 2016-01-21            setCurrSelectedRow(0,"");
// 2016-01-21            setCurrentRowStatus("firstRow");
        
        if (bIsQryEmpty) {
            ////System.out.println("EMMMTYQRY_IN:" + FnEaS.sObjName(myObjectID));
            setCurrentRowStatus("noRowAvailable");
               // 2016-01-21  krn.krn_sendMessage((Object) this,
               //            "noRowAvailable", "", "ROW", "TARGET", "");
        }
        else {
            setCurrSelectedRow(0,""); // 2016-01-21
// 2016-01-21           setCurrentRowStatus("firstRow");
            _valueChanged_event();
        }
        return "";
    } 
    
    @Override
    public String setConn(DBconnection cX) {
        super.setConn(cX);
        if (myXQuery==null) {
            myXQuery = new XQuery();
        }
        myXQuery.setConn(cX);
        return "";

    }

    @Override
    public String setKrn(system.Kernel krnl) {
        super.setKrn(krnl);
        myXQuery.setKrn(krnl);
        return "";

    }

    public String setXTableType(String tbtype) {
        if (!(tbtype.equals("DBTABLE") | tbtype.equals("FREEFORM"))) {
            krn.Message(this, "E", "Nepodporovaný typ XTable: " + tbtype, "Chyba");
            return "UNSUPPORTED_XTable-type";
        }
        sXTableType = tbtype;
        return "";
    }

    public String getXTableType() {
        return sXTableType;
    }
    public XDBtableColumn createColumn(String ColDefinition, int colposition, boolean bHidden) {
        XDBtableColumn myColumn = null;
        try {
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

            myColumn.setMyXTable((eoc.xinterface.XTable) this);
            // Zobudenie/ozivenie stlpca (pridaju sa rendereri a podobne)
            if (bHidden) {
                 myColumn.setHidden(bHidden);
                 myColumn.setMinWidth(0);
                 myColumn.setMaxWidth(0);
                 myColumn.setWidth(0);
                 myColumn.setResizable(false);
            }

            myTblModel.addColumn(myColumn);
        } catch (Exception ex) {
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } // try {
        return myColumn;
    }

    public String getsCurrentMasterKeyValue() {
        return sCurrentMasterKeyValue;
    }

    public void setsCurrentMasterKeyValue(String sMasterKeyValue) {
        sCurrentMasterKeyValue = sMasterKeyValue;
        if (sXTableType.equals("DBTABLE")) {
            myXQuery.setMasterKeyValue(sMasterKeyValue);
        }
    }

    public String getsOrderKeyValue() {
        return sCurrentOrderKeyValue;
    }

    public String getExternalTable() {
        return sExternalTable;
    }

    public void setsExternalTable(String sExternTbl) {
        sExternalTable = sExternTbl;
        if (sXTableType.equals("DBTABLE")) {
            myXQuery.setExternalTable(sExternTbl);
        }
    }

    public String[] getDisplayedFields() {
        return sDisplayedFields;
    }

    public void setDisplayedFields(String sDispFlds) {
        sDisplayedFieldDefinitions = sDispFlds;
        // sDispFlds = sDispFlds.replace(" ", "");
        int    numFields = FnEaS.iNumEntries(sDispFlds, ",");
        //// System.out.println(">>>>>setDisplayedFields():::" + numFields + " -- " + sDispFlds);
        iNumDisplayedColumns  = numFields; 

        sDisplayedFields         = new String[numFields];
        sDisplayedFieldLabels    = new String[numFields];
        sDisplayedFieldDataTypes = new String[numFields];
        iDisplayedFieldLengths   = new int[numFields];   
        iDisplayedFieldDecimals  = new int[numFields];      

        String sCurrColDef;
        int    iNumColAttrs;
        //citanie jednotlivych definicii stlpcov
        for (int i = 1; i <= numFields; i++) { 
            sCurrColDef = FnEaS.sEntry(i, sDispFlds, ",").trim(); // definicia jedneho stlpca
            iNumColAttrs = FnEaS.iNumEntries(sCurrColDef, "^"); // pocet vlastnosti v definicii
            // citanie vlastnosti definicie stlpca (poradie vlastnosti je povinne!)
            // prva vlastnost je vzdy nazov stlpca
            sDisplayedFields[i - 1] = FnEaS.sEntry(1, sCurrColDef, "^");
            if (iNumColAttrs > 1) 
            for (int f = 2; f <= iNumColAttrs; f++) {
               if (f==2) sDisplayedFieldLabels[i - 1] = FnEaS.sEntry(f, sCurrColDef, "^");
               else if (f==3) sDisplayedFieldDataTypes[i - 1] = FnEaS.sEntry(f, sCurrColDef, "^");
               else if (f==4) sDisplayedFieldDataTypes[i - 1] = FnEaS.sEntry(f, sCurrColDef, "^");
               else if (f==5) sDisplayedFieldDataTypes[i - 1] = FnEaS.sEntry(f, sCurrColDef, "^");
            } 
        }
    }

    public String getExternalKeyInExtTable() {
      //// krn.OutPrintln("XTable-getsExternalKey() returning: " + sExternalKey);
        return sExternalKeyInExtTable;
    }
    
    public String getExternalKeyInMyTable() {
      //// krn.OutPrintln("XTable-getsExternalKey() returning: " + sExternalKey);
        return sExternalKeyInMyTable;
    }

    public String getsExternalKeyDataType() {
    ////   krn.OutPrintln("XTable-getsExternalKeyType() returning: " + sExternalKeyDataType);
        return sExternalKeyDataType;
    }

    public String getsCurrentExternalKeyValue() {
       krn.OutPrintln("XTable-getsCurrentExternalKeyValue() returning: " + sCurrentExternalKeyValue);
        return sCurrentExternalKeyValue;
    }

    public String setsExternalKeyInExtTable(String sKeyName, String sKeyDataType) {
        sExternalKeyInExtTable = sKeyName;
        sExternalKeyDataType   = sKeyDataType;
        myXQuery.setExternalKeyInExtTable(sExternalKeyInExtTable, sExternalKeyDataType);
        return "";
    }

    public String setExternalKeyInMyTable(String sExtKeyName, String sKeyDataType) {
        sExternalKeyInMyTable = sExtKeyName;
        sExternalKeyDataType = sKeyDataType;
        myXQuery.setExternalKeyInMyTable(sExternalKeyInMyTable, sExternalKeyDataType);
        return "";
    }

    public Object getoRowSource() {
        //!@@ sem patri asi vratenie XQuery objektu, ale to je este otazne 
        // - 2013.5.8 - (den vytazstva :-)) )
        return owRowSource;
    }

    public void setoRowSource(eoc.IEOC_Object owRwSrc) {
        //!@@ sem patri asi vlozenie XQuery objektu, ale to je este otazne 
        // - 2013.5.8 - (den vytazstva :-)) )
       if (owRowSource==null) {
            owRowSource=krn.getLinkPartner(myObjectID, "ROW", "SOURCE");
        }
    }

    public void setMyTable(JTable jtbl) {
        myTable = jtbl;
    }

    public void setMyScrollPane(JScrollPane jscp) {
        myScrollPane = jscp;
        myViewport = myScrollPane.getViewport();
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
        while (myTblModel.getRowCount() > 0) {
            myTblModel.removeRow(0);
        }
    }
   public String getsExternalKeyInExtTable() {
       return sExternalKeyInExtTable;      
   }

   public String getsExternalKeyInMyTable() {
       return sExternalKeyInMyTable;
   }
   
   public String mukk() {
       return "MUKK BASMEKKsExternalKeyInExtTable== " + sExternalKeyInExtTable + " keyvaLLUE:" + sExternalKeyInExtTable;
   }
   
    // vytvorenie tabulky z resultset-u, zatial problematicke, bolo by treba mat 
    // definicie formatu, labelov, typu objektu pre ukazovanie hodnot, ... ulozene v DB,
    // co poznaju len 4GL prostredia (z hruba) (PROGRESS, POWER BUILDER, SAP ABAP, ...)
    public void buildTblFromResultSetStandard() {
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
        List<XDBtableColumn> alColumns = new ArrayList<>();
        
        try {
            deleteTableStructure(); // mazanie pripadnych default stlpcov
            myTable.setAutoCreateColumnsFromModel(false);
            rsMetaData = myXQuery.getrsMetaData();
            if (rsMetaData==null) {
                krn.Message("E", "myXQuery.getrsMetaData() - XQuery_ERROR", "NULL-ResultSet.metadata");
                return;
            }
            String colDef;   // Column definition format string
            String colName;  // meno stlpca resultsetu
            int colPositionInDispFlds = -1; // pozicia stlpca v sDisplayedFields
            String colLabel = ""; // Nadpis stlpca
            Boolean bHidden = false; // schovany stlpec
            iNumRsColumns = rsMetaData.getColumnCount();
            if ((sDisplayedFields!=null) && sDisplayedFields.length > 0) {
                iNumDisplayedColumns = sDisplayedFields.length;
            } else {
                iNumDisplayedColumns = iNumRsColumns;
            }
            krn.debugOut(this, 5, "buildTblFromResultSet()BEGIN -> "
                    + "myTableColumnModel.getColumnCount()="
                    + myTableColumnModel.getColumnCount()
                    + " myTable.getColumnCount()=" + myTable.getColumnCount()
                    + " myTable.getModel().getColumnCount()="
                    + myTable.getModel().getColumnCount());
            
            for_block:
            for (int i = 1; i <= iNumRsColumns; i++) {
                colName = rsMetaData.getColumnName(i);
                //////krn.OutPrintln("CREATING COLUMN: " + colName);
                bHidden = false;
                // vsetky stlpce sa beru z databazovej tabulky
////                System.out.println(">>>><0>>>>sDisplayedFields.length==" + sDisplayedFields.length + " /" + colName + " /" + colLabel);
                if ((sDisplayedFields!=null) && sDisplayedFields.length > 0) {
                    colPositionInDispFlds = -1;
                    for (int ie = 0; ie < sDisplayedFields.length; ie++) {
                        if (colName.toUpperCase().equals(sDisplayedFields[ie].toUpperCase())) {
                            colPositionInDispFlds = ie;
                            break;
                        }
                    }
                    if (colPositionInDispFlds == -1) {
                        bHidden = true;
                        colLabel = colName;
                    }
                    else {
                    // ziskanie nadpisu stlpca  
                    colLabel = sDisplayedFieldLabels[colPositionInDispFlds];
                    if (colLabel == null) colLabel = "";
                    }
                } // if ((sDisplayedFields!=null) && sDisplayedFields.length > 0)
                else { /* sDisplayedFields == null */
                    colLabel = colName;
                } // zatial sa to neda brat z DB
////                System.out.println(">>>><1>>>>sDisplayedFields.length==" + sDisplayedFields.length + " /" + colName + " /" + colLabel);
                // istota je gulomet :-)
                if (colLabel.equals("") /*pokial nebol urceny v sDisplayedFields*/) {
                    colLabel = colName;
                }
                //      dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
                //      numdecimals^formatstring^tooltyp^HIDDEN
                // coldef sa zalozi podla typu dotazu
                // colname bude label, a datovy typ sa neda zistiti, tak ze String
                 // jednotabulkovy standard
                colDef = "^" + sMasterTable; // dbname sa zatial ignoruje getDBname();
                colDef = colDef + "^" + colName;
                colDef = colDef + "^" + MyCn.getDbDriver().getDBcolumnInfo(MyCn.getConn(), sMasterTable, colName, "DataType");
                colDef = colDef + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "DefaultValue");
//               colDef = colDef + "^" + colName; // + krn.getDBcolumnInfo(sMasterTable, colName, "FieldLabel");
                colDef = colDef + "^" + colLabel;
                colDef = colDef + "^" + MyCn.getDbDriver().getDBcolumnInfo(MyCn.getConn(), sMasterTable, colName, "Length");
                colDef = colDef + "^" + MyCn.getDbDriver().getDBcolumnInfo(MyCn.getConn(), sMasterTable, colName, "Decimals");
                colDef = colDef + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "formatstring");
                colDef = colDef + "^" + MyCn.getDbDriver().getDBcolumnInfo(MyCn.getConn(), sMasterTable, colName, "Comment");

                /////krn.OutPrintln("CREATING COLUMN: " + colName + " WITH LABEL: " + colLabel);
                XDBtableColumn aktCol = 
                        createColumn(colDef, colPositionInDispFlds, bHidden);
                alColumns.add(aktCol);
                
                iNumTblColumns++;
            } // for (int i = 1; i <= numColumns; i++)
            // 2015-11-16 - pridanie realnych calculated-stlpcov
            /*
                 System.out.println("SDFDEFS:" + sDisplayedFieldDefinitions 
                                   + "\nSDFF:" + sDisplayedFields
                                   + "\nSDFL:" + sDisplayedFieldLabels
                                   );
                 */
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
            if ((sDisplayedFields!=null) && sDisplayedFields.length > 0) {
                int s = sDisplayedFields.length;
                for (int i = 0; i < alColumns.size();i++) {
                    XDBtableColumn aktCol = alColumns.get(i);
                    //// System.out.println(aktCol.getFieldLabel()+ " " + aktCol.getModelIndex());
                    if (aktCol.getModelIndex() == -1) {
                        aktCol.setModelIndex(s);
                        //// System.out.println(aktCol.getFieldLabel()+ " update to:" + aktCol.getModelIndex());
                        s++;
                    }
                }
            }
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
            for (int i = 0; i < alColumns.size();i++) {
                XDBtableColumn aktCol = alColumns.get(i);
                myTblModel.addColumn(aktCol);
               myTableColumnModel.addColumn(aktCol);
            }            
            //for (int i = 1; i <= myTblModel.getColumnCount(); i++) {
            //     myTableColumnModel.addColumn(myTable.getColumn(i));
            //}
            myTable.getTableHeader().revalidate();
            myTable.getTableHeader().repaint();
            myTable.revalidate();
            myTable.repaint();
        } catch (SQLException ex) {
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
        }

    } // public void buildTblFromResultSetStandard() {

    // vytvorenie tabulky z resultset-u, zatial problematicke, bolo by treba mat 
    // definicie formatu, labelov, typu objektu pre ukazovanie hodnot, ... ulozene v DB,
    // co poznaju len 4GL prostredia (z hruba) (PROGRESS, POWER BUILDER, SAP ABAP, ...)

    public void buildTblFromResultSetQuery() {
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
        List<TableColumn> alColumns = new ArrayList<>();
        
        try {
            deleteTableStructure(); // mazanie pripadnych default stlpcov
            myTable.setAutoCreateColumnsFromModel(false);
            rsMetaData = myXQuery.getrsMetaData();
            if (rsMetaData==null) {
                krn.Message("E", "myXQuery.getrsMetaData() - XQuery_ERROR", "NULL-ResultSet.metadata");
                return;
            }
            String colDef;   // Column definition format string
            String colName;  // meno stlpca resultsetu
            int colPositionInDispFlds = -1; // pozicia stlpca v sDisplayedFields
            String colLabel = ""; // Nadpis stlpca
            Boolean bHidden = false; // schovany stlpec
            iNumRsColumns = rsMetaData.getColumnCount();
            if ((sDisplayedFields!=null) && sDisplayedFields.length > 0) {
                iNumDisplayedColumns = sDisplayedFields.length;
            } else {
                iNumDisplayedColumns = iNumRsColumns;
            }
            krn.debugOut(this, 5, "buildTblFromResultSet()BEGIN -> "
                    + "myTableColumnModel.getColumnCount()="
                    + myTableColumnModel.getColumnCount()
                    + " myTable.getColumnCount()=" + myTable.getColumnCount()
                    + " myTable.getModel().getColumnCount()="
                    + myTable.getModel().getColumnCount());
            
            for_block:
            for (int i = 1; i <= iNumRsColumns; i++) {
                colName = rsMetaData.getColumnName(i);
                //////krn.OutPrintln("CREATING COLUMN: " + colName);
                bHidden = false;
                // vsetky stlpce sa beru z databazovej tabulky
////                System.out.println(">>>><0>>>>sDisplayedFields.length==" + sDisplayedFields.length + " /" + colName + " /" + colLabel);
                if ((sDisplayedFields!=null) && sDisplayedFields.length > 0) {
                    colPositionInDispFlds = -1;
                    for (int ie = 0; ie < sDisplayedFields.length; ie++) {
                        if (colName.toUpperCase().equals(sDisplayedFields[ie].toUpperCase())) {
                            colPositionInDispFlds = ie;
                            break;
                        }
                    }
                    if (colPositionInDispFlds == -1) {
                        bHidden = true;
                        colLabel = colName;
                    }
                    else {
                    // ziskanie nadpisu stlpca  
                    colLabel = sDisplayedFieldLabels[colPositionInDispFlds];
                    if (colLabel == null) colLabel = "";
                    }
                } // if ((sDisplayedFields!=null) && sDisplayedFields.length > 0)
                else { /* sDisplayedFields == null */
                    colLabel = colName;
                } // zatial sa to neda brat z DB
////                System.out.println(">>>><1>>>>sDisplayedFields.length==" + sDisplayedFields.length + " /" + colName + " /" + colLabel);
                // istota je gulomet :-)
                if (colLabel.equals("") /*pokial nebol urceny v sDisplayedFields*/) {
                    colLabel = colName;
                }
                //      dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
                //      numdecimals^formatstring^tooltyp^HIDDEN
                // coldef sa zalozi podla typu dotazu
                // colname bude label, a datovy typ sa neda zistiti, tak ze String
                if (sFullQuery.length() > 0) {
                colDef = "^" + sMasterTable; // dbname sa zatial ignoruje getDBname();
                colDef = colDef + "^" + colName;
                colDef = colDef + "^" + "String"; // krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "DataType");
                colDef = colDef + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "DefaultValue");
//               colDef = colDef + "^" + colName; // + krn.getDBcolumnInfo(sMasterTable, colName, "FieldLabel");
                colDef = colDef + "^" + colLabel;
                colDef = colDef + "^" + 100; // krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Length");
                colDef = colDef + "^" + 2;  // krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Decimals");
                colDef = colDef + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "formatstring");
                colDef = colDef + "^"; // krn.getDBcolumnInfo(MyCn, sMasterTable, colName, "Comment");
                } else
                { // jednotabulkovy standard
                colDef = "^" + sMasterTable; // dbname sa zatial ignoruje getDBname();
                colDef = colDef + "^" + colName;
                colDef = colDef + "^" + MyCn.getDbDriver().getDBcolumnInfo(MyCn.getConn(), sMasterTable, colName, "DataType");
                colDef = colDef + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "DefaultValue");
//               colDef = colDef + "^" + colName; // + krn.getDBcolumnInfo(sMasterTable, colName, "FieldLabel");
                colDef = colDef + "^" + colLabel;
                colDef = colDef + "^" + MyCn.getDbDriver().getDBcolumnInfo(MyCn.getConn(), sMasterTable, colName, "Length");
                colDef = colDef + "^" + MyCn.getDbDriver().getDBcolumnInfo(MyCn.getConn(), sMasterTable, colName, "Decimals");
                colDef = colDef + "^"; // + krn.getDBcolumnInfo(sMasterTable, colName, "formatstring");
                colDef = colDef + "^" + MyCn.getDbDriver().getDBcolumnInfo(MyCn.getConn(), sMasterTable, colName, "Comment");
                }
                /////krn.OutPrintln("CREATING COLUMN: " + colName + " WITH LABEL: " + colLabel);
                XDBtableColumn aktCol = 
                        createColumn(colDef, colPositionInDispFlds, bHidden);
                alColumns.add(aktCol);
                
                iNumTblColumns++;
            } // for (int i = 1; i <= numColumns; i++)
            // 2015-11-16 - pridanie realnych calculated-stlpcov
            /*
                 System.out.println("SDFDEFS:" + sDisplayedFieldDefinitions 
                                   + "\nSDFF:" + sDisplayedFields
                                   + "\nSDFL:" + sDisplayedFieldLabels
                                   );
                 */
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
            // pridanie komparatora pre triedenie objektov typu XDBtableColumn
            Collections.sort(alColumns, new Comparator<TableColumn>() {
                 @Override public int compare(TableColumn p1, TableColumn p2) {
                 return p1.getModelIndex() - p2.getModelIndex(); // Ascending
               }
               });
            //// System.out.println("after_sortingg:");
            for (int i = 0; i < alColumns.size();i++) {
                TableColumn aktCol = alColumns.get(i);
                //// System.out.println(aktCol.getFieldLabel()+ " " + aktCol.getModelIndex());
            }            
            for (int i = 0; i < alColumns.size();i++) {
                TableColumn aktCol = alColumns.get(i);
                myTblModel.addColumn(aktCol);
               myTableColumnModel.addColumn(aktCol);
            }            
            //for (int i = 1; i <= myTblModel.getColumnCount(); i++) {
            //     myTableColumnModel.addColumn(myTable.getColumn(i));
            //}
            myTable.getTableHeader().revalidate();
            myTable.getTableHeader().repaint();
            myTable.revalidate();
            myTable.repaint();
        } catch (SQLException ex) {
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
        }

    } // public void buildTblFromResultSet() {

    
    private int updateTableModelUpOrDown(String FirstRequiredOrderKeyValue,
            String sReadVector /* UP/DOWN - smer citania dalsich viet do tabulky*/, 
            boolean bIncludeKey // vcetne riadku s FirstRequiredOrderKeyValue
            ) {
        if (myTblModel==null) {
            krn.Message("W", "No suitable table-model available.\nReturning empty set of dada."
                    , "XTable.updateTableModelUpOrDown( ... )");
            return 0;
        }
////        System.out.println("CCCOLCOUNT:" + myTblModel.getColumnCount());
        sCurrentReadVector = sReadVector;
        bCurrentIncludeKey = bIncludeKey;
        int iCurrAddedRows = 0; // pocet momentalne pridanych riadkov
        krn.debugOut(this, 5, "Adding next cached packet of rows - FirstRequiredRSrow="
                + FirstRequiredOrderKeyValue + " sReadVector=" + sReadVector);
        
        /* NEMAZAT - dolezity test v pripade problemov
                * 
        krn.OutPrintln("XTABLE_Adding next cached packet of rows - FirstRequiredRSrow="
                + FirstRequiredOrderKeyValue + " sReadVector=" + sReadVector
                + " getTblMinOrderKeyVal()=" + getTblMinOrderKeyVal()
                + " getTblMaxOrderKeyVal()=" + getTblMaxOrderKeyVal()
                );
        */
        bTableCaching = true; // zabrani vykonavaniu valueChanged eventu a podobne
        crs = myXQuery.getChResultSet(FirstRequiredOrderKeyValue, sReadVector, bIncludeKey);
        /***
                if (FnEaS.sObjName(myObjectID).equals("dbt_ESU_ulohy")) 
           Kernel.staticMsg("IN_updateTableModelUpOrDown BEGIN CALLER012: " 
                   + FnEaS.getCallerMethodName(0) + "."
                   + FnEaS.getCallerMethodName(1) + "."
                   + FnEaS.getCallerMethodName(2)
           + "\n" + "FirstRequiredRSrow="
                + FirstRequiredOrderKeyValue + " sReadVector=" + sReadVector
                + " bIncludeKey=" +bIncludeKey);    
                */
        try {
            // QQQQ TOTO JE TU PIICOVINAAAA (asi :0)) => bIsQryEmpty = (!crs.first());
            Object[] rowData = new Object[iNumRsColumns + 3];

           /* FirstRequiredOrderKeyValue = !@@FIRST alebo !@@LAST
            * maze cely aktualny obsah tabulky a prida sa prvych (!@@FIRST)
            * alebo poslednych (!@@LAST) iNumFetchedRows riadkov
            */
            if (FirstRequiredOrderKeyValue.startsWith("!@@")) {
                // mazanie vsetkych riadkov
                while (myTblModel.getRowCount() > 0) {
                    myTblModel.removeRow(0);
                }
                setCurrSelectedRow(-1,"A");  // tabulka neobsahuje ziadny vybrany riadok
                iNumCachedRows = 0;  // tabulka neobsahuje ziadny riadok
            } // if (FirstRequiredOrderKeyValue.startsWith("!@@"))
            iLastErasedRows = 0;
            iLastCachedRows = 0;
            crs.beforeFirst();
            crs_read: // presun hodnot z chunk-resultsetu do pola udajov 
            while (crs.next()) {
                
                // ulozenie udajov z chunk-resultsetu do pola v poradi, v akom
                // su DBtableColumn-stlpce v tablemodelu
                tblmdl_read: 
                for (int i = 0, y = myTableColumnModel.getColumnCount(); i < y; i++) {
                    XDBtableColumn tc = (XDBtableColumn) myTableColumnModel.getColumn(i);
                    if (tc.getdbFieldName().startsWith("FUN@")) {
                        continue tblmdl_read;
                    }
                    if (!tc.getdbFieldName().startsWith("FUN@")) {
////                       System.out.println("PREEDERRXTABLE:" + tc.getdbFieldName() 
////                                + " IDXX:" + tc.getModelIndex()
////                        + " crsISNULL:" + (crs==null) + " rowdataLNG:" + rowData.length)  ;
////                        if (tc.getModelIndex() > -1)
                    rowData[tc.getModelIndex()] = crs.getString(tc.getdbFieldName());
                    }
                } //tblmdl_read: ulozenie udajov z chunk-resultsetu do pola
                
                iNumCachedRows++;
                iLastCachedRows++;
                if (sReadVector.equals("UP") /* & !FirstRequiredOrderKeyValue.equals("!@@LAST") */) {
                    myTblModel.insertRow(0, rowData);
                    iCurrAddedRows++;
                    currSelectedRow++;
                    calculateAddedRow(0);
            } else {
                    myTblModel.addRow(rowData);
                    iCurrAddedRows++;
                    calculateAddedRow(iNumCachedRows - 1);
                }
            } // crs_read: while (crs.next()) { // presun hodnot z chunk-resultsetu do pola udajov 

            if (iLastCachedRows==0) {
            }

            // ked je viac riadkov ako moze byt (CHUNK*FETCH), riadky navyse sa mazu
            // z opacnej strany tabulky, ako je smer hodnoty premennej sReadVector
            int remainder = 0;
            if (iNumFetchedRows != -9999) // fullFetch
                remainder = iNumCachedRows - (iNumFetchedRows * iNumMaxChunks);
            if (remainder > 0) {
                if (sReadVector.equals("DOWN")) { // mazanie horneho chunk-u
                for (int d = 0; d < remainder; d++ ) {
                    myTblModel.removeRow(0);
                    iNumCachedRows--;
                    iLastErasedRows--;
                    currSelectedRow--;
                }
                }
                else { // mazanie dolneho chunku
                for (int d = 0; d < remainder; d++ ) {
                    myTblModel.removeRow(myTblModel.getRowCount() - 1);
                    iNumCachedRows--;
                    iLastErasedRows--;
                    currSelectedRow--;
                }
                }
                // na silu, aby sa dala detekovat meniaca funkcia zo setCurrSelectRow()
                // pozor na Picsaba !
                krn.OutPrintln("Picsaba-" + currSelectedRow + "  iNumCachedRows" + iNumCachedRows);
                setCurrSelectedRow(currSelectedRow,"B"); 
            }
            if (FirstRequiredOrderKeyValue.equals("!@@LAST")) {
                myTable.scrollRectToVisible(myTable.getCellRect(myTable.getRowCount() - 1, 0, true));
                myTable.changeSelection(myTable.getRowCount() - 1, 0, false, false);
                setCurrSelectedRow(myTable.getRowCount() - 1,"C");
            }
            // Skok na prvy riadok tabulky
            else if (FirstRequiredOrderKeyValue.equals("!@@FIRST")) {
                myTable.scrollRectToVisible(myTable.getCellRect(0, 0, true));
                myTable.changeSelection(0, 0, false, false);
                setCurrSelectedRow(0,"d");
            }
            krn.debugOut(this, 5, "iNumCachedRows=" + iNumCachedRows);
        } catch (SQLException ex) {
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
        }
       bTableCaching = false; // bTableCaching treba vypnut hned po nacitani viet, ...
       validate();
       return iCurrAddedRows;
    }  // private int updateTableModelUpOrDown( ...

    public String getQueryBase() {
        return sQueryBase;
    }

    public String setQueryBase(String queryBase) {
        sQueryBase = queryBase;
        if (sXTableType.equals("DBTABLE")) {
            myXQuery.setQueryBase(queryBase);
        }
        return "";
    }
    public String setFullQuery (String sQry) {
        sFullQuery = sQry;        
        if (sXTableType.equals("DBTABLE")) {
            myXQuery.setFullQuery(sFullQuery);
        }
        return "";
    }
    
    public String getAppWhere() {
        return sQryAppWhere;
    }

    public String setAppWhere(String appWhere) {
//System.out.println("> > > Setting sQryAppWhere to " + appWhere + " XQRY is null?:" + (myXQuery == null));
        sQryAppWhere = appWhere;
//        if (sXTableType.equals("DBTABLE")) {
        if (!(myXQuery == null)) {
            myXQuery.setAppWhere(appWhere);
        }
        return "";
    }

    public String getOrderKey() {
        return sOrderKey;
    }

    public String setOrdering(String sOrdKey, String sVector, String sOrdKeyDataType) {
        sOrderKey = sOrdKey;
        sOrderVector = sVector;
        sOrderKeyDataType = sOrdKeyDataType;
        // System.out.println("setOrderingFrom_XTable.setOrdering_ " + sOrdKey + " _ " + sVector + " _ " + sOrdKeyDataType);
        myXQuery.invalidate();
        myXQuery.setOrdering(sOrderKey, sOrderVector, sOrderKeyDataType);
        rebuildQuery();
        return "";
    }

    public String setOrderKey(String sOrdKeyName, String sOrdKeyDataType) {
        sOrderKey = sOrdKeyName;
        sOrderKeyDataType = sOrdKeyDataType;
        return "";
    }

    public String getOrderKeyDataType() {
        return sOrderKey;
    }

    public String getUsrWhere() {
        return sQryUsrWhere;
    }

    public String setUsrWhere(String usrWhere) {
        sQryUsrWhere = usrWhere;
        if (sXTableType.equals("DBTABLE")) {
            myXQuery.setUsrWhere(usrWhere);
            this.closeQuery();
            this.rebuildQuery();
        // vytvorenie tabulky zo vzniknuteho dotazu
        //this.buildTblFromResultSet();
        // otvorenie vlastneho dotazu
        ////krn.OutPrintln("OPE_QRY 01");
            this.open_Query();
        }
        return "";
    }

    public String goToRow(EOC_message eocMsg) {
        if (!bInitialized || myObjectID == null) {
            return "";
        }
        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
        if (!bAfterInitialized) {
            eocAfterInitGoToRowMsg = eocMsg;
            return "";
        }
        krn.debugOut(this, 5, "Going to row: " + eocMsg.getParameters());

        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
        System.out.println("\n#####\nX1 >>>>>>>>>>--------- XTB_ESU_ulohy: :::Going to row: " + eocMsg.getParameters());

        if (!sExternalTable.equals("")) {
            this.closeQuery();
        }
////        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
////        System.out.println("\nX2 >>>>>>>>>>--------- XTB_ESU_ulohy:::Going to row: " + eocMsg.getParameters());
        if (eocMsg==null){
            this.closeQuery();
        }
        else {
            String s = eocMsg.getParameters();
            if (s.endsWith("="))
                sCurrentExternalKeyValue = "";
            else {
               String[] ss = s.split("=");
               sCurrentExternalKeyValue = ss[ss.length - 1];
            }
        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
        System.out.println("\nX3 >>>>>>>>>>--------- XTB_ESU_ulohy:::Going to row: " + eocMsg.getParameters());
        }
        //valahol itt  baj noRowAvailable nem kapcsol vissza rowAvailable-ra
        myXQuery.setExternalKeyValue(sCurrentExternalKeyValue);
        String rebuildQuery = this.rebuildQuery();
        this.open_Query();
        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
        System.out.println("\nX3 xx >>>>>>>>>>--------- XTB_ESU_ulohy:::Going to row: " + eocMsg.getParameters());
        goToFirst(null);
        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
        System.out.println("\n##\nX4 XTableeee_XTB_ESU_ulohy_:" + FnEaS.sObjName(myObjectID) + " goToRow afterrinit:" 
        + bAfterInitialized + "  mesiss:" + eocMsg.getParameters()
        + " numRows:" + myTable.getRowCount());
        // 2017-06-19
        if (myTable.getRowCount() == 0) { 
            sCurrentMasterKeyValue = "";
           //// setCurrentRowStatus("noRowAvailable");
//            EOC_message msg = new EOC_message((IEOC_Object) myObjectID,"noRowAvailable","", "");
//            krn.krn_sendMessage((IEOC_Object)myObjectID,msg,"ROW", "TARGET");
        }
        else { 
            sCurrentMasterKeyValue = (String) myTable.getValueAt(currSelectedRow,
                                      getTblColumnIndexBydbFieldName(myTable, sMasterKey));
            setCurrentRowStatus("rowAvailable");
        }    
            
        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
        System.out.println("\n##\nX5 XTableeee_XTB_ESU_ulohy_:" + FnEaS.sObjName(myObjectID) 
                           + " currSelectedRow====" + currSelectedRow +  " sMasterKeyValue====" + sCurrentMasterKeyValue);

        //EOC_message msg = new EOC_message((IEOC_Object) myObjectID,"noRowAvailable","", "");
        //krn.krn_sendMessage((IEOC_Object)myObjectID,msg,"ROW", "TARGET");
        
        return "";
    } // public String goToRow(String sMsg) {

     /**
      *  Vrati hodnotu cell-u na riadku 'rowIdx' 
      * pre databazovy udaj s nazvom 'sFieldName' ako String
      * @param rowIdx
      * @param sFieldName
      * @return 
      */
    public String getColVal(String sParam) {
        ////System.out.println(">#####>>getColValINN>>To som jaaa::" + FnEaS.sObjName(myObjectID) + " Gettings:" + sParam
        ////+ "  currSelectedRowww:" + currSelectedRow + "  myTable.getRowCount()" + myTable.getRowCount());
        try {
            if (sParam == null || currSelectedRow < 0 || myTable.getRowCount() == 0) {
                return null;
            };
            /////krn.OutPrintln("QRSELROW=" + currSelectedRow + " pre: " + sParam);
            return (String) myTable.getValueAt(currSelectedRow,
                    getTblColumnIndexBydbFieldName(myTable, sParam));
        } catch (Exception ex) {
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getColVal(int atRow, String sParam) {
        try {
            if (sParam == null) {
                return null;
            };
            return (String) myTable.getValueAt(atRow,
                    getTblColumnIndexBydbFieldName(myTable, sParam));
        } catch (Exception ex) {
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // skok na prvy riadok DB tabulky
    public String goToFirst(EOC_message eocMsg) {
        if (bIsQryEmpty) {
            ////System.out.println("EMMMTYQRY_IN_GOTOFIST:" + FnEaS.sObjName(myObjectID));
               EOC_message msg = new EOC_message((IEOC_Object) myObjectID,"noRowAvailable","", "");
               krn.krn_sendMessage((IEOC_Object)myObjectID,msg,"ROW", "TARGET");
////                  krn.krn_sendMessage(myObjectID, myObjectID,
////                           "noRowAvailable", "",  "ROW", "TARGET", "");
                  return "";
        }
        try {
            krn.debugOut(this, 5, "goToFirst start");
            if ((eocMsg==null) || !(eocMsg.getParameters().equals("MUST"))) {
            if (!bIsQryOpened) {
                krn.debugOut(this, 5, "goToFirst query is closed !");
                return "";
            }
            if (bIsQryEmpty) {
                krn.debugOut(this, 5, "goToFirst query is emty !");
                return "";
            }
            }
            // takzvany DOWN-chunk- t.j. dalsi balik riadkov !pod! aktualny riadok
            // t.j. chunk je z XQuery vrateny v spravnom/platnom order-e
            // ktory sa bude citat crs.next()-om a prida sa na spodok tabulky
            updateTableModelUpOrDown("!@@FIRST", "DOWN" /* sVector */ , true);
            bTableCaching = false;
            /*
            krn.OutPrintln(111111);
            myTable.changeSelection(0, 0, false, false); // namiesto bFocus==true
            krn.OutPrintln(111112);
            setCurrSelectedRow(0,"");
            krn.OutPrintln(111113);
            _evt_valueChanged();
            krn.OutPrintln(111114);
            */
            _valueChanged_event();
        } catch (Exception ex) {
            Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }

        krn.debugOut(this, 5, "goToFirst fired OK");
        return "";
    }

    // skok na posledny riadok DB tabulky
    public String goToLast(EOC_message eocMsg) {
        if (bIsQryEmpty) {
            ////System.out.println("EMPPPQRYGOTOLAST");
               EOC_message msg = new EOC_message((IEOC_Object) myObjectID,"noRowAvailable","", "");
               krn.krn_sendMessage((IEOC_Object)myObjectID,msg,"ROW", "TARGET");
////                  krn.krn_sendMessage(myObjectID, myObjectID,
////                           "noRowAvailable", "",  "ROW", "TARGET", "");
                  return "";
        }
        try {
            krn.debugOut(this, 5, "goToLast start");
            if (!bIsQryOpened) {
                krn.debugOut(this, 5, "goToFirst query is closed !");
                return "";
            }
            if (bIsQryEmpty) {
                krn.debugOut(this, 5, "goToFirst query is emty !");
                return "";
            }
            // takzvany UP-chunk- t.j. dalsi balik riadkov !nad! aktualny riadok
            // t.j. chunk je z XQuery vrateny v !OPACNOM!/NEPLATNOM order-e
            // ktory sa bude citat crs.next()-om a prida sa na vrch tabulky
            updateTableModelUpOrDown("!@@LAST", "UP" /* sVector */, true);
            bTableCaching = false;
            _valueChanged_event();
            ///krn.krnMsg("HHHHOOOPP - goToLast agter UpOrDown");
         myTable.scrollRectToVisible(myTable.getCellRect((myTable.getRowCount() - 1) , 0, true));
            myTable.changeSelection((myTable.getRowCount() - 1),
                    (myTable.getRowCount() - 1), false, false); // namiesto bFocus==true
            setCurrSelectedRow(myTable.getRowCount() - 1,"");
         } catch (Exception ex) {
            Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        ////krn.krnMsg("goToLast fired OK");
        krn.debugOut(this, 5, "goToLast fired OK");
        return "";
    }

    // skok na nasledujuci riadok
    public String goToNext(EOC_message eocMsg) {
        if (bIsQryEmpty) {
            ////System.out.println("EMPPPQRYGOTONEXT");
               EOC_message msg = new EOC_message(myObjectID,"noRowAvailable","", "");
               krn.krn_sendMessage(myObjectID,msg,"ROW", "TARGET");
////                  krn.krn_sendMessage(myObjectID, myObjectID,
////                           "noRowAvailable", "",  "ROW", "TARGET", "");
                  return "";
        }
        krn.OutPrintln("1 goToNext() - getCurrSelectedRow() = " + getCurrSelectedRow());
        if (getCurrSelectedRow() == (myTable.getRowCount() - 1)) {
            krn.OutPrintln("IF_A goToNext() ");
            if (myXQuery.isLastRow(sOrderKey, sCurrentOrderKeyValue)) {
                // neexistuje dalsi riadok v tabulke DB
                EOC_message msg = new EOC_message(myObjectID,"lastRow","", "");
                krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
////                krn.krn_sendMessage(myObjectID, myObjectID,
////                        "lastRow", "", "navigation", "source", "");
                return "";
            } else { // existuje dalsi riadok v tabulke DB
                updateTableModelUpOrDown(sCurrentOrderKeyValue, "DOWN", false);
            }
        } else {
            krn.OutPrintln("IF_B1 goToNext() " + currSelectedRow);
            myTable.setRowSelectionInterval(currSelectedRow + 1, currSelectedRow + 1);
            currSelectedRow = myTable.getSelectedRow();
            krn.OutPrintln("IF_B2 goToNext() " + currSelectedRow);
            //QQQ20140314- setCurrSelectedRow(currSelectedRow + 1,"");
            
            krn.OutPrintln("IF_B3 goToNext() " + currSelectedRow);
            _valueChanged_event();
            if (getCurrSelectedRow() == (myTable.getRowCount() - 1)) {
               if (myXQuery.isLastRow(sOrderKey, sCurrentOrderKeyValue)) {
                   // neexistuje dalsi riadok v tabulke DB
                   EOC_message msg = new EOC_message(myObjectID,"lastRow","", "");
                   krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
////                   krn.krn_sendMessage(myObjectID, myObjectID,
////                          "lastRow", "", "navigation", "source", "");
                   return "";
               }
            } else { // existuje dalsi riadok v tabulke DB
              EOC_message msg = new EOC_message(myObjectID,"notFirstOrLast","", "");
              krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
////              krn.krn_sendMessage(myObjectID, myObjectID,
////                 "notFirstOrLast", "", "navigation", "source", "");
            }
        }
        krn.OutPrintln("2 goToNext() " + getCurrSelectedRow());
        return "";
    }
    
    // skok na predosli riadok
    public String goToPrev(EOC_message eocMsg) {
       // krn.krnMsg("!!QQ!bIsQryEmpty=" + bIsQryEmpty);
        if (bIsQryEmpty) {
            ////System.out.println("EMPPPQRYGOTOPREV");
                  EOC_message msg = new EOC_message(myObjectID,"noRowAvailable","", "");
                  krn.krn_sendMessage(myObjectID,msg,"ROW", "TARGET");
////                  krn.krn_sendMessage(myObjectID, myObjectID,
////                           "noRowAvailable", "",  "ROW", "TARGET", "");
                  return "";
        }
        if (getCurrSelectedRow() == 0) {
            if (myXQuery.isFirstRow(sOrderKey, sCurrentOrderKeyValue)) {
                // neexistuje predosli riadok v tabulke DB
               EOC_message msg = new EOC_message(myObjectID,"firstRow","", "");
               krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
////                krn.krn_sendMessage(myObjectID, myObjectID,
////                        "firstRow", "", "navigation", "source", "");
                return "";
            } else { // existuje predosli riadok v tabulke DB
                updateTableModelUpOrDown(sCurrentOrderKeyValue, "UP", false);
            }
        } else {
            myTable.setRowSelectionInterval(getCurrSelectedRow() - 1, getCurrSelectedRow() - 1);
            currSelectedRow = myTable.getSelectedRow();
            _valueChanged_event();
            //QQQ20140314- setCurrSelectedRow(getCurrSelectedRow() - 1,"");
            if (getCurrSelectedRow() == 0) {
               if (myXQuery.isFirstRow(sOrderKey, sCurrentOrderKeyValue)) {
                  // neexistuje predosli riadok v tabulke DB
                  EOC_message msg = new EOC_message(myObjectID,"firstRow","", "");
                  krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
////                  krn.krn_sendMessage(myObjectID, myObjectID,
////                           "firstRow", "", "navigation", "source", "");
                  return "";
               }  
            }
            else {
               EOC_message msg = new EOC_message(myObjectID,"notFirstOrLast","", "");
               krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
////               krn.krn_sendMessage(myObjectID, myObjectID,
////                  "notFirstOrLast", "", "navigation", "source", "");
            }
        }
        return "";
    }

    // obnovnie dotazov
    public String rebuildQuery() {
        if (sXTableType.equals("DBTABLE")) {
            sQueryPrevStatement = sQueryFullStatement; //odlozenie predosleho dotazu
            // pri typu DBTABLE rebuild v XQuery riadia pohyby po tableModel-u (UP/DOWN)
            sQueryFullStatement = myXQuery.getQueryFullStatement();
        } else {
            // toto je ottazne, zatial to necham tak, ako je 
            krn.Message(this, "W", "Volanie metódy rebuildQuery pre neimplementivaný typ "
                    + "EOC_XTable (" + sXTableType + ")", "");
            /**
             * ********************************************************************
             * STARY KOD z EOC_dbtable - Pre istotu som to tu nechal String sQry
             * = ""; // base String sWhr = ""; // where String sOrd = ""; //
             * order by if (sQueryBase == null || sQueryBase.equals("")) {
             * return "EOC-ERR=Prázdna definícia základu dotazu !"; // CHYBA }
             *
             * sQry = sQueryBase; // zaklad dotazu
             *
             * // skladanie where-podmienky if (sQueryAppWhere != null &&
             * !sQueryAppWhere.equals("")) { sWhr = "(" + sQueryAppWhere + ")";
             * } if (sQueryUsrWhere != null && !sQueryUsrWhere.equals("")) {
             * sWhr = ((sWhr.equals("")) ? "" : "and") + "(" + sQueryUsrWhere +
             * ")"; }
             *
             * // skladanie order by klauzuly if (sOrderKey != null &&
             * !sOrderKey.equals("")) { sOrd = " order by " + sOrderKey + " "; }
             * sQueryPrevStatement = sQueryFullStatement; //odlozenie predosleho
             * dotazu sQueryFullStatement = sQry + ((sWhr.equals("")) ? " " : "
             * where " + sWhr) + ((sOrd.equals("")) ? " " : " order by" + sOrd);
             * **********************************************************************
             */
        }
        krn.debugOut(this, 4, "queryFullStatement = " + sQueryFullStatement);
        return "";
    } // String rebuildQuery()
    
    public void reopen_Query() {
              bIsQryOpened = false;
              bTableCaching = true;
              closeQuery();
              open_Query();
              updateTableModelUpOrDown("!@@FIRST",sCurrentReadVector, bCurrentIncludeKey);
  
    }
    // otvorenie dotazu (typ DBTABLE poziada o otvoreni dotazu svoj XQuery object)
    public void open_Query() {
        // System.out.println(">>> XTbl_open_Query()=>" + sQueryFullStatement);
        try {
            if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
        System.out.println(FnEaS.sObjName(myObjectID) + " XTable_open_QRY: Axx");
            MyCn.getConn().setAutoCommit(false);
            if (sXTableType.equals("DBTABLE")) {
                myXQuery.rebuildQuery("!@@FIRST", "DOWN",true);
                myXQuery.openQuery("!@@FIRST", "DOWN",true);
                sQueryFullStatement=myXQuery.getQueryFullStatement();
////                krn.OutPrintln("QQQsQueryFullStatement = " + sQueryFullStatement);
                //goToFirst("");
                updateTableModelUpOrDown("!@@FIRST", "DOWN" /* sVector */, true);
                bIsQryEmpty = myXQuery.isQryEmpty();
           }
        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
            System.out.println(FnEaS.sObjName(myObjectID) + " XTable_open_QRY: Bxx:" + bIsQryEmpty);
        } catch (SQLException ex) {
            Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (bIsQryEmpty) {
            ////System.out.println("EMPPPQRYGOTOLASTopen_Query()");
            // vsetky stavy riadku su neplatne, a id riadku sa neda odoslat
            bDbFirstRow = bDbLastRow = bTblFirstRow = bTblLastRow = false;
        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
            System.out.println(FnEaS.sObjName(myObjectID) + " XTable_open_QRY: emptyA");
            sCurrentMasterKeyValue = "";
            setCurrentRowStatus("noRowAvailable");
        if (FnEaS.sObjName(myObjectID).equals("XTB_ESU_ulohy"))
            System.out.println(FnEaS.sObjName(myObjectID) + " XTable_open_QRY: emptyB");
        }
        updateTableModelUpOrDown("!@@FIRST",sCurrentReadVector,bCurrentIncludeKey);
        bIsQryOpened = true;
        bTableCaching = false;
//        return "";
    }

    public String closeQuery() {
        krn.debugOut(this, 5, "CloseQuery()->bIsQryOpened=" + bIsQryOpened);
////        krn.OutPrintln("CloseQuery() Closingg->bIsQryOpened=" + bIsQryOpened);
        if (!bIsQryOpened) {
            return "";
        }
////        krn.OutPrintln("1TableCaching-ON");
        bTableCaching = true;
        DefaultTableModel tblModel = (DefaultTableModel) myTable.getModel();
        for (int i = tblModel.getRowCount() - 1; i >= 0; i--) {
            tblModel.removeRow(i);
        }
        myXQuery.closeQuery();
        myXQuery.invalidate();
        bTableCaching = false;
        iNumCachedRows = 0; // uz nacitane davky riadkov z RS
        setCurrSelectedRow(-1,"");
        bIsQryOpened = false;
        bIsQryEmpty = false;
        myTable.setModel(/*(DBTableModel)*/ tblModel);
        revalidate();
//        krn.krnMsg("XTable - closeQuery - sending noRowAvailable ");

        // 2016-01-20
        // tu sa odosle sygnal 'noRowAvailable' po kazdej zmene 
        // hodnoty premennej sMasterKey, ale to treba 'vytrpiet'
        // nakolko stav je v tomto momente naozaj taky .
       //// System.out.println("====CLOSQURR: 1" + FnEaS.sObjName(myObjectID));
     //   setCurrentRowStatus("noRowAvailable");
       //// System.out.println("=====CLOSQURR: 2" + FnEaS.sObjName(myObjectID));
        return "";
    }  // public String closeQuery()

    // nastavi nazov hlavnej tabulky
    public String setMasterTable(String sMstTblName) {
        sMasterTable = sMstTblName;
        if (sXTableType.equals("DBTABLE")) {
            myXQuery.setMasterTable(sMstTblName);
        }
        return "";
    }

    // nastavi nazov pre sMasterKey (primarny kluc tabulky)
    public String setMasterKey(String sMstKeyName, String sMstKeyDataType) {
        // QQQ - sem patri test platnych hodnot pre parameter, zatial ignorujem
        sMasterKey = sMstKeyName;
        sMasterKeyDataType = sMstKeyDataType;
        if (sXTableType.equals("DBTABLE")) {
            myXQuery.setMasterKey(sMstKeyName, sMstKeyDataType);
        }
        if (sOrderKey.equals("")) {
            sOrderKey = sMasterKey;
            sOrderKeyDataType = sMasterKeyDataType;
            if (sXTableType.equals("DBTABLE")) {
                //// System.out.println("SettingORDERKEYTOOO:" + sOrderKey + " >> " + sOrderKeyDataType);
                myXQuery.setOrderKey(sOrderKey, sOrderKeyDataType);
            }
        }

        return "";
    }

    ///@Override
    public String OLDreceiveMessage(EOC_message eocMsg) {

        if (eocMsg==null) System.out.println("XTableee_null_esginReceive");

        // musi byt ako prva instrukcia v metode !!!
        if (eocMsg==null) return FnEaS.nullEocMessageResponse(myObjectID);
        
        System.out.println("XTableee_mesginReceive: " + myObjectID.toString() 
                + "\n>>>>>>>>>>>> " +  eocMsg.getMessage() + "=>" + eocMsg.getParameters());
        /* Volanie metody v EOC_NObject, ktora pusti metodu sMessage pokial je to
         * existujuca metoda, a sMessage ma prefix RUN
         */
        if (!bAfterInitialized) {
            return "";
        }

        String supRecMsg = super.receiveMessage(eocMsg);
        System.out.println("XTableee_mesginReceive: supRecMsg: " + supRecMsg);
        // sprava bola spracovana rodicom
        if (supRecMsg.equals("")) {
            krn.debugOut(this, 0, "SUPER_invoked in EOC_XTable, and his return empty string (OK)");
            return "";
        }
        // sprava nebola spracovana rodicom
        krn.debugOut(this, 5, "SUPER_invoked in EOC_XTable, and his return > " + supRecMsg);
        try {
            // testovat bude treba podla obsahu sParameters
            krn.debugOut(this, 5, "receiveMessage  in EOC_dbtableOld=>" + eocMsg.getMessage());
            Method mtd;
            try {
                mtd = this.getClass().getMethod(eocMsg.getMessage(), new Class[]{String.class});
            } catch (NoSuchMethodException ex) {
                mtd = null;
            }
            if (mtd == null) {
                krn.debugOut(this, 5, "mtd1 is null");
                try {
                    mtd = this.getClass().getDeclaredMethod(eocMsg.getMessage(),
                            new Class[]{String.class});
                } catch (NoSuchMethodException ex) {
                    krn.debugOut(this, 5, "xxAA " + ex.toString());
                    mtd = null;
                } catch (NullPointerException ex) {
                    krn.debugOut(this, 5, "xxBB " + ex.toString());
                    mtd = null;
                }
            }
            try {
                if (!(mtd==null)) {
                krn.debugOut(this, 4, "invokeA invoking: " + mtd.toString());
                Object rv = mtd.invoke(this, eocMsg.getParameters());
                krn.debugOut(this, 4, "invokeB");
                return rv.toString();
                }
                else {
                   
                }
            } catch (IllegalAccessException ex) {
                krn.debugOut(this, 0, "xx " + ex.toString());
            } catch (IllegalArgumentException ex) {
                krn.debugOut(this, 0, "xx " + ex.toString());
            } catch (InvocationTargetException ex) {
                krn.debugOut(this, 0, "xx " + ex.toString());
            }
        } catch (SecurityException ex) {
            return "NOTRECEIVED-NObject=SecurityExceptionA";
        }
        krn.debugOut(this, 0, "EOC_query-receiveMessage-b" + this.getClass().getName()
                + " supRecMsg=" + supRecMsg);

        return supRecMsg;
    }

    public String rowDeleted (EOC_message eocMsg) {
       krn.OutPrintln("XTable-rowDeeleted=" + eocMsg.getParameters());
       int aktRow = getCurrSelectedRow();
       myTblModel.removeRow(aktRow);
       int numRows = myTblModel.getRowCount() - 1; // riadky zacinaju od nuly !
       if (aktRow > numRows) {aktRow = numRows;}
       myTable.changeSelection(aktRow, 0, false, false);
       setCurrSelectedRow(aktRow,"");
       if (myTblModel.getRowCount()==0) {
           EOC_message msg = new EOC_message(myObjectID,"noRowAvailable","", "");
           krn.krn_sendMessage(myObjectID,msg,"ROW", "TARGET");
////           krn.krn_sendMessage(myObjectID, myObjectID,
////                        "noRowAvailable", "",  "ROW", "TARGET", "");
       }
       return "";
    }

    public String rowCreated(EOC_message msg) throws SQLException {
        krn.OutPrintln("XTable-rowCreated->Scrolling to row: " + msg.getParameters()
        + " num-tablemodelrows=" + myTblModel.getRowCount());
        
        // presun ukazovatela v tabulke na vytvorenu/alebo hladnu vetu
        ////krn.krnMsg("Start scrolling ...");
        clearOrdering();
        scrollToRow (msg.getParameters(), "BOTTOM");
        return "";
    }

    // odrolovanie tabulky na riadok s id-om sTblId   
    public boolean scrollToRow(String sTblId, String sRowPosition /* TOP/BOTTOM */ ) throws SQLException {
//       krn.Message("!!!scrollToRow()->sTblId=" + sTblId + "\nbIsQryEmpty=" + bIsQryEmpty
//                  + "\nsRowPosition=" + sRowPosition);

       // ked dotaz nie je otvoreny alebo je prazdny, otvori sa tu
       if (!bIsQryOpened ||bIsQryEmpty) {
           krn.OutPrintln("OP_QRY 04");
           this.open_Query(); // otvorenie dotazu
           if (bIsQryEmpty) { // dotaz neobsahuje ziadny riadok
           return false; // nie je kam odrolovat
           }
       }
       boolean bScrolled = false;
       //myTable.revalidate();
       // PRVY pokus o odrolovanie na potrebny riadok (funguje, pokial je veta v tabulke)
       bScrolled = _scrollToRow(sTblId,sRowPosition);
 ////      krn.krnMsg("first bScrolled = " + bScrolled +  " rowPosition = " + sRowPosition);
       // ked sa riadoch nenachadza v tabulke, dolozi sa tu
       if (!bScrolled) {
           // ziskanie hodnoty triediaceho stlpca na ziadanom riadku
           String newOrdKey = 
              MyCn.getDbDriver().getDBcolValue (MyCn.getConn(),"", sMasterTable, sMasterKey,sTblId, sOrderKey);
////       krn.krnMsg("!!!scrollToRow()->newTblId=" + sParam + "\nnewOrdKey=" + newOrdKey 
////                  + "\nbIsQryEmpty=" + bIsQryEmpty);
           // Zistenie, ci veta s novym klucom nepatri medzi aktualne riadky
           // Funkcia vrati potrebnu poziciu riadku alebo -1, ked je veta
           // mimo existujucich riadkov
           ////krn.krnMsg("getting iRowPos for " + sOrderKey + " vector " + sOrderVector 
                    ////  +  " = " + newOrdKey);
           // !!! Asi by tu bolo treba testovat, ci iny uzivatel nepridal/zmenil vetu 
           // v databazovej tabule, ktora suvysi s aktualnym cached-set-om
           // a v pripade potreby nacitat obsah tabulky znovu - v dalsej verzii. :-)
           int iRowPos;
           iRowPos = _getRowPos(sOrderKey,newOrdKey);
           boolean bRowAdded;
////           krn.krnMsg("A - adding rov to position: " + iRowPos);
           if (iRowPos >= 0) { // Staci dolozit riadok na poziciu iRowPos
////               krn.krnMsg("iRowPos=" + iRowPos);
               xcrs = myXQuery.getXResultSet(sTblId);
               xcrs.beforeFirst();
               bRowAdded = xcrs.next();
////               krn.krnMsg("B - adding rov to position: " + iRowPos);
               Object[] rowData = new Object[iNumRsColumns + 3];
               for (int i = 0, y = myTableColumnModel.getColumnCount(); i < y; i++) {
                   XDBtableColumn tc = (XDBtableColumn) myTableColumnModel.getColumn(i);
                   rowData[tc.getModelIndex()] = xcrs.getString(tc.getdbFieldName());
                }
               myTblModel.insertRow(iRowPos, rowData);
               myTable.addRowSelectionInterval(iRowPos, iRowPos);
               return true;   
           }
            
           // tu by sa ma CHUNK s potrebnym riadkom dolozit do tabulky
           if (sRowPosition.equals("TOP")) {
               // ked sRowPosition=TOP, oznaceny ma byt prvy riadok
               updateTableModelUpOrDown(newOrdKey, "UP", true);
           }
           else {
               // ked sRowPosition=BOTTOM, oznaceny ma byt posledny riadok
               updateTableModelUpOrDown(newOrdKey, "DOWN", true);
           }
           // tu by mal byt uz riadok v tabulke
           
         ////krn.krnMsg("STOOOOP !!!!!  je tam ten riadok V TOMTO MOMENTE do prdelee(ked nie, tak je problem) ??");
          // DRUHY/POSLEDNY pokus o odrolovanie na potrebny riadok
          ///// krn.krnMsg("scrool2A " + bScrolled);
           bScrolled = _scrollToRow(sTblId,sRowPosition);
         /////  krn.krnMsg("scrool2B " + bScrolled);
      }
////        krn.krnMsg("STOOOOP !!!  je tam ten este stale riadok do prdelee ??");
       return bScrolled;
    }
    
    // volana je z metody scrollToRow. Skusi sa lokalizovat v udajoch tabulky.
    private boolean _scrollToRow(String sTblId, String sRowPosition /* TOP/BOTTOM */ ) {
        //hladanie v udajoch table-model-u
        int rowCount = myTblModel.getRowCount(); // pocet riadkov
//        int idColIdx = myTableModel.findColumn(sMasterKey);
        int idColIdx = myTableColumnModel.getColumnIndex(sMasterKey);
        String sValue = null;
//        Vector dataVect = myTableModel.getDataVector();
//        Vector currRow  = null;  // aktualny riadko tabulky
        for (int ii=0; ii<rowCount; ii++) {
            sValue = (String) myTable.getModel().getValueAt(ii, idColIdx);
       ////    krn.krnMsg("STOOOOPAAAbsmekk - " + "\n" 
       /////             + "sValue.equals(sTblId) -> " + sValue.equals(sTblId) +
       ////      sValue + " -- " + sTblId);
            if (sValue.equals(sTblId)) {
            Rectangle rect = myTable.getCellRect(ii, idColIdx, true);
            myTable.scrollRectToVisible(rect);
            myTable.setRowSelectionInterval(ii, ii);
            setCurrSelectedRow(ii,"");
            return true;
            }
        }
        return false;
    }

    
    private int _getRowPos(String sKeyName, String sKeyValue) {
    //    krn.krnMsg("A_getRowPos sOrderVector = " + sOrderVector );
        int rowCount = myTblModel.getRowCount(); // pocet riadkov
     //   krn.krnMsg("B_getRowPos sOrderVector = " + sOrderVector + rowCount );
        //int idColIdx = myTableColumnModel.getColumnIndex(sKeyName); // cislo stlpca

        int idColIdx = -1; // cislo stlpca sa hlada podla nazvu stlpca v databazovej tabulke
        //QQQ !!! POZOR - asi bude treba dolozit nazov databazovej tabulky do testu !!!
        colFind_block:
        for (int ia = 0; ia < myTableColumnModel.getColumnCount(); ia++) {
                XDBtableColumn tbColumn = (XDBtableColumn) myTableColumnModel.getColumn(ia);
////                krn.krnMsg("tbColumn.getdbFieldName().equals(sKeyName) " 
////                       + tbColumn.getdbFieldName() + " == " + sKeyName 
////                       + "=> " + tbColumn.getdbFieldName().equals(sKeyName));
                if (tbColumn.getdbFieldName().equals(sKeyName)) {
                  idColIdx = ia;
                  break colFind_block;
                }
        }
       //// krn.krnMsg("C_getRowPos sOrderVector = " + sOrderVector + idColIdx);
        // Hladanie vhodnej pozicie riadku v tabulke pre hodnotyu dokladaneho kluca.
        // !!! Hlada sa bezohladu na velke/male pismena !!!
        String sAktValue = null;
        for (int ii=0; ii<rowCount; ii++) {
            sAktValue = (String) myTable.getModel().getValueAt(ii, idColIdx);
////            krn.krnMsg("D_getRowPos sOrderVector =>>> "
  ////                  + sAktValue + " ???? " + sKeyValue + " <==> " 
    ////                + sKeyValue.compareTo(sAktValue) + " rowPosition = " + ii);
            if (sOrderVector.toLowerCase().equals("asc") 
                && (sKeyValue.toLowerCase().compareTo(sAktValue.toLowerCase()) < 0) ) {
                return ii;
            }
            if (sOrderVector.toLowerCase().equals("desc") 
                && (sKeyValue.toLowerCase().compareTo(sAktValue.toLowerCase()) > 0) ) {
                return ii;
            }
        }
        
        return -1;
    }
    /**
     * Creates new form EOC_dbtable
     */
    public XTable() {
    //    owRowSource = new Object();
        eocRowStatusMsg = new EOC_message (this, "","","");

        initComponents();
    }

    // refreshuje aktualny riadok (row) tabulkoveho objektu (JTable)
    public String rowUpdated(EOC_message mssg) {
        //// krn.Message("ROWUPDATEDIN:" + FnEaS.sObjName(this) + " PARRAM:" + mssg.getParameters());
        try {
            // nacitanie aktualneho riadku z tabulky z DB s novymi udajmi
            xcrs = myXQuery.getXResultSet(sCurrentMasterKeyValue);
            xcrs.beforeFirst();
            xcrs.next();
            // refreshing udajov v tableModel-u
            Object celldata = new Object();
            o_for:
            for (int i = 0, y = myTableColumnModel.getColumnCount(); i < y; i++) {
                XDBtableColumn tc = (XDBtableColumn) myTableColumnModel.getColumn(i);
                if (tc.getdbFieldName().startsWith("FUN@")) { // calculated column
                    continue o_for;
                }
                celldata = xcrs.getString(tc.getdbFieldName());
////                System.out.println("#### >>> rowUpdatedInXTableRewrite:" +  celldata + " in row: " +  currSelectedRow
////                        + " at index: " + tc.getModelIndex());
                myTblModel.setValueAt(celldata, currSelectedRow, tc.getModelIndex());
            }
            calculateAddedRow(currSelectedRow);
            myTable.repaint();
        } catch (SQLException ex) {
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
        return "";
    }

    @Override
    public String getEOC_objectType() {
        return "EOC_XTable";
    }

    // vrati poradove cislo stlpca v columnmodelu tabulky podla obsahu jeho hlavicky
    private int getTblColumnIndexByColHeader(JTable table, String name) {
        for (int i = 0; i < myTableColumnModel.getColumnCount(); i++) {
            XDBtableColumn tc = (XDBtableColumn) myTableColumnModel.getColumn(i);
            if (tc.getHeaderValue().equals(name)) {
                // QQQ - osetrit zmenu poradia v objekte tc ???
                return /* tc.getModelIndex() */ i;
            }
        }
        return -1;
    }

    // vrati poradove cislo stlpca v columnmodelu tabulky podla jeho nazvu
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

    // vypise nazvy vsetkych stlpcov z aktualneho columnModel-u na standardny vystup
    public void showColumnNames() {
        for (int i = 0; i < myTableColumnModel.getColumnCount(); i++) {
            XDBtableColumn tc = (XDBtableColumn) myTableColumnModel.getColumn(i);
            krn.OutPrintln(tc.getFieldLabel() + " -> " + tc.getdbFieldName()
                    + "   dbFieldDataType=" + tc.getDataType()
                    + "   genericDataType=" + krn.getGenericDataType(tc.getDataType()));
        }
    }

    // nastavenie poctu naraz citanych viet z tabulky DB
    public void setiNumFetchedRows(int iNumFtchRows) {
//        System.out.println("XTBL_SETINUMFTCHROW:" + iNumFetchedRows);
        iNumFetchedRows = iNumFtchRows;
        myXQuery.setiNumFetchedRows(iNumFetchedRows);
    }
    
    public String getScreenValue(String objName) {
 //      System.out.println("XTable.getScreenValue()for: " + objName + "atRoww:" + currSelectedRow
 //      + "myTable.getModel().getcolcontt::" + myTable.getModel().getColumnCount());
       return (String) myTable.getModel().getValueAt(currSelectedRow,
                        getTblColumnIndexBydbFieldName(myTable, objName));
    }

    public String freezeInterface (EOC_message msg) {
       myTable.setEnabled(false);
       myTable.setBackground(Color.lightGray);
      //  krn.krnMsg("enablingWidgetsA return ''");
       //EOC_message msg = new EOC_message(myObjectID,"freezeInterface","", "");
       msg.setSender(myObjectID);
       krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
       if (msg.isOwner(myObjectID))
           krn.krn_sendMessage(myObjectID,msg,"row", "source");
       krn.krn_sendMessage(myObjectID,msg,"row", "target");
       //krn.krnMsg("enablingWidgetsB return ''");
//nemsegit-nemsegit :o))
       return "";
    }    
    
    public String unfreezeInterface (EOC_message msg) {
       myTable.setEnabled(true);
       myTable.setBackground(Color.white);
////       EOC_message msg = new EOC_message(myObjectID,"unfreezeInterface","", "");
       msg.setSender(myObjectID);
       krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
       if (msg.isOwner(myObjectID))
           krn.krn_sendMessage(myObjectID,msg,"row", "source");
       krn.krn_sendMessage(myObjectID,msg,"row", "target");
       return "";
    }    
     
    public void setTableEditable(boolean bVal) { 
       myTblModel.setTableEditable(bVal);
    } 

    public String getTblMinOrderKeyVal() {
        if ( myTable.getRowCount() == 0) { 
            ////krn.OutPrintln("getTblMinOrderKeyVal()!!!NULLL");
            return null; 
        }
        int colIdx = getTblColumnIndexBydbFieldName(myTable, sOrderKey);
        String sRetVal = (String) myTable.getValueAt(0, colIdx);
/**
        krn.OutPrintln("getTblMinOrderKeyVal()=myTableModel.getRowCount()" 
                + myTblModel.getRowCount() + " -- " + myTable.getRowCount()
                + " getTblColumnIndexBydbFieldName(myTable, sOrderKey)=" + sOrderKey + "="
                + getTblColumnIndexBydbFieldName(myTable, sOrderKey) + "=" + sRetVal);
**/
//        return (String) myTableModel.getValueAt(0,
//                getTblColumnIndexBydbFieldName(myTable, sOrderKey));
        return sRetVal;
    }

    public String getTblMaxOrderKeyVal() {
        if ( myTable.getRowCount() == 0) { 
            /////krn.OutPrintln("getTblMaxOrderKeyVal()!!!NULLL");
            return null; 
        }
        int colIdx = getTblColumnIndexBydbFieldName(myTable, sOrderKey);
        String sRetVal = (String) myTable.getValueAt(myTable.getRowCount() - 1,
                         colIdx);
/*
        krn.OutPrintln("getTblMaxOrderKeyVal()=myTableModel.getRowCount()" 
                + myTblModel.getRowCount() + " -- " + myTable.getRowCount()
                + " getTblColumnIndexBydbFieldName(myTable, sOrderKey)=" + sOrderKey + "="
                + getTblColumnIndexBydbFieldName(myTable, sOrderKey) + "=" + sRetVal);
        */

//        return (String) myTableModel.getValueAt(myTableModel.getRowCount() - 1,
//                getTblColumnIndexBydbFieldName(myTable, sOrderKey));
        return sRetVal;
    }

    public int getFirstVisibleRow() {
        return myTable.rowAtPoint(myScrollPane.getViewport().getViewPosition());
    }
    
    public int getLastVisibleRow() {
       int y = myScrollPane.getViewport().getViewPosition().y;
       y += myScrollPane.getViewport().getExtentSize().getHeight();
       int row = myTable.rowAtPoint(new Point(0,y));
       int iLvr = (row > -1 && row < (myTable.getRowCount() - 1)) ? row: (myTable.getRowCount() - 1);
       //krn.OutPrintln(FnEaS.getCallerMethodName() + " -> getLastVisibleRow()rowAtPoint="
       //        + row + " => returning" + iLvr + " rovcount="  + myTable.getRowCount());
       return iLvr;
    }    
    
    public void _valueChanged_event() {

        String newRowStatus = null;

        if (bTableCaching || bIsQryEmpty || !bIsQryOpened) {
            // vsetky stavy riadku su neplatne, a id riadku sa neda odoslat
            bDbFirstRow = bDbLastRow = bTblFirstRow = bTblLastRow = false;
            sCurrentMasterKeyValue = "";
            setCurrSelectedRow(-1,"VCH-A");
        }
        else setCurrSelectedRow(myTable.getSelectionModel().getMinSelectionIndex(),"VCH-B");

        if (currSelectedRow == -1) { // Tabulka neobsahuje ziadny riadok
            // vsetky stavy riadku su neplatne, a id riadku sa neda odoslat
            bDbFirstRow = bDbLastRow = bTblFirstRow = bTblLastRow = false;
            sCurrentMasterKeyValue = "";
            newRowStatus = "noRowAvailable";
                
         } // if (currSelectedRow == -1) { // Tabulka neobsahuje ziadny riadok
            
        else {   
            // Aktualizacia hodnot master a order klucov podla vysvieteneho riadku
            sCurrentMasterKeyValue = (String) myTable.getValueAt(currSelectedRow,
                getTblColumnIndexBydbFieldName(myTable, sMasterKey));
            sCurrentOrderKeyValue = (String) myTable.getValueAt(currSelectedRow,
                getTblColumnIndexBydbFieldName(myTable, sOrderKey));
            // viacnasobny vyskyt udalosti value_changed ignoruje sa

            if (!(sTestedMasterKeyValue==null)) {
               if (sTestedMasterKeyValue.equals(sCurrentMasterKeyValue)
                  && (!FnEaS.getCallerMethodName().equals("afterInitialize"))) {
                   return;
               }
            }

            sOldCurrentMasterKeyValue = sCurrentMasterKeyValue;
            sTestedMasterKeyValue = sCurrentMasterKeyValue;

            // urcenie stavu aktualneho riadku tabulky podla jeho pozicie

            // je to prvy riadok v DB-tabulke ?
            bDbFirstRow = myXQuery.isFirstRow(sOrderKey, sCurrentOrderKeyValue);
            // je to posledny riadok v DB-tabulke ?
            bDbLastRow = myXQuery.isLastRow(sOrderKey, sCurrentOrderKeyValue);
            // je to prvy riadok v DB-tabulke ?
            bTblFirstRow = (currSelectedRow == 0); // (-1 lebo prvy riadok je NULTY !!!)
            // je to posledny riadok v DB-tabulke ?
            bTblLastRow = (currSelectedRow == myTblModel.getRowCount() -1);
            
            /* NEMAZAT - dolezity test v pripade problemov 
            ***********************************************************************
            ///if (FnEaS.sObjName(myObjectID).equals("DBT_eas_users")) {
            krn.OutPrintln("\nDBT_eas_users -> ValueChanged ->> sCurrentOrderKeyValue="
                        + sCurrentOrderKeyValue
                        + "\nsCurrentMasterKeyValue=" + sCurrentMasterKeyValue
                        + " = " + sTestedMasterKeyValue
                        + " \nMySelectedRow=" + iMySelectedRow 
                        + " \nisTblFirstRow()=" + bTblFirstRow
                        + " \nisTblLastRow()=" + bTblLastRow
                        + " \nisDbFirstRow()=" + bDbFirstRow
                        + " \nisDbLastRow()=" + bDbLastRow
                        + "\niMySelectedRow == myTableModel.getRowCount()"  
                        + iMySelectedRow + " == " + (myTblModel.getRowCount() -1)
                    ); 
             /// }
             ***********************************************************************/
            // QQQ - nemal by tu byt test, ci to vlastne treba ? - mozno sa to automaticky
            // NESCROLL-uje, ked je riadok viditelny, a potom test netreba
            myTable.scrollRectToVisible(myTable.getCellRect(currSelectedRow , 0, true));
            if (bTblLastRow && !bDbLastRow) {
               //// krn.OutPrintln("citame novy chunk na spodok tabulky.");
                updateTableModelUpOrDown(sCurrentOrderKeyValue, "DOWN", false);
            } 
            if (bTblFirstRow && !bDbFirstRow) {
               //// krn.OutPrintln("citame novy chunk na spodok tabulky.");
                updateTableModelUpOrDown(sCurrentOrderKeyValue, "UP",  false);
            } 
            krn.debugOut(myObjectID,0,"_evt_valueChanged() on table " + sMasterTable + ": " + bTableCaching + " " 
                + bIsQryEmpty + " " + (!bIsQryOpened) + " selRow: " + currSelectedRow
                + " CurMasterKey: " + sCurrentMasterKeyValue); 

            EOC_message msg = new EOC_message(myObjectID,"goToRow",sMasterTable + "=" + sCurrentMasterKeyValue, "");
            krn.krn_sendMessage(myObjectID,msg,"row", "target");

            if (bDbFirstRow && !bDbLastRow ) {
                // neexistuje predosli riadok v tabulke DB
                newRowStatus = "firstRow";
            }
            else if (bDbLastRow && !bDbFirstRow) {
                // neexistuje dalsi riadok v tabulke DB
                newRowStatus = "lastRow";
            }
            else if (!bDbLastRow && !bDbFirstRow) {
                    newRowStatus = "notFirstOrLast";
            }
            else if (bDbLastRow && bDbFirstRow) {
                    newRowStatus = "oneRow";
            }
        }
        // funkcia zmeni stav objektu, a distribuuje 
        // signal o zmene stavu ostatnym objektom
        setCurrentRowStatus(newRowStatus);
    } // public void _evt_valueChanged()
    
    public void _evt_vk_page_down() {

        int iLstVisibleRow = getLastVisibleRow(); // posledy viditelny riadok na obrazovke
        oldMovingVector = currMovingVector;
        currMovingVector = "DOWN";
        if (sCurrentRowStatus.equals("lastRow") || sCurrentRowStatus.equals("oneRow")
                || sCurrentRowStatus.equals("noRowAvailable")) {
           return ;
        }
        if ( iLstVisibleRow < iCurrVisibleRows) {
            goToLast(null);
            return;
        }
        krn.OutPrintln("iCurrVisibleRows==>" + iCurrVisibleRows);
        // ked pod aktualnym najspodnejsim viditelnym riadkom uz nie je dost riadkov, 
        // t.j. 1 CHUNK,t.j.  iNumFetchedRows  riadkov, prida sa naspodok dalsi CHUNK
        krn.OutPrintln(">>>>AAA>> " + iLstVisibleRow +" > " + myTable.getRowCount() + " - " + (iCurrVisibleRows));
        if (iLstVisibleRow > (myTable.getRowCount() - iCurrVisibleRows) - 1)
        {
            int i = updateTableModelUpOrDown(getTblMaxOrderKeyVal(), "DOWN", false);
            if (i > 0) {
            krn.OutPrintln(11111333);
                setCurrSelectedRow(currSelectedRow + iCurrVisibleRows - 1,"1");
            krn.OutPrintln(11122333);
           }
            else { // uz nebolo co pridat, skace sa na posledny riadok
                setCurrSelectedRow(myTable.getRowCount() - 1,"2");
                bDbLastRow = true;
                bTableCaching = false;
            }
        }
        else {
            krn.OutPrintln(11111);
            setCurrSelectedRow(currSelectedRow + iCurrVisibleRows - 1,"3");
            krn.OutPrintln(11122);
        }
//////        myTable.clearSelection();
        if (iLastErasedRows==0) {
            myTable.scrollRectToVisible(myTable.getCellRect(currSelectedRow, 0, true));
        }
        else {
            Rectangle rct;
            rct = myTable.getCellRect(currSelectedRow - iCurrVisibleRows, 0, true);
            myTable.scrollRectToVisible(rct);
            rct.setLocation(rct.x,rct.y+myViewport.getHeight());
        }
        myTable.changeSelection(currSelectedRow, 0, false, false);
        bTableCaching = false;
        _valueChanged_event();
    } // _evt_vk_page_down()
    
    public void _evt_vk_page_up() {

        int iFirstVisibleRow = getFirstVisibleRow(); // posledy viditelny riadok na obrazovke
        oldMovingVector = currMovingVector;
        currMovingVector = "UP";
        if (sCurrentRowStatus.equals("firstRow") || sCurrentRowStatus.equals("oneRow")
                || sCurrentRowStatus.equals("noRowAvailable")) {
           return ;
        }
        if ( iFirstVisibleRow == 0) {
            goToFirst(null);
            return;
        }
        // ked pod aktualnym najspodnejsim viditelnym riadkom uz nie je dost riadkov, 
        // t.j. 1 CHUNK,t.j.  iNumFetchedRows  riadkov, prida sa naspodok dalsi CHUNK
        if (iFirstVisibleRow <= iCurrVisibleRows  - 1 )
        {
/////           krn.krnMsg(iFirstVisibleRow + " <= " + (iCurrVisibleRows - 1));
           int i = updateTableModelUpOrDown(getTblMinOrderKeyVal(), "UP", false);
            if (i > 0) {
                setCurrSelectedRow(currSelectedRow + i - iCurrVisibleRows + 1,"1");
   myTable.changeSelection(currSelectedRow, 0, false, false);
            Rectangle rct;
            rct = myTable.getCellRect(currSelectedRow + iCurrVisibleRows, 0, true);
            myTable.scrollRectToVisible(rct);
            rct = myTable.getCellRect(currSelectedRow, 0, true);
            myTable.scrollRectToVisible(rct);
            String sgp = myTable.getValueAt(currSelectedRow +  iCurrVisibleRows, 1).toString();
            //krn.krnMsg("11stlocc!!!xxx " + rct.x + " " + rct.y + " + " +  myViewport.getHeight()
            //        + "\n " + (rct.y + myViewport.getHeight()) + "\n" + sgp);
           rct.setLocation(rct.x, 1 /*rct.y + myViewport.getHeight()*/);
    /////       myScrollPane.s);
            //krn.krnMsg("22stlocc!!!");
            }
            else { // uz nebolo co pridat, skace sa na posledny riadok
                setCurrSelectedRow(0,"2");
                bDbLastRow = true;
                bTableCaching = false;
            }
/////            krn.krnMsg("retttA = " + currSelectedRow + " i = " + i);
        }
        else {
            setCurrSelectedRow(currSelectedRow - iCurrVisibleRows + 1,"3");
            Rectangle rct;
            rct = myTable.getCellRect(currSelectedRow, 0, true);
//            myTable.scrollRectToVisible(rct);
            rct.setLocation(rct.x,0 /*rct.y - myViewport.getHeight()*/);
           myTable.changeSelection(currSelectedRow, 0, false, false);
           //// krn.krnMsg("retttB = " + currSelectedRow + " i = " + 0);
            return;
        }
        
 /////       krn.krnMsg("iLastErasedRows===" + iLastErasedRows + "  + currSelectedRow=" +  + currSelectedRow);
 ////                   myTable.scrollRectToVisible(myTable.getCellRect(currSelectedRow, 0, true));

//        if (iLastErasedRows==0) {
//            myTable.scrollRectToVisible(myTable.getCellRect(currSelectedRow, 0, true));
//        }
//        else {
//            Rectangle rct;
//            rct = myTable.getCellRect(currSelectedRow - iCurrVisibleRows, 0, true);
//            myTable.scrollRectToVisible(rct);
//            rct.setLocation(rct.x,0 /*rct.y - myViewport.getHeight()*/);
//        }

        //krn.krnMsg("scrolled");        
        myTable.changeSelection(currSelectedRow, 0, false, false);
        bTableCaching = false;
        _valueChanged_event();
    } // _evt_vk_page_down()
    
    public void _evt_vk_page_upppp() {
        
        int iFirstVisibleRow = getFirstVisibleRow(); //prvy viditelny riadok na obrazovke
        oldMovingVector = currMovingVector;
        currMovingVector = "UP";
        if (sCurrentRowStatus.equals("firstRow") || sCurrentRowStatus.equals("oneRow")
                || sCurrentRowStatus.equals("noRowAvailable")) {
            return ;
        }

        int iAddedRows = 0;
        // ked nad aktualnym najvrchnejsim viditelnym riadkom uz nie je dost riadkov, 
        // t.j. 1 CHUNK,t.j.  iNumFetchedRows  riadkov, prida sa navrch dalsi CHUNK
        if(iFirstVisibleRow < iCurrVisibleRows  - 1 ) 
        {
        krn.OutPrintln("AAAAA -- iFirstVisibleRow=" + iFirstVisibleRow);
           iAddedRows = updateTableModelUpOrDown(getTblMinOrderKeyVal(), "UP", false);
           iFirstVisibleRow = getFirstVisibleRow();
           krn.OutPrintln("BBBBB -- iFirstVisibleRow=" + iFirstVisibleRow + " iAddedRows=" + iAddedRows );
           ///00 krn.krnMsg(" BBBBB_AAAAA");
////          krn.krnMsg("iMySelectedRowA0=>>> " + iMySelectedRow + " added rows = " + iAddedRows);
           
           if (iAddedRows > 0) {
           if (currSelectedRow > (iCurrVisibleRows + 1)) {
              setCurrSelectedRow(currSelectedRow - iCurrVisibleRows + 1,"1");
////            krn.krnMsg("iMySelectedRowA1=>>> " + currSelectedRow);
              krn.Message(" A XtablepageUPPPP");
           }
           else {
              setCurrSelectedRow(iAddedRows /*- iCurrVisibleRows + 1*/,"2");
              krn.Message(" B XtablepageUPPPP");
           }
           }
           else { // uz nebolo co pridat, skace sa na prvy riadok
              setCurrSelectedRow(0,"3");
/////            krn.krnMsg("iMySelectedRowA2=>>> " + iMySelectedRow);
              bDbFirstRow = true;
              bTableCaching = false;
           }
        }
        else {
           // iFirstVisibleRow = iFirstVisibleRow - iCurrVisibleRows + 1;
           if (currSelectedRow > (iCurrVisibleRows + 1)) {
               setCurrSelectedRow(currSelectedRow - iCurrVisibleRows + 1,"4");
////             krn.krnMsg("iMySelectedRowB1=>>> " + currSelectedRow);
          }
           else {
               setCurrSelectedRow(0,"4"); // skok na horny riadok
////               krn.krnMsg("iMySelectedRowB2=>>> " + currSelectedRow);
           }
           //myTable.scrollRectToVisible(myTable.getCellRect(iMySelectedRow, 0, true));
        }
        if (iLastErasedRows==0) {
            myTable.scrollRectToVisible(myTable.getCellRect(currSelectedRow, 0, true));
/////            krn.krnMsg("iMySelectedRowC0=>>> " + iMySelectedRow);
        }
        else {
            Rectangle rct;
            rct = myTable.getCellRect(currSelectedRow/* - iCurrVisibleRows + 1 */, 0, true);
            myTable.scrollRectToVisible(rct);
            rct.setLocation(rct.x,rct.y/*+myViewport.getHeight()*/);
/////            krn.krnMsg("iMySelectedRowC1=>>> " + iMySelectedRow + " added rows = " + iAddedRows);
/////            myTable.changeSelection(iMySelectedRow, 0, false, false);
        }
/////QQQ tu uz je to neskoro        setMySelectedRow(iMySelectedRow);
        if (iAddedRows > 0) {
        krn.OutPrintln("_evt_vk_page_up()->iMySelectedRow = " + currSelectedRow + " added rows = " + iAddedRows);
        }
        myTable.changeSelection(currSelectedRow, 0, false, false);
        bTableCaching = false;
        _valueChanged_event();
        
        //////////////////////////
        ////   iFirstVisibleRow = getFirstVisibleRow();
        ////   setMySelectedRow(iFirstVisibleRow);
        ////   myTable.changeSelection(iFirstVisibleRow, 0, false, false);
    }

    public void calculateAddedRow(int rowIdx) {
    /* 
        krn.OutPrintln("New added row (" + (rowIdx + 1) + ") " + sMasterKey + " = " +
                (String) myTableModel.getValueAt(rowIdx,
                getTblColumnIndexBydbFieldName(myTable, sMasterKey)));                
     */
        Object[] rowData = new Object[iNumRsColumns + 3];
        // spracovanie stlpcov
        String fldName;
        String funcName;
        String calcType;

        calc_block:
        for (int colIdx = 0; colIdx < myTableColumnModel.getColumnCount(); colIdx++) {
//2015-11-17            keby ze sa typuje kolumn moze byt aj CalculatedColumn
//            typovat najpr na tablecolumn az po teste na dalsi typ potom pokracovat dalej
             XDBtableColumn tc = (XDBtableColumn) myTableColumnModel.getColumn(colIdx);
             fldName  = tc.getdbFieldName();
             if (!fldName.startsWith("FUN@")) {
                 continue calc_block;
             }
             funcName = fldName.substring(3);
////2015-11-19             calcType = tc.getDefaultValue();
             calcType = fldName.substring(0,3);
             //System.out.print(fldName);
             if (methodSource == null) methodSource = myObjectID;
             Integer callType = 0;
             if (calcType.startsWith("FUN@")) {
////                 System.out.println("XTABLE_INVOOOKINGGG INN;" + methodSource.getClass().getSimpleName());
                 Method mtd = //null;
                         FnEaS.getEOCMethod(methodSource, funcName, new Class[] {XTable.class,Integer.class});
                 if (mtd==null) {
                     mtd = FnEaS.getEOCMethod(methodSource, funcName, new Class[] {Object.class});
                     if (mtd!=null) callType = 1;
                 }
                 if (mtd==null) {
                     mtd = FnEaS.getEOCMethod(methodSource, funcName, new Class[] {null});
                     if (mtd!=null) callType = 2;
                 }
                 if (mtd==null)  {
                     myTblModel.setValueAt( "NO_FUNCTION_" + funcName, rowIdx, colIdx);
                 }
                 else {
////                     System.out.println("calculateAddedRow---fldName:" + fldName +
////                         "XTABLE_callType==" + callType + " inn:" + myObjectID.toString());
                     try {
                         ////myTable.setValueAt( "FUNCTION_OK_" + funcName, rowIdx, colIdx);
                         String calcVal = "XTB_NULL_calcValue";
                         // parametre XTable, Integer rowIdx,
                         // vatsinou je metoda umiestnena v externom objekte/programe
                         if (callType == 0) {
                             try {
                             calcVal = (String) mtd.invoke(methodSource, (XTable) myObjectID, (Integer) rowIdx);
                             }
                             catch (InvocationTargetException ex) {
                              //   log.error("oops!", );
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, "KUURNIK: " + ex.getCause());
                             }
                             
                         }    
                         // parametre: Integer rowIdx,
                         // vatsinou je metoda umiestnena v children XTable-objekte
                         if (callType == 1)
                             calcVal = (String) mtd.invoke(methodSource, (Integer) rowIdx);
                         // parametre: Ziadne,
                         // Metoda by mala byt umiestnena v children XTable-objekte
                         // inak nema priliz zmysel
                         if (callType == 2)
                             calcVal = mtd.invoke(methodSource).toString();
                         myTable.setValueAt(calcVal ,rowIdx,colIdx);
                     } catch (IllegalAccessException ex) {
                         System.out.println("XTABLE_IACCESS");
                         Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (IllegalArgumentException ex) {
                         System.out.println("XTABLE_IARG");
                         Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (InvocationTargetException ex) {
                         System.out.println("XTABLE_ITARG");
                         Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
                     }
                 }
             }
        }
    }
    
    private void setCurrentRowStatus (String newStatus) {
        sCurrentRowStatus = newStatus;
        _RowStatusChanged();
    }
    public void setCurrentObjPerms() {
        if (sCurrentRowStatus.equals("noRowAvailable")) {
            super.setObjPerms(new EOC_message(myObjectID,"setObjPerms","N","true"));
        }
        else {
            super.setObjPerms(new EOC_message(myObjectID,"setObjPerms","NUCDP","true"));
        }
    }
    
    private void _RowStatusChanged() {
                setCurrentObjPerms();

        if ( sCurrentRowStatus.equals("noRowAvailable")
          || sCurrentRowStatus.equals("oneRow")     
          || sCurrentRowStatus.equals("notFirstOrLast")     
          || sCurrentRowStatus.equals("lastRow")     
          || sCurrentRowStatus.equals("firstRow")     
        ) {
               EOC_message msg = new EOC_message(myObjectID,sCurrentRowStatus,"", "");
               krn.krn_sendMessage(myObjectID,msg,"navigation", "source");
        }
        
        // sprava row-targetom o zmene riadku
        /*
        if ( sCurrentRowStatus.equals("noRowAvailable") ) {
            setLocalObjPerms("N");
            EOC_message msg = new EOC_message(myObjectID,sCurrentRowStatus,"", "");
            krn.krn_sendMessage(myObjectID,msg,"row", "target");
        }
*/
        if ( sCurrentRowStatus.equals("goToRow") ) {
            krn.Message ("PIICOVINA-goToRow-status v _rowStatusChanged in XTable !!!");
            EOC_message msg = new EOC_message(myObjectID,"goToRow",sMasterTable + "=" + sCurrentMasterKeyValue, "");
            krn.krn_sendMessage(myObjectID,msg,"row", "target");
        }
        
        if (sCurrentRowStatus.equals("noRowAvailable")) {
            setLocalObjPerms("N");
            sCurrentMasterKeyValue = "";
//            EOC_message msg = new EOC_message(myObjectID,"goToRow",sMasterTable + "=" + sCurrentMasterKeyValue, "");
            EOC_message msg = new EOC_message(myObjectID,sCurrentRowStatus,"", "");
            krn.krn_sendMessage(myObjectID,msg,"row", "target");
        }
        else {
            determineLocalObjPerms(); 
            setObjPerms(new EOC_message(myObjectID,"setObjPerms",localObjPerms,"")); // podla stavu objektu sa da zadat iba novy roadok
        // odoslanie noveho ID-u row-target-om
        if ((sTestedMasterKeyValue!=null) && (!sTestedMasterKeyValue.equals(sCurrentMasterKeyValue))) {
            EOC_message msg = new EOC_message(myObjectID,"goToRow",sMasterTable + "=" + sCurrentMasterKeyValue, "");
            krn.krn_sendMessage(myObjectID,msg,"row", "target");
            sTestedMasterKeyValue = sCurrentMasterKeyValue;
        }
        }
    }
    
    public void determineLocalObjPerms() {
       localObjPerms = ((sCurrentMasterKeyValue!=null)&&(!sCurrentMasterKeyValue.equals(""))
                        ?"NCUDP":"N");
       setObjPerms(new EOC_message(myObjectID,"setObjPerms",localObjPerms,"")); // podla stavu objektu sa da zadat iba novy roadok
    }
    
   private void setLocalObjPerms(String sPerms) {
       localObjPerms = sPerms;
       setObjPerms(new EOC_message(myObjectID,"setObjPerms",localObjPerms,"")); // podla stavu objektu sa da zadat iba novy roadok
   } 

   public String getCurrentObjPerms() {
       if (sCurrentRowStatus.equals("noRowAvailable"))
           return "N";
       else
           return "NUCDP";
   } 
   
    public String noRowAvailable(EOC_message msg) {
        //kurva eletbe. ez hianyzott !!! 2016-01-17:18:18
        sTestedMasterKeyValue = null;
        closeQuery();
        return "";
    }    

    // skok na prvy riadok DB tabulky
    public String filterChanged (EOC_message eocMsg) {
        setUsrWhere(eocMsg.getParameters());
        return "";
    }

    @Override
    public String destroy() {
        try {
            super.destroy(); //To change body of generated methods, choose Tools | Templates.
            // 2015-8-20
////            System.out.println("XTable - destroooing >> " + FnEaS.sObjName(this) /*this.toString()*/);
            if (crs!=null) crs.close();  // pracovny rowset na preberanie udajov z XQuery
            if (xcrs!=null) xcrs.close(); // pracovny rowset na preberanie jednej vety z XQuery
        } catch (SQLException ex) {
            Logger.getLogger(XTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (myXQuery !=null) myXQuery.destroy();
        return "";
    }
    private void deleteSortVectorIconFromColHeaders() {
         eoc.dbdata.DBtableColumn currTC;
         Character lastCh;
         // mazanie nepotrebnych vektor sipok, ked existuje
         for (int i = 0; i < myTable.getColumnCount(); i++) {
              currTC = (DBtableColumn) myTable.getColumnModel().getColumn(i);
              lastCh = currTC.getFieldLabel().charAt(currTC.getFieldLabel().length() - 1);
              ////System.out.println("lastCh===" + lastCh);
              if (lastCh == '▲' || lastCh == '▼') {
              //if (i != columnIndex) {
                  currTC.setHeaderValue(currTC.getFieldLabel().substring(0, 
                  currTC.getFieldLabel().length() - 2));
              //}  
              }
         }
    }
    
    public void clearOrdering() {
                   // mazanie nepotrebnych vektor sipok, ked existuje
//                    System.out.println("CTRL+I baszmeg");
                    deleteSortVectorIconFromColHeaders();
                    //e.consume();
                    revalidate();
                    repaint();  // !!! bez tohoto sa neobjavi novy headervalue na obrazovke !
                    sOrderKey = sMasterKey;
                    sOrderKeyDataType = sMasterKeyDataType;
                    sOrderVector = "asc";
                    if (sXTableType.equals("DBTABLE")) {
                        myXQuery.invalidate();
                        myXQuery.setOrdering(sOrderKey, sOrderVector,sOrderKeyDataType);
                        //myXQuery.setOrderKey(sOrderKey, sOrderKeyDataType);
                    }
                    closeQuery();
                    open_Query();
    }
    
    public String refresh(String sPar) {
       // System.out.println("1111111-refreshujem baszmek na: " + sPar);
       this.closeQuery();
        myXQuery.setExternalKeyValue(sCurrentExternalKeyValue);
        this.rebuildQuery();
        this.open_Query();
        goToFirst(null);
        //myTable.revalidate();
        //myTable.repaint();
        //System.out.println("22222222-refreshujem baszmek na: " + sPar);
        return "";
    }
    public void setMethodSource(Object mtdSrc) {
        methodSource = mtdSrc;
    }
     public Object getColumnValue (String colName, Integer rowIdx) {
        //saktoto neje pouzite
        Object o = "COLNOTFOUND:" + colName;
        XDBtableColumn xcl;
        for (int i = 0; i < myTableColumnModel.getColumnCount(); i++) {
            xcl = (XDBtableColumn) myTableColumnModel.getColumn(i);
//            System.out.println("Teestingcolumn: " + xcl.getdbFieldName() + "->  FORCOLANME:" + colName);
            if (xcl.getdbFieldName().equalsIgnoreCase(colName)) {
                int iMdlIdx = xcl.getModelIndex();
                o = myTblModel.getValueAt(rowIdx, iMdlIdx);
//                System.out.println("TeestingcolumnRETuurning: " + o.toString() + " idxcol:" + i + " mdlCOL:" + iMdlIdx);
                break;
            }
        }
        return o;
    }
    
     public void recalculateRows() {
         //// System.out.println("CAAALCUULROOOWS");
         for (int i = 0; i < myTblModel.getRowCount(); i++) {
             calculateAddedRow(i);
         }
     }
     
     public Object getValueBydbFieldName(String sFieldName) {
         Object oRetVal = null;
         int colIdx = getTblColumnIndexBydbFieldName(myTable, sFieldName);
         oRetVal = myTable.getModel().getValueAt(myTable.getSelectedRow(), colIdx);
         return oRetVal;
     }
     /**
      *  Vrati hodnotu cell-u na riadku 'rowIdx' 
      * pre databazovy udaj s nazvom 'sFieldName'
      * @param rowIdx
      * @param sFieldName
      * @return 
      */
     public Object getValueBydbFieldName(Integer rowIdx, String sFieldName) {
         Object oRetVal = null;
         int colIdx = getTblColumnIndexBydbFieldName(myTable, sFieldName);
         //// System.out.println("COOOLIDX:" + colIdx + " FOOR:" + sFieldName);
         oRetVal = myTable.getModel().getValueAt(rowIdx, colIdx);
         //// System.out.println("COOOLIDX:" + colIdx + " FOOR:" + sFieldName + " ORETV:" + oRetVal.toString());
         return oRetVal;
     }
     
     public String getCurrentRowStatus() {
         return sCurrentRowStatus;
     }
    
   public void rowDblClicked() {
       System.out.println("NOT_OVERRIDDEN_rowDblClicked()");
   }

   
    public String deleterow(EOC_message eocMsg) {
//       sTxnType = "delete";
      if (sCurrentMasterKeyValue==null || sCurrentMasterKeyValue.equals("")) {
         //   Kernel.staticMsg("Nie je dostupný žiadny označený riadok tabuľky. STATICMSG");
          krn.Message("W","Nie je dostupný žiadny označený riadok tabuľky.","");
          return "";
      }
      if (!krn.krnQuest("QN","Vymazať aktuálnu vetu ?","")) return "<CANCELED>";
      try {
          String stm = "";
                 stm = "delete from " + sMasterTable + " where " 
                     + sMasterKey + " = "  + sCurrentMasterKeyValue.toString();
          //ResultSet rss; // pomocny resultset
                 System.out.println("DELSTATEMENTT: " + stm);
          PreparedStatement pss = MyCn.getConn().prepareStatement(stm);
          pss.execute();             
          pss.close(); // ????????    
          MyCn.getConn().commit();
          ((DefaultTableModel) myTable.getModel()).removeRow(myTable.getSelectedRow());

          EOC_message msg = new EOC_message(myObjectID,sCurrentMasterKeyValue.toString(),"", "");
          krn.krn_sendMessage(myObjectID, msg, "row", "target");
////          krn.krn_sendMessage
////           ((Object) this, "rowDeleted", sMasterKeyValue.toString(), "row", "target" , "");
      } 
      catch (SQLException ex) {
         krn.Message(this,"E", ex.getMessage(),"Chyba pri pokuse o výmaz vety: " );
         return "UPDATE_ERROR" +  ex.getMessage();
      }                                              
      finally {
    //          sTxnType = "";
         return "";
      }
    }
     
     @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("XTB_jpanel");
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

