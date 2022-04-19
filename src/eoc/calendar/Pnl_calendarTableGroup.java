/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.calendar;
import eoc.EOC_message;
import eoc.IEOC_VisualObject;
import eoc.communication.CommLink;
import eoc.database.DBconnection;
import java.awt.Color;
import system.Kernel;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class Pnl_calendarTableGroup  extends eoc.widgets.PObject {
    ArrayList<Pnl_calendarTable> tableGroup;
    Class mainJointPoint; 
    Pnl_calendarTable currentRoot;
    public Pnl_calendarTableGroup() {
       super();
       this.tableGroup     = new ArrayList<>();
       this.mainJointPoint = null;
       
       initComponents();
       jTabbedPane1.setBackgroundAt(0, Color.getHSBColor(0.55f, 0.4f, 1f));
       jTabbedPane1.setBackgroundAt(1, Color.getHSBColor(0.55f, 0.4f, 0.97f));
       jTabbedPane1.setBackgroundAt(2, Color.getHSBColor(0.55f, 0.4f, 0.91f));
       jTabbedPane1.setBackgroundAt(3, Color.getHSBColor(0.55f, 0.4f, 0.88f));
       jTabbedPane1.setBackgroundAt(4, Color.getHSBColor(0.55f, 0.4f, 0.85f));
       myObjectID = this;
       jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int i = jTabbedPane1.getSelectedIndex();
                if (tableGroup.size()-1>= i) {
                //System.out.println("Tab changed to: " + i + " >> " 
                //   + FnEaS.sObjName((Pnl_calendarTable)tableGroup.get(i))
                //);
                    currentRoot = (Pnl_calendarTable)tableGroup.get(i);    
                    currentRoot.myTable.doLayout();
                    currentRoot.myTable.invalidate();
                    currentRoot.myTable.revalidate();
                    currentRoot.myTable.repaint();

                }
            }
    });    
    }
   public boolean addToGroup(Pnl_calendarTable ptb, Class cls) {
       if (tableGroup.contains(ptb)) {
           krn.Message("E", "V skupine tabuliek už existuje objekt: " + FnEaS.sObjName(ptb), "Viacnásobé pripojenie objektu !");
           return false;
       }
       if (cls==null) {
           krn.Message("E", "Pokus o pripojeni objektu: " + FnEaS.sObjName(ptb)
                        + "s neurčitou triedou jointPoint-u.", "Viacnásobé pripojenie objektu !");
           return false;
       }
       if (mainJointPoint!=null && ptb.jointPointClass != mainJointPoint) {
           krn.Message("E", "Pokus o pripojeni objektu: " + FnEaS.sObjName(ptb)
                        + "s inou triedou jointPoint-u (" + cls.getSimpleName() + ")."
                        + "\nPredefinovaný jointPoint je: " + mainJointPoint + "." ,
                          "Neplatný spojovací bod objektu !");
           return false;
       }
       // prvy pridany objekt urci triedu mainJointPoint-u;
       if (mainJointPoint==null) mainJointPoint = cls;
       //System.out.println("PUTTOGROUPP:");
       tableGroup.add(ptb);
       System.out.println("PUTTOGROUPP:" + tableGroup.size() + " " + FnEaS.sObjName(ptb));
       //System.out.println(tableGroup.keySet().
       return true;
   }
   
   public String sendMessageToGroup(Object oSender,String sMsg, Object oParams) {
       IEOC_VisualObject vob;
       String retVals = "";
       for (Pnl_calendarTable curTbl: tableGroup) {
           if (((Object) curTbl) == oSender) continue;
           if (sMsg.equals("setCurrentToday")) {
               String rv = curTbl.setCurrentToday(oSender,oParams);
               if (rv == null) rv = "NULL";
               retVals = retVals + " " + rv;
           };
           retVals = retVals.trim();
        }
        return retVals;
    }
           
   @Override
    public String initialize(Kernel kr, DBconnection cX) {

        String initialize = super.initialize(kr,  cX);
        
        if (!initialize.equals("")) { return initialize; } 
        
        // inicializacia
        pnl_brwHourDay1.initialize(kr,cX);
        pnl_brwHourDayWeek1.initialize(kr,cX);
        pnl_brwHourDayMonth1.initialize(kr,cX);
        pnl_brwDayMonthYear1.initialize(kr,cX);
        pnl_calendarBrowser1.initialize(kr,cX);
        
        // pripojenie tabuliek do skupiny
        pnl_brwHourDay1.joinToGroup(this);
        pnl_brwHourDayWeek1.joinToGroup(this);
        pnl_brwHourDayMonth1.joinToGroup(this);
        pnl_brwDayMonthYear1.joinToGroup(this);
        pnl_calendarBrowser1.joinToGroup(this);
        
        return "";    
    }

    @Override
    public String afterInitialize() {
        
        super.afterInitialize();
        
        // retazene volanie metod afterInitialize()
        pnl_brwHourDay1.afterInitialize();
        pnl_brwHourDayWeek1.afterInitialize();
        pnl_brwHourDayMonth1.afterInitialize();
        pnl_brwDayMonthYear1.afterInitialize();
        pnl_calendarBrowser1.afterInitialize();
        
        String cPg =  krn.getUserProperty("calendar_folder");
        if(cPg==null || cPg.equals("")) cPg = "2"; // default hodnota
        cPg = cPg.trim();
        Integer iPg = Integer.parseInt(cPg);
        jTabbedPane1.setSelectedIndex(0);
       //// Kernel.staticMsg("SVITCHINGPAGE: " + cPg);
        jTabbedPane1.setSelectedIndex(iPg);
        
        return "";        
    }
    
    

    public String goToOwner(EOC_message eocMsg /* String ownerObj */) {
        //krn.Message(">>>>>> Group-goToOwner(): " + ((CalendarOwner) ownerObj).getOwnerName());
        pnl_brwHourDay1.goToOwner(eocMsg);
        pnl_brwHourDayWeek1.goToOwner(eocMsg);
        pnl_brwHourDayMonth1.goToOwner(eocMsg);
        pnl_brwDayMonthYear1.goToOwner(eocMsg);
        pnl_calendarBrowser1.goToOwner(eocMsg);
        return "";
    }

    public String goToToday (EOC_message eocMsg /* String ownerObj */) {
    ////    krn.Message("Group-goToToday()");
        if (currentRoot != null) currentRoot.goToToday();
        return "";
    }    
    
    public String goToPrev (EOC_message eocMsg /* String ownerObj */) {
    ////    krn.Message("Group-goToPrev()");
        if (currentRoot != null) currentRoot.goToPrev();
        return "";
    }    

    public String goToNext (EOC_message eocMsg /* String ownerObj */) {
     ////  krn.Message("Group-goToNext()");
        if (currentRoot != null) currentRoot.goToNext();
        return "";
    }    
    
    public String displayedValueTypeChanged(String s) {
        if (currentRoot != null) currentRoot.displayedValueTypeChanged(s);
        return "";
    }
    
    
    
     public String nullVisibilityChanged(Object vsb) {
//QQQNEMA        pnl_brwHourDay1.nullVisibilityChanged(vsb);
//        pnl_brwHourDayMonth1.nullVisibilityChanged(vsb);
//QQQNEMA        pnl_brwMonth1.nullVisibilityChanged(vsb);
//QQQNEMA        pnl_brwWeek1.nullVisibilityChanged(vsb);
//QQQNEMA        pnl_brwYear1.nullVisibilityChanged(vsb));
        return "";
     }

     public String displayedValueTypeChanged (Object vsb) {
//QQQNEMA        pnl_brwHourDay1.nullVisibilityChanged(vsb);
        pnl_brwHourDayMonth1.displayedValueTypeChanged(vsb);
//QQQNEMA        pnl_brwMonth1.nullVisibilityChanged(vsb);
//QQQNEMA        pnl_brwWeek1.nullVisibilityChanged(vsb);
//QQQNEMA        pnl_brwYear1.nullVisibilityChanged(vsb));
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnl_brwHourDay1 = new eoc.calendar.Pnl_brwHourDay();
        pnl_brwHourDayWeek1 = new eoc.calendar.Pnl_brwHourDayWeek();
        pnl_brwHourDayMonth1 = new eoc.calendar.Pnl_brwHourDayMonth();
        pnl_brwDayMonthYear1 = new eoc.calendar.Pnl_brwDayMonthYear();
        pnl_calendarBrowser1 = new eoc.calendar.Pnl_calendarBrowser();

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        jTabbedPane1.addTab("Hodiny za deň", pnl_brwHourDay1);
        jTabbedPane1.addTab("Hodiny po dňoch za týždeň ", pnl_brwHourDayWeek1);
        jTabbedPane1.addTab("Hodiny po dňoch za mesiac", pnl_brwHourDayMonth1);
        jTabbedPane1.addTab("Dni po mesiacoch za rok", pnl_brwDayMonthYear1);
        jTabbedPane1.addTab("Podrobné prezeranie zápisov", pnl_calendarBrowser1);

        jTabbedPane1.setSelectedIndex(2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 904, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        if (krn != null)
            krn.setUserProperty("calendar_folder", String.valueOf(jTabbedPane1.getSelectedIndex()));
    }//GEN-LAST:event_jTabbedPane1StateChanged

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane1;
    private eoc.calendar.Pnl_brwDayMonthYear pnl_brwDayMonthYear1;
    private eoc.calendar.Pnl_brwHourDay pnl_brwHourDay1;
    private eoc.calendar.Pnl_brwHourDayMonth pnl_brwHourDayMonth1;
    private eoc.calendar.Pnl_brwHourDayWeek pnl_brwHourDayWeek1;
    private eoc.calendar.Pnl_calendarBrowser pnl_calendarBrowser1;
    // End of variables declaration//GEN-END:variables
}
