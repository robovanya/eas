/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.cube;

import eoc.calendar.CalendarCellTblModel;
import eoc.calendar.CalendarEvent;
import eoc.calendar.CalendarOwner;
import eoc.calendar.CalendarTableColumn;
import eoc.calendar.Stime;
import eoc.database.DBconnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import system.FnEaS;
import system.Kernel;

/**
 *  Objekt na dolovanie udajov z casovej kocky 
 *  podla zadanych hranicnych hodnot
 *
 * @author rvanya
 */
public class CubeMiner {

    eoc.calendar.CalendarOwner myOwner;
    Calendar calFirstDay;
    Calendar calLastDay;
    DBconnection myCn;
    Kernel krn;
    ArrayList<CalendarEvent> calEvtList;
    JTable myTable;
    DefaultTableModel myTableModel;
    DefaultTableColumnModel myColumnModel;
    boolean bVerbose = false;
    private String sDimension = null; //moze mat hodnotu HourDay alebo DayMonth

    public void setVerbose(boolean bVbs) {
        bVerbose = bVbs;     
    }
    
    public CubeMiner(Kernel kr, DBconnection cn,String sDim) {
        this.calEvtList = new ArrayList<>();
        krn  = kr;
        myCn = cn;
        if (sDim != null) {
            setsDimension(sDim);
        }
    }
    
    public boolean setsDimension(String sDim) {
           if (!(sDim.equals("HourDay") || sDim.equals("DayMonth") || sDim.equals("Browser"))) {
               krn.Message("E", "Podporované typy dimenzií: HourDay, DayMonth a Browser", "Nepodporovaný typ dimenzie !");
               sDimension = null;
               return false;
           }
           else sDimension = sDim;
        return true;
    }
    
    public String getsDimension() {
        return sDimension;
    }
    
    public void reloadData() {
        loadData(myOwner,calFirstDay,calLastDay);
    }
   
    public void refresh() {
        loadData(myOwner,calFirstDay,calLastDay);
        if (sDimension.equals("HourDay")) expandToHourDayTable(myTable);
        if (sDimension.equals("DayMonth")) expandToDayMonthTable(myTable);
    }

    
    public void loadData(CalendarOwner ownr, Calendar fromDate,Calendar toDate) {
        myOwner     = ownr;
        calFirstDay = fromDate;
        calLastDay  = toDate;
       if (bVerbose) {
           System.out.println("cubeMinner:loading_FROMDT:" + FnEaS.calToStr(calFirstDay, "yyyy-MM-dd"));
           System.out.println("cubeMinner:loading_TODT:" + FnEaS.calToStr(calLastDay, "yyyy-MM-dd"));
        }
        Integer accountID = myOwner.getId_eas_calAccount();
        System.out.println("cubeMinner:loading_FORID:" + accountID);
        String odDt = FnEaS.calToStr(calFirstDay, "yyyy-MM-dd");
        String doDt = FnEaS.calToStr(calLastDay, "yyyy-MM-dd");
        String sqlStm = "";
        if (accountID > 0)
            sqlStm = "SELECT list(id_eas_calEvt ORDER BY d_od_date, c_od_time)"
                + " as listOfIDs FROM eas_calEvt WHERE id_eas_calAccount = " 
                + accountID
                /* vadna podmienka do 2016-11-09
                + " and (d_od_date >= '" + odDt + "'"
                + " and d_do_date <= '" + doDt + "')"
                */
                /* vadna podmienka aj potom :o)
                + " and (d_od_date <= '" + doDt + "'"
                + " and d_do_date >= '" + odDt + "')"
                */
                + " and (d_od_date <= '" + doDt + "'"
                + " and d_do_date >= '" + odDt + "')";
        else // skupinovy objekt - accountID by mal byt -9999
            sqlStm = "SELECT list(id_eas_calEvt ORDER BY d_od_date, c_od_time)"
                + " as listOfIDs FROM eas_calEvt WHERE id_eas_calAccount IN (" 
                + myOwner.getOwnerPozn()
                /* vadna podmienka do 2016-11-09
                + " and (d_od_date >= '" + odDt + "'"
                + " and d_do_date <= '" + doDt + "')"
                */
                /* vadna podmienka aj potom :o)
                + " and (d_od_date <= '" + doDt + "'"
                + " and d_do_date >= '" + odDt + "')"
                */
                + ") AND ((d_od_date <= '" + doDt + "'"
                + " AND d_do_date >= '" + odDt + "'))";

//        evidentne je nieco s tym dotazom, co nevidim
        if (accountID < 0)
            System.out.println("CUBEMINER_LoadDataQRY:" + sqlStm);
        
        Object[][] oo = krn.SQLQ_getQueryResultSetAsArray(myCn.getConn(), sqlStm, false);
        String ids = oo[0][0].toString();
        String[] idy = ids.split(",");
        
        calEvtList.clear(); // mazanie predoslych nacitanych udajov
        if (ids.length() > 0) {
        for (int i = 0; i < idy.length; i++) {
            if (bVerbose) System.out.println("Adding event numbverr: " + i + " idIS:" + idy[i]);
            
            CalendarEvent evt = new CalendarEvent(krn, myCn,null,null,Integer.parseInt(idy[i]));
            //evt.listValues();
            //evt.setEventId(Integer.parseInt(idy[i]));
//            System.out.println("Adding event: " + evt.getProperty("c_nazov_ulohy"));
            calEvtList.add(evt);
        }
        } // if (idy.length > 0)
    } // public void loadData(...)
    
    // mazanie dat v bunkach s calCellTblModel- Objektmi 
    public void clearCellModels() {
       for (int rw = 0; rw < myTableModel.getRowCount(); rw++) {
       for (int cl = 0; cl < myColumnModel.getColumnCount(); cl++) {
           Object o = myTableModel.getValueAt(rw, cl);
           if (o instanceof CalendarCellTblModel) {
                CalendarCellTblModel cmdl = (CalendarCellTblModel) o;
                cmdl.setRowCount(0);
                myTableModel.setValueAt(o,rw, cl);
           }
       } // for (int cl = 0 ... 
       } // for (int rw = 0 ...
    } // public void clearCellModels() {
    
    public void expandToTable(JTable tbl) {
     if (sDimension.equals("HourDay")) expandToHourDayTable(tbl);
     if (sDimension.equals("DayMonth")) expandToDayMonthTable(tbl);
     if (sDimension.equals("Browser")) expandToBrowserTable(tbl);
    }

    public void expandToBrowserTable(JTable tbl) {
       myTable = tbl;
       myTableModel  = (DefaultTableModel) myTable.getModel();
       myColumnModel = (DefaultTableColumnModel) myTable.getColumnModel();
       myTableModel.setRowCount(0); // mazanie starych dat

       // virtualna mapa rozlozenia udalosti (na hodinu max 20 udalosti) 
       for (CalendarEvent curEvt: calEvtList) {
           Object[] oRow = new Object[myTable.getColumnCount()];
           oRow[0] = curEvt; // ulozi sa len sem renderer to ma implementovane
      /*     oRow[1] = curEvt;
           oRow[2] = curEvt;
           oRow[3] = curEvt;
           oRow[4] = curEvt;
           oRow[5] = curEvt;*/
           ((DefaultTableModel) myTable.getModel()).addRow(oRow);
           /*
           Calendar oddt = (Calendar) curEvt.getProperty("d_od_date");
           Calendar dodt = (Calendar) curEvt.getProperty("d_do_date");
           oddt = FnEaS.setTimeToNull(oddt);
           dodt = FnEaS.setTimeToNull(dodt);
           Stime odtm = new Stime(curEvt.getProperty("c_od_time").toString());
           Stime dotm = new Stime(curEvt.getProperty("c_do_time").toString());
           int odHour = odtm.getHour();
           //int odMin  = odtm.getMin(); zatial netreba
           int doHour = dotm.getHour();
           */
           //int doMin  = dotm.getMin(); zatial netreba
           // citanie po stlpcoch (columns)
           /*
           for (int cd = 1; cd < myColumnModel.getColumnCount(); cd++) {
               Calendar tempCal = ((CalendarTableColumn) myTable.getColumnModel().getColumn(cd)).myDate;
               tempCal = FnEaS.setTimeToNull(tempCal);
               if (bVerbose) {
               System.out.println("TESTINNG__COLL_FOR_HourDay_Dimesion:" + cd + " TCLSS:" 
                       + FnEaS.calToStr(oddt, "dd.MM.yyyy") + " >= "  
                       + FnEaS.calToStr(tempCal, "dd.MM.yyyy") + " <= "  
                       + FnEaS.calToStr(dodt, "dd.MM.yyyy")  
                       );
               }
               if ((tempCal.compareTo(oddt) >= 0)
                   && (tempCal.compareTo(dodt) <= 0)   ) {      
               for (int ch = odHour; ch <= doHour; ch++) {
                    Object o = myTableModel.getValueAt(ch, cd);
        if (bVerbose) System.out.println("WRITING_TO_CELL:" + cd + " ==> " + FnEaS.calToStr(tempCal, "dd.MM.yyyy"));
                    if (o instanceof CalendarCellTblModel) {
                        Object[] oo = new Object[3];
                        oo[0] = "";// UP
                        oo[1] = ""; //DOWN
                        oo[2] = curEvt;
                        CalendarCellTblModel cmdl = (CalendarCellTblModel) o;
                        cmdl.addRow(oo);
                    } // if (o instanceof CalendarCellTblModel) {
               } // for (int ch = odHour; ch <= doHour; ch++) {
               } // if ((tempCal.compareTo(calFirstDay) >= 0) ...
           } // for (int cd = 1; cd < myTableModel.getColumnCount() ...
           */
       }

       myTableModel.fireTableDataChanged();
       
    } // public void expandToHourDayTable(...)
    
    public void expandToHourDayTable(JTable tbl) {
       myTable = tbl;
       myTableModel  = (DefaultTableModel) myTable.getModel();
       myColumnModel = (DefaultTableColumnModel) myTable.getColumnModel();

       clearCellModels(); // mazanie dat v bunkach s calCellTblModel- Objektmi 

       int rows = myTableModel.getRowCount() + 1; // 0-element je hlavicka riadku
       int cols = myColumnModel.getColumnCount() + 1; // 0-element je hlavicka riadku

       // virtualna mapa rozlozenia udalosti (na hodinu max 20 udalosti) 
       //CalendarEvent[][][] evtArray = new CalendarEvent[rows][cols][20];
       for (CalendarEvent curEvt: calEvtList) {
           Calendar oddt = (Calendar) curEvt.getProperty("d_od_date");
           Calendar dodt = (Calendar) curEvt.getProperty("d_do_date");
           oddt = FnEaS.setTimeToNull(oddt);
           dodt = FnEaS.setTimeToNull(dodt);
           Stime odtm = new Stime(curEvt.getProperty("c_od_time").toString());
           Stime dotm = new Stime(curEvt.getProperty("c_do_time").toString());
           int odHour = odtm.getHour();
           //int odMin  = odtm.getMin(); zatial netreba
           int doHour = dotm.getHour();
           //int doMin  = dotm.getMin(); zatial netreba
           // citanie po stlpcoch (columns)
           for (int cd = 1; cd </*=*/ myColumnModel.getColumnCount(); cd++) {
               Calendar tempCal = ((CalendarTableColumn) myTable.getColumnModel().getColumn(cd)).myDate;
               tempCal = FnEaS.setTimeToNull(tempCal);
               if (bVerbose) {
               System.out.println("TESTINNG__COLL_FOR_HourDay_Dimesion:" + cd + " TCLSS:" 
                       + FnEaS.calToStr(oddt, "dd.MM.yyyy") + " >= "  
                       + FnEaS.calToStr(tempCal, "dd.MM.yyyy") + " <= "  
                       + FnEaS.calToStr(dodt, "dd.MM.yyyy")  
                       );
               }
               if ((tempCal.compareTo(oddt) >= 0)
                   && (tempCal.compareTo(dodt) <= 0)   ) {      
               for (int ch = odHour; ch <= doHour; ch++) {
                    Object o = myTableModel.getValueAt(ch, cd);
        if (bVerbose) System.out.println("WRITING_TO_CELL:" + cd + " ==> " + FnEaS.calToStr(tempCal, "dd.MM.yyyy"));
                    if (o instanceof CalendarCellTblModel) {
                        Object[] oo = new Object[3];
                        oo[0] = ""/*UP*/;
                        oo[1] = ""/*DOWN*/;
                        oo[2] = curEvt;
                        CalendarCellTblModel cmdl = (CalendarCellTblModel) o;
                        cmdl.addRow(oo);
                    } // if (o instanceof CalendarCellTblModel) {
               } // for (int ch = odHour; ch <= doHour; ch++) {
               } // if ((tempCal.compareTo(calFirstDay) >= 0) ...
           } // for (int cd = 1; cd </*=*/ myTableModel.getColumnCount() ...
       }

       myTableModel.fireTableDataChanged();
       
    } // public void expandToHourDayTable(...)

    public void expandToDayMonthTable(JTable tbl) {
       myTable = tbl;
       myTableModel  = (DefaultTableModel) myTable.getModel();
       myColumnModel = (DefaultTableColumnModel) myTable.getColumnModel();

       clearCellModels(); // mazanie dat v bunkach s calCellTblModel- Objektmi 

       int rows = myTableModel.getRowCount() + 1; // 0-element je hlavicka riadku
       int cols = myColumnModel.getColumnCount() + 1; // 0-element je hlavicka riadku

       // citanie virtualnej mapy rozlozenia udalosti (na hodinu max 20 udalosti) 
       for (CalendarEvent curEvt: calEvtList) {
           Calendar oddt = (Calendar) curEvt.getProperty("d_od_date");
           Calendar dodt = (Calendar) curEvt.getProperty("d_do_date");
           oddt = FnEaS.setTimeToNull(oddt);
           dodt = FnEaS.setTimeToNull(dodt);
           Calendar tempCal = Calendar.getInstance();
           tempCal = FnEaS.setTimeToNull(tempCal);
           tempCal.set(oddt.get(Calendar.YEAR),0,1); // prvy den v roku
           // citanie mesiacov po stlpcoch (columns)
           for (int mes = 0; mes <= 11; mes++) {
           tempCal.set(oddt.get(Calendar.YEAR),mes,1); // prvy den v roku
               if (bVerbose && mes < 3) {
               System.out.println("TESTINNG__COLL_FOR_DayMonth_Dimesion:" + mes + " TCLSS:" 
                       + FnEaS.calToStr(oddt, "dd.MM.yyyy") + " >= "  
                       + FnEaS.calToStr(tempCal, "dd.MM.yyyy") + " <= "  
                       + FnEaS.calToStr(dodt, "dd.MM.yyyy")  
                       );
               }
               
               for (int den = 1; den <= tempCal.getActualMaximum(Calendar.DATE); den++) {
                tempCal.set(oddt.get(Calendar.YEAR), mes, den);
               if ((tempCal.compareTo(oddt) >= 0)
                   && (tempCal.compareTo(dodt) <= 0)   ) {      
                    Object o = myTableModel.getValueAt(den-1, mes+1);
                    if (bVerbose) {
                        System.out.println("WRITING_TO_CELL:" + mes + " ==> " 
                                     + FnEaS.calToStr(tempCal, "dd.MM.yyyy"));
                    }
                    if (o instanceof CalendarCellTblModel) {
                        Object[] oo = new Object[3];
                        oo[0] = ""/*UP*/;
                        oo[1] = ""/*DOWN*/;
                        oo[2] = curEvt;
                        CalendarCellTblModel cmdl = (CalendarCellTblModel) o;
                        cmdl.addRow(oo);
                    } // if (o instanceof CalendarCellTblModel) {
               } // for (int ch = odHour; ch <= doHour; ch++) {
               } // if ((tempCal.compareTo(calFirstDay) >= 0) ...
           } // for (int cd = 1; cd </*=*/ myTableModel.getColumnCount() ...
       }

       myTableModel.fireTableDataChanged();
       
    } // public void expandToDayMonthTable(...)
    
}
