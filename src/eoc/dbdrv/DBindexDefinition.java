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
public class DBindexDefinition extends eoc.EASproperties {

    private Kernel     krn;
    private IEOC_DB_driver DBdriver;
    private Connection conn;
    private String     DBtype;
    
    // konstruktor
    @SuppressWarnings("empty-statement")
    public DBindexDefinition(IEOC_DB_driver drv) {

        this.DBdriver = drv;

        this.setProperty("owner_name", ""); 
        this.setProperty("table_name", "");
        this.setProperty("index_name", "");
        this.setProperty("columns", "");
        this.setProperty("vectors", "");
        this.setProperty("num_columns", "");
        
        /* povolene nazvy vlastnosti
         * !!! Pozor na spravne poradie. Pole sa vyuziva pri pouzivani nazvov
         * vlastnosti v roznych cykloch v kode (napr. metoda setProperties(String s)
         * 
         * !!! Take iste poradie maju mad aj hodnoty v definicnych suboroch
         * typu CSV alebo XLS alebo ODT na riadkoch $TABLE|...........
         * *************************************************************************/
        String[] allProp = {"owner_name","table_name","index_name","columns",
                            "vectors","num_columns"};
        allowedProperties = allProp;
        this.clear(); 
        // uzamknutie objektu (znemozni pridavanie dalsich vlastnosti)
        this.lock(allowedProperties);
        
    }
    
    public void setProperties(String propStr /* delene znakom |*/) {
        int iEnt = FnEaS.iNumEntries(propStr,"|");
        // Zaujimavych je len prvych 10 clankov retazca (owner,table,idxname+7xcolumns)
        //, ostatne su vzdy prazdne
        if (iEnt > 10) {
            iEnt = 10; 
        }
        krn.OutPrintln("PROPSTR=" + propStr);
        // prve tri clanky su owner,table,indexname
        for (int i = 1; i<=3; i++) {
         //   krn.OutPrintln("setting-property: " + allowedProperties[i - 1]
         //           + " to \"" + FnEaS.sEntry(i, propStr, "|") + "\"");
            this.setProperty(allowedProperties[i - 1],FnEaS.sEntry(i, propStr, "|"));
        }
        // dalsich sedem clankov obsahuju definicie typu "column/(ASC/DESC)" 
        // kde default v casti /(ASC/DESC) je ASC (nemusi sa uviest)

        String colDef  = null; // definicia indexoveho stlpca
        String colName = null; // nazov stlpca
        String columns = "";   // nazvy stlpcov, delene ciarkou
        String vectors = "";   // smery triedeni stlpcov delene ciarkou
        String idxVect = "";   // orientacia triedenia stlpca (DESC je jedina specialita)
        int numCols = 0;       // pocet stlpcov v indexovom kluci
        col_block:
        for (int i = 4; i<=10; i++) {
            colDef  = FnEaS.sEntry(i, propStr, "|");
            colName = FnEaS.sEntry(1, colDef, "/");
            if (colName.equals("")) {
                break col_block;
            }
            krn.OutPrintln("IDX_COLDEF=" + colDef);
            if (FnEaS.iNumEntries(colDef, "/") > 1) {
               idxVect = FnEaS.sEntry(2, colDef, "/").substring(0, 3).toUpperCase();
            }
            idxVect = idxVect.equals("DES") ? "DESC" : "ASC";
            
            numCols++;
            columns = columns + "," + colName;
            vectors = vectors + "," + idxVect;
            //krn.OutPrintln(columns + "\n" + vectors);
        }
        this.setProperty("columns",columns.substring(1));
        this.setProperty("vectors",vectors.substring(1));
        this.setProperty("num_columns",String.valueOf(numCols));

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
    
    public String addIndex() {
        //krn.krnMsg("CreateEasysBase in " + dbType);
        String mtdName = /*DBtype + */ "defaultAddIndex"; 
        Method mtd=null;
        //krn.OutPrintln("1 addIndex -> CREATING INDEX: " + getProperty("index_name"));

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

    public String defaultAddIndex() throws SQLException {
        //krn.OutPrintln("defaultAddIndex()-> begin for " + DBtype);
        // QQQ 2017.11.07 - TOTO BY SA NEMALO VETVIT PODLA TYPU DB PO NOVOM, ALE FUNGOVAT BY TO MALO AJ TAKTO
        if (DBtype.equals("sybase")) {
        if (DBdriver.IndexExist(conn, this.getProperty("table_name"),
                getIndexName())) {
                krn.OutPrintln("... sybaseAddIndex()(on sybase DB)->Index " + getIndexName() + " uz existuje.");
                return "";
        }
        }

        if (DBtype.equals("postgres")) {
        if (DBdriver.IndexExist(conn, this.getProperty("table_name"),
                getIndexName())) {
                krn.OutPrintln("... defaultAddIndex()(on postgres DB)->Index " + getIndexName() + " uz existuje.");
                return "";
        }
        }

        if (DBtype.equals("mysql")) {
        if (DBdriver.IndexExist(conn, this.getProperty("table_name"),
                getIndexName())) {
                krn.OutPrintln("... defaultAddIndex()(on mysql DB)->Index " + getIndexName() + " uz existuje.");
                return "";
        }
        }

        Statement stm = conn.createStatement();
        String sqlCmd;
        String cols = this.getProperty("columns");
        String vcts = this.getProperty("vectors");
        String idxs = ""; // definicia stlpcov s vektormi
        sqlCmd = "CREATE INDEX " + getIndexName() + " ON " 
               + this.getProperty("table_name") + " ( " ;
        //krn.OutPrintln("111 defaultAddIndex()-> " + sqlCmd + "\n" + idxs);
        for (int i = 1;i <=Integer.parseInt(this.getProperty("num_columns")); i++) {
            idxs = idxs + "," + FnEaS.sEntry(i,cols,",") + " " + FnEaS.sEntry(i,vcts,",");
        }
        
        sqlCmd = sqlCmd + idxs.substring(1) + " ) ";
        //krn.OutPrintln("ADDINDEX> " + sqlCmd);
        stm.execute(sqlCmd);
        return "";
    }   
    
    public String getIndexName() { // vrati zadany, alebo generovany nazov indexu
        String idxName = ""; 
        idxName = this.getProperty("index_name");
        if (idxName.equals("")) {
            // najde sa prvy nazov indexu podla nazvu prveho stlpca + poradove cislo
            idxName = FnEaS.sEntry(1, this.getProperty("columns"),",");
        }
        return idxName;
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
                krn.OutPrintln("... postgresAddTable()->Tabulka " + tbl_name + " uz existuje.");
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
               System.out.print(" !!!!sqlCmt = ");  krn.OutPrintln(sqlCmt);
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

