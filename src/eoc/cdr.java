/*
 *  cdr.java
 * 
 * Maly, velmi rychly kodovaci-dekodovaci programcok :-)
 *
 * 2016-12-20  -  Robo Vanya
 * 
 **********************************************************************/
package eoc;
import java.io.File;
    import java.util.Arrays;
import java.util.Map;
    public class cdr {   
        static String[] sc = {"M","0","L","2","C","4","D","5","X","6"};
        public static void main(String args[]) {   
                String cdd = cd("martina");
                System.out.println("Codeded " + cdd);
                System.out.println(getComputerName());
                File parentDirectory = new File(System.getProperty("user.dir"));
        System.out.println(parentDirectory.getAbsoluteFile().getParentFile().getAbsolutePath());

                System.exit(0);   
        }   
        
        public static String cd (String s) {
            String v = "";
            String thestr = "";
             Double dRnd = (Math.random() * 94) + 33;
             Integer iRnd = dRnd.intValue();
           for(int i = 0; i < s.length(); i++) {
                 char c = s.charAt(i);
                 int ic = ((int) c) * (i%2==0?2:3);
                 String sic = String.format("%03d", ic);
                 thestr = thestr +  sic;
            }
            thestr = iRnd + thestr;
            thestr = cdx(thestr.trim());
            return thestr;
        }

        private static String cdx (String s) {
            String v = "";
            String thestr = "";
            for(int i = 0; i < s.length(); i++) {
                 String c = s.substring(i,i+1);
                 Integer ic = Integer.parseInt(c);
                 thestr = thestr + sc[ic];
            }
            return thestr;         
        }
        
        public static String dc (String s) {
            String thestr = "";
            Integer idx;
            String ci;
            String c = "";
            ci = dcx(s.trim());
            for(int i = 0; i < ci.length(); i++) {
                 c = c + ci.substring(i,i+1);
                 if (c.length() == 3) {
                     int iac = (int) Integer.parseInt(c) / ((i/3)%2==0?2:3); 
                     c = "";
                     thestr = thestr + (char) iac;
                 }
            }
            return thestr;
        }
        
        public static String dcx (String s) {
            String thestr = "";
            Integer idx;
            String ci;
            String c;
            Integer cut = s.length()%3;
            if (cut == 0) cut = 3; // kriticky moment, ktory som takmer prehliadol !
            s = s.substring(cut);
            for(int i = 0; i < s.length(); i++) {
                 c = s.substring(i,i+1);
                 idx = Arrays.asList(sc).indexOf(c);
                 thestr = thestr + idx;
            }
            return thestr;
        }
    static private String getComputerName()
{
     Map<String, String> env = System.getenv();
    if (env.containsKey("COMPUTERNAME"))
        return env.get("COMPUTERNAME");
    else if (env.containsKey("HOSTNAME"))
        return env.get("HOSTNAME");
    else
        return "Unknown Computer";
}
    }   