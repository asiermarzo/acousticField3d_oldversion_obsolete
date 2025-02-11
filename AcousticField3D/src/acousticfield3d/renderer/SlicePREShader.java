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
import acousticfield3d.simulation.Simulation;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Asier
 */
public class SlicePREShader extends Shader{    
    int minColor, maxColor;
    int preCube;
    int centerCube, sizeCube;
    
    public SlicePREShader(String vProgram, String fProgram) {
        super(vProgram, fProgram, ORDER_OPAQUE);
      
    }
        

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
        
        minColor = gl.glGetUniformLocation(shaderProgramID, "minColor");
        maxColor = gl.glGetUniformLocation(shaderProgramID, "maxColor");
        preCube = gl.glGetUniformLocation(shaderProgramID, "preCube");
        centerCube = gl.glGetUniformLocation(shaderProgramID, "cubeCenter");
        sizeCube = gl.glGetUniformLocation(shaderProgramID, "cubeSize");
    }
    
    

    @Override
    void changeGLStatus(GL2 gl, Renderer renderer, Simulation s, MeshEntity e) {
        renderer.enableBlend(gl, false);
        renderer.enableCullFace(gl, ! e.getMesh().equals( Resources.MESH_QUAD ));
        renderer.enableDepthTest(gl, true);
        renderer.enableTexture2D(gl, false);
        renderer.enableTexture3D(gl, true);
        
        gl.glBindTexture(GL2.GL_TEXTURE_3D, renderer.preCubeId);
    }

    @Override
    void bindUniforms(GL2 gl, Scene scene, Renderer renderer,Simulation s, MeshEntity me, Matrix4f projectionViewModel, Matrix4f viewModel, Matrix4f model, FloatBuffer fb) {
       super.bindUniforms(gl, scene, renderer, s, me, projectionViewModel, viewModel, model, fb);
       
       gl.glUniform1f(minColor, renderer.getForm().rtSlicePanel.getAmpColorMin());
       gl.glUniform1f(maxColor, renderer.getForm().rtSlicePanel.getAmpColorMax()); 
       
       gl.glUniform3f(centerCube, 
               (renderer.preCubeMax.x + renderer.preCubeMin.x) / 2.0f, 
               (renderer.preCubeMax.y + renderer.preCubeMin.y) / 2.0f,
               (renderer.preCubeMax.z + renderer.preCubeMin.z) / 2.0f);
       
       gl.glUniform3f(sizeCube, 
               (renderer.preCubeMax.x - renderer.preCubeMin.x) , 
               (renderer.preCubeMax.y - renderer.preCubeMin.y) ,
               (renderer.preCubeMax.z - renderer.preCubeMin.z));
       
       gl.glUniform1i(preCube, 0);
       
    }
    
    
}
