/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package system.perm;

import eoc.database.DBconnection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import system.modul.Menu;
import system.modul.MenuItem;
import system.modul.Modul;

/**
 *
 * @author rvanya
 */
public class Permd {
    
    private String         wrapperAppName;
    private system.Kernel  krn;
    private DBconnection     DBcnWork;
    private DBconnection     DBcnOLAP;
    private DBconnection     DBcnWWW;
    private String         currentUser = "default";
    private String         currentUserDescript = "default_description";
    private PermDefinition rootSuperPermDefinition;
    private PermDefinition rootUserPermDefinition;
    private String         tmStamp; // cas inicializacie vrstvy CRON
    private boolean        bSuperUser = false; // default
    
    public void initialize() {
        //krn.dskMsg("Inicializácia vrstvy Crond ... OK");
        wrapperAppName = krn.getWrapperAppName();

       //// System.out.println("permd.initialize()_begin");

        tmStamp = krn.getUpdateStamp("Permd"); // ziskanie casu inicializacie vrstvy CRON
//DEBUG System.out.println("A-BUSTEST");
        // zalozenie zakladnej skupiny (NESKUPINA - <bez_skupiny>, nieco ako PUBLIC)
        boolean bBaseUGrpOK = testBaseUserGroup();
//DEBUG System.out.println("B-BUSTEST");

        boolean bBaseUserOK = testBaseUser();
//DEBUG System.out.println("C-BUSTEST");

        boolean bBasePObjOK = testBasePermObjs();
//DEBUG System.out.println("D-BUSTEST");
        
        if (bBaseUGrpOK && bBasePObjOK && bBaseUserOK) 
           krn.WriteEventToLog("Inicializácia vrstvy Perm ... OK\n"); 
        else {
        // Odchytenie pripadnej chyby v testXY funkciach
        if (!bBaseUGrpOK)
           krn.WriteEventToLog("Inicializácia vrstvy Perm ... testBaseUserGroup() ... FAILED\n"); 
        if (!bBasePObjOK)
           krn.WriteEventToLog("Inicializácia vrstvy Perm ... testBasePermObjs() ... FAILED\n"); 
        }
    }

   private boolean testBaseUserGroup() {
        // zalozenie zakladnej skupiny (NESKUPINA - <bez_skupiny>, nieco ako PUBLIC)
       /*
set term !;
execute block as
DECLARE i integer;
begin
  i = (select count(*) from eas_usrgrp where c_group = '<bez_skupiny>');
IF ( i = 0 or i = null ) THEN
begin
insert into eas_usrgrp (c_group, c_popis, c_zapisal, c_zmenil) 
  values ('<bez_skupiny>','Užívateľia tejto skupiny musia mať nastavené vlastné práva','2017-10-20 12:01 Permd','2017-10-20 12:01 Permd');
END
end!
set term ;!
       */
/*
DO $$
DECLARE icount integer default 0;
BEGIN
icount = (select count(*) from eas.eas_usrgrp where c_group = '<bez_skupiny>');
IF icount = 0 or icount = null THEN
insert into eas.eas_usrgrp (c_group, c_popis, c_zapisal, c_zmenil) values ('<bez_skupiny>','Užívateľia tejto skupiny musia mať nastavené vlastné práva','2022-03-07 08:38 Permd','2022-03-07 08:38 Permd');
END IF;
END ;$$       
*/       
       String dbtype = DBcnWork.getDbDriver().getDBtype(DBcnWork.getConn());
//QQQ       System.out.println("testBaseUserGroup()  DB_TYYYPEEEE:" + DBcnWork.getDbDriver().getDBtype(DBcnWork.getConn()));
        String cmd = 
        (dbtype.startsWith("POSTGRES")?"DO $$\n DECLARE i integer;\n":"") +
        "begin\n" +
        (dbtype.startsWith("SYBASE")?"DECLARE i integer;\n SET ":"") +
        "i = (select count(*) from eas.eas_usrgrp where c_group = '<bez_skupiny>');\n" +
        "IF i = 0 or i = null THEN\n" +
        "insert into eas.eas_usrgrp (c_group, c_popis, c_zapisal, c_zmenil) values ('<bez_skupiny>'," +
        "'Užívateľia tejto skupiny musia mať nastavené vlastné práva','" +
                tmStamp + "','" + tmStamp + "');\n" + "END IF;\n" + "end;" +
        (dbtype.startsWith("POSTGRES")?"$$\n":"\n");
        
//QQQSystem.out.println("BEGGINNN:\n" + cmd);
        // zapis zakladnej skupiny uzivatelov (t.j. NESKUPINY :-) )
        return DBcnWork.getDbDriver().SQL_callSqlStatement(DBcnWork.getConn(), cmd, true);
        
   }

   private boolean testBaseUser() {
        // zalozenie zakladneho admin-uzivatela
        String dbtype = DBcnWork.getDbDriver().getDBtype(DBcnWork.getConn());
//QQQ       System.out.println("testBaseUser() DB_TYYYPEEEE:" + DBcnWork.getDbDriver().getDBtype(DBcnWork.getConn()));
        String cmd = 
        (dbtype.startsWith("POSTGRES")?"DO $$\n DECLARE i integer;\n":"") +
        "begin\n" +
        (dbtype.startsWith("SYBASE")?"DECLARE i integer;\n SET ":"") +
        "i = (select count(*) from eas.eas_users where c_user = '" + wrapperAppName + "');\n" +
        "IF i = 0 or i = null THEN\n" +
        "insert into eas.eas_users (c_user, c_popis, c_zapisal, c_zmenil,b_sysadmin) values ('" + 
        wrapperAppName + "'," +
        "'Administrátor systému " + wrapperAppName + "','" +
                tmStamp + "','" + tmStamp + "',1);\n" + "END IF;\n" + "end;" +
        (dbtype.startsWith("POSTGRES")?"$$\n":"\n");

        // zapis zakladnej skupiny uzivatelov (t.j. NESKUPINY :-) )
        return DBcnWork.getDbDriver().SQL_callSqlStatement(DBcnWork.getConn(), cmd, true);
        
   }

   /**
    * Test existencie SUPER permission objektu level-u root 
    * pre aktuálnu applikáciu (krn.getWrapperAppName())
    *
    * @return true, keď objekt existuje, alebo sa ho podari založiť
    */
   public boolean testBasePermObjs() {
       Object o; 
       Integer i;
       // test existencie SUPER objektu level-u root pre aktualnu applikaciu
       String cmdSel = "select " //count(*) 
                       + "id_eas_permobj as retval from eas_permobj where "
                  + "c_appname = '" + krn.getWrapperAppName() + "' and " 
                  + "c_typpermobj = 'PROGRAM' and c_permobject = 'SYSTEM_" 
                  + krn.getWrapperAppName() + "'";
       o = krn.SQLQ_getQueryAsValue(DBcnWork.getConn(), cmdSel, true);
       i = ((o!=null) ? Integer.parseInt(o.toString()) : 0);
       
       // zapis root-uzla objektov pristupovych prav, pokial neexistuje
       if (i == 0) { // pridanie root-level perm-objektu
           String cmdIns = "INSERT INTO eas_permobj (c_typpermobj, c_permobject,"
               + " c_popis,id_parent_permobj,c_defaultperm, c_zapisal, c_zmenil, c_appname)"
               + " VALUES ('PROGRAM','SYSTEM_" + krn.getWrapperAppName() 
               + "','Prístupové práva pre SYSTÉM:" + krn.getWrapperAppName() + "',"
               + "null,'NUDP','" + krn.getUpdateStamp() + "','"
               + krn.getUpdateStamp() + "','" + krn.getWrapperAppName() + "')";

           krn.SQLQ_callSqlStatement(DBcnWork.getConn(), cmdIns, true);
           
           // test existencie SUPER objektu level-u root pre aktualnu applikaciu
           o = krn.SQLQ_getQueryAsValue(DBcnWork.getConn(), cmdSel, true);
           i = Integer.parseInt(o.toString());
           
           if (i == 0) // Problem s root-level perm-objektom !!!
               System.err.println("permd: Problém s vytvorením root-level permission objektu !!!");
           else  // Uspesne vytvorenie root-level perm-objekt-u
               System.out.println("permd: Vytvorenie root-level permission-objektu " 
                      + " pre systém: " + krn.getWrapperAppName() + " .. OK\n");
       }
       if (i > 0) {
           // root nema parent-permDefinition !
           rootSuperPermDefinition = createSuperPermDefinition(null, i);
           return true;
       }
       else return false;
       
   }

   public PermDefinition createSuperPermDefinition(PermDefinition parPermDef, Integer permobjId) {
      //// System.out.println(">>>>>>>>>  createSuperPermDefinition():::" + permobjId);
       PermDefinition suprPrmDef = null;
       String cmd = "select * from eas_permobj where id_eas_permobj = " + permobjId;
/*                + "c_typpermobj = 'PROGRAM' and c_permobject = 'SYSTEM_" 
                + krn.getWrapperAppName() + "' and c_appname = '" + krn.getWrapperAppName() + "'";
  */
       ResultSet rs = krn.SQLQ_getQueryResultSet(DBcnWork.getConn(), cmd);
        try {
            if (rs.next()) {
               suprPrmDef = new PermDefinition("USER","SUPER", rs.getString("c_appname")
                   ,krn,this, DBcnWork,rs.getString("c_typpermobj")
                   ,rs.getString("c_popis")
                   ,parPermDef,rs.getString("c_permobject")
                   ,rs.getString("c_defaultperm"));
               suprPrmDef.setSuperPermDefinitionRowID(permobjId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Permd.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       return suprPrmDef;
   }

   public void setKernel(system.Kernel k) {
       krn = k;
      // setConnections(krn.getCnWork(), krn.getCnOLAP(), krn.getCnWWW());
   }

    public void setDbConnections(DBconnection cWrk, DBconnection cOla, DBconnection cWw) 
        throws FileNotFoundException, IOException, SQLException {
        this.DBcnWork = cWrk;
        this.DBcnOLAP = cOla;
        this.DBcnWWW  = cWw;
     }
/*    
   public void setConnections (Connection cWo, Connection cOL, Connection cWW) {
        CnWork = cWo;
        CnOLAP = cOL;
        CnWWW  = cWW;
   }
*/
   public void setCurrentUser(String userName) {
       System.out.println("permd: setting user name to " + userName);
       currentUser = userName;
       
           bSuperUser = isSuperUser(currentUser);
       
       System.out.println("permd: loading permission-cache for " + userName 
               + (bSuperUser ? " SUPER-USER" : "") + " ... OK");
       
   }

   public boolean isSuperUser(String usrName) {
       boolean bIsSuper = false;
       String sel = "select b_sysadmin as retval from eas_users where c_user = '" + usrName + "'";
       Object o = null;
       try {
           o = krn.SQLQ_getQueryAsValue(DBcnWork.getConn(), sel, true);
       }
       catch (Exception ex) {
           o = null;
       }
       Integer i = 0;
       if (o != null) i = Integer.parseInt(o.toString());
       bIsSuper = (i > 0);
       return bIsSuper;
       
   } 
   public String getCurrentUser() {
       return currentUser;
   }
   
   public Integer getCurrentUserID() {
       String sql = "SELECT id_eas_users as retVal FROM eas_users " 
                  + "WHERE c_user = '" + currentUser + "'";
       Object o = krn.SQLQ_getQueryValue(DBcnWork.getConn(), sql, "retVal");
       Integer i = Integer.parseInt(o.toString());
       return i;
   }
   
   public void permUpdate(boolean bPermUpdating) {
    
   }
   
public void buildMenuPermTreeModel(Pnl_usrperms pnl, JTree myTree, DefaultTreeModel treeModel, DefaultMutableTreeNode rootNode) {
    ArrayList<Modul> EOC_xmodules;
    EOC_xmodules = krn.getEOC_xmodules();
    
    int iIdx = 0;
   // Pridanie struktur menu-objektov z menu-barov modulov EaSys 
   for (Modul mdl : EOC_xmodules) {
       /* vytvorenie uzla pre aktualny modul 
        *
        * parentom modulov je parentProgObject SYSTEM, 
        * teda PermDefinition - objekt: rootSuperPermDefinition.
        * Dalej uz sa dedi PermDefinition-objekt nadradeneho uzla stromu
        ******************************************************************/
       PermDefinition pDefModul;
       pDefModul = new PermDefinition("USER","SUPER",wrapperAppName, krn, this, DBcnWork, "MODUL"
                 ,"MODUL:" + mdl.sMDL, rootSuperPermDefinition, "MODUL_" + mdl.sMDL,"");
       PermTreeNode currModulTree;
       currModulTree = new PermTreeNode(pnl, myTree, pDefModul);
       treeModel.insertNodeInto(currModulTree,rootNode, iIdx);
       iIdx++;
       JMenuBar jmb = mdl.mbar_MDL;
       int mCnt = jmb.getMenuCount();
       for (int imn = 0; imn < mCnt; imn++) {
           JMenu jmn;
           jmn = jmb.getMenu(imn);
           // parentom menu-objektov modulu je parentProgObject "MODUL_" + mdl.sMDL
           PermDefinition pDefMenu = null;
           System.out.println("aaa");
           try {
           pDefMenu = new PermDefinition("USER","SUPER", wrapperAppName, krn, this, DBcnWork, "MENU"
                        ,"MENU:" + jmn.getText(), pDefModul, "MENU_" + jmn.getText(),"");
           }
               catch (Exception ex) {
                            Logger.getLogger(Permd.class.getName()).log(Level.SEVERE, null, ex);
       
                       }
           System.out.println("bbb");
           PermTreeNode currMenuTree;
           currMenuTree = new PermTreeNode(pnl, myTree, pDefMenu);
           currModulTree.add(currMenuTree);
           if (jmn==null) continue;

           int iCnt = jmn.getItemCount();
           for (int i = 0; i < iCnt; i++) {
               MenuItem itm = (MenuItem) jmn.getItem(i);
               if (itm==null) continue;
               PermTreeNode currMenuItemTree;
               PermDefinition pDefMenuItem = null;
               // itm by mal mat vytvoreny PermDefinition-object pocas
               // vytvoreni menu
               
               pDefMenuItem = itm.getSuperPermDefinition();
               pDefMenuItem.setParentPermDefinitionObject(pDefMenu);
               currMenuItemTree = new PermTreeNode(pnl, myTree, pDefMenuItem);
               currMenuTree.add(currMenuItemTree);
           }
       }
   }
    
}


public void validateSuper_PermDefinition(PermDefinition permDf) {
    String  progObj       = permDf.getPermProgObject();  
    String  typObj        = permDf.getPermObjType();  
    PermDefinition parentPermDef = permDf.getParentPermDefinition();
    String pID = null;
    if (parentPermDef != null) {
        Integer parentID = permDf.getParentPermDefinition().getSuperPermDefinitionRowID();
        pID = parentID.toString();
    }
    String sqlSelStm = "select id_eas_permobj "// count(*) "
                  + "as retval from eas_permobj where "
                  + "c_appname = '" + permDf.getWrapperAppName() 
                  + "' and " + ((pID!=null)?"id_parent_permobj = " + pID:"id_parent_permobj is null")
                  + " and c_permobject = '" + progObj + "'"
                  + "and c_typpermobj = '" + typObj + "'";
    Object o;
    Integer i;
    o = krn.SQLQ_getQueryAsValue(DBcnWork.getConn(), sqlSelStm, true);
    // System.out.println(o);
    i = ((o!=null)?Integer.parseInt(o.toString()):0);
////    System.out.println("permd: VALIDATING permDefinition:  " + permDf.getParentPermProgObject()
////            + " / " + permDf.getPermProgObject() + "  SCORE:" + i);
    // pridanie neexistujuceho perm-objektu
//060114    System.out.println("SSSU> validate_SuperPermDefinition(permDf) eas_permobj:" + progObj 
//060414            + " for:" + typObj);
    if (i==0) {
       String sqlInsStm = "INSERT INTO eas_permobj (c_typpermobj, c_permobject,"
             + " c_popis,id_parent_permobj,c_defaultperm, c_zapisal, c_zmenil, c_appname)"
             + " VALUES ('" + typObj + "','" + progObj
             + "','" + permDf.getPermLabel().replace("'","''") + "',"
             + pID + ",'NUDP','" + krn.getUpdateStamp() + "','"
             + krn.getUpdateStamp() + "','" +  permDf.getWrapperAppName() + "');";
   // System.out.println("permd: VALIDATING superPermDefinition INSERT_STATEMENT:\n" + sqlSelStm + "\n"+ sqlInsStm);
       krn.SQLQ_callSqlStatement(DBcnWork.getConn(), sqlInsStm, true);
       o = krn.SQLQ_getQueryAsValue(DBcnWork.getConn(), sqlSelStm, true);
       i = Integer.parseInt(o.toString());
    }
    permDf.setSuperPermDefinitionRowID(i);
}


public void validateUserPermDefinition(PermDefinition permDf) {
    String  progObj       = permDf.getPermProgObject();  
    String  typObj        = permDf.getPermObjType();  
    PermDefinition parentPermDef = permDf.getParentPermDefinition();
    String pID = null;
    if (parentPermDef != null) {
        Integer parentID = parentPermDef.getSuperPermDefinitionRowID();
        if (parentID != null)
            pID = parentID.toString();
    }
    // hladanie uzivatelskeho zapisu 
    /*
    String sqlSelStm = "select c_permstr "// count(*) "
                  + "as retval from eas_perms where "
                  + "c_appname = '" + permDf.getWrapperAppName() 
                  + "' and c_uname = '" + permDf.getPermUserName() + "'"
                  + " and c_permobject = '" + progObj + "'"
                  + "and c_typpermobj = '" + typObj + "'";
*/
    if (permDf.getPermUserName().equals("SUPER")) return;
    
    String sqlSelStm = "select c_permstr "// count(*) "
                  + "as retval from eas_perms where "
                  + "c_appname = '" + permDf.getWrapperAppName() 
                  + "' and c_usertype = 'USER'"
                  + " and c_uname = '" + permDf.getPermUserName() + "'"
                  + " and c_permobject = '" + progObj + "'";
    ////System.out.println("vaaalUUUssPeer: " + sqlSelStm);
    Object o;
    o = krn.SQLQ_getQueryAsValue(DBcnWork.getConn(), sqlSelStm, true);
    if (o==null) o = "XERR";
    //// System.out.print("validateUserPermDefinition(permDf) valuuu in eas_perms for :" + progObj);
    if (bSuperUser) {
        permDf.setPermNew(true);
        permDf.setPermUpdate(true);
        permDf.setPermDelete(true);
        permDf.setPermPrint(true);
        o = "NUDP";
    }
    else {
        permDf.setPermNew(o.toString().contains("N"));
        permDf.setPermUpdate(o.toString().contains("U"));
        permDf.setPermDelete(o.toString().contains("D"));
        permDf.setPermPrint(o.toString().contains("P"));
    }
    /*
    if (progObj.contains("Pnl_eas_usrgrp"))
    System.out.println("PERMD - Permission for user " + permDf.getPermUserName()
            + " on object: " + progObj + " is: " + o.toString() + " super:::" + bSuperUser
            + "\n permUserType::" + permDf.getPermUserType() + " assignedVal::" + permDf.getPermStr()
    + " toPermObject:" + permDf.toString());
    */
////    System.out.println("permd: VALIDATING permDefinition:  " + permDf.getParentPermProgObject()
////            + " / " + permDf.getPermProgObject() + "  SCORE:" + i);
    // pridanie neexistujuceho perm-objektu
}

/*
public Integer getPermRoot_ID() {
    return getPermobj_ID(krn.getWrapperAppName(),"PROGRAM","SYSTEM_" + krn.getWrapperAppName());
}
*/
public PermDefinition getSuperPermRoot() {
    Integer i = null;
    if (rootSuperPermDefinition==null) {
        System.out.println("iiibasnekk nullroot!!!");
        rootSuperPermDefinition = createSuperPermDefinition(null, i);
        i = getSuperPermobj_ID(wrapperAppName, "PROGRAM", "SYSTEM_" + wrapperAppName);
    }
   // System.out.println("Permd.getSuperPermRoot()== " + rootSuperPermDefinition.getPermLabel());
//    + " --- rowID: " + rootSuperPermDefinition.getUserPermDefinitionRowID());
    return rootSuperPermDefinition;
}
/*
public Integer getSuperPermobj_ID(String wrapperAppName, String typpermobj, String permobj) {
////    System.out.println("getPermobj_ID()for:" + wrapperAppName + " / " + typpermobj + " / " + permobj);
    String stm = "select id_eas_permobj as retval from eas_permobj where c_appname = '"
            + wrapperAppName + "' and c_typpermobj = '"
            + typpermobj + "' and c_permobject = '" + permobj + "'";
    Object o = krn.SQLQ_getQueryAsValue(DBcnWork.getConn(), stm, true);
    int i = Integer.parseInt(o.toString());
    return i;
}
*/
public Integer getSuperPermobj_ID(String wrapperAppName, String typpermobj, String permobj) {
////    System.out.println("getPermobj_ID()for:" + wrapperAppName + " / " + typpermobj + " / " + permobj);
    String stm = "select id_eas_permobj as retval from eas_permobj where c_appname = '"
            + wrapperAppName + "' and c_typpermobj = '"
            + typpermobj + "' and c_permobject = '" + permobj + "'";
    System.out.println("SUUPEIDGEGT:" + stm);
    Object o = krn.SQLQ_getQueryAsValue(DBcnWork.getConn(), stm, true);
    int i = Integer.parseInt(o.toString());
    return i;
}

public PermDefinition getUSER_PermRoot() {
    return rootUserPermDefinition;
}

public void setWrapperAppName(String wrpAppName) {
    wrapperAppName = wrpAppName;
}
    
public String getWrapperAppName() {
   return wrapperAppName;
}

public void loadPermCacheForUser(String usrType, String usrName) {
    ArrayList<Modul> EOC_xmodules;
    EOC_xmodules = krn.getEOC_xmodules();
    
    int iIdx = 0;
   // Pridanie struktur menu-objektov z menu-barov modulov EaSys 
   for (Modul mdl : EOC_xmodules) {
       /* vytvorenie uzla pre aktualny modul 
        *
        * parentom modulov je parentProgObject SYSTEM, 
        * teda PermDefinition - objekt: rootSuperPermDefinition.
        * Dalej uz sa dedi PermDefinition-objekt nadradeneho uzla stromu
        ******************************************************************/
       PermDefinition pDefModul;
       if (mdl.getUserPermDefinition() == null) {
           pDefModul = new PermDefinition("USER",currentUser,wrapperAppName, krn, this, DBcnWork, "MODUL"
                     ,"MODUL:" + mdl.sMDL, rootSuperPermDefinition, "MODUL_" + mdl.sMDL,"");
           mdl.setUserPermDefinition(pDefModul);
       }    
       pDefModul = mdl.getUserPermDefinition();
       pDefModul.initForUser(usrType,usrName);
       iIdx++;
       JMenuBar jmb = mdl.mbar_MDL;
       int mCnt = jmb.getMenuCount();
       for (int imn = 0; imn < mCnt; imn++) {
           Menu jmn;
           jmn = (Menu) jmb.getMenu(imn);
           // parentom menu-objektov modulu je parentProgObject "MODUL_" + mdl.sMDL
           PermDefinition pDefMenu;
           if (jmn.getUserPermDefinition() == null) {
               pDefMenu = new PermDefinition("USER",currentUser, wrapperAppName, krn, this, DBcnWork, "MENU"
                          ,"MENU:" + jmn.getText(), pDefModul, "MENU_" + jmn.getText(),"");
               jmn.setUserPermDefinition(pDefMenu);
           }
           pDefMenu = mdl.getUserPermDefinition();
           pDefMenu.initForUser(usrType,usrName);
           if (jmn==null) continue;

           int iCnt = jmn.getItemCount();
           for (int i = 0; i < iCnt; i++) {
               MenuItem itm = (MenuItem) jmn.getItem(i);
               if (itm==null) continue;
               PermDefinition pDefMenuItem;
               // itm by mal mat vytvoreny PermDefinition-object pocas
               // vytvoreni menu
               
               if (itm.getUserPermDefinition() == null) {
                   if (itm.getSuperPermDefinition() == null) {
                      System.out.println("Permd: FATAL - no perm definition found in menu-item: " + itm.getText());
                   continue;
                   }
                   
                   pDefMenuItem = new PermDefinition("USER",currentUser, wrapperAppName
                           ,krn, this, DBcnWork, "MENU-ITEM"
                          ,"MENU-ITEM_" + itm.getText(), pDefMenu
                          ,itm.getSuperPermDefinition().getPermProgObject(),"");
                   itm.setUserPermDefinition(pDefMenuItem);
               }
               pDefMenuItem = itm.getUserPermDefinition();
               pDefMenuItem.initForUser(usrType,usrName);
           }
       }
   }
}
//myPermDef.initForUser(usrType, usrName);


}
