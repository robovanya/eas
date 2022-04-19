/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc;
/**
 *
 * @author rvanya
 */
public interface IEOC_viewer extends IEOC_VisualObject{
    public String setMasterTable(String sMstTblName);
    public String setMasterKey(String sMstKeyName);
    public String displayRow(); 
    public String rowValuesChanged();
    public String rowIdentificatorChanged(String sNewTableId);
    public String goToRow(EOC_message eocMsg);
    public String getQueryBase();
    public String setQueryBase(String QueryBase);
    public String getAppWhere();
    public String setAppWhere(String AppWhere);
    String rebuildQuery();
    public String OpenQuery();
    public String rebuildAndOpenQuery();
    public String closeQuery();
    public String addrow(EOC_message eocMsg);
    public String copyrow(EOC_message eocMsg);
    public String updaterow(EOC_message eocMsg);
    public String deleterow(EOC_message eocMsg);
    public String committxn(EOC_message eocMsg);
    public String rollbacktxn(EOC_message eocMsg);
    public String enableWidgets(); 
    public String disableWidgets(); 
    public String clearWidgets(); 
    public String aftercommit(EOC_message eocMsg);
    public String refresh();

}
        
