/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system.modul;

import eoc.IEOC_VisualObject;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import system.FnEaS;
import system.Kernel;
import system.perm.PermDefinition;
import system.modul.MenuItem;
/**
 *
 * @author rvanya
 */
public class Modul implements system.perm.IPermSource {
    private String    wrapperAppName;
    public String     sMDL;
    public String     sMDL_TOOLTIP;
    public JButton    btn_MDL;   // button modulu
    public eoc.widgets.VTabbedPane vtp_MDL;   // tabbed-pane modulu
////    public system.Mdl_TabbedPane vtp_MDL;   // tabbed-pane modulu
    public String     sMDL_state; // aktualny stav modulu (ACTIVE/PASSIVE/STOP/NONE)
    public JMenuBar   mbar_MDL;    // menu-bar modulu
//    private JMenu     mn;
//    private EOC_MenuItem mi;
    private final system.Kernel krn;
    //private final java.awt.event.ActionListener mi_actionListener;
    private final java.awt.event.ActionListener btn_actionListener;
    private final java.awt.event.MouseAdapter mi_mouseListener;
    private final Icon imgLedAkt = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_act.png"));
    private final Icon imgLedNea = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_nea.png"));
    private final Icon imgLedDis = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_dis.png"));
    private final Icon imgLedErr = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_err.png"));
    private final Icon imgLedNull = new javax.swing.ImageIcon(
            getClass().getResource("/easys/res/img/led_mdl_null.png"));
    
    private final eoc.readers.PingPongFileReader ppRdr;
    private Menu currGenMenu; // aktualne generovane menu podla def suboru
    private String currGenMenuName; // meno aktualne generovaneho menu
                                   // podla def suboru
    private String lastGenMenuName; // meno naposledy generovaneho menu
                                   // podla def suboru
    private PermDefinition superPermDef;
    private PermDefinition userPermDef;
    
    private String aktMenu = ""; // pomocna premenna
     
  public Modul (String sMDL_in, String sMDL_TOOLTIP_in,
                     String sState, Kernel krnl) 
      throws NoSuchMethodException, IOException, FileNotFoundException,
             IllegalAccessException, IllegalArgumentException, 
             InvocationTargetException, URISyntaxException {
     sMDL         = sMDL_in;
     sMDL_TOOLTIP = sMDL_TOOLTIP_in;
     krn          = krnl;
//     vtp_MDL      = new system.Mdl_TabbedPane(sMDL_in, krn);
     vtp_MDL      = new eoc.widgets.VTabbedPane(sMDL_in, krn);
     btn_MDL      = new JButton(sMDL_in);
     wrapperAppName = krn.getWrapperAppName();

     PermDefinition pDefModul = new PermDefinition("USER","SUPER", wrapperAppName, krn
                            ,krn.getPermd(),krn.getDBcnWork(), "MODUL"
                            ,"MODUL:" + sMDL, krn.getPermd().getSuperPermRoot()
                            ,"MODUL_" + sMDL,"");
     superPermDef = pDefModul;
//         public PermDefinition(Kernel kr, Permd pd, Connection cn, String puType, String puName, 
//                          String pLbl, String pParentPObj, String pPObj) {

     /*
     mi_actionListener =  new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
              mi_ActionPerformed(evt);
         }
     };
     */
     btn_actionListener =  new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            btn_ActionPerformed(evt);
         }
     };
     mi_mouseListener = new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
                  if (e.getButton() == MouseEvent.BUTTON3) {
                     ////System.out.println("Right Button Pressed on " + e.getSource().toString());
                  }
                  else if (e.getButton() == MouseEvent.BUTTON1) {
                     mi_ActionPerformed(e);
                     ////System.out.println("Left Button Pressed on " + e.getSource().toString());
                  }
               }
            };
     
      btn_MDL.setFocusable(false);
      btn_MDL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      btn_MDL.setIconTextGap(0);
      btn_MDL.setName("btn_" + sMDL_in); // NOI18N
      btn_MDL.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
         
     btn_MDL.addActionListener(btn_actionListener);
     ppRdr = new eoc.readers.PingPongFileReader();
     mbar_MDL = new JMenuBar();
    // String path = getClass().getClassLoader().getResource(".").getPath();
  //   URL url = getClass().getResource("").toURI().toURL();
  //    String path = url.getPath();

//if(url.getProtocol().equals("jar"))
//    path = new File(((JarURLConnection)url.openConnection()).getJarFileURL().getFile()).getParent();
     String osSlash = krn.get_os_slash();
     String mdlDefFileName = krn.getSystemDirectory().getAbsolutePath() 
            + osSlash + sMDL_in + osSlash + "definitions" + osSlash + "menu.def";
     File fModulDef = new File(mdlDefFileName);
     if (!fModulDef.exists()) {
       krn.WriteEventToLog("Definičný súbor modulu " + sMDL_in + " neexistuje." + " (" + mdlDefFileName + ")");
        sMDL_state   = "ERROR";
     } 
     else {
         parseMdlDefFile(mdlDefFileName);
         // krn.WriteEventToLog("Definičný súbor modulu " + sMDL_in + " sa spracoval." + " (" + mdlDefFileName + ")");
        sMDL_state   = sState;
     }

  }
    private boolean parseMdlDefFile (String defFile) 
            throws NoSuchMethodException, IOException, FileNotFoundException
            , IllegalAccessException, IllegalArgumentException
            , InvocationTargetException {
        Method mtd;
        mtd = this.getClass().getMethod("handleMdlDefFileRow", new Class[] {String.class});
        mtd.setAccessible(true);
        currGenMenu = null; // zaciatok generovania menu
        lastGenMenuName = "";
        currGenMenuName = "";
        // spracovanie definicneho suboru menu
        ppRdr.readFileByMethod_csv((Object) this, defFile, mtd);
        // krn.OutPrintln("readind DEF finalize -> adding Menu: " + currGenMenu.getName());
        if (currGenMenu != null) mbar_MDL.add(currGenMenu);
        //mb_MDL.setSize(400, 50);
////        krn.OutPrintln(mb_MDL.getMenu(0).toString());
        return true;    
    }
    
    public void handleMdlDefFileRow(String MdlDefFileRow) {
        if (MdlDefFileRow.startsWith("//") // je to poznamkovy/(vatsinou) header riadok
            | MdlDefFileRow.startsWith("|")  // je to prazdny riadok (chyba $typ_riadku)
            | MdlDefFileRow.equals("") // uplne prazdny riadok
           ) {
            return; 
        }
        String sObjectTypeOnLine = FnEaS.sEntry(1, MdlDefFileRow, "|").trim();
        // nova menu definicia v definicnom subore,
        // vytvara sa dalsi menu-objekt do menuBar objektu
        if (sObjectTypeOnLine.equals("$MENU")) {
            currGenMenu     = new Menu();
///            kazdy menu-obj by mal mat permDefinition
            currGenMenuName = FnEaS.sEntry(2, MdlDefFileRow, "|").trim();
            PermDefinition pDefMenu;
            currGenMenu.setName(currGenMenuName);
            currGenMenu.setText(FnEaS.sEntry(4, MdlDefFileRow, "|").trim());
            currGenMenu.setToolTipText(FnEaS.sEntry(7, MdlDefFileRow, "|").trim());
            // pre MENU je rodicom MODUL !!!
            aktMenu = "MENU_" + currGenMenu.getText();
           pDefMenu = new PermDefinition("USER","SUPER",wrapperAppName, krn, krn.getPermd()
                   ,krn.getDBcnWork(), "MENU", "MENU:" + currGenMenuName
                   ,superPermDef ,"MENU_" + currGenMenu.getText()
                   ,"");
            currGenMenu.setSuperPermDefinition(pDefMenu);
            if (!currGenMenuName.equals(lastGenMenuName)) {
               mbar_MDL.add(currGenMenu);
               lastGenMenuName = currGenMenuName;
            }
        }
        else if (sObjectTypeOnLine.equals("$MENUITEM")) {
          String lbl  = FnEaS.sEntry(4, MdlDefFileRow, "|").trim(); 
          String prg  = FnEaS.sEntry(6, MdlDefFileRow, "|").trim(); 
          boolean mtp = FnEaS.sEntry(8, MdlDefFileRow, "|")
                        .toUpperCase().trim().equals("TRUE"); 
          //krn.OutPrintln("reading MENUITEM: " + lbl);
          MenuItem mi = new system.modul.MenuItem(lbl,prg,mtp);
          //mi.addActionListener(mi_actionListener);
          mi.addMouseListener(mi_mouseListener);
          PermDefinition pDefMenuItem;
          pDefMenuItem = new PermDefinition("USER",/* krn.getPermd().getCurrentUser() */ "SUPER" 
                  ,wrapperAppName, krn
                  ,krn.getPermd(),krn.getDBcnWork(), "MENU-ITEM"
                  ,lbl, currGenMenu.getSuperPermDefinition(), prg,"");
          mi.setToolTipText(FnEaS.sEntry(7, MdlDefFileRow, "|"));
          mi.setSuperPermDefinition(pDefMenuItem);
        //krn.OutPrintln("readind DEF -> adding MenuItem: " + MdlDefFileRow);
          currGenMenu.add(mi);
        }
        else if (sObjectTypeOnLine.startsWith("$SEPARATOR")) {
           currGenMenu.addSeparator();    
        }
    }
    
    private void mi_ActionPerformed(java.awt.event.MouseEvent evt)  {
      MenuItem emi = (MenuItem) evt.getSource();
      if (evt.isControlDown()) {
                Kernel.staticMsg("<html><B>Informácie k objektu: </B>"
                 + "<BR><BR> MAIN_LABEL: <B>" + emi.getText() + "</B>" 
                 + "<BR><BR> PERM_LABEL: <B>" + emi.getPermDefinition().getPermLabel() + "</B>" 
                 + "<BR><BR> PERM_PROGRAM: <B>" + emi.getPermDefinition().getPermProgObject() + "</B>" 
                 + "<BR><BR> PERM_OBJECT: <B>" + emi.getPermDefinition().toString() + "</B>" 
                 + "<BR> PARENT_PERM: <B>" + emi.getPermDefinition().getParentPermDefinition().toString() + "</B>" 
                 + "<BR><BR> PERM_OBJ_TYPE: <B>" + emi.getPermDefinition().getPermObjType() + "</B>" 
                 + "<BR> PERM_USER_TYPE: <B>" + emi.getPermDefinition().getPermUserType() + "</B>" 
                 + "<BR> SUPER_ROWID: <B>" + emi.getPermDefinition().getSuperPermDefinitionRowID() + "</B>" 
                 + "<BR><BR> PERMISSION: <B>" 
                        + (emi.getPermDefinition().isPermNew() ? "N" : "")
                        + (emi.getPermDefinition().isPermUpdate() ? "U" : "")
                        + (emi.getPermDefinition().isPermDelete() ? "D" : "")
                        + (emi.getPermDefinition().isPermPrint() ? "P" : "")
                 + "</html>"
                 );
          
          return;
      }
      
      //krn.OutPrintln(">>##>> ActionPerformed in " + emi.getText());
      Class classToLoad = null;
      Object obj = null;    
      try {
         classToLoad = Class.forName(emi.getprogClass());
      } catch (ClassNotFoundException ex) {
         //Logger.getLogger(EOC_XModul.class.getName()).log(Level.SEVERE, null, ex);
         krn.Message("E", "Program: " + emi.getprogClass() + " sa nenašiel.", "");
         return;
      }
      try {
         if (emi.getPermDefinition().getPermStr().equals("")) {
             Kernel.staticMsg("Na spustenie programu nemáte dostatočné práva.");
             return;
         } 
         obj = classToLoad.newInstance();
      } catch (InstantiationException ex) {
         Logger.getLogger(Modul.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         Logger.getLogger(Modul.class.getName()).log(Level.SEVERE, null, ex);
      }
      IEOC_VisualObject pnl = (IEOC_VisualObject) obj;
      krn.EOC_startAppl(sMDL,emi,pnl,true /* multiple runable */);
    }
    
    private void btn_ActionPerformed(java.awt.event.ActionEvent evt) {                                        
        krn.selectModul(sMDL);
    }                                       
    
    public String getsMDL() {
        return sMDL;
    }

    public void setsMDL(String sMDL) {
        this.sMDL = sMDL;
    }

    public String getsMDL_TOOLTIP() {
        return sMDL_TOOLTIP;
    }

    public void setsMDL_TOOLTIP(String sMDL_TOOLTIP) {
        this.sMDL_TOOLTIP = sMDL_TOOLTIP;
    }

    public Object getoBtn_MDL() {
        return (Object) btn_MDL;
    }

    public Object getoMb_MDL() {
        return (Object) mbar_MDL;
    }

    public Object getoVtp_MDL() {
        return (Object) vtp_MDL;
    }

    public String getsMDL_state() {
        return sMDL_state;
    }

    public void setsMDL_state(String sMDL_state) {
        this.sMDL_state = sMDL_state;
    }

    public JMenuBar getModulMenuMar() {
       return mbar_MDL;
    }
    
    public void setBtnMDLicon(String sState) {
        switch (sState) {
            case "ACTIVE":
                btn_MDL.setIcon(imgLedAkt);
                break;
            case "PASSIVE":
                btn_MDL.setIcon(imgLedNea);
                break;
            case "STOPPED":
                btn_MDL.setIcon(imgLedDis);
                break;
            case "ERROR":
                btn_MDL.setIcon(imgLedErr);
                break;
            case "":
                btn_MDL.setIcon(imgLedNull);
        }
    }
    
    public JMenu getMenu() {
        return currGenMenu;
    }

    @Override
    public PermDefinition getSuperPermDefinition() {
        return superPermDef;
    }

    @Override
    public void setSuperPermDefinition(PermDefinition permDf) {
        superPermDef = permDf;
    }

    @Override
    public PermDefinition getUserPermDefinition() {
        return userPermDef;
    }

    @Override
    public void setUserPermDefinition(PermDefinition permDf) {
        userPermDef = permDf;
    }

      
}

