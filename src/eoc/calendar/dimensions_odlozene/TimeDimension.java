package eoc.calendar.dimensions_odlozene;

import eoc.calendar.dimensions_odlozene.HVDimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import jxl.write.DateTime;

/**
 * Objekt - ulomok/segment casovej kocky, ohraniceny datumami zaciatku a konca
 *          + ukazovatelom bodu zaujmu v 2D priestore na horizontalnej osi.
 *          Ked je bAutoTmeShift zapnuty, dosiahnutie hranicnej hodnoty 
 *          na horizontalnej osi vyvola prepnutie segmentu do dalsieho ulomku
 *          kocky, inak sa ozve pipnutie, a vysle sa signal ON_TOP alebo ON_END
 * 
 * @author rvanya
 */
public class TimeDimension extends HVDimension {

    public TimeDimension() {
        super();
    };
    public TimeDimension(Class xptrClass, Class yptrClass) {
        super(xptrClass, yptrClass);
    }

    
/*
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
*/


    @Override
    public void initProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getProperty(String propName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object setProperty(String propName, Object propVaue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
