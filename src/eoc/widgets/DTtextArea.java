/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.widgets;
import eoc.database.DBconnection;
import system.Kernel;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class DTtextArea extends JTextArea implements eoc.iEOC_DBtableField {
    private class myFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
    ////        System.out.println("GAJNEDFOCU");
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextArea txa = (JTextArea) e.getComponent();
////            System.out.println("GLOSTEDD---FOCU" + txa.getText());
            dbTableField.setDBfieldValueObjectForWrite(txa.getText());
        }
    }
    
    private String dbOwnerName;
    private String dbTableName;
    private String dbFieldName;
    private String dbFieldLabel;
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
    private String dbScreenValueDefinition = "";
    private String genericDataType = "";
    private eoc.dbdrv.DBtableField dbTableField;
    private boolean bInitialized = false;
    private DBconnection MyCn;
    private PreparedStatement      ps;
    private ResultSet              rs;  

    public void setdbScreenValueDefinition(String valDef) {
        // QQQ - bolo by treba testovat obsah definicie, nez sa to pouzije,
        // QQQ   ale len z beziaceho prostredia, nie z IDE
        dbScreenValueDefinition = valDef;
        /* POZNAMKY:
         - Prazdna hodnota znamena, ze sa vypise hodnota vo valueObjekte
           DBtableField objektu
         - inak sa ako text vypise obsah vysledku SQL dotazu
           ( ! dotaz musi selektovat jednu hodnotu do stringu s menom retVal !);
        -  alebo vysledok String funkcie, ktorej sa odovzda ako parameter
           hodnota vo valueObjekte (vlastne valueObjekt)
        */
    }

    // displayValue je akymsi suctom funkcii setText() a setValue()
    // ktora donuti objekt 'resetnut' lubovolne predosle hodnoty
    public void displayValue(String strValue) {
        this.putStringToValue(strValue);
    //    this.setText(strValue);
        
    }
    public String getDbValueForWrite() {
        if (dbTableField != null)
            return dbTableField.getValueForWriteAsString("'");
        else 
            return null;
       // return xxxdbScreenValueDefinition;
    }
    
    public void setDbValueForWrite(String s) {
        dbTableField.setDBfieldValueObjectForWrite(s);
        ////x  dbScreenValueDefinition = s;
    }
    
    @Override
    public eoc.dbdrv.DBtableField getDBtableField() {
        return dbTableField;
    }

    public final class LengthRestrictedDocument extends PlainDocument {

     private final int limit;

     public LengthRestrictedDocument(int limit) {
        this.limit = limit;
        //// System.out.println("DocLengthLimitt=" + limit);
     }

     @Override
     public void insertString(int offs, String str, AttributeSet a) 
                 throws BadLocationException {
         if (str == null) return;

         if ((getLength() + str.length()) <= limit) 
            super.insertString(offs, str, a);
     } 
} // public final class LengthRestrictedDocument extends PlainDocument {
   
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
        dbTableField = new eoc.dbdrv.DBtableField(
                   (Component) this, cnn,dbOwnerName,dbTableName,dbFieldName);
        // doplnenie pripadnych NUll-hodnot attributov
        // podla vlastnosti stlpca z tabukly v databaze
        setDBpropertiesFromColumnInfo();
        ////System.out.println("DTfield_genericDataType in DTextAreaa==" + genericDataType);
          addMouseListener(new MouseAdapter() { 
          @Override
          public void mousePressed(MouseEvent me) { 
              if (me.isControlDown()) {
                Kernel.staticMsg("<html><B>Informácie k objektu: </B>"
                   + dbTableField.dbColumnInfo.tablename 
                      + "." + dbTableField.dbColumnInfo.columnname
                   + "<BR> Typ objektu: <B>" + me.getComponent().getClass().getSimpleName() + "</B>"
                   + "<BR> DB_datatype: <B>" + dbTableField.dbColumnInfo.realdatatype + "</B>"
                   + "<BR>Generic_type: <B>" + dbTableField.dbColumnInfo.genericdatatype + "</B>"
                   + "<BR>FORMATstring: <B>" + dbTableField.dbColumnInfo.formatString + "</B>"
                   + "<BR><BR>VALUE_FOR_WRITE: <B>" + dbTableField.getValueForWriteAsString("'") + "</B>"
                   + "</html>"
                );
              }
          } 
         }); 

         if (bInitialized && this.getDBtableField().bCreationErrors) {
             this.setBackground(Color.YELLOW/* .red*/);
         }

        //listeners
       addPropertyChangeListener("value", new PropertyChangeListener() {
           @Override
           public void propertyChange(PropertyChangeEvent evt) {
               if (!bInitialized) return;
               System.out.println("PropChangListener:: " + getText()+" --EVT>" + evt.getPropertyName());
                if (evt.getPropertyName().equals("value")) {
                    JFormattedTextField tf;
                    tf = (JFormattedTextField) evt.getSource();
                    if  (dbScreenValueDefinition.equals(""))
                        dbTableField.valueObjectForWrite = tf.getValue();
                    
                   System.out.println("procchang_value on: " + dbTableField.dbColumnInfo.columnname 
                    + " to: " + dbTableField.valueObjectForWrite.toString()
                    + "   caller: " + FnEaS.getCallerMethodName(1));
                }
                
           }
        });
       addPropertyChangeListener("text", new PropertyChangeListener() {
           @Override
           public void propertyChange(PropertyChangeEvent evt) {
               ////System.out.println("PropChangListener:: " + getText()+" --EVT>" + evt.getPropertyName());
//               tu updatnut value ??
               if (!bInitialized) return;
                if (evt.getPropertyName().equals("text")) {
                    System.out.println("propertychang_text:" + getText() + " on " + dbTableField.dbColumnInfo.columnname);
                    setValue(/*getFormatter().valueToString(*/getText()/*)*/);
                }
                
           }
        });
       
        this.addFocusListener(new myFocusListener());
        /*
        addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("ActListener:: " + getText()+" ACTION--> " + arg0.getActionCommand());
                //i what to set same value to TXTFLD2 using property change listener
            }
        });
        */
        
        bInitialized = true;
    }

    public boolean isInitialized() {
        return bInitialized;
    }
    
    public DTtextArea() {
        super();  
        addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_TAB)
                {
                    e.consume();
                    KeyboardFocusManager.
                        getCurrentKeyboardFocusManager().focusNextComponent();
                }

                if (e.getKeyCode() == KeyEvent.VK_TAB
                &&  e.isShiftDown())
                {
                    e.consume();
                    KeyboardFocusManager.
                        getCurrentKeyboardFocusManager().focusPreviousComponent();
                }
            }
        });
          
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

    public Object getDbDataType() {
        return dbDataType;
    }

    public void setDbDataType(Object dbDataType) {
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
    }
    
    public void setDBcolumnInfo(eoc.dbdrv.DBcolumnInfo colInf) {
        dbTableField.setDBcolumnInfo(colInf);
    }

    public void setDBpropertiesFromColumnInfo() {
        dbDataType      = dbTableField.dbColumnInfo.realdatatype;
        if (dbFormatString==null)
            dbFormatString  = dbTableField.dbColumnInfo.formatString;
//        if (dbDefaultValue==null)
//            dbDefaultValue  = dbTableField.dbColumnInfo.defaultValue;
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
    
    public void clear() {
        if (dbTableField == null) return;
        if (dbTableField.bCreationErrors) return;
///        System.out.println("Self-clearing: " 
///                + dbTableField.dbColumnInfo.getFullColumnName()
///                + " datatype = " + dbTableField.dbColumnInfo.genericdatatype
///                );
/*        
        System.out.println("Self-clearingB: " 
               + dbTableField.dbColumnInfo.getFullColumnName()
                + " datatype = " + dbTableField.dbColumnInfo.genericdatatype
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
                this.setText("");
                //777
//                this.setValue(new SimpleDateFormat("DD.MM.yyyy", 
//                                  Locale.ENGLISH).parse(null));
               break;
            default:
                this.setText("");
                break;
        }
    }
    // metody setText a setValue sa spoja, aby sa vykonavali spolu t.j. sucasne
    // ako ekvivalenti jeden druhemu. Vychadza to z generic data type EOC

    public boolean commitScreenValue() {
        ////System.out.println("COMMITING-getTEXT==" + this.getText());
        return putStringToValue (this.getText());
    }
    
    public boolean putStringToValue (String t) {
        //System.out.println("putStringToValue() = " + t);
        try {
            if (t==null) t = "";
            switch (dbTableField.dbColumnInfo.genericdatatype) {
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
//je setValue presmerovane do setText ? 
                    this.setValue(Double.parseDouble(t));
                    break;
                case "date":
                    //777
//                this.setValue(new Date(""));
                   //// System.out.println("datuummooo=" + t);
                    if (t==null) {
                        this.setText("");
                        this.setValue(null);
                    }
                    else {
                        if (t.length() > 0)
                            this.setValue(new SimpleDateFormat("yyyy-MM-DD",
                                              Locale.ENGLISH).parse(t));
                        else
                            this.setValue(new SimpleDateFormat("yyyy-MM-DD", 
                                              Locale.ENGLISH).parse(null));
                    }
                    break;
                default:
                    this.setValue(t);
                    break;
            }
        } catch (ParseException ex) {
            Logger.getLogger(DTtextArea.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
/*
      public void setValue(String s) {
        super.setText(s); 
        System.out.println ("TXA_setValue():" + s);
        dbTableField.valueObjectForWrite = s;
    }
*/
  public void setValue(Object valueObj) {
        if (!bInitialized) return;
        if (dbTableField.bCreationErrors) {
            super.setText(null); 
            dbTableField.valueObjectForWrite = null;
            return;
        }
        if (valueObj.toString()=="0") valueObj = 0.0;
        ///System.out.println("!!>>>!! --  setValue on " + dbTableField.dbColumnInfo.getFullColumnName()
        ///+ "  (valueType===" + dbTableField.dbColumnInfo.genericdatatype + ")  value==" 
        ///+ ((o != null) ? this.getValue().toString() : "<null>"))
        ///+ ((valueObj != null) ? valueObj.toString() : "<null>")
        ///+ " invooker:" + easys.FnEaS.getCallerMethodName()
        ///);
        if  (dbScreenValueDefinition.equals("") ||  dbScreenValueDefinition.equals(null)) {
                super.setText(String.valueOf(valueObj)); 
                dbTableField.valueObjectForWrite = valueObj;
                this.setText(String.valueOf(valueObj));
          ////System.out.println ("TXAX_setValue():" + valueObj);
        }        
        else {
            // value sa ulozi do dbTableField
            dbTableField.valueObjectForWrite = valueObj;
            // na obrazovku sa ulozi ofdvodena hodnota, na zaklade
            // dbScreenValueDefinition
            String rv = getScreenValueFromDefinition();
        ////System.out.println("dbScreenValueDefinition==>>" + rv);
            this.setText(rv);
        ////  System.out.println ("TXBX_setValue():" + rv);
      }
        
    }

    private String getScreenValueFromDefinition() {
        // QQQQ
        ////System.out.println("dbScreenValueDefinition==" + dbScreenValueDefinition);
        //return dbScreenValueDefinition;
        if (dbScreenValueDefinition.startsWith("QRY@")) {
            try {
            if (ps==null) 
                    ps = MyCn.getConn().prepareStatement(dbScreenValueDefinition.substring(4),
                            ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
             } catch (SQLException ex) {
                return "...";
                //Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                   //ps.setInt(1, currentTblId);
                    String curValW = dbTableField.valueObjectForWrite.toString();
                    ps.setInt(1, Integer.parseInt(curValW));
                    rs = ps.executeQuery();
                    rs.next();
                    return rs.getString("retVal");
             } catch (SQLException ex) {
                return "...";
                //Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
            }
                
//            return "QRYYY =>" +  dbTableField.valueObjectForWrite.toString();
        }
        else 
            return "NO-QRY";
    }
    
    public Object getValue() {
        if (!bInitialized) return "";
        System.out.println ("TXA_getValue():" + dbTableField.valueObjectForWrite);
        return dbTableField.valueObjectForWrite;
        //return super.getText(); 
    }

    @Override
    public void append(String str) {
        this.append(str); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getText() {
        //this.getText();
        return super.getText(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setText(String t) {
        super.setText(t); //To change body of generated methods, choose Tools | Templates.
        if (dbTableField!=null)
            dbTableField.valueObjectForWrite = t;
    }

    @Override
    public String evaluateDbScreenValueDefinition() {
        return Kernel.evaluateDbScreenValueDefinition
                                 (this, MyCn.getConn(), dbScreenValueDefinition);
    }

}
 