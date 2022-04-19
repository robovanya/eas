/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc;

import eoc.database.DBconnection;
import java.sql.Connection;
import system.Kernel;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import system.FnEaS;
import system.perm.PermHandler;

/**
 *
 * @author rvanya
 */
public class EASobject extends Object implements IEOC_Object {
    
    public  IEOC_Object myObjectID;
    private PermHandler permHandler;

    public  Kernel krn;
    public  DBconnection MyCn;
    public  boolean bInitialized;
    private String usrPerms = "";  // "NOKT-V"
    private String objPerms = "NUDP";
    private String perms = "";
    private int connectionStatus;
    private String sEOCobjectType = "<EOC_Object_type_not_defined>";
    private Map<String, String> properties = new HashMap<>();
    private final Map<String, String> privateData = new HashMap<>();


    public EASobject() {
    }
    
    public String getProp(String key) {
        return properties.get(key);
    }

    public void setProp(String key, String value) {
        properties.put(key, value);
    }

    public void delProp(String key) {
        properties.remove(key);
    }
    @Override
    public String getUsrPerms() { return permHandler.getUsrPerms();}

    @Override
    public String getObjPerms() { return permHandler.getObjPerms();}

    @Override
    public String getPerms() { return permHandler.getPerms();}
    
    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        if (bInitialized) return "";
        setMyObjectID(this);
        this.setKrn(kr);
        this.setConn(cX);
        createPermHandler(krn, myObjectID);

        setEOC_objectType("EOC_Object");
        bInitialized = true;
        return "";
    }

    @Override
    public String destroy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DBconnection getConn() {
        return MyCn;
    }

    @Override
    public String sendMessage(EOC_message eocMsg, String sLinkType, String sVector) {
       return krn.krn_sendMessage(this, eocMsg, sLinkType, sVector);
    }

    @Override
    public String receiveMessage(EOC_message eocMsg) {

        // musi byt ako prva instrukcia v metode !!!
        if (eocMsg==null) return FnEaS.nullEocMessageResponse(myObjectID);
               krn.debugOut(this,5,this.getClass().getName() + " EOC_Object-Receiving message: "  
                         + eocMsg.getMessage() + " s parametrami: " + eocMsg.getParameters());
       try {
          // testovat bude treba podla obsahu sParameters
          krn.debugOut(this,5,"receiveMessageInNObject=" + eocMsg.getMessage());
          Method mtd;
          mtd = this.getClass().getMethod(eocMsg.getMessage(), new Class[]  {String.class});
          if (mtd == null) {
             mtd = this.getClass().getDeclaredMethod(eocMsg.getMessage(),new Class[] {String.class});
          }
          if (mtd != null) {
             krn.debugOut(this,5,"MethodN: " + mtd.toString() 
                     + " is generic=" + mtd.toGenericString());
          }
          try {
             Object retVal;
             if (!mtd.getReturnType().toString().equals("class java.lang.String")) {
                krn.Message (this, "E", 
                           "Vrátená hodnota nie je typu 'String'", mtd.getName());
             }
             retVal = mtd.invoke(this, eocMsg.getParameters());
             return retVal.toString();
          } catch (IllegalAccessException ex) {
             krn.debugOut(this,0,"xNObject " + ex.toString());
             return "NOTRECEIVED-NObject=SUPER-IllegalAccessException";
          } catch (IllegalArgumentException ex) {
             krn.debugOut(this,0,"xNObject " + ex.toString());
             return "NOTRECEIVED-NObject=SUPER-IllegalArgumentException";
          } catch (InvocationTargetException ex) {
             krn.debugOut(this,5,"xNObject " + ex.toString());
             return "NOTRECEIVED-NObject=SUPER-InvocationTargetException";
          }
       } catch (NoSuchMethodException ex) {
          return "NOTRECEIVED-NObject=SUPER-NoSuchMethodException";
       } catch (SecurityException ex) {
          return "NOTRECEIVED-NObject=SUPER-SecurityException";
       }
       finally {
          return "";
       }
    }

    @Override
    public String setConn(DBconnection cX) {
        this.MyCn = cX;
        return "";
    }

    @Override
    public String setKrn(Kernel krnl) {
        this.krn = krnl;
        return "";
    }

    @Override
    public void setConnectionStatus(int status) {
         connectionStatus = status;
    }

    @Override
    public String afterInitialize() {
        return "";
    }

    @Override
    public String CallMethod (Object oCaller, String sMethod, String sParameters) {
        return krn.CallMethod(oCaller, this,sMethod,sParameters);
    }

    @Override
    public void setEOC_objectType(String sObjectType) {
        sEOCobjectType = sObjectType;
    }

    @Override
    public String getEOC_objectType() {
      return sEOCobjectType;
   }

    @Override
    public void setMyObjectID(IEOC_Object evo) {
        myObjectID = this;
    }

    @Override
    public void setPrivateData(String key, String val) {
        privateData.put(key, val);
    }

    @Override
    public String getPrivateData(String key) {
        return privateData.get(key);
    }

    @Override
    public String createLinkTo(Object oCreator, Object oVSrc, Object oVTrg, String sLink, String sState) {
        // Link by mal nastavit skutocny-odvodeny objekt, preto je tu hlaska
        // Metoda by sa mala v nom override-ovat, bez odkazu na parent-a
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PermHandler getPermHandler() {
        return permHandler;
    }

    @Override
    public String objectPermsChanged(EOC_message eocMsg) {
        permHandler.setObjPerms(eocMsg);
        return "";
    }

    @Override
    public String userPermsChanged(EOC_message eocMsg) {
        permHandler.setUsrPerms(eocMsg);
        return "";
    }

    public void createPermHandler(Kernel kr, IEOC_Object ieo) {
        permHandler = new PermHandler(kr, ieo);
    }

    @Override
    public void setObjPerms (EOC_message eocMsg) {
       permHandler.setObjPerms(eocMsg);
    }
    
    @Override
    public void setUsrPerms (EOC_message eocMsg) {
       permHandler.setUsrPerms(eocMsg);
    }

    
}
