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
import java.awt.Dimension;
import java.sql.Connection;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import system.FnEaS;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_brwDayMonthYear extends Pnl_calendarTable {

    // konstruktor
    public Pnl_brwDayMonthYear() {
        headerHeight = 32;
        firstColWidth = 70;
        minColWidth = 25;

        setDimensionBoundsFor(calCurrentToday);
        displayedValueTypeChanged("Pocet");
    }

    @Override
    public String setDimensionBoundsFor(Calendar cal) {
        calCurrentToday = (Calendar) cal.clone();
        calFirstDay = (Calendar) calCurrentToday.clone();
        calLastDay  = (Calendar) calCurrentToday.clone();
        calFirstDay.set(Calendar.DAY_OF_MONTH,1);
        calFirstDay.set(Calendar.MONTH,0);
        monthMaxDay = 31; // maximalny pocet dni v mesiacoch
        displayedDays = 12;
        calLastDay.set(Calendar.DAY_OF_MONTH,31);
        calLastDay.set(Calendar.MONTH,11);
        return "";
    }

        // create an empty dimension and an CubeMinner object if necessary
    public void createDimensionFor(Calendar calTod, 
                                   String head,String diagMJ, String vertMj) {
        if (cubeMinner == null) cubeMinner = new CubeMiner(krn, MyCn,"DayMonth"); // pre istotu
        else cubeMinner.setsDimension("DayMonth");
        setDimensionBoundsFor(calTod);
        System.out.println(FnEaS.calToStr(calFirstDay, null) + " " + FnEaS.calToStr(calCurrentToday, null) + " " + FnEaS.calToStr(calLastDay, null));
        myTableModel.setColumnCount(0);
        myTableModel.setRowCount(0);
        myColumnModel = new DefaultTableColumnModel();
        myTable.setColumnModel(myColumnModel);
        myTable.getTableHeader().setReorderingAllowed(false);       

        String headers[] = new String[13];
        headers[0] = "<html><center><B>Deň</B></center></html>";
            CalendarTableColumn ctc0 = new CalendarTableColumn();
            ctc0.setHeaderValue(headers[0]);
            ctc0.myDate = null;
            ctc0.setCellRenderer(new CalendarCellRenderer((Pnl_calendarTable) this));
            ctc0.setModelIndex(0);
            myColumnModel.addColumn(ctc0);
            myTableModel.addColumn(ctc0);
        
        // pridanie stlpcov dimenzii
        Calendar calTemp = (Calendar) calFirstDay.clone(); //pomocna premenna
        String currMonth = ""; //pomocna premenna
        String currColor = ""; //pomocna premenna
        
        // pridanie stlpcov mesiacov za aktualny rok
        int curYear = calFirstDay.get(Calendar.YEAR);
        for (int mnth = 0; mnth <= 11; mnth++) {
           currMonth = FnEaS.getMonthName(mnth, 3/* pocet pismen */);

           currColor = "#827839";
//           if (currDay.startsWith("so")) currColor = "#4AA02C";
//           if (currDay.startsWith("ne")) currColor = "#FFA62F";
           
            headers[mnth + 1] = "<html><center><B>" + Integer.toString(curYear) + "<br>"
                       + "<font color=\"" + currColor + "\">" + currMonth 
                       + "</font></B></center></br></html>"; 
            CalendarTableColumn ctc = new CalendarTableColumn();
            ctc.setHeaderValue(headers[mnth + 1]);
            ctc.myDate = (Calendar) calTemp.clone();
            ctc.setModelIndex(mnth + 1);
            myTable.getColumnModel().addColumn(ctc);
            myTableModel.addColumn(headers[mnth + 1]);
        }
        
        DefaultTableModel dtm = new DefaultTableModel();
        DefaultTableCellRenderer r;
        r = (DefaultTableCellRenderer)myTable.getTableHeader().getDefaultRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        dtm.setColumnIdentifiers(headers);
        myTable.getTableHeader().setPreferredSize(new Dimension(myTable.getColumnModel().getTotalColumnWidth(),headerHeight));        
        myTable.getColumnModel().getColumn(0).setPreferredWidth(firstColWidth);
        
        // pridanie riadkov dimenzie po dnoch
        Object[] rowData = new Object[displayedDays + 1];
        for (int i = 1; i <= 31; i++) {
           rowData[0] = Integer.toString(i) + ".";
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

    public void showLabel() {
        String str = 
                   "Rok: " + calCurrentToday.get(Calendar.YEAR) + " - "
                   + " cielový mesiac:" + FnEaS.getMonthName(calCurrentToday.get(Calendar.MONTH), 20)
                   //+ calCurrentToday.get(Calendar.WEEK_OF_YEAR) + ". týždeň"
                   ;
        setDimensionLabel(str);
    }

    @Override
    public String setCurrentToday(Object oSender,Object newToday) {
        calCurrentToday = (Calendar) newToday;
        setDimensionBoundsFor(calCurrentToday);
        createDimensionFor(calCurrentToday,"Deň","D","M");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        return "";
    }
    
    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        super.initialize(kr, cX); //To change body of generated methods, choose Tools | Templates.
        // cubeMinner.setVerbose(true);
        // urcenie rozmerov a ukazovatelov tabulky rezu
        createDimensionFor(calCurrentToday,"Deň","D","M");
        return "";
    }

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
        int curYear = calCurrentToday.get(Calendar.YEAR);
        calCurrentToday.set(Calendar.YEAR, curYear - 1);
        setDimensionBoundsFor(calCurrentToday);
        createDimensionFor(calCurrentToday,"Deň","D","M");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        String retval = calendarTableGroup.sendMessageToGroup(this,"setCurrentToday",calCurrentToday);

        return "";
    }

     @Override
    public String goToNext() {
        int curYear = calCurrentToday.get(Calendar.YEAR);
        calCurrentToday.set(Calendar.YEAR, curYear + 1);
        setDimensionBoundsFor(calCurrentToday);
        createDimensionFor(calCurrentToday,"Deň","D","M");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        String retval = calendarTableGroup.sendMessageToGroup(this,"setCurrentToday",calCurrentToday);

        return "";
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
