/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.renderer;

import acousticfield3d.gui.controls.Gradients;
import acousticfield3d.gui.panels.PreIsoPanel;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Resources;
import acousticfield3d.shapes.Mesh;
import acousticfield3d.simulation.IsoSurface;
import acousticfield3d.simulation.PreCubeCalculator;
import acousticfield3d.simulation.Simulation;
import java.awt.event.ActionListener;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asier
 */
public class MarchingCubes extends Thread{
    public ActionListener listener;
    
    private final int cores;
    
    private final PreIsoPanel form;
    private final ExecutorService executor;
    private final ArrayList<Future> pendingTasks;
    private final ArrayList<Mesh> meshes;
    
    final Vector3f minSimBound = new Vector3f(), maxSimBound = new Vector3f();
    FloatBuffer data;
    int dataSize;
    int step;
    float isoValue;
    
    private float complete;
    
    public MarchingCubes(PreIsoPanel form, float isoValue, ActionListener listener) {
        this.form = form;
        this.isoValue = isoValue;
        
        cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cores);
       
        pendingTasks = new ArrayList<>();
        meshes = new ArrayList<>();
        this.listener = listener;
    }
    
    @Override
    public void run() {
        multiThreadCalc();
                
        executor.shutdownNow();
        
        if(listener != null){
            listener.actionPerformed(null);
        }
    }

    public void multiThreadCalc(){
        initCalc();
        
        submitTasks();
       
        waitForTasks();
        
        IsoSurface iSurface = new IsoSurface();
        iSurface.setIsoValue( isoValue );
        for(Mesh m : meshes){
            MeshEntity me = new MeshEntity(Resources.MESH_CUSTOM, null, Resources.SHADER_SOLID_SPEC);
            me.customMesh = m;
            me.setDoubledSided( true );
            if (form.mf.preCubePanel.isAmpSelected()){
                float tValue = FastMath.clamp( isoValue / form.mf.rtSlicePanel.getAmpColorMax(), 0.0f, 1.0f);
                me.setColor( Gradients.get().getGradientAmp(tValue) );
            }else{
                float pV = (isoValue % FastMath.TWO_PI) / FastMath.TWO_PI;
                if(pV < 0.0f) { pV += 1; }
                me.setColor( Gradients.get().getGradientPhase(pV) );
            }
            me.getMaterial().ambient = 0.6f;
            me.getMaterial().diffuse = 0.5f;
            me.getMaterial().specular = 0.3f;
            me.getMaterial().shininess = 10;
            iSurface.getMeshes().add(me);
            form.mf.getScene().getEntities().add( me );
        }
        form.addIsoSurface( iSurface );
        
        meshes.clear();
    }
    
     private void submitTasks() {
        pendingTasks.clear();
   
         //range distribution
        int zPixelPerThread = dataSize / step / cores;
        
        final boolean q1 = form.isQ1();
        final boolean q2 = form.isQ2();
        final boolean q3 = form.isQ3();
        final boolean q4 = form.isQ4();
        
        int zStart = 2;
        for(int i = 1; i <= cores; ++i){
            int zEnd = zStart + step * zPixelPerThread ;
            if (i == cores) { zEnd -= step+3; }
            
            final int dataSizeLStep = dataSize - step - 1;
            int startX = 2, endX = dataSizeLStep;
            if (zStart < (dataSize/2)- step){
                if (!q1) { startX = dataSize/2 + 2; }
                if (!q4) { endX = dataSize/2 + 2; }
            }else{
                if (!q2) { startX = dataSize/2 + 2; }
                if (!q3) { endX = dataSize/2 + 2; }
            }
            
            TaskCalc tc = new TaskCalc(zStart, zEnd, startX, endX, this);
            
            pendingTasks.add(executor.submit(tc) );
            
            zStart = zEnd;
        }
        
    }

    private void waitForTasks() {
        //wait for the futures to finish
        for(Future f : pendingTasks){
            try {
                f.get();
            } catch (InterruptedException ex) {
                Logger.getLogger(PreCubeCalculator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(PreCubeCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        pendingTasks.clear();
    }
    
    protected void addMesh(Mesh mesh){
        mesh.rewindBuffers();
        synchronized(meshes){
            meshes.add( mesh );
        }
    }

    protected synchronized void updateProgress(float percentage){
        complete += percentage;
        int p = (int)(complete / cores * 100.0f);
        form.updateIsoProgress(p);
    }
    
      private void initCalc() {
        meshes.clear();
        complete = 0;
        updateProgress(0);
                
        Simulation s = form.mf.getSimulation();
        Vector3f cubeCenter = form.mf.getScene().getPreCubeVol().getTransform().getTranslation();
        Vector3f cubeSize = form.mf.getScene().getPreCubeVol().getTransform().getScale().divide(2.0f);
        minSimBound.set( cubeCenter.subtract( cubeSize ) );
        maxSimBound.set( cubeCenter.add( cubeSize ) );
         
        data = form.mf.getRenderer().getPreCube();
        
        dataSize = form.mf.preCubePanel.getPreCubeSize();
        step = form.getIsoStep();
    }
      
    public static void interp(Vector3f pos, float iso, Vector3f a, Vector3f b, float aVal, float bVal) {
        final float p = (iso - aVal) / (bVal - aVal);
        pos.x = a.x + p * (b.x - a.x);
        pos.y = a.y + p * (b.y - a.y);
        pos.z = a.z + p * (b.z - a.z);
    }
    
    public class TaskCalc implements Runnable{
        final int startZ, endZ;
        final int startX, endX;
        final MarchingCubes w;
        final FloatBuffer data;
        final Vector3f minSim;
        final Vector3f maxSim;

        final int dataSize;
        final int dataSizeS;
        final int step;

        final float stepX,stepY,stepZ;
        final int dataSizeLStep;

        final int iStepX,iStepY,iStepZ;
        final float progressPerZ;

        final float isoValue;

        public TaskCalc(int startZ, int endZ, int startX, int endX, MarchingCubes w) {
            this.startZ = startZ;
            this.endZ = endZ;
            this.startX = startX;
            this.endX = endX;
            this.w = w;

            data = w.data;
            minSim = w.minSimBound;
            maxSim = w.maxSimBound;

            dataSize = w.dataSize;
            dataSizeS = dataSize * dataSize;
            step = w.step;

            stepX = (maxSim.x - minSim.x) / dataSize;
            stepY = (maxSim.y - minSim.y) / dataSize;
            stepZ = (maxSim.z - minSim.z) / dataSize;
            dataSizeLStep = dataSize - step - 1;

            iStepX = step;
            iStepY = step * dataSize;
            iStepZ = step * dataSizeS;
            progressPerZ = 1.0f / ((endZ - startZ) / step);

            isoValue = w.isoValue;
        }

        private Mesh createMesh() {
            final int maxShortRange = (256 * 256 / 2) - 2;
            //in marching cubes the proportion of vertices to tris is about 12/5 (to check)
            return new Mesh(maxShortRange, maxShortRange / 12 * 5);
        }
    
        @Override
        public void run() {  
            final float[] val = new float[8];
            final Vector3f[] pos = new Vector3f[8];
            for(int i = 0; i < 8; ++i){
                pos[i] = new Vector3f();
            }
            final Vector3f tPos = new Vector3f();
            final Vector3f tNor = new Vector3f();
            
            Mesh m = createMesh();
            FloatBuffer mPos = m.getPosition();
            FloatBuffer mNor = m.getNormal();
            FloatBuffer mTex = m.getTexture();
            ShortBuffer mInd = m.getIndices();
            int maxVert = mPos.capacity() / 3;
            int maxInd = mInd.capacity();
            short numVert = 0; int numInd = 0;
            short[] vIndex = new short[12];
            
            for(int iz = startZ; iz < endZ; iz += step){
                for(int iy = 2; iy < dataSizeLStep; iy += step){
                    for(int ix = startX; ix < endX; ix += step){
                        int pixel = ix + iy * dataSize + iz * dataSizeS;
                        float x = minSim.x + ix * stepX;
                        float y = minSim.y + iy * stepY;
                        float z = minSim.z + iz * stepZ;
                        
                        if (numVert >= maxVert - 12 || numInd >= maxInd - 5*3){ //mesh is full, create a new one
                            m.setVerticesAndTris(numVert, numInd/3);
                            w.addMesh( m ); //add it to the collection of meshes
                            m = createMesh(); //get a new one
                            mPos = m.getPosition();
                            mNor = m.getNormal();
                            mTex = m.getTexture();
                            mInd = m.getIndices();
                            maxVert = mPos.capacity() / 3;
                            maxInd = mInd.capacity();
                            numVert = 0; numInd = 0;
                        }
                        
                        val[0] = MarchingCubes.this.data.get(pixel); //0 0 0
                        pos[0].set(x, y, z);
                        val[1] = MarchingCubes.this.data.get(pixel + iStepX); //+ 0 0
                        pos[1].set(x + stepX, y, z);
                        val[2] = MarchingCubes.this.data.get(pixel + iStepX + iStepY); //+ + 0
                        pos[2].set(x + stepX, y + stepY, z);
                        val[3] = MarchingCubes.this.data.get(pixel + iStepY); //0 + 0
                        pos[3].set(x, y + stepY, z);
                        val[4] = MarchingCubes.this.data.get(pixel + iStepZ); //0 0 +
                        pos[4].set(x, y, z + stepZ);
                        val[5] = MarchingCubes.this.data.get(pixel + iStepX + iStepZ); //+ 0 +
                        pos[5].set(x + stepX, y, z + stepZ);
                        val[6] = MarchingCubes.this.data.get(pixel + iStepX + iStepY + iStepZ); //+ + +
                        pos[6].set(x + stepX, y + stepY, z + stepZ);
                        val[7] = MarchingCubes.this.data.get(pixel + iStepY + iStepZ); //0 + +
                        pos[7].set(x, y + stepY, z + stepZ);
                        
                        int cubeindex = 0;
                        if (val[0] < isoValue) { cubeindex |= 1;}
                        if (val[1] < isoValue) { cubeindex |= 2; }
                        if (val[2] < isoValue) { cubeindex |= 4;}
                        if (val[3] < isoValue) { cubeindex |= 8;}
                        if (val[4] < isoValue) { cubeindex |= 16;}
                        if (val[5] < isoValue) { cubeindex |= 32;}
                        if (val[6] < isoValue) { cubeindex |= 64;}
                        if (val[7] < isoValue) { cubeindex |= 128;}

                        /* Cube is entirely in/out of the surface */
                        if (cubeindex == 0 || cubeindex == 255) {
                            continue;
                        }

                        /* Find the vertices where the surface intersects the cube */
                        final int verticesBitField = edgeTable[cubeindex];
                        if ((verticesBitField & 1) != 0) {
                            vIndex[0] = numVert++;
                            interp(tPos, isoValue, pos[0], pos[1], val[0], val[1]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 2) != 0) {
                            vIndex[1] = numVert++;
                            interp(tPos, isoValue, pos[1], pos[2], val[1], val[2]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 4) != 0) {
                            vIndex[2] = numVert++;
                            interp(tPos, isoValue, pos[2], pos[3], val[2], val[3]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 8) != 0) {
                            vIndex[3] = numVert++;
                            interp(tPos, isoValue, pos[3], pos[0], val[3], val[0]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 16) != 0) {
                            vIndex[4] = numVert++;
                            interp(tPos, isoValue, pos[4], pos[5], val[4], val[5]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 32) != 0) {
                            vIndex[5] = numVert++;
                            interp(tPos, isoValue, pos[5], pos[6], val[5], val[6]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 64) != 0) {
                            vIndex[6] = numVert++;
                            interp(tPos, isoValue, pos[6], pos[7], val[6], val[7]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 128) != 0) {
                            vIndex[7] = numVert++;
                            interp(tPos, isoValue, pos[7], pos[4], val[7], val[4]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 256) != 0) {
                            vIndex[8] = numVert++;
                            interp(tPos, isoValue, pos[0], pos[4], val[0], val[4]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 512) != 0) {
                            vIndex[9] = numVert++;
                            interp(tPos, isoValue, pos[1], pos[5], val[1], val[5]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 1024) != 0) {
                            vIndex[10] = numVert++;
                            interp(tPos, isoValue, pos[2], pos[6], val[2], val[6]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }
                        if ((verticesBitField & 2048) != 0) {
                            vIndex[11] = numVert++;
                            interp(tPos, isoValue, pos[3], pos[7], val[3], val[7]);
                            calcNormal(tNor, tPos);
                            mPos.put( tPos.x ); mPos.put( tPos.y ); mPos.put( tPos.z );
                            mTex.put(0.0f); mTex.put( 0.0f );
                            mNor.put( tNor.x ); mNor.put( tNor.y ); mNor.put( tNor.z );
                        }

                        /* Create the triangles */
                        final int[] tris = triTable[cubeindex];
                        for (int i = 0; tris[i] != -1; i += 3) {
                            mInd.put( vIndex[tris[i]] );
                            mInd.put( vIndex[tris[i + 1]] );
                            mInd.put( vIndex[tris[i + 2]] );
                            numInd += 3;
                        }

                    }
                }
                w.updateProgress(progressPerZ); 
            }
            
            
            //add the current mesh to the collection
            addMesh( m );
           
        } //end of run

        private void calcNormal(Vector3f tNor, Vector3f tPos) {
            //word coords to cube
            int cx = (int)((tPos.x - minSim.x) / stepX);
            int cy = (int)((tPos.y - minSim.y) / stepY);
            int cz = (int)((tPos.z - minSim.z) / stepZ);
            
            //cube to pixel
            int pixel = cx + cy * dataSize + cz * dataSizeS;
            tNor.x = data.get( pixel - iStepX ) - data.get( pixel + iStepX );
            tNor.y = data.get( pixel - iStepY ) - data.get( pixel + iStepY );
            /*
            if (pixel+iStepZ >= data.capacity() || pixel - iStepZ < 0){
                int breakDebug = 1;
            }
            */
            tNor.z = data.get( pixel - iStepZ ) - data.get( pixel + iStepZ );
            tNor.normalizeLocal();
        }
        
    }
   
    
    
    
    public static final int[] edgeTable={ //256
	0x0  , 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c,
	0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00,
	0x190, 0x99 , 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
	0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90,
	0x230, 0x339, 0x33 , 0x13a, 0x636, 0x73f, 0x435, 0x53c,
	0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30,
	0x3a0, 0x2a9, 0x1a3, 0xaa , 0x7a6, 0x6af, 0x5a5, 0x4ac,
	0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0,
	0x460, 0x569, 0x663, 0x76a, 0x66 , 0x16f, 0x265, 0x36c,
	0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60,
	0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff , 0x3f5, 0x2fc,
	0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0,
	0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55 , 0x15c,
	0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950,
	0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc ,
	0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0,
	0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc,
	0xcc , 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0,
	0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c,
	0x15c, 0x55 , 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650,
	0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc,
	0x2fc, 0x3f5, 0xff , 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0,
	0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c,
	0x36c, 0x265, 0x16f, 0x66 , 0x76a, 0x663, 0x569, 0x460,
	0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac,
	0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa , 0x1a3, 0x2a9, 0x3a0,
	0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c,
	0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33 , 0x339, 0x230,
	0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c,
	0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99 , 0x190,
	0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c,
	0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x0   };

    public static final int[][] triTable = //256x16
	{{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1},
	{3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1},
	{3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
	{3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1},
	{9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1},
	{9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
	{2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1},
	{8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1},
	{9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
	{4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1},
	{3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1},
	{1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1},
	{4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1},
	{4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
	{9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
	{5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1},
	{2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1},
	{9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
	{0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
	{2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1},
	{10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1},
	{4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1},
	{5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1},
	{5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1},
	{9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1},
	{0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1},
	{1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1},
	{10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1},
	{8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1},
	{2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1},
	{7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1},
	{9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1},
	{2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1},
	{11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1},
	{9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1},
	{5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1},
	{11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1},
	{11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
	{1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1},
	{9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1},
	{5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1},
	{2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
	{0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
	{5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1},
	{6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1},
	{3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1},
	{6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1},
	{5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1},
	{1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
	{10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1},
	{6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1},
	{8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1},
	{7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1},
	{3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
	{5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1},
	{0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1},
	{9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1},
	{8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1},
	{5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1},
	{0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1},
	{6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1},
	{10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1},
	{10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1},
	{8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1},
	{1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1},
	{3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1},
	{0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1},
	{10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1},
	{3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1},
	{6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1},
	{9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1},
	{8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1},
	{3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1},
	{6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1},
	{0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1},
	{10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1},
	{10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1},
	{2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1},
	{7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1},
	{7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1},
	{2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1},
	{1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1},
	{11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1},
	{8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1},
	{0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1},
	{7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
	{10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
	{2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
	{6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1},
	{7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1},
	{2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1},
	{1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1},
	{10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1},
	{10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1},
	{0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1},
	{7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1},
	{6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1},
	{8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1},
	{9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1},
	{6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1},
	{4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1},
	{10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1},
	{8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1},
	{0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1},
	{1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1},
	{8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1},
	{10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1},
	{4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1},
	{10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
	{5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
	{11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1},
	{9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
	{6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1},
	{7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1},
	{3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1},
	{7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
	{9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1},
	{3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1},
	{6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1},
	{9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1},
	{1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1},
	{4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1},
	{7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1},
	{6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1},
	{3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1},
	{0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1},
	{6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1},
	{0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1},
	{11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1},
	{6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1},
	{5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1},
	{9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1},
	{1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1},
	{1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1},
	{10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1},
	{0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1},
	{5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1},
	{10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1},
	{11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1},
	{9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1},
	{7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1},
	{2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1},
	{8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1},
	{9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1},
	{9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1},
	{1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1},
	{9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1},
	{9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1},
	{5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1},
	{0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1},
	{10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1},
	{2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1},
	{0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1},
	{0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1},
	{9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1},
	{5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1},
	{3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1},
	{5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1},
	{8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1},
	{0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1},
	{9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1},
	{0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1},
	{1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1},
	{3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1},
	{4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1},
	{9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1},
	{11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1},
	{11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1},
	{2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1},
	{9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1},
	{3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1},
	{1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1},
	{4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1},
	{4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1},
	{0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1},
	{3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1},
	{3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1},
	{0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1},
	{9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1},
	{1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
	{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};
}
