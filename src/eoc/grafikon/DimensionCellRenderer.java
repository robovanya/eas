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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
 
public class DimensionCellRenderer extends DefaultTableCellRenderer 
{
//private static final long serialVersionUID = 6703872492730589499L;

    boolean bLivingCells           = false;
    public Component getTableCellRendererComponent(JTable table, 
            Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {

        // Ziskanie objektu v skumanej bunke(column) riadku(row) tabulky
        Component cellComponent = 
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // ked bunka nie je oznacena, vyfarbi sa podla toho,
        // ci je na parnom alebo neparnom riadku
        if (!isSelected) {
            if (column < 4) return cellComponent;
            if (table.getValueAt(row, 1).getClass() == boolean.class/* .equals("true")*/)
            {
                if (row % 2 == 0) 
                cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.01, (float)0.99));
                else
                cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.09, (float)0.95));
            }    
            else {
                if (row % 2 == 0) 
                cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.3, (float)0.99));
                else
                cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.7, (float)0.95));
            }
        } // if (!isSelected) {
        boolean glvs  = ((EOC_GrafTable) table).getbLivingCells();
        bLivingCells=glvs;
        String s = ((EOC_GrafTable) table).getRendererMode();
        Object newVal = null;
        if (!s.equals("<none>"))
            newVal = new SimpleDateFormat(s).format(Calendar.getInstance().getTime());
        else
            newVal = "";
      
        if ((!table.getModel().getValueAt(row, column).equals(newVal))
                | (bLivingCells)) {
            if (value.getClass() == Boolean.class) {
            }
            else  {
//                String s = ((EOC_GrafTable) table).getRendererMode();
                 //newVal =//String.valueOf(column);
                if (bLivingCells) {
                    //krn.OutPrintln(bLivingCells);
//                 String timeStamp = 
//                         new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
  //                       new SimpleDateFormat(s).format(Calendar.getInstance().getTime());
                 
                }
        if (!s.equals("<none>"))
            newVal = new SimpleDateFormat(s).format(Calendar.getInstance().getTime());
        else
            newVal = "";
            table.getModel().setValueAt(newVal, row, column);
            setText((String)newVal);
            }
        } // if (!table.getModel().getValueAt(row, column).equals(newVal)) {
        /*
        */
        //cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.05, (float)0.95));
        //setText((String)value);
        /*
        if (!isSelected)
            cellComponent.setBackground(Color.getHSBColor((float) 0.4,(float)0.1, (float)0.95));
        if (isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }
        */
       //cellComponent.repaint();
       return cellComponent;
    }
}
