/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.gui.MainForm;
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
public class IsoRTGorkovShader extends ShaderTransducers{
     
    int colorGain;

    int gSize;
    int gSep;
    int kPreToVel;
    int kPre;
    int kVel;
    int pVol;
    
    int centerCube, sizeCube;
    int densityDist;
    int maxAlpha;
    int minIso, maxIso;
    
    public IsoRTGorkovShader(String vProgram, String fProgram) {
        super(vProgram, fProgram, ORDER_TRANSLUCENT, 10);
    }
        

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
        
        gSize = gl.glGetUniformLocation(shaderProgramID, "gSize");
        gSep = gl.glGetUniformLocation(shaderProgramID, "gSep");
        kPreToVel = gl.glGetUniformLocation(shaderProgramID, "kPreToVel");
        kPre = gl.glGetUniformLocation(shaderProgramID, "kPre");
        kVel = gl.glGetUniformLocation(shaderProgramID, "kVel");
        pVol = gl.glGetUniformLocation(shaderProgramID, "pVol");
        
        colorGain = gl.glGetUniformLocation(shaderProgramID, "simInvGain");
        centerCube = gl.glGetUniformLocation(shaderProgramID, "cubeCenter");
        sizeCube = gl.glGetUniformLocation(shaderProgramID, "cubeSize");
     
        densityDist = gl.glGetUniformLocation(shaderProgramID, "densityDist");
        
        maxAlpha = gl.glGetUniformLocation(shaderProgramID, "maxAlpha");
        maxIso = gl.glGetUniformLocation(shaderProgramID, "maxIso");
        minIso = gl.glGetUniformLocation(shaderProgramID, "minIso");
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
       
        MainForm f = renderer.getForm();
        
        gl.glUniform1i(gSize, f.rtSlicePanel.getGradientSize() );
        gl.glUniform1f(gSep, renderer.vgSep );
        gl.glUniform1f(kPreToVel, renderer.vkPreToVel ); 
        gl.glUniform1f(kPre, renderer.vkPre ); 
        gl.glUniform1f(kVel, renderer.vkVel ); 
        gl.glUniform1f(pVol, renderer.vpVol );

       final float vMax = renderer.getForm().rtSlicePanel.getGorkovPosColorMax();
       gl.glUniform1f(colorGain, vMax);
       
       Vector3f cubeT = scene.getRtIsoVol().getTransform().getTranslation();
       Vector3f cubeS = scene.getRtIsoVol().getTransform().getScale();
       
       gl.glUniform3f(centerCube, cubeT.x, cubeT.y, cubeT.z);
       gl.glUniform3f(sizeCube, cubeS.x, cubeS.y, cubeS.z);
       
       gl.glUniform1f(densityDist, 1.0f / f.rtIsoPanel.getRtIsoDensity() ); 
       gl.glUniform1f(maxAlpha,  f.rtIsoPanel.getRtIsoMaxAlpha() );
       gl.glUniform1f(minIso,  f.rtIsoPanel.getRtMinIsoValue() * vMax);
       gl.glUniform1f(maxIso,  f.rtIsoPanel.getRtMaxIsoValue() * vMax);
    }
    

}
