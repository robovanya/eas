/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package system;

import eoc.IEOC_DB_driver;
import eoc.database.DBconnection;
import java.util.Map;

/**
 *
 * @author rvanya
 */
public interface IWelcome {
    public DBconnection[] getDBconnections();
    public Map<String, String>[] getFrameValues();
    public IEOC_DB_driver[] getDBdrivers();
}
