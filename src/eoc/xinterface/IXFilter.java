/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.xinterface;

/**
 *
 * @author rvanya
 */
public interface IXFilter {

    /**
     *
     * 
     * @return 
     */
    public String buildFilter();
    public void buildAndSendFilter();
    public void setActive(boolean bActive);
}
