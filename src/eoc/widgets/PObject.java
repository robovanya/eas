/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.widgets;
import eoc.EOC_message;
import eoc.IEOC_Object;
import eoc.IEOC_VisualObject;
import eoc.database.DBconnection;
import eoc.xinterface.XViewer;
import system.Kernel;
import system.FnEaS;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusListener;
import java.lang.reflect.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import system.perm.PermHandler;

/**
 *
 * @author rvanya
 */
public class PObject extends javax.swing.JPanel implements eoc.IEOC_VisualObject {
    // sluzi na dokonanie ukonov inicializacie objektu.
    // volana je z kontainera objektu po inicializacii vsetkych objektov.
    // Sluzi hlavne na odoslanie spravy (EOC_message) ostatnym objektom, za ucelom
    // synchronizacie stavu objektov
    public final boolean HIDE_ERRORS = true;
    public final boolean VIEW_ERRORS = false;
    
    public IEOC_Object myObjectID;
    private PermHandler permHandler;

    public Kernel krn;
    public DBconnection MyCn;
    public boolean bInitialized = false;
    public boolean bAfterInitialized = false;
    public JFrame myJFrame;
    
    private int connectionStatus;
    public eoc.IEOC_VisualObject parentContainer; 
    
    private Map<String, String> properties  = new HashMap<>();
    private Map<String, String> privateData = new HashMap<>();
    public String sEOCobjectType = "<EOC_PObject_type_not_defined>";
    
    public PObject() {
    }
    
    public eoc.IEOC_Object getFirstPnlObjectByClass(PObject pob, Class searchedClass) {
        Class currObjClass;  // trieda skumaneho objektu
        Class CurrSuperObjClassName;
        eoc.IEOC_Object ieo = null;  // zisteny objekt
        Component[] c = pob.getComponents();  // widgety JFrame-kontainera 
        o_for:
        for (Object o : c) {
            currObjClass = o.getClass();
            //sCurrSuperObjClassName = currObjClass.getSuperclass().getSimpleName();
            if (currObjClass.getSimpleName().equals("JTabbedPane")) {
                ieo = getFirstTabObjectByClass((JTabbedPane) o, searchedClass);
                if (ieo != null) break;
            }
            else {
                /*
                krn.OutPrintln("TESTING PNL : " + currObjClass.getName()
                + " SUPER-IS:" + currObjClass.getSuperclass().getSimpleName());
                
                krn.OutPrintln("currObjClass.isAssignableFrom(searchedClass) =="
                 + currObjClass.isAssignableFrom(searchedClass)    );
                krn.OutPrintln("currObjClass == searchedClass == "
                 + (currObjClass == searchedClass)    );
                */
                if (currObjClass == searchedClass) {
                   ieo = (eoc.IEOC_Object) o;
                   ////krn.OutPrintln("PNL - currObjClass == searchedClass !! => " + ieo);
                   
                   break;
                }

                if (PObject.class.isAssignableFrom(currObjClass)) {
                    ieo = getFirstPnlObjectByClass((PObject) o,searchedClass);  
                    if (ieo != null) break;
                }
   /*             krn.OutPrintln("currObjClass.isAssignableFrom(eoc.EOC_PObject.class) =="
                 + currObjClass.isAssignableFrom(eoc.EOC_PObject.class)    ); */
            }
            
            
           /*  
            if ((!(o instanceof cls))
                && (!(o instanceof JTabbedPane))) continue;
            */
          /*  
            s = cl.getSimpleName();
//            s = cl.getName();
        krn.OutPrintln("getObjectByClassAndName() getting begin for class: " 
                + cls + " object: " + oName + " super:: " + o.getClass().getSuperclass());
            if (s.equals("JTabbedPane")) {
                ieo = getTabObjectByClassAndName((JTabbedPane) o, cls, oName);
            }
            else {
                // nizkourovnove objekty nas nezaujimaju (nevyuzivaju komunikaciu EOC)
                if (!(cl.getSuperclass().getName().equals("eoc.EOC_PObject"))) {
                    krn.OutPrintln("getObjectByName() iGGGgnoring class: " + cl.getSuperclass().getName()
                    + " class:" + cl.getName());
                    continue o_for;
                }
                krn.OutPrintln("getObjectByClassAndName() COMPARING: "
                    + cl.getSuperclass().getName() + " WITH " + cls + " AND " 
                    + s + " WITH " + oName
                    + " ==== " + (cl.getSuperclass().getName().equals(cls) && s.equals(oName))); 
                if (cl.getSuperclass().getName().equals(cls) && s.equals(oName)) { 
                   ieo = (IEOC_Object) o;
                   break;
                }
            }
            if (cl.getSuperclass().getName().equals("eoc.EOC_PObject")) {
                eoc.EOC_PObject pob = (eoc.EOC_PObject) o;
                ieo = pob.getObjectByClassAndName(cls, oName);
            }
            if (ieo != null) break;
        */
        }
       //// krn.OutPrintln("PNL - FINALLYUScurrObjClass == searchedClass !! => " + ieo);
        return ieo;
    }
    
    public eoc.IEOC_Object getFirstTabObjectByClass(JTabbedPane tbObj, Class searchedClass) {
        Class currObjClass;                // trieda skumaneho objektu
        eoc.IEOC_Object ieo = null;  // zisteny objekt
        //Component[] c = this.getComponents();  // widgety JFrame-kontainera 
        Component[] c = tbObj.getComponents();  // widgety JTabbedPane-kontainera
        o_for:
        for (Object o : c) {
            currObjClass = o.getClass();
            if (currObjClass.getSimpleName().equals("JTabbedPane")) {
                ieo = getFirstTabObjectByClass((JTabbedPane) o, searchedClass);
                if (ieo != null) break;
            }
            else {
                /*
                krn.OutPrintln("TESTING TBP: " + currObjClass.getName()
                + " SUPER-IS:" + currObjClass.getSuperclass().getSimpleName());
                krn.OutPrintln("currObjClass.isAssignableFrom(searchedClass) =="
                 + currObjClass.isAssignableFrom(searchedClass)    );
                krn.OutPrintln("currObjClass == searchedClass == "
                 + (currObjClass == searchedClass)    );
                */
                if (currObjClass == searchedClass) {
                   krn.OutPrintln("TBP - currObjClass == searchedClass !!");
                   ieo = (eoc.IEOC_Object) o;
                   break;
                }
 /*               krn.OutPrintln("currObjClass.isAssignableFrom(eoc.EOC_PObject.class) =="
                 + currObjClass.isAssignableFrom(eoc.EOC_PObject.class)    );
                krn.OutPrintln("eoc.EOC_PObject.class.isAssignableFrom(currObjClass) =="
                 + eoc.EOC_PObject.class.isAssignableFrom(currObjClass)    ); */
                if (PObject.class.isAssignableFrom(currObjClass)) {
                    ieo = getFirstPnlObjectByClass((PObject) o,searchedClass);  
                    if (ieo != null) break;
                }
            }
        }     
        return ieo;
    }

                 /*

    public IEOC_Object getTabObjectByClassAndName
             (JTabbedPane tbObj, String cls, String oName) {
        IEOC_Object ieo = null;
        Class cl;        // trieda skumaneho objektu
        Component[] c = tbObj.getComponents();  // widgety JFrame-kontainera 
        String s;        // nazov typu skumaneho objektu
        krn.OutPrintln("getTabObjectByClassAndName() getting begin for class: " 
                + cls + " object: " + oName);
        o_for:
        for (Object o : c) {
            if ((!(o instanceof eoc.EOC_PObject))
                && (!(o instanceof JTabbedPane))) continue;
            cl = o.getClass();
            s = cl.getSimpleName();
//            s = cl.getName();
            krn.OutPrintln("getTabObjectByName() my class: " + cl.getSuperclass()
                   + "  my base object: " + s);
            if (s.equals("JTabbedPane")) {
                ieo = getTabObjectByClassAndName((JTabbedPane) o, cls, oName);
            }
            else {
                // nizkourovnove objekty nas nezaujimaju (nevyuzivaju komunikaciu EOC)
                if (!(cl.getSuperclass().getName().equals("eoc.EOC_PObject"))) {
                    krn.OutPrintln("getObjectByName() iGGGgnoring class: " + cl.getSuperclass().getName()
                    + " class:" + cl.getName());
                    continue o_for;
                }
                krn.OutPrintln("getTabObjectByClassAndName() COMPARING:\n"
                    + cl.getSuperclass().getName() + " WITH " + cls + " AND " 
                    + s + " WITH " + oName 
                    + " ==== " + (cl.getSuperclass().getName().equals(cls) && s.equals(oName))); 
                if (cl.getSuperclass().getName().equals(cls) && s.equals(oName)) {
                   ieo = (IEOC_Object) o;
                   break;
                }
            if (cl.getSuperclass().getName().equals("eoc.EOC_PObject")) {
                eoc.EOC_PObject pob = (eoc.EOC_PObject) o;
                ieo = pob.getObjectByClassAndName(cls, oName);
            }
            }
            if (ieo != null) break;
        }
        return ieo;
    }
   */ 
    
    public void getObjectByName() {
        //eoc.EOC_PObject = 
        krn.OutPrintln("getObjectByName() called");
                Class cl;        // trieda skumaneho objektu
        Component[] c = this.getComponents();  // widgety JFrame-kontainera 
       String s;        // nazov typu skumaneho objektu
        o_for:
        for (Object o : c) {
            cl = o.getClass();
            s = cl.getSimpleName();
        krn.OutPrintln("getObjectByName() my base object: " + s);
        if (s.equals("JTabbedPane")) {
            getTabObjectByName((JTabbedPane) o);
        }
        }
    }
    
    public void getTabObjectByName(JTabbedPane tbObj) {
        //eoc.EOC_PObject = 
        krn.OutPrintln("getTabObjectByName() called");
                Class cl;        // trieda skumaneho objektu
        Component[] c = tbObj.getComponents();  // widgety JFrame-kontainera 
       String s;        // nazov typu skumaneho objektu
       String b;        // nazov typu skumaneho objektu
        o_for:
        for (Object o : c) {
            cl = o.getClass();
            s = cl.getSimpleName();
            b = cl.getCanonicalName();
        krn.OutPrintln("getTabObjectByName() my class: " + cl.getSuperclass()
                + "  my base object: " + s
                + " my get name == " + b);
        }
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
    public String initialize(Kernel kr, DBconnection cX) {
        if (bInitialized) return "";
        this.setKrn(kr);
        this.setConn(cX);
        setMyObjectID(this);
        createPermHandler(krn, myObjectID);
        setEOC_objectType("EOC_PObject");
        Component[] components = this.getComponents();
        PObject pob = null;
        for (int i = 0; i < components.length; ++i) {
            try {
                pob = (PObject) components[i];
                pob.initialize(kr,cX);
            }
            catch (java.lang.ClassCastException ex) {
               // ignoruje sa to (zaujimaju nas len EOC_PObject objekty)
            }
        }
        krn.setWidgetFonts((Container) this, krn.getDefaultFont());
        bInitialized = true;
        return "";
    }

    @Override
    public String afterInitialize() {
       if (bAfterInitialized) return "";
       Component[] components = this.getComponents();
       PObject pob = null;
       for (int i = 0; i < components.length; ++i) {
           try {
              pob = (PObject) components[i];
              pob.afterInitialize();
           }
           catch (java.lang.ClassCastException ex) {
               // ignoruje sa to (zaujimaju nas len EOC_PObject objekty)
           }
       }
       bAfterInitialized = true;
       return "";
    }

    @Override
    public String destroy() {
        // 2015-8-20
        ////System.out.println("PObject - destroooing >> " + FnEaS.sObjName(this) /*this.toString()*/);
        Component cmp[] = this.getComponents();
        /*
        System.out.println("COMPONENTLIST:");
        for (Component cm: cmp) {
           System.out.println("PObject-destroy: COMPONENT-cm is " + cm.getClass().getSimpleName());
        }
        */
        iBlock:
        for (Component cm: cmp) {
            
            if (cm instanceof eoc.IEOC_VisualObject) {
                ((eoc.IEOC_VisualObject) cm).destroy();
            }
            
            else {
               if (cm instanceof JPanel) {
                    Component pcmp[] = ((JPanel) cm).getComponents();
                    pBlock:
                    for (Component pcm: pcmp) {
                        if (pcm instanceof eoc.IEOC_VisualObject) {
                          ((eoc.IEOC_VisualObject) pcm).destroy();
                        }
                        //else System.out.println("JPanel-destroy: pcm is " + pcm.getClass().getSimpleName());
                    }
                }
               //else System.out.println("PObject-destroy: cm is " + cm.getClass().getSimpleName());
            }
        }
        return "";
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
        
     ////   System.out.println("PObjeecteee_mesginReceive: " + FnEaS.sObjName(myObjectID) 
     ////           + " MSG>> " +  eocMsg.getMessage() + " PAR=> " + eocMsg.getParameters());
        // musi byt ako prva instrukcia v metode !!!
        if (eocMsg==null) return FnEaS.nullEocMessageResponse(myObjectID);
        
        // standardne hlasenie metody
       krn.debugOut(this,5,this.getClass().getName() + " EOC_PObject-Receiving message: "  
                         + eocMsg.getMessage() + " s parametrami: " + eocMsg.getParameters());
       //krn.krnMsg(this.getClass().getName() + " ** 1a PICABAAAA - EOC_VisualObject-Receiving message: "  
       //                  + sMessage + " s parametrami: " + sParameters);
       try {
          // ziskanie vhodnej metody
          Method mtd;
          mtd = FnEaS.getEocMessageMethod(myObjectID, eocMsg);
          if ((mtd==null))
          System.out.println("PObjeecteee_mesginReceive: >>>> NOT_EOC_MESSAGE_TYPE_METHOD_FOUND_IN" + FnEaS.sObjName(myObjectID));
          if (mtd == null)
             mtd = FnEaS.getEOCMethod(myObjectID, eocMsg.getMessage(), null);
          if (mtd == null)
              mtd = FnEaS.getEOCMethod(myObjectID, eocMsg.getMessage(), new Class[] {eocMsg.getParameters().getClass()});
         // krn.krnMsg(this.getClass().getName() + " ** 1b EOC_VisualObject-Receiving message: "  
         //                + sMessage + " s parametrami: " + sParameters + " mtd-is-null=" + (mtd==null));

          try {
             String retVal = "NOTRECEIVED-in_PObject";
             if (mtd != null) {
                if (!mtd.getReturnType().toString().equals("class java.lang.String")
                    && !mtd.getReturnType().equals(Void.TYPE)) {
                   krn.Message(this, "W", 
                              "Vrátená hodnota nie je typu 'String'", mtd.getName());
                }
                mtd.setAccessible(true);
                /*if (eocMsg.getParameters() == null) {
                   retVal = (String) mtd.invoke(this);
                }
                else {*/
////        System.out.println("PObjeecteee_mesginReceiveINVOKIJNGG IN: " + FnEaS.sObjName(myObjectID) 
////                + " messg: " +  eocMsg.getMessage() + " with_params:" + eocMsg.getParameters());
                   retVal = (String) mtd.invoke(this,eocMsg);
              //  }
    ////    System.out.println("PObjeecteee_mesginReceiveRETVALL_IS: " +  retVal + " INOBJECT: " + FnEaS.sObjName(myObjectID) );
                if (retVal==null) {retVal = ""; } // asi je metoda typu void
                return retVal;
             }
/*
             // hladanie a spustenielokalnej verzie receiveMessage, pokial existuje
             mtd = FnEaS.getLocalReceiveMessageMethod(myObjectID);
         //krn.krnMsg(this.getClass().getName() + " ** 2b EOC_VisualObject-Receiving message: "  
         //                + sMessage + " s parametrami: " + sParameters + " RVL=" + retVal);
           if (mtd!=null) {
                 String locRetVal;
                 mtd.setAccessible(true);
                 locRetVal = (String) mtd.in .invoke(eocMsg.getSender(),eocMsg.getMessage()
                                                ,eocMsg.getParameters(),eocMsg.getOthers());
                 if (locRetVal!=null) { retVal = retVal + locRetVal; }
             }
       krn.Message(this.getClass().getName() + " ** 3 EOC_VObject-Receiving message: "  
                         + eocMsg.getMessage() + " s parametrami: " + eocMsg.getParameters() + " RVL=" + retVal);
       */
             return retVal;
             
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
    public void setParentContainer(eoc.IEOC_VisualObject cntnr) {
        parentContainer = cntnr;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void setMyObjectID(eoc.IEOC_Object evo) {
        //public IEOC_VisualObject myObjectID;
        myObjectID = evo;
    }

    @Override
    public String CallMethod (Object oCaller, String sMethod, String sParameters) {
        krn.OutPrintln("PObjjecallmethod=" + sMethod);
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
    public Map<String, String> saveFrameToDef() {
        return krn.saveFrameToDef(myObjectID);
    }

    @Override
    public void restoreFrameFromDef(Object oPanel, Map<String,String> defMap) {
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
        FocusListener f = fl[0];
        return f;
    }

    @Override
    public String createLinkTo(Object oCreator, Object oVSrc, Object oVTrg, String sLink, String sState) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setObjPerms (EOC_message eocMsg) {
       permHandler.setObjPerms(eocMsg);
       /*
       String lastObjPerms = objPerms;
       objPerms = eocMsg.getParameters().toUpperCase();
       String lastPerms = perms;
       countPerms();
        System.out.println("###\n>>> ############OBJ# - ObjPERMS--PObject-setting "
          + FnEaS.sObjName(myObjectID) + " -> OBJPERMS_TO:" + eocMsg.getParameters() + " in_perms: " + lastPerms
          + " afterCount: " + perms + "\n###");
      // if (!objPerms.equals(lastObjPerms) || !perms.equals(lastPerms)) {
           EOC_message msg = new EOC_message(this,"permissionChanged", perms, "");
           krn.krn_sendMessage(this,msg,"*","target");
       //}
       */
    }
    
    @Override
    public void setUsrPerms (EOC_message eocMsg) {
       permHandler.setUsrPerms(eocMsg);
       /*
       String lastUsrPerms = usrPerms;
       usrPerms = eocMsg.getParameters().toUpperCase();
       String lastPerms = perms;
       countPerms();
        System.out.println("###\n>>> ############USR# - UsrPERMS--PObject-setting "
          + FnEaS.sObjName(myObjectID) + " -> OBJPERMS_TO:" + eocMsg.getParameters() + " in_perms: " + lastPerms
          + " afterCount: " + perms + "\n###");
       // udalost sa odosiela, len ked doslo k zmene hodnoty perms
       //if (!usrPerms.equals(lastUsrPerms) || !perms.equals(lastPerms)) {
           //vecer tu riesime volanie linku ownerom !!!
           EOC_message msg = new EOC_message(this,"permissionChanged", perms, "");
           krn.krn_sendMessage(this,msg,"*","ALL");
       //}
       */
    }
    
    public void createPermHandler(Kernel kr, IEOC_Object ieo) {
        permHandler = new PermHandler(kr, ieo);
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

    @Override
    public String getUsrPerms() { return permHandler.getUsrPerms();}

    @Override
    public String getObjPerms() { return permHandler.getObjPerms();}

    @Override
    public String getPerms() { return permHandler.getPerms();}

}

