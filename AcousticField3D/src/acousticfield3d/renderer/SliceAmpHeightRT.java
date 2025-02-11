/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.gui.MainForm;
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
public class SliceAmpHeightRT extends ShaderTransducers{ 
    int colouring;
    int minPosColor, maxPosColor;
    int heightGain;
    int alphaValue;
    int heightDiv;
    
    private float lastAlpha = 1.0f;
   
    public SliceAmpHeightRT(String vProgram, String fProgram) {
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
 
        heightDiv = gl.glGetUniformLocation(shaderProgramID, "heightDiv");
        
        heightGain = gl.glGetUniformLocation(shaderProgramID, "heightGain");
        alphaValue = gl.glGetUniformLocation(shaderProgramID, "alphaValue");
      
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
        
       gl.glUniform1f(heightDiv, (float) f.rtSlicePanel.getHeightDivs() );
     
        
       lastAlpha = f.rtSlicePanel.getRTSliceAlpha();
       
       gl.glUniform1f(alphaValue, lastAlpha );
       gl.glUniform1f(heightGain, f.rtSlicePanel.getHeightGain() );
       
       gl.glUniform1f(minPosColor, f.rtSlicePanel.getAmpColorMin());
       gl.glUniform1f(maxPosColor, f.rtSlicePanel.getAmpColorMax());
       gl.glUniform1i(colouring, f.rtSlicePanel.getColouringCombo());
    }

    //notice the change from vertex shader to fragment, that is because HeightMaps calc the field in the vertex shader
    @Override
    protected String preProcessVertex(String sourceCode, HashMap<String,String> templates) {
        return super.preProcessFragment(sourceCode, templates);
    }
    
    
}
