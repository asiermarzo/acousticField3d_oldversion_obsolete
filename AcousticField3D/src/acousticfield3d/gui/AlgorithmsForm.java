/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.algorithms.Algorithm;
import acousticfield3d.algorithms.BFGSOptimization;
import acousticfield3d.algorithms.TimeReversal;
import acousticfield3d.algorithms.bfgs.BFGSProgressListener;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Quaternion;
import acousticfield3d.math.Transform;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.simulation.Simulation;
import acousticfield3d.simulation.Transducer;
import acousticfield3d.utils.Parse;
import java.util.ArrayList;

/**
 *
 * @author Asier
 */
public class AlgorithmsForm extends javax.swing.JFrame implements BFGSProgressListener{
    MainForm mf;
    private final ArrayList<Algorithm> enabledAlgorithms;
    private final ArrayList<Algorithm> algorithms;
    
    private double[] lastX;
    private double lastFX;
    private double[] currentX;
    private double currentFX;
    private double[] X;
    private double FX;
            
    private int reportEvery = 1;
    public AlgorithmsForm(MainForm mf) {
        this.mf = mf;
        algorithms = new ArrayList<>();
        enabledAlgorithms = new ArrayList<>();
        initComponents();
        
        algorithms.add( new TimeReversal( this ) );
        algorithms.add( new BFGSOptimization( this ) );
    }

    public MainForm getMf() {
        return mf;
    }
    
    private Algorithm getAlgorithm(int index){
        return algorithms.get(index);
    }
    
    public void calcAlgorithm(int index, boolean additionalOptions, boolean updateAfter){
        calcAlgorithm ( getAlgorithm(index), additionalOptions );
        
        if ( updateAfter ){
            mf.needUpdate();
        }
    }
    
    public void setBarValue(int p){
        progressBar.setValue( p );
    }
    
    public int getSteps(){
        return Parse.stringToInt( stepsText.getText() );
    }
    
    public int getReportEvery(){
        return Parse.stringToInt( reportEveryText.getText() );
    }
    
    public double getXMin(){
        return Parse.stringToDouble( xMinText.getText() );
    }
    
    public double getGMin(){
        return Parse.stringToDouble( gMinText.getText() );
    }
    
    public boolean isEqualize(){
        return equalizerCheck.isSelected();
    }
    
    public double getEqualizeConstant(){
        return Parse.stringToDouble( equalizerText.getText() );
    }
    
    public Vector3f getLaplacianConstants(){
        return new Vector3f().parse( laplacianConstantsText.getText() );
    }
    
    public float getRefAmplitude(){
        return Parse.stringToFloat( refPressureText.getText() );
    }
    
    public float getRotY(){
        return Parse.stringToFloat( rotYText.getText() );
    }
    public float getRotZ(){
        return Parse.stringToFloat( rotZText.getText() );
    }
    public void setRotY(float r){
        rotYText.setText( r + "");
    }
    public void setRotZ(float r){
        rotZText.setText( r + "");
    }
    
    public boolean isPressure(){
        return pressureCheck.isSelected();
    }
    public boolean isIndPressure(){
        return multiPressureCheck.isSelected();
    }
    public boolean isGorkov(){
        return gorkovCheck.isSelected();
    }
    public boolean isForce(){
        return forceCheck.isSelected();
    }
    public boolean isBottle(){
        return bottleCheck.isSelected();
    }
    public boolean isGLaplacian(){
        return maxGLaplacian.isSelected();
    }

    public double getAlpha(){
        return Parse.stringToDouble( alphaText.getText() );
    }
    
    public double getLowPressureK(){
        return Parse.stringToDouble( lowPressureKText.getText() );
    }
    
    public float getCentralWeight(){
        return Parse.stringToFloat( centralWText.getText() );
    }
    
    public Vector3f getWeightsN(){
        return new Vector3f().parse( weightsNText.getText() );
    }
    
    public Vector3f getWeightsP(){
        return new Vector3f().parse( weightsPText.getText() );
    }
    
    public Vector3f getDistances(){
        return new Vector3f().parse( distancesText.getText() );
    }
    
    public boolean isBasinHopping(){
        return basinCheck.isSelected();
    }
    
    public int basinSteps(){
        return Parse.stringToInt( basinStepsText.getText() );
    }
    
    public double basinStepSize(){
        return Parse.stringToDouble(basinDiffXText.getText() );
    }
    public double basinTemperature(){
        return Parse.stringToDouble( basinTText.getText() );
    }
    
    public void enableAlgorithm(int index, boolean enabled){
        //gather and set control points
        Algorithm a = getAlgorithm(index);
        if (enabled){
            if(! enabledAlgorithms.contains( a )){
                a.controlPoints.clear();
                mf.scene.gatherMeshEntitiesWithTag( a.controlPoints, Entity.TAG_CONTROL_POINT);
                enabledAlgorithms.add( a );
            }
        }else{
             enabledAlgorithms.remove( getAlgorithm(index) );
        }
    }
    
    public void tickEnabledAlgorithms(){
        for(Algorithm a : enabledAlgorithms){
            a.calc(mf, mf.simulation);
        }
    }
    
    private void calcAlgorithm(Algorithm a, boolean additionalOptions){
        if(a != null){
            //gather the beads
            a.controlPoints.clear();
            for(Entity e : mf.getSelection()){
                if( (e instanceof MeshEntity) && ((e.getTag() & Entity.TAG_CONTROL_POINT) != 0)){
                    a.controlPoints.add((MeshEntity) e );
                }
            }
            //mf.scene.gatherMeshEntitiesWithTag( a.controlPoints, Entity.TAG_CONTROL_POINT);
            
            final Entity firstBead = a.controlPoints.get(0);
            final boolean rotate = rotateCheck.isSelected();
            final boolean onlySel = onlySelectedCheck.isSelected();
            final Simulation sim = mf.simulation;
            final float ry = Parse.stringToFloat( rotYText.getText() ) * FastMath.DEG_TO_RAD;
            final float rz = Parse.stringToFloat( rotZText.getText() ) * FastMath.DEG_TO_RAD;
            final boolean useKickStarter = kickStartCheck.isSelected();
            final int steps = Parse.stringToInt( stepsText.getText() );
            
            if (additionalOptions){
                if ( useKickStarter ) {
                    calcAlgorithm( getAlgorithm(0), false ); //focalize
                }
                
                if (rotate || onlySel){
                    if(rotate){
                       //transform transducers
                       for(Transducer e : sim.getTransducers()){
                           e.snapTransform();
                           e.rotateAround(firstBead, 0, ry, rz);
                       }
                    }
                    if(onlySel){
                       for(Transducer t : sim.getTransducers()){
                           t.setPAmplitude( 0.0f );
                       }
                       for (Entity e : mf.getSelection()){
                           if (e instanceof Transducer){
                               Transducer t = (Transducer)e;
                               t.setPAmplitude( 1.0f );
                           }
                       }
                    }
                }
                
                if ( useKickStarter ) {
                    if (holoMemoryCheck.isSelected()){
                        mf.holoPatternsForm.addMemorizedHoloPattern();
                    }else{
                        mf.holoPatternsForm.addCurrentPattern();
                    }
                }
                
                mf.renderer.updateTransducersBuffers( sim );
            }
            
            if (steps > 0 || !additionalOptions){
                a.calc(mf, mf.simulation);
            }
            
            if (additionalOptions && rotate){
                //untransform transducers
                for(Transducer e : sim.getTransducers()){
                      e.restoreTransform();
                }
            }
            
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        varToOptimizeGroup = new javax.swing.ButtonGroup();
        bottleCheck = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        distancesText = new javax.swing.JTextField();
        centralWText = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        weightsPText = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        weightsNText = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        groupHoloOrigin = new javax.swing.ButtonGroup();
        forceCheck = new javax.swing.JRadioButton();
        okButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        singleCalcButton = new javax.swing.JButton();
        singleEnableButton = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        calcBFGSButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        stepsText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        xMinText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        gMinText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        area = new javax.swing.JTextArea();
        clearAreaButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        reportEveryText = new javax.swing.JTextField();
        pressureCheck = new javax.swing.JRadioButton();
        gorkovCheck = new javax.swing.JRadioButton();
        lowPressureKText = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        maxGLaplacian = new javax.swing.JRadioButton();
        equalizerCheck = new javax.swing.JCheckBox();
        equalizerText = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        laplacianConstantsText = new javax.swing.JTextField();
        diffButton = new javax.swing.JButton();
        basinCheck = new javax.swing.JCheckBox();
        basinStepsText = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        basinDiffXText = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        basinTText = new javax.swing.JTextField();
        snapButton = new javax.swing.JButton();
        rotateCheck = new javax.swing.JCheckBox();
        rotYText = new javax.swing.JTextField();
        onlySelectedCheck = new javax.swing.JCheckBox();
        rotZText = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        alphaText = new javax.swing.JTextField();
        kickStartCheck = new javax.swing.JCheckBox();
        holoMemoryCheck = new javax.swing.JRadioButton();
        holoAddCheck = new javax.swing.JRadioButton();
        multiPressureCheck = new javax.swing.JRadioButton();
        jLabel16 = new javax.swing.JLabel();
        refPressureText = new javax.swing.JTextField();

        jButton1.setText("jButton1");

        jButton2.setText("jButton2");

        varToOptimizeGroup.add(bottleCheck);
        bottleCheck.setText("bottle");

        jLabel7.setText("distances:");

        distancesText.setText("10E-3 10E-3 10E-3");

        centralWText.setText("4");

        jLabel9.setText("Central Weight:");

        weightsPText.setText("-1 0 -1");

        jLabel11.setText("weightsP:");

        weightsNText.setText("-1 -4 -1");

        jLabel8.setText("weightsN:");

        varToOptimizeGroup.add(forceCheck);
        forceCheck.setText("Force");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Algorithms");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Single Focal Point");

        singleCalcButton.setText("Calc");
        singleCalcButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleCalcButtonActionPerformed(evt);
            }
        });

        singleEnableButton.setText("Enable");
        singleEnableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleEnableButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("BFGS");

        calcBFGSButton.setText("Calc");
        calcBFGSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcBFGSButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Steps:");

        stepsText.setText("3000");

        jLabel4.setText("xMin:");

        xMinText.setText("1E-25");

        jLabel5.setText("gMin:");

        gMinText.setText("1E-25");

        area.setColumns(20);
        area.setRows(5);
        jScrollPane1.setViewportView(area);

        clearAreaButton.setText("Clear");
        clearAreaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAreaButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("report every:");

        reportEveryText.setText("1000");

        varToOptimizeGroup.add(pressureCheck);
        pressureCheck.setText("Pressure");

        varToOptimizeGroup.add(gorkovCheck);
        gorkovCheck.setText("MinGorkov");

        lowPressureKText.setText("1");

        jLabel10.setText("LowPK");

        varToOptimizeGroup.add(maxGLaplacian);
        maxGLaplacian.setSelected(true);
        maxGLaplacian.setText("MaxLapMinAmp");

        equalizerCheck.setText("equalizer");

        equalizerText.setText("1");

        jLabel12.setText("WeightsXYZ:");

        laplacianConstantsText.setText("1.0 1.0 1.0");

        diffButton.setText("Diff");
        diffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffButtonActionPerformed(evt);
            }
        });

        basinCheck.setText("BasinH");

        basinStepsText.setText("100");

        jLabel14.setText("diffX(*t)");

        basinDiffXText.setText("0.1");

        jLabel15.setText("T:");

        basinTText.setText("100");

        snapButton.setText("Snap");
        snapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapButtonActionPerformed(evt);
            }
        });

        rotateCheck.setText("rotYZ");

        rotYText.setText("0.0");

        onlySelectedCheck.setText("only selected");

        rotZText.setText("0.0");

        jLabel13.setText("Alpha:");

        alphaText.setText("1.0");

        kickStartCheck.setText("KickStart");

        groupHoloOrigin.add(holoMemoryCheck);
        holoMemoryCheck.setSelected(true);
        holoMemoryCheck.setText("holoMemory");

        groupHoloOrigin.add(holoAddCheck);
        holoAddCheck.setText("holoAdd");

        varToOptimizeGroup.add(multiPressureCheck);
        multiPressureCheck.setText("MPre");

        jLabel16.setText("RefPressure");

        refPressureText.setText("1000");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(clearAreaButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xMinText, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gMinText, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(alphaText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stepsText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reportEveryText, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(calcBFGSButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(diffButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(snapButton, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(basinCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(basinStepsText, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(basinDiffXText, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(basinTText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laplacianConstantsText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lowPressureKText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rotateCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rotYText, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rotZText, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(onlySelectedCheck))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pressureCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(multiPressureCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(gorkovCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxGLaplacian))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(singleCalcButton)
                                .addGap(18, 18, 18)
                                .addComponent(singleEnableButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(kickStartCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(holoMemoryCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(holoAddCheck)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(equalizerCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(equalizerText, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refPressureText)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(singleCalcButton)
                    .addComponent(singleEnableButton)
                    .addComponent(jLabel1))
                .addGap(3, 3, 3)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(calcBFGSButton)
                            .addComponent(diffButton)
                            .addComponent(basinCheck)
                            .addComponent(basinStepsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(snapButton))))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(basinDiffXText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(basinTText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(stepsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(reportEveryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(xMinText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(gMinText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(alphaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pressureCheck)
                    .addComponent(gorkovCheck)
                    .addComponent(maxGLaplacian)
                    .addComponent(multiPressureCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(laplacianConstantsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lowPressureKText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(equalizerCheck)
                    .addComponent(equalizerText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(refPressureText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rotateCheck)
                    .addComponent(rotYText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(onlySelectedCheck)
                    .addComponent(rotZText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kickStartCheck)
                    .addComponent(holoMemoryCheck)
                    .addComponent(holoAddCheck))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(clearAreaButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        setVisible( false );
    }//GEN-LAST:event_okButtonActionPerformed

    private void singleCalcButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleCalcButtonActionPerformed
        calcFocal();
    }//GEN-LAST:event_singleCalcButtonActionPerformed

    private void calcBFGSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcBFGSButtonActionPerformed
        runBFGS( true, true );
    }//GEN-LAST:event_calcBFGSButtonActionPerformed

    public void runBFGS(boolean runInParallel, boolean updateAfter){
        if (runInParallel){
            reportEvery = getReportEvery();
            WorkingCalc wc = new WorkingCalc();
            wc.start();
        }else{
            calcAlgorithm(1, true, updateAfter);
        }
        
    }
    
    private void singleEnableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleEnableButtonActionPerformed
        enableAlgorithm(0, singleEnableButton.isSelected());
    }//GEN-LAST:event_singleEnableButtonActionPerformed

    private void clearAreaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAreaButtonActionPerformed
        area.setText( "" );
    }//GEN-LAST:event_clearAreaButtonActionPerformed

    private void diffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffButtonActionPerformed
        final double diffFX = currentFX - lastFX;
        double dist = 0.0;
        
        double avgDiff = 0.0;
        final int s = lastX.length;
        for(int i = 0; i < s; ++i){
            final double d = FastMath.angleDiff((float)currentX[i], (float)lastX[i]);
            avgDiff += d;
            dist += d*d;
        }
        avgDiff /= s;
        dist -= avgDiff*avgDiff*s;
        dist = Math.sqrt( dist ) / s;
        
        area.append("DiffFX " + diffFX + " diffX per t " + dist + "\n");
    }//GEN-LAST:event_diffButtonActionPerformed

    private void snapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapButtonActionPerformed
        lastFX = currentFX;
        lastX = currentX;
        currentFX = FX;
        currentX = X;
    }//GEN-LAST:event_snapButtonActionPerformed

    @Override
    public void bfgsOnStep(int currentSteps, int totalSteps, double diffX, double diffG, int hessian) {
        progressBar.setValue( currentSteps * 100 / totalSteps);
        if (currentSteps % reportEvery == 0){
            area.append( currentSteps + "/" + totalSteps + " -> " + 
                    "diffX=" + diffX + " | diffG=" + diffG + "| hessians = " + hessian + "\n");
        }
    }
    
    @Override
    public void bfgsOnFinish(int iters, boolean didExitX, boolean didExitG, int hessian, double fx, double[] x){
        FX = fx;
        X = x;
        area.append( "Finish iters = " + iters + " x = " + didExitX + " g = " + didExitG + "| hessians = " + hessian +"\n");
    }

    public void selectDefaultAlg(int algNumber) {
        if (algNumber == 0){
            pressureCheck.setSelected( true );
        }else if (algNumber == 1){
            forceCheck.setSelected( true );
        }else if (algNumber == 2){
            gorkovCheck.setSelected( true );
        }else if (algNumber == 3){
            maxGLaplacian.setSelected( true );
        }
    }

    public void calcFocal() {
        calcAlgorithm(0, false, true);
    }

    
    public class WorkingCalc extends Thread {

        @Override
        public void run() {
            calcBFGSButton.setEnabled( false );
            try{
                calcAlgorithm(1, true, true);
            }catch (Exception e){
                e.printStackTrace();
            }
            calcBFGSButton.setEnabled( true );
        }
        
    }
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField alphaText;
    private javax.swing.JTextArea area;
    private javax.swing.JCheckBox basinCheck;
    private javax.swing.JTextField basinDiffXText;
    private javax.swing.JTextField basinStepsText;
    private javax.swing.JTextField basinTText;
    private javax.swing.JRadioButton bottleCheck;
    private javax.swing.JButton calcBFGSButton;
    private javax.swing.JTextField centralWText;
    private javax.swing.JButton clearAreaButton;
    private javax.swing.JButton diffButton;
    private javax.swing.JTextField distancesText;
    private javax.swing.JCheckBox equalizerCheck;
    private javax.swing.JTextField equalizerText;
    private javax.swing.JRadioButton forceCheck;
    private javax.swing.JTextField gMinText;
    private javax.swing.JRadioButton gorkovCheck;
    private javax.swing.ButtonGroup groupHoloOrigin;
    private javax.swing.JRadioButton holoAddCheck;
    private javax.swing.JRadioButton holoMemoryCheck;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox kickStartCheck;
    private javax.swing.JTextField laplacianConstantsText;
    private javax.swing.JTextField lowPressureKText;
    private javax.swing.JRadioButton maxGLaplacian;
    private javax.swing.JRadioButton multiPressureCheck;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox onlySelectedCheck;
    private javax.swing.JRadioButton pressureCheck;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField refPressureText;
    private javax.swing.JTextField reportEveryText;
    private javax.swing.JTextField rotYText;
    private javax.swing.JTextField rotZText;
    private javax.swing.JCheckBox rotateCheck;
    private javax.swing.JButton singleCalcButton;
    private javax.swing.JToggleButton singleEnableButton;
    private javax.swing.JButton snapButton;
    private javax.swing.JTextField stepsText;
    private javax.swing.ButtonGroup varToOptimizeGroup;
    private javax.swing.JTextField weightsNText;
    private javax.swing.JTextField weightsPText;
    private javax.swing.JTextField xMinText;
    // End of variables declaration//GEN-END:variables


}
