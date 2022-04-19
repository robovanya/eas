/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.dbdrv;

import eoc.IEOC_DB_driver;
import system.Kernel;
import system.FnEaS;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rvanya
 */
public class DBtableDefinition extends eoc.EASproperties {

    private Kernel     krn;
    private IEOC_DB_driver DBdriver;
    private Connection conn;
    private String     DBtype;
    
    // konstruktor
    public DBtableDefinition(IEOC_DB_driver DBdrv) {

        this.DBdriver = DBdrv;

        this.setProperty("owner_name", ""); 
        this.setProperty("table_name", "");
        this.setProperty("label", "");
        this.setProperty("tooltip", "");
        this.setProperty("description", "");
        this.setProperty("table_type", "");
        this.setProperty("on_commit", "");
        
        /* povolene nazvy vlastnosti
         * !!! Pozor na spravne poradie. Pole sa vyuziva pri pouzivani nazvov
         * vlastnosti v roznych cykloch v kode (napr. metoda setProperties(String s)
         * 
         * !!! Take iste poradie maju mad aj hodnoty v definicnych suboroch
         * typu CSV alebo XLS alebo ODT na riadkoch $TABLE|...........
         * *************************************************************************/
        String[] allProp = {"owner_name","table_name","label","tooltip",
                            "description","table_type","on_commit"};
        allowedProperties = allProp;

        this.clear(); 
        // uzamknutie objektu (znemozni pridavanie dalsich vlastnosti)
        this.lock(allowedProperties);
        
    }
    public void setProperties(String propStr /* delene znakom |*/) {
        int iEnt = FnEaS.iNumEntries(propStr,"|");
        // Zaujimavych je len prvych sedem clankov retazca, ostatne su vzdy prazdne
        if (iEnt > 7) {
            iEnt = 7; 
        }
        for (int i = 1; i<=iEnt; i++) {
         //   krn.OutPrintln("setting-property: " + allowedProperties[i - 1]
         //           + " to \"" + FnEaS.sEntry(i, propStr, "|") + "\"");
            this.setProperty(allowedProperties[i - 1],FnEaS.sEntry(i, propStr, "|"));
        }
    }
    
    public void setConn (Connection cn) {
        conn = cn;
        DBtype = DBdriver.getDBtype(conn).toLowerCase();
    }
    
    public void setKrn(Kernel k) {
        krn = k;
    }
    
    // toto by mohlo sluzit na vytvarnie tabulky z GUI
    public void set_owner_name(String val)  {this.setProperty("owner_name",val);} 
    public void set_table_name$(String val) {this.setProperty("table_name",val);}
    public void set_label$(String val)      {this.setProperty("label",val);}
    public void set_tooltip(String val)     {this.setProperty("tooltip",val);}
    public void set_description(String val) {this.setProperty("description",val);}
    public void set_table_type(String val)  {this.setProperty("table_type",val);}
    public void set_on_commit(String val)   {this.setProperty("on_commit",val);}
    
    public String addTable() {
        //krn.krnMsg("CreateEasysBase in " + dbType);
        String mtdName = DBtype + "AddTable"; 
        Method mtd=null;
        //krn.OutPrintln("1 addTable -> CREATING TABLE: " + getProperty("table_name"));

        try {
            mtd = this.getClass().getMethod(mtdName);
          if (mtd == null) {
             mtd = this.getClass().getDeclaredMethod(mtdName);
          }
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
        }
        //krn.OutPrintln("2 addTable -> CREATING TABLE: " + getProperty("table_name"));
          if (mtd != null) {
            // krn.krnMsg("xMethodV: " + mtd.toString() 
            //         + " is generic=" + mtd.toGenericString());
            try {
                 String retVal = (String) mtd.invoke(this);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          else {
              krn.Message("Method: " + mtdName + "not found.");
          }
        return "";
    }

    public String sybaseAddTable() {
        try {
            Statement stm = conn.createStatement();
            String sqlCmd;
            String sqlCmt;
            String tbl_name = this.getProperty("table_name");
            String cmt = "";
            
            cmt = cmt + this.getProperty("description").trim();
            
            if (cmt.equals("")) {
               cmt = cmt + this.getProperty("tooltip").trim();
            }
            
            if (!cmt.equals("")) {
            cmt = this.getProperty("label",""," - ") + cmt;
            }
            else {
            cmt = this.getProperty("label");
            }
            
            cmt = cmt.trim();
            
            if (cmt.equals("")) {
                sqlCmt = "COMMENT ON TABLE " + this.getProperty("owner_name","",".")  
                  + tbl_name + " IS null";
            }
            else {
                sqlCmt = "COMMENT ON TABLE " + this.getProperty("owner_name","",".")  
                  + tbl_name + " IS '" + cmt + "'";
            }
                    
            if (DBdriver.TableExist(conn, tbl_name)) {
                krn.OutPrintln("... sybaseAddTable()->Tabulka " + tbl_name + " uz existuje.");
            //    return "";
            }
            else {
                sqlCmd = "CREATE " + " " + this.getProperty("owner_name","",".")  
                        + this.getProperty("table_type")
                        + " TABLE " + tbl_name + "\n (" 
                        + "id_" + tbl_name 
                        + " INT NOT NULL DEFAULT AUTOINCREMENT,\n "
                        + "c_history_data LONG VARCHAR NOT NULL default '',\n"
                        + "c_vytvoril VARCHAR  NOT NULL DEFAULT CURRENT USER,\n"
                        + "d_vytvoril DATETIME NOT NULL DEFAULT CURRENT TIMESTAMP,\n"
                        + "c_zmenil   VARCHAR  NOT NULL DEFAULT CURRENT USER,\n"
                        + "d_zmenil   DATETIME NOT NULL DEFAULT CURRENT TIMESTAMP,\n"
                        + " PRIMARY KEY (" + "id_" + tbl_name + ") ,\n"
                        + " UNIQUE (" + "id_" + tbl_name + "));";
            
                krn.OutPrintln("sybaseAddTable()->CREATE-TABLE: " + sqlCmd);
                stm.execute(sqlCmd);
            }
            
            if (!sqlCmt.equals("")) {
               // System.out.print(" sqlCmt = ");  krn.OutPrintln(sqlCmt);
            stm.execute(sqlCmt);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            return "Table creation failed in sybaseAddTable()";
        }
        return "";
    }

    public String postgresAddTable() {
        try {
            Statement stm = conn.createStatement();
            String sqlCmd;
            String sqlCmt;
            String tbl_name = this.getProperty("table_name");
            String cmt = "";
            
            cmt = cmt + this.getProperty("description").trim();
            
            if (cmt.equals("")) {
               cmt = cmt + this.getProperty("tooltip").trim();
            }
            
            if (!cmt.equals("")) {
            cmt = this.getProperty("label",""," - ") + cmt;
            }
            else {
            cmt = this.getProperty("label");
            }
            
            cmt = cmt.trim();
            
            if (cmt.equals("")) {
                sqlCmt = "COMMENT ON TABLE " + this.getProperty("owner_name","",".")  
                  + tbl_name + " IS null";
            }
            else {
                sqlCmt = "COMMENT ON TABLE " + this.getProperty("owner_name","",".")  
                  + tbl_name + " IS '" + cmt + "'";
            }
                    
            if (DBdriver.TableExist(conn, tbl_name)) {
               // krn.OutPrintln("... postgresAddTable()->Tabulka " + tbl_name + " uz existuje.");
            //    return "";
            }
            else {
                sqlCmd = "CREATE " + " " + this.getProperty("owner_name","",".")  
                        + this.getProperty("table_type")
                        + " TABLE " + tbl_name + "\n (" 
                        + "id_" + tbl_name 
                        + " NUMERIC(21,0) NOT NULL DEFAULT nextval('tblmasterid'::regclass),\n "
                        + "c_history_data TEXT  NOT NULL default '',\n"
                        + "c_vytvoril     TEXT  NOT NULL DEFAULT CURRENT_USER,\n"
                        + "d_vytvoril TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                        + "c_zmenil       TEXT  NOT NULL DEFAULT CURRENT_USER,\n"
                        + "d_zmenil   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                        + " PRIMARY KEY (" + "id_" + tbl_name + ") ,\n"
                        + " UNIQUE (" + "id_" + tbl_name + "));";
                krn.OutPrintln("postgresAddTable()->CREATE-TABLE: " + sqlCmd);
                stm.execute(sqlCmd);
            }
            
            if (!sqlCmt.equals("")) {
              // System.out.print(" !!!!sqlCmt = ");  krn.OutPrintln(sqlCmt);
            stm.execute(sqlCmt);
            conn.commit();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            return "Table creation failed in postgresAddTable()";
        }
        return "";
    }

        public String mysqlAddTable() {
        try {
            Statement stm = conn.createStatement();
            String sqlCmd;
            String sqlCmt;
            String tbl_name = this.getProperty("table_name");
            String own_name = this.getProperty("owner_name","",".");
            String full_tbl_name = own_name + tbl_name;
            String cmt = "";
            
            cmt = cmt + this.getProperty("description").trim();
            
            if (cmt.equals("")) {
               cmt = cmt + this.getProperty("tooltip").trim();
            }
            
            if (!cmt.equals("")) {
            cmt = this.getProperty("label",""," - ") + cmt;
            }
            else {
            cmt = this.getProperty("label");
            }
            
            cmt = cmt.trim();
            
            if (cmt.equals("")) {
                sqlCmt = "ALTER TABLE " + full_tbl_name + " COMMENT ' '";
            }
            else {
                sqlCmt = "ALTER TABLE " + full_tbl_name + " COMMENT '" + cmt + "'";
            }
                    
            if (DBdriver.TableExist(conn, tbl_name)) {
                krn.OutPrintln("... mysqlAddTable()->Tabulka " + full_tbl_name + " uz existuje.");
            //    return "";
            }
            else {
                sqlCmd = "CREATE " + " " + this.getProperty("table_type")
                        + " TABLE " + full_tbl_name + "\n (" 
                        + "id_" + tbl_name 
                        + " INTEGER NOT NULL AUTO_INCREMENT,\n "
                        + "c_history_data TEXT  NOT NULL ,\n"
                        + "c_vytvoril     TEXT  NOT NULL ,\n"
                        + "d_vytvoril TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                        + "c_zmenil       TEXT  NOT NULL ,\n"
                        + "d_zmenil   TIMESTAMP NOT NULL ,\n"
                        + " PRIMARY KEY (" + "id_" + tbl_name + ") ,\n"
                        + " UNIQUE (" + "id_" + tbl_name + "));";
                krn.OutPrintln("mysqlAddTable()->CREATE-TABLE: " + sqlCmd);
                stm.execute(sqlCmd);
            }
            
            if (!sqlCmt.equals("")) {
               System.out.print(" !!!!>>sqlCmt = ");  krn.OutPrintln(sqlCmt);
            stm.execute(sqlCmt);
            conn.commit();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            return "Table creation failed in mysqlAddTable()";
        }
        return "";
    }

}

