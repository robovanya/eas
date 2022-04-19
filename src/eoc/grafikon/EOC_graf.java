/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.grafikon;

import eoc.database.DBconnection;
import system.Kernel;
import system.FnEaS;
import eoc.dbdata.DBtableColumn;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rvanya
 */
public class EOC_graf implements IEOC_graf_obj {
    private final Kernel krn;
    private final DBconnection MyCn;
    private final String cGrafName;
    private final String cMyLabel;
    private final String cMyToolTip;
    private DBtableColumn dbtcColumns[];
    private Object oo_RowData[][];
    private String sPrimaryDbTable;
    private String sPrimaryDbKey;
    private DBtableColumn dbtcPrimaryKey;
    private boolean bHiddenPrimaryKey;
    private EOC_BrwGrafikon  myParentBrowser;
    private EOC_graf         myParentGraf;
    private final EOC_GrafTable    myTable;
    private JScrollPane             myScrollPane;
    private int oldTblWidth  = 0;
    private int currTblWidth = 0;
    private boolean bInitialized = false;
    private boolean bRefreshed;
    boolean isDimension; // odvodene objekty maju premennu vidiet
    // tblData a tblColumns obsahuju stlpce idx a bFake 
    // idx je interny index pre triedenie dimenzii na zaklade oPrimaryKey
    // bFake je true, pokial sa jedna o prazdny riadok dimenzie
    // v pripade udajov hlaviciek riadkov je bFake vzdy false ! 
    ArrayList<Object[]> alTblData;    // rozsirene pole o idx a bFake hodnoty 
    ArrayList<DBtableColumn>   alTblColumns; // rozsirene pole o idx a bFake stlpce
    ArrayList<EOC_Gdimension> dimensionList; // pole dimenzii akt. graf-objektu
    private JComboBox<String> jcbDimensions;
    EOC_Gdimension currentDim;
    private boolean hideIdxColumns = false;
    private boolean hideMasterKey  = false;
    boolean bRendering = false;
    ArrayList<Object[]> alIdxId;       // pole indexov internej tabulky
    ArrayList<Object[]> alParentIdxId; // pole indexov internej tabulky 
                                       // nadradeneho uzla
    String[] mandatoryColumnNames = new String[4]; // "idx" "bFake" "currIdx" sPrimaryKey
    DBtableColumn[] mandatoryColumns = new DBtableColumn[4];
    boolean bLivingCells = false;

    // konstruktor
    public EOC_graf(Kernel kr, DBconnection cn, String cGrfName,
                    String cGrfLabel,String cGrfTooltip,
                    boolean isDim, EOC_graf parentGrf,
                    DBtableColumn[] oRowHeaders,Object ooRwData[][],
                    String sPrimaryTableName,
                    String sPrimaryKeyFieldName, boolean bHiddenKey) {
        krn = kr;
        MyCn = cn;
        cGrafName         = cGrfName;
        cMyLabel          = cGrfLabel;
        cMyToolTip        = cGrfTooltip;
        isDimension       = isDim;
        sPrimaryDbTable   = sPrimaryTableName;
        sPrimaryDbKey     = sPrimaryKeyFieldName;
        bHiddenPrimaryKey = bHiddenKey;
        mandatoryColumnNames[0] = "idx";
        mandatoryColumnNames[1] = "bFake";
        mandatoryColumnNames[2] = "currIdx";
        mandatoryColumnNames[3] = sPrimaryDbKey;
        
        // vytvorenie tabulkoveho objektu grafikonu
        myTable = new EOC_GrafTable(this,isDimension, kr);
        if (isDimension) {
            // krn.OutPrintln("CREATED NEW DIMENSION: " + cGrfName);
            myTable.setDefaultRenderer(String.class, new DimensionCellRenderer());
            myParentGraf = parentGrf;
        }
        else {
            myTable.setDefaultRenderer(String.class, new HeaderCellRenderer());
           // krn.OutPrintln("\nCREATED NEW GRAFICON: " + cGrfName);
        }
            myTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
                public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.INSERT) {
                        int iInsRow = myTable.getModel().getRowCount() - 1;
                        Object[] oIdx = new Object[2];
                        oIdx[0] = myTable.getModel().getValueAt(iInsRow, 0);
                        oIdx[1] = myTable.getModel().getValueAt(iInsRow, 3); 
//                        krn.OutPrintln("inSRT: " + oIdx[0] + " -=- " + oIdx[1]);
                        alIdxId.add(oIdx);
                    }
                }
            }); 
                    
        alTblData     = new ArrayList<>();
        alTblColumns  = new ArrayList<>();
        dimensionList = new ArrayList<>(); // pole dimenzii
        alIdxId       = new ArrayList<>(); // pole idx->id poli int. tabulky
        alParentIdxId = new ArrayList<>(); // pole idx->id poli nadradeneho uzla
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        myTable.setAutoCreateColumnsFromModel(false);
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setTblColumns(oRowHeaders,sPrimaryDbKey, bHiddenPrimaryKey); 
        setRowData(ooRwData);
    } // constructor
    
    public DBtableColumn getPrimaryKeyColumn() {
         return dbtcPrimaryKey;
    }
    
    public void setParentBrowser(EOC_BrwGrafikon brwGraf) {
        myParentBrowser   = brwGraf;
    }
    
    public EOC_BrwGrafikon getParentBrowser() {
        return myParentBrowser;
    }

    public void setDimensionComboBox(JComboBox<String> jcbDims) {
        jcbDimensions = jcbDims;
    }
    
    public void initialize( /* EaS_krn kr, Connection cn */) {
        
        if (bInitialized) return; // inicializaciu mozu vyzadovat rozne objekty
        
        refreshTable(true /* mustRefresh */ );
        /*
        if (!isDimension) { // vytvorenie troch pokusnych dimenzii
            for (int i = 1; i < 150; i++)
            createFakeDimension(i);
        }
        */
        ////krn.OutPrintln("Initializing: " + cGrafName + " ... DONE");
        bInitialized = true;
    } // initialize()

    @Override
    public void setRendering (boolean renderingState, String propagateMode,
            Object caller, boolean ignoreCaller) {
        bRendering = renderingState;
        //krn.OutPrintln(getcGrafName() + " setRendering: " + bRendering);
        //myTable.setRendering(truFe);
    /* - toto tu je dost diskutabilne -
         * v buducnosti budu moct byt dimenzie zaroven uzlami (node-object)
         * co bude znamenat, ze sa budu spravat aj ako graf-objekty,
         * ale iba pre objekty na konaroch podriadenych stromov
         ***************************************************************/
        if (!isDimension) { // je to graf-objekt
            if (propagateMode.equals("UP") || propagateMode.equals("ALL")) {
                // myParentBrowser je nadradeny objekt
                if (myParentBrowser != null) { 
                    myParentBrowser.setRendering(renderingState, "UP",this, true);
                }
            }
            if (propagateMode.equals("DOWN") || propagateMode.equals("ALL")) {
                // spracovanie podriadenych dimenzii
                for (EOC_Gdimension dim : dimensionList) {
                    dim.setRendering(renderingState,"DOWN",this, true);
                }
            }
        }    
        else { // objekt dimenzie
            if (propagateMode.equals("UP") || propagateMode.equals("ALL")) {
                // tu je diskutabilne, ci sa ma odovzdavat hodnota
                // premennej propagateMode aleo hodnota "UP"
                myParentGraf.setRendering(renderingState, propagateMode,
                        this, true);
            }
            // tu sa odovzda v boducnosti hodnota premennej propagateMode
            // objektom na konaroch podriadenych stromov (24.1.2015)
            if (propagateMode.equals("DOWN")) {
                
            }
        }
    } // setRendering (boolean renderingState)
    
    /*
     * Vytvorenie columnModelu s potrebnymi stlpcami
     * a tableModelu, s potrebnymi riadkami
     ***************************************************************/
    private void refreshTable(boolean mustRefresh) {
////krn.OutPrintln("AAA");
        // mazanie stlpcov z ColumnModel-u
        while (myTable.getColumnModel().getColumnCount() > 0) {
            myTable.getColumnModel().removeColumn(
            myTable.getColumnModel().getColumn(0));
        }
/////krn.OutPrintln("BBB");
        // mazanie riadkov a stlpcov z tabulky (z TableModel-u)
        DefaultTableModel dtm = (DefaultTableModel) myTable.getModel();
        dtm.setRowCount(0);
        dtm.setColumnCount(0);

        oldTblWidth = myTable.getWidth();
        currTblWidth = 0; // scitava sa to nizsie
        // vytvorenie objektov-stlpcov do ColumnModelu a TableModelu
/////        krn.OutPrintln("alTblColumns.size()==A=" + alTblColumns.size());
        for (int i = 0; i < alTblColumns.size(); i++) {
            DBtableColumn t = alTblColumns.get(i); // iba kvoli prehladnosti
           //// krn.OutPrintln("TOTABLECOLUMN: " + alTblColumns.get(i).getdbFieldName() 
           ////         + " -- " + alTblColumns.get(i).getFieldLabel()
           ////         + " obj: " + alTblColumns.get(i).toString());
            if (hideIdxColumns && (i < 3) ) { // schovanie pomocnych stlpcov
                t.setMinWidth(0);
                t.setMaxWidth(0);
                t.setWidth(0);
                t.setPreferredWidth(0);
            }
            t.setModelIndex(i);
            myTable.getColumnModel().addColumn(t);
            ((DefaultTableModel) myTable.getModel()).addColumn(t);
            currTblWidth = currTblWidth + alTblColumns.get(i).getWidth();
        }
        // pridanie riadkov hlaviciek do tableModelu
        for (Object[] oOneRow : alTblData) {
            ((DefaultTableModel) myTable.getModel()).insertRow(
             ((DefaultTableModel) myTable.getModel()).getRowCount(), oOneRow);
        }
        for (int i = 0; i < myTable.getColumnModel().getColumnCount(); i++) {
           FnEaS.packColumn(myTable, i, 5);
        }
        myTable.repaint();
        //Kernel.Msg("AFTERREFRESH--" + cGrafName);
    }

public void hideIdxColumns(boolean bHide) {
        hideIdxColumns = bHide;
        for (int i = 0; i < 3; i++) {
            DBtableColumn t = alTblColumns.get(i); // iba kvoli prehladnosti
           //// krn.OutPrintln("TOTABLECOLUMN: " + alTblColumns.get(i).getdbFieldName() 
           ////         + " -- " + alTblColumns.get(i).getFieldLabel()
           ////         + " obj: " + alTblColumns.get(i).toString());
            if (hideIdxColumns) { // schovanie pomocnych stlpcov
                t.setMinWidth(0);
                t.setMaxWidth(0);
                t.setWidth(0);
                t.setPreferredWidth(0);
            }
            else {
                t.setMinWidth(50);
                t.setMaxWidth(100);
                t.setWidth(80);
                t.setPreferredWidth(80);
            }
        }
        for (EOC_Gdimension d: dimensionList) {
            d.hideIdxColumns(hideIdxColumns);
        }
}

public void hideMasterKey(boolean bHide) {
        hideMasterKey = bHide;
            if (hideMasterKey) { // schovanie pomocnych stlpcov
                dbtcPrimaryKey.setMinWidth(0);
                dbtcPrimaryKey.setMaxWidth(0);
                dbtcPrimaryKey.setWidth(0);
                dbtcPrimaryKey.setPreferredWidth(0);
            }
            else {
                dbtcPrimaryKey.setMinWidth(50);
                dbtcPrimaryKey.setMaxWidth(100);
                dbtcPrimaryKey.setWidth(80);
                dbtcPrimaryKey.setPreferredWidth(80);
            }
        for (EOC_Gdimension d: dimensionList) {
            d.hideMasterKey(hideMasterKey);
        }
}

    public EOC_GrafTable getTable() {
         return myTable;
    }
    // pridanie modelov do tabulky
    public void fillTables() {
        if (!isDimension)
            myParentBrowser.getRowHeaderViewport()
                           .setSize(currTblWidth, myTable.getHeight() );

        if (!isDimension) {
            myParentBrowser.getRowHeaderViewport().setView(myTable);
            myParentBrowser.getRowHeaderViewport().setPreferredSize(myTable.getPreferredSize());
            myParentBrowser.getDimScrollPane().setRowHeaderView(
                myParentBrowser.getRowHeaderViewport());
            myParentBrowser.getDimScrollPane().setCorner(
                JScrollPane.UPPER_LEFT_CORNER,myTable.getTableHeader());
            myParentBrowser.revalidate();
            myParentBrowser.repaint();
        }        
        myTable.revalidate();
        myTable.repaint();
    }

    public boolean getIsDimension() {
        return isDimension;
    }
    
    public String getcGrafName() {
        return cGrafName;
    }

    public String getcGrafLabel() {
        return cMyLabel;
    }

    public String getcGrafToolTip() {
        return cMyToolTip;
    }

    /**
     * blabla pre getcolumns metodu
     * 
     * @return vrátí pole objektov 
     */
    public DBtableColumn[] getColumns() {
        return dbtcColumns;
    }

    private boolean setTblColumns(DBtableColumn[] dbtcColumns, 
                                  String sPrimKey, boolean bHiddenKey) {

        // test existencie kluca medzi stlpcami hlavicky, a pridanie hodnot.
        // hlada sa podla nazvu kluca
        boolean isOK = false;
        for (int i = 0; i < dbtcColumns.length; i++) {
            ////krn.OutPrintln("dbtcColumns[i].getdbFieldName()" + dbtcColumns[i].getdbFieldName() + " ?? " + sPrimaryKey);
            if (dbtcColumns[i].getdbFieldName().equalsIgnoreCase(sPrimKey)) {
                isOK = true;
                this.dbtcColumns       = dbtcColumns;
                this.sPrimaryDbKey     = sPrimKey;
                this.dbtcPrimaryKey    = dbtcColumns[i]; 
                this.bHiddenPrimaryKey = bHiddenKey;
                dbtcColumns[i].setHidden(bHiddenPrimaryKey);
                break;
            }              
        }
        for (int i = 0; i < dbtcColumns.length; i++) {
//            krn.OutPrintln("Graf-setTblColumns()--DDdbtcColumns[" + i + "].getdbFieldName()==" + dbtcColumns[i].getdbFieldName());
        }
        if (isOK) {
            
            // pridanie dalsich potrebnych stalych stlpcov
            mandatoryColumns[0] = new DBtableColumn(krn,MyCn,
                "^^idx^integer^null^idx^7^0^#######^Pôvodný index riadku");
            mandatoryColumns[1] = new DBtableColumn(krn,MyCn,
                "^^bFake^boolean^null^bFake^15^0^#######^Určuje, či je aktuálny riadok neexistujúci");
            mandatoryColumns[2] = new DBtableColumn(krn,MyCn,
                "^^currIdx^integer^null^currIdx^30^0^#######^Index riadku podľa aktuálneho triedenia");
            // primarnyKluc sa prilozi, ako POSLEDNY medzi stalych stlpcov
            mandatoryColumns[3] = dbtcPrimaryKey;  
            
            alTblColumns.clear(); // Mazanie pripadnych existujucich udajov
            alTblColumns.addAll(Arrays.asList(mandatoryColumns));
            for (DBtableColumn dbtcColumn : dbtcColumns) {
                if (dbtcColumn != dbtcPrimaryKey) {
                    alTblColumns.add(dbtcColumn);
                }
            } 
        }
        else Kernel.staticMsg("Graf: " + cGrafName + "Primárny klúč " +
                         sPrimKey + " chýba z pola stľpcov: ");
        return isOK;
    }

    
    private boolean setRowData(Object[][] ooRowValues) {
        if (ooRowValues == null) return false;
        oo_RowData = ooRowValues; // ulozenie hodnoty parametra
                                 // pre pripad neskorsieho pouzitia
    
        // pridanie novych riadkov so stlpcami idx, bFake 
        // + stlpcami z parametra ooRowValues
        // citanie jednotlivych riadkov
        //// krn.OutPrintln("alcoolums-size==" + alTblColumns.size());

        for (int y=0; y < ooRowValues.length; y++) {
           Object[] oneBaseRow;
           oneBaseRow = ooRowValues[y];
           Object[] oHeaderRowValues = new Object[alTblColumns.size()];
           // pridanie internych pomocnych stlpcov 
           oHeaderRowValues[0] = new Integer(y); // idx
           oHeaderRowValues[1] = isDimension; // bFake
           oHeaderRowValues[2] = new Integer(y); // curIdx
        ///// if (isDimension) krn.OutPrintln("onebaseroww==" + oneBaseRow.length + " > " + Arrays.deepToString(oneBaseRow));
           for (int i = 0; i < oneBaseRow.length; i++) {
               oHeaderRowValues[i + 3] = oneBaseRow[i];
           }
       /////  if (isDimension) krn.OutPrintln("oHeaderRowValues==" + oHeaderRowValues.length + " > " + Arrays.deepToString(oHeaderRowValues));
           alTblData.add(oHeaderRowValues);
       }
       ////  if (isDimension) krn.OutPrintln("graf-setoRowData-fiiiiniiiito!");
        return true;
    }

    /**
     * @return the oPrimaryDbKey
     */
    public Object getsPrimaryDbKey() {
        return sPrimaryDbKey;
    }

    /**
     * @param oPrimaryKey the oPrimaryDbKey to set
     */
    public void setsPrimaryDbKey(String oPrimaryKey) {
        this.sPrimaryDbKey = oPrimaryKey;
    }
    
    /**
     * @return the oPrimaryDbTable
     */
    public Object getsPrimaryDbTable() {
        return sPrimaryDbTable;
    }

    /**
     * @param oPrimaryDbTable the oPrimaryDbTable to set
     */
    public void setsPrimaryKey(String oPrimaryDbTable) {
        this.sPrimaryDbTable = oPrimaryDbTable;
    }
    
/*    
  public void setGrafCombo (JComboBox cbg) {
      jcbGrafikons = cbg;
  }
*/
    
  public void setDimensionCombo (JComboBox<String> cbd) {
      jcbDimensions = cbd;
  }
  
   public boolean addDimension(EOC_Gdimension dim) {
      //QQQ sem patri test, ci cPrimaryKey je sucastou oRowHeaders
      //QQQ sem patri test, ci cGrafName uz neexistuje v ArrayListe grafList
      if (dim.getsPrimaryDbTable() != sPrimaryDbTable) {
          Kernel.staticMsg("addDimension() " + cGrafName + " " + cMyLabel + " >> "
          + " reqired primary dbTable: " + dim.getsPrimaryDbTable() + " <> " + sPrimaryDbTable);
          return false;
      }
      if (dim.getsPrimaryDbKey() != sPrimaryDbKey) {
          Kernel.staticMsg("addDimension() " + cGrafName + " " + cMyLabel + " >> "
          + " reqired primary key: " + dim.getsPrimaryDbKey() + " <> " + sPrimaryDbKey);
          return false;
      }
      dimensionList.add(dim);
      // krn.OutPrintln("dimensionList.add(dim).size()==" + dimensionList.size());
      dim.setDimensionCombo(jcbDimensions);
      dim.setParentBrowser(myParentBrowser);
      return true; 
   }
    
  public boolean addDimension(String cGrafName,String cGrafLabel,String cGrafTooltip, 
                         DBtableColumn oDimHeaders[],Object ooDimData[][],
                         String sPrimKey, boolean bHiddenKey) {
      //QQQ sem patri test, ci cPrimaryKey je sucastou oRowHeaders
      //QQQ sem patri test, ci cGrafName uz neexistuje v ArrayListe grafList
      if (sPrimKey != sPrimaryDbKey) {
          Kernel.staticMsg("addDimension() " + cGrafName + " " + cGrafLabel + " >> "
          + " reqired primary key: " + sPrimKey + " <> " + sPrimaryDbKey);
          return false;
      }
      boolean retVal = false;
      EOC_Gdimension dim = 
          new EOC_Gdimension(krn, MyCn, cGrafName,cGrafLabel,cGrafTooltip,
                             true /*isDimension */,
                             /*myParentBrowser,*/ (EOC_graf) this, 
                             oDimHeaders, ooDimData, sPrimaryDbTable,
                             sPrimaryDbKey, bHiddenKey /*, jcbDimensions*/);
      dim.setParentBrowser(myParentBrowser);
      dimensionList.add(dim);
      return retVal;
  }

  public ArrayList<EOC_Gdimension> getDimensionList() {
      return dimensionList;
  }
  
  private void refresh() {
       // NEMAZAT!  Bude pouzite pre funkcionality refreshovania udajov
      // currentGraf.refresh();
      // sem patri refreshovanie dimenzii z dimensionList-u
     
  }

/* Vytvori prázdnu dimenziu.
 * Objekty gafikónu používajú prázdnu dimenziu na úpravu fumkcionality obrazovky
 * alebo pri vytváraní novej reálnej/platnej dimenzie
 *********************************************************************/
  public EOC_Gdimension createFakeDimension(int dimNum, String fakeLabel) {
      DBtableColumn[] fakeDimColumns;
      Object[][] fakeDimData;
      
//      dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
//      numdecimals^formatstring^tooltyp^HIDDEN
      // vytvorenie pola stlpcov
      fakeDimColumns = new DBtableColumn[2];
      //oPrimaryKey vsade prerobit na String !!, co je fieldName stlpca
      fakeDimColumns[0] = //sPrimaryKey;
            new DBtableColumn(krn,MyCn,
                "^^id^integer^null^id^7^0^#######^Pôvodný index riadku");
      fakeDimColumns[1] = 
            new DBtableColumn(krn,MyCn,
                "^^nazov^char^null^nazov^50^0^#######^" +
                "< " + cGrafName + " - Prázdna dimenzia >");
     // fakeDimColumns[1].
      fakeDimData = new Object[alTblData.size()][2];
      int i = -1; // counter
      for (Object[] o: alTblData) {
          i++; // increase counter
         fakeDimData[i][0] = null;  // primarny kluc - fakeDim nema primarny kluc
         fakeDimData[i][1] = o[3].toString() + " is ID FOR <fakeDim " + dimNum 
                           + " row " + i + " for graf "  
                           + cGrafName + " -- " + fakeLabel + " >"; // prazdna hodnota 
      }
      //QQkrn1 krn.OutPrintln("Creating FAKEDIM number " + dimNum + " begin");
      EOC_Gdimension dim = new EOC_Gdimension(krn, MyCn, 
          "emptyDim " + dimNum,"Prázdna dimenzia " + dimNum + " pre grafikón "
              + cGrafName ,"Objekty gafikónu používajú prázdnu"
          + " dimenziu na úpravu fumkcionality obrazovky",
          true /* isDimension */,
          /*myParentBrowser,*/ (EOC_graf) this,
          fakeDimColumns, fakeDimData, sPrimaryDbTable, sPrimaryDbKey, 
          bHiddenPrimaryKey /* , jcbDimensions */ );
      
      return dim;
      ////krn.OutPrintln("Creating FAKE-DIMMENSION number " + dimNum + " bgin PK=" + sPrimaryKey);
      ////dimensionList.add(dim);
      ////dim.initialize( /* krn, MyCn */ ); 
      ////krn.OutPrintln("Creating FAKE-DIMMENSION number " + dimNum + " - finished");
      //QQkrn1krn.OutPrintln("Creating FAKE-DIMMENSION number " + dimNum + "  with " 
      //                   + fakeDimData.length + " rows - end");
  }

  public static int getDayNumOfWeek(Date dt) {
      SimpleDateFormat format=new SimpleDateFormat("yyyy-M-dd");
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      return cal.get(Calendar.DAY_OF_WEEK);
      
  }

  public static String getDayNameOfWeek(Date dt, int trimTo) {
      String wd[] = {"nedeľa","pondelok","utorok","streda","štvrtok",
                     "piatok","sobota"};
      SimpleDateFormat format=new SimpleDateFormat("yyyy-M-dd");
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      String day = wd[cal.get(Calendar.DAY_OF_WEEK) - 1];
      if (trimTo > 0) {
          if (day.length() > trimTo)
          day = day.substring(0,trimTo);
      }
      return day;
  }
/**
 * Vrátí ArrayList polí objektov v tvare o[0]=idx, o[1]=id
 * @return 
 */
  public ArrayList<Object[]> getIdxIdRelations () {
      /* zastaraly, nedokonceny kod, alIdxId udrzuje TableModelListener objekt tabulky
      ArrayList<Object[]> idxId = new ArrayList<>();
      DefaultTableModel tbm = (DefaultTableModel) myTable.getModel();  // kvoli prehladnosti
      for (int i = 0; i < tbm.getRowCount(); i++) {
          Object[] oIdx = new Object[2];
          oIdx[0] = tbm.getValueAt(i, 0);
          oIdx[1] = tbm.getValueAt(i, 3); 
          krn.OutPrintln(oIdx[0] + " -- " + oIdx[1]);
      }
      return idxId;
      */
      return alIdxId;
  }   
  // rozsirene pole o idx a bFake hodnoty 

  
  /**
   * Vytvorí podfuk-riadky pre položky nadradeného uzla, pokial neexistuje
   * reálna položka, spojená s nadradenou položkou cez identifikátor.
   * Objekt musí mať definovanú nadradený uzol (myParentGraf)
   */
  public void createFakeRows() {
      
      if (myParentGraf == null) {
          Kernel.staticMsg("Dimenzia " + cGrafName + " nemá definovaný nadradený uzol."
                      + "Fake-riadky sa nedajú vytvoriť !");
          return;
      }
      // ziskane platnych idx-klucov s prisluchajucimi masterKey-mi 
      // od nadradeneho uzla
      alParentIdxId = myParentGraf.getIdxIdRelations();
      krn.OutPrintln("aaa_alTblData.size()===" + alTblData.size());
      // ciatanie dat z pola klucov nadradeneho uzla
//      DefaultTableModel tbm = (DefaultTableModel) myTable.getModel()).setRowCount(0);
      DefaultTableModel tbm = (DefaultTableModel) myTable.getModel();
      // poskladanie predlohy riadku
      Object[] oRow = new Object[tbm.getColumnCount()];
      krn.OutPrintln("tbm.getColumnCount()===" + tbm.getColumnCount());
      for (int c = 0; c < tbm.getColumnCount(); c++) {
         //// oRow = tbm.getDataVector().toArray();
          oRow[c] = new String();
      }
            
      fblock: // hlada sa kazdy parent->idx-id kluc
      for (int i = 0; i < alParentIdxId.size(); i++) {
          Object[] o = alParentIdxId.get(i);
          // hlada sa na kazdom riadku internej tabulky
          for (int r = 0; r < tbm.getRowCount(); r++) {
              if (tbm.getValueAt(r, 1) == o[0]) continue fblock; // kluc existuje                 
          }
          oRow[0] = o[0];
          oRow[1] = true;
          tbm.addRow(oRow);
          
      } 
      
      DBtableColumn[] fakeDimColumns;
      Object[][] fakeDimData;
      
//      dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
//      numdecimals^formatstring^tooltyp^HIDDEN
      // vytvorenie pola stlpcov
      fakeDimColumns = new DBtableColumn[2];
      //oPrimaryKey vsade prerobit na String !!, co je fieldName stlpca
      fakeDimColumns[0] = //sPrimaryKey;
            new DBtableColumn(krn,MyCn,
                "^^id^integer^null^id^7^0^#######^Pôvodný index riadku");
      fakeDimColumns[1] = 
            new DBtableColumn(krn,MyCn,
                "^^nazov^char^null^nazov^50^0^#######^" +
                "< " + cGrafName + " - Prázdna dimenzia >");
     // fakeDimColumns[1].
      fakeDimData = new Object[alTblData.size()][2];
      int i = -1; // counter
      /*
      for (Object[] o: alTblData) {
          i++; // increase counter
         fakeDimData[i][0] = null;  // primarny kluc - fakeDim nema primarny kluc
         fakeDimData[i][1] = o[3].toString() + " is ID FOR <fakeDim " + dimNum 
                           + " row " + i + " for graf "  
                           + cGrafName + " -- " + fakeLabel + " >"; // prazdna hodnota 
      }
      */
      //QQkrn1 krn.OutPrintln("Creating FAKEDIM number " + dimNum + " begin");
      //EOC_Gdimension dim = new EOC_Gdimension(krn, MyCn, 
      //    "emptyDim " + dimNum,"Prázdna dimenzia " + dimNum + " pre grafikón "
      //        + cGrafName ,"Objekty gafikónu používajú prázdnu"
      //    + " dimenziu na úpravu fumkcionality obrazovky",
      //    true /* isDimension */,
      //    /*myParentBrowser,*/ (EOC_graf) this,
      //    fakeDimColumns, fakeDimData, sPrimaryKey, bHiddenPrimaryKey
      //    /* , jcbDimensions */ );
  }
  

  /**
   * Vrátí pole reťazcov s názvami dní z mesiaca skúmaného dátumu
   * 
   * @param dt     - dátum zo skúmaného mesiaca
   * @param trimTo - Počet použitých písmen z názvu dňa
   * @return 
   */
  public static String[] getDayNamesOfMonth(Date dt, int trimTo) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
      int dayOfWeek = 0;
      Date myDate = dt;
      String daysOfMonth[] = new String [maxDay];
      for (int day = 1; day <= maxDay; day++) {
          cal.set(Calendar.DAY_OF_MONTH, day);
          myDate = cal.getTime();
          daysOfMonth[day - 1] = getDayNameOfWeek(myDate,2);
      }
      return daysOfMonth;
  }

  /**  
   * Vrátí pole stľpcových objektov pre každý deň v mesiaci.
   * Pokial parameter keyCol nie je NULL, pridá sa na začiatok pola.
   *
   *     Rozumis, Kissúr ?
   * 
   * @param year
   * @param month
   * @param keyCol
   * @return
   * @throws ParseException 
   */
  public eoc.dbdata.DBtableColumn[] 
       getTimeDimensionColumns(int year, int month, eoc.dbdata.DBtableColumn keyCol) 
       throws ParseException {
     /*            new DBtableColumn(krn,MyCn,
                "^^id^integer^null^id^7^0^#######^Pôvodný index riadku");
*/
      SimpleDateFormat format=new SimpleDateFormat("yyyy-M-dd");
      String strDate = year +"-" + month + "-1"; // prvy den v mesiaci
      Date dt = format.parse(strDate);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      int minDay = 0;
      int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // posledny den v mesiaci
      int arrayLength = 0;
      int addedCol = 0;
      if (keyCol != null) {
          arrayLength = 1; // prida sa miesto pre stlec s identifikatorom
          addedCol = 1;
      }
      arrayLength = arrayLength + maxDay;
      eoc.dbdata.DBtableColumn[] dbc = new eoc.dbdata.DBtableColumn[arrayLength];
      if (keyCol != null) dbc[0] = keyCol;  // prida sa miesto pre stlpec s identifikatorom
      String[] dayNames = EOC_graf.getDayNamesOfMonth(dt, 3);
      for (int iDay = minDay; iDay < maxDay; iDay++) {
         String sDay = dayNames[iDay]; 
         DBtableColumn dtc =  new DBtableColumn(krn,MyCn,
                "^^" + sDay + "^integer^null^" + sDay + " (" + (iDay + 1) + ")" + "^7^0^#######^Deń v mesiaci");
         dbc[iDay + addedCol] = dtc;
      }
      return dbc;
  }
  
  
/* Vytvori prázdnu dimenziu.
 * Objekty gafikónu používajú prázdnu dimenziu na úpravu fumkcionality obrazovky
 * alebo pri vytváraní novej reálnej/platnej dimenzie
 *********************************************************************/
  public void createFakeTimeDimension(int year, int month, int dimNum) {
      DBtableColumn[] fakeDimColumns;
      Object[][] fakeDimData;
      
//      dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
//      numdecimals^formatstring^tooltyp^HIDDEN
      // vytvorenie pola stlpcov
      fakeDimColumns = new DBtableColumn[2];
      //oPrimaryKey vsade prerobit na String !!, co je fieldName stlpca
      fakeDimColumns[0] = //sPrimaryKey;
            new DBtableColumn(krn,MyCn,
                "^^id^integer^null^id^7^0^#######^Pôvodný index riadku");
      fakeDimColumns[1] = 
            new DBtableColumn(krn,MyCn,
                "^^nazov^char^null^nazov^50^0^#######^" +
                "< " + cGrafName + " - Prázdna dimenzia >");
     // fakeDimColumns[1].
      fakeDimData = new Object[alTblData.size()][2];
      int i = -1; // counter
      for (Object[] o: alTblData) {
          i++; // increase counter
         fakeDimData[i][0] = null;  // primarny kluc - fakeDim nema primarny kluc
         fakeDimData[i][1] = o[3].toString() + " is ID FOR <fakeDim " + dimNum 
                           + " row " + i + " for graf "  
                           + cGrafName + " >"; // prazdna hodnota 
      }
      //QQkrn1 krn.OutPrintln("Creating FAKEDIM number " + dimNum + " begin");
      EOC_Gdimension dim = new EOC_Gdimension(krn, MyCn, 
          "emptyDim " + dimNum,"Prázdna dimenzia " + dimNum + " pre grafikón "
              + cGrafName ,"Objekty gafikónu používajú prázdnu"
          + " dimenziu na úpravu fumkcionality obrazovky",
          true /* isDimension */,
          /*myParentBrowser,*/ (EOC_graf) this,
          fakeDimColumns, fakeDimData, sPrimaryDbTable, sPrimaryDbKey,
          bHiddenPrimaryKey /*,jcbDimensions*/);
      ////krn.OutPrintln("Creating FAKE-DIMMENSION number " + dimNum + " bgin PK=" + sPrimaryKey);
      dimensionList.add(dim);
      dim.initialize( /* krn, MyCn */ ); 
      ////krn.OutPrintln("Creating FAKE-DIMMENSION number " + dimNum + " - finished");
      //QQkrn1krn.OutPrintln("Creating FAKE-DIMMENSION number " + dimNum + "  with " 
      //                   + fakeDimData.length + " rows - end");
  }

  public void grafikonWideRemoveRow(int rw) {
 // sem patri mazanie riadku rw z tabulky pokial to je graf-objekt
 // plus mazanie riadku rw v dim objektov, ulozenych v dimensionList-e 
      // NEMAZAT - MOZNO BUDE POTREBNE
 if (!isDimension) {    
//      rowHeaderData.remove(rw);
//           dimensionData.remove(rw);
//           allData.remove(rw);
    //       bRendering = true;
    //       rowHeaderTableModel.removeRow(rw);
    //       bRendering = true;
  //         dimensionTableModel.removeRow(rw);
 }
    }
  
  /* objekt sa seba selektuje (sa ?) :-)
   *************************************************************************/
  public void makeSelected() {
    /* inicializacia potrebnych premennych v graf-objekte,
     * na zaklade internych hodnot objektu, pokial este neboli inicializovane
     *************************************************************************/
    if (!bInitialized) {
        krn.Message("makeSelected() -> Graf-objekt nie je inicializovaný.");
    }
    if (!isDimension)
    krn.OutPrintln(((myParentBrowser==null?"pbrNull":"pbrOK")) + " -- " + ((myTable==null?"tblNull":"tblOK"))
    + " numDemensions== " + dimensionList.size());
    if (!isDimension) myParentBrowser.setHeaderTable(myTable);
    else myParentBrowser.setDimensionTable(myTable);
    this.fillTables();
    int dimIdx = -1;
    int idxCnt = -1;
    // ked sa bude jednat o prve volanie, vybere sa prva dimenzia
    boolean firstCall = false;  
    if (!isDimension) {
        /* pridanie svojich dimenzii do vyberu dimenzii grafikonu
         ***********************************************************/
        // mazanie existujucieho vyberu comboboxu dimenzii
        setRendering(true,"ALL", this, true);
        while (jcbDimensions.getItemCount() > 0) 
             jcbDimensions.removeItemAt(jcbDimensions.getItemCount() - 1); 

        // pridanie vlastnych dimenzii do vyberu comboboxu dimenzii
        for (EOC_Gdimension dim : dimensionList) {
            jcbDimensions.addItem(dim.getcGrafLabel());
            idxCnt++;
             if (currentDim == null) { // je to prve volanie metody
                 dimIdx = 0;
                 currentDim = dim;
                 firstCall  = true; // prve volanie metody
             } 
             // nejedna sa o prve volanie, vybere sa aktualna/pouzivana dimenzia
             if (!firstCall) { 
                 if (dim == currentDim)  {
                     dimIdx = idxCnt;
                 }
             }
        }
        selectDimension(dimIdx);
        if (dimIdx > -1)
            jcbDimensions.setSelectedIndex(dimIdx);
    } // if (!isDimension) {
    else
        setRendering(false,"ALL", this, true);
  }
    
  public void selectDimension(Integer dimIdx) {
      if (dimIdx > -1 && dimIdx != null) {
          // vykreslenie tabulky potrebnej dimenzia
          currentDim = dimensionList.get(dimIdx);
          currentDim.makeSelected();
      }
      else {
          myParentBrowser.hideDimensions();
          // vypraznenie oblasti tabuliek dimenzii
      }
  }
  
  public void selectDimension(String dimName) {
      Integer dimIdx  = -1;
      int     counter = -1;
      x_block:
      for (EOC_Gdimension gd: dimensionList) {
           counter ++; 
           //krn.OutPrintln("gd.getcGrafName()==" + gd.getcGrafName());
           if (gd.getcGrafName().equalsIgnoreCase(dimName)) {
              dimIdx = counter;
              break x_block;
           }
      }
      if (dimIdx > -1 && dimIdx != null) {
          // vykreslenie tabulky potrebnej dimenzia
          currentDim = dimensionList.get(dimIdx);
          currentDim.makeSelected();
      }
      else {
          Kernel.staticMsg("V  grafikóne " + this.cGrafName 
                     + " sa dimenzia s názvom: " + dimName + " nenašla.");
          myParentBrowser.hideDimensions();
          // vypraznenie oblasti tabuliek dimenzii
      }
  }
  
  public void setHeaderWidth(int newWidth) {
     if ((!isDimension) && (myParentBrowser != null) ) {
         myParentBrowser.reselectCurrentGraficon(); // upravi obrazovku 
     }
  }
protected void changeSelection(int rowIdx) {
    if (bRendering) return;
    myParentBrowser.changeSelection(rowIdx, isDimension);
}

protected void shareIdxKeyList (ArrayList<Object[]> alShrIdxKey, 
                                SortOrder sortOrder, EOC_graf ListOwner) {
    // Spätna kontrola ci sa metoda nevola z nespravneho objektu
    // parentom volajuceho objektu musi byt tento objekt
    /*
    if ((!isDimension) && (ListOwner != (EOC_graf) this)) {
       krn.krnMsg("E", "Odmietnuté zdielanie triediacého klúča medzi dimenziami"
                     + " v EOC_graf-objekte '" + cGrafName + "'"
                     , "Nesprávné volanie metódy shareIdxKeyList()");
       return;
    };*/
    // dimenzia to predáva dalej, parent-objektu
    // parent-objekt tuto dimeziu vynecha pre distribuovani noveho kluca
    if (isDimension && (ListOwner==this)) { 
        myParentGraf.shareIdxKeyList(alShrIdxKey, sortOrder, ListOwner);
      return;
    }
    if ((ListOwner.isDimension) && (!isDimension)) {
            getTable().setRendering(true);
            getTable().treatIdxKey(alShrIdxKey, ListOwner);
            getTable().setRendering(false);
    }


   // distribucia noveho indexoveho kluca 
        
        // citanie ArrayList-u zdielanej index-definicie
        // POZNAMKA !!! - bolo by treba ArrayList prenasat nejakym sposobom 
        // v povodnom poradi, potom by sa tabulka myTable dala citat
        // bez potreby vyhladavania !!!
            setRendering(true, "ALL", this, true);
        for (EOC_Gdimension g: dimensionList) {
            // ListOwner len kvoli istote
            if ((EOC_graf) g == ListOwner) {
                krn.OutPrintln("GRAF: " + cGrafName 
                      + " - shareIdxKeyList() - Breaking dimension: "
                      + g.getcGrafName());
                continue; 
            }
 //               krn.OutPrintln("GRAF: " + cGrafName 
 //                     + " - shareIdxKeyList() - treating key in dimension: "
 //                     + g.getcGrafName());
 //   public void setRendering (boolean renderingState, String propagateMode,
 //            Object caller, boolean ignoreCaller) {
            g.getTable().setRendering(true);
            g.getTable().treatIdxKey(alShrIdxKey, ListOwner);
            g.getTable().setRendering(false);
        }
            setRendering(false, "ALL", this, true);
                
       ////docasne myTable.getRowSorter().toggleSortOrder(2);
            
}

    public void replaceDataColumns(DBtableColumn[] dbc) {
        if (!isDimension) {
            for (EOC_Gdimension dim : dimensionList) {
                dim.replaceDataColumns(dbc);
           }
        }
        else {
            alTblColumns.clear(); // mazanie povodnych stlpcov z pola
            alTblColumns.addAll(Arrays.asList(mandatoryColumns));
            for (DBtableColumn dbc1 : dbc) {
                if (dbc1 != dbtcPrimaryKey) {
                    alTblColumns.add(dbc1);
                }
            }
krn.OutPrintln("lnG==" + alTblColumns.size());
            refreshTable(true /*musi*/);
            createFakeRows();

        }
    }
public void reviveCells(boolean bRevive) {
    bLivingCells = bRevive;
    if (isDimension) {
       myTable.reviveCells(bLivingCells);
    }
    else {
       for (EOC_Gdimension d: dimensionList) {
           d.reviveCells(bLivingCells);
       }
    }
}
    

}

