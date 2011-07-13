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

package plptool.gui.frames;

import java.awt.Color;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.awt.Desktop;
import java.awt.Point;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.UndoManager;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.net.URI;



import java.io.File;
import java.awt.datatransfer.DataFlavor;

//For Syntax Highlighting
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import plptool.Msg;
import plptool.Constants;
import plptool.Config;
import plptool.gui.ProjectDriver;
import plptool.gui.SerialTerminal;

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
    private ProjectDriver plp;
    private DevUndoManager undoManager;
    private javax.swing.JPopupMenu popupProject;
    private int oldPosition;

    private TextLineNumber tln;
    private TextLineHighlighter tlh;

    /** Records number of non character keys pressed */
    int nonTextKeyPressed = 0;

    public SimpleAttributeSet[] styles = setupHighlighting();
    
    /** Creates new form PLPDevelop */
    public Develop(ProjectDriver plp) {
        this.plp = plp;
        initComponents();

        DefaultMutableTreeNode projectRoot = new DefaultMutableTreeNode("No PLP Project Open");
        DefaultTreeModel treeModel = new DefaultTreeModel(projectRoot);
        treeProject.setModel(treeModel);
        
        splitterH.setDividerLocation(0.25);

        tln = new TextLineNumber(txtEditor);
        tlh = new TextLineHighlighter(txtEditor);
        scroller.setRowHeaderView(tln);

        catchyPLP();

        oldPosition = 0;

        Msg.output = txtOutput;
        scroller.setEnabled(false);
        txtOutput.setEditable(false);
        rootmenuProject.setEnabled(false);
        menuPrint.setEnabled(false);
        menuImportASM.setEnabled(false);
        menuNewASM.setEnabled(false);
        menuExportASM.setEnabled(false);
        menuDeleteASM.setEnabled(false);
        menuSave.setEnabled(false);
        menuSaveAs.setEnabled(false);
        btnAssemble.setEnabled(false);
        menuFindAndReplace.setEnabled(false);
        rootmenuEdit.setEnabled(false);
        btnSave.setEnabled(false);
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

        undoManager = new DevUndoManager();
        undoManager.setLimit(Config.devMaxUndoEntries);
        
        initPopupMenus();

        
        this.setLocationRelativeTo(null);

        Msg.M(Constants.copyrightString);
    }

    public void updateComponents() {
        try {
        if(plp.isSimulating()) {
            plptool.mips.SimCore sim = (plptool.mips.SimCore) plp.sim;
            int pc_index = plp.asm.lookupAddrIndex(sim.id_stage.i_instrAddr);
            if(pc_index == -1 || sim.isStalled()) {
                tln.setHighlight(-1);
                return;
            }

            int lineNum = plp.asm.getLineNumMapper()[pc_index];
            int fileNum = plp.asm.getFileMapper()[pc_index];

            int yPos = (lineNum - 1) * txtEditor.getFontMetrics(txtEditor.getFont()).getHeight();
            int viewPortY = scroller.getViewport().getViewPosition().y;

            if(yPos > (viewPortY + scroller.getHeight()) || yPos < viewPortY)
                scroller.getViewport().setViewPosition(new Point(0, yPos - scroller.getSize().height / 2));

            tln.setHighlight(lineNum - 1);
            tlh.setY(yPos);
            tlh.repaint();

            if(plp.open_asm != fileNum) {
                plp.open_asm = fileNum;
                plp.refreshProjectView(false);
            } else
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        tln.repaint();
                    }
                });

        }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Looks like the GUI is being refreshed too fast!\n" +
                                          "Slow down simulation speed or turn off IDE refresh in simulation options to prevent this error.", "PLPTool Error", JOptionPane.ERROR_MESSAGE);

            if(Constants.debugLevel >= 5)
                e.printStackTrace();

            if(plp.g_simrun != null)
                plp.g_simrun.stepCount = -1;
        }
    }

    public void changeFormatting() {
        java.awt.Font newFont = new java.awt.Font(Config.devFont, java.awt.Font.PLAIN, Config.devFontSize);
        txtEditor.setFont(newFont);
        txtEditor.setBackground(Config.devBackground);
        txtEditor.setForeground(Config.devForeground);
        styles = setupHighlighting();
    }

    public void notifyplpModified() {
        if(trackChanges) {
            if(Config.nothighlighting) {
                //plp.setModified();
            }
        }
    }

    public javax.swing.JTextPane getOutput() {
        return txtOutput;
    }

    public javax.swing.JEditorPane getEditor() {
        return txtEditor;
    }

    public void setEditorText(String str) {
        txtEditor.setContentType("text");
        trackChanges = false;
        if(!str.equals(txtEditor.getText())) {
            txtEditor.setText(str);
            if(Config.devSyntaxHighlighting && str.length() <= Config.filetoolarge)
                syntaxHighlight();
        }
        trackChanges = true;
        undoManager = new DevUndoManager();
        undoManager.setLimit(Config.devMaxUndoEntries);

        txtEditor.getDocument().addUndoableEditListener(new UndoableEditListener() {

            @Override
            public void undoableEditHappened(UndoableEditEvent evt) {
                undoManager.safeAddEdit(evt.getEdit());
            }
        });
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

    public void setToolbarVisible(boolean b) {
        toolbar.setVisible(b);
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
        rootmenuEdit.setEnabled(true);
        menuFindAndReplace.setEnabled(true);
        menuPrint.setEnabled(true);
        btnSave.setEnabled(true);
    }

    public final void closeProject() {
        plp.plpfile = null;
        plp.setUnModified();
        txtCurFile.setText("No file open");
        endSim();
        plp.refreshProjectView(false);
    }

    public final void disableBuildControls() {
        disableSimControls();
        menuSave.setEnabled(false);
        menuSaveAs.setEnabled(false);
        menuPrint.setEnabled(false);
        rootmenuProject.setEnabled(false);
        rootmenuEdit.setEnabled(false);
        txtEditor.setEnabled(false);
    }

    public final void disableSimControls() {
        menuSimulate.setEnabled(false);
        menuProgram.setEnabled(false);
        btnSimulate.setEnabled(false);
        btnProgram.setEnabled(false);
        menuQuickProgram.setEnabled(false);
        endSim();
     }

    public final  void enableSimControls() {
        menuSimulate.setEnabled(true);
        menuProgram.setEnabled(true);
        btnSimulate.setEnabled(true);
        btnProgram.setEnabled(true);
        menuQuickProgram.setEnabled(true);
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
                //undoManager = new DoManager(txtEditor.getText());
        }
    }

    public void openPLPFile() {
        switch(askSaveFirst("open a project", "Open a project")) {
            case 2:
                return;
            default:
                Msg.output = txtOutput;

                final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
                fc.setFileFilter(new PlpFilter());
                fc.setAcceptAllFileFilterUsed(false);
                fc.setCurrentDirectory(new File(plp.curdir));

                int retVal = fc.showOpenDialog(null);

                if(retVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                    plp.curdir = fc.getSelectedFile().getParent();
                    plp.open(fc.getSelectedFile().getAbsolutePath());
                }

                //undoManager = new DoManager(txtEditor.getText());
        }
    }

    public int savePLPFileAs() {
        Msg.output = txtOutput;

        final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setFileFilter(new PlpFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(new File(plp.curdir));

        int retVal = fc.showSaveDialog(null);

        if(retVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            plp.plpfile = new File(fc.getSelectedFile().getAbsolutePath());
            plp.curdir = fc.getSelectedFile().getParent();
            if(!plp.plpfile.getName().endsWith(".plp"))
                plp.plpfile = new File(plp.plpfile.getAbsolutePath() + ".plp");
            plp.save();
            plp.open(plp.plpfile.getAbsolutePath());
        }

        return retVal;
    }

    public int askSaveFirst(String action, String capAction) {
        int ret = javax.swing.JFileChooser.APPROVE_OPTION;

        if(plp.isModified()) {
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
                if(plp.plpfile.getName().equals("Unsaved Project"))
                    ret = this.savePLPFileAs();
                else
                    plp.save();

            if(ret == javax.swing.JFileChooser.APPROVE_OPTION)
                return n;
            else
                return 2;
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
                    Msg.E("Can not delete last source file.",
                             Constants.PLP_GENERIC_ERROR, null);

                    return Constants.PLP_GENERIC_ERROR;
                }

                String[] tokens = nodeStr.split(": ");

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
        Msg.output = txtOutput;

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
        Msg.output = txtOutput;
        int indexToExport = -1;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeProject.getLastSelectedPathComponent();

        if(node == null)
            return Constants.PLP_GENERIC_ERROR;

        if(node.isLeaf()) {
            String nodeStr = (String) node.getUserObject();

            if(nodeStr.endsWith("asm")) {
                String[] tokens = nodeStr.split(": ");
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
        Config.nothighlighting = false;
        int currpos = 0;
        String lines[] = txtEditor.getText().split("\\r?\\n");
        int doclength = lines.length;

        for(int i=0;i<doclength;i++) {
            String currline = lines[i];
            syntaxHighlight(currline, currpos, styles);
            currpos += lines[i].length() + 1;
        }
        Config.nothighlighting = true;
    }

    private void redo() {
        if(undoManager.canRedo()) {
            undoManager.safeRedo();
        }
    }

    private void undo() {
        if(undoManager.canUndo()) {
            undoManager.safeUndo();
        }
    }

    public void syntaxHighlight(int line) {
        Config.nothighlighting = false;
        try {
            String currline = txtEditor.getText().split("\\r?\\n")[line];
            int currpos = 0;
            for(int i=0;i<line;i++) {
                currpos += txtEditor.getText().split("\\r?\\n")[i].length() + 1;
            }
            syntaxHighlight(currline, currpos, setupHighlighting());
        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
        }
        Config.nothighlighting = true;
    }

    //Do not call this class without setting highlighting to true
    //Or without recording selected text
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

    //Called whenever syntax styles change
    private SimpleAttributeSet[] setupHighlighting() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(def,Config.devFont);
        StyleConstants.setFontSize(def,Config.devFontSize);
        SimpleAttributeSet[] styleSetup = new SimpleAttributeSet[11];
        for(int i=0;i<11;i++) {
            styleSetup[i] = new SimpleAttributeSet(def);
            StyleConstants.setForeground(styleSetup[i],Config.syntaxColors[i]);
            StyleConstants.setBold(styleSetup[i], Config.syntaxBold[i]);
            StyleConstants.setItalic(styleSetup[i], Config.syntaxItalic[i]);
        }
        return styleSetup;
    }

    public void catchyPLP() {
        String catchyStr;

        catchyStr =  "Progressive Learning Platform\n\n";
        catchyStr += "You can start by creating a new project or opening an existing one.\n\n";

        catchyStr += "Build: " + plptool.Version.stamp + "\n";
        catchyStr += "OS/arch: " + System.getProperty("os.name")
                      + "/" + System.getProperty("os.arch") + "\n";

        boolean savedConfig = Config.devSyntaxHighlighting;
        Config.devSyntaxHighlighting = false;
        txtEditor.setText(catchyStr);
        Config.devSyntaxHighlighting = savedConfig;
    }

    public DevUndoManager getUndoManager() {
        return undoManager;
    }

    private void assemble() {
        Msg.output = txtOutput;

        if(plp.plpfile != null)
            plp.assemble();

        if(Config.devSyntaxHighlightOnAssemble) {
            Config.nothighlighting = false;
            syntaxHighlight();
            Config.nothighlighting = true;
            Config.devSyntaxHighlightOnAssemble = false;
        }
    }

    public void beginSim() {
        if(plp.simulate() == Constants.PLP_OK) {
            txtEditor.setEditable(false);
            rootmenuSim.setEnabled(true);
            btnSimulate.setSelected(true);
            btnSimRun.setVisible(true);
            separatorSim.setVisible(true);
            btnSimReset.setVisible(true);
            btnSimStep.setVisible(true);
        } else
            endSim();
    }

    public void endSim() {
        txtEditor.setEditable(true);
        menuSimRun.setSelected(false);
        menuSimView.setSelected(false);
        menuSimWatcher.setSelected(false);
        menuSimMemory.setSelected(false);
        menuSimIO.setSelected(false);
        plp.desimulate();
        rootmenuSim.setEnabled(false);
        btnSimulate.setSelected(false);
        tln.setHighlight(-1);
        btnSimRun.setSelected(false);
        btnSimStep.setVisible(false);
        btnSimReset.setVisible(false);
        btnSimRun.setVisible(false);
        separatorSim.setVisible(false);
    }

    public void stopRunState() {
        menuSimRun.setSelected(false);
        btnSimRun.setSelected(false);
    }

    public javax.swing.JCheckBoxMenuItem getToolCheckboxMenu(int index) {
        switch(index) {
            case Constants.PLP_TOOLFRAME_IOREGISTRY:
                return menuSimIO;

            case Constants.PLP_TOOLFRAME_SIMCPU:
                return menuSimView;

            case Constants.PLP_TOOLFRAME_WATCHER:
                return menuSimWatcher;

            case Constants.PLP_TOOLFRAME_SIMRUN:
                return menuSimRun;

            case Constants.PLP_TOOLFRAME_SIMLEDS:
                return menuLEDs;

            case Constants.PLP_TOOLFRAME_SIMSWITCHES:
                return menuSwitches;

            case Constants.PLP_TOOLFRAME_SIMUART:
                return menuUART;

            case Constants.PLP_TOOLFRAME_SIMVGA:
                return menuVGA;

            case Constants.PLP_TOOLFRAME_SIMPLPID:
                return menuPLPID;

            case Constants.PLP_TOOLFRAME_SIMSEVENSEGMENTS:
                return menuSevenSegments;

            default:
                return null;
        }
    }

    /**
     * Attach listeners to the specified module frame x so it deselects the
     * corresponding control menu when closing
     *
     * @param x Module frame to attach the listener to
     * @param int Menu type requested
     */
    public void attachModuleFrameListeners(final javax.swing.JFrame x, final int menu) {
        x.addWindowListener(new java.awt.event.WindowListener() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                x.setVisible(false);
                getToolCheckboxMenu(menu).setSelected(false);
            }

            @Override public void windowOpened(java.awt.event.WindowEvent evt) { }
            @Override public void windowDeactivated(java.awt.event.WindowEvent evt) { }
            @Override public void windowActivated(java.awt.event.WindowEvent evt) { }
            @Override public void windowDeiconified(java.awt.event.WindowEvent evt) { }
            @Override public void windowIconified(java.awt.event.WindowEvent evt) { }
            @Override public void windowClosed(java.awt.event.WindowEvent evt) { }
        });
    }

    private void setLEDsFrame(boolean v) {
        for(int i = 0; i < plp.ioreg.getNumOfModsAttached(); i++) {
            plptool.PLPSimBusModule module = plp.ioreg.getModule(i);

            if(module instanceof plptool.mods.LEDArray) {
                ((JFrame)plp.ioreg.getModuleFrame(i)).setVisible(v);
                menuLEDs.setSelected(v);
                plp.updateComponents(false);
            }
        }
    }

    private void setSwitchesFrame(boolean v) {
        for(int i = 0; i < plp.ioreg.getNumOfModsAttached(); i++) {
            plptool.PLPSimBusModule module = plp.ioreg.getModule(i);

            if(module instanceof plptool.mods.Switches) {
                ((JFrame)plp.ioreg.getModuleFrame(i)).setVisible(v);
                menuSwitches.setSelected(v);
                plp.updateComponents(false);
            }
        }
    }

    private void setSevenSegmentsFrame(boolean v) {
        for(int i = 0; i < plp.ioreg.getNumOfModsAttached(); i++) {
            plptool.PLPSimBusModule module = plp.ioreg.getModule(i);

            if(module instanceof plptool.mods.SevenSegments) {
                ((JFrame)plp.ioreg.getModuleFrame(i)).setVisible(v);
                menuSevenSegments.setSelected(v);
                plp.updateComponents(false);
            }
        }
    }

    private void setUARTFrame(boolean v) {
        for(int i = 0; i < plp.ioreg.getNumOfModsAttached(); i++) {
            plptool.PLPSimBusModule module = plp.ioreg.getModule(i);

            if(module instanceof plptool.mods.UART) {
                ((JFrame)plp.ioreg.getModuleFrame(i)).setVisible(v);
                menuUART.setSelected(v);
                plp.updateComponents(false);
            }
        }
    }

    private void setVGAFrame(boolean v) {
        for(int i = 0; i < plp.ioreg.getNumOfModsAttached(); i++) {
            plptool.PLPSimBusModule module = plp.ioreg.getModule(i);

            if(module instanceof plptool.mods.VGA) {
                ((JFrame)plp.ioreg.getModuleFrame(i)).setVisible(v);
                menuVGA.setSelected(v);
                plp.updateComponents(false);
            }
        }
    }

    private void setPLPIDFrame(boolean v) {
        for(int i = 0; i < plp.ioreg.getNumOfModsAttached(); i++) {
            plptool.PLPSimBusModule module = plp.ioreg.getModule(i);

            if(module instanceof plptool.mods.PLPID) {
                ((JFrame)plp.ioreg.getModuleFrame(i)).setVisible(v);
                menuPLPID.setSelected(v);
                plp.updateComponents(false);
            }
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

        devMainPane = new javax.swing.JPanel();
        splitterV = new javax.swing.JSplitPane();
        splitterH = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeProject = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        txtCurFile = new javax.swing.JLabel();
        scroller = new javax.swing.JScrollPane();
        txtEditor = new javax.swing.JTextPane();
        lblPosition = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JTextPane();
        toolbar = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnAssemble = new javax.swing.JButton();
        btnSimulate = new javax.swing.JToggleButton();
        btnProgram = new javax.swing.JButton();
        separatorSim = new javax.swing.JToolBar.Separator();
        btnSimStep = new javax.swing.JButton();
        btnSimRun = new javax.swing.JToggleButton();
        btnSimReset = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        rootmenuFile = new javax.swing.JMenu();
        menuNew = new javax.swing.JMenuItem();
        menuSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuOpen = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        menuSaveAs = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        menuPrint = new javax.swing.JMenuItem();
        menuSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        rootmenuEdit = new javax.swing.JMenu();
        menuCopy = new javax.swing.JMenuItem();
        menuCut = new javax.swing.JMenuItem();
        menuPaste = new javax.swing.JMenuItem();
        menuSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuFindAndReplace = new javax.swing.JMenuItem();
        menuSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuUndo = new javax.swing.JMenuItem();
        menuRedo = new javax.swing.JMenuItem();
        rootmenuProject = new javax.swing.JMenu();
        menuAssemble = new javax.swing.JMenuItem();
        menuSimulate = new javax.swing.JMenuItem();
        menuProgram = new javax.swing.JMenuItem();
        menuQuickProgram = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuNewASM = new javax.swing.JMenuItem();
        menuImportASM = new javax.swing.JMenuItem();
        menuExportASM = new javax.swing.JMenuItem();
        menuDeleteASM = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuSetMainProgram = new javax.swing.JMenuItem();
        rootmenuTools = new javax.swing.JMenu();
        menuOptions = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        menuSerialTerminal = new javax.swing.JMenuItem();
        rootmenuSim = new javax.swing.JMenu();
        menuSimStep = new javax.swing.JMenuItem();
        menuSimReset = new javax.swing.JMenuItem();
        menuSimRun = new javax.swing.JCheckBoxMenuItem();
        menuStepSize = new javax.swing.JMenu();
        menuStep1 = new javax.swing.JRadioButtonMenuItem();
        menuStep2 = new javax.swing.JRadioButtonMenuItem();
        menuStep3 = new javax.swing.JRadioButtonMenuItem();
        menuStep4 = new javax.swing.JRadioButtonMenuItem();
        menuStep5 = new javax.swing.JRadioButtonMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        menuSimTools = new javax.swing.JMenu();
        menuSimView = new javax.swing.JCheckBoxMenuItem();
        menuSimWatcher = new javax.swing.JCheckBoxMenuItem();
        menuSimMemory = new javax.swing.JCheckBoxMenuItem();
        menuSimIO = new javax.swing.JCheckBoxMenuItem();
        menuIOReg = new javax.swing.JMenu();
        menuLEDs = new javax.swing.JCheckBoxMenuItem();
        menuSwitches = new javax.swing.JCheckBoxMenuItem();
        menuSevenSegments = new javax.swing.JCheckBoxMenuItem();
        menuUART = new javax.swing.JCheckBoxMenuItem();
        menuVGA = new javax.swing.JCheckBoxMenuItem();
        menuPLPID = new javax.swing.JCheckBoxMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        menuExitSim = new javax.swing.JMenuItem();
        rootmenuHelp = new javax.swing.JMenu();
        menuQuickRef = new javax.swing.JMenuItem();
        menuManual = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuIssues = new javax.swing.JMenuItem();
        menuIssuesPage = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
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

        devMainPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        devMainPane.setName("devMainPane"); // NOI18N

        splitterV.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        splitterV.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitterV.setResizeWeight(0.7);
        splitterV.setName("splitterV"); // NOI18N

        splitterH.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        splitterH.setName("splitterH"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        treeProject.setName("treeProject"); // NOI18N
        treeProject.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeProjectMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeProjectMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(treeProject);

        splitterH.setLeftComponent(jScrollPane2);

        jPanel1.setName("jPanel1"); // NOI18N

        txtCurFile.setFont(resourceMap.getFont("txtCurFile.font")); // NOI18N
        txtCurFile.setText(resourceMap.getString("txtCurFile.text")); // NOI18N
        txtCurFile.setName("txtCurFile"); // NOI18N

        scroller.setName("scroller"); // NOI18N

        txtEditor.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        txtEditor.setFont(resourceMap.getFont("txtEditor.font")); // NOI18N
        txtEditor.setEnabled(false);
        txtEditor.setName("txtEditor"); // NOI18N
        txtEditor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtEditorMousePressed(evt);
            }
        });
        txtEditor.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtEditorCaretUpdate(evt);
            }
        });
        txtEditor.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                txtEditorCaretPositionChanged(evt);
            }
        });
        txtEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEditorKeyTyped(evt);
            }
        });
        scroller.setViewportView(txtEditor);

        lblPosition.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPosition.setText(resourceMap.getString("lblPosition.text")); // NOI18N
        lblPosition.setName("lblPosition"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroller, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtCurFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 445, Short.MAX_VALUE)
                .addComponent(lblPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCurFile)
                    .addComponent(lblPosition))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroller, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
        );

        splitterH.setRightComponent(jPanel1);

        splitterV.setTopComponent(splitterH);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtOutput.setFont(resourceMap.getFont("txtOutput.font")); // NOI18N
        txtOutput.setName("txtOutput"); // NOI18N
        jScrollPane1.setViewportView(txtOutput);

        splitterV.setRightComponent(jScrollPane1);

        javax.swing.GroupLayout devMainPaneLayout = new javax.swing.GroupLayout(devMainPane);
        devMainPane.setLayout(devMainPaneLayout);
        devMainPaneLayout.setHorizontalGroup(
            devMainPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitterV, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
        );
        devMainPaneLayout.setVerticalGroup(
            devMainPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitterV, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
        );

        getContentPane().add(devMainPane, java.awt.BorderLayout.CENTER);

        toolbar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setName("toolbar"); // NOI18N

        btnNew.setIcon(resourceMap.getIcon("btnNew.icon")); // NOI18N
        btnNew.setText(resourceMap.getString("btnNew.text")); // NOI18N
        btnNew.setToolTipText(resourceMap.getString("btnNew.toolTipText")); // NOI18N
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnNew.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnNew.setMinimumSize(new java.awt.Dimension(42, 46));
        btnNew.setName("btnNew"); // NOI18N
        btnNew.setPreferredSize(new java.awt.Dimension(42, 46));
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        toolbar.add(btnNew);

        btnOpen.setIcon(resourceMap.getIcon("btnOpen.icon")); // NOI18N
        btnOpen.setText(resourceMap.getString("btnOpen.text")); // NOI18N
        btnOpen.setToolTipText(resourceMap.getString("btnOpen.toolTipText")); // NOI18N
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnOpen.setName("btnOpen"); // NOI18N
        btnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        toolbar.add(btnOpen);

        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setToolTipText(resourceMap.getString("btnSave.toolTipText")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        toolbar.add(btnSave);

        jSeparator4.setName("jSeparator4"); // NOI18N
        toolbar.add(jSeparator4);

        btnAssemble.setIcon(resourceMap.getIcon("btnAssemble.icon")); // NOI18N
        btnAssemble.setText(resourceMap.getString("btnAssemble.text")); // NOI18N
        btnAssemble.setToolTipText(resourceMap.getString("btnAssemble.toolTipText")); // NOI18N
        btnAssemble.setFocusable(false);
        btnAssemble.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAssemble.setMargin(new java.awt.Insets(2, 0, 2, 0));
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
        btnSimulate.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnSimulate.setName("btnSimulate"); // NOI18N
        btnSimulate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSimulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimulateActionPerformed(evt);
            }
        });
        toolbar.add(btnSimulate);

        btnProgram.setIcon(resourceMap.getIcon("btnProgram.icon")); // NOI18N
        btnProgram.setText(resourceMap.getString("btnProgram.text")); // NOI18N
        btnProgram.setToolTipText(resourceMap.getString("btnProgram.toolTipText")); // NOI18N
        btnProgram.setFocusable(false);
        btnProgram.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProgram.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnProgram.setName("btnProgram"); // NOI18N
        btnProgram.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProgramActionPerformed(evt);
            }
        });
        toolbar.add(btnProgram);

        separatorSim.setName("separatorSim"); // NOI18N
        toolbar.add(separatorSim);

        btnSimStep.setIcon(resourceMap.getIcon("btnSimStep.icon")); // NOI18N
        btnSimStep.setText(resourceMap.getString("btnSimStep.text")); // NOI18N
        btnSimStep.setToolTipText(resourceMap.getString("btnSimStep.toolTipText")); // NOI18N
        btnSimStep.setFocusable(false);
        btnSimStep.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSimStep.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnSimStep.setName("btnSimStep"); // NOI18N
        btnSimStep.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSimStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimStepActionPerformed(evt);
            }
        });
        toolbar.add(btnSimStep);

        btnSimRun.setIcon(resourceMap.getIcon("btnSimRun.icon")); // NOI18N
        btnSimRun.setText(resourceMap.getString("btnSimRun.text")); // NOI18N
        btnSimRun.setToolTipText(resourceMap.getString("btnSimRun.toolTipText")); // NOI18N
        btnSimRun.setFocusable(false);
        btnSimRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSimRun.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnSimRun.setName("btnSimRun"); // NOI18N
        btnSimRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSimRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimRunActionPerformed(evt);
            }
        });
        toolbar.add(btnSimRun);

        btnSimReset.setIcon(resourceMap.getIcon("btnSimReset.icon")); // NOI18N
        btnSimReset.setText(resourceMap.getString("btnSimReset.text")); // NOI18N
        btnSimReset.setToolTipText(resourceMap.getString("btnSimReset.toolTipText")); // NOI18N
        btnSimReset.setFocusable(false);
        btnSimReset.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSimReset.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnSimReset.setName("btnSimReset"); // NOI18N
        btnSimReset.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSimReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimResetActionPerformed(evt);
            }
        });
        toolbar.add(btnSimReset);

        getContentPane().add(toolbar, java.awt.BorderLayout.PAGE_START);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        rootmenuFile.setText(resourceMap.getString("rootmenuFile.text")); // NOI18N
        rootmenuFile.setName("rootmenuFile"); // NOI18N

        menuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuNew.setIcon(resourceMap.getIcon("menuNew.icon")); // NOI18N
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
        menuOpen.setIcon(resourceMap.getIcon("menuOpen.icon")); // NOI18N
        menuOpen.setText(resourceMap.getString("menuOpen.text")); // NOI18N
        menuOpen.setName("menuOpen"); // NOI18N
        menuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuOpen);

        menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuSave.setIcon(resourceMap.getIcon("menuSave.icon")); // NOI18N
        menuSave.setText(resourceMap.getString("menuSave.text")); // NOI18N
        menuSave.setName("menuSave"); // NOI18N
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuSave);

        menuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuSaveAs.setText(resourceMap.getString("menuSaveAs.text")); // NOI18N
        menuSaveAs.setName("menuSaveAs"); // NOI18N
        menuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveAsActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuSaveAs);

        jSeparator8.setName("jSeparator8"); // NOI18N
        rootmenuFile.add(jSeparator8);

        menuPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        menuPrint.setText(resourceMap.getString("menuPrint.text")); // NOI18N
        menuPrint.setName("menuPrint"); // NOI18N
        menuPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPrintActionPerformed(evt);
            }
        });
        rootmenuFile.add(menuPrint);

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

        menuFindAndReplace.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menuFindAndReplace.setText(resourceMap.getString("menuFindAndReplace.text")); // NOI18N
        menuFindAndReplace.setName("menuFindAndReplace"); // NOI18N
        menuFindAndReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFindAndReplaceActionPerformed(evt);
            }
        });
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
        rootmenuProject.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rootmenuProjectMouseClicked(evt);
            }
        });
        rootmenuProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootmenuProjectActionPerformed(evt);
            }
        });

        menuAssemble.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        menuAssemble.setIcon(resourceMap.getIcon("menuAssemble.icon")); // NOI18N
        menuAssemble.setText(resourceMap.getString("menuAssemble.text")); // NOI18N
        menuAssemble.setName("menuAssemble"); // NOI18N
        menuAssemble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAssembleActionPerformed1(evt);
            }
        });
        rootmenuProject.add(menuAssemble);

        menuSimulate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        menuSimulate.setIcon(resourceMap.getIcon("menuSimulate.icon")); // NOI18N
        menuSimulate.setText(resourceMap.getString("menuSimulate.text")); // NOI18N
        menuSimulate.setName("menuSimulate"); // NOI18N
        menuSimulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSimulateActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuSimulate);

        menuProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        menuProgram.setIcon(resourceMap.getIcon("menuProgram.icon")); // NOI18N
        menuProgram.setText(resourceMap.getString("menuProgram.text")); // NOI18N
        menuProgram.setName("menuProgram"); // NOI18N
        menuProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuProgramActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuProgram);

        menuQuickProgram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.SHIFT_MASK));
        menuQuickProgram.setText(resourceMap.getString("menuQuickProgram.text")); // NOI18N
        menuQuickProgram.setName("menuQuickProgram"); // NOI18N
        menuQuickProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuQuickProgramActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuQuickProgram);

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

        menuSetMainProgram.setText(resourceMap.getString("menuSetMainProgram.text")); // NOI18N
        menuSetMainProgram.setName("menuSetMainProgram"); // NOI18N
        menuSetMainProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSetMainProgramActionPerformed(evt);
            }
        });
        rootmenuProject.add(menuSetMainProgram);

        jMenuBar1.add(rootmenuProject);

        rootmenuTools.setText(resourceMap.getString("rootmenuTools.text")); // NOI18N
        rootmenuTools.setName("rootmenuTools"); // NOI18N

        menuOptions.setText(resourceMap.getString("menuOptions.text")); // NOI18N
        menuOptions.setName("menuOptions"); // NOI18N
        menuOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOptionsActionPerformed(evt);
            }
        });
        rootmenuTools.add(menuOptions);

        jSeparator7.setName("jSeparator7"); // NOI18N
        rootmenuTools.add(jSeparator7);

        menuSerialTerminal.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        menuSerialTerminal.setText(resourceMap.getString("menuSerialTerminal.text")); // NOI18N
        menuSerialTerminal.setName("menuSerialTerminal"); // NOI18N
        menuSerialTerminal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSerialTerminalActionPerformed(evt);
            }
        });
        rootmenuTools.add(menuSerialTerminal);

        jMenuBar1.add(rootmenuTools);

        rootmenuSim.setText(resourceMap.getString("rootmenuSim.text")); // NOI18N
        rootmenuSim.setName("rootmenuSim"); // NOI18N

        menuSimStep.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuSimStep.setText(resourceMap.getString("menuSimStep.text")); // NOI18N
        menuSimStep.setName("menuSimStep"); // NOI18N
        menuSimStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSimStepActionPerformed(evt);
            }
        });
        rootmenuSim.add(menuSimStep);

        menuSimReset.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        menuSimReset.setText(resourceMap.getString("menuSimReset.text")); // NOI18N
        menuSimReset.setName("menuSimReset"); // NOI18N
        menuSimReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSimResetActionPerformed(evt);
            }
        });
        rootmenuSim.add(menuSimReset);

        menuSimRun.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        menuSimRun.setText(resourceMap.getString("menuSimRun.text")); // NOI18N
        menuSimRun.setName("menuSimRun"); // NOI18N
        menuSimRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSimRunActionPerformed(evt);
            }
        });
        rootmenuSim.add(menuSimRun);

        menuStepSize.setText(resourceMap.getString("menuStepSize.text")); // NOI18N
        menuStepSize.setName("menuStepSize"); // NOI18N

        menuStep1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.ALT_MASK));
        menuStep1.setSelected(true);
        menuStep1.setText(resourceMap.getString("menuStep1.text")); // NOI18N
        menuStep1.setName("menuStep1"); // NOI18N
        menuStepSize.add(menuStep1);

        menuStep2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.ALT_MASK));
        menuStep2.setText(resourceMap.getString("menuStep2.text")); // NOI18N
        menuStep2.setName("menuStep2"); // NOI18N
        menuStepSize.add(menuStep2);

        menuStep3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.ALT_MASK));
        menuStep3.setText(resourceMap.getString("menuStep3.text")); // NOI18N
        menuStep3.setName("menuStep3"); // NOI18N
        menuStepSize.add(menuStep3);

        menuStep4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.ALT_MASK));
        menuStep4.setText(resourceMap.getString("menuStep4.text")); // NOI18N
        menuStep4.setName("menuStep4"); // NOI18N
        menuStepSize.add(menuStep4);

        menuStep5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.ALT_MASK));
        menuStep5.setText(resourceMap.getString("menuStep5.text")); // NOI18N
        menuStep5.setName("menuStep5"); // NOI18N
        menuStepSize.add(menuStep5);

        rootmenuSim.add(menuStepSize);

        jSeparator9.setName("jSeparator9"); // NOI18N
        rootmenuSim.add(jSeparator9);

        menuSimTools.setText(resourceMap.getString("menuSimTools.text")); // NOI18N
        menuSimTools.setName("menuSimTools"); // NOI18N

        menuSimView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuSimView.setText(resourceMap.getString("menuSimView.text")); // NOI18N
        menuSimView.setName("menuSimView"); // NOI18N
        menuSimView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSimViewActionPerformed(evt);
            }
        });
        menuSimTools.add(menuSimView);

        menuSimWatcher.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuSimWatcher.setText(resourceMap.getString("menuSimWatcher.text")); // NOI18N
        menuSimWatcher.setName("menuSimWatcher"); // NOI18N
        menuSimWatcher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSimWatcherActionPerformed(evt);
            }
        });
        menuSimTools.add(menuSimWatcher);

        menuSimMemory.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuSimMemory.setText(resourceMap.getString("menuSimMemory.text")); // NOI18N
        menuSimMemory.setName("menuSimMemory"); // NOI18N
        menuSimTools.add(menuSimMemory);

        menuSimIO.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuSimIO.setText(resourceMap.getString("menuSimIO.text")); // NOI18N
        menuSimIO.setName("menuSimIO"); // NOI18N
        menuSimIO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSimIOActionPerformed(evt);
            }
        });
        menuSimTools.add(menuSimIO);

        rootmenuSim.add(menuSimTools);

        menuIOReg.setText(resourceMap.getString("menuIOReg.text")); // NOI18N
        menuIOReg.setName("menuIOReg"); // NOI18N

        menuLEDs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        menuLEDs.setText(resourceMap.getString("menuLEDs.text")); // NOI18N
        menuLEDs.setName("menuLEDs"); // NOI18N
        menuLEDs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLEDsActionPerformed(evt);
            }
        });
        menuIOReg.add(menuLEDs);

        menuSwitches.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        menuSwitches.setText(resourceMap.getString("menuSwitches.text")); // NOI18N
        menuSwitches.setName("menuSwitches"); // NOI18N
        menuSwitches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSwitchesActionPerformed(evt);
            }
        });
        menuIOReg.add(menuSwitches);

        menuSevenSegments.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        menuSevenSegments.setText(resourceMap.getString("menuSevenSegments.text")); // NOI18N
        menuSevenSegments.setName("menuSevenSegments"); // NOI18N
        menuSevenSegments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSevenSegmentsActionPerformed(evt);
            }
        });
        menuIOReg.add(menuSevenSegments);

        menuUART.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_MASK));
        menuUART.setText(resourceMap.getString("menuUART.text")); // NOI18N
        menuUART.setName("menuUART"); // NOI18N
        menuUART.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuUARTActionPerformed(evt);
            }
        });
        menuIOReg.add(menuUART);

        menuVGA.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
        menuVGA.setText(resourceMap.getString("menuVGA.text")); // NOI18N
        menuVGA.setName("menuVGA"); // NOI18N
        menuVGA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuVGAActionPerformed(evt);
            }
        });
        menuIOReg.add(menuVGA);

        menuPLPID.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_MASK));
        menuPLPID.setText(resourceMap.getString("menuPLPID.text")); // NOI18N
        menuPLPID.setName("menuPLPID"); // NOI18N
        menuPLPID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPLPIDActionPerformed(evt);
            }
        });
        menuIOReg.add(menuPLPID);

        rootmenuSim.add(menuIOReg);

        jSeparator10.setName("jSeparator10"); // NOI18N
        rootmenuSim.add(jSeparator10);

        menuExitSim.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        menuExitSim.setText(resourceMap.getString("menuExitSim.text")); // NOI18N
        menuExitSim.setName("menuExitSim"); // NOI18N
        menuExitSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitSimActionPerformed(evt);
            }
        });
        rootmenuSim.add(menuExitSim);

        jMenuBar1.add(rootmenuSim);

        rootmenuHelp.setText(resourceMap.getString("rootmenuHelp.text")); // NOI18N
        rootmenuHelp.setName("rootmenuHelp"); // NOI18N
        rootmenuHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootmenuHelpActionPerformed(evt);
            }
        });

        menuQuickRef.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuQuickRef.setText(resourceMap.getString("menuQuickRef.text")); // NOI18N
        menuQuickRef.setName("menuQuickRef"); // NOI18N
        menuQuickRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuQuickRefActionPerformed(evt);
            }
        });
        rootmenuHelp.add(menuQuickRef);

        menuManual.setText(resourceMap.getString("menuManual.text")); // NOI18N
        menuManual.setName("menuManual"); // NOI18N
        menuManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuManualActionPerformed(evt);
            }
        });
        rootmenuHelp.add(menuManual);

        jSeparator5.setName("jSeparator5"); // NOI18N
        rootmenuHelp.add(jSeparator5);

        menuIssues.setText(resourceMap.getString("menuIssues.text")); // NOI18N
        menuIssues.setName("menuIssues"); // NOI18N
        menuIssues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuIssuesActionPerformed(evt);
            }
        });
        rootmenuHelp.add(menuIssues);

        menuIssuesPage.setText(resourceMap.getString("menuIssuesPage.text")); // NOI18N
        menuIssuesPage.setName("menuIssuesPage"); // NOI18N
        menuIssuesPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuIssuesPageActionPerformed(evt);
            }
        });
        rootmenuHelp.add(menuIssuesPage);

        jSeparator6.setName("jSeparator6"); // NOI18N
        rootmenuHelp.add(jSeparator6);

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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        exit();
    }//GEN-LAST:event_menuExitActionPerformed

    private void rootmenuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootmenuHelpActionPerformed

    }//GEN-LAST:event_rootmenuHelpActionPerformed

    private void menuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenActionPerformed
        openPLPFile();
    }//GEN-LAST:event_menuOpenActionPerformed

    private void menuSimulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSimulateActionPerformed
        if(plp.asm.isAssembled()) {
            beginSim();
        }
    }//GEN-LAST:event_menuSimulateActionPerformed

    private void menuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewActionPerformed
        newPLPFile();
        if(Config.devSyntaxHighlighting)
            syntaxHighlight();
    }//GEN-LAST:event_menuNewActionPerformed

    private void menuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
        about();
    }//GEN-LAST:event_menuAboutActionPerformed

    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
        if(plp.save() == Constants.PLP_FILE_USE_SAVE_AS)
            savePLPFileAs();
    }//GEN-LAST:event_menuSaveActionPerformed

    private void menuAssembleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAssembleActionPerformed
        Msg.output = txtOutput;

        if(plp.plpfile != null)
            plp.assemble();

        Config.nothighlighting = false;
        syntaxHighlight();
        Config.nothighlighting = true;
}//GEN-LAST:event_menuAssembleActionPerformed

    private void menuAssembleActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAssembleActionPerformed1
        assemble();
    }//GEN-LAST:event_menuAssembleActionPerformed1

    private void menuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveAsActionPerformed
        savePLPFileAs();
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
        if(plp.open_asm != 0)
            plp.setMainAsm(plp.open_asm);
    }//GEN-LAST:event_menuSetMainProgramActionPerformed

    private void btnAssembleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssembleActionPerformed
        assemble();
    }//GEN-LAST:event_btnAssembleActionPerformed

    private void menuExportASMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExportASMActionPerformed
        exportASM();
    }//GEN-LAST:event_menuExportASMActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        Msg.output = txtOutput;
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
        //undoManager.modify("delete", "", txtEditor.getSelectionStart(), txtEditor.getSelectionEnd(), txtEditor.getSelectionStart(), txtEditor.getText().length());
        txtEditor.cut();
    }//GEN-LAST:event_menuCutActionPerformed

    private void menuPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPasteActionPerformed
        txtEditor.paste();
        Config.devSyntaxHighlightOnAssemble = true;
    }//GEN-LAST:event_menuPasteActionPerformed

    private void menuUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuUndoActionPerformed
        undo();
    }//GEN-LAST:event_menuUndoActionPerformed

    private void menuRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRedoActionPerformed
        redo();
    }//GEN-LAST:event_menuRedoActionPerformed

    private void txtEditorCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtEditorCaretPositionChanged
        
    }//GEN-LAST:event_txtEditorCaretPositionChanged

    private void treeProjectMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeProjectMousePressed
        if(evt.getClickCount() == 2) { // user double clicked the project tree
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeProject.getLastSelectedPathComponent();

            if(node == null)
                return;

            if(node.isLeaf()) {
                String nodeStr = (String) node.getUserObject();

                if(nodeStr.endsWith("asm")) {

                    String[] tokens = nodeStr.split(": ");

                    Msg.I("Opening " + nodeStr, null);

                    plp.updateAsm(plp.open_asm, txtEditor.getText());
                    plp.open_asm = Integer.parseInt(tokens[0]);
                    plp.refreshProjectView(false);
                    if (Config.devSyntaxHighlighting) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                syntaxHighlight();
                            }
                        });
                    }
                }
            }
        } else if(plp.plpfile != null && evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            popupProject.show(treeProject, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_treeProjectMousePressed

    private void txtEditorCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtEditorCaretUpdate
        if(plp.asms != null && plp.asms.size() > 0) {
            int caretPos = txtEditor.getCaretPosition();
            Element root = txtEditor.getDocument().getDefaultRootElement();

            int line = root.getElementIndex(caretPos)+1;
            //line = txtEditor.getText().substring(0, caretPos).split("\\r?\\n").length;
            String fName = plp.asms.get(plp.open_asm).getAsmFilePath();
            txtCurFile.setText(fName + ":" + line + (plp.open_asm == 0 ? " <main program>" : ""));
        }
    }//GEN-LAST:event_txtEditorCaretUpdate

    private void treeProjectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeProjectMouseClicked

    }//GEN-LAST:event_treeProjectMouseClicked

    private void rootmenuProjectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rootmenuProjectMouseClicked
    }//GEN-LAST:event_rootmenuProjectMouseClicked

    private void txtEditorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtEditorMousePressed
        if(plp.plpfile != null && evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            popupEdit.show(txtEditor, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_txtEditorMousePressed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        menuNewActionPerformed(evt);
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        menuOpenActionPerformed(evt);
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        menuSaveActionPerformed(evt);
    }//GEN-LAST:event_btnSaveActionPerformed

    private boolean deleteOccured;

    private void txtEditorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEditorKeyTyped
        
        deleteOccured = false;
        boolean modified = false;

        if((int)evt.getKeyChar() == 10 || (int)evt.getKeyChar() > 31 && (int)evt.getKeyChar() < 127) {
            deleteOccured = (txtEditor.getSelectedText() != null) || (txtEditor.getSelectedText() != null && !txtEditor.getSelectedText().equals(""));
            modified = true;
        } else if ((int)evt.getKeyChar() == 127) {
            deleteOccured = true;
            modified = true;
        } else if ((int)evt.getKeyChar() == 8) {
            deleteOccured = true;
            modified = true;
        } else if ((int)evt.getKeyChar() == 24) {
            deleteOccured = true;
            modified = true;
        } else if ((int)evt.getKeyChar() == 22) {
            deleteOccured = (txtEditor.getSelectedText() == null) || (txtEditor.getSelectedText() != null && !txtEditor.getSelectedText().equals(""));
            Config.devSyntaxHighlightOnAssemble = true;
            modified = true;
        }

        if(modified) {
            Msg.D("Text has been modified.", 5, this);
            plp.setModified();

            if(txtEditor.isEditable()) {
                disableSimControls();
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(Config.devSyntaxHighlighting && !deleteOccured) {
                    Config.nothighlighting = false;
                    int caretPos = txtEditor.getCaretPosition();
                    syntaxHighlight(txtEditor.getText().substring(0, caretPos).split("\\r?\\n").length-1);
                    txtEditor.setCaretPosition(caretPos);
                    Config.nothighlighting = true;
                }
            }
        });
    }//GEN-LAST:event_txtEditorKeyTyped

    private void menuQuickProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuQuickProgramActionPerformed
        plp.g_prg.program();
        plp.g_prg.setVisible(true);
    }//GEN-LAST:event_menuQuickProgramActionPerformed

    private void btnProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProgramActionPerformed
        menuProgramActionPerformed(null);
    }//GEN-LAST:event_btnProgramActionPerformed

    private void menuQuickRefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuQuickRefActionPerformed
        plp.g_qref.setVisible(true);
    }//GEN-LAST:event_menuQuickRefActionPerformed

    private void menuManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuManualActionPerformed
        Desktop desktop = Desktop.getDesktop();
        if(desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                URI uri = new URI(Constants.manualURI);
                desktop.browse(uri);
            } catch(Exception e) {}
        }
    }//GEN-LAST:event_menuManualActionPerformed

    private void menuIssuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuIssuesActionPerformed
        Desktop desktop = Desktop.getDesktop();
        if(desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                URI uri = new URI(Constants.issueEntryURI);
                desktop.browse(uri);
            } catch(Exception e) {}
        }
    }//GEN-LAST:event_menuIssuesActionPerformed

    private void menuIssuesPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuIssuesPageActionPerformed
        Desktop desktop = Desktop.getDesktop();
        if(desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                URI uri = new URI(Constants.issueTrackerURI);
                desktop.browse(uri);
            } catch(Exception e) {}
        }
    }//GEN-LAST:event_menuIssuesPageActionPerformed

    private void menuFindAndReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFindAndReplaceActionPerformed
        plp.g_find.setCurIndex(this.txtEditor.getCaretPosition());
        plp.g_find.setVisible(false);
        plp.g_find.setVisible(true);
    }//GEN-LAST:event_menuFindAndReplaceActionPerformed

    private void menuOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOptionsActionPerformed
        plp.g_opts.getTabs().setSelectedIndex(0);

        plp.g_opts.setVisible(false);
        plp.g_opts.setVisible(true);
    }//GEN-LAST:event_menuOptionsActionPerformed

    private void menuSerialTerminalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSerialTerminalActionPerformed
        if(plp.term == null)
            plp.term = new SerialTerminal(false);

        plp.term.setVisible(false);
        plp.term.setVisible(true);
    }//GEN-LAST:event_menuSerialTerminalActionPerformed

    private void menuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPrintActionPerformed
        try {
            txtEditor.print();
        } catch(Exception e) {
            Msg.E("Failed to print currently open file.", Constants.PLP_PRINT_ERROR, this);
        }
    }//GEN-LAST:event_menuPrintActionPerformed

    private void menuSimStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSimStepActionPerformed
        plp.sim.step();
        plp.updateComponents(true);
    }//GEN-LAST:event_menuSimStepActionPerformed

    private void menuExitSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitSimActionPerformed
        endSim();
    }//GEN-LAST:event_menuExitSimActionPerformed

    private void menuSimRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSimRunActionPerformed
        if(menuSimRun.isSelected()) {
            plp.g_simrun = new plptool.gui.SimRunner(plp);
            plp.g_simrun.start();
            menuSimRun.setSelected(true);
            btnSimRun.setSelected(true);
        } else {
            if(plp.g_simrun != null) {
                try {
                    plp.g_simrun.stepCount = 0;
                } catch(Exception e) {}
            }
            menuSimRun.setSelected(false);
            btnSimRun.setSelected(false);
        }
    }//GEN-LAST:event_menuSimRunActionPerformed

    private void menuSimIOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSimIOActionPerformed
        if(menuSimIO.isSelected()) {
            if(plp.g_ioreg == null) {
                plp.g_ioreg = new IORegistryFrame(plp);
                //plp.g_simsh.getSimDesktop().add(plp.g_ioreg);
            }

            plp.g_ioreg.setVisible(true);
        } else {
            if(plp.g_ioreg != null)
                plp.g_ioreg.setVisible(false);
        }
    }//GEN-LAST:event_menuSimIOActionPerformed

    private void menuSimWatcherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSimWatcherActionPerformed
        if(menuSimWatcher.isSelected()) {
            if(plp.g_watcher == null) {
                plp.g_watcher = new Watcher(plp);
                attachModuleFrameListeners(plp.g_watcher, Constants.PLP_TOOLFRAME_WATCHER);
            }

            plp.g_watcher.setVisible(true);
        } else {
            if(plp.g_watcher != null)
                plp.g_watcher.setVisible(false);
        }
    }//GEN-LAST:event_menuSimWatcherActionPerformed

    private void menuSimViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSimViewActionPerformed
        if(menuSimView.isSelected()) {
            plp.g_sim.setVisible(true);
        } else {
            plp.g_sim.setVisible(false);
        }
    }//GEN-LAST:event_menuSimViewActionPerformed

    private void menuSimResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSimResetActionPerformed
        if(plp.g_simrun != null)
            plp.g_simrun.stepCount = -1;
        plp.sim.reset();
        
        plp.updateComponents(true);
        plp.refreshProjectView(false);
    }//GEN-LAST:event_menuSimResetActionPerformed

    private void menuLEDsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLEDsActionPerformed
        setLEDsFrame(menuLEDs.isSelected());
    }//GEN-LAST:event_menuLEDsActionPerformed

    private void menuSwitchesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSwitchesActionPerformed
        setSwitchesFrame(menuSwitches.isSelected());

    }//GEN-LAST:event_menuSwitchesActionPerformed

    private void menuSevenSegmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSevenSegmentsActionPerformed
        setSevenSegmentsFrame(menuSevenSegments.isSelected());
    }//GEN-LAST:event_menuSevenSegmentsActionPerformed

    private void menuUARTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuUARTActionPerformed
        setUARTFrame(menuUART.isSelected());
    }//GEN-LAST:event_menuUARTActionPerformed

    private void menuVGAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuVGAActionPerformed
        setVGAFrame(menuVGA.isSelected());
    }//GEN-LAST:event_menuVGAActionPerformed

    private void menuPLPIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPLPIDActionPerformed
        for(int i = 0; i < plp.ioreg.getNumOfModsAttached(); i++) {
            plptool.PLPSimBusModule module = plp.ioreg.getModule(i);

            if(module instanceof plptool.mods.PLPID) {
                ((JFrame)plp.ioreg.getModuleFrame(i)).setVisible(menuPLPID.isSelected());
                plp.updateComponents(false);
            }
        }
    }//GEN-LAST:event_menuPLPIDActionPerformed

    private void btnSimulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimulateActionPerformed
        if(btnSimulate.isSelected()) {
            if(plp.isAssembled())
                beginSim();
        } else
            endSim();
    }//GEN-LAST:event_btnSimulateActionPerformed

    private void btnSimRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimRunActionPerformed
        if(btnSimRun.isSelected()) {
            plp.g_simrun = new plptool.gui.SimRunner(plp);
            plp.g_simrun.start();
            menuSimRun.setSelected(true);
            btnSimRun.setSelected(true);
        } else {
            if(plp.g_simrun != null) {
                try {
                    plp.g_simrun.stepCount = 0;
                } catch(Exception e) {}
            }
            menuSimRun.setSelected(false);
            btnSimRun.setSelected(false);
        }
    }//GEN-LAST:event_btnSimRunActionPerformed

    private void btnSimStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimStepActionPerformed
        plp.sim.step();
        plp.updateComponents(true);
    }//GEN-LAST:event_btnSimStepActionPerformed

    private void btnSimResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimResetActionPerformed
        if(plp.g_simrun != null)
            plp.g_simrun.stepCount = -1;
        plp.sim.reset();

        plp.updateComponents(true);
        plp.refreshProjectView(false);
    }//GEN-LAST:event_btnSimResetActionPerformed

    private void initPopupMenus() {
        popupmenuNewASM = new javax.swing.JMenuItem();
        popupmenuNewASM.setText("New ASM file..."); // NOI18N
        popupmenuNewASM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewASMActionPerformed(evt);
            }
        });

        popupmenuImportASM = new javax.swing.JMenuItem();
        popupmenuImportASM.setText("Import ASM file..."); // NOI18N
        popupmenuImportASM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuImportASMActionPerformed(evt);
            }
        });

        popupmenuExportASM = new javax.swing.JMenuItem();
        popupmenuExportASM.setText("Export selected ASM file..."); // NOI18N
        popupmenuExportASM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExportASMActionPerformed(evt);
            }
        });

        popupmenuDeleteASM = new javax.swing.JMenuItem();
        popupmenuDeleteASM.setText("Remove selected ASM file"); // NOI18N
        popupmenuDeleteASM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeleteASMActionPerformed(evt);
            }
        });

        popupProject = new javax.swing.JPopupMenu();
        popupProject.add(popupmenuNewASM);
        popupProject.add(popupmenuImportASM);
        popupProject.add(popupmenuExportASM);
        popupProject.add(popupmenuDeleteASM);


        popupmenuCopy = new javax.swing.JMenuItem();
        popupmenuCopy.setText("Copy"); // NOI18N
        popupmenuCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCopyActionPerformed(evt);
            }
        });

        popupmenuCut = new javax.swing.JMenuItem();
        popupmenuCut.setText("Cut"); // NOI18N
        popupmenuCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCutActionPerformed(evt);
            }
        });

        popupmenuPaste = new javax.swing.JMenuItem();
        popupmenuPaste.setText("Paste"); // NOI18N
        popupmenuPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPasteActionPerformed(evt);
            }
        });

        popupEdit = new javax.swing.JPopupMenu();
        popupEdit.add(popupmenuCopy);
        popupEdit.add(popupmenuCut);
        popupEdit.add(popupmenuPaste);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssemble;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnProgram;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSimReset;
    private javax.swing.JToggleButton btnSimRun;
    private javax.swing.JButton btnSimStep;
    private javax.swing.JToggleButton btnSimulate;
    private javax.swing.JPanel devMainPane;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JLabel lblPosition;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuItem menuAssemble;
    private javax.swing.JMenuItem menuCopy;
    private javax.swing.JMenuItem menuCut;
    private javax.swing.JMenuItem menuDeleteASM;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuExitSim;
    private javax.swing.JMenuItem menuExportASM;
    private javax.swing.JMenuItem menuFindAndReplace;
    private javax.swing.JMenu menuIOReg;
    private javax.swing.JMenuItem menuImportASM;
    private javax.swing.JMenuItem menuIssues;
    private javax.swing.JMenuItem menuIssuesPage;
    private javax.swing.JCheckBoxMenuItem menuLEDs;
    private javax.swing.JMenuItem menuManual;
    private javax.swing.JMenuItem menuNew;
    private javax.swing.JMenuItem menuNewASM;
    private javax.swing.JMenuItem menuOpen;
    private javax.swing.JMenuItem menuOptions;
    private javax.swing.JCheckBoxMenuItem menuPLPID;
    private javax.swing.JMenuItem menuPaste;
    private javax.swing.JMenuItem menuPrint;
    private javax.swing.JMenuItem menuProgram;
    private javax.swing.JMenuItem menuQuickProgram;
    private javax.swing.JMenuItem menuQuickRef;
    private javax.swing.JMenuItem menuRedo;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JMenuItem menuSaveAs;
    private javax.swing.JPopupMenu.Separator menuSeparator1;
    private javax.swing.JPopupMenu.Separator menuSeparator3;
    private javax.swing.JPopupMenu.Separator menuSeparator4;
    private javax.swing.JPopupMenu.Separator menuSeparator5;
    private javax.swing.JMenuItem menuSerialTerminal;
    private javax.swing.JMenuItem menuSetMainProgram;
    private javax.swing.JCheckBoxMenuItem menuSevenSegments;
    private javax.swing.JCheckBoxMenuItem menuSimIO;
    private javax.swing.JCheckBoxMenuItem menuSimMemory;
    private javax.swing.JMenuItem menuSimReset;
    private javax.swing.JCheckBoxMenuItem menuSimRun;
    private javax.swing.JMenuItem menuSimStep;
    private javax.swing.JMenu menuSimTools;
    private javax.swing.JCheckBoxMenuItem menuSimView;
    private javax.swing.JCheckBoxMenuItem menuSimWatcher;
    private javax.swing.JMenuItem menuSimulate;
    private javax.swing.JRadioButtonMenuItem menuStep1;
    private javax.swing.JRadioButtonMenuItem menuStep2;
    private javax.swing.JRadioButtonMenuItem menuStep3;
    private javax.swing.JRadioButtonMenuItem menuStep4;
    private javax.swing.JRadioButtonMenuItem menuStep5;
    private javax.swing.JMenu menuStepSize;
    private javax.swing.JCheckBoxMenuItem menuSwitches;
    private javax.swing.JCheckBoxMenuItem menuUART;
    private javax.swing.JMenuItem menuUndo;
    private javax.swing.JCheckBoxMenuItem menuVGA;
    private javax.swing.JMenu rootmenuEdit;
    private javax.swing.JMenu rootmenuFile;
    private javax.swing.JMenu rootmenuHelp;
    private javax.swing.JMenu rootmenuProject;
    private javax.swing.JMenu rootmenuSim;
    private javax.swing.JMenu rootmenuTools;
    private javax.swing.JScrollPane scroller;
    private javax.swing.JToolBar.Separator separatorSim;
    private javax.swing.JSplitPane splitterH;
    private javax.swing.JSplitPane splitterV;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JTree treeProject;
    private javax.swing.JLabel txtCurFile;
    private javax.swing.JTextPane txtEditor;
    private javax.swing.JTextPane txtOutput;
    // End of variables declaration//GEN-END:variables

    //popup menu items
    private javax.swing.JMenuItem popupmenuDeleteASM;
    private javax.swing.JMenuItem popupmenuExportASM;
    private javax.swing.JMenuItem popupmenuImportASM;
    private javax.swing.JMenuItem popupmenuNewASM;

    private javax.swing.JMenuItem popupmenuCopy;
    private javax.swing.JMenuItem popupmenuCut;
    private javax.swing.JMenuItem popupmenuPaste;

    private javax.swing.JPopupMenu popupEdit;

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

class DevUndoManager extends javax.swing.undo.UndoManager{

    java.util.ArrayList<Boolean> editTypeList;
    int position;
    int lastUndoPosition;

    public DevUndoManager() {
        super();
        position = 0;
        lastUndoPosition = 0;

        editTypeList = new java.util.ArrayList<Boolean>();
    }

    public boolean safeAddEdit(javax.swing.undo.UndoableEdit anEdit) {
            editTypeList.add(position, Config.nothighlighting);
            position++;
            Msg.D("++++ " + Config.nothighlighting + " undo position: " + position, 5, null);
            return super.addEdit(anEdit);
    }
    
    public void safeUndo() {
        boolean oldSyntaxOption = Config.devSyntaxHighlighting;
        Config.devSyntaxHighlighting = false;

        if(position <= 0 || !super.canUndo())
            return;

        // shed formatting events
        while(super.canUndo() && position > 0 && !editTypeList.get(position - 1)) {
            position--;
            super.undo();
        }

        if(position > 0 && super.canUndo()) {
            super.undo();
            position--;
            lastUndoPosition = position;
        }

        Config.devSyntaxHighlighting = oldSyntaxOption;

        Msg.D("<--- undo position: " + position, 5, null);
    }

    public void dumpList() {
        System.out.println();
        for(int i = 0; i < editTypeList.size(); i++) {
            System.out.println(i + "\t" + editTypeList.get(i));
        }
        System.out.println();
    }
    
    public void safeRedo() {
        boolean oldSyntaxOption = Config.devSyntaxHighlighting;
        Config.devSyntaxHighlighting = false;

        if(position > editTypeList.size() || !super.canRedo())
            return;

        // shed formatting events
        while(super.canRedo() && position < editTypeList.size() - 1 && !editTypeList.get(position + 1)) {
            super.redo();
            position++;
        }

        if(super.canRedo()) {
            super.redo();
            position++;
        }
        
        Config.devSyntaxHighlighting = oldSyntaxOption;

        Msg.D("---> undo position: " + position, 5, null);
    }

    public void reset() {
        editTypeList.clear();
        position = 0;
        super.discardAllEdits();
    }

    public void addEditType(boolean isHighlight) {

    }

    public boolean getNextEditType() {
        return false;
    }

    public boolean getPreviousEditType() {
        return false;
    }
}