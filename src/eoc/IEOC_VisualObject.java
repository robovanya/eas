/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc;
import java.sql.Connection;
import system.Kernel;
import java.awt.event.FocusListener;
import java.util.Map;
import system.FnEaS;
/**
 *
 * @author rvanya
 */
public interface IEOC_VisualObject extends eoc.IEOC_Object {
   public void setParentContainer(eoc.IEOC_VisualObject cntnr);
   public eoc.IEOC_VisualObject getParentContainer();
   public boolean isContainer();
   public Map<String, String> saveFrameToDef();
   public void restoreFrameFromDef(Object oPanel, Map<String, String> defMap);
   public FocusListener getMyFocusListener();
}
