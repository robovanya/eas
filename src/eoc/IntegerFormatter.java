/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc;

import eoc.widgets.DTfield;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author rvanya
 */
public class IntegerFormatter extends NumberFormatter {
    NumberFormat integerFormat;

    public IntegerFormatter() {
        super();
        integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setMaximumFractionDigits(0);
        integerFormat.setMaximumIntegerDigits(7);
        this.setFormat(integerFormat);
    }
    
    // This method reads the text from model class to view class
    
    @Override
    public void install(JFormattedTextField ftf) {
        
        int numColumns = 7;
        if (ftf instanceof DTfield) {
            numColumns = ((DTfield)ftf).getDBtableField().dbColumnInfo.length;
            integerFormat.setMaximumIntegerDigits(numColumns);
            integerFormat.setMaximumFractionDigits(0);
            integerFormat.setMinimumFractionDigits(0);
            this.setFormat(integerFormat);
        }
        super.install(ftf); //To change body of generated methods, choose Tools | Templates.
        ftf.setColumns(numColumns);

    }
    
    @Override
    public void setAllowsInvalid(boolean allowsInvalid) {
        super.setAllowsInvalid(false); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String valueToString(Object arg0) {
        String s = "";
        if (arg0==null) s = "0";
        else s = arg0.toString().trim();
        ////System.out.println("INTEGER_convertedd_valueToString===" + s);
        return s;
        // return super.valueToString(arg0);
    }
    
    // This method reads the text from view class to modal class and does the validations
    @Override
    public Object stringToValue(String arg0) {
        ////System.out.println("INTEGERstringToValue===" + arg0);
        String s;
        if (arg0==null) s = "0";
        else s = arg0.trim();
        ////System.out.println("INTEGERstringToValueParsing===" + s);
     ////   try {
        Integer i = Integer.parseInt(s);
     ////   }
    ////    catch (Exception e) {
    ////        this.
   ////     }
        return i;
    }    
}
