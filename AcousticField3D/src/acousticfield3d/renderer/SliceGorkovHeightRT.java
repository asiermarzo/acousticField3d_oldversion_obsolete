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
public class SliceGorkovHeightRT extends ShaderTransducers{ 
    int colouring;
    int minNegColor, maxNegColor;
    int minPosColor, maxPosColor;
    int heightGain;
    int alphaValue;
    int heightDiv;
    
    int gSep;
    int kPreToVel;
    int kPre;
    int kVel;
    int pVol;
    
    private float lastAlpha = 1.0f;
   
    public SliceGorkovHeightRT(String vProgram, String fProgram) {
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
 
        gSep = gl.glGetUniformLocation(shaderProgramID, "gSep");
        kPreToVel = gl.glGetUniformLocation(shaderProgramID, "kPreToVel");
        kPre = gl.glGetUniformLocation(shaderProgramID, "kPre");
        kVel = gl.glGetUniformLocation(shaderProgramID, "kVel");
        pVol = gl.glGetUniformLocation(shaderProgramID, "pVol");
        heightDiv = gl.glGetUniformLocation(shaderProgramID, "heightDiv");
        
        heightGain = gl.glGetUniformLocation(shaderProgramID, "heightGain");
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
        String mesh = e.getMesh();
                
        if (mesh.equals( Resources.MESH_QUAD ) || 
                mesh.equals( Resources.MESH_CUSTOM ) || 
                mesh.equals(Resources.MESH_GRID)){
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
        
        gl.glUniform1f(gSep, renderer.vgSep );
        gl.glUniform1f(heightDiv, (float) renderer.getForm().rtSlicePanel.getHeightDivs() );
        gl.glUniform1f(kPreToVel, renderer.vkPreToVel ); 
        gl.glUniform1f(kPre, renderer.vkPre ); 
        gl.glUniform1f(kVel, renderer.vkVel ); 
        gl.glUniform1f(pVol, renderer.vpVol ); 
        
       lastAlpha = renderer.getForm().rtSlicePanel.getRTSliceAlpha();
       
       gl.glUniform1f(alphaValue, lastAlpha );
       gl.glUniform1f(heightGain, renderer.getForm().rtSlicePanel.getHeightGain() );
       gl.glUniform1f(minNegColor, renderer.getForm().rtSlicePanel.getGorkovNegColorMin());
       gl.glUniform1f(maxNegColor, renderer.getForm().rtSlicePanel.getGorkovNegColorMax());
       gl.glUniform1f(minPosColor, renderer.getForm().rtSlicePanel.getGorkovPosColorMin());
       gl.glUniform1f(maxPosColor, renderer.getForm().rtSlicePanel.getGorkovPosColorMax());
       gl.glUniform1i(colouring, renderer.getForm().rtSlicePanel.getColouringCombo());
    }


    @Override
    protected String preProcessVertex(String sourceCode, HashMap<String,String> templates) {
        return super.preProcessFragment(sourceCode, templates);
    }
    
    
}
