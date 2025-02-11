/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.simulation;

import acousticfield3d.gui.controls.Gradients;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Transform;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Resources;
import acousticfield3d.utils.Color;

/**
 *
 * @author Asier
 */
public class Transducer extends MeshEntity{
    public float frequency = 40e3f;        // Hz
    public float size = 10e-3f;    // m, size of the transducer
    public float width = 0.0025f;     // height of the trans
    public float power = 2.53f;           // Pa, pressure level constant (from microphone measurments)
    
    public String name;
    public float pAmplitude;
    public float phase; //a phase of 2 means 2pi 

    public float pAmplitudeOffset = 0.0f;
    public float phaseOffset = 0.0f;
    
    private int orderNumber;
    
    public boolean useGreyScale;
    private boolean isVirtualReflection;

    private Transform snappedTransform = new Transform();
    
    public Transducer() {
        super(Resources.MESH_TRANSDUCER, null, Resources.SHADER_SOLID_SPEC);
        getMaterial().ambient = 0.8f;
        getMaterial().diffuse = 0.2f;
        getMaterial().specular = 0.1f;
        useGreyScale = false;
        isVirtualReflection = false;
        updateEntity();
        name = "no name";
    }
    
    public void snapTransform(){
        snappedTransform.set( getTransform() );
    }
    public void restoreTransform(){
        getTransform().set( snappedTransform );
    }
    
    public void updateEntity(){
        getTransform().getScale().set(size, width, size);
        setColor( getColor() );
    }
    
    private void initVars(Simulation s){
        width = s.transWidth;
        size = s.transSize;
        power = s.transPower;
        frequency = s.transFrequency;
    }
    
    public void initFromSimulation(Simulation s){
        initVars(s); 
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public static int calcXMOSPhase(final float phase, final float amp, final int phaseDisc, final int phaseForOff){
        int xPhase;
        if (amp == 0.0f) {
            xPhase = phaseForOff;
        } else {
            xPhase = -(Math.round(phase * phaseDisc)) % (phaseDisc * 2);
            if (xPhase < 0) {
                xPhase += phaseDisc * 2;
            }
        }
        return xPhase;
    }
    
    public int getXMOSPhase(final int phaseDisc, final int phaseForOff){
        return calcXMOSPhase(getPhase(), getPAmplitude(), phaseDisc, phaseForOff);
    }

    @Override
    public int getColor() {
        final float p = FastMath.abs( (phase + 1.0f) / 2.0f % 1.0f );
        if(useGreyScale){
            final int ip = (int)(p*255);
            final int ia = (int)(pAmplitude*255);
            return Color.create( ip, ip, ip, ia);
        }else{
            return Gradients.get().getGradientAmpAndPhase(pAmplitude, p);
        }
            
    }

    @Override
    public String getMesh() {
        if (selected){
            return Resources.MESH_BOX;
        }else{
            return super.getMesh(); 
        }
        
    }
    
    

    //<editor-fold defaultstate="collapsed" desc="getters and setters">
    

    public float getpAmplitudeOffset() {
        return pAmplitudeOffset;
    }

    public void setpAmplitudeOffset(float pAmplitudeOffset) {
        this.pAmplitudeOffset = pAmplitudeOffset;
    }

    public float getPhaseOffset() {
        return phaseOffset;
    }

    public void setPhaseOffset(float phaseOffset) {
        this.phaseOffset = phaseOffset;
    }
    
    public float calcRealDiscAmplitude(boolean disc, float discValue, float transPower){
        if (disc){
            return (FastMath.discretize(getPAmplitude(),discValue) + getpAmplitudeOffset()) * transPower;
        }else{
            return (getPAmplitude() + getpAmplitudeOffset()) * transPower;
        }
    }

    public float calcRealDiscPhase(boolean disc, float discValue){
        if (disc){
            return ( FastMath.discretize(getPhase(),discValue) + getPhaseOffset()) * FastMath.PI;
        }else{
            return (getPhase() + getPhaseOffset()) * FastMath.PI;
        }
    }
    
    
    public float getPAmplitude() {
        return pAmplitude;
    }
    
    public void setPAmplitude(float pAmplitude) {
        this.pAmplitude = pAmplitude;
    }
    
    public float getPhase() {
        return phase;
    }
    
    public void setPhase(float phase) {
        this.phase = phase;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getpAmplitude() {
        return pAmplitude;
    }

    public void setpAmplitude(float pAmplitude) {
        this.pAmplitude = pAmplitude;
    }

    public boolean isIsVirtualReflection() {
        return isVirtualReflection;
    }

    public void setIsVirtualReflection(boolean isVirtualReflection) {
        this.isVirtualReflection = isVirtualReflection;
    }
    
    
//</editor-fold>

    @Override
    public String toString() {
        return name;
    }

    public Transducer createReflectedCopy() {
        Transducer t = new Transducer();
        
        t.getTransform().set( getTransform() );
        
        t.isVirtualReflection = true;
        
        t.frequency = frequency;        // Hz
        t.size = size;    // m, size of the transducer
        t.width = width;     // width of the trasnsducer for directivity calc
        t.power = power;           // Pa, pressure level constant (from microphone measurments)
        t.pAmplitude = pAmplitude;
        t.phase = phase; 
        t.pAmplitudeOffset = pAmplitudeOffset;
        t.phaseOffset = phaseOffset;
    
  
        return t;
    }

    
}
