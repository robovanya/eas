/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.widgets;
import java.awt.Color;
import system.Kernel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class DTXfield extends DTfield implements eoc.iEOC_DBtableField {
    private String dbScreenValueDefinition = "";
    private PreparedStatement      ps;
    private ResultSet              rs;  
    private String dbTextFieldName = "";

    public String getDbTextFieldName() {
        return dbTextFieldName;
    }

    public void setDbTextFieldName(String dbTextFieldName) {
        this.dbTextFieldName = dbTextFieldName;
    }

    public DTXfield() {
          super();  
          myObjID = this;
          //myObjID = this;
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
       this.addMouseListener(super.getMouseListeners()[0]
        );
       
    }


    @Override
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
               String rv;
               String vfwID = FnEaS.sEntry(FnEaS.iNumEntries(rtv,","), rtv, ",");
               rv = rtv.replace("," + vfwID, "");
               System.out.println("DTXfield_putRetValToValues()_XPUTTIGrtv:" + rtv); 
               System.out.println("DTXfield_putRetValToValues()_XPUTTIGwfwID:" + vfwID); 
               System.out.println("DTXfield_putRetValToValues()_XPUTTIGrv:" + rv); 
               dbTableField.valueObjectForWrite = vfwID;
               myObjID.setText(rv);
             }
       
    }
    
    public boolean commitScreenValue() {
        //return putStringToValue (this.getText());
        return true;
    }
  //  vyriesit, ako ziskat druhu hodnotu -- asi by field nemal byt nikdy enablovany len volba vyberu povolena
  //  pozno staci dat aj do dtfield, ale kernel-citanie widgetov treba aj tak upravit nejakym sposobom
    public boolean putStringToValue (String t) {
        if (FnEaS.iNumEntries(t, ",") > 1 ) {
            
        }
//QQQ        System.out.println("DTXputStringToValue() for field: " 
//QQQ          + super.getDBtableField().dbColumnInfo.columnname + " = " + t
//QQQ          + "genericdatatype: " + super.getDBtableField().dbColumnInfo.genericdatatype);
 //     dbTableField
        try {
            if (t==null) t = "";
            switch (super.getDBtableField().dbColumnInfo.genericdatatype) {
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
            Logger.getLogger(DTXfield.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public void setValue(Object valueObj) {
                super.setValue(valueObj); 
                // valueObj sa nesmie ulozit. Uklada sa ID vety po vybere
                dbTableField.valueObjectForWrite = valueObj;
    }
    // displayValue je akymsi suctom funkcii setText() a setValue()
    // ktora donuti objekt 'resetnut' lubovolne predosle hodnoty
    @Override
    public void displayValue(String sValue) {
        String sv = getdbScreenValueDefinition();
        String funcName;
        Object o = sValue;
        if (sv.startsWith("FUN@")) {
            funcName = sv.substring(4);
//@@@@@@@@@@@@@@@@@@@
                System.out.println("DTXdisplayValue INVOOOKINGGG INN;" + methodSource.getClass().getSimpleName()
                 + " with param: " + o);
                 Method mtd = FnEaS.getEOCMethod(methodSource, funcName, new Class[] {Object.class});
                 

/*
                 if (mtd==null) {
                     mtd = FnEaS.getEOCMethod(methodSource, funcName, new Class[] {null});
                     if (mtd!=null) callType = 2;
                 }
*/        
                 if (mtd==null)  {
                     this.setText("NO_FUNCTION_" + funcName + " for_val:" + o.toString() + " IN methodSOURCE: " 
                     + FnEaS.sObjName(methodSource));
                     return;
                 }
                 else {
                      //myTable.setValueAt( "FUNCTION_OK_" + funcName, rowIdx, colIdx);
                       String calcVal = "DTX_NULL_calcValue";
                try {
                    calcVal = (String) mtd.invoke(methodSource, o);
                } catch (IllegalAccessException ex) {
                    System.out.println("ERR_A");
                    Logger.getLogger(DTXfield.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    System.out.println("ERR_B");
                    Logger.getLogger(DTXfield.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    System.out.println("ERR_C:" + ex.getCause());
                    Logger.getLogger(DTXfield.class.getName()).log(Level.SEVERE, null, ex);
                }
                       this.putStringToValue(sValue);
                       this.setText(calcVal);
                }        
        } //         if (sv.startsWith("FUN@")) {
        else {
            this.putStringToValue(sValue);
//   odstavene 2015-11-05 03:31        
            this.setMainFormatter();
            this.setText(sValue);
       }     
        
    }
/*****
 * MOOOVEEE Y DTfield
 * *************************/
    public String getdbScreenValueDefinition() {
         return dbScreenValueDefinition; 
    }

    public void setdbScreenValueDefinition (String valDef) {
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
/*
    public String getDbValueForWrite() {
        return dbScreenValueDefinition;
    }
    
    public void setDbValueForWrite(String s) {
        dbScreenValueDefinition = s;
    }
  */  
    private String getScreenValueFromDefinition() {
       //// System.out.println("dbScreenValueDefinition==" + dbScreenValueDefinition
       ////       + " DTfield.DBfieldname:" + this.dbFieldName);
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
        else {
            if (dbScreenValueDefinition.length() > 0)
                return "";
            // v DTXfield je v nom nazov stlpca - na co som to pouzil ?
            //return "NO-QRY_" + dbScreenValueDefinition;
        }
        return "";
    }
    
    @Override
    public String evaluateDbScreenValueDefinition() {
        return Kernel.evaluateDbScreenValueDefinition
                                 (this, MyCn.getConn(), dbScreenValueDefinition);
    }

    @Override
    public void clear() throws ParseException {
       //// System.out.println("dbTableField.dbColumnInfo.genericdatatype>>>>" 
       ////         + dbTableField.dbColumnInfo.genericdatatype);
        super.clear(); //To change body of generated methods, choose Tools | Templates.
        // setValue(XX) sa vbykonalo v super-objekte !
        this.setText("");
        /**
        switch (dbTableField.dbColumnInfo.genericdatatype) {
            case "string":
                this.setText("");
                //this.setValue("");
                break;
            case "integer":
                this.setText("0");
                //this.setValue(0);
                break;
            case "decimal":
                //this.setText("0.00");
                //this.setValue(0.00);
                break;
            case "date":
                this.setText("  .  .    ");
                //this.setValue(new Date());
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
        * ***/
        
    }

    
}
 