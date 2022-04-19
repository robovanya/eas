/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package system.modul;

import javax.swing.JMenu;
import system.perm.PermDefinition;

/**
 *
 * @author rvanya
 */
public class Menu extends JMenu 
       implements system.perm.IPermSource {

    private boolean bPermUpdating = false;
    private PermDefinition superPermDef;
    private PermDefinition userPermDef;

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
   
}
