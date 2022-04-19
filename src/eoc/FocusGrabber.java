/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc;

import javax.swing.JComponent;

/**
 *
 * @author rvanya
 */
public class FocusGrabber implements Runnable {
  private JComponent component;

  public FocusGrabber(JComponent component) {
   this.component = component;
   }
  public void run() {
   component.grabFocus();
   }
} 