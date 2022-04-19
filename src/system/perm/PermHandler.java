/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package system.perm;

import eoc.EOC_message;
import eoc.IEOC_Object;
import system.FnEaS;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class PermHandler {
    Kernel krn; 
    private String usrPerms;
    private String objPerms;
    private String perms; // sucet pristupovych prav uzivatela+objektu.
                          // udrzuje sa z setUsrPerms() a setObjPerms()
                          // aby ich nebolo treba opakovane scitavat
    private IEOC_Object voParent;

    public PermHandler(Kernel krnl, IEOC_Object prnt) {
        krn         = krnl;
        voParent    = prnt;
        // zacina sa full-pravami, default-ne
        usrPerms    = "NUCDP"; // "NOKT-V"
        objPerms    = "NUDCP";
        perms       = "NUDPC";
    }
    
    public void setObjPerms(EOC_message msg) {
        if (msg.isOwner(voParent)) return;
        objPerms = msg.getParameters();
        countPerms();
//        if (msg.getOthers().contains("TRANSMIT")) {
           //EOC_message msg = new EOC_message(voParent,"permissionChanged", perms, "true");
           krn.krn_sendMessage(voParent,msg,"*","ALL");
//        }
    }

    public void setUsrPerms(EOC_message msg) {
        usrPerms = msg.getParameters();
        countPerms();
        if (msg.getOthers().contains("TRANSMIT")) {
           //EOC_message msg = new EOC_message(voParent,"permissionChanged", perms, "true");
           krn.krn_sendMessage(voParent,msg,"*","ALL");
        }
    }

   public void countPerms() {
 ////      System.out.println(">>> COUNTERMS--PObject-" + FnEaS.sObjName(myObjectID)+ "-COUNTING:" + objPerms + " + " + usrPerms 
 ////              + "  -> CURRENTPERM:" + perms);
       perms = "";
       if (objPerms.contains("N") && usrPerms.contains("N")) {
          perms = perms + "N";
       }
       if (objPerms.contains("U") && usrPerms.contains("U")) {
          perms = perms + "U";
       }
       if (objPerms.contains("C") && usrPerms.contains("N")) {
          perms = perms + "C";
       }
       if (objPerms.contains("D") && usrPerms.contains("D")) {
          perms = perms + "D";
       }
       if (objPerms.contains("P") && usrPerms.contains("P")) {
          perms = perms + "P";
       }
       ////System.out.println(FnEaS.sObjName(myObjectID) + " => PObject.countPerms()-PERMCOUNT(OBJ+USR):" + objPerms + "+" + usrPerms + "==" + perms);
    }

    public String getObjPerms() {
        return objPerms;
    }

    public String getUsrPerms() {
        return usrPerms;
    }
    
    public String getPerms() {
        return perms;
    }
/*
    public String getJoinedPerms(boolean bTransmit) {
        return (objPerms==null?"":objPerms) + "#" + (usrPerms==null?"":usrPerms)
                 + "#" + (bTransmit?"true":"false");
    }
*/

    /*
    @Override
    public boolean disjoinPerms(String sPerms) {
        String a = "";
        a = FnEaS.sEntry(1, sPerms, "#");
        objPerms = a;
        if (FnEaS.iNumEntries(sPerms, "#") > 1) {
            a = FnEaS.sEntry(2, sPerms, "#");
            usrPerms = a;
        }
        if (FnEaS.iNumEntries(sPerms, "#") > 2) {
            a = FnEaS.sEntry(3, sPerms, "#");
        }    
        return (a.toLowerCase()=="true");
    }
    
    */
}
