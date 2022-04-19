/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.dbdata;

import eoc.database.DBconnection;
import javax.swing.*;
import javax.swing.table.*;
import system.Kernel;
import system.FnEaS;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.util.Date;
/**
 *
 * @author rvanya
 */
public class DBtableColumn extends TableColumn {
   protected Kernel krn;
   private   DBconnection MyCn;
   private   Integer iMinWidth, iMaxWidth, iWidth, iPreferredWidth;
   private   boolean bResizable = true;
   private   String  sSortVector="";
   protected eoc.dbdata.ColumnDefinition colDefinition;

//!ZATIAL   private String  dbEnableWhen;
//!ZATIAL   private String  headerTooltyp;
//!ZATIAL   private String  cellTooltyp;
//!ZATIAL   private String  dbGroupList;
//!ZATIAL   private String  dbPrivateData;

// konstruktor 1
public DBtableColumn(Kernel krnl, DBconnection cnn) { 
   krn = krnl;    
   MyCn = cnn;
   iMinWidth       = this.getMinWidth();
   iMaxWidth       = this.getMaxWidth();
   iWidth          = this.getWidth();
   iPreferredWidth = this.getPreferredWidth();
////   System.out.println("DBtableColumnKonstruktor1= no initialStringAvailable" );
}

// konstruktor 2
public DBtableColumn(Kernel krnl, DBconnection cnn, String colDef) { 
   krn = krnl;    
   MyCn = cnn;
   iMinWidth       = this.getMinWidth();
   iMaxWidth       = this.getMaxWidth();
   iWidth          = this.getWidth();
   iPreferredWidth = this.getPreferredWidth();
   colDefinition   = new eoc.dbdata.ColumnDefinition(krn,MyCn,colDef);
   setHeaderValue(colDefinition.getFieldLabel());
////   System.out.println("DBtableColumnKonstruktor2=" + this.getdbTableName() + "." + this.getdbFieldName() + "labell:" + this.getFieldLabel());
}

// konstruktor 3
public DBtableColumn(Kernel krnl, DBconnection cnn, eoc.dbdata.ColumnDefinition colDef) { 
   krn = krnl;    
   MyCn = cnn;
   iMinWidth       = this.getMinWidth();
   iMaxWidth       = this.getMaxWidth();
   iWidth          = this.getWidth();
   iPreferredWidth = this.getPreferredWidth();
   colDefinition   = colDef;
   setHeaderValue(colDefinition.getFieldLabel());
////   System.out.println("DBtableColumnKonstruktor3=" + this.getdbTableName() + "." + this.getdbFieldName() + "labell:" + this.getFieldLabel());
}



public DBtableColumn() {
}

public class myCellRenderer extends 
        //DefaultTableCellRenderer 
        JLabel
                           implements TableCellRenderer {

    public myCellRenderer() {
        this.setOpaque(true); //aby bol background viditelny
        this.repaint();
    }

    @Override
    public Component getTableCellRendererComponent(
                            JTable table, Object txt,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        setForeground(Color.black);
        krn.OutPrintln("getTableCellRendererComponent-dbFieldName=" 
                + colDefinition.getFieldName());
        String oldText = (String) table.getValueAt(row, column);
        String myText = this.getText();
        if (oldText==null || txt==null) { return this; } // pre istotu

        if (!oldText.equals(txt)) {
           krn.OutPrintln("By som zistil: " + table.getValueAt(row, column) + " menim na: " + txt);
           this.setText(String.valueOf(txt));
        }
        else {
           if (!txt.equals(myText)) {
                 //krn.OutPrintln("txt <> myText - oldText " + txt + " <> " + myText + " - " + oldText );
                 this.setText(String.valueOf(txt));
            }
        }
        /* RGB
         * */
        if (table.isRowSelected(row)) {
            this.setBackground(new Color(204,102,255));
        }
        else {
            this.setBackground((row % 2) == 0
                ?new Color(255,255,204):new Color(204,255,255));
         }
        /*
        if (table.isRowSelected(row)) {
            this.setBackground(Color.RGBtoHSB(204,102,255,(Float[]) {10,10,10}));
        }
        else {
            this.setBackground((row % 2) == 0
                ?new Color(255,255,204):new Color(204,255,255));
         }
         * */
         return (Component) this;
    }
    
}


public void initialize(String colDef) {
    colDefinition = new eoc.dbdata.ColumnDefinition(krn, MyCn, colDef);
////    krn.OutPrintln("DBtableColumn_INITII==" + colDefinition.getFieldLabel() + "___" 
////            + colDefinition.getFieldName() + "___" +  colDefinition.getFieldLength());
//    this.setfieldLabel(colDefinition.getfieldLabel());
    // QQQWORK sirka stlpca sa neskor urci podla fontu pouzitim FontMetric-objeku
    Integer widthA = (colDefinition.getFieldLength() + colDefinition.getNumDecimals() + 2) * 6;
    Integer widthB = (colDefinition.getFieldLabel().length() + 2) * 6;
    Integer widthBiggest =  widthA >= widthB ? widthA : widthB;
    this.setWidth(widthBiggest);
    this.setPreferredWidth(this.getWidth());
    this.setMaxWidth(this.getWidth() * 2);
}

public void setOwnerName (String s) { colDefinition.setOwnerName(s);}

public String getOwnerName () { return colDefinition.getOwnerName(); }

public void setdbTableName (String s) { colDefinition.setTableName(s); }

public String getdbTableName () { return colDefinition.getTableName(); }

public void setdbFieldName (String s) { colDefinition.setFieldName(s); }

public String getdbFieldName () { return colDefinition.getFieldName(); }

public void setfieldLabel (String s) { 
   colDefinition.setFieldLabel(s);
   colDefinition.setHeaderValue(s);
//QQQWORK - tu by sa mal menit font hlavicky stlpca, pokial je stlpec 
//          specialneho typu (s == "!@@xxxxxx")   
//   this.myXTable.myTblModel.
//   Font myFont = this.getHeaderRenderer().getClass().

}

public String getFieldLabel () { return colDefinition.getFieldLabel(); }

public void setDataType (String s) { 
   colDefinition.setDataType(s);
   colDefinition.setGenericDataType(krn.getGenericDataType(s));
}

public String getDataType () { return colDefinition.getDataType(); }

public void setDefaultValue (String s) { colDefinition.setDefaultValue(s); }

public String getDefaultValue () { return colDefinition.getDefaultValue(); }

public void setFieldLength (Integer i) { 
    colDefinition.setFieldLength(i);
   // zistenie potrebnej sirky stlpca
   int biggerLength = (
       colDefinition.getFieldLength() > colDefinition.getFieldLabel().length()
       ? colDefinition.getFieldLength() : colDefinition.getFieldLabel().length());
   this.setWidth(biggerLength * 8);
   this.setPreferredWidth(biggerLength * 8);
   this.setMaxWidth((biggerLength + (biggerLength / 2)) * 8);
   //this.headerRenderer.
}

public void setNumDecimals (Integer i) { colDefinition.setNumDecimals(i); }

public void setdbFormatString (String s) { colDefinition.setFormatString(s); }

public void setTooltip (String s) { 
   colDefinition.setTooltip(s); 
 // this.getHeaderRenderer().getTableCellRendererComponent(null, headerValue, isResizable, isResizable, width, width). .setTooltip("XXX");
}
public boolean getHidden() { return colDefinition.getHidden(); }

    public String getGenericDataType() {
        return colDefinition.getGenericDataType();
    }

    public void setGenericDataType(String dataType) {
        colDefinition.setGenericDataType(dataType);
    }


public void setHidden(boolean bHide) {
   if (bHide & colDefinition.isHidden()) {return; }
   if ((!bHide) && !(colDefinition.isHidden())) {return; }
   if (bHide) {
       iMinWidth       = this.getMinWidth();
       iMaxWidth       = this.getMaxWidth();
       iWidth          = this.getWidth();
       iPreferredWidth = this.getPreferredWidth();
       bResizable      = this.getResizable();
       this.setMinWidth(0);
       this.setMaxWidth(0);
       this.setWidth(0);
       this.setPreferredWidth(0);
       this.setResizable(false);
       colDefinition.setHidden(true);
   }
   else {
       this.setMinWidth(iMinWidth);
       this.setMaxWidth(iMaxWidth);
       this.setWidth(iWidth);
       this.setPreferredWidth(iPreferredWidth);
       this.setResizable(bResizable);
       colDefinition.setHidden(false);
   }
}

public void setSortVector(String sVect) {
   if (!sVect.equals("")) {
       sSortVector = sVect;
   }
}
public String getSortVector() {
   return sSortVector;
}

// generic datove typy: string,integer,decimal,date,boolean
public Class getDataTypeClass() {
////    krn.OutPrintln("gggetGenericDataType() - column: " + this.getdbFieldName()
////                        + " == " + getGenericDataType());
    if (getGenericDataType() == null) return String.class;
    Class clss = null;
    switch (getGenericDataType().toLowerCase()) {
            case "integer": clss = Integer.class; break;
            case "decimal": clss = Double.class;  break;
            case "date":    clss = Date.class; break;
            case "boolean": clss = Boolean.class; break;
            default:        clss = String.class; break;
    }
////    krn.OutPrintln("gggetGenericDataType() - column: " + this.getdbFieldName()
////                        + " == " + getGenericDataType() + " -returning Class: " + clss.getSimpleName());
    return clss;
}



public boolean isCalculated() {
    return colDefinition.getFieldName().startsWith("FUN@");
}

public boolean isSortable() {
    return !isCalculated();
}
}
