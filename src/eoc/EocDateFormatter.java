/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author rvanya
 */
public class EocDateFormatter extends javax.swing.text.DateFormatter {
   
    public EocDateFormatter() {
        super();
        super.setValueClass(Date.class);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setLenient(false);
//        final javax.swing.text.DateFormatter dateFmtr = new javax.swing.text.DateFormatter(df);
//        dateFmtr.setValueClass(Date.class);
        super.setFormat(df);
        super.setAllowsInvalid(false);
//                     dfmtFact = new DefaultFormatterFactory(dateFmtr);
//                     dfmtFact.setDefaultFormatter(dateFmtr);
//                     dfmtFact.setEditFormatter(dateFmtr);
//                     this.setFormatterFactory(dfmtFact);
//                     dateFmtr.install(this);
//                     this.setFormatter(dateFmtr);
//        dateFmtr.setOverwriteMode(false);
       // super.setOverwriteMode(false);
        
    }
    
}
