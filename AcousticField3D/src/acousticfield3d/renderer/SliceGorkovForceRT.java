/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.gui.MainForm;
import acousticfield3d.gui.panels.RtSlicePanel;
import acousticfield3d.math.Matrix4f;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Resources;
import acousticfield3d.scene.Scene;
import acousticfield3d.simulation.Simulation;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Asier
 */
public class SliceGorkovForceRT extends ShaderTransducers{ 
    int colouring;
    int xyzPlot;
    int minNegColor, maxNegColor;
    int minPosColor, maxPosColor;
    int alphaValue;
    
    int uk1s,uk2s;
    int gSize;
    int gSep;
    int kPreToVel;
    int kPre;
    int kVel;
    int pVol;
    
    private float lastAlpha = 1.0f;
   
    public SliceGorkovForceRT(String vProgram, String fProgram) {
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
 
        uk1s = gl.glGetUniformLocation(shaderProgramID, "uk1s");
        uk2s = gl.glGetUniformLocation(shaderProgramID, "uk2s");
        gSize = gl.glGetUniformLocation(shaderProgramID, "gSize");
        gSep = gl.glGetUniformLocation(shaderProgramID, "gSep");
        kPreToVel = gl.glGetUniformLocation(shaderProgramID, "kPreToVel");
        kPre = gl.glGetUniformLocation(shaderProgramID, "kPre");
        kVel = gl.glGetUniformLocation(shaderProgramID, "kVel");
        pVol = gl.glGetUniformLocation(shaderProgramID, "pVol");
        
        alphaValue = gl.glGetUniformLocation(shaderProgramID, "alphaValue");
        minNegColor = gl.glGetUniformLocation(shaderProgramID, "minNegColor");
        maxNegColor = gl.glGetUniformLocation(shaderProgramID, "maxNegColor");
        minPosColor = gl.glGetUniformLocation(shaderProgramID, "minPosColor");
        maxPosColor = gl.glGetUniformLocation(shaderProgramID, "maxPosColor");
        colouring = gl.glGetUniformLocation(shaderProgramID, "colouring");
        xyzPlot = gl.glGetUniformLocation(shaderProgramID, "xyzPlot");        
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
        final RtSlicePanel panel = renderer.getForm().rtSlicePanel;
        
        gl.glUniform1f(uk1s, panel.isForce1() ? 1.0f : 0.0f);
        gl.glUniform1f(uk2s, panel.isForce2() ? 1.0f : 0.0f);
        gl.glUniform1i(gSize, panel.getGradientSize() );
        gl.glUniform1f(gSep, renderer.vgSep );
        gl.glUniform1f(kPreToVel, renderer.vkPreToVel ); 
        gl.glUniform1f(kPre, renderer.vkPre ); 
        gl.glUniform1f(kVel, renderer.vkVel ); 
        gl.glUniform1f(pVol, renderer.vpVol );
        
       lastAlpha = panel.getRTSliceAlpha();
       gl.glUniform1f(alphaValue, lastAlpha ); 
       gl.glUniform1f(minNegColor, panel.getGorkovNegColorMin());
       gl.glUniform1f(maxNegColor, panel.getGorkovNegColorMax());
       gl.glUniform1f(minPosColor, panel.getGorkovPosColorMin());
       gl.glUniform1f(maxPosColor, panel.getGorkovPosColorMax());
       gl.glUniform1i(colouring, panel.getColouringCombo());
       gl.glUniform1i(xyzPlot, panel.getXYZPlot());
    }


    
}
