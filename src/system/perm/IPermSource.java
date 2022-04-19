/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package system.perm;

/**
 *
 * @author rvanya
 */
public interface IPermSource {
    public PermDefinition getSuperPermDefinition();
    public void setSuperPermDefinition(PermDefinition permDf);
    public PermDefinition getUserPermDefinition();
    public void setUserPermDefinition(PermDefinition permDf);
    /*
    public PermDefinition getPermDefinition();
    */
}
