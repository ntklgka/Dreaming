package wrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

/**
 * @author Andrei 
 * Handles the loading of geometry data into VAOs. It also keeps track of all
 * the created VAOs and VBOs so that they can all be deleted when the application
 * closes.
 */

public class Loader {
	
	//used for memory management 
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	//this method takes information about the model's vertices, loads them into the VAO
	//and returns data about the VAO as a RawModel object
	public RawModel loadToVAO(float[] positions,float[] textureCoords, float[] normals, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices); //binds the indices buffer the the vao, this is done automatically since there is only one indices buffer per vao
		storeDataInAttributeList(0, 3, positions); //stores the positions into attribute 0
		storeDataInAttributeList(1, 2, textureCoords); //stores the texture coordinates into attribute 1
		storeDataInAttributeList(2, 3, normals); //stores the normals into attribute 2
		unbindVAO();
		return new RawModel(vaoID, indices.length); //the number of vertices is the length of the indices buffer
	}
	
	//loads up a texture into memory so that it can be used
	//uses Slick-Util texture loader: http://slick.ninjacave.com/slick-util/
	public int loadTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png")); //file path of the texture
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D); //generate all the lower resolutions versions of the texture
			// use these lowers resolution images when the texture is rendered on a surface with lower dimensions than the texture
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int textureID = texture.getTextureID(); //gets the ID of the texture
		textures.add(textureID); //adds it to the texture list
		return textureID;
	}
	
	//once we close the scene this method deletes all the VBOS and VAOS
	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}
	
	//creates a new empty VAO
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays(); //creates an empty VAO and returns the ID
		vaos.add(vaoID); // add it to the VAO list
		GL30.glBindVertexArray(vaoID); //binds the VAO
		return vaoID;
	}
	
	//stores data into the attribute list of a VAO
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers(); //data needs to be stored as a VBO, so we generate a buffer and store the ID
		vbos.add(vboID); // add it to the VBO list
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); //we bind the buffer
		FloatBuffer buffer = storeDataInFloatBuffer(data); //array of floats needs to be converted into a float buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); //used for storing data into the VBO
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0); // put the VBO into the VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind the buffer
	}
	
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	//loads and up binds the indices buffer to the vao
	private void bindIndicesBuffer(int[] indices) {
		int vboId = GL15.glGenBuffers(); //creates an empty vbo and returns the id
		vbos.add(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId); //binds the vbo, this time its an element array buffer
		IntBuffer buffer = storeDataInIntBuffer(indices); //converts the array of indices into an int buffer
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); //stores the int buffer into the vbo
	}
	
	//Indices need to be stored into an int buffer
	//works the same way as the FloatBuffer method
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	//Data needs to be stored in the vbo as a float buffer
	//used for creating and preparing the float buffer
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data); //stores the data into the buffer
		buffer.flip(); //used for preparing the data to be read from the buffer
		return buffer;
	}

}
