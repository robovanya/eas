/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package linuxWdog;

import eoc.readers.PingPongFileReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Wdog {
    PingPongFileReader ppRdr;
    Method mtd;
    File fIn;
    boolean bScanIsActive = false;
    Calendar cld = Calendar.getInstance();
    Wdog wd;
    public void initialize() {
        wd = this;
        try {
            ppRdr = new PingPongFileReader();
        } catch (IOException ex) {
            Logger.getLogger(Wdog.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            mtd = this.getClass().getMethod("readLine", new Class[] {String.class});
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Wdog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Wdog.class.getName()).log(Level.SEVERE, null, ex);
        }
        mtd.setAccessible(true);
       setFile("c:/etc/mail/pocuvame06.log");
       startScanning();
    }

    public void readLine(String line) {
            System.out.println("RL: " + line); 
    }
    
    private void setFile(String fileName) {
       fIn = new File(fileName);
    }
    
    public void startScanning() {
        bScanIsActive = true;
       System.out.println("fisssNULL" + (fIn==null));
        ppRdr.readFileByMethod_csv(wd, fIn, mtd);
        //while (bScanIsActive) {
        // }
    }

    public void stopScanning() {
        bScanIsActive = false;
    }
}
