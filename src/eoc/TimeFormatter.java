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
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.MaskFormatter;
public class TimeFormatter extends MaskFormatter {
    
    public TimeFormatter() {
        super();
        try {
            this.setMask("##:##");
        } catch (ParseException ex) {
            Logger.getLogger(TimeFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // This method reads the text from model class to view class
    @Override
    public void install(JFormattedTextField ftf) {
        super.install(ftf); //To change body of generated methods, choose Tools | Templates.
        ftf.setColumns(5);

    }
    
    @Override
    public void setAllowsInvalid(boolean allowsInvalid) {
        super.setAllowsInvalid(false); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String valueToString(Object arg0) {
        String s = "00:00";
        if (arg0==null) s = "00:00"; 
        else s = arg0.toString().trim();
        s = convertToTimeFormat(s);
        ////System.out.println("TIME_convertedd_valueToString===" + s);
        ////System.out.println("TIMFORRMATER: " + arg0 + " retVal: " + s);
        return s;
        // return super.valueToString(arg0);
    }
    
    // This method reads the text from view class to modal class and does the validations
    @Override
    public Object stringToValue(String arg0) throws ParseException {
      ////  System.out.println("TIMEstringToValue===" + arg0);
        String s;
        s = convertToTimeFormat(arg0);
        return s;
    }
    
    String convertToTimeFormat(String arg0) {
        String s;
        String hours;
        String mins;
       //// System.out.println("convertToTimeFormatINNG:" + arg0 + " -- " + null);
        if (arg0==null | arg0.equalsIgnoreCase("<NULL>") 
            | arg0.trim().equals("") | arg0.trim().equals(":")) { 
            hours = "00";
            mins = "00";
        }
        else {
           String[] hm;
           hm = arg0.split(":");
           /*
           s = arg0.toString().trim();
           s = s.replaceAll(":", "");
           while (s.length() < 4) {
               s = "0" + s;
           }
           System.out.println("convertToTimeFormattt:" + s );
           hours = s.substring(0, 2);
           mins  = s.substring(2);
                   */
           hours = hm[0];
           if (hm.length > 1) 
               mins  = hm[1];
           else
               mins = "00";
        }  
////        System.out.println("TIMMFORM:" + arg0 + " hours:" + hours + " mins:" + mins);
        if (Integer.parseInt(hours) > 23) hours = "23";
        if (Integer.parseInt(mins) > 59) mins = "59";
        if (hours.length() < 2) hours = "0" + hours;
        if (mins.length() < 2) mins = "0" + mins;
        s = hours + ":" + mins; 
        return s;
    }
    
}
