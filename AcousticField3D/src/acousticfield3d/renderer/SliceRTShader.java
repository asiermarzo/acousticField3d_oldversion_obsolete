/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.math.FastMath;
import acousticfield3d.math.Matrix4f;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Resources;
import acousticfield3d.scene.Scene;
import acousticfield3d.simulation.FieldSource;
import acousticfield3d.simulation.Simulation;
import java.nio.FloatBuffer;
import java.util.HashMap;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Asier
 */
public class SliceRTShader extends ShaderTransducers{
    private FieldSource source;
    
    int colouring;
    int minPosColor, maxPosColor;
    int minNegColor, maxNegColor;
    int alphaValue;
    
    int timeRaw;
    int timeBottomMin, timeBottomMax, timeTopMin, timeTopMax;
    
    private float lastAlpha = 1.0f;
    public SliceRTShader(String vProgram, String fProgram, FieldSource source) {
        super(vProgram, fProgram, ORDER_OPAQUE, 10);
        this.source = source;
    }

    @Override
    public int getRenderingOrder(MeshEntity me) {
        if (lastAlpha < 1.0f){
            return ORDER_TRANSLUCENT;
        }else{
            return ORDER_OPAQUE;
        }
    }
    
    public FieldSource getSource() {
        return source;
    }

    public void setSource(FieldSource source) {
        this.source = source;
    }
    

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
 
        alphaValue = gl.glGetUniformLocation(shaderProgramID, "alphaValue");
        minPosColor = gl.glGetUniformLocation(shaderProgramID, "minPosColor");
        maxPosColor = gl.glGetUniformLocation(shaderProgramID, "maxPosColor");
        colouring = gl.glGetUniformLocation(shaderProgramID, "colouring");
        
        timeRaw = gl.glGetUniformLocation(shaderProgramID, "time");
        timeBottomMin = gl.glGetUniformLocation(shaderProgramID, "timeBottomMin");
        timeBottomMax = gl.glGetUniformLocation(shaderProgramID, "timeBottomMax");
        minNegColor = gl.glGetUniformLocation(shaderProgramID, "minNegColor");
        maxNegColor = gl.glGetUniformLocation(shaderProgramID, "maxNegColor");
        timeTopMin = gl.glGetUniformLocation(shaderProgramID, "timeTopMin");
        timeTopMax = gl.glGetUniformLocation(shaderProgramID, "timeTopMax");
    }
    
    @Override
    void changeGLStatus(GL2 gl, Renderer renderer, Simulation s, MeshEntity e) {
        renderer.enableBlend(gl, lastAlpha < 1.0f);
        if (e.getMesh().equals( Resources.MESH_QUAD ) || e.getMesh().equals( Resources.MESH_CUSTOM )){
            renderer.enableCullFace(gl, false);
        }else{
            renderer.enableCullFace(gl, true);
        }
        
        renderer.enableDepthTest(gl, true);
        renderer.enableTexture2D(gl, false);
    }

    @Override
    void bindUniforms(GL2 gl, Scene scene, Renderer renderer,Simulation s, MeshEntity me, Matrix4f projectionViewModel, Matrix4f viewModel, Matrix4f model, FloatBuffer fb) {
       super.bindUniforms(gl, scene, renderer, s, me, projectionViewModel, viewModel, model, fb);
        
       lastAlpha = renderer.getForm().rtSlicePanel.getRTSliceAlpha();
       gl.glUniform1f(alphaValue, lastAlpha ); 
       gl.glUniform1f(minPosColor, renderer.getForm().rtSlicePanel.getAmpColorMin());
       gl.glUniform1f(maxPosColor, renderer.getForm().rtSlicePanel.getAmpColorMax());
       
       gl.glUniform1f(minNegColor, -renderer.getForm().rtSlicePanel.getAmpColorMax());
       gl.glUniform1f(maxNegColor, -renderer.getForm().rtSlicePanel.getAmpColorMin());
       
       gl.glUniform1i(colouring, renderer.getForm().rtSlicePanel.getColouringCombo());
       
       final float time = renderer.getForm().animPanel.getCurrentTime();
       final float margin = renderer.getForm().miscPanel.getInstantAmplitudeMargin() * FastMath.PI;
       final float period = 1.0f / renderer.getForm().simulation.getTransducers().get(0).getFrequency();

       
       gl.glUniform1f(timeRaw, time);
       
       final float topMin = (((time % period) / period * 2.0f) - 1.0f) * FastMath.PI;

        gl.glUniform1f(timeTopMin, topMin);
        gl.glUniform1f(timeTopMax, topMin + margin);
        gl.glUniform1f(timeBottomMin, topMin - FastMath.TWO_PI);
        gl.glUniform1f(timeBottomMax, topMin + margin - FastMath.TWO_PI);
    }

    @Override
    protected String preProcessFragment(String sourceCode, HashMap<String,String> templates) {
        sourceCode = super.preProcessFragment(sourceCode, templates);
        
        sourceCode = sourceCode.replaceAll("_USE_AMP_", source == FieldSource.sourceAmp ? "" : "//");
        sourceCode = sourceCode.replaceAll("_USE_PHASE_", source == FieldSource.sourcePhase ? "" : "//");
        sourceCode = sourceCode.replaceAll("_USE_AMPPHASE_", source == FieldSource.sourceAmpPhase ? "" : "//");
        sourceCode = sourceCode.replaceAll("_USE_TAMP_", source == FieldSource.sourceTamp ? "" : "//");
        sourceCode = sourceCode.replaceAll("_USE_TAMPDIFFFR_", source == FieldSource.sourceTAmpDiffFr ? "" : "//");
        
        return sourceCode;
    }

    @Override
    void render(GL2 gl, Simulation s, MeshEntity me) {
        //TimerUtil.get().tick("Render slice");
        super.render(gl, s, me); //To change body of generated methods, choose Tools | Templates.
        //TimerUtil.get().tack("Render slice");
    }
    
    
}
