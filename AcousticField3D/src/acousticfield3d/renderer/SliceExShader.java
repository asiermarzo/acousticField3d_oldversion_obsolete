/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

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
public class SliceExShader extends Shader{
    private FieldSource source;
    
    int colouring;
    int minPosColor, maxPosColor;
    int alphaValue;
    int texture;
    
    int exGain;
    int exOffset;
    
    private float lastAlpha = 1.0f;
    public SliceExShader(String vProgram, String fProgram, FieldSource source) {
        super(vProgram, fProgram, ORDER_OPAQUE);
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
 
        texture = gl.glGetUniformLocation(shaderProgramID, "text");
        alphaValue = gl.glGetUniformLocation(shaderProgramID, "alphaValue");
        minPosColor = gl.glGetUniformLocation(shaderProgramID, "minPosColor");
        maxPosColor = gl.glGetUniformLocation(shaderProgramID, "maxPosColor");
        colouring = gl.glGetUniformLocation(shaderProgramID, "colouring");
        exGain = gl.glGetUniformLocation(shaderProgramID, "exGain");
        exOffset = gl.glGetUniformLocation(shaderProgramID, "exOffset");
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
        renderer.enableTexture2D(gl, true);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, renderer.textureId);
    }

    @Override
    void bindUniforms(GL2 gl, Scene scene, Renderer renderer,Simulation s, MeshEntity me, Matrix4f projectionViewModel, Matrix4f viewModel, Matrix4f model, FloatBuffer fb) {
       super.bindUniforms(gl, scene, renderer, s, me, projectionViewModel, viewModel, model, fb);
        
       lastAlpha = renderer.getForm().rtSlicePanel.getRTSliceAlpha();
       
       gl.glUniform1i(texture, 0);
       gl.glUniform1f(alphaValue, lastAlpha ); 
       gl.glUniform1f(minPosColor, renderer.getForm().rtSlicePanel.getAmpColorMin());
       gl.glUniform1f(maxPosColor, renderer.getForm().rtSlicePanel.getAmpColorMax());
       gl.glUniform1i(colouring, renderer.getForm().rtSlicePanel.getColouringCombo());
       gl.glUniform1f(exGain, renderer.getForm().exSlicePanel.getGain());
       gl.glUniform1f(exOffset, renderer.getForm().exSlicePanel.getOffset());
    }

    @Override
    protected String preProcessFragment(String sourceCode, HashMap<String,String> templates) {
        sourceCode = super.preProcessFragment(sourceCode, templates);
        
        sourceCode = sourceCode.replaceAll("_USE_AMP_", source == FieldSource.sourceAmp ? "" : "//");
        sourceCode = sourceCode.replaceAll("_USE_PHASE_", source == FieldSource.sourcePhase ? "" : "//");
        sourceCode = sourceCode.replaceAll("_USE_AMPPHASE_", source == FieldSource.sourceAmpPhase ? "" : "//");
        
        return sourceCode;
    }

    
    
}
