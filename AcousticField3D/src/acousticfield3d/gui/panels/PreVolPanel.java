/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui.panels;

import acousticfield3d.gui.MainForm;
import acousticfield3d.gui.controls.VolAlphaPanel;
import acousticfield3d.renderer.VolPreShader;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.Scene;
import acousticfield3d.utils.Parse;

/**
 *
 * @author Asier
 */
public class PreVolPanel extends javax.swing.JPanel {
    public MainForm mf;
    
    VolAlphaPanel volPreAlphaPanel;
    
    public PreVolPanel(MainForm mf) {
        this.mf = mf;
        volPreAlphaPanel = new VolAlphaPanel(VolPreShader.ALPHA_MAP_VALUES);
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

        renderMethodGroup = new javax.swing.ButtonGroup();
        preVolEnableCheck = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        preVolDensityText = new javax.swing.JTextField();
        simToPreCubeButton = new javax.swing.JButton();
        preCubeToSimButton = new javax.swing.JButton();
        alphaVolPanel = volPreAlphaPanel;
        volCheck = new javax.swing.JRadioButton();
        maxProjCheck = new javax.swing.JRadioButton();

        preVolEnableCheck.setText("Enable");
        preVolEnableCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preVolEnableCheckActionPerformed(evt);
            }
        });

        jLabel19.setText("Density:");

        preVolDensityText.setText("128");

        simToPreCubeButton.setText("c=s");
        simToPreCubeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simToPreCubeButtonActionPerformed(evt);
            }
        });

        preCubeToSimButton.setText("s=c");
        preCubeToSimButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preCubeToSimButtonActionPerformed(evt);
            }
        });

        alphaVolPanel.setBackground(new java.awt.Color(255, 255, 255));
        alphaVolPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                alphaVolPanelMouseDragged(evt);
            }
        });
        alphaVolPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                alphaVolPanelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout alphaVolPanelLayout = new javax.swing.GroupLayout(alphaVolPanel);
        alphaVolPanel.setLayout(alphaVolPanelLayout);
        alphaVolPanelLayout.setHorizontalGroup(
            alphaVolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        alphaVolPanelLayout.setVerticalGroup(
            alphaVolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 88, Short.MAX_VALUE)
        );

        renderMethodGroup.add(volCheck);
        volCheck.setSelected(true);
        volCheck.setText("Vol");
        volCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volCheckActionPerformed(evt);
            }
        });

        renderMethodGroup.add(maxProjCheck);
        maxProjCheck.setText("MaxProjection");
        maxProjCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxProjCheckActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(volCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxProjCheck)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(alphaVolPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(simToPreCubeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(preCubeToSimButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(preVolEnableCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(preVolDensityText, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(preVolDensityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(preVolEnableCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(volCheck)
                    .addComponent(maxProjCheck))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(simToPreCubeButton)
                    .addComponent(preCubeToSimButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(alphaVolPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void preVolEnableCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preVolEnableCheckActionPerformed
        Scene.setVisible(mf.scene.getEntities(), Entity.TAG_PRE_VOL, preVolEnableCheck.isSelected());
        mf.needUpdate();
    }//GEN-LAST:event_preVolEnableCheckActionPerformed

    private void simToPreCubeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simToPreCubeButtonActionPerformed
        mf.scene.simToCube(mf.scene.getPreCubeVol(), mf.simulation );
        mf.needUpdate();
    }//GEN-LAST:event_simToPreCubeButtonActionPerformed

    private void preCubeToSimButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preCubeToSimButtonActionPerformed
        mf.scene.cubeToSim(mf.scene.getPreCubeVol(), mf.simulation);
        mf.scene.updateBoundaryBoxes(mf.simulation);
        mf.simPanel.simulationBoundariesToGUI();
        mf.needUpdate();
    }//GEN-LAST:event_preCubeToSimButtonActionPerformed

    private void alphaVolPanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_alphaVolPanelMouseDragged
        volPreAlphaPanel.setValueAt(evt.getX(), evt.getY());
    }//GEN-LAST:event_alphaVolPanelMouseDragged

    private void alphaVolPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_alphaVolPanelMousePressed
        volPreAlphaPanel.setValueAt(evt.getX(), evt.getY());
    }//GEN-LAST:event_alphaVolPanelMousePressed

    private void volCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volCheckActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_volCheckActionPerformed

    private void maxProjCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxProjCheckActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_maxProjCheckActionPerformed

    public int getRenderMethod() {
        if ( volCheck.isSelected() ){
            return 0;
        }else if ( maxProjCheck.isSelected() ){
            return 1;
        }
        
        return 0;
    }
    
    public VolAlphaPanel getPreVolAlphaPanel() {
        return volPreAlphaPanel;
    }
    
    public float getPreVolDensity(){
        return Parse.stringToFloat( preVolDensityText.getText() );
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel alphaVolPanel;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JRadioButton maxProjCheck;
    private javax.swing.JButton preCubeToSimButton;
    private javax.swing.JTextField preVolDensityText;
    private javax.swing.JCheckBox preVolEnableCheck;
    private javax.swing.ButtonGroup renderMethodGroup;
    private javax.swing.JButton simToPreCubeButton;
    private javax.swing.JRadioButton volCheck;
    // End of variables declaration//GEN-END:variables

    
}
