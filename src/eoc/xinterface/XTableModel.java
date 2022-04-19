/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.xinterface;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rvanya
 */
public class XTableModel extends DefaultTableModel {
   
    private boolean bTableEditable = false;
    private DefaultTableModel myObjectID = this;
    
    @Override
    public boolean isCellEditable(int row, int column) {
       //all cells false
       return bTableEditable;
    }

    public void setTableEditable (boolean bVal) {
       bTableEditable = bVal;
    }
    
}
