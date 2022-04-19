package system.desktop;

import eoc.IEOC_IReport;
import eoc.IEOC_ReportSource;
import eoc.IEOC_VisualObject;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import system.Kernel;
import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import system.ireport.Frm_ReportViewer;
import system.ireport.Jtable_ReportParameters;
import test.JFontChooser;

public class Pnl_Functions extends javax.swing.JPanel {

    public Kernel krn;    
    ImageIcon icAll;
    ImageIcon icBlue;
    IEOC_ReportSource myReportSource  = null;    
    IEOC_VisualObject myTargetObject  = null;    
    IEOC_IReport      myCurrentReport = null;    
    Jtable_ReportParameters tbl_ReportParameters;
    JScrollPane jTableScrollPane;
    
    TableColumn colProp;
    TableColumn colValue;
    TableColumn colDesc;
    
    public Pnl_Functions() {
        initComponents();
        icAll  = new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/message_all2.png")); // NOI18N
        icBlue = new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/message_blue.png")); // NOI18N

        tbl_ReportParameters = new Jtable_ReportParameters(krn);
                tbl_ReportParameters.setFont(new java.awt.Font("Tahoma", 0, 14));
/*                
        tbl_ReportParameters.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {{null, null, null}},
            new String [] {"Parameter", "Hodnota", "Popis"}));
*/        
        //boolean[] canEdit = new boolean [] {false, true, false};

        tbl_ReportParameters.setRowHeight(24);
        
        jTableScrollPane = new JScrollPane(tbl_ReportParameters);
 
        colProp  = tbl_ReportParameters.getColumnModel().getColumn(0);
        colValue = tbl_ReportParameters.getColumnModel().getColumn(1);
        colDesc  = tbl_ReportParameters.getColumnModel().getColumn(2);
        
        colProp.setMaxWidth(240);
        colProp.setPreferredWidth(120);
//        tbl_ReportParameters.getColumnModel().getColumn(0).setMaxWidth(60);
        colValue.setResizable(false);
        colDesc.setResizable(true);

        jTableScrollPane.setViewportView(tbl_ReportParameters);

        javax.swing.GroupLayout pnl_tableLayout = new javax.swing.GroupLayout(pnl_table);
        pnl_table.setLayout(pnl_tableLayout);
        pnl_tableLayout.setHorizontalGroup(
            pnl_tableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTableScrollPane)
        );
        pnl_tableLayout.setVerticalGroup(
            pnl_tableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
        );
        
    }
    

  
    public Kernel getKrn() {
        return krn;
    }

    public void setKrn(Kernel krn) {
        this.krn = krn;
        xD_ParameterEditor.initialize(krn, krn.getDBcnWork());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu_tools = new javax.swing.JPopupMenu();
        jMenuItem_napoveda = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem_ShowEOC_links = new javax.swing.JMenuItem();
        jMenuItem_ShowMenuStructure = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem_activeThreads = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem_defaultFont = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem_KernelVerbosity = new javax.swing.JMenuItem();
        xD_ParameterEditor = new system.ireport.XD_ParameterEditor();
        jPanel2 = new javax.swing.JPanel();
        btn_OK = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tf_NazovZostavy = new javax.swing.JTextField();
        pnl_table = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jTgBtn_cron = new javax.swing.JToggleButton();
        btn_calendar = new javax.swing.JButton();
        btn_events = new javax.swing.JButton();
        btn_tools = new javax.swing.JButton();
        btn_log = new javax.swing.JButton();
        btn_currentUsers = new javax.swing.JButton();
        btn_print = new javax.swing.JButton();
        btn_chat = new javax.swing.JButton();

        jMenuItem_napoveda.setText("Nápoveda");
        jMenuItem_napoveda.setToolTipText("Nápoveda k aktuálnemu objektu obrazovky (ZATIAL NIE JE IMPLEMETOVANÁ)");
        jMenuItem_napoveda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_napovedaActionPerformed(evt);
            }
        });
        jPopupMenu_tools.add(jMenuItem_napoveda);
        jPopupMenu_tools.add(jSeparator1);

        jMenuItem_ShowEOC_links.setText("Aktuálna EOC_link štruktúra");
        jMenuItem_ShowEOC_links.setToolTipText("Výpis aktuálnej  EOC_link štruktúry, evidovanej jadrom Easys do Log-u");
        jMenuItem_ShowEOC_links.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_ShowEOC_linksActionPerformed(evt);
            }
        });
        jPopupMenu_tools.add(jMenuItem_ShowEOC_links);

        jMenuItem_ShowMenuStructure.setText("Aktuálna štruktúra Menu");
        jMenuItem_ShowMenuStructure.setToolTipText("Výpis aktuálne evidovanej štruktúry Menu jadrom Easys do Log-u");
        jMenuItem_ShowMenuStructure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_ShowMenuStructureActionPerformed(evt);
            }
        });
        jPopupMenu_tools.add(jMenuItem_ShowMenuStructure);
        jPopupMenu_tools.add(jSeparator2);

        jMenuItem_activeThreads.setText("Aktívne paralelné vlákna (Threads)");
        jMenuItem_activeThreads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_activeThreadsActionPerformed(evt);
            }
        });
        jPopupMenu_tools.add(jMenuItem_activeThreads);
        jPopupMenu_tools.add(jSeparator3);

        jMenuItem_defaultFont.setText("Prednastavený font");
        jMenuItem_defaultFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_defaultFontActionPerformed(evt);
            }
        });
        jPopupMenu_tools.add(jMenuItem_defaultFont);
        jPopupMenu_tools.add(jSeparator4);

        jMenuItem_KernelVerbosity.setText("Diskrétnosť systémového jadra");
        jMenuItem_KernelVerbosity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_KernelVerbosityActionPerformed(evt);
            }
        });
        jPopupMenu_tools.add(jMenuItem_KernelVerbosity);

        xD_ParameterEditor.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                xD_ParameterEditorWindowClosing(evt);
            }
        });

        btn_OK.setText("OK");
        btn_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_OKActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Názov zostavy:");

        tf_NazovZostavy.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tf_NazovZostavy.setText("tf_NazovZostavy");
        tf_NazovZostavy.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(tf_NazovZostavy, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(207, 207, 207)
                .addComponent(btn_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tf_NazovZostavy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btn_OK)
                .addContainerGap())
        );

        pnl_table.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnl_tableLayout = new javax.swing.GroupLayout(pnl_table);
        pnl_table.setLayout(pnl_tableLayout);
        pnl_tableLayout.setHorizontalGroup(
            pnl_tableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnl_tableLayout.setVerticalGroup(
            pnl_tableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout xD_ParameterEditorLayout = new javax.swing.GroupLayout(xD_ParameterEditor.getContentPane());
        xD_ParameterEditor.getContentPane().setLayout(xD_ParameterEditorLayout);
        xD_ParameterEditorLayout.setHorizontalGroup(
            xD_ParameterEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xD_ParameterEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xD_ParameterEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_table, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        xD_ParameterEditorLayout.setVerticalGroup(
            xD_ParameterEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xD_ParameterEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_table, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("jButton1");

        jTgBtn_cron.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/cron_blue.png"))); // NOI18N
        jTgBtn_cron.setSelected(true);
        jTgBtn_cron.setToolTipText("<HTML>Stav rozhrania CRON<BR>(nezávislá paralelne bežiaca vrstva)<BR><BR>Keď je démon CRON zapnutý,na pozadí<BR>bežia automatické procesy systému EaSys.<BR><BR>Inak <B><I>nem</I></B> viete, čo sa deje v systéme.<BR><BR>(na čo ste vlastne zvyknutí.)&nbsp&nbsp&nbsp<B>:oO)</B></HTML>");
        jTgBtn_cron.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTgBtn_cronMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTgBtn_cronMouseExited(evt);
            }
        });
        jTgBtn_cron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTgBtn_cronActionPerformed(evt);
            }
        });

        btn_calendar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/calendar_blue.png"))); // NOI18N
        btn_calendar.setToolTipText("Môj kalendár");
        btn_calendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_calendarActionPerformed(evt);
            }
        });

        btn_events.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/events.png"))); // NOI18N
        btn_events.setToolTipText("Prehliadač systémových udalostí (ZATIAL NIE JE IMPLEMETOVANÁ)");
        btn_events.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eventsActionPerformed(evt);
            }
        });

        btn_tools.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_tools.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/tools.png"))); // NOI18N
        btn_tools.setToolTipText("Aktualne dostupné všeobecné nástroje a služby systému Easys");
        btn_tools.setComponentPopupMenu(jPopupMenu_tools);
        btn_tools.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_toolsActionPerformed(evt);
            }
        });

        btn_log.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/log.png"))); // NOI18N
        btn_log.setToolTipText("Prehliadač udalostí vlastnej applikácie (správy jadra Easys (Kernel-messages))");
        btn_log.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_logActionPerformed(evt);
            }
        });

        btn_currentUsers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/users.png"))); // NOI18N
        btn_currentUsers.setToolTipText("Aktuálne prihlásení užívatelia (ZATIAL NIE JE IMPLEMETOVANÁ)");
        btn_currentUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_currentUsersActionPerformed(evt);
            }
        });

        btn_print.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/print_blue.png"))); // NOI18N
        btn_print.setToolTipText("Volanie tlačovej funkcie aktuálneho objektu obrazovky, pokial ju obsahuje.");
        btn_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_printActionPerformed(evt);
            }
        });

        btn_chat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/message_all2.png"))); // NOI18N
        btn_chat.setToolTipText("Komunikačné rozhranie (správy, chat)");
        btn_chat.setAutoscrolls(true);
        btn_chat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_chatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jTgBtn_cron, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_chat, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_print, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_calendar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_currentUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_events, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_tools, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_log, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_chat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTgBtn_cron, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btn_calendar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btn_currentUsers, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_events, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_tools, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_log, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_print, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_calendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_calendarActionPerformed
        krn.showMyCalendar();
        // krn.volaco
    }//GEN-LAST:event_btn_calendarActionPerformed

    private void btn_eventsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eventsActionPerformed
        krn.Message("Prehliadač systémových udalostí zatial nie je implementovaná.");
        //krn.volaco
    }//GEN-LAST:event_btn_eventsActionPerformed

    private void btn_toolsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_toolsActionPerformed
       // krn.volaco
        showPopup(evt);
    }//GEN-LAST:event_btn_toolsActionPerformed

    private void showPopup(ActionEvent ae)
    {
        // Get the event source
        Component b=(Component)ae.getSource();
       
        // Get the location of the point 'on the screen'
        Point p=b.getLocationOnScreen();
       
        // Show the JPopupMenu via program
       
        // Parameter desc
        // ----------------
        // this - represents current frame
        // 0,0 is the co ordinate where the popup
        // is shown
        jPopupMenu_tools.show(this,0,0);
       
        // Now set the location of the JPopupMenu
        // This location is relative to the screen
        jPopupMenu_tools.setLocation(p.x,p.y+b.getHeight());
    }

    private void btn_logActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_logActionPerformed
     //   krn.callTestedFn();        // TODO add your handling code here:
        krn.showLog();
    }//GEN-LAST:event_btn_logActionPerformed

    private void jTgBtn_cronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTgBtn_cronActionPerformed

        if (jTgBtn_cron.isSelected()) {
            jTgBtn_cron.setIcon(new javax.swing.ImageIcon(
//                getClass().getResource("/easys/res/img/LEDO_1.png")));
                getClass().getResource("/easys/res/img/cron_blue.png")));
            krn.setCronState("enabled");    
        }
        else {
            jTgBtn_cron.setIcon(new javax.swing.ImageIcon(
//                getClass().getResource("/easys/res/img/LEDO_WA.png")));
                getClass().getResource("/easys/res/img/cron_orange.png")));
            krn.setCronState("disabled");    
            
        }
    }//GEN-LAST:event_jTgBtn_cronActionPerformed

    private void jMenuItem_napovedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_napovedaActionPerformed
        // TODO add your handling code here:
        Kernel.staticMsg("<HTML>Načo Vám je nejaká nápoveda ?<BR><BR>" 
                + "<B>EaSys</B> je taký jednoduchý systém, " 
                + "že jeho použitie<BR> nemôže byť predsa " 
                + "pre Vás žiadnym problémom.<BR><BR>"
                + "A užívateľské rozhranie systému <B>NZBD</B> "
                + "už vôbec nie.<BR><BR>&NBSP&NBSP&NBSP&NBSP&NBSP&NBSP&NBSP"
                + "&NBSP&NBSP&NBSP&NBSP&NBSP&NBSP&NBSP&NBSP&NBSP<B>:oO)</B><BR><BR></HTML>");
    }//GEN-LAST:event_jMenuItem_napovedaActionPerformed

    private void jMenuItem_ShowEOC_linksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_ShowEOC_linksActionPerformed
        krn.showEOC_links("Log");
        btn_log.doClick();
    }//GEN-LAST:event_jMenuItem_ShowEOC_linksActionPerformed

    private void jMenuItem_ShowMenuStructureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_ShowMenuStructureActionPerformed
        krn.showMenuBar();
        btn_log.doClick();
    }//GEN-LAST:event_jMenuItem_ShowMenuStructureActionPerformed

    @Override
    public void setFont(Font f) {
        super.setFont(f); //To change body of generated methods, choose Tools | Templates.
        if (f == null) return;
        if (jMenuItem_defaultFont != null) {
            jMenuItem_defaultFont.setFont(f);
            jMenuItem_defaultFont.setToolTipText(f.getFamily() + "#" + f.getStyle() + "#" + f.getSize());
        }
    }

    private void btn_currentUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_currentUsersActionPerformed
        krn.Message("Prehliadač prihlásených užívateľov zatial nie je implementovaná.");
        // krn.volaco
    }//GEN-LAST:event_btn_currentUsersActionPerformed

    private void btn_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_printActionPerformed

        if (myReportSource == null) {
            krn.Message("W", "Nie je dostupný žiadný droj zostavy." , "Upozornenie");
            return;
        }
        myCurrentReport = myReportSource.getEocReport();
        System.out.println("##EDITABLESS##1: " + myCurrentReport.getEditableParameterCount());
        if (myCurrentReport.getEditableParameterCount() > 0) {
            xD_ParameterEditor.initializeAs(myTargetObject, true, myCurrentReport, tbl_ReportParameters);
        System.out.println("##EDITABLESS##2: " + myCurrentReport.getEditableParameterCount());
            xD_ParameterEditor.setLocationRelativeTo(krn.getDsk());
            System.out.println("##EDITABLESS##2 1: ");
            tf_NazovZostavy.setText(myCurrentReport.getReportName());
            xD_ParameterEditor.setVisible(true);
            System.out.println("##EDITABLESS##2 2: ");
            myCurrentReport.displayHashMap(); // vypis obsahu tabulky do log-u
            System.out.println("##EDITABLESS##2 3: ");

        System.out.println("##EDITABLESS##3: " + myCurrentReport.getEditableParameterCount());
        }
////            oszt itt kihuzni a returnot - letesztelni valtozik e a hash-table, es felhasznalni
                
        Frm_ReportViewer frpv = new Frm_ReportViewer();
        frpv.initialize(krn,krn.getDBcnWork());
        Dimension d = krn.getDsk().getSize(); 
        d.height = d.height - 96;
        d.width  = d.width - 48;
        frpv.setSize(d);
        frpv.setLocationRelativeTo(krn.getDsk());
        frpv.setVisible(true);
        frpv.setReport(myCurrentReport);
    }//GEN-LAST:event_btn_printActionPerformed

    private void btn_chatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_chatActionPerformed
        krn.showChat();
    }//GEN-LAST:event_btn_chatActionPerformed

    private void jMenuItem_defaultFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_defaultFontActionPerformed
        // TODO add your handling code here:
        JFontChooser dialog = new JFontChooser();     
        String s = jMenuItem_defaultFont.getToolTipText();
        String  fname = "Arial";
        Integer ftype = 0; 
        Integer fsize = 12;
        if (s != null && s.length() > 0) {
            String[] ss = s.split("#");
            if (ss.length == 3) {
                fname = ss[0];
                ftype = Integer.parseInt(ss[1]);
                fsize = Integer.parseInt(ss[2]);
            }
        }  
        dialog.setValues(fname, ftype, fsize);
        Font f = dialog.showDialog(jMenuItem_defaultFont, "Výber fontu");
        if (f == null) return; // nic sa nevybralo
        jMenuItem_defaultFont.setFont(f);
        krn.setDefaultFont(f);
       // JPanel jp = (JPanel) krn.getDsk();
        /*
        krn.setWidgetFonts( (Container) krn.getDsk().getRootPane(), f);
        System.out.println("FOOTNAME: " + f.getFontName());
        System.out.println("FOOTNAMEx: " + f.getFamily());
        System.out.println("FOOTSTYLE: " + f.getStyle());
        System.out.println("FOOTSIZE: " + f.getSize());
        System.out.println("DefaultFont?????>>>>>"+ f.getFontName() + "#" + f.getStyle() + "#" + f.getSize());
        */
        krn.setUserProperty("DefaultFont", f.getFamily() + "#" + f.getStyle() + "#" + f.getSize());
        krn.applyDefaultFont();
        jMenuItem_defaultFont.setToolTipText(f.getFamily() + "#" + f.getStyle() + "#" + f.getSize());
/*
dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);                                      
dialog.setVisible(true);                                                                                
if (!dialog.isCancelSelected()) {                                                                       
    System.out.println("Selected font is: " + dialog.getSelectedFont());                                
}                 
*/
    }//GEN-LAST:event_jMenuItem_defaultFontActionPerformed

    private void jMenuItem_activeThreadsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_activeThreadsActionPerformed
        Set threadSet = Thread.getAllStackTraces().keySet();
        String sThreads = "";
        for (Object th : threadSet) {
            Thread t = (Thread) th;
            String s = "ACTIVETHREAD: " + t.getName() + " Is Daemon " + t.isDaemon() + " Is Alive " + t.isAlive();
            System.out.println(s);
            sThreads = sThreads + "\n" + s;
        }
        if (sThreads.length() > 0)
            Kernel.staticMsg(sThreads, "Výpis aktívnych vlákien (Threads)");
        else
            Kernel.staticMsg("Žiadne vlákno nie je aktívne.", "Výpis aktívnych vlákien (Threads)");
    }//GEN-LAST:event_jMenuItem_activeThreadsActionPerformed

    private void jTgBtn_cronMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTgBtn_cronMouseEntered
         ToolTipManager.sharedInstance().setDismissDelay(120000);
    }//GEN-LAST:event_jTgBtn_cronMouseEntered

    private void jTgBtn_cronMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTgBtn_cronMouseExited
        ToolTipManager.sharedInstance().setDismissDelay(40000);
    }//GEN-LAST:event_jTgBtn_cronMouseExited

    private void jMenuItem_KernelVerbosityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_KernelVerbosityActionPerformed
        int ans;
        /*
        String sans;
        sans = Integer.parseInt( JOptionPane.showInputDialog(krn.getDsk(),
                "Text",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                null,
                "[sample text to help input]"));
        */
        String ukcns = JOptionPane.showInputDialog("Zadajte úroveň ukecanosti systémového jadra (EaSys-kernel). (0-6)");
        
        try {
        Integer i = Integer.parseInt(ukcns);
        if (i < 0 || i > 6) {
           Kernel.staticMsg("Musite zadať číslo od nula po šesť.\n\n"
                          + "0 = žiadna      6 = maximálne ukecané jadro.");
                }
        else krn.setVerbose(i);
        }
        catch (Exception ex) {
           Kernel.staticMsg("Musite zadať číslo od nula po šesť.\n\n"
                          + "0 = žiadna      6 = maximálne ukecané jadro.");
        }

    }//GEN-LAST:event_jMenuItem_KernelVerbosityActionPerformed

    private void btn_OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_OKActionPerformed
        // potvrdenie pripadnej editacie parametra
        if ( tbl_ReportParameters.isEditing()) {
             tbl_ReportParameters.getCellEditor().stopCellEditing();
        }
        xD_ParameterEditor.dispose();
    }//GEN-LAST:event_btn_OKActionPerformed

    private void xD_ParameterEditorWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_xD_ParameterEditorWindowClosing
        xD_ParameterEditor.savePropertyTableToHashMap();
    }//GEN-LAST:event_xD_ParameterEditorWindowClosing

    public void setCronState(String sState) {
        jTgBtn_cron.setSelected(sState.equalsIgnoreCase("Enabled"));
        jTgBtn_cron.getActionListeners()[0].actionPerformed(null);
    };
    public void notifyNewMessages(boolean bExists) {
                btn_chat.setIcon(bExists?icBlue:icAll);
                notifyNewMessagesPokus();
                btn_chat.setIcon(icAll);
        
    }
    
    public void notifyNewMessagesPokus() {

   new Thread(new Runnable() { public void run() {
       Thread.currentThread().setName("EaSys_blinker");
        for (int i = 1; i < 9; i++) {
            if (i%2 == 0) {
                btn_chat.setIcon(icBlue);
               // icBlue.getImage().flush();
            }
            else {
                btn_chat.setIcon(icAll);
                //icAll.getImage().flush();
            }
            if (btn_chat.getIcon() != null)
            ((ImageIcon) btn_chat.getIcon()).getImage().flush();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Pnl_Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
        /*
            */
        ////    System.out.println("IIIII::::" + i + " (i%2 == 0) -- " + (i%2 == 0));
        } 
        btn_chat.setIcon(icAll);
        btn_chat.revalidate();
        btn_chat.repaint();
        }}).start();
    }
    
    public void setPrintTarget(IEOC_ReportSource rsrc) {
       myReportSource = rsrc;
       btn_print.setEnabled(rsrc != null);
    }
    
    public void setTargetEocVisualOject(IEOC_VisualObject vob) {
       myTargetObject = vob;
    }

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_OK;
    private javax.swing.JButton btn_calendar;
    private javax.swing.JButton btn_chat;
    private javax.swing.JButton btn_currentUsers;
    private javax.swing.JButton btn_events;
    private javax.swing.JButton btn_log;
    private javax.swing.JButton btn_print;
    private javax.swing.JButton btn_tools;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuItem_KernelVerbosity;
    private javax.swing.JMenuItem jMenuItem_ShowEOC_links;
    private javax.swing.JMenuItem jMenuItem_ShowMenuStructure;
    private javax.swing.JMenuItem jMenuItem_activeThreads;
    private javax.swing.JMenuItem jMenuItem_defaultFont;
    private javax.swing.JMenuItem jMenuItem_napoveda;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu_tools;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JTable jTable1;
    private javax.swing.JToggleButton jTgBtn_cron;
    private javax.swing.JPanel pnl_table;
    private javax.swing.JTextField tf_NazovZostavy;
    private system.ireport.XD_ParameterEditor xD_ParameterEditor;
    // End of variables declaration//GEN-END:variables
}
