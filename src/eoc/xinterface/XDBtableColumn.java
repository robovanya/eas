/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.xinterface;

import eoc.EOC_message;
import eoc.database.DBconnection;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import system.Kernel;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.TableCellRenderer;
/**
 *
 * @author rvanya
 */
public class XDBtableColumn extends eoc.dbdata.DBtableColumn {
   private eoc.xinterface.XTable myXTable = null;
   private JTable                myTable = null;

   private class MyTableHeaderRenderer extends JLabel implements TableCellRenderer {

       @Override
       public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
           setText(value.toString());
           setToolTipText((String) value);
           Border border = BorderFactory.createSoftBevelBorder(SoftBevelBorder.RAISED) 
                                        //.createLineBorder(Color.MAGENTA, 1)
                   ;
           this.setBorder(border);
           Font font = this.getFont();
           if (isCalculated())
              font = font.deriveFont(Font.ITALIC);
           this.setFont(font);
           return this;
       }
    }
       
   @Override
   public void setSortVector(String sVect) {
   if (!sVect.equals("")) {
      super.setSortVector(sVect);
      ////System.out.println("setOrderingFrom_EOC_TableColumn.setOrdering");
       myXTable.setOrdering(colDefinition.getFieldName(), sVect,
                            colDefinition.getGenericDataType());
       myXTable.rebuildQuery();
       myXTable.open_Query();
       myXTable.goToFirst(null);
   }
}
/*
public XDBtableColumn() {

}
*/
public XDBtableColumn(Kernel krnl, DBconnection cnn) { 
    super(krnl,cnn);    
    this.setHeaderRenderer(new MyTableHeaderRenderer());
//    this.getHeaderRenderer(). .getTableCellRendererComponent(null, headerValue, isResizable, bResizable, row, column)
}

public void setMyXTable(XTable jXtbl) {
   myXTable = jXtbl;
}
public XTable getMyXTable() {
   return myXTable;
}

public void setMyTable(JTable jTbl) {
   myTable = jTbl;
}
public JTable getMyTable() {
   return myTable;
}
}
