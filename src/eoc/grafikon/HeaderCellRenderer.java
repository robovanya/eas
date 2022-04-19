/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.grafikon;

/**
 *
 * @author rvanya
 */
 
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
 
public class HeaderCellRenderer extends DefaultTableCellRenderer 
{
private static final long serialVersionUID = 6703872492730589499L;

    public Component getTableCellRendererComponent(JTable table, 
            Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component cellComponent = 
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
        if (!isSelected)
            cellComponent.setBackground(Color.getHSBColor((float) 0.4,(float)0.1, (float)0.95));
        if (isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }
        
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
