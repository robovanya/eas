/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

import java.util.Arrays;

/**
 *
 * @author rvanya
 */
public class CalendarOwner {
//            "SELECT id_eas_calAccount,c_accountOwnerType,c_owner_table," +
//            "id_c_owner_table,c_owner_name,c_owner_pozn FROM eas_calAccount",
private int id_eas_calAccount;    
private String accountOwnerType;    
private String ownerTable;    
private int id_ownerTable;    
private String ownerName;    
private String ownerPozn;
private Object[] aAccount = new Object[6];
    public CalendarOwner (Object[] account_row) {
        /* account_row ma obsahovat jeden riadok z dotazu:
         * "SELECT id_eas_calAccount,c_accountOwnerType,c_owner_table," +
         * "id_c_owner_table,c_owner_name,c_owner_pozn FROM eas_calAccount"
         */
        setVariables(account_row);
    }
    
    private void setVariables(Object[] account_row) {
        setId_eas_calAccount(Integer.parseInt(account_row[0].toString()));
        setAccountOwnerType(account_row[1].toString());
        setOwnerTable(account_row[2].toString());
        setId_ownerTable(Integer.parseInt(account_row[3].toString()));
        setOwnerName(account_row[4].toString());
        setOwnerPozn(account_row[5].toString());
        aAccount = account_row;
    }
    
    public Object[] getAccountrow () {
        return aAccount;
    }
    
    public int getId_eas_calAccount() {
        return id_eas_calAccount;
    }

    public void setId_eas_calAccount(int id_eas_calAccount) {
        this.id_eas_calAccount = id_eas_calAccount;
        aAccount[0] = id_eas_calAccount;
    }

    public String getAccountOwnerType() {
        return accountOwnerType;
    }

    public void setAccountOwnerType(String accountOwnerType) {
        this.accountOwnerType = accountOwnerType;
        aAccount[1] = accountOwnerType; 
        }

    public String getOwnerTable() {
        return ownerTable;
    }

    public void setOwnerTable(String ownerTable) {
        this.ownerTable = ownerTable;
        aAccount[2] = ownerTable; 
    }

    public int getId_ownerTable() {
        return id_ownerTable;
    }

    public void setId_ownerTable(int id_ownerTable) {
        this.id_ownerTable = id_ownerTable;
        aAccount[3] = id_ownerTable; 
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        aAccount[4] = ownerName; 
    }

    public String getOwnerPozn() {
        return ownerPozn;
    }

    public void setOwnerPozn(String ownerPozn) {
        this.ownerPozn = ownerPozn;
        aAccount[5] = ownerPozn; 
    }

    public String toString() {
       return Arrays.deepToString(aAccount);
    }

}
