/*
    Copyright 2010-2011 David Fritz, Brian Gordon, Wira Mulia

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

package plptool.gui.frames;

import plptool.Config;
import plptool.gui.ProjectDriver;
import plptool.Msg;

/**
 *
 * @author wira
 */
public class OptionsFrame extends javax.swing.JFrame {

    private ProjectDriver plp = null;

    public OptionsFrame(ProjectDriver plp) {
        this.plp = plp;
        plptool.PLPToolbox.attachHideOnEscapeListener(this);
        initComponents();
        this.setLocationRelativeTo(null);
        cmbFontSize.removeAllItems();
        cmbFontSize.addItem(8);
        cmbFontSize.addItem(10);
        cmbFontSize.addItem(11);
        cmbFontSize.addItem(12);
        cmbFontSize.addItem(14);
        cmbFontSize.addItem(16);
        cmbFontSize.addItem(24);
        cmbFontSize.addItem(36);
        cmbFontSize.addItem(48);
        cmbFontSize.addItem(72);
        cmbFontSize.setSelectedIndex(3);

        prgMaxChunkSize.setText("" + Config.prgMaxChunkSize);
        prgProgramInChunks.setSelected(Config.prgProgramInChunks);
        prgReadTimeout.setText("" + Config.prgReadTimeout);

        reloadConfig();
    }

    public javax.swing.JTabbedPane getTabs() {
        return tabsOptions;
    }

    public void restoreSavedOpts() {
        sSimSpeed.setValue(Config.simRunnerDelay);
    }

    private void apply() {
        update();
        Config.simAllowExecutionOfArbitraryMem = simNoExecute.isSelected();
        Config.simBusReturnsZeroForUninitRegs = simBusReadDefaultZero.isSelected();
        Config.simDumpTraceOnFailedEvaluation = simDumpTraceOnFailedEval.isSelected();
        if(Config.devSyntaxHighlighting = editorSyntaxHighlighting.isSelected())
            plp.g_dev.changeFormatting();

        Config.simRunnerDelay = sSimSpeed.getValue();
        Config.simRefreshDevDuringSimRun = simRefreshDev.isSelected();
        Config.prgProgramInChunks = prgProgramInChunks.isSelected();
        Config.simHighlightLine = simHighlightLine.isSelected();
        Config.simFunctional = simFunctional.isSelected();

        if(cmbFontSize.getItemCount() > 0) {
            Config.devFontSize = (Integer) cmbFontSize.getItemAt(cmbFontSize.getSelectedIndex());
            plp.g_dev.changeFormatting();
            plp.refreshProjectView(false);
        }

        if(plp.g_simctrl != null)
            plp.g_simctrl.updateSlider();

        plp.updateComponents(true);

        try {
            int chunkSize = Integer.parseInt(prgMaxChunkSize.getText());
            if(chunkSize % 4 != 0) {
                prgMaxChunkSize.setText("" + Config.prgMaxChunkSize);
                return;
            }

            Config.prgMaxChunkSize = chunkSize;
        } catch(Exception e) {
            prgMaxChunkSize.setText("" + Config.prgMaxChunkSize);
        }

        try {
            int readTimeout = Integer.parseInt(prgReadTimeout.getText());

            Config.prgReadTimeout = readTimeout;
        } catch(Exception e) {
            prgReadTimeout.setText("" + Config.prgReadTimeout);
        }
    }

    private void triggerChange() {
        btnApply.setEnabled(true);
    }

    private void update() {
        btnApply.setEnabled(false);
    }

    public void reloadConfig() {
        simNoExecute.setSelected(Config.simAllowExecutionOfArbitraryMem);
        simBusReadDefaultZero.setSelected(Config.simBusReturnsZeroForUninitRegs);
        simDumpTraceOnFailedEval.setSelected(Config.simDumpTraceOnFailedEvaluation);
        editorSyntaxHighlighting.setSelected(Config.devSyntaxHighlighting);
        sSimSpeed.setValue(Config.simRunnerDelay);
        simRefreshDev.setSelected(Config.simRefreshDevDuringSimRun);
        simHighlightLine.setSelected(Config.simHighlightLine);
        prgProgramInChunks.setSelected(Config.prgProgramInChunks);
        prgMaxChunkSize.setText("" + Config.prgMaxChunkSize);
        prgReadTimeout.setText("" + Config.prgReadTimeout);
        simFunctional.setSelected(Config.simFunctional);
        update();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabsOptions = new javax.swing.JTabbedPane();
        paneEditor = new javax.swing.JPanel();
        editorSyntaxHighlighting = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        cmbFontSize = new javax.swing.JComboBox();
        paneSim = new javax.swing.JPanel();
        sSimSpeed = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        simNoExecute = new javax.swing.JCheckBox();
        simBusReadDefaultZero = new javax.swing.JCheckBox();
        simDumpTraceOnFailedEval = new javax.swing.JCheckBox();
        simRefreshDev = new javax.swing.JCheckBox();
        simHighlightLine = new javax.swing.JCheckBox();
        simFunctional = new javax.swing.JCheckBox();
        paneProgrammer = new javax.swing.JPanel();
        prgProgramInChunks = new javax.swing.JCheckBox();
        lblMaxChunkSize = new javax.swing.JLabel();
        prgMaxChunkSize = new javax.swing.JTextField();
        lblReadTimeout = new javax.swing.JLabel();
        prgReadTimeout = new javax.swing.JTextField();
        lblPrgWarning = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(plptool.gui.PLPToolApp.class).getContext().getResourceMap(OptionsFrame.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        tabsOptions.setName("tabsOptions"); // NOI18N

        paneEditor.setName("paneEditor"); // NOI18N

        editorSyntaxHighlighting.setSelected(true);
        editorSyntaxHighlighting.setText(resourceMap.getString("editorSyntaxHighlighting.text")); // NOI18N
        editorSyntaxHighlighting.setName("editorSyntaxHighlighting"); // NOI18N
        editorSyntaxHighlighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editorSyntaxHighlightingActionPerformed(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        cmbFontSize.setModel(new javax.swing.DefaultComboBoxModel());
        cmbFontSize.setName("cmbFontSize"); // NOI18N
        cmbFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFontSizeActionPerformed(evt);
            }
        });
        cmbFontSize.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cmbFontSizePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout paneEditorLayout = new javax.swing.GroupLayout(paneEditor);
        paneEditor.setLayout(paneEditorLayout);
        paneEditorLayout.setHorizontalGroup(
            paneEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editorSyntaxHighlighting)
                    .addGroup(paneEditorLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFontSize, 0, 394, Short.MAX_VALUE)))
                .addContainerGap())
        );
        paneEditorLayout.setVerticalGroup(
            paneEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorSyntaxHighlighting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(paneEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(347, Short.MAX_VALUE))
        );

        tabsOptions.addTab(resourceMap.getString("paneEditor.TabConstraints.tabTitle"), paneEditor); // NOI18N

        paneSim.setName("paneSim"); // NOI18N

        sSimSpeed.setMajorTickSpacing(100);
        sSimSpeed.setMaximum(1000);
        sSimSpeed.setMinorTickSpacing(50);
        sSimSpeed.setPaintLabels(true);
        sSimSpeed.setPaintTicks(true);
        sSimSpeed.setValue(100);
        sSimSpeed.setName("sSimSpeed"); // NOI18N
        sSimSpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sSimSpeedStateChanged(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        simNoExecute.setSelected(true);
        simNoExecute.setText(resourceMap.getString("simNoExecute.text")); // NOI18N
        simNoExecute.setName("simNoExecute"); // NOI18N
        simNoExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simNoExecuteActionPerformed(evt);
            }
        });

        simBusReadDefaultZero.setSelected(true);
        simBusReadDefaultZero.setText(resourceMap.getString("simBusReadDefaultZero.text")); // NOI18N
        simBusReadDefaultZero.setName("simBusReadDefaultZero"); // NOI18N
        simBusReadDefaultZero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simBusReadDefaultZeroActionPerformed(evt);
            }
        });

        simDumpTraceOnFailedEval.setText(resourceMap.getString("simDumpTraceOnFailedEval.text")); // NOI18N
        simDumpTraceOnFailedEval.setName("simDumpTraceOnFailedEval"); // NOI18N
        simDumpTraceOnFailedEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simDumpTraceOnFailedEvalActionPerformed(evt);
            }
        });

        simRefreshDev.setText(resourceMap.getString("simRefreshDev.text")); // NOI18N
        simRefreshDev.setName("simRefreshDev"); // NOI18N
        simRefreshDev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simRefreshDevActionPerformed(evt);
            }
        });

        simHighlightLine.setSelected(true);
        simHighlightLine.setText(resourceMap.getString("simHighlightLine.text")); // NOI18N
        simHighlightLine.setName("simHighlightLine"); // NOI18N
        simHighlightLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simHighlightLineActionPerformed(evt);
            }
        });

        simFunctional.setText(resourceMap.getString("simFunctional.text")); // NOI18N
        simFunctional.setName("simFunctional"); // NOI18N
        simFunctional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simFunctionalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paneSimLayout = new javax.swing.GroupLayout(paneSim);
        paneSim.setLayout(paneSimLayout);
        paneSimLayout.setHorizontalGroup(
            paneSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneSimLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(simHighlightLine)
                    .addComponent(simDumpTraceOnFailedEval)
                    .addComponent(simNoExecute)
                    .addComponent(sSimSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(simBusReadDefaultZero)
                    .addComponent(simRefreshDev)
                    .addComponent(simFunctional))
                .addContainerGap())
        );
        paneSimLayout.setVerticalGroup(
            paneSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneSimLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sSimSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(simNoExecute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(simBusReadDefaultZero)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(simDumpTraceOnFailedEval)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(simRefreshDev)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(simHighlightLine)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(simFunctional)
                .addContainerGap(158, Short.MAX_VALUE))
        );

        tabsOptions.addTab(resourceMap.getString("paneSim.TabConstraints.tabTitle"), paneSim); // NOI18N

        paneProgrammer.setName("paneProgrammer"); // NOI18N

        prgProgramInChunks.setSelected(true);
        prgProgramInChunks.setText(resourceMap.getString("prgProgramInChunks.text")); // NOI18N
        prgProgramInChunks.setName("prgProgramInChunks"); // NOI18N
        prgProgramInChunks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prgProgramInChunksActionPerformed(evt);
            }
        });

        lblMaxChunkSize.setText(resourceMap.getString("lblMaxChunkSize.text")); // NOI18N
        lblMaxChunkSize.setName("lblMaxChunkSize"); // NOI18N

        prgMaxChunkSize.setText(resourceMap.getString("prgMaxChunkSize.text")); // NOI18N
        prgMaxChunkSize.setName("prgMaxChunkSize"); // NOI18N
        prgMaxChunkSize.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                prgMaxChunkSizeKeyPressed(evt);
            }
        });

        lblReadTimeout.setText(resourceMap.getString("lblReadTimeout.text")); // NOI18N
        lblReadTimeout.setName("lblReadTimeout"); // NOI18N

        prgReadTimeout.setText(resourceMap.getString("prgReadTimeout.text")); // NOI18N
        prgReadTimeout.setName("prgReadTimeout"); // NOI18N
        prgReadTimeout.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                prgReadTimeoutKeyPressed(evt);
            }
        });

        lblPrgWarning.setText(resourceMap.getString("lblPrgWarning.text")); // NOI18N
        lblPrgWarning.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblPrgWarning.setName("lblPrgWarning"); // NOI18N

        javax.swing.GroupLayout paneProgrammerLayout = new javax.swing.GroupLayout(paneProgrammer);
        paneProgrammer.setLayout(paneProgrammerLayout);
        paneProgrammerLayout.setHorizontalGroup(
            paneProgrammerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneProgrammerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneProgrammerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPrgWarning)
                    .addComponent(prgProgramInChunks)
                    .addGroup(paneProgrammerLayout.createSequentialGroup()
                        .addGroup(paneProgrammerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblReadTimeout)
                            .addComponent(lblMaxChunkSize))
                        .addGap(18, 18, 18)
                        .addGroup(paneProgrammerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(prgMaxChunkSize, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                            .addComponent(prgReadTimeout, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))))
                .addContainerGap())
        );
        paneProgrammerLayout.setVerticalGroup(
            paneProgrammerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneProgrammerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(prgProgramInChunks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(paneProgrammerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMaxChunkSize)
                    .addComponent(prgMaxChunkSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(paneProgrammerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReadTimeout)
                    .addComponent(prgReadTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblPrgWarning)
                .addContainerGap(286, Short.MAX_VALUE))
        );

        tabsOptions.addTab(resourceMap.getString("paneProgrammer.TabConstraints.tabTitle"), paneProgrammer); // NOI18N

        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnApply.setText(resourceMap.getString("btnApply.text")); // NOI18N
        btnApply.setEnabled(false);
        btnApply.setName("btnApply"); // NOI18N
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabsOptions, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnApply, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnApply))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sSimSpeedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sSimSpeedStateChanged
        triggerChange();
    }//GEN-LAST:event_sSimSpeedStateChanged

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void simNoExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simNoExecuteActionPerformed
        triggerChange();
    }//GEN-LAST:event_simNoExecuteActionPerformed

    private void simBusReadDefaultZeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simBusReadDefaultZeroActionPerformed
        triggerChange();
    }//GEN-LAST:event_simBusReadDefaultZeroActionPerformed

    private void simDumpTraceOnFailedEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simDumpTraceOnFailedEvalActionPerformed
        triggerChange();
    }//GEN-LAST:event_simDumpTraceOnFailedEvalActionPerformed

    private void editorSyntaxHighlightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editorSyntaxHighlightingActionPerformed
        triggerChange();

    }//GEN-LAST:event_editorSyntaxHighlightingActionPerformed

    private void cmbFontSizePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cmbFontSizePropertyChange
        
    }//GEN-LAST:event_cmbFontSizePropertyChange

    private void cmbFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFontSizeActionPerformed
        triggerChange();
    }//GEN-LAST:event_cmbFontSizeActionPerformed

    private void simRefreshDevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simRefreshDevActionPerformed
        triggerChange();
    }//GEN-LAST:event_simRefreshDevActionPerformed

    private void prgProgramInChunksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prgProgramInChunksActionPerformed
        triggerChange();
    }//GEN-LAST:event_prgProgramInChunksActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        apply();
    }//GEN-LAST:event_btnApplyActionPerformed

    private void prgMaxChunkSizeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_prgMaxChunkSizeKeyPressed
        triggerChange();
    }//GEN-LAST:event_prgMaxChunkSizeKeyPressed

    private void prgReadTimeoutKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_prgReadTimeoutKeyPressed
        triggerChange();
    }//GEN-LAST:event_prgReadTimeoutKeyPressed

    private void simHighlightLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simHighlightLineActionPerformed
        triggerChange();
    }//GEN-LAST:event_simHighlightLineActionPerformed

    private void simFunctionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simFunctionalActionPerformed
        triggerChange();
        if(plp.isSimulating())
            Msg.W("The simulation may need to be restarted.", null);
    }//GEN-LAST:event_simFunctionalActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnClose;
    private javax.swing.JComboBox cmbFontSize;
    private javax.swing.JCheckBox editorSyntaxHighlighting;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblMaxChunkSize;
    private javax.swing.JLabel lblPrgWarning;
    private javax.swing.JLabel lblReadTimeout;
    private javax.swing.JPanel paneEditor;
    private javax.swing.JPanel paneProgrammer;
    private javax.swing.JPanel paneSim;
    private javax.swing.JTextField prgMaxChunkSize;
    private javax.swing.JCheckBox prgProgramInChunks;
    private javax.swing.JTextField prgReadTimeout;
    private javax.swing.JSlider sSimSpeed;
    private javax.swing.JCheckBox simBusReadDefaultZero;
    private javax.swing.JCheckBox simDumpTraceOnFailedEval;
    private javax.swing.JCheckBox simFunctional;
    private javax.swing.JCheckBox simHighlightLine;
    private javax.swing.JCheckBox simNoExecute;
    private javax.swing.JCheckBox simRefreshDev;
    private javax.swing.JTabbedPane tabsOptions;
    // End of variables declaration//GEN-END:variables

}
