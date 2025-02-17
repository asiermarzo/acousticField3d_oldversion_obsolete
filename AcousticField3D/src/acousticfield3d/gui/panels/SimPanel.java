/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui.panels;

import acousticfield3d.gui.MainForm;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.Scene;
import acousticfield3d.simulation.Simulation;
import acousticfield3d.utils.Parse;

/**
 *
 * @author Asier
 */
public class SimPanel extends javax.swing.JPanel {
    public MainForm mf;
    
    public SimPanel(MainForm mf) {
        this.mf = mf;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel18 = new javax.swing.JLabel();
        minXText = new javax.swing.JTextField();
        maxXText = new javax.swing.JTextField();
        minYText = new javax.swing.JTextField();
        maxYText = new javax.swing.JTextField();
        minZText = new javax.swing.JTextField();
        maxZText = new javax.swing.JTextField();
        applySizeButton = new javax.swing.JButton();
        autoSizeButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        guiGainText = new javax.swing.JTextField();
        showSimBBCheck = new javax.swing.JCheckBox();
        jLabel33 = new javax.swing.JLabel();
        tempText = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        particleSpeedText = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        particleSize = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        mediumDensityText = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        particleDensityText = new javax.swing.JTextField();

        jLabel18.setText("Sim Size");

        minXText.setText("minX");

        maxXText.setText("maxX");

        minYText.setText("minY");

        maxYText.setText("maxY");

        minZText.setText("minZ");

        maxZText.setText("maxZ");

        applySizeButton.setText("Apply");
        applySizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applySizeButtonActionPerformed(evt);
            }
        });

        autoSizeButton.setText("Auto");
        autoSizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSizeButtonActionPerformed(evt);
            }
        });

        jLabel14.setText("GUI gain");

        guiGainText.setText("0.01");

        showSimBBCheck.setSelected(true);
        showSimBBCheck.setText("ShowBB");
        showSimBBCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSimBBCheckActionPerformed(evt);
            }
        });

        jLabel33.setText("Temp:");

        tempText.setText("25");
        tempText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempTextActionPerformed(evt);
            }
        });

        jLabel36.setText("PSpeed:");

        particleSpeedText.setText("2400");
        particleSpeedText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                particleSpeedTextActionPerformed(evt);
            }
        });

        jLabel37.setText("PSize:");

        particleSize.setText("0.001");
        particleSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                particleSizeActionPerformed(evt);
            }
        });

        jLabel38.setText("MDens:");

        mediumDensityText.setText("1.2");
        mediumDensityText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediumDensityTextActionPerformed(evt);
            }
        });

        jLabel39.setText("PDens:");

        particleDensityText.setText("25");
        particleDensityText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                particleDensityTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(showSimBBCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel14)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(guiGainText))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(applySizeButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(autoSizeButton)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(particleSize))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(particleSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(particleDensityText))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tempText, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mediumDensityText))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(minYText, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(minXText, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(minZText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(maxYText, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxXText, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxZText))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(guiGainText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(applySizeButton)
                    .addComponent(autoSizeButton))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minXText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxXText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minYText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxYText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minZText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxZText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showSimBBCheck)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(tempText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(mediumDensityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(particleSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(particleDensityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(particleSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void applySizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applySizeButtonActionPerformed
        guiToSimulationBoundaries();
        mf.scene.updateBoundaryBoxes(mf.simulation);
        mf.needUpdate();
    }//GEN-LAST:event_applySizeButtonActionPerformed

    private void autoSizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSizeButtonActionPerformed
        mf.simulation.updateSimulationBoundaries();
        mf.scene.updateBoundaryBoxes(mf.simulation);
        mf.scene.adjustCameraNearAndFar(mf.simulation);
        simulationBoundariesToGUI();
        mf.needUpdate();
    }//GEN-LAST:event_autoSizeButtonActionPerformed

    private void showSimBBCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSimBBCheckActionPerformed
        Scene.setVisible(mf.scene.getEntities(), Entity.TAG_SIMULATION_BOUNDINGS, showSimBBCheck.isSelected());
        mf.needUpdate();
    }//GEN-LAST:event_showSimBBCheckActionPerformed

    private void tempTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tempTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_tempTextActionPerformed

    private void particleSpeedTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_particleSpeedTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_particleSpeedTextActionPerformed

    private void particleSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_particleSizeActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_particleSizeActionPerformed

    private void mediumDensityTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediumDensityTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_mediumDensityTextActionPerformed

    private void particleDensityTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_particleDensityTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_particleDensityTextActionPerformed

    public float getGUIGain(){
        return Parse.stringToFloat( guiGainText.getText() );
    }
    
    public void guiToSimulationBoundaries(){
        Vector3f max = mf.simulation.getBoundaryMax();
        Vector3f min = mf.simulation.getBoundaryMin();
        min.x = Parse.stringToFloat( minXText.getText() );
        min.y = Parse.stringToFloat( minYText.getText() );
        min.z = Parse.stringToFloat( minZText.getText() );
        max.x = Parse.stringToFloat( maxXText.getText() );
        max.y = Parse.stringToFloat( maxYText.getText() );
        max.z = Parse.stringToFloat( maxZText.getText() );
        mf.scene.updateBoundaryBoxes(mf.simulation);
    }
    
    public void simulationBoundariesToGUI(){
        Vector3f max = mf.simulation.getBoundaryMax();
        Vector3f min = mf.simulation.getBoundaryMin();
        minXText.setText( mf.decimalFormat.format( min.x ));
        minYText.setText( mf.decimalFormat.format( min.y ));
        minZText.setText( mf.decimalFormat.format( min.z ));
        maxXText.setText( mf.decimalFormat.format( max.x ));
        maxYText.setText( mf.decimalFormat.format( max.y ));
        maxZText.setText( mf.decimalFormat.format( max.z ));
        guiGainText.setText( mf.decimalFormat.format( mf.simulation.minDistanceBoundary() / 100.0f));
    }
    
    public float getWaveLength(){
        final float c = Simulation.getSoundSpeedInAir(mf.simPanel.getTemperature());
        return c / mf.simulation.getTransFrequency();
    }
    
    public float getTemperature(){
        return Parse.stringToFloat( tempText.getText() );
    }
    
    public float getMediumDensity(){
        return Parse.stringToFloat( mediumDensityText.getText() );
    }
    
    public float getParticleSpeed(){
        return Parse.stringToFloat( particleSpeedText.getText() );
    }
    
    public float getParticleDensity(){
        return Parse.stringToFloat( particleDensityText.getText() );
    }
    
    public float getParticleSize(){
        return Parse.stringToFloat( particleSize.getText() );
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applySizeButton;
    private javax.swing.JButton autoSizeButton;
    private javax.swing.JTextField guiGainText;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JTextField maxXText;
    private javax.swing.JTextField maxYText;
    private javax.swing.JTextField maxZText;
    private javax.swing.JTextField mediumDensityText;
    private javax.swing.JTextField minXText;
    private javax.swing.JTextField minYText;
    private javax.swing.JTextField minZText;
    private javax.swing.JTextField particleDensityText;
    private javax.swing.JTextField particleSize;
    private javax.swing.JTextField particleSpeedText;
    private javax.swing.JCheckBox showSimBBCheck;
    private javax.swing.JTextField tempText;
    // End of variables declaration//GEN-END:variables
}
