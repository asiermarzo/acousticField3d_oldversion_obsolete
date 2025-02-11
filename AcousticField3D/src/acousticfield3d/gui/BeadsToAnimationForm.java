/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.math.FastMath;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Scene;
import acousticfield3d.utils.Parse;
import java.util.ArrayList;
import java.util.HashMap;
import ktools.utils.TextFrame;

/**
 *
 * @author Asier
 */
public class BeadsToAnimationForm extends javax.swing.JFrame {
    MainForm mf;
    
    public BeadsToAnimationForm(MainForm mf) {
        this.mf = mf;
        initComponents();
    }
    
    public boolean isBeadsEnabled(){
        return enableCheck.isSelected();
    }
    public boolean isFramesAll(){
        return frameAllCheck.isSelected();
    }
    public boolean isFramesOnlyCurrent(){
        return frameCurrentCheck.isSelected();
    }
    public boolean isFrameHighlight(){
        return highlightCurrentFrameCheck.isSelected();
    }
    
    public boolean isEnablePerTick(){
        return enablePerTickCheck.isSelected();
    }
    public boolean isNumberAll(){
        return numberAllCheck.isSelected();
    }
    public boolean isNumberOnly(){
        return numberOnlyCheck.isSelected();
    }
    public int getNumber(){
        return Parse.stringToInt( numberText.getText() );
    }
    
    public boolean isSliceFollow(){
        return sliceCheck.isSelected();
    }
    public boolean isSliceFollowXYZ(){
        return sliceFollowsXYZCheck.isSelected();
    }
    public boolean isFollowCubeX(){
        return cubeXCheck.isSelected();
    }
    public boolean isFollowCubeY(){
        return cubeYCheck.isSelected();
    }
    public boolean isFollowCubeZ(){
        return cubeZCheck.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frameGroup = new javax.swing.ButtonGroup();
        numberGroup = new javax.swing.ButtonGroup();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        frameAllCheck = new javax.swing.JRadioButton();
        frameCurrentCheck = new javax.swing.JRadioButton();
        highlightCurrentFrameCheck = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        numberAllCheck = new javax.swing.JRadioButton();
        numberOnlyCheck = new javax.swing.JRadioButton();
        numberText = new javax.swing.JTextField();
        sliceCheck = new javax.swing.JCheckBox();
        cubeXCheck = new javax.swing.JCheckBox();
        cubeYCheck = new javax.swing.JCheckBox();
        cubeZCheck = new javax.swing.JCheckBox();
        enableCheck = new javax.swing.JCheckBox();
        enablePerTickCheck = new javax.swing.JCheckBox();
        snapPositionsButton = new javax.swing.JButton();
        applyPositionButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        sliceFollowsXYZCheck = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Beads to animation");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Frames:");

        frameGroup.add(frameAllCheck);
        frameAllCheck.setText("All");

        frameGroup.add(frameCurrentCheck);
        frameCurrentCheck.setSelected(true);
        frameCurrentCheck.setText("Current");

        highlightCurrentFrameCheck.setText("highlight current");

        jLabel2.setText("Number:");

        numberGroup.add(numberAllCheck);
        numberAllCheck.setSelected(true);
        numberAllCheck.setText("All");

        numberGroup.add(numberOnlyCheck);
        numberOnlyCheck.setText("Only");

        numberText.setText("0");

        sliceCheck.setText("Slice follow");

        cubeXCheck.setText("CubeX");

        cubeYCheck.setText("CubeY");

        cubeZCheck.setText("CubeZ");

        enableCheck.setText("enable");

        enablePerTickCheck.setText("enable per tick");

        snapPositionsButton.setText("Snap positions");
        snapPositionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapPositionsButtonActionPerformed(evt);
            }
        });

        applyPositionButton.setText("Apply");
        applyPositionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyPositionButtonActionPerformed(evt);
            }
        });

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        sliceFollowsXYZCheck.setText("followsXYZ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(enableCheck)
                        .addGap(18, 18, 18)
                        .addComponent(enablePerTickCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(snapPositionsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exportButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(applyPositionButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cubeXCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cubeYCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cubeZCheck))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(sliceCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliceFollowsXYZCheck))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(18, 18, 18)
                                    .addComponent(frameAllCheck)
                                    .addGap(18, 18, 18)
                                    .addComponent(frameCurrentCheck)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(highlightCurrentFrameCheck))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(18, 18, 18)
                                    .addComponent(numberAllCheck)
                                    .addGap(18, 18, 18)
                                    .addComponent(numberOnlyCheck)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(numberText))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(enableCheck)
                        .addComponent(enablePerTickCheck)))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(frameAllCheck)
                    .addComponent(frameCurrentCheck)
                    .addComponent(highlightCurrentFrameCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(numberAllCheck)
                    .addComponent(numberOnlyCheck)
                    .addComponent(numberText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sliceCheck)
                    .addComponent(sliceFollowsXYZCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cubeXCheck)
                    .addComponent(cubeYCheck)
                    .addComponent(cubeZCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(snapPositionsButton)
                    .addComponent(applyPositionButton)
                    .addComponent(exportButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

        
    private final HashMap<MeshEntity, Vector3f> snappedPositions = new HashMap<>();
    private void snapPositionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapPositionsButtonActionPerformed
        snappedPositions.clear();
        ArrayList<MeshEntity> cps = new ArrayList<>();
        mf.scene.gatherMeshEntitiesWithTag(cps, Entity.TAG_CONTROL_POINT);
        for (MeshEntity me : cps) {
            snappedPositions.put(me, new Vector3f(me.getTransform().getTranslation()));
        }
    }//GEN-LAST:event_snapPositionsButtonActionPerformed

    private void applyPositionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyPositionButtonActionPerformed
        ArrayList<MeshEntity> cps = new ArrayList<>();
        for(MeshEntity me : snappedPositions.keySet()){
            me.getTransform().getTranslation().set( snappedPositions.get(me) );
        }
        mf.needUpdate();
    }//GEN-LAST:event_applyPositionButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        ArrayList<MeshEntity> cps = new ArrayList<>();
        mf.scene.gatherMeshEntitiesWithTag(cps, Entity.TAG_CONTROL_POINT);
        StringBuilder sb = new StringBuilder();
        for (MeshEntity me : cps) {
            final Vector3f pos = me.getTransform().getTranslation();
            sb.append("bead at " + pos.x + " " + pos.y + " " + pos.z + " " + 0 + "\n");
        }
        TextFrame.showText("Beads", sb.toString(), this);
    }//GEN-LAST:event_exportButtonActionPerformed

    public void adjustBeads(int frame, int tag){
        final BeadsToAnimationForm b = mf.beadsToAnimationForm;
        final boolean allFramesVisible = b.isFramesAll();
        final boolean isHighlightCurrentFrame = b.isFrameHighlight();
        
        final boolean allNumbers = b.isNumberAll();
        final int onlyNumber = b.getNumber();
           
        final Scene scene = mf.getScene();
        final float scale = mf.cpPanel.getControlPointSize();
        
        for (MeshEntity me : scene.getEntities()){
            if ((me.getTag() & tag) != 0){
                me.setVisible( (allFramesVisible || me.getFrame() == frame) && 
                        (allNumbers || me.getNumber() == onlyNumber));
                if (isHighlightCurrentFrame){
                    if( me.getFrame() == frame ){
                        me.getTransform().getScale().set(scale * 1.5f);
                    }else{
                        me.getTransform().getScale().set(scale * 0.5f);
                    }
                }else{
                    //me.getTransform().getScale().copyTo(scale);
                }
            }
        } 
    }
    
    public void adjustSlicesAndCubes(int frame, int tag){
        final BeadsToAnimationForm b = mf.beadsToAnimationForm;
        final boolean allFramesVisible = b.isFramesAll();
        final boolean isHighlightCurrentFrame = b.isFrameHighlight();
        
        final boolean allNumbers = b.isNumberAll();
        final int onlyNumber = b.getNumber();
        
        final boolean followSlice = b.isSliceFollow();
        final boolean followSliceXYZ = b.isSliceFollowXYZ();
        final boolean followCubeX = b.isFollowCubeX();
        final boolean followCubeY = b.isFollowCubeY();
        final boolean followCubeZ = b.isFollowCubeZ();
        final boolean followCube = followCubeX || followCubeY || followCubeZ;
        
        final Scene scene = mf.getScene();
        
        if(followSlice || followCube ){
            Entity targetCP = null;
            
            for(MeshEntity me : scene.getEntities()){
                if ( (me.getTag() & tag) != 0 &&
                        ((allFramesVisible && !isHighlightCurrentFrame) || me.getFrame() == frame) && 
                        (allNumbers || me.getNumber() == onlyNumber)){
                        targetCP = me;
                        break;
                }
            }
            if ( targetCP != null){
                Vector3f pos = targetCP.getTransform().getTranslation();
                if( followCube ){
                    final Vector3f preCubeT = scene.getPreCubeVol().getTransform().getTranslation();
                    final Vector3f rtCubeT = scene.getRtCubeVol().getTransform().getTranslation();
                    final Vector3f rtIsoVol = scene.getRtIsoVol().getTransform().getTranslation();
                    if (followCubeX){
                        preCubeT.x = pos.x;
                        rtCubeT.x = pos.x;
                        rtIsoVol.x = pos.x;
                    } 
                    if(followCubeY){
                        preCubeT.y = pos.y;
                        rtCubeT.y = pos.y;
                        rtIsoVol.y = pos.y;
                    }
                    if(followCubeZ){
                        preCubeT.z = pos.z;
                        rtCubeT.z = pos.z;
                        rtIsoVol.z = pos.z;
                    }
                }
                if( followSlice ){
                    for(MeshEntity me : scene.getEntities()){
                        float[] rot = new float[3];
                        if((me.getTag() & Entity.TAG_SLICE) != 0){
                            if (followSliceXYZ){
                                me.getTransform().getTranslation().set( pos );
                            }else{
                                me.getTransform().getRotation().toAngles(rot);
                                if (FastMath.abs( rot[0] * FastMath.RAD_TO_DEG ) > 45.0f){
                                    me.getTransform().getTranslation().y =  pos.y;
                                }else if(FastMath.abs( rot[1] * FastMath.RAD_TO_DEG ) > 45.0f){
                                    me.getTransform().getTranslation().x =  pos.x;
                                } else{
                                    me.getTransform().getTranslation().z =  pos.z;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyPositionButton;
    private javax.swing.JCheckBox cubeXCheck;
    private javax.swing.JCheckBox cubeYCheck;
    private javax.swing.JCheckBox cubeZCheck;
    private javax.swing.JCheckBox enableCheck;
    private javax.swing.JCheckBox enablePerTickCheck;
    private javax.swing.JButton exportButton;
    private javax.swing.JRadioButton frameAllCheck;
    private javax.swing.JRadioButton frameCurrentCheck;
    private javax.swing.ButtonGroup frameGroup;
    private javax.swing.JCheckBox highlightCurrentFrameCheck;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton numberAllCheck;
    private javax.swing.ButtonGroup numberGroup;
    private javax.swing.JRadioButton numberOnlyCheck;
    private javax.swing.JTextField numberText;
    private javax.swing.JCheckBox sliceCheck;
    private javax.swing.JCheckBox sliceFollowsXYZCheck;
    private javax.swing.JButton snapPositionsButton;
    // End of variables declaration//GEN-END:variables
}
