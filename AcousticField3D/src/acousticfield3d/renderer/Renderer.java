/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.gui.MainForm;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Matrix4f;
import acousticfield3d.math.TempVars;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Resources;
import acousticfield3d.scene.Scene;
import acousticfield3d.simulation.Simulation;
import acousticfield3d.simulation.Transducer;
import acousticfield3d.utils.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Comparator;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Asier
 */
public class Renderer {
    private final Scene scene;
    private final MainForm form;
   
    private boolean cullFace;
    private boolean depthTest;
    private boolean blend;
    private boolean texture2d;
    private boolean texture3d;
    private boolean writeColor;
    
    int nTransducers;
    FloatBuffer positions;
    FloatBuffer normals;
    FloatBuffer specs;
    
    float vkPreToVel;
    float vkPre;
    float vkVel;
    float vpVol;
    float vgSep;
    
    final Vector3f preCubeMin = new Vector3f(), preCubeMax = new Vector3f();
    private int preCubeSize;
    private FloatBuffer newPreCube;
    private FloatBuffer oldPreCube;
    private int preCubeNewSize;
    int preCubeId;
    int textureId;
    boolean needToReuploadTexture;
    float[][] textureData0;
    float[][] textureData1;
    
    private boolean needToReloadShaders;

    public MainForm getForm() {
        return form;
    }
    
    public Renderer(Scene scene, MainForm form) {
        needToReloadShaders = false;
        needToReuploadTexture = false;
        this.scene = scene;
        this.form = form;
    }

    public void reloadShaders() {
        needToReloadShaders = true;
    }
    
    
    public void init(GL2 gl, int w, int h){
        Resources.init(gl, ! form.miscPanel.isAnalyticalNoDirShaders());
         
        //create 3d texture for preCube
        createPreCube(gl);
        
        gl.glClearColor(0, 0, 0.0f, 1);
        
        //set camera
        reshape(gl, w, h);
    }
    
    /*

    private void initCL(GL3 gl)
    {
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CLDeviceBinding.CL_DEVICE_TYPE_GPU;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
  `      CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        initContextProperties(contextProperties, gl);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(
            contextProperties, 1, new cl_device_id[]{device}, 
            null, null, null);
        
        // Create a command-queue for the selected device
        commandQueue = 
            clCreateCommandQueue(context, device, 0, null);

        // Read the program source code and create the program
        String source = readFile("kernels/simpleGL.cl");
        cl_program program = clCreateProgramWithSource(context, 1, 
            new String[]{ source }, null, null);
        clBuildProgram(program, 0, null, "-cl-mad-enable", null, null);

        // Create the kernel which computes the sine wave pattern
        kernel = clCreateKernel(program, "sine_wave", null);
        
        // Set the constant kernel arguments 
        clSetKernelArg(kernel, 1, Sizeof.cl_uint, 
            Pointer.to(new int[]{ meshWidth }));
        clSetKernelArg(kernel, 2, Sizeof.cl_uint, 
            Pointer.to(new int[]{ meshHeight }));
    }
    */
    public void reshape(GL2 gl, int w, int h){
        gl.glViewport( 0, 0, w, h );
        
        //set camera
        scene.getCamera().updateProjection(w/(float)h);
    }
    
    public void dispose(GL2 gl){
        //deatach shader
        gl.glUseProgram(0);
        
        //deattach textures
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_3D, 0);
        
        //relase shaders and textures
        Resources.get().releaseResources(gl);
        
        //delete texture 3d
        destroyPreCube(gl);
    }
    
    private void preRender(GL2 gl){
        form.simulation.updateReflectedTransducers();
        
        if (needToReloadShaders){
            needToReloadShaders = false;
            Resources.get().reloadShaders(gl);
        }
        
        if(needToReuploadTexture){
            needToReuploadTexture = false;
            updateDataTexture(gl);
        }
        
        updateGorkovConsts();
        
        //tick the algorithms working in continous mode
        form.algorithmsForm.tickEnabledAlgorithms();
        
        //copy the phases and amp from the clone transducers
        form.transControlPanel.updateClones();
        
        //update bead controllers
        form.beadController.tick();
       
        //update mouse controller
        if (form.mouseControlForm != null){
            form.mouseControlForm.tick();
        }
        
        Simulation simulation = form.getSimulation();
        updateTransducersBuffers(simulation);
        
        //check if the shaders using transducers have the correct number of them
        if( nTransducers > 0 ){
            Resources.get().updateShaderTransducers(nTransducers, gl);
        }
        
        //check if there is a new preCalc cube available
        if (newPreCube != null){
            oldPreCube = newPreCube;
            //if the size is different -> destroy
            if (preCubeSize != preCubeNewSize){
                destroyPreCube(gl);
                createPreCube(gl);
            }
            preCubeSize = preCubeNewSize;
            gl.glEnable( GL2.GL_TEXTURE_3D);
            gl.glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL2.GL_R32F, 
                    preCubeSize, preCubeSize, preCubeSize, 0, GL2.GL_RED,  GL2.GL_FLOAT, newPreCube);
            gl.glDisable(GL2.GL_TEXTURE_3D);
            newPreCube = null;
        }
        
        //tick physics if they are enabled
        form.physicsPanel.tickIfEnabledForRender();
        
        //tick path tracer if it is enabled
       form.movePanel.tickPathMovementIfEnabled();
        
        //followSlices and Cubes enabled per tick
        if ( form.beadsToAnimationForm.isEnablePerTick() ){
            form.beadsToAnimationForm.adjustBeads(0, Entity.TAG_CONTROL_POINT);
            form.beadsToAnimationForm.adjustSlicesAndCubes(0, Entity.TAG_CONTROL_POINT);
        }
    }

    private void updateGorkovConsts() {
        final float rohP = form.simPanel.getParticleDensity();
        final float roh = form.simPanel.getMediumDensity();
        final float cP = form.simPanel.getParticleSpeed();
        final float c = Simulation.getSoundSpeedInAir(form.simPanel.getTemperature());
        final float omega = form.simulation.getTransFrequency() * FastMath.TWO_PI;
        final float particleR = form.simPanel.getParticleSize();
        
        final float kapa = 1.0f / (roh * (c*c));
        final float kapa_p = 1.0f / (rohP * (cP*cP));
        final float k_tilda = kapa_p / kapa;
        final float f_1_bruus = 1.0f - k_tilda;
        
        final float roh_tilda = rohP / roh;
        final float f_2_bruus = (2.0f * (roh_tilda - 1.0f)) / ((2.0f * roh_tilda) + 1.0f);
        
        vgSep = form.simPanel.getWaveLength() / form.rtSlicePanel.getGradientDivs();
        vkPreToVel = 1.0f / (roh*omega);
        vkPre = f_1_bruus*0.5f*kapa*0.5f;
        vkVel = f_2_bruus*(3.0f/4.0f)*roh*0.5f;
        vpVol = (4.0f/3.0f)*FastMath.PI*(particleR*particleR*particleR);
    }

    public void updateTransducersBuffers(Simulation simulation) {
        //transducers data
        nTransducers = simulation.getTransducers().size();
        final int minCapacity3 = 3  * nTransducers;
        final int minCapacity4 = 4  * nTransducers;
        if (positions != null && positions.capacity() < minCapacity3) { BufferUtils.destroyDirectBuffer(positions); positions = null;}
        if (normals != null && normals.capacity() < minCapacity3) { BufferUtils.destroyDirectBuffer(normals); normals = null;}
        if (specs != null && specs.capacity() < minCapacity4) { BufferUtils.destroyDirectBuffer(specs); specs = null;}

        if (positions == null){ positions = BufferUtils.createFloatBuffer(minCapacity3); }
        if (normals == null){ normals = BufferUtils.createFloatBuffer( minCapacity3); }
        if (specs == null){ specs = BufferUtils.createFloatBuffer(minCapacity4); }
        
        positions.rewind();
        normals.rewind();
        specs.rewind();
        
        //calculate transducer uniform
        // x y z 1 -> positions
        // nx ny nz 1 -> normals
        // k amp phase w -> specs
        
        Vector3f transNormal = new Vector3f();
        float temperature = form.simPanel.getTemperature();
        float airSpeed = Simulation.getSoundSpeedInAir(temperature);  // m/s, sound speed in air
        
        final boolean discAmp = form.miscPanel.isAmpDiscretizer();
        final boolean discPhase = form.miscPanel.isPhaseDiscretizer();
        final float ampDiscStep = 1.0f / form.miscPanel.getAmpDiscretization();
        final float phaseDiscStep = 1.0f / form.miscPanel.getPhaseDiscretization();
        final float transPower = simulation.getTransPower();
        
        for(Transducer t : simulation.getTransducers()){
            positions.put( t.getTransform().getTranslation().x );
            positions.put( t.getTransform().getTranslation().y );
            positions.put( t.getTransform().getTranslation().z );
            
            t.getTransform().getRotation().mult( Vector3f.UNIT_Y, transNormal);
            normals.put( transNormal.x );
            normals.put( transNormal.y );
            normals.put( transNormal.z );
            
            float omega = 2.0f * FastMath.PI * t.getFrequency();      // angular frequency
            float k = omega / airSpeed;        // wavenumber
            specs.put( k ); // k
            
            specs.put( t.calcRealDiscAmplitude(discAmp, ampDiscStep, transPower)); // amp
            specs.put( t.calcRealDiscPhase(discPhase, phaseDiscStep) ); // phase
            
            //specs.put( t.getWidth() ); // width
            specs.put( (omega * form.animPanel.getCurrentTime()) % FastMath.TWO_PI); // omega
        }
        
        positions.rewind();
        normals.rewind();
        specs.rewind();
    }
    
    private void postRender(GL2 gl){
        form.cpPanel.updateControlPointValues();
    }
    
    public void render(GL2 gl, int w, int h){
        preRender(gl);
        
        gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        
        Matrix4f projection = scene.getCamera().getProjection();
        
        Matrix4f view = new Matrix4f();
        scene.getCamera().getTransform().copyTo(view);
        view.invertLocal();
     
        TempVars tv = TempVars.get();
        
        gl.glEnable(GL2.GL_CULL_FACE); cullFace = true;
	gl.glEnable(GL2.GL_DEPTH_TEST); depthTest = true;
	gl.glDisable(GL2.GL_BLEND); blend = false;
        gl.glDisable(GL2.GL_TEXTURE_2D); texture2d = false;
        gl.glDisable(GL2.GL_TEXTURE_3D); texture3d = false;
        gl.glColorMask(true, true, true, true); writeColor = true;

        Texture lastTexture = null;
	Shader lastShader = null;
	Matrix4f model = tv.tempMat4;
        Matrix4f viewModel = tv.tempMat42;
        Matrix4f projectionViewModel = tv.tempMat43;

        synchronized (form) {
            calcDistanceToCameraOfEntities();
            sortEntities();
            
            for (MeshEntity me : scene.getEntities()) {

                if (!me.isVisible()) {
                    continue;
                }

                me.getTransform().copyTo(model);

                int shaderId = me.getShader();
                Shader currentShader = Resources.get().getShader(shaderId);
                if (currentShader == null) {
                    continue;
                }
                if (lastShader != currentShader) {
                    lastShader = currentShader;
                    gl.glUseProgram(lastShader.shaderProgramID);
                    gl.glUniform1i(lastShader.texDiffuse, 0);
                }
                if(lastShader != null) { lastShader.changeGLStatus(gl, this, form.getSimulation(), me); }

                if (lastTexture != me.getTexture()) {
                    lastTexture = me.getTexture();
                    if (lastTexture != null) {
                        gl.glBindTexture(GL2.GL_TEXTURE_2D, lastTexture.getId());
                    } else {
                        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
                    }
                }

                //check for negative scale
                boolean usesCull = true;
                boolean needReverseCulling = usesCull && (model.get(0, 0) * model.get(1, 1) * model.get(2, 2) < 0);

                if (needReverseCulling) {
                    //gl.glCullFace(GL2.GL_FRONT);
                }

                view.mult(model, viewModel);
                projection.mult(viewModel, projectionViewModel);

                lastShader.bindAttribs(gl, form.getSimulation(), me);

                lastShader.bindUniforms(gl, scene, this, form.getSimulation(), me, projectionViewModel, viewModel, model, tv.floatBuffer16);

                lastShader.render(gl, form.getSimulation(), me);

                lastShader.unbindAttribs(gl, form.getSimulation(), me);

                if (needReverseCulling) {
                    //gl.glCullFace(GL2.GL_BACK);
                }

            }
        }

        tv.release();

        postRender(gl);
    }
        
    void enableCullFace(GL2 gl, boolean enabled){
        if (enabled != cullFace){
            if (enabled){
                gl.glEnable(GL2.GL_CULL_FACE);
            }else{
                gl.glDisable(GL2.GL_CULL_FACE);
            }
            cullFace = enabled;
        }
    }
    
    void enableDepthTest(GL2 gl, boolean enabled){
        if (enabled != depthTest){
            if (enabled){
                gl.glEnable(GL2.GL_DEPTH_TEST);
            }else{
                gl.glDisable(GL2.GL_DEPTH_TEST);
            }
            depthTest = enabled;
        }
    }
        
    void enableBlend(GL2 gl, boolean enabled){
        if (enabled != blend){
            if (enabled){
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
            }else{
                gl.glDisable(GL2.GL_BLEND);
            }
            blend = enabled;
        }
    }
            
    void enableTexture2D(GL2 gl, boolean enabled){
        if (enabled != texture2d){
            if (enabled){
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }else{
                gl.glDisable(GL2.GL_TEXTURE_2D);
            }
            texture2d = enabled;
        }
    }
    
    void enableTexture3D(GL2 gl, boolean enabled){
        if (enabled != texture3d){
            if (enabled){
                gl.glEnable(GL2.GL_TEXTURE_3D);
            }else{
                gl.glDisable(GL2.GL_TEXTURE_3D);
            }
            texture3d = enabled;
        }
    }
    
    void enableWriteColor(GL2 gl, boolean enabled){
        if (enabled != writeColor){
            if (enabled){
                gl.glColorMask(true, true, true, true);
            }else{
                gl.glColorMask(false, false, false, false);
            }
            writeColor = enabled;
        }
    }
    
    
    public void updatePreCalcCube(Vector3f minSimBound, Vector3f maxSimBound, FloatBuffer data, int dataSize) {
        //TODO calc cube finished
        
        preCubeMin.set ( minSimBound );
        preCubeMax.set ( maxSimBound );
        newPreCube = data;
        preCubeNewSize = dataSize;
        form.needUpdate();
    }
  
    private void createPreCube(GL2 gl) {
        IntBuffer i = BufferUtils.createIntBuffer(1);
        gl.glEnable( GL2.GL_TEXTURE_3D );
        gl.glGenTextures(1, i);
        preCubeId = i.get();
        gl.glBindTexture(GL2.GL_TEXTURE_3D, preCubeId);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP);
        //glTexImage3D(GL_TEXTURE_3D, 0, GL_RGB8, WIDTH, HEIGHT, DEPTH, 0, GL_RGB,  GL_UNSIGNED_BYTE, texels);
        gl.glDisable(GL2.GL_TEXTURE_3D );
    }

    private void destroyPreCube(GL2 gl){
        gl.glEnable( GL2.GL_TEXTURE_3D );
        IntBuffer i = BufferUtils.createIntBuffer(1);
        i.put( preCubeId ); i.rewind();
        gl.glDeleteTextures(1, i);
        gl.glDisable(GL2.GL_TEXTURE_3D );
    }

    private void sortEntities() {
        Collections.sort(scene.getEntities(), new Comparator<MeshEntity>() {
                @Override
                public int compare(MeshEntity o1, MeshEntity o2) {
                    final int r1 = o1.renderingOrder;
                    final int r2 = o2.renderingOrder;
                    if (r1 == r2){
                        if (r1 == Shader.ORDER_TRANSLUCENT){
                            return Float.compare( o1.distanceToCamera, o2.distanceToCamera);
                        }else{
                            return Float.compare( o2.distanceToCamera, o1.distanceToCamera);
                        }
                    }else{
                        return Integer.compare(r1, r2);
                    }
                  
                }
            });
    }

    private void calcDistanceToCameraOfEntities() {
        Vector3f camRay = new Vector3f( Vector3f.UNIT_Z );
        Vector3f oPos = new Vector3f();
        Vector3f cPos = scene.getCamera().getTransform().getTranslation();
        scene.getCamera().getTransform().getRotation().mult(camRay, camRay);
        for(MeshEntity me : scene.getEntities()){
            if(me.isVisible()){
                oPos.set( me.getTransform().getTranslation() ).subtractLocal( cPos );
                me.distanceToCamera = camRay.dot( oPos );
                me.renderingOrder = Resources.get().getShader( me.getShader() ).getRenderingOrder( me );
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    public FloatBuffer getPreCube() {
        return oldPreCube;
    }
    
    public int getnTransducers() {
        return nTransducers;
    }
    
    public FloatBuffer getPositions() {
        return positions;
    }
    
    public FloatBuffer getNormals() {
        return normals;
    }
    
    public FloatBuffer getSpecs() {
        return specs;
    }
    
    public float getVkPreToVel() {
        return vkPreToVel;
    }
    
    public float getVkPre() {
        return vkPre;
    }
    
    public float getVkVel() {
        return vkVel;
    }
    
    public float getVpVol() {
        return vpVol;
    }
    
    public float getVgSep() {
        return vgSep;
    }
//</editor-fold>

    public void assignNewTexture(float[][] d0, float[][] d1) {
        textureData0 = d0;
        textureData1 = d1;
        needToReuploadTexture = true;
    }

    private void updateDataTexture(GL2 gl){
        enableTexture2D(gl, true);

        //destroy previous one
        if (textureId != 0){  
            IntBuffer i = BufferUtils.createIntBuffer(1);
            i.put( textureId ); i.rewind();
            gl.glDeleteTextures(1, i);
        }
        
        //create texture
        IntBuffer i = BufferUtils.createIntBuffer(1);
        gl.glEnable( GL2.GL_TEXTURE_2D );
        gl.glGenTextures(1, i);
        textureId = i.get();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        
        //upload texture
        final int size = textureData0.length;
        FloatBuffer buffer = BufferUtils.createFloatBuffer(size * size * 2);
        int index = 0;
        for(int iy = 0; iy < size; ++iy){
            for(int ix = 0; ix < size; ++ix){
                buffer.put(index, textureData0[ix][iy]);
                buffer.put(index+1, textureData1[ix][iy]);
                index+=2;
            }
        }
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RG32F, 
                    size, size, 0, GL2.GL_RG,  GL2.GL_FLOAT, buffer);
           
    }
}
