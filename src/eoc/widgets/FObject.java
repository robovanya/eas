/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.widgets;

import eoc.EOC_message;
import eoc.IEOC_Object;
import eoc.database.DBconnection;
import system.Kernel;
import java.awt.event.FocusListener;
import java.lang.reflect.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import system.FnEaS;
import system.perm.PermHandler;

/**
 *
 * @author rvanya
 */
public class FObject extends javax.swing.JFrame implements eoc.IEOC_VisualObject {
    
    public IEOC_Object myObjectID;
    private PermHandler permHandler;
    public Kernel krn;
    public DBconnection MyCn;
    public boolean bInitialized;
    public JFrame myJFrame;
    
    private String usrPerms = ""; // "NOKT-V"
    private String objPerms = "NUDP";
    private String perms = "";
    private int connectionStatus;
    
    private Map<String, String> properties = new HashMap<>();
    private final Map<String, String> privateData = new HashMap<>();
    
    private eoc.IEOC_VisualObject parentContainer; 
    private String sEOCobjectType = "<EOC_FOject_type_not_defined>";

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
    public String initialize(Kernel kr, DBconnection cX) {
        this.setKrn(kr);
        this.setConn(cX);
        setEOC_objectType("EOC_FObject");
        //krn.krnMsg("SuperFObjrectInitialized");
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

    // prijimac sprav od cudzich objektov
    @Override
    public String receiveMessage(EOC_message eocMsg) {

        // musi byt ako prva instrukcia v metode !!!
        if (eocMsg==null) return FnEaS.nullEocMessageResponse(myObjectID);
        
        // standardne hlasenie metody
       krn.debugOut(this,5,this.getClass().getName() + " EOC_VObject-Receiving message: "  
                         + eocMsg.getMessage() + " s parametrami: " + eocMsg.getParameters());
       //krn.krnMsg(this.getClass().getName() + " ** 1a PICABAAAA - EOC_VisualObject-Receiving message: "  
       //                  + sMessage + " s parametrami: " + sParameters);
       String retVal = "NOTRECEIVED-in_FObject";
       try {
          // ziskanie vhodnej metody
          Method mtd;
          mtd = FnEaS.getEocMessageMethod(myObjectID, eocMsg);
         // krn.krnMsg(this.getClass().getName() + " ** 1b EOC_VisualObject-Receiving message: "  
         //                + sMessage + " s parametrami: " + sParameters + " mtd-is-null=" + (mtd==null));

          try {
             if (mtd != null) {
                if (!mtd.getReturnType().toString().equals("class java.lang.String")
                    && !mtd.getReturnType().equals(Void.TYPE)) {
                   krn.Message(this, "W", 
                              "Vrátená hodnota nie je typu 'String'", mtd.getName());
                }
                mtd.setAccessible(true);
                if (eocMsg.getParameters() == null) {
                   retVal = (String) mtd.invoke(this);
                }
                else {
                   retVal = (String) mtd.invoke(this,eocMsg.getParameters());
                }
                if (retVal==null) {retVal = ""; } // asi je metoda typu void
                return retVal;
//krn.krnMsg(this.getClass().getName() + " ** 3a PICABAAAA - EOC_VisualObject-Receiving message: " + retVal);
       //krn.krnMsg(this.getClass().getName() + " ** 2a EOC_VisualObject-Receiving message: "  
           //              + sMessage + " s parametrami: " + sParameters + " RVL=" + retVal
           //     + " mtd-is-null=" + (mtd==null));
             }
/*
             // hladanie a spustenielokalnej verzie receiveMessage, pokial existuje
             mtd = FnEaS.getLocalReceiveMessageMethod(myObjectID);
         //krn.krnMsg(this.getClass().getName() + " ** 2b EOC_VisualObject-Receiving message: "  
         //                + sMessage + " s parametrami: " + sParameters + " RVL=" + retVal);
           if (mtd!=null) {
                 String locRetVal;
                 mtd.setAccessible(true);
                 locRetVal = (String) mtd.invoke(eocMsg.getSender(),eocMsg.getMessage()
                                                ,eocMsg.getParameters(),eocMsg.getOthers());
                 if (locRetVal!=null) { retVal = retVal + locRetVal; }
             }
       krn.Message(this.getClass().getName() + " ** 3 EOC_VObject-Receiving message: "  
                         + eocMsg.getMessage() + " s parametrami: " + eocMsg.getParameters() + " RVL=" + retVal);
             return retVal;
           */  
          } catch (IllegalAccessException ex) {
             krn.debugOut(this,0,"VObject " + ex.toString());
             return "NOTRECEIVED-VObject=SUPER-IllegalAccessException=" + eocMsg.getMessage();
          } catch (IllegalArgumentException ex) {
             krn.debugOut(this,0,"VObject " + ex.toString());
             return "NOTRECEIVED-VObject=SUPER-IllegalArgumentException=" + eocMsg.getMessage();
          } catch (InvocationTargetException ex) {
             krn.debugOut(this,5,"VObject " + ex.toString()
                     + "\n>>>==== to je ON ====>>>" + ex.getTargetException());
             return "NOTRECEIVED-VObject=SUPER-InvocationTargetException=" + eocMsg.getMessage();
          }
       } catch (SecurityException ex) {
          return "NOTRECEIVED-VObject=SUPER-SecurityException=" + eocMsg.getMessage();
       }
       finally {
          // return "";
       }
       return retVal;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setParentContainer(eoc.IEOC_VisualObject cntnr) {
       parentContainer = cntnr;  
   }

    @Override
    public boolean isContainer() {
       return true;
    }

    @Override
    public void setMyObjectID(eoc.IEOC_Object evo) {
       myObjectID = evo;
    }

    @Override
    public String CallMethod (Object oCaller, String sMethod, String sParameters) {
        return krn.CallMethod(oCaller, this, sMethod, sParameters);
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
    public Map<String, String> saveFrameToDef() {
        return krn.saveFrameToDef(this);
    }

    @Override
    public void restoreFrameFromDef(Object oPanel, Map<String, String> defMap) {
        krn.restorFrameFromDef(oPanel, defMap);
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
    public eoc.IEOC_VisualObject getParentContainer() {
        return parentContainer;
    }

    @Override
    public FocusListener getMyFocusListener() {
        FocusListener[] fl = this.getFocusListeners();
        return fl[0];
    }

    @Override
    public String createLinkTo(Object oCreator, Object oVSrc, Object oVTrg, String sLink, String sState) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setObjPerms (EOC_message eocMsg) {
       permHandler.setObjPerms(eocMsg);
    }
    
    @Override
    public void setUsrPerms (EOC_message eocMsg) {
       permHandler.setUsrPerms(eocMsg);
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

    @Override
    public PermHandler getPermHandler() {
        return permHandler;
    }

    public void createPermHandler(Kernel kr, IEOC_Object ieo) {
        permHandler = new PermHandler(kr, ieo);
    }

    @Override
    public String getUsrPerms() { return permHandler.getUsrPerms();}

    @Override
    public String getObjPerms() { return permHandler.getObjPerms();}

    @Override
    public String getPerms() { return permHandler.getPerms();}
    
}
