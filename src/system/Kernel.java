/*******************************************************************************
 **
 ** Kernel.java
 **
 ** Jadro systemu   EaSys V3 ( do 04.09.2014 Easys V1)
 **
 **                 EaSys V2 ( do 04.09.2014 Easys V1)
 **
 ** 01.09.2012  -- Attila Vanya/Robo Vanya
 **
 ** 04.09.2014  -- Verzia jadra zvysene na EaSys V2 (ERISS)
 **                Pobezi na viacerych typoch databaz. 
 **                  Planovane typy: Postgress 9.xx, Sybase SQLanywhere 12.++
 **                                  MySQL 5.++, FireBird 2.5++
 **
 ** 04.09.2014  -- Robko prebral kompletne 'riadenie' nakolko Atti kasle na to
 **                co je v poriadku. Ma svoju novu robotu, podobnu ako ja 
 **                tak nech sa stara o tu. 
 **                Idem budovat V2 - ERISS (Easys Reduced Instruction SQL Set)
 **                Bude prekladat zakladne definicie tabuliek, stlpcov a podobne
 **                podla typu aktualne spojenej DB. Podpora sa predpoklada
 **                pre typy databaz: Postgres, Sybase, MySQL a Firebird
 **                Momentalne ma zaujimaju hlavne Sybase a Firebird
 **                ale ako-tak sa postarme aj o ostatne driver-y
 **
 ** 27.08.2015  -- Po dohode vo firme sa vylepsi podpora Sybase DB,
 **                ostatne pride na rad potom (Firebird je dokonceny zhruba na 60%) 
 **
 ** 01.03.2022  -- Zaciatok vytvarania verzie EaSys V3
 **                Prechod na multiplatfornu verziu (LINUX/WINDOWS) 
 **                a rozsirenie o FUllStack prostredie (VUE + SpringBoot)
 ** 
 **                   
 *******************************************************************************/

package system;

import system.modul.Modul;
import system.desktop.Desktop;
//import eoc.dbdrv.DBdrv;
import system.cron.Pnl_cron;
import eoc.*;
import eoc.calendar.Win_myCalendar;
import eoc.dbdrv.DBcolumnInfo;
import java.awt.*;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import jxl.read.biff.BiffException;
import eoc.communication.CommLink;
import eoc.database.DBconnection;
import eoc.messages.Win_chat;
import eoc.widgets.DTXfield;
import eoc.widgets.DTcomboBox;
import eoc.widgets.DTfield;
import eoc.widgets.DTtextArea;
import eoc.widgets.VTabbedPane;
import eoc.widgets.VTabbedPane.TabListComponent;
import eoc.xinterface.XViewer;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.text.DefaultFormatter;
import system.desktop.Pnl_Functions;
import system.modul.MenuItem;

public final class Kernel {
//    driver-funkcie dat do Sybase driveru a z kernela prec, aj ked vsetko 
//    popada. volania krn.SQL_function zmenit na krn.DBdriver.SQLfunction
//   Font defaultFont = new Font ("SansSerif", Font.PLAIN, 16); 
   Font defaultFont = new Font ("Ariel", Font.PLAIN, 12); 

    public final boolean NO_ERROR = true;  // 2015-11-10 sluzi na potlacenie 
                                    // chybovych hlasok
    private String wrapperAppName;
    final String APP_NAME        = "EaSys";
    final String APP_VERSION     = "V3 - ERISS";
    final String APP_DESCRIPTION = "(EaSys Reduced Instructions - SQL Set)";
    final String KERNEL_VERSION  = "3.0_20220301";
    /*
    private Connection       CnWork;
    private Connection       CnOLAP;
    private Connection       CnWWW;
    private IEOC_DB_driver            DBdrvWork;
    private IEOC_DB_driver            DBdrvOLAP;
    private IEOC_DB_driver            DBdrvWWW;
    private String           CnWorkDbType;
    private String           CnOLAPDbType;
    private String           CnWWWDbType;
    */
    private DBconnection     DBcnWork;
    private DBconnection     DBcnOLAP;
    private DBconnection     DBcnWWW;
    private String           workURL; // work connection URL 
    private String           workUser; // work connection username 
    private String           workPwd; // work connection user pasword
    private Desktop          dsk;
    private JPanel           dskPanel;
    private JScrollPane      dskScrollPane;
    private system.cron.Crond    cron;
    private system.cron.Pnl_cron pnl_cron;
    private Pnl_Functions  pnl_functions;
    private system.perm.Permd    perm;
    private system.desktop.Pnl_modules  pnl_mdl;
    public  eoc.cinn.Pnl_cinn  cinn;
    private EASpropertiesFile usrProps; // uzivatelske parametre
    private EASpropertiesFile staProps;
    private Win_chat win_chat;
    private PrintStream stdOutStream;
    private File        fUserOutFile;
    private PrintWriter outFileWriter;
    private Statement mainStm;
    private ResultSet mainRS;  
    private String os_type;
    private String os_version;
    
    private final Icon imgLedAkt = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_act.png"));
    private final Icon imgLedNea = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_nea.png"));
    private final Icon imgLedDis = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_dis.png"));
    private final Icon imgLedErr = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_err.png"));
    private final Icon imgLedNull = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_null.png"));
    
    private Integer iVerboseLevel = 6; // uroven 'ukecanosti jadra'
                                       // hystoricky sa vyvinulo tak 
                                       // ze plati: 6=0 a 0=6 , :o}
    //private final String sSupportedWidgetTypes = 
    private final String sSupportedWidgetTypes = 
            "JTextField,JFormattedTextField,JCheckBox,JComboBox,JRadioButton," + 
            "DTfield,CDTfield,DTtextArea,DTcomboBox,DTXfield,DTcheckBox";
    ArrayList<eoc.communication.CommLink> EOC_Links = new ArrayList<>();

    // Old Technology (V1)
    // ArrayList<EOC_modul> EOC_modules = new ArrayList();
    // New technology (V2)
    ArrayList<Modul> EOC_xmodules = new ArrayList<>();
    Object oEOC_ToolBar_TXN;
    private String extDBtype;
    private String intDBtype;
    private ResultSet rss;  

    // Systemove adresare
    private File parentDirectory;  // hlavny adresar aplikacie
    private File parentSystemDirectory;  // hlavny systemovy adresar aplikacie EaSys
    private File sysDirectory;  // ./sys
    private File usrDirectory;    // ./usr
    private File staDirectory; // ./sta
    private File currUsrDirectory;    // ./usr/<currentUserName>
    private File currStaDirectory; // ./sta/<currentComputerName>
    
    private IEOC_VisualObject actualWho; // aktualny objekt, ziadajuci vyber z DB

    private Statement getQuery_stm;
    private ResultSet getQuery_rst;
    private String    currentUserName     = "default";
    private String    currentComputerName = "default";
    private eoc.readers.Frm_FileViewer logFrm; // prezerac log-suboru aplikacie
    private String metaHeaderDelimiter = "⌂"; // ASCII 127
    private DateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//    private JOptionPane msgOptionPane = new JOptionPane();
    private static String[] sc = {"M","0","L","2","C","4","D","5","X","6"}; //default-cryptkey
    private String notFlushedOut = ""; // OutPrint don uklada vstup, kym nema kam zapisovat
    private JFrame win_mycalendar;

    private boolean startPerm = true;
    private boolean startCron = true;
    // konstruktor jadra EaSys
    public Kernel() {
        os_type = System.getProperty("os.name");
        os_version = System.getProperty("os.version");
        currentComputerName = getComputerName();
        this.OutPrintln("System EaSys:  System kernel CREATED\n"
                      + "Kernel version: " + KERNEL_VERSION + "\n"
                      + system.FnEaS.currDateTime() + "\n" 
                      + getAppName() + " " + getAppVersion() + "\n"
                      + getAppDescription()+ "\n\n"
                      + "Running on: " + os_type + " version:" + os_version);

        this.checkDirectories();
    } // koniec konstruktora jadra

    public Kernel(boolean prm, boolean crn) {
        startPerm = prm;
        startCron = crn;
        os_type = System.getProperty("os.name");
        currentComputerName = getComputerName();
        this.OutPrintln("System EaSys   System kernel CREATED\n\n" 
                      + "Kernel version: " + KERNEL_VERSION + "\n\n"
                      + system.FnEaS.currDateTime() + "\n" 
                      + getAppName() + " " + getAppVersion() + "\n"
                      + getAppDescription() + "\n\n"
                      + "Running on: " + os_type + " version:" + os_version);
        this.checkDirectories();
    } // koniec konstruktora jadra

    public String get_os_type() {
       return os_type;    
    }
    
    public String get_os_slash() {
       return (os_type.toUpperCase().equals("LINUX")?"/":"\\");    
    }

    public boolean setUserProperty (String propName, String PropValue) {
        checkPropFiles();
        return usrProps.setProperty(propName, PropValue);
    }
    
    public String getUserProperty (String propName) {
    checkPropFiles();
        //// System.out.println("NULLusrProps? = " + (usrProps==null) + " GETTpropName = " + propName);
        return usrProps.getProperty(propName);
    }
    
    public boolean setStationProperty (String propName, String PropValue) {
        checkPropFiles();
        return staProps.setProperty(propName, PropValue);
    }
    
    public String getStationProperty (String propName) {
        checkPropFiles();
        return staProps.getProperty(propName);
    }
    
    public void setWrapperAppName(String wrpAppName) {
        wrapperAppName = wrpAppName;
        if (!wrapperAppName.equals(wrpAppName))
        OutPrintln("\nKernel: Setting wrapper application to: " + 
                (wrpAppName==null?"null":wrpAppName) + "\n");
    }
    
    public String getWrapperAppName() {
        return wrapperAppName;
    }
    
    public void initialize() 
         throws NoSuchMethodException, FileNotFoundException, IOException,
                IllegalAccessException, IllegalArgumentException,
                InvocationTargetException,
                BiffException,
                SQLException {
       this.OutPrintln("\nKernel: INIT .... OK." 
               + "\n\nKernel -> Accepted user:     " + currentUserName  
                 + "\nKernel -> Accepted computer: " + currentComputerName + "\n"
       );
       usrProps = null; // nulovanie defaultu
       checkPropFiles();
/*
       cron  = new system.cron.Crond();
       cron.setKernel(this);
       perm  = new system.perm.Permd();
       perm.setKernel(this);
       perm.setCurrentUser(currentUser);
       */
            /*
            DBdrvWork = new DBdrv(); // ovladac pracovnej databazy
            DBdrvOLAP = new DBdrv(); // ovladac pracovnej databazy
            DBdrvWWW  = new DBdrv(); // ovladac pracovnej databazy
            DBcnWork.getDbDriver().setKrn(this);
            DBdrvOLAP.setKrn(this);
            DBdrvWWW.setKrn(this);
            */
            this.OutPrintln("\nStarting universal EOC database driver ... OK\n");
            this.OutPrintln("EOC database driver: supported databases:");
            this.OutPrintln("\tPostgres 9.0++ (translating SQL instructions for Postgres 9.2 jdbc4-driver)");
            this.OutPrintln("\tMySql 5.0++ (translating SQL instructions for Mysql 5.1.23 java-connector driver)");
            this.OutPrintln("\tFirebird 2.5++ (translating SQL instructions for Jaybird 2.2.7 driver)");
            this.OutPrintln("\tSQLanywhere 12.0++ (Sybase - translating SQL instructions for sajdbc/sajdbc4 drivers)\n");
//               krn.setUserProperty("DefaultFont", f.getFontName() + "#" + f.getStyle() + "#" + f.getSize());
       cinn = new eoc.cinn.Pnl_cinn();
       cinn.setKrn(this);
       cinn.setDsk(dsk);
       /*
            DBcnWork.getDbDriver().setKrn(this);
            DBdrvOLAP.setKrn(this);
            DBdrvWWW.setKrn(this);
       */
       if (DBcnWork != null) DBcnWork.setKernel(this);
       ////this.OutPrintln("CnWorkiiis----" + (CnWork==null));
       //DBcnWork.getDbDriver().setConn(CnWork);
       if (DBcnOLAP != null) DBcnOLAP.setKernel(this);
       // if (CnOLAP != null) DBdrvOLAP.setConn(CnOLAP);
       if (DBcnWWW != null) DBcnWWW.setKernel(this);
       // if (CnWWW != null) DBdrvWWW.setConn(CnWWW);
       if (startCron) {
           this.OutPrintln("Starting CROND ... OK\n");
           cron  = new system.cron.Crond();
           cron.setKernel(this);
       }
       else
           this.OutPrintln("Starting CROND ... CRON LAYER DISABLED BY INVOKING SYSTEM.\n");

       if (startPerm) {
           this.OutPrintln("Starting PERMD ... OK\n");
           perm  = new system.perm.Permd();
           perm.setKernel(this);
           perm.setWrapperAppName(wrapperAppName);
           perm.setCurrentUser(currentUserName);
       }
       else
           this.OutPrintln("Starting PERMD ... PERM LAYER DISABLED BY INVOKING SYSTEM.\n");

       if (startCron) {
           cron.setDbConnections(DBcnWork,DBcnOLAP,DBcnWWW);
           cron.setPnl_cron(pnl_cron);
           cron.setPnl_functions(pnl_functions);
           cron.setKernel(this);
           cron.initialize();
       }
       if (startPerm) {
           perm.setDbConnections(DBcnWork,DBcnOLAP,DBcnWWW);
           perm.initialize();
       }
       if (dsk != null && dsk.getRunLevel().equals("ADMIN")) {
           try {
              // krnMsg("No tak idem vytvárať základnie tabulkovie.\n\nTvoj Kernel.\n\n");
               DBcnWork.getDbDriver().CreateBaseTables();
              // krnMsg("No tak som sa teda incializoval, bazmeg !\n\nTvoj Kernel.\n\n"
              //      + "CnIn=" + CnIn.getMetaData().getURL());
            
           } catch (SQLException ex) {
                Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
           }
       }

    }
    
    private void checkPropFiles() {
  ////      System.out.println("checkPropFilesIS: usrProps>" + (usrProps == null)
  /////       + "staProps>" + (staProps == null) );
       if (usrProps == null)
           try {
               usrProps = new EASpropertiesFile(getUsrDir("./" + currentUserName + "/properties.prp"), this);
       } catch (FileNotFoundException ex) {
           Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
           Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
       }
       if (staProps == null)
           try {
               staProps = new EASpropertiesFile(getStaDir("./" + currentComputerName + "/properties.prp"), this);
       } catch (FileNotFoundException ex) {
           Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
           Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
       }
    }
    
    public void initOutStream() {
        try {
////            String userOutFileName = "usr/" + DBcnWork.getUsr() + "/out.log";
            String userOutFileName = getUsrDir("./" + currentUserName + "/out.log");
            System.out.println("Redirecting output to file: " + userOutFileName + " ...  OK");
            fUserOutFile = new File(userOutFileName);
            if (!fUserOutFile.exists()) fUserOutFile.createNewFile();
            stdOutStream  = new PrintStream(fUserOutFile,"windows-1250");
            outFileWriter = new PrintWriter(stdOutStream, true);
        } catch (IOException ex) {
            System.out.println("Redirecting output to file:cathIO " + " ...  OK");
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void OutPrintln(Object o) {
        OutPrint(o.toString() + "\n");
    }
    
    public void OutPrintln(String txt) {
        OutPrint(txt + "\n");
    }
    
    public void OutPrint(Object o) {
        OutPrint(o.toString());
    }

    public void OutError(Exception ex) {
        OutPrint(ex.getCause());
    }

    public void OutError(SQLException ex) {
        System.out.println("Xex.getCause() => " + ex.getMessage());
        OutPrint(ex.getMessage());
    }

    public void OutPrint(String txt) {
        if (txt == null) txt = "null";
        if (outFileWriter==null) {
            if ((txt.equals("null")) || txt.trim().equals(""))  {
               return;
            }
            else {
               // System.out.println("\nNo suitable outFileWriter available -> outFileWriter is null for message: \n\t" + txt);
               notFlushedOut += (txt==null?"NULL":txt); 
               System.out.print(txt);
               return;
                
            }
        }
        else {
            if (notFlushedOut.length() > 0) {
                outFileWriter.print(notFlushedOut);
                notFlushedOut = "";
            }
            outFileWriter.print(txt);
            outFileWriter.flush();
            System.out.print("  " + txt);
        }
    }
    
    public void WriteEventToLog(String evtText) {
       // Calendar.getInstance().
        this.OutPrintln(evtText);
        if (win_chat != null)
            win_chat.getPnl_chat().addLogRow(system.cron.Crond.getTimeStamp() + " == " + evtText);
    }
    
    public void showChat() {
        System.out.println("SHOWCHAT");
        win_chat.setVisible(true);
        win_chat.setState(JFrame.NORMAL);
                
    }
    
    public void setCronState(String state) {
        cron.setState(state);
    }
    
    public void debugOut(Object obj, Integer iLevel, String sMsg) {
        if (iLevel >= iVerboseLevel) {
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[2];
            String methodName = e.getMethodName();
            this.OutPrintln(">>" + obj.getClass().getName() + "->"
                    + methodName + " -- " + sMsg);
        }
    }
    
    public int MessageOut(Object obj, Integer iLevel, String sMsg) {
       debugOut(obj, iLevel, sMsg);
       return Message (sMsg, obj.toString());
    }

    public void dskMessage(String sMsg) {
       dsk.dskMessage(sMsg);
    }

    public void dskMessageClear(String sMsg) {
       dsk.dskMessageClear(sMsg);
    }
    
    public void dskSetActivity(String lbl, String sMsg,int akt,int max) {
       dsk.dskSetActivity(lbl, sMsg,akt,max);
    }

    public void dskSetActivity(int akt,int max) {
       dsk.dskSetActivity(akt,max);
    }
    
    public static void staticMsg(String sMsg) {
       ////JOptionPane.showMessageDialog(null,sMsg," STAT_TIT",
       ////JOptionPane.PLAIN_MESSAGE);
       staticMsg("",sMsg,"");
    }

    public static void staticMsg(String sMsg, String sTitle) {
       ////JOptionPane.showMessageDialog(null,sMsg," STAT_TIT",
       ////JOptionPane.PLAIN_MESSAGE);
       staticMsg("",sMsg,sTitle);
    }

    public static void staticMsg(String sType, String sMsg, String sTitle) {

        String sMsgType = "";

      if (!sType.equals("")) { sMsgType = sType.substring(0,1).toUpperCase(); }

      if (sTitle == null || sTitle.trim().equals(""))
            sTitle = "Hlásenie"; // + wrapperAppName + " (EaSys-descendant)";
        
      JOptionPane optionPane = new JOptionPane(sMsg);
      switch(sMsgType) {
          case  "E": {
             if (sTitle==null) {sTitle = "Chyba !";}
             JDialog ddialog = optionPane.createDialog(sTitle);
           //  ddialog.
             ddialog.setAlwaysOnTop(true);
             ddialog.setVisible(true); 
//             JOptionPane.showMessageDialog(null,sMsg,sTitle + " STAT_E",
//             JOptionPane.ERROR_MESSAGE);
             break;
          }
          case  "W": {
             if (sTitle==null) {sTitle = "Pozor !";}
             JDialog ddialog = optionPane.createDialog(sTitle);
             ddialog.setAlwaysOnTop(true);
             ddialog.setVisible(true); 
//             JOptionPane.showMessageDialog(null,sMsg + " STAT_W",sTitle + " STAT_W",
//             JOptionPane.WARNING_MESSAGE);
             break;
          }
          case  "I": {
             if (sTitle==null) {sTitle = "Informácia";}
             JDialog ddialog = optionPane.createDialog(sTitle);
             ddialog.setAlwaysOnTop(true);
             ddialog.setVisible(true); 
           //  JOptionPane.showMessageDialog(null,sMsg,sTitle + " STAT_I",
           // JOptionPane.INFORMATION_MESSAGE);
             break;
          }
          default: {
             if (sTitle==null) {sTitle = "";}
             JDialog ddialog = optionPane.createDialog(sTitle);
             ddialog.setAlwaysOnTop(true);
             ddialog.setVisible(true); 
            // JOptionPane.showMessageDialog(null,sMsg,sTitle + " STAT_D",
            // JOptionPane.PLAIN_MESSAGE);
          }    
          
      }  // switch(sMsgType) {
     } // public static void krnStaticMsg( ...

    public int Message(String sMsg) {
       return Message("",sMsg,"");
    }

    public int Message(String sMsg, String sTitle) {
       return Message("",sMsg,sTitle);
    }
/*
    public int Message(Component parent, String sType, String sMsg, String sTitle) {
        return 0;x
    }
    */
    public int Message(String sType, String sMsg, String sTitle) {
      int selection = 0;
      String sMsgType = "I";
      if ((!sType.equals("")) && (sType!=null)) { sMsgType = sType.substring(0,1).toUpperCase(); }
      int iMsgType = JOptionPane.INFORMATION_MESSAGE;
        if (sMsgType.equalsIgnoreCase("W"))      iMsgType = JOptionPane.WARNING_MESSAGE;
        else if (sMsgType.equalsIgnoreCase("E")) iMsgType = JOptionPane.ERROR_MESSAGE;
        else if (sMsgType.equalsIgnoreCase("Q")) iMsgType = JOptionPane.QUESTION_MESSAGE;
        else iMsgType = JOptionPane.INFORMATION_MESSAGE;
        
        // aktualny parent-objekt
        Window w = getSelectedWindow(Window.getWindows());
        ////System.out.println("NULL_WW:" + (w==null) + " " + Window.getWindows().length);
        
        if (iMsgType == JOptionPane.QUESTION_MESSAGE)
            selection = JOptionPane.showConfirmDialog(w, sMsg, sTitle /* + "NEWQ" */,JOptionPane.YES_NO_OPTION, iMsgType);
        else
            JOptionPane.showMessageDialog(w, sMsg, sTitle /* + "NEWI" */, iMsgType);
        
        return selection;
    }    
    
    public int Message (Object obj, String sType, String sMsg, String sTitle) {
        String sMsgType = "";
      if (!sType.equals("")) { sMsgType = sType.substring(0,1).toUpperCase(); }
      return Message (sType,sMsg,sTitle);
     } // public void krnMsg( ...
    
    public int MessagePrn(String sMsg) {
       this.OutPrintln(sMsg);
       return Message (sMsg);
    }

Window getSelectedWindow(Window[] windows) {
    Window result = null;
    for (int i = 0; i < windows.length; i++) {
        Window window = windows[i];
        //System.out.println("window.getName()=>>=>" + window.getName() + " isActive()==> " + window.isActive());
        if (window.isActive()) {
            result = window;
            break;  // robo 2017-5-29
        } else {
            Window[] ownedWindows = window.getOwnedWindows();
            if (ownedWindows != null) {
                result = getSelectedWindow(ownedWindows);
               if (result != null) break; // robo 2017-5-29
            }
        }
    }
    return result;
}     
    
    public boolean krnQuest(String sType, String sMsg, String sTitle) {
       
       int iDialType = JOptionPane.INFORMATION_MESSAGE;
       
       int iOut;
       iOut = Message("QN", sMsg, sTitle);
       /**
       Object[] volby = {"Ano","Nie"};

       sType = sType.toUpperCase();
       
       if (sType.contains("I")) {iDialType = JOptionPane.INFORMATION_MESSAGE;}
       else if (sType.contains("Q")) {iDialType = JOptionPane.QUESTION_MESSAGE;}
       else if (sType.contains("W")) {iDialType = JOptionPane.WARNING_MESSAGE;}
       else if (sType.contains("E")) {iDialType = JOptionPane.ERROR_MESSAGE;}

       
        iOut = JOptionPane.showOptionDialog(dsk.getRootPane(), sMsg, sTitle,
                JOptionPane.YES_NO_OPTION,iDialType,null,volby,
               (sType.contains("Y")?volby[0]:volby[1]));
       */
       return (iOut==0 ? true : false);
    }
 
    public static boolean QMsg(String sType, String sMsg, String sTitle) {
       
       int iDialType = JOptionPane.INFORMATION_MESSAGE;
       
       int iOut = 1;

       Object[] volby = {"Ano","Nie"};

       sType = sType.toUpperCase();
       
        /* 
        JOptionPane pane = new JOptionPane(); 
        JDialog dialog = pane.createDialog("Hi there!"); 
        dialog.setAlwaysOnTop(true); 
        dialog.show(); 
        */        
       if (sType.contains("I")) {iDialType = JOptionPane.INFORMATION_MESSAGE;}
       else if (sType.contains("Q")) {iDialType = JOptionPane.QUESTION_MESSAGE;}
       else if (sType.contains("W")) {iDialType = JOptionPane.WARNING_MESSAGE;}
       else if (sType.contains("E")) {iDialType = JOptionPane.ERROR_MESSAGE;}

        iOut = JOptionPane.showOptionDialog(null /*dsk.getRootPane()*/, sMsg, sTitle,
                JOptionPane.YES_NO_OPTION,iDialType,null,volby,
               (sType.contains("Y")?volby[0]:volby[1]));
       
       return (iOut==0 ? true : false);
    }

    public void setDsk(Desktop dsk) {
        this.dsk = dsk;
    }

    public void setDskPane(JPanel dpn) {
        this.dskPanel = dpn;
    }
    
    public void setScrollPane(JScrollPane dspn) {
        this.dskScrollPane = dspn;
    }
    
    public void setPnl_cron(Pnl_cron dpn) {
        this.pnl_cron = dpn;
        pnl_cron.setKrn(this);
    }
    
    public void setPnl_functions(Pnl_Functions dpn) {
        this.pnl_functions = dpn;
        pnl_functions.setKrn(this);
    }
    
    public void setWin_chat(Win_chat cht) {
        win_chat = cht;
        win_chat.setKrn(this);
    }

    public DBconnection getDBcnWork() {
        return DBcnWork;
    }
    
    public DBconnection getDBcnOLAP() {
        return DBcnOLAP;
    }

    public DBconnection getDBcnWWW() {
        return DBcnWWW;
    }
   
/*
    public void setDbDrivers(IEOC_DB_driver cWrk, IEOC_DB_driver cOla, IEOC_DB_driver cWw) {
        if (cWrk != null) { 
            DBdrvWork = cWrk;
            AAAA
            DBcnWork = cWrk.ge
            CnWork
        }
        else {
        }

        if (cOla != null) DBdrvOLAP = cOla;
        if (cWw != null) DBdrvWWW  = cWw;
    }
*/
    /*      
    public void setConnections(DBconnection cWrk, DBconnection cOla, DBconnection cWw) {
        
        if (cWrk != null) this.CnWork = cWrk.getConn();
//         2016-1-3 -- spustit eoc.database.sybase.tools.set_server_options.sql
//        try {
//            Statement stm = CnWork.createStatement();
//            stm.execute("set option MAX_STATEMENT_COUNT = 150;");
//            stm.execute("set option MAX_CURSOR_COUNT = 150;");
//        } catch (SQLException ex) {
//            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
//        CnWork.set. .MAX_STATEMENT_COUNT = 
        if (cOla != null) this.CnOLAP = cOla.getConn();
        if (cWw != null) this.CnWWW  = cWw.getConn();
//        try {
//            mainStm = CnWork.createStatement();
//            try {
//            CnWork.setClientInfo(intDBtype, APP_NAME); //.setClientInfo("ApplicationName","EaSys");
//            } catch (SQLClientInfoException ex) {
//            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
//            }
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
*/
    public DBconnection getDBconn(String connType /*Work/OLAP/WWW*/ )  {
        if (connType.equalsIgnoreCase("Work")) return DBcnWork;
        else if (connType.equalsIgnoreCase("OLAP")) return DBcnOLAP;
        else if (connType.equalsIgnoreCase("WWW")) return DBcnWWW;
        else return null;
    }

    public void setDbConnections(DBconnection cWrk, DBconnection cOla, DBconnection cWw) 
        throws FileNotFoundException, IOException, SQLException {
        this.DBcnWork = cWrk;
        if (cWrk != null) DBcnWork.setKernel(this);
        
        this.DBcnOLAP = cOla;
        if (cOla != null) DBcnOLAP.setKernel(this);
        this.DBcnWWW  = cWw;
        if (cWw != null) DBcnWWW.setKernel(this);
        currentUserName = DBcnWork.getUsr();
        /*
        setConnections(DBcnWork, 
                ((DBcnOLAP!=null)?DBcnOLAP:null),
                ((DBcnWWW!=null)?DBcnWWW:null));
        */
        ////if (usrProps == null) usrProps = 
        ////      new EASpropertiesFile("./usr/" + currentUser + "/properties.prp");
        checkPropFiles();        
        if (dsk!=null) dsk.evtDBconnectionsChanged();
        //staProps = new EASpropertiesFile(""); // nazovDB
    }

    public void setVerbose(Integer iVerbLevel) {
        iVerboseLevel = iVerbLevel;
       this.OutPrintln("krn->Setting verboselevel to: " + iVerbLevel);
    }

    public void exit(Integer iState) {
         // QQQ sem patri spracovanie riadeneho ukoncenia spustenych modulov
         // v programov, beziacich v nich.
        System.exit(iState);
    }

    public void EOC_XAddModul(String sMDL_in, String sMDL_TOOLTIP_in, String sState)
         throws NoSuchMethodException, IOException, FileNotFoundException, 
                IllegalAccessException, IllegalArgumentException, 
                InvocationTargetException,
                URISyntaxException {
        final Modul xMdl = new Modul(sMDL_in, sMDL_TOOLTIP_in, sState, this);
        EOC_xmodules.add(xMdl);
        setWidgetFonts(xMdl.mbar_MDL, defaultFont);
        this.OutPrintln("Kernel.EOC_XAddModul() -- mounting: " + sMDL_in);
        JButton btn = (JButton) xMdl.getoBtn_MDL();
        btn.setToolTipText(sMDL_TOOLTIP_in);
        btn.setFont(defaultFont);
        setBtnMDLicon(btn, sState); // nastavi potrebnu ikonu pre tlacitko
        if (sState.equals("NONE")) {
            btn.setEnabled(false);
        }
        eoc.widgets.VTabbedPane vtp = (eoc.widgets.VTabbedPane) xMdl.getoVtp_MDL();
        vtp.setVisible(false);
        this.setWidgetFonts((Container) vtp, defaultFont);

        dskPanel.add(vtp, BorderLayout.CENTER);
        pnl_mdl.addButton(btn);
        pnl_mdl.validate();
        btn.setVisible(true);
    }
    
    public void setPnl_mdl(system.desktop.Pnl_modules pnlmdl) {
       pnl_mdl = pnlmdl;
    }
    
    public void EOC_AddLink(Object oVCntr, Object oVSrc,
            Object oVTrg, String sLink, String sState) {
        debugOut(this, 5, "Adding " + sLink + " link");
        if (sState==null) sState = "ENABLED";
        EOC_Links.add(new CommLink(oVCntr,  oVSrc,
                 oVTrg, sLink.toUpperCase(), sState.toUpperCase()));
    }
    
    /* vrati objekt na opacnej strane linku podla hodnoty sVector */
    public Object getLinkPartner(Object oVQuest,
            String sLink, String sVector) {
        sLink = sLink.toUpperCase();
        sVector = sVector.toUpperCase();
        Iterator itr = EOC_Links.iterator();
        Integer i;
        i = 0;
        link_read: /* prechod pola EOC_Linkov */
        while (itr.hasNext()) {
            Object element = itr.next();
            i = i + 1;
            // ziskanie link-objektu
            final CommLink lnk = (CommLink) element;
            // link nie je pozadovaneho typu, jede se dal
            if (!lnk.sEOC_Link.equals(sLink)) { continue link_read; }
            // TARGE hlada SOURCE
            if (sVector.equals("SOURCE")) {
               // oVQuest je typu oVTarget (vacsinou)
               if (oVQuest != null && lnk.oVTarget.equals(oVQuest)
                   && lnk.sEOC_Link.equals(sLink)) { // je to spravny Link
                   return lnk.oVSource;
               }
            }
            if (sVector.equals("TARGET")) {
               // oVQuest je typu oVTarget (vacsinou)
               if (oVQuest != null && lnk.oVSource.equals(oVQuest)
                   && lnk.sEOC_Link.equals(sLink)) { // je to spravny Link
                   return lnk.oVTarget;
               }
            }
       } // while (itr.hasNext()) {

        return null;
    }
    

    // ATTI - volane predtym, ako sa vypne tab
    // OTEC - malo by sa to presmerovat spat do programu na tab-e,
    //    na spracovanie udalosti
    public void TabClosing(String nazovModulu, String nazovTabu, Component comp) {
        debugOut(this, 5, "rusim " + nazovModulu + ", " + nazovTabu);
        ((eoc.IEOC_VisualObject) comp).destroy(); // 2015-8-20
    }
    
//    public void TabChanging (String nazovModulu, String nazovTabu, Component comp) {
    public void TabChanging (String nazovModulu, String nazovTabu, VTabbedPane.TabListComponent tbc) {
        //VTabbedPane.TabListComponent tbc
        debugOut(this, 5, " menim vyber tab-u v module: " + nazovModulu + ", na: " + nazovTabu);
        ////System.out.println("Kernel: menim vyber tab-u v module: " + nazovModulu + ", na: " + nazovTabu);
        setCurrentTabListComponent(tbc);
    }

    private void setCurrentTabListComponent(VTabbedPane.TabListComponent tbc) {
        if (tbc != null) {
            if (tbc.myComponent instanceof IEOC_ReportSource)
                pnl_functions.setPrintTarget((IEOC_ReportSource) tbc.myComponent);
            else 
                pnl_functions.setPrintTarget(null);
            if (tbc.myComponent instanceof IEOC_VisualObject)
                pnl_functions.setTargetEocVisualOject((IEOC_VisualObject) tbc.myComponent);
            else 
                pnl_functions.setTargetEocVisualOject(null);
        }
        else {
            pnl_functions.setPrintTarget(null);
            pnl_functions.setTargetEocVisualOject(null);
        }
    }

    public void krn_disableWidgets(JPanel jp) {
        Component[] compArray = jp.getComponents();  // widgety JFrame-kontainera 
        
////        System.out.println("DISSABBLNG_FROMPANNEL1: "  + compArray.length + "  ..>> " + jp.toString());
        krn_disableWidgets(compArray);
////        System.out.println("DISSABBLNG_FROMPANNEL2: "  + compArray.length + "  ..>> " + jp.toString());
        jp.repaint();
       // krnMsg("widgets was disabled");
    }

    public void krn_disableWidgets(javax.swing.JScrollPane sp) {
        Component[] compArray = sp.getComponents();  // widgety JFrame-kontainera 
        krn_disableWidgets(compArray);
        sp.repaint();
       // krnMsg("widgets was disabled");
    }

    public void krn_disableWidgets(javax.swing.JViewport wp) {
        Component[] compArray = wp.getComponents();  // widgety JFrame-kontainera 
        krn_disableWidgets(compArray);
        wp.repaint();
       // krnMsg("widgets was disabled");
    }

    public void krn_disableWidgets(Component[] c) {
        Class cl;        // trieda skumaneho objektu
        String sname;    // nazov typu skumaneho objektu
        o_for:
        for (Object o : c) {
            if (o instanceof JLabel) continue;  // Labely sa netestuju

            cl = o.getClass();
            sname = cl.getSimpleName();
      
            if (o instanceof JPanel) {
                javax.swing.JPanel jpn = (javax.swing.JPanel) o;
                krn_disableWidgets(jpn);
                jpn.repaint();
                continue o_for;
            }

            if (o instanceof JScrollPane) {
                javax.swing.JScrollPane sp = (javax.swing.JScrollPane) o;
                javax.swing.JViewport vp = sp.getViewport();
                krn_disableWidgets(vp);
                sp.repaint();
                continue o_for;
            }
            
            if (o instanceof JViewport) {
                javax.swing.JViewport vp = (javax.swing.JViewport) o;
            this.OutPrintln("testdisabling: redirecting viewport begin");
                krn_disableWidgets(vp);
            this.OutPrintln("testdisabling: redirecting viewport end");
                vp.repaint();
                continue o_for;
            }
       //Class cl;        // trieda skumaneho objektu
       //cl = o.getClass();
       /*
            this.OutPrintln("testdisabling:"
                 + "  ComponentType:" + o.getClass().getComponentType()
                 + "   Class.getName():" + o.getClass().getName()
                 + "   Class.getCanonicalName():" + o.getClass().getCanonicalName()
                 );
                    */
            
////            System.out.println("DISSABBLNG: " + sname);
            if (FnEaS.iLookup(sname,sSupportedWidgetTypes,",") >  0) {
            switch (sname.toString()) {
                case "DTXfield": {
                    eoc.widgets.DTXfield myObj = (eoc.widgets.DTXfield) o;
                    myObj.setDisabledTextColor(Color.BLUE);
                    myObj.setEnabled(false);
                    break;
                }
                case "DTfield": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                    myObj.setDisabledTextColor(Color.BLUE);
                    myObj.setEnabled(false);
////                    System.out.println("DISSABBLNG---->: " + myObj.getDbFieldName());
////                    if (myObj.getDbFieldName().equals("c_cas_hlasenia")) {
////                        krnMsg("nu asko totovyzseraa?");
////                    }
                    break;
                }
                case "DTtextArea": {
                    eoc.widgets.DTtextArea myObj = (eoc.widgets.DTtextArea) o;
                    ////System.out.println("TEXTTAR-CO");
                    myObj.setForeground(Color.BLUE);
                    myObj.setDisabledTextColor(Color.BLUE);
                    //myObj.setEnabled(false);
                    myObj.setEditable(false);
////                    System.out.println("DISSABBLNG---->: " + myObj.getDbFieldName());
                    break;
                }
                case "DTcomboBox": {
                    eoc.widgets.DTcomboBox myObj = (eoc.widgets.DTcomboBox) o;
                    ////System.out.println("cobodisabling: " + myObj.getDbFieldName());
                    myObj.setForeground(Color.BLUE);
                    //myObj.setDisabledTextColor(Color.BLUE);
                    //myObj.setEnabled(false);
                    myObj.setEnabled(false);
////                    System.out.println("DISSABBLNG---->: " + myObj.getDbFieldName());
                    /////System.out.println("disaablujem kombox");
                    break;
                }
                case "DTcheckBox": {
                    eoc.widgets.DTcheckBox myObj = (eoc.widgets.DTcheckBox) o;
                    ///// System.out.println(">>>>chebodisabling: " + myObj.getDbFieldName());
                    myObj.setForeground(Color.BLUE);
                    //myObj.setDisabledTextColor(Color.BLUE);
                    //myObj.setEnabled(false);
                    myObj.setEnabled(false);
////                    System.out.println("DISSABBLNG---->: " + myObj.getDbFieldName());
                    /////System.out.println("disaablujem kombox");
                    break;
                }
                case "JTextField":
                /* preskoci na JFormattedTextField */
                case "JFormattedTextField": {
                    JTextField myObj = (JTextField) o;
                    myObj.setDisabledTextColor(Color.BLUE);
                    myObj.setEnabled(false);
                    break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                    myObj.setEnabled(false);
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    myObj.setEnabled(false);
                    break;
                }
                case "JRadioButton": {
                    JRadioButton myObj = (JRadioButton) o;
                    myObj.setEnabled(false);
                    break;
                }
                case "CDTfield": {
                    /**
                    EOC_CDTfield myObj = (EOC_CDTfield) o;
                    myObj.setDisabledTextColor(Color.BLUE);
                    myObj.setEnabled(false);
                    * */
                    break;
                }
                default:
                    break;
            } // switch (s) {
        
            }
            if (o instanceof eoc.iEOC_DBtableField) {
              //  o.setEnabled(false);
            }
        }
       // krnMsg("widgets was disabled");
    }
    
    public void krn_enableWidgets(javax.swing.JPanel jp, String sTxnType) {
        ////System.out.println("EnablingComponentWidgetForPanel: " + jp.toString());
        Component[] compArray = jp.getComponents();  // widgety JFrame-kontainera 
        krn_enableWidgets(compArray, sTxnType);
        jp.repaint();
       // krnMsg("widgets was disabled");
    }

    public void krn_enableWidgets(javax.swing.JScrollPane sp, String sTxnType) {
        ////System.out.println("EnablingComponentWidgetForScrollPane: " + sp.toString());
        Component[] compArray = sp.getComponents();  // widgety JFrame-kontainera 
        krn_enableWidgets(compArray, sTxnType);
        sp.repaint();
       // krnMsg("widgets was disabled");
    }

    public void krn_enableWidgets(javax.swing.JViewport wp, String sTxnType) {
        ////System.out.println("EnablingComponentWidgetForViewport: " + wp.toString());
        Component[] compArray = wp.getComponents();  // widgety JFrame-kontainera 
        krn_enableWidgets(compArray, sTxnType);
        wp.repaint();
       // krnMsg("widgets was disabled");
    }
    
    public void krn_enableWidgets(Component[] c, String sTxnType) {
        Class cl;        // trieda skumaneho objektu
        String s;        // nazov typu skumaneho objektu
        o_for:
        for (Object o : c) {
            if (o instanceof JLabel) continue; // Labely sa netestuju
            /////System.out.println("EnablingComponentWidget: " + o.getClass());

            cl = o.getClass();
            s = cl.getSimpleName();
            ////System.out.println("krn_enableWidgets_class_simpleName==" + cl.getName() + "____" + s);

            if (o instanceof JPanel) {
                javax.swing.JPanel jpn = (javax.swing.JPanel) o;
                krn_enableWidgets(jpn, sTxnType);
                jpn.repaint();
                continue o_for;
            }

            if (s.toString().equals("JScrollPane")) {
                javax.swing.JScrollPane sp = (javax.swing.JScrollPane) o;
                javax.swing.JViewport vp = sp.getViewport();
                krn_enableWidgets(vp, sTxnType);
                sp.repaint();
                continue o_for;
            }
            
            if (s.toString().equals("JViewport")) {
                javax.swing.JViewport vp = (javax.swing.JViewport) o;
                krn_enableWidgets(vp, sTxnType);
                vp.repaint();
                continue o_for;
            }

            //this.OutPrintln("testdisabling " + s.toString());
            //cl.getComponentType()
            if (FnEaS.iLookup(s,sSupportedWidgetTypes,",") >  0) {
            //System.out.println("krneenab: " + s);
             switch (s) {
                case "DTXfield": {
                    eoc.widgets.DTXfield myObj = (eoc.widgets.DTXfield) o;
                    ////this.OutPrintln("krn_enableWidgets->myObj.getDbEnableWhen()=" + myObj.getDbEnableWhen());
                    if (!myObj.getDbEnableWhen().equals("-")) {
                       DefaultFormatter f = (DefaultFormatter) myObj.getFormatter();
                       myObj.setForeground(Color.BLACK);
                       myObj.setEnabled(true);
                       myObj.setEditable(false);
                       //myObj.setMainFormatter();
                       //f.setOverwriteMode(false);
                    }
                    break;
                }
                case "DTfield": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                    ////this.OutPrintln("krn_enableWidgets->myObj.getDbEnableWhen()=" + myObj.getDbEnableWhen());
                    if (!myObj.getDbEnableWhen().equals("-")) {
                       DefaultFormatter f = (DefaultFormatter) myObj.getFormatter();
                       myObj.setForeground(Color.BLACK);
                       myObj.setEnabled(true);
                       //myObj.setMainFormatter();
                       //f.setOverwriteMode(false);
                    }
                    break;
                }
                case "DTtextArea": {
                    eoc.widgets.DTtextArea myObj = (eoc.widgets.DTtextArea) o;
                    ////System.out.println("TEXTTAR-CO");
                    myObj.setForeground(Color.BLACK);
                    myObj.setDisabledTextColor(Color.BLACK);
                    //myObj.setEnabled(false);
                    myObj.setEditable(true);
                   //// System.out.println("DTtextArea-enadisa");
                    break;
                }
                case "DTcomboBox": {
                    eoc.widgets.DTcomboBox myObj = (eoc.widgets.DTcomboBox) o;
                    myObj.setForeground(Color.BLACK);
                    // myObj.setDisabledTextColor(Color.BLACK);
                    //myObj.setEnabled(false);
                    myObj.setEnabled(true);
                    myObj.setEditable(false);
                    break;
                }
                case "DTcheckBox": {
                    eoc.widgets.DTcheckBox myObj = (eoc.widgets.DTcheckBox) o;
                    myObj.setForeground(Color.BLACK);
                    // myObj.setDisabledTextColor(Color.BLACK);
                    //myObj.setEnabled(false);
                    myObj.setEnabled(true);
                    break;
                }
                case "CDTfield": {
                    /**
                    EOC_CDTfield myObj = (EOC_CDTfield) o;
                    //this.OutPrintln("krn_enableWidgets->myObj.getDbEnableWhen()=" + myObj.getDbEnableWhen());
                    if (!myObj.getDbEnableWhen().equals("-")) {
                       myObj.setEnabled(true);
                    }
                    * */
                    break;
                }
                case "JTextField": {
                    JTextField myObj = (JTextField) o;
                    myObj.setEnabled(true);
                    break;
                }
                case "JFormattedTextField": {
                    JFormattedTextField myObj = (JFormattedTextField) o;
                    myObj.setEnabled(true);
                    DefaultFormatter f = (DefaultFormatter) myObj.getFormatter();
                    if (f!=null) f.setOverwriteMode(false);
                    break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                    myObj.setEnabled(true);
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    myObj.setEnabled(true);
                    myObj.setEditable(false);
                    break;
                }
                case "JRadioButton": {
                    JRadioButton myObj = (JRadioButton) o;
                    myObj.setEnabled(true);
                    break;
                }
               default:
                    break;
            } // switch (s) {
        
            }
        }
    }  
   public void krn_refreshFormatters(javax.swing.JPanel jp) {
        Component[] compArray = jp.getComponents();  // widgety JFrame-kontainera 
        Class cl;        // trieda skumaneho objektu
        String s;        // nazov typu skumaneho objektu
        o_for:
        for (Object o : compArray) {
            if (o instanceof DTfield) ((DTfield) o).setMainFormatter();
        }
   }
   
    public void krn_clearWidgets(javax.swing.JPanel jp, String sTxnType) {
        Component[] compArray = jp.getComponents();  // widgety JFrame-kontainera 
        krn_clearWidgets(compArray, sTxnType);
        jp.repaint();
       // krnMsg("widgets was disabled");
    }

    public void krn_clearWidgets(javax.swing.JScrollPane jp, String sTxnType) {
        Component[] compArray = jp.getComponents();  // widgety JFrame-kontainera 
        krn_clearWidgets(compArray, sTxnType);
        jp.repaint();
       // krnMsg("widgets was disabled");
    }

    public void krn_clearWidgets(javax.swing.JViewport wp, String sTxnType) {
        Component[] compArray = wp.getComponents();  // widgety JFrame-kontainera 
        krn_clearWidgets(compArray, sTxnType);
        wp.repaint();
       // krnMsg("widgets was disabled");
    }
    
    public void krn_clearWidgets(Component[] c, String sTxnType) {
        Class cl;        // trieda skumaneho objektu
        String s;        // nazov typu skumaneho objektu
        o_for:
        for (Object o : c) {
            cl = o.getClass();
            s = cl.getSimpleName();

            if (o instanceof JPanel) {
                javax.swing.JPanel jpn = (javax.swing.JPanel) o;
                krn_clearWidgets(jpn, sTxnType);
                //krn_enableWidgets(jpn, sTxnType);
                jpn.repaint();
                continue o_for;
            }

            if (s.toString().equals("JScrollPane")) {
                javax.swing.JScrollPane sp = (javax.swing.JScrollPane) o;
                javax.swing.JViewport vp = sp.getViewport();
                krn_clearWidgets(vp, sTxnType);
                sp.repaint();
                continue o_for;
            }
            
            if (s.toString().equals("JViewport")) {
                javax.swing.JViewport vp = (javax.swing.JViewport) o;
                krn_clearWidgets(vp, sTxnType);
                vp.repaint();
                continue o_for;
            }

            //this.OutPrintln("testdisabling " + s.toString());
            //cl.getComponentType()
            if (FnEaS.iLookup(s,sSupportedWidgetTypes,",") >  0) {
             switch (s) {
                case "DTfield": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                 try {
                     // 16.10.2014 myObj.setText("");
                     //// System.out.println("clearing: " + myObj.getDbFieldName());
                     myObj.clear();
                 } catch (ParseException ex) {
                     Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    break;
                }
                case "DTXfield": {
                    eoc.widgets.DTXfield myObj = (eoc.widgets.DTXfield) o;
                   ///// System.out.println("CeeringDTX");
                 try {
                     // 16.10.2014 myObj.setText("");
                     //// System.out.println("clearing: " + myObj.getDbFieldName());
                     myObj.clear();
                 } catch (ParseException ex) {
                     Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    break;
                }
                case "DTtextArea": {
                    eoc.widgets.DTtextArea myObj = (eoc.widgets.DTtextArea) o;
                    //// System.out.println("clearing: " + myObj.getDbFieldName());
                    myObj.clear();
                    break;
                }
                case "DTcomboBox": {
                    eoc.widgets.DTcomboBox myObj = (eoc.widgets.DTcomboBox) o;
                 try {
                     //// System.out.println("clearing: " + myObj.getDbFieldName());
                     myObj.clear();
                 } catch (ParseException ex) {
                     Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    break;
                }
                 case "DTcheckBox": {
                    eoc.widgets.DTcheckBox myObj = (eoc.widgets.DTcheckBox) o;
                 try {
                     //// System.out.println("clearing: " + myObj.getDbFieldName());
                     myObj.clear();
                 } catch (ParseException ex) {
                     Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    break;
                }
               case "JTextField":
                /* preskoci na JFormattedTextField */
                case "JFormattedTextField": {
                    JTextField myObj = (JTextField) o;
                    myObj.setText("");
                    break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                   // myObj.setText("");
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    myObj.setSelectedIndex(0);
                    break;
                }
                case "JRadioButton": {
                    JRadioButton myObj = (JRadioButton) o;
                    myObj.setSelected(false) ;
                    break;
                }
                case "CDTfield": {
                    /*
                    EOC_CDTfield myObj = (EOC_CDTfield) o;
                    // 16.10.2014 myObj.setText("");
                    myObj.clear();
                    */
                    break;
                }
                default:
                    break;
            } // switch (s) {
        
            }
        }
    }  
    
    public String GetObjName(Object oMyObj) {
        return ((oMyObj == null) ? "NULL" : FnEaS.sObjName(oMyObj));
    }

    private Boolean isLinkSource(CommLink lnk, Object src) {
        final IEOC_VisualObject vob;
        vob = (IEOC_VisualObject) src;
        if (vob != null && lnk.oVSource != null && vob.hashCode() == lnk.oVSource.hashCode()) {
            return true;
        }
        return false;
    }

    private Boolean isLinkTarget(CommLink lnk, Object src) {
        final IEOC_Object vob;
        vob = (IEOC_Object) src;
        if (vob != null && lnk.oVTarget != null && vob.hashCode() == lnk.oVTarget.hashCode()) {
            return true;
        }
        return false;
    }

    private IEOC_Object getLinkSource(CommLink lnk) {
        final IEOC_Object vob;
        vob = (IEOC_Object) lnk.oVSource;
        if (vob != null) {
            return (IEOC_Object) vob;
        }
        return null;
    }

    private IEOC_Object getLinkTarget(CommLink lnk) {
        final IEOC_VisualObject vob;
        vob = (IEOC_VisualObject) lnk.oVTarget;
        if (vob != null) {
            return vob;
        }
        return null;
    }

    private String getLinkRole(CommLink lnk, Object src) {
    if (isLinkSource(lnk, src)) {
            return "SOURCE";
        }
        if (isLinkTarget(lnk, src)) {
            return "TARGET";
        }
        return "";
    }
    /*
     * odovzda spravu cielovemu objektu linku v pripade neuspechu vrati
     * neprazdny retazec
     */
    private String sSendMsgToLinkTarget(CommLink lnk, EOC_message eocMsg) {
        final IEOC_VisualObject vob;
        vob = (IEOC_VisualObject) lnk.oVTarget;
        if (vob != null) {
            debugOut(this, 5, "krn sending message " + eocMsg.getMessage()
                    + " owned by " + eocMsg.getOwner().getClass().getName()
                    + " from " + eocMsg.getSender().getClass().getName()
                    + " to " + vob.getClass().getName());
            return vob.receiveMessage(eocMsg);
        }
        debugOut(this, 5, "NOOBJECTDETECTED");
        return "EOC_ERROR=MSG_NOT_SEND";
    }
    /*
     * odovzda spravu cielovemu objektu linku v pripade neuspechu vrati
     * neprazdny retazec
     */
    private String sSendMsgToLinkSource(CommLink lnk, EOC_message eocMsg) {
        final IEOC_VisualObject vob;
        vob = (IEOC_VisualObject) lnk.oVSource;
        if (vob != null) {
            return vob.receiveMessage(eocMsg);
        }
        return "EOC_ERROR=MSG_NOT_SEND";
    }
    /*
     * odovzda spravu objektu na opacnej strane linku v pripade neuspechu vrati
     * neprazdny retazec
     */
    private String sSendMsgToLinkOpposite(CommLink lnk, EOC_message eocMsg) {

        IEOC_VisualObject vob = null;
        if ((Object) lnk.oVSource == eocMsg.getSender()) { vob = (IEOC_VisualObject) lnk.oVTarget; }
        if ((Object) lnk.oVTarget == eocMsg.getSender()) { vob = (IEOC_VisualObject) lnk.oVSource; }
        if (vob != null) {
            return vob.receiveMessage(eocMsg);
        }
        return "EOC_ERROR=MSG_NOT_SEND";
    }
    public void showEOC_links(String sType) {
        if (sType.toUpperCase().equals("LOG")) {
            OutPrintln("\nKernel: Actual EOC_Link structure:");
            OutPrintln("==================================");
            Iterator itr = EOC_Links.iterator();
            while (itr.hasNext()) {
                Object element = itr.next();
                // ziskanie link-objektu
                final CommLink lnk = (CommLink) element;
                String linkStr = "";
 /* povodny kod verzie 1
                linkStr += "Link: " + lnk.sEOC_Link;
                linkStr += " Owner: " + (lnk.oOwnerAppl==null?"NULL":lnk.oOwnerAppl.toString());
                linkStr += " Source: " + (lnk.oVSource==null?"NULL":lnk.oVSource.toString());
                linkStr += " Target: " + (lnk.oVTarget==null?"NULL":lnk.oVTarget.toString());
                linkStr += " State: " + (lnk.sLinkState==null?"NULL":lnk.sLinkState);
                */
                linkStr += "Link: " + lnk.sEOC_Link;
                linkStr += " Owner: " + (lnk.oOwnerAppl==null?"NULL":FnEaS.sObjName(lnk.oOwnerAppl));
                linkStr += " Source: " + (lnk.oVSource==null?"NULL":FnEaS.sObjName(lnk.oVSource));
                linkStr += " Target: " + (lnk.oVTarget==null?"NULL":FnEaS.sObjName(lnk.oVTarget));
                linkStr += " State: " + (lnk.sLinkState==null?"NULL":lnk.sLinkState);

                OutPrintln(linkStr);
            }
            OutPrintln("\n");
        }
        
    }
    
    public String krn_sendMessage(IEOC_Object oSender, EOC_message eocMsg,
                 String sLink, String sVector) {
        /*
         * zavedenim IEOC_VObject (visible) t.j. telo objektu :-) a IEOC_NObject
         * (notvisible) t.j. dusa objektu :-) sa to tu skoplikovalo, ale kaslat
         * na to, pokial tato metoda bude 'semafor'-om, ktory uriadi dalsiu
         * cinnost
         * ****************************************************
         */
         // priklad volania: krn.krn_sendMessage((Object) this,
         //                      "lastRow", "", "navigation", "source", "");
        
        debugOut(this, 0, "Sending begin for message: " + eocMsg.getMessage());
        sLink = sLink.toUpperCase();
        sVector = sVector.toUpperCase();

        Iterator itr = EOC_Links.iterator();
        /* prechod pola EOC_Linkov */
        Integer iNumManagedLinks = 0;
       // String rv = " don't possible to sent for: ";  // return value
       String rv = " return-value not assigned ";  // default value
        link_read:
        while (itr.hasNext()) {
            Object element = itr.next();
            // ziskanie link-objektu
            final CommLink lnk = (CommLink) element;
            if (!lnk.sEOC_Link.equals(sLink) && !sLink.equals("ALL") && !sLink.equals("*")) {
                continue link_read; // link nie je pozadovaneho typu, jede se dal
            }
            final String sLinkRole; // bude to bud TARGET/SOURCE alebo ""
            sLinkRole = getLinkRole(lnk, oSender);
            sLinkRole.toUpperCase();
            /*
             * oSender nie je sucastou aktualneho Link-u, posielanie vsetkym
             * linkom zatial ignorujeme (asi aj natrvalo :-))
             */
            if (sLinkRole.equals("")) {
                continue link_read;
            }
            /*
             * su dve 'TRUE' moznosti: Role sendera je spravna , alebo vektor je
             * "ALL"
             */
            // prvy pripad - typ A - posiela to svojim cielovym objektom
            if (sLinkRole.equals("SOURCE") && sVector.equals("TARGET")) {
                eocMsg.setSender(oSender);
                debugOut(this, 0, "a-SOURCE-TARGET-KRN;" + eocMsg.getMessage());
                iNumManagedLinks = iNumManagedLinks + 1;
                rv = sSendMsgToLinkTarget(lnk, eocMsg);
                //krnMsg("A-rv -link: " + lnk.sEOC_Link + " =>" + sMessage + " =>" + rv + "  o::" + oSender.toString());
            }
            // prvy pripad - typ B - posiela to svojim zdrojovym objektom
            if (sLinkRole.equals("TARGET") && sVector.equals("SOURCE")) {
                eocMsg.setSender(oSender);
                debugOut(this, 0, "b-TARGET-SOURCE-KRN;" + eocMsg.getMessage());
                iNumManagedLinks = iNumManagedLinks + 1;
                rv = sSendMsgToLinkSource(lnk, eocMsg);
                //krnMsg("B-rv -link: " + lnk.sEOC_Link + " =>" + sMessage + " =>" + rv + "  o::" + oSender.toString());
            }
            // druhy pripad - posiela to svojim vsetkym objektom
            if (sVector.equals("ALL")) {
                eocMsg.setSender(oSender);
                debugOut(this, 0, "c-ALL-KRN;" + eocMsg.getMessage());
                iNumManagedLinks = iNumManagedLinks + 1;
                rv = sSendMsgToLinkOpposite(lnk, eocMsg);
                //krnMsg("C-rv -link: " + lnk.sEOC_Link + " =>" + sMessage + " =>" + rv + "  o::" + oSender.toString());
            }
        } // while (itr.hasNext()) {
        
        // dany link momentalne neexistuje, je to v poriadku
        // Vyssie by sa malo testovat aj stav, ked je link disablovany
        if (iNumManagedLinks==0) {
            debugOut(this, 0, "link: " + sLink + " for object: " + GetObjName(oSender) + " not exist. (may not be a problem)");
            return "";
        }

        if (rv.equals("")) {
            debugOut(this, 0, "Empty return value:" + GetObjName(oSender) + (char) 10 + "Message: "
                   + eocMsg.getMessage() + " was successfully sended for: " + sLink + "." + sVector
                   + " (No message=good message - by UNIX/LINUX)");
            return "";
        } else {
            debugOut(this, 5, GetObjName(oSender) + (char) 10 + "Message: "
                   + eocMsg.getMessage() + " don't send properly for: " + sLink + "." + sVector
                   + " returned value: " + rv);
            return (rv.startsWith("NOME-")?"NOME-":"") + "EOC_ERROR=MESSAGE_NOT_SEND_PROPERLY "
                    + "\n\nMessage:" + eocMsg.getMessage() + " for link: " + sLink + 
                    " from object: " + GetObjName(oSender) //+
                    //" to object:"+ GetObjName(oSender)
                     + "\n\nReturn: " + rv;
        }
    }

    public void EOC_startAppl(String sSkrMdl, MenuItem mitm, IEOC_VisualObject oApplPnl,
            Boolean bMultiple /* moze sa pustat viacnasobne */) {

        final Modul myxMdl;
        myxMdl = EOC_getxModul(sSkrMdl);
        String sTtl = mitm.getText();
        final eoc.widgets.VTabbedPane vtp = (eoc.widgets.VTabbedPane) myxMdl.getoVtp_MDL();
        final Component comp;
        comp = (Component) oApplPnl;
        boolean moze = true;
        if (!bMultiple) {
            for (int i = 0; i < vtp.getTabCount(); i++) {
                if (vtp.getComponentAt(i).equals(comp)) {
                    moze = false;   // aby nebolo mozne dva krat pridat presne to iste
                }
            }
        }
        if (moze) {
            debugOut(this, 5, "Starting application: " + sTtl);
            //final IEOC_VisualObject vob;
            //vob = (IEOC_VisualObject) oApplPnl;
            String initOK;
             this.OutPrintln("krn.Starting application: IEOC_VisualObject=" + GetObjName(oApplPnl)
             + " with user-permissions: " + mitm.getPermDefinition().getPermStr());
             oApplPnl.setConn(DBcnWork);
            initOK = oApplPnl.initialize((Kernel) this, DBcnWork);
            
            if (initOK.equals("")) {
               vtp.addClosableTab(sTtl, comp);
               vtp.setSize(dskPanel.getSize());
               this.setWidgetFonts((Container) vtp, defaultFont);
               ////System.out.println("KRSTART_DIST_USR_PERM:" + mitm.getPermDefinition().getPermStr());
               EOC_message msgu = new EOC_message(null,"setUsrPerms",mitm.getPermDefinition().getPermStr(),"true");
//               oApplPnl.setUsrPerms(mitm.getPermDefinition().getPermStr(), true /*bTransmit*/);
               oApplPnl.setUsrPerms(msgu);
               EOC_message msgo = new EOC_message(null,"setObjPerms",mitm.getPermDefinition().getPermStr(),"true");
//               oApplPnl.setObjPerms(mitm.getPermDefinition().getPermStr(), true /*bTransmit*/);
               oApplPnl.setObjPerms(msgo);
               oApplPnl.afterInitialize();
               //krnMsg("afterInitialize-launched");
               
            }
            else {
                return;
            }

        }
    }

    public void setBtnMDLicon(JButton btn, String sSt) {
        switch (sSt) {
            case "ACTIVE":
                btn.setIcon(imgLedAkt);
                break;
            case "PASSIVE":
                btn.setIcon(imgLedNea);
                break;
            case "STOPPED":
                btn.setIcon(imgLedDis);
                break;
            case "DISABLED":
                btn.setIcon(imgLedDis);
                break;
            case "NONE":
                btn.setIcon(imgLedNull);
                break;
            case "":
                btn.setIcon(imgLedNull);
                 break;
       }
    }

    public Desktop getDsk() {
        return dsk;
    }

    public Modul EOC_getxModul(String sMdl) {
        Iterator itr = EOC_xmodules.iterator();
        /*
         * prechod pola modul-objektov
         */
        while (itr.hasNext()) {
            Object element = itr.next();
            Modul mdl = (Modul) element;
            if (mdl.sMDL.equals(sMdl)) {
                return mdl;
            }
        }
        return null;
    }

    public String EOC_set_ToolBal_TXN(Object oCurrToolBar) {
        oEOC_ToolBar_TXN = oCurrToolBar;
        return "";
    }
       
    public void listWidgets(JPanel jp) {
        String s;
        String n;
        Class cl;

        Component[] c = jp.getComponents();
        for (Object o : c) {
            cl = o.getClass();
            s = cl.getSimpleName();
            //krnOut(this,s);
            /*
             * spravovanie JTextField-u
             */
            switch (s) {
                case "JTextField":
                /*
                 * preskoci na JFormattedTextField
                 */
                case "JFormattedTextField": {
                    JTextField myObj = (JTextField) o;
                    n = myObj.getName();
                    break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                    n = myObj.getName();
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    n = myObj.getName();
                    break;
                }
                case "JRadioButton": {
                    JRadioButton myObj = (JRadioButton) o;
                    n = myObj.getName();
                    break;
                }
                case "DTfield": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                    n = myObj.getName();
                    break;
                }
                /*
                case "EOC_CDTfield": {
                    EOC_CDTfield myObj = (EOC_CDTfield) o;
                    n = myObj.getName();
                    
                    break;
                }
                */
                default:
                    n = "";
                    break;
            } // switch (s) {
            if (n == null) {
                n = "";
            }
            if (!n.equals("")) {
                this.OutPrintln("EaS_krn.listWidgets():" + s + " -- " + n);
            }
        } // for (Object o : c) {

    }

    public void krn_displayRow(JPanel jp, String tbl, ResultSet rs) throws SQLException, ParseException {
        String s;        // nazov typu skumaneho objektu
        String n;        // hodnota vlastnosti 'Name' skumaneho objektu
        String myColName;  // meno stlpca skumaneho objektu
        String myColType; // typ stlpca podla nazvu (substr(myColName,1,2)
        String myTblName;  // meno tabulky skumaneho objektu
        String currentValue = ""; // hodnota pre aktualny 'colname'
        Class cl;        // trieda skumaneho objektu
        
      try { // moze to tak byt, zavisi to od momentalneho stavu XViewer-a
          if (rs == null || rs.isClosed() || (!rs.isFirst())) {
             krn_clearWidgets(jp, "ADD");
             return;
          }
       } catch (SQLException ex) {
           Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
       }

         
      Component[] c = getComponentsInTabOrder(jp);  // widgety JFrame-kontainera
        for (Object o : c) {
            cl = o.getClass();      // trieda skumaneho objektu/widgetu
            s = cl.getSimpleName(); // meno skumaneho objektu/widgetu
            // spracovavaju sa len objekty EOC-architektury
            if (!(o instanceof iEOC_DBtableField)) continue;
            // moze saq to stat pri inicializacii
            if (((iEOC_DBtableField)o).getDBtableField() == null) continue;

            // nastavenie vlastosti EOC-objektov
            if ((o instanceof eoc.widgets.DTXfield)) {
                eoc.widgets.DTXfield myObj = (eoc.widgets.DTXfield) o;
                System.out.println("@@@@@@@@@@@@@@@@@@krn_displayRow(...):" + (myObj == null) 
                        + " DBFLDSS: " + (myObj.getDBtableField() == null));
                if (myObj.getDBtableField().bCreationErrors) continue;
                currentValue = rs.getString(myObj.getDbFieldName());
                myObj.getDBtableField().valueObjectForWrite = currentValue;
                System.out.println("@@@@@@@@@@@@@@@@@@krn_displayRow(...) vluje:" + currentValue );
                // 2017-07-18
                if (myObj.getDbTextFieldName().trim().length() > 0) {
                    currentValue = rs.getString(myObj.getDbTextFieldName());
                    myObj.setText(currentValue);
                }
                else
                   myObj.displayValue(currentValue);
                    
            }
            // toto musi byt po DTXfield-testu, lebo ten je aj instanciou DTfield !!!
            else if ((o instanceof eoc.widgets.DTfield)) {
                eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
////                System.out.println("@@@@@@@@@@@@@@@@@@krn_displayRow(...):" + (myObj == null) 
////                        + " DBFLDSS: " + (myObj.getDBtableField() == null));
                if (myObj.getDBtableField() == null) continue;
                if (myObj.getDBtableField().bCreationErrors) continue;
                currentValue = rs.getString(myObj.getDbFieldName());
                myObj.displayValue(currentValue);
            }
            else if ((o instanceof eoc.widgets.DTtextArea)) {
                eoc.widgets.DTtextArea myObj = (eoc.widgets.DTtextArea) o;
        // this.OutPrintln("displayrow  ===CRTERR " + (myObj.getDBtableField().bCreationErrors));
                if (myObj.getDBtableField().bCreationErrors) continue;
                currentValue = rs.getString(myObj.getDbFieldName());
//                myObj.getFormatter().stringToValue(currentValue);
//                myObj.setText(currentValue);
//                myObj.putStringToValue(currentValue);
                // riadok vyssie bolo presunute do funkcie nizsie
                // do objektu DTfield. 
                // displayValue je akymsi suctom funkcii setText() a setValue()
                // ktora donuti objekt 'resetnut' lubovolne predosle hodnoty
                // this.OutPrintln("KRN:: Displaying:" + currentValue);
                myObj.displayValue(currentValue);
            }
            else if ((o instanceof eoc.widgets.DTcomboBox)) {
                eoc.widgets.DTcomboBox myObj = (eoc.widgets.DTcomboBox) o;
        // this.OutPrintln("displayrow  ===CRTERR " + (myObj.getDBtableField().bCreationErrors));
                if (myObj.getDBtableField().bCreationErrors) continue;
                currentValue = rs.getString(myObj.getDbFieldName());
//                myObj.getFormatter().stringToValue(currentValue);
//                myObj.setText(currentValue);
//                myObj.putStringToValue(currentValue);
                // riadok vyssie bolo presunute do funkcie nizsie
                // do objektu DTfield. 
                // displayValue je akymsi suctom funkcii setText() a setValue()
                // ktora donuti objekt 'resetnut' lubovolne predosle hodnoty
                // this.OutPrintln("KRN:: Displaying:" + currentValue);
////                System.out.println("CBXdisplaying:" + currentValue + " in " + myObj.getDbFieldName());
                myObj.displayValue(currentValue);
            }
            else if ((o instanceof eoc.widgets.DTcheckBox)) {
                eoc.widgets.DTcheckBox myObj = (eoc.widgets.DTcheckBox) o;
        // this.OutPrintln("displayrow  ===CRTERR " + (myObj.getDBtableField().bCreationErrors));
                if (myObj.getDBtableField().bCreationErrors) continue;
                currentValue = rs.getString(myObj.getDbFieldName());
                myObj.displayValue(currentValue);
            }
            /*
            else if ((o instanceof EOC_CDTfield)) {
                 EOC_CDTfield myObj = (EOC_CDTfield) o;
                 if (myObj.getDBtableField().bCreationErrors) continue;
                 currentValue = rs.getString(myObj.getDbFieldName());
                myObj.setText(currentValue);
                }
            */
            else if ((o instanceof JButton)) {
             // tak sa na to vykakat
            }
            else {
                 this.OutPrintln(
                    "krn_displayRow() - Nepodporovany typ objektu: " + 
                     o.getClass().getSimpleName());
            }

        }           

//cl.getComponentType()
            /**** 
            if (FnEaS.iLookup(s,sWidgetTypes,",") >  0) {
             switch (s) {
            // ziskanie meta-dat aktualneho druhu widgetu z vlastnosti 'Name'
            switch (s) {
                case "JTextField":
                case "JFormattedTextField": {
                    JTextField myObj = (JTextField) o;
                    n = myObj.getName();
                    break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                    n = myObj.getName();
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    n = myObj.getName();
                    break;
                }
                case "JRadioButton": // zatial sa nebude pouzivat, nie som
                {                // ochotny vytvarat groupy rucne :-)
                    JRadioButton myObj = (JRadioButton) o;
                    n = myObj.getName();
                    break;
                }
                case "EOC_DTfield": {
                    EOC_DTfield myObj = (EOC_DTfield) o;
                    n = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                    break;
                }
                case "EOC_CDTfield": {
                    EOC_CDTfield myObj = (EOC_CDTfield) o;
                    this.OutPrintln("krn_displayRow---");
                    n = myObj.getdbFieldForWrite() + "#" + myObj.getDbTableName();
                    break;
                }
                default:
                    n = "";
                    break;
            } // switch (s) {
    */
/*
            if (n == null || n.equals("null#null")) {
                n = "";
            } // aby sa to jednoduchsie vyhodnocovalo
            // (neznasam NULL hodnoty :-) )
            if (FnEaS.iNumEntries(n, "#") < 2) {
                n = "";
            } // 'Name' nema spravny format, preto sa nepouzije 
            if (n.equals("")) {
                continue o_for;
            } // ked ' Name' nie je zadane, jede se dal

            // rozoberanie meta-data, ulozenej v 'Name' objektu/widgetu
            StringTokenizer st = new StringTokenizer(n, "#");
            myColName = st.nextToken();
            myColType = myColName.substring(0, 2);
            myTblName = st.nextToken();
            // ziskanie hodnoty stlpca pre widget
            try {
                currentValue = rs.getString(myColName);
            } catch (SQLException ex) {
                this.OutPrintln("EaS_krn - SQL-ERROR: " + ex.getMessage());
                Logger.getLogger(EaS_krn.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (currentValue == null) {
                currentValue = "";
            }
            if (myColType.equals("d_")) {
                currentValue = FnEaS.sDBdateToSCdate(currentValue);
            }
            ****/
            // Pridelenie hodnoty pre aktualny objekt/widget podla jeho typu
            //EEE! - zatial netestujem nazov tabulky 
            /***
            switch (s) {
                case "JTextField": // preskoci na JFormattedTextField
                case "JFormattedTextField": {
                    JTextField myObj = (JTextField) o;
                    myObj.setText(currentValue);
                    break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                    myObj.setSelected(currentValue.equals("t"));
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    if (myColName.startsWith("i_")) {
                        myObj.setSelectedIndex(Integer.valueOf(currentValue));
                    }
                    if (myColName.startsWith("c_")) {
                        myObj.setSelectedItem(currentValue);
                    }
                    break;
                }
                case "EOC_DTfield": {
                    EOC_DTfield myObj = (EOC_DTfield) o;
                    myObj.setText(currentValue);
                    myObj.revalidate();
                    break;
                }
                case "EOC_CDTfield": {
                    EOC_CDTfield myObj = (EOC_CDTfield) o;
                    myObj.setText(currentValue);
                    myObj.revalidate();
                    break;
                }
                case "JRadioButton": // zatial sa nebude pouzivat, nie som
                {                // ochotny vytvarat groupy rucne :-)
                    break;
                }
                default:
                    n = "";
                    break;
            } // switch (s) {
           this.OutPrintln("XXcccurrentValue= " + currentValue + " for object= " + n); 
        } // o_for: for (Object o : c) {
        //krnMsg("displayrow  2");
            ***/
        
    } // public void krn_displayRow

    /**
     * Vrátí pole komponentov v kontaineri jp vrátane komponent, uložených
     * vo vnorených/children-kontaineroch.
     * (číta štruktúru stromu widget-ov rekurzívne)
     * 
     * @param (Container) jp
     * @return Component[]
     */
    public Component[] getComponentsInTabOrder(Container jp) {
        Component[] cpAll;
        Component[] cp0 = jp.getComponents();
         cpAll = cp0;
        for (Component c: cp0) {
            //System.out.println("getComponentsInTabOrder===" + c.getClass());
            if (c instanceof JPanel || c instanceof JScrollPane || c instanceof JViewport) {
                Component[] cp1 = getComponentsInTabOrder((Container) c);
                cpAll = FnEaS.joinArrays(cpAll, cp1);
                //System.out.println("AABB_cpAll.length is===" + cpAll.length + " IN:" + jp.getClass().getSimpleName());
            }
        }        
        return cpAll;
        /*
        jp.invalidate();
        jp.validate();
        ArrayList<Component> a = new ArrayList<>();
        Container focusRoot;
        if (jp.isFocusCycleRoot()) focusRoot = jp;
        else focusRoot = jp.getFocusCycleRootAncestor();

        this.OutPrintln("11getComponentsInTabOrder==jp=" + jp.getName() 
                + "\n" + focusRoot.getName() );
        
        FocusTraversalPolicy ftp = focusRoot.getFocusTraversalPolicy();
        Component comp = ftp.getFirstComponent(focusRoot);
        this.OutPrintln("12getComponentsInTabOrder==jp.comcnt=" + jp.getComponentCount());
        this.OutPrintln(" -FIRST-FOCTRAPOL -  " + comp);
        this.OutPrintln(" -LAST-FOCTRAPOL -  " + ftp.getLastComponent(focusRoot));
        a.add(comp);
        Component first = comp;
        a.add(comp);
        while (comp != null) {
            this.OutPrintln(" -NEXT-FOCTRAPOL -  " + comp);
             comp = ftp.getComponentAfter(focusRoot, comp);
             //if (comp.equals(first)) break;
             a.add(comp);
        }
        
        Component[] c = new Component[a.size()];
        c = a.toArray(c);
        return c;
        */
    };
    
    public static String getDbObjNameProperty(String simpleClassName, Object o) {
        String objNameProperty; // hodnota vlastnosti 'Name' skumaneho objektu
        switch (simpleClassName) {
            case "JTextField":  // preskoci na JFormattedTextField
            case "JFormattedTextField": {
                 JTextField myObj = (JTextField) o;
                 objNameProperty = myObj.getName();
                 break;
            }
            case "JCheckBox": {
                 JCheckBox myObj = (JCheckBox) o;
                 objNameProperty = myObj.getName();
                 break;
            }
            case "JComboBox": {
                 JComboBox myObj = (JComboBox) o;
                 objNameProperty = myObj.getName();
                 break;
            }
            case "JRadioButton": { // zatial sa nebude pouzivat, nie som
                                   // ochotny vytvarat groupy rucne :-)
                 JRadioButton myObj = (JRadioButton) o;
                 objNameProperty = myObj.getName();
                 break;
            }
            case "DTfield": {
                 eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                 objNameProperty = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                 break;
            }
            case "DTXfield": {
                 eoc.widgets.DTXfield myObj = (eoc.widgets.DTXfield) o;
                 objNameProperty = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                 break;
            }
            /*
            case "EOC_CDTfield": {
                 EOC_CDTfield myObj = (EOC_CDTfield) o;
                 //objNameProperty = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                 ////this.OutPrintln("getInsertStatement1---");
                 objNameProperty = myObj.getdbFieldForWrite() + "#" + myObj.getDbTableName();
//                    this.OutPrintln("krn.getInsertStatement()--> EOC_CDTfield DB === "
//                            + myObj.getDbFieldName() + "#" + myObj.getDbTableName());
                    //this.OutPrintln("krn.getInsertStatement()--> EOC_CDTfield "
                    //+ " objNameProperty=" + objNameProperty
                    // + " obj: " + o.toString()
                    //);
                break;
            }
            */
            default:
                objNameProperty = "";
                break;
        } // switch (simpleClassName) {
        objNameProperty = objNameProperty.trim();

        // vadna hodnota sa zmeni na prazdny retazec
        if ((objNameProperty == null) || objNameProperty.equals("#")
             || objNameProperty.equals("null#")
             || objNameProperty.equals("#null")
             || objNameProperty.equals("null#null")) objNameProperty = "";
        if (FnEaS.iNumEntries(objNameProperty, "#") < 2) objNameProperty = ""; 
        if (FnEaS.sEntry(1,objNameProperty, "#")==null 
            || FnEaS.sEntry(1,objNameProperty, "#").equals("null")) 
            objNameProperty = "";
        
        return objNameProperty;   
    } // public String getDbObjNameProperty
    

    public boolean validDBcolumnName(String sFullColName,  String tblName) {
        // 'objNameProperty' nema spravny format, preto sa nepouzije 
        String myColName = "";
        String myTblName = "";
        // rozoberanie meta-data, ulozenej v 'Name' objektu/widgetu
        myColName = FnEaS.sEntry(1,sFullColName, "#");
        myTblName = FnEaS.sEntry(2,sFullColName, "#");
        if (sFullColName.equals("")
            || (myColName.equals("<NONE>"))
            // ked objek nesuvisi s tabulkou 'tblName', jede se dal
            || (!(myTblName.equals(tblName) 
                 | tblName.contains("." + myTblName))) 
            || (myTblName.equals("") || myColName.equals(""))
        ) return false; 
        else    
          return true;
    }
    
    public String getDBnameToken(Component[] c, boolean columnOnly,
                         boolean includeZapisal, boolean includeZmenil) {
        
        String nameToken = "";
        
        for (Object o : c) {
            // easys spracovava iba objekty s instanciou iEOC_DBtableField
            // o ostatne sa musi programator postarat na Local-e, t.j. v paneli
            // vo funkcii local_getDBnameToken()
            if (!(o instanceof iEOC_DBtableField)) continue; 
            iEOC_DBtableField dbfld;
            String colName;
            dbfld = (iEOC_DBtableField) o;
            // ked je objekt chybny, igoruje sa 
            if (dbfld.getDBtableField().creationErrors.length() > 0) continue;
            colName = dbfld.getDBtableField().dbColumnInfo.columnname.trim();
            if (!includeZapisal && colName.toLowerCase().equals("c_zapisal"))
                continue;
            if (!includeZmenil && colName.toLowerCase().equals("c_zmenil"))
                continue;
            /*
            if (o instanceof DTXfield)
               System.out.println("NAMEROKK2: " + ((DTXfield) o).getAccessibleContext().getAccessibleName() + " colNameee:" + colName);
            else if (o instanceof DTfield)
               System.out.println("NAMEROKK1: " + ((DTfield) o).getAccessibleContext().getAccessibleName() + " colNameee:" + colName);
            else if (o instanceof DTcomboBox)
               System.out.println("NAMEROKK3: " + ((DTcomboBox) o).getAccessibleContext().getAccessibleName() + " colNameee:" + colName);
            else if (o instanceof DTtextArea)
               System.out.println("NAMEROKK4: " + ((DTtextArea) o).getAccessibleContext().getAccessibleName() + " colNameee:" + colName);
            else 
               System.out.println("NAMEROKK5: " + o.getClass().getSimpleName());
            */
            nameToken = nameToken + "^" + colName;
            if (o instanceof DTXfield) { // prida sa text-hodnota
                DTXfield dtxfld; 
                dtxfld = (DTXfield) o;
                if (dtxfld.getDbTextFieldName().trim().length() > 0)
                    nameToken = nameToken + "^" + dtxfld.getDbTextFieldName();
            } 
/*            
*/
        }
        // odrezanie prvej ciarky v retazci
        if (nameToken.length() > 1) nameToken = nameToken.substring(1);

        if (includeZapisal) nameToken = nameToken + "^c_zapisal";
        if (includeZmenil) nameToken = nameToken + "^c_zmenil";
        return  nameToken;
        
    } // public String getDBNameToken(Component[] c, boolean columnOnly)
    
    public String getDBvalueToken(Component[] c, boolean columnOnly,
                          boolean includeZapisal, boolean includeZmenil) {
        String valueToken = "";
        Class cl;
        String simpleClassName = "";
        for (Object o : c) {
            // easys spracovava iba objekty s instanciou iEOC_DBtableField
            // o ostatne sa musi programator postarat na Local-e, t.j. v paneli
            // vo funkcii local_getDBnameToken()
            if (!(o instanceof iEOC_DBtableField)) continue; 
            
            iEOC_DBtableField dbfld;
            dbfld = (iEOC_DBtableField) o;
            String colName;

            // ked je objekt chybny, igoruje sa 
            if (dbfld.getDBtableField().creationErrors.length() > 0) continue;
            
            colName = dbfld.getDBtableField().dbColumnInfo.columnname.trim();
            //System.out.println("getDBvalueToken:  " + colName);
            //System.out.println("getDBvalueToken=> " + dbfld.getDBtableField().getValueForWriteAsString("'"));
            
            if (!includeZapisal && colName.toLowerCase().equals("c_zapisal"))
                continue;
            if (!includeZmenil && colName.toLowerCase().equals("c_zmenil"))
                continue;
            
            valueToken = valueToken + "^" 
                    + dbfld.getDBtableField().getValueForWriteAsString("'");
            
            
            if (o instanceof DTXfield) { // prida sa text-hodnota
                DTXfield dtxfld; 
                dtxfld = (DTXfield) o;
                if (dtxfld.getDbTextFieldName().trim().length() > 0)
                    valueToken = valueToken + "^'" + dtxfld.getText() + "'";
            } 

            
////            System.out.println("getDBvalueTokennosFor:" + dbfld.getDBtableField().getFullColumnName()
////            + " TOOKEN==:" + valueToken);
        }
        if (valueToken.length() > 1) valueToken = valueToken.substring(1);
        String updStamp = getUpdateStamp();
        if (includeZapisal) valueToken = valueToken + "^'" + updStamp + "'";
        if (includeZmenil) valueToken = valueToken  + "^'" + updStamp + "'";
        return  valueToken;
    } // public String getDBvalueToken(Component[] c, boolean columnOnly)
    
    public String getInsertStatement(Connection cnn, JPanel jp, String tblName,
                                 ArrayList alFldValPairs) {
        this.OutPrintln("getInsertStatement()-begin");
        String simpleClassName; // nazov typu skumaneho objektu
        String objNameProperty; // hodnota vlastnosti 'Name' skumaneho objektu
        String myTblName;       // meno tabulky skumaneho objektu
        String myColName;       // meno stlpca skumaneho objektu
        String myColType;       // 'generic' datovy typ stlpca 
        String currentValue = "";  // hodnota pre aktualny 'colname'
        String currNameToken = ""; // aktualna (fields) sekcia insert prikazu
        String currValueToken = "";  // aktualna (values) sekcia insert prikazu
        Class cl;                  // trieda skumaneho objektu
        String ret_val = "";       // vrateny SQL-UPDATE retazec

        jp.validate(); // toto asi netreba,  bol pridany pokusne QQQ
        this.OutPrintln("getInsertStatement()-01");

        /* Spracovanie widgetov kontainera, ziskanie retazca 'field#table'
         * pre dalsie spracovanie 
         *****************************************************************/
        Component[] c = getComponentsInTabOrder(jp);  // widgety JFrame-kontainera
        this.OutPrintln("getInsertStatement()-02");
        currNameToken = getDBnameToken(c, true /* columnOnly */, false, false);
        this.OutPrintln("getInsertStatement()-03 - nameToken: " + currNameToken);
        currValueToken = getDBvalueToken(c, true /* columnOnly */, false, false);
        this.OutPrintln("getInsertStatement()-04 - valueToken: " + currValueToken);
        currNameToken = currNameToken.replace('^', ',');
        currValueToken = currValueToken.replace('^', ',');
        String fld;
        Object Val;
        int iFldValCount = alFldValPairs.size();
        Object[] a;
        if (iFldValCount > 0) {
            for (int i = 0; i < iFldValCount; i++) {
                a = (Object[]) alFldValPairs.get(i);
                currNameToken = currNameToken + "," + a[0];
                if (a[1] instanceof String)
                    currValueToken = currValueToken + ",'" + a[1] +  "'";
                else 
                    currValueToken = currValueToken + "," + a[1];
            }
        }
        System.out.println("TOKKEEEN:\n" + currNameToken + "\n" + currValueToken);
        this.OutPrintln("getInsertStatement()-05");
        ret_val = "insert into " + tblName  + " (" + currNameToken 
                    + ") values (" + currValueToken + ")";

        return ret_val;
    }  // public String getInsertStatement
    
    public String getInsertStatement(Connection cnn, String tblName,
                                 Hashtable<String,Object> htFldValPairs,
                                 boolean bIgnoreAutomatedFields) {
        this.OutPrintln("getInsertStatementFromHashtable()-begin");
        String simpleClassName; // nazov typu skumaneho objektu
        String objNameProperty; // hodnota vlastnosti 'Name' skumaneho objektu
        String currentValue = "";  // hodnota pre aktualny 'colname'
        String currNameToken = ""; // aktualna (fields) sekcia insert prikazu
        String currValueToken = "";  // aktualna (values) sekcia insert prikazu
        Class cl;                  // trieda skumaneho objektu
        String ret_val = "";       // vrateny SQL-UPDATE retazec
        String stringApostroph = "'";
        this.OutPrintln("getInsertStatementFromHashtable()-01");

        /* Spracovanie riadkov/zaznamov Hashtable objektu
         *****************************************************************/
        Enumeration<String> enumKey = htFldValPairs.keys();
        boolean bIgnoreEntry = false;
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            bIgnoreEntry = false;
            if (bIgnoreAutomatedFields) {
                bIgnoreEntry = (key.toLowerCase().equals("id_" + tblName.toLowerCase())
                                || key.toLowerCase().equals("c_zapisal")   
                                || key.toLowerCase().equals("c_zmenil"));   
            } 
            if (bIgnoreEntry) continue;
            Object val = htFldValPairs.get(key);
            currNameToken  = currNameToken + "," + key;
            String updEntry; // update/insert entry aj s apostrofom pokial treba
            String strVal = "";
            String valueClass = val.getClass().getSimpleName(); 
            if (valueClass.equals("Date")) {
               strVal = sqlDateFormat.format(val);
               strVal = stringApostroph + strVal + stringApostroph;
            }
            else {
                strVal = val.toString().trim();
                if (valueClass.equals("String"))
                    strVal = stringApostroph + strVal + stringApostroph;
            } 
            currValueToken = currValueToken + "," + strVal;
        /*
        */  
        
            System.out.println("KEYYY:" + key + "  -> EnumerateEEE:: " + val.getClass().getSimpleName()
                    + " wrtval: " + strVal);
            //currValueToken = currValueToken + "," + 
        }
        
        currNameToken = currNameToken.substring(1); // odrezanie prvej ciarky
        currValueToken = currValueToken.substring(1); // odrezanie prvej ciarky

        System.out.println("TOKKEEEN:\n" + currNameToken + "\n" + currValueToken);
        this.OutPrintln("getInsertStatement()-05");
        ret_val = "insert into " + tblName  + " (" + currNameToken 
                    + ") values (" + currValueToken + ")";

        return ret_val;
    }  // public String getInsertStatement

    public String getInsertStatement(Connection cnn, String tblName,
                                 HashMap<String,Object> htFldValPairs,
                                 boolean bIgnoreAutomatedFields) {
        this.OutPrintln("getInsertStatementFromHashtable()-begin");
        String simpleClassName; // nazov typu skumaneho objektu
        String objNameProperty; // hodnota vlastnosti 'Name' skumaneho objektu
        String currentValue = "";  // hodnota pre aktualny 'colname'
        String currNameToken = ""; // aktualna (fields) sekcia insert prikazu
        String currValueToken = "";  // aktualna (values) sekcia insert prikazu
        Class cl;                  // trieda skumaneho objektu
        String ret_val = "";       // vrateny SQL-UPDATE retazec
        String stringApostroph = "'";
        this.OutPrintln("getInsertStatementFromHashtable()-01");

        /* Spracovanie riadkov/zaznamov Hashtable objektu
         *****************************************************************/
        Enumeration<String> enumKey = Collections.enumeration(htFldValPairs.keySet());
        boolean bIgnoreEntry = false;
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            bIgnoreEntry = false;
            if (bIgnoreAutomatedFields) {
                bIgnoreEntry = (key.toLowerCase().equals("id_" + tblName.toLowerCase())
                                || key.toLowerCase().equals("c_zapisal")   
                                || key.toLowerCase().equals("c_zmenil"));   
            } 
            if (bIgnoreEntry) continue;
            Object val = htFldValPairs.get(key);
            currNameToken  = currNameToken + "," + key;
            String updEntry; // update/insert entry aj s apostrofom pokial treba
            String strVal = "";
            String valueClass = val.getClass().getSimpleName(); 
            if (valueClass.equals("Date")) {
                System.out.println("KRN_GETINSTAT FORVALUE:" +val);
               strVal = sqlDateFormat.format(val);
               strVal = stringApostroph + strVal + stringApostroph;
            }
            else {
                strVal = val.toString().trim();
                if (valueClass.equals("String"))
                    strVal = stringApostroph + strVal + stringApostroph;
            } 
            currValueToken = currValueToken + "," + strVal;
        /*
        */  
        
            System.out.println("KEYYY:" + key + "  -> EnumerateEEE:: " + val.getClass().getSimpleName()
                    + " wrtval: " + strVal);
            //currValueToken = currValueToken + "," + 
        }
        
        currNameToken = currNameToken.substring(1); // odrezanie prvej ciarky
        currValueToken = currValueToken.substring(1); // odrezanie prvej ciarky

        System.out.println("TOKKEEEN:\n" + currNameToken + "\n" + currValueToken);
        this.OutPrintln("getInsertStatement()-05");
        ret_val = "insert into " + tblName  + " (" + currNameToken 
                    + ") values (" + currValueToken + ")";

        return ret_val;
    }  // public String getInsertStatement

    public String getInsertStatement(Connection cnn, JPanel jp, String tblName,
           String extKey, String extKeyDataType, String extKeyVal,
           ArrayList alFldValPairs) {
       // rozsirena verzia, vyuziva standardnu verziu getInsertStatement, s tym
       // ze don vlozi kod pre update externeho kluca vety tabulky
       String insStm = getInsertStatement(cnn, jp, tblName, alFldValPairs);
       ////krnMsg("!!!insStm=" + insStm);
       this.OutPrintln("getInsertStatemen--textKey = " + extKey + 
               "\n    STMTTT = " + insStm);
       if (extKey.equals("<NONE>")) { return insStm; }
       if (insStm.contains(extKey)) {
          return insStm;
       }
       if (insStm != null) {
           String sqlVal = extKeyVal; // zabalena verzia hodnoty pre SQL prikaz
           String types = "STRING,CHARACTER,VARCHAR,DATETIME";
           if (types.contains(extKeyDataType)) {
              sqlVal = "'" + sqlVal + "'"; // hodnota sa zabali medzi uvodzovky
           }
           insStm = insStm.replace(tblName  + " (", tblName  + " (" + extKey + ",");
           insStm = insStm.replace("values (", "values (" + sqlVal + ",");
       }
       this.OutPrintln("insStm=" + insStm);
       return insStm;
       
    } // public String getInsertStatement s external table udajmi

        public String getUpdateStatement(Connection cnn, JPanel jp, String tblName,
                                         ArrayList alFldValPairs) {
        if (tblName.contains(".")) {
            tblName = tblName.substring(tblName.lastIndexOf(".") + 1);
        }
        String s;         // nazov typu skumaneho objektu
        String n;         // hodnota vlastnosti 'Name' skumaneho objektu
        String myColName; // meno stlpca skumaneho objektu
        String myColType; // typ stlpca podla nazvu (substr(myColName,1,2)
        String myTblName;  // meno tabulky skumaneho objektu
        String currentValue = ""; // hodnota pre aktualny 'colname'
        String currNameToken = ""; // aktualna (fields) sekcia insert prikazu
        String currValueToken = "";  // aktualna (values) sekcia insert prikazu
        String currUpdToken = ""; //  vrateny SQL-UPDATE retazec
        Class cl;        // trieda skumaneho objektu

        // Component[] c = jp.getComponents();  // widgety JFrame-kontainera 
        Component[] c = getComponentsInTabOrder(jp);  // widgety JFrame-kontainera
        
        currNameToken = getDBnameToken(c, true /* columnOnly */, false, false);
        this.debugOut(this,5,"getUpdateStatement()-03 - nameToken: " + currNameToken);
        currValueToken = getDBvalueToken(c, true /* columnOnly */, false, false);
        this.debugOut(this,5,"getUpdateStatement()-04 - valueToken: " + currValueToken);

        ////currNameToken = currNameToken.replace('^', ',');
        ////currValueToken = currValueToken.replace('^', ',');
        /////this.OutPrintln("currNameToken===" + currNameToken);
        /////this.OutPrintln("currValueToken===" + currValueToken);
        // predpoklada sa, ze oba tokeny maju rovnaky pocet clankov

        String fld;
        Object Val;
        int iFldValCount = alFldValPairs.size();
        Object[] a;
        if (iFldValCount > 0) {
            for (int i = 0; i < iFldValCount; i++) {
                a = (Object[]) alFldValPairs.get(i);
                System.out.println(i + " ___>___ " + a[0] + " == " + a[1]);
                currNameToken = currNameToken + "^" + a[0];
//                if (a[1] instanceof String)
                    currValueToken = currValueToken + "^'" + a[1] +  "'";
//                else 
  //                  currValueToken = currValueToken + "," + a[1];
            }
        }
        
        String[] currNamArray;  // pole nazvov
        String[] currValArray;  // pole hodnot
        currNamArray = currNameToken.split("\\^");
        currValArray = currValueToken.split("\\^");
        for (int i = 0; i < currNamArray.length; i++) {
            currUpdToken = currUpdToken + "," 
                         + currNamArray[i] + "=" + currValArray[i];
        }
        if (currUpdToken.length() > 1) {
            currUpdToken = currUpdToken.substring(1);
        }
////        System.out.println("PERMUPDSTAT==" + cmd);
        this.OutPrintln("UPDSTATEMENT: " + currUpdToken);
        return currUpdToken;
    }  // public String getUpdateStatement
    
    public String getUpdateStatement(Connection cnn, String tblName,
                                 Hashtable<String,Object> htFldValPairs,
                                 boolean bIgnoreAutomatedFields) {
        this.OutPrintln("getUpdateStatementFromHashtable()-begin");
        String simpleClassName; // nazov typu skumaneho objektu
        String objNameProperty; // hodnota vlastnosti 'Name' skumaneho objektu
        String currentValue = "";  // hodnota pre aktualny 'colname'
        String currNameToken = ""; // aktualna (fields) sekcia insert prikazu
        String currValueToken = "";  // aktualna (values) sekcia insert prikazu
        Class cl;                  // trieda skumaneho objektu
        String ret_val = "";       // vrateny SQL-UPDATE retazec
        String stringApostroph = "'";
        String  primaryKeyName = "id_" + tblName.toLowerCase();
        Integer primaryKeyValue = null;
        String currUpdToken = ""; 
        
        this.OutPrintln("getUpdateStatementFromHashtable()-01");

        /* Spracovanie riadkov/zaznamov Hashtable objektu
         *****************************************************************/
        Enumeration<String> enumKey = htFldValPairs.keys();
        boolean bIgnoreEntry;
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            bIgnoreEntry = false;
            if (key.toLowerCase().equals(primaryKeyName)) {
                primaryKeyValue = Integer.parseInt(htFldValPairs.get(key).toString());
            }
            if (bIgnoreAutomatedFields) {
                bIgnoreEntry = (key.toLowerCase().equals(primaryKeyName)
                                || key.toLowerCase().equals("c_zapisal")   
                                || key.toLowerCase().equals("c_zmenil"));   
            } 
            if (bIgnoreEntry) continue;
            Object val = htFldValPairs.get(key);
            currNameToken  = currNameToken + "^" + key;
            String updEntry; // update/insert entry aj s apostrofom pokial treba
            String strVal = "";
            String valueClass = val.getClass().getSimpleName(); 
            if (valueClass.equals("Date")) {
               strVal = sqlDateFormat.format(val);
               strVal = stringApostroph + strVal + stringApostroph;
            }
            else if (valueClass.equals("Calendar")) {
               strVal = sqlDateFormat.format(((Calendar) val).getTime());
               strVal = stringApostroph + strVal + stringApostroph;
            }
            else {
                strVal = val.toString().trim();
                if (valueClass.equals("String"))
                    strVal = stringApostroph + strVal + stringApostroph;
            } 
            currValueToken = currValueToken + "^" + strVal;
        }
        
        currNameToken = currNameToken.substring(1); // odrezanie prvej ciarky
        currValueToken = currValueToken.substring(1); // odrezanie prvej ciarky

        System.out.println("TOKKEEEN:\n" + currNameToken + "\n" + currValueToken);
        this.OutPrintln("getUpdateStatement()-05");
        String[] currNamArray;  // pole nazvov
        String[] currValArray;  // pole hodnot
        currNamArray = currNameToken.split("\\^");
        currValArray = currValueToken.split("\\^");
        for (int i = 0; i < currNamArray.length; i++) {
            currUpdToken = currUpdToken + "," 
                         + currNamArray[i] + "=" + currValArray[i];
        }
        if (currUpdToken.length() > 1) {
            currUpdToken = currUpdToken.substring(1);
        }
        ret_val = "UPDATE " + tblName  + " SET " + currUpdToken 
                    + " WHERE " + primaryKeyName + " = " + primaryKeyValue;
        return ret_val;
    }  // public String getUpdateStatement        
        
    public String getUpdateStatement(Connection cnn, String tblName,
                                 HashMap<String,Object> htFldValPairs,
                                 boolean bIgnoreAutomatedFields) {
        this.OutPrintln("getUpdateStatementFromHashtable()-begin");
        String simpleClassName; // nazov typu skumaneho objektu
        String objNameProperty; // hodnota vlastnosti 'Name' skumaneho objektu
        String currentValue = "";  // hodnota pre aktualny 'colname'
        String currNameToken = ""; // aktualna (fields) sekcia insert prikazu
        String currValueToken = "";  // aktualna (values) sekcia insert prikazu
        Class cl;                  // trieda skumaneho objektu
        String ret_val = "";       // vrateny SQL-UPDATE retazec
        String stringApostroph = "'";
        String  primaryKeyName = "id_" + tblName.toLowerCase();
        Integer primaryKeyValue = null;
        String currUpdToken = ""; 
        
        this.OutPrintln("getUpdateStatementFromHashtable()-01");

        /* Spracovanie riadkov/zaznamov Hashtable objektu
         *****************************************************************/
        Enumeration<String> enumKey = Collections.enumeration(htFldValPairs.keySet());
        boolean bIgnoreEntry;
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            bIgnoreEntry = false;
            if (key.toLowerCase().equals(primaryKeyName)) {
                primaryKeyValue = Integer.parseInt(htFldValPairs.get(key).toString());
            }
            if (bIgnoreAutomatedFields) {
                bIgnoreEntry = (key.toLowerCase().equals(primaryKeyName)
                                || key.toLowerCase().equals("c_zapisal")   
                                || key.toLowerCase().equals("c_zmenil"));   
            } 
            if (bIgnoreEntry) continue;
            Object val = htFldValPairs.get(key);
            currNameToken  = currNameToken + "^" + key;
            String updEntry; // update/insert entry aj s apostrofom pokial treba
            String strVal = "";
            String valueClass = val.getClass().getSimpleName(); 
            if (valueClass.equals("Date")) {
               strVal = sqlDateFormat.format(val);
               strVal = stringApostroph + strVal + stringApostroph;
            }
            else if (valueClass.equals("Calendar")) {
               strVal = sqlDateFormat.format(((Calendar) val).getTime());
               strVal = stringApostroph + strVal + stringApostroph;
            }
            else {
                strVal = val.toString().trim();
                if (valueClass.equals("String"))
                    strVal = stringApostroph + strVal + stringApostroph;
            } 
            currValueToken = currValueToken + "^" + strVal;
        }
        
        currNameToken = currNameToken.substring(1); // odrezanie prvej ciarky
        currValueToken = currValueToken.substring(1); // odrezanie prvej ciarky

        System.out.println("TOKKEEEN:\n" + currNameToken + "\n" + currValueToken);
        this.OutPrintln("getUpdateStatement()-05");
        String[] currNamArray;  // pole nazvov
        String[] currValArray;  // pole hodnot
        currNamArray = currNameToken.split("\\^");
        currValArray = currValueToken.split("\\^");
        for (int i = 0; i < currNamArray.length; i++) {
            currUpdToken = currUpdToken + "," 
                         + currNamArray[i] + "=" + currValArray[i];
        }
        if (currUpdToken.length() > 1) {
            currUpdToken = currUpdToken.substring(1);
        }
        ret_val = "UPDATE " + tblName  + " SET " + currUpdToken 
                    + " WHERE " + primaryKeyName + " = " + primaryKeyValue;
        return ret_val;
    }  // public String getUpdateStatement        

/*
    public  static String getDBtype (Connection cn) {
        String dbt = eoc.dbdrv.DBdrv.getDBtype (cn);
        return dbt;
    }
*/
    public static String getDBtype (Connection cn) {
        DatabaseMetaData dmd = null;
        String dbtype = null;
        try {
            dmd = cn.getMetaData();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        String url = null;
        try {
            url = dmd.getURL();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
            if (url.contains("sqlanywhere")) {
                dbtype = "SYBASE";
            }
            if (url.contains("postgres")) {
                dbtype = "POSTGRES";
            }
            if (url.contains("mysql")) {
                dbtype = "MYSQL";
            }
            if (url.contains("firebird")) {
                dbtype = "FIREBIRD";
            }
      //      krn.OutPrintln("EOC_DBdrv.getDBtype().dmd.url=" + url + " -> " + dbtype);
       return dbtype;
    }
    
    
    public static String OLD_fillDBcolumnInfo 
        (DBcolumnInfo cinf, Connection cnn, String own, String tbl, String col) {
        own = own==null ? "" : own.trim();
        tbl = tbl==null ? "" : tbl.trim();
        col = col==null ? "" : col.trim();
        if (tbl.equals("") || col.equals("")) 
            return "Nekompletná definícia tabuľky: " + tbl + "." + col; 
        // DB-specificke metody uz netestuju obsah parametrov,
        // vratia len chybu neexistecie tabulky, popripade inu chybu z DB
        switch (getDBtype(cnn).toUpperCase()) {
            case "POSTGRES": {
                return OLD_fillPostgresDBcolumnInfo(cinf, cnn, own, tbl, col);
            }
            case "SYBASE": {
                return OLD_fillSybaseDBcolumnInfo(cinf, cnn, own, tbl, col);
            }
            default: {
                return "Nepodporovaný typ databázy v spojení: " + cnn.toString();
            }
        } // switch (Info) {
    }

    public static String getGenericDataType (String specificDataType) {
        // generic datove typy: string,integer,decimal,date,boolean
        specificDataType = specificDataType.toLowerCase(); // pre istotu
        System.out.println("KERRRNELLL ----- getGenericDataType-->specificDataType=" + specificDataType);
        // character
        if (specificDataType.indexOf("char") > -1
            || specificDataType.indexOf("string") > -1
            || specificDataType.indexOf("text") > -1
                )
        { return "string"; }
        // integer
        else if (specificDataType.indexOf("int") > -1
                || specificDataType.indexOf("short") > -1) { return "integer"; }
        // decimal
        else if (specificDataType.indexOf("decimal") > -1
            || specificDataType.indexOf("numeric") > -1
            || specificDataType.indexOf("float") > -1
            || specificDataType.indexOf("real") > -1
            || specificDataType.indexOf("double") > -1
               )
            { return "decimal"; }
        // !!!! POZOR !!! - typ DATETIME bude treba asi rozdelit
        else if (specificDataType.indexOf("date") > -1) { return "date"; }
        else if (specificDataType.indexOf("times") > -1) { return "date"; }
        // boolean
        else if (specificDataType.indexOf("bit") > -1
            || specificDataType.indexOf("boolean") > -1
                )
            { return "boolean"; }
        // binary
        else if (specificDataType.indexOf("binary") > -1
            || specificDataType.indexOf("varbinary") > -1
                )
            { return "binary"; }
        // QQQ treba spracovat long-binary typy
        else {
////            krnMsg(this,"I",getCurrentMethodName() + "()-> Nepodporovaný dátový typ: " 
////                    + specificDataType ,"krn.getGenericDataType()");
            System.out.println("getGenericDataType()-> Nepodporovaný dátový typ: " 
                    + specificDataType);
            return "";
        }
    }
    
    public static String OLD_fillSybaseDBcolumnInfo (DBcolumnInfo colInf, 
            Connection cnn, String owner, String tbl, String col) {
        String cQry;
        ResultSet rs;  
        ////System.out.println("fillSybaseDBcolumnInfo==>>owner===" + owner);
        cQry = "SELECT * FROM eas_dbcolumns WHERE table_name = '" + tbl + "' and column_name = '" + col + "'";
        ////System.out.println("fillSybaseDBcolumnInfo===Qry==" + cQry);
        colInf.tablename  = tbl;
        colInf.columnname = col;
        try {
            Statement stm;
            stm = cnn.createStatement();
            rs = stm.executeQuery(cQry);
            if (rs.next()) {
                colInf.realdatatype = rs.getString("coltype");
                colInf.nullallowed  = !rs.getString("nulls").equals("0");
                colInf.length       = Integer.parseInt(rs.getString("length"));
                colInf.decimals     = Integer.parseInt(rs.getString("syslength"));
                colInf.comment      = rs.getString("remarks");
                colInf.defaultValue = rs.getString("default_value");
                colInf.genericdatatype = getGenericDataType(colInf.realdatatype);
                // skladame format string:
                if (colInf.length > 0) {
                String f = "";
                switch (colInf.genericdatatype) {
                     case "decimal":
                         //f = FnEaS.repeat("#", colInf.length  - colInf.decimals)
                         //+ "." + FnEaS.repeat("#",colInf.decimals);
                         int iLng = colInf.length - colInf.decimals;
                         for (int i = 1; i <= iLng; i++) {
                           f = "#" + f;  
                           if ((i % 3) == 0) {
                               f = ' ' + f; // <nbsp> 
                           }
                         }  
                         f = f + "." + FnEaS.repeat("#",colInf.decimals);
                         colInf.notNullDefaultValue = "0.0";
                     break;
                     case "integer":
                         //f = FnEaS.repeat("#", colInf.length);
                         for (int i = 1; i <= colInf.length; i++) {
                           f = "#" + f;  
                           if ((i % 3) == 0) {
                               f = ' ' + f; // <nbsp> 
                           }
                         }
                         colInf.notNullDefaultValue = "0";
                     break;
                     case "date":
                         f = "##.##.####";
                         colInf.notNullDefaultValue = "01.01.0000";
                     break;
                     default:
//                         if 
                         f = FnEaS.repeat("*",colInf.length);
                         
                }
                f = f.trim();
                colInf.formatString = f;
                ///this.OutPrintln(col + "  FOORMATSTR:" + colInf.genericdatatype
                ///        + " IS:" + f);                
                }
                stm.close();
                return "";
            }
            else {
                System.out.println("fillSybaseDBcolumnInfo()=> FAILED FOR == " + tbl + "." + col);
                stm.close();
                return "fillSybaseDBcolumnInfo()=> FAILED FOR == " + tbl + "." + col;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
     }

    public /*static*/ String OLD_getSybaseDBcolumnInfo (Connection cnn, /* String owner, */ 
                                         String tbl, String col, String Info) {
        String cOut = "";
        String cProperty = "";
        String cQry;
        cQry = "SELECT * FROM eas_dbcolumns WHERE table_name = '" + tbl + "' and column_name = '" + col + "'";
        ////this.OutPrintln("cQry= " + cQry);
        try {
           switch (Info.toLowerCase()) {
              case "datatype": { cProperty = "coltype"; break; }
              case "nullallowed": { cProperty = "nulls"; break; }
              case "length": { cProperty = "length"; break; }
              case "decimals": { cProperty = "syslength"; break; }
              case "comment": { cProperty = "remarks"; break; }
              default: {
                  //krnStaticMsg("E", "Unsupported property: " + Info, "getSybaseDBcolumnInfo()");
                  return cOut;
              }
           }
           //this.OutPrintln("cProperty: " + cProperty);
//            Statement stm;
//            ResultSet rs;  
//            stm = cnn.createStatement();
            mainRS = mainStm.executeQuery(cQry);
            mainRS.next();
            //System.out.println("GETTINGPROP:" + tbl + "." + col + "." + cProperty);
            cOut = mainRS.getString(cProperty);
            //stm.close();
            //mainRS.close();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        //this.OutPrintln("cOut=" + cOut);
        return cOut;
     }
    
    public static String OLD_fillPostgresDBcolumnInfo (DBcolumnInfo colInf, 
            Connection cnn, String owner, String tbl, String col) {
        String cQry;
        ResultSet rs;  
        cQry = "select *, "
             + "(select pg_catalog.obj_description(oid) from " 
             + "pg_catalog.pg_class c where c.relname=cols.table_name)" 
             + " as table_comment,"
             + "(select pg_catalog.col_description(oid,cols.ordinal_position::int)"
             + "from pg_catalog.pg_class c where c.relname=cols.table_name)"
             + " as column_comment"
             + " from information_schema.columns cols where table_name = '" 
             + tbl + "' and column_name = '" + col + "'";
        colInf.tablename  = tbl;
        colInf.columnname = col;
        try {
            Statement stm;
            stm = cnn.createStatement();
            rs = stm.executeQuery(cQry);
            if (rs.next()) {
                colInf.realdatatype = rs.getString("data_type");
                colInf.nullallowed = !rs.getString("is_nullable").equals("0");
                colInf.length = Integer.parseInt(rs.getString("character_maximum_length"));
                  // pre decimal/numeric -- numeric_precision_integer
                colInf.decimals = Integer.parseInt(rs.getString("numeric_scale"));
                colInf.comment = rs.getString("column_comment");
                stm.close();
                return "";
            }
            else {
                System.out.println("fillPostgresDBcolumnInfo()=> FAILED FOR == " + tbl + "." + col);
                stm.close();
                return "fillPostgresDBcolumnInfo()=> FAILED FOR == " + tbl + "." + col;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
     }
    
    public static String getPostgresDBcolumnInfo (Connection cnn, String tbl, String col, String Info) {
        String cOut = "";
        String cProperty = "";
        String cQry;
        cQry = "select *, "
             + "(select pg_catalog.obj_description(oid) from " 
             + "pg_catalog.pg_class c where c.relname=cols.table_name)" 
             + " as table_comment,"
             + "(select pg_catalog.col_description(oid,cols.ordinal_position::int)"
             + "from pg_catalog.pg_class c where c.relname=cols.table_name)"
             + " as column_comment"
             + " from information_schema.columns cols where table_name = '" 
             + tbl + "' and column_name = '" + col + "'";
        //this.OutPrintln("cQry= " + cQry);
        try {
            switch (Info.toLowerCase()) {
              case "datatype": { cProperty = "data_type"; break; }
              case "nullallowed": { cProperty = "is_nullable"; break; }
              case "length": { 
                  cProperty = "character_maximum_length"; break; 
                  // pre decimal/numeric -- numeric_precision_integer
              }
              case "decimals": { cProperty = "numeric_scale"; break; }
              case "comment": { cProperty = "column_comment"; break; }
              default: {
                  staticMsg("E", "Unsupported property: " + Info, "getPostgresDBcolumnInfo()");
              }
           }
           
           Statement stm;
           ResultSet rs;  
           stm = cnn.createStatement() ;
           rs = stm.executeQuery(cQry);
           rs.next();
           cOut = rs.getString(cProperty);
           stm.close();
           rs.close();
        } catch (SQLException ex) {
            System.out.println("catch-fired => " + Kernel.class.getName() + " -- ERROR");
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        ////this.OutPrintln("cOut=" + cOut);
        return cOut;
     }

    public boolean isIndexed(Connection cnn, String sTbl, String sCol) {
        switch (getDBtype(cnn)) {
            case "POSTGRES": {
                return isIndexedPostgres(cnn, sTbl, sCol);
            }
            case "SYBASE": {
                return isIndexedSybase(cnn, sTbl, sCol);
            }
        } // switch (Info) {

       return false;
    }

    public boolean isIndexedPostgres(Connection cnn, String sTbl, String sCol) {
       String cQry;
       boolean retval = false;
       cQry = "select t.relname as table_name,i.relname as index_name"
          + ", a.attname as column_name "
          + "from pg_class t, pg_class i, pg_index ix, pg_attribute a "
          + "where t.oid = ix.indrelid and i.oid = ix.indexrelid "
          + "and a.attrelid = t.oid and a.attnum = ANY(ix.indkey) "
          + "and t.relname = '" + sTbl + "' and a.attname = '" + sCol + "' "
          + "order by t.relname, i.relname" ;

       Statement stm;
       ResultSet rs;
       try {
           stm = cnn.createStatement();
           rs = stm.executeQuery(cQry);
           retval = rs.next();
           stm.close();
           rs.close();
       } catch (SQLException ex) {
           Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
       }
       return retval;
    }

    public boolean isIndexedSybase(Connection cnn, String sTbl, String sCol) {
       String cQry;
       boolean retval = false;
       cQry = "select * from sysindex join systable on systable.table_name = '" 
          + sTbl  + "' and systable.table_id = sysindex.table_id "
          + "join syscolumn on syscolumn.table_id = systable.table_id "
          + "and syscolumn.column_name = '" + sCol + "'";
       Statement stm;
       ResultSet rs;
       try {
          stm = cnn.createStatement();
          rs = stm.executeQuery(cQry);
          retval = rs.next();
          stm.close();
          rs.close();
       } catch (SQLException ex) {
          Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
       }
       return retval;
    }
    
    public String OLD_getDBcolValue(Connection cnn, String db, String tbl,
           String tblid, String tblIDVal, String colName) {
      try {
         String cQry, retval;
         Statement stm;
         ResultSet rs;
         cQry = "select " + colName + " from " + tbl 
              + " where " + tblid + " = " + tblIDVal;     
         System.out.println("krn_getDBcolValue()-qry== " + cQry);  
         stm = cnn.createStatement();
         rs = stm.executeQuery(cQry);
         rs.next();
         retval = rs.getString(colName);    
         stm.close();
         rs.close();
         return retval;
      } catch (SQLException ex) {
         Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
    }
    
    public static Method getMethod (Object oKto, String sMetoda) {
      Method mtd = null;
      try {
         mtd = oKto.getClass().getMethod(sMetoda, new Class[]  {String.class});
         if (mtd == null) {
            mtd = oKto.getClass().getDeclaredMethod(sMetoda, new Class[]  {String.class});
         }
      } catch (NoSuchMethodException ex) {
         // Moze vratit aj null-hodnotu 
         // Logger.getLogger(EaS_krn.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SecurityException ex) {
         // Moze vratit aj null-hodnotu 
         // Logger.getLogger(EaS_krn.class.getName()).log(Level.SEVERE, null, ex);
      }
      return mtd;
    }
/*
    public static Method getStaticMethod (Object oKto, String sMetoda) {
      Method mtd = null;
      try {
         mtd = oKto.getClass().getMethod(sMetoda, new Class[]  {String.class});
         if (mtd == null) {
            mtd = oKto.getClass().getDeclaredMethod(sMetoda, new Class[]  {String.class});
         }
      } catch (NoSuchMethodException ex) {
         // Moze vratit aj null-hodnotu 
         // Logger.getLogger(EaS_krn.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SecurityException ex) {
         // Moze vratit aj null-hodnotu 
         // Logger.getLogger(EaS_krn.class.getName()).log(Level.SEVERE, null, ex);
      }
      return mtd;
    }
*/
    public boolean bExists (Connection cnn, String tbl, String fld, String val) {
      try {
         Statement stm = cnn.createStatement(ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
         ResultSet lrs = stm.executeQuery("select 1 from " + tbl + " where " + fld + " = '" + val + "'");
         return lrs.first();
      } catch (SQLException ex) {
         Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
         return false;
      }
    }
    
    /* tato verzia vyberu je problematicka 
     * (spatny zapis textu do komponenty comp) */
    /*
    public void dbVyber(Connection cnn, eoc.IEOC_VisualObject vThis, 
            Component comp, String tbl, String msKey, String colDefs, 
            String retCol,String sWhere, String sOrder, String sTitle) {

        eoc.EOC_vyber myVyber = new eoc.EOC_vyber(dsk,true);
        myVyber.initialize(this,CnEx,CnIn,vThis.getMasterConn());
        myVyber.createVyberFor ((IEOC_VisualObject) vThis, comp, tbl, msKey,
                                 colDefs, retCol, sWhere, sOrder, sTitle);
    }
    */
    public String createUVyberDefString(String cisName, String cDelimiter) {
        String cisDefStr = "";
        try {
            // Vyhladanie definicie ciselnika
            String cQry;
            cQry = "SELECT * FROM eas_uni_cis_def WHERE " +
                    "c_typ_cis = '" + cisName + "'";

            System.out.println("EERQRY" + cQry);
            mainRS = mainStm.executeQuery(cQry);
            mainRS.next();
            String cNazovCiselnika = mainRS.getString("c_nazov_cis");
            String cPopisCiselnika = mainRS.getString("c_popis_cis");
            String cSkratPolLabel  = mainRS.getString("c_skrat_pol_label");
            String cNazPolLabel    = mainRS.getString("c_naz_pol_label");
            String cPopisPolLabel  = mainRS.getString("c_popis_pol_label");
            String cSkratPolLength = mainRS.getString("i_skrat_pol_length");
            String cNazPolLength   = mainRS.getString("i_naz_pol_length");
            String cPopisPolLength = mainRS.getString("i_popis_pol_length");
            // Priklad definicneho retaca
            //#DODAV#ID#CISLO,NAZOV,ULICA,MESTO#NAZOV#Výber je neviem čoho#<WHERE>#<ORDER_BY>
            //#BYT#ID#KMC^Var. symbol, FUN@getUlica^Ulica^string^30#KMC,FUN@getUlica,VCH_ID#Výber bytov##KMC#Výber bytu, kde sa má oprava vykonať 
            //#BYT#ID#KMC^Var. symbol, FUN@getUlica^Ulica^string^30#KMC,FUN@getUlica,VCH_ID#Výber bytov##KMC#Výber bytu, kde sa má oprava vykonať             // 
            cisDefStr = "eas_uni_cis#id_eas_uni_cis#"
                      + "c_skratka^" + cSkratPolLabel + "^" + cSkratPolLength + ","
                      + "c_naz_pol_cis^" + cNazPolLabel + "^" + cNazPolLength + ","
                      + "c_popis^" + cPopisPolLabel + "^" + cPopisPolLength
                      + "#c_skratka#" + cNazovCiselnika + "#c_typ_cis = '" 
                      + cisName + "'#c_skratka#" + cPopisCiselnika;
            if (iVerboseLevel < 6)
                Message ("Creating vyber for: " + cNazovCiselnika + "\nDEF: " + cisDefStr);
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cisDefStr;
    }

    public String genVyberForMe(eoc.IEOC_VisualObject vReguestor, Component comp, 
                           String vybDef) {
        // priklad vybDef (prvy znak je pouzity rozdelovac):
        //#DODAV#ID#CISLO,NAZOV,ULICA,MESTO#NAZOV#Výber je neviem čoho#<WHERE>#<ORDER_BY>
        String delimiter = vybDef.substring(0,1);
        String sVyber_RetVal = "<CANCELED>";
        /*
         * 2016-01-31
         * specialne vybery
         * presmeruju sa na creatory definicnych retazcov
         *
         ****************************************************************/
        // Vyber z univerzalneho ciselnika
        if (delimiter.equals("Q")) {
             delimiter = vybDef.substring(1,2);
//             vybDef = createQVyberDefString(vybDef.substring(2), delimiter);
             vybDef = vybDef.substring(2);
            String[] vybAttrs = vybDef.split(delimiter);
            if (vybAttrs.length < 4) return "";

            String sQry     = vybAttrs[0];
            String retVals   = vybAttrs[1];
            String sTitle    = "";
 //           String sWhere    = "";
 //           String sOrder    = "";
            String sHeaders  = "";

            if (vybAttrs.length > 4) { sTitle   = vybAttrs[4]; }
            else { sTitle = "Výber"; } 
//            if (vybAttrs.length > 5) { sWhere   = vybAttrs[5]; }
//            else { sWhere = ""; } 
//            if (vybAttrs.length > 6) { sOrder   = vybAttrs[6]; }
//            else { sOrder = ""; }
            if (vybAttrs.length > 7) { sHeaders = vybAttrs[7]; }
            else { sHeaders = ""; }

//            if (sWhere.startsWith("<")) sWhere = "";
//            if (sOrder.startsWith("<")) sOrder = "";

            ////       Msg("GENEERATINGVYBER A");
            system.QVyber myVyber = 
                new system.QVyber(dsk /* parent frame */,true /* modal */);
            myVyber.setMethodSource((Object) vReguestor);
            myVyber.initialize(this,vReguestor.getConn());
            myVyber.setTitle(sTitle);
            sVyber_RetVal = myVyber.createQVyberForMe (vReguestor, comp,
                    /*tblName, masterKey,*/ sQry, retVals, /*sWhere, sOrder,*/ sTitle, sHeaders);
         }
        else {         
            if (delimiter.equals("U")) {
                delimiter = vybDef.substring(1,2);
                vybDef = createUVyberDefString(vybDef.substring(2), delimiter);
            }
            else vybDef = vybDef.substring(1);
         
            String[] vybAttrs = vybDef.split(delimiter);
            if (vybAttrs.length < 4) return "";

            String tblName   = vybAttrs[0];
            String masterKey = vybAttrs[1];
            String colDefs   = vybAttrs[2];
            String retVal    = vybAttrs[3];
            String sTitle    = "";
            String sWhere    = "";
            String sOrder    = "";

            if (vybAttrs.length > 4) { sTitle  = vybAttrs[4]; }
            else { sTitle = "Výber"; } 
            if (vybAttrs.length > 5) { sWhere  = vybAttrs[5]; }
            else { sWhere = ""; } 
            if (vybAttrs.length > 6) { sOrder  = vybAttrs[6]; }
            else { sOrder = ""; }

            if (sWhere.startsWith("<")) sWhere = "";
            if (sOrder.startsWith("<")) sOrder = "";

            ////       Msg("GENEERATINGVYBER A");
            system.Vyber myVyber = 
                new system.Vyber(dsk /* parent frame */,true /* modal */);
            myVyber.setMethodSource((Object) vReguestor);
            myVyber.initialize(this,vReguestor.getConn());
            myVyber.setTitle(sTitle);
            sVyber_RetVal = myVyber.createVyberForMe (vReguestor, comp,
                    tblName, masterKey, colDefs, retVal, sWhere, sOrder, sTitle);
        }
////        Msg("GENEERATINGVYBER B");
////        krnMsg("VYBRALA SA HODNOTA: " + sVyber_RetVal);
        return sVyber_RetVal;
    }

    public void fill_htMethods(String findInObject ,String sMethodNames,
            Hashtable<String, Method> ht, boolean bClear) {
        if (bClear) ht.clear();
        
    }
    
    public void selectModul(String sAktMDL) {
        Iterator itr = EOC_xmodules.iterator();
        /*
         * prechod pola xmodul-objektov
         */
        this.OutPrintln("\nKernel: selecting modul: " + sAktMDL + " ... ");
        VTabbedPane selectedTbp = null;
        while (itr.hasNext()) {
            Object element = itr.next();
            system.modul.Modul xmdl;
            xmdl = (Modul) element;
            //  nastavenia menu-baru
            JMenuBar mbr;
            mbr = (JMenuBar) xmdl.getModulMenuMar();
            //final JInternalFrame ifrm ;
            //ifrm = (JInternalFrame) mdl.oIfrm_MDL ;
            VTabbedPane tbp = (VTabbedPane) xmdl.getoVtp_MDL();
            if (xmdl.getsMDL().equalsIgnoreCase(sAktMDL)) {
                tbp = (VTabbedPane) xmdl.getoVtp_MDL();
                selectedTbp = tbp;
                this.OutPrintln("Kernel: modul " + sAktMDL + " - selected.\n");
                dsk.setJMenuBar(mbr);
                dsk.setAppWindLabel(xmdl.sMDL_TOOLTIP);
                // dsk.setAktModulPane(sAktMDL);
//                tbp.setSize(dskPane.getSize());
                tbp.setSize(dskPanel.getSize());
                tbp.setLocation(0, 0);
                //ifrm.setBorder(null);
                //ifrm.setTitle(mdl.sMDL_TOOLTIP);
                tbp.setVisible(true);
                dsk.validate();
                dskScrollPane.revalidate();
                xmdl.sMDL_state = "ACTIVE";
            } else if (xmdl.sMDL_state.equals("ACTIVE")) {
                xmdl.sMDL_state = "PASSIVE";
                tbp.setVisible(false);
                dsk.validate();
            }
            // nastavi potrebnu ikonu pre tlacitko
            xmdl.setBtnMDLicon(xmdl.sMDL_state);
            // QQQ nastavenie tabbed-pane modolu
            //dsk.setAktModulPane(sAktMDL);
        }
        // 2017-6-28 - update spojenia tabbedpane modulu s info-panelom
        if ((selectedTbp != null) && (selectedTbp.getTabCount() > 0)) {
            TabListComponent tbc = null;
            tbc = selectedTbp.getCurrentTabListComponent();
////            Kernel.staticMsg(tbc.getData(),"Selecting TabComponent from: " + sAktMDL);
            setCurrentTabListComponent(tbc);
        }
        else {
            setCurrentTabListComponent(null);
        }
       
    }
    
    public void resizeModulTabs() {
       // Msg("resizeModulTabs -> " + ds.toString());
//this.OutPrintln(// "resizeModulTabs -> " + ds.toString() +
  //     "\nresizeModulTabs TO " + dskPanel.getSize().toString());
        Iterator itr = EOC_xmodules.iterator();
        /*
         * prechod pola xmodul-objektov
         */
        while (itr.hasNext()) {
            Object element = itr.next();
            Modul xmdl;
            xmdl = (Modul) element;
            JTabbedPane tbp;
            tbp = (JTabbedPane) xmdl.getoVtp_MDL();
            tbp.setSize(dskPanel.getSize());
        }
    }

    // vrati meno volajucej metody
    public static String getCurrentMethodName() {
    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    StackTraceElement e = stacktrace[2];//coz 0th will be getStackTrace so 1st
    String methodName = e.getMethodName();
    System.out.println("methodName=" + methodName);
    return methodName;
    }

    // skrateny ekvivalent getCurrentMethodName()
    public static String getCurMtdNm() {
    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    StackTraceElement e = stacktrace[2];//coz 0th will be getStackTrace so 1st
    String methodName = e.getMethodName();
    System.out.println("methodName=" + methodName);
    return methodName;
    }

    // vyhlada databazove objekty obrazovky, a pripade problemov
    // (neexistencia v DB a podobne) objekt oznaci farebne, 
    // a ulozi dovod oznacenia do tooltip-u
    
    public String getObjDbFieldNameProperty(Object o) {
        Class cl;
        String simpleClassName;
        String dbFldNameProperty;
        String dbFldName;
        cl = o.getClass();
        simpleClassName = cl.getSimpleName();
            switch (simpleClassName) {
                case "EOC_DTfield": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                    dbFldNameProperty = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                    break;
                }
                case "EOC_DTtextArea": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                    dbFldNameProperty = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                    break;
                }
                case "EOC_DTcomboBox": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                    dbFldNameProperty = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                    break;
                }
                case "JTextField":
                // preskoci na JFormattedTextField
                case "JFormattedTextField": {
                    JTextField myObj = (JTextField) o;
                    dbFldNameProperty = myObj.getName();
                    break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                    dbFldNameProperty = myObj.getName();
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    dbFldNameProperty = myObj.getName();
                    break;
                }
                case "JRadioButton": {
                    JRadioButton myObj = (JRadioButton) o;
                    dbFldNameProperty = myObj.getName();
                    break;
                }
                default:
                    dbFldNameProperty = "";
                    break;
            } // switch (s) {
            if (dbFldNameProperty == null) {
                dbFldNameProperty = "";
            }
        
        return dbFldNameProperty;
    }
    
    public void initializeDbOjects (DBconnection mycn, XViewer jp,String master_tbl_name) throws SQLException {
        ////System.out.println("initializeDbOjects() mycn -- " + mycn.toString()); 
        String s;
        String n;
        Class cl;
        Component[] c = getComponentsInTabOrder(jp);  // widgety JFrame-kontainera
        //// System.out.println("FUULENGTHHHH IN INITIALIZERis===" + c.length);
        x_block:
        for (Object o : c) {
            if (o instanceof iEOC_DBtableField) {
                 iEOC_DBtableField ifld = (iEOC_DBtableField) o;
                 ifld.initialize(mycn);
                 //// System.out.println("krn.initializeDbOjects:::" + ifld.getDBtableField().dbColumnInfo.columnname
                 ////                    + "  CLASS::" + o.getClass().getName());
                 if (o instanceof DTfield) {
                     DTfield df = (DTfield) o;
                     ////System.out.println( " -><- initializeDbOjects:>" + df.getDbFieldName() + " -> formatterclass: " + df.getFormatter().getClass());
                 }
                 continue x_block;
            }
            else {
////                 System.out.println("krn.NOTinitializeDbOjects NOT iEOC_DBtableField:::" + o.getClass().getName());
                continue x_block;
            }
        } // for (Object o : c) {
    }

    public void manageWidgetTree (eoc.IEOC_VisualObject who, JPanel jp,
                                  String coVsetko, boolean remanage) {
//        Component[] c = jp.getComponents();
        Component[] c = getComponentsInTabOrder(jp);  // widgety JFrame-kontainera
        String myMasterTable = "";
        if (who instanceof XViewer) {
            XViewer xv = (XViewer) who;
            myMasterTable = xv.getMasterTable();
        }
        if (myMasterTable == null) myMasterTable = "<NULL_MASTTER_TABLE>";
        if (myMasterTable.contains(".")) {
            myMasterTable = myMasterTable.substring(myMasterTable.indexOf(".") + 1);
        }
            
        ////System.out.println("FUULENGTHHHH IN MANAGERis===" + c.length + " masterTable==" + myMasterTable);
        /*
        for (Object o : c) {
            // spracovavaju sa len objekty EOC-architektury
            System.out.println("Manangein widget from treeXXX:" + o.getClass().getSimpleName());
        }
        */
        for (Object o : c) {
            // spracovavaju sa len objekty EOC-architektury
            ////System.out.println("Manangein widget from tree:" + o.getClass().getSimpleName() 
            ////        + " isINST:" + (o instanceof iEOC_DBtableField));
            //if (!(o instanceof iEOC_DBtableField)) continue;
            // nastavenie vlastosti EOC-objektov
            if (o instanceof iEOC_DBtableField) {
                 //iEOC_DBtableField myObj = (eoc.widgets.DTfield) o;
                 iEOC_DBtableField myObj = (iEOC_DBtableField) o;
                 ////System.out.println("Manage-DTWidgetFromTree: " + myObj.getDBtableField().getFullColumnName()
                 ////+ "  myMasterTable===" + myMasterTable 
                 ////        + "  objtABLE:" + myObj.getDBtableField().dbColumnInfo.tablename  + " " + myObj.isInitialized());
                 
                 if (!myObj.isInitialized()) myObj.initialize(DBcnWork);
                 ////System.out.println("----- ManagengDTWidgetFromTree: " + myObj.getDBtableField().getFullColumnName());
                 if (o instanceof DTfield) {
                     ////System.out.println( " -><- manageWidgetTreeA:>" + df.getDbFieldName() + " -> formatterclass: " + df.getFormatter().getClass());
                     DTfield df = (DTfield) o;
                     manageWidget_DTfield(who,jp,(DTfield) myObj, coVsetko, remanage);
                 }
                 if (o instanceof DTfield) {
                     DTfield df = (DTfield) o;
                     /////System.out.println( " -><- manageWidgetTreeB:>" + df.getDbFieldName() + " -> formatterclass: " + df.getFormatter().getClass());
                 }
                 if (o instanceof eoc.widgets.DTtextArea) {
                     manageWidget_DTtextArea(who,jp,(DTtextArea) myObj, coVsetko, remanage);
                 }
                 if (o instanceof eoc.widgets.DTcomboBox) {
                     manageWidget_DTcomboBox(who,jp,(DTcomboBox) myObj, coVsetko, remanage);
                 }
                 // zmena farby objektu + tooltip-textu
                 if (myMasterTable != "" && myMasterTable != null) {
//QQQ V3                     if (!myObj.getDBtableField().dbColumnInfo.tablename.equals(myMasterTable)) {
                     if (!myObj.getDBtableField().dbColumnInfo.getFullTableName().equals(myMasterTable)) {
                     if (o instanceof DTfield) {
                         DTfield df = (DTfield) o;
                         df.setBackground(Color.ORANGE);
                         df.setToolTipText("Hlavná tabuľka: " + myMasterTable 
                             + " a tabuľka objektu: " 
//QQQ V3                             + myObj.getDBtableField().dbColumnInfo.tablename
                             + myObj.getDBtableField().dbColumnInfo.getFullTableName()
                             + " sa nezhodujú.");
                     }
                     }
                 }
            }
            /*
            else if ((o instanceof EOC_CDTfield)) {
                 EOC_CDTfield myObj = (EOC_CDTfield) o;
                 if (!myObj.isInitialized()) myObj.initialize(CnIn);
                 ///this.OutPrintln("ManagengCDTWidgetFromTree: " + myObj.getDBtableField().getFullColumnName());
                 manageWidget_DTfield(who,jp,(EOC_DTfield) myObj,
                                         coVsetko, remanage);
                    myObj.setConnection(who.getCnMst());
                    myObj.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                }
            */
            else {
                if (!(o instanceof JLabel) & !(o instanceof JScrollPane)
                    & !(o instanceof JViewport) & !(o instanceof JScrollBar)
                    & !(o instanceof JTextField) & !(o instanceof JPanel)
                    & !(o instanceof JFormattedTextField) & !(o instanceof JComboBox)
                    & !(o instanceof JCheckBox) & !(o instanceof JButton)
                    ) {
                    this.OutPrintln(
                        "manageWidgetTree() - Nepodporovany typ objektu: " + 
                        o.getClass().getSimpleName());
                }
            }
        } // for (Object o : c) {
    } // manageWidgetTree (...
    
    public void manageWidget_DTfield (eoc.IEOC_VisualObject who, JPanel jp,
                  eoc.widgets.DTfield myObj, String coVsetko, boolean remanage) {
        FocusListener myFocList = who.getMyFocusListener();
/*
        if (myFocList==null) 
    System.out.println("NullFocusListener for :" + myObj.getName());
else
    System.out.println("OK FocusListener for :" + ((DTfield)myObj).getDbFieldName());
*/
        if (myObj.getDBtableField().bCreationErrors) {
            myObj.setBackground(Color.red);
            String stt = "Vadný objekt:\n" + myObj.getDBtableField().creationErrors;
            myObj.setToolTipText(stt);
            return; 
        }

       if (myObj.getdbChooseDefinition().length()  > 0)
            addDbChooser(who, jp, myObj);
        
        // uprava zarovnavania objektu podla datoveho typu
////        if (myObj.getDbValueForWrite().equals("")) {
        if (!(myObj instanceof eoc.widgets.DTXfield)) {
        if (myObj.getDBtableField().dbColumnInfo.genericdatatype.equals("decimal") 
         || myObj.getDBtableField().dbColumnInfo.genericdatatype.equals("integer")) 
            myObj.setHorizontalAlignment(JTextField.RIGHT);
        else 
            myObj.setHorizontalAlignment(JTextField.LEFT);
        }
        else 
            myObj.setHorizontalAlignment(JTextField.LEFT);
        
        /////System.out.println( " ->>>- beforeDTfieldInitA:>" + myObj.getDbFieldName() + " -> formatterclass: " + myObj.getFormatter().getClass());
        myObj.setKrn(this);
        if (who instanceof eoc.xinterface.XViewer)
            myObj.setViewer((eoc.xinterface.XViewer) who);
        myObj.initialize(DBcnWork);
        /////System.out.println( " ->>>- afterDTfieldInitB:>" + myObj.getDbFieldName() + " -> formatterclass: " + myObj.getFormatter().getClass());
        
        //if (myFocList!=null) 
            myObj.addFocusListener(myFocList);
    } // public void manageWidget_DTfield (....
    
    public void manageWidget_DTtextArea (eoc.IEOC_VisualObject who, JPanel jp,
                  eoc.widgets.DTtextArea myObj, String coVsetko, boolean remanage) {

        if (myObj.getDBtableField().bCreationErrors) {
            myObj.setBackground(Color.red);
            String stt = "Vadný objekt:\n" + myObj.getDBtableField().creationErrors;
            myObj.setToolTipText(stt);
            return; 
        }
        myObj.initialize(DBcnWork);
        
    } // public void manageWidget_DTfield (....
    
    
    public void manageWidget_DTcomboBox (eoc.IEOC_VisualObject who, JPanel jp,
                  eoc.widgets.DTcomboBox myObj, String coVsetko, boolean remanage) {

        if (myObj.getDBtableField().bCreationErrors) {
            myObj.setBackground(Color.red);
            String stt = "Vadný objekt:\n" + myObj.getDBtableField().creationErrors;
            myObj.setToolTipText(stt);
            return; 
        }

        myObj.initialize(DBcnWork);
        
    } // public void manageWidget_DTfield (....
    
    public Object getValueObject (String sDtType) {
        switch (sDtType.toLowerCase()) {
            case "decimal": 
                return new Double(0.0);
            case "integer":
                return new Integer(0);
            case "date":
                return new Date(0);
            default:
                return new String("");
       } 
        
    }
    
    protected MaskFormatter createStringFormatter(String sDtType, String frmtStr) {
       MaskFormatter formatter = null;
       try {
          formatter = new MaskFormatter(frmtStr);
          Class cls;
          switch (sDtType.toLowerCase()) {
          case "integer":
              cls = java.lang.Integer.class;
              break;
          case "decimal":
              cls = java.lang.Number.class;
              break;
          case "date":
              cls = java.util.Date.class;
              break;
          default:
              cls = java.lang.String.class;
          }
          formatter.setValueClass(cls);
       } catch (java.text.ParseException exc) {
          System.err.println("formatter is bad: " + exc.getMessage());
       }
       
       return formatter;
    }

    private void addDbChooser(IEOC_VisualObject who, JPanel jp, final eoc.widgets.DTfield myObj) {
        actualWho = who;
        dsk.setPreferredSize(dsk.getSize()); 
        dsk.pack();
        Integer myWidth  = myObj.getSize().width;
        Integer myHeight = myObj.getSize().height;
        if (myWidth  == 0) myWidth  = myObj.getPreferredSize().width;
        if (myHeight == 0) myHeight = myObj.getPreferredSize().height;
        String chooseDef = myObj.getdbChooseDefinition();
        if (chooseDef==null || chooseDef.equals("")) {
             return; // nic sa nedeje, objekt nema definovany vyber
        }
        //myObj.setKrn(this);
        /*
       myObj.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                String retVal;
                if (evt.getClickCount()==2) {
                    Component cmp = evt.getComponent(); 
                    cmp.getClass().getSimpleName();
                    ////ako to tu spracovat 
                    if (cmp instanceof eoc.widgets.DTfield) {
                        System.out.println("FiringBASSSMEK");
                        eoc.widgets.DTfield dtf = (eoc.widgets.DTfield) cmp;
                        retVal = 
                          genVyber(actualWho, dtf, dtf.getdbChooseDefinition());
                        krnMsg("rrretVaaal is: " + retVal);
                        putRetValToComponent(retVal, cmp);
                        evt.consume();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
              // this.OutPrintln("----mousePresseeeed" + e.paramString());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
              // this.OutPrintln("----mouseReleaseeeeed" + e.paramString());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
               // this.OutPrintln("----mouseEntereeed" + e.paramString());
            }

            @Override
            public void mouseExited(MouseEvent e) {
              //  this.OutPrintln("----mouseExiteeed" + e.paramString());
            }
        });
       
       myObj.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
          ///// this.OutPrintln("Kernel-actionPerformed-inMyObj!!!");
           System.out.println("krn_e.getActionCommand()==" + e.getActionCommand());
           } 
        });
        myObj.revalidate();
       //// jp.add(btn);
       // btn.setVisible(true);
        //jp.repaint();
        myObj.setBackground((Color.GREEN));
        //krnMsg(myObj.getDbFieldName() + "- doPrdele " + myWidth + " - " + myHeight);
       // this.OutPrintln(myObj.getDbFieldName() + "- kua");
       // this.OutPrintln("mame button, alebo nemame, kua ?");
        */
        myObj.setBackground((Color.GREEN));
    } // addDbChooser()
    
/**
 * Spustenie ziadanej metody sMethod v objekte myThis s parametrami sParameters
 *  
 * @param myThis
 * @param sMethod
 * @param sParameters
 * @return 
 */
    public String CallMethod (Object oCaller, Object oInObj, String sMethod, String sParameters) {
        try {
////            this.OutPrintln("krn.CallMethod() call in: " + myThis.getClass().getName()
////            + " method:" + sMethod + " params:" + sParameters );
            this.OutPrintln("krn.CallMethod() call for:" + FnEaS.sObjName(oCaller) + " in: " + FnEaS.sObjName(oInObj)
            + " method:" + sMethod + " with params:" + sParameters );
            // testovat bude treba podla obsahu sParameters
            Method mtd=null;
            if (sParameters!=null) {
                mtd = oInObj.getClass().getMethod(sMethod, new Class[]  {String.class});
                if (mtd == null) {
                    mtd = oInObj.getClass().getDeclaredMethod(sMethod, new Class[] {String.class});
                }
            }
            else {
                mtd = oInObj.getClass().getMethod(sMethod);
                if (mtd == null) {
                    mtd = oInObj.getClass().getDeclaredMethod(sMethod);
                }
            }
            try {
                Object retVal;
                String sRetVal;
                ///this.OutPrintln("return-type: " + mtd.getReturnType());
                /*
                if (!mtd.getReturnType().toString().equals("class java.lang.String")) {
                    this.OutPrintln("CallMethod(" + mtd.getName() 
                               + "): VrĂˇtenĂˇ hodnota nie je typu 'String'");
                }
                else {
                    this.OutPrintln("CallMethod(" + mtd.getName() 
                               + "): VrĂˇtenĂˇ hodnota JE typu 'String'");
                }
                */
                this.OutPrintln("krn.CallMethod() BEGIN-> " + sMethod);
                if (sParameters != null) {
                   retVal = mtd.invoke(oInObj, sParameters);
                }
                else {
                   //kuknut na INVOKE
//                   retVal = mtd.invoke(myThis, null);
                   retVal = mtd.invoke(oInObj);
                }
                this.OutPrintln("krn.CallMethod() END-> " + sMethod
                + " retVal==" + retVal);
                sRetVal = retVal.toString();
                return sRetVal;
             } catch (IllegalAccessException ex) {
                this.OutPrintln("CallMethod().ERROR-> " + ex.toString());
                return "krn-NOTCALLED-ObjectWrapper=SUPER-IllegalAccessException";
//                Logger.getLogger(EOC_NObject.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                this.OutPrintln("CallMethod().ERROR-> " + ex.toString());
                return "krn-NOTCALLED-ObjectWrapper=SUPER-IllegalArgumentException";
//                Logger.getLogger(EOC_NObject.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                this.OutPrintln("CallMethod().ERROR-> " + ex.toString());
                return "krn-NOTCALLED-ObjectWrapper=SUPER-InvocationTargetException";
//                Logger.getLogger(EOC_NObject.class.getName()).log(Level.SEVERE, null, ex);
            }
       } catch (NoSuchMethodException ex) {
           // Logger.getLogger(EOC_NObject.class.getName()).log(Level.SEVERE, null, ex);
                return "krn-NOTCALLED-ObjectWrapper=SUPER-NoSuchMethodException";

        } catch (SecurityException ex) {
         //   Logger.getLogger(EOC_NObject.class.getName()).log(Level.SEVERE, null, ex);
                return "krn-NOTCALLED-ObjectWrapper=SUPER-SecurityException";

        }
        
    }
    
    public void dskAddMessage(String sMsg) {
       dsk.dskAddMessage(sMsg);
    }
    
    public Map<String, String> saveFrameToDef(Object oPanel) {
        Component[] compArray = null;  // pole objektov, nacitanych z kontainera
        Class  clsObj;         // trieda skumaneho objektu
        String sObjClassName;  // nazov triedy skumaneho objektu
        String sName;     // hodnota vlastnosti 'Name' skumaneho objektu
        String sAccDesc;  // hodnota vlastnosti 'accessibleDescription' skumaneho objektu
        String sScreenValue;   // Obrazovkova hodnota, nastavena v skumanom objekte 
        Map<String, String> mapDef = new HashMap<>();
         if (oPanel instanceof JPanel) {
             JPanel jp = (JPanel) oPanel;
             compArray =  jp.getComponents();  // widgety JFrame-kontainera 
         }
        o_for:
        for (Object o : compArray) {
            clsObj        = o.getClass();           // trieda skumaneho objektu/widgetu
            sObjClassName = clsObj.getSimpleName(); // meno typu skumaneho objektu/widgetu
            // ziskanie meta-dat aktualneho druhu widgetu z vlastnosti 'Name'
             switch (sObjClassName) {
                case "JTextField":  // preskoci na JFormattedTextField
                case "JPasswordField":  // preskoci na JFormattedTextField
                case "JFormattedTextField": {
                    JTextField myObj = (JTextField) o;
                    sName    = myObj.getName();
                    sAccDesc = myObj.getAccessibleContext().getAccessibleDescription();
                    sScreenValue = myObj.getText();
                   break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                    sName    = myObj.getName();
                    sAccDesc = myObj.getAccessibleContext().getAccessibleDescription();
                    sScreenValue = (myObj.isSelected() ? "true" : "false");
                                // 2015-05-04 - myObj.getText();
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    sName    = myObj.getName();
                    sAccDesc = myObj.getAccessibleContext().getAccessibleDescription();
                    sScreenValue = (String) myObj.getSelectedItem();
                    break;
                }
                case "JRadioButton": // zatial sa nebude pouzivat, nie som
                {                // ochotny vytvarat groupy rucne :-)
                    JRadioButton myObj = (JRadioButton) o;
                    sName    = myObj.getName();
                    sAccDesc = myObj.getAccessibleContext().getAccessibleDescription();
                    // sScreenValue = myObj.getText();
                    sScreenValue = (myObj.isSelected() ? "true" : "false");
                    break;
                }
                case "EOC_DTfield": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                    sName    = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                    sAccDesc = myObj.getAccessibleContext().getAccessibleDescription();
                    sScreenValue = myObj.getText();
                   break;
                }
                default:
                    sName = "";
                    sAccDesc = ""; 
                    sScreenValue = "";
                    break;
            } // switch (s) {
             //qqqpto
            if (!sObjClassName.equals("JLabel")
                 && !sObjClassName.equals("JButton")) { // JLabely ma momentalne nezaujimaju
            /*
             this.OutPrintln("sObjClassName == " + sObjClassName + 
                     " sName == " + sName 
                     + " sAccDesc == " + sAccDesc + " sScreenValue == " + sScreenValue);
               * */
             mapDef.put(sName, sScreenValue);
            }
         } // o_for: for (Object o : c) {
 return mapDef;
    }

    public void restorFrameFromDef(Object oPanel, Map<String, String> defMap) {
////        System.out.println("KUUUU>>>>    restoringFrameFromDef()" + (oPanel instanceof JPanel)
////        + "  => " + defMap.toString());
        Component[] compArray = null;  // pole objektov, nacitanych z kontainera
        Class  clsObj;         // trieda skumaneho objektu
        String sObjClassName;  // nazov triedy skumaneho objektu
        String sName;     // hodnota vlastnosti 'Name' skumaneho objektu
        String sDefVal;  // hodnota v defMap
         if (oPanel instanceof JPanel) {
             JPanel jp = (JPanel) oPanel;
             compArray =  jp.getComponents();  // widgety JFrame-kontainera 
         }
///krnMsg("getInsStm-002");
        o_for:
        for (Object o : compArray) {
            clsObj        = o.getClass();           // trieda skumaneho objektu/widgetu
            sObjClassName = clsObj.getSimpleName(); // meno typu skumaneho objektu/widgetu
  //          sObjName = FnEaS.sObjName(o);
            // ziskanie meta-dat aktualneho druhu widgetu z vlastnosti 'Name'
             switch (sObjClassName) {
                case "JTextField":  // preskoci na JFormattedTextField
                case "JPasswordField":  // preskoci na JFormattedTextField
                case "JFormattedTextField": {
                    JTextField myObj = (JTextField) o;
                    sName    = myObj.getName();
                    sDefVal = (String) defMap.get(sName);
////                    System.out.println("restoring: " + sName + "  to:" + sDefVal);
                    if (!(sDefVal==null)) { myObj.setText(sDefVal); }
                   break;
                }
                case "JCheckBox": {
                    JCheckBox myObj = (JCheckBox) o;
                    sName    = myObj.getName();
                    sDefVal = (String) defMap.get(sName);
                    if (!(sDefVal==null)) { 
                        myObj.setSelected
                          (sDefVal.equals("true") ? true : false); 
                    }
                    else { myObj.setSelected(false); }; // null-hodnoty neznasame :o)
                    break;
                }
                case "JComboBox": {
                    JComboBox myObj = (JComboBox) o;
                    sName    = myObj.getName();
                    sDefVal = (String) defMap.get(sName);
                    if (!(sDefVal==null)) { myObj.setSelectedItem(sDefVal); }
                    break;
                }
                case "JRadioButton": // zatial sa nebude pouzivat, nie som
                {                // ochotny vytvarat groupy rucne :-)
                    JRadioButton myObj = (JRadioButton) o;
                    sName    = myObj.getName();
                    sDefVal = (String) defMap.get(sName);
                    if (!(sDefVal==null)) { 
                        myObj.setSelected
                          (sDefVal.equals("true") ? true : false); 
                    }
                    else { myObj.setSelected(false); }; // null-hodnoty neznasame :o)
                    break;
                }
                case "EOC_DTfield": {
                    eoc.widgets.DTfield myObj = (eoc.widgets.DTfield) o;
                    sName    = myObj.getDbFieldName() + "#" + myObj.getDbTableName();
                    sDefVal = (String) defMap.get(sName);
                    if (!(sDefVal==null)) { myObj.setText(sDefVal); }
                   break;
                }
            } // switch (s) {
         } // o_for: for (Object o : c) {
    }
    
    public String frameDefToString(Map<String, String> defMap) {
        // Transformuje HashMap-format frame-value-definicii na attributeString-fromat
        String retVal = "";
  	for (Map.Entry entry : defMap.entrySet()) {
            retVal = retVal + "^" + entry.getKey() + "=" + entry.getValue();
        }
        // odrezanie delimitera ^ na zaciatku retazca
        if (retVal.length() > 0) retVal = retVal.substring(1);
        System.out.println("REEETURNINGVALL:" + retVal);
        return retVal;
        
    }
    public Map<String, String> defStringToFrameDef(String str) {
////    Map<String, String>[] FrameValues; // obsahy connect-panelov
        // Transformuje HashMap-format frame-value-definicii na attributeString-fromat
        HashMap<String, String> mp = new HashMap<>();
        if ((str==null) || (str.length() == 0)) return mp;
        String arrayOfSets[] = str.split("\\^");
        for (int i = 0; i < arrayOfSets.length; i++) {
            String entrSet = arrayOfSets[i];
            entrSet = entrSet.replace("====", "^");
////            System.out.println("ENTRYSETT!!!!: " + entrSet);
            String arrraySet[] = entrSet.split("\\^");
////            System.out.println("ARRAYSETT!!!!: " + arrraySet[0] + " -> " + arrraySet[1]);
            mp.put(arrraySet[0], arrraySet[1]);
        }
        return mp;
        
    }
    
    public Map stringToFrameDef(String frmDef) {
        // Transformuje attributeString-format frame-value-definicii na HashMap-format
        Map<String, String> mapDef = new HashMap<>();
        String sKeyAndVal;
        String sKey;
        String sVal;
        String sDelimiter = frmDef.substring(0,1);
        int iEntries = FnEaS.iNumEntries(frmDef, sDelimiter);
        for (int i = 1; i <= iEntries; i++) {
            sKeyAndVal = FnEaS.sEntry(i, frmDef, sDelimiter);
            sKey       = FnEaS.sEntry(1, sKeyAndVal, "=");
            sVal       = FnEaS.sEntry(2, sKeyAndVal, "=");
            mapDef.put(sKey, sVal);
        }
        return mapDef;
    }
    
    public void setParentDirectory() {
        parentDirectory = new File(System.getProperty("user.dir"));
        parentDirectory = parentDirectory.getAbsoluteFile().getParentFile();
    }
    
    public void checkDirectories() {
        /*
    private File parentDirectory;  // hlavny adresar aplikacie
    private File parentSystemDirectory;  // hlavny systemovy adresar aplikacie EaSys
    private File sysDirectory;  // ./sys
    private File usrDirectory;    // ./usr
    private File staDirectory; // ./sta
*/
        this.OutPrintln("\nEaSys.kernel: Checking system directories ...");
        setParentDirectory();
        this.OutPrintln("\tParent directory: " + parentDirectory + " ... OK");
        String osSlash = get_os_slash();
        parentSystemDirectory = new File(parentDirectory.toString() + osSlash +"EaSys");
        this.OutPrint("\tEaSys directory: " + parentSystemDirectory + " ...");
        if (parentSystemDirectory.exists()) {
            this.OutPrintln(" OK");
        }
        else {
            parentSystemDirectory.mkdirs();
            this.OutPrintln(" created");
        }
        
        sysDirectory = new File(parentSystemDirectory.toString() + osSlash + "sys");
        this.OutPrint("\tSystem directory: " + sysDirectory + " ...");
        if (sysDirectory.exists()) {
            this.OutPrintln(" OK");
        }
        else {
            sysDirectory.mkdirs();
            this.OutPrintln(" created");
        }
        
        usrDirectory = new File(parentSystemDirectory.toString() + osSlash +"usr");
        this.OutPrint("\tUser directory: " + usrDirectory + " ...");
        if (usrDirectory.exists()) {
            this.OutPrintln(" OK");
        }
        else {
            usrDirectory.mkdirs();
            this.OutPrintln(" created");
        }
        
        staDirectory = new File(parentSystemDirectory.toString() + osSlash + "sta");
        this.OutPrint("\tStation directory: " + staDirectory + " ...");
        if (staDirectory.exists()) {
            this.OutPrintln(" OK");
        }
        else {
            staDirectory.mkdirs();
            this.OutPrintln(" created");
        }

        currUsrDirectory = new File(parentSystemDirectory.toString() + 
                           osSlash + "usr" + osSlash + currentUserName);
        this.OutPrint("\tCurrent user directory: " + currUsrDirectory + " ...");
        if (currUsrDirectory.exists()) {
            this.OutPrintln(" OK");
        }
        else {
            currUsrDirectory.mkdirs();
            this.OutPrintln(" created");
        }
        
        currStaDirectory = new File(parentSystemDirectory.toString() + 
                                    osSlash + "sta" + osSlash + currentComputerName);
        this.OutPrint("\tCurrent station directory: " + currStaDirectory + " ...");
        if (currStaDirectory.exists()) {
            this.OutPrintln(" OK");
        }
        else {
            currStaDirectory.mkdirs();
            this.OutPrintln(" created");
        }

        this.OutPrintln(" ... ");
        // kontrola/vytvorenie potrebnych suborov
////        System.out.println("SYSMANEU---systemDirectory.getAbsolutePath()==" 
////                + sysDirectory.getAbsolutePath() + "\\easys\\definitions\\menu.def");
        
        System.out.println();
        String easys_menu_filename = sysDirectory.getAbsolutePath() + osSlash + 
                   "easys" + osSlash + "definitions" + osSlash + "menu.def";
        
        this.OutPrintln("easys_menu_filename !!!!>>>>>:" + easys_menu_filename);
        File fSysMenu = new File(easys_menu_filename);
//QQQ            this.OutPrintln("easys_menu_file_real !!!>>>>>:" + fSysMenu.getAbsolutePath().toString());
        /* fSysMenu.exists()  */
        /* Subor sa pre istotu prepise, je zakladom 
         * pre spravny beh aktualnej verzie Easys */
        /* BIG_ERROR !!!
Apr 07, 2022 11:09:18 AM system.Kernel checkDirectories
SEVERE: null
java.io.IOException: No such file or directory
	at java.base/java.io.UnixFileSystem.createFileExclusively(Native Method)
	at java.base/java.io.File.createNewFile(File.java:1035)
	at system.Kernel.checkDirectories(Kernel.java:4038)
	at system.Kernel.<init>(Kernel.java:201)
	at easys.Easys.<init>(Easys.java:106)
	at easys.Easys.main(Easys.java:214)        
        */
        if (fSysMenu.exists()) fSysMenu.delete();
        try {
            /////krn.OutPrintln("Creating fileeeeeeee");
            fSysMenu.createNewFile();
            PrintWriter writer =
                new PrintWriter(easys_menu_filename, "windows-1250");
//QQQQ            this.OutPrintln("REWRITING !!!!>>>>>:" + easys_menu_filename);
            writer.println(
"// Definicie menu struktury modulu Easys\n" + 
"// Mozne platne riadky sa zacinaju znakom ${OBJEKT_TYPE}\n" + 
"// a su delene znakom |\n" +
"// \n" +
"// !! TENTO SUBOR NEOPRAVOVAT !!\n" +
"// !! Je udrzovany jadrom systemu EaSys\n" +
"// !! Jadro: " + APP_NAME + "  " + APP_VERSION + "\n" +
"// \n" +
"//============================================================================\n" +
"//objecttype |name           |state     |label                |icon    |program   |tooltyp |multiplay(default=true)\n" +
"//==========================================================================================\n" +
"$MENU        |mnEASYS        |STOPPED   |Easys                |//icon  |//prog  |Základný modul systému EaSys\n" + 
"//******************************************************************************************\n" +
"$MENUITEM    |miLicencia     |DISABLED  |Licencia             |//icon  |easys.system.prog.sysconfig.Pnl_aktualna_licencia\n" +
"$SEPARATOR   |--------------------------------------------------------------------------------------------------\n" +
"$MENUITEM    |miSysDef       |//state   |Konfigurácia systému |//icon  |easys.system.prog.sysconfig.Pnl_sysconfig\n" +
"$SEPARATOR   |--------------------------------------------------------------------------------------------------\n" +
"$MENUITEM    |miUsrGroup     |//state   |Skupiny užívateľov   |//icon  |easys.system.prog.skupinyUzivatelov.Pnl_eas_usrgrp\n" +
"$SEPARATOR   |--------------------------------------------------------------------------------------------------\n" +
"$MENUITEM    |miUsers        |//state   |Užívatelia  |//icon  |easys.system.prog.uzivatelia.Pnl_eas_users\n" +
"$MENUITEM    |miPermDef      |//state   |Prístupové práva užívateľov |//icon  |system.perm.Pnl_usrperms\n" +
"$SEPARATOR   |--------------------------------------------------------------------------------------------------\n" +
"$MENUITEM    |miCronDef      |//state   |Nastavenie paralelnej vrstvy 'Cron' |//icon  |easys.system.cron.Pnl_cronconfig\n" +
"$SEPARATOR   |--------------------------------------------------------------------------------------------------\n" +
"$MENUITEM    |miPrnDef       |//state   |Nastavenie tlačiarne |//icon  |easys.system.print.Pnl_prnconfig\n" +
"//******************************************************************************************\n" +
"$MENU        |mnCalendar     |STOPPED   |Kalendár                |//icon  |//prog  |Základný modul systému EaSys\n" + 
"//******************************************************************************************\n" +
"$MENUITEM    |miMyKalEvent   |//state   |Nová udalosť |//icon  eoc.calendar.Pnl_myNewEvent\n" +
"$SEPARATOR   |--------------------------------------------------------------------------------------------------\n" +
"$MENUITEM    |miMyKalendar   |//state   |Môj kalendár |//icon  |eoc.calendar.Pnl_myCalendar\n" +
"$SEPARATOR   |--------------------------------------------------------------------------------------------------\n" +
"$MENUITEM    |miKalendarContainer   |//state   |Všeobecný kalendár |//icon  |eoc.calendar.Pnl_calendarContainer\n" +
"//******************************************************************************************\n" +
"$MENU      |mnNastroje     |DISABLED  | Nástroje     |//icon  |//prog  |easys.system.tools.Pnl_systemtools\n" +
"//******************************************************************************************\n" +
"$MENUITEM    |miDirComp   |//state   |Porovnanie adresárov |//icon  |system.tools.Pnl_directoryComparator\n" +
"//******************************************************************************************\n"         
        );
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    private void checkBaseFiles() {
    }
    public String getEASobjectID(Component THIS) {
        String retVal = "";
        retVal = retVal + FnEaS.sEntry(1, THIS.getParent().toString(),"[")
         + "."
         + FnEaS.sEntry(1, THIS.toString(),"[")
         + "."
         + THIS.getName();
        return retVal;
    }
    
    public String getEASobjectName(Component obj) {
        String retVal = "";
        retVal = retVal + FnEaS.sEntry(1, obj.toString(),"[");
        retVal = FnEaS.sEntry(FnEaS.iNumEntries(retVal,"."),retVal,".");
        return retVal;
    }

    public void sleep(int iMiliSec) {
            try {
                Thread.sleep(iMiliSec);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }            
    }

    public String validateObjectValues (Connection cnn, JPanel jp, String tblName) {
        Component[] compArray = getComponentsInTabOrder(jp);  // widgety JFrame-kontainera
        for (Component comp: compArray) {
           //getMethod();         
           /* comp */ 
        }
        return "";
    }

    public String OLD_getDBcolumnInfo (Connection cnn, String tal, String col, String Info) {
        String myTbl = tal;
        // rozobratie parametra na owwner + nazov_tabulky
        if (tal.contains(".")) {
            myTbl = tal.substring(tal.lastIndexOf(".") + 1);
        }
        switch (getDBtype(cnn).toUpperCase()) {
            case "POSTGRES": {
                return getPostgresDBcolumnInfo(cnn, myTbl, col, Info);
            }
            case "SYBASE": {
                return OLD_getSybaseDBcolumnInfo(cnn, myTbl, col, Info);
            }
        } // switch (Info) {
        return "";
    }
    
    public ResultSet SQLQ_getQueryResultSet(Connection cnn,String qry) { 
        ResultSet retval;
        retval = DBcnWork.getDbDriver().SQL_getQueryResultSet (cnn, qry);
        return retval;
  }
/*
    public static ResultSet getStaticQueryResultSet(Connection cnn,String qry) {
        Statement getStatQuery_stm;
        ResultSet getStatQuery_rst = null;
        try {
             getStatQuery_stm = cnn.createStatement();
             getStatQuery_rst = getStatQuery_stm.executeQuery(qry);
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
      return getStatQuery_rst;
  }
    */
/*
    public static Object getStaticQueryAsValue (Connection cnn,String qry) {
        Statement getStatQuery_stm;
        Object    qryValue = null;
        try {
             getStatQuery_stm = cnn.createStatement();
             qryValue = getStatQuery_stm.executeQuery(qry);
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
      return qryValue;
  }
    */
    /**
     * Spusti sql-príkaz stm. bCommit vyvolá commit, po vykonaní príkazu.
     * V prípade úspechu vrátí TRUE inak FALSE 
     * @param cnn
     * @param stm
     * @param bCommit
     * @return 
     */
    public boolean SQLQ_callSqlStatement(Connection cnn, String stm, boolean bCommit) { 
      boolean retval;
      retval = DBcnWork.getDbDriver().SQL_callSqlStatement(cnn, stm, bCommit);
      return retval;
  }

/**
 * Vrátí hodnotu dotazu. Dotaz musí obsahovať stlpec s názvom valName.
 * POZOR! V prípade, že výsledok dotazu obsahuje viac riadkov, vrátí hodnotu
 * z prvého riadku !
 * 
 * @param cnn
 * @param qry
 * @param valName
 * @return 
 */
public Object SQLQ_getQueryValue
      (Connection cnn,String qry, String valName) {
    Object retval;
    retval = DBcnWork.getDbDriver().SQL_getQueryValue (cnn, qry, valName);
    return retval;
}   

public boolean SQLQ_Exists
      (Connection cnn,String tblName, String sWhere) {
    boolean retval;
    retval = DBcnWork.getDbDriver().SQL_Exists(cnn, tblName, sWhere);
    return retval;
}   
      
/**
 * Vrátí hodnotu dotazu. Dotaz musí obsahovať stlpec s názvom 'retval'.
 * POZOR! V prípade, že výsledok dotazu obsahuje viac riadkov, vrátí hodnotu
 * z prvého riadku !
 * 
 * @param cnn
 * @param qry
 * @param no_error
 * @return 
 */      
public /* static */ Object SQLQ_getQueryAsValue
      (Connection cnn,String qry, boolean no_error) {
    Object retval;
    retval = DBcnWork.getDbDriver().SQL_getQueryAsValue (cnn, qry, no_error);
    return retval;
}    

/*
public static Object getStaticQueryValue
      (Connection cnn,String qry, String valName) throws SQLException {
    // ziskanie udajov z DB      
    ResultSet rs = getStaticQueryResultSet(cnn, qry);
    rs.next();
    Object o = rs.getObject(valName);

    return o;
}    

public static Object getStaticQueryValue
      (Connection cnn,String qry) throws SQLException {
    // ziskanie udajov z DB      
    ResultSet rs = getStaticQueryResultSet(cnn, qry);
    rs.next();
    Object o = rs.getObject(valName);

    return o;
}
      */
/**
 * Vrátí výsledok dotazu qry v trojdimenzionálnom poli (pole s poliami obsahu riadkov)
 * Ked je withHeaders zapnutí, na prvom riadku sú názvy stľpcov 
 * 
 * @param cnn
 * @param qry
 * @param withHeaders
 * @return 
 */      
public Object[][] SQLQ_getQueryResultSetAsArray
      (Connection cnn,String qry, boolean withHeaders) {
    Object[][] retval;
    retval = DBcnWork.getDbDriver().SQL_getQueryResultSetAsArray (cnn, qry, withHeaders);
    return retval;
}    

/**
 * Transformuje údaje v resultSet-e na trojdimenzionálne pole (pole s poliami obsahu riadkov)
 * Ked je withHeaders zapnutí, na prvom riadku sú názvy stľpcov 
 * 
 * @param rs
 * @param withHeaders
 * @return
 * @throws SQLException 
 */      
public Object[][] SQLQ_getResultSetAsArray (ResultSet rs, boolean withHeaders)
    throws SQLException {
    Object[][] retval;
    retval = DBcnWork.getDbDriver().SQL_getResultSetAsArray (rs, withHeaders);
    return retval;
}

/**
 * Vrátí pole eoc.dbdata.ColumnDefinition objektov, vytvorených na základe 
 * štruktúry resultSet-u rs
 * 
 * @param cn
 * @param rs
 * @return
 * @throws SQLException 
 */
public eoc.dbdata.ColumnDefinition[] SQLQ_getResultSetAsColumnDefinitionArray(Connection cn, ResultSet rs) 
    throws SQLException {
    eoc.dbdata.ColumnDefinition[] retval;
    retval = DBcnWork.getDbDriver().SQL_getResultSetAsColumnDefinitionArray (cn, rs);
    return retval;
}    

public String getAppName() {
    return APP_NAME;
}
public String getAppVersion() {
    return APP_VERSION;
}
public String getAppDescription() {
    return APP_DESCRIPTION;
}

public void setStanicaUserAttribute(String attrName, String attrValue) throws IOException {
//    this.OutPrintln("setUserAttribute(): user:" + DBcnWork.getUsr() + " ATTR: " + attrName + " TO:" + attrValue);
//    File f = new File("./usr");
    usrProps.setProperty(attrName,attrValue);
}

public String getStanicaUserAttribute(String attrName) {
//    this.OutPrintln("setUserAttribute(): user:" + DBcnWork.getUsr() + " ATTR: " + attrName + " TO:" + attrValue);
//    File f = new File("./usr");
    String rv = "";
    if (usrProps!=null) {
            rv = usrProps.getProperty(attrName);
    }
    return rv;
    
}

public void showLog() {
    if (logFrm == null) {
        logFrm = new eoc.readers.Frm_FileViewer();
        logFrm.setVisible(true);
        try {
            logFrm.showFile(fUserOutFile);
        } catch (IOException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    else {
        /*
        if (!logFrm.isVisible()) logFrm.setVisible(true);
        else {
            if (logFrm.getExtendedState() == JFrame.ICONIFIED)
                logFrm.setState(JFrame.NORMAL);
        }
        */
        logFrm.setVisible(true);
        logFrm.setState(JFrame.NORMAL);
    }   
}

public void showMyCalendar() {
    if (win_mycalendar == null) {
        win_mycalendar = new Win_myCalendar(this, DBcnWork);
        win_mycalendar.setVisible(true);
    }
    else {
        win_mycalendar.setVisible(true);
        win_mycalendar.setState(JFrame.NORMAL);
    }
}
        
public void showMenuBar() {
   OutPrintln("\nKernel: Actual Menu structure:");
   OutPrintln("==================================");
   for (Modul m : EOC_xmodules) {
       JMenuBar jmb = m.mbar_MDL;
       int mCnt = jmb.getMenuCount();

       OutPrintln("MODUL: " + m.sMDL + " - " + m.sMDL_TOOLTIP 
             + " - " + m.sMDL_state + " MenuCount()==" + mCnt);

       for (int imn = 0; imn < mCnt; imn++) {
           JMenu mn = jmb.getMenu(imn);
           if (mn==null) continue;
           int iCnt = mn.getItemCount();
           OutPrintln(" MENU: " + mn.getText() + " - " + mn.getToolTipText() + " ItemCount()==" + iCnt);
           for (int i = 0; i < iCnt; i++) {
               JMenuItem itm = mn.getItem(i);
               if (itm==null) continue;
               OutPrintln("  ---> Menu-item: " + itm.getText() /* getClass().SimpleName()*/);
           }
       }
   }    
}

public system.perm.Permd getPermd() {
    if (perm==null) {
       perm  = new system.perm.Permd();
       perm.setKernel(this);
    }
    
    return perm;
}

public system.cron.Crond getCrond() {
    if (cron==null) {
       Kernel.staticMsg("E", "Kernel: Cron daemon not exist.", "Kernel.getCrond()");
//       cron  = new system.cron.Crond();
//       cron.setKernel(this);
    }
    
    return cron;
}

public void perm__Update(boolean bPermUpdating) {
     //perm.permUpdate()    
    // POKUS
    
   for (Modul m : EOC_xmodules) {
       JMenuBar jmb = m.mbar_MDL;
       int mCnt = jmb.getMenuCount();

////       System.out.println("MODUL: " + m.sMDL + " - " + m.sMDL_TOOLTIP + " - " + m.sMDL_state + " MenuCount()==" + mCnt);

       for (int imn = 0; imn < mCnt; imn++) {
           JMenu jmn;
           jmn = jmb.getMenu(imn);
           /*
           Menu mn;
           if (jmn instanceof system.modul.Menu) {
             mn = (Menu)jmn; 
             mn.setText(mn.getText() + " [UPPD]");
           }
           */
           // old style
           if (bPermUpdating)
               jmn.setText(jmn.getText() + " [UPPD]");
           else
               jmn.setText(jmn.getText().replace(" [UPPD]",""));
           if (jmn==null) continue;
           
           int iCnt = jmn.getItemCount();
  ////         System.out.println("MENU: " + mn.getText() + " - " + mn.getToolTipText() + " ItemCount()==" + iCnt);
           for (int i = 0; i < iCnt; i++) {
               JMenuItem itm = jmn.getItem(i);
               if (itm!=null) 
                   if (bPermUpdating)
                       itm.setText(itm.getText() + " [UPPD]");
                   else
                      itm.setText(itm.getText().replace(" [UPPD]",""));
 ////              if (itm!=null) 
////                  System.out.println("---> Menu-item: " + itm.getText() /* getClass().SimpleName()*/);
           }
       }
   }
} // public void permUpdate(boolean bPermUpdating)

public ArrayList<Modul> getEOC_xmodules() {
    return EOC_xmodules;
}

/**
 * 
 * 
 * @param cn
 * @param stmStr
 * @param bCommit
 * @return 
 */
public String SQLQ_runStatement(Connection cn, String stmStr, boolean bCommit) {
    String retval;
    retval = DBcnWork.getDbDriver().SQL_runStatement(cn, stmStr, bCommit);
    return retval;
}

public String runInsertStatement(Connection cn, String tbl, String flds, String values,
            boolean includeZapisal, boolean includeZmenil, boolean bCommit) {

    String nameToken = "^";
    String valueToken = "^";
    String stmStr = "INSERT INTO " + tbl + " (";
    nameToken = nameToken + flds;
    valueToken = valueToken + values;
    if (includeZapisal) nameToken = nameToken + "^c_zapisal";
    if (includeZmenil)  nameToken = nameToken + "^c_zmenil";
    String updStamp = getUpdateStamp();
    if (includeZapisal) valueToken = valueToken + "^'" + updStamp + "'";
    if (includeZmenil)  valueToken = valueToken  + "^'" + updStamp + "'";

    nameToken  = nameToken.replace('^', ',');
    valueToken = valueToken.replace('^', ',');
    
    if (nameToken.length() > 1)  nameToken  = nameToken.substring(1);
    if (valueToken.length() > 1) valueToken = valueToken.substring(1);
        stmStr = stmStr + nameToken + ") VALUES (" + valueToken + ")";
    System.out.println(">>>>>#####>>>>>>>>>>>>>krn.runInsertStatement()::::" + stmStr);
        try {
            Statement stmt = cn.createStatement();
            stmt.executeQuery(stmStr);
            if (bCommit) cn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    return "";
}

public String runUpdateStatement(Connection cn, String tbl, String flds, String values,
            String sWhere, boolean includeZapisal, boolean includeZmenil, boolean bCommit) {

    String nameToken = "^";
    String valueToken = "^";
    String stmStr = "INSERT INTO " + tbl + " (";
    nameToken = nameToken + flds;
    valueToken = valueToken + values;
    if (includeZapisal) nameToken = nameToken + "^c_zapisal";
    if (includeZmenil)  nameToken = nameToken + "^c_zmenil";
    String updStamp = getUpdateStamp();
    if (includeZapisal) valueToken = valueToken + "^'" + updStamp + "'";
    if (includeZmenil)  valueToken = valueToken  + "^'" + updStamp + "'";

    nameToken  = nameToken.replace('^', ',');
    valueToken = valueToken.replace('^', ',');
    
    if (nameToken.length() > 1)  nameToken  = nameToken.substring(1);
    if (valueToken.length() > 1) valueToken = valueToken.substring(1);
        stmStr = stmStr + nameToken + ") VALUES (" + valueToken + ")";
        try {
            Statement stmt = cn.createStatement();
            stmt.executeQuery(stmStr);
            if (bCommit) cn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    return "";
}

/*
tu treba pridat runInsertStetement(connection,tablename,fields,values,bPridatZapisal,bPridatZmenil, bCommit)
        a
tu treba pridat runUpdateStetement(connection,tablename,fields,values,bPridatZapisal,bPridatZmenil, bCommit)
*/
public static String evaluateDbScreenValueDefinition(
    eoc.iEOC_DBtableField oCaller, 
    Connection cn, String dbScreenValueDefinition) {
    String retval = null;
    dbScreenValueDefinition = dbScreenValueDefinition.trim();
    String sCommand = "";
    Object oRetVal = null;
   //// vyhladat retazec dbScreenValueDefinition v kodoch easys aj nzbd a upravit co je treba
    // 2017-07-18
    if (dbScreenValueDefinition.equals("") || dbScreenValueDefinition == null)
        oRetVal = oCaller.getDBtableField().valueObjectForWrite.toString();
    /* QQQ 2017.10.29 vypnute kvoli DB-driver-u
    else if (dbScreenValueDefinition.startsWith("SQL@")) {
        sCommand = dbScreenValueDefinition.substring(4);
        oRetVal = SQL_getQueryAsValue(cn, sCommand, false); // false==NO_ERROR
    }
    */
    else if (dbScreenValueDefinition.startsWith("FUN@")) {
        sCommand = dbScreenValueDefinition.substring(4);
        Method mtd = Kernel.getMethod(oCaller, sCommand);
        try {
             oRetVal = mtd.invoke(oCaller,new Object[0]);
        } catch (IllegalAccessException ex) {
             Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
             Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
             Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    retval = oRetVal.toString();
    
    return retval;
}

public String getUpdateStamp() {
    return getUpdateStamp(currentUserName);
}

public String getUpdateStamp(String owner) {
    String retval = "";
    Integer[] aktDT = FnEaS.getTimeDataAt6();
    retval = String.format("%04d", aktDT[0]) 
             + "-" + String.format("%02d", aktDT[1])
             + "-" + String.format("%02d", aktDT[2]) 
             + " " + String.format("%02d", aktDT[3]) 
             + ":" + String.format("%02d", aktDT[4]) + " " + owner;
    return retval;
}
/**
 * Pridanie/update univerzálneho číselníka
 * 
 * @param cn
 * @param typCis
 * @param nazovCis
 * @param popisCis
 * @param lblPolSkrat
 * @param polSkratLength
 * @param lblPolNaz
 * @param polNazLength
 * @param lblPolPopis
 * @param polPopisLength
 * @param metadata 
 */
public void add_UniCiselnik(Connection cn,String typCis, String nazovCis, 
                            String popisCis, 
                            String lblPolSkrat, int polSkratLength,
                            String lblPolNaz, int polNazLength,
                            String lblPolPopis, int polPopisLength,
                            String metadata) {
            // standardny metaHeaderDelimiter je: ⌂
            String s;
            Object o = SQLQ_getQueryValue(cn, "select * from eas_uni_cis_def where c_typ_cis = '" 
                                         + typCis + "'", "c_typ_cis");
             s = (o != null?o.toString():"<NULL>");
//            ked existuje prepisat, ked nie, vytvorit
            if (s.equals("<NULL>")) {
                System.out.println("Kernel: Creating uni_cis_def for: " + s);
                s = "insert into eas_uni_cis_def (" 
                    + "c_typ_cis,c_nazov_cis,c_popis_cis,c_metadata,"
                    + "c_skrat_pol_label,i_skrat_pol_length,"
                    + "c_naz_pol_label,i_naz_pol_length,"
                    + "c_popis_pol_label,i_popis_pol_length,"
                    + "C_zapisal,c_zmenil"
                    + ")"
                    + " values ('"
                    + typCis + "','"
                    + nazovCis + "','"
                    + popisCis + "','"
                    + metadata + "','"
                    + lblPolSkrat + "',"
                    + polSkratLength + ",'"
                    + lblPolNaz + "',"
                    + polNazLength + ",'"
                    + lblPolPopis + "',"
                    + polPopisLength + ",'"
                    + getUpdateStamp() + "','"
                    + getUpdateStamp() + "'"
                    + ");";
                    SQLQ_callSqlStatement(cn,s,true);
            }
            else {
                s = "update eas_uni_cis_def set " 
                    + "c_nazov_cis ='" + nazovCis + "'"
                    + ",c_popis_cis ='" + popisCis + "'"
                    + ",c_metadata ='" + metadata + "'"
                    + ",c_skrat_pol_label ='" + lblPolSkrat + "'"
                    + ",i_skrat_pol_length =" + polSkratLength + ""
                    + ",c_naz_pol_label ='" + lblPolNaz + "'"
                    + ",i_naz_pol_length =" + polNazLength + ""
                    + ",c_popis_pol_label ='" + lblPolPopis + "'"
                    + ",i_popis_pol_length =" + polPopisLength + ""
                    + ",c_zmenil ='" + getUpdateStamp() + "'"
                    + " where c_typ_cis = '" + typCis + "';";
                    SQLQ_callSqlStatement(cn,s,true);
            }
                    
    
}

public String getMetaHeaderDelimiter() {
    // standardny metaHeaderDelimiter je: ⌂
    return metaHeaderDelimiter;
}

public void loadPermCacheForCurrentUser() {
    perm.loadPermCacheForUser("USER", currentUserName);
}

/*
    public String krn_sendMessage(Object oOwner, Object oSender, String sMessage,
            Object sParameters, String sLink, String sVector, String sOthers,
            boolean bOwnerSender) {
        //
        // zavedenim IEOC_VObject (visible) t.j. telo objektu :-) a IEOC_NObject
        // (notvisible) t.j. dusa objektu :-) sa to tu skoplikovalo, ale kaslat
        // na to, pokial tato metoda bude 'semafor'-om, ktory uriadi dalsiu
        // cinnost
        // ****************************************************
        //
         // priklad volania: krn.krn_sendMessage((Object) this,
         //                      "lastRow", "", "navigation", "source", "", true);
        if (!bOwnerSender) { // je to standardne volanie obalene touto metodou
            return krn_sendMessage(oSender, sMessage,
                                   sParameters, sLink, sVector, sOthers); 
        }
        //if (1==1) return ""; //QQQDOCASNE
        
////        System.out.println("\n\n\n OwnerTypeMessageSender: " + (oSender==null?"NULL":FnEaS.sObjName(oSender))
////                             + "  Sending begin for owner-message: " + sMessage + " For link:" + sLink);
        debugOut(this, 0, "Sending begin for owner-message: " + sMessage + " For link:" + sLink);
        sLink = sLink.toUpperCase();
        sVector = sVector.toUpperCase();

        Iterator itr = EOC_Links.iterator();
        // prechod pola EOC_Linkov 
        Integer iNumManagedLinks = 0;
        // String rv = " don't possible to sent for: ";  // return value
         String rv = " return-value not assigned ";  // default value
        link_read:
        while (itr.hasNext()) {
            Object element = itr.next();
            // ziskanie link-objektu
            final CommLink lnk = (CommLink) element;
            if ((!lnk.sEOC_Link.equals(sLink)) && (!sLink.equals("ALL")) && (!sLink.equals("*"))) {
                System.out.println("OWNMSGSENDD_NOLINKK: " + lnk.sEOC_Link);
                continue link_read; // link nie je pozadovaneho typu, jede se dal
            }
            if (lnk.oOwnerAppl != oSender) { 
                //System.out.println("OWNMSGSENDD_NOSENDERR: " + FnEaS.sObjName(lnk.oOwnerAppl));
                continue link_read; // sender nie je ownerom aktualeho linku
            }
//            System.out.println("OWNMSGSENDD_SENDFOR LINKK: " + lnk.sEOC_Link 
//                      + " SRCOBJ:" + FnEaS.sObjName(lnk.oVSource) 
//                      + " TARGOBJ:" + FnEaS.sObjName(lnk.oVTarget));
            // link je pozadovaneho typu a ownerom je oSender
            // sprava sa doruci target-objektom aj sorce-objektom
                debugOut(this, 0, "Sending owner-message for all objects of link: " + sMessage);
                iNumManagedLinks = iNumManagedLinks + 1;
                rv = sSendMsgToLinkSource(oOwner, oSender, lnk, sMessage, sParameters, sOthers);
                rv += sSendMsgToLinkTarget(oOwner, oSender, lnk, sMessage, sParameters, sOthers);
        } // while (itr.hasNext()) {
        
        // dany link momentalne neexistuje, je to v poriadku
        // Vyssie by sa malo testovat aj stav, ked je link disablovany
        if (iNumManagedLinks==0) {
            debugOut(this, 0, "link: " + sLink + " for owner-object: " + GetObjName(oSender) + " not exist. (may not be a problem)");
            return "";
            
        }

        if (rv.equals("")) {
            debugOut(this, 0, "Empty return value:" + GetObjName(oSender) + (char) 10 + "Owner-message: "
                   + sMessage + " was successfully sended for: " + sLink + "." + sVector
                   + " (No message=good message - by UNIX/LINUX)");
            return "";
        } else {
            debugOut(this, 5, GetObjName(oSender) + (char) 10 + "Owner-Message: "
                   + sMessage + " don't send properly for: " + sLink + "." + sVector
                   + " returned value: " + rv);
            return "EOC_ERROR=OWNER_MESSAGE_NOT_SEND_PROPERLY "
                    + "#(message:" + sMessage + " for link: " + sLink + 
                    " from object: " + GetObjName(oSender) //+
                    //" to object:"+ GetObjName(oSender)
                     + ")";
        }
    }
    */
    
    public void add_BaseUniCis (Connection cn, String typCis,
                                String writeStyle, Object[][] data) {
        //// System.out.println("DTTL:" + data.length);
        boolean bRewrite = writeStyle.toUpperCase().contains("REWRITE");
        String sql;
        Object o;
        for (int i = 0; i < data.length; i++) {
            
            Object[] row = data[i];
            sql = "SELECT id_eas_uni_cis_def AS retval FROM eas_uni_cis_def " + 
                "WHERE c_typ_cis = '" + typCis + "'";
            o = SQLQ_getQueryAsValue(cn, sql, true); // ziskanie ID-u definicie ciselnika
            Integer iDefID = Integer.parseInt(o.toString());
            sql = "SELECT id_eas_uni_cis AS retval FROM eas_uni_cis " + 
                "WHERE c_typ_cis = '" + typCis +
                "' AND c_skratka = '" + row[0] + "'";
            o = SQLQ_getQueryAsValue(cn, sql, true); // test, ci uz existuje
            String updStamp = getUpdateStamp();
            String domusTbl = null;
            Integer tblId    = null;
            if (row.length > 3) domusTbl = row[3].toString();
            if (row.length > 4) tblId    = Integer.parseInt(row[4].toString());
            sql = ""; // mazanie SQL-prikazu
            if (o==null) { // zalozenie polozky do cielnika
                /*
                sql = "INSERT INTO eas_uni_cis " + 
                    "(id_eas_uni_cis_def,c_typ_cis,c_skratka,c_naz_pol_cis,c_popis,c_domus_table,id_domus_table)" +
                    "VALUES (" + iDefID + ",'"+ typCis + "','" + row[0] + "','" 
                               + row[1] + "','" + row[2] + "','" + domusTbl + "'," + tblId + ")";
*/
                sql = "INSERT INTO eas_uni_cis " + 
                    "(id_eas_uni_cis_def,c_typ_cis,c_skratka,c_naz_pol_cis,c_popis)" +
                    "VALUES (" + iDefID + ",'"+ typCis + "','" + row[0] + "','" 
                               + row[1] + "','" + row[2] + "')";
                
                SQLQ_callSqlStatement(cn, sql, true /* commit */);
            }
            
            /* toto sluzil len na testovanie
            for (int ir = 0; ir < row.length; ir++) {
                 System.out.print(row[ir]);
            }
            System.out.println();
             */
            
        }
    }
    
    public int SQLQ_getNumDbColumns (Connection cnn, String tblName) {
        int retval;
        retval = DBcnWork.getDbDriver().SQL_getNumDbColumns (cnn, tblName);
        return retval;
    }
    
    public Hashtable<String, Object> SQLQ_getSqlTableAsHashtable(Connection cnn, String tblName) {
        Hashtable<String, Object> retval;
        retval = DBcnWork.getDbDriver().SQL_getSqlTableAsHashtable (cnn, tblName);
        return retval;
    }
    
    public HashMap<String, Object> SQLQ_getSqlTableAsHashMap(Connection cnn, String tblName) {
        HashMap<String, Object> retval;
        retval = DBcnWork.getDbDriver().SQL_getSqlTableAsHashMap (cnn, tblName);
        return retval;
    }
    
public boolean SQLQ_getDbTableStructToHashMap(Connection cnn, String tblName,
                                          HashMap<String,Object> hmp) {
        boolean retval;
        retval = DBcnWork.getDbDriver().SQL_getDbTableStructToHashMap(cnn, tblName,hmp);
        return retval;
}
    
  /**
   * Nuluje hodnoty hashtabulky
   * 
   * v Hashtabulke Hashtable<String, Object> vymeni objekty
   * za objekty rovnakeho datoveho typu s nulovymi hodotami
   * 
   * @param htb 
   */      
    public void clearHashTableValues(Hashtable<String, Object> htb) {
       Enumeration<String> enumKey = htb.keys();
       while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            Object val = htb.get(key);
            Object dto = null;
            String dtType = val.getClass().getSimpleName();
            switch (dtType.toLowerCase()) {
                case "double":
                     dto = new Double(0);
                     break;
                case "integer":
                     dto = new Integer(0);
                     break;
                case "calendar":
                     dto = Calendar.getInstance();
                     break;
                default:
                    dto = new String();
           } // switch (dtType.toLowerCase()) {
       } // while(enumKey.hasMoreElements()) {
    }

    public void clearHashMapValues(HashMap<String, Object> htb) {
       Set<String> enumSet = htb.keySet();
      for (Object o: enumSet) {
            String key = o.toString();
            Object val = htb.get(key);
            Object dto = null;
            String dtType = val.getClass().getSimpleName();
            switch (dtType.toLowerCase()) {
                case "double":
                     dto = new Double(0);
                     break;
                case "integer":
                     dto = new Integer(0);
                     break;
                case "calendar":
                     dto = Calendar.getInstance();
                     break;
                default:
                    dto = new String();
           } // switch (dtType.toLowerCase()) {
       } // while(enumKey.hasMoreElements()) {
    }

    public boolean SQLQ_getSqlRowToHashTable(Connection cnn,
                             Hashtable<String,Object> htb, String sQry) {
        boolean retval;
        retval = DBcnWork.getDbDriver().SQL_getSqlRowToHashTable (cnn, htb, sQry);
        return retval;
    }

    public boolean SQLQ_getSqlRowToHashMap(Connection cnn,
                             HashMap<String,Object> htb, String sQry) {
        boolean retval;
        retval = DBcnWork.getDbDriver().SQL_getSqlRowToHashMap (cnn, htb, sQry);
        return retval;
    }

    public boolean SQLQ_getDataToDbHashMap(
                             HashMap<String,Object> htb, Object[] hdrs, Object[] data) {
        boolean retval;
        retval = DBcnWork.getDbDriver().SQL_getDataToDbHashMap(htb, hdrs, data);
        return retval;
    }

    public String calToStr(Calendar cal, String fmt) {
        java.util.Date dat = cal.getTime();
        return new SimpleDateFormat(fmt).format(dat);
    }
    
    public String getTechusFileRepository() {
        String s;
        s = sysDirectory + get_os_slash() + "docs" + get_os_slash() + "Techus";
        return s;
    }

    public static int getMessageSelection(JOptionPane optionPane) {
    int returnValue = JOptionPane.CLOSED_OPTION;

    Object selectedValue = optionPane.getValue();
    if (selectedValue != null) {
      Object options[] = optionPane.getOptions();
      if (options == null) {
        if (selectedValue instanceof Integer) {
          returnValue = ((Integer) selectedValue).intValue();
        }
      } else {
        for (int i = 0, n = options.length; i < n; i++) {
          if (options[i].equals(selectedValue)) {
            returnValue = i;
            break; // out of for loop
          }
        }
      }
    }
    return returnValue;
  }

        public static String cd (String s) {
            String v = "";
            String thestr = "";
             Double dRnd = (Math.random() * 94) + 33;
             Integer iRnd = dRnd.intValue();
           for(int i = 0; i < s.length(); i++) {
                 char c = s.charAt(i);
                 int ic = ((int) c) * (i%2==0?2:3);
                 String sic = String.format("%03d", ic);
                 thestr = thestr +  sic;
            }
            thestr = iRnd + thestr;
            thestr = cdx(thestr.trim());
            return thestr;
        }

        private static String cdx (String s) {
            String v = "";
            String thestr = "";
            for(int i = 0; i < s.length(); i++) {
                 String c = s.substring(i,i+1);
                 Integer ic = Integer.parseInt(c);
                 thestr = thestr + sc[ic];
            }
            return thestr;         
        }
        
        public static String dc (String s) {
            String thestr = "";
            Integer idx;
            String ci;
            String c = "";
            ci = dcx(s.trim());
            for(int i = 0; i < ci.length(); i++) {
                 c = c + ci.substring(i,i+1);
                 if (c.length() == 3) {
                     int iac = (int) Integer.parseInt(c) / ((i/3)%2==0?2:3); 
                     c = "";
                     thestr = thestr + (char) iac;
                 }
            }
            return thestr;
        }
        
        public static String dcx (String s) {
            String thestr = "";
            Integer idx;
            String ci;
            String c;
            Integer cut = s.length()%3;
            if (cut == 0) cut = 3; // kriticky moment, ktory som takmer prehliadol !
            s = s.substring(cut);
            for(int i = 0; i < s.length(); i++) {
                 c = s.substring(i,i+1);
                 idx = Arrays.asList(sc).indexOf(c);
                 thestr = thestr + idx;
            }
            return thestr;
        }

        public File getParentDirectory() {
            // login-obrazovka potrebuje parentDirectory pred inicializaciou jadra
            // jadro sa inicializuje po potvrdeni mena uzivatela 
            if (parentDirectory==null) setParentDirectory();
            return parentDirectory;
        }
        public File getSystemDirectory() {
            return sysDirectory;
        }
        
        public String getSysDir() {
//            return this.getSystemDirectory().getAbsolutePath();
            return sysDirectory.getAbsolutePath();
        }
        
        public String getSysDir (String relativeFileName) {
            String sDir = getSysDir();
            String s = relativeFileName;
            
            if (s.startsWith("."))
                s = sDir + get_os_slash() + s.substring(1);
            else 
                s = sDir + get_os_slash() + s;
////                s = s.replace("/", "\\");
            s = s.replace("\\", "/");
            s = s.replace("//", "/");
                
            return s;
        }

        public String getUsrDir() {
            return usrDirectory.getAbsolutePath();
        }
        
        public String getUsrDir (String relativeFileName) {
            String sDir = getUsrDir();
            String s = relativeFileName;
            
            if (s.startsWith("."))
                s = sDir + get_os_slash() + s.substring(1);
            else 
                s = sDir + get_os_slash() + s;

            s = s.replace("\\", "/");
            s = s.replace("//", "/");
                
            return s;
        }

        public String getStaDir() {
            return staDirectory.getAbsolutePath();
        }
        
        public String getStaDir (String relativeFileName) {
            String sDir = getStaDir();
            String s = relativeFileName;
            
            if (s.startsWith("."))
                s = sDir + get_os_slash() + s.substring(1);
            else 
                s = sDir + get_os_slash() + s;

            s = s.replace("\\", "/");
            s = s.replace("//", "/");
                
            return s;
        }

        static private String getComputerName()
        {
            return FnEaS.getComputerName();
        }
        
        public int getVerboseLevel() {
            return iVerboseLevel;
        }

        public void setWorkConnection(String DB_URL, String DBusrname, String pwd) {
            workURL  = DB_URL;
            workUser = DBusrname; 
            workPwd  = pwd;
        }
        
        public String getWorkURL() {
            return workURL;
        }
        
        public String getWorkUsr() {
            return workUser;
        }
        
        public String getWorkPwd() {
            return workPwd;
        }
        
        public Font getDefaultFont() {
//            return Kernel.getDefaultFont();
            return defaultFont;
        }

        public void setDefaultFont(Font f) {
//            return Kernel.getDefaultFont();
            defaultFont = f;
        }
  /*      
        public static Font getDefaultFont() {
            Font f = new Font ("Ariel", Font.PLAIN, 14); 
            return f;
        }
    */    
        public static Integer[] getFontSize(String text, Font font) {
            Integer[] i = new Integer[2];
            AffineTransform affinetransform = new AffineTransform();     
            FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
//            Font font = new Font("Tahoma", Font.PLAIN, 12);
            int textwidth = (int)(font.getStringBounds(text, frc).getWidth());
            int textheight = (int)(font.getStringBounds(text, frc).getHeight());
            i[0] = textwidth;
            i[1] = textheight;
            return i;
        };

    
    public void setWidgetFonts(Container jp, Font f) {
        String s;
        String n;
        Class cl;
        jp.setFont(f);
        Component[] c = jp.getComponents();
        for (Object o : c) {
            cl = o.getClass();
            s = cl.getSimpleName();
            ////System.out.println("setWidgetFonts - Component: " + s + " (" + cl + ")");
            if (o instanceof system.modul.Menu) setWidgetFonts((system.modul.Menu) o, f);
            else if (o instanceof Container) setWidgetFonts((Container) o, f);
            else ((Component) o).setFont(f);
        } // for (Object o : c) {
    }
    
    public void setWidgetFonts(system.modul.Menu jp, Font f) {
        jp.setFont(f);
        int i = jp.getItemCount();
        //System.out.println("setWidgetFonts - Menu: " + jp.getText() + " ItemCount::" + i);
        for (int ii = 0; ii < i; ii++) {
            JMenuItem o = jp.getItem(ii);
            if (o != null) o.setFont(f);
            else {
                MenuItem mo = (MenuItem) jp.getItem(ii);
                if (mo != null) mo.setFont(f);
                else continue;
            }
            ////System.out.println("setWidgetFonts - MenuItem: " + o.getText());
        } // for (Object o : c) {
    }
    
    public void setWidgetFonts(JMenuBar jp, Font f) {
        if (jp==null) return;
        int i = jp.getMenuCount();
        for (int ii = 0; ii < i; ii++) {
            system.modul.Menu o = (system.modul.Menu) jp.getMenu(ii);
            setWidgetFonts(o, f);
            ////System.out.println("setWidgetFonts - JMenu: " + o.getText());
        } // for (Object o : c) {
    }

    public void applyDefaultFont() {
        String sFontDeclaration = getUserProperty("DefaultFont");
        if (sFontDeclaration==null) sFontDeclaration = "Ariel#0#15";
////        System.out.println("$###################### ################# FOOTDECL::: " + sFontDeclaration);
        defaultFont = new Font(FnEaS.sEntry(1, sFontDeclaration, "#"),
                      Integer.parseInt(FnEaS.sEntry(2, sFontDeclaration, "#")),
                      Integer.parseInt(FnEaS.sEntry(3, sFontDeclaration, "#"))
                      );
        setMenuBarWidgetFonts(defaultFont);
        setWidgetFonts( (Container) getDsk(), defaultFont);
    }
    

    public void setMenuBarWidgetFonts(Font f) {
        Iterator itr = EOC_xmodules.iterator();
        while (itr.hasNext()) {
            Object element = itr.next();
            final system.modul.Modul xmdl;
            xmdl = (Modul) element;
            //  nastavenia menu-baru
            final JMenuBar mbr;
            mbr = (JMenuBar) xmdl.getModulMenuMar();
            setWidgetFonts(mbr, f);
            
            final JTabbedPane tbp;
            tbp = (JTabbedPane) xmdl.getoVtp_MDL();
            this.setWidgetFonts((Container) tbp, defaultFont);
            
        }
    }    

    public iEOC_DBtableField krn_get_DTobject(JPanel jp, String tbl, String fld) {

      iEOC_DBtableField retObj = null;
        
      Component[] c = getComponentsInTabOrder(jp);  // widgety JFrame-kontainera

      for (Object o : c) {

        // spracovavaju sa len objekty EOC-architektury
        if (!(o instanceof iEOC_DBtableField)) continue;

        if (  ((iEOC_DBtableField) o).getDBtableField().dbColumnInfo.tablename.equalsIgnoreCase(tbl)
           && ((iEOC_DBtableField) o).getDBtableField().dbColumnInfo.columnname.equalsIgnoreCase(fld)
           ) {
              retObj = (iEOC_DBtableField) o;
              break;
         }
       }    
       return retObj;
    }

    public String krn_get_DTobjectString(JPanel jp, String tbl, String fld) {
        String s = null;
        iEOC_DBtableField DBfld;
        DBfld = krn_get_DTobject(jp, tbl, fld);
        if (DBfld != null) {
            s = DBfld.getDBtableField().getValueForWriteAsString("");
        }
        return s;
    }

    public String krn_get_DTobjectText(JPanel jp, String tbl, String fld) {
        String s = null;
        iEOC_DBtableField DBfld;
        DBfld = krn_get_DTobject(jp, tbl, fld);
        if (DBfld != null) {
            s = ((DTfield) DBfld).getText();
        }
        return s;
    }

    public Boolean krn_setObjAttr(String objType, String objName, String attrName, String attrValue, String attrDataType) {

    String sWhere = "c_objType = '" + objType + "'and " + 
                     "c_objName = '" + objName + "' and c_attrName = '" + attrName + "'";
    
    boolean bExists = SQLQ_Exists(DBcnWork.getConn(), "eas_objattr", sWhere);

    boolean retval;
    
    String sql;
    if (!bExists) {
        sql = "insert into eas_objattr (c_objType,c_objName,c_attrName,c_attrVal,c_datatype) values (" 
            + "'"   + objType + "','" + objName + "','" + attrName + "','" 
            + attrValue + "','" + attrDataType + "')";
        System.out.println(sql);
        retval =  SQLQ_callSqlStatement(DBcnWork.getConn(), sql, true);
    }
    else {
        sql = "update eas_objattr set c_attrVal = '" + attrValue + "' where " + sWhere; 
        retval =  SQLQ_callSqlStatement(DBcnWork.getConn(), sql, true);
    }
    
    return retval;
    
    }
            
    public String krn_getObjAttr(String objType, String objName, String attrName) {
        String retVal;
        String sql = "select c_attrVal from eas_objattr where c_objType = '" + objType + "'and " + 
                     "c_objName = '" + objName + "' and c_attrName = '" + attrName + "'";
        
        Object o = SQLQ_getQueryValue(DBcnWork.getConn(),sql, "c_attrVal");
        
        retVal = o.toString();
        
        return retVal;
    }

}
