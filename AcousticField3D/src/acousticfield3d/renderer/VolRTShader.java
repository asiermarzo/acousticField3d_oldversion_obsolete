/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.math.Matrix4f;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Scene;
import acousticfield3d.simulation.Simulation;
import java.nio.FloatBuffer;
import java.util.HashMap;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Asier
 */
public class VolRTShader extends ShaderTransducers{
    public static final int ALPHA_MAP_VALUES = 64;
 
    int simInvGain;

    int centerCube, sizeCube;
    int densityDist;
    int maxAlpha;
    int alphaMap;
    int alphaMapSum;
    
     
    public VolRTShader(String vProgram, String fProgram) {
        super(vProgram, fProgram, ORDER_TRANSLUCENT, 10);
    }
        

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
        
        simInvGain = gl.glGetUniformLocation(shaderProgramID, "simInvGain");
      
        centerCube = gl.glGetUniformLocation(shaderProgramID, "cubeCenter");
        sizeCube = gl.glGetUniformLocation(shaderProgramID, "cubeSize");
     
        densityDist = gl.glGetUniformLocation(shaderProgramID, "densityDist");
        
        maxAlpha = gl.glGetUniformLocation(shaderProgramID, "maxAlpha");
        alphaMap = gl.glGetUniformLocation(shaderProgramID, "alphaMap");
        alphaMapSum = gl.glGetUniformLocation(shaderProgramID, "alphaMapSum");
    }
    
    @Override
    protected String preProcessFragment(String sourceCode,  HashMap<String,String> templates) {
        sourceCode = super.preProcessFragment(sourceCode, templates);
        
        sourceCode = sourceCode.replaceAll("_N_ALPHA_", ALPHA_MAP_VALUES + "");
        
        return sourceCode;
    }
    

    @Override
    void changeGLStatus(GL2 gl, Renderer renderer, Simulation s, MeshEntity e) {
        renderer.enableBlend(gl, true);
        renderer.enableCullFace(gl, true);
        renderer.enableDepthTest(gl, true);
        renderer.enableTexture2D(gl, false);
        renderer.enableTexture3D(gl, false);
    }
    

    @Override
    void bindUniforms(GL2 gl, Scene scene, Renderer renderer, Simulation s, MeshEntity me, Matrix4f projectionViewModel, Matrix4f viewModel, Matrix4f model, FloatBuffer fb) {
       super.bindUniforms(gl, scene, renderer, s, me, projectionViewModel, viewModel, model, fb);
       
       gl.glUniform1f(simInvGain, renderer.getForm().rtVolPanel.getRTVolAmpScale() ); 
       
       Vector3f cubeT = scene.getRtCubeVol().getTransform().getTranslation();
       Vector3f cubeS = scene.getRtCubeVol().getTransform().getScale();
       
       gl.glUniform3f(centerCube, cubeT.x, cubeT.y, cubeT.z);
       gl.glUniform3f(sizeCube, cubeS.x, cubeS.y, cubeS.z);
       
       gl.glUniform1f(densityDist, 1.0f / renderer.getForm().rtVolPanel.getRTVolDensity() ); 
       
       gl.glUniform1f(maxAlpha,  renderer.getForm().rtVolPanel.getRTCubeMaxAlpha() ); 
       
       synchronized(renderer.getForm().rtVolPanel.getRTVolAlphaPanel()){
            gl.glUniform1fv(alphaMap, ALPHA_MAP_VALUES, renderer.getForm().rtVolPanel.getRTVolAlphaPanel().getValues());
            gl.glUniform1f(alphaMapSum, renderer.getForm().rtVolPanel.getRTVolAlphaPanel().getTotalValue());
       }
    }
  
}
