/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.messages;

/**
 *
 * @author rvanya
 */
public class StandardMessage extends javax.swing.JPanel {

    /**
     * Creates new form EOC_StandardMessage
     */
    public StandardMessage() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    
    public void setMsg(String txt) {
        cb_StandardMessage.addItem((String) txt);
        cb_StandardMessage.setSelectedItem(txt);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cb_StandardMessage = new javax.swing.JComboBox<String>();

        setBackground(new java.awt.Color(255, 51, 153));

        cb_StandardMessage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cb_StandardMessage, 0, 319, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cb_StandardMessage)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cb_StandardMessage;
    // End of variables declaration//GEN-END:variables
}