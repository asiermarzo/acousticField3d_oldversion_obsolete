/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.scene;

import acousticfield3d.math.Vector3f;
import acousticfield3d.renderer.Texture;

/**
 *
 * @author Asier
 */
public class PhysicEntity extends MeshEntity{
    Vector3f velocity = new Vector3f();
    
    public PhysicEntity() {
    }

    public PhysicEntity(String mesh, Texture texture, int shader) {
        super(mesh, texture, shader);
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
    
}
