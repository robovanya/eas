/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.widgets;

import eoc.database.DBconnection;
import system.Kernel;
import java.awt.Component;
import java.sql.Connection;
import javax.swing.JTabbedPane;

/**
 *
 * @author rvanya
 */
public class TBPObject extends eoc.widgets.PObject  implements eoc.IEOC_VisualObject {
    public  JTabbedPane      myTBP;

    public TBPObject() {
      //  UIManager.put("ToolTip.background", new ColorUIResource(255, 247, 200)); //#fff7c8
   }
   
    public void setMyTBP(JTabbedPane tbp) {
        myTBP = tbp;
    }

    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        String initialize = super.initialize(kr, cX);
        setMyObjectID(this);
        setEOC_objectType("EOC_TBPObject");

        if (!initialize.equals("")) { return initialize; } 
        //krn.OutPrintln("myTBP.getComponentCount()==" + myTBP.getComponentCount());
        Component[] c = myTBP.getComponents();
        Class cl;
        String s;
        for (Object o : c) {
            cl = o.getClass();
            s = cl.getSimpleName();
            ////krn.OutPrintln("TAB_COMPPO init: " + s /* + " CL:" + cl.getName()
            ////+ "  CANNONCL:" + cl.getCanonicalName() */);
            eoc.widgets.PObject eo = (eoc.widgets.PObject) o;
            eo.initialize(kr, cX);
            
        }
        // ked sa neselctuje 1 a potom 0-ty tab, pri kliknuti na browseri 
        // na tabe 0 sa objavia widgety z tabu 1, co momentalne nechcen riesit
        // toto je taky 'work-around' alebo 'z nudze cnost'
        myTBP.setSelectedIndex(1);
        myTBP.setSelectedIndex(0);
        myTBP.revalidate();
        
        return initialize;
    }

    @Override
    public String afterInitialize() {
        Component[] c = myTBP.getComponents();
        Class cl;
        String s;
        for (Object o : c) {
            cl = o.getClass();
            s = cl.getSimpleName();
            eoc.widgets.PObject eo = (eoc.widgets.PObject) o;
            eo.afterInitialize();
        }
        String afterInitialize =  super.afterInitialize(); 
        return afterInitialize;
    }


    
        
    
}
