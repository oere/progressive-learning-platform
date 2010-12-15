/*
    Copyright 2010 David Fritz, Brian Gordon, Wira Mulia

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

/*
 * PLPToolView.java
 */

package plptool.gui;

import plptool.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Color;

/**
 * The application's main frame.
 */
public class PLPToolView extends FrameView {

    static final int MAX_STEPS = 60000;
    PLPBackend backend;

    public PLPToolView(SingleFrameApplication app, PLPBackend backend) {
        super(app);

        this.backend = backend;

        initComponents();
        PLPMsg.output = Output; // reroute PLPMsg output
        PLPMsg.M("PLPTool GUI\n");
        PLPMsg.m("Welcome to the GUI PLP software. You can start using this tool ");
        PLPMsg.M("by opening the Develop tab to write or open an assembly file.");
        PLPMainPane.setEnabledAt(1, false);
        PLPMainPane.setEnabledAt(2, false);
        
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = PLPToolApp.getApplication().getMainFrame();
            aboutBox = new PLPToolAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        PLPToolApp.getApplication().show(aboutBox);
    }

    public void plpMsgRouteBack() {
        PLPMsg.output = Output;
    }

    public void selectFirstPane(){
        PLPMainPane.setSelectedIndex(0);
    }

    public void summonFrame(javax.swing.JInternalFrame frame) {
        simDesktop.add(frame);
        frame.setVisible(true);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        PLPMainPane = new javax.swing.JTabbedPane();
        IDEPane = new javax.swing.JPanel();
        IDEBar = new javax.swing.JToolBar();
        IDENewBtn = new javax.swing.JButton();
        IDEOpenBtn = new javax.swing.JButton();
        IDESaveBtn = new javax.swing.JButton();
        IDESaveAsBtn = new javax.swing.JButton();
        IDEAssembleBtn = new javax.swing.JButton();
        IDEScroller = new javax.swing.JScrollPane();
        IDEStdOut = new javax.swing.JTextArea();
        IDESplitter = new javax.swing.JSplitPane();
        IDETreePane = new javax.swing.JScrollPane();
        IDEContextTree = new javax.swing.JTree();
        jScrollPane4 = new javax.swing.JScrollPane();
        IDEEditor = new javax.swing.JEditorPane();
        IDECommander = new javax.swing.JTextField();
        IDELabel_Command = new javax.swing.JLabel();
        SimPane = new javax.swing.JPanel();
        simControls = new javax.swing.JPanel();
        btnStep = new javax.swing.JButton();
        lblControl = new javax.swing.JLabel();
        tglRun = new javax.swing.JToggleButton();
        txtSteps = new javax.swing.JTextField();
        btnReset = new javax.swing.JButton();
        tglIODisplay = new javax.swing.JToggleButton();
        btnMem = new javax.swing.JToggleButton();
        lblFlags = new javax.swing.JLabel();
        txtFlags = new javax.swing.JTextField();
        btnOpts = new javax.swing.JButton();
        simDesktop = new javax.swing.JDesktopPane();
        PrgPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        OutputPane = new javax.swing.JPanel();
        OutputScrollPane = new javax.swing.JScrollPane();
        Output = new javax.swing.JTextArea();

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(plptool.gui.PLPToolApp.class).getContext().getResourceMap(PLPToolView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(plptool.gui.PLPToolApp.class).getContext().getActionMap(PLPToolView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        exitMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                exitMenuItemMouseReleased(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        PLPMainPane.setMinimumSize(new java.awt.Dimension(56, 55));
        PLPMainPane.setName("PLPMainPane"); // NOI18N

        IDEPane.setName("IDETab"); // NOI18N
        IDEPane.setVerifyInputWhenFocusTarget(false);

        IDEBar.setFloatable(false);
        IDEBar.setRollover(true);
        IDEBar.setName("IDEBar"); // NOI18N

        IDENewBtn.setText(resourceMap.getString("IDENewBtn.text")); // NOI18N
        IDENewBtn.setFocusable(false);
        IDENewBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        IDENewBtn.setName("IDENewBtn"); // NOI18N
        IDENewBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        IDEBar.add(IDENewBtn);

        IDEOpenBtn.setText(resourceMap.getString("IDEOpenBtn.text")); // NOI18N
        IDEOpenBtn.setFocusable(false);
        IDEOpenBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        IDEOpenBtn.setName("IDEOpenBtn"); // NOI18N
        IDEOpenBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        IDEBar.add(IDEOpenBtn);

        IDESaveBtn.setText(resourceMap.getString("IDESaveBtn.text")); // NOI18N
        IDESaveBtn.setFocusable(false);
        IDESaveBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        IDESaveBtn.setName("IDESaveBtn"); // NOI18N
        IDESaveBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        IDEBar.add(IDESaveBtn);

        IDESaveAsBtn.setText(resourceMap.getString("IDESaveAsBtn.text")); // NOI18N
        IDESaveAsBtn.setFocusable(false);
        IDESaveAsBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        IDESaveAsBtn.setName("IDESaveAsBtn"); // NOI18N
        IDESaveAsBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        IDEBar.add(IDESaveAsBtn);

        IDEAssembleBtn.setText(resourceMap.getString("IDEAssembleBtn.text")); // NOI18N
        IDEAssembleBtn.setFocusable(false);
        IDEAssembleBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        IDEAssembleBtn.setName("IDEAssembleBtn"); // NOI18N
        IDEAssembleBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        IDEAssembleBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                IDEAssembleBtnMouseClicked(evt);
            }
        });
        IDEAssembleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDEAssembleBtnActionPerformed(evt);
            }
        });
        IDEBar.add(IDEAssembleBtn);

        IDEScroller.setName("IDEScroller"); // NOI18N

        IDEStdOut.setColumns(20);
        IDEStdOut.setEditable(false);
        IDEStdOut.setFont(resourceMap.getFont("IDEStdOut.font")); // NOI18N
        IDEStdOut.setRows(5);
        IDEStdOut.setName("IDEStdOut"); // NOI18N
        IDEScroller.setViewportView(IDEStdOut);

        IDESplitter.setResizeWeight(0.7);
        IDESplitter.setName("IDESplitter"); // NOI18N

        IDETreePane.setName("IDETreePane"); // NOI18N

        IDEContextTree.setName("IDEContextTree"); // NOI18N
        IDETreePane.setViewportView(IDEContextTree);

        IDESplitter.setRightComponent(IDETreePane);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        IDEEditor.setFont(resourceMap.getFont("IDEEditor.font")); // NOI18N
        IDEEditor.setName("IDEEditor"); // NOI18N
        jScrollPane4.setViewportView(IDEEditor);

        IDESplitter.setLeftComponent(jScrollPane4);

        IDECommander.setText(resourceMap.getString("IDECommander.text")); // NOI18N
        IDECommander.setName("IDECommander"); // NOI18N

        IDELabel_Command.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        IDELabel_Command.setText(resourceMap.getString("IDELabel_Command.text")); // NOI18N
        IDELabel_Command.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        IDELabel_Command.setInheritsPopupMenu(false);
        IDELabel_Command.setName("IDELabel_Command"); // NOI18N

        javax.swing.GroupLayout IDEPaneLayout = new javax.swing.GroupLayout(IDEPane);
        IDEPane.setLayout(IDEPaneLayout);
        IDEPaneLayout.setHorizontalGroup(
            IDEPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(IDESplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
            .addGroup(IDEPaneLayout.createSequentialGroup()
                .addComponent(IDELabel_Command)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IDECommander, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE))
            .addComponent(IDEScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
            .addComponent(IDEBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
        );
        IDEPaneLayout.setVerticalGroup(
            IDEPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IDEPaneLayout.createSequentialGroup()
                .addComponent(IDEBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IDESplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IDEScroller, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(IDEPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IDECommander, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(IDELabel_Command)))
        );

        PLPMainPane.addTab(resourceMap.getString("IDETab.TabConstraints.tabTitle"), IDEPane); // NOI18N

        SimPane.setEnabled(false);
        SimPane.setName("EmulatorTab"); // NOI18N

        simControls.setName("simControls"); // NOI18N

        btnStep.setText(resourceMap.getString("btnStep.text")); // NOI18N
        btnStep.setName("btnStep"); // NOI18N
        btnStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepActionPerformed(evt);
            }
        });

        lblControl.setText(resourceMap.getString("lblControl.text")); // NOI18N
        lblControl.setName("lblControl"); // NOI18N

        tglRun.setText(resourceMap.getString("tglRun.text")); // NOI18N
        tglRun.setName("tglRun"); // NOI18N
        tglRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglRunActionPerformed(evt);
            }
        });

        txtSteps.setText(resourceMap.getString("txtSteps.text")); // NOI18N
        txtSteps.setName("txtSteps"); // NOI18N

        btnReset.setText(resourceMap.getString("btnReset.text")); // NOI18N
        btnReset.setName("btnReset"); // NOI18N
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        tglIODisplay.setText(resourceMap.getString("tglIODisplay.text")); // NOI18N
        tglIODisplay.setName("tglIODisplay"); // NOI18N
        tglIODisplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglIODisplayActionPerformed(evt);
            }
        });

        btnMem.setText(resourceMap.getString("btnMem.text")); // NOI18N
        btnMem.setName("btnMem"); // NOI18N

        lblFlags.setText(resourceMap.getString("lblFlags.text")); // NOI18N
        lblFlags.setName("lblFlags"); // NOI18N

        txtFlags.setText(resourceMap.getString("txtFlags.text")); // NOI18N
        txtFlags.setName("txtFlags"); // NOI18N

        btnOpts.setText(resourceMap.getString("btnOpts.text")); // NOI18N
        btnOpts.setName("btnOpts"); // NOI18N
        btnOpts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOptsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout simControlsLayout = new javax.swing.GroupLayout(simControls);
        simControls.setLayout(simControlsLayout);
        simControlsLayout.setHorizontalGroup(
            simControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(simControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblControl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSteps, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnStep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tglRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReset)
                .addGap(18, 18, 18)
                .addComponent(tglIODisplay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMem, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblFlags)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFlags, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOpts)
                .addContainerGap(152, Short.MAX_VALUE))
        );
        simControlsLayout.setVerticalGroup(
            simControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(simControlsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(simControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblControl)
                    .addComponent(tglRun)
                    .addComponent(btnStep)
                    .addComponent(txtSteps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset)
                    .addComponent(tglIODisplay)
                    .addComponent(btnMem)
                    .addComponent(lblFlags)
                    .addComponent(txtFlags, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOpts))
                .addContainerGap())
        );

        simDesktop.setBackground(resourceMap.getColor("simDesktop.background")); // NOI18N
        simDesktop.setName("simDesktop"); // NOI18N

        javax.swing.GroupLayout SimPaneLayout = new javax.swing.GroupLayout(SimPane);
        SimPane.setLayout(SimPaneLayout);
        SimPaneLayout.setHorizontalGroup(
            SimPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(simControls, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(simDesktop, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
        );
        SimPaneLayout.setVerticalGroup(
            SimPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SimPaneLayout.createSequentialGroup()
                .addComponent(simDesktop, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(simControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        PLPMainPane.addTab(resourceMap.getString("EmulatorTab.TabConstraints.tabTitle"), SimPane); // NOI18N

        PrgPane.setEnabled(false);
        PrgPane.setName("PrgPane"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setName("jComboBox1"); // NOI18N

        jComboBox2.setEditable(true);
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.setName("jComboBox2"); // NOI18N

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox3.setName("jComboBox3"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout PrgPaneLayout = new javax.swing.GroupLayout(PrgPane);
        PrgPane.setLayout(PrgPaneLayout);
        PrgPaneLayout.setHorizontalGroup(
            PrgPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PrgPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PrgPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PrgPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, 0, 686, Short.MAX_VALUE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, 0, 686, Short.MAX_VALUE)
                    .addComponent(jComboBox3, 0, 686, Short.MAX_VALUE))
                .addContainerGap())
        );
        PrgPaneLayout.setVerticalGroup(
            PrgPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PrgPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PrgPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PrgPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PrgPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(391, Short.MAX_VALUE))
        );

        PLPMainPane.addTab(resourceMap.getString("PrgPane.TabConstraints.tabTitle"), PrgPane); // NOI18N

        OutputPane.setName("OutputPane"); // NOI18N

        OutputScrollPane.setName("OutputScrollPane"); // NOI18N

        Output.setColumns(20);
        Output.setEditable(false);
        Output.setFont(resourceMap.getFont("Output.font")); // NOI18N
        Output.setRows(5);
        Output.setName("Output"); // NOI18N
        OutputScrollPane.setViewportView(Output);

        javax.swing.GroupLayout OutputPaneLayout = new javax.swing.GroupLayout(OutputPane);
        OutputPane.setLayout(OutputPaneLayout);
        OutputPaneLayout.setHorizontalGroup(
            OutputPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OutputPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(OutputScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 839, Short.MAX_VALUE)
                .addContainerGap())
        );
        OutputPaneLayout.setVerticalGroup(
            OutputPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OutputPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(OutputScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addContainerGap())
        );

        PLPMainPane.addTab(resourceMap.getString("OutputPane.TabConstraints.tabTitle"), OutputPane); // NOI18N

        PLPMainPane.getAccessibleContext().setAccessibleName(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N

        setComponent(PLPMainPane);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMenuItemMouseReleased
    }//GEN-LAST:event_exitMenuItemMouseReleased

    private void IDEAssembleBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_IDEAssembleBtnMouseClicked
        
    }//GEN-LAST:event_IDEAssembleBtnMouseClicked

    private void IDEAssembleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDEAssembleBtnActionPerformed
        int ret = 0;
        PLPMsg.output = IDEStdOut;
        destroySimulation();
        PLPMsg.M("Assembling...");
        
        if(PLPCfg.cfgArch.equals("plpmips"))
            backend.asm = new plptool.mips.Asm(IDEEditor.getText(), "IDEEditor");

        if((ret = backend.asm.preprocess(0)) == Constants.PLP_OK) {
            ret = backend.asm.assemble();
        }
        if(ret == Constants.PLP_OK) {
            PLPMsg.M("Done.");
            PLPMainPane.setEnabledAt(1, true);
            PLPMainPane.setEnabledAt(2, true);
            
            backend.g_err = new PLPErrorFrame();

            if(PLPCfg.cfgArch.equals("plpmips"))
                backend.sim = new plptool.mips.SimCore((plptool.mips.Asm) backend.asm, -1);
    
            backend.sim.reset();
            backend.sim.step();

            if(PLPCfg.cfgArch.equals("plpmips"))
                backend.g_sim = new plptool.mips.SimCoreGUI(backend);

            simDesktop.add(backend.g_sim);
            simDesktop.add(backend.g_err);
            
            backend.g_sim.setVisible(true);
            backend.g_err.setVisible(true);

            PLPMainPane.setSelectedIndex(1);
        }
        else {
            PLPMainPane.setEnabledAt(1, false);
            PLPMainPane.setEnabledAt(2, false);
            
            PLPMsg.M("Fix your code.");
            PLPMsg.M("");
            PLPMsg.output = Output;
        }
    }//GEN-LAST:event_IDEAssembleBtnActionPerformed

    private void btnStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStepActionPerformed
        if(simRunner != null)
            simRunner.stepCount = 0;
        backend.g_err.clearError();

        try {
            int steps = Integer.parseInt(txtSteps.getText());
            if(steps <= MAX_STEPS && steps > 0) {
                for(int i = 0; i < steps; i++)
                    backend.sim.step();
                backend.g_sim.updateComponents();
            } else {
                txtSteps.setText("1");
            }
        } catch(Exception e) {
            txtSteps.setText("1");
        }

        if(PLPMsg.lastError != 0)
            backend.g_err.setError(PLPMsg.lastError);
    }//GEN-LAST:event_btnStepActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        if(simRunner != null)
            simRunner.stepCount = 0;
        backend.sim.reset();
        backend.g_sim.updateComponents();
        backend.g_err.clearError();
    }//GEN-LAST:event_btnResetActionPerformed

    private void tglIODisplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglIODisplayActionPerformed
        if(tglIODisplay.isSelected()) {
            if(backend.g_ioreg == null) {
                backend.g_ioreg = new PLPIORegistry(backend);
                simDesktop.add(backend.g_ioreg);
            }
            backend.g_ioreg.setVisible(true);
        } else
            backend.g_ioreg.setVisible(false);
    }//GEN-LAST:event_tglIODisplayActionPerformed

    private void tglRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglRunActionPerformed
        if(tglRun.isSelected()) {
            simRunner = new PLPSimRunner(backend);
            simRunner.start();
        } else {
            if(simRunner != null) {
                try {
                    simRunner.stepCount = 0;
                } catch(Exception e) {}
            }
        }
    }//GEN-LAST:event_tglRunActionPerformed

    private void btnOptsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOptsActionPerformed
        if(opts == null)
            opts = new PLPOptions();
        opts.setVisible(true);
    }//GEN-LAST:event_btnOptsActionPerformed

    public void unselectTglRun() {
        tglRun.setSelected(false);
    }

    public void destroySimulation() {
        if(backend.sim != null) {
            backend.g_sim.dispose();
            backend.g_err.dispose();
            backend.ioreg.removeAllModules(backend.sim);
        }

        if(simRunner != null) {
            simRunner.stepCount = 0;
        }

        simDesktop.removeAll();
        backend.g_ioreg.dispose();
        backend.g_ioreg = null;
        tglIODisplay.setSelected(false);
    }

    public javax.swing.JDesktopPane getSimDesktop() {
        return simDesktop;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton IDEAssembleBtn;
    private javax.swing.JToolBar IDEBar;
    private javax.swing.JTextField IDECommander;
    private javax.swing.JTree IDEContextTree;
    private javax.swing.JEditorPane IDEEditor;
    private javax.swing.JLabel IDELabel_Command;
    private javax.swing.JButton IDENewBtn;
    private javax.swing.JButton IDEOpenBtn;
    private javax.swing.JPanel IDEPane;
    private javax.swing.JButton IDESaveAsBtn;
    private javax.swing.JButton IDESaveBtn;
    private javax.swing.JScrollPane IDEScroller;
    private javax.swing.JSplitPane IDESplitter;
    private javax.swing.JTextArea IDEStdOut;
    private javax.swing.JScrollPane IDETreePane;
    private javax.swing.JTextArea Output;
    private javax.swing.JPanel OutputPane;
    private javax.swing.JScrollPane OutputScrollPane;
    private javax.swing.JTabbedPane PLPMainPane;
    private javax.swing.JPanel PrgPane;
    private javax.swing.JPanel SimPane;
    private javax.swing.JToggleButton btnMem;
    private javax.swing.JButton btnOpts;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnStep;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblControl;
    private javax.swing.JLabel lblFlags;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel simControls;
    private javax.swing.JDesktopPane simDesktop;
    private javax.swing.JToggleButton tglIODisplay;
    private javax.swing.JToggleButton tglRun;
    private javax.swing.JTextField txtFlags;
    private javax.swing.JTextField txtSteps;
    // End of variables declaration//GEN-END:variables

    private JDialog             aboutBox;
    private PLPSimRunner        simRunner;
    private PLPOptions          opts;

}
