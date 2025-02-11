/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui.panels;

import acousticfield3d.gui.MainForm;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Scene;
import acousticfield3d.workers.PlayerThread;
import acousticfield3d.simulation.AnimKeyFrame;
import acousticfield3d.simulation.Animation;
import acousticfield3d.utils.DialogUtils;
import acousticfield3d.utils.FileUtils;
import acousticfield3d.utils.Parse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asier
 */
public class AnimPanel extends javax.swing.JPanel {
    public MainForm mf;
    
    public Animation currentAnimation;
    Animation emptyAnimation;
    PlayerThread player;
    
    public AnimPanel(MainForm mf) {
        this.mf = mf;
        
        emptyAnimation = new Animation();
        currentAnimation = emptyAnimation;
        
        initComponents();
        stepAnimCheckActionPerformed(null);
        
        player = new PlayerThread(this, 25.0f);
        player.start();
    }

    public float getCurrentTime() {
        return player.getCurrentTime();
    }
    
    public Animation[] getSelectedAnimations(){
        final int[] selectedIndices = animationList.getSelectedIndices();
        final int size = selectedIndices.length;
        final Animation[] anims = new Animation[size];
        for(int i = 0; i < size; ++i){
            anims[i] = mf.simulation.getAnimations().getAt(selectedIndices[i]);
        }
        return anims;
    }
       
    public void initSimulation(){
        currentAnimation = emptyAnimation;
        statusList.setModel( currentAnimation.keyFrames );
        animationList.setModel( mf.simulation.animations );
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        wraperGroup = new javax.swing.ButtonGroup();
        addAnim = new javax.swing.JButton();
        delAnim = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        animationList = new javax.swing.JList();
        addStatus = new javax.swing.JButton();
        snapStatus = new javax.swing.JButton();
        delStatus = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        statusList = new javax.swing.JList();
        stopWrap = new javax.swing.JRadioButton();
        repeatWrap = new javax.swing.JRadioButton();
        pingpongWrap = new javax.swing.JRadioButton();
        stopButton = new javax.swing.JButton();
        playToggle = new javax.swing.JToggleButton();
        playSlider = new javax.swing.JSlider();
        stepsSpeedText = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        durationText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        waitText = new javax.swing.JTextField();
        stepAnimCheck = new javax.swing.JCheckBox();
        prevButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        currentFrameText = new javax.swing.JLabel();

        addAnim.setText("A");
        addAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAnimActionPerformed(evt);
            }
        });

        delAnim.setText("D");
        delAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delAnimActionPerformed(evt);
            }
        });

        animationList.setModel(mf.simulation.getAnimations());
        animationList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                animationListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(animationList);

        addStatus.setText("A");
        addStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStatusActionPerformed(evt);
            }
        });

        snapStatus.setText("S");
        snapStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapStatusActionPerformed(evt);
            }
        });

        delStatus.setText("D");
        delStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delStatusActionPerformed(evt);
            }
        });

        statusList.setModel(currentAnimation.getKeyFrames());
        statusList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(statusList);

        wraperGroup.add(stopWrap);
        stopWrap.setText("stop");

        wraperGroup.add(repeatWrap);
        repeatWrap.setText("repeat");

        wraperGroup.add(pingpongWrap);
        pingpongWrap.setSelected(true);
        pingpongWrap.setText("pingpong");

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        playToggle.setMnemonic('a');
        playToggle.setText("Play");
        playToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playToggleActionPerformed(evt);
            }
        });

        playSlider.setPaintTicks(true);
        playSlider.setValue(0);
        playSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playSliderStateChanged(evt);
            }
        });

        stepsSpeedText.setText("1");

        jLabel12.setText("Speed:");

        jLabel13.setText("Duration:");

        durationText.setText("1");

        jLabel1.setText("Wait:");

        waitText.setText("200");

        stepAnimCheck.setSelected(true);
        stepAnimCheck.setText("StepAnim");
        stepAnimCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepAnimCheckActionPerformed(evt);
            }
        });

        prevButton.setText("Prev");
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        currentFrameText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentFrameText.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(addAnim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delAnim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(snapStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delStatus))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(100, 100, 100))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(playSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(stopWrap)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(repeatWrap)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pingpongWrap))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(durationText, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stepsSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(waitText))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playToggle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(stepAnimCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stopButton)))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(prevButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentFrameText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextButton)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addAnim)
                    .addComponent(delAnim)
                    .addComponent(addStatus)
                    .addComponent(snapStatus)
                    .addComponent(delStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(durationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pingpongWrap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(stopWrap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(repeatWrap)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prevButton)
                    .addComponent(nextButton)
                    .addComponent(currentFrameText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stopButton)
                    .addComponent(playToggle)
                    .addComponent(stepAnimCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(stepsSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(waitText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
    }// </editor-fold>//GEN-END:initComponents

    
    public int getCurrentFrame(){
        return player.getCurrentFrame();
    }
    
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }
        
    public void setCurrentFrame(float value){
        currentFrameText.setText(value + "");
    }
    
    public boolean isPlaying(){
        return playToggle.isSelected();
    }
    
    public void setPlaying(boolean playing){
        playToggle.setSelected( playing );
    }
    
    public float getStepSpeed(){
        return Parse.stringToFloat( stepsSpeedText.getText() );
    }
    
    public boolean isWrapStop(){
        return stopWrap.isSelected();
    }
    public boolean isWrapRepeat(){
        return repeatWrap.isSelected();
    }
    public boolean isWrapPingPong(){
        return pingpongWrap.isSelected();
    }
    public boolean isStepAnim(){
        return stepAnimCheck.isSelected();
    }
    public float getWaitTime(){
        return Parse.stringToFloat( waitText.getText() );
    }
    
     public void updateTimeSlider(float p){
        int pi = (int)(p*100.0f);
        playSlider.setValue(pi);
    }
     
    private void addAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAnimActionPerformed
        String name = DialogUtils.getStringDialog(this, "Name?", "anim" + mf.simulation.animations.getSize());
        if(name != null){
            final Animation anim = createNewAnimation(name);
            mf.simulation.animations.add( anim );
        }
    }//GEN-LAST:event_addAnimActionPerformed

    public Animation createNewAnimation(String name){
        Animation anim = new Animation();
        anim.name = name;
        final int size = mf.simulation.animations.getSize();
        anim.setNumber(size == 0 ? 0 : (mf.simulation.animations.getElements().get(size - 1).getNumber() + 1));
        return anim;
    }
    
    private void delAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delAnimActionPerformed
        int[] selectedIndices = animationList.getSelectedIndices();
        mf.simulation.animations.delete(selectedIndices);
    }//GEN-LAST:event_delAnimActionPerformed

    private void animationListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_animationListValueChanged
        int index = animationList.getSelectedIndex();
        if(index != -1){
            Animation anim = mf.simulation.animations.getAt(index);
            currentAnimation = anim;
        }else{
            currentAnimation = emptyAnimation;
        }
        statusList.setModel( currentAnimation.getKeyFrames() );
        
        //delete old beads
        Scene.removeWithTag(mf.scene.getEntities(), Entity.TAG_BEAD);
        
        //add new beads
        mf.scene.getEntities().addAll( currentAnimation.controlPoints );
        
    }//GEN-LAST:event_animationListValueChanged

    public void pressAddKeyFrame(){
        addStatusActionPerformed(null);
    }
    
    private void addStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStatusActionPerformed
        if (currentAnimation != emptyAnimation){
            snapAndAddNewKeyFrame( currentAnimation );
        }
    }//GEN-LAST:event_addStatusActionPerformed

    private AnimKeyFrame snapAndAddNewKeyFrame(final Animation anim){
        AnimKeyFrame akf = new AnimKeyFrame();
        final int size = anim.keyFrames.getSize();
        akf.setNumber(size == 0 ? 0 : (anim.keyFrames.getElements().get(size - 1).getNumber() + 1));
        akf.duration = Parse.stringToFloat(durationText.getText());
        akf.snap(mf.simulation);
        anim.keyFrames.add(akf);
        return akf;
    }
    
    private void snapStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapStatusActionPerformed
        int[] selected = statusList.getSelectedIndices();
        for(int i : selected){
            AnimKeyFrame akf = currentAnimation.keyFrames.getAt(i);
            akf.setDuration( Parse.stringToFloat(durationText.getText()));
            akf.snap(mf.simulation);
        }
    }//GEN-LAST:event_snapStatusActionPerformed

    
    public void extractSignature(boolean onlyFocal) {
        //get currentAnimation
        final Animation c = getCurrentAnimation();
        
        //get list of points
        final ArrayList<MeshEntity> cps = c.getControlPoints();
        
        //get total frames
        final int totalFrames = c.getKeyFrames().getSize();
        
        //create and add new animation
        final Animation newAnim = createNewAnimation( c.getName() + (onlyFocal ? "foc" : "sig"));
        mf.simulation.animations.add( newAnim );
        
        //iterate over points
        for(int frame = 0; frame <= totalFrames; ++frame){
            player.applyFrame(frame);
            Entity currentBead = mf.cpPanel.selectFrame(frame);
            if (onlyFocal){
                mf.algorithmsForm.calcFocal();
            }else{    
                mf.holoPatternsForm.memorizePattern();
                mf.algorithmsForm.calcFocal();
                mf.holoPatternsForm.subtractFromHoloMemory();
                mf.holoPatternsForm.uniformPhase();
                mf.holoPatternsForm.normalizePhase();
                mf.holoPatternsForm.uniformPhase();
            }
            snapAndAddNewKeyFrame(newAnim);
            
            // add the key frame           
            mf.animPanel.pressAddKeyFrame();
            
            //add the bead to the new keyframe
            final Entity bead = currentBead;
            if(bead != null){
                final Vector3f pos = bead.getTransform().getTranslation();
                final int lastFrame = newAnim.getKeyFrames().getSize() - 1;
                MeshEntity newBead = mf.cpPanel.createControlPoint(pos.x, pos.y, pos.z, lastFrame, 0, true);
                newAnim.getControlPoints().add( newBead );
                newBead.getTransform().set(bead.getTransform());
            }
        }
    }
    
    private void delStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delStatusActionPerformed
        int[] selected = statusList.getSelectedIndices();
        currentAnimation.keyFrames.delete(selected);
    }//GEN-LAST:event_delStatusActionPerformed

    private void statusListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statusListMouseClicked
        //apply current akf
        if (evt.getClickCount() == 2){
            int selected = statusList.getSelectedIndex();
            if(selected != -1){
                player.applyFrame(selected);
                if (mf.transControlPanel.isDynamicTables()){
                    mf.transControlPanel.sendSetFrame( selected );
                }else{
                    mf.transControlPanel.sendData();
                }
                mf.needUpdate();
            }
        }
    }//GEN-LAST:event_statusListMouseClicked

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        playToggle.setSelected(false);
        player.stopReproduction();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void playToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playToggleActionPerformed
        player.playOrPause();
    }//GEN-LAST:event_playToggleActionPerformed

    public boolean shouldIgnorePlayerSlider = false;
    private void playSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playSliderStateChanged
        if (shouldIgnorePlayerSlider){
            shouldIgnorePlayerSlider = false;
            return;
        }
        int value = playSlider.getValue();
        player.applyAtPercetange( value / 100.0f);
        mf.needUpdate();
    }//GEN-LAST:event_playSliderStateChanged

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        player.prev();
    }//GEN-LAST:event_prevButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        player.next();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void stepAnimCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepAnimCheckActionPerformed
        if (stepAnimCheck.isSelected()){
            stepsSpeedText.setText("1");
            waitText.setText("200");
        }else{
            stepsSpeedText.setText("0.01");
            waitText.setText("33");
        }
    }//GEN-LAST:event_stepAnimCheckActionPerformed


    public void setAnimationFrame(int command) {
        player.applyFrame(command);
    }
    
    public void importAnimation() {
        Animation a = currentAnimation;
        if (a == null) {return;}
            
        String file = FileUtils.selectFile(this, "open", ".txt", null);
        if ( file != null){
            try {
                a.loadTable( new String(FileUtils.getBytesFromFile( new File(file))), mf.simulation);
            } catch (IOException ex) {
                Logger.getLogger(AnimPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    public void exportAnimation() {
        String file = FileUtils.selectNonExistingFile(this, ".txt");
        if(file != null){
            String content = currentAnimation.saveAsTable(mf.simulation);
            try {
                FileUtils.writeBytesInFile(new File(file), content.getBytes());
            } catch (IOException ex) {
                Logger.getLogger(AnimPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     
    
    public void switchBottomTop() {
        currentAnimation.switchTopBottom( mf.simulation );
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAnim;
    private javax.swing.JButton addStatus;
    private javax.swing.JList animationList;
    private javax.swing.JLabel currentFrameText;
    private javax.swing.JButton delAnim;
    private javax.swing.JButton delStatus;
    private javax.swing.JTextField durationText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton nextButton;
    private javax.swing.JRadioButton pingpongWrap;
    private javax.swing.JSlider playSlider;
    private javax.swing.JToggleButton playToggle;
    private javax.swing.JButton prevButton;
    private javax.swing.JRadioButton repeatWrap;
    private javax.swing.JButton snapStatus;
    private javax.swing.JList statusList;
    private javax.swing.JCheckBox stepAnimCheck;
    private javax.swing.JTextField stepsSpeedText;
    private javax.swing.JButton stopButton;
    private javax.swing.JRadioButton stopWrap;
    private javax.swing.JTextField waitText;
    private javax.swing.ButtonGroup wraperGroup;
    // End of variables declaration//GEN-END:variables

    

 
}
