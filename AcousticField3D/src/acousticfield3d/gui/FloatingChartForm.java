/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.algorithms.DirectHoloTwoSides;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.utils.Parse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asier
 */
public class FloatingChartForm extends javax.swing.JFrame {
    public final static float[][] DATA_SETS = {
        {47.55f, 43.34f,35.87f,	85.30f, 51.55f,	14.24f,	34.24f,	22.11f, 65.23f,	47.85f,	21.19f,	72.51f},
        {11.23f, 81.32f,26.51f,	60.26f, 15.55f,	90.00f,	83.44f,	25.14f, 49.66f,	36.43f,	91.84f,	52.45f},
        {53.79f,2.93f,	73.84f,	8.40f, 4.51f,	19.76f,	96.28f,	63.03f, 93.54f,	6.10f,	79.20f,	35.98f},
        {94.46f,60.58f,	15.80f,	46.78f, 68.68f,	52.99f,	67.33f,	6.01f, 92.27f,	67.32f,	8.07f,	45.51f},
        {12.40f,96.37f,	22.05f,	88.07f, 16.83f,	86.55f,	18.41f,	3.90f, 46.87f,	18.78f,	54.45f,	13.20f},
        {96.31f,26.52f,	33.03f,	26.08f, 14.46f,	41.19f,	79.31f,	93.34f, 20.85f,	0.82f,	62.66f,	98.89f}
    };
        
    final MainForm mf;
    
    final ArrayList<Vector3f> initialPosition = new ArrayList<>();
    final ArrayList<Entity> beads = new ArrayList<Entity>();
    
    public FloatingChartForm(MainForm mf) {
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

        algorithmTypeGroup = new javax.swing.ButtonGroup();
        snapBeadsButton = new javax.swing.JButton();
        resetBeadsButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        focalRadio = new javax.swing.JRadioButton();
        holoRadio = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        maxStepText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        updateEveryText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        rangeText = new javax.swing.JTextField();
        goButton = new javax.swing.JButton();
        randomButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        dataSetSpinner = new javax.swing.JSpinner();
        goToInitialPos = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        transPerBeadText = new javax.swing.JTextField();
        simultaneousCheck = new javax.swing.JCheckBox();
        doubleXCheck1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Control Plot");

        snapBeadsButton.setText("Snap Beads");
        snapBeadsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapBeadsButtonActionPerformed(evt);
            }
        });

        resetBeadsButton.setText("Reset Beads");
        resetBeadsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBeadsButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Algorithm:");

        algorithmTypeGroup.add(focalRadio);
        focalRadio.setText("Focal");

        algorithmTypeGroup.add(holoRadio);
        holoRadio.setSelected(true);
        holoRadio.setText("HoloPI");

        jLabel2.setText("Max step:");

        maxStepText.setText("0.0002");

        jLabel3.setText("Update Every:");

        updateEveryText.setText("0.1");

        jLabel4.setText("Range:");

        rangeText.setText("0 0.009 0");

        goButton.setText("Go");
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        randomButton.setText("Random");
        randomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("DataSet:");

        dataSetSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 6, 1));

        goToInitialPos.setText("Initial");
        goToInitialPos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToInitialPosActionPerformed(evt);
            }
        });

        jLabel6.setText("TransPerBead:");

        transPerBeadText.setText("14");

        simultaneousCheck.setText("Simultaneous");

        doubleXCheck1.setText("doubleX");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(randomButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(goToInitialPos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(goButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(transPerBeadText, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(snapBeadsButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(resetBeadsButton))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(focalRadio)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(holoRadio))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(maxStepText, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(updateEveryText, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(rangeText))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataSetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(simultaneousCheck)))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(227, Short.MAX_VALUE)
                    .addComponent(doubleXCheck1)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(snapBeadsButton)
                    .addComponent(resetBeadsButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(focalRadio)
                    .addComponent(holoRadio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(transPerBeadText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(maxStepText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(updateEveryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(rangeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(dataSetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(simultaneousCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(goButton)
                    .addComponent(randomButton)
                    .addComponent(goToInitialPos))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(62, 62, 62)
                    .addComponent(doubleXCheck1)
                    .addContainerGap(179, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void snapBeadsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapBeadsButtonActionPerformed
        initialPosition.clear();
        beads.clear();
        
        final ArrayList<Entity> selection = mf.getSelection();
        for(Entity e : selection){
            if((e.getTag() & Entity.TAG_CONTROL_POINT) != 0){
                beads.add(e);
                initialPosition.add( e.getTransform().getTranslation().clone() );
            }
        }
    }//GEN-LAST:event_snapBeadsButtonActionPerformed

    private void resetBeadsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBeadsButtonActionPerformed
        final int s = beads.size();
        for(int i = 0; i < s; ++i){
            beads.get(i).getTransform().getTranslation().set( initialPosition.get(i) );
        }
        mf.needUpdate();
    }//GEN-LAST:event_resetBeadsButtonActionPerformed

    private void randomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomButtonActionPerformed
        final ArrayList<Vector3f> targetPos = Vector3f.cloneVector(initialPosition);
        final Vector3f range = new Vector3f().parse(rangeText.getText());
        final int s = beads.size();

        for (int i = 0; i < s; ++i) {
            targetPos.get(i).addLocal( 
                    FastMath.random(-range.x, range.x),
                    FastMath.random(-range.y, range.y),
                    FastMath.random(-range.z, range.z));
        }
        goToPositions(targetPos);
    }//GEN-LAST:event_randomButtonActionPerformed

    private void goToInitialPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToInitialPosActionPerformed
        goToPositions(initialPosition);
    }//GEN-LAST:event_goToInitialPosActionPerformed

    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
        final int nData = (Integer) dataSetSpinner.getValue() - 1;
        final float[] data = DATA_SETS[nData];
        
        //transform range and add initial positions
        final Vector3f range = new Vector3f().parse( rangeText.getText() );
        
        final ArrayList<Vector3f> targetPos = Vector3f.cloneVector(initialPosition);
        final int s = beads.size();

        for (int i = 0; i < s; ++i) {
            final float value = data[i];
            final float changeY = (value - 50.0f) * range.y / 50.0f;  
            targetPos.get(i).y += changeY;
        }
        
        goToPositions(targetPos);
    }//GEN-LAST:event_goButtonActionPerformed

    public boolean getUseFocal(){
        return focalRadio.isSelected();
    }
    public int getTransPerBead(){
        return Parse.stringToInt( transPerBeadText.getText() );
    }
    public boolean getUseDoubleXRange(){
        return simultaneousCheck.isSelected();
    }
    
    private void goToPositions(final ArrayList<Vector3f> targets) {
        final boolean useFocal = getUseFocal();
        final float maxStep = Parse.stringToFloat( maxStepText.getText() );
        final float updateEvery = Parse.stringToFloat( updateEveryText.getText() );
        final int transPerBead = getTransPerBead();
        final boolean useDoubleXForDistance = getUseDoubleXRange();
        final boolean simultaneous = simultaneousCheck.isSelected();
        
        if (!simultaneous){
            //create sets with beads and targets
            final int beadsPerSet = 4;
            final int size = Math.min(beads.size(), targets.size());
            final int nSets = size / beadsPerSet;
            
           final ArrayList<Entity> cBeads = new ArrayList<>(beadsPerSet);
            final ArrayList<Vector3f> cTargets = new ArrayList<>(beadsPerSet);
            
            int index = 0;
            for(int i = 0; i < nSets; ++i){
                cBeads.clear(); cTargets.clear();
                for(int j = 0; j < beadsPerSet; ++j){
                    cBeads.add( beads.get(index) );
                    cTargets.add( targets.get(index) );
                    index++;
                }
                
                while (true) {
                    //calc the algoritm
                    for (Entity e : cBeads) {
                        DirectHoloTwoSides.calcWithTransducers(mf, e, transPerBead, !useFocal, useDoubleXForDistance);
                    }

                    //send
                    mf.transControlPanel.sendSerialDataOfBoard( i );

                    boolean allReachedTarget = moveTowardsTargets(maxStep, cBeads, cTargets);
                    
                    try {
                        //wait
                        Thread.sleep((long) (updateEvery * 1000));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FloatingChartForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    if (allReachedTarget) {
                        break;
                    }
                }
            }
            
            
            
        }else{
            final ArrayList<Entity> cBeads = new ArrayList<>(beads);
            final ArrayList<Vector3f> cTarges = new ArrayList<>(targets);

            while (true) {
                //calc the algoritm
                for (Entity e : cBeads) {
                    DirectHoloTwoSides.calcWithTransducers(mf, e, transPerBead, !useFocal, useDoubleXForDistance);
                }

                //send
                mf.transControlPanel.sendData();

                boolean allReachedTarget = moveTowardsTargets(maxStep, cBeads, cTarges);
                if (allReachedTarget) {
                    break;
                }

                try {
                    //wait
                    Thread.sleep((long) (updateEvery * 1000));
                } catch (InterruptedException ex) {
                    Logger.getLogger(FloatingChartForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        mf.needUpdate();
    }

    private boolean moveTowardsTargets(final float maxStep,
            final ArrayList<Entity> cBeads, 
            final ArrayList<Vector3f> cTargets) {
        
        boolean allTargestsReached = true;
        final int size = Math.min(cBeads.size(), cTargets.size());
        
        //calc new positions of the beads
        //are the beads at the target position -> break
        for (int i = 0; i < size; ++i) {
            final Entity e = cBeads.get(i);
            final Vector3f cPos = e.getTransform().getTranslation();
            final Vector3f cTarget = cTargets.get(i);
            
            final Vector3f diff = cTarget.subtract(cPos);
            float dist = diff.length();
            if (dist > 0.0001f) {
                allTargestsReached = false;
                diff.divideLocal(dist);
                dist = FastMath.min(dist, maxStep);
                cPos.addLocalInc(diff, dist);
            }
        }
        return allTargestsReached;
    }
    
    private boolean moveToTarget(){
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup algorithmTypeGroup;
    private javax.swing.JSpinner dataSetSpinner;
    private javax.swing.JCheckBox doubleXCheck1;
    private javax.swing.JRadioButton focalRadio;
    private javax.swing.JButton goButton;
    private javax.swing.JButton goToInitialPos;
    private javax.swing.JRadioButton holoRadio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField maxStepText;
    private javax.swing.JButton randomButton;
    private javax.swing.JTextField rangeText;
    private javax.swing.JButton resetBeadsButton;
    private javax.swing.JCheckBox simultaneousCheck;
    private javax.swing.JButton snapBeadsButton;
    private javax.swing.JTextField transPerBeadText;
    private javax.swing.JTextField updateEveryText;
    // End of variables declaration//GEN-END:variables

    
}
