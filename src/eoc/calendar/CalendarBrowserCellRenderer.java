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
 
import eoc.TimeFormatter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import system.FnEaS;
 
public class CalendarBrowserCellRenderer extends DefaultTableCellRenderer 
{
//private static final long serialVersionUID = 6703872492730589499L;
    Pnl_calendarTable tblContainer;
    boolean bVisibleNullValues /*bLivingCells*/ = false;
    Class displayedValueClass;
    String displayedPropertyName;
    
    CalendarBrowserCellRenderer(Pnl_calendarTable obj, String propertyName) {
        super();
        tblContainer = obj;
        displayedPropertyName = propertyName;
    }
    
    public Component getTableCellRendererComponent(JTable table, 
            Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        // Ziskanie objektu v skumanej bunke(column) riadku(row) tabulky
        Component cellComponent = 
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Object oVal = tblContainer.myTable.getValueAt(row, 0);
        CalendarEvent myCalendarEvent = (CalendarEvent) oVal;         

//        String valueClassName = value.getClass().getSimpleName();
        String displayedVal = "";
        Double dbl = 0.0;
        Calendar calCurrentToday = tblContainer.calCurrentToday;

////        sem ide ziskanie hodnoty podla propertyName ktora moze popisat aj zlozeny udaj
        TimeFormatter tf = new TimeFormatter();
        switch (displayedPropertyName) {
            case "Osoba":
//                displayedVal = FnEaS.calToStr(calCurrentToday,"dd.MM.yyyy");
                displayedVal = myCalendarEvent.getOwnerName();
                break;    
            case "Názov":
                //displayedVal = myCalendarEvent.getProperty("c_popis_ucelu").toString();
                displayedVal = myCalendarEvent.getProperty("c_nazov_ulohy").toString();
                break;    
            case "Začiatok":
                Calendar cod = (Calendar) myCalendarEvent.getProperty("d_od_date");
                String   tod = myCalendarEvent.getProperty("c_od_time").toString();
                displayedVal = FnEaS.calToStr(cod,"dd.MM.yyyy") + "  " + tf.valueToString(tod);
                break;        
            case "Splniť do":
                Calendar cdo = (Calendar) myCalendarEvent.getProperty("d_do_date");
                String   tdo = myCalendarEvent.getProperty("c_do_time").toString();
                displayedVal = FnEaS.calToStr(cdo,"dd.MM.yyyy") + "  " + tf.valueToString(tdo);
                break;        
            case "Miesto konania":
                displayedVal = myCalendarEvent.getProperty("c_miesto_konania").toString();
                break;    
            case "Termín o":
                displayedVal = (String) myCalendarEvent.getProperty(displayedPropertyName);
                break;    
            default:
                displayedVal = "NEZNÁME:" + displayedPropertyName;
                break;    
        }

////            displayedVal = myCalendarEvent.getProperty(displayedPropertyName).toString();
        System.out.println("displayedVal==>" + displayedVal);
        ((JLabel) cellComponent).setText(displayedVal);
        if (!isSelected) {
            if (column == 0) {
                Integer[] iselRows = tblContainer.selCols;
                if (iselRows == null) {
                    iselRows = new Integer[1];
                    iselRows[0] = -1;
                }
                cellComponent.setBackground(tblContainer.colHeadBtnColor);
                if (tblContainer.isSelectedRow(row)) 
                    cellComponent.setBackground(tblContainer.getMyTable().getSelectionBackground());

                ((JLabel) cellComponent).setHorizontalAlignment(SwingConstants.CENTER);
                ((JLabel) cellComponent).setVerticalAlignment(SwingConstants.CENTER);
                 Font f = ((JLabel) cellComponent).getFont();
                 // bold
                 ((JLabel) cellComponent).setFont(f.deriveFont(f.getStyle() | Font.BOLD));
                
                return cellComponent;
            }
            /*if (table.getValueAt(row, 1).getClass() == boolean.class)
            {
                if (row % 2 == 0) 
                cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.01, (float)0.99));
                else
                cellComponent.setBackground(Color.getHSBColor((float) 0.2,(float)0.09, (float)0.95));
            }    
            else { */
                if (row % 2 == 0) 
                cellComponent.setBackground(Color.getHSBColor((float) (0.2 + dbl),(float)0.3, (float)0.99));
                else
                cellComponent.setBackground(Color.getHSBColor((float) (0.2 + dbl),(float)0.5, (float)0.99));
           // }
        } // if (!isSelected) {
/*        
*/
       return cellComponent;
    }
}
