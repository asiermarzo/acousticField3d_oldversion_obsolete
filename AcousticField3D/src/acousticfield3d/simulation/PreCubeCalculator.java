/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.simulation;

import acousticfield3d.gui.panels.PreCubePanel;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Vector3f;
import acousticfield3d.renderer.Renderer;
import acousticfield3d.utils.BufferUtils;
import java.awt.event.ActionListener;
import java.nio.FloatBuffer;
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
public class PreCubeCalculator extends Thread{
    public ActionListener listener;
    
    private final int cores;
    
    final PreCubePanel form;
    private final ExecutorService executor;
    private final ArrayList<Future> pendingTasks;
    private boolean needsUpdate;
    private boolean finish;
    
    final Vector3f minSimBound = new Vector3f(), maxSimBound = new Vector3f();
    FloatBuffer data;
    int dataSize;
    int nTrans;
    float[] transPosition;
    float[] transNormal;
    float[] transSpec;
    
    FieldSource source;
    
    private float complete;
    
    public PreCubeCalculator(PreCubePanel form) {
        this.form = form;
        cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cores);
       
        pendingTasks = new ArrayList<>();
        needsUpdate = false;
    }

    
    public synchronized void update(){
        needsUpdate = true;
        notify();
    }
    
    public synchronized void updateAndWait(){
        needsUpdate = true;
        finish = false;
        notify();
        if(!finish){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(PreCubeCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void run() {
        while(!interrupted()){
            if (needsUpdate){
                needsUpdate = false;
                
                initCalc();
                
                multiThreadCalc();
                
                final Renderer renderer = form.mf.getRenderer();
                renderer.updatePreCalcCube(minSimBound, maxSimBound, data, dataSize);
                
                if (listener != null){
                    listener.actionPerformed(null);
                }
            }
            finish = true;
            synchronized (this) {
                notifyAll();
            }
            if (!needsUpdate){
                synchronized(this){ //wait till new notifications
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
        
        executor.shutdownNow();
    }

    private void submitTasks() {
        pendingTasks.clear();
   
         //range distribution
        int nPixels = dataSize * dataSize * dataSize;
        int pixelsPerThread = nPixels / cores;
        
        int pixelStart = 0;
        for(int i = 1; i <= cores; ++i){
            int pixelEnd = pixelStart + pixelsPerThread + ((i == cores) ? nPixels % cores : 0);
          
            TaskCalc tc = new TaskCalc(pixelStart, pixelEnd,this);
            
            pendingTasks.add(executor.submit(tc) );
            
            pixelStart = pixelEnd;
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
    
    public synchronized void check(){
        notify();
    }
    
    public synchronized void finish(){
        interrupt();
    }


    public void multiThreadCalc(){
        submitTasks();
       
        waitForTasks();
        
        //if gorkov is required -> 
            //do the second pass
            //wait
        
    }

    private void initCalc() {    
        complete = 0;
        updateProgress(0);
        
        source = form.getPreCubeSource();
        
        Simulation s = form.mf.getSimulation();
        Vector3f cubeCenter = form.mf.getScene().getPreCubeVol().getTransform().getTranslation();
        Vector3f cubeSize = form.mf.getScene().getPreCubeVol().getTransform().getScale().divide(2.0f);
        minSimBound.set( cubeCenter.subtract( cubeSize ) );
        maxSimBound.set( cubeCenter.add( cubeSize ) );
         
        dataSize = form.getPreCubeSize();
        nTrans = s.getTransducers().size();
        final int dataSize3 = dataSize*dataSize*dataSize;
        
        if (transPosition == null || transPosition.length != nTrans * 3){
            transPosition = new float[nTrans * 3];
        }
        if (transNormal == null || transNormal.length != nTrans * 3){
            transNormal = new float[nTrans * 3];
        }
        if (transSpec == null || transSpec.length != nTrans * 4){
            transSpec = new float[nTrans * 4];
        }
       
        if (data == null || data.capacity() != dataSize3){
            if (data != null){ BufferUtils.destroyDirectBuffer( data ); }
            data = BufferUtils.createFloatBuffer( dataSize3 );
        }
        
        Vector3f tn = new Vector3f();
        float temperature = form.mf.simPanel.getTemperature();
        float airSpeed = Simulation.getSoundSpeedInAir(temperature);  // m/s, sound speed in air
        
        final boolean discAmp = form.mf.miscPanel.isAmpDiscretizer();
        final boolean discPhase = form.mf.miscPanel.isPhaseDiscretizer();
        final float ampDiscStep = 1.0f / form.mf.miscPanel.getAmpDiscretization();
        final float phaseDiscStep = 1.0f / form.mf.miscPanel.getPhaseDiscretization();
        final float transPower = form.mf.simulation.getTransPower();
        
        int it = 0, iq = 0;
        for(Transducer t : s.getTransducers()){
            transPosition[it+0] = t.getTransform().getTranslation().x;
            transPosition[it+1] = t.getTransform().getTranslation().y;
            transPosition[it+2] = t.getTransform().getTranslation().z;
            
            t.getTransform().getRotation().mult( Vector3f.UNIT_Y, tn);
            transNormal[it+0] = tn.x;
            transNormal[it+1] = tn.y;
            transNormal[it+2] = tn.z;
            
            float omega = 2.0f * FastMath.PI * t.getFrequency();      // angular frequency
            float k = omega / airSpeed;        // wavenumber
            transSpec[iq+0] = k; // k
            transSpec[iq+1] = t.calcRealDiscAmplitude(discAmp, ampDiscStep, transPower); // amp
            transSpec[iq+2] = t.calcRealDiscPhase(discPhase, phaseDiscStep); // phase
            transSpec[iq+3] = t.getWidth(); // width
            
            it += 3;
            iq += 4;
        }
    }
    
    protected synchronized void updateProgress(float percentage){
        complete += percentage;
        int p = (int)(complete / cores * 100.0f);
        form.updatePreCalcProgress(p);
    }
    
    public class TaskCalc implements Runnable{
        final int startPixel, endPixel;
        final PreCubeCalculator w;

        public TaskCalc(int startPixel, int endPixel, PreCubeCalculator w) {
            this.startPixel = startPixel;
            this.endPixel = endPixel;
            this.w = w;
        } 

        @Override
        public void run() {
            final FloatBuffer oData = w.data;
            final Vector3f minSim = w.minSimBound;
            final Vector3f maxSim = w.maxSimBound;
            
            final float namp = form.getNegativeAmp();
            final int dataSize = w.dataSize;
            final int dataSizeS = dataSize*dataSize;
            final int nTrans = w.nTrans;
            final float[] transPos = w.transPosition;
            final float[] transNormal = w.transNormal;
            final float[] transSpec = w.transSpec;
            final FieldSource source = w.source;
            
            final float stepX = (maxSim.x - minSim.x) / dataSize;
            final float stepY = (maxSim.y - minSim.y) / dataSize;
            final float stepZ = (maxSim.z - minSim.z) / dataSize;
            final int dataSizeL1 = dataSize - 1;
            Vector3f diffVec = new Vector3f();
            Vector3f diffNorm = new Vector3f();
            
            final int counterMax = (endPixel - startPixel) / 50;
            int counter = counterMax;
            
            final float ta = w.form.timeA;
            final float tb = w.form.timeB;
            
            for (int pixel = startPixel; pixel < endPixel; ++pixel) {
                float a = 0.0f, b = 0.0f;
                int ix = (pixel % dataSize);
                int iy = (pixel / dataSize % dataSize);
                int iz = (pixel / dataSizeS % dataSize);
                float x = minSim.x + ix * stepX;
                float y = minSim.y + iy * stepY;
                float z = minSim.z + iz * stepZ;
                /*
                if (ix == 0 || ix == dataSizeL1 || iy == 0 || iy == dataSizeL1 || iz == 0 || iz == dataSizeL1){
                    oData.put(pixel, 0);
                }else{*/
                    for (int i = 0; i < nTrans; ++i) {
                        int it = i * 3, iq = i * 4;

                        diffVec.set(x, y, z).subtractLocal(transPos[it + 0], transPos[it + 1], transPos[it + 2]);
                        diffNorm.set(transNormal[it + 0], transNormal[it + 1], transNormal[it + 2]);

                        float dist = diffVec.normalizeLocalAndReturnLength();

                        float dot = diffNorm.dot(diffVec);
                        diffNorm.multLocal(dot).subtractLocal(diffVec);
                        float dum = 0.5f * transSpec[iq + 0] * transSpec[iq + 3] * diffNorm.length();

                        float directivity = FastMath.sinc( dum );

                        float ampDirAtt = transSpec[iq + 1] * directivity / dist;
                        float kdPlusPhase = transSpec[iq + 0] * dist + transSpec[iq + 2];
                        a += ampDirAtt * FastMath.cos(kdPlusPhase);
                        b += ampDirAtt * FastMath.sin(kdPlusPhase);
                    }
                    
                    if (source == FieldSource.sourceAmp) {
                        oData.put(pixel, FastMath.sqrt(a * a + b * b));
                        //oData.put(pixel, a * ta + b * tb );
                    } else if (source == FieldSource.sourcePhase) {
                        oData.put(pixel, FastMath.atan2(b, a));
                    } else if (source == FieldSource.sourceNegativeAmp) {
                        oData.put(pixel, namp - FastMath.sqrt(a * a + b * b));
                    }
                //}
                
                
                if(--counter == 0){
                    counter = counterMax;
                    w.updateProgress(1.0f / 50.0f); 
                }
            }
        }
        
    }
   
    
}
