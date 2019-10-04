package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import wrapper.DisplayManager;

/**
 * @author Andrei
 * The class that represents the player in our scene
 * 
 * 
 */
public class Player extends Entity {

	//constants for the player movement
	private static final float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -70;
	private static final float JUMP_POWER = 30;
	
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private boolean isInAir = false;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);		
	}
	
	public void move(Terrain terrain) {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0); //increase the players rotation
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds(); //calculate the distance the player is going to move forward
		//we know the distance and the rotation angle. We can now calculate the z and x axis components
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz); //increase the players position
		
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds(); //jumping
		super.increasePosition(dx, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), dz); //increase the height
		
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}
	
	//used for jumping
	private void jump() {
		if(!isInAir) { //only allowed to jump when not in the air
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	//keyboard inputs
	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}else {
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		}else {
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}

}
