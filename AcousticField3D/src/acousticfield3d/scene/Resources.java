/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.scene;

import acousticfield3d.renderer.IsoRTGorkovShader;
import acousticfield3d.renderer.IsoRTShader;
import acousticfield3d.renderer.SliceRTShader;
import acousticfield3d.renderer.Shader;
import acousticfield3d.renderer.ShaderTransducers;
import acousticfield3d.renderer.SliceAmpHeightRT;
import acousticfield3d.renderer.SliceColorBar;
import acousticfield3d.renderer.SliceExHeightAmp;
import acousticfield3d.renderer.SliceExShader;
import acousticfield3d.renderer.SliceGorkovForceRT;
import acousticfield3d.renderer.SliceGorkovHeightRT;
import acousticfield3d.renderer.SliceGorkovRT;
import acousticfield3d.renderer.SlicePREShader;
import acousticfield3d.renderer.VolPreShader;
import acousticfield3d.renderer.VolRTShader;
import acousticfield3d.shapes.Box;
import acousticfield3d.shapes.Cylinder;
import acousticfield3d.shapes.Mesh;
import acousticfield3d.shapes.Quad;
import acousticfield3d.shapes.Sphere;
import acousticfield3d.shapes.Torus;
import acousticfield3d.simulation.FieldSource;
import acousticfield3d.utils.StringUtils;
import java.util.HashMap;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Asier
 */
public class Resources {
    public static final String TEMPLATE_SHADERS_ANAL_NODIR = "FieldCalcsAnalyticalNoDir.glsl";
    public static final String TEMPLATE_SHADERS_STENCILDIR = "FieldCalcsStencil.glsl";
    
    public static final int SHADER_SOLID = 1;
    public static final int SHADER_SOLID_SPEC = 2;
    public static final int SHADER_SOLID_DIFF = 3;
    
    public static final int SHADER_SLICE_PRE = 5;
    public static final int SHADER_VOL_PRE = 6;
    public static final int SHADER_VOL_RT = 7;
    public static final int SHADER_ISO_RT_AMP = 8;
    public static final int SHADER_MASK = 9;
    
    public static final int SHADER_SLICE_RT_AMP = 10;
    public static final int SHADER_SLICE_RT_PHASE = 11;
    public static final int SHADER_SLICE_RT_AMPPHASE = 12;
    public static final int SHADER_SLICE_RT_GORKOV = 13;
    public static final int SHADER_SLICE_RT_GORKOV_LAPLACIAN = 14;
    public static final int SHADER_SLICE_RT_GORKOV_HEIGHT = 16;
    public static final int SHADER_SLICE_RT_AMP_HEIGHT = 17;
    
    public static final int SHADER_ISO_RT_GORKOV = 18;
    public static final int SHADER_ISO_RT_PHASE = 19;
    public static final int SHADER_SLICE_RT_GORKOV_FORCE = 20;
    public static final int SHADER_SLICE_COLORBAR = 21;
    public static final int SHADER_SLICE_EXTERNAL_AMP = 22;
    public static final int SHADER_SLICE_EXTERNAL_PHASE = 23;
    
    public static final int SHADER_ISO_RT_TAMP = 24;
    public static final int SHADER_SLICE_RT_TAMP = 25;
    public static final int SHADER_HEIGHT_EX_AMP = 26;
    public static final int SHADER_SLICE_RT_TAMPDIFFFR = 27;
    
    public static final String MESH_CUSTOM = "custom";
    public static final String MESH_QUAD = "quad";
    public static final String MESH_QUADDS = "quadDSide";
    public static final String MESH_BOX = "box";
    public static final String MESH_SPHERE = "sphere";
    public static final String MESH_DONUT = "donut";
    public static final String MESH_CYLINDER = "cylinder";
    public static final String MESH_TRANSDUCER = "transducer";
    public static final String MESH_GRID = "grid";
    public static final int MESH_GRID_DIVS = 128;
    
    private static Resources _instance;
    public static Resources get(){
        return _instance;
    }
    
    public static void init(GL2 gl, boolean useStencil){
        if (_instance != null){
            _instance.releaseResources(gl);
            _instance = null;
        }
        _instance = new Resources(gl, useStencil);
        
    }
     
    private final HashMap<Integer, Shader> shaders;
    private final HashMap<String, Mesh> meshes;
    private final HashMap<String, String> templates;
    
   
    private Resources(GL2 gl, boolean useStencil){
        shaders = new HashMap<>();
        meshes = new HashMap<>();
        templates = new HashMap<>();
        
        initTemplates(useStencil ? TEMPLATE_SHADERS_STENCILDIR : TEMPLATE_SHADERS_ANAL_NODIR);
        initResources(gl);
    }

    public static final String BEGIN_TAG = "//BEGIN";
    public static final String END_TAG = "//END";
    public static final String TEMPLATE_TAG = "//TEMPLATE";
    public static final String INCLUDE_TAG = "//INCLUDE";
    
    public void initTemplates(String file){
        templates.clear();
        String content = Shader.getSourceCode( file );
        int si = 0;
        while ( (si = content.indexOf(BEGIN_TAG, si)) != -1){
            String templateName = StringUtils.getBetween(content, BEGIN_TAG, "\n", si).trim();
            
            int endPosition = content.indexOf(END_TAG, si+1);
            String templateContent = content.substring(si, endPosition);
            templates.put(templateName, templateContent);
            si = endPosition + 1;
        }    
    }
    
    private void initResources(GL2 gl) {
        //load shaders
        shaders.put(SHADER_SOLID, new Shader("ColorPlain.vsh", "ColorPlain.fsh", Shader.ORDER_OPAQUE).init(gl, templates));
        shaders.put(SHADER_SOLID_DIFF, new Shader("ColorDiff.vsh", "ColorDiff.fsh", Shader.ORDER_OPAQUE).init(gl, templates));
        shaders.put(SHADER_SOLID_SPEC, new Shader("ColorSpec.vsh", "ColorSpec.fsh", Shader.ORDER_OPAQUE).init(gl, templates));
        shaders.put(SHADER_MASK, new Shader("MatteMask.vsh", "MatteMask.fsh", Shader.ORDER_MASK).init(gl, templates));
        
        shaders.put(SHADER_SLICE_RT_AMP, new SliceRTShader("SliceRT_V.glsl", "SliceRT_F.glsl", FieldSource.sourceAmp).init(gl, templates));
        shaders.put(SHADER_SLICE_RT_TAMP, new SliceRTShader("SliceRT_V.glsl", "SliceRT_F.glsl", FieldSource.sourceTamp).init(gl, templates));
        shaders.put(SHADER_SLICE_RT_TAMPDIFFFR, new SliceRTShader("SliceRT_V.glsl", "SliceRT_F.glsl", FieldSource.sourceTAmpDiffFr).init(gl, templates));
        shaders.put(SHADER_SLICE_RT_PHASE, new SliceRTShader("SliceRT_V.glsl", "SliceRT_F.glsl", FieldSource.sourcePhase).init(gl, templates));
        shaders.put(SHADER_SLICE_RT_AMPPHASE, new SliceRTShader("SliceRT_V.glsl", "SliceRT_F.glsl", FieldSource.sourceAmpPhase).init(gl, templates));
        shaders.put(SHADER_SLICE_RT_GORKOV, new SliceGorkovRT("SliceGorkov_V.glsl", "SliceGorkov_F.glsl").init(gl, templates));
        shaders.put(SHADER_SLICE_RT_GORKOV_FORCE, new SliceGorkovForceRT("SliceGorkovForce_V.glsl", "SliceGorkovForce_F.glsl").init(gl, templates));
        shaders.put(SHADER_SLICE_RT_GORKOV_LAPLACIAN, new SliceGorkovRT("SliceGorkovLaplacian_V.glsl", "SliceGorkovLaplacian_F.glsl").init(gl, templates));
        shaders.put(SHADER_SLICE_RT_GORKOV_HEIGHT, new SliceGorkovHeightRT("SliceGorkovHeight_V.glsl", "SliceHeight_F.glsl").init(gl, templates));
        shaders.put(SHADER_SLICE_RT_AMP_HEIGHT, new SliceAmpHeightRT("SliceAmpHeight_V.glsl", "SliceHeight_F.glsl").init(gl, templates));
        shaders.put(SHADER_HEIGHT_EX_AMP, new SliceExHeightAmp("AmpExtHeight_V.glsl", "SliceHeight_F.glsl").init(gl, templates));
        
        shaders.put(SHADER_ISO_RT_GORKOV, new IsoRTGorkovShader("IsoGorkov_V.glsl", "IsoGorkov_F.glsl").init(gl, templates));

        shaders.put(SHADER_SLICE_PRE, new SlicePREShader("SlicePre_V.glsl", "SlicePre_F.glsl").init(gl, templates));
        shaders.put(SHADER_VOL_PRE, new VolPreShader("VolPre_V.glsl", "VolPre_F.glsl").init(gl, templates));
        shaders.put(SHADER_VOL_RT, new VolRTShader("VolRT_V.glsl", "VolRT_F.glsl").init(gl, templates));
        shaders.put(SHADER_ISO_RT_AMP, new IsoRTShader("IsoRT_V.glsl", "IsoRT_F.glsl").init(gl, templates));
        shaders.put(SHADER_ISO_RT_PHASE, new IsoRTShader("IsoRT_V.glsl", "IsoRTPhase_F.glsl").init(gl, templates));
        
        shaders.put(SHADER_ISO_RT_TAMP, new IsoRTShader("IsoRT_V.glsl", "IsoRTTamp_F.glsl").init(gl, templates));
      
        shaders.put(SHADER_SLICE_COLORBAR, new SliceColorBar("SliceRT_V.glsl", "SliceColorbar.glsl").init(gl, templates));
        shaders.put(SHADER_SLICE_EXTERNAL_AMP, new SliceExShader("SliceRT_V.glsl", "SliceExternal.glsl", FieldSource.sourceAmp).init(gl, templates));
        shaders.put(SHADER_SLICE_EXTERNAL_PHASE, new SliceExShader("SliceRT_V.glsl", "SliceExternal.glsl", FieldSource.sourcePhase).init(gl, templates));
        
        
        //load meshes
        meshes.put(MESH_BOX, new Box(0.5f, 0.5f, 0.5f) );
        meshes.put(MESH_SPHERE, new Sphere(8, 8, 0.5f) );
        meshes.put(MESH_DONUT, new Torus(10, 10, 0.2f, 0.5f) );
        meshes.put(MESH_CYLINDER, new Cylinder(4, 16, 0.5f, 1, true, false) );
        meshes.put(MESH_TRANSDUCER, new Cylinder(4, 16, 0.3f, 0.5f, 1,true, false, -0.5f) );
        meshes.put(MESH_QUAD, new Quad(1, 1, 1, false) );
        meshes.put(MESH_GRID, new Quad(1, 1, MESH_GRID_DIVS, false) );
        meshes.put(MESH_QUADDS, new Quad(1, 1, 1, true) );
        
        
        //load textures
    }
    
    
    public void updateShaderTransducers(int n, GL2 gl){
        for(int i : shaders.keySet()){
            Shader s = shaders.get(i);
            if(s instanceof ShaderTransducers){
                ShaderTransducers st = (ShaderTransducers)s;
                st.updateTransducersNumber(n, gl, templates);
            }
        }
    }
    
    public void reloadShaders(GL2 gl){
       for(int i : shaders.keySet()){
            Shader s = shaders.get(i);
            s.reload(gl, templates);
        } 
    }
    
    public Shader getShader(int s){
        return shaders.get(s);
    }
    
    public Mesh getMesh(String m){
        return meshes.get(m);
    }
    
    public void releaseResources(GL2 gl){
        //delete shaders
        for(int s : shaders.keySet()){
            shaders.get(s).unloadShader(gl);
        }
        
        //delete textures
    }
}
