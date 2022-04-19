/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.widgets;

import eoc.EOC_message;
import eoc.IEOC_Object;
import eoc.database.DBconnection;
import system.Kernel;
import system.FnEaS;
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
//import javax.swing.table.TableColumn;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableModel;
import javax.swing.table.*;

/**
 *  DBtable
 *  Zaklad/rodic pre objekty typu browser databazovych tabuliek
 * 2013-1-28
 * @author rvanya
 */
public class DBtable extends eoc.widgets.PObject {
    private PreparedStatement   pstm;
    //private Statement           stc;
    private ResultSet         rset;  
    private String   sExternalTbl = ""; // cudzia riadiaca tabulka, na ktorom 
                                     // je dotaz browsera zavysly
    private String   sExternalKey = ""; // meno stlpca cudzej tabulky, na ktorom
                                     // je dotaz browsera zavysly
    private Object owRowSource; // Zdroj, poskytujuci hodnotu sExternalKey
    private String   sMasterTbl = ""; // hlavna tabulka dotazu
    private String   sMasterKey = ""; // vyhladavany stlpec hlavnej tabulky dotazu
                                 // pri spravne navrhnutej DB to je ekvivalent
                                 // sExternalKey
    private String sMasterKeyDataType = "number"; // datovy typ sMasterkey
                                       // char,number,date,datetime,boolean,...
    private String sMasterKeyValue = ""; // aktualna hodnota vyhladavaneho kluca
    private String sDisplayedFields = ""; // potrebne stlpce
    
    public String getsMasterKeyValue() {
        return sMasterKeyValue;
    }

    public void setsMasterKeyValue(String sMasterKeyValue) {
        this.sMasterKeyValue = sMasterKeyValue;
    }
    private String[] sSlaveTbls; // podriadene tabulky v dotaze
    private Long     currentTblId; // ID aktualneho riadku
    private Long    oldCurrentTblId;  // Naposledy spracovany ID
    private Long        testedTblId; // prave spracovany ID (valueChanged event)
    private int        currentRow; // prave spracovany row (valueChanged event)
    private int       numTblColumns; // pocet VSETKYCH(calculated,others,RSrow) 
                                     // stlpcov tabulky 
    private int        numRsColumns; // Pocet stlpcov tabulky, nacitane 
                                     // z resultSet-u (VVZDY SU PRED ostatnymi
                                     // stlpcami)
    private int numDisplayedColumns;
    
    // casti retazca query prikazu 
    private String queryBase;          // select * from eas_usrgrp
    private String queryAppWhere;      // !eas_usrgrp.bDisabled
    private String queryUsrWhere;      // eas_usrgrp.c_meno = 'jozika'
    private String queryAppOrderBy;    // !eas_usrgrp.bDisabled
    private String queryUsrOrderBy;    // eas_usrgrp.c_meno = 'jozika'
    private String queryFullStatement; // poskladany dotaz
    private String queryPrevStatement; // predosly poskladany dotaz
    // Stavy objektu 
    private boolean bIsQryOpened=false; // ci je dotaz otvoreny
    private boolean bIsQryEmpty=true;   // ci je dotaz otvoreny
     private boolean bFirstRow;         // ci je na prvom riadku/8
    private boolean bLastRow;           // ci je na poslednom riadku
    // Pointery/casti children-objektu (potomka)
    private JScrollPane myScrollPane;
    private JTable      myTable;
    private Object myObjectID;

    // premenne, riadiace caching tabulky v browseri
    private boolean tableCaching    = true;
    private int     numFetchedRows  = 30;
    private int     numCachedChunks = 0; // uz nacitane davky riadkov z RS
    private int     numMaxChunks = 3;    // default
    private int     firstTblRow = 0;     // prvy riadok v tabulky (vzdy nula !)
    private int     lastTblRow = 0;      // v principe sa rovna ..
                                         // .. numCachedChunks * numFetchedRows
    private int     firstRSRow = 0;      // row z RS na 1. riadku tabulky
    private int     lastRSRow = 0;       // row z RS na posl. riadku tabulky
    private eoc.IEOC_Object owOpposite; // objekt na druhej strane Link-u 
    private String sEOCobjectType = "<EOC_dbtble_object_type_not_defined>";

    // public String initialize(EaS_krn kr, Connection cnn) {
    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        super.initialize(kr, cX);
        myObjectID = this;
        setEOC_objectType("DBtable");
        // QQQ - TU BY MAL BYT TEST OBSAHU ZVONKU NASTAVOVANYCH PREMENNYCVH 
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        // **** TU - event
        public void valueChanged(ListSelectionEvent e) {
           krn.debugOut(this,5,"myObjectID " + myObjectID + 
                   " value changed=>" + tableCaching + " " + bIsQryEmpty  + " " +  !bIsQryOpened
                   +(tableCaching || bIsQryEmpty || !bIsQryOpened));
            if (tableCaching || bIsQryEmpty || !bIsQryOpened) {return;}; 
            // pocas upravy obsahu tebulky,prazdeho dotazu, a.t.d.
            // sa ignoruje vyskyt tejto udalosti
            currentRow =  myTable.getSelectedRow(); // aktualna, vysvietena veta  
            if (currentRow == -1) {return;}
            // pozicia akt.vety v resultSete  
            krn.OutPrintln(numDisplayedColumns + "-- " + currentRow);
//            Object aktRsRow = myTable.getValueAt(currentRow, numDisplayedColumns - 1); 
            try {
                rset.absolute(myTable.getSelectedRow() + 1);
                testedTblId = rset.getLong(sMasterKey);
                if (testedTblId.equals(currentTblId)) {return;}
                } catch (SQLException ex) {
                    Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
                }
                oldCurrentTblId = currentTblId;
                try {
                    currentTblId = rset.getLong(sMasterKey);
                } catch (SQLException ex) {
                    Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
                }
                EOC_message msg = new EOC_message((IEOC_Object) myObjectID,"goToRow", sMasterTbl + "=" + currentTblId.toString(), "");
                krn.krn_sendMessage((IEOC_Object)myObjectID,msg,"row", "target");
////               krn.krn_sendMessage((Object) myObjectID, (Object) myObjectID, "goToRow", 
////                        sMasterTbl + "=" + currentTblId.toString() , "row", "target", "");
       } // public void valueChanged(ListSelectionEvent e) {
        
       }); // .addListSelectionListener
        myTable.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
               krn.OutPrintln("e.getKeyCode()=" + e.getKeyCode() + "++>" + e.getKeyChar());
               krn.OutPrintln("myObjectID=" + myObjectID);
               // Ctrl+j
               if (e.getKeyCode()==74 && e.isControlDown()) {
                  krn.Message(this, "I", queryFullStatement, "Aktuálny dotaz");
                  e.consume(); return;
               }
               int riadok = myTable.getSelectedRow(); 
               
               if(e.getKeyCode() == KeyEvent.VK_UP) {
                  if (riadok >= 0) {riadok = riadok - 1;}
                  if (riadok ==-1) {
                     if (firstRSRow == 1) {e.consume(); return;}
                     updateTableModelUp(firstRSRow - 1, true);
                  }
               } // if(e.getKeyCode() == KeyEvent.VK_UP) {
               
               if(e.getKeyCode() == KeyEvent.VK_DOWN){
                  riadok = riadok + 1;
                  //   krn.OutPrintln("VK_DOWN->riadok: " + riadok +
                  //  " lastTblRow: " + lastTblRow);
                  if (riadok ==lastTblRow) 
                     {updateTableModelDown(lastRSRow + 1, true);}
               } // if(e.getKeyCode() == KeyEvent.VK_DOWN){
               
               if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN 
                                 && riadok ==myTable.getRowCount()-1){
                  updateTableModelDown(lastRSRow + 1, false);
               } // if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && ...
               
               if(e.getKeyCode() == KeyEvent.VK_PAGE_UP && riadok ==0){
                  //if (riadok == 0) {return;}
                  if (firstRSRow == 1) {e.consume(); return;}
                  updateTableModelUp(firstRSRow - 1, false);
                 } // if(e.getKeyCode() == KeyEvent.VK_PAGE_UP && riadok ==0){
                 
            } // public void keyPressed(KeyEvent e) {

            @Override
            public void keyReleased(KeyEvent e) {
            }
        }); // .addKeyListener
        
        myScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
           @Override
           public void adjustmentValueChanged(AdjustmentEvent e) {
              int akt = myScrollPane.getVerticalScrollBar().getValue() 
                      + myScrollPane.getVerticalScrollBar().getModel().getExtent();
              if (bIsQryEmpty || bIsQryOpened) { return; }
              if(akt == myScrollPane.getVerticalScrollBar().getMaximum()){
                        updateTableModelDown(1, true);
              }
            krn.debugOut(this,0,"akt=>valueChanged - myTable.getSelectedRow()" 
                    + " firstTblRow:" + firstTblRow + " lastTblRow:" + lastTblRow 
                    + " firstRSRow:" + firstRSRow  + " lastRSRow:" + lastRSRow);
              if(akt == myScrollPane.getVerticalScrollBar().getMinimum()){
                  krn.OutPrintln("akt==getMinimum=>" + akt);
                        //updateTableModelUp(1, true);
              }
           } // public void adjustmentValueChanged(AdjustmentEvent e) {
        }); // myScrollPane.getVerticalScrollBar() ...>>>
               // >>>.addAdjustmentListener(new AdjustmentListener()
       return "";
   } 
                
   public void setsMasterKeyDataType(String sMasterKeyDataType) {
      // QQQ - sem patri test platnych hodnot pre parameter, zatial ignorujem
      this.sMasterKeyDataType = sMasterKeyDataType.toUpperCase();
   }
    
    public String getsExternalTbl() {
        return sExternalTbl;
    }

    public void setsExternalTbl(String sExternTbl) {
        this.sExternalTbl = sExternTbl;
    }

    public String getDisplayedFields() {
        return sDisplayedFields;
    }

    public void setDisplayedFields(String sDispFlds) {
        this.sDisplayedFields = sDispFlds;
    }
    public String getsExternalKey() {
        return sExternalKey;
    }

    public void setsExternalKey(String sExternalKey) {
        this.sExternalKey = sExternalKey;
    }

    public Object getoRowSource() {
        return owRowSource;
    }

    public void setoRowSource(Object owRwSrc) {
        this.owRowSource = owRwSrc;
    }

    public void setMyTable(JTable jtbl) {
        myTable = jtbl;
    }
    
    public void setMyScrollPane(JScrollPane jscp) {
        myScrollPane = jscp;
    }
    
    public void buildTblFromResultSet() {
        try {
            krn.OutPrintln("Building table: " + this.toString());
            DefaultTableModel tblModel;
            String colName;
            tblModel = new DefaultTableModel();
            ResultSetMetaData metaData = rset.getMetaData();
            numRsColumns = metaData.getColumnCount();
            if (! sDisplayedFields.equals("")) {
            numDisplayedColumns = FnEaS.iNumEntries(sDisplayedFields, ",");
            }
            else {
               numDisplayedColumns = numRsColumns;
            }
            for_block:
            for (int i = 1; i <= numRsColumns; i++) {
 //              colName = new JLabel (metaData.getColumnName(i).toString(), JLabel.CENTER);
 //              krn.OutPrintln("colanme=" + colName.getText());
               colName = metaData.getColumnName(i);
 //              krn.OutPrintln("colname=" + colName);
               if (! sDisplayedFields.equals("")) {
                   if (FnEaS.iLookup(colName.toUpperCase(), sDisplayedFields.toUpperCase(), ",") == 0) {
 //                     krn.OutPrintln("Ignoring: " + colName);
                      continue for_block;
                   }
                   else {
                      krn.OutPrintln("Accepting: " + colName);
                      
                   }
               }
               krn.OutPrintln(" Adding column--: " + colName);
               TableColumn tblColumn = new TableColumn();
               tblColumn.setHeaderValue((Object) colName);
               tblModel.addColumn(String.valueOf(colName));  
            } // for (int i = 1; i <= numColumns; i++)
            // Vytvorenie stlpca pre row-number DB-ResultSet-u
            colName = "Bacova";
            TableColumn tbColumn = new TableColumn();
            tbColumn.setHeaderValue((Object) colName);
            krn.OutPrintln("HeaderValue="+ tbColumn.getHeaderValue());
            tblModel.addColumn("Bacova");  
            numTblColumns = numDisplayedColumns + 1;
            //krn.krnMsg(this, "I", "Adding db-row-column", "");
            rset.beforeFirst();
            myTable.setModel((TableModel) tblModel);
            rset.next();
            krn.OutPrintln("buildTblFromResultSet -- rs.getRow()--->" + rset.getRow());
            if (rset.getRow() != 0) {
               updateTableModelDown(1, true);
               myTable.setRowSelectionInterval (0, 0); // skok na prvy riadok
            }
            myTable.validate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // public void buildTblFromResultSet() {
    
    private void updateTableModelDown(int FirstRequiredRSrow,boolean bFocus){
       if (!bIsQryOpened || bIsQryEmpty) {
          //krn.OutPrintln("updateTableModelDown->Query not opened !!!");
          return;
       }
       krn.debugOut(this,5,"DOWN Adding next packet of rows - FirstRequiredRSrow="
               + FirstRequiredRSrow);
       tableCaching=true;
       DefaultTableModel tblModel = (DefaultTableModel)myTable.getModel();
       int prevrRowCount = tblModel.getRowCount();
       //krn.OutPrintln("DOWN Rows before : " + prevrRowCount);
       int numAddedRows = 0;
       int newFocusedRow = -1;
       int myRequiredRow = FirstRequiredRSrow;
       if (bFocus) {newFocusedRow = FirstRequiredRSrow;}
        try {
           if (lastRSRow!=0) {
            rset.absolute(lastRSRow); // skok na posledny riadok
           }
           else {
           rset.beforeFirst();   
           }
           krn.debugOut(this,5,"DOWN Adding next chunk of rows: last rs-row:" + lastRSRow);
        } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
       try{                
          JTableHeader th = myTable.getTableHeader();  
          TableColumnModel tcm = th.getColumnModel();  
          while (numAddedRows < numFetchedRows) {
              // QQQ - tento riadok my ukradlo styri dni ! - kuuurva - 2013.1.29 !!!
              if (!rset.next()) {break;};
              Object[] rowData = new Object[numRsColumns + 1];
              // for (int i = 0; i < numRsColumns; i++) {
              for(int i = 0, y = tcm.getColumnCount() - 1; i < y; i++) {  
                 TableColumn tc = tcm.getColumn(i);  
                 // rowData[i] = rst.getObject(i + 1);
                // krn.OutPrintln("tc=" + tc.getHeaderValue());
 
                  rowData[i] = rset.getString(tc.getHeaderValue().toString());
              }
              // novy riadok na 'fokusovanie' je prvy riadok z pridanych
              if (newFocusedRow == -1) {newFocusedRow = rset.getRow();} 
              rowData[numRsColumns] = rset.getRow();
              tblModel.addRow(rowData);
              numAddedRows = numAddedRows + 1;
           } // while (rst.next() && numRows <= numFetchedRows) 
           numCachedChunks = numCachedChunks + 1;
           lastRSRow = lastRSRow + numFetchedRows;
           lastTblRow = lastTblRow + numFetchedRows;
           if (numCachedChunks > numMaxChunks) { // mazanie z hora
              for (int i = 0; i < numFetchedRows; i++) {
                  tblModel.removeRow(0);
                  lastTblRow = lastTblRow - 1; // cis.posl.riadku tabulky sa zmensuje
                  firstRSRow = firstRSRow + 1; // prvy riadok resultset-u sa zvatsuje
                  FirstRequiredRSrow = FirstRequiredRSrow - 1;
              }
                  numCachedChunks = numCachedChunks - 1; // jedna davka bola odstranena
            }
        } catch(Exception ex){
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }  
       finally {   
         tableCaching=false;
      }
      //krn.OutPrintln("DOWN - before adding tablemodel.");
        myTable.setModel(tblModel);  
        /*
        if (bFocus) {
           myTable.setRowSelectionInterval
              (FirstRequiredRSrow, FirstRequiredRSrow);
        }
        * */
        //krn.OutPrintln("DOWN riadok = " + myTable.getSelectedRow());
        //krn.OutPrintln("DOWN Rows after : " + (tblModel.getRowCount()-1));
        validate();
    } 
    // <<< private void updateTableModelDown()

    private void updateTableModelUp(int FirstRequiredRSrow,boolean bFocus){
       krn.debugOut(this,5,"UP Adding next packet of rows - FirstRequiredRSrow="
               + FirstRequiredRSrow);
       tableCaching=true;
       DefaultTableModel tblModel = (DefaultTableModel)myTable.getModel();
       int prevrRowCount = tblModel.getRowCount();
       krn.OutPrintln("UP Rows before : " + prevrRowCount);
       int numAddedRows = 0;
       int newFocusedRow = -1;
       int myRequiredRow = FirstRequiredRSrow;
        try {
            rset.absolute(firstRSRow); // skok na posledny riadok
            krn.OutPrintln("UP Adding next chunk of rows: last rs-row:" + rset.getRow());
        } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
       try{                
           while (numAddedRows < numFetchedRows) {
              rset.previous();
              Object[] rowData = new Object[numRsColumns + 1];
              for (int i = 0; i < numRsColumns; i++) {
                   rowData[i] = rset.getObject(i + 1);
              }
              // novy riadok na 'fokusovanie' je prvy riadok z pridanych
              if (newFocusedRow == -1) {newFocusedRow = rset.getRow();} 
              rowData[numRsColumns] = rset.getRow();
              tblModel.insertRow(0,rowData);
              numAddedRows = numAddedRows + 1;
           } // while (rst.next() && numRows <= numFetchedRows) 
           numCachedChunks = numCachedChunks + 1;
           firstRSRow = firstRSRow - numFetchedRows;
           lastTblRow = lastTblRow + numFetchedRows;
           if (numCachedChunks > numMaxChunks) { // mazanie z dola
              for (int i = 0; i < numFetchedRows; i++) {
                 tblModel.removeRow(lastTblRow);
                 lastTblRow = lastTblRow - 1; // cis.posl.riadku tabulky sa zmensuje
                 lastRSRow = lastRSRow - 1; // posl. riadok resultset-u sa zmensuje
                 FirstRequiredRSrow = FirstRequiredRSrow - 1;
             }
             numCachedChunks = numCachedChunks - 1; // jedna davka bola odstranena
           }
        } catch(Exception ex){
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }  
       finally {   
         tableCaching=false;
      }
      
        myTable.setModel(tblModel);  
        /*
        if (bFocus) {
           myTable.setRowSelectionInterval
              (FirstRequiredRSrow, FirstRequiredRSrow);
        }
        * */
        krn.OutPrintln("UP riadok = " + myTable.getSelectedRow());
        krn.OutPrintln("UP Rows after : " + (tblModel.getRowCount()-1));
        validate();
    } 
    // <<< private void updateTableModelUp()
    
    public String getQueryBase() {
        return queryBase;
    }

    public String setQueryBase(String queryBase) {
        this.queryBase = queryBase;
        return "";
    }

    public String getAppWhere() {
        return queryAppWhere;
    }

    public String setAppWhere(String appWhere) {
        this.queryAppWhere = appWhere;
        return "";
    }

    public String getAppOrderBy() {
        return queryAppOrderBy;
    }

    public String setAppOrderBy(String appOrderBy) {
        queryAppOrderBy = appOrderBy;
        return "";
    }

    public String getUsrWhere() {
        return queryUsrWhere;
    }

    public String setUsrWhere(String usrWhere) {
        this.queryUsrWhere = usrWhere;
        return "";
    }

    public String getUsrOrderBy() {
        return queryUsrOrderBy;
    }

    public String setUsrOrderBy(String usrOrderBy) {
        this.queryUsrOrderBy = usrOrderBy;
        return "";
    }

    public String goToRow(String sMsg) {
        if (!bInitialized || myObjectID==null) {return "";}
        krn.debugOut(this, 5, "Going to row: "  + sMsg);
        if (!sMasterTbl.equals("")) {this.closeQuery();}
        oldCurrentTblId = currentTblId; // zistenie noveho hlavneho ID-u
        if (owRowSource==null) {
            krn.getLinkPartner(myObjectID, "ROW", "SOURCE");
        }
        if (owRowSource != null) {
           DBtable dbt = (DBtable) owRowSource; 
           sMasterKeyValue = dbt.getColVal(sMasterKey);
           krn.debugOut(this,5,"goToRow->sMasterKeyValue = " + sMasterKeyValue);
        }
        this.setAppWhere (sMasterTbl + "." + sMasterKey 
                           + " = '" + sMasterKeyValue + "'");
        String rebuildQuery = this.rebuildQuery();
        String OpenQuery    = this.openQuery();
          // OKAMYH PRAVDY ???
          // OKAMYH PRAVDY ???
               krn.debugOut(this,5,"goToRow->Going to row: 4-po  -> " + queryFullStatement
                       + " --- " + bIsQryOpened + !bIsQryEmpty + numRsColumns
                       + "-" + lastRSRow);
               if (bIsQryOpened==true && bIsQryEmpty==false) {
                   updateTableModelDown(lastRSRow + 1, true);
                   goToFirst("");
                   // TUTTI !!! :-)
               }
          krn.debugOut(this, 5, "Going to row-po GoToFirst: "  + sMsg + " finished");
          return "";
   } // public String goToRow(String sMsg) {
    
    public String getColVal(String sParam) {
        try {
            krn.OutPrintln("DBtable_getColVal-sParam=" + sParam);
            if (sParam == null) {return null;};
            return rset.getString(sParam);
        } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String goToFirst(String sParam) {
        try {
            krn.debugOut(this,5,"goToFirst start");
            if (!bIsQryOpened) {
               krn.debugOut(this,5,"goToFirst query is closed !");
               return "";
            }
           if (bIsQryEmpty) {
               krn.debugOut(this,5,"goToFirst query is emty !");
               return "";
           }
            rset.first(); 
  //                   jTable1.requestFocus();
            // krn.OutPrintln("goToFirst>> " 
           //             + myTable.getRowCount() + " -  " + rst.getRow());
           myTable. changeSelection(0,0,false, false);
                    // skok na prvy riadok dotazu
            oldCurrentTblId = currentTblId; // zistenie noveho hlavneho ID-u
            currentTblId = rset.getLong(sMasterKey);
            // sprava zmeny row-u svojim row-cielom
            if (oldCurrentTblId != currentTblId | sParam.equals("MUST")) {
                krn.debugOut(this,5,"goToFirst--sending-goToRow=" +  currentTblId.toString());
                EOC_message msg = new EOC_message(this,"goToRow", currentTblId.toString(), "");
                krn.krn_sendMessage(this,msg,"row", "target");
           ////   krn.krn_sendMessage((Object) this, (Object) this, "goToRow", currentTblId.toString() ,
           ////                                   "row", "target", "");
            }
            // sprava o dosiahnuti prveho riadku    
            krn.debugOut(this,0,"goToFirst2--sendingFirstRow=" + currentTblId.toString());
            EOC_message msg = new EOC_message(this,"firstRow","", "");
            krn.krn_sendMessage(this,msg,"navigation", "source");
////            krn.krn_sendMessage((Object) this, (Object) this, "firstRow","",
////                                            "navigation", "source", "");

               krn.debugOut(this,0,"goToFirst3--bbb=" + currentTblId.toString());
        } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            krn.debugOut(this,5,"goToFirst fired OK");
        return "";
    }

     public String goToNext(String sParam) {
        try {
            rset.next();
            Integer curr_row;
            curr_row = rset.getRow()-1;
            //krn.OutPrintln(myTable.getRowCount() + " -  " + rst.getRow());
            //myTable.scrollRectToVisible(myTable.getCellRect(rst.getRow()-1 , 0, true));
            myTable.scrollRectToVisible(myTable.getCellRect(rset.getRow()-1 , 0, false));
            myTable.changeSelection(rset.getRow()-1, 0,false, false);
            //myTable.getRowCount() < 
            oldCurrentTblId = currentTblId; // zistenie noveho hlavneho ID-u
            currentTblId = rset.getLong(sMasterKey);
            // sprava zmeny row-u svojim row-cielom
            if (oldCurrentTblId != currentTblId) {
            EOC_message msg = new EOC_message(this,"goToRow",currentTblId.toString(), "");
            krn.krn_sendMessage(this,msg,"row", "target");
////              krn.krn_sendMessage((Object) this, (Object) this, "goToRow" 
////                      , currentTblId.toString() ,"row", "target", "");
            }
            // sprava o dosiahnuti posledneho riadku   
            if (rset.isLast()) {
            EOC_message msg = new EOC_message(this,"lastRow","", "");
            krn.krn_sendMessage(this,msg,"navigation", "source");
////            krn.krn_sendMessage((Object) this, (Object) this, "lastRow","",
////                                              "navigation", "source", "");
            }  else {
            EOC_message msg = new EOC_message(this,"notFirstOrLast","", "");
            krn.krn_sendMessage(this,msg,"navigation", "source");
////               krn.krn_sendMessage((Object) this, (Object) this, "notFirstOrLast","",
////                                              "navigation", "source", "");
             
            }    
         } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String goToPrev(String sParam) {
        try {
            rset.previous();
            krn.OutPrintln(myTable.getRowCount() + " -  " + rset.getRow());
             myTable.scrollRectToVisible(myTable.getCellRect(rset.getRow()-1 , 0, true));
             myTable.changeSelection(rset.getRow()-1, 0,false, false);
          oldCurrentTblId = currentTblId; // zistenie noveho hlavneho ID-u
            currentTblId = rset.getLong(sMasterKey);
            // sprava zmeny row-u svojim row-cielom
            if (oldCurrentTblId != currentTblId) {
              EOC_message msg = new EOC_message(this,"goToRow",currentTblId.toString(), "");
              krn.krn_sendMessage(this,msg,"row", "target");
////              krn.krn_sendMessage((Object) this, (Object) this, "goToRow", currentTblId.toString() ,
////                                              "row", "target", "");
            }

            // sprava o dosiahnuti prveho riadku   
            if (rset.isFirst()) {
                EOC_message msg = new EOC_message(this,"firstRow","", "");
                krn.krn_sendMessage(this,msg,"navigation", "source");
////            krn.krn_sendMessage((Object) this, (Object) this, "firstRow","",
////                                              "navigation", "source", "");
            }   else {
               EOC_message msg = new EOC_message(this,"notFirstOrLast","", "");
               krn.krn_sendMessage(this,msg,"navigation", "source");
////               krn.krn_sendMessage((Object) this, (Object) this, "notFirstOrLast","",
////                                              "navigation", "source", "");
             
            }    
  
        } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

     public String goToLast(String sParam) {
        try {
            krn.debugOut(this,5,"goToLast start");
            rset.last();       // skok na posledny riadok dotazu
            krn.OutPrintln("Posledna veta v tabulke:" + myTable.getRowCount() 
                    + " - Posledna veta v result-set-e: " + rset.getRow());
            myTable.scrollRectToVisible(myTable.getCellRect(myTable.getRowCount()-1 , 0, true));
            myTable.changeSelection(rset.getRow()-1, 0,false, false);

            oldCurrentTblId = currentTblId; // zistenie noveho hlavneho ID-u
            currentTblId = rset.getLong(sMasterKey);
            // sprava zmeny row-u svojim row-cielom
            if (oldCurrentTblId != currentTblId) {
                krn.debugOut(this,5,"goToLast1--sending-goToRow=" +  currentTblId.toString());
              EOC_message msg = new EOC_message(this,"goToRow",currentTblId.toString(), "");
              krn.krn_sendMessage(this,msg,"row", "target");
////              krn.krn_sendMessage((Object) this, (Object) this, "goToRow", currentTblId.toString() ,
////                                              "row", "target", "");
            }
            // sprava o dosiahnuti prveho riadku    
                krn.debugOut(this,5,"goToLast2--sendingLastRow=" + currentTblId.toString());
            EOC_message msg = new EOC_message(this,"lastRow","", "");
            krn.krn_sendMessage(this,msg,"navigation", "source");
////            krn.krn_sendMessage((Object) this, (Object) this, "lastRow","",
////                                             "navigation", "source", "");

               krn.debugOut(this,5,"goToLast3--bbb=" + currentTblId.toString());
        } catch (SQLException ex) {
            Logger.getLogger(/*EOC_query*/DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            krn.debugOut(this,5,"goToLlast fired OK");
        return "";
    }
   
    public String rebuildQuery() {
        String sQry = "";  // base
        String sWhr = "";  // where
        String sOrd = "";  // order by
        if (queryBase == null || queryBase.equals("")) {
            return "EOC-ERR=Prázdna definícia základu dotazu !"; // CHYBA
        }
        
        sQry = queryBase;  // zaklad dotazu
        if (!sExternalKey.isEmpty()) {
             if (sMasterKeyValue!=null) {
             if (sMasterKeyDataType.equals("NUMBER") 
                 || sMasterKeyDataType.equals("BOOLEAN")) {
                 sWhr = "(" + sMasterKey + " = " + sMasterKeyValue + ")";
             }
             else {// QQQ - docasne, treba domysliet
                 sWhr = "(" + sMasterKey + " = '" + sMasterKeyValue + "')";
             }
             } // if (sMasterKeyValue!=null)
             else {
                 sWhr = "(" + sMasterKey + " = null" + ")";
             }
        }
        // skladanie where-podmienky
        if (queryAppWhere != null && !queryAppWhere.equals("")) {
           if (!sWhr.isEmpty()) {
              sWhr = sWhr + " and ";
           }
            sWhr = sWhr + " (" + queryAppWhere + ")";
        }
        if (queryUsrWhere != null && !queryUsrWhere.equals("")) {
            sWhr = ((sWhr.equals("")) ? "" : "and") + "(" + queryUsrWhere + ")";
        }
        
       // skladanie order by klauzuly
        if (queryAppOrderBy != null && !queryAppOrderBy.equals("")) {
            sOrd = "(" + queryAppOrderBy + ")";
        }
        if (queryUsrOrderBy != null && !queryUsrOrderBy.equals("")) {
            sOrd = ((sOrd.equals("")) ? "" : ",") + queryUsrOrderBy;
        }
        queryPrevStatement = queryFullStatement; //odlozenie predosleho dotazu
        queryFullStatement = sQry 
                           + ((sWhr.equals("")) ? " " : " where " + sWhr)
                           + ((sOrd.equals("")) ? " " : " order by " + sOrd
                           + "limit 30 offset 0");
        krn.debugOut(this,5, "queryFullStatement = " + queryFullStatement);
      return "";
    } // String rebuildQuery()
  
    public String openQuery() {
        /*
        try {
            //rs.
            //qry = em.createQuery(queryFullStatement);
            conn.setAutoCommit(false);
            st = conn.createStatement();
            st.setFetchSize(numFetchedRows);
            rst = st.executeQuery(queryFullStatement);
 //           rst = st.
         } catch (SQLException ex) {
            Logger.getLogger(EOC_query.class.getName()).log(Level.SEVERE, null, ex);
        }
*/        
        try {
            MyCn.getConn().setAutoCommit(false);
            /*#############################################
            /*#############################################
             * A: -> PRIKLAD, KTORY NEFUNGUJE
            ######## END A: -> ############################*/
            pstm = MyCn.getConn().prepareStatement(queryFullStatement, 
                  ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE) ;
           //          prepareStatement(queryFullStatement);
            pstm.setFetchSize(numFetchedRows);
            //stm.setFetchDirection(pstm. );
            if (bIsQryOpened) {rset.close();}  // zatvorenie predosleho dotazu
            
            rset = pstm.executeQuery(); // otvorenie dotazu
           
           /*#############################################
            * * B: -> PRIKLAD, KTORY FUNGUJE V MySQL nefunguje dobre v postgres
            * vyzera to tak, ze fORWARD_ONLY rsultset MySQL nevadi (GetFirst,GetPrev)
            * ale vadi postgresu
            * stc = conn.createStatement() ;
           //          prepareStatement(queryFullStatement);
            stc.setFetchSize(numFetchedRows);
            //stm.setFetchDirection(pstm. );
            rst = stc.executeQuery(queryFullStatement); // otvorenie dotazu
          // rst.setFetchDirection(ResultSet.TYPE_SCROLL_SENSITIVE);
             ############################################*/
            rset.setFetchSize(numFetchedRows);
            //krn.OutPrintln("LIST: " + qry.getResultList());
           //QQQ docasne odstaveny goToFirst();                 // skok na prvy riadok
            krn.debugOut(this, 5, "OpenQuery()-> " + queryFullStatement);
           rset.beforeFirst();
            rset.next();
            if (rset.getRow()==0) {   // dotaz je prazdny 
               bIsQryEmpty = true;
               closeQuery();
               EOC_message msg = new EOC_message(this,"noRowAvailable","", "");
               krn.krn_sendMessage(this,msg,"navigation", "source");
////               krn.krn_sendMessage((Object) this, (Object) this,"noRowAvailable","","navigation","source","");
               //EOC_message msg2 = new EOC_message(this,"noRowAvailable","", "");
               krn.krn_sendMessage(this,msg,"row", "target");
////               krn.krn_sendMessage((Object) this, (Object) this,"noRowAvailable","","row", "target","");
            }
            else { bIsQryEmpty = false;
                   bIsQryOpened=true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

     public String closeQuery() {
        krn.debugOut(this, 5,"CloseQuery()->bIsQryOpened=" + bIsQryOpened);
        if (!bIsQryOpened) {return "";}
        tableCaching=true;
        DefaultTableModel tblModel = (DefaultTableModel) myTable.getModel();
        /*
        tblModel.getDataVector().removeAllElements();
        tblModel.setNumRows(0);
        */
        //ODSKUSAT
        //tblemodel.getDataVector().removeAllElements();
        //tablemodel.fireTableDataChanged();
        for (int i = tblModel.getRowCount() - 1; i >= 0; i--) {
           tblModel.removeRow(i);
        }
        tableCaching   = false;
        numCachedChunks = 0; // uz nacitane davky riadkov z RS
        firstTblRow = 0;     // prvy riadok v tabulky (vzdy nula !)
        lastTblRow = 0;      // v principe sa rovna ..
                             // .. numCachedChunks * numFetchedRows
        firstRSRow = 0;      // row z RS na 1. riadku tabulky
        lastRSRow = 0;       // row z RS na posl. riadku tabulky
        try {
           rset.close();
           krn.OutPrintln("CloseQuery()->result set been closed.");
        } catch (SQLException ex) {
          Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
        bIsQryOpened=false;
        bIsQryEmpty=false;
        myTable.setModel((TableModel) tblModel);
        revalidate();       
        EOC_message msg = new EOC_message(this,"noRowAvailable","", "");
        krn.krn_sendMessage(this,msg,"navigation", "source");
////        krn.krn_sendMessage((Object) this, (Object) this, "noRowAvailable", "", "navigation", "source", "");
        return "";
     }  // public String closeQuery()
     
     public String setMasterTable(String sMstTblName) {
       sMasterTbl = sMstTblName;
       return "";
    }
    
     public String setMasterKey(String sMstKeyName, String sMstKeyDType) {
      // QQQ - sem patri test platnych hodnot pre parameter, zatial ignorujem
        this.sMasterKeyDataType = sMstKeyDType;
        sMasterKey = sMstKeyName;
        return "";
    }

     @Override
    public String receiveMessage(EOC_message eocMsg) {

        // musi byt ako prva instrukcia v metode !!!
        if (eocMsg==null) return FnEaS.nullEocMessageResponse(myObjectID);
        
        /* Volanie metody v EOC_NObject, ktora pusti metodu sMessage pokial je to
      * existujuca metoda, a sMessagema prefix RUN
      */
        String supRecMsg = super.receiveMessage(eocMsg);
        // sprava bola spracovana rodicom
        if (supRecMsg.equals("")) {
           krn.debugOut(this,0,"SUPER_invoked in DBtableOld, and his return empty string (OK)");
           return "";
        }
        // sprava nebola spracovana rodicom
        krn.debugOut(this,5,"SUPER_invoked in DBtableOld, and his return > " + supRecMsg);
//        Class params[] = new Class[];
        try {
              // params[1]. = sParameters;
              // testovat bude treba podla obsahu sParameters
              krn.debugOut(this,5,"receiveMessage  in DBtableOld=>" + eocMsg.getMessage());
              Method mtd;
              try {
              mtd = this.getClass().getMethod(eocMsg.getMessage(), new Class[]  {String.class});
              } catch (NoSuchMethodException ex) {
                  mtd = null;
              }
              if (mtd == null) {
                    krn.debugOut(this, 5,"mtd1 is null");
                    try {
                          mtd = this.getClass().getDeclaredMethod(eocMsg.getMessage(),  
                                                        new Class[]  {String.class});
                    } catch (NoSuchMethodException ex) {
                        krn.debugOut(this,5,"xxAA " + ex.toString());
                        mtd = null;
                     } catch (NullPointerException ex) {
                        krn.debugOut(this,5,"xxBB " + ex.toString());
                        mtd = null;
                    }
               }
//              krn.debugOut(this,0,"xxMethod: " + mtd.toString() + " for param " + sParameters);
              //   this.goToFirst();
              // vypise: Method: public java.lang.String eoc.EOC_query.goToFirst()
              try {
                   krn.debugOut(this,4,"invokeA invoking: " + mtd.toString());
                    Object rv =  mtd.invoke(this, eocMsg.getParameters());
                    krn.debugOut(this,4,"invokeB");
                    return rv.toString();
              } catch (IllegalAccessException ex) {
                   krn.debugOut(this,0,"xx " + ex.toString());
              } catch (IllegalArgumentException ex) {
                   krn.debugOut(this,0,"xx " + ex.toString());
              } catch (InvocationTargetException ex) {
                   krn.debugOut(this,0,"xx " + ex.toString());
              }
        } catch (SecurityException ex) {
             //   Logger.getLogger(EOC_NObject.class.getName()).log(Level.SEVERE, null, ex);
             return "NOTRECEIVED-NObject=SecurityExceptionA";
        }
        krn.debugOut(this,0, "EOC_query-receiveMessage-b" + this.getClass().getName() 
                            + " supRecMsg=" + supRecMsg);
      
        return supRecMsg;
    }

     public String rowCreated(String sParam) {
                openQuery() ;
        try {
            rset.last();
           // sprava o dosiahnuti posledneho riadku   
            if (rset.isLast()) {
               EOC_message msg = new EOC_message(this,"lastRow","", "");
               krn.krn_sendMessage(this,msg,"navigation", "source");
////            krn.krn_sendMessage((Object) this, (Object) this, "lastRow","",
////                                              "navigation", "source", "");
            }  else {
               EOC_message msg = new EOC_message(this,"notFirstOrLast","", "");
               krn.krn_sendMessage(this,msg,"navigation", "source");
 ////              krn.krn_sendMessage((Object) this, (Object) this, "notFirstOrLast","",
 ////                                             "navigation", "source", "");
            }    

        } catch (SQLException ex) {
            Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
        }
            oldCurrentTblId = currentTblId; // zistenie noveho hlavneho ID-u
            currentTblId = Long.valueOf(sParam);
            // sprava zmeny row-u svojim row-cielom
            if (oldCurrentTblId != currentTblId) {
              EOC_message msg = new EOC_message(this,"goToRow","", "");
              krn.krn_sendMessage(this,msg,"row", "target");
////              krn.krn_sendMessage((Object) this, (Object) this, "goToRow" 
////                      , currentTblId.toString(), "row", "target", "");
            } // if (oldCurrentTblId != currentTblId)

            return "";

        /* OLD CODE
        openQuery() ;
        try {
            rst.last();
           // sprava o dosiahnuti posledneho riadku   
            if (rst.isLast()) {
            krn.krn_sendMessage((Object) this, "lastRow","",
                                              "navigation", "source", "");
            }  else {
               krn.krn_sendMessage((Object) this, "notFirstOrLast","",
                                              "navigation", "source", "");
            }    

        } catch (SQLException ex) {
            Logger.getLogger(EOC_query.class.getName()).log(Level.SEVERE, null, ex);
        }
            oldCurrentTblId = currentTblId; // zistenie noveho hlavneho ID-u
            currentTblId = Long.valueOf(sParam);
            // sprava zmeny row-u svojim row-cielom
            if (oldCurrentTblId != currentTblId) {
              krn.krn_sendMessage((Object) this, "goToRow" 
                      , currentTblId.toString(), "row", "target", "");
            } // if (oldCurrentTblId != currentTblId)

            return "";
            * */
    }
    /**
     * Creates new form DBtable
     */
    public DBtable() {
        owRowSource = new Object();
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    public String rowUpdated (String sParam) {
      try {
          krn.OutPrintln("DBtable->rowUpdated (String sParam)");
          krn.OutPrintln("1 rset.getRow()=" + rset.getRow() + " currentTblId = " + currentTblId);
          rset.getRow();
    //     rset. .refreshRow();
         krn.OutPrintln("1 rset.getRow()=" + rset.getRow());
         rset.getRow();
       } catch (SQLException ex) {
         Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
      }
              Object[] rowData = new Object[numDisplayedColumns + 1];
              for (int i = 0; i < numDisplayedColumns; i++) {
          try {
            // krn.OutPrintln("1DBtable->rowUpdated->rowData["+ i + "]");
             rowData[i] = rset.getObject(i + 1);
             //krn.OutPrintln("22DBtable->rowUpdated->rowData["+ i + "]=" + rowData[i]);
          } catch (SQLException ex) {
             Logger.getLogger(DBtable.class.getName()).log(Level.SEVERE, null, ex);
          }
              }
              // novy riadok na 'fokusovanie' je prvy riadok z pridanych
       DefaultTableModel tblModel = (DefaultTableModel)myTable.getModel();
           for (int ii = 0; ii < numDisplayedColumns; ii++) {
              String colname = tblModel.getColumnName(ii);
              krn.OutPrintln("22DBtable->rowUpdated->colname = " + colname);
              tblModel.setValueAt(getColVal(colname), currentRow,ii);
           }
//              tblModel. addRow(rowData);

       // krn.krnMsg(this,"e","Row Updated " + getColVal("c_group"),"");
       return "";
    }
    
        @Override
    public String getEOC_objectType() {
           return sEOCobjectType;
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

      getAccessibleContext().setAccessibleName("DBT_jpanel");
   }// </editor-fold>//GEN-END:initComponents
   // Variables declaration - do not modify//GEN-BEGIN:variables
   // End of variables declaration//GEN-END:variables

}
