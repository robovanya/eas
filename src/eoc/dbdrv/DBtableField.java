/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.dbdrv;

import eoc.database.DBconnection;
import java.awt.Component;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class DBtableField {
    public Component parentComponent;
    public eoc.dbdrv.DBcolumnInfo dbColumnInfo;
    public String parentSimpleClassName;
    public String stringValue;
    public String creationErrors = "";
    public boolean bCreationErrors;
    public Object valueObjectForWrite;
    // Bezne formatovanie datumu pre zapis do DB
    private DateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DBtableField(Component dbc, DBconnection cnn, String sOwner, String sTable, String sColumn) {
        /////System.out.println("DBtableFieldConstructor:" + cnn.toString() + "\n FOR:" + dbc.toString());
        parentComponent = dbc;
        dbColumnInfo = new eoc.dbdrv.DBcolumnInfo(cnn, sOwner, sTable, sColumn);
       ///// System.out.println("DBTCOLINFOOOCREATON" + (dbColumnInfo == null));
        // pripadne chyby pri vytvarani objektu 
        // (vatsinou chybny nazov ownera, tabulky alebo stlpca)
        addError(dbColumnInfo.creationErrors);
        // trieda rodicovskeho/parent objektu/widgetu
        Class cl;
        cl = dbc.getClass();      
        // meno triedy rodicovskeho/parent objektu/widgetu
        parentSimpleClassName = cl.getSimpleName(); 
    }
   
    public void addError(String errMsg) {
        creationErrors = creationErrors + (creationErrors.length()>0 ? "#" : "") + errMsg;
        bCreationErrors = creationErrors.length() > 0;
    }
    
    public String getFullColumnName() {
        if (dbColumnInfo==null)
           return ".";
        else 
           return dbColumnInfo.getFullColumnName();
    }

    public void setDBcolumnInfo(eoc.dbdrv.DBcolumnInfo colInf) {
        dbColumnInfo    = colInf;
    }
    
    public void setDBfieldValueObjectForWrite(Object valObj) {
        //// krn.OutPrintln("setDBfieldValueObjectttt caller=" +easys.FnEaS.getCallerMethodName());
        valueObjectForWrite = valObj;
    }
    
    public String getValueForWriteAsString(String stringApostroph) {
        ////preco nevrati hodnotu, ked je objekt typu DBtextArea ???
        // ziskanie hodnotu value-objektu
        /*
        System.out.println("getValueAsString() in: " 
                + parentComponent.getClass().getSimpleName() 
                + " -- " + this.getFullColumnName()
                + " = " 
                + (valueObjectForWrite==null ? "<NULL>": valueObjectForWrite.toString()));
*/
                
     //  System.out.println("getValueForWriteAsString()-->:" +  dbColumnInfo.columnname + " -> valueObjectForWrite===" + valueObjectForWrite
     //                       + " is null: " + (valueObjectForWrite==null));        
        if (valueObjectForWrite==null) return null;
        String strVal = "";
        if (this.dbColumnInfo.genericdatatype.equals("date")) {
            //if (valueObjectForWrite.toString())
            strVal = valueObjectForWrite.toString().trim();
            // detekovanie "-  -" -- prazdny datum
            if (strVal.startsWith("-")) return null;
            if (strVal.startsWith(".")) return null;
            if (strVal.startsWith("_")) return null;
            if (!strVal.contains("-")) {
                // pouzitie sibnutej metody na ziskanie datumu - 2017-05-25
                Calendar c = FnEaS.getStringAsCalendar(strVal, null);
            strVal = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1)
                   + "-" + c.get(Calendar.DAY_OF_MONTH);
            // 2017-05-25  strVal = sqlDateFormat.format(valueObjectForWrite.toString());
            //System.out.println("getValueForWriteAsString()1 = " + strVal);
            //strVal = sqlDateFormat.format(strVal);
            
            //System.out.println("getValueForWriteAsString()2 = " + strVal);
            strVal = stringApostroph + strVal + stringApostroph;
            //System.out.println("getValueForWriteAsString()3 = " + strVal);
            }
            else strVal = stringApostroph + strVal + stringApostroph;
            //System.out.println("getValueForWriteAsString()4 = " + strVal);
        }
        else {
             strVal = valueObjectForWrite.toString().trim();
             if (this.dbColumnInfo.genericdatatype.equals("string"))
                 strVal = stringApostroph + strVal + stringApostroph;
        } 
                
        return strVal;
    }

}
