/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

/**
 *
 * @author rvanya
 */
 
import eoc.grafikon.*;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
 
public class HeaderCellRenderer extends DefaultTableCellRenderer 
{
private static final long serialVersionUID = 6703872492730589499L;
    Pnl_calendarTable tblContainer;
    boolean bVisibleNullValues /*bLivingCells*/ = false;

    HeaderCellRenderer(Pnl_calendarTable obj) {
        super();
        tblContainer = obj;
    }

    public Component getTableCellRendererComponent(JTable table, 
            Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component cellComponent = 
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        JLabel jlb = (JLabel) cellComponent;
        Border border = BorderFactory.createEtchedBorder();
        jlb.setBorder(border);

        Integer[] iselCols = tblContainer.selCols;
        if (iselCols == null) {
            iselCols = new Integer[1];
            iselCols[0] = -1;
        }
        cellComponent.setBackground(tblContainer.rowHeadBtnColor);
        if (Arrays.asList(iselCols).contains(column)) 
            cellComponent.setBackground(tblContainer.getMyTable().getSelectionBackground());
        else
          cellComponent.setBackground(Color.getHSBColor((float) (0.08 + 0),(float)0.3, (float)0.99));
        
/*
 
        if(table.getValueAt(row, column).equals("Y")){
            cellComponent.setBackground(Color.YELLOW);
        } else if(table.getValueAt(row, column).equals("N")){
            cellComponent.setBackground(Color.GRAY);
        }
        */
        /*
        if (value.getClass()==boolean.class) {
        }
        else setText((String)value);
        */
        /*
        if (!isSelected)
            cellComponent.setBackground(Color.getHSBColor((float) 0.4,(float)0.1, (float)0.95));
        if (isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }
        */
        /*
        else
        {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        */
       return cellComponent;
    }
}
