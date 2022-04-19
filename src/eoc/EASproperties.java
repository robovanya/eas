/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc;

import system.FnEaS;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author rvanya
 */
public class EASproperties extends Properties {
    
    Boolean    bLocked            = false;
    public String[]   allowedProperties  = null;
    public String[]   allowedValues      = null;
    
    public void lock(String[] alwProp) {
        allowedProperties = alwProp;
        allowedValues     = null;
        bLocked = true;
    }
    
    public void lock(String[] alwProp, String[] alwVals) {
        allowedProperties = alwProp;
        allowedValues     = alwVals;
        bLocked = true;
    }
    
    public void unlock() {
        bLocked = false;
    }
    
    @Override
    public String getProperty(String key, String defaultValue) {
        return super.getProperty(key, defaultValue);
    }
    
    @Override
    public String getProperty(String key) {
        return super.getProperty(key);
    }
    
    public String getProperty(String key,String prefix,String suffix) {
        String rtv = super.getProperty(key);
        if (!rtv.equals("")) {
            rtv = prefix  + rtv + suffix;
        }
        return rtv;
    }
    
    @Override
    public Object setProperty(String name,String value) {
        name = name.toLowerCase();
        
        // test existencie vlastnosti, pokial je objekt uzamknuty
        if (bLocked) {
            boolean propertyExists = false;
            locate_property:
            for (int i = 0; i < allowedProperties.length; i++) {
               if (allowedProperties[i].toLowerCase().equals(name)) {
                   propertyExists =  true;
                   break locate_property;
               }  // if (allowedProperties[i] ...
            } // for (int i ...
            if (!propertyExists) {
                System.out.println("Property '" + name + " not allowed.\n\n" +
                       "Allowed properties: " + FnEaS.sArrayToString(allowedValues));
                return null;
            }
        } // if (bLocked) ...
        return super.setProperty(name, value);
    }
    
    @Override
    public void clear() {
        Enumeration e = this.keys();
        while (e.hasMoreElements()) {
        String s = (String) e.nextElement();
        if (!(s==null)) {
         //krn.OutPrintln("Clearing -> Element: " + s);
         this.setProperty(s,"");
        }
        }
    }

    
    
}
