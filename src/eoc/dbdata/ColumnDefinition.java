/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.dbdata;

import eoc.database.DBconnection;
import system.Kernel;
import system.FnEaS;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author rvanya
 */

//  STRUKTURA retazca definice stlpca, ktora sa spracuje konstruktorom
//  dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
//  numdecimals^formatstring^tooltyp^HIDDEN

/****************************************************************************
 * Objekt obsahuje bud definiciu databazoveho stlpca s pridanymi vlastnostami
 * alebo definiciu specialneho stlpca, kde tableName sa zacina sekvenciou !@@
 * (!@@calculated, !@@image, ....)
 * SETTER metody funguju len ked sa vola kostruktor s parametrom
 *        colDef == "settersAllowed"
 ****************************************************************************/
public class ColumnDefinition {
private Kernel     krn;
private DBconnection myDBconn;
private String  ownerName;   
private String  tableName;
private String  fieldName;
private String  dataType;
private String  genericDataType;
private String  defaultValue;
private String  fieldLabel;
private Integer fieldLength;
private Integer numDecimals;
private String  formatString;
private String  tooltip;
private boolean hidden;
private String  headerValue;
private boolean settersAllowed = false;
private boolean setFromInitializer = false;
private Class columnClass;

// konstruktor, ktory vytvori vlastnosti z databazy
public ColumnDefinition(Kernel kr, DBconnection cn, String owner, String table,
                            String field, String label) {
    krn  = kr;
    myDBconn = cn;
    //DOKONCIT !!!
}

// konstruktor, ktory vytvori vlastnosti z retazca definicii (delimiter==^)
// alebo retazec musi mat hodnotu "settersAllowed", co prepne objekt
// do modu, ktory dovoli nastavovanie hodnot setXXXXX metodami
// POZNAMKA: setter-y pre hidden a headerValue su dostupne vzdy
public ColumnDefinition(Kernel kr, DBconnection cn, String colDef) {
    krn  = kr;
    myDBconn = cn;
    initializeFromColDefString(colDef);
}

public ColumnDefinition(Kernel kr, DBconnection cn, 
                            ResultSetMetaData colDef, int colIdx) 
      throws SQLException {
    krn  = kr;
    myDBconn = cn;
    initializeFromRSMetaData(myDBconn, colDef, colIdx);
}

/* columnDefinition - data
private String  ownerName;   
private String  tableName;
private String  fieldName;
private String  dataType;
private String  genericDataType;
private String  defaultValue;
private String  fieldLabel;
private Integer fieldLength;
private Integer numDecimals;
private String  formatString;
private String  tooltip;
private boolean hidden;
private String headerValue;
*/    
public boolean initializeFromRSMetaData(DBconnection cn, ResultSetMetaData colDef,
                                        int colIdx) throws SQLException {
     settersAllowed = true;
     int ii;
     // nazov databazy/ownera
     this.setOwnerName(colDef.getSchemaName(colIdx));
     // nazov tabulky
     this.setTableName(colDef.getTableName(colIdx));
     // nazov stlpca DBtabulky
     this.setFieldName(colDef.getColumnName(colIdx));
     // datovy typ stlpca
//     this.setDataType(krn.getDBcolumnInfo(cn, colDef.getTableName(colIdx), 
//                       colDef.getColumnName(colIdx), "DataType"));
     this.setDataType(colDef.getColumnTypeName(colIdx));
     ////krn.OutPrintln(colDef.getColumnName(colIdx) + " .......  -> GRNDATTATYPE==" + dataType);  
     this.setGenericDataType(krn.getGenericDataType(this.getDataType()));
     krn.OutPrintln("EOC_columnDefinition-IDX==" + colIdx 
             + " COLNAM==" + colDef.getColumnName(colIdx)
             + " -> GRNGENERICDTT==" + genericDataType);  
     // nadpis stlpca
     this.setFieldLabel(colDef.getColumnLabel(colIdx));
     // dlzka udaja v characteroch
     // default hodnota
   //  colDef.
//     this.setDefaultValue(krn.getDBcolumnInfo(cn, colDef.getTableName(colIdx), 
  //                     colDef.getColumnName(colIdx), "DefaultValue"));
     try {
          ii = colDef.getPrecision(colIdx);
          if (ii > 50) {
              ii = 50;  // obmedzenie sirky stlpca
          }
     } catch (Exception ex) {
          ii = 10;
     }
     this.setFieldLength(ii);
     
     // dlzka desatinnej casti udaja v characteroch
     this.setNumDecimals(colDef.getScale(colIdx));

     this.setFormatString(getDefaultFormatString());
     if ((getTableName().length() > 0) && (getFieldName().length() > 0))
        this.setTooltip(myDBconn.getDbDriver().getDBcolumnInfo(myDBconn.getConn(), getTableName(), getFieldName(), "Comment"));
//     this.setTooltip(krn.getDBcolumnInfo(MyCn, getTableName(), getFieldName(), "Comment"));
     //this.setHidden(s1.toUpperCase().equals("HIDDEN"));
     settersAllowed = false;
   return true; 
} // initializeFromColDefString(String colDef)

public String getDefaultFormatString() {
    // skladame format string:
    String f = "";
    if (getFieldLength() == 0) return f;
    switch (getGenericDataType()) {
         case "decimal":
             //f = FnEaS.repeat("#", colInf.length  - colInf.decimals)
             //+ "." + FnEaS.repeat("#",colInf.decimals);
             int iLng = getFieldLength() - getNumDecimals();
             for (int i = 1; i <= iLng; i++) {
                 f = "#" + f;  
                 if ((i % 3) == 0) {
                     f = ' ' + f; // <nbsp> 
                 }
             }  
             f = f + "." + FnEaS.repeat("#",getNumDecimals());
             //colInf.notNullDefaultValue = "0.0";
             break;
         case "integer":
             //f = FnEaS.repeat("#", colInf.length);
             for (int i = 1; i <= getFieldLength(); i++) {
                  f = "#" + f;  
                  if ((i % 3) == 0) {
                      f = ' ' + f; // <nbsp> 
                  }
             }
             //colInf.notNullDefaultValue = "0";
             break;
         case "date":
             f = "##.##.####";
             //colInf.notNullDefaultValue = "01.01.0000";
             break;
         default:
             f = FnEaS.repeat("*",getFieldLength());
    } // switch (getGenericDataType()) {
    f = f.trim();
    this.setFormatString(f);
    ///krn.OutPrintln(col + "  FOORMATSTR:" + colInf.genericdatatype
    ///        + " IS:" + f);                
    return f;
}

public boolean initializeFromColDefString(String colDef) {
    if (colDef.equals("settersAllowed")) {
        settersAllowed = true;
        return true;
    }
    else settersAllowed = true; // povoli sa to pre interne pouzitie
            
   int numEntr = FnEaS.iNumEntries(colDef, "^");
   if (numEntr < 7) {
       /*
       krn.krnMsg(this, "e", "Definícia stlpca musí mať aspoň 7 častí,"
                      + " delených znakom ^.\n\n" + colDef,
                        "Chybná definície stľpca tabuľky");
               */
       Kernel.staticMsg("Definícia stlpca musí mať aspoň 7 častí,"
                      + " delených znakom ^.\n\n" + colDef);
       return false;
   }
            String s1;
            Integer ii;
            // extraktovanie udajov  z ColDefinition-retazca do vlastnosti ColumnObjektu
            for (int i = 1; i <= numEntr; i++) {
                s1 = FnEaS.sEntry(i, colDef, "^");
                if (i == 1) {
                    this.setOwnerName(s1);
                } // nazov databazy
                else if (i == 2) {
                    this.setTableName(s1);
                } // nazov tabulky
                else if (i == 3) {
                    this.setFieldName(s1);
                } // nazov stlpca DBtabulky
                else if (i == 4) {
                    this.setDataType(s1);
                } // datovy typ stlpca
                else if (i == 5) {
                    this.setDefaultValue(s1);
                } // default hodnota
                else if (i == 6) {
                    this.setFieldLabel(s1);
                } // nadpis stlpca
                else if (i == 7) { // dlzka udaja v characteroch
                    try {
                        ii = Integer.valueOf(s1);
                        if (ii > 50) {
                            ii = 50;
                        } // obmedzenie sirky stlpca
                    } catch (Exception ex) {
                        ii = 10;
                    }
                    this.setFieldLength(ii);
                } else if (i == 8) {  // dlzka desatinnej casti udaja v characteroch
                    try {
                        ii = Integer.valueOf(s1);
                    } catch (Exception ex) {
                        ii = 2;
                    }
                    this.setNumDecimals(ii);
                } else if (i == 9) {
                    this.setFormatString(s1);
                } else if (i == 10) {
                    this.setTooltip(s1);
                    this.setHeaderValue(this.getFieldLabel());
                } else if (i == 11) {
                    this.setHidden(s1.toUpperCase().equals("HIDDEN"));
                    this.setHeaderValue(this.getFieldLabel());
                }
            } // for (int i =  ...
            this.setGenericDataType(krn.getGenericDataType (this.getDataType()));   
   //createDefaultHeaderRenderer();
   //this.setCellRenderer(new myCellRenderer());
        settersAllowed = false; // zakaze sa to po internom pouziti
        return true; 
    } // initializeFromColDefString(String colDef)
    
    public void setHeaderValue(String sVal) {
        // setter je povoleny vzdy
        headerValue = sVal;
    } 
    public String getHeaderValue(String sVal) {
        return headerValue;
    } 

    public Kernel getKrn() {
        return krn;
    }

    public void setKrn(Kernel krn) {
        this.krn = krn;
    }

    public DBconnection getMyCn() {
        return myDBconn;
    }

    public void setMyCn(DBconnection MyCn) {
        myDBconn = MyCn;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        if (!settersAllowed) {
            krn.Message("Setter-metóda pre vlastnosť 'ownerName' je zakázaná.\n" 
                    + "nastavenie hodnoty na '" + ownerName + "' bolo odmietnuté.");
            return;
        }
        this.ownerName = ownerName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        if (!settersAllowed) {
            krn.Message("Setter-metóda pre vlastnosť 'tableName' je zakázaná.\n" 
                    + "nastavenie hodnoty na '" + tableName + "' bolo odmietnuté.");
            return;
        }
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        if (!settersAllowed) {
            krn.Message("Setter-metóda pre vlastnosť 'fieldName' je zakázaná.\n" 
                    + "nastavenie hodnoty na '" + fieldName + "' bolo odmietnuté.");
            return;
        }
        this.fieldName = fieldName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        if (!settersAllowed) {
            krn.Message("Setter-metóda pre vlastnosť 'dataType' je zakázaná.\n" 
                    + "nastavenie hodnoty na '" + dataType + "' bolo odmietnuté.");
            return;
        }
        this.dataType = dataType;
    }

    public String getGenericDataType() {
        return genericDataType;
    }

    public void setGenericDataType(String genericDataType) {
        if (!settersAllowed) {
            krn.Message("Setter-metóda pre vlastnosť 'genericDataType' je zakázaná.\n" 
                    + "nastavenie hodnoty na '" + genericDataType + "' bolo odmietnuté.");
            return;
        }
        this.genericDataType = genericDataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public Integer getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    public Integer getNumDecimals() {
        return numDecimals;
    }

    public void setNumDecimals(Integer numDecimals) {
        this.numDecimals = numDecimals;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public boolean getHidden() {
        return hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isSettersAllowed() {
        return settersAllowed;
    }

    public void setSettersAllowed(boolean settersAllowed) {
        this.settersAllowed = settersAllowed;
    }

    public eoc.dbdata.DBtableColumn transformToEOC_DBtableColumn() {
        eoc.dbdata.DBtableColumn tbc = 
            new DBtableColumn(krn,myDBconn,transformToColDefString());
        return tbc;
    }

    public String transformToColDefString() {
        //      dbname^tablename^fieldname^datatype^defaultvalue^fieldlabel^fieldlength^...
        //      numdecimals^formatstring^tooltyp^HIDDEN
        String s1;
//                if (i == 1) {
        s1 = this.getOwnerName();
//                else if (i == 2) {
        s1 =  s1 + "^" + this.getTableName();
//                else if (i == 3) {
        s1 =  s1 + "^" + this.getFieldName();
//                else if (i == 4) {
        s1 =  s1 + "^" + this.getDataType();
//                else if (i == 5) {
        s1 =  s1 + "^" + this.getDefaultValue();
//                else if (i == 6) {
        s1 =  s1 + "^" + this.getFieldLabel();
//                else if (i == 7) { // dlzka udaja v characteroch
        s1 =  s1 + "^" + this.getFieldLength();
//                } else if (i == 8) {  // dlzka desatinnej casti udaja v characteroch
        s1 =  s1 + this.getNumDecimals();
//                } else if (i == 9) {
        s1 =  s1 + this.getFormatString();
//                } else if (i == 10) {
        s1 =  s1 + this.getTooltip();
//                    this.setHeaderValue(this.getFieldLabel());
//                } else if (i == 11) {
        s1 =  s1 + this.getHidden();

        return s1;

    }
    public void setColumnClass(Class cls) {
        krn.OutPrintln("FIELD:" + fieldName + " .. Setting column-class:" + columnClass);
        columnClass = cls;
        this.setColumnClass(cls);
    } 
    
    public Class getColumnClass() {
        krn.OutPrintln("FIELD:" + fieldName + " .. Returning column-class:" + columnClass);
        return columnClass;
    }}
