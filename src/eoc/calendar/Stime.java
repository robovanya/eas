/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class Stime {
    public String     sTime = null;
    private Integer[] iTime = new Integer[3]; // hour,min,sec
    
    public Stime(String s) {
        sTime = s.trim();
        iTime = new Integer[] {0,0,0};
        if (sTime == null || sTime.equals("") ) sTime = "0:0:0";
        else {
            int numentr = FnEaS.iNumEntries(sTime,":");
            if (numentr < 2) sTime = sTime + ":0:0"; // prida sa :min:sec
            else if (numentr < 3) sTime = sTime + ":0"; // prida sa :sec
            String[] ss = sTime.split(":");
            iTime[0] = Integer.parseInt(ss[0]);
            iTime[1] = Integer.parseInt(ss[1]);
            iTime[2] = Integer.parseInt(ss[2]);
        }
    }
    
    public Integer getHour() {
        return iTime[0];
    }
    
    public Integer getMin() {
        return iTime[1];
    }
    
    public Integer getSec() {
        return iTime[2];
    }
    
}
