/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.widgets;
import eoc.database.DBconnection;
import system.Kernel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author rvanya
 */
public class DTcomboBox extends JComboBox<String> implements eoc.iEOC_DBtableField {

    private DTcomboBox myObjID;
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
    //private String fullColumnName = ".";    
    private eoc.dbdrv.DBtableField dbTableField;
    private boolean bInitialized = false;
    private DBconnection MyCn;
    private PreparedStatement      ps;
    private ResultSet              rs;  

    DefaultComboBoxModel<String> cbmdl;
    String[] cbmdl_item_list;
    
    public void setdbScreenValueDefinition(String valDef) {
        // QQQ - bolo by treba testovat obsah definicie, nez sa to pouzije,
        // QQQ   ale len z beziaceho prostredia, nie z IDE
        dbScreenValueDefinition = valDef;
        /* POZNAMKY:
         - Prazdna hodnota znamena, ze sa vypise hodnota vo valueObjekte
           DBtableField objektu
         - inak sa ako text vypise obsah vysledku SQL dotazu SQL@<kod dotazu>
           ( ! dotaz musi selektovat jednu hodnotu do stringu s menom retVal !);
           alebo String hodnota z funkcie FUN@<nazovFunkcie()>
        */
    }

    // displayValue je akymsi suctom funkcii setText() a setValue()
    // ktora donuti objekt 'resetnut' lubovolne predosle hodnoty
    public void displayValue(String strValue) {
//             Kernel.Msg("displayValue-pocetkomponetkomb:" + this.getComponents().length);
             this.putStringToValue(strValue);
       //      this.setMainFormatter();
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
        dbScreenValueDefinition = s;
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
        dbTableField = new eoc.dbdrv.DBtableField(
                   (Component) this, cnn,dbOwnerName,dbTableName,dbFieldName);
        // doplnenie pripadnych NUll-hodnot attributov
        // podla vlastnosti stlpca z tabukly v databaze
        setDBpropertiesFromColumnInfo();
        setMainFormatter();
        bInitialized = true;
    }

    public void setMainFormatter() {
       // necinna metoda - kvoli kompaktibilite s DTfield
    }
    
    public boolean isInitialized() {
        return bInitialized;
    }
    
    
    public DTcomboBox() {
          super();  
          myObjID = this;
          //this.addFocusListener(new eoc.EOCFocusListener());
    this.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent itmEvt) {
            if (((JComboBox) itmEvt.getSource()).isEnabled()) {
            if (itmEvt.getStateChange()==ItemEvent.SELECTED) {
               System.out.println("Selected item===" + itmEvt.getItem());
               commitScreenValue();
            }
            }
        }          
    });
          this.addMouseListener(new MouseAdapter() { 
          public void mousePressed(MouseEvent me) { 
              if (me.isControlDown()) {
                Kernel.staticMsg("<html><B>Informácie k objektu: </B>"
                   + dbTableField.dbColumnInfo.tablename 
                      + "." + dbTableField.dbColumnInfo.columnname
                   + "<BR> Typ objektu: <B>" + me.getComponent().getClass().getSimpleName() + "</B>"
                   + "<BR> DB_datatype: <B>" + dbTableField.dbColumnInfo.realdatatype + "</B>"
                   + "<BR>Generic_type: <B>" + dbTableField.dbColumnInfo.genericdatatype + "</B>"
                   + "<BR>FORMATstring: <B>" + dbTableField.dbColumnInfo.formatString + "</B>"
                   + "<BR>DBFORMATstring: <B>" + dbFormatString + "</B>"
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
       addPropertyChangeListener(new PropertyChangeListener() {
           @Override
           public void propertyChange(PropertyChangeEvent evt) {
               ////System.out.println("PropChangListener:: " + getText()+" --EVT>" + evt.getPropertyName());
                if (evt.getPropertyName().equals("value")) {
                        // zatial len hlasi udalost, 
                     System.out.println("PropChangListenerINcomboBoxx:: " + getText() + " --EVT>" + evt.getPropertyName());
                     /*
                    JFormattedTextField tf;
                    tf = (JFormattedTextField) evt.getSource();
                    if  (dbScreenValueDefinition.equals(""))
                        dbTableField.valueObjectForWrite = tf.getValue();
                     */
                }
                else if (evt.getPropertyName().equals("text")) {
                        // zatial len hlasi udalost, 
                         System.out.println("PropChangListenerINcomboBoxx:: " + getText() + " --EVT>" + evt.getPropertyName());
                        ////setValue(getFormatter().valueToString(getText()));
                        /////setValue(getText());
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
        ////System.out.println("Self-clearingA: " + dbTableField);
        this.setSelectedIndex(0);
        if (dbTableField == null) return;
        if (dbTableField.bCreationErrors) return;
        /*
        System.out.println("Self-clearingB: " 
               + dbTableField.dbColumnInfo.getFullColumnName()
                + " datatype = " + dbTableField.dbColumnInfo.genericdatatype
                );
        */
        String selectedValue = this.getSelectedItem().toString();
        switch (dbTableField.dbColumnInfo.genericdatatype) {
            case "string":
                this.setValue(selectedValue);
                break;
            case "integer":
                this.setValue(Integer.parseInt(selectedValue));
                break;
            case "decimal":
                this.setValue(Double.parseDouble(selectedValue));
                break;
            case "date":
                 DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                 this.setValue(df.parse(selectedValue));
               break;
            case "boolean":
                this.setValue(false);
                break;
            default:
                this.setValue(selectedValue);
                break;
        }
    }

    public boolean commitScreenValue() {
        return putStringToValue (this.getSelectedItem().toString());
    }
    
    public String getFirstItemBeginAs(String bgn) {
        String rv = null;
        for (int i = 0; i < this.getItemCount(); i++) {
            if (this.getItemAt(i).startsWith(bgn)) {
                rv = this.getItemAt(i);
                break;
            }
        }
        return rv;
    }
    
    public boolean putStringToValue (String t) {
        //System.out.println("putStringToValue() = " + t);
        try {
            if (t==null) t = "";
/////           System.out.println("puttingcobox:" + dbTableField.dbColumnInfo.genericdatatype + " displajig:" + t);
            switch (dbTableField.dbColumnInfo.genericdatatype) {
                case "character":
                case "string":
                    //QQQ sem musi ist test na zatial neexistujucu premennu,
                    //QQQ ci sa moze/ma vyberat tymto sposobom
                    String realValue = getFirstItemBeginAs(t);
                    this.setValue(realValue);
/////            System.out.println("puttingcoboxSeelitem:" + realValue);
                    this.setSelectedItem(realValue);
                    break;
                case "integer":
                    t = t.replace('\160', '\0');
                    if (t.equals("")) t = "0";
                    this.setValue(Integer.parseInt(t));
                    break;
                case "decimal":
                    if (t.equals("")) t = "0.0";
                    this.setValue(Double.parseDouble(t));
                    break;
                case "date":
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
            Logger.getLogger(DTcomboBox.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public void setValue(Object valueObj) {
        // ked je objekt vadny
        if (dbTableField.bCreationErrors) {
            //super.setValue(null); 
            dbTableField.valueObjectForWrite = null;
            cbmdl = new DefaultComboBoxModel<String>();
            cbmdl.removeAllElements();
            cbmdl.addElement("<none>");
            this.setModel(cbmdl);
            return;
        }
        String itm = "";
        // ked je to standardny comboBox bez dotazu na vyber
        if  (dbScreenValueDefinition.equals("") ||  dbScreenValueDefinition.equals(null)) {
            if (cbmdl_item_list==null) {
                itm_block:
                for (int i = 0; i < this.getModel().getSize(); i++) {
                   itm = this.getModel().getElementAt(i); 
                   if (itm.startsWith(valueObj.toString())) {
                       super.setSelectedItem(itm); 
                       break itm_block;
                   }
                }
            } 
                    
            dbTableField.valueObjectForWrite = itm;
            // !!! v ComboBoxe - setText odkazuje na setValue !!!
            // this.setText(valueObj.toString());
        }        
        else {
            // value sa ulozi do dbTableField
            dbTableField.valueObjectForWrite = valueObj;
            // na obrazovku sa ulozi ofdvodena hodnota, na zaklade
            // dbScreenValueDefinition
            String rv = getScreenValueFromDefinition();
            
       //// System.out.println("dbScreenValueDefinitionInDTcomboBox.setValue==>>" + rv);
            // !!! v ComboBoxe - setText odkazuje na setValue !!!
            // this.setText(rv);
        }
        
    }

    private String getScreenValueFromDefinition() {
        ////System.out.println("dbScreenValueDefinition==" + dbScreenValueDefinition
        ////      + " DTcomboBox.DBfieldname:" + this.dbFieldName);
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
                    String curValW = dbTableField.valueObjectForWrite.toString();
                    ps.setInt(1, Integer.parseInt(curValW));
                    rs = ps.executeQuery();
                    rs.next();
                    return rs.getString("retVal");
             } catch (SQLException ex) {
                return "...";
                //Logger.getLogger(DTfield.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else 
            return "NO-QRY";
    }
    
    public Object getValue() {
        if (dbTableField != null)
            return dbTableField.valueObjectForWrite; 
        else
            return null;
    }

    @Override
    public void setText(String txt) {
////          this.getModel().
          setValue(txt);
    }

    public String getText() {
        Integer idx = this.getSelectedIndex();
        if (idx == null || idx < 0) return "";
        else return this.getSelectedItem().toString();
    }

    @Override
    public String evaluateDbScreenValueDefinition() {
        return Kernel.evaluateDbScreenValueDefinition
                                 (this, MyCn.getConn(), dbScreenValueDefinition);
    }
    
}
 