/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.grafikon;

import eoc.database.DBconnection;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class EOC_Gdimension extends EOC_graf {

    public EOC_Gdimension(Kernel kr, DBconnection cn, 
            String cGrfName, String cGrfLabel, String cGrfTooltip, 
            boolean bIsDimension, /*EOC_BrwGrafikon brw,*/ EOC_graf parentGrf, 
            eoc.dbdata.DBtableColumn[] oRowHeaders,Object ooRowData[][],
            String sPrimaryTableName,
            String sPrimaryKeyFieldName, boolean bHiddenKey
            /*,JComboBox jcbDims */) {
        super(kr, cn, cGrfName, cGrfLabel, cGrfTooltip,
            true, /*brw,*/ parentGrf, oRowHeaders, ooRowData,
            sPrimaryTableName, sPrimaryKeyFieldName, bHiddenKey/*, jcbDims*/);
        isDimension = true;
    }
    
}
