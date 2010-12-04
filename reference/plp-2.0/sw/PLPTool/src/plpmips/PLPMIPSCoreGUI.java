/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PLPMIPSCoreGUI.java
 *
 * Created on Dec 4, 2010, 12:12:09 AM
 */

package plpmips;

/**
 *
 * @author wira
 */
public class PLPMIPSCoreGUI extends javax.swing.JInternalFrame {

    private PLPMIPSSim sim;

    /** Creates new form PLPMIPSCoreGUI */
    public PLPMIPSCoreGUI(PLPMIPSSim sim) {
        this.sim = sim;
        initComponents();
        updateComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        step = new javax.swing.JButton();
        reset = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        PC = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        nextInstr = new javax.swing.JTextField();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(plp.PLPToolApp.class).getContext().getResourceMap(PLPMIPSCoreGUI.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        step.setText(resourceMap.getString("step.text")); // NOI18N
        step.setName("step"); // NOI18N
        step.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepActionPerformed(evt);
            }
        });

        reset.setText(resourceMap.getString("reset.text")); // NOI18N
        reset.setName("reset"); // NOI18N
        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        PC.setEditable(false);
        PC.setText(resourceMap.getString("PC.text")); // NOI18N
        PC.setName("PC"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        nextInstr.setEditable(false);
        nextInstr.setText(resourceMap.getString("nextInstr.text")); // NOI18N
        nextInstr.setName("nextInstr"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reset)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PC, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextInstr, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(step)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(399, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reset)
                    .addComponent(step)
                    .addComponent(jLabel1)
                    .addComponent(PC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(nextInstr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepActionPerformed
        sim.step();
        updateComponents();
    }//GEN-LAST:event_stepActionPerformed

    private void resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetActionPerformed
        sim.reset();
        sim.step();
        updateComponents();
    }//GEN-LAST:event_resetActionPerformed

    public void updateComponents() {
        PC.setText(String.format("0x%08x", sim.pc.eval()));
        nextInstr.setText(MIPSInstr.format(sim.id_stage.i_instruction));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField PC;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField nextInstr;
    private javax.swing.JButton reset;
    private javax.swing.JButton step;
    // End of variables declaration//GEN-END:variables

}