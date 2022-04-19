/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.messages;

import eoc.database.DBconnection;
import eoc.dbdata.DBtable_buffer;
import java.sql.Connection;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Eoc_message extends DBtable_buffer {

    public Eoc_message(Kernel kr, DBconnection cn, String tblName, String IDname) {
        super(kr, cn, tblName, IDname, false);
    }

    
}
