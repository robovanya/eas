/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc;

import eoc.widgets.DTfield;
import eoc.widgets.DTtextArea;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author rvanya
 */
public class EOCFocusListener extends FocusAdapter {
    
       @Override
       public void focusGained(FocusEvent e) {
           super.focusGained(e);
//           krn.OutPrintln(e.getSource().toString() + "\n" + e.getComponent().getName());
            //displayMessage("Focus gained", e);
           Component cp = (Component) e.getSource();
           if (cp instanceof DTfield) {
              DTfield df = (DTfield) cp;
//            df.setMainFormatter();
////              System.out.println("Focus gained on: " +  df.getDbFieldName()
////                     + " formatterClass:" + df.getFormatter().getClass()) ;
           } 
           if (cp instanceof DTfield) {
               JTextField j = (JTextField) cp;
               j.setCaretPosition(0);
               //((DTfield) j).setMainFormatter();
           }

           if (cp instanceof DTtextArea) {
                JTextArea j = (JTextArea) cp;
               j.setCaretPosition(0);
           }           //throw new UnsupportedOperationException("Not supported yet.");
       }

       @Override
       public void focusLost(FocusEvent e) {
           super.focusLost(e);
           /*
           Component cp = (Component) e.getComponent();
           if (cp instanceof DTfield) {
              DTfield df = (DTfield) cp;
              df.setValue(((JFormattedTextField)cp).getText());
//              df.setMainFormatter();
           }   
           //super.focusLost(e);
           if (cp instanceof DTfield) {
 //              try {
                   DTfield dtf = (DTfield) cp;
                   String s = dtf.getText();
               try { 
                   s = dtf.getFormatter().stringToValue(s).toString();
               } catch (ParseException ex) {
                   Logger.getLogger(EOCFocusListener.class.getName()).log(Level.SEVERE, null, ex);
               }
                   System.out.println("FocuLos: text:" + dtf.getText() + " formatted:" + s );
                   dtf.putStringToValue(s);
                   // displayMessage("Caret on nulpos", e);
               }
                   */
       }
    }

