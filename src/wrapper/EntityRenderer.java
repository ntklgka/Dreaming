package wrapper;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import tools.Maths;

public class EntityRenderer {

	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	//render method. Takes in the hash map of textured models and entities
	public void render(Map<TexturedModel, List<Entity>> entities) {
		for(TexturedModel model:entities.keySet()) { //loop through all of the keys in the hash map
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model); //get all entities that use that textured model
			for(Entity entity:batch) { //for each of these entities
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); //draws using indices
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel model) {
		 RawModel rawModel = model.getRawModel();
	     GL30.glBindVertexArray(rawModel.getVaoID());
	     GL20.glEnableVertexAttribArray(0); // enable positions
	     GL20.glEnableVertexAttribArray(1); // enable textures
	     GL20.glEnableVertexAttribArray(2); // enable normals
	     ModelTexture texture = model.getTexture();
	     shader.loadFakeLightningVariable(texture.isUseFakeLightning());
	     shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
	     GL13.glActiveTexture(GL13.GL_TEXTURE0); //activate texture bank 0, sampler2d in the fragment shader is in 0 by default
	     GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID()); //bind our texture to it
	}
	
	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
	    GL20.glDisableVertexAttribArray(1);
	    GL20.glDisableVertexAttribArray(2);
	    GL30.glBindVertexArray(0);
	}
	
	//prepares each entity. All it does is create the transformation matrix and loads it to the shader
	private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
	}
	
}
