/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.math.Matrix4f;
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
public class VolPreShader extends Shader{
    public static final int ALPHA_MAP_VALUES = 64;
    
    int preCube;
    int centerCube, sizeCube;
    int densityDist;
    
    int alphaMap;
    int alphaMapSum;
    
    int renderMethod;
    int colouring;
    int minPosColor, maxPosColor;
    int alphaValue;

    public VolPreShader(String vProgram, String fProgram) {
        super(vProgram, fProgram, ORDER_TRANSLUCENT);
    }
        

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
        
        renderMethod = gl.glGetUniformLocation(shaderProgramID, "renderMethod");
                
        preCube = gl.glGetUniformLocation(shaderProgramID, "preCube");
        centerCube = gl.glGetUniformLocation(shaderProgramID, "cubeCenter");
        sizeCube = gl.glGetUniformLocation(shaderProgramID, "cubeSize");
     
        densityDist = gl.glGetUniformLocation(shaderProgramID, "densityDist");
        
        alphaMap = gl.glGetUniformLocation(shaderProgramID, "alphaMap");
        alphaMapSum = gl.glGetUniformLocation(shaderProgramID, "alphaMapSum");
        
        alphaValue = gl.glGetUniformLocation(shaderProgramID, "alphaValue");
        minPosColor = gl.glGetUniformLocation(shaderProgramID, "minPosColor");
        maxPosColor = gl.glGetUniformLocation(shaderProgramID, "maxPosColor");
        colouring = gl.glGetUniformLocation(shaderProgramID, "colouring");
    }
    
    @Override
    protected String preProcessFragment(String sourceCode,  HashMap<String,String> templates) {
        return super.preProcessFragment( 
                sourceCode.replaceAll("_N_ALPHA_", ALPHA_MAP_VALUES + ""), 
                templates);
    }

    @Override
    void changeGLStatus(GL2 gl, Renderer renderer, Simulation s, MeshEntity e) {
        renderer.enableBlend(gl, true);
        renderer.enableCullFace(gl, true);
        renderer.enableDepthTest(gl, true);
        renderer.enableTexture2D(gl, false);
        renderer.enableTexture3D(gl, true);
        
        gl.glBindTexture(GL2.GL_TEXTURE_3D, renderer.preCubeId);
    }

    @Override
    void bindUniforms(GL2 gl, Scene scene, Renderer renderer, Simulation s, MeshEntity me, Matrix4f projectionViewModel, Matrix4f viewModel, Matrix4f model, FloatBuffer fb) {
       super.bindUniforms(gl, scene, renderer, s, me, projectionViewModel, viewModel, model, fb);
       
       gl.glUniform1f(minPosColor, renderer.getForm().rtSlicePanel.getAmpColorMin());
       gl.glUniform1f(maxPosColor, renderer.getForm().rtSlicePanel.getAmpColorMax());
       gl.glUniform1i(colouring, renderer.getForm().rtSlicePanel.getColouringCombo());
       gl.glUniform1i(renderMethod, renderer.getForm().preVolPanel.getRenderMethod() );

       gl.glUniform1f(alphaValue, renderer.getForm().rtSlicePanel.getRTSliceAlpha() ); 
       gl.glUniform1f(minPosColor, renderer.getForm().rtSlicePanel.getAmpColorMin());
       gl.glUniform1f(maxPosColor, renderer.getForm().rtSlicePanel.getAmpColorMax());
       gl.glUniform1i(colouring, renderer.getForm().rtSlicePanel.getColouringCombo());
       
       gl.glUniform3f(centerCube, 
               (renderer.preCubeMax.x + renderer.preCubeMin.x) / 2.0f, 
               (renderer.preCubeMax.y + renderer.preCubeMin.y) / 2.0f,
               (renderer.preCubeMax.z + renderer.preCubeMin.z) / 2.0f);
       
       gl.glUniform3f(sizeCube, 
               (renderer.preCubeMax.x - renderer.preCubeMin.x) , 
               (renderer.preCubeMax.y - renderer.preCubeMin.y) ,
               (renderer.preCubeMax.z - renderer.preCubeMin.z));

       gl.glUniform1f(densityDist, 1.0f / renderer.getForm().preVolPanel.getPreVolDensity() ); 
       
       synchronized(renderer.getForm().preVolPanel.getPreVolAlphaPanel()){
            gl.glUniform1fv(alphaMap, ALPHA_MAP_VALUES, renderer.getForm().preVolPanel.getPreVolAlphaPanel().getValues());
            gl.glUniform1f(alphaMapSum, renderer.getForm().preVolPanel.getPreVolAlphaPanel().getTotalValue());
       }
       
       gl.glUniform1i(preCube, 0);
    }
    
    
}
