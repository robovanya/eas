/*******************************************************************************
 **
 ** EaS_dsk.java
 **
 ** Uzivatelske prostredie systemu   EaSys V1
 **
 ** 04.09.2014 - Robo Vanya
 **
 *******************************************************************************/

package system.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.sql.Connection;
import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import system.FnEaS;
import system.Kernel;
import easys.system.prog.sysconfig.Pnl_licencia;
import eoc.database.DBconnection;
import eoc.messages.Win_chat;
import java.awt.Dimension;
/**
 *
 * @author rvanya
 */
public class Desktop extends eoc.widgets.FObject  { 

    private String runLevel;
 
    Pnl_licencia pnl_licencia = new Pnl_licencia();
    Image imgEasys = Toolkit.getDefaultToolkit().getImage("easicon.png");
    private final int oldindex=-1;
    private String currentModul; //2012-10-2
    private eoc.readers.PingPongFileReader ppRdr;
    private String      sVyber_RetValCol = "";
    private String      sVyber_RetVal = "";
    private Win_chat win_chat;
    
    public Desktop(Kernel krn, String runAs) throws IOException {
        initComponents();
  

        this.setLocation(25, 25);
        setVisible(true);
        this.krn = krn;
        this.runLevel = runAs;
        if (runLevel == null) runLevel = "DESKTOP";
        lblRunLevel.setText(krn.getWrapperAppName() + " - " + runLevel);
        lblRunLevel.setToolTipText("Funkčná/riadiaca vrstva behu systému EaSys");
        krn.initOutStream();
        krn.setDsk(this);        
        krn.setDskPane(desktopPanel);
        krn.setScrollPane(desktopScrollPane);
        krn.setPnl_cron(eOC_Pnl_cron1);
        krn.setPnl_functions(eOC_Pnl_Functions);
        krn.setPnl_mdl(eOC_Pnl_modules);
//        krn.setEOC_chat((eoc.IEOC_chat) dsk_chat);
        eOC_Pnl_Functions.setKrn(krn);
        eOC_Pnl_ConnectionInfo.setKrn(krn);
        eOC_Pnl_ConnectionInfo.setKrn(krn);

        MyCn = krn.getDBcnWork();      
        //CnIn = krn.getCnIn();      
        eOC_Pnl_StandardMessages.initialize();
        ppRdr = new eoc.readers.PingPongFileReader();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        EAS_vyber = new javax.swing.JDialog();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        dBT_XTB_vyber1 = new eoc.xinterface.XTB_vyber();
        desktopScrollPane = new javax.swing.JScrollPane();
        desktopPanel = new javax.swing.JPanel();
        lblRunLevel = new javax.swing.JLabel();
        pnl_bottom = new javax.swing.JPanel();
        eOC_Pnl_ConnectionInfo = new system.desktop.Pnl_ConnectionInfo();
        eOC_Pnl_StandardMessages = new eoc.messages.Pnl_StandardMessages();
        eOC_Pnl_cron1 = new system.cron.Pnl_cron();
        pnl_top = new javax.swing.JPanel();
        eOC_Pnl_modules = new system.desktop.Pnl_modules();
        eOC_Pnl_Functions = new system.desktop.Pnl_Functions();

        EAS_vyber.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                EAS_vyberWindowClosed(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                EAS_vyberWindowDeactivated(evt);
            }
        });
        EAS_vyber.addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                EAS_vyberWindowStateChanged(evt);
            }
        });

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Zruš");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dBT_XTB_vyber1Layout = new javax.swing.GroupLayout(dBT_XTB_vyber1);
        dBT_XTB_vyber1.setLayout(dBT_XTB_vyber1Layout);
        dBT_XTB_vyber1Layout.setHorizontalGroup(
            dBT_XTB_vyber1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        dBT_XTB_vyber1Layout.setVerticalGroup(
            dBT_XTB_vyber1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 331, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout EAS_vyberLayout = new javax.swing.GroupLayout(EAS_vyber.getContentPane());
        EAS_vyber.getContentPane().setLayout(EAS_vyberLayout);
        EAS_vyberLayout.setHorizontalGroup(
            EAS_vyberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EAS_vyberLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 188, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(66, 66, 66))
            .addGroup(EAS_vyberLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dBT_XTB_vyber1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        EAS_vyberLayout.setVerticalGroup(
            EAS_vyberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EAS_vyberLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dBT_XTB_vyber1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(EAS_vyberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("EaSys beta");
        setIconImage(imgEasys);
        setMinimumSize(new java.awt.Dimension(1024, 768));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });

        desktopScrollPane.setBackground(new java.awt.Color(204, 153, 255));
        desktopScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        desktopPanel.setBackground(new java.awt.Color(204, 255, 204));
        desktopPanel.setAutoscrolls(true);
        desktopPanel.setPreferredSize(new java.awt.Dimension(750, 450));
        desktopPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                desktopPanelComponentResized(evt);
            }
        });

        lblRunLevel.setFont(new java.awt.Font("Andalus", 1, 14)); // NOI18N
        lblRunLevel.setForeground(new java.awt.Color(102, 0, 102));
        lblRunLevel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRunLevel.setText("DESKTOP");

        javax.swing.GroupLayout desktopPanelLayout = new javax.swing.GroupLayout(desktopPanel);
        desktopPanel.setLayout(desktopPanelLayout);
        desktopPanelLayout.setHorizontalGroup(
            desktopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, desktopPanelLayout.createSequentialGroup()
                .addContainerGap(883, Short.MAX_VALUE)
                .addComponent(lblRunLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        desktopPanelLayout.setVerticalGroup(
            desktopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(desktopPanelLayout.createSequentialGroup()
                .addComponent(lblRunLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(620, Short.MAX_VALUE))
        );

        desktopScrollPane.setViewportView(desktopPanel);

        javax.swing.GroupLayout pnl_bottomLayout = new javax.swing.GroupLayout(pnl_bottom);
        pnl_bottom.setLayout(pnl_bottomLayout);
        pnl_bottomLayout.setHorizontalGroup(
            pnl_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bottomLayout.createSequentialGroup()
                .addComponent(eOC_Pnl_ConnectionInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eOC_Pnl_StandardMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eOC_Pnl_cron1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnl_bottomLayout.setVerticalGroup(
            pnl_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bottomLayout.createSequentialGroup()
                .addGroup(pnl_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(eOC_Pnl_ConnectionInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(eOC_Pnl_StandardMessages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(eOC_Pnl_cron1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(7, 7, 7))
        );

        javax.swing.GroupLayout pnl_topLayout = new javax.swing.GroupLayout(pnl_top);
        pnl_top.setLayout(pnl_topLayout);
        pnl_topLayout.setHorizontalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(eOC_Pnl_modules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnl_topLayout.setVerticalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(eOC_Pnl_modules, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(desktopScrollPane, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pnl_top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(eOC_Pnl_Functions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eOC_Pnl_Functions, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(desktopScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_bottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );

        getAccessibleContext().setAccessibleName("EaSys_desktop");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public String initialize(Kernel krnl, DBconnection cnX) {
        this.krn = krnl;
        win_chat = new Win_chat();
        setScreenDefaultDimension();
        return "";
    }
    public void setCronState(String sState) {
         eOC_Pnl_Functions.setCronState(sState);
    };

    @Override
    public String afterInitialize() {
        win_chat.initialize(krn, MyCn);
        krn.setWin_chat(win_chat);
        krn.applyDefaultFont();

        return ""; //To change body of generated methods, choose Tools | Templates.
    }

/*
    public String initialize() throws NoSuchMethodException, IOException, 
                         FileNotFoundException, IllegalAccessException,
                         IllegalArgumentException, InvocationTargetException,
                         URISyntaxException {
        dsk_messages = new easys.EaS_dsk_messages();
        dsk_chat = new easys.EaS_dsk_chat();
        krn.setEOC_chat((eoc.IEOC_chat) dsk_chat);
        
         return "";
    };
*/
    private boolean parseMdlDefFile (String defFile)
            throws NoSuchMethodException, IOException, FileNotFoundException,
                   IllegalAccessException, IllegalArgumentException,
                   InvocationTargetException {
        Method mtd;
        mtd = this.getClass().getMethod("handleMdlDefFileRow", new Class[] {String.class});
        mtd.setAccessible(true);
        // spracovanie definicneho suboru modulov
        //krn.krnMsg("ppRdr is null== " + (ppRdr == null)
        //+ "\nmtd is null== " + (mtd==null) 
        //+ "\ndefFile == " + defFile);
        //System.out.println("defFiledefFile==" + defFile);
        ppRdr.readFileByMethod_csv((Object) this, defFile, mtd);
        return true;
    }
    
    public void handleMdlDefFileRow(String MdlDefFileRow) 
        throws NoSuchMethodException, IOException, FileNotFoundException,
               IllegalAccessException, IllegalArgumentException, 
               InvocationTargetException, URISyntaxException {
        if (MdlDefFileRow.startsWith("//") // je to poznamkovy/(vatsinou) header riadok
            | MdlDefFileRow.startsWith("|")  // je to prazdny riadok (chyba $typ_riadku)
            | MdlDefFileRow.equals("") // uplne prazdny riadok
           ) {
            return; 
        }
        krn.EOC_XAddModul(
             FnEaS.sEntry(2, MdlDefFileRow, "|").trim(), // skratka
             FnEaS.sEntry(3, MdlDefFileRow, "|").trim(), // tooltip
             FnEaS.sEntry(4, MdlDefFileRow, "|").trim()); //
    }
    
    
    public void loadModul(String sModulName) throws IOException, NoSuchMethodException, 
        FileNotFoundException, IllegalAccessException, 
        IllegalArgumentException, InvocationTargetException {
        String mdlDefFileName;
        String osSlash = krn.get_os_slash();
        File f;
        mdlDefFileName = krn.getSystemDirectory().getAbsolutePath() 
                         + osSlash + sModulName.toLowerCase();
        f = new File(mdlDefFileName);
        if (!f.exists()) {
            f.mkdir();
        }
        mdlDefFileName = krn.getSystemDirectory().getAbsolutePath() 
                         +  osSlash + sModulName.toLowerCase() + osSlash + "definitions";
        f = new File(mdlDefFileName);
        if (!f.exists()) {
            f.mkdir();
        }
        mdlDefFileName = krn.getSystemDirectory().getAbsolutePath() +
                    osSlash + sModulName.toLowerCase() + osSlash + "definitions" + 
                   osSlash + "modul.def";
//QQQ        System.out.println("MODUL DEFINITION FILE:" + mdlDefFileName + "\n");
        f = new File(mdlDefFileName);
        if (!f.exists()) {
            final Path dst = Paths.get(mdlDefFileName);
            BufferedWriter writer = 
                Files.newBufferedWriter(dst,Charset.forName("cp1250"));
            //Citanie suboru po riadkoch
            String sLine;
            sLine =
"// Definicie menu modulu UBF_NZBD\n" +
"// Mozne platne riadky sa zacinaju znakom ${OBJEKT_TYPE}\n" + 
"// a su delene znakom |\n" +
"// \n" +
"// !! TENTO SUBOR NEOPRAVOVAT !!\n" +
"// !! Jeho obsah je udrziavany jadrom systemu EaSys\n" +
"// !! Jadro: " + krn.getAppName() + "  " + krn.getAppVersion() + "\n" +
"// \n" +
"//==========================================================================================\n" +
"//typ_objektu  |skratka_modulu |Tooltyp   |Stav modulu (STOPPED,NONE,ACTIVE,...) |ACTIVATION_KEY\n" +
"//==========================================================================================\n" +
"$MODUL    |Easys               |Základný modul systému EaSys        |STOPPED\n" +
"$MODUL    |UBF_NZBD          |Údržba bytového fondu               |STOPPED\n" +
"$MODUL    |Domus             |Rozšírenie k IS Domus(Anasoft)      |STOPPED\n" +
"$MODUL    |Finus             |Rozšírenie k IS Finus(Anasoft)      |STOPPED\n" +
"//$MODUL    |Kiss           |Kiss úr saját modulja                         |STOPPED\n" +
"MODUL    |Pohoda            |Retšaurácia POHODA                  |NONE\n" +
"//$COMMAND  |krn.selectModul   |EAS";
            writer.write(sLine);
            writer.flush();
            writer.close();
        } // if (!f.exists()) {
        parseMdlDefFile (mdlDefFileName);
       
    }
    
    public void loadModules() throws NoSuchMethodException, IOException, 
        FileNotFoundException, IllegalAccessException, 
        IllegalArgumentException, InvocationTargetException, URISyntaxException {
        switch (runLevel) {
        case "DESKTOP": {
            desktopPanel.setBackground (new Color(204, 204, 255));
            /* old style to date: 2014-07-29 
          krn.EOC_XAddModul("EAS","Základný modul systému EaSys", "STOPPED");
            krn.EOC_XAddModul("UBF_NZBD","Údržba bytového fondu", "STOPPED");
            krn.EOC_XAddModul("Domus","Rozšírenie k IS Domus(Anasoft)", "STOPPED");
            krn.EOC_XAddModul("Finus","Rozšírenie k IS Finus(Anasoft)", "STOPPED");
            krn.EOC_XAddModul("Atti","Rozšírenie k IS Finus(Anasoft)", "NONE");
            krn.EOC_XAddModul("Učt","Účtovníctvo", "NONE");
            krn.EOC_XAddModul("HRS","Hruskar ur", "STOPPED");
            krn.EOC_XAddModul("Pohoda","Reštaurácia POHODA", "NONE");
            */
            // new style
            /*
            String mdlDefFileName = "./sys/easys/definitions/modul.def";
            if (mdlDefFileName==null) {
                
            }
            parseMdlDefFile (mdlDefFileName);
            */
            // newsejsi style :o) - 28.4.2015 
////            krn.OutPrintln("1AAA");
            loadModul("easys");
////            krn.OutPrintln("2AAA");
            krn.selectModul("EASYS");
////            krn.OutPrintln("3AAA");
            break;
        }
        case "ENGINE": {
            desktopPanel.setBackground (new Color(204, 255, 204));
            krn.EOC_XAddModul("Nástroje","Centrálny server modul systému EaSys", "STOPPED");
            krn.selectModul("Nástroje");
            break;
        }
        case "ADMIN": {
            desktopPanel.setBackground (new Color( 255, 204, 204));
            krn.EOC_XAddModul("Administrátor","Administrácia prostredia systému EaSys", "STOPPED");
            krn.selectModul("Administrátor");
            break;
        }
        } // switch (runLevel) {
        
    }
    public void dskMessage(String sMsg) {
       eOC_Pnl_StandardMessages.setMessage(null,sMsg);
    }
    public void dskMessageClear(String sMsg) {
       eOC_Pnl_StandardMessages.setActivity(null, sMsg, 0, 0);
    }
    
    public void dskSetActivity(String lbl, String sMsg, int akt, int max) {
       eOC_Pnl_StandardMessages.setActivity(lbl, sMsg, akt, max);
    }
    
    public void dskSetActivity(int akt, int max) {
       eOC_Pnl_StandardMessages.setActivity(akt, max);
    }

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusGained

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        this.validate();
    }//GEN-LAST:event_formWindowStateChanged

    private void desktopPanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_desktopPanelComponentResized
            if (!(krn == null)) krn.resizeModulTabs(/*this.getSize()*/);
        // TODO add your handling code here:
    }//GEN-LAST:event_desktopPanelComponentResized

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        sVyber_RetVal = "OK";
        EAS_vyber.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        sVyber_RetVal = "<CANCELED>";
        EAS_vyber.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void EAS_vyberWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_EAS_vyberWindowClosed
        sVyber_RetVal = "<WIN_CLOSED>";
        // TODO add your handling code here:
    }//GEN-LAST:event_EAS_vyberWindowClosed

    private void EAS_vyberWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_EAS_vyberWindowStateChanged
        sVyber_RetVal = "<WIN_STATE_CHANGED> z " + evt.getOldState() + " na " + evt.getNewState();
    }//GEN-LAST:event_EAS_vyberWindowStateChanged

    private void EAS_vyberWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_EAS_vyberWindowDeactivated
        sVyber_RetVal = "<WIN_DEACTIVATED>";
        // TODO add your handling code here:
    }//GEN-LAST:event_EAS_vyberWindowDeactivated

   public void SetKernel(Kernel k) {
        krn = k;
    }

   public void SetConn (DBconnection cX) {
        MyCn = cX;
    }

   public void setAppWindLabel (String sLbl) { this.setTitle(sLbl); }

    @Override
    public String destroy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String receiveMessage(eoc.EOC_message eocMsg) {

        // musi byt ako prva instrukcia v metode !!!
        if (eocMsg==null) return FnEaS.nullEocMessageResponse(myObjectID);
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DBconnection getConn() {
        return MyCn;
    }

    @Override
    public String setConn(DBconnection cX) {
        MyCn = cX;
        return "";
    }

    @Override
    public String setKrn(Kernel krnl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getEOC_objectType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setConnectionStatus(int status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isContainer() {
        return true;
    }
    
    public void dskAddMessage(String sMsg) {
       //QQQQAA eOC_messages1.AddMessage(sMsg);
    }

    public String getRunLevel() {
        return runLevel; 
    }
   
   class VXTabbedPane extends JTabbedPane {

    private JTabbedPane pane = this;    
    public String nazov;
    
    public VXTabbedPane(String nazov){  
        this.nazov = nazov;
    }
    
    public void setNazov(String nazov){
        this.nazov = nazov;
    }
    
    public String getNazov(){
        return nazov;
    }

    // Atti
    public void addClosableTab(String titulka, Component comp) {
        boolean moze = true;
        for(int i=0; i<pane.getTabCount(); i++ ){
            if(pane.getComponentAt(i).equals(comp)){
                moze = false;   // aby nebolo mozne dva krat pridat presne to iste
            }
        }
        if(moze){
            pane.add(comp);        
            pane.setTabComponentAt(pane.getTabCount()-1, new VXTabbedPane.TabButton(titulka));
            pane.setSelectedIndex(pane.getTabCount()-1); // po pridani, nech je tab hned aj selectovany
        }
        
    } // public void addClosableTab(String titulka, Component comp)
    
    // Atti
    class TabButton extends JPanel {
          
        JLabel titulka = new JLabel();
        JLabel vypinac = new JLabel();
        
        public TabButton( String label ) {
        setOpaque(false);
        titulka.setText(label);
        vypinac.setText("E");
        vypinac.setForeground(Color.BLACK);
        vypinac.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println("TUTUUUU SE ODCHAAAZIII");
                
                vypnutTab();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                vypinac.setForeground(Color.RED);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                vypinac.setForeground(Color.BLACK);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(titulka, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vypinac))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titulka)
                    .addComponent(vypinac)))
        );
        
    } // public TabButton( String label )
        
    public void vypnutTab(){
        System.out.println("TU SE ODCHAAAZIII");
        pane.remove(pane.indexOfTabComponent( this ) );            
    }
        
    } // class TabButton extends JPanel 
    
} // class VXTabbedPane extends JTabbedPane
   
   public void createVyberFor(eoc.IEOC_VisualObject vThis, Component comp, 
                              String tbl, String colDefs, String retCol, 
                              String sWhere, String sOrder, String sTitle) { 
      sVyber_RetValCol  = retCol;
      //questor_frm = vThis;
      //questor_fld = comp;
      
      // inicializacia objektu eOC_XTB_vyber
      dBT_XTB_vyber1.setConn(MyCn);
      dBT_XTB_vyber1.setKrn(krn);
      dBT_XTB_vyber1.setXTableType("DBTABLE");
      dBT_XTB_vyber1.setMasterTable(tbl);
      dBT_XTB_vyber1.setMasterKey("id_eas_usrgrp","INTEGER");
      dBT_XTB_vyber1.setDisplayedFields(colDefs);
      dBT_XTB_vyber1.setQueryBase("select * from " + tbl);
      // this.setAppWhere("1=0"); // prazdne query
      // this.setAppWhere("id_eas_users < 50000");
      dBT_XTB_vyber1.setiNumFetchedRows(60);
      dBT_XTB_vyber1.initialize(krn,MyCn);
   }

    public String OLD_genVyber(String vybDef) {
        EAS_vyber.doLayout();
        EAS_vyber.repaint();
        EAS_vyber.setModal(true);
        EAS_vyber.setVisible(true);
//        krn.krnMsg("kde je vyber , haaa ?");
        return sVyber_RetVal;
    }

    public void evtDBconnectionsChanged() throws SQLException {
        // udalost sa odosle info-objektu databazovych pripojeni.
        eOC_Pnl_ConnectionInfo.evtDBconnectionsChanged();
    }
 
    public void setScreenDefaultDimension() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Double width = screenSize.getWidth();
        Double height = screenSize.getHeight();       
        width  = width - 100;
        height = height - 100;
        //screenSize.setSize(width, height);
        this.setBounds(50, 50, width.intValue(), height.intValue());
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog EAS_vyber;
    private eoc.xinterface.XTB_vyber dBT_XTB_vyber1;
    private javax.swing.JPanel desktopPanel;
    private javax.swing.JScrollPane desktopScrollPane;
    private system.desktop.Pnl_ConnectionInfo eOC_Pnl_ConnectionInfo;
    private system.desktop.Pnl_Functions eOC_Pnl_Functions;
    private eoc.messages.Pnl_StandardMessages eOC_Pnl_StandardMessages;
    private system.cron.Pnl_cron eOC_Pnl_cron1;
    private system.desktop.Pnl_modules eOC_Pnl_modules;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel lblRunLevel;
    private javax.swing.JPanel pnl_bottom;
    private javax.swing.JPanel pnl_top;
    // End of variables declaration//GEN-END:variables

}
