/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui.panels;

import acousticfield3d.gui.MainForm;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Resources;
import acousticfield3d.scene.Scene;
import acousticfield3d.shapes.Quad;
import acousticfield3d.utils.Parse;
import java.util.ArrayList;

/**
 *
 * @author Asier
 */
public class RtSlicePanel extends javax.swing.JPanel {
    
    public static final int Combo_Amplitude = 0;
    public static final int Combo_Phase = 1;
    public static final int Combo_PhaseAndAmp = 2;
    public static final int Combo_Gorkov = 3;
    public static final int Combo_LaplacianGorkov = 4;
    public static final int Combo_InstantAmplitude = 5;
    public static final int Combo_AmplitudeHeight = 6;
    public static final int Combo_GorkovHeight = 7;
    public static final int Combo_ColourGradient = 8;
    public static final int Combo_ForceX = 9;
    public static final int Combo_ForceY = 10;
    public static final int Combo_ForceZ = 11;
    public static final int Combo_ExternalAmplitude = 12;
    public static final int Combo_ExternalPhase = 13;
    public static final int Combo_ExtHeightAmp = 14;
    public static final int Combo_PreSlice = 15;
    public static final int Combo_TAmpDiffFr= 16;

    private MainForm mf;
    
    public RtSlicePanel(MainForm mf) {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel34 = new javax.swing.JLabel();
        gradientDivText = new javax.swing.JTextField();
        gradientSizeCombo = new javax.swing.JComboBox();
        jLabel40 = new javax.swing.JLabel();
        sliceAddButton = new javax.swing.JButton();
        sliceDelButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        colAmpMinText = new javax.swing.JTextField();
        addDonutRTButton = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        rtSliceAlphaText = new javax.swing.JTextField();
        rtSliceEnableCheck = new javax.swing.JCheckBox();
        jLabel35 = new javax.swing.JLabel();
        colGorPosMinText = new javax.swing.JTextField();
        colAmpMaxText = new javax.swing.JTextField();
        colGorPosMaxText = new javax.swing.JTextField();
        colGorNegMinText = new javax.swing.JTextField();
        colGorNegMaxText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        colouringCombo = new javax.swing.JComboBox();
        heightMapButton = new javax.swing.JButton();
        heightDivs = new javax.swing.JTextField();
        heightGainText = new javax.swing.JTextField();
        force1Check = new javax.swing.JCheckBox();
        force2Check = new javax.swing.JCheckBox();
        sliceSourceCombo = new javax.swing.JComboBox();

        jLabel34.setText("GradDiv(lambda/X)");

        gradientDivText.setText("5");
        gradientDivText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradientDivTextActionPerformed(evt);
            }
        });

        gradientSizeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3", "5", "9" }));
        gradientSizeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradientSizeComboActionPerformed(evt);
            }
        });

        jLabel40.setText("Stencil size:");

        sliceAddButton.setText("Add");
        sliceAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sliceAddButtonActionPerformed(evt);
            }
        });

        sliceDelButton.setText("Del");
        sliceDelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sliceDelButtonActionPerformed(evt);
            }
        });

        jLabel11.setText("AmpCol:");

        colAmpMinText.setText("0");
        colAmpMinText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colAmpMinTextActionPerformed(evt);
            }
        });

        addDonutRTButton.setText("Donut");
        addDonutRTButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDonutRTButtonActionPerformed(evt);
            }
        });

        jLabel32.setText("Alpha:");

        rtSliceAlphaText.setText("1.0");
        rtSliceAlphaText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rtSliceAlphaTextActionPerformed(evt);
            }
        });

        rtSliceEnableCheck.setSelected(true);
        rtSliceEnableCheck.setText("enable");
        rtSliceEnableCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rtSliceEnableCheckActionPerformed(evt);
            }
        });

        jLabel35.setText("Color Gorkov(e-10) Laplacian(e-4):");

        colGorPosMinText.setText("0");
        colGorPosMinText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colGorPosMinTextActionPerformed(evt);
            }
        });

        colAmpMaxText.setText("1500");
        colAmpMaxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colAmpMaxTextActionPerformed(evt);
            }
        });

        colGorPosMaxText.setText("100");
        colGorPosMaxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colGorPosMaxTextActionPerformed(evt);
            }
        });

        colGorNegMinText.setText("-100");
        colGorNegMinText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colGorNegMinTextActionPerformed(evt);
            }
        });

        colGorNegMaxText.setText("0");
        colGorNegMaxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colGorNegMaxTextActionPerformed(evt);
            }
        });

        jLabel1.setText("Colouring:");

        colouringCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "linear fire", "grey", "sine16grey", "cosine fire" }));
        colouringCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colouringComboActionPerformed(evt);
            }
        });

        heightMapButton.setText("Height");
        heightMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightMapButtonActionPerformed(evt);
            }
        });

        heightDivs.setText("128");
        heightDivs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightDivsActionPerformed(evt);
            }
        });

        heightGainText.setText("10000000");
        heightGainText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightGainTextActionPerformed(evt);
            }
        });

        force1Check.setSelected(true);
        force1Check.setText("1");
        force1Check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                force1CheckActionPerformed(evt);
            }
        });

        force2Check.setSelected(true);
        force2Check.setText("2");
        force2Check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                force2CheckActionPerformed(evt);
            }
        });

        sliceSourceCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Amplitude", "Phase", "Phase&Amp", "Gorkov", "Laplacian Gorkov", "Instant Amplitude", "Amplitude Height", "Gorkov Height", "Colour gradient", "ForceX", "ForceY", "ForceZ", "External Amplitude", "External Phase", "Ext Height Amp", "PreCube", "T Amp diff FR" }));
        sliceSourceCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sliceSourceComboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sliceSourceCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sliceAddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addDonutRTButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sliceDelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rtSliceEnableCheck)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rtSliceAlphaText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colAmpMinText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colAmpMaxText))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(colGorNegMinText)
                            .addComponent(colGorPosMinText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(colGorPosMaxText)
                            .addComponent(colGorNegMaxText)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colouringCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(heightMapButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(heightDivs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(heightGainText))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(force1Check)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(force2Check)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rtSliceEnableCheck)
                    .addComponent(jLabel32)
                    .addComponent(rtSliceAlphaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(colouringCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(colAmpMinText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colAmpMaxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colGorNegMinText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colGorNegMaxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colGorPosMinText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colGorPosMaxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(sliceSourceCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(force1Check)
                    .addComponent(force2Check))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heightMapButton)
                    .addComponent(heightDivs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(heightGainText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sliceAddButton)
                    .addComponent(sliceDelButton)
                    .addComponent(addDonutRTButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sliceAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sliceAddButtonActionPerformed
        MeshEntity me = createSlice();
        mf.addMeshEntityToSceneCenter(me);
        mf.needUpdate();
    }//GEN-LAST:event_sliceAddButtonActionPerformed

    public MeshEntity createSlice(){
        MeshEntity me = new MeshEntity(Resources.MESH_QUAD, null, getSliceShader());
        me.setTag( Entity.TAG_SLICE );
        return me;
    }
    
    private void sliceDelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sliceDelButtonActionPerformed

        ArrayList<Entity> slices = new ArrayList<>();
        for( Entity e : mf.selection){
            if ( e.getTag() == Entity.TAG_SLICE) { slices.add( e ); }
        }

        //delete transducers from animation->animationKeys
        /*
        for(Animation anim : simulation.animations.getElements()){
            anim.deleteTransducers(slices);
        }*/

        //Remove from simulation and selection
        for (Entity t : slices){
            mf.scene.getEntities().remove( t );
            mf.selection.remove(t);
        }

        mf.needUpdate();
    }//GEN-LAST:event_sliceDelButtonActionPerformed

    private void colAmpMinTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colAmpMinTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colAmpMinTextActionPerformed

    private void addDonutRTButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDonutRTButtonActionPerformed
        MeshEntity me = new MeshEntity(Resources.MESH_DONUT, null, getSliceShader());
        me.setTag(Entity.TAG_SLICE);
        mf.addMeshEntityToSceneCenter(me);
        mf.needUpdate();
    }//GEN-LAST:event_addDonutRTButtonActionPerformed

    private void rtSliceAlphaTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rtSliceAlphaTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_rtSliceAlphaTextActionPerformed

    private void rtSliceEnableCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rtSliceEnableCheckActionPerformed
        Scene.setVisible( mf.scene.getEntities(), Entity.TAG_SLICE, rtSliceEnableCheck.isSelected() );
        mf.needUpdate();
    }//GEN-LAST:event_rtSliceEnableCheckActionPerformed

    private void gradientDivTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradientDivTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_gradientDivTextActionPerformed

    private void colGorPosMinTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colGorPosMinTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colGorPosMinTextActionPerformed

    private void gradientSizeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradientSizeComboActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_gradientSizeComboActionPerformed

    private void colAmpMaxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colAmpMaxTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colAmpMaxTextActionPerformed

    private void colGorPosMaxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colGorPosMaxTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colGorPosMaxTextActionPerformed

    private void colGorNegMinTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colGorNegMinTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colGorNegMinTextActionPerformed

    private void colGorNegMaxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colGorNegMaxTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colGorNegMaxTextActionPerformed

    private void colouringComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colouringComboActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colouringComboActionPerformed

    private void heightGainTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightGainTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_heightGainTextActionPerformed

    public int getHeightDivs(){
        return Parse.stringToInt( heightDivs.getText() );
    }
    
    private void assignMeshToHeight(MeshEntity me){
        final int d = getHeightDivs();
        if (d == Resources.MESH_GRID_DIVS) {
            me.setMesh(Resources.MESH_GRID);
            me.customMesh = null;
        } else {
            me.setMesh(Resources.MESH_CUSTOM);
            me.customMesh = new Quad(1.0f, 1.0f, d, false);
        }
    }
    
    private void heightDivsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightDivsActionPerformed
        for(Entity e : mf.selection){
            if (e.getTag() == Entity.TAG_SLICE){
                if (e instanceof MeshEntity){
                    MeshEntity me = (MeshEntity)e;
                    assignMeshToHeight(me);
                }
            }
        }
        mf.needUpdate();
    }//GEN-LAST:event_heightDivsActionPerformed

    private void heightMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightMapButtonActionPerformed
        MeshEntity me = new MeshEntity(Resources.MESH_GRID, null, getSliceShader());
        me.setTag(Entity.TAG_SLICE);
        assignMeshToHeight( me );
        mf.addMeshEntityToSceneCenter(me);
        mf.needUpdate();
    }//GEN-LAST:event_heightMapButtonActionPerformed

    private void force1CheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_force1CheckActionPerformed
        assignSliceSources();
        mf.needUpdate();
    }//GEN-LAST:event_force1CheckActionPerformed

    private void force2CheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_force2CheckActionPerformed
        assignSliceSources();
        mf.needUpdate();
    }//GEN-LAST:event_force2CheckActionPerformed

    private void sliceSourceComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sliceSourceComboActionPerformed
        assignSliceSources();
        mf.needUpdate();
    }//GEN-LAST:event_sliceSourceComboActionPerformed

    public boolean isForce1(){
        return force1Check.isSelected();
    }
    public boolean isForce2(){
        return force2Check.isSelected();
    }
    
    public int getSelectSliceSource(){
        return sliceSourceCombo.getSelectedIndex();
    }
    
    private int getSliceShader(){
        final int i = getSelectSliceSource();
        
        if (i == Combo_Amplitude){
            return Resources.SHADER_SLICE_RT_AMP;
        }else if (i == Combo_Phase){
            return Resources.SHADER_SLICE_RT_PHASE;
        }else if (i == Combo_PhaseAndAmp){
            return Resources.SHADER_SLICE_RT_AMPPHASE;
        }else if (i == Combo_Gorkov){
            return Resources.SHADER_SLICE_RT_GORKOV;
        }else if (i == Combo_LaplacianGorkov){
            return Resources.SHADER_SLICE_RT_GORKOV_LAPLACIAN;
        }else if (i == Combo_GorkovHeight){
            return Resources.SHADER_SLICE_RT_GORKOV_HEIGHT;
        }else if(i == Combo_AmplitudeHeight){
            return Resources.SHADER_SLICE_RT_AMP_HEIGHT;
        }else if(i == Combo_ForceX || i == Combo_ForceY || i == Combo_ForceZ){
            return Resources.SHADER_SLICE_RT_GORKOV_FORCE;
        }else if( i == Combo_ColourGradient ){
            return Resources.SHADER_SLICE_COLORBAR;
        }else if( i == Combo_InstantAmplitude){
            return Resources.SHADER_SLICE_RT_TAMP;
        }else if ( i == Combo_ExternalAmplitude ){
            return Resources.SHADER_SLICE_EXTERNAL_AMP;
        }else if (i == Combo_ExternalPhase ){
            return Resources.SHADER_SLICE_EXTERNAL_PHASE;
        }else if (i == Combo_ExtHeightAmp){
            return Resources.SHADER_HEIGHT_EX_AMP;
        }else if (i == Combo_PreSlice){
            return Resources.SHADER_SLICE_PRE;
        }else if (i == Combo_TAmpDiffFr){
            return Resources.SHADER_SLICE_RT_TAMPDIFFFR;
        }
        
        return Resources.SHADER_SLICE_RT_AMP;
    }
        
    private void assignSliceSources(){
        int shader = getSliceShader();
        for(Entity e : mf.selection){
            if (e.getTag() == Entity.TAG_SLICE){
                if (e instanceof MeshEntity){
                    MeshEntity me = (MeshEntity)e;
                    me.setShader( shader );
                }
            }
        }
    }
    
    public int getColouringCombo(){
        return colouringCombo.getSelectedIndex();
    }
            
    public float getAmpColorMin() {
        return Parse.stringToFloat( colAmpMinText.getText() );
    }
    
    public float getAmpColorMax() {
        return Parse.stringToFloat( colAmpMaxText.getText() );
    }
    
    final static float divGorkovColor = 10000000000.0f;
    public float getGorkovNegColorMin(){
        return Parse.stringToFloat( colGorNegMinText.getText() ) / divGorkovColor;
    }
    
    public float getGorkovNegColorMax(){
        return Parse.stringToFloat( colGorNegMaxText.getText() ) / divGorkovColor;
    }
    public float getGorkovPosColorMin(){
        return Parse.stringToFloat( colGorPosMinText.getText() ) / divGorkovColor;
    }
    
    public float getGorkovPosColorMax(){
        return Parse.stringToFloat( colGorPosMaxText.getText() ) / divGorkovColor;
    }
    
    public float getRTSliceAlpha(){
        return Parse.stringToFloat( rtSliceAlphaText.getText() );
    }
    
    public float getGradientDivs(){
        return Parse.stringToFloat( gradientDivText.getText() );
    }
    
    public int getGradientSize(){
        return gradientSizeCombo.getSelectedIndex() + 1;
    }
    
    public float getHeightGain(){
        return Parse.stringToFloat( heightGainText.getText() );
    }
    
    public int getXYZPlot() {
        final int i = getSelectSliceSource();
        
        if(i == Combo_ForceX){
            return 0;
        }else if(i == Combo_ForceY){
            return 1;
        }else if(i == Combo_ForceZ){
            return 2;
        }
        return 0;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDonutRTButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField colAmpMaxText;
    private javax.swing.JTextField colAmpMinText;
    private javax.swing.JTextField colGorNegMaxText;
    private javax.swing.JTextField colGorNegMinText;
    private javax.swing.JTextField colGorPosMaxText;
    private javax.swing.JTextField colGorPosMinText;
    private javax.swing.JComboBox colouringCombo;
    private javax.swing.JCheckBox force1Check;
    private javax.swing.JCheckBox force2Check;
    private javax.swing.JTextField gradientDivText;
    private javax.swing.JComboBox gradientSizeCombo;
    private javax.swing.JTextField heightDivs;
    private javax.swing.JTextField heightGainText;
    private javax.swing.JButton heightMapButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JTextField rtSliceAlphaText;
    private javax.swing.JCheckBox rtSliceEnableCheck;
    private javax.swing.JButton sliceAddButton;
    private javax.swing.JButton sliceDelButton;
    private javax.swing.JComboBox sliceSourceCombo;
    // End of variables declaration//GEN-END:variables

    
}
