/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.scene;

import acousticfield3d.math.Matrix4f;
import acousticfield3d.math.Quaternion;
import acousticfield3d.math.Ray;
import acousticfield3d.math.Vector2f;
import acousticfield3d.math.Vector3f;
import acousticfield3d.simulation.Simulation;
import acousticfield3d.simulation.Transducer;
import acousticfield3d.utils.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Asier
 */
public class Scene {
    Camera camera;
    Light light;
    ArrayList<MeshEntity> entities;
    
    MeshEntity preCubeVol;
    MeshEntity rtCubeVol;
    MeshEntity rtIsoVol;
    MeshEntity kinectSlice;
    
    public Scene() {
        camera = new Camera();
        light = new Light();
        entities = new ArrayList<>();
        initScene();
    }

    public MeshEntity getPreCubeVol() {
        return preCubeVol;
    }
    
    
    public void adjustCameraNearAndFar(Simulation s){
        getCamera().setNear( s.getTransSize() / 2.0f);
        getCamera().setFar( s.maxDistanceBoundary() * 10.0f);
    }
    
    private void adjustCameraToPoint(Simulation s, float aspect, Vector3f position){
        Vector3f simCenter = s.getSimulationCenter();
        
        adjustCameraNearAndFar(s);
        getCamera().setOrtho(false);
        getCamera().updateProjection( aspect );
        getCamera().getTransform().getTranslation().set(
                position.x, 
                position.y,
                position.z);
        getCamera().activateObservation(true, simCenter);
    }
    
    public void adjustCameraToSimulation(Simulation s, float aspect){
        Vector3f simCenter = s.getSimulationCenter();
        Vector3f simMax = s.getBoundaryMax();
        
        adjustCameraToPoint( s, aspect,
                new Vector3f( simCenter.x, 
                simMax.y + getCamera().getNear() * 8.0f,
                simMax.z + getCamera().getNear() * 8.0f ));
    }
    
    public void adjustCameraToTop(Simulation s, float aspect){
        Vector3f simCenter = s.getSimulationCenter();
        Vector3f simMax = s.getBoundaryMax();
        
        adjustCameraToPoint( s, aspect,
                new Vector3f( simCenter.x, 
                simMax.y + getCamera().getNear() * 8.0f,
                simCenter.z ));
    }
    
    public void adjustCameraToFront(Simulation s, float aspect){
        Vector3f simCenter = s.getSimulationCenter();
        Vector3f simMax = s.getBoundaryMax();
        
        adjustCameraToPoint( s, aspect,
                new Vector3f( simCenter.x, 
                simCenter.y,
                simMax.z + getCamera().getNear() * 8.0f ));
    }
    
    public void adjustCameraToCover(Entity e) {
        final Vector3f pos = e.getTransform().getTranslation();
        final Quaternion rot = e.getTransform().getRot();
        final Vector3f scale = e.getTransform().getScale();
        
        final float w2 = scale.getX() / 2.0f;
        final float h2 = scale.getY() / 2.0f;
        final float dist = w2 + h2;
        final Vector3f normal = rot.mult( Vector3f.UNIT_Z );
        
        final Vector3f camPos = camera.getTransform().getTranslation();
        camPos.set( normal );
        camPos.multLocal( dist );
        camPos.addLocal( pos );
       
        camera.getTransform().lookAt( pos );
        camera.getProjection().setProjection(
                camera.getNear(), camera.getFar(), 
                -w2, w2, h2, -h2, true);
    }
    
  
    public void addTransducersFromSimulation(Simulation simulation){
        entities.addAll( simulation.getTransducers() );
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public ArrayList<MeshEntity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<MeshEntity> entities) {
        this.entities = entities;
    }

    private void initScene() {
        camera.getTransform().setTranslation(0, 0, 20);
        camera.setFov(45); camera.setOrtho(false);
        camera.updateProjection(1.5f);
        camera.setObservationMode(true);
        
        
        light.getTransform().getTranslation().set(10,10,-10);
        //light.getBehaviours().add( new RotateAround(new Vector3f(0, 10, 0), 10, 1f));
        
        MeshEntity me;
        me = new MeshEntity(Resources.MESH_SPHERE, null, Resources.SHADER_SOLID);
        me.setColor(Color.WHITE);
        me.getTransform().setScale(0.0001f);
        me.getTransform().getTranslation().set(light.getTransform().getTranslation());
        //me.getBehaviours().add(new Follow(light));
        entities.add(me);
       
    }
    
    //x and y should be in the range -1, 1
    public Vector3f screenPointToVector(float x, float y){
        x = x * 2.0f - 1.0f;
        y = y * 2.0f - 1.0f;
        
        Vector3f toReturn =  new Vector3f(x, y, -camera.getNear());
        
        Matrix4f invProjection = getCamera().getProjection().invert();
        
        invProjection.multiplyPoint(toReturn, toReturn);
        
        getCamera().getTransform().transformPoint(toReturn, toReturn);
        
        return toReturn;
    }

    public Ray pointToRay(float x, float y) {
        Ray r = new Ray();
        r.fromTwoPoints(camera.getTransform().getTranslation(), screenPointToVector(x, y));
        return r;
    }
    
    public Vector3f clickToObject(float x, float y, MeshEntity entity){
        Ray r = pointToRay(x, y);
        float currentDistance = entity.rayToBox(r);
        return r.pointAtDistance( currentDistance );
    }
    
    public MeshEntity pickObject(float x, float y, int tagBits){
        Ray r = pointToRay(x, y);
        
        
        float minDistance = Float.MAX_VALUE;
        MeshEntity pick = null;
        for( MeshEntity me : entities){
            if((me.tag & tagBits) != 0 ){
                float currentDistance = me.rayToBox(r);
                if (currentDistance >= 0.0f && currentDistance < minDistance){
                    minDistance = currentDistance;
                    pick = me;
                }
            }
        }
        return pick;
    }
    
    public static void setVisible(List<MeshEntity> list, int tagBit, int frame, int number, boolean visible){
        Iterator<MeshEntity> i = list.iterator();
        while (i.hasNext()){
            MeshEntity e = i.next();
            if ((e.tag & tagBit) != 0){
                if ( (frame == -1 || e.frame == frame)&& (number == -1 || e.number == number)){
                    e.setVisible(visible);
                }else{
                    e.setVisible(! visible);
                }
            }
        }
    }
    
    
    public static void setVisible(List<MeshEntity> list, int tagBit, boolean visible){
        Iterator<MeshEntity> i = list.iterator();
        while (i.hasNext()){
            MeshEntity e = i.next();
            if ((e.tag & tagBit) != 0){
                e.setVisible(visible);
            }
        }
    }
    
    public static void setShader(List<MeshEntity> list, int tagBit, int shader){
        Iterator<MeshEntity> i = list.iterator();
        while (i.hasNext()){
            MeshEntity e = i.next();
            if ((e.tag & tagBit) != 0){
                e.setShader( shader );
            }
        }
    }
    
    public static void removeWithTag(List<MeshEntity> list, int tagBit){
        Iterator<MeshEntity> i = list.iterator();
        while (i.hasNext()){
            Entity e = i.next();
            if ((e.tag & tagBit) != 0){
                i.remove();
            }
        }
    }
    
    public static Entity getFirstWithTag(ArrayList<Entity> list, int tag){
        for(Entity e : list){
            if( (e.getTag() & tag) != 0){
                return e;
            }
        }
        return null;
    }
    
    public Entity getFirstWithTag(int tag){
        for(MeshEntity e : entities){
            if( (e.getTag() & tag) != 0){
                return e;
            }
        }
        return null;
    }
    
    public void gatherMeshEntitiesWithTag(ArrayList<MeshEntity> a, int tag){
        for(MeshEntity e : entities){
            if( (e.getTag() & tag) != 0){
                a.add(e);
            }
        }
    }
    
    public void updateBoundaryBoxes(Simulation simulation){
        removeWithTag(entities, Entity.TAG_SIMULATION_BOUNDINGS);
        
        MeshEntity me;
        final float boxWidth = simulation.getTransSize() / 16.0f;
        Vector3f min = simulation.getBoundaryMin();
        Vector3f max = simulation.getBoundaryMax();
        final float simWidth = max.y - min.y;
        final float midY = (max.y + min.y) / 2.0f;
        
        me = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_SOLID);
        me.setColor(Color.WHITE);
        me.tag = Entity.TAG_SIMULATION_BOUNDINGS;
        me.getTransform().getTranslation().set(min.x, midY, min.z);
        me.getTransform().getScale().set(boxWidth,simWidth,boxWidth);
        entities.add(me);
        
        me = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_SOLID);
        me.setColor(Color.WHITE);
        me.tag = Entity.TAG_SIMULATION_BOUNDINGS;
        me.getTransform().getTranslation().set(min.x, midY, max.z);
        me.getTransform().getScale().set(boxWidth,simWidth,boxWidth);
        entities.add(me);
        
        me = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_SOLID);
        me.setColor(Color.WHITE);
        me.tag = Entity.TAG_SIMULATION_BOUNDINGS;
        me.getTransform().getTranslation().set(max.x, midY, min.z);
        me.getTransform().getScale().set(boxWidth,simWidth,boxWidth);
        entities.add(me);
        
        me = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_SOLID);
        me.setColor(Color.WHITE);
        me.tag = Entity.TAG_SIMULATION_BOUNDINGS;
        me.getTransform().getTranslation().set(max.x, midY, max.z);
        me.getTransform().getScale().set(boxWidth,simWidth,boxWidth);
        entities.add(me);
        
        simToCube(getPreCubeVol(), simulation);
        simToCube(getRtCubeVol(), simulation);
        simToCube(getRtIsoVol(), simulation);
    }


    public void addInitVizObjects() {
        removeWithTag(entities, Entity.TAG_PRE_VOL | Entity.TAG_RT_ISO | Entity.TAG_RT_VOL);
        
        
        preCubeVol = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_VOL_PRE);
        preCubeVol.setTag( Entity.TAG_PRE_VOL );
        preCubeVol.visible = false;
        getEntities().add( preCubeVol );
        
        rtCubeVol = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_VOL_RT);
        rtCubeVol.setTag( Entity.TAG_RT_VOL );
        rtCubeVol.visible = false;
        rtCubeVol.color = Color.WHITE;
        getEntities().add( rtCubeVol );
        
        rtIsoVol = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_ISO_RT_AMP);
        rtIsoVol.setTag( Entity.TAG_RT_ISO );
        rtIsoVol.visible = false;
        rtIsoVol.color = Color.WHITE;
        rtIsoVol.getMaterial().ambient = 0.7f;
        rtIsoVol.getMaterial().diffuse = 0.5f;
        rtIsoVol.getMaterial().specular = 0.5f;
        rtIsoVol.getMaterial().shininess = 10;
        getEntities().add( rtIsoVol );
        
        kinectSlice = new MeshEntity(Resources.MESH_CUSTOM, null, Resources.SHADER_SLICE_RT_AMP);
        kinectSlice.setTag( Entity.TAG_KINECT_MESH );
        kinectSlice.visible = false;
        kinectSlice.getTransform().getScale().set(0.001f);
        getEntities().add( kinectSlice );
    }
    
    public void cubeToSim(MeshEntity cube, Simulation s){
        Vector3f sCenter = cube.getTransform().getTranslation();
        Vector3f sSizeHalf = cube.getTransform().getScale().divide(2.0f);
        
        s.setBoundaryMax( sCenter.add(sSizeHalf) );
        s.setBoundaryMin( sCenter.subtract(sSizeHalf) );
    }
    
    public void simToCube(MeshEntity cube, Simulation s){
        cube.getTransform().setTranslation( s.getSimulationCenter());
        cube.getTransform().getRotation().set( Quaternion.IDENTITY );
        cube.getTransform().setScale( s.getSimulationSize() );
    }
   

    public MeshEntity getRtCubeVol() {
        return rtCubeVol;
    }

    public MeshEntity getRtIsoVol() {
        return rtIsoVol;
    }

    public MeshEntity getKinectSlice() {
        return kinectSlice;
    }

    public void removeWithTag(int tag) {
        Iterator<MeshEntity> iter = entities.iterator();
        while(iter.hasNext()){
            MeshEntity me = iter.next();
            if( (me.getTag() & tag) != 0 ){
                iter.remove();
            }
        }
    }

    
    public static void gatherArraySizeXZ(ArrayList<? extends Entity> entities, Vector2f min, Vector2f max){
        min.set(Float.MAX_VALUE);
        max.set(-Float.MAX_VALUE);
        for(Entity t : entities){
            final Vector3f pos = t.getTransform().getTranslation();
            min.setMin( pos.x, pos.z);
            max.setMax( pos.x, pos.z);
        }
    }
    
    public static void getSizeOfEntities(ArrayList<? extends Entity> entities, Vector3f min, Vector3f max){
        final int nEnts = entities.size();
        
        min.set(Float.MAX_VALUE);
        max.set(-Float.MAX_VALUE);
        
        if(nEnts == 0){
            min.set(0);
            max.set(0);
        }else{
            for(Entity e : entities){
                min.minLocal( e.getTransform().getTranslation() );
                max.maxLocal( e.getTransform().getTranslation() );
            }
        }
    }
    
     public static void getCenterAndDimOfEntities(ArrayList<? extends Entity> entities, Vector3f center, Vector3f dim){
         final Vector3f aux = new Vector3f();
         getSizeOfEntities(entities, center, aux);
         
         dim.set(aux).subtractLocal(center);
         center.addLocal(aux).divideLocal(2f);
     }
    
}
