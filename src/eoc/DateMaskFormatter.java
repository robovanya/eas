/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc;

/**
 *
 * Standardne formatter-y robia blbosti so slovenskym datumom
 * s formatom dd.MM.yyyy, tak som sa rozhodol naprogramovat
 * vlastny formatter - 2016-12-08
 * 
 * @author rvanya
 */
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import system.FnEaS;
import system.Kernel;
public class DateMaskFormatter extends MaskFormatter {
 // staticke aj dynamicke honoty  delimiteru a placeholder-a
 // treba menit spolu !!!    
char dtDelimiter = '.'; // deliaci znak  
char placeholder = '_'; // prazdny znak  
String dtDelimiterStr = "."; // deliaci znak  
String placeholderStr = "_"; // prazdny znak  
static String dtDelimiterString = "."; // deliaci znak  
static String placeholderString = "_"; // prazdny znak  
Kernel krn;
    public DateMaskFormatter(Kernel kr) {
        super();
        krn = kr;
        try {
            this.setMask("##" + dtDelimiter + "##" + dtDelimiter + "####");
            this.setPlaceholderCharacter(placeholder);
        } catch (ParseException ex) {
            Logger.getLogger(DateMaskFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getDelimiterString() {
        return dtDelimiterString;
    }
    
    public static String getPlaceholderString() {
        return placeholderString;
    }
    
    public static boolean isEmptyDate(String dtStr) {
        
        if (dtStr == null) return true;
        
        dtStr = dtStr.replace(dtDelimiterString, "");
        dtStr = dtStr.replace(placeholderString, "").trim();
        
        return (dtStr.length()==0);
    }

    public String getDelimiterStr() {
        return dtDelimiterStr;
    }
    
    public String getPlaceholderStr() {
        return placeholderStr;
    }
    
    public char getDelimiter() {
        return dtDelimiter;
    }
    
    // This method reads the text from model class to view class
    @Override
    public void install(JFormattedTextField ftf) {
        super.install(ftf); //To change body of generated methods, choose Tools | Templates.
        ftf.setColumns(10);
////        ftf.setEnabled(true);
////        ftf.setEditable(true);
    }
    
    @Override
    public void setAllowsInvalid(boolean allowsInvalid) {
        super.setAllowsInvalid(false); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String valueToString(Object arg0) {
        if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! DATEMASKFORRMATER_valueToStringBEGINN=== " + arg0);
        ////System.out.println("valuTuStringg_KOVERRRTT:" + arg0);
        //if (arg0 == null) return "  " + dtDelimiter + "  " + dtDelimiter + "    ";
        if (arg0 == null) return "__" + dtDelimiter + "__" + dtDelimiter + "____";
        if (!arg0.toString().contains("-")) {
        arg0 = convertToDate(arg0.toString());
        }
        String s;
        if (arg0==null || arg0.equals("    -  -  ")) s = "__" + dtDelimiter + "__" + dtDelimiter + "____";
        else {
            /* PO STAROM
            Date dt = (Date) arg0;
            Integer i;
            i = dt.getDate();
            String dd = (i < 10)? "0" + i.toString():i.toString();
            i = dt.getMonth() + 1;
            String MM = (i < 10)? "0" + i.toString():i.toString();
            i = dt.getYear() + 1900;
            String yyyy = (i < 999)? "0" + i.toString():i.toString();
            s = dd + ":" + MM + ":" + yyyy;
            */
            // PO NOVOM
            s = arg0.toString();
            String[] dt;
            /* old
            if (s.length() > 0)
               dt = s.split("-");
            else {
               dt = new String[3];
               dt[0] = "_____";
               dt[1] = "__";
               dt[2] = "__";
            }   
            */
            if (s.length() > 0) {
                dt = s.split("-");
               // System.out.println("DTTTT:" + s + " >>> " + dt[0] + ".." + dt[1] + ".." + dt[2]);
                if (dt[0].trim().equals("99999")) {
                    dt[0] = "_____";
                    dt[1] = "__";
                    dt[2] = "__";
                }
                else {
                    if (dt[1].length() == 1) dt[1] = "0" + dt[1];
                    if (dt[2].length() == 1) dt[2] = "0" + dt[2];
                }
            }  
            else {
               dt = new String[3];
               dt[0] = "_____";
               dt[1] = "__";
               dt[2] = "__";
            }   
            
            if (dt[1].length() == 1) dt[1] = "0" + dt[1];
            if (dt[2].length() == 1) dt[2] = "0" + dt[2];
            
            s = dt[2] + dtDelimiter + dt[1]  + dtDelimiter +  dt[0];
        }
        ////System.out.println("TIME_convertedd_valueToString===" + s);
        if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! DATEMASKFORRMATER_valueToString=== " + arg0 + " retVal: " + s);
       //// System.out.println("valuTuStringg_KOVERRRTT_return:" + s);
        return s;
        // return super.valueToString(arg0);
    }
    
    // This method reads the text from view class to modal class and does the validations
    @Override
    public Object stringToValue(String arg0) throws ParseException {
       if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! DATEMASKFORRMATER_stringToValue=== " + arg0);
        //Date dt;
        String dt;
        ////System.out.println("StriiiToValu_KOVERRRTT:" + arg0);
        if (arg0.contains("DAY_OF_WEEK_IN_MONTH=")) arg0 = FnEaS.crazyDateRead(arg0,".");
        dt = convertToDate(arg0);
        return dt;
    }
    
    String /*Date*/ convertToDate(String arg0) {
        String dd;
        String MM;
        String yyyy;
     ////   System.out.println("FORMATTINGDTE-------------------------------:" + arg0);
        if (krn != null) krn.debugOut(this,5,"\nTO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! convertToDate_converting:" + arg0 + " CLASSSS:" + arg0.getClass().getSimpleName());

        if (arg0.equals("  .  .    ") || arg0.equals("  " + dtDelimiter + "  " + dtDelimiter + "    ")  
                || arg0.equals("__" + dtDelimiter + "__" + dtDelimiter + "____")) {
            if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! FIND_EMPTY");
            return null;
        }
        
        if (arg0==null | arg0.trim().equals("")) { 
            if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! FIND_NULL");
            return null;
//            dd = "00";
//            MM = "00";
//            yyyy = "0000";
        }
        else if (arg0.contains("-")) {
            if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! FIND_-");
           String[] hm;
                hm = arg0.split("\\-");
                yyyy = hm[0];
                MM = hm[1];
                dd = hm[2];
        }
        else if (arg0.contains(".")) {
            if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! FIND_DOT");
           String[] hm;
                hm = arg0.split("\\.");
                dd = hm[0];
                MM = hm[1];
                yyyy = hm[2];
        }
        else if (arg0.contains(dtDelimiterStr) && (!arg0.contains("CET"))) {
            if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! FIND_DOUBLEDOT");
           String[] hm;
                hm = arg0.split(dtDelimiterStr);
                dd = hm[0];
                MM = hm[1];
                yyyy = hm[2];
        }
        else {
           String[] hm;
                //arg0 = arg0.replace(" ","&"); // sialene, ale budizz
            if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! FIND_SPACE IN:" + arg0);
                hm = arg0.split("\\s+");
                yyyy = hm[5];
//                MM = hm[2];
                String months = "JanFebMarAprMajJunJulAugSepOktNovDec";
                if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! FINDINGMONTH: " + hm[1] + " ISSS: " + (months.indexOf(hm[1]) + 3));
                Integer i = (months.indexOf(hm[1]) + 3) / 3;
                //MM fv= "12"; // maly podfuk namiesto Dec
                MM = i.toString();
                dd = hm[2];
                }
        
        if (yyyy.startsWith("____")) yyyy = "99999";
        if (MM.equals("__"))     MM   = "9";
        if (dd.equals("__"))     dd   = "9";
        
        Integer yy = Integer.parseInt(yyyy);
        Integer M = Integer.parseInt(MM);
        MM = M.toString();
        yyyy = yy.toString();
        if (Integer.parseInt(yyyy) > 4000) yyyy = "4000";
        if (Integer.parseInt(MM) > 12) MM = "12";
        if (Integer.parseInt(dd) > 31) dd = "31";
        if (yyyy.length() <4) yyyy = "0" + yyyy;
        if (MM.length() < 2) MM = "0" + MM;
        if (dd.length() < 2) dd = "0" + dd;
        //Date dt;
//        dt = new Date(Integer.parseInt(yyyy),Integer.parseInt(MM),Integer.parseInt(dd));
        String dt;
        dt = yyyy + "-" + MM + "-" + dd; 
    ///// System.out.println("FORMATTINGDTE-RET---------------------------:" + dt);

        if (krn != null) krn.debugOut(this,5,"TO TO VYHLADAT - krn DOSTUPE CASOM Z DateMeskFormatter !!! NEWDATE_FROM_STRING: " + dt.toString() + "\n");
        return dt;
    }
    /*
    public void refreshForEnabled(JTextField tf) {
        String st = placeholderStr + placeholderStr + "." + placeholderStr + placeholderStr + "." 
                  + placeholderStr + placeholderStr +placeholderStr + placeholderStr;
        tf.setText(st);
    }
    */
}
