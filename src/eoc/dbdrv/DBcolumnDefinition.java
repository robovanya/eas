/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.dbdrv;

////import eoc.dbdrv.EOC_DBdrv;
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
//import org.jopendocument.dom.spreadsheet.MutableCell;
//import org.jopendocument.dom.spreadsheet.Sheet;
//import org.jopendocument.dom.spreadsheet.SpreadSheet;


/**
 *
 * @author rvanya
 */
public class DBcolumnDefinition extends eoc.EASproperties {

    private Kernel     krn;
    private IEOC_DB_driver      DBdriver;
    private Connection conn;
    private String     DBtype;
    private String     pomStr; // pomocna premenna
    private String     pomStrB; // dalsia pomocna premenna
    // owner, tbl-name,col-name, data-type
    // ('CHAR','DECI','DATE','DATETIME','TIME','INT','BLOB','LOGIC'),
    // length, decimals,Default,Format,Description(popis/poznamka),help(tooltyp)
    // ,metaData, referencesTB.COLUMN, on_update (CASCADE,SET NULL,SET DEFAULT,RSTRICT),
    // on_delete (CASCADE,SET NULL,SET DEFAULT,RSTRICT)

    // konstruktor
    public DBcolumnDefinition(IEOC_DB_driver DBxdrv) {

        /* povolene nazvy vlastnosti
         * !!! Pozor na spravne poradie. Pole sa vyuziva pri pouzivani nazvov
         * vlastnosti v roznych cykloch v kode (napr. metoda setProperties(String s)
         * 
         * !!! Take iste poradie maju mad aj hodnoty v definicnych suboroch
         * typu CSV alebo XLS alebo ODT na riadkoch $COLUMN|...........
         * *************************************************************************/
        String[]  allProp = {"owner_name","table_name","column_name",
                 "data_type","length","decimals","default","format","null_allowed",
                 "unique","primary_key","check","refTable","refColumn","on_update",
                 "on_delete","label","tooltip","description"};
        allowedProperties = allProp;

        DBdriver = DBxdrv;
        createProperties();

        this.clear(); 
        // uzamknutie objektu (znemozni pridavanie dalsich vlastnosti)
        this.lock(allowedProperties);
    }
    
    private void createProperties() {
        // Vytvorenie povolenych vlastnosti
        this.setProperty("owner_name", ""); 
        this.setProperty("table_name", "");
        this.setProperty("column_name", "");
        this.setProperty("data_type", "");
        this.setProperty("length", "");
        this.setProperty("decimals", "");
        this.setProperty("default", "");
        this.setProperty("format", "");
        this.setProperty("null_allowed", "");
        this.setProperty("unique", "");
        this.setProperty("primary_key", "");
        this.setProperty("check", "");
        this.setProperty("refTable", "");
        this.setProperty("refColumn", "");
        this.setProperty("on_update", "");
        this.setProperty("on_delete", "");
        this.setProperty("label", "");
        this.setProperty("tooltip", "");
        this.setProperty("description", "");
    }   
    
    public void set_owner_name(String val)   {this.setProperty("owner_name",val);} 
    public void set_table_name$(String val)   {this.setProperty("table_name",val);}
    public void set_column_name$(String val)  {this.setProperty("column_name",val);}
    public void set_data_type$(String val)    {this.setProperty("data_type",val);}
    public void set_null_allowed(String val) {this.setProperty("null_allowed",val);}
    public void set_unique(String val)       {this.setProperty("unique",val);}
    public void set_primary_key(String val)  {this.setProperty("primary_key",val);}
    public void set_length$(String val)       {this.setProperty("length",val);}
    public void set_decimals$(String val)     {this.setProperty("decimals",val);}
    public void set_default(String val)      {this.setProperty("default",val);}
    public void set_check(String val)        {this.setProperty("check",val);}
    public void set_refTable(String val)     {this.setProperty("refTable",val);}
    public void set_refColumn(String val)    {this.setProperty("refColumn",val);}
    public void set_on_update(String val)    {this.setProperty("on_update",val);}
    public void set_on_delete(String val)    {this.setProperty("on_delete",val);}
    public void set_label(String val)        {this.setProperty("label",val);}
    public void set_tooltip(String val)      {this.setProperty("tooltip",val);}
    public void set_description(String val)  {this.setProperty("description",val);}
    

    // vratenie hodnot attributov, alebo default-value (DV)
    public String get_owner_name(String val)   {return this.getProperty("owner_name",val);} 
    public String get_table_name$(String val)   {return this.getProperty("table_name",val);}
    public String get_column_name$(String val)  {return this.getProperty("column_name",val);}
    public String get_data_type$(String val)    {
        pomStr = this.getProperty("data_type",val);
        return (pomStr.substring(0,4).equals("char") ? "varchar" : pomStr);
    }
    public String get_null_allowed(String val) {return this.getProperty("null_allowed",val);}
    public String get_unique(String val)       {return this.getProperty("unique",val);}
    public String get_primary_key(String val)  {return this.getProperty("primary_key",val);}
    public String get_length$(String val)       {return this.getProperty("length",val);}
    public String get_decimals$(String val)     {return this.getProperty("decimals",val);}
    public String get_default(String val)      {return this.getProperty("default",val);}
    public String get_check(String val)        {return this.getProperty("check",val);}
    public String get_refTable(String val)     {return this.getProperty("refTable",val);}
    public String get_refColumn(String val)    {return this.getProperty("refColumn",val);}
    public String get_on_update(String val)    {return this.getProperty("on_update",val);}
    public String get_on_delete(String val)    {return this.getProperty("on_delete",val);}
    public String get_label(String val)        {return this.getProperty("label",val);}
    public String get_tooltip(String val)      {return this.getProperty("tooltip",val);}
    public String get_description(String val)  {return this.getProperty("description",val);}

    // vratenie hodnot attributov, alebo default-value 
    public String get_owner_name()   {return this.getProperty("owner_name");} 
    public String get_table_name$()   {return this.getProperty("table_name");}
    public String get_column_name$()  {return this.getProperty("column_name");}
    public String get_data_type$()    {return this.getProperty("data_type");}
    public String get_null_allowed() {return this.getProperty("null_allowed");}
    public String get_unique()       {return this.getProperty("unique");}
    public String get_primary_key()  {return this.getProperty("primary_key");}
    public String get_length$()       {return this.getProperty("length");}
    public String get_decimals$()     {return this.getProperty("decimals");}
    public String get_default()      {return this.getProperty("default");}
    public String get_check()        {return this.getProperty("check");}
    public String get_refTable()     {return this.getProperty("refTable");}
    public String get_refColumn()    {return this.getProperty("refColumn");}
    public String get_on_update()    {return this.getProperty("on_update");}
    public String get_on_delete()    {return this.getProperty("on_delete");}
    public String get_label()        {return this.getProperty("label");}
    public String get_tooltip()      {return this.getProperty("tooltip");}
    public String get_description()  {return this.getProperty("description");}

    // vratenie hodnot attributov S BODKOU na konci, alebo prazdneho retazca 
    public String get_owner_name(String sPref, String sSuff) {
        pomStr = this.getProperty("owner_name");
        
        return (pomStr.equals("") ? pomStr : sPref + pomStr + sSuff);
    } 
    
    public String get_realdata_type$() {
        
        pomStr = this.getProperty("data_type").substring(0, 4);
        switch (pomStr) {
            case "char": pomStr = "varchar";
                         pomStrB = this.get_length$();
                         if (!pomStrB.equals("")) {
                             pomStr = pomStr + "(" + pomStrB + ")";
                         }
                         break;
            default: pomStr = "BADDATATYPE_" + pomStr;
                     break;
        }
        return pomStr;
    }
     
    // Nastavenie zakladnych/POVINNYCH hodnot pre definiciu stlpca trabulky
    public void set_$props (String tb, String col, String dtt, String lng, String dec) {
        this.setProperty("table_name",tb);
        this.setProperty("column_name",col);
        this.setProperty("data_type",dtt);
        this.setProperty("length",lng);
        this.setProperty("decimals",dec);
    };

    
    public void setProperties(String propStr /* delene znakom |*/) {
        int iEnt = FnEaS.iNumEntries(propStr,"|");
        // Zaujimavych je len prvych devatnast clankov retazca, ostatne su vzdy prazdne
        if (iEnt > 19) {
            iEnt = 19; 
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
    
    public String addColumn() {
        //krn.krnMessage("CreateEasysBase in " + dbType);
        String mtdName = DBtype + "AddColumn"; 
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
        //krn.OutPrintln("addColumn() -> CREATING COLUMN: " + getProperty("column_name"));
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

    public String sybaseAddColumn() {
        try {
/* allowedProperties = {"owner_name","table_name","column_name",
                 "data_type","length","decimals","default","format","null_allowed",
                 "unique","primary_key","check","refTable","refColumn","on_update",
                 "on_delete","label","tooltip","description"};        */    
            Statement stm = conn.createStatement();
            String sqlCmd;
            String sqlCmt;
            String tbl_name    = this.getProperty("table_name");
            String col_name    = this.getProperty("column_name");
            String cmt         = "";
            String s = this.getProperty("decimals");
            s = (s.equals( "") ? "0" : s);
            Integer iLength    = Integer.parseInt(this.getProperty("length"));
            Integer iDecimals  = Integer.parseInt(s);
            String nullAllowed = this.getProperty("null_allowed");
            
            //krn.OutPrintln(iLength + "--"+ iDecimals);
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
                sqlCmt = "COMMENT ON COLUMN " + this.getProperty("owner_name","",".")  
                  + tbl_name + "." + col_name + " IS null";
            }
            else {
                sqlCmt = "COMMENT ON COLUMN " + this.getProperty("owner_name","",".")  
                  + tbl_name + "." + col_name + " IS '" + cmt + "'";
            }
                    
            if (DBdriver.ColumnExist(conn, tbl_name, col_name)) {
                krn.OutPrintln("... sybaseAddColumn()->Stľpec " + tbl_name + "." 
                                   + col_name + " uz existuje.");
            //    return "";
            }
            else {
                sqlCmd = 
                    "ALTER TABLE " + this.getProperty("owner_name","",".")  
                        + tbl_name + "\n" 
                        + " ADD " + col_name + " " 
                        + DBdriver.getSQLdataType(this.getProperty("data_type")
                        ,iLength,iDecimals,DBtype)
                        ;
                if (nullAllowed.equals("")) {
                    sqlCmd = sqlCmd + " NOT NULL"; // prazdny udaj znamena OK
                }
                else {
                    sqlCmd = sqlCmd + " NULL"; // inam moze mat hodnotu NULL
                    
                }
                krn.OutPrintln("sybaseAddColumn()->CREATE-COLUMN: " + sqlCmd);
                stm.execute(sqlCmd);
            }
            
            if (!sqlCmt.equals("")) {
               // System.out.print(" sqlCmt = ");  krn.OutPrintln(sqlCmt);
            stm.execute(sqlCmt);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            return "Table creation failed in sybaseAddColumn()";
        }
        return "";
    }

    public String postgresAddColumn() {
        try {
/* allowedProperties = {"owner_name","table_name","column_name",
                 "data_type","length","decimals","default","format","null_allowed",
                 "unique","primary_key","check","refTable","refColumn","on_update",
                 "on_delete","label","tooltip","description"};        */    
            Statement stm = conn.createStatement();
            String sqlCmd;
            String sqlCmt;
            String tbl_name    = this.getProperty("table_name");
            String col_name    = this.getProperty("column_name");
            String cmt         = "";
            String s = this.getProperty("decimals");
            s = (s.equals( "") ? "0" : s);
            Integer iLength    = Integer.parseInt(this.getProperty("length"));
            Integer iDecimals  = Integer.parseInt(s);
            String nullAllowed = this.getProperty("null_allowed");
            
            //krn.OutPrintln(iLength + "--"+ iDecimals);
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
                sqlCmt = "COMMENT ON COLUMN " + this.getProperty("owner_name","",".")  
                  + tbl_name + "." + col_name + " IS null";
            }
            else {
                sqlCmt = "COMMENT ON COLUMN " + this.getProperty("owner_name","",".")  
                  + tbl_name + "." + col_name + " IS '" + cmt + "'";
            }
            //krn.OutPrintln("... postgresAddColumn()->Stľpec " + tbl_name + "." + col_name);
                                 
            if (DBdriver.ColumnExist(conn, tbl_name, col_name)) {
            //   krn.OutPrintln("... postgresAddColumn()->Stľpec " + tbl_name + "." 
            //                      + col_name + " uz existuje.");
            //    return "";
            }
            else {
                sqlCmd = 
                    "ALTER TABLE " + this.getProperty("owner_name","",".")  
                        + tbl_name + "\n" 
                        + " ADD " + col_name + " " 
                        + DBdriver.getSQLdataType(this.getProperty("data_type")
                        ,iLength,iDecimals,DBtype)
                        ;
                if (nullAllowed.equals("")) {
                    sqlCmd = sqlCmd + " NOT NULL"; // prazdny udaj znamena OK
                }
                else {
                    sqlCmd = sqlCmd + " NULL"; // inam moze mat hodnotu NULL
                    
                }
                krn.OutPrintln("postgresAddColumn()->CREATE-COLUMN: " + sqlCmd);
                stm.execute(sqlCmd);
            }
            
            if (!sqlCmt.equals("")) {
               // System.out.print(" sqlCmt = ");  krn.OutPrintln(sqlCmt);
            stm.execute(sqlCmt);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            return "Table creation failed in postgresAddColumn()";
        }
        return "";
    }

    public String mysqlAddColumn() {
        try {
/* allowedProperties = {"owner_name","table_name","column_name",
                 "data_type","length","decimals","default","format","null_allowed",
                 "unique","primary_key","check","refTable","refColumn","on_update",
                 "on_delete","label","tooltip","description"};        */    
            Statement stm = conn.createStatement();
            String sqlCmd;
            String sqlCmt;
            String tbl_name    = this.getProperty("table_name");
            String col_name    = this.getProperty("column_name");
            String cmt         = "";
            String s = this.getProperty("decimals");
            s = (s.equals( "") ? "0" : s);
            Integer iLength    = Integer.parseInt(this.getProperty("length"));
            Integer iDecimals  = Integer.parseInt(s);
            String nullAllowed = this.getProperty("null_allowed");
            
            //krn.OutPrintln(iLength + "--"+ iDecimals);
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
                sqlCmt = "COMMENT ON COLUMN " + this.getProperty("owner_name","",".")  
                  + tbl_name + "." + col_name + " IS null";
            }
            else {
                sqlCmt = "COMMENT ON COLUMN " + this.getProperty("owner_name","",".")  
                  + tbl_name + "." + col_name + " IS '" + cmt + "'";
            }
                    
            if (DBdriver.ColumnExist(conn, tbl_name, col_name)) {
                krn.OutPrintln("... mysqlAddColumn()->Stľpec " + tbl_name + "." 
                                   + col_name + " uz existuje.");
            //    return "";
            }
            else {
                sqlCmd = 
                    "ALTER TABLE " + this.getProperty("owner_name","",".")  
                        + tbl_name + "\n" 
                        + " ADD " + col_name + " " 
                        + DBdriver.getSQLdataType(this.getProperty("data_type")
                        ,iLength,iDecimals,DBtype)
                        ;
                if (nullAllowed.equals("")) {
                    sqlCmd = sqlCmd + " NOT NULL"; // prazdny udaj znamena OK
                }
                else {
                    sqlCmd = sqlCmd + " NULL"; // inam moze mat hodnotu NULL
                    
                }
                krn.OutPrintln("mysqlAddColumn()->CREATE-COLUMN: " + sqlCmd);
                stm.execute(sqlCmd);
            }
            
            if (!sqlCmt.equals("")) {
              System.out.print(" sqlCmt = ");  krn.OutPrintln(sqlCmt);
            //stm.execute(sqlCmt);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(IEOC_DB_driver.class.getName()).log(Level.SEVERE, null, ex);
            return "Table creation failed in mysqlAddColumn()";
        }
        return "";
    }
}
