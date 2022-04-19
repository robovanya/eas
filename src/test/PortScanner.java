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

import java.net.*;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
    public class PortScanner {
        static byte[] ipAddr  = new byte[]{127,0,0,1};
//                 byte[] ipAddr = new byte[]{109, 105, 37,88};
//                 ipAddr = new byte[]{127,0,0,1};
         public static void main(String[] args) {
         InetAddress ia=null;
         String host=null;
             try {
             
             //host=JOptionPane.showInputDialog("Enter the Host name to scan:\n example: xxx.com");
//                 if(host!=null){
//                     if (host.matches(host))
//                 ia = InetAddress.getByName(host);
                 ia = InetAddress.getByAddress(ipAddr);             
                 
             scan(ia); 
             //}
         }
             catch (UnknownHostException e) {
             System.err.println(e );
         }
         System.exit(0);
     }
     
        public static void scan(final InetAddress remote) {
         
         
        int port=0;
//        String hostname = remote.getHostName();
        String hostname = Arrays.toString(ipAddr);
         
                for ( port = 1; port < 65536; port++) {
                 try {
                 Socket s = new Socket(remote,port);
                 System.out.println("Server is LISTENING on port " + port+ " of " + hostname);
                 s.close();
             }
                 catch (IOException ex) {
                 // The remote host is not listening on this port
                 //System.out.println("Server is not listening on port " + port+ " of " + hostname);
             }
         }
     }
}   