/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package linuxWdog;

/**
 *
 * @author rvanya
 */
public class Wdogd {
      public static Wdog wdog;
      public static void main(String args[]) {   
      wdog = new Wdog();   
      wdog.initialize();
   }   // main
  
}
