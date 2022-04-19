/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author rvanya
 */
public class ColumnHeaderRenderer extends JLabel implements TableCellRenderer {

public ColumnHeaderRenderer(JTable table) {
    JTableHeader header = table.getTableHeader();
    setOpaque(true);
    setBorder(BorderFactory.createEtchedBorder());
    setHorizontalAlignment(CENTER);
    setForeground(header.getForeground());
    setBackground(header.getBackground());
    setFont(header.getFont());
    setPreferredSize(new Dimension(0, 25));

}

public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

    Color bg = UIManager.getColor("TableHeader.background");
    int selectedColumn = table.getSelectedColumn();
    if (selectedColumn == column){
        bg = new Color(107, 142, 35);
        setFont(getFont().deriveFont(Font.BOLD));// !!!!trying to do it here!!!!
    } else {
        setFont(UIManager.getFont("TableHeader.font"));
    }
    setBackground(bg);
    setText(value.toString());
    return this;
}

}
