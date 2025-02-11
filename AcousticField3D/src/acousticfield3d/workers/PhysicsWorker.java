/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.workers;

import acousticfield3d.algorithms.CachedPointFieldCalc;
import acousticfield3d.gui.MainForm;
import acousticfield3d.gui.panels.PhysicsPanel;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Vector3f;
import acousticfield3d.renderer.Renderer;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.PhysicEntity;
import static java.lang.Thread.interrupted;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Asier
 */
public class PhysicsWorker extends Thread{
    final MainForm mf;
    final PhysicsPanel panel;
    
    public PhysicsWorker(PhysicsPanel panel){
        this.panel = panel;
        mf = panel.mf;
    }
    
    public void tickPhysics(){
        final int steps = panel.getSteps();
        final float h = panel.getSecsPerFrame() / (float)steps;
        
        final Vector3f G = panel.getGravity();
        final float vDamp = panel.getVDamp();
        
        final boolean resetSpeed = panel.isResetSpeedEveryStep();
        final boolean leaveTrail = panel.isLeaveTrail();
        final boolean destroy = panel.isDestroyMaxDist();
        final float maxDistS  = panel.getDestroyMaxDist() * panel.getDestroyMaxDist();
        
        final int maxSteps = panel.getSimulationsSteps();
        final float densityK = 4.0f/3.0f * FastMath.PI * mf.simPanel.getParticleDensity();
        final Renderer r = mf.renderer;
        
        final Vector3f f = new Vector3f();
        
        final Vector3f p0 = new Vector3f(), v0 = new Vector3f(), a0 = new Vector3f();
        final Vector3f p1 = new Vector3f(), v1 = new Vector3f(), a1 = new Vector3f();
        final Vector3f p2 = new Vector3f(), v2 = new Vector3f(), a2 = new Vector3f();
        final Vector3f p3 = new Vector3f(), v3 = new Vector3f(), a3 = new Vector3f();
               
        CachedPointFieldCalc calc = CachedPointFieldCalc.create(Vector3f.ZERO, mf);
        calc.allocate(r);
        double[] phases = mf.simulation.getTransPhasesAsArray();
        
        //itarete over all the beads
        final ArrayList<MeshEntity> trailsToAdd = new ArrayList<>();
        synchronized(mf){
            Iterator<MeshEntity> iter =  mf.scene.getEntities().iterator();
            while(iter.hasNext()){
                MeshEntity me = iter.next();
                if (me.isVisible() &&
                        (! me.selected) &&
                        (me.getTag() & Entity.TAG_CONTROL_POINT) != 0 && 
                        (me.getTag() & Entity.TAG_BEAD) == 0){
                    PhysicEntity e = (PhysicEntity)me;
                    
                    final float scale = me.getTransform().getScale().length();
                    final float mass = densityK * scale*scale*scale;

                    final Vector3f pos = e.getTransform().getTranslation();
                    final Vector3f vel = e.getVelocity();
                    
                    final Vector3f GM = new Vector3f(G);
                    GM.multLocal(mass);

                    //physic step
                    for(int i = 0; i < steps; ++i){ //RK4
                        p0.set( pos );
                        v0.set( vel );
                        calc.calcGorkovGradient(p0, a0, r, phases);
                        a0.negateLocal();
                        a0.addLocal( GM );   
                        a0.multLocal( 1.0f / mass);

                        p1.set( pos ); p1.addLocalInc( v0, 0.5f * h);
                        v1.set( vel );  v1.addLocalInc( a0, 0.5f * h);
                        calc.calcGorkovGradient(p1, a1, r, phases);
                        a1.negateLocal();
                        a1.addLocal( GM );   
                        a1.multLocal( 1.0f / mass);

                        p2.set( pos ); p2.addLocalInc( v1, 0.5f * h);
                        v2.set( vel );  v2.addLocalInc( a1, 0.5f * h);
                        calc.calcGorkovGradient(p2, a2, r, phases);
                        a2.negateLocal();
                        a2.addLocal( GM );   
                        a2.multLocal( 1.0f / mass);

                        p3.set( pos ); p3.addLocalInc( v2, h);
                        v3.set( vel );  v3.addLocalInc( a2,  h);
                        calc.calcGorkovGradient(p3, a3, r, phases);
                        a3.negateLocal();
                        a3.addLocal( GM );   
                        a3.multLocal( 1.0f / mass);

                        //integrate acceleration -> velocity
                        vel.x += (h/6.0f) * ( a0.x + 2.0f*a1.x + 2.0f*a2.x + a3.x );
                        vel.y += (h/6.0f) * ( a0.y + 2.0f*a1.y + 2.0f*a2.y + a3.y );
                        vel.z += (h/6.0f) * ( a0.z + 2.0f*a1.z + 2.0f*a2.z + a3.z );

                        //integrate velocity -> position
                        pos.x += (h/6.0f) * ( v0.x + 2.0f*v1.x + 2.0f*v2.x + v3.x );
                        pos.y += (h/6.0f) * ( v0.y + 2.0f*v1.y + 2.0f*v2.y + v3.y );
                        pos.z += (h/6.0f) * ( v0.z + 2.0f*v1.z + 2.0f*v2.z + v3.z );

                        //damping velocity
                        vel.multLocal(FastMath.clamp( 1.0f - vDamp * h, 0.0f, 1.0f));
                    }

                    if(resetSpeed){
                        e.getVelocity().reset();
                    }

                    if(leaveTrail){
                        MeshEntity trail = new MeshEntity(e.getMesh(), e.getTexture(), e.getShader());
                        trail.getTransform().set( e.getTransform() );
                        trail.getMaterial().set( e.getMaterial() );
                        trail.setColor( e.getColor() );
                        trail.setNumber( e.getNumber() );
                        trail.setFrame( mf.animPanel.getCurrentFrame() );
                        trail.setTag( Entity.TAG_BEAD_TRAIL );
                        trailsToAdd.add( trail );
                    }

                    //max dist destroy
                    if (destroy){
                        if (e.getTransform().getTranslation().lengthSquared() > maxDistS){
                            iter.remove();
                        }
                    }

                }
            }
            mf.scene.getEntities().addAll( trailsToAdd );
        }
    }
    
    public void tickPhysics(MeshEntity bead, int step1, int step2, float time) {
        final int totalSteps = step1 * step2;
        final float h = time / (float)totalSteps;
        final Vector3f G = panel.getGravity();
        final float vDamp = panel.getVDamp();
        final boolean resetSpeed = panel.isResetSpeedEveryStep();
        final Renderer r = mf.renderer;

        final Vector3f p0 = new Vector3f(), v0 = new Vector3f(), a0 = new Vector3f();
        final Vector3f p1 = new Vector3f(), v1 = new Vector3f(), a1 = new Vector3f();
        final Vector3f p2 = new Vector3f(), v2 = new Vector3f(), a2 = new Vector3f();
        final Vector3f p3 = new Vector3f(), v3 = new Vector3f(), a3 = new Vector3f();
               
        CachedPointFieldCalc calc = CachedPointFieldCalc.create(Vector3f.ZERO, mf);
        calc.allocate(r);
        double[] phases = mf.simulation.getTransPhasesAsArray();
        PhysicEntity e = (PhysicEntity) bead;

        final float densityK = 4.0f/3.0f * FastMath.PI * mf.simPanel.getParticleDensity();
        final float scale = mf.simPanel.getParticleSize();
        final float mass = densityK * scale * scale * scale;

        final Vector3f pos = e.getTransform().getTranslation();
        final Vector3f vel = e.getVelocity();

        final Vector3f GM = new Vector3f(G);
        GM.multLocal(mass);

        //physic step
        for (int i = 0; i < step1; ++i) { //RK4
            for (int j = 0; j < step2; ++j) {

                p0.set(pos);
                v0.set(vel);
                calc.calcGorkovGradient(p0, a0, r, phases);
                a0.negateLocal();
                a0.addLocal(GM);
                a0.multLocal(1.0f / mass);

                p1.set(pos);
                p1.addLocalInc(v0, 0.5f * h);
                v1.set(vel);
                v1.addLocalInc(a0, 0.5f * h);
                calc.calcGorkovGradient(p1, a1, r, phases);
                a1.negateLocal();
                a1.addLocal(GM);
                a1.multLocal(1.0f / mass);

                p2.set(pos);
                p2.addLocalInc(v1, 0.5f * h);
                v2.set(vel);
                v2.addLocalInc(a1, 0.5f * h);
                calc.calcGorkovGradient(p2, a2, r, phases);
                a2.negateLocal();
                a2.addLocal(GM);
                a2.multLocal(1.0f / mass);

                p3.set(pos);
                p3.addLocalInc(v2, h);
                v3.set(vel);
                v3.addLocalInc(a2, h);
                calc.calcGorkovGradient(p3, a3, r, phases);
                a3.negateLocal();
                a3.addLocal(GM);
                a3.multLocal(1.0f / mass);

                //integrate acceleration -> velocity
                vel.x += (h / 6.0f) * (a0.x + 2.0f * a1.x + 2.0f * a2.x + a3.x);
                vel.y += (h / 6.0f) * (a0.y + 2.0f * a1.y + 2.0f * a2.y + a3.y);
                vel.z += (h / 6.0f) * (a0.z + 2.0f * a1.z + 2.0f * a2.z + a3.z);

                //integrate velocity -> position
                pos.x += (h / 6.0f) * (v0.x + 2.0f * v1.x + 2.0f * v2.x + v3.x);
                pos.y += (h / 6.0f) * (v0.y + 2.0f * v1.y + 2.0f * v2.y + v3.y);
                pos.z += (h / 6.0f) * (v0.z + 2.0f * v1.z + 2.0f * v2.z + v3.z);

                //damping velocity
                vel.multLocal(FastMath.clamp(1.0f - vDamp * h, 0.0f, 1.0f));
            }

            if (resetSpeed) {
                e.getVelocity().reset();
            }

        }
    }
    
    public synchronized void playOrPause(){
        notify();
    }
    
    @Override
    public void run() {
        while(!interrupted()){
            if (panel.isTickEverySecs()){
                
                mf.needUpdate();
                
                int secsToSleep = (int) (panel.getTickEverySecs() * 1000);
                
                try {
                    Thread.sleep( secsToSleep );
                } catch (InterruptedException ex) {
                }
                
            }else{
                synchronized(this){
                    try {
                        wait();
                    } catch (InterruptedException ex) {}
                }
            }
        }
    }

    
}
