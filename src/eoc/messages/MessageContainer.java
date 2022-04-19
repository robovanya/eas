/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.messages;

import eoc.database.DBconnection;
import java.sql.Connection;
import java.util.ArrayList;
import system.FnEaS;
import system.Kernel;
import system.cron.Crond;

/**
 *
 * @author rvanya
 */
public class MessageContainer {
        Kernel krn;
        Crond  cron;
        DBconnection DBcnWrk;
        private Pnl_messages pnlMessages;
        
        private ArrayList<MessageRecord> alMessageRecords; // Eoc_message,Pnl_message,.....
        private ArrayList<Integer> alMessageRecordIDs; // Eoc_message,Pnl_message,.....
        private ArrayList<Integer> alViewedMessageRecordIDs; // Eoc_message,Pnl_message,.....
        private Integer lastLoadedMessageRecordID = null;
        private Integer lastViewedMessageRecordID = null;
        private String fromDate;
        
        public MessageContainer() {
            this.alMessageRecords         = new ArrayList<>();
            this.alMessageRecordIDs       = new ArrayList<>();
            this.alViewedMessageRecordIDs = new ArrayList<>();
        }
        
        public ArrayList<MessageRecord> getMessageRecors() {
            return alMessageRecords;
        }
            
        public ArrayList<Integer> getMessageRecorIDs() {
            return alMessageRecordIDs;
        }
        
        public ArrayList<Integer> getViewedMessageRecorIDs() {
            return alViewedMessageRecordIDs;
        }

        public boolean addMessage (MessageRecord mrc) {
            alMessageRecords.add(mrc);
            alMessageRecordIDs.add(mrc.message.getInteger("id_eas_cromsg"));
            Pnl_message oMsg = new eoc.messages.Pnl_message(mrc.message);
            if (pnlMessages!=null) { // pri Initialize nemusi platit
                pnlMessages.AddNewMessage(oMsg);
                mrc.message.listEntries("", true);
            }

            return true; 
        }
        
        public void initialize(Kernel kr, Crond cd, DBconnection cn) {

            krn     = kr;
            cron    = cd;
            DBcnWrk = cn;

            fromDate = FnEaS.currDate("yyyy-MM-dd");
            
            loadNewMessages(-9999); // nacitanie sprav (-9999 znamena ze je to 
                                    // prve nacitavanie ktore urci aj datum)

     }
        
        public void setPnlMessages(Pnl_messages pnl) {
             pnlMessages = pnl;
             pnlMessages.setMessageContainer(this);

        }

    public void loadNewMessages(Integer predpoklMnozstvo) {
        
        if (predpoklMnozstvo == -9999) { // prve nacitavie, dalej sa fromDate neriesi
            // zisti sa datum prvej neprecitanej spravy
            String sF = "SELECT FIRST d_msgdate FROM eas_cromsg WHERE c_cro_user = '" 
                        + krn.getPermd().getCurrentUser() + "'"
                        + " AND d_readdate iS NULL ORDER BY id_eas_cromsg";
            sF = DBcnWrk.getDbDriver().arrangeQry(sF);
            //DEBUG System.out.println("A loadNewMessages== " + sF);
            Object o = DBcnWrk.getDbDriver().SQL_getQueryValue(DBcnWrk.getConn(), sF, "d_msgdate");
            //DEBUG System.out.println("B loadNewMessages== " + (o==null));
            String sFirstUnreaded = o==null?fromDate:o.toString();

            // ked je prva neprecitana sprava mensia ako obmedzujuci datum
            // obmedzujuci datum sa upravi, aby sa nacitali vsetky neprecitane spravy
            if (sFirstUnreaded.compareTo(fromDate) < 0) fromDate = sFirstUnreaded;
        }
        
        // Ziskaju sa messages od obmedzujuceho datumu
        String s = "SELECT * FROM eas_cromsg WHERE c_cro_user = '" 
             + krn.getPermd().getCurrentUser() + "'"
                    + " AND d_msgdate >= '" + fromDate + "'"
                    + (lastLoadedMessageRecordID!=null?" AND id_eas_cromsg > " + lastLoadedMessageRecordID:"")
                    + " ORDER BY id_eas_cromsg";
        Object[][] oQry = krn.SQLQ_getQueryResultSetAsArray(DBcnWrk.getConn(), s, true);
        Object[] hdrs = null;
        Object[] data;
        int i = -1;
        //v tejto slucke, alebo po nej distribuovat spravy do panelu a udrzat prepojenie
        for (Object[] oo: oQry) {
            i++;
            if (i==0) { // prvy riadok obsahuje nazvy stlpcov tabulky
                hdrs = oo;
                continue;
            }
            data = oo;
            Eoc_message emsg = new Eoc_message(krn,DBcnWrk,"eas_cromsg","id_eas_cromsg");
            if (emsg.setDataFromArrays(hdrs, data)) { // objekt sa vytvoril bez chyby
                MessageRecord mrc = new MessageRecord(emsg,null);
                this.addMessage(mrc);
                lastLoadedMessageRecordID = mrc.message.getInteger("id_eas_cromsg");
                //DEBUG   System.out.println(">>>>>LASTLOADEDMSGIDD:" + lastLoadedMessageRecordID);
                
            }

        }
    }
        
        
}
