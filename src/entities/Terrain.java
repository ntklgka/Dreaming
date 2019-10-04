package entities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import tools.Maths;
import wrapper.Loader;
import wrapper.ModelTexture;

/**
 * @author Andrei
 * Represents a terrain in our scene
 */
public class Terrain {
	//initialization values
	private static final float SIZE = 800;
	private static final float  MAX_HEIGHT = 45;
	private static final float  MAX_PIXEL_COLOUR = 256 * 256 * 256;
	
	private float x;
	private float z;
	private RawModel model; // the terrain mesh
	private ModelTexture texture; // the terrain texture
	
	//stores the height of each vertex on the terrain
	private float[][] heights;
	
	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, String heightMap) {
		this.texture = texture;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader, heightMap);
	}
	
	
	public float getX() {
		return x;
	}


	public float getZ() {
		return z;
	}


	public RawModel getModel() {
		return model;
	}


	public ModelTexture getTexture() {
		return texture;
	}
	
	//gets the height of the terrain for any given x,z coordinate
	public float getHeightOfTerrain(float worldX, float worldZ) {
		//transform these world coordinates into positions relative to the terrain
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		//calculate the number of grid squares in the terrain
		//-1 because the number of grids in a side is equal to the number of vertices in that side - 1
		float gridSquareSize = SIZE / ((float)heights.length - 1);
		//find out which grid square we are in
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize); 
		//test if the position is actually on the terrain
		if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		//we know which grid square we are in, so now we will find out where we are on the grid square
		//we divide by gridSquareSize to get a value between 0,1 of the position of the entity from the top left corner of the grid square
		float xCoord = (terrainX % gridSquareSize)/gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize)/gridSquareSize;
		float answer;
		//we test to see which triangle of the grid square the entity is standing on
		if (xCoord <= (1-zCoord)) {
			//now we find the height of the entity at that point in the triangle using Barry Centric interpolation
			//taken from the Math class in the toolbox package
			answer = Maths
					.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths
					.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;	
	}

	
	//generates a terrain from a height map
	//the next two methods are done using code from this tutorial https://www.youtube.com/watch?v=O9v6olrHPwI
	private RawModel generateTerrain(Loader loader, String heightMap){
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/"+heightMap+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int VERTEX_COUNT = image.getHeight();
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				float height = getHeight(j, i, image);
				heights[j][i] = height;
				vertices[vertexPointer*3+1] = height;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j,i,image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private float getHeight(int x, int z, BufferedImage image) {
		if(x<0 || x>= image.getHeight() || z<0 || z>=image.getHeight()) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOUR /2f;
		height /= MAX_PIXEL_COLOUR /2f;
		height *= MAX_HEIGHT;
		return height;
		
	}
	
	//normals are calculated using finite difference
	//https://stackoverflow.com/questions/13983189/opengl-how-to-calculate-normals-in-a-terrain-height-grid
	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x-1, z, image);
		float heightR = getHeight(x+1, z, image);
		float heightD = getHeight(x, z-1, image);
		float heightU = getHeight(x, z+1, image);
		
		Vector3f normal = new Vector3f(heightL-heightR,2f, heightD - heightU);
		normal.normalise();
		return normal;
	}
	
}
