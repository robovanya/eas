/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import jxl.write.DateTime;

/**
 * Objekt - ulomok/segment casovej kocky, ohraniceny datumami zaciatku a konca
 *          + ukazovatelom bodu zaujmu v 2D priestore na horizontalnej osi.
 *          Ked je bAutoTimeShift zapnuty, dosiahnutie hranicnej hodnoty 
 *          na horizontalnej osi vyvola prepnutie segmentu do dalsieho ulomku
 *          kocky, inak sa ozve pipnutie, a vysle sa signal ON_TOP alebo ON_END
 * 
 * @author rvanya
 */
public class TimeChunk {
    /*
    public String ownerType;
    public Integer ownerId;
    public String ownerName;
    public String ktodza;
    */
    public Calendar dtmZaciatok;
    public Calendar dtmKoniec;
    
    public TimeChunk(Calendar zac, Calendar kon) {
        dtmZaciatok = zac;
        dtmKoniec = kon;
    }
    
    public String toString() {
        String s;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

       if (dtmZaciatok != null) 
           s = sdf.format(dtmZaciatok.getTime());
       else 
           s = "<NULL>";
       s = s + " - ";
       if (dtmKoniec != null) 
           s = s + sdf.format(dtmKoniec.getTime());
       else 
           s = s + "<NULL>";
        return s;
    }
    public Date getOdDate() {
        if (dtmZaciatok == null) return null;
        Date d = new Date();
        d = dtmZaciatok.getTime();
        return d;
    }

    public Date getDoDate() {
        if (dtmKoniec == null) return null;
        Date d = new Date();
        d = dtmKoniec.getTime();
        return d;
    }
    public String getOdTime() {
        if (dtmZaciatok == null) return null;
        String s;
        int hour = dtmZaciatok.get(Calendar.HOUR_OF_DAY);
        int minute = dtmZaciatok.get(Calendar.MINUTE);

        s = hour + ":" + minute;
        return s;
    }
    public String getDoTime() {
        if (dtmKoniec == null) return null;
        String s;
        int hour = dtmKoniec.get(Calendar.HOUR_OF_DAY);
        int minute = dtmKoniec.get(Calendar.MINUTE);

        s = hour + ":" + minute;
        return s;
    }
}
