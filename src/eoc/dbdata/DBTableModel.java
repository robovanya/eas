/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.dbdata;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rvanya
 */
public class DBTableModel extends DefaultTableModel {
   
    private boolean bTableEditable = false;
    
    @Override
    public boolean isCellEditable(int row, int column) {
       //all cells false
       return bTableEditable;
    }

    public void setTableEditable (boolean bVal) {
       bTableEditable = bVal;
    }
}
