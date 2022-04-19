/*******************************************************************************
 **
 ** Easys.java
 **
 ** Spustaci program systemu   EaSys V1
 **
 ** 04.09.2014 - Atti/Robo Vanya
 **
 *******************************************************************************/
package easys;

import eoc.EOC_message;
import eoc.IEOC_DB_driver;
import eoc.IEOC_Object;
import eoc.database.DBconnection;
import java.sql.Connection;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import jxl.read.biff.BiffException;
import javax.swing.UIManager.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import system.*;
import system.perm.PermHandler;

public class Easys implements eoc.IEOC_Object {
  // typ externej databazy  
  //  private String exDBtype = "mysql";
  private String exDBtype = "postgres";
  //private String WorkDBtype = "sybase";
    
  // typ internej databazy  
   // private String inDBtype = "mysql";
    private String OLAPDBtype = "postgres";
   // private String inDBtype = "sybase";
    /*
    private Connection CnnWork = null;
    private Connection CnnOLAP = null;
    private Connection CnnWWW = null;
    */
    private DBconnection DBcnWork = null;
    private DBconnection DBcnOLAP = null;
    private DBconnection DBcnWWW  = null;

    // konnekt definicie pracovnej databazy
    // Attiho connect -- 
    //private String exDBurl = "jdbc:postgresql://192.168.1.110:5432/EaSys";
    // private String exDBurl = "jdbc:postgresql://Localhost:5432/EaSys";
    // private String exDBurl = "jdbc:sqlanywhere:uid=rvanya;pwd=4g4w00;eng=sql12";
    // NEDE-2016-01-09 private String WorkDBurl = "jdbc:sqlanywhere:uid=rvanya;pwd=4g4w00;eng=sybEasys";
    // NEDE-2016-01-09 private String WorkDBlogin = "EaS";
    // NEDE-2016-01-09 private String WorkDBheslo = "4g4w00";

    
    // konnekt definicie internej databazy
    // Attiho connect -- 
    // NEDE-2016-01-09 private String OLAPDBurl = "jdbc:postgresql://192.168.1.110:5432/EaSys";
    // private String inDBurl = "jdbc:postgresql://Localhost:5432/EaSys";
    // private String inDBurl = "jdbc:sqlanywhere:uid=rvanya;pwd=4g4w00;eng=sql12";
    // NEDE-2016-01-09 private String OLAPDBlogin = "EaS";
    // NEDE-2016-01-09 private String OLAPDBheslo = "4g4w00";

    private system.desktop.Desktop dsk;
    private Kernel krn;
    private static String runLevel;
    private /* Welcome_dialog_full */ eoc.widgets.DObject welcome;
    private boolean init_OK;
    private final Map<String, String> privateData = new HashMap<>();
    
    // ekvivalent init procesu linux-u
    public Easys(String runAs, String applicationClassName) 
        throws BiffException, ClassNotFoundException, SQLException,
               NoSuchMethodException, IOException, FileNotFoundException,
               IllegalAccessException, IllegalArgumentException,
               InvocationTargetException, URISyntaxException {
        // konnektovanie externej a internej databazy
//        connect_exDB(); // konnektovanie externej databazy
//        connect_inDB(); // konnektovanie internej databazy
        try {
            // Nastavenie vyzoru objektov obrazoviek a ToolTip-ov
            for (javax.swing.UIManager.LookAndFeelInfo info :
                 javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
//                if ("Nimbus".equals(info.getName())) {
//                if ("CDE/Motif".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    /* "com.sun.java.swing.plaf.motif.MotifLookAndFeel" */
                    break;
                }
            }
        } catch (Exception ex) {/* pripadna chyba ma nezaujima, ignorujem */}
        runLevel = "DESKTOP";
        UIManager.put("Label.font", new Font(Font.SANS_SERIF , 0, 12));
        UIManager.put("ToolTip.background", new ColorUIResource(255, 247, 200)); //#fff7c8
        Border border = BorderFactory.createLineBorder(new Color(76,79,83));    //#4c4f53
        UIManager.put("ToolTip.border", border);
        krn = new Kernel();
        if (applicationClassName == null) applicationClassName = "EASYS";
        krn.setWrapperAppName(applicationClassName);
        // Spusta sa LOGIN-obrazovka pripojenia k databazam
        //welcome = new system.Welcome_dialog_full(dsk, true /* modal */, false/* simple(true)/advanced(false) */);
@SuppressWarnings("unchecked")
        Map<String, String>[] hmFrameValues = new HashMap[3]; // obsahy connect-panelov
        welcome = new system.Welcome_dialog_simple(null, true /* modal */,
//       welcome = new system.Welcome_dialog_full(null, true /* modal */,
                  false/* simple(true)/advanced(false) */,
                  hmFrameValues /* obsah frameObjektu Pnl_DBconnect pri ukonceni */);
        welcome.initialize(krn,DBcnWork);
        welcome.setLocationRelativeTo(dsk);
        // ziskanie hodnot welcome-obrazovky na neskorsie pouzitie
        //saveConnectionsForUser();
        DBconnection[] cnx;
        cnx = ((IWelcome) welcome).getDBconnections();
        if ( cnx[0] != null) DBcnWork = cnx[0];
        if ( cnx[1] != null) DBcnOLAP = cnx[1];
        if ( cnx[2] != null) DBcnWWW  = cnx[2];
        String cnStat = "";
        if (DBcnWork == null) {
            cnStat = "Pracovná databáza nie je pripojená.";
        }
        init_OK = (DBcnWork != null);
        // krn.OutPrintln("BBB-init_OK=" + init_OK);
        if (!init_OK) {
            krn.Message (cnStat,"Problém pri pripojení databáz.");
            System.exit(-1);
        }
        
       System.out.println("\nAvailable database connections:"
         + "\n      WORK: " 
         + ((cnx[0]==null)?"not connected":cnx[0].getDatabaseName() + " (" + cnx[0].getDBType() + ")") 
         + "\n      OLAP: "
         + ((cnx[1]==null)?"not connected":cnx[1].getDatabaseName() + " (" + cnx[1].getDBType() + ")") 
         + "\n       WWW: "
         + ((cnx[2]==null)?"not connected":cnx[2].getDatabaseName() + " (" + cnx[2].getDBType() + ")") 
               + "\n");

        krn.setDbConnections(cnx[0], cnx[1], cnx[2]);
        dsk = new system.desktop.Desktop(krn, runLevel);
        krn.setDsk(dsk);
        dsk.SetKernel(krn);
        dsk.SetConn(DBcnWork);
        dsk.initialize(krn, DBcnWork); // 2012-10-2 Prvy pokus s metodou initialize()
/*
        krn.OutPrintln("\n\n\n" + system.FnEaS.currDateTime() + "\n" 
                    + krn.getAppName() + " version: " + krn.getAppVersion() + "\n"
                    + krn.getAppDescription() + "\n\n" 
                    + "System kernel STARTED");
        */
        try {
            krn.setWrapperAppName(applicationClassName); // 2015-8-27, Zaciatok tvorby systemu pre NZBD
            krn.initialize(); // 2013-6-16 - Kernel spusta potrebne sluzby/demony (cron, perm, ...)
            dsk.afterInitialize();
            krn.setStationProperty("login_work", defMapToSring(hmFrameValues[0]));
            /*
     PermDefinition pDefM = new PermDefinition(krn, krn.getPermd(),krn.getCnWork(), "USER"
                           , krn.getPermd().getCurrentUser()
                           ,"SYSTEM:SYSTEM", null,"SYSTEM_SYSTEM" );
            */
            
            dsk.setCronState("disabled");
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Easys.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Easys.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Easys.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Easys.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Easys.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Easys.class.getName()).log(Level.SEVERE, null, ex);
        }

       krn.OutPrintln("Initializing runLevel: " + runLevel + "\n");
       dsk.loadModules(); // 2012-10-2 Prvy pokus s nacitanim modulov 
                          // z definicneho suboru z disku
       krn.loadPermCacheForCurrentUser();
    }
    public String defMapToSring(Map<String,String> map) {
        String str = "";
        if ((map==null) || map.isEmpty()) return "";
        for (String key : map.keySet()) {
            String val = map.get(key);
            str = str + "^" + key + "====" + val; 
        }       
        // odrezanie delimitera ^ na zaciatku retazca
        if (str.length() > 0) str = str.substring(1);
    
        return str;
    }
    
    public static void main(String[] args) 
           throws BiffException, ClassNotFoundException, SQLException,
                  NoSuchMethodException, IOException, FileNotFoundException, 
                  IllegalAccessException, IllegalArgumentException, 
                  InvocationTargetException, URISyntaxException {
       System.out.println("running system EaSys ... ");

       if (args.length > 0) runLevel = args[0].toUpperCase();
       else runLevel = "DESKTOP";

       // runLevel = "DESKTOP";  // testovanie roznych prostredi sa zadava tu 
       
       new Easys(runLevel,"EASYS");
    }

    public Kernel getKrn() {
        return krn;
    }

    @Override
    public String initialize(Kernel krn, DBconnection cnX) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String destroy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DBconnection getConn() {
        return krn.getDBcnWork();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String setConn(DBconnection cX) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String setKrn(Kernel krnl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setConnectionStatus(int status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String afterInitialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String CallMethod(Object oCaller, String sMethod, String sParameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getEOC_objectType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
/**
 * 
 * @param sObjectType 
 */
    public void setEOC_objectType(String sObjectType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMyObjectID(IEOC_Object evo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setPrivateData(String key, String val) {
        privateData.put(key, val);
    }

    @Override
    public String getPrivateData(String key) {
        return privateData.get(key);
    }

    @Override
    public String createLinkTo(Object oCreator, Object oVSrc, Object oVTrg, String sLink, String sState) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String sendMessage(EOC_message eocMessage, String sLinkType, String sVector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String receiveMessage(EOC_message eocMessage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PermHandler getPermHandler() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createPermHandler(Kernel kr, IEOC_Object ieo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setObjPerms(EOC_message eocMsg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setUsrPerms(EOC_message eocMsg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getUsrPerms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getObjPerms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPerms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String objectPermsChanged(EOC_message eocMsg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String userPermsChanged(EOC_message eocMsg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
