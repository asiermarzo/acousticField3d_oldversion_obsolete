/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.simulation.Animation;
import acousticfield3d.utils.FileUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Asier
 */
public class ExportPNGSequenceFrame extends javax.swing.JFrame {
    MainForm mf;
    
    
    public ExportPNGSequenceFrame(MainForm mf) {
        this.mf = mf;
        initComponents();
    }
        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        dirText = new javax.swing.JTextField();
        selectButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        currentFrameText = new javax.swing.JLabel();
        oneFrameCheck = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        secsPerFrameText = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export PNG seq");

        jLabel1.setText("Directory:");

        selectButton.setText("Select");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        currentFrameText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentFrameText.setText("999/999");

        oneFrameCheck.setText("One frame per keyframe");
        oneFrameCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneFrameCheckActionPerformed(evt);
            }
        });

        jLabel3.setText("Secs per frame:");

        secsPerFrameText.setText("0.1");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(oneFrameCheck)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secsPerFrameText, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentFrameText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dirText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(dirText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oneFrameCheck)
                    .addComponent(jLabel3)
                    .addComponent(secsPerFrameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentFrameText, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        String path = FileUtils.selectDirectory(this, "select", null);
        if (path != null) {
            dirText.setText( path );
        }
    }//GEN-LAST:event_selectButtonActionPerformed

    private void oneFrameCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneFrameCheckActionPerformed
        secsPerFrameText.setEnabled(! oneFrameCheck.isSelected() );
    }//GEN-LAST:event_oneFrameCheckActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
                okButton.setEnabled(false);

        BufferedImage bi = new BufferedImage(mf.gljpanel.getWidth(), mf.gljpanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final String path = dirText.getText();
        final boolean isStep = oneFrameCheck.isSelected();
        final float secsPerFrame = Float.parseFloat( secsPerFrameText.getText() );
        
        final Animation cAnim = mf.animPanel.getCurrentAnimation();
        if (isStep){
            final int nFrames = cAnim.getKeyFrames().getSize();
            currentFrameText.setText( 0 + " / " + nFrames);
            progressBar.setValue(100 * 0 / nFrames);
            for(int currentFrame=0; currentFrame < nFrames; ++currentFrame){
                cAnim.applyAtFrame(currentFrame, mf.simulation);
                mf.gljpanel.paint(bi.getGraphics());
                try {
                    ImageIO.write(bi, "png", new File(path, currentFrame + ".png"));
                } catch (IOException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                currentFrameText.setText( currentFrame + " / " + nFrames);
                progressBar.setValue(100 * currentFrame / nFrames);
            }
        }else{
            final float duration = cAnim.getDuration();
            final int nFrames = (int) ( duration / secsPerFrame );
            int currentFrame=0;
            currentFrameText.setText( currentFrame + " / " + nFrames);
            progressBar.setValue(100 * currentFrame / nFrames);
            for(float currentTime = 0; currentTime <= duration; currentTime +=  secsPerFrame){
                cAnim.applyAtTime(currentTime, mf.simulation);
                mf.gljpanel.paint(bi.getGraphics());
                try {
                    ImageIO.write(bi, "png", new File(path, currentFrame + ".png"));
                } catch (IOException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                currentFrame++;
                currentFrameText.setText( currentFrame + " / " + nFrames);
                progressBar.setValue(100 * currentFrame / nFrames);
            }
        }

        okButton.setEnabled(true);
            }
        };
        
        Thread t = new Thread(r, "Exporting PNG seq");
        t.start();
    }//GEN-LAST:event_okButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currentFrameText;
    private javax.swing.JTextField dirText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox oneFrameCheck;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField secsPerFrameText;
    private javax.swing.JButton selectButton;
    // End of variables declaration//GEN-END:variables
}
