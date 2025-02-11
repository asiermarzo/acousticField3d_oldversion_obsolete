/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.gui.MainForm;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Matrix4f;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Resources;
import acousticfield3d.scene.Scene;
import acousticfield3d.simulation.Simulation;
import java.nio.FloatBuffer;
import java.util.HashMap;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Asier
 */
public class SliceColorBar extends ShaderTransducers{ 
    int colouring;
    int minNegColor, maxNegColor;
    int minPosColor, maxPosColor;
    int alphaValue;
    int useAmp;
    
    private float lastAlpha = 1.0f;   
    public SliceColorBar(String vProgram, String fProgram) {
        super(vProgram, fProgram, ORDER_OPAQUE, 10);
    }

    @Override
    public int getRenderingOrder(MeshEntity me) {
        if (lastAlpha < 1.0f){
            return ORDER_TRANSLUCENT;
        }else{
            return ORDER_OPAQUE;
        }
    }
    

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
 

        useAmp = gl.glGetUniformLocation(shaderProgramID, "useAmp");        
        alphaValue = gl.glGetUniformLocation(shaderProgramID, "alphaValue");
        minNegColor = gl.glGetUniformLocation(shaderProgramID, "minNegColor");
        maxNegColor = gl.glGetUniformLocation(shaderProgramID, "maxNegColor");
        minPosColor = gl.glGetUniformLocation(shaderProgramID, "minPosColor");
        maxPosColor = gl.glGetUniformLocation(shaderProgramID, "maxPosColor");
        colouring = gl.glGetUniformLocation(shaderProgramID, "colouring");
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
       
       MainForm f = renderer.getForm();
        
       lastAlpha = renderer.getForm().rtSlicePanel.getRTSliceAlpha();
       gl.glUniform1f(alphaValue, lastAlpha ); 
       gl.glUniform1f(minNegColor, f.rtSlicePanel.getGorkovNegColorMin());
       gl.glUniform1f(maxNegColor, f.rtSlicePanel.getGorkovNegColorMax());
       gl.glUniform1f(minPosColor, f.rtSlicePanel.getGorkovPosColorMin());
       gl.glUniform1f(maxPosColor, f.rtSlicePanel.getGorkovPosColorMax());
       gl.glUniform1i(colouring, f.rtSlicePanel.getColouringCombo());
       gl.glUniform1i(useAmp, f.rtSlicePanel.isForce1() ? 1 : 0);
    }
    
    
}
