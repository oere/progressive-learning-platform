/*
 * PLPToolView.java
 */

package plptool;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * The application's main frame.
 */
public class PLPToolView extends FrameView {

    public PLPToolView(SingleFrameApplication app) {
        super(app);

        initComponents();              
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
        IDEFirstPassBtn = new javax.swing.JButton();
        IDEAssembleBtn = new javax.swing.JButton();
        IDEScroller = new javax.swing.JScrollPane();
        IDEStdOut = new javax.swing.JTextArea();
        IDESplitter = new javax.swing.JSplitPane();
        IDETextPane = new javax.swing.JScrollPane();
        IDEEditor = new javax.swing.JTextPane();
        IDETreePane = new javax.swing.JScrollPane();
        IDEContextTree = new javax.swing.JTree();
        IDECommander = new javax.swing.JTextField();
        IDELabel_Command = new javax.swing.JLabel();
        EmuPane = new javax.swing.JPanel();
        PrgPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(plptool.PLPToolApp.class).getContext().getResourceMap(PLPToolView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(plptool.PLPToolApp.class).getContext().getActionMap(PLPToolView.class, this);
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

        PLPMainPane.setName("PLPMainPane"); // NOI18N

        IDEPane.setName("IDETab"); // NOI18N
        IDEPane.setVerifyInputWhenFocusTarget(false);

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

        IDEFirstPassBtn.setText(resourceMap.getString("IDEFirstPassBtn.text")); // NOI18N
        IDEFirstPassBtn.setFocusable(false);
        IDEFirstPassBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        IDEFirstPassBtn.setName("IDEFirstPassBtn"); // NOI18N
        IDEFirstPassBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        IDEBar.add(IDEFirstPassBtn);

        IDEAssembleBtn.setText(resourceMap.getString("IDEAssembleBtn.text")); // NOI18N
        IDEAssembleBtn.setFocusable(false);
        IDEAssembleBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        IDEAssembleBtn.setName("IDEAssembleBtn"); // NOI18N
        IDEAssembleBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        IDEBar.add(IDEAssembleBtn);

        IDEScroller.setName("IDEScroller"); // NOI18N

        IDEStdOut.setColumns(20);
        IDEStdOut.setEditable(false);
        IDEStdOut.setRows(5);
        IDEStdOut.setName("IDEStdOut"); // NOI18N
        IDEScroller.setViewportView(IDEStdOut);

        IDESplitter.setResizeWeight(0.7);
        IDESplitter.setName("IDESplitter"); // NOI18N

        IDETextPane.setName("IDETextPane"); // NOI18N

        IDEEditor.setName("IDEEditor"); // NOI18N
        IDETextPane.setViewportView(IDEEditor);

        IDESplitter.setLeftComponent(IDETextPane);

        IDETreePane.setName("IDETreePane"); // NOI18N

        IDEContextTree.setName("IDEContextTree"); // NOI18N
        IDETreePane.setViewportView(IDEContextTree);

        IDESplitter.setRightComponent(IDETreePane);

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
            .addComponent(IDEBar, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
            .addComponent(IDESplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
            .addGroup(IDEPaneLayout.createSequentialGroup()
                .addComponent(IDELabel_Command, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(IDECommander, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE))
            .addComponent(IDEScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
        );
        IDEPaneLayout.setVerticalGroup(
            IDEPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IDEPaneLayout.createSequentialGroup()
                .addComponent(IDEBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IDESplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IDEScroller, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(IDEPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IDECommander, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(IDELabel_Command)))
        );

        PLPMainPane.addTab(resourceMap.getString("IDETab.TabConstraints.tabTitle"), IDEPane); // NOI18N

        EmuPane.setName("EmulatorTab"); // NOI18N

        javax.swing.GroupLayout EmuPaneLayout = new javax.swing.GroupLayout(EmuPane);
        EmuPane.setLayout(EmuPaneLayout);
        EmuPaneLayout.setHorizontalGroup(
            EmuPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 693, Short.MAX_VALUE)
        );
        EmuPaneLayout.setVerticalGroup(
            EmuPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        PLPMainPane.addTab(resourceMap.getString("EmulatorTab.TabConstraints.tabTitle"), EmuPane); // NOI18N

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
                    .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, 0, 516, Short.MAX_VALUE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, 0, 516, Short.MAX_VALUE)
                    .addComponent(jComboBox3, 0, 516, Short.MAX_VALUE))
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
                .addContainerGap(379, Short.MAX_VALUE))
        );

        PLPMainPane.addTab(resourceMap.getString("PrgPane.TabConstraints.tabTitle"), PrgPane); // NOI18N

        PLPMainPane.getAccessibleContext().setAccessibleName(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N

        setComponent(PLPMainPane);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMenuItemMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_exitMenuItemMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel EmuPane;
    private javax.swing.JButton IDEAssembleBtn;
    private javax.swing.JToolBar IDEBar;
    private javax.swing.JTextField IDECommander;
    private javax.swing.JTree IDEContextTree;
    private javax.swing.JTextPane IDEEditor;
    private javax.swing.JButton IDEFirstPassBtn;
    private javax.swing.JLabel IDELabel_Command;
    private javax.swing.JButton IDENewBtn;
    private javax.swing.JButton IDEOpenBtn;
    private javax.swing.JPanel IDEPane;
    private javax.swing.JButton IDESaveAsBtn;
    private javax.swing.JButton IDESaveBtn;
    private javax.swing.JScrollPane IDEScroller;
    private javax.swing.JSplitPane IDESplitter;
    private javax.swing.JTextArea IDEStdOut;
    private javax.swing.JScrollPane IDETextPane;
    private javax.swing.JScrollPane IDETreePane;
    private javax.swing.JTabbedPane PLPMainPane;
    private javax.swing.JPanel PrgPane;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar menuBar;
    // End of variables declaration//GEN-END:variables

    private JDialog aboutBox;

    public void appendStdOut(String text) {
        IDEStdOut.append(text + "\n");
    }
}
