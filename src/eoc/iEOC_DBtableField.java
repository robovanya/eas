/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc;

import eoc.database.DBconnection;
import java.sql.Connection;

/**
 *
 * @author rvanya
 */
public interface iEOC_DBtableField {
    /*
     * Obsahuje funkcie, potrebne pre komunikaciu s implementujucim objektom
     * v oblasti datovych a inych udajov suvisiacich s databazovym stlpcom,
     * ktory dany objekt reprezentuje na obrazovke
     * (cez funkcie vnoreneho objektu,typu EOC_DBtableField)
     */
    public eoc.dbdrv.DBtableField getDBtableField();
    
    public void initialize(DBconnection cnn);
    
    public void setText(String txt);

    public boolean isInitialized();

    public String getText();
    
    public String evaluateDbScreenValueDefinition();
    
}
