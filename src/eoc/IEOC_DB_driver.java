/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package eoc;

import eoc.database.DBconnection;
import eoc.dbdrv.DBcolumnInfo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import jxl.read.biff.BiffException;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public interface IEOC_DB_driver {

public void setKernel(Kernel k);
public void setDBconnection(DBconnection cn); // msater dDBconnection object
public void setConnection(Connection cn);
public Connection getConnection();

public String getDBtype (Connection cn);

public boolean SQL_Exists (Connection cnn,String tblName, String sWhere);
public boolean SQL_callSqlStatement(Connection cnn, String stm, boolean bCommit);
public boolean SQL_getDataToDbHashMap(HashMap<String,Object> htb, Object[] hdrs, Object[] data);
public boolean SQL_getDbTableStructToHashMap(Connection cnn, String tblName,HashMap<String,Object> hmp);
public /*Xstatic*/ int SQL_getNumDbColumns (Connection cnn, String tblName);
public Object SQL_getQueryAsValue (Connection cnn,String qry, boolean no_error);
public ResultSet SQL_getQueryResultSet(Connection cnn,String qry);
public Object[][] SQL_getQueryResultSetAsArray (Connection cnn,String qry, boolean withHeaders);
public Object SQL_getQueryValue (Connection cnn,String qry, String valName);  
public Object[][] SQL_getResultSetAsArray (ResultSet rs, boolean withHeaders) throws SQLException;
public eoc.dbdata.ColumnDefinition[] SQL_getResultSetAsColumnDefinitionArray(Connection cn, ResultSet rs) throws SQLException;
public boolean SQL_getSqlRowToHashMap(Connection cnn, HashMap<String,Object> htb, String sQry);
public boolean SQL_getSqlRowToHashTable(Connection cnn, Hashtable<String,Object> htb, String sQry);
public HashMap<String, Object> SQL_getSqlTableAsHashMap(Connection cnn, String tblName);
public Hashtable<String, Object> SQL_getSqlTableAsHashtable(Connection cnn, String tblName);
public String SQL_runStatement(Connection cn, String stmStr, boolean bCommit);
// z povodnej triedy DBdrv
public boolean TableExist(Connection cn, String tbl) throws SQLException;
public boolean ColumnExist(Connection cn, String tbl, String col) throws SQLException;
public boolean IndexExist(Connection cn, String tbl, String idx) throws SQLException;
public String getSQLdataType (String EOC_data_type, int dtLength, int dtDecimals, String dbtype);
public String getDatabaseName() throws SQLException;
public String CreateBaseTables() 
        throws NoSuchMethodException, FileNotFoundException, IOException, 
               IllegalAccessException, IllegalArgumentException, 
               InvocationTargetException, BiffException, SQLException;
public void parseAndProcessDBdefFile_xls(String fileName) 
         throws NoSuchMethodException, FileNotFoundException, IOException, 
                IllegalAccessException, IllegalArgumentException,
                InvocationTargetException,
                BiffException;
public String fillDBcolumnInfo 
        (DBcolumnInfo cinf, Connection cnn, String own, String tbl, String col);

public String getDBcolumnInfo (Connection cnn, String tal, String col, String Info);

public String getDBcolValue(Connection cnn, String db, String tbl,
                            String tblid, String tblIDVal, String colName);
public String arrangeQry(String sQry);    
public String arrangeProc(String sProc);    
public int getTableID (String sTableName);    

}
