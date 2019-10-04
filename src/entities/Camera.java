package entities;


import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
 
/**
 * @author Andrei
 * This is the class that represents our virtual camera
 * 
 */
public class Camera {
     
	private float distanceFromPlayer = 70; //the zoom
	private float angleAroundPlayer = 0;
	
	
    private Vector3f position = new Vector3f(0,20,25);
    private float pitch = 15; //how high or low
    private float yaw = 0; // how left or right
    private float roll; // how much it's tilted
    
    //camera is going to be following the player around
    private Player player;
    
    //so we need information about the player
    public Camera(Player player){
    	this.player = player;
    }
    
    //called whenever we want to move the camera around
    public void move(){
    	//these 2 methods will gives us the information necessary to calculate the cameras position and rotation
    	calculateZoom();
    	calculatePitchAndAngle();
    	
    	//horizontal and vertical distances of the camera from the player
    	float horizontalDistance = calculateHorizontalDistance();
    	float verticalDistance = calculateVerticalDistance();
    	
    	//calculate the cameras position
    	calculateCameraPosition(horizontalDistance, verticalDistance);
    	
    	//using intersection of parallel lines angle equivalence we can get the yaw
    	//http://jwilson.coe.uga.edu/EMAT6680/Dunbar/Math7200/ParallelLines/parall5.gif
    	//the yaw is equal to 180 minus theta from the calculateCameraPosition method
    	this.yaw = 180 - (player.getRotY() + angleAroundPlayer);

    }
 
    public Vector3f getPosition() {
        return position;
    }
 
    public float getPitch() {
        return pitch;
    }
 
    public float getYaw() {
        return yaw;
    }
 
    public float getRoll() {
        return roll;
    }
    
    //calculates the actual position of the camera
    private void calculateCameraPosition(float horizDistance, float verticDistance) {
    	//theta is the player rotation angle + angle around the player input by the user
    	//http://www.technologyuk.net/mathematics/geometry/images/geometry_0013.gif
    	//equivalent triangle angles is why we can use the player rotation here
    	float theta = player.getRotY() + angleAroundPlayer;
    	
    	float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta))); //same way we calculated horizontal and vertical distances
    	float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
    	
    	// the positions of the camera
    	// we subtract them from the players position because the cameras offsets from the player
    	// will be in the negative x and z direction
    	position.x = player.getPosition().x - offsetX;
    	position.z = player.getPosition().z - offsetZ;
    	
    	//we know how far the camera is away from the player with verticDistance
    	//and we know the players y position. So we can calculate the cameras y position
    	//6 is used as an offset
    	position.y = (player.getPosition().y + verticDistance) + 6;
    }
    
    //horizontal distance
    private float calculateHorizontalDistance() {
    	return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }
    
    //vertical distance
    private float calculateVerticalDistance() {
    	return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }
    
    private void calculateZoom() {
    	float zoomLevel = Mouse.getDWheel() * 0.1f; //mouse wheel input
    	distanceFromPlayer -= zoomLevel; //zooms out when we move the mousewheel down
    	//some limiting values
    	if (distanceFromPlayer < 10) {
    		distanceFromPlayer = 10;
    	}
    	if (distanceFromPlayer > 200) {
    		distanceFromPlayer = 200;
    	}
    	
    }
    
    private void calculatePitchAndAngle() {
    	if(Mouse.isButtonDown(1)) { //right mouse button
    		float pitchChange = Mouse.getDY() * 0.1f; //how much the mouse has moved 
    		pitch -= pitchChange; //subtract it from the pitch
    		
    		float angleChange = Mouse.getDX() * 0.3f; //how much the mouse has moved
    		angleAroundPlayer -= angleChange;
    		//some limiting values
    		if (pitch < 1) {
    			pitch = 1;
    		}
    		if (pitch > 100) {
    			pitch = 100;
    		}
    	}
    }
     
 
}