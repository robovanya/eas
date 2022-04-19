/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system.modul;

import system.perm.PermDefinition;

/**
 *
 * @author rvanya
 */
public class MenuItem extends javax.swing.JMenuItem 
       implements system.perm.IPermSource {
   private String  progClass; // spustany program
   private boolean bMultiple; // viackrat spustitelny
   private boolean bPermUpdating = false;
   private PermDefinition superPermDef;
   private PermDefinition userPermDef;
   
   public MenuItem(String lbl, String prgCls, boolean bTyp) {
      this.setText(lbl);
      progClass = prgCls;
      bMultiple = bTyp;
   }
   public String getprogClass () {
      return progClass;
   }
   public boolean getbMultiple () {
      return bMultiple;
   }

    @Override
    public PermDefinition getSuperPermDefinition() {
        return superPermDef;
    }

    @Override
    public void setSuperPermDefinition(PermDefinition permDf) {
        superPermDef = permDf;
    }

    @Override
    public PermDefinition getUserPermDefinition() {
        return userPermDef;
    }

    @Override
    public void setUserPermDefinition(PermDefinition permDf) {
        userPermDef = permDf;
    }

    public PermDefinition getPermDefinition() {
        if (userPermDef != null)
            return userPermDef;
        else
            return superPermDef;
    }
  
}
