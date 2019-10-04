package entities;


import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

/**
 * @author Andrei
 * An entity is an instance of a textured model
 * This class will allow us to create multiple entities which all use the same textured model
 */
public class Entity {
	
    private TexturedModel model;
    private Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;
 
    //constructor
    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
            float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }
    
    //moves the entity in the world
    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }
    
    //rotates the entity
    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }
    
    public TexturedModel getModel() {
        return model;
    }
 
    public void setModel(TexturedModel model) {
        this.model = model;
    }
 
    public Vector3f getPosition() {
        return position;
    }
 
    public void setPosition(Vector3f position) {
        this.position = position;
    }
 
    public float getRotX() {
        return rotX;
    }
 
    public void setRotX(float rotX) {
        this.rotX = rotX;
    }
 
    public float getRotY() {
        return rotY;
    }
 
    public void setRotY(float rotY) {
        this.rotY = rotY;
    }
 
    public float getRotZ() {
        return rotZ;
    }
 
    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }
 
    public float getScale() {
        return scale;
    }
 
    public void setScale(float scale) {
        this.scale = scale;
    }
 
}
