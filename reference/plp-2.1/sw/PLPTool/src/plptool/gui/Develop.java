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

package plptool.gui;

import javax.swing.tree.*;

import java.io.File;

//For Syntax Highlighting
import javax.swing.text.*;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import plptool.PLPMsg;
import plptool.Constants;
import plptool.PLPCfg;

/**
 *
 * @author wira
 */
public class Develop extends javax.swing.JFrame {
    final static int RTYPE = 0;
    final static int ITYPE = 1;
    final static int BRANCH = 2;
    final static int JUMP = 3;
    final static int MEMTYPE = 4;
    final static int NOP = 5;
    final static int REG = 6;
    final static int IMM = 7;
    final static int LABEL = 8;
    final static int COMMENT = 9;
    final static int SYS = 10;

    boolean trackChanges = false;
    ProjectDriver plp;
    javax.swing.undo.UndoManager undoManager;

    int lastline = 0;
    boolean nothighlighting = true;
    
    /** Creates new form PLPDevelop */
    public Develop(ProjectDriver plp) {
        this.plp = plp;
        initComponents();

        DefaultMutableTreeNode projectRoot = new DefaultMutableTreeNode("No PLP Project Open");
        DefaultTreeModel treeModel = new DefaultTreeModel(projectRoot);
        treeProject.setModel(treeModel);
        
        splitter.setDividerLocation(0.25);
        
        PLPMsg.output = txtOutput;
        txtEditor.setEnabled(false);
        txtOutput.setEditable(false);
        rootmenuProject.setEnabled(false);
        menuImportASM.setEnabled(false);
        menuNewASM.setEnabled(false);
        menuExportASM.setEnabled(false);
        menuDeleteASM.setEnabled(false);
        menuSave.setEnabled(false);
        menuSaveAs.setEnabled(false);
        btnAssemble.setEnabled(false);
        menuFind.setEnabled(false);
        menuFindAndReplace.setEnabled(false);
        disableSimControls();

        treeProject.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent we) {
                exit();
            }
        });

        txtEditor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                notifyplpModified();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                notifyplpModified();
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                notifyplpModified();
            }
        });


        this.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);

        undoManager = new javax.swing.undo.UndoManager();
        txtEditor.getDocument().addUndoableEditListener(undoManager);

        this.setLocationRelativeTo(null);

        PLPMsg.M("Welcome to Progressive Learning Platform Software Tool version " + Constants.versionString);
    }

    public void updateComponents() {

    }

    public void notifyplpModified() {
        if(trackChanges) {
            if(nothighlighting) {
                plp.modified = true;
                plp.updateWindowTitle();
            }
        }
    }

    public javax.swing.JTextArea getOutput() {
        return txtOutput;
    }

    public javax.swing.JEditorPane getEditor() {
        return txtEditor;
    }

    public void setEditorText(String str) {
        trackChanges = false;
        txtEditor.setText(str);
        trackChanges = true;
        undoManager.discardAllEdits();
    }

    public String getEditorText() {
        return txtEditor.getText();
    }

    public void setCurFile(String path) {
        txtCurFile.setText(path);
    }

    public javax.swing.JTree getProjectTree() {
        return treeProject;
    }

    public final void enableBuildControls() {
        rootmenuProject.setEnabled(true);
        menuImportASM.setEnabled(true);
        menuNewASM.setEnabled(true);
        menuSave.setEnabled(true);
        menuSaveAs.setEnabled(true);
        btnAssemble.setEnabled(true);
        menuDeleteASM.setEnabled(true);
        menuExportASM.setEnabled(true);
    }

    public final void disableSimControls() {
        menuSimulate.setEnabled(false);
        menuProgram.setEnabled(false);
        btnSimulate.setEnabled(false);
    }

    public final  void enableSimControls() {
        menuSimulate.setEnabled(true);
        menuProgram.setEnabled(true);
        btnSimulate.setEnabled(true);
    }

    public int save() {
        
        return Constants.PLP_OK;
    }

    public void exit() {
        switch(askSaveFirst("exit", "Exit")) {
            case 2:
                return;
            default:
                System.exit(0);
        }
    }

    public void newPLPFile() {
        switch(askSaveFirst("create a new project", "Create a new project")) {
            case 2:
                return;
            default:
                plp.create();
        }
    }

    public void openPLPFile() {
        switch(askSaveFirst("open a project", "Open a project")) {
            case 2:
                return;
            default:
                PLPMsg.output = txtOutput;

                final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
                fc.setFileFilter(new PlpFilter());
                fc.setAcceptAllFileFilterUsed(false);
                fc.setCurrentDirectory(new File(plp.curdir));

                int retVal = fc.showOpenDialog(null);

                if(retVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                    plp.curdir = fc.getSelectedFile().getParent();
                    plp.open(fc.getSelectedFile().getAbsolutePath());
                }
        }
    }

    public int askSaveFirst(String action, String capAction) {
        if(plp.modified) {
            Object[] options = {"Save and " + action,
                    capAction + " without saving",
                    "Cancel"};
            int n = javax.swing.JOptionPane.showOptionDialog(this,
                "HALT! The project is modified and you are trying to " +
                action + ". How would you like to proceed?",
                "Project is modified and not saved",
                javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

            if(n == 0)
                plp.save();

            return n;
        }

        return -1;
    }

    public int deleteASM() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeProject.getLastSelectedPathComponent();

        if(node == null)
            return Constants.PLP_GENERIC_ERROR;

        if(node.isLeaf()) {
            String nodeStr = (String) node.getUserObject();

            if(nodeStr.endsWith("asm")) {

                if(plp.asms.size() <= 1) {
                    PLPMsg.E("Can not delete last source file.",
                             Constants.PLP_GENERIC_ERROR, null);

                    return Constants.PLP_GENERIC_ERROR;
                }

                String[] tokens = nodeStr.split("::");

                int remove_asm = Integer.parseInt(tokens[0]);
                if(remove_asm == plp.open_asm) {
                    plp.open_asm = 0;
                    plp.refreshProjectView(false);
                }

                plp.removeAsm(remove_asm);
            }
        }

        return Constants.PLP_OK;
    }

    public int importASM() {
        PLPMsg.output = txtOutput;

        final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setFileFilter(new AsmFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(new File(plp.curdir));

        int retVal = fc.showOpenDialog(null);

        if(retVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            plp.importAsm(fc.getSelectedFile().getAbsolutePath());
        }

        return Constants.PLP_OK;
    }

    public int exportASM() {
        PLPMsg.output = txtOutput;
        int indexToExport = -1;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeProject.getLastSelectedPathComponent();

        if(node == null)
            return Constants.PLP_GENERIC_ERROR;

        if(node.isLeaf()) {
            String nodeStr = (String) node.getUserObject();

            if(nodeStr.endsWith("asm")) {
                String[] tokens = nodeStr.split("::");
                indexToExport = Integer.parseInt(tokens[0]);
            }
        }

        if(indexToExport >= 0) {
            final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
            fc.setFileFilter(new AsmFilter());
            fc.setAcceptAllFileFilterUsed(false);
            fc.setCurrentDirectory(new File(plp.curdir));

            int retVal = fc.showSaveDialog(null);

            if(retVal == javax.swing.JFileChooser.APPROVE_OPTION)
                plp.exportAsm(indexToExport, fc.getSelectedFile().getAbsolutePath());
        }

        return Constants.PLP_OK;
    }

    public void about() {
        plp.g_about.setVisible(true);
    }

    private void syntaxHighlight() {
        nothighlighting = false;
        int currpos = 0;
        int doclength = txtEditor.getText().split("\\r?\\n").length;
        SimpleAttributeSet[] styles = setupHighlighting();
        for(int i=0;i<doclength;i++) {
            String currline = txtEditor.getText().split("\\r?\\n")[i];
            syntaxHighlight(currline, currpos, styles);
            currpos += txtEditor.getText().split("\\r?\\n")[i].length() + 1;
        }
        nothighlighting = true;
    }

    public void syntaxHighlight(int line) {
        nothighlighting = false;
        String currline = txtEditor.getText().split("\\r?\\n")[line];
        int currpos = 0;
        for(int i=0;i<line;i++) {
            currpos += txtEditor.getText().split("\\r?\\n")[i].length() + 1;
        }
        syntaxHighlight(currline, currpos, setupHighlighting());
        nothighlighting = true;
    }

    //Do not call this class without setting highlighting to true
    private void syntaxHighlight(String text, int position, SimpleAttributeSet[] styles) {
        StyledDocument doc = txtEditor.getStyledDocument();
        int currentposition = 0;
        int startposition = 0;
        int texttype = -1;
        String currtext = "";
        while (currentposition < text.length()) {
            StringBuilder currtextbuffer = new StringBuilder();
            while (currentposition < text.length() && (text.charAt(currentposition) == '\t' || text.charAt(currentposition) == ' ' || text.charAt(currentposition) == ',')) {
                    currentposition++;
            }
            startposition = currentposition;
            while (currentposition < text.length() && (text.charAt(currentposition) != '\t' && text.charAt(currentposition) != ' ')) {
                    currtextbuffer.append(text.charAt(currentposition));
                    currentposition++;
            }
            currtext = currtextbuffer.toString();

            if(texttype == COMMENT || currtext.contains("#")) {
                    texttype = COMMENT;
            } else if (texttype == SYS || currtext.contains(".")) {
                    texttype = SYS;
            } else if(currtext.contains(":")) {
                    texttype = LABEL;
            } else if(currtext.contains("$")) {
                    texttype = REG;
            } else if(isimmediate(currtext)) {
                    texttype = IMM;
            } else if(currtext.equals("nop")) {
                    texttype = NOP;
            } else if (texttype == -1) {
                if(currtext.contains("w")) {
                        texttype = MEMTYPE;
                } else if(currtext.contains("j")) {
                        texttype = JUMP;
                } else if(currtext.contains("b")) {
                        texttype = BRANCH;
                } else if(currtext.contains("i")) {
                        texttype = ITYPE;
                } else {
                        texttype = RTYPE;
                }
            } else {
                texttype = LABEL;
            }

            try {
                doc.remove(startposition+position,currentposition-startposition);
            } catch (BadLocationException ble) {
                System.err.println("Deletion error   position:" + position + "," + currtext.length());
            }

            try {
                doc.insertString(startposition+position,currtext,styles[texttype]);
            } catch (BadLocationException ble) {
                System.err.println("Insertion error   position:" + position);
            }
            
            currentposition++;
        }
    }

    private boolean isimmediate(String num) {
        Pattern pattern0 = Pattern.compile("-?[0-9]*");
        Matcher matcher0 = pattern0.matcher(num);
        Pattern pattern1 = Pattern.compile("0x[0-9a-fA-F]*");
        Matcher matcher1 = pattern1.matcher(num);
        return (matcher0.matches() || matcher1.matches());
    }

    private SimpleAttributeSet[] setupHighlighting() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(def,"Monospaced");
        StyleConstants.setFontSize(def,11);
        SimpleAttributeSet[] styles = new SimpleAttributeSet[11];
        styles[RTYPE] = new SimpleAttributeSet(def);
        StyleConstants.setForeground(styles[0],Color.blue);
        styles[ITYPE] = new SimpleAttributeSet(styles[RTYPE]);
        styles[BRANCH] = new SimpleAttributeSet(styles[RTYPE]);
        styles[JUMP] = new SimpleAttributeSet(styles[RTYPE]);
        styles[MEMTYPE] = new SimpleAttributeSet(styles[RTYPE]);
        styles[NOP] = new SimpleAttributeSet(styles[RTYPE]);
        StyleConstants.setForeground(styles[NOP],Color.gray);
        styles[REG] = new SimpleAttributeSet(def);
        StyleConstants.setForeground(styles[REG], Color.red);
        styles[IMM] = new SimpleAttributeSet(def);
        StyleConstants.setForeground(styles[IMM], Color.orange);
        styles[LABEL] = new SimpleAttributeSet(def);
        StyleConstants.setBold(styles[LABEL], true);
        styles[COMMENT] = new SimpleAttributeSet(def);
        StyleConstants.setForeground(styles[COMMENT], Color.green);
        styles[SYS] = new SimpleAttributeSet(def);
        StyleConstants.setForeground(styles[SYS], Color.pink);
        return styles;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        devMainPane = new javax.swing.JPanel();
        scrollOutput = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JTextArea();
        scrollPos = new javax.swing.JScrollPane();
        lblPosition = new javax.swing.JTextPane();
        splitter = new javax.swing.JSplitPane();
        scrollerTree = new javax.swing.JScrollPane();
        treeProject = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        txtCurFile = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtEditor = new javax.swing.JTextPane();
        toolbar = new javax.swing.JToolBar();
        btnAssemble = new javax.swing.JButton();
        btnSimulate = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnAbout = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        rootmenuFile = new javax.swing.JMenu();
        menuNew = new javax.swing.JMenuItem();
        menuSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuOpen = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        menuSaveAs = new javax.swing.JMenuItem();
        menuSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        rootmenuEdit = new javax.swing.JMenu();
        menuCopy = new javax.swing.JMenuItem();
        menuCut = new javax.swing.JMenuItem();
        menuPaste = new javax.swing.JMenuItem();
        menuSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuFind = new javax.swing.JMenuItem();
        menuFindAndReplace = new javax.swing.JMenuItem();
        menuSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuUndo = new javax.swing.JMenuItem();
        menuRedo = new javax.swing.JMenuItem();
        rootmenuProject = new javax.swing.JMenu();
        menuAssemble = new javax.swing.JMenuItem();
        menuSimulate = new javax.swing.JMenuItem();
        menuProgram = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuNewASM = new javax.swing.JMenuItem();
        menuImportASM = new javax.swing.JMenuItem();
        menuExportASM = new javax.swing.JMenuItem();
        menuDeleteASM = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuSetMainProgram = new javax.swing.JMenuItem();
        rootmenuHelp = new javax.swing.JMenu();
        menuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(plptool.gui.PLPToolApp.class).getContext().getResourceMap(Develop.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("resources/plp.png"));
        setName("Form"); // NOI18N
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        devMainPane.setName("devMainPane"); // NOI18N

        scrollOutput.setName("scrollOutput"); // NOI18N

        txtOutput.setColumns(20);
        txtOutput.setFont(resourceMap.getFont("txtOutput.font")); // NOI18N
        txtOutput.setRows(5);
        txtOutput.setName("txtOutput"); // NOI18N
        scrollOutput.setViewportView(txtOutput);

        scrollPos.setName("scrollPos"); // NOI18N

        lblPosition.setEditable(false);
        lblPosition.setFont(resourceMap.getFont("lblPosition.font")); // NOI18N
        lblPosition.setName("lblPosition"); // NOI18N
        scrollPos.setViewportView(lblPosition);

        javax.swing.GroupLayout devMainPaneLayout = new javax.swing.GroupLayout(devMainPane);
        devMainPane.setLayout(devMainPaneLayout);
        devMainPaneLayout.setHorizontalGroup(
            devMainPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, devMainPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPos, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(scrollOutput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
        );
        devMainPaneLayout.setVerticalGroup(
            devMainPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(devMainPaneLayout.createSequentialGroup()
                .addComponent(scrollOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPos, javax.swing.GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE))
        );

        splitter.setDividerLocation(100);
        splitter.setName("splitter"); // NOI18N
        splitter.setPreferredSize(new java.awt.Dimension(150, 342));

        scrollerTree.setName("scrollerTree"); // NOI18N

        treeProject.setName("treeProject"); // NOI18N
        treeProject.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeProjectMousePressed(evt);
            }
        });
        scrollerTree.setViewportView(treeProject);

        splitter.setLeftComponent(scrollerTree);

        jPanel1.setName("jPanel1"); // NOI18N

        txtCurFile.setBackground(resourceMap.getColor("txtCurFile.background")); // NOI18N
        txtCurFile.setEditable(false);
        txtCurFile.setText(resourceMap.getString("txtCurFile.text")); // NOI18N
        txtCurFile.setName("txtCurFile"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtEditor.setFont(resourceMap.getFont("txtEditor.font")); // NOI18N
        txtEditor.setMinimumSize(new java.awt.Dimension(1, 1));
        txtEditor.setName("txtEditor"); // NOI18N
        txtEditor.setPreferredSize(new java.awt.Dimension(1, 1));
        txtEditor.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtEditorCaretUpdate(evt);
            }
        });
        txtEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEditorKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEditorKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEditorKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(txtEditor);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtCurFile, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtCurFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
        );

        splitter.setRightComponent(jPanel1);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setName("toolbar"); // NOI18N

        btnAssemble.setIcon(resourceMap.getIcon("btnAssemble.icon")); // NOI18N
        btnAssemble.setText(resourceMap.getString("btnAssemble.text")); // NOI18N
        btnAssemble.setToolTipText(resourceMap.getString("btnAssemble.toolTipText")); // NOI18N
        btnAssemble.setFocusable(false);
        btnAssemble.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAssemble.setName("btnAssemble"); // NOI18N
        btnAssemble.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAssemble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssembleActionPerformed(evt);
            }
        });
        toolbar.add(btnAssemble);

        btnSimulate.setIcon(resourceMap.getIcon("btnSimulate.icon")); // NOI18N
        btnSimulate.setText(resourceMap.getString("btnSimulate.text")); // NOI18N
        btnSimulate.setToolTipText(resourceMap.getString("btnSimulate.toolTipText")); // NOI18N
        btnSimulate.setFocusable(false);
        btnSimulate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSimulate.setName("btnSimulate"); // NOI18N
        btnSimulate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSimulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimulateActionPerformed(evt);
            }
        });
        toolbar.add(btnSimulate);

        jSeparator3.setName("jSeparator3"); // NOI18N
        toolbar.add(jSeparator3);

        btnAbout.setIcon(resourceMap.getIcon("btnAbout.icon")); // NOI18N
        btnAbout.setText(resourceMap.getString("btnAbout.text")); // NOI18N
        btnAbout.setFocusable(false);
        btnAbout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbout.setName("btnAbout"); // NOI18N
        btnAbout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAboutActionPerformed(evt);
            }
        });
        toolbar.add(btnAbout);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        rootmenuFile.setText(resourceMap.getString("rootmenuFile.text")); // NOI18N
        rootmenuFile.setName("rootmenuFile"); // NOI18N

        menuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuNew.setText(resourceMap.getString("menuNew.text")); // NOI18N
        menuNew.setName("menuNew"); // NOI18N
        menuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuNew);

        menuSeparator1.setName("menuSeparator1"); // NOI18N
        rootmenuFile.add(menuSeparator1);

        menuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuOpen.setText(resourceMap.getString("menuOpen.text")); // NOI18N
        menuOpen.setName("menuOpen"); // NOI18N
        menuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuOpen);

        menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuSave.setText(resourceMap.getString("menuSave.text")); // NOI18N
        menuSave.setName("menuSave"); // NOI18N
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuSave);

        menuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        menuSaveAs.setText(resourceMap.getString("menuSaveAs.text")); // NOI18N
        menuSaveAs.setName("menuSaveAs"); // NOI18N
        menuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveAsActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuSaveAs);

        menuSeparator3.setName("menuSeparator3"); // NOI18N
        rootmenuFile.add(menuSeparator3);

        menuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuExit.setText(resourceMap.getString("menuExit.text")); // NOI18N
        menuExit.setName("menuExit"); // NOI18N
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuExit);

        jMenuBar1.add(rootmenuFile);

        rootmenuEdit.setText(resourceMap.getString("rootmenuEdit.text")); // NOI18N
        rootmenuEdit.setName("rootmenuEdit"); // NOI18N

        menuCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        menuCopy.setText(resourceMap.getString("menuCopy.text")); // NOI18N
        menuCopy.setName("menuCopy"); // NOI18N
        menuCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCopyActionPerformed(evt);
            }
        });
        rootmenuEdit.add(menuCopy);

        menuCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        menuCut.setText(resourceMap.getString("menuCut.text")); // NOI18N
        menuCut.setName("menuCut"); // NOI18N
        menuCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCutActionPerformed(evt);
            }
        });
        rootmenuEdit.add(menuCut);

        menuPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        menuPaste.setText(resourceMap.getString("menuPaste.text")); // NOI18N
        menuPaste.setName("menuPaste"); // NOI18N
        menuPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPasteActionPerformed(evt);
            }
        });
        rootmenuEdit.add(menuPaste);

        menuSeparator4.setName("menuSeparator4"); // NOI18N
        rootmenuEdit.add(menuSeparator4);

        menuFind.setText(resourceMap.getString("menuFind.text")); // NOI18N
        menuFind.setName("menuFind"); // NOI18N
        rootmenuEdit.add(menuFind);

        menuFindAndReplace.setText(resourceMap.getString("menuFindAndReplace.text")); // NOI18N
        menuFindAndReplace.setName("menuFindAndReplace"); // NOI18N
        rootmenuEdit.add(menuFindAndReplace);

        menuSeparator5.setName("menuSeparator5"); // NOI18N
        rootmenuEdit.add(menuSeparator5);

        menuUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        menuUndo.setText(resourceMap.getString("menuUndo.text")); // NOI18N
        menuUndo.setName("menuUndo"); // NOI18N
        menuUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuUndoActionPerformed(evt);
            }
        });
        rootmenuEdit.add(menuUndo);

        menuRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        menuRedo.setText(resourceMap.getString("menuRedo.text")); // NOI18N
        menuRedo.setName("menuRedo"); // NOI18N
        menuRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRedoActionPerformed(evt);
            }
        });
        rootmenuEdit.add(menuRedo);

        jMenuBar1.add(rootmenuEdit);

        rootmenuProject.setText(resourceMap.getString("rootmenuProject.text")); // NOI18N
        rootmenuProject.setName("rootmenuProject"); // NOI18N
        rootmenuProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootmenuProjectActionPerformed(evt);
            }
        });

        menuAssemble.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuAssemble.setIcon(resourceMap.getIcon("menuAssemble.icon")); // NOI18N
        menuAssemble.setText(resourceMap.getString("menuAssemble.text")); // NOI18N
        menuAssemble.setName("menuAssemble"); // NOI18N
        menuAssemble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAssembleActionPerformed1(evt);
            }
        });
        rootmenuProject.add(menuAssemble);

        menuSimulate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        menuSimulate.setIcon(resourceMap.getIcon("menuSimulate.icon")); // NOI18N
        menuSimulate.setText(resourceMap.getString("menuSimulate.text")); // NOI18N
        menuSimulate.setName("menuSimulate"); // NOI18N
        menuSimulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSimulateActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuSimulate);

        menuProgram.setText(resourceMap.getString("menuProgram.text")); // NOI18N
        menuProgram.setName("menuProgram"); // NOI18N
        menuProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuProgramActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuProgram);

        jSeparator1.setName("jSeparator1"); // NOI18N
        rootmenuProject.add(jSeparator1);

        menuNewASM.setText(resourceMap.getString("menuNewASM.text")); // NOI18N
        menuNewASM.setName("menuNewASM"); // NOI18N
        menuNewASM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewASMActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuNewASM);

        menuImportASM.setText(resourceMap.getString("menuImportASM.text")); // NOI18N
        menuImportASM.setName("menuImportASM"); // NOI18N
        menuImportASM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuImportASMActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuImportASM);

        menuExportASM.setText(resourceMap.getString("menuExportASM.text")); // NOI18N
        menuExportASM.setName("menuExportASM"); // NOI18N
        menuExportASM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExportASMActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuExportASM);

        menuDeleteASM.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        menuDeleteASM.setText(resourceMap.getString("menuDeleteASM.text")); // NOI18N
        menuDeleteASM.setName("menuDeleteASM"); // NOI18N
        menuDeleteASM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeleteASMActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuDeleteASM);

        jSeparator2.setName("jSeparator2"); // NOI18N
        rootmenuProject.add(jSeparator2);

        menuSetMainProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        menuSetMainProgram.setText(resourceMap.getString("menuSetMainProgram.text")); // NOI18N
        menuSetMainProgram.setName("menuSetMainProgram"); // NOI18N
        menuSetMainProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSetMainProgramActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuSetMainProgram);

        jMenuBar1.add(rootmenuProject);

        rootmenuHelp.setText(resourceMap.getString("rootmenuHelp.text")); // NOI18N
        rootmenuHelp.setName("rootmenuHelp"); // NOI18N
        rootmenuHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootmenuHelpActionPerformed(evt);
            }
        });

        menuAbout.setIcon(resourceMap.getIcon("menuAbout.icon")); // NOI18N
        menuAbout.setText(resourceMap.getString("menuAbout.text")); // NOI18N
        menuAbout.setName("menuAbout"); // NOI18N
        menuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAboutActionPerformed(evt);
            }
        });
        rootmenuHelp.add(menuAbout);

        jMenuBar1.add(rootmenuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(devMainPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
            .addComponent(splitter, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitter, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(devMainPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        exit();
    }//GEN-LAST:event_menuExitActionPerformed

    private void rootmenuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootmenuHelpActionPerformed

    }//GEN-LAST:event_rootmenuHelpActionPerformed

    private void menuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenActionPerformed
        openPLPFile();
        if(PLPCfg.cfgSyntaxHighlighting)
            syntaxHighlight();
    }//GEN-LAST:event_menuOpenActionPerformed

    private void menuSimulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSimulateActionPerformed
        if(plp.asm.isAssembled())
            plp.simulate();
    }//GEN-LAST:event_menuSimulateActionPerformed

    private void menuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewActionPerformed
        newPLPFile();
    }//GEN-LAST:event_menuNewActionPerformed

    private void menuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
        about();
    }//GEN-LAST:event_menuAboutActionPerformed

    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
        plp.save();
        if(PLPCfg.cfgSyntaxHighlighting)
            syntaxHighlight();
    }//GEN-LAST:event_menuSaveActionPerformed

    private void menuAssembleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAssembleActionPerformed
        PLPMsg.output = txtOutput;

        if(plp.plpfile != null)
            plp.assemble();
}//GEN-LAST:event_menuAssembleActionPerformed

    private void menuAssembleActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAssembleActionPerformed1
        PLPMsg.output = txtOutput;

        if(plp.plpfile != null)
            plp.assemble();
    }//GEN-LAST:event_menuAssembleActionPerformed1

    private void menuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveAsActionPerformed
        PLPMsg.output = txtOutput;

        final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setFileFilter(new PlpFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(new File(plp.curdir));

        int retVal = fc.showSaveDialog(null);

        if(retVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            plp.plpfile = fc.getSelectedFile().getAbsolutePath();
            plp.curdir = fc.getSelectedFile().getParent();
            if(!plp.plpfile.endsWith(".plp"))
                plp.plpfile += ".plp";
            plp.save();
            plp.open(plp.plpfile);
        }
    }//GEN-LAST:event_menuSaveAsActionPerformed

    private void menuImportASMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuImportASMActionPerformed
        importASM();
    }//GEN-LAST:event_menuImportASMActionPerformed

    private void menuDeleteASMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDeleteASMActionPerformed
        deleteASM();
    }//GEN-LAST:event_menuDeleteASMActionPerformed

    private void rootmenuProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootmenuProjectActionPerformed

    }//GEN-LAST:event_rootmenuProjectActionPerformed

    private void menuSetMainProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSetMainProgramActionPerformed
        plp.main_asm = plp.open_asm;
        if(nothighlighting) {
            plp.modified = true;
        }
        plp.refreshProjectView(true);
    }//GEN-LAST:event_menuSetMainProgramActionPerformed

    private void treeProjectMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeProjectMousePressed
        if(evt.getClickCount() == 2) { // user double clicked the project tree
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeProject.getLastSelectedPathComponent();

            if(node == null)
                return;

            if(node.isLeaf()) {
                String nodeStr = (String) node.getUserObject();

                if(nodeStr.endsWith("asm")) {

                    String[] tokens = nodeStr.split("::");

                    PLPMsg.I("Opening " + nodeStr, null);

                    plp.updateAsm(plp.open_asm, txtEditor.getText());
                    plp.open_asm = Integer.parseInt(tokens[0]);
                    plp.refreshProjectView(false);
                }
            }
        }
}//GEN-LAST:event_treeProjectMousePressed

    private void btnAssembleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssembleActionPerformed
        PLPMsg.output = txtOutput;

        if(plp.plpfile != null)
            plp.assemble();
    }//GEN-LAST:event_btnAssembleActionPerformed

    private void btnSimulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimulateActionPerformed
        if(plp.asm.isAssembled())
            plp.simulate();
    }//GEN-LAST:event_btnSimulateActionPerformed

    private void btnAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAboutActionPerformed
        about();
    }//GEN-LAST:event_btnAboutActionPerformed

    private void menuExportASMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExportASMActionPerformed
        exportASM();
    }//GEN-LAST:event_menuExportASMActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        PLPMsg.output = txtOutput;
    }//GEN-LAST:event_formWindowGainedFocus

    private void menuProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuProgramActionPerformed
        plp.g_prg.setVisible(true);
    }//GEN-LAST:event_menuProgramActionPerformed

    private void menuNewASMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewASMActionPerformed
        plp.g_fname.setMode(false);
        plp.g_fname.setVisible(true);
    }//GEN-LAST:event_menuNewASMActionPerformed

    private void menuCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCopyActionPerformed
        txtEditor.copy();
    }//GEN-LAST:event_menuCopyActionPerformed

    private void menuCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCutActionPerformed
        txtEditor.cut();
    }//GEN-LAST:event_menuCutActionPerformed

    private void menuPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPasteActionPerformed
        txtEditor.paste();
    }//GEN-LAST:event_menuPasteActionPerformed

    private void menuUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuUndoActionPerformed
        if(undoManager.canUndo())
            undoManager.undo();
    }//GEN-LAST:event_menuUndoActionPerformed

    private void menuRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRedoActionPerformed
        if(undoManager.canRedo())
            undoManager.redo();
    }//GEN-LAST:event_menuRedoActionPerformed

    private void txtEditorCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtEditorCaretUpdate
        int caretPos = txtEditor.getCaretPosition();
        int line = txtEditor.getText().substring(0, caretPos).split("\\r?\\n").length;

        if(PLPCfg.cfgSyntaxHighlighting) {
            if (line != lastline) {
                //Would rather use invokeAndWait, but there are some issues concerning
                //calling from within this method
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        syntaxHighlight(lastline);
                    }
                });
                lastline = line;
            }
        }

        lblPosition.setText(caretPos + " line: " + line);
    }//GEN-LAST:event_txtEditorCaretUpdate

    private void txtEditorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEditorKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEditorKeyPressed

    private void txtEditorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEditorKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEditorKeyReleased

    private void txtEditorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEditorKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEditorKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbout;
    private javax.swing.JButton btnAssemble;
    private javax.swing.JButton btnSimulate;
    private javax.swing.JPanel devMainPane;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JTextPane lblPosition;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuItem menuAssemble;
    private javax.swing.JMenuItem menuCopy;
    private javax.swing.JMenuItem menuCut;
    private javax.swing.JMenuItem menuDeleteASM;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuExportASM;
    private javax.swing.JMenuItem menuFind;
    private javax.swing.JMenuItem menuFindAndReplace;
    private javax.swing.JMenuItem menuImportASM;
    private javax.swing.JMenuItem menuNew;
    private javax.swing.JMenuItem menuNewASM;
    private javax.swing.JMenuItem menuOpen;
    private javax.swing.JMenuItem menuPaste;
    private javax.swing.JMenuItem menuProgram;
    private javax.swing.JMenuItem menuRedo;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JMenuItem menuSaveAs;
    private javax.swing.JPopupMenu.Separator menuSeparator1;
    private javax.swing.JPopupMenu.Separator menuSeparator3;
    private javax.swing.JPopupMenu.Separator menuSeparator4;
    private javax.swing.JPopupMenu.Separator menuSeparator5;
    private javax.swing.JMenuItem menuSetMainProgram;
    private javax.swing.JMenuItem menuSimulate;
    private javax.swing.JMenuItem menuUndo;
    private javax.swing.JMenu rootmenuEdit;
    private javax.swing.JMenu rootmenuFile;
    private javax.swing.JMenu rootmenuHelp;
    private javax.swing.JMenu rootmenuProject;
    private javax.swing.JScrollPane scrollOutput;
    private javax.swing.JScrollPane scrollPos;
    private javax.swing.JScrollPane scrollerTree;
    private javax.swing.JSplitPane splitter;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JTree treeProject;
    private javax.swing.JTextField txtCurFile;
    private javax.swing.JTextPane txtEditor;
    private javax.swing.JTextArea txtOutput;
    // End of variables declaration//GEN-END:variables

}

class AsmFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(java.io.File f) {
        if(f.isDirectory())
            return true;

        if(f.getAbsolutePath().endsWith(".asm"))
            return true;

        return false;
    }

    public String getDescription() {
        return ".ASM files";
    }
}

class PlpFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(java.io.File f) {
        if(f.isDirectory())
            return true;

        if(f.getAbsolutePath().endsWith(".plp"))
            return true;

        return false;
    }

    public String getDescription() {
        return "PLP project files";
    }
}