/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.widgets;
import eoc.DateMaskFormatter;
import eoc.IEOC_DateSupporter;
import eoc.IntegerFormatter;
import eoc.TimeFormatter;
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
public class DTvariable extends JFormattedTextField {

    DTvariable myObjID;
    //private String fullColumnName = ".";    
    /*
    eoc.dbdrv.DBtableField dbTableField;
    */
    // namiesto eoc.dbdrv.DBtableField.xxxxx
    private String myGenericDataType = "string";
    private boolean bNullAllowed = false;
    private String myFormatString = "xxxxxxxxxx";
    private Class  myValueClass = null;
    private Object myValueObj   = null;
    private Object myValueStr   = null;
    
    private boolean bInitialized = false;
    Connection MyCn;
    private boolean   bFormatterAdded; // ci bol spravny formatter nainstalovany  
    private Kernel krn;
    private eoc.IEOC_VisualObject myViewer;
    Object methodSource;
    private DateSupporter dateSupporter;

    public void setKrn(Kernel kr) {
       krn = kr;    
    }
    
    public void setNullAllowed(Boolean b) {
       bNullAllowed = b;    
    }

    public boolean getNullAllowed() {
       return bNullAllowed;    
    }

    public void setFormatString(String s) {
       myFormatString = s;
    }

    public String getFormatString() {
       return myFormatString;
    }

    public void setValueClass(Class c) {
       myValueClass = c;    
    }

    public Class getValueClass() {
       return myValueClass;    
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
    
    public void setViewer(eoc.IEOC_VisualObject vw) {
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

    /*
    @Override
    public eoc.dbdrv.DBtableField getDBtableField() {
        return dbTableField;
    }
*/
    // tuto metodu vola kernel pri ostrom behu programu
    // Vytvoria sa vnorene objekty, ktore pri design-mod-e neexistuju
    public void initialize(Connection cnn) {
        
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
        /*
        dbTableField = new eoc.dbdrv.DBtableField(
                   (Component) this, cnn,dbOwnerName,dbTableName,dbFieldName);
        */
       ///// System.out.println("DBTABLEFILDCREATON" + (dbTableField == null) + " FORR: " + this.dbFieldName);
        // doplnenie pripadnych NUll-hodnot attributov
        // podla vlastnosti stlpca z tabukly v databaze
        //        setDBpropertiesFromColumnInfo();
        setMainFormatter();
        if (myGenericDataType.equalsIgnoreCase("date")) {
            
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
                               Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                       if (dotPos == 3 || dotPos == 4) {
                          String s = dateSupporter.getMonthStr();
                           try {                                  
                               myObjID.getDocument().insertString(3,s, null);
                               myObjID.getCaret().setDot(6);
                           } catch (BadLocationException ex) {
                               Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                       if (dotPos >= 6) {
                          String s = dateSupporter.getYearStr();
                           try {                                  
                               myObjID.getDocument().insertString(6,s, null);
                               myObjID.getCaret().setDot(10);
                           } catch (BadLocationException ex) {
                               Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                    }
                    else {
                        try { 
                            myObjID.getDocument().insertString(myObjID.getCaret().getDot(), String.valueOf(e.getKeyChar()), null);
                        } catch (BadLocationException ex) {
                            Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
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
            if (input instanceof DTvariable) {
                DTvariable dtf = (DTvariable) input;
                AbstractFormatter currFmtr = dtf.getFormatter();
                if (currFmtr instanceof DateMaskFormatter) {
                    String dtStr = dtf.getText();
                    verified = parseDate(dtStr,(DateMaskFormatter) currFmtr);
                    if (!verified && bNullAllowed) {
                        // test praznej hodnoty
                        verified = DateMaskFormatter.isEmptyDate(dtStr);
                    }
                    
                    if (!verified) {
                        krn.Message("E", "Nesprávny dátum: " + dtStr + "\nnullallowed:" + bNullAllowed, "Chybný dátum");
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
        if (myFormatString.equals("$TIME") && 
            (currFmtr==null || (!(currFmtr instanceof TimeFormatter)))) {
//            updated = true;
            //// System.out.println("dbFormatString====" + dbFormatString + "  class:" + dbTableField.getClass());
            final TimeFormatter timeFmtr;
            timeFmtr = new TimeFormatter();
            timeFmtr.setValueClass(myValueClass);
            timeFmtr.setAllowsInvalid(false);
            try {
                timeFmtr.setMask("##:##");
            } catch (ParseException ex) {
                Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
            }
            myFormatString = "##:##";
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
                Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
            }
            ///// System.out.println("AtTimeFormatter: text:" + this.getText() + " formatted:" + s );
            bFormatterAdded = true;
            return;
        } // if (dbFormatString.equals("$TIME")) {
        else { 
            switch (myGenericDataType.toLowerCase()) {
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
                    Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
                 }
                  setFormatString("##" + delim + "##" + delim + "####");
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
                 decimalFormat.setMaximumFractionDigits(myFormatString.length() - myFormatString.indexOf(".") - 1);
                 decimalFormat.setMaximumIntegerDigits(myFormatString.indexOf("."));
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
                 System.out.println(" ->genericDataTypeDEFAULTFORMATTER==" + myGenericDataType.toLowerCase()
                 + " myValueClass ===" + getValueClass());
                 if (currFmtr instanceof DefaultFormatter) break;
                 this.setValue("");
                 defaultFmtr  = new DefaultFormatter();
                 defaultFmtr.setValueClass(myValueClass);
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
    
    public boolean isInitialized() {
        return bInitialized;
    }
    
    
    public DTvariable() {
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
                   + "<BR> Typ objektu: <B>" + myValueClass.getSimpleName() + "</B>"
                   + "<BR>Generic_type: <B>" + myGenericDataType + "</B>"
                   + "<BR>FORMATstring: <B>" + myFormatString + "</B>"
                   + "<BR>DBFORMATTER: <B>" 
                     + ((myObjID.getFormatter()==null)?"<NULL>":myObjID.getFormatter().toString()) + "</B>"
                   + "</html>"
                );
              }
          } 

/*           
            @Override
            public void mouseClicked(MouseEvent evt) {
                String retVal;
                if (evt.getClickCount()==2 && dbChooseDefinition.length() > 0) {
                        retVal = 
                        krn.genVyberForMe(myViewer,myObjID, dbChooseDefinition);
                        //krn.Message("DTfield_MouseClicked_rrretVaaal is: " + retVal);
                        putRetValToValues(retVal);
                        evt.consume();
                }
            }
*/            

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

              @Override
              public void mouseClicked(MouseEvent e) {
//                  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
              }
        });
       
       this.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
          ///// this.OutPrintln("Kernel-actionPerformed-inMyObj!!!");
           System.out.println("krn_e.getActionCommand()==" + e.getActionCommand());
           } 
        });

          
        //listeners
       addPropertyChangeListener(new PropertyChangeListener() {
           @Override
           public void propertyChange(PropertyChangeEvent evt) {
               ////System.out.println("PropChangListener:: " + getText()+" --EVT>" + evt.getPropertyName());
                if (evt.getPropertyName().equals("value")) {
                    JFormattedTextField tf;
                    tf = (JFormattedTextField) evt.getSource();
                    /*
                    if (!(myObjID instanceof DTXfield)) 
                        dbTableField.valueObjectForWrite = tf.getValue();
                     */
                }
                else if (evt.getPropertyName().equals("text")) {
                    try {
                        setValue(getFormatter().valueToString(getText()));
                        /////setValue(getText());
                    } catch (ParseException ex) {
                        Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
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
               System.out.println("DTvariable_putRetValToValues()_PUTTIGrtv:" + rtv); 
               myObjID.setText(FnEaS.sEntry(1, rtv, "^"));
               /*
               myObjID.getDBtableField().setDBfieldValueObjectForWrite(FnEaS.sEntry(2, rtv, "^"));
               */
            }
       
    }

/*
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
*/    
    public void clear() throws ParseException {

        setMainFormatter();
        switch (myGenericDataType) {
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
        krn.debugOut(this,5,"DTvariable_FputStringToValue() = " + t + " GENERICDATATYPEis: " + myGenericDataType);
        try {
            if (t==null) t = "";
            switch (myGenericDataType) {
                case "character":
                    this.setValue(t);
                    break;
                case "integer":
                    //this.setText("0");
                    t = t.replace('\160', '\0');
                    ////System.out.println("ParsingInttt:" + t);
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
            Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public void setValue(Object valueObj) {
        if (valueObj.toString()=="0") valueObj = 0.0;
////        if  (dbScreenValueDefinition.equals("") ||  dbScreenValueDefinition.equals(null)) {
/////////        if  (!(myObjID instanceof DTXfield)) {
                myValueObj = valueObj;
                if (!myGenericDataType.equalsIgnoreCase("Date"))
                    super.setValue(myValueObj); 
                else {
                    if (myValueObj.toString().contains("DAY_OF_MONTH="))
                        myValueStr = FnEaS.crazyDateRead(myValueObj.toString(),".");
                    else
                        myValueStr = myValueObj.toString();
                    System.out.println("DaaaTeee: " + myValueObj.toString() + "\n ISSSS: " + myValueStr);
                    super.setValue(myValueStr); 
                }
               //dbTableField.valueObjectForWrite = valueObj;
            try {
                this.setText(this.getFormatter().valueToString(valueObj));
            } catch (ParseException ex) {
                Logger.getLogger(DTvariable.class.getName()).log(Level.SEVERE, null, ex);
            }

//////////        }        
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
        return myValueStr;
        //return super.getValue(); 
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

    /*
    @Override
    public String evaluateDbScreenValueDefinition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */
    
    public DTvariable getMyObjID() {
        return myObjID;
    }
    public void setGenericDataType(String dtp) {
        myGenericDataType = dtp;    
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
 