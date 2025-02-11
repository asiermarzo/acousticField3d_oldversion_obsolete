/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui.panels;

import acousticfield3d.gui.MainForm;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.PhysicEntity;
import acousticfield3d.utils.FileUtils;
import acousticfield3d.utils.Parse;
import acousticfield3d.workers.PhysicsWorker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asier
 */
public class PhysicsPanel extends javax.swing.JPanel {
    public final MainForm mf;
    final PhysicsWorker worker;
    
    public PhysicsPanel(MainForm mf) {
        this.mf = mf;
        initComponents();
        
        worker = new PhysicsWorker(this);
        worker.start();
    }
    
    
    
    public void tickIfEnabledForRender() {
        if(enableRenderCheck.isSelected()){
            worker.tickPhysics();
        }
    }
    
    public void tickIfEnabledForAnimation() {
        if(enableAnimCheck.isSelected() && !enableRenderCheck.isSelected()){
            worker.tickPhysics();
        }
    }
    
    public boolean isTickEverySecs(){
        return tickCheck.isSelected();
    }
    
    public boolean isResetSpeedEveryStep(){
        return resetSpeedEveryFrameCheck.isSelected();
    }
    
    public boolean isLeaveTrail(){
        return leaveTrailCheck.isSelected();
    }
    
    public float getTickEverySecs(){
        return Parse.stringToFloat( tickEverySecsText.getText() );
    }

    public float getSecsPerFrame(){
        return Parse.stringToFloat( secsPerTickText.getText() );
    }
    
    public float getVDamp(){
        return Parse.stringToFloat( velocityDampText.getText() );
    }
    
    public int getSteps(){
        return Parse.stringToInt( stepsText.getText() );
    }

    public Vector3f getGravity(){
        Vector3f g = new Vector3f();
        g.parse( gText.getText() );
        return g;
    }
    
    public boolean isDestroyMaxDist(){
        return maxDistCheck.isSelected();
    }
    
    public float getDestroyMaxDist(){
        return Parse.stringToFloat( maxDistText.getText() );
    }
    
    public int getSimulationsSteps(){
        return Parse.stringToInt( simulationStepsText.getText() );
    }
    
    public int getAddNBeads(){
        return Parse.stringToInt( addBeadsText.getText() );
    }
    
    
    public static final String CSV_SEPARATOR = "\t";
    public void exportTrail() {
        String file = FileUtils.selectFile(mf, "export", ".txt", null);
        if(file != null){
            //gather all the trails
            ArrayList<MeshEntity> trails = new ArrayList<>();
            mf.scene.gatherMeshEntitiesWithTag(trails, Entity.TAG_BEAD_TRAIL);
            
            /*
            //sort by frame and then number
            Collections.sort(trails, new Comparator<MeshEntity>() {
                @Override
                public int compare(MeshEntity o1, MeshEntity o2) {
                    int cByFrame = Integer.compare(o1.getFrame(), o2.getFrame());
                    return cByFrame == 0 ? Integer.compare(o1.getNumber(), o2.getNumber()) : cByFrame;
                }
            });
            */
            
            //write them
            StringBuilder sb = new StringBuilder();
            int lastFrame = -1;
            int currentNumber = 0;
            int maxNumber = 0;
            int number = 0 ;

            for (MeshEntity bead : trails) {
                final Vector3f pos = bead.getTransform().getTranslation();
                sb.append(pos.x + CSV_SEPARATOR + pos.y + CSV_SEPARATOR + pos.z + "\n");
            }
            
            /*
            for (MeshEntity bead : trails) {
                maxNumber = Math.max(maxNumber, bead.getNumber());
            }
            
            final float nx = 0.0f, ny = 0.0f, nz = 0.0f;
            for (MeshEntity bead : trails) {
                final Vector3f pos = bead.getTransform().getTranslation();
                double x = pos.x;
                double y = pos.y;
                double z = pos.z;
                double div = 0.0;

                final int frame = bead.getFrame();
                
                if (frame != lastFrame) {
                    if (lastFrame != -1){
                        while(currentNumber < maxNumber){
                            sb.append(nx + CSV_SEPARATOR + (-nz) + CSV_SEPARATOR + ny + CSV_SEPARATOR + div + CSV_SEPARATOR);
                            currentNumber++;
                        }
                    }
                    currentNumber = 0;
                    lastFrame = frame;
                    sb.append("\n");
                    sb.append(frame + CSV_SEPARATOR);
                } else {   
                    currentNumber++;
                }
                
                number = bead.getNumber();
                
                while(currentNumber < number){
                    sb.append(nx + CSV_SEPARATOR + (-nz) + CSV_SEPARATOR + ny + CSV_SEPARATOR + div + CSV_SEPARATOR);
                    currentNumber++;
                }
                sb.append(x + CSV_SEPARATOR + (-z) + CSV_SEPARATOR + y + CSV_SEPARATOR + div + CSV_SEPARATOR);
            }

            StringBuilder sbHeaders = new StringBuilder();

            sbHeaders.append("frame" + CSV_SEPARATOR);
            for (int i = 0; i <= maxNumber; ++i) {
                sbHeaders.append("x" + i + CSV_SEPARATOR + "y" + i + CSV_SEPARATOR + "z" + 
                        i + CSV_SEPARATOR + "divergence" + i + CSV_SEPARATOR);
            }
            sbHeaders.append(sb.toString());
    
            */
            try {
                FileUtils.writeBytesInFile(new File(file), sb.toString().getBytes());
                //FileUtils.writeBytesInFile(new File(file), sbHeaders.toString().getBytes());
            } catch (IOException ex) {
                Logger.getLogger(PhysicsPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enableRenderCheck = new javax.swing.JCheckBox();
        tickCheck = new javax.swing.JCheckBox();
        tickEverySecsText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        secsPerTickText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        stepsText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        gText = new javax.swing.JTextField();
        maxDistCheck = new javax.swing.JCheckBox();
        maxDistText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        simulationStepsText = new javax.swing.JTextField();
        addBeadsButton = new javax.swing.JButton();
        addBeadsText = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        velocityDampText = new javax.swing.JTextField();
        simulateButton = new javax.swing.JButton();
        attachButton = new javax.swing.JButton();
        resetSpeedEveryFrameCheck = new javax.swing.JCheckBox();
        leaveTrailCheck = new javax.swing.JCheckBox();
        cleanTrailButton = new javax.swing.JButton();
        enableAnimCheck = new javax.swing.JCheckBox();

        enableRenderCheck.setText("enable render");

        tickCheck.setText("self-tick");
        tickCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tickCheckActionPerformed(evt);
            }
        });

        tickEverySecsText.setText("0.04");

        jLabel1.setText("secs per frame:");

        secsPerTickText.setText("0.025");

        jLabel2.setText("steps:");

        stepsText.setText("10");

        jLabel3.setText("G:");

        gText.setText("0 -9.8 0");

        maxDistCheck.setSelected(true);
        maxDistCheck.setText("max dist destroy:");

        maxDistText.setText("1");

        jLabel4.setText("steps:");

        simulationStepsText.setText("1000");

        addBeadsButton.setText("Add");
        addBeadsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBeadsButtonActionPerformed(evt);
            }
        });

        addBeadsText.setText("1000");

        jLabel6.setText("VelDamp:");

        velocityDampText.setText("0.9");

        simulateButton.setText("Simulate");
        simulateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulateButtonActionPerformed(evt);
            }
        });

        attachButton.setText("Attach");
        attachButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachButtonActionPerformed(evt);
            }
        });

        resetSpeedEveryFrameCheck.setText("resetSpeedEveryStep");

        leaveTrailCheck.setText("leave trail");

        cleanTrailButton.setText("Clean");
        cleanTrailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanTrailButtonActionPerformed(evt);
            }
        });

        enableAnimCheck.setText("enable anim");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secsPerTickText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stepsText, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(velocityDampText))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(addBeadsButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addBeadsText))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(simulateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(simulationStepsText)
                            .addComponent(attachButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tickCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tickEverySecsText, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(leaveTrailCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cleanTrailButton))
                            .addComponent(resetSpeedEveryFrameCheck)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(maxDistCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxDistText, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(enableRenderCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(enableAnimCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enableRenderCheck)
                    .addComponent(enableAnimCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tickCheck)
                    .addComponent(tickEverySecsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(secsPerTickText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(stepsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(velocityDampText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(gText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxDistCheck)
                    .addComponent(maxDistText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetSpeedEveryFrameCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(leaveTrailCheck)
                    .addComponent(cleanTrailButton))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(simulateButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(simulationStepsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addBeadsButton)
                    .addComponent(addBeadsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addBeadsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBeadsButtonActionPerformed
        final int nBeads = getAddNBeads();
        final Vector3f sCenter = mf.simulation.getSimulationCenter();
        final Vector3f sSize = mf.simulation.getSimulationSize();
        sSize.multLocal( 1.0f / 2.0f);
        
        for(int i = 0; i < nBeads; ++i){
            mf.cpPanel.addControlPoint (
                    sCenter.x + FastMath.random( -sSize.x , sSize.x), 
                    sCenter.y + FastMath.random( -sSize.y , sSize.y), 
                    sCenter.z + FastMath.random( -sSize.z , sSize.z), 
                    mf.cpPanel.getNumber(), 0, false);
        }
        mf.needUpdate();
    }//GEN-LAST:event_addBeadsButtonActionPerformed

    private void tickCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tickCheckActionPerformed
        worker.playOrPause();
    }//GEN-LAST:event_tickCheckActionPerformed

    private void simulateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simulateButtonActionPerformed
        final int steps = getSimulationsSteps();
        for(int i = 0; i < steps; ++i){
            worker.tickPhysics();
        }
        mf.needUpdate();
    }//GEN-LAST:event_simulateButtonActionPerformed

    public void simulateForBead(MeshEntity bead, int step1, int step2, float time) {
        worker.tickPhysics( bead, step1, step2, time);
    }
    
    private void attachButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachButtonActionPerformed
       ArrayList<MeshEntity> beads = new ArrayList<>();
        for ( MeshEntity me :   mf.scene.getEntities()){
            if (me.isVisible() && 
                    (me.getTag() & Entity.TAG_CONTROL_POINT) != 0 && 
                    (me.getTag() & Entity.TAG_BEAD) != 0){
                beads.add( me );
            }
        }
        for(MeshEntity bead : beads){
            final Vector3f p = bead.getTransform().getTranslation();
            MeshEntity b = mf.cpPanel.addControlPoint (p.x, p.y, p.z, mf.cpPanel.getNumber(), 0, false);
            b.setColor( bead.getColor() );
        }
        mf.needUpdate();
    }//GEN-LAST:event_attachButtonActionPerformed

    private void cleanTrailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanTrailButtonActionPerformed
        synchronized (mf) {
            mf.scene.removeWithTag( Entity.TAG_BEAD_TRAIL );
        }
        mf.needUpdate();
    }//GEN-LAST:event_cleanTrailButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBeadsButton;
    private javax.swing.JTextField addBeadsText;
    private javax.swing.JButton attachButton;
    private javax.swing.JButton cleanTrailButton;
    private javax.swing.JCheckBox enableAnimCheck;
    private javax.swing.JCheckBox enableRenderCheck;
    private javax.swing.JTextField gText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JCheckBox leaveTrailCheck;
    private javax.swing.JCheckBox maxDistCheck;
    private javax.swing.JTextField maxDistText;
    private javax.swing.JCheckBox resetSpeedEveryFrameCheck;
    private javax.swing.JTextField secsPerTickText;
    private javax.swing.JButton simulateButton;
    private javax.swing.JTextField simulationStepsText;
    private javax.swing.JTextField stepsText;
    private javax.swing.JCheckBox tickCheck;
    private javax.swing.JTextField tickEverySecsText;
    private javax.swing.JTextField velocityDampText;
    // End of variables declaration//GEN-END:variables

    

    

    
}
