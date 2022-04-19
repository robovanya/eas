/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package system;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.io.StreamTokenizer;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
//import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Zakladne/atomicke funkcie sytemu EaSys
 * @author rvanya
 */
public class FnEaS {
    
    /* vrati pocet clankov v retazci */
    public static int iNumEntries(String sStr, String sDelimiter) {
        if (sStr == null) { return 0; }
        if (sStr.isEmpty()) { return 0; }
        // 2015-11-05 return sStr.length() - sStr.replace(sDelimiter, "").length() + 1;
        ////System.out.println(sDelimiter + "---sDelimiter.length()===" + sDelimiter.length() + " in string:" + sStr);
        return ((sStr.length() - sStr.replace(sDelimiter, "").length()) 
                 / sDelimiter.length()) + 1;
    }
    
    /* ####################################################################
     * vrati SKUTOCNU!!! poziciu hladanej hodnoty v retazci */
    public static int iLookup(String cFind, String sStr, String sDelimiter) {
       StringTokenizer st = new StringTokenizer(sStr,sDelimiter);
       String sHodnota;
       for (int i = 0; i < iNumEntries(sStr,sDelimiter); i++) {
           sHodnota = st.nextToken();
           if (sHodnota.equals(cFind)) return i + 1;
       }
       return 0;
    }
    
    /* ####################################################################
     * Vrati hodnotu iPozicia-teho clena v retazci */
    public static String sEntry_NEW(int iPozicia,String sStr, String sDelimiter) {
       String sHodnota = ""; // diskutabilne, ale NULL nechcem pouzit
       String curChar;
       int delLength = sDelimiter.length();
       String firstDeliChar = sDelimiter.substring(0,1);
       String curDelExt = "";
       int iEntr = 0;
       ent:
       for (int i = 0; i < sStr.length(); i++) {
           curChar =  sStr.substring(i, i + 1);
           if (curChar.equals(firstDeliChar) && (curDelExt.length() < delLength)) {
              if (curDelExt.equals("")) iEntr++;
              if (iEntr == iPozicia) break ent; // QQQ diskutabilne, hodnoty by 
                                                // nemali obsahovat znak z delimiteru
              else { 
                  curDelExt = curDelExt + curChar;
                  sHodnota = "";
             }
           } else {
               if (curDelExt.length() == delLength) {
                   curDelExt = "";
               }
                   sHodnota = sHodnota + curChar;
           }
       } // ent: for (int i =  ...
       return sHodnota;
    }
    
    public static String sEntry(int iPozicia,String sStr, String sDelimiter) {
       String sHodnota = ""; // diskutabilne, ale NULL nechcem pouzit
       String curChar;
       int delLength = sDelimiter.length();
       if (delLength > 1 ) {
           //pokus o skratenie delimitera
           if (!sStr.contains("ţ")) {
           // ţ alt+238
               sStr = sStr.replaceAll(sDelimiter, "ţ");
               sDelimiter = "ţ";
           }
       }
       int iEntr = 0;
       ent:
       for (int i = 0; i < sStr.length(); i++) {
           curChar =  sStr.substring(i, i + 1);
           //krn.OutPrintln("sEntry-curChar=" + curChar);
           if (curChar.equals(sDelimiter)) {
              iEntr++;
//              krn.OutPrintln("sEntry-curChar=" + curChar + "  iEntr=" + iEntr + "=" + iPozicia + "_>" + (iEntr == iPozicia));
              if (iEntr == iPozicia) { 
                 //krn.OutPrintln("sEntry-returning=" + sHodnota);
                 break ent; }
              else {sHodnota = "";}
           } else { sHodnota = sHodnota + curChar; }
       } // ent: for (int i =  ...
       return sHodnota;
    }
    
    public static String sEntry_OLD(int iPozicia,String sStr, String sDelimiter) {
       String sHodnota = ""; // diskutabilne, ale NULL nechcem pouzit
       String curChar;
       int iEntr = 0;
       ent:
       for (int i = 0; i < sStr.length(); i++) {
           curChar =  sStr.substring(i, i + 1);
           //krn.OutPrintln("sEntry-curChar=" + curChar);
           if (curChar.equals(sDelimiter)) {
              iEntr++;
//              krn.OutPrintln("sEntry-curChar=" + curChar + "  iEntr=" + iEntr + "=" + iPozicia + "_>" + (iEntr == iPozicia));
              if (iEntr == iPozicia) { 
                 //krn.OutPrintln("sEntry-returning=" + sHodnota);
                 break ent; }
              else {sHodnota = "";}
           } else { sHodnota = sHodnota + curChar; }
       } // ent: for (int i =  ...
       return sHodnota;
    }
    
    /* ####################################################################
     * Vrati poziciu sEntry v retazci sStr */
    public static int iEntryIdx(String sEntry, String sStr, String sDelimiter) {
       int delLength = sDelimiter.length();
       if (delLength > 1 ) {
           //pokus o skratenie delimitera
           if (!sStr.contains("ţ")) {
           // ţ alt+238
               sStr = sStr.replaceAll(sDelimiter, "ţ");
               sDelimiter = "ţ";
           }
       }
       int iEntry = 0; // diskutabilne, ale NULL nechcem pouzit
       String curChar;
       String curEntry = ""; // zacina sa prazdnou hodnotou
       ent:
       for (int i = 0; i < sStr.length(); i++) {
           curChar =  sStr.substring(i, i + 1);
           if (curChar.equals(sDelimiter)) {
              //curEntry = curEntry + curChar;
              // EaS_krn.Msg(iEntry+ ": " + curEntry + " IS=" + curEntry.equals(sEntry) + " " + sEntry);
              iEntry++;
              if (curEntry.equals(sEntry)) { break ent; }
              else { curEntry = ""; }
           } 
           else { curEntry = curEntry + curChar; }
       } // ent: for (int i =  ...
       return iEntry;
    }
    
    /* ####################################################################
     * Vymeni hodnotu iPozicia-teho clena v retazci sStr za retazec sNewEntry */
    public static String sReplaceEntry(int iPozicia,String sStr, 
                                       String sNewEntry, String sDelimiter) {
        
       String sAktEntry = ""; // diskutabilne, ale NULL nechcem pouzit
       String sNewStr   = ""; // diskutabilne, ale NULL nechcem pouzit
       String curChar;
       boolean bReplaced = false;
       int iEntr = 1; // entry-counter
       ent:
       for (int i = 0; i < sStr.length(); i++) {

           curChar =  sStr.substring(i, i + 1);
           // zacina sa dalsi Entry
           if (curChar.equals(sDelimiter)) {
              if (iEntr == iPozicia) { 
                  sNewStr = sNewStr + sNewEntry;
                  bReplaced = true;
              }
              else {
                  sNewStr = sNewStr + sAktEntry;
              }
              sNewStr = sNewStr + sDelimiter;
              sAktEntry = "";
              iEntr++;
           } // if (curChar.equals(sDelimiter))
           else { sAktEntry = sAktEntry + curChar; }
       } // ent: for (int i = ...
       if (bReplaced)
          sNewStr = sNewStr + sAktEntry; // posledny Entry
       else
          sNewStr = sNewStr + sNewEntry; 
      // EaS_krn.Msg( iPozicia + "\n" + sStr + "\n" + sNewEntry + "\n" + sNewStr);
       return sNewStr;
    }
    

    /* ####################################################################
     * konvertuje datumovy retazec z yyyy-MM-dd na dd.MM.yyyy */
    public static String sDBdateToSCdate(String sDBdate) {
        String sSCstr;
        if (sDBdate.isEmpty()||sDBdate==null)  { sDBdate=""; }
        if (iNumEntries(sDBdate,"-")==3) {
            sSCstr = sEntry(3,sDBdate,"-") + "." 
                   + sEntry(2,sDBdate,"-") + "." 
                   + sEntry(1,sDBdate,"-");
        }
        else { sSCstr = "  .  .    "; }
        return sSCstr;
    }
    
    /* ####################################################################
     * konvertuje datumovy retazec z dd.MM.yyyy na yyyy-MM-dd */
    public static String sSCdateToDBdate(String sSCdate) {
        String sDBstr;
        if (sSCdate.isEmpty()||sSCdate==null)  { sSCdate=""; }
        if (iNumEntries(sSCdate,".")==3) {
            sDBstr = sEntry(3,sSCdate,".") + "-" 
                   + sEntry(2,sSCdate,".") + "-" 
                   + sEntry(1,sSCdate,".");
        }
        else { sDBstr = "null" /*"   -  -    "*/; }
        if (sDBstr.equals("    -  -  ")) {sDBstr = "null";}
        return sDBstr;
    }
    
    public static String sObjName(Object o) {
        String anm = "";
        String s = "NONAME";
        if (o instanceof Component) {
            anm = ((Component) o).getAccessibleContext().getAccessibleName();
            if (anm == null) anm = "";
        }
        if (anm.equals("")) {
            s = sEntry(1,o.toString(),"[");
            s = s.substring(s.lastIndexOf(".") + 1);
            s = "NONAME_" + s;
        }
        else s = anm;
        return s;
    }
    
    public static String sArrayToString(String[] sArr) {
        String str = "";
        int i;
        for (i = 0; i < sArr.length; i++) {
           str = str + ',' + sArr[i];
        }
        if (str.length() > 0) {
            str = str.substring(1);
        }
        return str;
    }
    
    public static String repeat(String st,  int rpts) {
        String str = "";
        for(int j = 0; j < rpts; j++) {
              str = str + st;
        }
        return str;
    }

    public static String repeat(char c,int rpts) {
        String str = "";
        for(int j = 0; j < rpts; j++) {
              str = str + c;
        }
        return str;
    }

    public static String getCallerMethodName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];
        return e.getMethodName();
    }
    
    public static String getCallerMethodName(int iStack) {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3 + iStack];
        return e.getMethodName();
    }
    /*
    public static Method getEOCMethod (Object oWhere, String sMtd, Object sParam) {
        Method mtd = null;
        if (sParam==null) { 
            try {
                mtd = oWhere.getClass().getMethod(sMtd);
            } catch (NoSuchMethodException ex) {
                mtd = null;
            } catch (SecurityException ex) {
                Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        else { 
            try { 
                mtd = oWhere.getClass().getMethod(sMtd, new Class[] {String.class});
            } catch (NoSuchMethodException ex) {
                mtd = null;
            } catch (SecurityException ex) {
                Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (mtd == null) {
            if (sParam==null) { 
                try {
                    mtd = oWhere.getClass().getDeclaredMethod(sMtd);
                } catch (NoSuchMethodException ex) {
                     mtd = null;
                } catch (SecurityException ex) {
                    Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                try {
                    mtd = oWhere.getClass().getDeclaredMethod(sMtd, new Class[] {String.class});
                } catch (NoSuchMethodException ex) {
                   mtd = null;
                } catch (SecurityException ex) {
                    Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (mtd==null) {
        }
        return mtd;
    }
    */
    public static Method getEocMessageMethod (Object oWhere, eoc.EOC_message eocMsg) {
        Method mtd = null;

        // hladanie metody s parametrom triedy EOC_message.CLASS
        Class[] clss = {eocMsg.getClass()};
        try { 
            mtd = oWhere.getClass().getMethod(eocMsg.getMessage(), clss);
        } catch (NoSuchMethodException ex) {
            mtd = null;
        } catch (SecurityException ex) {
            Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        if (mtd != null) return mtd;
        try {
            mtd = oWhere.getClass().getDeclaredMethod(eocMsg.getMessage(), clss);
        } catch (NoSuchMethodException ex) {
           mtd = null;
        } catch (SecurityException ex) {
            Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        // hladanie metody s parametrom triedy EOC_message.CLASS
        clss[0] = String.class;
        
        if (mtd == null) {
            try {
                mtd = oWhere.getClass().getDeclaredMethod(eocMsg.getMessage(), clss);
            } catch (NoSuchMethodException ex) {
               mtd = null;
            } catch (SecurityException ex) {
                Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        */
        return mtd;
    }
    
    public static Method getEOCMethod (Object oWhere, String sMtd, Class[] clss) {
        if (clss==null) {
            System.out.println("FnEaS.getEOCMethod(): NULL-CLASS-ARRAY! for method: " 
                    + sMtd + " OBJECTT:" + oWhere.toString());
            return null;
        }
        Method mtd = null;
        ////if (clss[0] != null)
        ////System.out.println("getEOCMethod_oWhere1==" + sMtd + " classs:" + clss[0].getSimpleName()
        ////        + " INOBJECCT:" + FnEaS.sObjName(oWhere));
       //// else
        ////System.out.println("getEOCMethod_oWhere2==" + sMtd + " classs: <NULL>"
        ////        + " INOBJECCT:" + FnEaS.sObjName(oWhere));
        if (clss[0]==null) { 
            try {
                mtd = oWhere.getClass().getMethod(sMtd);
            } catch (NoSuchMethodException ex) {
                mtd = null;
            } catch (SecurityException ex) {
                Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        else { 
            try { 
                mtd = oWhere.getClass().getMethod(sMtd, clss);
            } catch (NoSuchMethodException ex) {
                mtd = null;
            } catch (SecurityException ex) {
                Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (mtd == null) {
            if (clss[0]==null) { 
                try {
                    mtd = oWhere.getClass().getDeclaredMethod(sMtd);
                } catch (NoSuchMethodException ex) {
                     mtd = null;
                } catch (SecurityException ex) {
                    Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                try {
                    mtd = oWhere.getClass().getDeclaredMethod(sMtd, clss);
                } catch (NoSuchMethodException ex) {
                   mtd = null;
                } catch (SecurityException ex) {
                    Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (mtd==null) {
        }
      ////  System.out.println("getEOCMethod_oWhere finding: " + sMtd + " :== " +  (mtd != null)
      ////      + " CLSS_LENGTH== " + clss.length);
        return mtd;
    }
    /*
    */
    
    /*
    public static Method getEocMessageMethod (Object oWhere, String sMtd,
        String sParam1, String sParam2, String sParam3) {
        Method mtd = null;
        Class[] cls = new Class[] {Object.class, String.class,String.class,String.class};
        try { 
            mtd = oWhere.getClass().getMethod(sMtd, cls);
        } catch (NoSuchMethodException ex) {
            mtd = null;
        } catch (SecurityException ex) {
            Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (mtd == null) {
                try {
                    mtd = oWhere.getClass().getDeclaredMethod(sMtd, cls);
                } catch (NoSuchMethodException ex) {
                    mtd = null;
                } catch (SecurityException ex) {
                    Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        //krn.OutPrintln("getEOCMetod33: " + sMtd + " = " + mtd.toString());
        return mtd;
    }
    */
/*    
    public static Method getLocalReceiveMessageMethod (Object oWhere) 
        throws NoSuchMethodException {
        Method mtd = getEOCMethod(oWhere,"local_receiveMessage","","","");
        return mtd;
   }
   */ 
    public static String currDate(String fmt) {
         DateFormat dateFormat = new SimpleDateFormat(fmt);
         Calendar cal = Calendar.getInstance();
         return dateFormat.format(cal.getTime());
    }
    
    public static String currDate() {
      return currDate("yyyy/MM/dd");    
    }    
    
    public static String currTime(String fmt) {
         DateFormat dateFormat = new SimpleDateFormat(fmt);
         Calendar cal = Calendar.getInstance();
         return dateFormat.format(cal.getTime());
    }

    public static String currTime() {
      return currTime("HH:mm");    
    }    
    
    public static Integer currDatePart(String partName) {
         //// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        partName = partName.toUpperCase(); // pre istotu
         Integer val = null;
         Calendar cal = Calendar.getInstance();
         switch(partName) {
             case "D":
             case "DAY":
             case "DAY_OF_MONTH": 
                 val = cal.get(Calendar.DAY_OF_MONTH); break;
             case "H":
             case "HOUR":
             case "HOUR_OF_DAY": 
                 val = cal.get(Calendar.HOUR_OF_DAY); break;
             case "m":
             case "MIN":
             case "MINUTE": 
                 val = cal.get(Calendar.MINUTE); break;
             case "S":
             case "SEC":
             case "SECOND": 
                 val = cal.get(Calendar.SECOND); break;
             case "M":
             case "MONTH":
                 val = cal.get(Calendar.MONTH) + 1; break; // januar = 0 !!!
             case "Y":
             case "YEAR":
                 val = cal.get(Calendar.YEAR); break;
         }
         return val;
    }
    
    public static Integer[] getTimeDataAt6() {
        Integer retVal[] = new Integer[6];
        Calendar cal = Calendar.getInstance();
        //Y(ear),M(onth),D(ay),H(our),m(inute),s(econd)
        retVal[0] = cal.get(Calendar.YEAR);
        retVal[1] = cal.get(Calendar.MONTH);
        retVal[2] = cal.get(Calendar.DAY_OF_MONTH);
        retVal[3] = cal.get(Calendar.HOUR_OF_DAY);
        retVal[4] = cal.get(Calendar.MINUTE);
        retVal[5] = cal.get(Calendar.SECOND);
        
        return retVal;
    }
    
    public static String currDateTime() {
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         Calendar cal = Calendar.getInstance();
         return dateFormat.format(cal.getTime());
    }
    
    public static String datedString(String str) {
        return currDateTime() + " - " + str;
    }
    
    public static Object[] joinArrays(Object[] aA, Object[] aB) {
        int aLen = aA.length;
        int bLen = aB.length;
        Object[] aC = new Object [aLen + bLen];
        System.arraycopy(aA, 0, aC, 0, aLen);
        System.arraycopy(aB, 0, aC, aLen, bLen);
        return aC;
    }

    public static Component[] joinArrays(Component[] aA, Component[] aB) {
        int aLen = aA.length;
        int bLen = aB.length;
        Component[] aC = new Component [aLen + bLen];
        System.arraycopy(aA, 0, aC, 0, aLen);
        System.arraycopy(aB, 0, aC, aLen, bLen);
        return aC;
    }

    public static Object[][] joinArrays(Object[][] aA, Object[][] aB) {
        int aLen = aA.length;
        int bLen = aB.length;
        System.out.println("alength==" + aA.length + "  blength==" + aB.length);
        int aaLen = aA[0].length;
        int bbLen = aB[0].length;

        Object[][] aC = new Object [aLen][aaLen + bbLen];
        for (int i = 0; i<aLen; i++) {
            Object[] jndRow = joinArrays(aA[i], aB[i]);
            for (int j = 0; j < jndRow.length; j++) {
                aC[i][j] = jndRow[j];
            }
        }
        return aC;
    }
    
    public static ArrayList joinArrayLists(ArrayList<Object> aA, ArrayList<Object> aB) {
        int aLen = aA.size();
        int bLen = aB.size();
        ArrayList<Object> aC  = new ArrayList<>();
        for (int j = 0; j < aA.size(); j++) {
             aC.add(aA.get(j));
        }
        for (int j = 0; j < aB.size(); j++) {
             aC.add(aB.get(j));
        }
        return aC;
    }

    public static ArrayList joinArrayListsAL(ArrayList<ArrayList<Object>> aA, 
                                             ArrayList<ArrayList<Object>> aB) {
        int aLen = aA.size();
        int bLen = aB.size();
        // POZOR! aLen musi myt rovnaky ako bLen !!! Sem by patrila ajhlaska
        // alebo pridat parameter, aby sa kratsia strana doplnila null-hodnotami
        int aaLen = aA.get(0).size();
        int bbLen = aB.get(0).size();
        ArrayList<Object> aAo; 
        ArrayList<Object> aBo; 
        ArrayList<ArrayList<Object>> aC  = new ArrayList<>();
        
        for (int i = 0; i < aLen; i++) {
            aAo = aA.get(i);
            aBo = aB.get(i);
            ArrayList<Object> aCo = new ArrayList<>();
            //aCo.clear(); // mazanie predoslych hodnot
            
            for (int j = 0; j < aAo.size(); j++) {
                aCo.add(aAo.get(j));
            }
            for (int j = 0; j < aBo.size(); j++) {
                aCo.add(aBo.get(j));
            }
            aC.add(aCo);
        }
        return aC;
    }
/*   
    public static String[] sStringToArray(String str, String dlm, int iLow, int iHigh) {
        krn.OutPrintln(11);
        String[] sta;
        int numEntr = FnEaS.iNumEntries(str, dlm);
        if (numEntr  < iHigh) {
            iHigh = numEntr;  // fault-tolerancy
        }
        krn.OutPrintln(iHigh + " >> " + str + " delim:" + dlm);
        int idx = 0;
        for (int i = iLow; i<=iHigh;i++) {
           sta[idx] =  FnEaS.sEntry(i,str,dlm);
           idx++;
        }
        return sta;
    }
*/
    
public static void packColumn(JTable table, int vColIndex, int margin) {
    DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
    TableColumn col = colModel.getColumn(vColIndex);
    int width = 0;

    // Get width of column header
    TableCellRenderer renderer = col.getHeaderRenderer();
    if (renderer == null) {
        renderer = table.getTableHeader().getDefaultRenderer();
    }
    java.awt.Component comp = renderer.getTableCellRendererComponent(
        table, col.getHeaderValue(), false, false, 0, 0);
    width = comp.getPreferredSize().width;

    // Get maximum width of column data
    for (int r=0; r<table.getRowCount(); r++) {
        renderer = table.getCellRenderer(r, vColIndex);
        comp = renderer.getTableCellRendererComponent(
            table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
        width = Math.max(width, comp.getPreferredSize().width);
    }

    // Add margin
    width += 2*margin;

    // Set the width
    col.setPreferredWidth(width);
}    
public static void applyEvent(/*Component eSrc,*/Component eTrg, String sEvt) {
     ////EaS_krn.Msg("applyEvent() >> AKCIÓ VAN, BAZMEG !!! -> " + eTrg.getName() + " => " + sEvt);
      ActionEvent ae = 
          new ActionEvent(eTrg, ActionEvent.ACTION_PERFORMED, sEvt);
      //ae.setSource(eTrg);
       //Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(ae);
  //    krn.krnMsg("DispA " + ae.getSource().toString() + " --> " + ae.getActionCommand()
  //    + "\n\nEVENT:\n" + ae.toString());
for(ActionListener a: eTrg.getListeners(ActionListener.class)) {
    a.actionPerformed(ae);
}
  
        //processEvent(ae);  //can be used too.
        eTrg.dispatchEvent(ae );
      //krn.krnMsg("DispB -> isSelected()==" + jCHB_TCPconnection.isSelected());
       // eTrg.revalidate();
      //krn.krnMsg("DispC");
    }
  public void setUserAttribute(String attrName, String attrValue) {
      
  }
  
  public static Integer[] intToIntegerArray(int[] intA) {
      Integer[] newArray = new Integer[intA.length];
      int i = 0;
      for (int value : intA) {
          newArray[i++] = Integer.valueOf(value);
      }
      return newArray;
  }

      public static String calToStr(Calendar cal, String fmt) {
        if (cal == null) return null;
        java.util.Date dat = cal.getTime();
        if (fmt == null) fmt = "yyyy-MM-dd"; // default = SQL standard
        return new SimpleDateFormat(fmt).format(dat);
    }

public static Calendar DateToCalendar(Date date){ 
  Calendar cal = Calendar.getInstance();
  cal.setTime(date);
  return cal;
}  

public static int getDiffDays(Calendar startD, Calendar endD) {
Calendar start = (Calendar) startD.clone();    
Calendar end   = (Calendar) endD.clone();    
//start.add(Calendar.DAY_OF_MONTH, (int)diffDays);
int diffDays = 0;    
while (start.before(end)) {
    start.add(Calendar.DAY_OF_MONTH, 1);
    diffDays++;
}
while (start.after(end)) {
    start.add(Calendar.DAY_OF_MONTH, -1);
    diffDays--;
}    
return diffDays;
}

public static Calendar setTimeToNull(Calendar cl){ 
    Calendar cal = (Calendar) cl.clone();
  cal.set(Calendar.HOUR_OF_DAY, 0);
  cal.set(Calendar.MINUTE, 0);
  cal.set(Calendar.SECOND, 0);
  cal.set(Calendar.MILLISECOND, 0);
  return cal;
}  

public static String getMonthName (int mnth, int lngth) {
    String Months[] = {"Január","Február","Marec","Apríl","Máj","Jún","Júl","August","September","Október","November","December"};
    String retv = Months[mnth];
    if (retv.length() > lngth) retv = retv.substring(0, lngth);
    return retv;
}

public static String getComputerName()
{
     Map<String, String> env = System.getenv();
     String compName = "";
//env.forEach((key, value) -> {
/* list env
System.out.println("env-KEYYYSSSS:");
for(Map.Entry<String,String> entry:env.entrySet()){
    System.out.println("account: " + entry.getKey() + ", password: " + entry.getValue());
} 
*/
    try {
        // execReadToString("cat /etc/hostname")
        if (env.containsKey("COMPUTERNAME"))
            compName = env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            compName = env.get("HOSTNAME");
        else {
            compName = InetAddress.getLocalHost().getHostName();
            compName = InetAddress.getLocalHost().getCanonicalHostName();
        }
        } catch (UnknownHostException ex) {
            Logger.getLogger(FnEaS.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (compName.equals("")) { compName = "Unknown Computer"; };
        
        return compName;
}

public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();
    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;

}

public static String doubleToStr(double value, int places) {
    String fmt;
    if (places < 0) fmt = "#.00";
    else if (places == 0) fmt = "#";
    else {
     fmt = "#.";
     for (int i = 1; i <= places; i++) {
         fmt = fmt + "0";
     }
    }
    System.out.println(">>>>doubleToStr>>>" + fmt);
    DecimalFormat dFmt = new DecimalFormat(fmt);
    return dFmt.format(value);
}

public static String intToStr(Integer value, int zeroes) {

    String fmt = "%0" + zeroes + "d";

    return String.format(fmt, value);
}

public static String getTblOwner(String owtb) {
        String s[] = owtb.split("\\.");
        return (s.length > 1?s[0]:"");
}

public static String getTblName(String owtb) {
        String s[] = owtb.split("\\.");
        return (s.length > 1?s[1]:s[0]);
}

// crazy-method par excellence
// Ivanyuskina kokotina !
// BUDE TREBA VYTVORIT MOZNO AJ getBiggerDateTime !!!
public static int getBiggerDate(Object a, Object b) {
    
    int vysledok = 0; // 0 = neviem (PRUSER!), -1 = prvy je NULL, -2 = druhy je NULL
                      // 1 = prvy je vatsi, 2 = druhy je vatsi
    
    return vysledok;

}

// crazy-method number 1 - par excellence
// Ivanyuskina kokotina !
// BUDE TREBA VYTVORIT MOZNO AJ getBiggerDateTime !!!
public static Calendar getObjectAsCalendar(Object o) {
    Calendar cal = null;
    
    if (o instanceof Calendar) return ((Calendar) o);
    
    if (o instanceof String) return getStringAsCalendar ((String) o, null);
    
    return cal;
    
}

// crazy-method number 2 - par excellence
// Ivanyuskina kokotina !
public static Calendar getStringAsCalendar(String o, String cFmtType) {
    System.out.println("getStringAsCalendar(...)-PREPARINGG:" + o);
    if (o == null) return null;
    
    if (cFmtType == null) cFmtType = "";

    o = o.trim(); // mazanie pripadnych krajnych medzier
    
    // cFmtType musi byt YMD alebo DMY,ine sa nespracovava !!!
    cFmtType = cFmtType.toUpperCase();
    if ((!cFmtType.equals("DMY")) && (!cFmtType.equals("YMD"))) cFmtType = "";
    
    char[] array = o.toCharArray();
    
    String delimiter = ""; // delimiter datumu
    String pureValue = ""; // string bez delimiterov
//    String[] pureValues = {"","","","","","",""};
    Integer[] pureValues = {0,0,0,0,0,0,0};
    int delimCounter = 0;
    
    for (char ch : array) {

        if (Character.isDigit(ch)) {
            pureValue += ch;
        }

       //if (Character.isLetter(ch)) {
           else {   
           if (delimiter.equals("")) {
               delimiter = String.valueOf(ch);
               if (cFmtType.equals("")) {
                   cFmtType = (pureValue.length() > 2 ? "YMD" : "DMY") ;
               }
           } // if (delimiter.equals(""))
           pureValues[delimCounter] = Integer.parseInt(pureValue);
           pureValue = "";
           delimCounter ++;
       } // else { // if (Character.isLetter(ch))
    } // for (char ch : array)
    pureValues[delimCounter] = Integer.parseInt(pureValue);
    pureValue = "";
    
    ////System.out.println( cFmtType + " PUREVALUESS: " + Arrays.deepToString(pureValues));

    Calendar cal = Calendar.getInstance();
    /////System.out.println("PUREVALUESS_CAL!!11:" + calToStr(cal,"yyyy-MM-dd hh:mm:ss"));
    //cal.setLenient(false);
    /////System.out.println("PUREVALUESS_CAL!!22:" + calToStr(cal,"yyyy-MM-dd hh:mm:ss"));
    if (cFmtType.equals("YMD")) {
       ////cal.set
       cal.set(pureValues[0], pureValues[1] -1, pureValues[2], pureValues[3], pureValues[4], pureValues[5]);
    }
    else {    
    ////System.out.println( cFmtType + " PUREVALUESS BNEFF: " + Arrays.deepToString(pureValues));
       cal.set(pureValues[2], pureValues[1] -1, pureValues[0], pureValues[3], pureValues[4], pureValues[5]);
    /////System.out.println( cFmtType + " PUREVALUESS BAFTT: " + Arrays.deepToString(pureValues));
    }
    
    /////System.out.println("PUREVALUESS_RTURNING:" + calToStr(cal,"yyyy-MM-dd hh:mm:ss"));
       cal.clear(Calendar.MILLISECOND);
    return cal;
    
}

// crazy-method number 2 - par excellence
// Ivanyuskina kokotina !
public static Calendar getLongAsCalendar(Long o) {

    if (o == null) return null;

    Calendar cal = null;
        
    
    return cal;
    
}

public static String nullEocMessageResponse(Object oNotifier) {
    return "NULL-EocMessage in: " + oNotifier.getClass().getSimpleName(); 
}

public static String sFullObjName(Object o) {
    String s = o.toString();
    String[] ss = s.split("@");
    return ss[0];
}

public static String sFullObjPath(Object o) {
    return sFullObjName(o).replace(".","/");
}

public static String crazyDateRead(String s, String delim) {
    String dt = "";
    String y = "";
    String M = "";
    String d = "";
    String[] ds = s.split(",");
    for (String x: ds) {
     ////   System.out.println("DIRTY:" + x);
        if (x.startsWith("YEAR=")) y = sEntry(2,x,"=");
        if (x.startsWith("MONTH=")) {
            Integer i = Integer.parseInt(sEntry(2,x,"="))+ 1;
            M = i.toString();
        }
        if (x.startsWith("DAY_OF_MONTH=")) d = sEntry(2,x,"=");
    }
    dt = d + delim + M + delim + y;
    
    // prazdny datum
    if (dt.equals(delim + delim)) 
        dt = "  " + delim + "  " + delim + "    "; 
    return dt;
}

public static boolean isNumeric(String str)
{
    for (char c : str.toCharArray())
    {
        if (!Character.isDigit(c)) return false;
    }
    return true;
}
} //public class FnEaS {

    