package eoc.calendar.dimensions_odlozene;

import eoc.calendar.dimensions_odlozene.IDimension;
import eoc.EASobject;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import jxl.write.DateTime;

/**
 * Objekt - ulomok/segment dimenzie, ohraniceny triedami/class-mi zaciatku a konca
 *          + ukazovatelom bodu zaujmu v 2D priestore na horizontalnej osi.
 *          Ked je bAutoTmeShift zapnuty, dosiahnutie hranicnej hodnoty 
 *          na horizontalnej osi vyvola prepnutie segmentu do dalsieho ulomku
 *          kocky, inak sa ozve pipnutie, a vysle sa signal ON_TOP alebo ON_END
 * 
 * @author rvanya
 */
public class HVDimension extends EASobject implements Serializable, IDimension {
    private Class XPointerClass;
    private Class YPointerClass;
    private Object initialXPointer;
    private Object initialYPointer;
    private Object currentXPointer;
    private Object currentYPointer;
    private Object topXBoundary;
    private Object downXBoundary;
    private Object topYBoundary;
    private Object downYBoundary;
    private String[] cutterXBoundaries;
    
    private HashMap<String,Object> hmProperties;

    
    // bean-kostruktor
    public HVDimension() {
        
    }
    // kostruktor
    public HVDimension(Class xptrClass,Class yptrClass) {
        XPointerClass = xptrClass;
        YPointerClass = yptrClass;
        hmProperties = new HashMap<>();
    }
    
    @Override
    public Object getTopXBoundary() {
         return topXBoundary;    
    }    

    @Override
    public void setTopXBoundary(Object o) {
        topXBoundary = (XPointerClass.cast(o));    
    }    
    
    @Override
    public Object getDownXBoundary() {
         return downXBoundary;    
    }    

    @Override
    public void setDownXBoundary(Object o) {
        downXBoundary = (XPointerClass.cast(o));    
    }    
    
    public String getXBoundaryDescription() {
        String s = "";
        s = XPointerClass.cast(downXBoundary).toString() 
          + " - " + XPointerClass.cast(topXBoundary).toString();
        return s;
    }
    
    @Override
    public Object getTopYBoundary() {
         return topYBoundary;    
    }    

    @Override
    public void setTopYBoundary(Object o) {
        topYBoundary = (YPointerClass.cast(o));    
    }    
    
    @Override
    public Object getDownYBoundary() {
         return downYBoundary;    
    }    

    @Override
    public void setDownYBoundary(Object o) {
        downYBoundary = (YPointerClass.cast(o));    
    }    
    
    @Override
    public String getYBoundaryDescription() {
        String s = "";
        s = YPointerClass.cast(downYBoundary).toString() 
          + " - " + YPointerClass.cast(topYBoundary).toString();
        return s;
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
    public void initializeXFor(Class ptrClass, Object ptrValue) {
    XPointerClass   = XPointerClass;
    initialXPointer = XPointerClass.cast(ptrValue);
    }

    @Override
    public void initializeYFor(Class ptrClass, Object ptrValue) {
    YPointerClass   = YPointerClass;
    initialYPointer = YPointerClass.cast(ptrValue);
  }

    @Override
    public Object getInitialXPointer() {
        return initialXPointer;
    }

    @Override
    public Object getInitialYPointer() {
        return initialYPointer;
    }

    @Override
    public Object getCurrentXPointer() {
        return currentXPointer;
    }

    @Override
    public Object getCurrentYPointer() {
        return currentYPointer;
    }

    @Override
    public Object MoveXPointerUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object MoveYPointerUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object MoveXPointerDown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object MoveYPointerDown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
