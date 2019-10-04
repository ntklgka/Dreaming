package models;

/**
 * @author Andrei
 * This class represents a 3D Object stored in memory
 */
public class RawModel {
	
	//these variables are the two things we need to know about a model once its stored in memory
	private int vaoID;
	private int vertexCount;

	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
