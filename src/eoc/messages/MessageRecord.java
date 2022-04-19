/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.messages;

/**
 * Obsahuje udaje spravy v DBtable_buffer + Pnl_message-objekt, ktory spravu zobrazuje
 * 
 * @author rvanya
 */
public class MessageRecord {
    
        public final Pnl_message panel;   // obrazovka pre message
        public final Eoc_message message; // obsah message (DBtable_buffer-extended)
        
        public MessageRecord (Eoc_message msg, Pnl_message pnl) {
             message = msg;
             panel = pnl;
        }
    
}
