/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

import eoc.database.DBconnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class CalendarEvent {
    
    Kernel krn;
    DBconnection myCn;
    CalendarOwner myOwner;
    TimeChunk myTimeChunk;
    Integer   evtId;
    HashMap<String, Object> hmpMyEvent;
    public CalendarEvent(Kernel kr, DBconnection c, CalendarOwner ownr,
                         TimeChunk tch, Integer eId) {
        hmpMyEvent = new HashMap<String, Object>();
        krn = kr;
        myCn = c;
        myOwner = ownr;
        myTimeChunk = tch;
        setEventId(eId);
    }
    public void setEventId(Integer eId) {
            // vytvorenie tabulky, pokial este nema strukturu DB-tabulky
////        System.out.println("SETV_AA");
            if (hmpMyEvent.size()==0)
//                hmpMyEvent = krn.SQL_getSqlTableAsHashtable(myCn, "eas_calEvt");
                hmpMyEvent = myCn.getDbDriver().SQL_getSqlTableAsHashMap(myCn.getConn(), "eas_calEvt");
////        System.out.println("SETV_BB");
        if (eId == null) {
            // vznikne prazdna tabulka so spravnymi datovymi typmi objektov 
////            System.out.println("HTTTA:" + (hmpMyEvent==null) + " " + (myCn==null));
            // vytvorenie tabulky, pokial este nema strukturu DB-tabulky
//            if (hmpMyEvent.size()==0)
//                hmpMyEvent = krn.SQL_getSqlTableAsHashtable(myCn, "eas_calEvt");
////            System.out.println("HTTTB:" + hmpMyEvent==null);
            if (myTimeChunk != null) putTimeChunkToHashTable();
            listHashMap(hmpMyEvent);
            // mazu sa vsetky hodnoty objektu

        }
        else {
            // vznikne tabulka so spravnymi datovymi typmi objektov aj s hodnotami 
            // nacitaju sa hodnoty z databazy
////            System.out.println("CalEvent_setOeventID_A:" + hmpMyEvent.size());
            myCn.getDbDriver().SQL_getSqlRowToHashMap(myCn.getConn(), hmpMyEvent,
                   "select * from eas_calEvt where id_eas_calEvt = " + eId);
////            System.out.println("CalEvent_setOeventID_B:" + hmpMyEvent.size());
        }
    }

    private void putTimeChunkToHashTable() {
            hmpMyEvent.put("d_od_date", myTimeChunk.getOdDate());
            hmpMyEvent.put("d_do_date", myTimeChunk.getDoDate());
            hmpMyEvent.put("c_od_time", myTimeChunk.getOdTime());
            hmpMyEvent.put("c_do_time", myTimeChunk.getDoTime());
        
    }
    
    public void listHashMap(HashMap ht) {
    System.out.println(">>>>LISTINGGG Hashtable entries:");
    for (Object key : ht.keySet()) {
        String sKey = key.toString();
        Object oVal = ht.get(sKey);
        String sVal = null;
        if (oVal instanceof Calendar) {
            Calendar cal = (Calendar) oVal;
//            sVal = krn.calToStr(cal,"yyyy-MM-dd");
            sVal = krn.calToStr(cal,"dd.MM.yyyy");
            
        }
        else sVal = oVal.toString();
        System.out.println(key + ":" + sVal);
    }
    }

    public void listValues() {
        listHashMap(hmpMyEvent);
        
    }
    
    public String committxn(String sTxnType) {
        sTxnType = sTxnType.toLowerCase();
        System.out.println("COMMITBEGINFORTXN: " + sTxnType);
        String stm = "";
        if (sTxnType.equals("update")) {
                stm = ""; // docasne - volat krn-funkciu !!!              
////            stm = stm + " where " + sMasterKey + " = "  + currentTblId.toString();
////            stm = "update " + sMasterTbl + " set " + stm;
            //krn.OutPrintln("stm.toString()" + stm.toString());
            stm = krn.getUpdateStatement(myCn.getConn(), "eas_calEvt", hmpMyEvent,
                                        true /* ignoreAutomatedFields*/);
            System.out.println("COMMITINGUPDATESTM: " + stm);
            krn.OutPrintln("committxn->UPDATE-STM=" + stm);
        }
        
        if (sTxnType.equals("add") || sTxnType.equals("copy") ) {

            stm= krn.getInsertStatement(myCn.getConn(), "eas_calEvt", hmpMyEvent,
                                        true /* ignoreAutomatedFields*/);
            System.out.println("COMMITINGINSTM: " + stm);
            krn.OutPrintln("committxn->INSERT-STM=" + stm);
        
        }
        
        if (sTxnType.equals("delete")) {
            evtId = Integer.parseInt(hmpMyEvent.get("id_eas_calEvt").toString());
            stm =  "DELETE eas_calEvt WHERE eas_calEvt.id_eas_calEvt = " + evtId;
            System.out.println("COMMITINGINSTM: " + stm);
            listValues();
            krn.OutPrintln("committxn->DELETE-STM=" + stm);
        
        }
        
        try {    
           ResultSet rss; // pomocny resultset
////           PreparedStatement pss = MyCn.prepareStatement(stm);
           Statement stmt = myCn.getConn().createStatement();
////           pss.execute(); 
           System.out.println("EXECUUTIIING: " + stm);
           stmt.executeQuery(stm);
////           stmt.close(); // ????????    
           myCn.getConn().commit();
           krn.OutPrintln("TRANSACTION COMMITTED");
           if (sTxnType.equals("add") || sTxnType.equals("copy") ) {
              if (myCn.getDbDriver().getDBtype(myCn.getConn()).equals("SYBASE")) {
////                 pss = MyCn.prepareStatement("select @@identity as sCurrKey");
                 stm = "select @@identity as sCurrKey";
              }
              if (myCn.getDbDriver().getDBtype(myCn.getConn()).equals("POSTGRES")) {
////                 pss = MyCn.prepareStatement("SELECT currval('tblmasterid') as sCurrKey");
                 stm = "SELECT currval('tblmasterid') as sCurrKey";
              }
              ResultSet myrss = stmt.executeQuery(stm);
              myrss.next();
              evtId = myrss.getInt("sCurrKey");
              
           }
           else if (sTxnType.equals("update")) { 
//              updatedTblId = currentTblId;
//              OpenQuery(); 
//              displayRow();
           }
           else if (sTxnType.equals("delete")) { 
//              updatedTblId = currentTblId;
//              OpenQuery(); 
//              displayRow();
               krn.Message("dleting" + evtId);
           }

////           System.out.println("Z KEREJ PICE JE updatedTblId=" + updatedTblId + " " + sTxnType
////           + " TU JE STM:" + stm); // ID
////          ODPOVED:  V eas_uni_cis ma AUTOINCREMENT omylom zapnuty 
////          aj stlpec id_eas_uni_cis_def
           krn.debugOut(this,5,"updated_evtId=" + evtId); // ID
////           pss.close(); // ????????    
           stmt.close(); // ????????    
           myCn.getConn().commit();
        } 
        catch (SQLException ex) {
           String errMsg = ex.getMessage();
           errMsg = errMsg.replace("[Sybase][JDBC Driver][SQL Anywhere]RAISERROR executed:", "");
           krn.Message(this,"E", errMsg,"Chyba pri zápise: " );
           krn.OutPrintln("Chyba pri zápise: " + "UPDATE_ERROR -> " +  ex.getMessage());
           return "UPDATE_ERROR" +  ex.getMessage();
        }                                              
        
        return "";
    }
    
    public Object getProperty(String propName) {
        Object o = null;
        if (hmpMyEvent.containsKey(propName)) {
            o = hmpMyEvent.get(propName);
        }
        return o;
    }
    
    public String getOwnerName() {
        String rv;
        Object o = hmpMyEvent.get("id_eas_calAccount");
        rv = "SELECT c_owner_pozn as retval FROM eas.eas_calAccount WHERE id_eas_calAccount = " + o.toString();
        rv = myCn.getDbDriver().SQL_getQueryAsValue(myCn.getConn(), rv, true).toString();
        return rv;
    }

}
