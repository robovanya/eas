/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package system.ireport;

import eoc.IEOC_IReport;
import eoc.IEOC_VisualObject;
import eoc.xinterface.XDObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rvanya
 */
public class XD_ParameterEditor extends XDObject {

    IEOC_IReport myCurrentReport = null;    
    JTable myTable;
    DefaultTableModel myModel;
    HashMap<String,Object>  hmParams; //     = new HashMap<>();
    HashMap<String,String>  hmLabels; //     = new HashMap<>();
    HashMap<String,Boolean> hmEditStatus; // = new HashMap<>();
@SuppressWarnings("unchecked")
    public void initializeAs(IEOC_VisualObject parent, boolean modal, IEOC_IReport irp, JTable jtb) {
        super.initializeAs(parent, modal);
        myCurrentReport = irp;

        hmParams = myCurrentReport.getParameterMap();
        hmLabels = myCurrentReport.getLabelMap();
        myTable = jtb;
        myModel = (DefaultTableModel) myTable.getModel();
        initTablePromIReportParameters();
        myTable.setCellEditor(null);
        setTitle("Parametre zostavy: " + myCurrentReport.getTitle());

        /*        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(myModel);
        myTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(1);
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        */
     }

    @SuppressWarnings("unchecked")
    public void initTablePromIReportParameters() {
        myModel.setRowCount(0); // mazanie obsahu tabnulky
        String key;
        Object val;
        List keys = new ArrayList(hmParams.keySet());
        Collections.sort(keys);
        
        for (int i= 0; i < keys.size(); i++) {
             key = keys.get(i).toString();
             Object[] oRow = {null,null,null};
             oRow[0] = key.toString();
             oRow[1] = hmParams.get(key);
             oRow[2] = hmLabels.get(key).toString();
             System.out.println("RD_FROM_HASH: " + key + " == " +  hmParams.get(key) + " CLASSSS:" +  hmParams.get(key).getClass()
             + " >> " + oRow[1].getClass());
             myModel.addRow(oRow);
        }
    }
    
    public void savePropertyTableToHashMap() {
        Object[] row = {null,null};
        for (int i = 0; i < myTable.getRowCount(); i++){
             row[0] = myModel.getValueAt(i, 0);
             row[1] = myModel.getValueAt(i, 1);
             System.out.println("WR_TO_HASH: " + row[0] + " == " + row[1] + " >>> " + myModel.getValueAt(i, 1).getClass()
                     + " >>> " + row[1].getClass());
             hmParams.put(row[0].toString(), row[1]);
        }
    }

    
}
