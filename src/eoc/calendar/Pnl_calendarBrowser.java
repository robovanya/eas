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
import java.sql.Connection;
import java.util.Calendar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import system.FnEaS;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_calendarBrowser extends Pnl_calendarTable {
    boolean bColumnsCreated; // indikuje, ci su uz stlpce vytvoremne (robi sa to iba raz)

    // konstruktor
    public Pnl_calendarBrowser() {
        super();
        bColumnsCreated = false;
        firstColWidth = 200;
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
    public void handleDoubleClick(int row, int col) {
//      CalendarEvent calEvt = ((calMenuItem) e.getSource()).myCalEvent;
      CalendarEvent calEvt = (CalendarEvent) myTable.getValueAt(row, 0);
      String calOwnr = calEvt.getProperty("c_owner").toString();
      String curUser = krn.getPermd().getCurrentUser();
      if (!calOwnr.equals(curUser)) {
          krn.Message("E", "Udalosť vytvoril: " + calOwnr 
             + "\n\nUžívateľ: " + curUser + " nemôže upravovať údaje.", "Nedostatočné práva");
          return;
      }
      /*
      krn.Message("Selected ittemm:" + ((calMenuItem) e.getSource()).getText()
      + "\nSELECTIONINDEX IS:" + i
      + "\nEVENTIDISS:" + calEvt.getProperty("id_eas_calEvt"));
      */
      // toto tu je HORIBILNA PIIICOVINA ! ...myCalEvent.myOwner ma byt nastaveny pri vzniku
      // objektu myCalEvent
      calEvt.myOwner = myCalendarOwner;
      calendarUpdater.view(calEvt, "UPDATE");
      cubeMinner.refresh();
      myTable.repaint();
    }


    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        super.initialize(kr, cX); //To change body of generated methods, choose Tools | Templates.
        // urcenie rozmerov a ukazovatelov tabulky rezu
        createDimensionFor(calCurrentToday,"Hodina","H","D");

        return "";
    }
    
    private void addColumnToTable(String cName, int idx, int minW, int maxW) {
            Calendar calTemp = (Calendar) calFirstDay.clone(); //pomocna premenna
            CalendarTableColumn ctc = new CalendarTableColumn();
            ctc.setHeaderValue(cName);
            ctc.myDate = (Calendar) calTemp.clone();
            ctc.setModelIndex(idx);
            ctc.setCellRenderer(new CalendarBrowserCellRenderer((Pnl_calendarTable) this,cName));
            ctc.setMinWidth(minW);
            ctc.setMaxWidth(maxW);
            myTable.getColumnModel().addColumn(ctc);
            myTableModel.addColumn(cName);
    }
    
    public void createDimensionFor(Calendar calTod,
                                   String Head,String diagonalMJ, String verticalMj) {
        if (cubeMinner == null) cubeMinner = new CubeMiner(krn, MyCn,"Browser"); // pre istotu
        else cubeMinner.setsDimension("Browser");

        setDimensionBoundsFor(calTod);

        if (!bColumnsCreated) {
            // myTableModel = new DefaultTableModel();
            //myColumnModel = new DefaultTableColumnModel();
            myTableModel.setColumnCount(0);
            myTableModel.setRowCount(0);
            myColumnModel = new DefaultTableColumnModel();
            myTable.setColumnModel(myColumnModel);
            myTable.getTableHeader().setReorderingAllowed(false);       

            
           addColumnToTable("Osoba", 0, 140, 290);
           addColumnToTable("Názov", 1, 200, 600);
           addColumnToTable("Začiatok", 2, 120, 120);
           addColumnToTable("Splniť do", 3, 120, 120);
           addColumnToTable("Miesto konania", 4, 200, 600);
           addColumnToTable("Termín o", 5, 100, 100 );

           CalendarTableColumn tcHodina = (CalendarTableColumn) myColumnModel.getColumn(0); //new TableColumn();
           tcHodina.setHeaderValue("<html><center><B>    Osoba</B></center></html>");
           //tcHodina.setMinWidth(90);
           //tcHodina.setMaxWidth(90);
           /*
           myTableModel.addColumn("Hodina");
           myTableModel.addColumn("Názov");
           myTableModel.addColumn("Začiatok");
           myTableModel.addColumn("Splniť do");
           myTableModel.addColumn("Miesto konania");
           myTableModel.addColumn("Termín o");
           
           myTable.setAutoCreateColumnsFromModel(true);
           myTable.setModel(myTableModel);  
           myColumnModel = (DefaultTableColumnModel) myTable.getColumnModel();
           TableColumn tcHodina = myColumnModel.getColumn(0); //new TableColumn();
           tcHodina.setHeaderValue("<html><center><B>    Hodina</B></center></html>");
           tcHodina.setMinWidth(90);
           tcHodina.setMaxWidth(90);
           tcHodina.setCellRenderer(new CalendarBrowserCellRenderer((Pnl_calendarTable) this,"Hodina"));
           myColumnModel.addColumn(tcHodina);

            TableColumn tcNazov =  myColumnModel.getColumn(1); //new TableColumn();
            tcNazov.setHeaderValue("Názov");
            tcNazov.setMinWidth(200);
            tcNazov.setCellRenderer(new CalendarBrowserCellRenderer((Pnl_calendarTable) this,"Názov"));
            myColumnModel.addColumn(tcNazov);

            TableColumn tcZaciatok =  myColumnModel.getColumn(2); //new TableColumn();
            tcZaciatok.setHeaderValue("Začiatok");
            tcZaciatok.setMinWidth(120);
            tcZaciatok.setMaxWidth(120);
            tcZaciatok.setCellRenderer(new CalendarBrowserCellRenderer((Pnl_calendarTable) this,"Začiatok"));
            myColumnModel.addColumn(tcZaciatok);

            TableColumn tcKoniec =  myColumnModel.getColumn(3); //new TableColumn();
            tcKoniec.setHeaderValue("Splniť do");
            tcKoniec.setMinWidth(120);
            tcKoniec.setMaxWidth(120);
            tcKoniec.setCellRenderer(new CalendarBrowserCellRenderer((Pnl_calendarTable) this,"Splniť do"));
            myColumnModel.addColumn(tcKoniec);

            TableColumn tcMiesto =  myColumnModel.getColumn(4); //new TableColumn();
            tcMiesto.setHeaderValue("Miesto konania");
            tcMiesto.setMinWidth(200);
            tcMiesto.setCellRenderer(new CalendarBrowserCellRenderer((Pnl_calendarTable) this,"Miesto konania"));
            myColumnModel.addColumn(tcMiesto);

            TableColumn tcTermin =  myColumnModel.getColumn(5); //new TableColumn();
            tcTermin.setHeaderValue("Termín o");
            tcTermin.setMinWidth(100);
            tcTermin.setCellRenderer(new CalendarBrowserCellRenderer((Pnl_calendarTable) this,"Termín o"));
            myColumnModel.addColumn(tcTermin);
*/
            // myTable.setColumnModel(myColumnModel);  

            numDimCols = 6;
            myTable.getTableHeader().setReorderingAllowed(false);       
            bColumnsCreated = true;
        } // if (!bColumnsCreated) {

        minRowHeight = myTable.getRowHeight();
        numDimRows = myTableModel.getRowCount();
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
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

        public String goToOwner(EOC_message eocMsg) {
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
