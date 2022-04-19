/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.util.Calendar;
import system.FnEaS;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class code_tester {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        code();
        // TODO code application logic here
    }
    
    public static void code() {
         //Calendar cD = Calendar.getInstance();
//         Calendar c = FnEaS.getObjectAsCalendar("15-02-2017");
//        String sCal = "2017.05.24-12:56:19";
        /*
        String sCal = "25.05.2017";
        
        Calendar c = FnEaS.getObjectAsCalendar(sCal);
         
         
        //System.out.println("CalD=" + FnEaS.calToStr(cD, "yyyy.MM.dd-HH:mm:ss"));
        System.out.println("Cal=" + FnEaS.calToStr(c, "yyyy.MM.dd-HH:mm:ss"));
        */
        /*
                String s1 = Kernel.cd("4g4w00a");
                String s2 = Kernel.cd("peklo");
                String s3 = Kernel.cd("mzahoranska");
        Kernel.staticMsg("CD:\n" + s1 + "\n" + s2 + "\n" + s3);
        s1 = Kernel.dc(s1);
        s2 = Kernel.dc(s2);
        s3 = Kernel.dc(s3);
        Kernel.staticMsg("DC:\n" + s1 + "\n" + s2 + "\n" + s3);
        */

//            System.out.println(FnEaS.intToStr(7, 2));
/*
        String s = "UBF_NZBD.ESU.report.Rpt_ESU_doklad";
        //String[] ss = s.split("\\.");
//            System.out.println(s.contains(".") + " " + ss.length);
        String ss = s.replace(".","/");
        String sss = FnEaS.sFullObjPath(s);
        System.out.println(ss + "\n" + sss);
        */
        /*
        String s = "KOVERRRTT:1,endDayOfWeek=1,endTime=3600000,endTimeMode=2]],firstDayOfWeek=2,minimalDaysInFirstWeek=4,ERA=1,YEAR=2017,MONTH=6,WEEK_OF_YEAR=27,WEEK_OF_MONTH=1,DAY_OF_MONTH=7,DAY_OF_YEAR=188,DAY_OF_WEEK=6,DAY_OF_WEEK_IN_MONTH=1,AM_PM=0,HOUR=10,HOUR_OF_DAY=10,MINUTE=22,SECOND=2,MILLISECOND=621,ZONE_OFFSET=3600000,DST_OFFSET=3600000].1,startDayOfWeek=1,startTime=3600000,startTimeMode=2,endMode=2,endMonth=9,endDay=.java.util.GregorianCalendar[time=1499415722621,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id=\"Europe/Prague\",offset=3600000,dstSavings=3600000,useDaylight=true,transitions=141,lastRule=java.util.SimpleTimeZone[id=Europe/Prague,offset=3600000,dstSavings=3600000,useDaylight=true,startYear=0,startMode=2,startMonth=2,startDay=";
        String dt = FnEaS.crazyDateRead(s);
        System.out.println("crazyDate: " + dt);
        */
        String s = "########.##";
        System.out.println(s.indexOf(".") + " . " + (s.length() - s.indexOf(".")));
    }
}
