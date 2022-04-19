/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.dbdrv;

import eoc.database.DBconnection;
import java.sql.Connection;

/**
 *
 * @author rvanya
 */
public class DBcolumnInfo {

public String tableowner      = "";
public String tablename       = "";
public String columnname      = "";
public String realdatatype    = "";
public String genericdatatype = "";
public boolean nullallowed    = false;
public Integer length         = 10;
public Integer decimals       = 10;
public String comment         = "";
public String formatString    = "";
public String defaultValue    = "";
public String notNullDefaultValue  = "";
public String creationErrors          = "";

public DBcolumnInfo(DBconnection cnn, String sOwner, String sTable, String sColumn) {
    creationErrors = cnn.getDbDriver().fillDBcolumnInfo(this, cnn.getConn(), sOwner, sTable, sColumn);
}

public String getNotNullDefaultValue() {
    return notNullDefaultValue;
}

public String getFullColumnName() {
    if (tableowner.equals("") || tableowner == null) 
        return tablename + "." + columnname;
    else
        return tableowner + "." + tablename + "." + columnname;
}

public String getFullTableName() {
    if (tableowner.equals("") || tableowner == null) 
        return tablename;
    else
        return tableowner + "." + tablename;
}

public String getCreationRerrors() {
    return creationErrors;
}
}
