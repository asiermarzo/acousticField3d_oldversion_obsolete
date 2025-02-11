/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.scene;

import acousticfield3d.math.Transform;
import acousticfield3d.math.Vector3f;
import acousticfield3d.renderer.Material;
import acousticfield3d.utils.Color;
import java.util.ArrayList;

/**
 *
 * @author Asier
 */
public class Entity {
    public static final int TAG_NONE = 1<<0;
    public static final int TAG_TRANSDUCER = 1<<1;
    public static final int TAG_CONTROL_POINT = 1<<2;
    public static final int TAG_SLICE = 1<<3;
    public static final int TAG_SIMULATION_BOUNDINGS = 1<<4;
    public static final int TAG_PRE_VOL = 1<<5;
    public static final int TAG_RT_ISO = 1<<6;
    public static final int TAG_RT_VOL = 1<<7;
    public static final int TAG_KINECT_MESH = 1<<8;
    public static final int TAG_MASK = 1<<9;
    public static final int TAG_BEAD = 1<<10;
    public static final int TAG_BEAD_TRAIL = 1<<11;
    public static final int TAG_BEAD_MOVE_PATH = 1<<12;

    Material material;
    int color;
    Transform transform;
    ArrayList<Behaviour> behaviours;
    int tag;
    int frame;
    int number;
    public boolean selected;
    
    
    public Entity() {
        tag = TAG_NONE;
        color = Color.WHITE;
        material = new Material();
        transform = new Transform();
        behaviours = new ArrayList<>();
        selected = false;
    }

    public int getColor() {
        if(!selected){
            return color;
        }else{
            return Color.GREEN;
        }
    }
    
    public int getRealColor(){
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    
    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
    
    
    public ArrayList<Behaviour> getBehaviours() {
        return behaviours;
    }

    public void setBehaviours(ArrayList<Behaviour> behaviours) {
        this.behaviours = behaviours;
    }

    public void lookAt(Entity other){
        Vector3f dir = other.getTransform().getTranslation().subtract( getTransform().getTranslation() );
        dir.negateLocal();
        getTransform().getRotation().lookAt(dir, Vector3f.UNIT_Y);
    }
    
    public void rotateAround(Entity other, float rx, float ry, float rz){
        getTransform().rotateAround( other.getTransform().getTranslation(), rx, ry, rz);
    }
}
