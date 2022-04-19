/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package eoc;

import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class EOC_message {
    private IEOC_Object eoOwner;
    private IEOC_Object eoSender;
    private String sMessage;
    private Object sParameters;
    private String sOthers;

    public EOC_message(IEOC_Object oOw, String sMsg,
                       Object sParam, String sOth) {
        eoOwner     = oOw;
        eoSender    = null;
        sMessage    = sMsg;
        sParameters = sParam;
        sOthers     = sOth;
    }
    public Object getOwner() {
        return eoOwner;
    }
    public Object getSender() {
        return eoSender;
    }
    public Object getSenderName() {
        return FnEaS.sObjName(eoSender);
    }

    public Object getOwnerName() {
        return FnEaS.sObjName(eoOwner);
    }

    public String getMessage() {
        return  sMessage;
    } 
    public String getParameters() {
        return  sParameters.toString();
    }

    public Object getParametersAsObject() {
        return  sParameters;
    }
public String getOthers() {
        return  sOthers;
    }
    public boolean involved(IEOC_Object eoObj) {
        return(eoObj == eoOwner || eoObj == eoSender);
    }
    public boolean isSender(IEOC_Object eoObj) {
        return(eoObj == eoSender);
    }
    public boolean isOwner(IEOC_Object eoObj) {
        return(eoObj == eoOwner);
    }
    public void setSender(IEOC_Object eoSndr) {
        eoSender = eoSndr;
    }

}
