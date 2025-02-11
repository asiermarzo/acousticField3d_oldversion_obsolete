/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.simulation;

import acousticfield3d.math.FastMath;
import acousticfield3d.math.Plane;
import acousticfield3d.math.Transform;
import acousticfield3d.utils.GenericListModel;
import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Scene;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Asier
 */
public class Simulation {
    float transFrequency = 40e3f;        // Hz
    float transSize = 10e-3f;    // m, size of the transducer
    float transWidth = 2.5e-3f;     // width of the trasnsducer for directivity calc
    //float transPower = 5.6f;           // Pa, pressure level constant (from microphone measurments)
    float transPower = 2.53f;           // Pa, pressure level constant (from microphone measurments)
   
    
    Vector3f boundaryMin, boundaryMax; //simulation boundaries
      
    public MeshEntity reflector;
    public ArrayList<Transducer> transducers;
    public ArrayList<MeshEntity> controlPoints;
    public GenericListModel<Animation> animations;
    public ArrayList<MeshEntity> maskObjects = new ArrayList<MeshEntity>();
    public float[] holoMemory;
    
    public Simulation() {
        reflector = null;
        float airSpeed = getSoundSpeedInAir(25);  // m/s, sound speed in air    
        float lambda = airSpeed/transFrequency;    // wavelength
 
        boundaryMin = new Vector3f(-lambda*3);
        boundaryMax = new Vector3f(lambda*3);
        transducers = new ArrayList<>();
        controlPoints = new ArrayList<>();
        animations = new GenericListModel<>();
    }
    
    public static float getSoundSpeedInAir(float temperature){
        return 331.4f + 0.6f * temperature;
    }
    
    public void addTransToAnimationsKeys(Transducer t) {
        for(Animation a : animations.getElements()){
            a.addTransducer(t);
        }
    }
    
    public void resetSimulation(int x, int y){
        //init field image
             
        //allocate transducers
        for (Transducer t : transducers){
            t.initFromSimulation(this);
            t.updateEntity();
        }
        
        updateSimulationBoundaries();
    }
    

    //<editor-fold defaultstate="collapsed" desc="Getters and setters">
    
    
    public MeshEntity getReflector() {
        return reflector;
    }

    public void setReflector(MeshEntity reflector) {
        this.reflector = reflector;
    }

    public ArrayList<MeshEntity> getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(ArrayList<MeshEntity> controlPoints) {
        this.controlPoints = controlPoints;
    }

    public float getTransFrequency() {
        return transFrequency;
    }
    
    public void setTransFrequency(float transFrequency) {
        this.transFrequency = transFrequency;
    }
    
    public float getTransSize() {
        return transSize;
    }
    
    public void setTransSize(float transSize) {
        this.transSize = transSize;
    }
    
    public float getTransWidth() {
        return transWidth;
    }
    
    public void setTransWidth(float transWidth) {
        this.transWidth = transWidth;
    }
    
    public float getTransPower() {
        return transPower;
    }
    
    public void setTransPower(float transPower) {
        this.transPower = transPower;
    }
    
    public Vector3f getBoundaryMin() {
        return boundaryMin;
    }

    public void setBoundaryMin(Vector3f boundaryMin) {
        this.boundaryMin = boundaryMin;
    }

    public Vector3f getBoundaryMax() {
        return boundaryMax;
    }

    public void setBoundaryMax(Vector3f boundaryMax) {
        this.boundaryMax = boundaryMax;
    }

    
    
    public ArrayList<Transducer> getTransducers() {
        return transducers;
    }
    
    public void setTransducers(ArrayList<Transducer> transducers) {
        this.transducers = transducers;
    }
   
    
    public GenericListModel<Animation> getAnimations() {
        return animations;
    }
    
    public void setAnimations(GenericListModel<Animation> animations) {
        this.animations = animations;
    }

    public float[] getHoloMemory() {
        return holoMemory;
    }

    public void setHoloMemory(float[] holoMemory) {
        this.holoMemory = holoMemory;
    }

    public ArrayList<MeshEntity> getMaskObjects() {
        return maskObjects;
    }

    public void setMaskObjects(ArrayList<MeshEntity> maskObjects) {
        this.maskObjects = maskObjects;
    }
    
    
    //</editor-fold>
    
    public float maxDistanceBoundary(){
        Vector3f distances = boundaryMax.subtract(boundaryMin);
        return distances.maxComponent(); 
    }
    
    public float minDistanceBoundary(){
        Vector3f distances = boundaryMax.subtract(boundaryMin);
        return distances.minComponent(); 
    }
    
    public void updateSimulationBoundaries(){
        Scene.getSizeOfEntities(transducers, boundaryMin, boundaryMax);
        
        boundaryMin.addLocal( - transSize / 2.0f);
        boundaryMax.addLocal( transSize / 2.0f );
        
        Vector3f distances = boundaryMax.subtract(boundaryMin);
        float maxDistance = distances.maxComponent();
        
        boundaryMin.x -= (maxDistance - distances.x)/2.0f;
        boundaryMin.y -= (maxDistance - distances.y)/2.0f;
        boundaryMin.z -= (maxDistance - distances.z)/2.0f;
        boundaryMax.x += (maxDistance - distances.x)/2.0f;
        boundaryMax.y += (maxDistance - distances.y)/2.0f;
        boundaryMax.z += (maxDistance - distances.z)/2.0f;
    }
    
    public Vector3f getSimulationCenter(){
        return new Vector3f(boundaryMax).addLocal(boundaryMin).divideLocal(2.0f);
    }

    public Vector3f getSimulationSize() {
        return new Vector3f(boundaryMax).subtractLocal(boundaryMin);
    }
    
    public void orderTransducersAsSelection(ArrayList<Entity> selection) {
        //get all the transducers from selection
        ArrayList<Transducer> transSel = new ArrayList<>();
        for(Entity e : selection){
            if (e instanceof Transducer){
                transSel.add( (Transducer) e);
            }
        }
        
        //pass all the transducers to hashmap
        List<Transducer> trans = getTransducers();
        HashMap<Transducer, Transducer> mapTrans = new HashMap<>();
        for(Transducer t : trans) { mapTrans.put(t,t); }
        
        //clear transducers
        trans.clear();
        
        //add from allTrans while removing them
        for(Transducer t : transSel){
            trans.add(t);
            mapTrans.remove(t);
        }
        
        //add the rest
        for(Transducer t : mapTrans.keySet()){
            trans.add(t);
        }
        
        labelNumberTransducers();
    }
    
    public void labelNumberTransducers(){
        int lastIndex = -1;
        for(Transducer t : transducers){
            if (t.getOrderNumber() <= lastIndex){
                t.setOrderNumber(lastIndex+1);
            }
            lastIndex = t.getOrderNumber();
        }
    }
    
    public void sortTransducers(){
        Collections.sort(transducers, new Comparator<Transducer>() {
            @Override
            public int compare(Transducer o1, Transducer o2) {
                return Integer.compare( o1.getOrderNumber(), o2.getOrderNumber());
            }
        });
    }
    
    public void sortAnimations(){
        Collections.sort(animations.getElements(), new Comparator<Animation>() {
            @Override
            public int compare(Animation o1, Animation o2) {
                return Integer.compare(o1.getNumber(), o2.getNumber());
            }
        });
        for(Animation a : animations.getElements()){
            a.sortKeyFrames();
        }
    }
    

    public double[] getTransPhasesAsArray() {
        int nTrans = transducers.size();
        if (isReflection()){
            nTrans /= 2;
        }
        double[] phases = new double[nTrans];
        for(int i = 0; i < nTrans; ++i){
            phases[i] = transducers.get(i).getPhase() * FastMath.PI;
        }
        return phases;
    }
    
    public boolean isReflection(){
        return reflector != null;
    }
    
    public void disableReflector(){
        if (reflector == null){
            return;
        }
        reflector = null;
        final int t = transducers.size();
        final int t2 = t / 2;
        for (int i = t - 1; i >= t2; --i) {
            transducers.remove(i);
        }
    }
    
    public void enableReflector(MeshEntity me, Scene scene){
        if (reflector != null){
            return;
        }
        reflector = me;
        final int tn = transducers.size();
        
        for(int i = 0; i < tn; ++i){
            Transducer t = transducers.get(i);
            Transducer copy = t.createReflectedCopy();
            transducers.add( copy );
            scene.getEntities().add( copy );
        }
        updateReflectedTransducers();
    }
    
    public void updateReflectedTransducers(){
        if (reflector == null){
            return;
        }
        final int t = transducers.size();
        final int t2 = t / 2;
       
        final Plane rPlane = new Plane();
        final Transform rTra = reflector.getTransform();
        rPlane.setOriginNormal(rTra.getTranslation() , rTra.getRotation().mult(Vector3f.UNIT_Z));

        Vector3f dir = new Vector3f();
        Vector3f dirR = new Vector3f();
        for(int i = 0; i < t2; ++i){
            final Transducer a = transducers.get( i );
            final Transducer b = transducers.get( i + t2 );
            
            b.setPhase( a.getPhase() ); //copy phase
            b.setPAmplitude( a.getPAmplitude() ); //copy amp
            
            //reflect position
            rPlane.reflect( a.getTransform().getTranslation(), b.getTransform().getTranslation());

            //reflect orientation 
            a.getTransform().getRotation().mult(Vector3f.UNIT_Y, dir);
            rPlane.reflect( dir, dirR);
            dirR.subtractLocal( b.getTransform().getTranslation() );
            dirR.normalizeLocal();
            b.getTransform().getRotation().lookAt(dirR, Vector3f.UNIT_Y);
            b.getTransform().getRotation().rotateLocalSpace(90 * FastMath.DEG_TO_RAD, 0, 0);
            
        }
    }
}
