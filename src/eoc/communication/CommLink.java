/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.communication;
import eoc.*;
/**
 * Struktura evidujuca jedno spojenie medzi dvoma EOC objektami
 * (EOC = Easy Object Communication
 * @author rvanya
 */
public class CommLink {
    public Object oOwnerAppl;    // Aplikacia, ktora vytvorila link-objekt
    public Object oVSource;       // riadiaci objekt spojenia
    public Object oVTarget;       // cielovy objekt spojenia
    public String sEOC_Link;         // nazov spojenia (ROW,UPDATE,NAVIGATE,FILTER,...)
    public String sLinkState;    // momentalny stav spojenia (ENABLED,DISABLED,...)
   public CommLink(Object oVCntr, Object oVSrc, Object oVTrg,
                   String sLink, String sState) {
     oOwnerAppl = oVCntr;
     oVSource    = oVSrc;
     oVTarget    = oVTrg;
     sEOC_Link  = sLink;
     sLinkState = sState;
////     krn.OutPrintln("EOC_Link==>" + sEOC_Link + "\n" + oVSource + "\n" + oVTarget);
  }

}
