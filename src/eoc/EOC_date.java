/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package eoc;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class EOC_date extends Object {
   Object valueObject;
   Integer d;
   Integer M;
   Integer y;
    public EOC_date (Calendar cal) {
        valueObject = cal;
        d = cal.get(Calendar.DAY_OF_MONTH);
        M = cal.get(Calendar.MONTH) + 1;
        y = cal.get(Calendar.YEAR);
    }
    public EOC_date (Date dt) {
        valueObject = dt;
        d = dt.getDate();
        M = dt.getMonth() + 1;
        y = dt.getYear() + 1900;
        System.out.println("EOC_date_DateClasss:" + d + "..." + M + "..." + y);
    }

    @Override
    public String toString() {
        return y + "-" + M + "-" + d;
    }
    
    public boolean setValue(String s) {
        String[] ss = {null,null,null};
        if (s.contains(".")) {
            s = s.replace(".", "-");
            System.out.println("REPLACEDSSSS: " + s);
            String[] sx = s.split("-");
            ss[0] = sx[2].trim();
            ss[1] = sx[1].trim();
            ss[2] = sx[0].trim();
        }
        else {
            String[] sx = s.split("-");
            ss[0] = sx[0].trim();
            ss[1] = sx[1].trim();
            ss[2] = sx[2].trim();
        }    
        System.out.println("valueObject.getClass()::: " + valueObject.getClass().toString()
        + "\n" + Arrays.toString(ss));
        y = (FnEaS.isNumeric(ss[0]) ? Integer.parseInt(ss[0]) : 99999);
        M = (FnEaS.isNumeric(ss[1]) ? Integer.parseInt(ss[1]) : 9);
        d = (FnEaS.isNumeric(ss[2]) ? Integer.parseInt(ss[2]) : 9);
        
        if (valueObject instanceof java.util.Calendar) {
            ((Calendar) valueObject).set(y,M,d);
        }
        if (valueObject instanceof java.util.GregorianCalendar) {
            ((GregorianCalendar) valueObject).set(y,M,d);
        }
        if (valueObject instanceof java.util.Date) {
            ((Date) valueObject).setYear(y);
            ((Date) valueObject).setMonth(M);
            ((Date) valueObject).setDate(d);
        }    
        return true;
    }
}
