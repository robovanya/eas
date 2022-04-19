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
    import java.net.Socket;   
    import java.util.Calendar;
    import com.jcraft.jsch.*;
import java.util.logging.Level;
    public class RaTaTaTa_portFinder {   
        public static void main(String args[]) {   
            Socket sock = null;
//            String ipBase = "188.215.41.247";   
//            String ipBase = "192.168.0.";   
//            String ipBase = "82.119.116.";   
            String ipBase = "192.168.1.111";    // win10
            String ipFull = null;   
            //while (true) {
           try { 
                for (int ip = 2; ip < 3; ip++) {
                   ipFull = ipBase; // + ip;
                   System.out.println("IP: " + ipFull);
                   if (sock != null) sock.close();
                   System.out.print("portscan: ");
                   for (int portnum = 1; portnum < 65536; portnum++) {
                       //System.out.println("port: " + ipFull + ":" + portnum);
//                       System.out.println(ipFull + ":" + portnum);
                       /*
                       sock = new Socket(ipFull,portnum);
                       PrintWriter wr = new PrintWriter(new     
                            OutputStreamWriter(sock.getOutputStream()),true);
//                        if (wr. != null) 
                        wr.println("blablablaaa :-))");    
                        */
        if (srvListening(ipFull,portnum)) {
             System.out.println();
             System.out.println("port: " + ipFull + ":" + portnum + "  OK");
             System.out.print("portscan ..: ");
        }
        else
//             System.out.print("port: " + ipFull + ":" + portnum + "  none");
             System.out.print(portnum + "..");
                        
                   }
                }
/*
                wr.println("blablablaaa :-))");    
//                 wr.println("test na sebe (sa) :-))");    
                 wr.flush();    
                 numShots++;
                 BufferedReader br =
                    new BufferedReader(new  InputStreamReader(s1.getInputStream()));     
      
                 srv = br.readLine();    
      
                // pokus o spustenie listovacieho script-u na vzdialenom serveri
                Process p = Runtime.getRuntime().exec("ls");   
      
                 BufferedReader stdInput = new BufferedReader(new    
                     InputStreamReader(p.getInputStream()));   
      
      
                BufferedReader stdError = new BufferedReader(new    
                     InputStreamReader(p.getErrorStream()));   
      
                // citanie navratovej hodnoti spustaneho script-u
                System.out.println("Standardny vystup prikazu:\n");   
                while ((s = stdInput.readLine()) != null) {   
                    System.out.println(s);   
                }   
      
                // citanie moznych standardnych chyb pri spustani prikazu   
                System.out.println("Standardne chyby pocas vykonavania prikazu (ked boli):\n");   
                while ((s = stdError.readLine()) != null) {   
                    System.out.println(s);   
                }     
      
                System.exit(0);   
      
            }   
      
            catch (IOException e) {   
                System.out.println(numShots + "-tý výstrel na " 
                          + srv + " priemerný počet konnektov za sekundu: " + priemer 
                          + "\nDľžka útoku: " + numSecs + " sekúnd.");   
               // e.printStackTrace();   
                //System.exit(-1);   
            }   
  */    
            }   catch (IOException ex) {
//                 System.out.println(ipFull + ":" + portnum);
                    java.util.logging.Logger.getLogger(RaTaTaTa_portFinder.class.getName()).log(Level.SEVERE, null, ex);
                }
        }   
 public static boolean srvListening(String host, int port)
{
    Socket s = null;
    try {
        s = new Socket(host, port);
        return true;
    }
    catch (Exception e) { return false; }
    finally {
        if(s != null)
            try {s.close();} catch(Exception e){}
    }
}


    }   