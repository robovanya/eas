/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.dbdata;

import eoc.database.DBconnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.FnEaS;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class DBtable_buffer /* implements Serializable */ {
    private Kernel krn;
    private DBconnection MyCn;
    private HashMap <String,Object> tbMap;
    private HashMap <String,Object> tbMapOld;
    private String myTblOwner = new String();
    private String myTblName = new String();
    private String myTblIDname = new String();
    private Integer myTblIDvalue = null;
//    private boolean bUpdated = false;
//    private DBtable_buffer oldBuffer = null;
//    private Boolean bOldBuffer = null;
    private Boolean bReadOnly  = null;
    // hlavny konstruktor 
    public DBtable_buffer(Kernel kr, DBconnection cn, String tblName, String IDname, Boolean rdOnly) {
        krn = kr;
        MyCn = cn;
  //      bOldBuffer = oldBfr;
        bReadOnly = rdOnly;
        initForDbTable(tblName, IDname);
        // ked to nie je oldBuffer, tak sa vytvori 
    //    if (!oldBfr) {
//            oldBuffer = new DBtable_buffer(kr,cn,tblName, IDname, true, true);
//        }
        //System.out.println("DBtable_bufferCreatedFields: " + Arrays.deepToString(tbMap.keySet().toArray()));
    }
    
     // kopirovaci konstruktor
    public DBtable_buffer(DBtable_buffer dbfr, Boolean rdOnly) {
        this.krn = dbfr.krn;
        this.MyCn = dbfr.MyCn;
        this.myTblOwner = dbfr.myTblOwner;
        this.myTblName = dbfr.myTblName;
        this.myTblIDname = dbfr.myTblIDname;
        this.myTblIDvalue = dbfr.myTblIDvalue;
//        this.bOldBuffer = dbfr.bOldBuffer;
        this.bReadOnly = dbfr.bReadOnly;
//        ked to ma kopirovat tak ma kopirovat aj hashhMap kua !
        this.tbMap = new HashMap<>(dbfr.tbMap);
        this.tbMapOld = new HashMap<>(dbfr.tbMapOld);
//        if (!bOldBuffer && (rdOnly != null)) bReadOnly = rdOnly;
//        dbfr.listEntries("MIVANAORIGBAN:", true);
//        this.listEntries("MIVANAKOPYBAN:", true);
    }
    
    public boolean isReadOnly() {
        return bReadOnly;
    }
/*
    public boolean isOldBuffer() {
        return bOldBuffer;
    }
*/
    public final boolean initForDbTable (String tblName, String IDname) {
        // krn.debugOut(this,6,"DBtable_buffer.initForDbTable(): " + tblName + " IDname:" + IDname);
        myTblOwner = FnEaS.getTblOwner(tblName);
        myTblName  = FnEaS.getTblName(tblName);
        myTblIDname = IDname;
        int numColumns = MyCn.getDbDriver().SQL_getNumDbColumns(MyCn.getConn(), tblName);
 //       System.out.println("####>> !!!!!!!!!!!!!!!!!!  DBtable_buffer.initForDbTable(): " + tblName 
 //               + " with ID:" + IDname + " numColumnns:" + numColumns 
 //               + " isReadOnly:" + this.isReadOnly());
        /*
        */
        tbMap    = new HashMap<>(numColumns);
        tbMapOld = new HashMap<>(numColumns);
        boolean b = MyCn.getDbDriver().SQL_getDbTableStructToHashMap(MyCn.getConn(), tblName, tbMap);
//        oldBuffer = new DBtable_buffer(this, true);
        //dataz = new HashMap<Integer,Object>(screen_dataz);        
        //newMap.putAll(myMap);
        tbMapOld.clear();
        tbMapOld.putAll(tbMap);
        return b;
    }
    
    public String getFullTblName() {
        String s = "";
        if (!myTblOwner.equals("")) s = s + myTblOwner + ".";
        s = s + myTblName;
        return s;
    }
    public boolean getDataForID(Integer idVal) {
        String sQry = "select * from " + getFullTblName() + " where " + myTblIDname 
                   + " = " + idVal;
      System.out.println("####>>>> DBtable_buffer.getDataForID() -> " + sQry);
      boolean b = MyCn.getDbDriver().SQL_getSqlRowToHashMap(MyCn.getConn(), tbMap, sQry);
////        System.out.println("####>>>> getDataForID() -> " + getFullTblName() + " >> ID:" + idVal + " success:" + b);
        if (b) myTblIDvalue = idVal;
        else myTblIDvalue = null;
//        bUpdated = false;
        /*
        try {
            oldBuffer = (DBtable_buffer) (this.clone());
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DBtable_buffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        //bufferCopy(oldBuffer);
        tbMapOld.clear();
        tbMapOld.putAll(tbMap);
        return b;
    }  
    
    public Object getValue(String colName) {
        Object o;
        o = tbMap.get(colName.trim());
        if (o == null) o = "";
        return o;
    }
    
    public Object getValueOld (String colName) {
        Object o;
        o = tbMapOld.get(colName);
        if (o == null) o = "";
        return o;
    }
    
    public boolean getBoolean(String colName) {
        String o;
        // System.out.println("getBoolean:" + colName);
        if (tbMap.containsKey(colName))
             o = tbMap.get(colName).toString();
        else o = "0";
        return (o.equals("1")?true:false);
    }

    public Integer getInteger(String colName) {
        String o;
        //QQQ sem by patril test, ci sa objekt da konvertovat na integer
        if (tbMap.containsKey(colName))
             o = tbMap.get(colName).toString();
        else o = "0";
        Integer i = Integer.parseInt(o.toString());
        return i;
    }

     public Integer getCalendar(String colName) {
        String o;
        //QQQ sem by patril test, ci sa objekt da konvertovat na integer
        if (tbMap.containsKey(colName))
             o = tbMap.get(colName).toString();
        else o = "0";
        Integer i = Integer.parseInt(o.toString());
        return i;
    }

   public void setValue(String colName, Object value) {
//        if (colName.trim().equals("id_owner_tbl")) 
//             System.out.println("SETBUFFID: " + colName + "==" + value.toString());
       colName = colName.trim();
        if (!colName.startsWith("@")) {
        if (!tbMap.containsKey(colName)) {
            String vypis = "Výpis štruktúry pre buffer " + getFullTblName() + ":\n";
            Set<String> k = tbMap.keySet();
            for (String key: k) {
                System.out.println("GeTTINGKY:" + key);
                Class c = null;
                Object o = tbMap.get(key);
                if (o != null) c = o.getClass();
                String cls = (c==null?"NULL":c.getName());
                vypis = vypis + key + " " + cls + "\n"; 

            }
            
            Kernel.staticMsg("Buffer tabulky " + getFullTblName() + " neobsahuje hodnotu s nazvom " + colName 
                     + "\n Zadajte názov premennej s prefixom @, a bude pri generovaní zápisov do databázy ignorovaný.\n\n"
                     + vypis);
            return;
        }
            Object oldVal = tbMap.get(colName);
//            if (oldVal==null && value!=null) bUpdated = true;
//            else if (oldVal!=null && value==null) bUpdated = true;
//            else if (!oldVal.toString().equals(value.toString())) bUpdated = true;

        }
        tbMap.put(colName, value);
        if (myTblIDname.equalsIgnoreCase(colName)) { 
            if (value != null)
                myTblIDvalue = Integer.parseInt(value.toString());
            else
                myTblIDvalue = null;
        }    
    }
    
    public boolean bUpdated() {
        if (isReadOnly()) return false;
        Set<String> keys = tbMap.keySet();
        for (String key: keys) {
           if (getValueOld(key)==null && this.getValue(key)!=null) return true; 
           if (getValueOld(key)!=null && this.getValue(key)==null) return true; 
           if (getValueOld(key)!=this.getValue(key)) return true; 
        }
        return false;
    }
    
    public boolean write(Boolean bCommit) {
        if (isReadOnly()) {
            Kernel.staticMsg("ReadOnly buffer sa nedá zapísať. (" + getFullTblName() + ")");
            return false;
        }
        if (!bUpdated() && !bIsNew()) {
            Kernel.staticMsg("DBtable_buffer: write() for ID: " + (myTblIDvalue==null?"NULL":myTblIDvalue) + " not necessary." );
            return true;
            
        }
        // Kernel.staticMsg("DBtable_buffer " + getFullTblName() + ": write() for ID: " + (myTblIDvalue==null?"NULL":myTblIDvalue) );
        Set<String> keys = tbMap.keySet();

        String nameToken = ""; // aktualna (fields) sekcia insert prikazu
        String valueToken = "";  // aktualna (values) sekcia insert prikazu
        for (String key: keys) {
            if (key.startsWith("@")) continue; // premenne sa nepouziju
            if (key.equalsIgnoreCase("c_zapisal") ||
                key.equalsIgnoreCase("c_zmenil")) continue; // premenne sa nepouziju
            if (key.equalsIgnoreCase(myTblIDname)) continue; // prim.kluc sa nezapisuje
            // System.out.println("DBtable_buffer:Writing: " + key + " = " + tbMap.get(key).toString());
            nameToken = nameToken + "^" + key;
            valueToken = valueToken + "^" + 
                getValueForWriteAsString(tbMap.get(key),"'");
        }
        // odstranenie nullteho delimiteru
        if (nameToken.length() > 1) nameToken = nameToken.substring(1);
        if (valueToken.length() > 1) valueToken = valueToken.substring(1);
        // Vlozenie novej vety do databazy
        if (bIsNew()) { // nova veta
            //QQQ sem by PATRIL kod pre oznacenie ciar v String-och
            nameToken  = nameToken.replace('^', ',');
            valueToken = valueToken.replace('^', ',');
            // odrezanie prvej ciarky v retazci
//            System.out.println("Commiting_INSERT: \n" + nameToken + "\n" + valueToken);
            String sql = "INSERT INTO " + getFullTblName() + "(" + nameToken + ") values ("
                    + valueToken + ")";
 //           System.out.println("DBtable_buffer:Writing_INSERT: \n" + sql);
            if (MyCn.getDbDriver().SQL_callSqlStatement(MyCn.getConn(), sql, true)) {
                sql = "select @@identity as sCurrKey";
                Object o = MyCn.getDbDriver().SQL_getQueryValue(MyCn.getConn(), sql, "sCurrKey");
                Integer newId = Integer.parseInt(o.toString());
                myTblIDvalue = newId;
            }
            else return false;
            if (bCommit) {
            try {
                MyCn.getConn().commit();
            } catch (SQLException ex) {
                Logger.getLogger(DBtable_buffer.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }        else { // oprava existujuceho riadku databazy
        String[] currNamArray;  // pole nazvov
        String[] currValArray;  // pole hodnot
        currNamArray = nameToken.split("\\^");
        currValArray = valueToken.split("\\^");
        String currUpdToken = "";
        for (int i = 0; i < currNamArray.length; i++) {
            currUpdToken = currUpdToken + "," 
                         + currNamArray[i] + "=" + currValArray[i];
        }
        // odstranenie prvej ciarky + zapis
        if (currUpdToken.length() > 1) {
 ////           System.out.println("UPDSTATEMENT11: " + currUpdToken);
            
            currUpdToken = currUpdToken.substring(1);
            
////            System.out.println("UPDSTATEMENT22: " + currUpdToken);
            currUpdToken = "UPDATE " + getFullTblName() + " SET " + currUpdToken
                         + " WHERE " + myTblIDname + " = " + myTblIDvalue; 
////            System.out.println("UPDSTATEMENT33: " + currUpdToken);
            System.out.println("DBtable_buffer:Writing_UPDATE: \n" + currUpdToken);
            if (!MyCn.getDbDriver().SQL_callSqlStatement(MyCn.getConn(), currUpdToken, bCommit)) return false;
            
        }
            
        } // else { // oprava existujuceho riadku databazy
        
        return true;
    }
    
    public boolean bIsNew() {
        return (myTblIDvalue==null || myTblIDvalue == -9999);
    }

    public boolean bufferCopyTo (DBtable_buffer newBuffer) {
        Set<String> keys = tbMap.keySet();
       //// System.out.println("copiingesBufferos:" + this.myTblName + " too: " + newBuffer.myTblName);
       ///// this.listEntries("", true);
       ///// newBuffer.listEntries("", true);
        for (String key: keys) {
            if (key.startsWith("@")) continue; // premenne sa nepouziju
            String colName = key;
            Boolean b = false;
            if (newBuffer.tbMap.containsKey(key)) {
                b = true;
                Object oValue = tbMap.get(key);
                newBuffer.tbMap.put(colName, oValue);
            } 
         /////   System.out.println("copiinges keys:" + key + " witvalju:" + tbMap.get(key).toString() + " sukkes:" + b);
            /*
            if (key.equalsIgnoreCase("c_zapisal") ||
                key.equalsIgnoreCase("c_zmenil")) continue; // premenne sa nepouziju
            if (key.equalsIgnoreCase(myTblIDname)) continue; // prim.kluc sa nezapisuje
            */
        }
        
        return true;
    } 
    
    
    public String getValueForWriteAsString(Object valueObjectForWrite, String stringApostroph) {
        if (valueObjectForWrite==null) return "null";
        if (valueObjectForWrite.toString().equals("<NULL>")) return "null";

        String strVal = "";
        if (valueObjectForWrite instanceof Double 
            || valueObjectForWrite instanceof Integer) {
             strVal = valueObjectForWrite.toString().trim();
        }
        else if (valueObjectForWrite instanceof Calendar) {
            strVal = FnEaS.calToStr((Calendar) valueObjectForWrite,"yyyy-MM-dd");
            if (strVal != null)
                strVal = stringApostroph + strVal + stringApostroph;
        }
        else {
             strVal = valueObjectForWrite.toString().trim();
             if (valueObjectForWrite instanceof String)
                 strVal = stringApostroph + strVal + stringApostroph;
        } 
                
        return strVal;
    }
    
    public boolean delete(Boolean bCommit) {
        if (isReadOnly()) {
            Kernel.staticMsg("ReadOnly buffer sa nedá vymazať. (" + getFullTblName() + ")");
            return false;
        }
        if (bIsNew()) {
            Kernel.staticMsg("Nový, nezapísaný záznam !"
            + "\n\nMazanie riadku nie je možné.", "DBtable_buffer(" + getFullTblName() + ").delete()") ;
            return false;
        }
        if (myTblIDvalue==null) {
            Kernel.staticMsg("Id s NULL-hodnotou !"
            + "\n\nMazanie riadku nie je možné.", "DBtable_buffer(" + getFullTblName() + ").delete()");
            return false;
        }

        String sql = "DELETE " + getFullTblName() + " WHERE " + myTblIDname + " = " + myTblIDvalue;
        System.out.println("Commiting_DELETE: \n" + sql);
        if (MyCn.getDbDriver().SQL_callSqlStatement(MyCn.getConn(), sql, true)) {
            if ((bCommit != null) && bCommit) try {
                MyCn.getConn().commit();
            } catch (SQLException ex) {
                Logger.getLogger(DBtable_buffer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }
        return false;
    }
    
    public String listEntries(String prefix, boolean bOut) {
        Set<String> keys = tbMap.keySet();
        String rv = "";
        if (prefix == null) prefix = "";
        rv = rv + prefix +  "\n#>>>>##> DBtable_buffer.listEntries()_forTable: " + getFullTblName() 
                            + " tblIDname: " + myTblIDname
                            + " valID: " + myTblIDvalue
                            + " isReadOnly:" + this.isReadOnly();
        for(String key: keys){
            rv = rv + "\n" + key + " = " + tbMap.get(key).toString();
        }
        if (bOut) System.out.println(rv);
        return rv;
    }
    
    public boolean setDataFromArrays (Object[] hdrs, Object[] data) {
        boolean bSuccess = krn.SQLQ_getDataToDbHashMap(tbMap,hdrs,data);
        myTblIDvalue = Integer.parseInt(tbMap.get(myTblIDname).toString());
        return bSuccess;
    }
        
    }
