/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.xinterface;

import eoc.EOC_message;
import eoc.database.DBconnection;
import system.Kernel;
import java.awt.Cursor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class XQuery extends eoc.EASobject {
   /*
    * XTable specific variables
    * ***********************************************************************/
    // nechcem vidiet prepared-nic. Az na vynimkz.
    // Rychlost dosiahneme inde, a na inom leveli !
    private Statement myBPs;
    private ResultSet         myBRs;  // kompletny resultset na odovzdanie udajov 

    private PreparedStatement myChPs;
    private ResultSet         myChRs;  // pracovny resultset na odovzdanie udajov 

    private Statement mainStm;
    private ResultSet mainRS;

    // Prepared-statement-y 
    private PreparedStatement myXPs;
    private ResultSet         myXRs;  // pracovny resultset na odovzdanie jednej vety

    private PreparedStatement myFPs;
    private ResultSet         myFRs;  // pracovny resultset prvej vety

    private PreparedStatement myLPs;
    private ResultSet         myLRs;  // pracovny resultset poslednej vety

    
    private String DBtype = "";

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
    private String sExternalKeyValue = ""; // aktualna hodnota vyhladavaneho kluca

    private eoc.IEOC_Object owRowSource; // Zdroj, poskytujuci hodnotu sExternalKey
   
    private String sMasterTable = ""; // hlavna tabulka dotazu

    // premenne, suvisiace s primarnym klucom tabulky
    private String sMasterKey = ""; // vyhladavany stlpec hlavnej tabulky dotazu
                                 // pri spravne navrhnutej DB to je ekvivalent
                                 // sExternalKey alebo alternativny unique key
    private String sMasterKeyDataType = "number"; // datovy typ sMasterkey
                                       // char,number,date,datetime,boolean,...
    private String sMasterKeyValue = ""; // aktualna hodnota vyhladavaneho kluca

    // premenne, suvisiace s aktualnym triediacim klucom tabulky
    private String sOrderVector = "asc"; // smer triedenia stlpeca hlavnej tabulky dotazu
    private String sOrderKey = ""; // triediaci stlpec hlavnej tabulky dotazu
    private String sOrderKeyDataType = "number"; // generic-datovy typ sOrderkey
                                       // char,number,date,datetime,boolean,...
    // casti retazca query prikazu 
    private String sBaseQueryRoot        = ""; // select * from eas_usrgrp
    private String sBaseQueryAppWhere    = ""; // !eas_usrgrp.bDisabled
    private String sChunkQueryAppWhere   = ""; // !eas_usrgrp.bDisabled
    private String sQueryUsrWhere        = ""; // eas_usrgrp.c_meno = 'jozika'
    private String sBQueryFullStatement  = ""; // poskladany dotaz
    private String sChQueryFullStatement = ""; // poskladany dotaz chunk-u
    private String sQueryPrevStatement   = ""; // predosly poskladany dotaz
    private String sOneRowQueryFullStatement  = ""; // poskladany dotaz pre jednu vetu
    private String sFQueryFullStatement  = ""; // poskladany dotaz pre prvu vetu
    private String sLQueryFullStatement  = ""; // poskladany dotaz pre poslednu vetu
    private String sFullQuery = "";         // uzivatelom zadany cely dotaz
    // Stavy objektu 
    private boolean bIsQryOpened=false;  // ci je dotaz otvoreny
    private boolean bIsQryEmpty=true;    // ci je dotaz otvoreny
    // premenne, riadiace caching tabulky v browseri
    private int     iNumFetchedRows  = -9999; // fullFetch //30;
    private String sFirstExistOrderKeyValue = ""; // hodnota na akt.riadku tabulky
    private String lastRebuildParams = ""; // hodnoty poslednych parametrov rebuildQuery
    

    protected void finalize(){
        System.out.println("XQuery--finalizeeee");
        if (mainStm!=null) try {
            mainStm.close();
        } catch (SQLException ex) {
            Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        super.initialize(kr, cX);
        try {
            mainStm = MyCn.getConn().createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public String getQueryFullStatement() {
        return sBQueryFullStatement;
    }

    public String getQueryBase() {
        return sBaseQueryRoot;
    }

    public String setQueryBase(String queryBase) {
        sBaseQueryRoot = queryBase;
        krn.debugOut(this,5,"Setting gueryBase to " + sBaseQueryRoot);
        return "";
    }

    public String getAppWhere() {
        return sBaseQueryAppWhere;
    }

    public String setAppWhere(String appWhere) {
        sBaseQueryAppWhere = appWhere;
        rebuildQuery(); // 2016-03-10 - zasadny, tri dni hladany problem vyrieseny
        return "";
    }

    public String getUsrWhere() {
        return sQueryUsrWhere;
    }

    public String setUsrWhere(String usrWhere) {
        sQueryUsrWhere = usrWhere;
        return "";
    }

    public String setFullQuery (String sQry) {
        sFullQuery = sQry;    
        //QQQ asi by bolo treba odrezat pripadnu where podmienku
        sBaseQueryRoot = sQry;
        return "";
    }
    

   @Override
    public String destroy() {
        try {
////            System.out.println("destroooing XQuery for >>>>>>>>>" + sMasterTable);
            if (myBPs != null) myBPs.close();
            if (myBRs != null) myBRs.close();  // kompletny resultset na odovzdanie udajov
            
            if (myChPs != null) myChPs.close();
            if (myChRs != null) myChRs.close();  // pracovny resultset na odovzdanie udajov

            if (myXPs != null) myXPs.close();
            if (myXRs != null) myXRs.close();  // pracovny resultset na odovzdanie jednej vety
            
            if (myFPs != null) myFPs.close();
            if (myFRs != null) myFRs.close();  // pracovny resultset prvej vety
            
            if (myLPs != null) myLPs.close();
            if (myLRs != null) myLRs.close();  // pracovny resultset poslednej vety
            
            if (mainStm != null) mainStm.close();
            if (mainRS != null)  mainRS.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public void rebuildQuery() {
        rebuildQuery(null,null,true);
    }
    
    // poskladanie obsahu vsetkych pouzivanych dotazov
    public String rebuildQuery(String sFirstExistOrdKeyValue, 
                               String sReadVector, boolean bIncludeKey) {
        if (sFullQuery.length() > 0) {
            sQueryPrevStatement  = sBQueryFullStatement; //odlozenie predosleho dotazu
            sBQueryFullStatement = sFullQuery;
            // !!! TOTO  TREBA ASI IMPLEMENTOVAT lastRebuildParams = rebParms;
         //   return "";
        }
        //SELECT sa ma odobrat a pridat sa ma where a order by !!!
        if (sFirstExistOrdKeyValue==null) sFirstExistOrdKeyValue = "!@@FIRST";
        if (sReadVector==null) sReadVector = "down";
        String rebParms = sFirstExistOrdKeyValue + "^" + sReadVector + "^" + bIncludeKey;
        if (rebParms.equals(lastRebuildParams)) return "";

        if (DBtype.equals("")) DBtype = MyCn.getDBType();
        String sBaseQryRoot     = "";  // base
        String sBaseOneRowQry   = "";  // base for one row
        String sChunkWhr   = "";  // where s obmedzenim pre aktualny chunk
        String sNoChunkWhr = "";  // where bez obmedzeni pre aktualny chunk
        String sOrd   = "";  // order by
        String sFQry  = "";  // base for first row
        String sFOrd  = "";  // order by for first row
        String sLQry  = "";  // base for last row
        String sLOrd  = "";  // order by for last row

        if (sFullQuery.length() > 0) sBaseQryRoot = sFullQuery;
        
        if (sBaseQueryRoot == null || sBaseQueryRoot.equals("")) {
            return "EOC-ERR=Prázdna definícia základu dotazu !"; // CHYBA
        }
        sBaseOneRowQry = sBaseQueryRoot + " where " + sMasterKey + "=?"; // dotazu na jednu vetu
        
        sBaseQryRoot = sFQry = sLQry = sBaseQueryRoot; //zaklad hlavneho/pracovneho dotazu 
        // istota je gulomet
        if ((sOrderKey == null) | sOrderKey.equals("")) { sOrderKey = sMasterKey; }
        // kluc/stlpec pre triedenie
        sOrd = sFOrd = sLOrd = sOrderKey;
        /*
        krn.krnMsg(
               "!!!sMasterKeyDataType=" + sMasterKeyDataType  + 
               "\n!!!sExternalKeyDataType=" + sExternalKeyDataType + 
               "\n!!!sOrderKeyDataType=" + sOrderKeyDataType  
               ) ;
               * */
        // vektor triedenia pre sub-dotazy na zistenie prvej a poslednej vety
        if (sOrderVector.equals("asc")) { sFOrd += " asc "; sLOrd += " desc "; }
        else { sFOrd += " desc "; sLOrd += " asc "; }
        
        // pridanie obmedzenia citania poctu viet pre Sybase DB
        if (DBtype.equals("SYBASE")) {
            // -9999 == fullFetch
            if (iNumFetchedRows != -9999) {
               sBaseQryRoot = sBaseQryRoot.replace("select", 
                                           "select top " + iNumFetchedRows + " start at 1");
            }
           sFQry = sFQry.replace("select", "select top 1 start at 1");
           sLQry = sLQry.replace("select", "select top 1 start at 1");
        }

        // ked to nie je !@@FIRST ani !@@LAST
       //  System.out.println("sFirstExistOrdKeyValue_is==== " + (sFirstExistOrdKeyValue == null?"NULL":sFirstExistOrdKeyValue));
        if ((sFirstExistOrdKeyValue!=null) && !(sFirstExistOrdKeyValue.startsWith("!@@"))) {
           if (sOrderVector.equals("asc")) {
           sChunkWhr = sOrderKey + (sReadVector.equals("UP")? " < " : " > ") + 
               (sOrderKeyDataType.equalsIgnoreCase("NUMERIC")||sOrderKeyDataType.equalsIgnoreCase("INTEGER") 
                   ?sFirstExistOrdKeyValue:"'" + sFirstExistOrdKeyValue + "'");
           }
           else {
            sChunkWhr = sOrderKey + (sReadVector.equals("UP")? ">" : "<") + 
               (sOrderKeyDataType.equalsIgnoreCase("NUMERIC")||sOrderKeyDataType.equalsIgnoreCase("INTEGER") 
                   ?sFirstExistOrdKeyValue:"'" + sFirstExistOrdKeyValue + "'");
           }
           // pridanie smeru triedenia pre jednotlive dotazy
           if (sReadVector.equals("UP")) {   
              // ked sa cita UP-chunk, triedene ma byt v opacnom poradi

             sOrd  = sOrd  + ( sOrderVector.equals("asc") ? " desc " : " asc " );
////!!!               sOrd  = sOrd  + " " + sOrderVector + " ";
           }
           else { // malo by to byt "DOWN", teda to aspon predpokladam, a hotovo
                  // lebo to by sme isli do dalsich 'dimenzii', co 'zatial' radsej nie. :-)
              // ked sa cita DOWN-chunk, triedene ma byt v spravnom poradi
              sOrd  = sOrd  + " " + sOrderVector + " ";
           }
        }
        // ked to je !@@FIRST alebo !@@LAST
        else { // 
           if (sFirstExistOrdKeyValue.equals("!@@FIRST")) {
              // ked sa cita prvy chunk, triedene ma byt v spravnom poradi
              sOrd  = sOrd  + " " + sOrderVector + " ";
           }
              // ked sa cita posledny chunk, triedene ma byt v opacnom poradi
           if (sFirstExistOrdKeyValue.equals("!@@LAST")) {
              sOrd  = sOrd  + ( sOrderVector.equals("asc") ? " desc " : " asc " );
           }
        }
        
        // v pripade bIncludeKey ma resultset obsahovat aj vetu s hodotou kluca
        // to znamena ze operator < sa meni na <=,  a operator > na >=
        if(bIncludeKey==true) {
            sChunkWhr = sChunkWhr.replace("<","<=");
            sChunkWhr = sChunkWhr.replace(">",">=");
        }
        ////   krn.OutPrintln("bIncludeOrdkey = " + bIncludeKey + " sWhr=" + sWhr);

        // skladanie where-podmienky na cudzi kluc
        if (!sExternalKeyInMyTable.equals("") && !sExternalKeyInMyTable.equals("<NONE>")) {
            System.out.println("##############sExternalKeyDataType:" + sExternalKeyDataType);
           if (sExternalKeyDataType.equalsIgnoreCase("NUMERIC") || sExternalKeyDataType.equalsIgnoreCase("INTEGER")) {
               if (sExternalKeyValue==null || sExternalKeyValue== "") sExternalKeyValue = "0";
           sNoChunkWhr = sExternalKeyInMyTable + " = " + sExternalKeyValue;
           sChunkWhr   = sExternalKeyInMyTable + " = " + sExternalKeyValue;
           }
           else {
           sNoChunkWhr = sExternalKeyInMyTable + " = '" + sExternalKeyValue + "'";
           sChunkWhr   = sExternalKeyInMyTable + " = '" + sExternalKeyValue + "'";
           }
        }
        if (sBaseQueryAppWhere != null && !sBaseQueryAppWhere.equals("")) {
            sChunkWhr += ((sChunkWhr.equals("")) ? "" : " and ") + " (" + sBaseQueryAppWhere + ")";
            sNoChunkWhr = " (" + sBaseQueryAppWhere + ")";
        }
        if (sQueryUsrWhere != null && !sQueryUsrWhere.equals("")) {
            sChunkWhr += ((sChunkWhr.equals("")) ? "" : " and ") + " (" + sQueryUsrWhere + ")";
            sNoChunkWhr += ((sNoChunkWhr.equals("")) ? "" : " and " ) + " (" + sQueryUsrWhere + ")";
        }
/////        System.out.println(">>>>>>>>>CHUKKVERRE:" + sChunkWhr + " noo: " + sNoChunkWhr);
//        sFLWhr = sWhr;
        // skladanie order by klauzuly
/*        sOrd  += sOrderKey;
        sFOrd += sOrderKey;
        sLOrd += sOrderKey;*/
        
        // pridanie obmedzenia citania poctu viet pre Postgres DB
        if (DBtype.equals("POSTGRES")) {
                        // -9999 == fullFetch
            if (iNumFetchedRows != -9999) {
                sOrd  = sOrd  + " limit " + iNumFetchedRows + " offset 0";
            }
            sFOrd = sFOrd + " limit 1 offset 0";
            sLOrd = sLOrd + " limit 1 offset 0";
        }
        
        sQueryPrevStatement = sBQueryFullStatement; //odlozenie predosleho dotazu
        /*
    private String sBaseQueryRoot        = ""; // select * from eas_usrgrp
    private String sBaseQueryAppWhere    = ""; // !eas_usrgrp.bDisabled
    private String sChunkQueryAppWhere   = ""; // !eas_usrgrp.bDisabled
    private String sQueryUsrWhere        = ""; // eas_usrgrp.c_meno = 'jozika'
    private String sBQueryFullStatement  = ""; // poskladany dotaz
    private String sChQueryFullStatement = ""; // poskladany dotaz chunk-u
    private String sQueryPrevStatement   = ""; // predosly poskladany dotaz
    private String sOneRowQueryFullStatement  = ""; // poskladany dotaz pre jednu vetu
    private String sFQueryFullStatement  = ""; // poskladany dotaz pre prvu vetu
    private String sLQueryFullStatement  = ""; // poskladany dotaz pre poslednu vetu
        */ 
    
        sChQueryFullStatement = sBaseQryRoot 
                           + ((sChunkWhr.equals("")) ? " " : " where " + sChunkWhr)
                           + ((sOrd.equals("")) ? " " : " order by " + sOrd);
        sBQueryFullStatement = sBaseQryRoot 
                           + ((sNoChunkWhr.equals("")) ? " " : " where " + sNoChunkWhr)
                           + ((sOrd.equals("")) ? " " : " order by " + sOrd);
        sOneRowQueryFullStatement = sBaseOneRowQry; 
        sFQueryFullStatement = sFQry 
                           + ((sNoChunkWhr.equals("")) ? " " : " where " + sNoChunkWhr)
                           + ((sFOrd.equals("")) ? " " : " order by " + sFOrd);
        sLQueryFullStatement = sLQry 
                           + ((sNoChunkWhr.equals("")) ? " " : " where " + sNoChunkWhr)
                           + ((sLOrd.equals("")) ? " " : " order by " + sLOrd);
//        krn.OutPrintln("rebuildQuery()->queryBFullStatement = " + sBQueryFullStatement); 
//        krn.OutPrintln("rebuildQuery()->queryChFullStatement = " + sChQueryFullStatement); 
        //krn.debugOut(this,4,"rebuildQuery()->queryFullStatement = " + sQueryFullStatement); 
//        krn.krnMsg("XQuery->rebuildQuery()->queryFullStatement = " + sBQueryFullStatement);
        krn.debugOut(this, 5,"XQuery->rebuildQuery()->queryFullStatement = " + sBQueryFullStatement);
        
        lastRebuildParams = rebParms;
        return "";
    } // String rebuildQuery()
    
    public void closeQuery() {
        try {
        if (!(myBPs==null)) myBPs.close();
        if (!(myChPs==null)) myChPs.close();
        if (!(myXPs==null)) myXPs.close();
        if (!(myFPs==null)) myFPs.close();
        if (!(myLPs==null)) myLPs.close();
        } catch (SQLException ex) {
            Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        bIsQryOpened = false;
        lastRebuildParams = "";        
    }
    
    // rendering/poskladanie XQuery-objektom pouzivanych dotazov
    public String openQuery(String sFirstExistOrdKeyValue,
                            String sReadVector, boolean bIncludeKey ) {
        // znovuzlozenie dotazov
        rebuildQuery(sFirstExistOrdKeyValue, sReadVector, bIncludeKey);
        
        /* NEMAZAT - dolezity test funcnosti, ked je zle 
         * =================================================================
         * definicie dotazov
    private String sBQueryFullStatement  = ""; // poskladany dotaz
    private String sChQueryFullStatement = ""; // poskladany dotaz chunk-u
    private String sQueryPrevStatement   = ""; // predosly poskladany dotaz
    private String sOneRowQueryFullStatement  = ""; // poskladany dotaz pre jednu vetu
    private String sFQueryFullStatement  = ""; // poskladany dotaz pre prvu vetu
    private String sLQueryFullStatement  = ""; // poskladany dotaz pre poslednu vetu
         * ================================================================= 
         System.out.println(
                "CALLER: " + FnEaS.getCallerMethodName() + "\n"
              + "sBQueryFullStatement: "  + sBQueryFullStatement + "\n"
              + "sChQueryFullStatement: " + sChQueryFullStatement + "\n" 
              + "sQueryPrevStatement: " + sQueryPrevStatement + "\n" 
              + "sOneRowQueryFullStatement: " + sOneRowQueryFullStatement + "\n" 
              + "sFQueryFullStatement: " + sFQueryFullStatement + "\n" 
              + "sLQueryFullStatement: " + sLQueryFullStatement );
         */
        try {
            if (!(myBPs==null)) myBPs.close();
            if (!(myChPs==null)) myChPs.close();
            if (!(myXPs==null)) myXPs.close();
            if (!(myFPs==null)) myFPs.close();
            if (!(myLPs==null)) myLPs.close();
          ////  System.out.println("FULSTATT_sBQueryFullStatement:" + sBQueryFullStatement);
            myBPs = MyCn.getConn().prepareStatement(sBQueryFullStatement,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // -9999 == fullFetch
            if (iNumFetchedRows!=-9999) myBPs.setFetchSize(iNumFetchedRows);
            //// System.out.println("CHRESSULTTT:" + sChQueryFullStatement);
            myChPs = MyCn.getConn().prepareStatement(sChQueryFullStatement,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (iNumFetchedRows!=-9999) myChPs.setFetchSize(iNumFetchedRows);
            myXPs = MyCn.getConn().prepareStatement(sOneRowQueryFullStatement,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (iNumFetchedRows!=-9999) myXPs.setFetchSize(iNumFetchedRows);
            myFPs = MyCn.getConn().prepareStatement(sFQueryFullStatement,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            myFPs.setFetchSize(1);
            myLPs = MyCn.getConn().prepareStatement(sLQueryFullStatement,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            myLPs.setFetchSize(1);
        } catch (SQLException ex) {
            Logger.getLogger(eoc.xinterface.XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
            krn.getDsk().setCursor(hourglassCursor);
            //krn.Message(">>>XQuery-openQuery(sChQueryFullStatement)\n" + sChQueryFullStatement);
            System.out.println(">>>XQuery-openQuery(sChQueryFullStatement): " + sChQueryFullStatement);
            // 060116 myChRs = myChPs.executeQuery(); // otvorenie dotazu
// SYBASE            myChRs = myChPs.executeQuery(sChQueryFullStatement); // otvorenie dotazu
//postgres
            myChRs = myChPs.executeQuery(); // otvorenie dotazu
//            myChRs = myChPs.executeQuery(sChQueryFullStatement); // otvorenie dotazu
            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            krn.getDsk().setCursor(normalCursor);
            
            myChRs.beforeFirst();  
            bIsQryEmpty = !myChRs.next();  
            bIsQryOpened = true;
        } catch (SQLException ex) {
            bIsQryEmpty = true;
            Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
           Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            krn.getDsk().setCursor(normalCursor);
         krn.debugOut(this,4,"openQuery()->sBQueryFullStatement=" + sBQueryFullStatement 
            + " ... bIsQryOpened=" + bIsQryOpened + " bIsQryEmpty=" + bIsQryEmpty);
       }
        return "";
    } // openQuery(String sFirstExistOrdKeyValue, ...

    // nastavenie nazvu hlavnej tabulky
    public String setMasterTable(String sMstTblName) {
       sMasterTable = sMstTblName;
       krn.debugOut(this,4,"setting sMasterTbl to: "  + sMstTblName);
       return "";
    }
   
   // prijem sprav cudzich objektov
    
    @Override
   public String receiveMessage(EOC_message eocMsg) {

        // musi byt ako prva instrukcia v metode !!!
        if (eocMsg==null) return FnEaS.nullEocMessageResponse(myObjectID);
        
      /* Volanie metody v EOC_Object, ktora pusti metodu sMessage pokial je to
       * existujuca metoda, a sMessage ma prefix RUN
       */
       try {
           // testovat bude treba podla obsahu sParameters
          krn.debugOut(this,5,"receiveMessageInEOC_query=>" + eocMsg.getMessage());
          Method mtd;
          try {
             mtd = this.getClass().getMethod(eocMsg.getMessage(), new Class[]  {String.class});
          } catch (NoSuchMethodException ex) {
             mtd = null;
          }
          if (mtd == null) {
             krn.debugOut(this, 5,"mtd1 is null");
             try {
                mtd = this.getClass().getDeclaredMethod(eocMsg.getMessage(), new Class[] {String.class});
             } catch (NoSuchMethodException ex) {
               krn.debugOut(this,5,"Neexistujúca metóda: " + ex.toString());
               mtd = null;
             } catch (NullPointerException ex) {
                krn.debugOut(this,5,"Metóda je NULL: " + ex.toString());
                mtd = null;
             }
          }
          try {
             Object rv =  mtd.invoke(this, eocMsg.getParameters());
             krn.debugOut(this,4,"Vykonaná metóda: " + mtd.getName());
             return rv.toString();
          } catch (IllegalAccessException ex) {
             krn.debugOut(this,0,"Illegálny prístup k metóde: " + ex.toString());
          } catch (IllegalArgumentException ex) {
             krn.debugOut(this,0,"Chyba argumentov pre: " + ex.toString());
          } catch (InvocationTargetException ex) {
             krn.debugOut(this,0,"Problém pri volaní: " + ex.toString());
          }
       } catch (SecurityException ex) {
         return "NOTRECEIVED-EOC_XQuery=SecurityExceptionA";
       }
       return "NOTRECEIVED-EOC_XQuery==";
   }

   public String getExternalTable() {
      return sExternalTable;
   }

   public String setExternalTable(String sExtTblName) {
      sExternalTable = sExtTblName;
      return "";
   }

   public String setExternalKeyValue(String sExtKeyVal) {
      sExternalKeyValue = sExtKeyVal;
      return "";
   }

   public String getExternalKeyInExtTable() {
      return sExternalKeyInExtTable;
   }

   public String getExternalKeyInMyTable() {
      return sExternalKeyInMyTable;
   }

    public String setExternalKeyInExtTable(String sKeyName, String sKeyDataType) {
       sExternalKeyInExtTable = sKeyName; 
       sExternalKeyDataType = sKeyDataType;
       return "";
    }

    public String setExternalKeyInMyTable(String sKeyName, String sKeyDataType) {
       sExternalKeyInMyTable = sKeyName; 
       sExternalKeyDataType = sKeyDataType;
       return "";
    }

   // nastavenie triedenia dotazu ( NazovKluca, ASC/DESC, DatovyTypKluca )
   public String setOrdering(String sOrdKey, String sOrder, String sOrdKeyDataType) {
      ////krn.OutPrintln("setOrderingFrom_XQuery.setOrdering");
      krn.debugOut(this,5,"setOrdering()->" + sOrdKey + " " 
                         + sOrder + " " + sOrdKeyDataType);
      sOrderKey         = sOrdKey;
      sOrderVector      = sOrder.toLowerCase();
      sOrderKeyDataType = sOrdKeyDataType;
//      rebuildQuery("!@@FIRST",(sOrder.equalsIgnoreCase("ASC")?"DOWN":"UP"),true);
//      openQuery("!@@FIRST","DOWN",true);
      return "";
   }
   
   public String getMasterTable() {
      return sMasterTable;
   }

   public String getMasterKey() {
      return sMasterKey;
   }

   public String setMasterKey(String sMstKeyName, String sMstKeyDataType) {
      sMasterKey = sMstKeyName; 
      sMasterKeyDataType = sMstKeyDataType;
      return "";
   }

   public String setOrderKey(String sOrdKeyName, String sOrdKeyDataType) {
      sOrderKey = sOrdKeyName; 
      sOrderKeyDataType = sOrdKeyDataType;
      return "";
   }

   public String getMasterKeyValue() {
      return sMasterKeyValue;
   }

   public String setMasterKeyValue(String sMstKeyVal) {
      sMasterKeyValue = sMstKeyVal; return "";
   }

   // vrati nastaveny generic-datovy typ pre sMasterKey 
   public String getMasterKeyDataType() { return sMasterKeyDataType; }

   // nastavy generic-datovy typ pre sMasterKey 
   public String setMasterKeyDataType(String sMstKeyDataType) {
      sMasterKeyDataType = sMstKeyDataType; return "";
   }

   // vrati nazov nastaveneho triediaceho kluca -> (nazov stlpca databazovej tabulky)
   public String getOrderKey() { return sOrderKey; }

   // vrati prave nastaveny pocet naraz nacitanych viet do resultset-u
   public Integer getiNumFetchedRows() { return iNumFetchedRows; }

   // nastavy pocet naraz nacitanych (cache-ovanych) viet do resultset-u
   public String setiNumFetchedRows(int iNumRows) {
      try {
         iNumFetchedRows = iNumRows;
         if (!(myBPs==null)) { 
             if (iNumFetchedRows!=-9999) myBPs.setFetchSize(iNumFetchedRows); 
         }
         if (!(myChPs==null)) { 
             if (iNumFetchedRows!=-9999) myChPs.setFetchSize(iNumFetchedRows);
         }
         return "";
      } catch (SQLException ex) {
         Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
         return ex.getMessage();
      }
   }

   // vytvory 'prazdy' dotaz, ktory je vzdy bez riadkov
   public String buildEmptyQuery() {
        if (sBaseQueryRoot == null || sBaseQueryRoot.equals("")) {
            return "EOC-ERR=Prázdna definícia základu dotazu !"; // CHYBA
        }
        return sBaseQueryRoot + " where 1=0";
   }

   // vrati strukturu naposledy pouziteho resultset-u
   public ResultSetMetaData getrsMetaData() {
      ResultSetMetaData rsmtdt;
      try {
         
         String Qry = buildEmptyQuery();
         krn.debugOut(this,4,"EmptyQry=" + Qry);
//         if (!(stm==null)) stm.close();
//         stm = MyCn.createStatement();
         //ResultSet rst = 
         System.out.println("!!!!!SRRACKERY 1: " + Qry);
         mainRS = mainStm.executeQuery(Qry);
         System.out.println("!!!!!SRRACKERY 2: " + Qry);
         rsmtdt = mainRS.getMetaData();
         System.out.println("!!!!!SRRACKERY 3: " + rsmtdt.getColumnCount());
         return rsmtdt;
      } catch (SQLException ex) {
         System.out.println("ReturningSQLanywherePreparedPicsa ako PostgresPicsa"); 
         Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   // vrati povodny, ne cache-ovany resultset, z ktoreho bol cache-ovany rowset vytvoreny   
   public ResultSet getBResultSet() {
      try {
         myBRs.beforeFirst();
         return myBRs;
      } catch (SQLException ex) {
         Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }
   
   // vrati povodny, cache-ovany resultset, z ktoreho bol cache-ovany rowset vytvoreny   
   public ResultSet getChResultSet() {
      try {
         myChRs.beforeFirst();
         return myChRs;
      } catch (SQLException ex) {
         Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }
   
   // vrati dalsi chunk, zacinajuci po key-hodnote sFirstExistOrdKeyValue smerom sVector
   public ResultSet getChResultSet(String sFirstExistOrdKeyValue, 
                                 String sReadVector, boolean bIncludeKey) {
       if (myChRs==null) return null;
//     System.out.println(">>>GG>>>> getChResultSet():::::" 
//     + sFirstExistOrdKeyValue + " / " + sReadVector + " / " + bIncludeKey);
      openQuery(sFirstExistOrdKeyValue, sReadVector, bIncludeKey);
      try {
         myChRs.beforeFirst();
         return myChRs;
      } catch (SQLException ex) {
         Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public ResultSet getXResultSet(String id) { 
      try {
         myXPs.setInt(1, Integer.parseInt(id));
         myXRs = myXPs.executeQuery(); // otvorenie dotazu
         myXRs.beforeFirst();  
         return myXRs;
      } catch (SQLException ex) {
         Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   // zistuje ci ordKeyVal patri prvemu riadku v tabulke podla aktualneho triedenia
   public boolean isFirstRow(String sKey, String sKeyVal) {
      try {
         myFRs = myFPs.executeQuery(); // otvorenie dotazu
         if (myFRs.next()) {
             return myFRs.getString(sKey).equals(sKeyVal);
         }
      } catch (SQLException ex) {
         Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
      }
      return false;
   }
   
   // zistuje ci ordKeyVal patri poslednemu riadku v tabulke podla aktualneho triedenia
   public boolean isLastRow(String sKey, String sKeyVal) {
      try {
       // krn.OutPrintln("sLQueryFullStatement= " + sLQueryFullStatement);
        myLRs = myLPs.executeQuery(); // otvorenie dotazu
         if (myLRs.next()) {
      //  krn.OutPrintln("2sLQueryFullStatement= " + sLQueryFullStatement);
             return myLRs.getString(sKey).equals(sKeyVal);
         }
      } catch (SQLException ex) {
         Logger.getLogger(XQuery.class.getName()).log(Level.SEVERE, null, ex);
      }
      return false;
   }

   public boolean isQryEmpty() {
      return bIsQryEmpty;
   }
   
   public void invalidate() {
        bIsQryOpened = false;
        lastRebuildParams = "";        
   }

   
}
