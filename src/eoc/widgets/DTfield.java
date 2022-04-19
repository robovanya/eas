/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.widgets;
import eoc.DateMaskFormatter;
import eoc.IEOC_DateSupporter;
import eoc.IntegerFormatter;
import eoc.TimeFormatter;
import eoc.database.DBconnection;
import eoc.iEOC_DBtableField;
import system.Kernel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class DTfield extends JFormattedTextField implements eoc.iEOC_DBtableField {

    DTfield myObjID;
    private String dbOwnerName;
    private String dbTableName;
    private String dbFieldName;
    private String dbFieldLabel;
    /*
    nechat len nutne, ostatne obsluzi dbtablefield objekt !!
    presne parametre musi obsahovat column objekt !!
    riadky so zaciatkom //>> by sa mali asi odstranit
    riadky so zaciatkom //L>> by sa mali asi prerobit na lokal=override
            lokal musi mat ekvivalent v dbdatafield->column objekt
    */
    private Object dbDataType;
    private String dbFormatString;
    private String dbPrivateData;
    private String dbDefaultValue;
    private String dbEnableWhen = ""; // N/C/U/* (New/Copy/Update/All)
    private String dbGroupList; // zaradenie objektu do specifickych,
                                // volne definovatelnych skupin. 
    
    private String dbOnEntry;   // funkcia pre kotrolu vstupu do udaja
    private String dbOnLeave;   // funkcia pre kotrolu opustenie udaja
        // skupiny sa mozu nazivat hociako, delene maju byt ciarkou,
        // Priklad: Faktura,PoklDokl
        // Pouzitie: krn.EnableFields(myPanel,"PoklDokl") enabluje objekty,
        //           patriace do skupiny PoklDokl v jPaneli myPanel
    private String dbChooseDefinition = "";  // Vyber hodnoty pre 
                                //TYP#tabulka#field1,field2,fieldx#returnfield
    private String genericDataType = "";
    //private String fullColumnName = ".";    
    eoc.dbdrv.DBtableField dbTableField;
    private boolean bInitialized = false;
    DBconnection MyCn;
    private boolean   bFormatterAdded; // ci bol spravny formatter nainstalovany  
    private Kernel krn;
//    private eoc.IEOC_VisualObject myViewer;
    private eoc.xinterface.XViewer myViewer;
    Object methodSource;
    private DateSupporter dateSupporter;

    public void setKrn(Kernel kr) {
       krn = kr;    
    }
    
    public class DateSupporter implements IEOC_DateSupporter {
        
        GregorianCalendar cal = new GregorianCalendar();
       //         Calendar.getInstance();
        iEOC_DBtableField dateStringSource = null;
        @Override
        public int getDay() {
            if (dateStringSource != null)
            setSupportForDateString(dateStringSource.getDBtableField().getValueForWriteAsString(""));
            return cal.get(Calendar.DAY_OF_MONTH);
        } 
        @Override
        public int getMonth() {
            if (dateStringSource != null)
            setSupportForDateString(dateStringSource.getDBtableField().getValueForWriteAsString(""));
            return cal.get(Calendar.MONTH) + 1;
        } 
        @Override
        public int getYear() {
            if (dateStringSource != null)
            setSupportForDateString(dateStringSource.getDBtableField().getValueForWriteAsString(""));
            return cal.get(Calendar.YEAR);
        } 
        @Override
        public String getDayStr() {
            String s = "";
            if (dateStringSource != null)
                //dateStringSource.getText()
            setSupportForDateString(dateStringSource.getDBtableField().getValueForWriteAsString(""));
//            setSupportForDateString(dateStringSource.getText());
            System.out.println(cal.toString());
            int i = cal.get(Calendar.DAY_OF_MONTH);
            s = FnEaS.intToStr(i, 2);
            return s;
        } 
        @Override
        public String getMonthStr() {
            String s = "";
            if (dateStringSource != null)
            setSupportForDateString(dateStringSource.getDBtableField().getValueForWriteAsString(""));
//            setSupportForDateString(dateStringSource.getText());
            int i = cal.get(Calendar.MONTH) + 1;
            s = FnEaS.intToStr(i, 2);
            return s;
        } 
        @Override
        public String getYearStr() {
            String s = "";
            if (dateStringSource != null)
            setSupportForDateString(dateStringSource.getDBtableField().getValueForWriteAsString(""));
//                setSupportForDateString(dateStringSource.getText());
            int i = cal.get(Calendar.YEAR);
            s = FnEaS.intToStr(i, 4);
            return s;
        } 

        @Override
        public void setSupportForDateString(String dtStr) {
            if (dtStr == null) dtStr = "<NULL>";
            String delim = "";
            if (dtStr.contains(".")) delim = ".";
            if (dtStr.contains("-")) delim = "-";
           System.out.println("setSupportForDateString() setting to: " + dtStr);
            if (delim.equals("")) {
                Kernel.staticMsg("E", "setSupportForDateString()\n\n"
                   + "Dátový reťazec: " + dtStr + "\n\n" 
                   + "Ponechaná podpora pre dnešný dátum", "Nespracovatelný reťazec !");
                return;
            }
            Integer[] iDt = new Integer [3];
            cal.setLenient(false);
            cal.clear();
//            cal.set(Calendar.DAY_OF_MONTH,1);
            if (delim.equals(".")) {
                String[] sDt = dtStr.split("\\.");
                cal.set(Integer.parseInt(sDt[2]),
                        Integer.parseInt(sDt[1]) -1,
                        Integer.parseInt(sDt[0]));
            }    
            if (delim.equals("-")) {
                String[] sDt = dtStr.split("-");
                cal.set(Integer.parseInt(sDt[0]),
                        Integer.parseInt(sDt[1]) -1,
                        Integer.parseInt(sDt[2]));
                /*
                cal.set(Calendar.YEAR, Integer.parseInt(sDt[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(sDt[1]));
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sDt[2]));
                */
            }
        }

        @Override
        public void setDateStringSource(iEOC_DBtableField dateStrSource) {
            dateStringSource = dateStrSource;
            setSupportForDateString(dateStringSource.getDBtableField().getValueForWriteAsString(""));
        }
        
        
        
    }

    public DateSupporter getDateSupporter() {
        return dateSupporter;
    }
    
    public void setViewer(/* eoc.IEOC_VisualObject */ eoc.xinterface.XViewer vw) {
       myViewer = vw;    
       methodSource = myViewer;
    }

    // displayValue je akymsi suctom funkcii setText() a setValue()
    // ktora donuti objekt 'resetnut' lubovolne predosle hodnoty
    public void displayValue(String strValue) {
        if (krn != null) {
            krn.debugOut(this,5,"TOTO VYHLADAT !!! Znamena to ze krn je casom dostupne v DTfield!!! DTFIELD_displayValue:" + strValue);
            //this.setFont(krn.getDefaultFont());
        }
             this.putStringToValue(strValue);
//   odstavene 2015-11-05 03:31        
           this.setMainFormatter();
           this.setText(strValue);
        
    }

    @Override
    public eoc.dbdrv.DBtableField getDBtableField() {
        return dbTableField;
    }

    // tuto metodu vola kernel pri ostrom behu programu
    // Vytvoria sa vnorene objekty, ktore pri design-mod-e neexistuju
    public void initialize(DBconnection cnn) {
        
        if (bInitialized) return;
        
        MyCn = cnn;
        /*
        System.out.println("********DTfieldInitialize()" + this.toString()
        + "\nCNN:" + cnn.toString()
        + "\ndbOwnerName==" + dbOwnerName
        + "\ndbTableName==" + dbTableName
        + "\ndbFieldName==" + dbFieldName
        );
        */
        System.out.println("********DTfieldInitialize()" + dbTableName);
        //QQQ V3 - add
        if (dbTableName.contains(".")) {
            dbOwnerName = FnEaS.sEntry(1, dbTableName, ".");
            dbTableName = FnEaS.sEntry(2, dbTableName, ".");
        }
        
        dbTableField = new eoc.dbdrv.DBtableField(
                   (Component) this, cnn,dbOwnerName,dbTableName,dbFieldName);
       ///// System.out.println("DBTABLEFILDCREATON" + (dbTableField == null) + " FORR: " + this.dbFieldName);
        // doplnenie pripadnych NUll-hodnot attributov
        // podla vlastnosti stlpca z tabukly v databaze
        setDBpropertiesFromColumnInfo();
        setMainFormatter();
        if (dbTableField.dbColumnInfo.genericdatatype.equalsIgnoreCase("date")) {
            
            this.setInputVerifier(new DateInputVerifier());
        
            this.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 32) {
                     
                       System.out.println("KYKODE: " + e.getKeyCode() + " atPOSSS:" + myObjID.getCaret().getDot());
                       int dotPos = myObjID.getCaret().getDot();
                       if (dotPos == 0 || dotPos == 1) {
                          String s = dateSupporter.getDayStr();
                           try {                                  
                               myObjID.getDocument().insertString(0,s, null);
                               myObjID.getCaret().setDot(3);
                           } catch (BadLocationException ex) {
                               Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                       if (dotPos == 3 || dotPos == 4) {
                          String s = dateSupporter.getMonthStr();
                           try {                                  
                               myObjID.getDocument().insertString(3,s, null);
                               myObjID.getCaret().setDot(6);
                           } catch (BadLocationException ex) {
                               Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                       if (dotPos >= 6) {
                          String s = dateSupporter.getYearStr();
                           try {                                  
                               myObjID.getDocument().insertString(6,s, null);
                               myObjID.getCaret().setDot(10);
                           } catch (BadLocationException ex) {
                               Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
/*        
        this.addFocusListener(new FocusListener() {

          @Override
          public void focusGained(FocusEvent e) {
          };

          @Override
          public void focusLost(FocusEvent e) {
            if (!e.isTemporary()) {
              DTfield dtf = (DTfield) e.getComponent();
              String content = dtf.getText();
              if (!content.equals("a") ) {
                krn.Message("E", "illegal value! " + content, "Chybný dátum");
                SwingUtilities.invokeLater(new FocusGrabber(dtf));
              }
            }
          }
        });   
        */
        }
        bInitialized = true;
    }

   public class DateInputVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            boolean verified = true;
            if (input instanceof DTfield) {
                DTfield dtf = (DTfield) input;
                AbstractFormatter currFmtr = dtf.getFormatter();
                if (currFmtr instanceof DateMaskFormatter) {
                    String dtStr = dtf.getText();
                    verified = parseDate(dtStr,(DateMaskFormatter) currFmtr);
                    if (!verified && dbTableField.dbColumnInfo.nullallowed) {
                        // test praznej hodnoty
                        verified = DateMaskFormatter.isEmptyDate(dtStr);
                    }
                    
                    if (!verified) {
                        krn.Message("E", "Nesprávny dátum: " + dtStr + "\nnullallowed:" + dbTableField.dbColumnInfo.nullallowed, "Chybný dátum");
                    }
                }
            }
            return verified;
        }

    }
   
   public boolean parseDate(String date, DateMaskFormatter currFmtr) {
       boolean retVal = true;
       SimpleDateFormat dtf = new SimpleDateFormat("dd" + currFmtr.getDelimiterStr() + "MM" + currFmtr.getDelimiterStr() + "yyyy");
       dtf.setLenient(false);
       try {
           dtf.parse(date);
       }
       catch (ParseException ex) { 
           retVal = false;
       }
       return retVal;
   }
   
   public void setMainFormatter() {
        if (bFormatterAdded) return; 
        // aktualny formatter objektu. Ked je zelaneho typu, novy sa neinstaluje
        AbstractFormatter currFmtr = this.getFormatter();
        DefaultFormatterFactory dfmtFact;
//        boolean isUpdated = false;
        if (dbFormatString.equals("$TIME") && 
            (currFmtr==null || (!(currFmtr instanceof TimeFormatter)))) {
//            updated = true;
            //// System.out.println("dbFormatString====" + dbFormatString + "  class:" + dbTableField.getClass());
            final TimeFormatter timeFmtr;
            timeFmtr = new TimeFormatter();
            timeFmtr.setValueClass(dbTableField.getClass());
            timeFmtr.setAllowsInvalid(false);
            try {
                timeFmtr.setMask("##:##");
            } catch (ParseException ex) {
                Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
            }
            dbTableField.dbColumnInfo.formatString = "##:##";
            dfmtFact = new DefaultFormatterFactory(timeFmtr);
            dfmtFact.setDefaultFormatter(timeFmtr);
            dfmtFact.setEditFormatter(timeFmtr);
            this.setFormatterFactory(dfmtFact);
            timeFmtr.install(this);
            this.setFormatter(timeFmtr);
            timeFmtr.setOverwriteMode(true);
            String s="0000";
            try {
                s = (String) this.getFormatter().valueToString(this.getValue());
            } catch (ParseException ex) {
                Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
            }
            ///// System.out.println("AtTimeFormatter: text:" + this.getText() + " formatted:" + s );
            bFormatterAdded = true;
            return;
        } // if (dbFormatString.equals("$TIME")) {

        else { 
            if (/* dbScreenValueDefinition.equals("") */
                    !(myObjID instanceof DTXfield)) {
               //// System.out.println(dbFieldName + " ->genericDataTypetooloo==" + genericDataType.toLowerCase());
                switch (genericDataType.toLowerCase()) {
                case "date":
                     if (currFmtr instanceof DateMaskFormatter) break;
////                     System.out.println ("ADDING>>>>DATEFORMATTERFOR MASKKKEDD:" + this.getDbTableName() + "." + this.getDbFieldName());
                     this.setValue("    -  -  ");
                     final DateMaskFormatter defDtmFmtr = new DateMaskFormatter(krn);
                     defDtmFmtr.setValueClass(String.class); // Date.class);
                     dfmtFact = new DefaultFormatterFactory();
                     defDtmFmtr.setAllowsInvalid(true);
                     //ediDtmFmtr.setAllowsInvalid(true);
                     //disDtmFmtr.setAllowsInvalid(false);
                     String delim = defDtmFmtr.getDelimiterStr();
                     try {
                        defDtmFmtr.setMask("##" + delim + "##" + delim + "####");
                     } catch (ParseException ex) {
                        Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     dbTableField.dbColumnInfo.formatString = "##" + delim + "##" + delim + "####";
                     dfmtFact = new DefaultFormatterFactory(defDtmFmtr);
                     dfmtFact.setDefaultFormatter(defDtmFmtr);
                     dfmtFact.setEditFormatter(defDtmFmtr);
                     this.setFormatterFactory(dfmtFact);
                     defDtmFmtr.install(this);
                     this.setFormatter(defDtmFmtr);
                     defDtmFmtr.setOverwriteMode(true);
                     break;
                case "string":
                     this.setFormatter(null);
                     final DefaultFormatter defaultFmtr;
                     defaultFmtr  = new DefaultFormatter();
                     defaultFmtr.setValueClass(String.class);
                     dfmtFact = new DefaultFormatterFactory(defaultFmtr);
                     dfmtFact.setDefaultFormatter(defaultFmtr);
                     dfmtFact.setEditFormatter(defaultFmtr);
                     this.setFormatterFactory(dfmtFact);
                     defaultFmtr.install(this);
                     this.setFormatter(defaultFmtr);
                     defaultFmtr.setOverwriteMode(false);
                     this.setValue("");
                     break;
                case "integer":
                     if (currFmtr instanceof IntegerFormatter) break;
                     final IntegerFormatter intFmtr;
                     intFmtr = new IntegerFormatter();
                     intFmtr.setValueClass(Integer.class);
                     dfmtFact = new DefaultFormatterFactory(intFmtr);
                     this.setFormatterFactory(dfmtFact);
                     dfmtFact.setDefaultFormatter(intFmtr);
                     dfmtFact.setEditFormatter(intFmtr);
                     intFmtr.install(this);
                     this.setFormatter(intFmtr);
                     intFmtr.setOverwriteMode(false);
                     break;
                case "decimal":
                     if (currFmtr instanceof NumberFormatter) break;
                     this.setValue(0.00);
                     final NumberFormat decimalFormat = NumberFormat.getNumberInstance();
                     decimalFormat.setMaximumFractionDigits(dbTableField.dbColumnInfo.decimals);
                     decimalFormat.setMaximumIntegerDigits(dbTableField.dbColumnInfo.length
                                                         - dbTableField.dbColumnInfo.decimals);
                     final NumberFormatter decFmtr = new NumberFormatter(decimalFormat);
                     decFmtr.setValueClass(Number.class);
                     dfmtFact = new DefaultFormatterFactory(decFmtr);
                     this.setFormatterFactory(dfmtFact);
                     dfmtFact.setDefaultFormatter(decFmtr);
                     dfmtFact.setEditFormatter(decFmtr);
                     decFmtr.install(this);
                     this.setFormatter(decFmtr);
                     decFmtr.setOverwriteMode(false);
                     break;
                default:
                     System.out.println(dbFieldName + " ->genericDataTypeDEFAULTFORMATTER==" + genericDataType.toLowerCase()
                     + " dbTableField.getClass()===" + dbTableField.getClass());
                     if (currFmtr instanceof DefaultFormatter) break;
                     this.setValue("");
                     defaultFmtr  = new DefaultFormatter();
                     defaultFmtr.setValueClass(dbTableField.getClass());
                     dfmtFact = new DefaultFormatterFactory(defaultFmtr);
                     dfmtFact.setDefaultFormatter(defaultFmtr);
                     dfmtFact.setEditFormatter(defaultFmtr);
                     this.setFormatterFactory(dfmtFact);
                     defaultFmtr.install(this);
                     this.setFormatter(defaultFmtr);
                     defaultFmtr.setOverwriteMode(false);
                     break;
                    
            } // switch (genericDataType)
            bFormatterAdded = true;
          }   
       }   
    }
    
    public boolean isInitialized() {
        return bInitialized;
    }
    
    
    public DTfield() {
          super();  
          ////Font f = new Font ("Tahoma", Font.PLAIN, 14); 
          myObjID = this;
          dateSupporter = new DateSupporter();
          //this.addFocusListener(new eoc.EOCFocusListener());
          /******
          this.addMouseListener(new MouseAdapter() { 
          public void mousePressed(MouseEvent evt) { 
              if (evt.isControlDown()) {
                Kernel.stdMsg("<html><B>Informácie k objektu: </B>"
                   + dbTableField.dbColumnInfo.tablename 
                      + "." + dbTableField.dbColumnInfo.columnname
                   + "<BR> Typ objektu: <B>" + evt.getComponent().getClass().getSimpleName() + "</B>"
                   + "<BR> DB_datatype: <B>" + dbTableField.dbColumnInfo.realdatatype + "</B>"
                   + "<BR>Generic_type: <B>" + dbTableField.dbColumnInfo.genericdatatype + "</B>"
                   + "<BR>FORMATstring: <B>" + dbTableField.dbColumnInfo.formatString + "</B>"
                   + "<BR>DBFORMATstring: <B>" + dbFormatString + "</B>"
                   + "<BR>DBFORMATTER: <B>" + myObjID.getFormatter().toString() + "</B>"
                   + "</html>"
                );
              }
          } 
         }); 
          *****/
       this.addMouseListener(new MouseListener() {

           public void mousePressed(MouseEvent evt) { 
              if (evt.isControlDown()) {
                 Kernel.staticMsg("<html><B>Informácie k objektu: </B>"
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
          } 

            @Override
            public void mouseClicked(MouseEvent evt) {
                String retVal;
                if (evt.getClickCount()==2 && dbChooseDefinition.length() > 0) {
                    /*
                    try {
                        Kernel.staticMsg("TXN_ISOLATION: " + String.valueOf(MyCn.getTransactionIsolation())
                        + "\n VIWRTXN: " + myViewer.getTxnType());
                        */
                        if (myViewer.getTxnType().equals("") || myViewer.getTxnType().equals(null)) {
                           evt.consume();
                           return;
                        }
                    /*        
                    } catch (SQLException ex) {
                        Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    */
                        retVal = 
                        krn.genVyberForMe(myViewer,myObjID, dbChooseDefinition);
                        //krn.Message("DTfield_MouseClicked_rrretVaaal is: " + retVal);
                        putRetValToValues(retVal);
                        evt.consume();
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
       
       this.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
          ///// this.OutPrintln("Kernel-actionPerformed-inMyObj!!!");
           System.out.println("krn_e.getActionCommand()==" + e.getActionCommand());
           } 
        });

          
         if (bInitialized && this.getDBtableField().bCreationErrors) {
             this.setBackground(Color.YELLOW/* .red*/);
         }

        //listeners
       addPropertyChangeListener(new PropertyChangeListener() {
           @Override
           public void propertyChange(PropertyChangeEvent evt) {
               ////System.out.println("PropChangListener:: " + getText()+" --EVT>" + evt.getPropertyName());
                if (evt.getPropertyName().equals("value")) {
                    JFormattedTextField tf;
                    tf = (JFormattedTextField) evt.getSource();
                    if (/* dbScreenValueDefinition.equals("") */
                        !(myObjID instanceof DTXfield)) 
                        dbTableField.valueObjectForWrite = tf.getValue();
                }
                else if (evt.getPropertyName().equals("text")) {
                    try {
                        setValue(getFormatter().valueToString(getText()));
                        /////setValue(getText());
                    } catch (ParseException ex) {
                        Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
           }
        });
    }
    
    public void putRetValToValues(String rtv) {
            /*
            if (cmp instanceof EOC_CDTfield) {
               EOC_CDTfield dtf = (EOC_CDTfield) cmp;
               dtf.setText(FnEaS.sEntry(1, rtv, "^"));
               dtf.setdb_ValueForWrite(FnEaS.sEntry(2, rtv, "^"));
                
            }
            else
            */
            { // je to EOC_DTfield
                rtv = rtv.trim();
//               eoc.iEOC_DBtableField /* eoc.widgets.DTfield */ dtf =
//                       (eoc.iEOC_DBtableField /* eoc.widgets.DTfield */) cmp; 
               System.out.println("DTfield_putRetValToValues()_PUTTIGrtv:" + rtv); 
               myObjID.setText(FnEaS.sEntry(1, rtv, "^"));
               System.out.println(myObjID.getDBtableField().dbColumnInfo.columnname +  "::::Setting____DBfieldValueObjectForWrite to:" + FnEaS.sEntry(2, rtv, "^"));
               myObjID.getDBtableField().setDBfieldValueObjectForWrite(FnEaS.sEntry(2, rtv, "^"));
            }
       
    }
    public String getDbTableName() {
        if (this.dbTableName!=null)
            return this.dbTableName;
        else
            return "";
    }

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public String getDbFieldName() {
        if (this.dbFieldName!=null)
            return this.dbFieldName;
        else
            return "";
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }

    public String getDbFieldLabel() {
        return dbFieldLabel;
    }

    public void setDbFieldLabel(String dbFieldLabel) {
        this.dbFieldLabel = dbFieldLabel;
    }

    public Object get_DbDataType() {
        return dbDataType;
    }

    public void set_DbDataType(Object dbDataType) {
        this.dbDataType = dbDataType;
    }

    public String getDbFormatString() {
        return dbFormatString;
    }

    public void setDbFormatString(String dbFormatString) {
        this.dbFormatString = dbFormatString;
    }
    public String getDbPrivateData() {
        return dbPrivateData;
    }

    public void setDbPrivateData(String dbPrivateData) {
        this.dbPrivateData = dbPrivateData;
    }
    public String getDbEnableWhen() {
        return dbEnableWhen;
    }

    public void setDbEnableWhen(String dbEnableWhen) {
        this.dbEnableWhen = dbEnableWhen;
    }

    public String getDbGroupList() {
        return dbGroupList;
    }

    public void setDbGroupList(String dbGroupList) {
        this.dbGroupList = dbGroupList;
    }

    public String getDbOnEntry() {
        return dbOnEntry;
    }

    public void setDbOnEntry(String s) {
        this.dbOnEntry = s;
    }

    public String getDbOnLeave() {
        return dbOnLeave;
    }

    public void setDbOnLeave(String s) {
        this.dbOnLeave = s;
    }

    public String getDbDefaultValue() {
        return dbDefaultValue;
    }

    public void setDbDefaultValue(String s) {
        this.dbDefaultValue = s;
    }

    public String getdbChooseDefinition() {
        return dbChooseDefinition;
    }

    public void setdbChooseDefinition(String s) {
        this.dbChooseDefinition = s;
        if(dbChooseDefinition.equals("") || dbChooseDefinition.equals(null)){
            setBackground(Color.WHITE);
        }
        else {
            setBackground(Color.GREEN);
        } 
    }
    
    public void setDBcolumnInfo(eoc.dbdrv.DBcolumnInfo colInf) {
        dbTableField.setDBcolumnInfo(colInf);
    }

    public void setDBpropertiesFromColumnInfo() {
        dbDataType      = dbTableField.dbColumnInfo.realdatatype;
        if (dbFormatString==null)
            dbFormatString  = dbTableField.dbColumnInfo.formatString;
        if (dbDefaultValue==null)
            dbDefaultValue  = dbTableField.dbColumnInfo.notNullDefaultValue;
        genericDataType = dbTableField.dbColumnInfo.genericdatatype;
//        fullColumnName  = dbTableField.dbColumnInfo.getFullColumnName();
        if ((!(dbTableField.dbColumnInfo.comment==null) 
             && (!dbTableField.dbColumnInfo.comment.trim().equals(""))))
        {
            this.setToolTipText(dbTableField.dbColumnInfo.comment); 
        }
        else { 
            this.setToolTipText("<HTML>Stľpec: <B>" + 
                    dbTableField.dbColumnInfo.getFullColumnName()
             + "</B> páni programátori nepovažovali za potrebné okomentovať. <B>:o)</B></HTML>");
        }
    }
    
    public void clear() throws ParseException {
 //       System.out.println("Self-clearingA: " + dbTableField);
        if (dbTableField == null) return;
        if (dbTableField.bCreationErrors) return;
        setMainFormatter();
        /*
            System.out.println("Self-clearingB: " 
               + dbTableField.dbColumnInfo.getFullColumnName()
                + " datatype = " + dbTableField.dbColumnInfo.genericdatatype
                + " dbScreenValueDefinition = " + dbScreenValueDefinition
                + " formatterr== " + this.getFormatter().toString()
                );
        */
        switch (dbTableField.dbColumnInfo.genericdatatype) {
            case "string":
                //this.setText("");
                this.setValue("");
                break;
            case "integer":
                //this.setText("0");
                this.setValue(0);
                break;
            case "decimal":
                //this.setText("0");
                this.setValue(0.00);
                break;
            case "date":
                this.setText("  .  .    ");
//                this.setValue(new Date());
                this.setValue("  .  .    ");
//                this.setValue(new SimpleDateFormat("DD.MM.yyyy", 
//                                  Locale.ENGLISH).parse(null));
               break;
            case "boolean":
                this.setValue(false);
                break;
            default:
                this.setText("");
                break;
        }
    }

    public boolean commitScreenValue() {
        return putStringToValue (this.getText());
    }
    
    public boolean putStringToValue (String t) {
        krn.debugOut(this,5,"DTFputStringToValue() = " + t + " GENERICDATATYPEis: " + dbTableField.dbColumnInfo.genericdatatype);
        System.out.println("DDDTTTFFFF_DTFputStringToValue() = " + t + " GENERICDATATYPEis: " + dbTableField.dbColumnInfo.genericdatatype
        + " field:" + dbTableField.dbColumnInfo.columnname);
        try {
            if (t==null) t = "";
            switch (dbTableField.dbColumnInfo.genericdatatype) {
                case "character":
                    this.setValue(t);
                    break;
                case "integer":
                    //this.setText("0");
                    t = t.replace('\160', '\0');
                    System.out.println("PAAAARSSSINGG_ParsingInttt:" + t);
                    if (t.equals("")) t = "0";
                    this.setValue(Integer.parseInt(t));
                    break;
                case "decimal":
                    //this.setText("0");
                    if (t.equals("")) t = "0.0";
                    this.setValue(Double.parseDouble(t));
                    break;
                case "date":
//                this.setValue(new Date(""));
                    if (t==null) {
                        this.setText("");
                        //this.setValue(null);
                        this.setValue("    -  -  ");
                    }
                    else {
                        ////System.out.println("SETTINGdateVALUE_TO: '" + t + "'");
                        if (!t.contains("-") ) { // Datumovy objekt
                           if (t.length() > 0)
                               this.setValue(new SimpleDateFormat("yyyy-MM-DD",
                                                 Locale.ENGLISH).parse(t));
                           else
//                               this.setValue(null);
                               this.setValue("    -  -  ");
 //                              this.setValue(new SimpleDateFormat("yyyy-MM-DD", 
 //                                                Locale.ENGLISH).parse("3000-01-01"));
//                                               Locale.ENGLISH).parse(null));
                        }
                        else { // retazec
                            if (t.length() > 0)
                               this.setValue(t);
                            else {
                               System.out.println("SETTINGdateVALUE_TOemptystring: '" + t + "'");
                               this.setValue("    -  -  ");
                            }
                            
                        }
                    }
                    break;
                default:
                    this.setValue(t);
                    break;
            }
        } catch (ParseException ex) {
            Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public void setValue(Object valueObj) {
        if (dbTableField.bCreationErrors) {
            super.setValue(null); 
            dbTableField.valueObjectForWrite = null;
            return;
        }
        if (valueObj.toString()=="0") valueObj = 0.0;
////        if  (dbScreenValueDefinition.equals("") ||  dbScreenValueDefinition.equals(null)) {
        if  (!(myObjID instanceof DTXfield)) {
                super.setValue(valueObj); 
                dbTableField.valueObjectForWrite = valueObj;
            try {
                this.setText(this.getFormatter().valueToString(valueObj));
            } catch (ParseException ex) {
                Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
            }

        }        
        /***
        else { // je to DTXfield
            // value sa ulozi do dbTableField
            dbTableField.valueObjectForWrite = valueObj;
            // na obrazovku sa ulozi ofdvodena hodnota, na zaklade
            // dbScreenValueDefinition
            String rv = evaluateDbScreenValueDefinition();
        ////System.out.println("dbScreenValueDefinition==>>" + rv);
            this.setText(rv);
        }
        * ****/
        
    }
    public void setValueDirect(Object valueObj) {
        super.setValue(valueObj); 
    }

    @Override
    public Object getValue() {
        return super.getValue(); 
    }
    public MaskFormatter createMaskFormatter(String sDtType, String frmtStr) {
       MaskFormatter formatter = null;
       try {
          formatter = new MaskFormatter(frmtStr);
          formatter.setOverwriteMode(false);
          Class cls;
          switch (sDtType.toLowerCase()) {
          case "integer":
              cls = java.lang.Integer.class;
              break;
          case "decimal":
              cls = java.lang.Number.class;
              break;
          case "date":
              cls = java.util.Date.class;
              break;
          default:
              cls = java.lang.String.class;
          }
         System.out.println("createMaskFormatter():" + formatter.toString());
          formatter.setValueClass(cls);
       } catch (java.text.ParseException exc) {
          System.err.println("reateMaskFormatter(): formatter is bad: " + exc.getMessage());
       }
       return formatter;
    }

    @Override
    public String evaluateDbScreenValueDefinition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public DTfield getMyObjID() {
        return myObjID;
    }
/*
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
        AbstractFormatter fmtr = this.getFormatter();
        System.out.println("DTfield.setEnableeed():" + enabled + " fmtr:" + fmtr.getClass().getName());
        if (fmtr instanceof DateMaskFormatter) {
            ((DateMaskFormatter)fmtr).refreshForEnabled(this);
        }
        }
    }
*/
}
 