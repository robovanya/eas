/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.grafikon;

import eoc.EASobject;

/**
 *
 * @author rvanya
 */
public interface IEOC_graf_obj {
    public void setRendering (boolean renderingState, 
                String propagateMode /* NO,UP,DOWN,ALL */,
                Object caller, boolean ignoreCaller);
}
