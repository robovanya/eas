/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package system.ireport;

import java.util.HashMap;
import java.util.Map;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class IReport implements eoc.IEOC_IReport {

    HashMap<String,Object>  hmParams     = new HashMap<>();
    HashMap<String,String>  hmLabels     = new HashMap<>();
    HashMap<String,Boolean> hmEditStatus = new HashMap<>();
    int iEditableParameters = 0;
    
    String sTitle;
    public IReport(String title) {
        setTitle(title);
    }
    
    @Override
    public HashMap getParameterMap() {
        return hmParams;
    }

    @Override
    public String getReportName() {
        return FnEaS.sFullObjPath(this);
    }

    @Override
    public void addParameter(String pLabel, String pName, Object oValue, boolean editable) {
        hmParams.put(pName, oValue);
        hmLabels.put(pName, pLabel);
        hmEditStatus.put(pName, editable);
        if (editable) iEditableParameters ++;
    }

    @Override
    public void setTitle(String title) {
        sTitle = (title!=null?title:getReportName());
    }

    @Override
    public String getTitle() {
        return sTitle;        
    }

    @Override
    public Integer getEditableParameterCount() {
        return iEditableParameters;
    }

    @Override
    public HashMap getLabelMap() {
        return hmLabels;
    }

    @Override
    public HashMap getEditStatusMap() {
        return hmEditStatus;
    }

    @Override
    public void displayHashMap() {
        System.out.println("\nListing hmParams HashMap:\n================================================================\n");
        for (Map.Entry<String, Object> entry : hmParams.entrySet()) {
             String key = entry.getKey();
             Object value = entry.getValue();
             System.out.println(key + " == " + value.toString());
        }
        System.out.println("\n\n");
    }
    
}
