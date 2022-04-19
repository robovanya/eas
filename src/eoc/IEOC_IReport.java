/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package eoc;

import java.util.HashMap;

/**
 *
 * @author rvanya
 */
public interface IEOC_IReport {
    public HashMap getParameterMap();
    public String getReportName();
    public void addParameter(String pLabel, String pName, Object oValue, boolean editable);
    public void setTitle(String title);
    public String getTitle();
    public Integer getEditableParameterCount();
    public HashMap getLabelMap();
    public HashMap getEditStatusMap();
    public void displayHashMap();
}
