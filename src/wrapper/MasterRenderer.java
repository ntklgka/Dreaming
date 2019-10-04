package wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Terrain;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;

/**
 * @author Andrei
 * Handles all the rendering code for the scene
 */
public class MasterRenderer {

	//used for the projection matrix
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private  Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	// we will be using a Hash Map to render different entities using the same textured model
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
    public MasterRenderer(){
    	enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderer(shader,projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
    }
    
    //enables culling the back faces
    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }
    
    public static void disableCulling() {
    	GL11.glDisable(GL11.GL_CULL_FACE);
    }
    
    //renders all the entities and terrains
	public void render(List<Light> lights, Camera camera) {
        prepare();
        shader.start();
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities); //renders all the entities in the hash map
        shader.stop();
        terrainShader.start();
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        terrains.clear();
        entities.clear();
	}
	
    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }
	
    //takes in the entity thats going to be processed and puts it into the hash map
	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel(); // find out which textured model the entity is using
		List<Entity> batch = entities.get(entityModel); //get the list that corresponds to that entity from the hash map
		if(batch!=null) { //if it already exists
			batch.add(entity); // we can add it
		}else {
			List<Entity> newBatch = new ArrayList<Entity>(); //create a new batch
			newBatch.add(entity); //add the entity
			entities.put(entityModel, newBatch); //add the batch to the hash map
		}
		
	}
	
	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
	}
	
	//called once every frame, prepares OpenGL for rendering the scene
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT); //clears the colour of the last frame
		GL11.glClearColor(0, 0, 0, 1); //sets the background colour
	}
	
	//creates a perspective projection matrix
	//code for this method is taken from this tutorial: https://www.youtube.com/watch?v=50Y9u7K0PZo
	private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
 
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
	}
}
