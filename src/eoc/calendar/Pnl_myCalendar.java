/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

import eoc.database.DBconnection;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_myCalendar extends Pnl_calendarContainer {
/*
    public Pnl_myCalendar() {
    }
*/
    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        super.initialize(kr, cX); //To change body of generated methods, choose Tools | Templates.
        boolean success = freezeForOwner(krn.getPermd().getCurrentUser());
//        krn.Message("STARTINGCALENDARFOR:" + krn.getPermd().getCurrentUser()
//        + (success?" OK":" FAILED"));
        return "";
    }
    
 
}
