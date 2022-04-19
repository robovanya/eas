/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

import eoc.IEOC_VisualObject;
import eoc.widgets.PObject;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import system.FnEaS;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_event_updater extends PObject {
    IEOC_VisualObject myObjID;
    String dbChooseDefinitionMkon = null;
    String dbChooseDefinitionDokl = null;
    Color defaultcolor;
    protected String sTxnType = ""; // typ transakcie ADD/COPY/UPDATE/DELETE
    private CalendarEvent currCalendarEvent;
    private JDialog myDialogBox;
    // Widgetmi generovane hodnoty
    private Integer iIdMiestoKonania = new Integer(0); // nastavuje sa vyberom miesta konania
    private String  sTabMiestoKonania = ""; // nastavuje sa vyberom miesta konania
    private String  sDoklUlohy = "";
    private Integer iIdDoklUlohy = new Integer(0); // nastavuje sa vyberom dokladu ulohy
    private Integer iIdSubjekt   = new Integer(0); // nastavuje sa vyberom subjektu
    private boolean updatePerformed = false;
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    DateFormatter dateFormatter = new DateFormatter(dateFormat);
    DefaultFormatterFactory datteFormatterFactory;
    /**
     * Creates new form event_updater
     */
    public Pnl_event_updater() {
        this.datteFormatterFactory = new DefaultFormatterFactory(dateFormatter);
        myObjID = this;
        initComponents();
       ftf_d_od_date.setFormatterFactory(datteFormatterFactory);
       ftf_d_do_date.setFormatterFactory(datteFormatterFactory);
       ftf_c_miesto_konania.addMouseListener(new MouseListener() {

           public void mousePressed(MouseEvent evt) { 
              /* 
              if (evt.isControlDown()) {
                 Kernel.stdMsg("<html><B>Informácie k objektu: </B>"
                   + dbTableField.dbColumnInfo.tablename 
                      + "." + dbTableField.dbColumnInfo.columnname
                   + "<BR> Typ objektu: <B>" + evt.getComponent().getClass().getSimpleName() + "</B>"
                   + "<BR> DB_datatype: <B>" + dbTableField.dbColumnInfo.realdatatype + "</B>"
                   + "<BR>Generic_type: <B>" + dbTableField.dbColumnInfo.genericdatatype + "</B>"
                   + "<BR>FORMATstring: <B>" + dbTableField.dbColumnInfo.formatString + "</B>"
                   + "<BR>DBFORMATstring: <B>" + dbFormatString + "</B>"
                   + "<BR>DBFORMATTER: <B>" 
                     + ((myObjID.getFormatter()==null)?"<NULL>":myObjID.getFormatter().toString()) + "</B>"
                   + "<BR><BR>VALUE_FOR_WRITE: <B>" + dbTableField.getValueForWriteAsString("'") + "</B>"
                   + "</html>"
                );
              }
               */
          } 

            @Override
            public void mouseClicked(MouseEvent evt) {
                String retVal;
                if (evt.getClickCount()==2 && dbChooseDefinitionMkon.length() > 0) {
                        retVal = 
// ORIG                        krn.genVyberForMe(myViewer,myObjID, dbChooseDefinitionMkon);
                        krn.genVyberForMe(myObjID,ftf_c_miesto_konania, dbChooseDefinitionMkon);
                        krn.Message("DTfield_MouseClicked_rrretVaaal is: " + retVal);
                        putRetValToValuesMkon(retVal);
                        evt.consume();
                }
            }
            
            public void putRetValToValuesMkon(String rtv) {
                System.out.println("RETTVALFORM_VYBER: " + rtv);
                /*
                String[] ss = rtv.split(",");
                iIdMiestoKonania  = Integer.parseInt(ss[2]);
                ftf_c_miesto_konania.setText(ss[0] + " " + ss[1]);
                */
                rtv = rtv.trim();
//               eoc.iEOC_DBtableField /* eoc.widgets.DTfield */ dtf =
//                       (eoc.iEOC_DBtableField /* eoc.widgets.DTfield */) cmp;
               String rv;
               String vfw = FnEaS.sEntry(FnEaS.iNumEntries(rtv,","), rtv, ",");
               rv = rtv.replace("," + vfw, "");
               ftf_c_miesto_konania.setText(rv);
               iIdMiestoKonania  = Integer.parseInt(vfw);
               try {
                   ftf_c_miesto_konania.commitEdit();
               } catch (ParseException ex) {
                   Logger.getLogger(Pnl_event_updater.class.getName()).log(Level.SEVERE, null, ex);
               }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
              // this.OutPrintln("----mouseReleaseeeeed" + e.paramString());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
               // this.OutPrintln("----mouseEntereeed" + e.paramString());
            }

            @Override
            public void mouseExited(MouseEvent e) {
              //  this.OutPrintln("----mouseExiteeed" + e.paramString());
            }
        });
       
        
        
        // uchovanie zakladnej farby test_fieldov
        defaultcolor = ftf_c_nazov_ulohy.getBackground();
        // vyvolanie reakcie comboboxu miesta konania
        // kvoli aktivacie vyberovej podmienky
        cb_c_tab_miesto_konaniaActionPerformed(null);
        cb_subjektActionPerformed(null);
        cb_c_stavActionPerformed(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txa_c_popis_ucelu = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        btn_save = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btn_cancel = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btn_delete = new javax.swing.JButton();
        jp_pripomenut = new javax.swing.JPanel();
        cb_pripominat = new javax.swing.JComboBox();
        ftf_i_pripomenutie = new javax.swing.JFormattedTextField();
        cb_mj_time = new javax.swing.JComboBox();
        cb_opakovanie = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jp_stav = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        cb_c_stav = new javax.swing.JComboBox();
        ftf_last_stav = new javax.swing.JFormattedTextField();
        btn_add_stav = new javax.swing.JButton();
        jp_time = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tgb_b_spojity_cas_usek = new javax.swing.JToggleButton();
        ftf_c_do_time = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        ftf_c_od_time = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbx_b_celodenna_uloha = new javax.swing.JCheckBox();
        ftf_d_do_date = new javax.swing.JFormattedTextField();
        ftf_d_od_date = new javax.swing.JFormattedTextField();
        jp_header = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ftf_c_nazov_ulohy = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        cb_c_tab_miesto_konania = new javax.swing.JComboBox();
        ftf_c_miesto_konania = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cb_subjekt = new javax.swing.JComboBox();
        cb_typ_dokl_ulohy = new javax.swing.JComboBox();
        ftf_c_dokl_ulohy = new javax.swing.JFormattedTextField();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setMinimumSize(new java.awt.Dimension(520, 540));
        setPreferredSize(new java.awt.Dimension(520, 540));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Popis/poznámky");

        txa_c_popis_ucelu.setColumns(20);
        txa_c_popis_ucelu.setRows(5);
        jScrollPane1.setViewportView(txa_c_popis_ucelu);

        btn_save.setText("Zapíš");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });
        jPanel1.add(btn_save);
        jPanel1.add(filler2);

        btn_cancel.setText("Zruš");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });
        jPanel1.add(btn_cancel);
        jPanel1.add(filler3);

        btn_delete.setText("Vymaž");
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });
        jPanel1.add(btn_delete);

        jp_pripomenut.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jp_pripomenut.setPreferredSize(new java.awt.Dimension(937, 38));

        cb_pripominat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nepripomínať", "Pripomínať" }));
        cb_pripominat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_pripominatActionPerformed(evt);
            }
        });

        ftf_i_pripomenutie.setText("5");
        ftf_i_pripomenutie.setValue(0);

        cb_mj_time.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "minút", "hodín", "deň", "týždeň" }));
        cb_mj_time.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_mj_timeActionPerformed(evt);
            }
        });

        cb_opakovanie.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Neopakovať", "Každý deň", "Každý pracovný deň", "Každý týždeň", "Každý mesiac", "Každý rok", " " }));
        cb_opakovanie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_opakovanieActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Opakovať:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("pred");

        javax.swing.GroupLayout jp_pripomenutLayout = new javax.swing.GroupLayout(jp_pripomenut);
        jp_pripomenut.setLayout(jp_pripomenutLayout);
        jp_pripomenutLayout.setHorizontalGroup(
            jp_pripomenutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_pripomenutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cb_pripominat, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftf_i_pripomenutie, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_mj_time, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_opakovanie, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jp_pripomenutLayout.setVerticalGroup(
            jp_pripomenutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_pripomenutLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jp_pripomenutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cb_pripominat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(cb_opakovanie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftf_i_pripomenutie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_mj_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(5, 5, 5))
        );

        jp_stav.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Stav:");

        cb_c_stav.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nešpecifikovaný", "Vyžaduje akciu", "Prebieha", "Dokončená", "Zrušená" }));
        cb_c_stav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_c_stavActionPerformed(evt);
            }
        });

        ftf_last_stav.setText("jFormattedTextField1");
        ftf_last_stav.setToolTipText("Posledný zadaný stav");
        ftf_last_stav.setEnabled(false);

        btn_add_stav.setText("+");
        btn_add_stav.setToolTipText("Pridanie nového stavu");
        btn_add_stav.setMinimumSize(new java.awt.Dimension(13, 23));
        btn_add_stav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_add_stavActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jp_stavLayout = new javax.swing.GroupLayout(jp_stav);
        jp_stav.setLayout(jp_stavLayout);
        jp_stavLayout.setHorizontalGroup(
            jp_stavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_stavLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cb_c_stav, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftf_last_stav)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_add_stav, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jp_stavLayout.setVerticalGroup(
            jp_stavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_stavLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jp_stavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_c_stav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftf_last_stav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_add_stav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jp_time.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Čas");

        tgb_b_spojity_cas_usek.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        tgb_b_spojity_cas_usek.setIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/spojite_off.png"))); // NOI18N
        tgb_b_spojity_cas_usek.setText("Spojitá ");
        tgb_b_spojity_cas_usek.setToolTipText("Slúži na test prekrývania úloh. Prekrývanie spojitých úloh nie je povolené. Prekrytie bežnou úlohou vyvolá upozornenie systému pri zápise.");
        tgb_b_spojity_cas_usek.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/easys/res/img/spojite_on.png"))); // NOI18N
        tgb_b_spojity_cas_usek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgb_b_spojity_cas_usekActionPerformed(evt);
            }
        });

        ftf_c_do_time.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new eoc.TimeFormatter()));
        ftf_c_do_time.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftf_c_do_timeActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Dátum");

        ftf_c_od_time.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new eoc.TimeFormatter()));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Koniec:");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Začiatok:");

        cbx_b_celodenna_uloha.setText("Celodenná udalosť/úloha");
        cbx_b_celodenna_uloha.setToolTipText("Informatívny údaj kvôli kompaktibilite s Outlook-om a  Thunderbird-om");
        cbx_b_celodenna_uloha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbx_b_celodenna_ulohaActionPerformed(evt);
            }
        });

        ftf_d_do_date.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new eoc.EocDateFormatter()));

        ftf_d_od_date.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new eoc.EocDateFormatter()));
        ftf_d_od_date.setValue(new java.util.Date());
        ftf_d_od_date.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftf_d_od_dateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jp_timeLayout = new javax.swing.GroupLayout(jp_time);
        jp_time.setLayout(jp_timeLayout);
        jp_timeLayout.setHorizontalGroup(
            jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_timeLayout.createSequentialGroup()
                .addGroup(jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftf_d_od_date, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ftf_d_do_date, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ftf_c_do_time, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ftf_c_od_time, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38)
                .addGroup(jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jp_timeLayout.createSequentialGroup()
                        .addComponent(tgb_b_spojity_cas_usek, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addComponent(cbx_b_celodenna_uloha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jp_timeLayout.setVerticalGroup(
            jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_timeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_timeLayout.createSequentialGroup()
                            .addGap(30, 30, 30)
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel6))
                        .addGroup(jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jp_timeLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(ftf_d_od_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftf_d_do_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_timeLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(ftf_c_od_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftf_c_do_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jp_timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_timeLayout.createSequentialGroup()
                            .addComponent(cbx_b_celodenna_uloha)
                            .addGap(51, 51, 51))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_timeLayout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addComponent(tgb_b_spojity_cas_usek, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Názov udalosti/úlohy:");

        ftf_c_nazov_ulohy.setText("jFormattedTextField5");
        ftf_c_nazov_ulohy.setToolTipText("Názov/popis úlohy/udalosti");
        ftf_c_nazov_ulohy.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Miesto konania");

        cb_c_tab_miesto_konania.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nešpecifikované", "VCS", "VCHOD", "BYT" }));
        cb_c_tab_miesto_konania.setToolTipText("Typ miesta, spojenej s udalosťou/úlohou");
        cb_c_tab_miesto_konania.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_c_tab_miesto_konaniaActionPerformed(evt);
            }
        });

        ftf_c_miesto_konania.setText("jFormattedTextField5");
        ftf_c_miesto_konania.setToolTipText("Popis/adresa miesta konania ");
        ftf_c_miesto_konania.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftf_c_miesto_konaniaActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Hospodársky subjekt:");

        cb_subjekt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nešpecifikovaný", "Novozámocké bytové družstvo", "BLOKY" }));
        cb_subjekt.setToolTipText("Hospodárský subjet, spojený s úlohou/udalosťou (súvisí s filtrovaním východiskových dokladov)");
        cb_subjekt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_subjektActionPerformed(evt);
            }
        });

        cb_typ_dokl_ulohy.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nešpecifikovaný", "Požiadavka", "Vystavená objednávka", "Prijatá objednávka", " " }));
        cb_typ_dokl_ulohy.setToolTipText("Typ východiskového dokladu ");
        cb_typ_dokl_ulohy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_typ_dokl_ulohyActionPerformed(evt);
            }
        });

        ftf_c_dokl_ulohy.setText("jFormattedTextField5");
        ftf_c_dokl_ulohy.setToolTipText("Číslo a popis východiskového dokladu");
        ftf_c_dokl_ulohy.setEnabled(false);

        javax.swing.GroupLayout jp_headerLayout = new javax.swing.GroupLayout(jp_header);
        jp_header.setLayout(jp_headerLayout);
        jp_headerLayout.setHorizontalGroup(
            jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_headerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cb_c_tab_miesto_konania, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addComponent(cb_typ_dokl_ulohy, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ftf_c_miesto_konania)
                    .addComponent(ftf_c_nazov_ulohy)
                    .addComponent(ftf_c_dokl_ulohy)
                    .addComponent(cb_subjekt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jp_headerLayout.setVerticalGroup(
            jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_headerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftf_c_nazov_ulohy, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cb_c_tab_miesto_konania, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jp_headerLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(ftf_c_miesto_konania, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_subjekt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cb_typ_dokl_ulohy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftf_c_dokl_ulohy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jp_pripomenut, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                            .addComponent(jp_stav, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jp_header, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jp_time, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jp_header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jp_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jp_stav, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jp_pripomenut, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbx_b_celodenna_ulohaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbx_b_celodenna_ulohaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbx_b_celodenna_ulohaActionPerformed

    private void tgb_b_spojity_cas_usekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgb_b_spojity_cas_usekActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tgb_b_spojity_cas_usekActionPerformed

    private void cb_c_tab_miesto_konaniaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_c_tab_miesto_konaniaActionPerformed
        String s = cb_c_tab_miesto_konania.getSelectedItem().toString();
        dbChooseDefinitionMkon = null;
        iIdMiestoKonania  = 0;
        sTabMiestoKonania = "";

        if (s.equalsIgnoreCase("Dom") || s.equalsIgnoreCase("VCS")) {
            dbChooseDefinitionMkon = 
"#VCS#ID#VCS^Č.VCS,ULICA^Ulica^string^70#VCS,ULICA,ID#Výber VČS##VCS#Výber domu, kde sa má oprava vykonať"
            ;
            sTabMiestoKonania = "VCS";
        }
        else if (s.equalsIgnoreCase("Vchod")) { 
            dbChooseDefinitionMkon = 
"#VCHOD#ID#VCHOD^C. vchodu,ULICA^Vchod^string^70#VCHOD,ULICA,ID#Výber vchodov##VCHOD#Výber vchodu, kde sa má oprava vykonať"
            ;
            sTabMiestoKonania = "VCHOD";
        }
        else if (s.equalsIgnoreCase("Byt")) {
            dbChooseDefinitionMkon = 
"#BYT#ID#KMC^Var. symbol, FUN@getByt^Byt^string^180#KMC,FUN@getByt,VCH_ID#Výber bytov##KMC#Výber bytu, kde sa má oprava vykonať"
            ;
            sTabMiestoKonania = "BYT";
        }
         ftf_c_miesto_konania.setBackground(dbChooseDefinitionMkon == null ? defaultcolor : (Color.GREEN));
         ftf_c_miesto_konania.setEditable(dbChooseDefinitionMkon == null);

    }//GEN-LAST:event_cb_c_tab_miesto_konaniaActionPerformed

    private void cb_c_stavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_c_stavActionPerformed
        String s = cb_c_stav.getSelectedItem().toString();
//!! na prebieha treba zalozit prvy stav plnenia s 0% !!
        btn_add_stav.setEnabled(s.equalsIgnoreCase("prebieha")); 
    }//GEN-LAST:event_cb_c_stavActionPerformed

    private void ftf_c_do_timeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftf_c_do_timeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ftf_c_do_timeActionPerformed

    private void btn_add_stavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_add_stavActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_add_stavActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed

        if (ftf_c_nazov_ulohy.getText().equals("")) {
            krn.Message("Názov úlohy musíte zadať.");
            ftf_c_nazov_ulohy.requestFocusInWindow();
            return;
        }
        if (ftf_c_miesto_konania.getText().equals("")) {
            krn.Message("Miesto konania musíte zadať.");
            ftf_c_miesto_konania.requestFocusInWindow();
            return;
        }
        putScreenToCalendarEvent(); // ulozenie hodnot obrazovky do udalosti
        currCalendarEvent.committxn(sTxnType);
   ///     myDialogBox.setIsUpdatedAfterView(true);
        myDialogBox.dispose(); // ked je vsetko OK
        
    }//GEN-LAST:event_btn_saveActionPerformed

    private void putScreenToCalendarEvent() {
        System.out.println("CURRCALENDARREVENTT_BEFORE:\n");
        currCalendarEvent.listValues();
        currCalendarEvent.hmpMyEvent.put("c_od_time", ftf_c_od_time.getValue());
        currCalendarEvent.hmpMyEvent.put("d_od_date", ftf_d_od_date.getValue());
//        currCalendarEvent.hmpMyEvent.put("c_zapisal", "");  // generuje sa automaticky
        currCalendarEvent.hmpMyEvent.put("id_eas_calAccount", currCalendarEvent.myOwner.getId_eas_calAccount());//!!
        currCalendarEvent.hmpMyEvent.put("c_stavy", ""); // dorobit formatovany vystup 
                                                         // z objektu ftf_last_stav alebo ako ? 
        currCalendarEvent.hmpMyEvent.put("b_spojity_cas_usek", (tgb_b_spojity_cas_usek.isSelected() ? 1 : 0));
        currCalendarEvent.hmpMyEvent.put("i_perc_dokoncenosti", getLastPercDokon()); // automaticky posledny zaznamenanany stav
                                                                                    // z c_stavy, ked je 100% prepne stav 
                                                                                    // na Ukoncene
        currCalendarEvent.hmpMyEvent.put("c_pripomenutie", cb_pripominat.getSelectedItem());
        currCalendarEvent.hmpMyEvent.put("c_cas_ukoncenia", ftf_c_do_time.getValue());
        currCalendarEvent.hmpMyEvent.put("b_celodenna_uloha", (cbx_b_celodenna_uloha.isSelected() ? 1 : 0));
        try {
            //        currCalendarEvent.hmpMyEvent.put("id_eas_calEvt", myObjectID); //  generuje sa automaticky!
            ftf_c_miesto_konania.commitEdit();
        } catch (ParseException ex) {
            Logger.getLogger(Pnl_event_updater.class.getName()).log(Level.SEVERE, null, ex);
        }
        currCalendarEvent.hmpMyEvent.put("c_miesto_konania", ftf_c_miesto_konania.getText());
//        currCalendarEvent.hmpMyEvent.put("c_zmenil", myObjectID); // generuje sa automaticky
        currCalendarEvent.hmpMyEvent.put("id_miesto_konania", iIdMiestoKonania); // nastavuje sa vyberom miesta konania
        currCalendarEvent.hmpMyEvent.put("c_typ_dokl_ulohy", cb_typ_dokl_ulohy.getSelectedItem()); 
        currCalendarEvent.hmpMyEvent.put("c_tab_miesto_konania", sTabMiestoKonania); // VCS,DOM,VCHOD,BYT
        currCalendarEvent.hmpMyEvent.put("c_stav", cb_c_stav.getSelectedItem());
        currCalendarEvent.hmpMyEvent.put("c_opakovanie", cb_opakovanie.getSelectedItem());
        currCalendarEvent.hmpMyEvent.put("id_dokl_ulohy", iIdDoklUlohy); // nastavuje sa vyberom dokladu ulohy
        currCalendarEvent.hmpMyEvent.put("c_do_time", ftf_c_do_time.getValue());
        currCalendarEvent.hmpMyEvent.put("d_do_date", ftf_d_do_date.getValue());
        currCalendarEvent.hmpMyEvent.put("d_ukoncenia", getLastDateDokon()); // automaticky posledny zaznamenanany stav
                                                                             // z c_stavy, v pripade 100%, inak null
        currCalendarEvent.hmpMyEvent.put("c_nazov_ulohy", ftf_c_nazov_ulohy.getText());
        currCalendarEvent.hmpMyEvent.put("c_popis_ucelu", txa_c_popis_ucelu.getText());
        currCalendarEvent.hmpMyEvent.put("id_subjekt", iIdSubjekt); // nastavuje sa vyberom miesta konania
        currCalendarEvent.hmpMyEvent.put("c_owner", krn.getPermd().getCurrentUser());
        currCalendarEvent.hmpMyEvent.put("i_id_owner", krn.getPermd().getCurrentUserID());

        
/***
        private Integer iIdMiestoKonania = new Integer(0); // nastavuje sa vyberom miesta konania
    private String  sTabMiestoKonania = ""; // nastavuje sa vyberom miesta konania
    private Integer iIdDoklUlohy = new Integer(0); // nastavuje sa vyberom dokladu ulohy
    private Integer iIdSubjekt   = new Integer(0); // nastavuje sa vyberom subjektu
****/        
        System.out.println("CURRCELEVVENTT_AFTERR:\n");
        currCalendarEvent.listValues();
/*
c_od_time:
d_od_date:18.05.2016
c_zapisal:
id_eas_calAccount:0
c_stavy:
b_spojity_cas_usek:0
i_perc_dokoncenosti:0
c_pripomenutie:
c_cas_ukoncenia:
b_celodenna_uloha:0
id_eas_calEvt:0
c_miesto_konania:
c_zmenil:
id_miesto_konania:0
c_typ_dokl_ulohy:
c_tab_miesto_konania:
c_stav:
c_opakovanie:
id_dokl_ulohy:0
c_kategoria:
c_do_time:
d_do_date:18.05.2016
d_ukoncenia:18.05.2016
c_nazov_ulohy:
c_popis_ucelu:
*/
    }
    
    private Integer getLastPercDokon() {
        System.out.println("!!! Not interpretted getLastPercDokon() method. Returning 0 % !");
        Integer i = 0;
        return i;
    }
    private Integer getLastPercDokon(CalendarEvent calEvt) {
        System.out.println("!!! Not interpretted getLastPercDokon(calEvt) method. Returning 0 % !");
        Integer i = 0;
        return i;
    }
    
    
    private Date getLastDateDokon() {
        System.out.println("!!! Not interpretted getLastDateDokon() method. Returning NULL !");
        Date d = new Date();
        return d;
    }
    
    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        myDialogBox.dispose();
    }//GEN-LAST:event_btn_cancelActionPerformed

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed
        currCalendarEvent.committxn("delete");
   ///     myDialogBox.setIsUpdatedAfterView(true);
        myDialogBox.dispose(); // ked je vsetko OK

    }//GEN-LAST:event_btn_deleteActionPerformed

    private void cb_typ_dokl_ulohyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_typ_dokl_ulohyActionPerformed
        String s = cb_typ_dokl_ulohy.getSelectedItem().toString();
        dbChooseDefinitionDokl = null;
// <iné>
//Požiadavka
//Vystavená objednávka
//Prijatá objednávka

        if (s.equalsIgnoreCase("Požiadavka")) 
            dbChooseDefinitionDokl = 
"#VCS#ID#VCS^Č.VCS,ULICA^Ulica^string^70#VCS,ULICA,ID#Výber VČS##VCS#Výber domu, kde sa má oprava vykonať"
            ;
        else if (s.equalsIgnoreCase("Vystavená objednávka")) 
            dbChooseDefinitionDokl = 
"#VCHOD#ID#VCHOD^+C. vchodu,ULICA^Vchod^string^70#VCHOD,ULICA,ID#Výber vchodov##VCHOD#Výber vchodu, kde sa má oprava vykonať"
            ;
        else if (s.equalsIgnoreCase("Prijatá objednávka"))
            dbChooseDefinitionDokl = 
"#BYT#ID#KMC^Var. symbol, FUN@getByt^Byt^string^180#KMC,FUN@getByt,VCH_ID#Výber bytov##KMC#Výber bytu, kde sa má oprava vykonať"
            ;
         ftf_c_dokl_ulohy.setBackground(dbChooseDefinitionDokl == null ? defaultcolor : (Color.GREEN));
         ftf_c_dokl_ulohy.setEnabled((dbChooseDefinitionDokl == null) && (iIdSubjekt != 0));
         ftf_c_dokl_ulohy.setEditable(dbChooseDefinitionDokl == null);
    }//GEN-LAST:event_cb_typ_dokl_ulohyActionPerformed

    private void cb_opakovanieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_opakovanieActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_opakovanieActionPerformed

    private void cb_pripominatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_pripominatActionPerformed
        String s = cb_pripominat.getSelectedItem().toString();
        ftf_i_pripomenutie.setEnabled(s.startsWith("Prip"));
        cb_mj_time.setEnabled(s.startsWith("Prip"));
    }//GEN-LAST:event_cb_pripominatActionPerformed

    private void cb_subjektActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_subjektActionPerformed
/*Nešpecifikovaný
Novozámocké bytové družstvo
BLOKY
        */       
        String s = cb_subjekt.getSelectedItem().toString();
        iIdSubjekt = 0;
        if (s.startsWith("Nov")) iIdSubjekt = 1;
        else if (s.startsWith("BLO")) iIdSubjekt = 4;
        if (iIdSubjekt == 0) {
            cb_typ_dokl_ulohy.setSelectedIndex(0);
            cb_typ_dokl_ulohy.setEnabled(false);
        }
        else 
            cb_typ_dokl_ulohy.setEnabled(true);
        cb_typ_dokl_ulohyActionPerformed(null);
    }//GEN-LAST:event_cb_subjektActionPerformed

    private void ftf_c_miesto_konaniaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftf_c_miesto_konaniaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ftf_c_miesto_konaniaActionPerformed

    private void cb_mj_timeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_mj_timeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_mj_timeActionPerformed

    private void ftf_d_od_dateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftf_d_od_dateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ftf_d_od_dateActionPerformed

    
        public String setCalendarEvent(CalendarEvent evt) {
        currCalendarEvent = evt;
        krn.krn_clearWidgets(this, sTxnType);
        
        System.out.println("setCalendarEventtt:" + currCalendarEvent.myOwner.toString() 
                + " forCUNK: " + (currCalendarEvent.myTimeChunk==null?"NULL":currCalendarEvent.myTimeChunk.toString()));
        /* ZASSSTARALE
        ftf_d_od_date.setValue(currCalendarEvent.myTimeChunk.getOdDate());
        ftf_d_do_date.setValue(currCalendarEvent.myTimeChunk.getDoDate());
        ftf_c_od_time.setValue(currCalendarEvent.myTimeChunk.getOdTime());
        ftf_c_do_time.setValue(currCalendarEvent.myTimeChunk.getDoTime());
        */
        /*
        */
        if (currCalendarEvent.hmpMyEvent.get("d_od_date") instanceof Date)
            ftf_d_od_date.setValue(currCalendarEvent.hmpMyEvent.get("d_od_date"));
        else
            ftf_d_od_date.setValue(((Calendar) currCalendarEvent.hmpMyEvent.get("d_od_date")).getTime());

        if (currCalendarEvent.hmpMyEvent.get("d_do_date") instanceof Date)
            ftf_d_do_date.setValue(currCalendarEvent.hmpMyEvent.get("d_do_date"));
        else
            ftf_d_do_date.setValue(((Calendar) currCalendarEvent.hmpMyEvent.get("d_do_date")).getTime());
        ftf_c_od_time.setValue(currCalendarEvent.hmpMyEvent.get("c_od_time"));
        ftf_c_do_time.setValue(currCalendarEvent.hmpMyEvent.get("c_do_time"));
        
        // opak putScreenToCalendarEvent()
        ftf_c_od_time.setValue(currCalendarEvent.hmpMyEvent.get("c_od_time"));
//!!DATEERROR        ftf_d_od_date.setValue(currCalendarEvent.hmpMyEvent.get("d_od_date"));
//        currCalendarEvent.hmpMyEvent.put("c_zapisal", "");  // generuje sa automaticky
/*
    iIdMiestoKonania = new Integer(0); // nastavuje sa vyberom miesta konania
    sTabMiestoKonania = ""; // nastavuje sa vyberom miesta konania
    iIdDoklUlohy = new Integer(0); // nastavuje sa vyberom dokladu ulohy
*/
        
///!!    zz       currCalendarEvent.hmpMyEvent.put("id_eas_calAccount", currCalendarEvent.myOwner.getId_eas_calAccount());//!!
///!!        currCalendarEvent.hmpMyEvent.put("c_stavy", ""); // dorobit formatovany vystup 
///!!                                                         // z objektu ftf_last_stav alebo ako ? 
        tgb_b_spojity_cas_usek.setSelected(Integer.parseInt(currCalendarEvent.hmpMyEvent.get("b_spojity_cas_usek").toString())==1);
 //!!       currCalendarEvent.hmpMyEvent.put("i_perc_dokoncenosti", getLastPercDokon()); // automaticky posledny zaznamenanany stav
 //!!                                                                                   // z c_stavy, ked je 100% prepne stav 
 //!!                                                                                   // na Ukoncene
        cb_pripominat.setSelectedItem(currCalendarEvent.hmpMyEvent.get("c_pripomenutie"));
        ftf_c_do_time.setValue(currCalendarEvent.hmpMyEvent.get("c_cas_ukoncenia"));
        
///!! ako zapisat ?        currCalendarEvent.hmpMyEvent.put("b_celodenna_uloha", (cbx_b_celodenna_uloha.isSelected() ? 1 : 0));
        ftf_c_miesto_konania.setValue(currCalendarEvent.hmpMyEvent.get("c_miesto_konania"));
//        currCalendarEvent.hmpMyEvent.put("c_zmenil", myObjectID); // generuje sa automaticky
        
        iIdMiestoKonania = (Integer) currCalendarEvent.hmpMyEvent.get("id_miesto_konania"); // nastavuje sa vyberom miesta konania
        cb_typ_dokl_ulohy.setSelectedItem(currCalendarEvent.hmpMyEvent.get("c_typ_dokl_ulohy")); 
        sTabMiestoKonania = currCalendarEvent.hmpMyEvent.get("c_tab_miesto_konania").toString(); // VCS,DOM,VCHOD,BYT
        cb_c_stav.setSelectedItem(currCalendarEvent.hmpMyEvent.get("c_stav"));
        cb_opakovanie.setSelectedItem(currCalendarEvent.hmpMyEvent.get("c_opakovanie"));
        iIdDoklUlohy = (Integer) currCalendarEvent.hmpMyEvent.get("id_dokl_ulohy"); // nastavuje sa vyberom dokladu ulohy
        ftf_c_do_time.setValue(currCalendarEvent.hmpMyEvent.get("c_do_time"));
//!!DATEERROR        ftf_d_do_date.setValue(currCalendarEvent.hmpMyEvent.get("d_do_date"));
//!! co s tym ?        currCalendarEvent.hmpMyEvent.put("d_ukoncenia", getLastDateDokon()); // automaticky posledny zaznamenanany stav
                                                                             // z c_stavy, v pripade 100%, inak null
        ftf_c_nazov_ulohy.setText(currCalendarEvent.hmpMyEvent.get("c_nazov_ulohy").toString());
        txa_c_popis_ucelu.setText(currCalendarEvent.hmpMyEvent.get("c_popis_ucelu").toString());
//        tu sme skoncili, a menime nastavenia a stil upozornenia a casu upozornenia
        
        return "";
    }
    
        public void setTxnType (String tType) {
            updatePerformed = false;
            sTxnType = tType.toUpperCase();
            if (sTxnType.equals("ADD") || sTxnType.equals("COPY")) {
                btn_save.setVisible(true);
                btn_cancel.setVisible(true);
                btn_delete.setVisible(false);
            }
            else if (sTxnType.equals("DELETE")) {
                btn_save.setVisible(false);
                btn_cancel.setVisible(true);
                btn_delete.setVisible(true);
            }
            else if (sTxnType.equals("UPDATE")) {
                btn_save.setVisible(true);
                btn_cancel.setVisible(true);
                btn_delete.setVisible(true);
            }
            else  {
                btn_save.setVisible(false);
                btn_cancel.setVisible(true);
                btn_delete.setVisible(false);
                
            }
        }
        
        public void setDialogBox(JDialog jd) {
            myDialogBox = jd;
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_add_stav;
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_save;
    private javax.swing.JComboBox cb_c_stav;
    private javax.swing.JComboBox cb_c_tab_miesto_konania;
    private javax.swing.JComboBox cb_mj_time;
    private javax.swing.JComboBox cb_opakovanie;
    private javax.swing.JComboBox cb_pripominat;
    private javax.swing.JComboBox cb_subjekt;
    private javax.swing.JComboBox cb_typ_dokl_ulohy;
    private javax.swing.JCheckBox cbx_b_celodenna_uloha;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JFormattedTextField ftf_c_do_time;
    private javax.swing.JFormattedTextField ftf_c_dokl_ulohy;
    private javax.swing.JFormattedTextField ftf_c_miesto_konania;
    private javax.swing.JFormattedTextField ftf_c_nazov_ulohy;
    private javax.swing.JFormattedTextField ftf_c_od_time;
    private javax.swing.JFormattedTextField ftf_d_do_date;
    private javax.swing.JFormattedTextField ftf_d_od_date;
    private javax.swing.JFormattedTextField ftf_i_pripomenutie;
    private javax.swing.JFormattedTextField ftf_last_stav;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jp_header;
    private javax.swing.JPanel jp_pripomenut;
    private javax.swing.JPanel jp_stav;
    private javax.swing.JPanel jp_time;
    private javax.swing.JToggleButton tgb_b_spojity_cas_usek;
    private javax.swing.JTextArea txa_c_popis_ucelu;
    // End of variables declaration//GEN-END:variables
}
