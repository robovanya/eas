/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc;

/**
 *
 * @author rvanya
 */
public interface IEOC_rowViewerSource {
    public void viewRowViewer(String txnLevel, String txnType);    
    public void hideRowViewer();    
    public Object getRowViewer();
}
