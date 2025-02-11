/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.DirectivityCalc;
import acousticfield3d.gui.controls.SliderPanel;
import acousticfield3d.utils.DialogUtils;
import acousticfield3d.utils.FileUtils;
import acousticfield3d.Log;
import acousticfield3d.algorithms.DirectHoloTwoSides;
import acousticfield3d.gui.panels.AnimPanel;
import acousticfield3d.gui.panels.ControlPointPanel;
import acousticfield3d.gui.panels.ExternalSlicePanel;
import acousticfield3d.gui.panels.MiscPanel;
import acousticfield3d.gui.panels.MovePanel;
import acousticfield3d.gui.panels.PhysicsPanel;
import acousticfield3d.gui.panels.PreCubePanel;
import acousticfield3d.gui.panels.PreIsoPanel;
import acousticfield3d.gui.panels.PreVolPanel;
import acousticfield3d.gui.panels.RtIsoPanel;
import acousticfield3d.gui.panels.RtSlicePanel;
import acousticfield3d.gui.panels.RtVolPanel;
import acousticfield3d.gui.panels.SimPanel;
import acousticfield3d.gui.panels.TransControlPanel;
import acousticfield3d.gui.panels.TransducersPanel;
import acousticfield3d.gui.specific.CreateIsoAnimation;
import acousticfield3d.gui.specific.ImportCube;
import acousticfield3d.gui.specific.ImportTransPhasePointForm;
import acousticfield3d.gui.specific.Knots;
import acousticfield3d.gui.specific.OpenCLTest;
import acousticfield3d.gui.specific.UDPPhasesForm;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Quaternion;
import acousticfield3d.math.Transform;
import acousticfield3d.math.Vector3f;
import acousticfield3d.renderer.Renderer;
import acousticfield3d.scene.BehavioursThread;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Scene;
import acousticfield3d.scene.SceneObjExport;
import acousticfield3d.simulation.Simulation;
import acousticfield3d.simulation.Transducer;
import acousticfield3d.utils.Parse;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import java.util.List;

/**
 *
 * @author Asier
 */
public class MainForm extends javax.swing.JFrame {
    public final DecimalFormat decimalFormat;
    public final DecimalFormat decimalFormat2;
    
    SliderPanel sliderPanel;
     
    JFrame fullFrame;
    
    public final GLJPanel gljpanel;
    public final Renderer renderer;
    public final Scene scene;
    public Simulation simulation;
    
    BehavioursThread animationThread;
    
    public final ArrayList<Entity> selection;
    boolean cameraLooked;
    boolean hasDragged;
    int firstDragX, firstDragY;
    
    public final RtSlicePanel rtSlicePanel;
    public final PreCubePanel preCubePanel;
    public final PreVolPanel preVolPanel;
    public final PreIsoPanel preIsoPanel;
    public final AnimPanel animPanel;
    public final RtVolPanel rtVolPanel;
    public final RtIsoPanel rtIsoPanel;
    public final MiscPanel miscPanel;
    public final ControlPointPanel cpPanel;
    public final SimPanel simPanel;
    public final TransducersPanel transPanel;
    public final TransControlPanel transControlPanel;
    public final PhysicsPanel physicsPanel;
    public final MovePanel movePanel;
    public final ExternalSlicePanel exSlicePanel;
    
    public final BeadsToAnimationForm beadsToAnimationForm;
    public final AlgorithmsForm algorithmsForm;
    public final HoloPatternsForm holoPatternsForm;
    public final ReportFrame reportFrame;
    public final BeadControllerForm beadController;
    public final ExtraBoardForm extraBoard;
    public final FloatingChartForm floatingChart;
    public final TransducersArrangementForm addTransducersForm; 
            
    public LeapMotionForm leapMotionForm = null;
    public MouseControlForm mouseControlForm = null;
    
    public MainForm() {
        sliderPanel = new SliderPanel(1, true);
        
        cameraLooked = true;
        
        decimalFormat = new DecimalFormat("0.0000");
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        decimalFormat.setDecimalFormatSymbols(otherSymbols);
        decimalFormat2 = new DecimalFormat("0.00");
        decimalFormat2.setDecimalFormatSymbols(otherSymbols);
        
        selection = new ArrayList<>();
         
        simulation = new Simulation();
        scene = new Scene();
        miscPanel = new MiscPanel(this);
        renderer = new Renderer(scene, this);
        
        rtSlicePanel = new RtSlicePanel(this);
        preCubePanel = new PreCubePanel(this);
        preVolPanel = new PreVolPanel(this);
        preIsoPanel = new PreIsoPanel(this);
        animPanel = new AnimPanel(this);
        rtVolPanel = new RtVolPanel(this);
        rtIsoPanel = new RtIsoPanel(this);
        cpPanel = new ControlPointPanel(this);
        simPanel = new SimPanel(this);
        transPanel = new TransducersPanel(this);
        transControlPanel = new TransControlPanel(this);
        physicsPanel = new PhysicsPanel(this);
        movePanel = new MovePanel(this);
        exSlicePanel = new ExternalSlicePanel(this);
        
        beadsToAnimationForm = new BeadsToAnimationForm(this);
        algorithmsForm = new AlgorithmsForm(this);
        holoPatternsForm = new HoloPatternsForm(this);
        reportFrame = new ReportFrame(this);
        beadController = new BeadControllerForm(this);
        extraBoard = new ExtraBoardForm(this);
        floatingChart = new FloatingChartForm(this);
        addTransducersForm = new TransducersArrangementForm(this);

        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        gljpanel = new GLJPanel(glcapabilities);
        gljpanel.addGLEventListener( new GLEventListener() {      
            @Override
            public void init( GLAutoDrawable glautodrawable ) {
                renderer.init(glautodrawable.getGL().getGL2(), 
                        glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight());
            }          
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                renderer.reshape( glautodrawable.getGL().getGL2(), width, height );
            }      
            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
                renderer.dispose( glautodrawable.getGL().getGL2() );
            }
            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                //TimerUtil.get().tack("Render");
                //TimerUtil.get().tick("Render");
                renderer.render( glautodrawable.getGL().getGL2(), 
                        glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
            }
        });
        
        initComponents();
        mainTabPanel.addTab("Trans", transPanel);
        mainTabPanel.addTab("RTSlice", rtSlicePanel);
        mainTabPanel.addTab("PreCube", preCubePanel);
        mainTabPanel.addTab("PreVol", preVolPanel);
        mainTabPanel.addTab("PreIso", preIsoPanel);
        mainTabPanel.addTab("Anim", animPanel);
        mainTabPanel.addTab("RTVol", rtVolPanel);
        mainTabPanel.addTab("RTiso", rtIsoPanel);
        mainTabPanel.addTab("Misc", miscPanel);
        mainTabPanel.addTab("CP", cpPanel);
        mainTabPanel.addTab("Sim", simPanel);
        mainTabPanel.addTab("TCr", transControlPanel);
        mainTabPanel.addTab("Phys", physicsPanel);
        mainTabPanel.addTab("Move", movePanel);
        mainTabPanel.addTab("ExSlice", exSlicePanel);
        
        initSimulation();
        
        animationThread = new BehavioursThread(scene, this);
        //animationThread.start();
   
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public Renderer getRenderer() {
        return renderer;
    }
    
    public void initSimulation(){
        animPanel.initSimulation(); 
         
        simulation.resetSimulation(panel.getWidth(), panel.getHeight());
        
        //remove old transducers
        Scene.removeWithTag(scene.getEntities(), Entity.TAG_TRANSDUCER);
        //remove old Control Points
        Scene.removeWithTag(scene.getEntities(), Entity.TAG_CONTROL_POINT);
        //remove old Masks
        Scene.removeWithTag(scene.getEntities(), Entity.TAG_MASK);
        //pass transducers to scene
        scene.addTransducersFromSimulation(simulation);
        //add control points
        scene.getEntities().addAll( simulation.getControlPoints() );
        //add all the masks
        scene.getEntities().addAll( simulation.getMaskObjects() );
        
        //load holomemory
        holoPatternsForm.setHoloMemory( simulation.getHoloMemory() );
        
        //update the boundaries
        scene.addInitVizObjects();
        simulation.updateSimulationBoundaries();
        scene.updateBoundaryBoxes(simulation);
        simPanel.simulationBoundariesToGUI();
        
        //init camera
        scene.adjustCameraToSimulation(simulation, getGLAspect());
        needUpdate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        wrapPlayButtonGroup = new javax.swing.ButtonGroup();
        slicesSource = new javax.swing.ButtonGroup();
        preCubeSource = new javax.swing.ButtonGroup();
        maskObjectsGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panelSlider = sliderPanel;
        mainTabPanel = new javax.swing.JTabbedPane();
        sliderFieldLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        rzText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rxText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        syText = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        xText = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        szText = new javax.swing.JTextField();
        ryText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        sxText = new javax.swing.JTextField();
        yText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        zText = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        containerPanel = new javax.swing.JPanel();
        panel = gljpanel;
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        loadSimMenu = new javax.swing.JMenuItem();
        saveSimMenu = new javax.swing.JMenuItem();
        screenCaptureMenu = new javax.swing.JMenuItem();
        exportObjMenu = new javax.swing.JMenuItem();
        exportObjWithMtlMenu = new javax.swing.JMenuItem();
        recToSelMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        resetCamMenu = new javax.swing.JMenuItem();
        unlockCameraMenu = new javax.swing.JMenuItem();
        frontCamMenu = new javax.swing.JMenuItem();
        topCamMenu = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        camLookSelectionMenu = new javax.swing.JMenuItem();
        originCamMenu = new javax.swing.JMenuItem();
        centerCamMenu = new javax.swing.JMenuItem();
        otherCamMenu = new javax.swing.JMenuItem();
        camCoverSelMenu = new javax.swing.JMenuItem();
        cameraMovMenu = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        delTransMenu = new javax.swing.JMenuItem();
        arrangeTransMenu = new javax.swing.JMenuItem();
        duplicateMenu = new javax.swing.JMenuItem();
        transRandomMenu = new javax.swing.JMenuItem();
        transOffsetsMenu = new javax.swing.JMenuItem();
        pointToTargetMenu = new javax.swing.JMenuItem();
        transSetPhase0Menu = new javax.swing.JMenuItem();
        transSetPhasePiMenu = new javax.swing.JMenuItem();
        transSetAmp0Menu = new javax.swing.JMenuItem();
        transSetAmp1Menu = new javax.swing.JMenuItem();
        offNextOnTransducerMenu = new javax.swing.JMenuItem();
        selectTopMenu = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        fullScreenMenu = new javax.swing.JMenuItem();
        camProjMenu = new javax.swing.JMenuItem();
        camViewMenu = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        parsePointsMenu = new javax.swing.JMenuItem();
        colorizeBeadsMenu = new javax.swing.JMenuItem();
        adjustToAnimMenu = new javax.swing.JMenuItem();
        exportTrailMenu = new javax.swing.JMenuItem();
        exportFieldsMenu = new javax.swing.JMenuItem();
        distToClosestCPMenu = new javax.swing.JMenuItem();
        addSelAsBeadMenu = new javax.swing.JMenuItem();
        beadPredictPositionsMenu = new javax.swing.JMenuItem();
        beadControllerMenu = new javax.swing.JMenuItem();
        jMenu10 = new javax.swing.JMenu();
        animImportMenu = new javax.swing.JMenuItem();
        animExportMenu = new javax.swing.JMenuItem();
        animPngSeqMenu = new javax.swing.JMenuItem();
        switchBottomTopMenu = new javax.swing.JMenuItem();
        extractFocalMenu = new javax.swing.JMenuItem();
        extractSignatureAnim = new javax.swing.JMenuItem();
        addKeyFrameMenu = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        exportSpacePointsMenu = new javax.swing.JMenuItem();
        directivityMenu = new javax.swing.JMenuItem();
        exportPhasesMenu = new javax.swing.JMenuItem();
        barsAndSlicesMenu = new javax.swing.JMenuItem();
        reportFrameMenu = new javax.swing.JMenuItem();
        jMenu12 = new javax.swing.JMenu();
        addPhaseCopyMenu = new javax.swing.JMenuItem();
        clearPhaseCopiesMenu = new javax.swing.JMenuItem();
        extraBoardMenu = new javax.swing.JMenuItem();
        LeapMenu = new javax.swing.JMenuItem();
        mouseControlMenu = new javax.swing.JMenuItem();
        menuExportPosAndPhase = new javax.swing.JMenuItem();
        plotMenu = new javax.swing.JMenuItem();
        gamePadMenu = new javax.swing.JMenuItem();
        loadPhasesAmpMenu = new javax.swing.JMenuItem();
        exportMatlabMenu = new javax.swing.JMenuItem();
        IsoAnimationMenu = new javax.swing.JMenuItem();
        importCubeMenu = new javax.swing.JMenuItem();
        udpPhaseMenu = new javax.swing.JMenuItem();
        transPhasePointImportMenu = new javax.swing.JMenuItem();
        jMenu11 = new javax.swing.JMenu();
        algorithmsMenu = new javax.swing.JMenuItem();
        phasePatternMenu = new javax.swing.JMenuItem();
        Holo2DirectMenu = new javax.swing.JMenuItem();
        OpenCLMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Acoustic SIM - Bristol BIG");

        panelSlider.setBackground(new java.awt.Color(255, 255, 255));
        panelSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelSliderMouseDragged(evt);
            }
        });
        panelSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelSliderMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panelSliderMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panelSliderLayout = new javax.swing.GroupLayout(panelSlider);
        panelSlider.setLayout(panelSliderLayout);
        panelSliderLayout.setHorizontalGroup(
            panelSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelSliderLayout.setVerticalGroup(
            panelSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        sliderFieldLabel.setText("MMM");

        rzText.setText("0");
        rzText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rzTextFocusGained(evt);
            }
        });
        rzText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rzTextActionPerformed(evt);
            }
        });

        jLabel1.setText("X");

        jLabel6.setText("RY");

        rxText.setText("0");
        rxText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rxTextFocusGained(evt);
            }
        });
        rxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rxTextActionPerformed(evt);
            }
        });

        jLabel5.setText("RZ");

        jLabel2.setText("Y");

        syText.setText("0");
        syText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                syTextFocusGained(evt);
            }
        });
        syText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syTextActionPerformed(evt);
            }
        });

        jLabel21.setText("SZ:");

        xText.setText("0");
        xText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                xTextFocusGained(evt);
            }
        });
        xText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xTextActionPerformed(evt);
            }
        });

        jLabel15.setText("SX:");

        szText.setText("0");
        szText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                szTextFocusGained(evt);
            }
        });
        szText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                szTextActionPerformed(evt);
            }
        });

        ryText.setText("0");
        ryText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ryTextFocusGained(evt);
            }
        });
        ryText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ryTextActionPerformed(evt);
            }
        });

        jLabel4.setText("RX");

        sxText.setText("0");
        sxText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sxTextFocusGained(evt);
            }
        });
        sxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sxTextActionPerformed(evt);
            }
        });

        yText.setText("0");
        yText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                yTextFocusGained(evt);
            }
        });
        yText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yTextActionPerformed(evt);
            }
        });

        jLabel3.setText("Z");

        zText.setText("0");
        zText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                zTextFocusGained(evt);
            }
        });
        zText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zTextActionPerformed(evt);
            }
        });

        jLabel16.setText("SY:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(szText, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sxText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xText)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(syText, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rzText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rxText)
                            .addComponent(ryText))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(xText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(rxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(yText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)
                                .addComponent(ryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(zText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(rzText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(sxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(syText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(szText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(sliderFieldLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(mainTabPanel))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderFieldLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainTabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
        );

        containerPanel.setLayout(new java.awt.BorderLayout());

        panel.setBackground(new java.awt.Color(0, 0, 0));
        panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelMouseDragged(evt);
            }
        });
        panel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                panelMouseWheelMoved(evt);
            }
        });
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        containerPanel.add(panel, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Simulation");

        loadSimMenu.setText("load");
        loadSimMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSimMenuActionPerformed(evt);
            }
        });
        jMenu1.add(loadSimMenu);

        saveSimMenu.setText("save");
        saveSimMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSimMenuActionPerformed(evt);
            }
        });
        jMenu1.add(saveSimMenu);

        screenCaptureMenu.setText("Screen Capture");
        screenCaptureMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                screenCaptureMenuActionPerformed(evt);
            }
        });
        jMenu1.add(screenCaptureMenu);

        exportObjMenu.setText("Export to obj");
        exportObjMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportObjMenuActionPerformed(evt);
            }
        });
        jMenu1.add(exportObjMenu);

        exportObjWithMtlMenu.setText("ExportToObjWithMtl");
        exportObjWithMtlMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportObjWithMtlMenuActionPerformed(evt);
            }
        });
        jMenu1.add(exportObjWithMtlMenu);

        recToSelMenu.setText("RecenterToSel");
        recToSelMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recToSelMenuActionPerformed(evt);
            }
        });
        jMenu1.add(recToSelMenu);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Camera");

        resetCamMenu.setText("reset");
        resetCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCamMenuActionPerformed(evt);
            }
        });
        jMenu2.add(resetCamMenu);

        unlockCameraMenu.setText("Un/Lock cam");
        unlockCameraMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unlockCameraMenuActionPerformed(evt);
            }
        });
        jMenu2.add(unlockCameraMenu);

        frontCamMenu.setText("Front");
        frontCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontCamMenuActionPerformed(evt);
            }
        });
        jMenu2.add(frontCamMenu);

        topCamMenu.setText("Top");
        topCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topCamMenuActionPerformed(evt);
            }
        });
        jMenu2.add(topCamMenu);

        jMenu3.setText("Look At");

        camLookSelectionMenu.setText("Selection");
        camLookSelectionMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camLookSelectionMenuActionPerformed(evt);
            }
        });
        jMenu3.add(camLookSelectionMenu);

        originCamMenu.setText("Origin 000");
        originCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                originCamMenuActionPerformed(evt);
            }
        });
        jMenu3.add(originCamMenu);

        centerCamMenu.setText("Center");
        centerCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerCamMenuActionPerformed(evt);
            }
        });
        jMenu3.add(centerCamMenu);

        otherCamMenu.setText("Other");
        otherCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherCamMenuActionPerformed(evt);
            }
        });
        jMenu3.add(otherCamMenu);

        jMenu2.add(jMenu3);

        camCoverSelMenu.setText("cover sel");
        camCoverSelMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camCoverSelMenuActionPerformed(evt);
            }
        });
        jMenu2.add(camCoverSelMenu);

        cameraMovMenu.setText("CameraMov");
        cameraMovMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraMovMenuActionPerformed(evt);
            }
        });
        jMenu2.add(cameraMovMenu);

        jMenuBar1.add(jMenu2);

        jMenu4.setText("Transducers");

        delTransMenu.setText("Del");
        delTransMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delTransMenuActionPerformed(evt);
            }
        });
        jMenu4.add(delTransMenu);

        arrangeTransMenu.setText("Arrangement");
        arrangeTransMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arrangeTransMenuActionPerformed(evt);
            }
        });
        jMenu4.add(arrangeTransMenu);

        duplicateMenu.setText("Duplicate");
        duplicateMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicateMenuActionPerformed(evt);
            }
        });
        jMenu4.add(duplicateMenu);

        transRandomMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        transRandomMenu.setText("Set Random");
        transRandomMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transRandomMenuActionPerformed(evt);
            }
        });
        jMenu4.add(transRandomMenu);

        transOffsetsMenu.setText("Set Offsets");
        transOffsetsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transOffsetsMenuActionPerformed(evt);
            }
        });
        jMenu4.add(transOffsetsMenu);

        pointToTargetMenu.setText("PointToTarget");
        pointToTargetMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointToTargetMenuActionPerformed(evt);
            }
        });
        jMenu4.add(pointToTargetMenu);

        transSetPhase0Menu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, 0));
        transSetPhase0Menu.setText("Phase=0");
        transSetPhase0Menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transSetPhase0MenuActionPerformed(evt);
            }
        });
        jMenu4.add(transSetPhase0Menu);

        transSetPhasePiMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        transSetPhasePiMenu.setText("Phase=PI");
        transSetPhasePiMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transSetPhasePiMenuActionPerformed(evt);
            }
        });
        jMenu4.add(transSetPhasePiMenu);

        transSetAmp0Menu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
        transSetAmp0Menu.setText("Amp=0");
        transSetAmp0Menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transSetAmp0MenuActionPerformed(evt);
            }
        });
        jMenu4.add(transSetAmp0Menu);

        transSetAmp1Menu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, 0));
        transSetAmp1Menu.setText("Amp=1");
        transSetAmp1Menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transSetAmp1MenuActionPerformed(evt);
            }
        });
        jMenu4.add(transSetAmp1Menu);

        offNextOnTransducerMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, 0));
        offNextOnTransducerMenu.setText("offNextOn");
        offNextOnTransducerMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offNextOnTransducerMenuActionPerformed(evt);
            }
        });
        jMenu4.add(offNextOnTransducerMenu);

        selectTopMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, 0));
        selectTopMenu.setText("Select Top");
        selectTopMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectTopMenuActionPerformed(evt);
            }
        });
        jMenu4.add(selectTopMenu);

        jMenuBar1.add(jMenu4);

        jMenu7.setText("View");
        jMenu7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu7ActionPerformed(evt);
            }
        });

        fullScreenMenu.setText("Full Screen");
        fullScreenMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullScreenMenuActionPerformed(evt);
            }
        });
        jMenu7.add(fullScreenMenu);

        camProjMenu.setText("Edit Projection");
        camProjMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camProjMenuActionPerformed(evt);
            }
        });
        jMenu7.add(camProjMenu);

        camViewMenu.setText("Edit View");
        camViewMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camViewMenuActionPerformed(evt);
            }
        });
        jMenu7.add(camViewMenu);

        jMenuBar1.add(jMenu7);

        jMenu8.setText("Beads");

        parsePointsMenu.setText("Parse");
        parsePointsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parsePointsMenuActionPerformed(evt);
            }
        });
        jMenu8.add(parsePointsMenu);

        colorizeBeadsMenu.setText("Colorize");
        colorizeBeadsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorizeBeadsMenuActionPerformed(evt);
            }
        });
        jMenu8.add(colorizeBeadsMenu);

        adjustToAnimMenu.setText("AdjustToAnim");
        adjustToAnimMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adjustToAnimMenuActionPerformed(evt);
            }
        });
        jMenu8.add(adjustToAnimMenu);

        exportTrailMenu.setText("Export Trail");
        exportTrailMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportTrailMenuActionPerformed(evt);
            }
        });
        jMenu8.add(exportTrailMenu);

        exportFieldsMenu.setText("Export fields");
        exportFieldsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportFieldsMenuActionPerformed(evt);
            }
        });
        jMenu8.add(exportFieldsMenu);

        distToClosestCPMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        distToClosestCPMenu.setText("DistToClosesCP");
        distToClosestCPMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distToClosestCPMenuActionPerformed(evt);
            }
        });
        jMenu8.add(distToClosestCPMenu);

        addSelAsBeadMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        addSelAsBeadMenu.setText("AddSelAsBead");
        addSelAsBeadMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSelAsBeadMenuActionPerformed(evt);
            }
        });
        jMenu8.add(addSelAsBeadMenu);

        beadPredictPositionsMenu.setText("PredictPositions");
        beadPredictPositionsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beadPredictPositionsMenuActionPerformed(evt);
            }
        });
        jMenu8.add(beadPredictPositionsMenu);

        beadControllerMenu.setText("BeadController");
        beadControllerMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beadControllerMenuActionPerformed(evt);
            }
        });
        jMenu8.add(beadControllerMenu);

        jMenuBar1.add(jMenu8);

        jMenu10.setText("Animations");

        animImportMenu.setText("Import");
        animImportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animImportMenuActionPerformed(evt);
            }
        });
        jMenu10.add(animImportMenu);

        animExportMenu.setText("Export");
        animExportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animExportMenuActionPerformed(evt);
            }
        });
        jMenu10.add(animExportMenu);

        animPngSeqMenu.setText("Export PNG seq");
        animPngSeqMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animPngSeqMenuActionPerformed(evt);
            }
        });
        jMenu10.add(animPngSeqMenu);

        switchBottomTopMenu.setText("SwitchBottomTop");
        switchBottomTopMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchBottomTopMenuActionPerformed(evt);
            }
        });
        jMenu10.add(switchBottomTopMenu);

        extractFocalMenu.setText("ExtractFocal");
        extractFocalMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractFocalMenuActionPerformed(evt);
            }
        });
        jMenu10.add(extractFocalMenu);

        extractSignatureAnim.setText("ExtractSignature");
        extractSignatureAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractSignatureAnimActionPerformed(evt);
            }
        });
        jMenu10.add(extractSignatureAnim);

        addKeyFrameMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        addKeyFrameMenu.setText("AddKeyFrame");
        addKeyFrameMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addKeyFrameMenuActionPerformed(evt);
            }
        });
        jMenu10.add(addKeyFrameMenu);

        jMenuBar1.add(jMenu10);

        jMenu9.setText("Utils");

        exportSpacePointsMenu.setText("Export Space Points");
        exportSpacePointsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSpacePointsMenuActionPerformed(evt);
            }
        });
        jMenu9.add(exportSpacePointsMenu);

        directivityMenu.setText("Directivity interp");
        directivityMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directivityMenuActionPerformed(evt);
            }
        });
        jMenu9.add(directivityMenu);

        exportPhasesMenu.setText("Export Phases");
        exportPhasesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportPhasesMenuActionPerformed(evt);
            }
        });
        jMenu9.add(exportPhasesMenu);

        barsAndSlicesMenu.setText("BarsAndSlices");
        barsAndSlicesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barsAndSlicesMenuActionPerformed(evt);
            }
        });
        jMenu9.add(barsAndSlicesMenu);

        reportFrameMenu.setText("ReportFrame");
        reportFrameMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportFrameMenuActionPerformed(evt);
            }
        });
        jMenu9.add(reportFrameMenu);

        jMenu12.setText("PhaseCopies");

        addPhaseCopyMenu.setText("Add");
        addPhaseCopyMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPhaseCopyMenuActionPerformed(evt);
            }
        });
        jMenu12.add(addPhaseCopyMenu);

        clearPhaseCopiesMenu.setText("Clear");
        clearPhaseCopiesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearPhaseCopiesMenuActionPerformed(evt);
            }
        });
        jMenu12.add(clearPhaseCopiesMenu);

        jMenu9.add(jMenu12);

        extraBoardMenu.setText("Extra board");
        extraBoardMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extraBoardMenuActionPerformed(evt);
            }
        });
        jMenu9.add(extraBoardMenu);

        LeapMenu.setText("leapMotion");
        LeapMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeapMenuActionPerformed(evt);
            }
        });
        jMenu9.add(LeapMenu);

        mouseControlMenu.setText("mouseControl");
        mouseControlMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mouseControlMenuActionPerformed(evt);
            }
        });
        jMenu9.add(mouseControlMenu);

        menuExportPosAndPhase.setText("Export posAndPhase");
        menuExportPosAndPhase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExportPosAndPhaseActionPerformed(evt);
            }
        });
        jMenu9.add(menuExportPosAndPhase);

        plotMenu.setText("Plot");
        plotMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotMenuActionPerformed(evt);
            }
        });
        jMenu9.add(plotMenu);

        gamePadMenu.setText("GamePad");
        gamePadMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gamePadMenuActionPerformed(evt);
            }
        });
        jMenu9.add(gamePadMenu);

        loadPhasesAmpMenu.setText("LoadPhasesAmp");
        loadPhasesAmpMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadPhasesAmpMenuActionPerformed(evt);
            }
        });
        jMenu9.add(loadPhasesAmpMenu);

        exportMatlabMenu.setText("ExportMatlab");
        exportMatlabMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMatlabMenuActionPerformed(evt);
            }
        });
        jMenu9.add(exportMatlabMenu);

        IsoAnimationMenu.setText("IsoAnimation");
        IsoAnimationMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IsoAnimationMenuActionPerformed(evt);
            }
        });
        jMenu9.add(IsoAnimationMenu);

        importCubeMenu.setText("Import Cube Data");
        importCubeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importCubeMenuActionPerformed(evt);
            }
        });
        jMenu9.add(importCubeMenu);

        udpPhaseMenu.setText("UDP phase");
        udpPhaseMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                udpPhaseMenuActionPerformed(evt);
            }
        });
        jMenu9.add(udpPhaseMenu);

        transPhasePointImportMenu.setText("Import TransPhasePoint");
        transPhasePointImportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transPhasePointImportMenuActionPerformed(evt);
            }
        });
        jMenu9.add(transPhasePointImportMenu);

        jMenuBar1.add(jMenu9);

        jMenu11.setText("Algorithms");

        algorithmsMenu.setText("Setup");
        algorithmsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algorithmsMenuActionPerformed(evt);
            }
        });
        jMenu11.add(algorithmsMenu);

        phasePatternMenu.setText("HoloPatterns");
        phasePatternMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phasePatternMenuActionPerformed(evt);
            }
        });
        jMenu11.add(phasePatternMenu);

        Holo2DirectMenu.setText("Holo2Direct");
        Holo2DirectMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Holo2DirectMenuActionPerformed(evt);
            }
        });
        jMenu11.add(Holo2DirectMenu);

        OpenCLMenu.setText("OpenCL");
        OpenCLMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenCLMenuActionPerformed(evt);
            }
        });
        jMenu11.add(OpenCLMenu);

        jMenuBar1.add(jMenu11);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private int lastButton, lastX, lastY;
    private void panelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMousePressed
        lastButton = evt.getButton();
        lastX = evt.getX();
        lastY = evt.getY();
        
        if(lastButton == 2){
            if (cameraLooked){
                scene.getCamera().activateObservation(true, scene.getCamera().getObservationPoint());
            }
        }else if(lastButton == 1){
            updateSelection(evt);
        }
    }//GEN-LAST:event_panelMousePressed

    private void panelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMouseDragged
        int x = evt.getX();
        int y = evt.getY();
        final float rotGain = 0.01f;
        final float moveGain =  simPanel.getGUIGain()  * 0.5f;
        float diffX = (x - lastX);
        float diffY = (y - lastY);
        
        if(lastButton == 1){
            
        }else if(lastButton == 2){
            if (cameraLooked){
                scene.getCamera().moveAzimuthAndInclination(-diffX * rotGain, -diffY * rotGain);
                scene.getCamera().updateObservation();
            }else{
                scene.getCamera().getTransform().rotateLocal(-diffY * rotGain, -diffX * rotGain, 0);
            }
        }else if(lastButton == 3){
           scene.getCamera().getTransform().moveLocalSpace(-diffX * moveGain, diffY * moveGain, 0);
        }
        
        needUpdate();
        lastX = x;
        lastY = y;
    }//GEN-LAST:event_panelMouseDragged

    private void panelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_panelMouseWheelMoved
         float wheel = (float)evt.getPreciseWheelRotation();
         final float wheelGain = simPanel.getGUIGain() * 6f;
         final float value = wheel * wheelGain;
         if (cameraLooked){
            scene.getCamera().setDistance(scene.getCamera().getDistance()+ value);  
            scene.getCamera().updateObservation();
         }else{
             scene.getCamera().getTransform().moveLocalSpace(0, 0, value);
         }
         needUpdate();
    }//GEN-LAST:event_panelMouseWheelMoved

    private void lookCamera(Vector3f v){
        scene.getCamera().setOrtho(false);
        scene.getCamera().updateProjection( getGLAspect());
        scene.getCamera().activateObservation(true, v);
    }
    
    private void originCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_originCamMenuActionPerformed
        lookCamera(Vector3f.ZERO);
    }//GEN-LAST:event_originCamMenuActionPerformed

    private void centerCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_centerCamMenuActionPerformed
        lookCamera( simulation.getSimulationCenter() );
    }//GEN-LAST:event_centerCamMenuActionPerformed

    private void otherCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherCamMenuActionPerformed
        String v = DialogUtils.getStringDialog(this, "Vector", "0.00 0.00 0.00");
        if (v != null){
            lookCamera( new Vector3f().parse(v) );
        }
    }//GEN-LAST:event_otherCamMenuActionPerformed

    public float getGLAspect(){
        return panel.getWidth() / (float) panel.getHeight();
    }
    
    private void loadSimMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSimMenuActionPerformed
        String target = FileUtils.selectFile(this, "open", ".xml.gz", null);
        if(target != null){
            loadSimulation(target);
        }
    }//GEN-LAST:event_loadSimMenuActionPerformed

    public void loadSimulation(String target) {
        try {
            simulation = (Simulation) FileUtils.readCompressedObject(new File(target));
            simulation.sortAnimations();
            simulation.sortTransducers();
            initSimulation();
            clearSelection();
            movePanel.snapFirstBead();
            needUpdate();
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setSelection(final List<Transducer> transducers){
        for(Transducer t : transducers){
            t.selected = true;
            selection.add(t);
        }
    }
    
    public void clearSelection(){
        for(Entity e : selection){
            e.selected = false;
        }
        selection.clear();
    }
    
    public void setSelection(Entity e){
        clearSelection();
        e.selected = true;
        selection.add(e);
    }
        
    private void saveSimMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSimMenuActionPerformed
        String file = FileUtils.selectNonExistingFile(this, ".xml.gz");
        if ( file != null){
            try {
                simulation.labelNumberTransducers();
                simulation.setHoloMemory( holoPatternsForm.getHoloMemory() );
                simulation.getMaskObjects().clear();
                scene.gatherMeshEntitiesWithTag( simulation.getMaskObjects(), Entity.TAG_MASK);
                FileUtils.writeCompressedObject(new File(file), simulation);
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_saveSimMenuActionPerformed

    private void delTransMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delTransMenuActionPerformed
        ArrayList<Transducer> trans = new ArrayList<>();
        for( Entity e : selection){
            if ( e instanceof Transducer) { trans.add( (Transducer) e ); }
        }
        
        transPanel.deleteTransducers( trans );
        
        clearSelection();
        needUpdate();
    }//GEN-LAST:event_delTransMenuActionPerformed

    private void arrangeTransMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arrangeTransMenuActionPerformed
        addTransducersForm.setVisible(true);
    }//GEN-LAST:event_arrangeTransMenuActionPerformed

    private void resetCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetCamMenuActionPerformed
        scene.adjustCameraToSimulation(simulation, getGLAspect());
        needUpdate();
    }//GEN-LAST:event_resetCamMenuActionPerformed

    private void xTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_xTextFocusGained
        changeSlider(FieldsToChange.xField, "X", simulation.getTransSize() * 8.0f, Float.MIN_VALUE, Float.MAX_VALUE);
    }//GEN-LAST:event_xTextFocusGained

    private void yTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_yTextFocusGained
        changeSlider(FieldsToChange.yField, "Y", simulation.getTransSize() * 8.0f, Float.MIN_VALUE, Float.MAX_VALUE);
    }//GEN-LAST:event_yTextFocusGained

    private void zTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_zTextFocusGained
        changeSlider(FieldsToChange.zField, "Z", simulation.getTransSize() * 8.0f, Float.MIN_VALUE, Float.MAX_VALUE);
    }//GEN-LAST:event_zTextFocusGained

    private void rxTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rxTextFocusGained
        changeSlider(FieldsToChange.rxField, "RX", 360, Float.MIN_VALUE, Float.MAX_VALUE);
    }//GEN-LAST:event_rxTextFocusGained

    private void ryTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ryTextFocusGained
        changeSlider(FieldsToChange.ryField, "RY", 360, Float.MIN_VALUE, Float.MAX_VALUE);
    }//GEN-LAST:event_ryTextFocusGained

    private void rzTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rzTextFocusGained
        changeSlider(FieldsToChange.rzField, "RZ", 360, Float.MIN_VALUE, Float.MAX_VALUE);
    }//GEN-LAST:event_rzTextFocusGained

    private void panelSliderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSliderMouseDragged
        float diff = sliderPanel.touchDrag(evt.getX(), evt.getY()); 
        changeSelectionField(sliderField, diff * sliderScale, false, true);
        needUpdate();
    }//GEN-LAST:event_panelSliderMouseDragged

    public void updateTransForField(FieldsToChange field, String text){
        if (text.length() < 1) {return;}
        boolean absolute;
        float value;
        if (text.charAt(0) == 'a'){
            absolute = false;
            value = Parse.stringToFloat( text.substring(1));
        }else{
            absolute = true;
            value = Parse.stringToFloat( text );
        }
        changeSelectionField(field, value, absolute, false);
        needUpdate();
    }
    
    private void xTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xTextActionPerformed
        updateTransForField(FieldsToChange.xField, xText.getText());
    }//GEN-LAST:event_xTextActionPerformed

    private void rxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rxTextActionPerformed
        updateTransForField(FieldsToChange.rxField, rxText.getText());
    }//GEN-LAST:event_rxTextActionPerformed

    private void yTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yTextActionPerformed
        updateTransForField(FieldsToChange.yField, yText.getText());
    }//GEN-LAST:event_yTextActionPerformed

    private void ryTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ryTextActionPerformed
        updateTransForField(FieldsToChange.ryField, ryText.getText());
    }//GEN-LAST:event_ryTextActionPerformed

    private void zTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zTextActionPerformed
        updateTransForField(FieldsToChange.zField, zText.getText());
    }//GEN-LAST:event_zTextActionPerformed

    private void rzTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rzTextActionPerformed
        updateTransForField(FieldsToChange.rzField, rzText.getText());
    }//GEN-LAST:event_rzTextActionPerformed

    
    private void panelSliderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSliderMousePressed
        sliderPanel.touchDown(evt.getX(), evt.getY(), 0);
    }//GEN-LAST:event_panelSliderMousePressed

    private void panelSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSliderMouseReleased
        sliderPanel.setShow( false );
        sliderPanel.repaint();
    }//GEN-LAST:event_panelSliderMouseReleased

    private void frontCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontCamMenuActionPerformed
        scene.adjustCameraToFront(simulation, getGLAspect());
        needUpdate();
    }//GEN-LAST:event_frontCamMenuActionPerformed

    private void topCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topCamMenuActionPerformed
        scene.adjustCameraToTop(simulation, getGLAspect());
        needUpdate();
    }//GEN-LAST:event_topCamMenuActionPerformed

    private void syTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syTextActionPerformed
        updateTransForField(FieldsToChange.syField, syText.getText());
    }//GEN-LAST:event_syTextActionPerformed

    private void syTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_syTextFocusGained
        changeSlider(FieldsToChange.syField, "SY", simulation.maxDistanceBoundary() / 8.0f, 0.0f, Float.MAX_VALUE);
    }//GEN-LAST:event_syTextFocusGained

    private void sxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sxTextActionPerformed
        updateTransForField(FieldsToChange.sxField, sxText.getText());
    }//GEN-LAST:event_sxTextActionPerformed

    private void sxTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sxTextFocusGained
        changeSlider(FieldsToChange.sxField, "SX", simulation.maxDistanceBoundary() / 8.0f, 0.0f, Float.MAX_VALUE);
    }//GEN-LAST:event_sxTextFocusGained

    private void szTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_szTextFocusGained
        changeSlider(FieldsToChange.szField, "SZ", simulation.maxDistanceBoundary() / 8.0f, 0.0f, Float.MAX_VALUE);
    }//GEN-LAST:event_szTextFocusGained

    private void szTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_szTextActionPerformed
       updateTransForField(FieldsToChange.szField, szText.getText());
    }//GEN-LAST:event_szTextActionPerformed

    private void transRandomMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transRandomMenuActionPerformed
        //for (Entity me : selection){
        for (Entity me : simulation.getTransducers()){
            if (me instanceof Transducer){
                Transducer t = (Transducer) me;
                //t.setPAmplitude( FastMath.random(0.0f, 1.0f));
                t.setPhase( FastMath.random(-1.0f, 1.0f));
            }
        }
        needUpdate();
    }//GEN-LAST:event_transRandomMenuActionPerformed

    private void closeFullScreen(){
        gljpanel.removeKeyListener( gljpanel.getKeyListeners()[0] );
        fullFrame.remove(gljpanel);
        this.containerPanel.add(gljpanel);
        fullFrame.dispose();
        fullFrame = null;
        this.containerPanel.revalidate();
        repaint();
    }
    private void fullScreenMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullScreenMenuActionPerformed
        fullFrame = new JFrame();
        gljpanel.addKeyListener(new KeyListener() {
            @Override public void keyReleased(KeyEvent e) {}
            @Override public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                Log.log("Exiting full screen");
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    closeFullScreen();
                }
            } 
        });
        fullFrame.setUndecorated(true);  // no decoration such as title and scroll bars
        this.remove( gljpanel );
        fullFrame.getContentPane().add(gljpanel);
        showOnScreen(1, fullFrame);
        fullFrame.setUndecorated(true);     // no decoration such as title bar
        fullFrame.setExtendedState(Frame.MAXIMIZED_BOTH);  // full screen mode
        fullFrame.setVisible(true);
        fullFrame.requestFocus();
    }//GEN-LAST:event_fullScreenMenuActionPerformed

    public static void showOnScreen(int screen, JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen > -1 && screen < gd.length) {
            frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
        } else if (gd.length > 0) {
            frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
        } else {
            throw new RuntimeException("No Screens Found");
        }
    }
    
    private void jMenu7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu7ActionPerformed

    
    public void addMeshEntityToSceneCenter( MeshEntity me){
        me.getTransform().getTranslation().set( simulation.getSimulationCenter() );
        me.getTransform().getScale().set( simulation.maxDistanceBoundary() );
        scene.getEntities().add( me );
    }
    
    private void camProjMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camProjMenuActionPerformed
        ProjectionForm pf = new ProjectionForm(scene.getCamera().getProjection());
        pf.setLocationRelativeTo(this);
        pf.setVisible(true);
    }//GEN-LAST:event_camProjMenuActionPerformed

    private void camViewMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camViewMenuActionPerformed
        TransformForm tf = new TransformForm(scene.getCamera().getTransform(), this);
        tf.setLocationRelativeTo(this);
        tf.setVisible(true);
    }//GEN-LAST:event_camViewMenuActionPerformed

    private void unlockCameraMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unlockCameraMenuActionPerformed
       cameraLooked = !cameraLooked;
    }//GEN-LAST:event_unlockCameraMenuActionPerformed

    
    private void exportSpacePointsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSpacePointsMenuActionPerformed
        String file = FileUtils.selectNonExistingFile(this, ".txt");
        if(file != null){
            StringBuilder sb = new StringBuilder();
            for(Entity e : selection){
                final Vector3f pos = e.getTransform().getTranslation();
                sb.append(pos.x + " ").append(pos.y + " ").append(pos.z + "\n");
            }
            try {
                FileUtils.writeBytesInFile(new File(file), sb.toString().getBytes());
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_exportSpacePointsMenuActionPerformed

    private void parsePointsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parsePointsMenuActionPerformed
        ParseControlPointsForm pcpf = new ParseControlPointsForm(this);
        pcpf.setLocationRelativeTo(this);
        pcpf.setVisible(true);
    }//GEN-LAST:event_parsePointsMenuActionPerformed

    private void transOffsetsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transOffsetsMenuActionPerformed
        TransducersOffsetForm tof = new TransducersOffsetForm(this);
        tof.setLocationRelativeTo(this);
        tof.setVisible(true);
    }//GEN-LAST:event_transOffsetsMenuActionPerformed

    private void screenCaptureMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_screenCaptureMenuActionPerformed
        String path = FileUtils.selectNonExistingFile(this, ".png");
        if(path != null){
           saveScreenshot(path);
        }
    }//GEN-LAST:event_screenCaptureMenuActionPerformed

    public void saveScreenshot(String path){
         BufferedImage bi = new BufferedImage(gljpanel.getWidth(), gljpanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            gljpanel.paint( bi.getGraphics() );
            try {
                ImageIO.write(bi, "png", new File(path));
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private void adjustToAnimMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adjustToAnimMenuActionPerformed
        beadsToAnimationForm.setLocationRelativeTo(this);
        beadsToAnimationForm.setVisible( true );
    }//GEN-LAST:event_adjustToAnimMenuActionPerformed

    private void animImportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animImportMenuActionPerformed
        animPanel.importAnimation();
    }//GEN-LAST:event_animImportMenuActionPerformed

    private void animExportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animExportMenuActionPerformed
        animPanel.exportAnimation();
    }//GEN-LAST:event_animExportMenuActionPerformed

    private void colorizeBeadsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorizeBeadsMenuActionPerformed
        ColorizeBeadsForm cbf = new ColorizeBeadsForm(this);
        cbf.setLocationRelativeTo(null);
        cbf.setVisible(true);
    }//GEN-LAST:event_colorizeBeadsMenuActionPerformed

    private void switchBottomTopMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchBottomTopMenuActionPerformed
        animPanel.switchBottomTop();
    }//GEN-LAST:event_switchBottomTopMenuActionPerformed

    private void addKeyFrameMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addKeyFrameMenuActionPerformed
        animPanel.pressAddKeyFrame();
    }//GEN-LAST:event_addKeyFrameMenuActionPerformed

    private void transSetPhase0MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transSetPhase0MenuActionPerformed
        transPanel.setTransPhase( 0.0f );
    }//GEN-LAST:event_transSetPhase0MenuActionPerformed

    private void transSetPhasePiMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transSetPhasePiMenuActionPerformed
        transPanel.setTransPhase( 1.0f );
    }//GEN-LAST:event_transSetPhasePiMenuActionPerformed

    private void transSetAmp0MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transSetAmp0MenuActionPerformed
        transPanel.setTransAmp( 0.0f );
    }//GEN-LAST:event_transSetAmp0MenuActionPerformed

    private void transSetAmp1MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transSetAmp1MenuActionPerformed
        transPanel.setTransAmp( 1.0f );
    }//GEN-LAST:event_transSetAmp1MenuActionPerformed

    private void animPngSeqMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animPngSeqMenuActionPerformed
        ExportPNGSequenceFrame epsf = new ExportPNGSequenceFrame(this);
        epsf.setLocationRelativeTo( this );
        epsf.setVisible( true );
    }//GEN-LAST:event_animPngSeqMenuActionPerformed

    private void camLookSelectionMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camLookSelectionMenuActionPerformed
        if(! selection.isEmpty() ){
            lookCamera( selection.get(0).getTransform().getTranslation() );
        }
    }//GEN-LAST:event_camLookSelectionMenuActionPerformed

    private void algorithmsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algorithmsMenuActionPerformed
        algorithmsForm.setLocationRelativeTo(this);
        algorithmsForm.setVisible( true );
    }//GEN-LAST:event_algorithmsMenuActionPerformed

    private void pointToTargetMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointToTargetMenuActionPerformed
        if (! selection.isEmpty()){
            final Vector3f target = selection.get(0).getTransform().getTranslation();
            for(Transducer t : simulation.getTransducers()){
                t.getTransform().lookAt( target );
                t.getTransform().rotateLocal( FastMath.degToRad( -90 ), 0, 0);
            }
        }
        needUpdate();
    }//GEN-LAST:event_pointToTargetMenuActionPerformed

    private void exportTrailMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportTrailMenuActionPerformed
        physicsPanel.exportTrail();
    }//GEN-LAST:event_exportTrailMenuActionPerformed

    private void exportPhasesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportPhasesMenuActionPerformed
        ExportPhasesForm exportPhases = new ExportPhasesForm(this);
        exportPhases.initValues();
        exportPhases.setLocationRelativeTo(this);
        exportPhases.setVisible(true);
    }//GEN-LAST:event_exportPhasesMenuActionPerformed

    private void offNextOnTransducerMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offNextOnTransducerMenuActionPerformed
        if (selection.isEmpty()){
            return;
        }
        Entity e = selection.get(0);
        if (! (e instanceof Transducer)){
            return;
        }
        Transducer t = (Transducer)e;
        transPanel.setTransAmp( 0.0f );
        int indexTrans = simulation.getTransducers().indexOf( t );
        if(indexTrans == -1){
            return;
        }
        clearSelection();
        if (indexTrans < simulation.getTransducers().size() - 1 ){
            selection.add( simulation.getTransducers().get( indexTrans + 1));
            transPanel.setTransAmp( 1.0f );
        }
    }//GEN-LAST:event_offNextOnTransducerMenuActionPerformed

    private void directivityMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directivityMenuActionPerformed
        DirectivityCalc dc = new DirectivityCalc();
        dc.setLocationRelativeTo(this);
        dc.setVisible(true);
    }//GEN-LAST:event_directivityMenuActionPerformed

    private void barsAndSlicesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barsAndSlicesMenuActionPerformed
        GroundBarsAndSlicesForm gbas = new GroundBarsAndSlicesForm(this);
        gbas.setLocationRelativeTo(this);
        gbas.setVisible( true );
    }//GEN-LAST:event_barsAndSlicesMenuActionPerformed

    private void exportObjMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportObjMenuActionPerformed
        SceneObjExport soe = new SceneObjExport(this);
        soe.export( false );
    }//GEN-LAST:event_exportObjMenuActionPerformed

    private void camCoverSelMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camCoverSelMenuActionPerformed
        if (! selection.isEmpty()){
            scene.adjustCameraToCover( selection.get(0) );
            needUpdate();
        }
    }//GEN-LAST:event_camCoverSelMenuActionPerformed

    private void phasePatternMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phasePatternMenuActionPerformed
       holoPatternsForm.setVisible( true );
    }//GEN-LAST:event_phasePatternMenuActionPerformed

    private void exportFieldsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportFieldsMenuActionPerformed
        ExportFieldForm eff = new ExportFieldForm(this);
        eff.setLocationRelativeTo(null);
        eff.setVisible(true);
    }//GEN-LAST:event_exportFieldsMenuActionPerformed

    private void reportFrameMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportFrameMenuActionPerformed
        reportFrame.setVisible( true );
    }//GEN-LAST:event_reportFrameMenuActionPerformed

    private void distToClosestCPMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_distToClosestCPMenuActionPerformed
        cpPanel.distanceToClosestCP();
    }//GEN-LAST:event_distToClosestCPMenuActionPerformed

    private void cameraMovMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraMovMenuActionPerformed
        CameraMoveFrame cmf = new CameraMoveFrame(this);
        cmf.setLocationRelativeTo(this);
        cmf.setVisible(true);
    }//GEN-LAST:event_cameraMovMenuActionPerformed

    private void exportObjWithMtlMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportObjWithMtlMenuActionPerformed
        SceneObjExport soe = new SceneObjExport(this);
        soe.export( true );
    }//GEN-LAST:event_exportObjWithMtlMenuActionPerformed

    private void extractFocalMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractFocalMenuActionPerformed
        animPanel.extractSignature( true );
    }//GEN-LAST:event_extractFocalMenuActionPerformed

    private void extractSignatureAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractSignatureAnimActionPerformed
        animPanel.extractSignature( false );
    }//GEN-LAST:event_extractSignatureAnimActionPerformed

    private void addPhaseCopyMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPhaseCopyMenuActionPerformed
        transControlPanel.addCopiesFromSelection();
    }//GEN-LAST:event_addPhaseCopyMenuActionPerformed

    private void clearPhaseCopiesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearPhaseCopiesMenuActionPerformed
        transControlPanel.clearCopies();
    }//GEN-LAST:event_clearPhaseCopiesMenuActionPerformed

    private void beadControllerMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beadControllerMenuActionPerformed
        beadController.setVisible(true);
    }//GEN-LAST:event_beadControllerMenuActionPerformed

    private void addSelAsBeadMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSelAsBeadMenuActionPerformed
        cpPanel.addSelAsBead();
    }//GEN-LAST:event_addSelAsBeadMenuActionPerformed

    private void beadPredictPositionsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beadPredictPositionsMenuActionPerformed
        PredicPositionsFrame ppf = new PredicPositionsFrame(this);
        ppf.setLocationRelativeTo(this);
        ppf.setVisible(true);
    }//GEN-LAST:event_beadPredictPositionsMenuActionPerformed

    private void Holo2DirectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Holo2DirectMenuActionPerformed
        TestHolo2DirectFrame th2 = new TestHolo2DirectFrame(this);
        th2.setLocationRelativeTo( this );
        th2.setVisible(true);
    }//GEN-LAST:event_Holo2DirectMenuActionPerformed

    private void extraBoardMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extraBoardMenuActionPerformed
        extraBoard.setLocationRelativeTo(null);
        extraBoard.setVisible(true);
    }//GEN-LAST:event_extraBoardMenuActionPerformed

    private void LeapMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeapMenuActionPerformed
        leapMotionForm = new LeapMotionForm(this);
        leapMotionForm.setLocationRelativeTo(this);
        leapMotionForm.setVisible(true);
    }//GEN-LAST:event_LeapMenuActionPerformed

    private void mouseControlMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mouseControlMenuActionPerformed
        mouseControlForm = new MouseControlForm(this);
        mouseControlForm.setLocationRelativeTo(this);
        mouseControlForm.setVisible(true);
    }//GEN-LAST:event_mouseControlMenuActionPerformed

    private void menuExportPosAndPhaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExportPosAndPhaseActionPerformed
        ExportPhasesForm.exportPosAndPhases(this);
    }//GEN-LAST:event_menuExportPosAndPhaseActionPerformed

    private void recToSelMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recToSelMenuActionPerformed
        movePanel.recenterToSel();
    }//GEN-LAST:event_recToSelMenuActionPerformed

    private void plotMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotMenuActionPerformed
        floatingChart.setLocationRelativeTo(this);
        floatingChart.setVisible(true);
    }//GEN-LAST:event_plotMenuActionPerformed

    private void duplicateMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicateMenuActionPerformed
        TransducersArrangementForm.duplicate(this);
    }//GEN-LAST:event_duplicateMenuActionPerformed

    private void gamePadMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gamePadMenuActionPerformed
        GamepadSwitchingForm gsf = new GamepadSwitchingForm(this);
        gsf.setLocationRelativeTo(this);
        gsf.setVisible(true);
    }//GEN-LAST:event_gamePadMenuActionPerformed

    private void loadPhasesAmpMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadPhasesAmpMenuActionPerformed
        Knots knots = new Knots(this);
        knots.setLocationRelativeTo(this);
        knots.setVisible(true);
    }//GEN-LAST:event_loadPhasesAmpMenuActionPerformed

    private void OpenCLMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenCLMenuActionPerformed
        OpenCLTest ocl = new OpenCLTest(this);
        ocl.setLocationRelativeTo( null );
        ocl.setVisible(true);
    }//GEN-LAST:event_OpenCLMenuActionPerformed

    private void exportMatlabMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMatlabMenuActionPerformed
        transPanel.exportMatlabFunc();
    }//GEN-LAST:event_exportMatlabMenuActionPerformed

    private void IsoAnimationMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IsoAnimationMenuActionPerformed
        CreateIsoAnimation cia = new CreateIsoAnimation(this);
        cia.setLocationRelativeTo(null);
        cia.setVisible(true);
    }//GEN-LAST:event_IsoAnimationMenuActionPerformed

    private void importCubeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importCubeMenuActionPerformed
        ImportCube ic = new ImportCube(this);
        ic.setVisible(true);
        ic.setLocationRelativeTo(this);
    }//GEN-LAST:event_importCubeMenuActionPerformed

    private void udpPhaseMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_udpPhaseMenuActionPerformed
        UDPPhasesForm f = new UDPPhasesForm(this);
        f.setLocationRelativeTo(this);
        f.setVisible(true);
    }//GEN-LAST:event_udpPhaseMenuActionPerformed

    private void selectTopMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectTopMenuActionPerformed
        DirectHoloTwoSides.selectTopTransducers( this );
    }//GEN-LAST:event_selectTopMenuActionPerformed

    private void transPhasePointImportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transPhasePointImportMenuActionPerformed
        final ImportTransPhasePointForm f = new ImportTransPhasePointForm(this);
        f.setLocationRelativeTo(this);
        f.setVisible(true);
    }//GEN-LAST:event_transPhasePointImportMenuActionPerformed
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Holo2DirectMenu;
    private javax.swing.JMenuItem IsoAnimationMenu;
    private javax.swing.JMenuItem LeapMenu;
    private javax.swing.JMenuItem OpenCLMenu;
    private javax.swing.JMenuItem addKeyFrameMenu;
    private javax.swing.JMenuItem addPhaseCopyMenu;
    private javax.swing.JMenuItem addSelAsBeadMenu;
    private javax.swing.JMenuItem adjustToAnimMenu;
    private javax.swing.JMenuItem algorithmsMenu;
    private javax.swing.JMenuItem animExportMenu;
    private javax.swing.JMenuItem animImportMenu;
    private javax.swing.JMenuItem animPngSeqMenu;
    private javax.swing.JMenuItem arrangeTransMenu;
    private javax.swing.JMenuItem barsAndSlicesMenu;
    private javax.swing.JMenuItem beadControllerMenu;
    private javax.swing.JMenuItem beadPredictPositionsMenu;
    private javax.swing.JMenuItem camCoverSelMenu;
    private javax.swing.JMenuItem camLookSelectionMenu;
    private javax.swing.JMenuItem camProjMenu;
    private javax.swing.JMenuItem camViewMenu;
    private javax.swing.JMenuItem cameraMovMenu;
    private javax.swing.JMenuItem centerCamMenu;
    private javax.swing.JMenuItem clearPhaseCopiesMenu;
    private javax.swing.JMenuItem colorizeBeadsMenu;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JMenuItem delTransMenu;
    private javax.swing.JMenuItem directivityMenu;
    private javax.swing.JMenuItem distToClosestCPMenu;
    private javax.swing.JMenuItem duplicateMenu;
    private javax.swing.JMenuItem exportFieldsMenu;
    private javax.swing.JMenuItem exportMatlabMenu;
    private javax.swing.JMenuItem exportObjMenu;
    private javax.swing.JMenuItem exportObjWithMtlMenu;
    private javax.swing.JMenuItem exportPhasesMenu;
    private javax.swing.JMenuItem exportSpacePointsMenu;
    private javax.swing.JMenuItem exportTrailMenu;
    private javax.swing.JMenuItem extraBoardMenu;
    private javax.swing.JMenuItem extractFocalMenu;
    private javax.swing.JMenuItem extractSignatureAnim;
    private javax.swing.JMenuItem frontCamMenu;
    private javax.swing.JMenuItem fullScreenMenu;
    private javax.swing.JMenuItem gamePadMenu;
    private javax.swing.JMenuItem importCubeMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JMenuItem loadPhasesAmpMenu;
    private javax.swing.JMenuItem loadSimMenu;
    private javax.swing.JTabbedPane mainTabPanel;
    private javax.swing.ButtonGroup maskObjectsGroup;
    private javax.swing.JMenuItem menuExportPosAndPhase;
    private javax.swing.JMenuItem mouseControlMenu;
    private javax.swing.JMenuItem offNextOnTransducerMenu;
    private javax.swing.JMenuItem originCamMenu;
    private javax.swing.JMenuItem otherCamMenu;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panelSlider;
    private javax.swing.JMenuItem parsePointsMenu;
    private javax.swing.JMenuItem phasePatternMenu;
    private javax.swing.JMenuItem plotMenu;
    private javax.swing.JMenuItem pointToTargetMenu;
    private javax.swing.ButtonGroup preCubeSource;
    private javax.swing.JMenuItem recToSelMenu;
    private javax.swing.JMenuItem reportFrameMenu;
    private javax.swing.JMenuItem resetCamMenu;
    private javax.swing.JTextField rxText;
    private javax.swing.JTextField ryText;
    private javax.swing.JTextField rzText;
    private javax.swing.JMenuItem saveSimMenu;
    private javax.swing.JMenuItem screenCaptureMenu;
    private javax.swing.JMenuItem selectTopMenu;
    private javax.swing.ButtonGroup slicesSource;
    private javax.swing.JLabel sliderFieldLabel;
    private javax.swing.JMenuItem switchBottomTopMenu;
    private javax.swing.JTextField sxText;
    private javax.swing.JTextField syText;
    private javax.swing.JTextField szText;
    private javax.swing.JMenuItem topCamMenu;
    private javax.swing.JMenuItem transOffsetsMenu;
    private javax.swing.JMenuItem transPhasePointImportMenu;
    private javax.swing.JMenuItem transRandomMenu;
    private javax.swing.JMenuItem transSetAmp0Menu;
    private javax.swing.JMenuItem transSetAmp1Menu;
    private javax.swing.JMenuItem transSetPhase0Menu;
    private javax.swing.JMenuItem transSetPhasePiMenu;
    private javax.swing.JMenuItem udpPhaseMenu;
    private javax.swing.JMenuItem unlockCameraMenu;
    private javax.swing.ButtonGroup wrapPlayButtonGroup;
    private javax.swing.JTextField xText;
    private javax.swing.JTextField yText;
    private javax.swing.JTextField zText;
    // End of variables declaration//GEN-END:variables

    public void needUpdate() {
        panel.repaint();
    }
    

    private void updateSelection(MouseEvent evt) {
        int x = evt.getX(); int y = evt.getY();
        int tags = Entity.TAG_NONE;
        Component comp = mainTabPanel.getSelectedComponent();
        if (comp == transPanel || comp == transControlPanel){
            tags |= Entity.TAG_TRANSDUCER;
        }else if (comp == rtSlicePanel || comp == exSlicePanel){
            tags |= Entity.TAG_SLICE;
        }else if (comp == preVolPanel){
            tags |= Entity.TAG_PRE_VOL;
        }else if (comp == rtVolPanel){
            tags |= Entity.TAG_RT_VOL;
        }else if (comp == rtIsoPanel){
            tags |= Entity.TAG_RT_ISO;
        }else if (comp == miscPanel){
            tags |= Entity.TAG_MASK;
        }else if (comp == cpPanel){
            if (cpPanel.isClickAndPlace()){
                MeshEntity e = scene.pickObject(
                    lastX / (float) panel.getWidth(),
                    1.0f - lastY / (float) panel.getHeight(), Entity.TAG_SLICE);
                if( e != null){
                    Vector3f col = scene.clickToObject(lastX / (float) panel.getWidth(), 1.0f - lastY / (float) panel.getHeight(), e);
                    cpPanel.addControlPoint(col.x, col.y, col.z, 0, -1, false);
                    needUpdate();
                }
                return;
            }
            tags |= Entity.TAG_CONTROL_POINT | Entity.TAG_BEAD;
        }else if (comp == movePanel){
            tags |= Entity.TAG_CONTROL_POINT | Entity.TAG_BEAD;
        }
        
        Entity e = scene.pickObject(
                lastX / (float) panel.getWidth(),
                1.0f - lastY / (float) panel.getHeight(), tags);
        if ( e == null ){
            clearSelection();
            needUpdate();
            return;
        }

        
        if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
           if(selection.contains(e)){
                selection.remove(e);
                e.selected = false;
            }else{
                e.selected = true;
                selection.add(e);
                entityToGUI(e); 
            }
        } else {
            clearSelection();
            e.selected = true;
            selection.add(e);
            entityToGUI(e);
        }

        needUpdate();
    }

    public void entityToGUIPosition(Entity e){
       xText.setText( decimalFormat.format( e.getTransform().getTranslation().x ));
       yText.setText( decimalFormat.format( e.getTransform().getTranslation().y ));
       zText.setText( decimalFormat.format( e.getTransform().getTranslation().z ));
       
    }
    
    public void entityToGUIRotation(Entity e){
        float[] angles = new float[3];
       e.getTransform().getRotation().toAngles(angles);
       rxText.setText( decimalFormat.format( angles[0] * FastMath.RAD_TO_DEG));
       ryText.setText( decimalFormat.format( angles[1] * FastMath.RAD_TO_DEG ));
       rzText.setText( decimalFormat.format( angles[2] * FastMath.RAD_TO_DEG ));
    }
    
    private void entityToGUI(Entity e) {
       entityToGUIPosition(e);
       
       entityToGUIRotation(e);
       
        if (e instanceof Transducer) {
            Transducer t = (Transducer) e;
            transPanel.getwText().setText(decimalFormat.format(t.width));
            transPanel.getFrText().setText(decimalFormat.format(t.getFrequency()));

            transPanel.getAmpText().setText(decimalFormat.format(t.getPAmplitude()));
            transPanel.getPhaseText().setText(decimalFormat.format(t.getPhase()));
            final float disc = miscPanel.getPhaseDiscretization();
            transControlPanel.updatePhaseSpinner = false;
            transControlPanel.getPhaseSpinner().setValue( Math.round( t.getPhase() * disc ) );
            transControlPanel.getAmpCheck().setSelected( t.getpAmplitude() != 0.0f);
            transControlPanel.getLabelText().setText( t.getOrderNumber() + "");
        }
       
       sxText.setText( decimalFormat.format( e.getTransform().getScale().x ));
       syText.setText( decimalFormat.format( e.getTransform().getScale().y ));
       szText.setText( decimalFormat.format( e.getTransform().getScale().z ));
    }

    public enum FieldsToChange{
        xField, yField, zField, rxField, ryField, rzField,
        wField, frField,
        ampField, phaseField,
        sxField, syField, szField
    };
    private FieldsToChange sliderField;
    private float sliderMin, sliderMax, sliderScale;
    public void changeSlider(FieldsToChange field, String name, float scale, float min, float max){
        sliderField = field;
        sliderFieldLabel.setText(name);
        sliderMin = min;
        sliderMax = max;
        sliderScale = scale;
    }
    
    private void changeSelectionField(FieldsToChange field, float value, boolean absolute, boolean updateTextField){
        float[] angles = new float[3];
        
        for(Entity e : selection){
            Transform tra = e.getTransform();
            
            if(field == FieldsToChange.xField){
                tra.getTranslation().x = absolute ? value : tra.getTranslation().x + value;
                if (updateTextField) { xText.setText( decimalFormat.format(tra.getTranslation().x)); }
            }else if(field == FieldsToChange.yField){
                tra.getTranslation().y = absolute ? value : tra.getTranslation().y + value;
                if (updateTextField) { yText.setText( decimalFormat.format(tra.getTranslation().y)); }
            }else if(field == FieldsToChange.zField){
                tra.getTranslation().z = absolute ? value : tra.getTranslation().z + value;
                if (updateTextField) { zText.setText( decimalFormat.format(tra.getTranslation().z)); }
            }else if(field == FieldsToChange.sxField){
                tra.getScale().x = absolute ? value : tra.getScale().x + value;
                if (updateTextField) { sxText.setText( decimalFormat.format( tra.getScale().x )); }
            }else if(field == FieldsToChange.syField){
                 tra.getScale().y = absolute ? value : tra.getScale().y + value;
                if (updateTextField) { syText.setText( decimalFormat.format( tra.getScale().y )); }
            }else if(field == FieldsToChange.szField){
                 tra.getScale().z = absolute ? value : tra.getScale().z + value;
                if (updateTextField) { szText.setText( decimalFormat.format( tra.getScale().z )); }
            }else if(field == FieldsToChange.rxField || 
                    field == FieldsToChange.ryField || 
                    field == FieldsToChange.rzField){
                float rads = value * FastMath.DEG_TO_RAD;
                Quaternion q = tra.getRotation();
                q.toAngles(angles);
                if(field == FieldsToChange.rxField) {
                    angles[0] = absolute ? rads : angles[0] + rads;
                    if (updateTextField) { rxText.setText( decimalFormat.format(angles[0] * FastMath.RAD_TO_DEG)); }
                }else if (field == FieldsToChange.ryField) {
                    angles[1] = absolute ? rads : angles[1] + rads;
                    if (updateTextField) { ryText.setText( decimalFormat.format(angles[1] * FastMath.RAD_TO_DEG)); }
                }else if (field == FieldsToChange.rzField) {
                    angles[2] = absolute ? rads : angles[2] + rads;
                    if (updateTextField) { rzText.setText( decimalFormat.format(angles[2] * FastMath.RAD_TO_DEG)); }
                }
                q.fromAngles(angles);
            }
            if (e instanceof Transducer){
                Transducer t = (Transducer)e;
                if(field == FieldsToChange.wField){
                    t.width = absolute ? value : t.width + value;
                    if (updateTextField) { transPanel.getwText().setText( decimalFormat.format( t.width )); }
                }else if(field == FieldsToChange.frField){
                    t.frequency = absolute ? value : t.frequency + value;
                    if (updateTextField) {transPanel.getFrText().setText( decimalFormat.format( t.frequency )); }
                }else if(field == FieldsToChange.ampField){
                    t.pAmplitude = absolute ? value : t.pAmplitude + value;
                    if (updateTextField) {transPanel.getAmpText().setText( decimalFormat.format( t.pAmplitude )); }
                }else if(field == FieldsToChange.phaseField){
                    t.phase = absolute ? value : t.phase + value;
                    if (updateTextField) { transPanel.getPhaseText().setText( decimalFormat.format( t.phase )); }
                }
            }
            
            updateTextField = false; //only use the first transducer, only one value can be displayed in the text field
        }
    }
    
    public Scene getScene() {
        return scene;
    }

    public ArrayList<Entity> getSelection() {
        return selection;
    }
       
}
