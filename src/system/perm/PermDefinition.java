/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package system.perm;

import eoc.database.DBconnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.perm.Permd;
import system.Kernel;
/**
 *
 * @author rvanya
 */
public class PermDefinition {
    private String  wrapperAppName;
    private Kernel  krn;
    private Permd   permd;  // demon pristupovych prav
    private DBconnection MyCn;    
    private String  permObjType; // GROUP/USER/... ?
    private String  permUserType;
    private String  permUserName;
    private String  permLabel;
    private String  permProgObject;
    private boolean permNew    = false; // default je false
    private boolean permUpdate = false; // default je false
    private boolean permDelete = false; // default je false
    private boolean permPrint  = false; // default je false
    private String  permRestriction; // !!! toto by malo byt nieco ako SqlQry object !!!
    private Integer myUserPermDefinitionRowID = null;
    private Integer mySuperPermDefinitionRowID = null;
    private PermDefinition parentPermDefinition;

    public PermDefinition(String usrType, String usrName, String appName
                          , Kernel kr, Permd pd, DBconnection cn
                          , String prmObjType, String pLbl
                          ,PermDefinition parentPermDef, String pPObj, String pDF) {
        wrapperAppName = appName; // nazov celej aplikacie xyz z nazvu xyz.jar
        krn            = kr;
        permd          = pd;
        MyCn           = cn;
        permObjType    = prmObjType;
        permUserType   = usrType; // GROUP/USER
        permUserName   = usrName;
        permLabel      = pLbl;
        if (parentPermDef==null) {
            // "SYSTEM_****" ma VZDY null-parenta ! (je najvyssim objektom)
             if (!pPObj.startsWith("SYSTEM_"))
                krn.OutPrintln("PermDefinition_OBJECT_CONSTRUCTOR WARNING: Permission object: "
                               + pPObj + " parentPermissionObject is NULL !");
        }
        setPrimaryPermProgObject(parentPermDef, pPObj);

        pDF = pDF.toUpperCase(); // preistotu
        permNew    = pDF.contains("N");
        permUpdate = pDF.contains("U");
        permDelete = pDF.contains("D");
        permPrint  = pDF.contains("P");
        
    };
    
    public String getPermLabel() {
        return permLabel;
    }
/* readonly permLabel
    public void setPermLabel(String permLabel) {
        this.permLabel = permLabel;
    }
*/
    public String getPermProgObject() {
        return permProgObject;
    }

    public String getPermObjType() {
        return permObjType;
    }

    public String getPermUserType() {
        return permUserType;
    }

    public String getPermUserName() {
        return permUserName;
    }

    /*
    public void setParentPermProgObject(String parentPermProgObj) {
        this.parentPermProgObject = parentPermProgObj;
    }
*/    
    public void setParentPermDefinitionObject(PermDefinition parentPermDef) {
        parentPermDefinition = parentPermDef;
        if (parentPermDef != null) {
            String sqlStm = "select count(*) as retval from eas_permobj "
                          + "where id_eas_permobj = " + parentPermDef.mySuperPermDefinitionRowID;
            Object o = krn.SQLQ_getQueryAsValue(MyCn.getConn(), sqlStm, true);
            int i = Integer.parseInt(o.toString());
            if (i == 0 && (!parentPermDef.permLabel.startsWith("MODUL:"))) {
                krn.OutPrintln("PermDefinition_OBJECT WARNING: Permission object: " + parentPermDef.permLabel //+ "\n" 
                    + "  Parent permission object with ID: "
                    + parentPermDef.mySuperPermDefinitionRowID + " not exist !");
/*
                krn.krnMsg("W", "Permission object: " + parentPermDef.permLabel + "\n" 
                    + "Parent permission object with ID: "
                    + parentPermDef.mySuperPermDefinitionRowID + " not exist !", "VAROVANIE");
                */
                // asi by tu bolo treba nabehnut do chyby !
////                parentPermProgObjectID = parentPermProgObjID; 
            }
        }
////        else parentPermProgObjectID = parentPermProgObjID; // null-id !!!
    }
    public PermDefinition getParentPermDefinition() {
        return parentPermDefinition;
    }
/*    
    public Integer getParentPermProgObjectID() {
        return this.parentPermProgObjectID;
    }
 */   
    
    private void setPrimaryPermProgObject(PermDefinition parentPermDef, String permProgObject) {
        if (permd==null) {
            krn.Message("E", "Démon prístupových práv pre '" 
                    + permProgObject
                    + "' neexistuje.", "SYSTEM: Permission daemon error");
        }
        setParentPermDefinitionObject(parentPermDef);
        this.permProgObject = permProgObject;
        validatePermDefinition();
        
    }
    
    public void validatePermDefinition() {
        // presunute do permd -> System.out.println("VALIDATING permDefinition:  " + parentPermProgObject + " / " + permProgObject);
        if (this.permUserType.equals("SUPER") || this.permUserName.equals("SUPER"))
            permd.validateSuper_PermDefinition(this);
        else 
            if (this.permUserType.equals("USER"))
           permd.validateUserPermDefinition(this);
        
    }

    public boolean isPermNew() {
        return permNew;
    }

    public void setPermNew(boolean permNew) {
        this.permNew = permNew;
    }

    public boolean isPermUpdate() {
        return permUpdate;
    }

    public void setPermUpdate(boolean permUpdate) {
        this.permUpdate = permUpdate;
    }

    public boolean isPermDelete() {
        return permDelete;
    }

    public void setPermDelete(boolean permDelete) {
        this.permDelete = permDelete;
    }

    public boolean isPermPrint() {
        return permPrint;
    }

    public void setPermPrint(boolean permPrint) {
        this.permPrint = permPrint;
    }

    public String getPermRestriction() {
        return permRestriction;
    }

    public void setPermRestriction(String permRestriction) {
        this.permRestriction = permRestriction;
    }

    public int getUserPermDefinitionRowID() {
        //kookotina if (permProgObjectID == null) permProgObjectID = -1;
        return myUserPermDefinitionRowID;
    }
// NEDE
//    toto tu nema byt, rowid by mal object pre seba zistit a IBA pouzivat
//            napisat test super-perm-definicii na zaklade vytvaraneho Menu pri rozbehu, pokial
//                    verzia alebo akysi CRC kod menu nesuhlasi
    public int getSuperPermDefinitionRowID() {
        //kookotina if (permProgObjectID == null) permProgObjectID = -1;
        // pokus o ziskanie ID u super-definicie z tabulky eas_permobj
        if (mySuperPermDefinitionRowID==null) {
            String sqlStm = "select id_eas_permobj as retval from eas_permobj "
                          + "where c_appname = '" + wrapperAppName + "'"
                          + " and c_permobject = '" + permProgObject + "'" 
                    ;
            Object o = krn.SQLQ_getQueryAsValue(MyCn.getConn(), sqlStm, true); 
            mySuperPermDefinitionRowID = Integer.parseInt(o.toString());
        }
        if (mySuperPermDefinitionRowID==null)
        System.out.println(this.getPermLabel() + " >>> getSuperPermDefinitionID()_returnNULL===" 
                + (mySuperPermDefinitionRowID==null));
        return mySuperPermDefinitionRowID;
    }
    
    public String getWrapperAppName() {
        return wrapperAppName;
    }
    
    public void initForUser(String usrType, String usrName) {
        permUserType   = usrType; // GROUP/USER
        permUserName   = usrName;
        // super-user je automaticky prepnuty na plne prava 
        if (permd.isSuperUser(usrName)) {
            permNew    = true;
            permUpdate = true;
            permDelete = true;
            permPrint  = true;
            return;
        }

/*
        System.out.println("WWWWWWWWWW- PermDefinition.initForUser(" 
                        + usrType + "\\" + usrName + "): Label:"
                + getPermLabel()
                + "  PermProgObject:" + getPermProgObject()
                + "  Parent node::::" + ((parentPermDefinition != null) 
                          ? parentPermDefinition.getPermProgObject() : "<NULL>")
                + "\n     ---PermDefinition:" 
                  + getPermObjType() + "/" 
                  + getPermUserType() + "/" 
                  + getPermUserName());
*/
//        setPermProgObject(parentPermDef, pPObj);
        String pDF = "";
        String sqlSelStm = "select c_permstr as retval from eas_perms where "
                + "c_appname = '" + wrapperAppName + "'"
//                + "and c_typpermobj = '" + permObjType + "'"
                + "and c_usertype = '" + permUserType + "'"
                + "and c_uname = '" + permUserName + "'"
                + "and c_permobject = '" + permProgObject + "'";
        Statement stm;
        ResultSet rst = null;
        Object o = null;
        try {
             // Priklad iebnuteho drivera SQlanywhere
             // OLD --> stm = MyCn.createStatement();
             // MyCn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
             stm = MyCn.getConn().createStatement();
             ////System.out.println("----------SSSSUUUPDOOOPICE:" + (sqlSelStm==null));
             rst = stm.executeQuery(sqlSelStm);
             if (rst.next()) {
                 o = rst.getObject("retval");
             }
             stm.close();
             rst.close();
             MyCn.getConn().commit();
//             stm.close();
             ////getStatQuery_stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Object o = Kernel.SQL_getQueryAsValue(MyCn, sqlSelStm, true);
        /*
        try {
            MyCn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(PermDefinition.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        pDF = ((o!=null) ? o.toString() : "");
        pDF = pDF.toUpperCase(); // preistotu
        ////pDF = "NP"; // pokusne
        permNew    = pDF.contains("N");
        permUpdate = pDF.contains("U");
        permDelete = pDF.contains("D");
        permPrint  = pDF.contains("P");
    }
    
    public boolean commit() { 
        String  pDefs = "";
        String  sId   = "0";
        Integer iId   = 0;
        String sqlSelStm = "select id_eas_perms as retval from eas_perms where "
                + "c_appname = '" + wrapperAppName + "'"
//                + "and c_typpermobj = '" + permObjType + "'"
                + "and c_usertype = '" + permUserType + "'"
                + "and c_uname = '" + permUserName + "'"
                + "and c_permobject = '" + permProgObject + "'";
        Statement stm;
        ResultSet rst = null;
        Object o = null;
        try {
             stm = MyCn.getConn().createStatement();
             rst = stm.executeQuery(sqlSelStm);
             if (rst.next()) {
                 o = rst.getObject("retval");
             }
             rst.close();
//             stm.close();
             ////getStatQuery_stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        sId = ((o!=null) ? o.toString() : "0");
        iId = Integer.parseInt(sId);
        String sqlUpdStm = "";
        pDefs = "" + (permNew?"N":"")
                   + (permUpdate?"U":"")
                   + (permDelete?"D":"")
                   + (permPrint?"P":"");

        if (iId == 0) { // vlozi sa nova veta do tabulky
        sqlUpdStm = "insert into eas_perms ("
                + "c_appname,c_usertype,c_uname,c_permobject,c_permstr,c_zapisal,c_zmenil,id_eas_permobj)"
                + " values ("
                + "'" + wrapperAppName + "',"
                + "'" + permUserType + "',"
                + "'" + permUserName + "',"
                + "'" + permProgObject + "',"
                + "'" + pDefs + "','" + krn.getUpdateStamp() + "','" + krn.getUpdateStamp() + "',"
                + mySuperPermDefinitionRowID
                + ")";
        }
        else { // upravi sa existujuca veta v tabulke
        sqlUpdStm = "update eas_perms set "
                + "c_appname = '" + wrapperAppName + "',"
                + "c_usertype = '" + permUserType + "',"
                + "c_uname = '" + permUserName + "',"
                + "c_permobject = '" + permProgObject + "',"
                + "c_permstr = '" + pDefs + "',"
                + "c_zmenil = '" + krn.getUpdateStamp() + "'"
                + " where id_eas_perms = " + iId;
        }
        try {
             stm = MyCn.getConn().createStatement();
             stm.executeUpdate(sqlUpdStm);
             MyCn.getConn().commit();
             stm.close();
//             stm.close();
             ////getStatQuery_stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public void setUserPermDefinitionRowID(int i) {
        myUserPermDefinitionRowID = i; 
    }
/*
    public Integer getUserPermDefinitionRowID() {
        return myUserPermDefinitionRowID; 
    }
*/    
    public void setSuperPermDefinitionRowID(int i) {
        mySuperPermDefinitionRowID = i; 
    }

    public String getPermStr() {
        String s = "";
        if (permNew) s = s + "N";
        if (permUpdate) s = s + "U";
        if (permDelete) s = s + "D";
        if (permPrint) s = s + "P";
        return s;        
    }
/*
    public int getSuperPermDefinitionRowID() {
        return mySuperPermDefinitionRowID; 
    }
 */   

}
