/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

import eoc.EOC_message;
import eoc.cube.CubeMiner;
import eoc.database.DBconnection;
import eoc.grafikon.EOC_graf;
import eoc.widgets.PObject;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import system.FnEaS;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_brwHourDayMonth extends Pnl_calendarTable {

    // konstruktor
    public Pnl_brwHourDayMonth() {
        super();
        headerHeight = 32;
        firstColWidth = 70;
        minColWidth = 25;
        
        setDimensionBoundsFor(calCurrentToday);
        displayedValueTypeChanged("Pocet");
    }

    @Override
    public String setDimensionBoundsFor(Calendar cal) {
        calCurrentToday = (Calendar) cal.clone();
        // toto clonovanie objektu cal robilo desive veci pri nastavovani 
        // dna - vznikali cudne datumi, vytvaranie pouzitim Calendar.getInstance()
        // a set-ovanie rku, mesiaca aj dna to odstranilo
        //        calFirstDay = (Calendar) cal.clone();
        //        calLastDay  = (Calendar) cal.clone();
        calFirstDay = Calendar.getInstance();
        calLastDay  = Calendar.getInstance();
        calFirstDay.set(calCurrentToday.get(Calendar.YEAR),
                        calCurrentToday.get(Calendar.MONTH), 1);
        monthMaxDay = calCurrentToday.getActualMaximum(Calendar.DATE);
        displayedDays = monthMaxDay;
        calLastDay.set(calCurrentToday.get(Calendar.YEAR),
                        calCurrentToday.get(Calendar.MONTH), monthMaxDay);
        /*
        System.out.println("SETTING_DIMENSION_BOUNDS_TO: " + FnEaS.calToStr(calCurrentToday, "dd-MM-yyyy")
        + " FROM:" + FnEaS.calToStr(calFirstDay, "dd-MM-yyyy") 
        + " TO:" + FnEaS.calToStr(calLastDay, "dd-MM-yyyy")
        + " monthMaxDayIS:" + monthMaxDay);
        */
        return "";
    }

    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        super.initialize(kr, cX); //To change body of generated methods, choose Tools | Templates.
        // urcenie rozmerov a ukazovatelov tabulky rezu
        createDimensionFor(calCurrentToday,"Hodina","H","D");

        return "";
    }
    // create an empty dimension and an CubeMinner object if necessary
    public void createDimensionFor(Calendar calTod, 
                                   String Head,String diagMJ, String vertMj) {
        if (cubeMinner == null) cubeMinner = new CubeMiner(krn, MyCn,"HourDay"); // pre istotu
        else cubeMinner.setsDimension("HourDay");
        setDimensionBoundsFor(calTod);
         /*
        calCurrentToday = calTod; 
        calFirstDay = calFir;
        calLastDay  = calLas;
        monthMaxDay = calLastDay.get(Calendar.DAY_OF_MONTH);
        displayedDays = monthMaxDay;
         */
        myTableModel.setColumnCount(0);
        myTableModel.setRowCount(0);
        myColumnModel = new DefaultTableColumnModel();
        myTable.setColumnModel(myColumnModel);
        myTable.getTableHeader().setReorderingAllowed(false);       

        String headers[] = new String[displayedDays + 1];
        headers[0] = "<html><center><B>Hodina</B></center></html>";
            CalendarTableColumn ctc0 = new CalendarTableColumn();
            ctc0.setHeaderValue(headers[0]);
            ctc0.myDate = null;
            ctc0.setCellRenderer(new CalendarCellRenderer((Pnl_calendarTable) this));
            ctc0.setModelIndex(0);
            myColumnModel.addColumn(ctc0);
            myTableModel.addColumn(ctc0);
        
        // pridanie stlpcov dimenzii
        Calendar calTemp = (Calendar) calFirstDay.clone(); //pomocna premenna
        String currDay   = ""; //pomocna premenna
        String currColor = ""; //pomocna premenna
        for (int d = 1; d <= displayedDays; d++) {
           calTemp.set(Calendar.DAY_OF_MONTH,d);
           currDay = EOC_graf.getDayNameOfWeek(calTemp.getTime(), 3/* pocet pismen */);

           currColor = "#827839";
           if (currDay.startsWith("so")) currColor = "#4AA02C";
           if (currDay.startsWith("ne")) currColor = "#FFA62F";
           
            headers[d] = "<html><center><B>" + Integer.toString(d) + "<br>"
                       + "<font color=\"" + currColor + "\">" + currDay 
                       + "</font></B></center></br></html>"; 
            CalendarTableColumn ctc = new CalendarTableColumn();
            ctc.setHeaderValue(headers[d]);
            ctc.myDate = (Calendar) calTemp.clone();
            ctc.setModelIndex(d);
            myTable.getColumnModel().addColumn(ctc);
            myTableModel.addColumn(headers[d]);
        }
        DefaultTableModel dtm = new DefaultTableModel();
        DefaultTableCellRenderer r;
        r = (DefaultTableCellRenderer)myTable.getTableHeader().getDefaultRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        dtm.setColumnIdentifiers(headers);
        myTable.getTableHeader().setPreferredSize(new Dimension(myTable.getColumnModel().getTotalColumnWidth(),headerHeight));        
        myTable.getColumnModel().getColumn(0).setPreferredWidth(firstColWidth);
        
        
        // pridanie riadkov dimenzii
        Object[] rowData = new Object[displayedDays + 1];
        for (int i = 0; i < 24; i++) {
           rowData[0] = Integer.toString(i) + ":00";
           String[] mdl = new String[3];
           mdl[0] = "UP";
           mdl[1] = "DOWN";
           mdl[2] = "calEvt";
           for (int dd = 1; dd <= displayedDays; dd++) {
            rowData[dd] = new CalendarCellTblModel(mdl,0);
           }
           myTableModel.addRow(rowData);
        }
        minRowHeight = myTable.getRowHeight();
        myTable.setModel(myTableModel);
        myTable.setColumnModel(myColumnModel);
        numDimRows = myTableModel.getRowCount();
        myTable.setVisible(true);
        myTable.repaint();
        showLabel();
        fireScrollpaneResized();
    } // public void createDimensionFor

    
    @Override
    public String goToToday() {
        Calendar newDay = Calendar.getInstance();
        createDimensionFor(newDay,"Hodina","H","D");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        String retval = calendarTableGroup.sendMessageToGroup(this,"setCurrentToday",calCurrentToday);
        return "";
    }

    @Override
    public String goToPrev() {
        int currMes = calCurrentToday.get(Calendar.MONTH);
        int currRok = calCurrentToday.get(Calendar.YEAR);
        if (currMes == 0 /* januar */) {
            currMes = 11; // december
            currRok = currRok - 1;
            calCurrentToday.set(Calendar.YEAR, currRok);
        }
        else {
            currMes = currMes - 1;
        }
        Calendar newDay = (Calendar) calCurrentToday.clone();
        newDay.set(Calendar.MONTH, currMes);
        createDimensionFor(newDay,"Hodina","H","D");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        String retval = calendarTableGroup.sendMessageToGroup(this,"setCurrentToday",calCurrentToday);
        return "";
    }

    @Override
    public String goToNext() {
        int currMes = calCurrentToday.get(Calendar.MONTH);
        int currRok = calCurrentToday.get(Calendar.YEAR);
        if (currMes == 11 /* januar */) {
            currMes = 0; // december
            currRok = currRok + 1;
            calCurrentToday.set(Calendar.YEAR, currRok);
        }
        else {
            currMes = currMes + 1;
        }
        Calendar newDay = (Calendar) calCurrentToday.clone();
        newDay.set(Calendar.MONTH, currMes);
        createDimensionFor(newDay,"Hodina","H","D");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        String retval = calendarTableGroup.sendMessageToGroup(this,"setCurrentToday",calCurrentToday);

        return "";
    }

    @Override
    public String setCurrentToday(Object oSender,Object newToday) {
        Calendar newDay = (Calendar) newToday;
        /*
       for (int rw = 0; rw < myTableModel.getRowCount(); rw++) {
       for (int cl = 0; cl < myColumnModel.getColumnCount(); cl++) {
           Object o = myTableModel.getValueAt(rw, cl);
           if (o instanceof CalendarCellTblModel) {
                CalendarCellTblModel cmdl = (CalendarCellTblModel) o;
                cmdl.setRowCount(0);
                myTableModel.setValueAt(o,rw, cl);
           }
           //else 
               //System.out.println(rw + " -- " + cl + "  COZAOBJECT" + o.toString());
       } // for (int cl = 0 ... 
       } // for (int rw = 0 ...
       myTable.setModel(myTableModel);
       */
        //QQQsetDimensionBoundsFor(calCurrentToday);
        createDimensionFor(newDay,"Hodina","H","D");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        myTable.invalidate();
        myTable.revalidate();
        myTable.repaint();

        return "";
    }


    public void showLabel() {
        String str = FnEaS.getMonthName(calCurrentToday.get(Calendar.MONTH), 20)
                   + " " + calCurrentToday.get(Calendar.YEAR);
        setDimensionLabel(str);
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 852, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 520, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

        public String goToOwner(EOC_message eocMsg) {
        myCalendarOwner = (CalendarOwner) eocMsg.getParametersAsObject();
        bOwnerGroupMode = (myCalendarOwner.getId_eas_calAccount() < 0);
        
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        myTable.setModel(myTableModel);
        myTable.setColumnModel(myColumnModel);
        cubeMinner.expandToTable(myTable);
        myTable.revalidate();
        myTable.repaint();
        return "";
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void setFont(Font font) {
        return;
        //super.setFont(font); //To change body of generated methods, choose Tools | Templates.
    }
}
