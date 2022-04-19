/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package eoc.dbdrv;

import eoc.IEOC_DB_driver;
import eoc.database.DBconnection;
import eoc.dbdata.ColumnDefinition;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.read.biff.BiffException;
import system.FnEaS;
import system.Kernel;
import static system.Kernel.getGenericDataType;
import static system.Kernel.staticMsg;

/**
 *
 * @author rvanya
 */
public class DbDrv_Firebird implements IEOC_DB_driver {

    private Kernel krn;
    private PreparedStatement ps_ColExist;
    private DBconnection myDBconn;
    private Connection conn;
    public eoc.dbdrv.DBtableDefinition     tblDf;
    public eoc.dbdrv.DBcolumnDefinition    colDf;
    public eoc.dbdrv.DBindexDefinition     idxDf;
    private eoc.readers.PingPongFileReader ppRdr;
    private Statement mainStm;
    private ResultSet mainRS;  

    @Override
    public boolean SQL_Exists(Connection cnn, String tblName, String sWhere) {
        // ziskanie udajov z DB      
        String qry = "SELECT FIRST 1 * FROM " + tblName;
        if ((sWhere != null) && (!sWhere.trim().equals(""))) {
           qry = qry + " WHERE " + sWhere;       
        }
        ResultSet rs = SQL_getQueryResultSet(cnn, qry);

        boolean retval;
        try {
             retval = rs.next();
        } catch (SQLException ex) {
             retval = false;
        }

        return retval;
    }

    @Override
    public boolean SQL_callSqlStatement(Connection cnn, String stm, boolean bCommit) {
        try {
           stm = arrangeProc(stm); 
 ////          System.out.println("-------SSSQL_callSqlStatement:\n" + stm + " commit:" + bCommit); 
//           PreparedStatement pss = cnn.prepareStatement(stm);
            Statement stmt = cnn.createStatement();
            stmt.execute(stm);
////System.out.println("-------SSSQL_callSqlStatement: after execute");
//           pss.execute(); 
           if (bCommit) cnn.commit();
/////System.out.println("-------SSSQL_callSqlStatement: after commit");
           stmt.close();
        } catch (SQLException ex) {
            krn.Message("E",ex.getMessage() + "\nQRRY:" + stm,"Chyba pri zápise.");
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
////System.out.println("-------SSSQL_callSqlStatement: before return true");
      return true;
    }

    @Override
    public boolean SQL_getDataToDbHashMap(HashMap<String, Object> htb, Object[] hdrs, Object[] data) {
//QQQE tato metoda by sa mala vetvit ako napriklad fillDBcolumnInfo( ... )
// kvoli obsluhe roznych typov DB serverov        
        String colName;
        String realDataType;
        String genericDataType;
        Object dataObject;
        if (hdrs.length != data.length) {
            krn.Message("E", "Dľžka poľa hlavičiek: " + hdrs.length + " a poľa údajov: " + data.length
                    + "\nÚdaje sa nedajú načítať do tabuľky so struktúrou:"
                    + htb.keySet().toArray(), "CHYBA PRI VKLADANÍ ÚDAJOV");
            return false;
        }
        krn.clearHashMapValues(htb); // nulovanie tabulky
        try {
            // citaju sa kluce hashtable,
            // a pokusi sa najst k nim hodnota v resultset-e
       Set<String> enumKey = htb.keySet();
      // System.out.println("enumHasMapKey:::" + enumKey.toString());
       int aPos = -1;
       for (Object o: hdrs) {
           aPos++;
            //System.out.println("CURRENT_enumHasMapKey:::" + o.toString());
            colName = hdrs[aPos].toString();
            Object val = data[aPos];
            if (val == null) val = "<NULL>";
            realDataType    = val.getClass().getSimpleName();
            genericDataType = getGenericDataType(realDataType);

                // urcenie datoveho typu objektu 
                switch (genericDataType) {
                     case "decimal":
                         dataObject = new Double(val.toString());
                         break;
                     case "integer":
                         dataObject = new Integer(val.toString());
                         break;
                     case "date":
                         dataObject = Calendar.getInstance();
//                         System.out.println("THEDATTEA:" + val.toString());
                         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                         try {
                             dataObject = dateFormat.parse(val.toString());
                             dataObject = FnEaS.DateToCalendar((java.util.Date) dataObject);
//                             System.out.println("THEDATTEB:" + dataObject.getClass().getSimpleName());
                             break;
                         } catch (ParseException ex) {
                             Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
                         }
                     default:
                         dataObject = val.toString();
                } // switch (genericDataType) {
                if (!enumKey.contains(colName)) {
                    
                    colName = "@" + colName; // zmena na premennu
                    // upozornenie na zmenu, ked sa udiala prvykrat 
                    if (!enumKey.contains(colName)) {
                        krn.Message("W","Zmena názvu údaja " + colName.substring(1) 
                                   + " na " + colName + " !","Atypická udalosť");
                    }
                }
                htb.put(colName, dataObject);
       } // while(enumKey.hasMoreElements()) {
        } catch (Exception ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean SQL_getDbTableStructToHashMap(Connection cnn, String tblName, HashMap<String, Object> hmp) {
//QQQE tato metoda by sa mala vetvit ako napriklad fillDBcolumnInfo( ... )
// kvoli obsluhe roznych typov DB serverov        
        String cQry;
        String colName;
        String realDataType;
        String genericDataType;
        Object dataObject;
        ResultSet rs;
        String ow = FnEaS.getTblOwner(tblName);
        String tb = FnEaS.getTblName(tblName);

         cQry = "SELECT * FROM syscolumns WHERE "
                + (ow.length() > 0?" creator = '" + ow + "' AND ":"")
                + " tname = '" + tb +  "'";
        try {
            Statement stm;
            stm = cnn.createStatement();
            rs = stm.executeQuery(cQry);
            if(rs != null) {
            while (rs.next()) {
                colName         = rs.getString("cname");
                realDataType    = rs.getString("coltype");
                genericDataType = getGenericDataType(realDataType);

                // urcenie datoveho typu objektu 
                switch (genericDataType) {
                     case "decimal":
                         dataObject = new Double(0);
                         break;
                     case "integer":
                         dataObject = new Integer(0);
                         break;
                     case "date":
                         dataObject = Calendar.getInstance();
                     break;
                     default:
                         dataObject = new String();
                }
                hmp.put(colName, dataObject);
            } // while (rs.next()) {
            } // if(rs != null) {
            rs.close();
            stm.close(); // ked resultset obsahoval vety
            cnn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
       return true;
    }

    @Override
    public int SQL_getNumDbColumns(Connection cnn, String tblName) {
        String cQry;
        int i = 0;
        String ow = FnEaS.getTblOwner(tblName);
        String tb = FnEaS.getTblName(tblName);
        cQry = "SELECT count(*) as retval FROM syscolumns WHERE "
                + (ow.length() > 0?" creator = '" + ow + "' AND ":"")
                + " tname = '" + tb +  "'";
        //System.out.println("QRRI: " + cQry);
        Object o = /*XKernel.*/SQL_getQueryValue(cnn, cQry, "retval");
        i = (int) o;
        return i;
    }
    
    @Override
    public Object SQL_getQueryAsValue(Connection cnn, String qry, boolean no_error) {
        Statement stm;
        ResultSet rst = null;
        Object o = null;
        try {
             stm = cnn.createStatement();
             rst = stm.executeQuery(qry);
            // ziskanie udajov z DB
            rst.next();
            try {
                o = rst.getObject("retval");
            }
            catch (Exception ex) {
                o = null;                
            }
            rst.close();
            stm.close();
            return o;
        } catch (SQLException ex) {
            if (!no_error)
               staticMsg(ex.getMessage() + "\n\n" + qry, "Problém s SQL dotazom");
            else {
                if (!(ex.getMessage().equals("Not on row")))
                    Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public ResultSet SQL_getQueryResultSet(Connection cnn, String qry) {
        /*
        try {
            cnn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        Statement stm;
        ResultSet rst = null;
        try {
             stm = cnn.createStatement();
             rst = stm.executeQuery(qry);
//             stm.close();
             ////getStatQuery_stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
      return rst;
    }

    @Override
    public Object[][] SQL_getQueryResultSetAsArray(Connection cnn, String qry, boolean withHeaders) {
    // ziskanie udajov z DB      
    ResultSet rs = SQL_getQueryResultSet(cnn, qry);
        try {
            // prerobenie udajov resultsetu na pole
            return SQL_getResultSetAsArray(rs, withHeaders);
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Object SQL_getQueryValue(Connection cnn, String qry, String valName) {
        // ziskanie udajov z DB      
        ResultSet rs = SQL_getQueryResultSet(cnn, qry);
        valName = valName.toUpperCase(); // len v Firebird !
        Object o = null;
        if (rs != null) {
        try {
            rs.next();
            o = rs.getObject(valName);
        } catch (SQLException ex) {
                // Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
                o = null;
        }
        }
        return o;
    }

    @Override
    public Object[][] SQL_getResultSetAsArray(ResultSet rs, boolean withHeaders) throws SQLException {
        // prerobenie udajov resultsetu na pole
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int columnCount = rsMetaData.getColumnCount();

        // pole viet z tabulky DB    
        ArrayList<Object[]> result = new ArrayList<>();
        while (rs.next()) {
            Object[] str = new Object[columnCount]; // pole pre jednu vetu z tabulky DB
            for (int i=0; i < columnCount; ++i){
                Object obj = rs.getObject(i + 1); // prvy objekt je 1. !
                str[i] = obj;
            }
            result.add(str);
        }
        int resultLength = result.size(); // pocet viet v poli
        Object[][] finalResult = 
                   new Object[resultLength + (withHeaders?1:0)][columnCount];
        if (withHeaders) {
            Object[] header = new Object[columnCount];
            for (int i=1; i <= columnCount; ++i){
                Object label = rsMetaData.getColumnLabel(i);
                header[i-1] = label;
            }
            finalResult[0] = header;
        }
        for(int i=0;i<resultLength;++i){
            Object[] row = result.get(i);
            finalResult[i + (withHeaders?1:0)] = row;
        }
        rs.close();
        return finalResult;
    }

    @Override
    public ColumnDefinition[] SQL_getResultSetAsColumnDefinitionArray(Connection cn, ResultSet rs) throws SQLException {
        ResultSetMetaData mtdt = rs.getMetaData();
        eoc.dbdata.ColumnDefinition[] colDef = new eoc.dbdata.ColumnDefinition[mtdt.getColumnCount()];
        for (int i = 1; i <= mtdt.getColumnCount(); i++) {
            System.out.println("COL:" + i + " \n"
              + " getColumnClassName:" + mtdt.getColumnClassName(i) + " \n"
              + " getColumnDisplaySize:" + mtdt.getColumnDisplaySize(i) + " \n"
              + " getColumnLabel:" + mtdt.getColumnLabel(i) + " \n"
              + " getColumnName:" + mtdt.getColumnName(i) + " \n"
              + " getColumnType:" + mtdt.getColumnType(i) + " \n"
              + " getColumnTypeName:" + mtdt.getColumnTypeName(i) + " \n"
              + " getPrecision:" + mtdt.getPrecision(i) + " \n"
              + " getScale:" + mtdt.getScale(i) + " \n"
              + " getSchemaName:" + mtdt.getSchemaName(i) + " \n"
              + " getTableName:" + mtdt.getTableName(i) + " \n"
              + " isAutoIncrement:" + mtdt.isAutoIncrement(i) + " \n\n"
                      );
              colDef[i - 1] = new eoc.dbdata.ColumnDefinition(krn, myDBconn, mtdt, i);
        }
        return colDef;
    }

    @Override
    public boolean SQL_getSqlRowToHashMap(Connection cnn, HashMap<String, Object> htb, String sQry) {
////        System.out.println("SQL_getSqlRowToHashtableFORRR:" + sQry);
//QQQE tato metoda by sa mala vetvit ako napriklad fillDBcolumnInfo( ... )
// kvoli obsluhe roznych typov DB serverov        
        String colName;
        String realDataType;
        String genericDataType;
        Object dataObject;
        ResultSet rs;
////       System.out.println("htblngBEFF:" + htb.size());
        krn.clearHashMapValues(htb); // nulovanie tabulky
////       System.out.println("htblngATFF:" + htb.size());
        try {
            Statement stm;
            stm = cnn.createStatement();
            rs = stm.executeQuery(sQry);
            if(rs == null) {
                 rs.close();
                 stm.close(); 
                return false;
            }    
//            rs.beforeFirst();
            rs.next();
            // citaju sa kluce hashtable,
            // a pokusi sa najst k nim hodnota v resultset-e
       Set<String> enumKey = htb.keySet();
      // System.out.println("enumHasMapKey:::" + enumKey.toString());
       for (Object o: enumKey) {
            //System.out.println("CURRENT_enumHasMapKey:::" + o.toString());
            colName = o.toString();
            Object val = rs.getObject(colName);
            if (val == null) val = "<NULL>";
            realDataType    = val.getClass().getSimpleName();
            genericDataType = getGenericDataType(realDataType);
//            System.out.println("SQL_getSqlRowToHashtable:: " 
//                    + key + " = " + realDataType + " -> " + genericDataType);
//            Object val = htb.get(key);
            
/*****            
                colName         = rs.getString("cname");
                realDataType    = rs.getString("coltype");
                genericDataType = getGenericDataType(realDataType);
****/

                // urcenie datoveho typu objektu 
                switch (genericDataType) {
                     case "decimal":
                         dataObject = new Double(val.toString());
                         break;
                     case "integer":
                         dataObject = new Integer(val.toString());
                         break;
                     case "date":
                         dataObject = Calendar.getInstance();
//                         System.out.println("THEDATTEA:" + val.toString());
                         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                         try {
                             dataObject = dateFormat.parse(val.toString());
                             dataObject = FnEaS.DateToCalendar((java.util.Date) dataObject);
//                             System.out.println("THEDATTEB:" + dataObject.getClass().getSimpleName());
                             break;
                         } catch (ParseException ex) {
                             Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
                         }
                     default:
                         dataObject = val.toString();
                } // switch (genericDataType) {
                htb.put(colName, dataObject);
       } // while(enumKey.hasMoreElements()) {
           rs.close();
           stm.close(); // ked resultset obsahoval vety
        } catch (Exception ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean SQL_getSqlRowToHashTable(Connection cnn, Hashtable<String, Object> htb, String sQry) {
////        System.out.println("SQL_getSqlRowToHashtableFORRR:" + sQry);
//QQQE tato metoda by sa mala vetvit ako napriklad fillDBcolumnInfo( ... )
// kvoli obsluhe roznych typov DB serverov        
        String colName;
        String realDataType;
        String genericDataType;
        Object dataObject;
        ResultSet rs;
        
       System.out.println("htblngBEFF:" + (htb==null));
       krn.clearHashTableValues(htb); // nulovanie tabulky
////       System.out.println("htblngATFF:" + htb.size());
        try {
            Statement stm;
            stm = cnn.createStatement();
            rs = stm.executeQuery(sQry);
            if(rs == null) {
                 rs.close();
                 stm.close(); 
                return false;
            }    
//            rs.beforeFirst();
            rs.next();
            // citaju sa kluce hashtable,
            // a pokusi sa najst k nim hodnota v resultset-e
       Enumeration<String> enumKey = htb.keys();
////       System.out.println("enumKey:::" + enumKey.toString());
       while(enumKey.hasMoreElements()) {
            colName = enumKey.nextElement();
            System.out.println("SQL_getSqlRowToHashtable:: " 
                    + colName + ".... testing");
            Object val = rs.getObject(colName);
            if (val != null)
               realDataType    = val.getClass().getSimpleName();
            else 
               realDataType = "<NULL>";
//            rs.getMetaData().getColumnClassName(rs.)
            System.out.println("SQL_getSqlRowToHashtable:: " 
                    + colName + " = " + realDataType + " -> .... testing");
            genericDataType = getGenericDataType(realDataType);
            System.out.println("SQL_getSqlRowToHashtable:: " 
                    + colName + " = " + realDataType + " -> " + genericDataType);
//            Object val = htb.get(key);
            
/*****            
                colName         = rs.getString("cname");
                realDataType    = rs.getString("coltype");
                genericDataType = getGenericDataType(realDataType);
****/

                // urcenie datoveho typu objektu 
                switch (genericDataType) {
                     case "decimal":
                         dataObject = new Double(val.toString());
                         break;
                     case "integer":
                         dataObject = new Integer(val.toString());
                         break;
                     case "date":
                         dataObject = Calendar.getInstance();
//                         System.out.println("THEDATTEA:" + val.toString());
                         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                         try {
                             dataObject = dateFormat.parse(val.toString());
                             dataObject = FnEaS.DateToCalendar((java.util.Date) dataObject);
//                             System.out.println("THEDATTEB:" + dataObject.getClass().getSimpleName());
                             break;
                         } catch (ParseException ex) {
                             Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
                         }
                     default:
                         dataObject = val!=null?val.toString():"<null>";
                } // switch (genericDataType) {
                htb.put(colName, dataObject);
       } // while(enumKey.hasMoreElements()) {
           rs.close();
           stm.close(); // ked resultset obsahoval vety
        } catch (Exception ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public HashMap<String, Object> SQL_getSqlTableAsHashMap(Connection cnn, String tblName) {
//QQQE tato metoda by sa mala vetvit ako napriklad fillDBcolumnInfo( ... )
// kvoli obsluhe roznych typov DB serverov        
        HashMap<String, Object> htbRecord = null; 
        String cQry;
        String colName;
        String realDataType;
        String genericDataType;
        Object dataObject;
        ResultSet rs;
         cQry = "SELECT * FROM syscolumns WHERE tname = '" + tblName +  "'";
        try {
            Statement stm;
            stm = cnn.createStatement();
            rs = stm.executeQuery(cQry);
            if(rs != null) {
            htbRecord = new HashMap<String, Object>();
            while (rs.next()) {
                colName         = rs.getString("cname");
                realDataType    = rs.getString("coltype");
                genericDataType = getGenericDataType(realDataType);

                // urcenie datoveho typu objektu 
                switch (genericDataType) {
                     case "decimal":
                         dataObject = new Double(0);
                         break;
                     case "integer":
                         dataObject = new Integer(0);
                         break;
                     case "date":
                         dataObject = Calendar.getInstance();
                     break;
                     default:
                         dataObject = new String();
                }
                htbRecord.put(colName, dataObject);
            } // while (rs.next()) {
            } // if(rs != null) {
            stm.close(); // ked resultset obsahoval vety
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
       return htbRecord;
    }

    @Override
    public Hashtable<String, Object> SQL_getSqlTableAsHashtable(Connection cnn, String tblName) {
//QQQE tato metoda by sa mala vetvit ako napriklad fillDBcolumnInfo( ... )
// kvoli obsluhe roznych typov DB serverov        
        Hashtable<String, Object> htbRecord = null; 
        String cQry;
        String colName;
        String realDataType;
        String genericDataType;
        Object dataObject;
        ResultSet rs;
        String ow = FnEaS.getTblOwner(tblName);
        String tb = FnEaS.getTblName(tblName);

         cQry = "SELECT * FROM syscolumns WHERE "
                + (ow.length() > 0?" creator = '" + ow + "' AND ":"")
                + " tname = '" + tb +  "'";
                 
        try {
            Statement stm;
            stm = cnn.createStatement();
            rs = stm.executeQuery(cQry);
            if(rs != null) {
            htbRecord = new Hashtable<String, Object>();
            while (rs.next()) {
                colName         = rs.getString("cname");
                realDataType    = rs.getString("coltype");
                genericDataType = getGenericDataType(realDataType);

                // urcenie datoveho typu objektu 
                switch (genericDataType) {
                     case "decimal":
                         dataObject = new Double(0);
                         break;
                     case "integer":
                         dataObject = new Integer(0);
                         break;
                     case "date":
                         dataObject = Calendar.getInstance();
                     break;
                     default:
                         dataObject = new String();
                }
                htbRecord.put(colName, dataObject);
            } // while (rs.next()) {
            } // if(rs != null) {
            stm.close(); // ked resultset obsahoval vety
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
       return htbRecord;
    }

    @Override
    public String SQL_runStatement(Connection cn, String stmStr, boolean bCommit) {
        try {
            Statement stmt = cn.createStatement();
            stmt.executeQuery(stmStr);
            if (bCommit) cn.commit();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    return "";
    }
    @Override
    public boolean ColumnExist(Connection cn, String tbl, String col) throws SQLException {
        // Test na existenciu tabulky 
        if (ps_ColExist==null) {
            String sqlCmd;
            sqlCmd = "select * from syscolumn join systable " 
                     + "where table_name= ? And column_name = ?";
            ps_ColExist = cn.prepareStatement(sqlCmd);
        }
        ps_ColExist.setString(1, tbl);
        ps_ColExist.setString(2, col);
        ResultSet rs = ps_ColExist.executeQuery();
            //stm.close();
            return rs.next();
    }

    @Override
    public void setKernel(Kernel k) {
        krn = k;
    }

    @Override
    public void setConnection(Connection cn) {
        conn = cn;
        try {
            mainStm = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(DbDrv_Sybase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getDBtype(Connection cn) {
        return "FIREBIRD";
    }

    @Override
    public boolean TableExist(Connection cn, String tbl) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean IndexExist(Connection cn, String tbl, String idx) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSQLdataType (String EOC_data_type, 
           int dtLength, int dtDecimals, String dbtype) {
        String SQLdtt = null;
        switch (EOC_data_type.substring(0, 3)) {
            case "cha": { 
                if(dbtype.equals("postgres")) {
                SQLdtt = "character varying(" + dtLength + ")"; 
                }
                else { // sybase, a.t.d.
                SQLdtt = "varchar(" + dtLength + ")"; 
                }
                break; 
            }  // char(acter)
            case "int": SQLdtt = "integer"; break;  // int(eger) 
            case "lin": SQLdtt = "bigint"; break;   // lint 
            case "dtt": SQLdtt = "datetime"; break; // dttm
            case "boo": SQLdtt = "bit"; break;      // bool(ean) 
            case "log": SQLdtt = "bit"; break;      // logi(cal) 
            case "dat": SQLdtt = "date"; break;      // date 
            case "dec": {
                SQLdtt = "numeric(" + dtLength + "," + dtDecimals + ")";
                
            } break;
            default: SQLdtt = EOC_data_type;        // date,text,
        }
        //krn.OutPrintln("getSQLdataType() returning: " + SQLdtt);
        return SQLdtt;
    }

    @Override
    public Connection getConnection() {
        return conn;
    }

    @Override
    public String getDatabaseName() throws SQLException {
        String dbname = "";
        ResultSet rs;
        Statement st;
        /*
        SYBASE
        if (conn != null) {
            st = conn.createStatement();
            rs = st.executeQuery("select DB_PROPERTY ( 'Name' ) as dbName");
            rs.next();
            dbname = rs.getString("dbName");
        }
        */
        // Firebird
        if (conn != null) {
 //           FBSimpleDataSource fbsrc = new FBSimpleDataSource();
//            st = conn.createStatement();
//            rs = st.executeQuery("select DB_PROPERTY ( 'Name' ) as dbName");
//            rs.next();
//            dbname = rs.getString("dbName");
//            dbname = fbsrc.getDatabase();
             String ur;
             ur = conn.getMetaData().getURL();
             dbname = ur.substring(ur.lastIndexOf("\\") + 1);
        }
        
        return dbname;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public String CreateBaseTables() 
        throws NoSuchMethodException, FileNotFoundException, IOException, 
               IllegalAccessException, IllegalArgumentException, 
               InvocationTargetException, BiffException, SQLException {
        //krn.krnMsg("A");
        String tbDef; // tbl-name,tbl-type(''/TMP/...),OnCommit, label, tooltyp
        tbDef = "|eas_tbldefs|Definície tabuliek" 
              + "|Rozsirene vlastnosti tabuliek pre architekturu EOC|||";
        tblDf.setProperties(tbDef);
        tblDf.addTable();
         tbDef = "|eas_coldefs|Definície stľpcov tabuliek" 
              + "|Rozsirene vlastnosti stľpcov tabuliek pre architekturu EOC|||";
        tblDf.setProperties(tbDef);
        tblDf.addTable();
        //krn.krnMsg("B");
        /*
    CREATE SEQUENCE tblmasterid
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 10000176
  CACHE 1;
ALTER TABLE tblmasterid
  OWNER TO eas;
  */
   
        String[] colDef; // tbl-name,col-name,data-type,NULL(Y/N),length,decimals,default,
                         // format,label,ColumnLabel,tooltyp
        colDef = new String[] {"eas_tbldefs", "tableName", "VARCHAR","N","30","0","",
                "x(30)","Názov tabuľky","Tabuľka",""};
       
            //String[] colDef; // tbl-name,col-name, data-type
            // ('CHAR','DECI','DATE','DATETIME','TIME','INT','BLOB','LOGIC'),
            // length, decimals,Default,Format,Description(popis/poznamka),help(tooltyp)
            // ,metaData, referencesTB.COLUMN, on_update (CASCADE,SET NULL,SET DEFAULT,RSTRICT),
            // on_delete (CASCADE,SET NULL,SET DEFAULT,RSTRICT)
            //colDef = new String[] {tblDef[0], "c_format", "CHAR",
              //  "","","x(20)","Formátovací reťazec databázového stľpca","","",
              //  };

          //  colDf.clear();  // mazanie pripadnych starych hodnot attributov
           // colDf.set_$props(tblDef[0], "c_format", "char", "30", "");
           // colDf.addColumn();
           // sybaseAddColumn(cn,colDf);
         //   addColumn(colDf);
         //   parseAndProcessDBdefFile_csv(".\\SQL\\DBstru\\DBdef_base.csv");    
        //krn.krnMsg("C");
         parseAndProcessDBdefFile_xls(".\\SQL\\DBstru\\DBdef_base.xls");    
        //krn.krnMsg("D");
         conn.commit();
// Vytvorenie stlpcov tabulky          
       return ""; 
    }

@Override
public void parseAndProcessDBdefFile_xls(String fileName) 
         throws NoSuchMethodException, FileNotFoundException, IOException, 
                IllegalAccessException, IllegalArgumentException,
                InvocationTargetException,
                BiffException {
       // citanie riadkov pomocou pingpongreadera
        Method mtd;
        mtd = this.getClass().getMethod("handleDBdefFileRow", new Class[] {String.class});
        mtd.setAccessible(true);
        krn.cinn.viewFrm();
        krn.cinn.setCinn("Reading: " + fileName);
        //krn.OutPrintln("Reading: " + fileName);
        ppRdr.readFileByMethod_xls((Object) this, fileName, mtd);
        krn.cinn.setCinn("Readin finished.");
        krn.cinn.hideFrm();
    }

    @Override
    public String fillDBcolumnInfo(DBcolumnInfo colInf, Connection cnn, String own, String tbl, String col) {
        own = own==null ? "" : own.trim();
        tbl = tbl==null ? "" : tbl.trim();
        col = col==null ? "" : col.trim();
        if (tbl.equals("") || col.equals("")) 
            return "Nekompletná definícia tabuľky: " + tbl + "." + col; 
        String cQry;
        ResultSet rs;  
        ////System.out.println("fillSybaseDBcolumnInfo==>>owner===" + owner);
        cQry = "SELECT * FROM syscolumns WHERE tname = '" + tbl + "' and cname = '" + col + "'";
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

    @Override
    public String getDBcolumnInfo (Connection cnn, /* String owner, */ 
                                         String tbl, String col, String Info) {
        String cOut = "";
        String cProperty = "";
        String cQry;
        tbl = FnEaS.getTblName(tbl);
        cQry = "SELECT * FROM syscolumns WHERE tname = '" + tbl + "' and cname = '" + col + "'";
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
           System.out.println("SYCOINFF:" + cQry);
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

    @Override
    public void setDBconnection(DBconnection cn) {
        myDBconn = cn;
    }

    @Override
    public String getDBcolValue(Connection cnn, String db, String tbl,
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

    @Override
    public String arrangeQry(String sQry) {
        if (sQry.contains("FIRST")) sQry = sQry.replace("FIRST", "FIRST 1");
         return sQry;
    }

    @Override
    public String arrangeProc(String sProc) {
        if (sProc.contains("SET") && !sProc.startsWith("UPDATE")) sProc = sProc.replace("SET", " ");
        if (sProc.contains("IF")) sProc = sProc.replace("IF", "IF (");
        if (sProc.contains("THEN")) sProc = sProc.replace("THEN", ") THEN");
        if (sProc.contains("END IF (")) sProc = sProc.replace("END IF (", "END IF");
//Vybrat declare riadky pred begin !!!        
//        sProc = "SET TERM !;\nEXECUTE BLOCK AS\nBEGIN\n" + sProc + "\nEND!\nSET TERM ;!\n";
        sProc = moveDeclareToFront(sProc).trim();
        if (!sProc.endsWith(";")) sProc = sProc + ";";
//        sProc = "EXECUTE BLOCK AS\nBEGIN\n" + sProc + "\nEND\n";
        sProc = "EXECUTE BLOCK AS\n" + sProc + "\n";
 
        System.out.println(">>>>>> arrangeProc::\n" + sProc);
         return sProc;
    }
    
    private String moveDeclareToFront(String s) {
        s = s.trim()/*.toUpperCase()*/;
        String sn = "";
        String[] sx;
        boolean addBeginAndEnd = !s.toUpperCase().startsWith("BEGIN");
        sx = s.split("\n");
        
        // posun riadkov s DECLARE na zaciatok.
        for (int i = 0; i < sx.length; i++) {
            if (sx[i].trim().toUpperCase().startsWith("DECLARE")) sn = sn + sx[i] + "\n";
            System.out.println("SX(" + i + "): " + sx[i]);
        }
        
        // pridanie ostatnych riadkov
        if (addBeginAndEnd) sn = sn + "BEGIN\n";
        for (int i = 0; i < sx.length; i++) {
            if (!sx[i].trim().toUpperCase().startsWith("DECLARE")) sn = sn + sx[i] + "\n";
        }
        if (addBeginAndEnd) sn = sn + "END\n";
        
        String[] sxn = sn.split("\n");

        // osetrenie IF - END IF. - Pridanie begin-end bloku
        int ifPos = -1;
        for (int i = 0; i < sxn.length; i++) {
            sxn[i] = sxn[i].replace("eas.", " ");
            if (sxn[i].trim().toUpperCase().startsWith("IF")) ifPos = i;
            if (sxn[i].trim().toUpperCase().startsWith("END IF")) {
                sxn[ifPos] = sxn[ifPos].replace("then", "THEN");
                sxn[ifPos] = sxn[ifPos].replace("THEN", "THEN\n BEGIN\n");
                sxn[i] = sxn[i].replace("end if", "END IF");
                sxn[i] = sxn[i].replace("END IF", "\nEND");
                sxn[i] = sxn[i].replace(";", "\n");
                ifPos = -1;
            }
        }
        sn = "";
        for (int i = 0; i < sxn.length; i++) {
            sn = sn + sxn[i] + "\n";
        }         
        
        return sn;
    };

    @Override
    public int getTableID(String sTableName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

}
