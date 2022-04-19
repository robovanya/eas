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
public class DbDrv_MySQL implements IEOC_DB_driver {

    @Override
    public boolean SQL_Exists(Connection cnn, String tblName, String sWhere) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean SQL_callSqlStatement(Connection cnn, String stm, boolean bCommit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean SQL_getDataToDbHashMap(HashMap<String, Object> htb, Object[] hdrs, Object[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean SQL_getDbTableStructToHashMap(Connection cnn, String tblName, HashMap<String, Object> hmp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object SQL_getQueryAsValue(Connection cnn, String qry, boolean no_error) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet SQL_getQueryResultSet(Connection cnn, String qry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[][] SQL_getQueryResultSetAsArray(Connection cnn, String qry, boolean withHeaders) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object SQL_getQueryValue(Connection cnn, String qry, String valName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[][] SQL_getResultSetAsArray(ResultSet rs, boolean withHeaders) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ColumnDefinition[] SQL_getResultSetAsColumnDefinitionArray(Connection cn, ResultSet rs) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean SQL_getSqlRowToHashMap(Connection cnn, HashMap<String, Object> htb, String sQry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean SQL_getSqlRowToHashTable(Connection cnn, Hashtable<String, Object> htb, String sQry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashMap<String, Object> SQL_getSqlTableAsHashMap(Connection cnn, String tblName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hashtable<String, Object> SQL_getSqlTableAsHashtable(Connection cnn, String tblName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String SQL_runStatement(Connection cn, String stmStr, boolean bCommit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean ColumnExist(Connection cn, String tbl, String col) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setKernel(Kernel k) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setConnection(Connection cn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDBtype(Connection cn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public int SQL_getNumDbColumns(Connection cnn, String tblName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSQLdataType(String EOC_data_type, int dtLength, int dtDecimals, String dbtype) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDatabaseName() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String CreateBaseTables() throws NoSuchMethodException, FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, BiffException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void parseAndProcessDBdefFile_xls(String fileName) throws NoSuchMethodException, FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, BiffException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String fillDBcolumnInfo(DBcolumnInfo cinf, Connection cnn, String own, String tbl, String col) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDBcolumnInfo(Connection cnn, String tal, String col, String Info) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDBconnection(DBconnection cn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDBcolValue(Connection cnn, String db, String tbl, String tblid, String tblIDVal, String colName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String arrangeQry(String sQry) {
        return sQry;
    }

    @Override
    public String arrangeProc(String sProc) {
        return sProc;
    }

    @Override
    public int getTableID(String sTableName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
