package main;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.Terrain;
import models.RawModel;
import models.TexturedModel;
import tools.ModelData;
import tools.OBJFileLoader;
import wrapper.DisplayManager;
import wrapper.Loader;
import wrapper.MasterRenderer;
import wrapper.ModelTexture;

/**
 * @author Andrei
 * Class containing the main method for the project.
 * It's in this class we initalize and render all the entities and terrain onto the scene
 */
public class SceneLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay(); //opens up the display
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();
		List<Entity> entities = new ArrayList<Entity>();
		List<Light> lights = new ArrayList<Light>();
		
		//create a new terrain with the use of a heightmap
		Terrain terrain = new Terrain(-1,-1,loader,new ModelTexture(loader.loadTexture("floor")), "heightmap");
		
		//load all the necessary information for a bunny object
		ModelData bunnyData = OBJFileLoader.loadOBJ("bunny");
		RawModel bunnyModel = loader.loadToVAO(bunnyData.getVertices(), bunnyData.getTextureCoords(),
				bunnyData.getNormals(), bunnyData.getIndices());
		TexturedModel bunnyStaticModel = new TexturedModel(bunnyModel,new ModelTexture(loader.loadTexture("white")));
		bunnyStaticModel.getTexture().setShineDamper(10);
		bunnyStaticModel.getTexture().setReflectivity(1);
		
		//load all the necessary info for a lamp object
		ModelData lamp = OBJFileLoader.loadOBJ("lamp");
		RawModel lampModel = loader.loadToVAO(lamp.getVertices(), lamp.getTextureCoords(), lamp.getNormals(), lamp.getIndices());
		TexturedModel lampStaticModel = new TexturedModel(lampModel,new ModelTexture(loader.loadTexture("lamp")));
		lampStaticModel.getTexture().setUseFakeLightning(true);
		lampStaticModel.getTexture().setShineDamper(10);
		lampStaticModel.getTexture().setReflectivity(1);
		
		//load a dragon model
		ModelData dragon = OBJFileLoader.loadOBJ("dragon");
		RawModel dragonModel = loader.loadToVAO(dragon.getVertices(), dragon.getTextureCoords(), dragon.getNormals(), dragon.getIndices());
		TexturedModel dragonStaticModel = new TexturedModel(dragonModel,new ModelTexture(loader.loadTexture("red")));
		dragonStaticModel.getTexture().setShineDamper(10);
		dragonStaticModel.getTexture().setReflectivity(1);
        
		//the main light (i.e a sun or a moon)
		lights.add(new Light(new Vector3f(0,10000,-7000), new Vector3f(0.4f,0.4f,0.4f)));
		
		int colour = 0;
		int centerX = -400;
		int centerZ = -400;
		int distance = 200;
		float theta = 0;
		
		//position the other lights and lamp posts in a hexagonal shape with alternating light colours
		for (int i = 0; i < 6; i++) {
			float posX = (float) (Math.cos(Math.toRadians(theta)) * distance) + centerX;
			float posZ = (float) (Math.sin(Math.toRadians(theta)) * distance) + centerZ;
			float posY = terrain.getHeightOfTerrain(posX, posZ);
			float lightPosY = posY + 41.5f;
			
			Vector3f colourVector = new Vector3f(0,0,0);
			switch(colour) {
				case 0: colourVector = new Vector3f(2,0,0);
					break;
				case 1: colourVector = new Vector3f(0,2,2);
					break;
				case 2: colourVector = new Vector3f(2,2,0);
					break;
			}
			colour = (colour+ 1)%3;
			theta = theta + 60;
			
			lights.add(new Light(new Vector3f(posX,lightPosY,posZ), colourVector, new Vector3f(1,0.01f,0.002f)));
			entities.add(new Entity(lampStaticModel, new Vector3f(posX, posY ,posZ),0,0,0,3));
		}
		
		//position model in the center of screen
		entities.add(new Entity(dragonStaticModel, new Vector3f(-400, terrain.getHeightOfTerrain(-400, -400) ,-400),0,180,0,5));
		
		//create a new instance of player
		Player player = new Player(bunnyStaticModel, new Vector3f(-400,0,-750), (float) 0, 0,0,1);
		
		//and a camera
		Camera camera = new Camera(player);
		
		// the main scene loop, where all the objects are updated and rendered every frame
		while (!Display.isCloseRequested()) {
			
			player.move(terrain);
			camera.move();
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
            for(Entity entity:entities){
                renderer.processEntity(entity);
            }
            entities.get(6).increaseRotation(0,0.5f,0);
			renderer.render(lights, camera);
			DisplayManager.updateDisplay();
		}
		
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
