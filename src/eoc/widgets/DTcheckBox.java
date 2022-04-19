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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

/**
 *
 * @author rvanya
 */
public class DTcheckBox extends JCheckBox implements eoc.iEOC_DBtableField {

    private DTcheckBox myObjID;
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
             boolean bSel = false;
             if (strValue != null) {
                 strValue = strValue.trim();
                 if (strValue.equals("1") || strValue.toLowerCase().equals("true"))
                     bSel = true;
             }
             this.setValue(bSel);
             this.setSelected(bSel);
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
    
    
    public DTcheckBox() {
          super();  
          myObjID = this;
          //this.addFocusListener(new eoc.EOCFocusListener());
    this.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent itmEvt) {
            if (((JCheckBox) itmEvt.getSource()).isEnabled()) {
            if (itmEvt.getStateChange()==ItemEvent.SELECTED) {
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
         
/*
       addPropertyChangeListener(new PropertyChangeListener() {
           @Override
           public void propertyChange(PropertyChangeEvent evt) {
               System.out.println("PropChangListener:: " + getText()+" --EVT>" + evt.getPropertyName());
                if (evt.getPropertyName().equals("value")) {
                        // zatial len hlasi udalost, 
                     System.out.println("PropChangListenerINcheckBoxx:: " + getText() + " --EVT>" + evt.getPropertyName());
//                    JFormattedTextField tf;
//                    tf = (JFormattedTextField) evt.getSource();
//                    if  (dbScreenValueDefinition.equals(""))
//                        dbTableField.valueObjectForWrite = tf.getValue();
                }
                else if (evt.getPropertyName().equals("text")) {
                        // zatial len hlasi udalost, 
                         System.out.println("PropChangListenerINcheckBoxx:: " + getText() + " --EVT>" + evt.getPropertyName());
                        ////setValue(getFormatter().valueToString(getText()));
                        /////setValue(getText());
                }
           }
        });
*/       
        addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myObjID.setValue((myObjID.isSelected()?1:0));
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
        this.setSelected(false);
        if (dbTableField == null) return;
        if (dbTableField.bCreationErrors) return;
        /*
        System.out.println("Self-clearingB: " 
               + dbTableField.dbColumnInfo.getFullColumnName()
                + " datatype = " + dbTableField.dbColumnInfo.genericdatatype
                );
        */
        Integer selectedValue = (this.isSelected()?1:0);
        switch (dbTableField.dbColumnInfo.genericdatatype) {
            case "string":
                this.setValue(selectedValue.toString());
                break;
            case "integer":
                this.setValue(selectedValue);
                break;
            case "decimal":
                this.setValue(Double.parseDouble(selectedValue.toString()));
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
        Integer selectedValue = (this.isSelected()?1:0);
        this.setValue(selectedValue);
        return true;  // putStringToValue (selectedValue.toString());
    }

    /*    
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
            Logger.getLogger(DTcheckBox.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
*/
    public void setValue(Object valueObj) {
        // ked je objekt vadny
        if (dbTableField.bCreationErrors) {
            dbTableField.valueObjectForWrite = null;
            this.setSelected(false);
            return;
        }
        boolean bSel = false;
        String strValue = valueObj.toString();
             if (strValue != null) {
                 strValue = strValue.trim();
                 if (strValue.equals("1") || strValue.toLowerCase().equals("true"))
                     bSel = true;
             }
//             this.setValue(bSel);
             this.setSelected(bSel);
                    
             dbTableField.valueObjectForWrite = (bSel?1:0);
    }

    public Object getValue() {
        if (dbTableField != null)
            return dbTableField.valueObjectForWrite; 
        else
            return null;
    }
/*
    @Override
    public void setText(String txt) {
////          this.getModel().
          setValue(txt);
    }

    public String getText() {
        return (this.isSelected()?"1":"0");
    }
*/
    @Override
    public String evaluateDbScreenValueDefinition() {
        return Kernel.evaluateDbScreenValueDefinition
                                 (this, MyCn.getConn(), dbScreenValueDefinition);
    }
    
}
 