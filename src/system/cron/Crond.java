/*******************************************************************************
 **
 ** EaS_cron.java
 **
 ** Automaticka vrstva systemu   EaSys V2
 **
 ** 04.09.2014
 **
 *******************************************************************************/
package system.cron;

import eoc.IEOC_DB_driver;
import eoc.database.DBconnection;
import eoc.dbdrv.DbDrv_Firebird;
import eoc.dbdrv.DbDrv_MySQL;
import eoc.dbdrv.DbDrv_Postgres;
import eoc.dbdrv.DbDrv_Sybase;
import eoc.messages.Eoc_message;
import eoc.messages.MessageContainer;
import eoc.messages.Pnl_message;
import eoc.messages.Pnl_messages;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import system.Kernel;
import system.desktop.Pnl_Functions;

/**
 *
 * @author rvanya
 */
public class Crond {
    private system.Kernel  krn;
    private DBconnection     DBcnCron;
    private DBconnection     DBcnWork;
    private DBconnection     DBcnOLAP;
    private DBconnection     DBcnWWW;
/*    
    private IEOC_DB_driver     dbDrvWrk;
    private IEOC_DB_driver     dbDrvCron;
    private IEOC_DB_driver     dbDrvOLAP;
    private IEOC_DB_driver     dbDrvWWW;
*/    

    private Pnl_cron       pnl_cron;
    private Pnl_Functions  pnl_functions;
    private Calendar       lastTeraz;
    private String         lastTime;
    private String         lastDate;
    private String         lastDateTime;
    private String[]       aLastDateTime;
    private String         currState = "ENABLED";
    private ArrayList<AutomatedTask> alAutomatedTasks;
    private Integer lastLogWrite;
    private int     logPeriod = 5;
    private Integer eoc_crotimID = null;
    private PreparedStatement psCronAccount; 
    private ResultSet         rsCronAccount; 
    private Pnl_messages      pnl_Messages;
    private String dtFirstMessage; // Den, od ktoreho sa nacitavaju spravy (rrrr-mm-dd)
//    private Integer[] aMyAllMessageIds; // eas_comsg-ID-y uzivatela pri spusteni EaSyS
                                      // dalej sa to udrzuje pridavanim novych messsage-ov
    private MessageContainer msgContainer;

/*
    public class MessageContainer {

        public MessageContainer() {
            this.alMessageRecords = new ArrayList<>();
            this.alMessageRecordIDs = new ArrayList<>();
        }
        
        private ArrayList<MessageRecord> alMessageRecords; // Eoc_message,Pnl_message,.....
        private ArrayList<Integer> alMessageRecordIDs; // Eoc_message,Pnl_message,.....
        private ArrayList<Integer> alViewedMessageRecordIDs; // Eoc_message,Pnl_message,.....
        
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
            return true; 
        }
        
        public void initialize() {
            
        }
    }
    */

/*
    public class MessageRecord {
        public final Pnl_message panel;
        public final Eoc_message message;
        
        public MessageRecord (Eoc_message msg, Pnl_message pnl) {
             message = msg;
             panel = pnl;
        }
    }
*/
    
    private class AutomatedTask {
         public final Object owner;
         public final Method methodForCall;
         public final String cyclingBy; //D(ay),H(our),M(inute),S(econd)
         private int     cycleLength;
         //Y(ear),M(onth),D(ay),H(our),m(inute),s(econd)
         public  Integer[] createdAt  = new Integer[6];
         public  Integer[] firedAt    = new Integer[6];
         public  Integer[] nextFireAt = new Integer[6];
         
         
         public AutomatedTask(Object o, Method m, String dhms, int cl,
                              boolean bFire) {
             owner         = o;  
             methodForCall = m;
             cyclingBy     = dhms; // D(ay),H(our),m(inute),s(econd)
             cycleLength   = cl;
             // Y(ear),M(onth),D(ay),H(our),m(inute),s(econd)
             // prida casove udaje vytvorenia
             createdAt = system.FnEaS.getTimeDataAt6();
             if (bFire) {
                 try {
           //  prida casove udaje volania
                     firedAt = createdAt;
                     methodForCall.invoke(owner, (Object[]) null);
           //  prida casove udaje dalsieho volania
                 } catch (IllegalAccessException ex) {
                     Logger.getLogger(Crond.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IllegalArgumentException ex) {
                     Logger.getLogger(Crond.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (InvocationTargetException ex) {
                     Logger.getLogger(Crond.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
         }
         
         public void setCycleLength(Integer iLngth) {
             cycleLength = iLngth;
         }
    }

    public Crond() {
        this.aLastDateTime = new String[6];
         alAutomatedTasks  = new ArrayList<>();
         msgContainer      = new MessageContainer();
    }
    
    private Timer t = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
           Teraz();
        }
    });
    
            
    public void initialize() {
        //krn.dskMsg("Inicializácia vrstvy Crond ... OK"); 
        krn.WriteEventToLog("\nInicializácia vrstvy Cron ... OK\n"); 
        // Vytvorenie vlastneho-paralelneho connection-objektu (kopie CnWrk)
        Connection cntn;
        try { 
            Properties prt = new Properties();
            prt.put("user", krn.getWorkUsr());
            prt.put("password", krn.getWorkPwd());
//            prt.put("sql_role_name", "EAS");
            prt.put("roleName", "EAS");

            prt.put("roleName", "EAS");
            prt.put("lc_ctype", "WIN1250");
            prt.put("charSet", "cp1250");
  //         prt.put("encoding", "NONE");
  //         prt.put("localencoding", "NONE");
//           prt.put("localEncoding", "WIN1250");
//lc_ctype, encoding, charSet or localEncoding            
//            cntn = DriverManager.getConnection(krn.getWorkURL(), krn.getWorkUsr(), krn.getWorkPwd());
            cntn = DriverManager.getConnection(krn.getWorkURL(), prt); // pripojenie na DB
            cntn.setAutoCommit(false);
            
            DBconnection dbc = new DBconnection();
            dbc.setKernel(krn);
//            dbc.setConnection(cntn);
            dbc.setDbType(krn.getDBcnWork().getDBType(), cntn);
//            CnCron = cntn;
            DBcnCron = dbc;
            // dbDrvCron = getDbDriver(dbc);
        } catch (SQLException ex) {
            Logger.getLogger(Crond.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String sql = "select id_eas_crotim from eas.eas_crotim "
                  + "where c_cro_user = '"
                  + krn.getPermd().getCurrentUser() 
                  + "'";
//        Object s = krn.SQLQ_getQueryValue(DBcnCron.getConn(), sql, "id_eas_crotim");
        Object s = DBcnCron.getDbDriver().SQL_getQueryValue(DBcnCron.getConn(), sql, "id_eas_crotim");
        initMessagerInterface();
        Teraz();
        t.start();
    }
    public IEOC_DB_driver getDbDriver(DBconnection dbc) {
        IEOC_DB_driver drv = null;
        if (dbc.getDBType().toUpperCase().equals("SYBASE"))
                drv = new DbDrv_Sybase();
        else if (dbc.getDBType().toUpperCase().equals("FIREBIRD"))
                drv = new DbDrv_Firebird();
        else if (dbc.getDBType().toUpperCase().equals("MySQL"))
                drv = new DbDrv_MySQL();
        else if (dbc.getDBType().toUpperCase().equals("POSTGRES"))
                drv = new DbDrv_Postgres();
        return drv;
    }

    
    public void initMessagerInterface() {
        msgContainer.initialize(krn, this, DBcnCron);
/*
        String s = "SELECT FIRST d_msgdate FROM eas_cromsg WHERE c_cro_user = '" 
                    + krn.getPermd().getCurrentUser() + "'"
                    + " AND d_readdate iS NULL" ;
        Object o = Kernel.SQL_getQueryValue(CnWrk, s, "d_msgdate");
        String sCurr = FnEaS.currDate("yyyy-MM-dd");
        String sFirst = o==null?sCurr:o.toString();
        if (sFirst.compareTo(sCurr) < 0) sCurr = sFirst;
        System.out.println("initMessagerInterface().dateFirst: " + sCurr
               // + " - " + sFirst + "<" + FnEaS.currDate("yyyy-MM-dd") 
        );
        // Ziskaju sa messages od zisteneho datumu
        s = "SELECT * FROM eas_cromsg WHERE c_cro_user = '" 
             + krn.getPermd().getCurrentUser() + "'"
                    + " AND d_msgdate >= '" + sCurr + "'";
        Object[][] oQry = krn.SQL_getQueryResultSetAsArray(CnWrk, s, true);
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
            Eoc_message emsg = new Eoc_message(krn,CnWrk,"eas_cromsg","id_eas_cromsg");
            if (emsg.setDataFromArrays(hdrs, data)) { // objekt sa vytvoril bez chyby
                msgContainer.addMessage(new MessageRecord(emsg,null));
            }
            ////System.out.println("pnlMessagespnlMessages:" + (pnlMessages == null));
            if (pnlMessages != null)
                pnlMessages.setMessageContainer(msgContainer);

        }
*/
            if (pnl_Messages != null) msgContainer.setPnlMessages(pnl_Messages);
    }
    
    public void setState(String state) {
        state = state.toUpperCase().trim();
        currState = state;
        if (state.equals("ENABLED")) {
            krn.WriteEventToLog("CRON: Enabling cron layer ... OK");
            krn.dskMessage("Cron layer enabled");
            krn.dskMessageClear("Cron-layer enabled");
        }
        else if (state.equals("DISABLED")) {
            krn.WriteEventToLog("CRON: Disabling cron layer ... OK (state can you change in EaSys-desktop UI)\n");
            krn.dskMessage("Cron layer disabled");
            krn.dskMessageClear("Cron-layer disabled");
        }
        else {
            krn.WriteEventToLog("CRON: Unidentified state request: " + state
                + " ... switch to new state denied" );
            
        }
    }

    /**
     * Vyhladá alebo založí crond-konto aktuálne prihláseného užívateľa
     * a identifikátor riadku v tabuľke (id_eas_crotim) 
     * uloží do premennej eoc_crotimID
     */
    private void searchCrondAccount() {

        if (eoc_crotimID != null) return;

        String sqlID = "SELECT id_eas_crotim FROM eas_crotim " 
                     + "WHERE c_cro_user = '" 
                     + krn.getPermd().getCurrentUser() + "'";
        Object o = DBcnCron.getDbDriver().SQL_getQueryValue(DBcnCron.getConn(), sqlID, "id_eas_crotim");

        // crond-konto pre uzivatela neexistuje, tak sa vytvori 
        if (o == null) {
            String sql = "insert into eas.eas_crotim "
                 + "(c_cro_user,d_last_date,i_last_time,c_message) values ('"
                 + krn.getPermd().getCurrentUser() 
                 + "','1970-01-01',0,'conto_created');";
            DBcnCron.getDbDriver().SQL_callSqlStatement(DBcnCron.getConn(), sql, true); // zalozenie crond-konta
            // ziskanie ID-u vytvoreneho crond-konta
            o = DBcnCron.getDbDriver().SQL_getQueryValue(DBcnCron.getConn(), sqlID, "id_eas_crotim");
        }
        if(o == null) return;
        eoc_crotimID = Integer.parseInt(o.toString());

        String sqlAccount = "SELECT * FROM eas.eas_crotim " 
                          + "WHERE c_cro_user = ?";
        try {
            psCronAccount = DBcnCron.getConn().prepareStatement(sqlAccount
//                    ,ResultSet.FETCH_UNKNOWN  //resultSetType
//                    ,ResultSet.CONCUR_READ_ONLY   // resultSetConcurrency
//                    ,ResultSet.HOLD_CURSORS_OVER_COMMIT // resultSetHoldability
            );
            psCronAccount.setString(1, krn.getPermd().getCurrentUser());
            rsCronAccount = psCronAccount.executeQuery();
//            rsCronAccount.beforeFirst();
            rsCronAccount.next();
        } catch (SQLException ex) {
            Logger.getLogger(Crond.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean refreshAccount() {
        boolean bSucces = false;
        try {
            rsCronAccount = psCronAccount.executeQuery();
            bSucces = rsCronAccount.next();
        } catch (SQLException ex) {
            Logger.getLogger(Crond.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bSucces;
    };

    
    /**
     * Tik časovača crond - vykoná zápis do crond-konta užívateľa
     * a spustí ostatné potrebné funkcie
     */
    private void updateTimeStrings(Calendar cl) {
        String tm = "";
        String hh = String.valueOf(lastTeraz.get(Calendar.HOUR_OF_DAY));
        String mm = String.valueOf(lastTeraz.get(Calendar.MINUTE));
        String ss = String.valueOf(lastTeraz.get(Calendar.SECOND));
        String dd = String.valueOf(lastTeraz.get(Calendar.DAY_OF_MONTH));
        String mo = String.valueOf(lastTeraz.get(Calendar.MONTH) + 1); 
        String yy = String.valueOf(lastTeraz.get(Calendar.YEAR));

        if(hh.length()==1) hh = "0" + hh;
        if(mm.length()==1) mm = "0" + mm;
        if(ss.length()==1) ss = "0" + ss;
        lastTime = hh + ":" + mm + ":" + ss;
        if(dd.length()==1) dd = "0" + dd;
        if(mo.length()==1) mo = "0" + mo;
        lastDate = dd + "." + mo + "." + yy;
        lastDateTime = yy + mo + dd + hh + mm + ss;
        aLastDateTime[0] = yy;
        aLastDateTime[1] = mo;
        aLastDateTime[2] = dd;
        aLastDateTime[3] = hh;
        aLastDateTime[4] = mm;
        aLastDateTime[5] = ss;
    }
    
    // Attiho nazov pre tlkot srdca :-) 
    // V skutocnosti mi s tym velmi pomohol na zaciatku vytvarania EaSys.
    private void Teraz() {
        // hlada sa existujuce crond-konto uzivatela 
        if (eoc_crotimID == null) searchCrondAccount();

        if (eoc_crotimID == null) { // fatalna chyba - konto sa nevytvorilo, ani nenaslo 
            krn.Message("E", "Neúspešné vytvorenie užívateľského konta pre cron-daemon", "PANIC-ERROR");
            return;
        }
       
        lastTeraz = Calendar.getInstance();

        updateTimeStrings(lastTeraz); // nastavenie ostatnych string-premennych
        
        // vypis aktualneho casu na obrazovke EaSys 
        // (vypis casu ukazuje, ze crond je zivi, a je OK)
        if (!(pnl_cron == null)) pnl_cron.setClock(lastTime);

        if (!currState.equals("ENABLED")) return; // kron je vypnuty, dalej sa nic nekona

        if (krn == null) return; // pre istotu

        // logovanie do cron-konta 
      if (lastTeraz.get(Calendar.SECOND) % logPeriod == 0) {
          String sql = "UPDATE eas.eas_crotim "
                     + " SET d_last_date = '" +  aLastDateTime[0] + "-" 
                     + aLastDateTime[1] + "-" 
                     + aLastDateTime[2] + "'"
                     + " ,i_last_time = " + lastDateTime
                     + " ,c_message = 'standard_login'"
                     + " WHERE id_eas_crotim = " + eoc_crotimID + ";";

          DBcnCron.getDbDriver().SQL_callSqlStatement(DBcnCron.getConn(), sql, true);
           try {
////               System.out.println("CROND: account-set: " + rsCronAccount.getFetchSize());
               refreshAccount();
               Integer numMsgs = rsCronAccount.getInt("i_num_new_messages");
////               System.out.println("CROND: new messages: " + numMsgs + " functionsPanel: " 
////                                + (pnl_functions!=null) + " pml_cron: " + (pnl_cron!=null));
               
               if (numMsgs > 0) { 
                   
                   pnl_functions.notifyNewMessages(numMsgs > 0); // zablika ikonou v tlacitku
        
                   if (pnl_cron.addNewMessages(numMsgs)) {
                       msgContainer.loadNewMessages(numMsgs); // pridanie novych sprav do kontainera
                       // odpisanie poctu sprav, na ktore sa upozornilo
                      String sqlx = "UPDATE eas.eas_crotim "
                                 + " SET i_num_new_messages = i_num_new_messages - " + numMsgs
                                 + " WHERE id_eas_crotim = " + eoc_crotimID + ";";
                      DBcnCron.getDbDriver().SQL_callSqlStatement(DBcnCron.getConn(), sqlx, true);
                   };
               }
               
           } catch (SQLException ex) {
               Logger.getLogger(Crond.class.getName()).log(Level.SEVERE, null, ex);
           }
        } 
    }

    public String getCurrentTime() {
        Teraz();
        return lastTime;
    }

    public String getCurrentDate() {
        Teraz();
        return lastDate;
    }

    public String getCurrentDateTime() {
        Teraz();
        return lastDate + " - " + lastTime;
    }

    public static String getTimeStamp() {
         String timeStamp = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss")
                            .format(Calendar.getInstance().getTime());                
         return timeStamp;
    }

    public void setKernel(system.Kernel k) {
        krn = k;
    }

    /*
   public void setConnections (Connection cWo, Connection cOL, Connection cWW) {
        CnWrk  = cWo;
        CnOLAP = cOL;
        CnWWW  = cWW;
    }
   */
    public void setDbConnections(DBconnection cWrk, DBconnection cOla, DBconnection cWw) 
        throws FileNotFoundException, IOException, SQLException {
        this.DBcnWork = cWrk;
        this.DBcnOLAP = cOla;
        this.DBcnWWW  = cWw;
     }
 /*   
   public void setDbDrivers (IEOC_DB_driver cWo, IEOC_DB_driver cOL, IEOC_DB_driver cWW) {
        dbDrvWrk  = cWo;
        dbDrvOLAP = cOL;
        dbDrvWWW  = cWW;
   }
 */  
    public void setPnl_cron(Pnl_cron dpn) {
        pnl_cron = dpn;
    }

    public void setPnl_functions(Pnl_Functions dpn) {
        pnl_functions = dpn;
    }

    public void setPnlMessages(Pnl_messages msgr) {
        if (pnl_Messages == null) {
            pnl_Messages = msgr;
            if (pnl_Messages != null)
                msgContainer.setPnlMessages(pnl_Messages);
        }
        else {
            krn.Message("W", "Duplicitná inicializácia obrazovky správ."
                    + "\n\nPonechaný pôvpodný objekt.", ""); 
        }
    }
   
}
