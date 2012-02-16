/*
    Copyright 2012 David Fritz, Brian Gordon, Wira Mulia

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

import plptool.DynamicModuleFramework;
import plptool.PLPToolbox;
import plptool.gui.ProjectDriver;

import java.io.File;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

/**
 *
 * @author wira
 */
public class ModuleManager extends javax.swing.JDialog {

    private ProjectDriver plp;

    /** Creates new form ModuleManager */
    public ModuleManager(java.awt.Frame parent, boolean modal, ProjectDriver plp) {
        super(parent, modal);
        this.plp = plp;
        initComponents();
        updateTable();
        setLocationRelativeTo(parent);
    }

    private void updateTable() {
        File autoloadDir = new File(PLPToolbox.getConfDir() + "/autoload");
        DefaultTableModel tbl = (DefaultTableModel) tblMods.getModel();
        while(tbl.getRowCount() > 0)
            tbl.removeRow(0);
        if(autoloadDir.exists() && autoloadDir.isDirectory()) {
            File[] files = autoloadDir.listFiles();
            for(int i = 0; i < files.length; i++) {
                if(files[i].getName().endsWith(".jar")) {
                    String[] row = new String[3];
                    row[0] = files[i].getName();
                    row[1] = DynamicModuleFramework.getManifestEntry(files[i].getAbsolutePath(), "title");
                    row[2] = DynamicModuleFramework.getManifestEntry(files[i].getAbsolutePath(), "version");
                    tbl.addRow(row);
                }
            }
            tblMods.setModel(tbl);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblInfo = new javax.swing.JLabel();
        scrollerTable = new javax.swing.JScrollPane();
        tblMods = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();
        lblDownload = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        btnDownload = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(plptool.gui.PLPToolApp.class).getContext().getResourceMap(ModuleManager.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        lblInfo.setText(resourceMap.getString("lblInfo.text")); // NOI18N
        lblInfo.setName("lblInfo"); // NOI18N

        scrollerTable.setName("scrollerTable"); // NOI18N

        tblMods.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File", "Title", "Version"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMods.setName("tblMods"); // NOI18N
        scrollerTable.setViewportView(tblMods);
        tblMods.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblMods.columnModel.title0")); // NOI18N
        tblMods.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblMods.columnModel.title1")); // NOI18N
        tblMods.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblMods.columnModel.title3")); // NOI18N

        btnDelete.setText(resourceMap.getString("btnDelete.text")); // NOI18N
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnBrowse.setText(resourceMap.getString("btnBrowse.text")); // NOI18N
        btnBrowse.setName("btnBrowse"); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        lblDownload.setText(resourceMap.getString("lblDownload.text")); // NOI18N
        lblDownload.setName("lblDownload"); // NOI18N

        txtURL.setText(resourceMap.getString("txtURL.text")); // NOI18N
        txtURL.setName("txtURL"); // NOI18N

        btnDownload.setText(resourceMap.getString("btnDownload.text")); // NOI18N
        btnDownload.setName("btnDownload"); // NOI18N
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });

        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnLoad.setText(resourceMap.getString("btnLoad.text")); // NOI18N
        btnLoad.setName("btnLoad"); // NOI18N
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollerTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                    .addComponent(lblInfo, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblDownload)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtURL, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBrowse)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                                .addComponent(btnLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollerTable, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDownload)
                    .addComponent(txtURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDownload))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowse)
                    .addComponent(btnClose)
                    .addComponent(btnLoad)
                    .addComponent(btnDelete))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        final JFileChooser fc = new javax.swing.JFileChooser();
        fc.setAcceptAllFileFilterUsed(true);
        fc.setCurrentDirectory(new File(plp.curdir));

        int retVal = fc.showOpenDialog(null);

        if(retVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            if(PLPToolbox.downloadJARForAutoload("file:///" + fc.getSelectedFile().getAbsolutePath(), null, false)) {
                updateTable();
                retVal = JOptionPane.showConfirmDialog(this,
                        "Would you like to load this module?",
                        "Load Module", JOptionPane.YES_NO_OPTION);
                if(retVal == JOptionPane.YES_OPTION) {
                    String[] manifest = DynamicModuleFramework.loadJarWithManifest(fc.getSelectedFile().getAbsolutePath());
                    if(manifest != null) {
                        DynamicModuleFramework.applyManifestEntries(manifest, plp);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int index = tblMods.getSelectedRow();
        if(index > -1) {
            String path = PLPToolbox.getConfDir() + "/autoload/" + tblMods.getValueAt(index, 0);
            File mod = new File(path);
            mod.delete();
            updateTable();
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        if(PLPToolbox.downloadJARForAutoload(txtURL.getText(), plp, false)) {
            updateTable();
            int retVal = JOptionPane.showConfirmDialog(this,
                            "Would you like to load this module?",
                            "Load Module", JOptionPane.YES_NO_OPTION);
            if(retVal == JOptionPane.YES_OPTION) {
                String tokens[] = txtURL.getText().split("/");
                String path = PLPToolbox.getConfDir() + "/autoload/" + tokens[tokens.length-1];
                String[] manifest = DynamicModuleFramework.loadJarWithManifest(path);
                if(manifest != null) {
                    DynamicModuleFramework.applyManifestEntries(manifest, plp);
                }
            }
        }
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        int index = tblMods.getSelectedRow();
        if(index > -1) {
            String path = PLPToolbox.getConfDir() + "/autoload/" + tblMods.getValueAt(index, 0);
            String[] manifest = DynamicModuleFramework.loadJarWithManifest(path);
            if(manifest != null)
                DynamicModuleFramework.applyManifestEntries(manifest, plp);
        }
    }//GEN-LAST:event_btnLoadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnLoad;
    private javax.swing.JLabel lblDownload;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JScrollPane scrollerTable;
    private javax.swing.JTable tblMods;
    private javax.swing.JTextField txtURL;
    // End of variables declaration//GEN-END:variables

}