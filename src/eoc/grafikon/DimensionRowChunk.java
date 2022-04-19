/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.grafikon;

import javax.swing.JTable;

/**
 *
 * @author rvanya
 * 
 * indexy vybratych stlpcov riadkou tabuly s cislom riadku 
 * a s hodnotou primarneho kluca 
 */
public class DimensionRowChunk {
    EOC_GrafTable    myTable;
    private Integer  rowIndex;
    private Object   masterKeyValue;
    private int[]    selectedColumnIndexes;
    private Object[] selectedColumnValues;
    
    public DimensionRowChunk(EOC_GrafTable tbl, int rwIdx, Object mkVal, int[] selColIdxs) {
        //QQQ sem by patril test na null hodnoty, nemali by existovat
        myTable               = tbl;
        rowIndex              = rwIdx;
        masterKeyValue        = mkVal;
        selectedColumnIndexes = selColIdxs;
        selectedColumnValues  = new Object[selectedColumnIndexes.length];
        for (int i = 0; i < selectedColumnValues.length; i++) {
            selectedColumnValues[i] = myTable.getValueAt(rowIndex, selectedColumnIndexes[i]);
        }
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public Object getMasterKeyValue() {
        return masterKeyValue;
    }

    public int[] getSelectedColumnIndexes() {
        return selectedColumnIndexes;
    }

}
