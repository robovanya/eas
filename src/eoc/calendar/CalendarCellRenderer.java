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
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
 
public class CalendarCellRenderer extends DefaultTableCellRenderer 
{
//private static final long serialVersionUID = 6703872492730589499L;
    Pnl_calendarTable tblContainer;
    boolean bVisibleNullValues /*bLivingCells*/ = false;
    String displayedValueType = "";
    
    CalendarCellRenderer(Pnl_calendarTable obj) {
        super();
        tblContainer = obj;
    }
    public Component getTableCellRendererComponent(JTable table, 
            Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {

        // Ziskanie objektu v skumanej bunke(column) riadku(row) tabulky
        Component cellComponent = 
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
////        System.out.println("COMPCLSS: " + value.getClass().getSimpleName());
        if (bVisibleNullValues != tblContainer.bVisibleNullValues) {
            bVisibleNullValues = tblContainer.bVisibleNullValues;
        }
        if (displayedValueType != tblContainer.displayedValueType) {
            displayedValueType = tblContainer.displayedValueType;
        }
        String valueClassName = value.getClass().getSimpleName();
        String displayedVal = "";
        Double dbl = 0.0;
        Integer rowCnt = 0;
        String tooltipTxt = "";
        if (valueClassName.equals("CalendarCellTblModel")) {
            CalendarCellTblModel cmdl = (CalendarCellTblModel) value; 
            rowCnt = cmdl.getRowCount();
            displayedVal = String.valueOf(rowCnt);
                if (rowCnt > 0) {
                  tooltipTxt = "<html>";
                  for (int r = 0; r < rowCnt; r++) { 
                      CalendarEvent cev = (CalendarEvent) cmdl.getValueAt(r, 2);
                      if (tblContainer.bOwnerGroupMode)
                      tooltipTxt = tooltipTxt + "<B>" + cev.getOwnerName() + ":  </B>"; 
                          
                      tooltipTxt = tooltipTxt 
                              + cev.getProperty("c_nazov_ulohy") + " -> " 
                              + cev.getProperty("c_miesto_konania") + " -> " 
                              + cev.getProperty("c_popis_ucelu");
                      if (r < (rowCnt - 1)) tooltipTxt = tooltipTxt + "<BR><BR>";
                      if (displayedValueType.equalsIgnoreCase("Text") && r == 0)
                              displayedVal = cev.getProperty("c_nazov_ulohy") + " -> " 
                              + cev.getProperty("c_miesto_konania") + " -> " 
                              + cev.getProperty("c_popis_ucelu");
                  }
                  tooltipTxt = tooltipTxt + "</html>";
            }
            ((JLabel) cellComponent).setToolTipText(tooltipTxt);
               if (displayedVal.equals("0") && (!bVisibleNullValues)) displayedVal = "";
        ((JLabel) cellComponent).setText(displayedVal);
        
        }
        /*
        else {
            String newVl = (String) value;
            newVl = newVl.replace("X", "");
            if (tblContainer.isSelectedRow(row)) {
                newVl = "X-" + newVl;
            }
//            else {
//                value = 
//            };
            ((JLabel) cellComponent).setText(newVl);
        };
    */    
        if (rowCnt > 0) dbl = 0.1 * rowCnt;
        // ked bunka nie je oznacena, vyfarbi sa podla toho,
        // ci je na parnom alebo neparnom riadku
        if (!isSelected) {
            if (column == 0) {
// celkom OK    cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.4, (float)0.99));
                Integer[] iselRows = tblContainer.selCols;
                if (iselRows == null) {
                    iselRows = new Integer[1];
                    iselRows[0] = -1;
                }
                cellComponent.setBackground(tblContainer.colHeadBtnColor);
                if (tblContainer.isSelectedRow(row) /*Arrays.asList(iselRows).contains(row)*/) 
//                if (Arrays.asList(iselRows).contains(row)) 
                    cellComponent.setBackground(tblContainer.getMyTable().getSelectionBackground());

                ((JLabel) cellComponent).setHorizontalAlignment(SwingConstants.CENTER);
                ((JLabel) cellComponent).setVerticalAlignment(SwingConstants.CENTER);
                 Font f = ((JLabel) cellComponent).getFont();
                 // bold
                 ((JLabel) cellComponent).setFont(f.deriveFont(f.getStyle() | Font.BOLD));
                
                return cellComponent;
            }
            if (table.getValueAt(row, 1).getClass() == boolean.class/* .equals("true")*/)
            {
                if (row % 2 == 0) 
                cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.01, (float)0.99));
                else
                cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.09, (float)0.95));
            }    
            else {
                if (row % 2 == 0) 
                cellComponent.setBackground(Color.getHSBColor((float) (0.2 + dbl),(float)0.3, (float)0.99));
                else
                cellComponent.setBackground(Color.getHSBColor((float) (0.2 + dbl),(float)0.5, (float)0.99));
            }
        } // if (!isSelected) {
       return cellComponent;
    }
}
