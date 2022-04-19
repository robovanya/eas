/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc;
import eoc.database.DBconnection;
import java.sql.Connection;
import system.Kernel;
import system.perm.PermHandler;
/**
 * Zakladne povinne metody EOC objektu
 * 
 * @author rvanya
 */
public interface IEOC_Object {
    
    // inicializacia objektu pri aktivovani (nie pri vytvarani !)
    public String initialize(Kernel krn, DBconnection cnX); 
    
    // vola sa po inicializacii vsetkych objektov 
    // (EOC linky by uz mali byt zalozene rozhraniami initialize())
    public String afterInitialize();
    
    // samoznicenie objektu
    public String destroy(); 
    
    // posielanie sprav ostatnym objektom cez EOC_krn
    public String sendMessage(EOC_message eocMessage, 
                              String sLinkType, String sVector);
    
    // prijimanie a spracovanie sprav od ostatnych objektov cez EOC_krn
    public String receiveMessage(EOC_message eocMessage);
    
    // vrati aktualne spojenie s databazou
    public DBconnection getConn();
    
    // nastavi aktualne spojenie s databazou
    public String setConn(DBconnection cWrk);
    
    // nastavi hlavne spojenie pre spracovanie DB-udajov, ktore je pouzite pri 
    // generovani automatizovanych retazcov prikazov vrstvou EOC

    // preda pointer systemoveho jadra aktualnemu objektu, pre dalsiu komunikaciu
    public String setKrn(Kernel krnl);
    
    // nastavi typ EOC objektu (EOC_dctable, EOC_dbtable, EOC_viewer, a.t.d.
    //void setEOC_objectType(String sObjectType);
    // vrati typ EOC objektu (EOC_dctable, EOC_dbtable, EOC_viewer, a.t.d.
    //String getEOC_objectType();
    
    public void setConnectionStatus(int status);
    
    public String CallMethod (Object oCaller, String sMethod, String sParameters);

   public void setEOC_objectType(String sObjectType); 

   public String getEOC_objectType();
   
   public void setMyObjectID(eoc.IEOC_Object evo); 

   public void setPrivateData(String key, String val);
   
   public String getPrivateData(String key);

   /* sluzi na delegovanie linku medzi objektami.
   napriklad cielovy kontainer moze link predelegovat na interny objekt 
   -- oVSrc je vetsinou ten isty objekt ako oCreator 
   -- ked oVTrg == null, targetom je tento objekt
   */
   public String createLinkTo(Object oCreator, Object oVSrc ,Object oVTrg,String sLink, String sState);

   public system.perm.PermHandler getPermHandler();
   void createPermHandler(Kernel kr, IEOC_Object ieo);
   public void setObjPerms (EOC_message eocMsg);
   public void setUsrPerms (EOC_message eocMsg);
   public String getUsrPerms();
   public String getObjPerms();
   public String getPerms();

   public String objectPermsChanged (EOC_message eocMsg);
   public String userPermsChanged (EOC_message eocMsg);
   
   /* presunute do permHandler-objektu
   public void countPerms();
   public void setLocalObjPerms (String s, boolean bTransmit);
   public void setLocalUsrPerms (String s, boolean bTransmit);
   /**
    * Zloží reťazec z objPerms, usrPerms a bTransmit
    * do tvaru objPerms#usrPerms#bTransmit.
    * 
    * @param sPerms
    * @return 
    */
   //// public String joinPerms (boolean bTransmit);

}
