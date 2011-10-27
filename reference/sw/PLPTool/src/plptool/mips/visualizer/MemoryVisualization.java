/*
    Copyright 2011 David Fritz, Brian Gordon, Wira Mulia

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

package plptool.mips.visualizer;

import plptool.gui.ProjectDriver;
import plptool.*;
import plptool.mips.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.net.*;
import java.io.*;

/**
 *
 * @author wira
 */
public class MemoryVisualization extends javax.swing.JFrame {

    private ProjectDriver plp;
    private SimCore sim;
    private DrawPanel canvas;
    private BufferedImage img;

    protected long startAddr = -1;
    protected long endAddr = -1;

    /** Creates new form MemoryVisualization */
    public MemoryVisualization(ProjectDriver plp) {
        initComponents();
        canvas = new DrawPanel(plp);
        canvas.setSize(container.getWidth(), container.getHeight());
        container.add(canvas);
        container.revalidate();
        this.sim = (SimCore) plp.sim;
        this.plp = plp;
    }

    public void setBG(String path) {
        try {
            URL u = new URL(path);
            img = ImageIO.read(u);

        } catch(Exception e) {
            Msg.E("Unable to fetch kitten image, boo!", Constants.PLP_OK, null);
        }
    }
    
    public void updateVisualization() {
        canvas.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtStartAddr = new javax.swing.JTextField();
        lbl1 = new javax.swing.JLabel();
        txtEndAddr = new javax.swing.JTextField();
        btnVisualize = new javax.swing.JButton();
        container = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(plptool.gui.PLPToolApp.class).getContext().getResourceMap(MemoryVisualization.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        txtStartAddr.setText(resourceMap.getString("txtStartAddr.text")); // NOI18N
        txtStartAddr.setName("txtStartAddr"); // NOI18N

        lbl1.setText(resourceMap.getString("lbl1.text")); // NOI18N
        lbl1.setName("lbl1"); // NOI18N

        txtEndAddr.setText(resourceMap.getString("txtEndAddr.text")); // NOI18N
        txtEndAddr.setName("txtEndAddr"); // NOI18N

        btnVisualize.setText(resourceMap.getString("btnVisualize.text")); // NOI18N
        btnVisualize.setName("btnVisualize"); // NOI18N
        btnVisualize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualizeActionPerformed(evt);
            }
        });

        container.setName("container"); // NOI18N

        javax.swing.GroupLayout containerLayout = new javax.swing.GroupLayout(container);
        container.setLayout(containerLayout);
        containerLayout.setHorizontalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 493, Short.MAX_VALUE)
        );
        containerLayout.setVerticalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 314, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(txtStartAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEndAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVisualize, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
            .addComponent(container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStartAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl1)
                    .addComponent(txtEndAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVisualize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVisualizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualizeActionPerformed
        try {
            startAddr = PLPToolbox.parseNum(txtStartAddr.getText());
            endAddr = PLPToolbox.parseNum(txtEndAddr.getText());
            updateVisualization();
        } catch(Exception e) {

        }
    }//GEN-LAST:event_btnVisualizeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVisualize;
    private javax.swing.JPanel container;
    private javax.swing.JLabel lbl1;
    private javax.swing.JTextField txtEndAddr;
    private javax.swing.JTextField txtStartAddr;
    // End of variables declaration//GEN-END:variables

    class DrawPanel extends JPanel {
        private ProjectDriver plp;
        private long oldSpVal;

        public DrawPanel(ProjectDriver plp) {
            super();
            oldSpVal = -1;
            this.plp = plp;
        }

        @Override
        public void paint(Graphics g) {
            g.setFont(new Font("Monospaced", Font.BOLD, 12));
            FontMetrics fm = g.getFontMetrics();
            int addrStrOffset = fm.stringWidth("0x00000000");
            this.setSize(this.getParent().getWidth(), this.getParent().getHeight());
            int W = this.getWidth();
            int H = this.getHeight();
            int fontHeight = g.getFontMetrics().getDescent();
            long locs = (endAddr - startAddr) / 4 + 1;

            g.setColor(Color.black);
            g.fillRect(0, 0, W, H);

            if(img != null) {
                g.drawImage(img, 0, 0, null);
            }

            if(locs < 1 || startAddr < 0 || endAddr < 0)
                return;

            int topOffset = fm.getHeight() + 10;
            int rightOffset = 50;

            g.setColor(new Color(240, 240, 240));
            g.fillRect(0, 0, W, topOffset);
            g.setColor(Color.black);
            g.drawString("Contents", W - 10 - addrStrOffset - rightOffset, 5 + fm.getHeight());
            g.drawString("Address", W - 30 - 2*addrStrOffset - rightOffset, 5 + fm.getHeight());
            g.drawString("$sp", W - 40 - 2*addrStrOffset - 30 - rightOffset, 5 + fm.getHeight());

            // if the user wants to see more than 32 memory locations, we do
            // a special case
            int yScaleFactor = 5;
            while(locs > Math.pow(2, yScaleFactor) && yScaleFactor < 32)
                yScaleFactor++;

            // too big, user wants to visualize more than 32-bit address space
            if(yScaleFactor == 32)
                return;
            
            long addrOffset = 4;
            if(yScaleFactor > 5) {
                locs /= (long) Math.pow(2, yScaleFactor - 5);
                addrOffset *= Math.pow(2, yScaleFactor - 5);
            }

            int rowH = (H - topOffset) / (int) locs;
            int stringYOffset = (rowH - fontHeight) / 2 + fontHeight;
            boolean drawStr = (rowH > stringYOffset);

            for(int i = 0; i < locs; i++) {
                Long spVal = sim.regfile.read(29);
                if(spVal == null) spVal = oldSpVal;
                if(spVal >= 0 && spVal >= startAddr + addrOffset*i && spVal < startAddr + addrOffset*i + addrOffset) {
                    g.setColor(Color.red);
                    int xPoints[] = {W - 40 - 2*addrStrOffset - 30 - rightOffset, W - 40 - 2*addrStrOffset - 30 - rightOffset, W - 40 - 2*addrStrOffset - 10 - rightOffset};
                    int yPoints[] = {topOffset + i*rowH + rowH / 2 - 5, topOffset + i*rowH + rowH / 2 + 5, topOffset + i*rowH + rowH / 2};
                    g.fillPolygon(xPoints, yPoints, 3);
                    //g.drawString("$sp -->", W - 50 - 2*addrStrOffset - g.getFontMetrics().stringWidth("$sp -->") - rightOffset, topOffset + i*rowH + stringYOffset);
                    oldSpVal = spVal;
                }
                
                if(sim.bus.isInstr(startAddr + addrOffset*i))
                    g.setColor(new Color(200, 200, 255));
                else if (!sim.bus.isMapped(startAddr + addrOffset*i))
                    g.setColor(new Color(255, 200, 200));
                else if (!sim.bus.isInitialized(startAddr + addrOffset*i))
                    g.setColor(new Color(225, 225, 225));
                else
                    g.setColor(new Color(190, 190, 190));

                g.fillRect(W - 20 - addrStrOffset - rightOffset, topOffset + i * rowH, 20 + addrStrOffset, rowH);
                g.setColor(new Color(25, 25, 25));
                g.drawLine(0, topOffset + (i+1) * rowH, W, topOffset + (i+1) * rowH);

                if(drawStr) {
                    if(yScaleFactor <= 5) {
                        g.setColor(Color.black);
                        g.drawString(String.format("0x%08x", plp.sim.bus.read(startAddr + addrOffset*i)), W - 10 - addrStrOffset - rightOffset, topOffset + i*rowH + stringYOffset);
                    }
                    g.setColor(plp.sim.visibleAddr == startAddr + addrOffset*i ? Color.red : Color.white);
                    g.drawString(String.format("0x%08x", startAddr + addrOffset*i), W - 10 - 2*addrStrOffset - 20 - rightOffset, topOffset + i*rowH + stringYOffset);
                }
            }
        }
    }
}


