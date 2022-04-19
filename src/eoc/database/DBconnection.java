/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.database;

import eoc.IEOC_DB_driver;
import eoc.dbdrv.DbDrv_Firebird;
import eoc.dbdrv.DbDrv_MySQL;
import eoc.dbdrv.DbDrv_Postgres;
import eoc.dbdrv.DbDrv_Sybase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.firebirdsql.pool.FBSimpleDataSource;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class DBconnection {
    String     DBlayer;    // WORK/OLAP/WWW
    String     DBType;    // SYBASE/POSTGRES/FIREBIRD/MYSQL
    String     cDbDriverName;    
    String     URL;
    String     usr;
    String     pwd;
    String connectionType = ""; // TCP/LOCAL
    IEOC_DB_driver dbDrv;
    //Connection conn;
    Kernel krn;

    public String getDBType() {
        return DBType;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public boolean setConnectionType(String connType) {
       String s = connType.trim().toUpperCase();
       if (!s.equals("TCP") && !s.equals("LOCAL")) {
           krn.Message("E", "Neplatný typ spojenia s databázou: " + s, "");
           return false;
       }
       if (!connectionType.equals(s)) {
           connectionType = s;
           System.out.println("DBconnection.setConnectionType(): " + s);
       }
       return true;
    }

    public void setDbType(String DBTyp, Connection cn) {
        if (DBTyp == null) return;
        String supportedDBtypes = "SYBASE,POSTGRES,FIREBIRD,MYSQL"; 
        if (!supportedDBtypes.contains(DBTyp.toUpperCase())) {
            Kernel.staticMsg("Typ databázy: " + DBTyp + " nie je podporovaný !");
            this.DBType = "";
        }
//       if ((DBType != null) && !DBType.equals(DBTyp.toUpperCase()))
       if (DBType != null)
              System.out.println("\nDBconnection.setDbType(): " + DBType);

        this.DBType = DBTyp.toUpperCase();

        // vytvori sa interný eoc-driver podla typu DB
        if (DBType.equalsIgnoreCase("SYBASE")) {
           dbDrv = new DbDrv_Sybase();            
           dbDrv.setConnection(cn);
           dbDrv.setDBconnection(this);
        }
        else if (DBType.equalsIgnoreCase("POSTGRES")) {
           dbDrv = new DbDrv_Postgres();            
           dbDrv.setConnection(cn);
           dbDrv.setDBconnection(this);
        }
        else if (DBType.equalsIgnoreCase("FIREBIRD")) {
           dbDrv = new DbDrv_Firebird();            
           dbDrv.setConnection(cn);
           dbDrv.setDBconnection(this);
        }
        else if (DBType.equalsIgnoreCase("MYSQL")) {
           dbDrv = new DbDrv_MySQL();            
           dbDrv.setConnection(cn);
           dbDrv.setDBconnection(this);
        }

        if ((dbDrv != null) && (krn != null)) dbDrv.setKernel(krn);
        
////        System.out.println("setDBBTYPP: " + (dbDrv==null));
////        System.out.println("DBDRVVVVsetDbTypeB:" + DBType + "isnull:" + (dbDrv==null));
    }
    
    public String getDbDriverName() {
        return cDbDriverName;
    }

    public IEOC_DB_driver getDbDriver() {
        return dbDrv;
    }

    public void setDbDriverName(String DBdriver) {
        if (DBdriver == null) {
            Kernel.staticMsg("E","Prázdny názov databázového ovládača!","Chyba");
            return;
        }
        this.cDbDriverName = DBdriver;
        if (cDbDriverName.toUpperCase().contains("SQLANYWHERE")
                || cDbDriverName.contains("Nie je potrebn")) {
           DBType = "SYBASE";
        }
        else if (cDbDriverName.toUpperCase().contains("POSTGRES")) {
           DBType = "POSTGRES";
        }
        else if (cDbDriverName.toUpperCase().contains("FIREBIRD")) {
           DBType = "FIREBIRD";
        }
        else if (cDbDriverName.toUpperCase().contains("MYSQL")) {
           DBType = "MYSQL";
        }
        else {
            Kernel.staticMsg("E", "K databáze s firemným ovládačom:\n"
                    + cDbDriverName + "\n"
                    + "sa nenašiel vhodný interný ovládač.",
                    "Fatálna chyba !");
        }
        //tu sa prida skutocny driver podla typu DB do premennej DbDrv
    }

    public String getDBlayer() {
        return DBlayer;
    }

    public void setDBlayer(String DBlayer) {
        this.DBlayer = DBlayer;
    }
    public Connection getConn() {
        if (dbDrv != null)
           return dbDrv.getConnection();
        else
           return null;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    
    public String getDatabaseName() throws SQLException {
        String dbname = "";
        if (dbDrv != null) dbname = dbDrv.getDatabaseName();
        return dbname;
    }
    
    public void setKernel(Kernel k) {
        krn = k;
       /// System.out.println("Adrv--- " + (dbDrv==null) + " -- " + (krn==null) );
        if (dbDrv != null) dbDrv.setKernel(krn);
    }

}
