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
import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.text.SimpleDateFormat;
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
public class Pnl_brwHourDay extends Pnl_calendarTable {

    // konstruktor
    public Pnl_brwHourDay() {
        super();
        firstColWidth = 70;
        minColWidth = 200;

        setDimensionBoundsFor(calCurrentToday);
        displayedValueTypeChanged("Text");
    }    // create an empty dimension and an CubeMiner object if necessary

    @Override
    public String setDimensionBoundsFor(Calendar cal) {
        calCurrentToday = (Calendar) cal.clone();
        calFirstDay = (Calendar) calCurrentToday.clone();
        calLastDay  = (Calendar) calCurrentToday.clone();

        displayedDays = 1;
        
        return "";
    }

    @Override
    public String setCurrentToday(Object oSender,Object newToday) {
        calCurrentToday = (Calendar) newToday;
        setDimensionBoundsFor(calCurrentToday);
        createDimensionFor(calCurrentToday,"Hodina","H","D");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        return "";
    }


    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        super.initialize(kr, cX); //To change body of generated methods, choose Tools | Templates.
        // urcenie rozmerov a ukazovatelov tabulky rezu
        createDimensionFor(calCurrentToday,"Hodina","H","D");

        return "";
    }
    
    
    public void createDimensionFor(Calendar calTod,
                                   String Head,String diagonalMJ, String verticalMj) {
        if (cubeMinner == null) cubeMinner = new CubeMiner(krn, MyCn,"HourDay"); // pre istotu
        else cubeMinner.setsDimension("HourDay");
        setDimensionBoundsFor(calTod);
        
        myTableModel.setColumnCount(0);
        myTableModel.setRowCount(0);
        myColumnModel = new DefaultTableColumnModel();
        myTable.setColumnModel(myColumnModel);

        myTableModel.addColumn("Hodina");
        myTableModel.addColumn("Deň");
        numDimCols = 1;
        Object[] rowData = new Object[2];
        for (int i = 0; i < 24; i++) {
            rowData[0] = Integer.toString(i) + ":00";
            rowData[1] = "";
            myTableModel.addRow(rowData);
        }
        myTable.setModel(myTableModel);
        minRowHeight = myTable.getRowHeight();
        numDimRows = myTableModel.getRowCount();
        
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
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");	
	String strDate;
        for (int d = 1; d <= displayedDays; d++) {
            if (d > 1) calTemp.add(Calendar.DAY_OF_MONTH,1); // zvysenie datumu o den
           currDay = EOC_graf.getDayNameOfWeek(calTemp.getTime(), 8/* pocet pismen */);
           strDate = sdf.format(calTemp.getTime());
           /*
           currColor = (currDay.startsWith("so")?"#4AA02C":"#827839");
           currColor = (currDay.startsWith("ne")?"#FFA62F":"#827839");
           */
           currColor = "#827839";
           if (currDay.startsWith("so")) currColor = "#4AA02C";
           if (currDay.startsWith("ne")) currColor = "#FFA62F";
           
            headers[d] = "<html><center><B>" + strDate + "<br>"
                       + "<font color=\"" + currColor + "\">" + currDay 
                       + "</font></B></center></br></html>"; 
            CalendarTableColumn ctc = new CalendarTableColumn();
            ctc.setHeaderValue(headers[d]);
            ctc.myDate = (Calendar) calTemp.clone();
            ctc.setModelIndex(d);
            myColumnModel.addColumn(ctc);
            myTableModel.addColumn(headers[d]);
            ctc.setCellRenderer(new CalendarCellRenderer((Pnl_calendarTable) this));
        }
    DefaultTableModel dtm = new DefaultTableModel();
        DefaultTableCellRenderer r;
        r = (DefaultTableCellRenderer)myTable.getTableHeader().getDefaultRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        myTable.getTableHeader().setPreferredSize(new Dimension(myTable.getColumnModel().getTotalColumnWidth(),headerHeight));        
        myColumnModel.getColumn(0).setPreferredWidth(firstColWidth);
        // pridanie riadkov dimenzii
        myTableModel.setRowCount(0);
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
        myTable.setColumnModel(myColumnModel);
        myTable.setModel(myTableModel);
        numDimRows = myTableModel.getRowCount();
        myTable.setVisible(true);
        myTable.revalidate();
        myTable.repaint();
        showLabel();
        fireScrollpaneResized();
    } // public void createDimensionFor

    public void showLabel() {
        String str = 
                EOC_graf.getDayNameOfWeek(calCurrentToday.getTime(), 8/* pocet pismen */)
                + " - " + FnEaS.calToStr(calCurrentToday,"dd.MM.yyyy") 
                //+ FnEaS.getMonthName(calCurrentToday.get(Calendar.MONTH), 20)
                ;
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
        int currDay = calCurrentToday.get(Calendar.DATE);
        Calendar newDay = (Calendar) calCurrentToday.clone();
        newDay.set(Calendar.DATE, currDay - 1);
        createDimensionFor(newDay,"Hodina","H","D");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        String retval = calendarTableGroup.sendMessageToGroup(this,"setCurrentToday",calCurrentToday);
        return "";
    }

    @Override
    public String goToNext() {
        int currDay = calCurrentToday.get(Calendar.DATE);
        Calendar newDay = (Calendar) calCurrentToday.clone();
        newDay.set(Calendar.DATE, currDay + 1);
        createDimensionFor(newDay,"Hodina","H","D");
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
        cubeMinner.expandToTable(myTable);
        String retval = calendarTableGroup.sendMessageToGroup(this,"setCurrentToday",calCurrentToday);
        return "";
    }

    public String goToOwner(EOC_message eocMsg /* String ownerObj */) {
        myCalendarOwner = (CalendarOwner) eocMsg.getParametersAsObject();
        bOwnerGroupMode = (myCalendarOwner.getId_eas_calAccount() < 0);
        
        cubeMinner.loadData(myCalendarOwner,calFirstDay,calLastDay);
       // myTable.setModel(myTableModel);
       // myTable.setColumnModel(myColumnModel);
        cubeMinner.expandToTable(myTable);
        myTable.revalidate();
        myTable.repaint();
        return "";
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
