/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.math.FastMath;
import acousticfield3d.math.Matrix4f;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Scene;
import acousticfield3d.simulation.Simulation;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Asier
 */
public class IsoRTShader extends ShaderTransducers{
     
    int simInvGain;

    int centerCube, sizeCube;
    int densityDist;
    int maxAlpha;
    int minIso, maxIso;
    
    int timeBottomMin, timeBottomMax, timeTopMin, timeTopMax;
    
    public IsoRTShader(String vProgram, String fProgram) {
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
        maxIso = gl.glGetUniformLocation(shaderProgramID, "maxIso");
        minIso = gl.glGetUniformLocation(shaderProgramID, "minIso");
        
        timeBottomMin = gl.glGetUniformLocation(shaderProgramID, "timeBottomMin");
        timeBottomMax = gl.glGetUniformLocation(shaderProgramID, "timeBottomMax");
        timeTopMin = gl.glGetUniformLocation(shaderProgramID, "timeTopMin");
        timeTopMax = gl.glGetUniformLocation(shaderProgramID, "timeTopMax");
        
       
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
       
       gl.glUniform1f(simInvGain, renderer.getForm().rtIsoPanel.getRtIsoAmpScale() ); 
       
       Vector3f cubeT = scene.getRtIsoVol().getTransform().getTranslation();
       Vector3f cubeS = scene.getRtIsoVol().getTransform().getScale();
       
       gl.glUniform3f(centerCube, cubeT.x, cubeT.y, cubeT.z);
       gl.glUniform3f(sizeCube, cubeS.x, cubeS.y, cubeS.z);
       
       
       gl.glUniform1f(densityDist, 1.0f / renderer.getForm().rtIsoPanel.getRtIsoDensity() ); 
       gl.glUniform1f(maxAlpha,  renderer.getForm().rtIsoPanel.getRtIsoMaxAlpha() );
       gl.glUniform1f(minIso,  renderer.getForm().rtIsoPanel.getRtMinIsoValue() );
       gl.glUniform1f(maxIso,  renderer.getForm().rtIsoPanel.getRtMaxIsoValue() );
       
        final float time = renderer.getForm().animPanel.getCurrentTime();
       final float margin = renderer.getForm().miscPanel.getInstantAmplitudeMargin() * FastMath.PI;
       final float period = 1.0f / renderer.getForm().simulation.getTransducers().get(0).getFrequency();

       final float topMin = (((time % period) / period * 2.0f) - 1.0f) * FastMath.PI;

        gl.glUniform1f(timeTopMin, topMin);
        gl.glUniform1f(timeTopMax, topMin + margin);
        gl.glUniform1f(timeBottomMin, topMin - FastMath.TWO_PI);
        gl.glUniform1f(timeBottomMax, topMin + margin - FastMath.TWO_PI);
    }
    

}
