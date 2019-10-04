package shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author Andrei
 * A generic shader program, containing all attributes and methods a shader program would have
 */
public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    
    //we need a FloatBuffer to load up matrixes
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16); //4x4 matrices
    
    //The constructor 
    public ShaderProgram(String vertexFile,String fragmentFile){
        vertexShaderID = loadShader(vertexFile,GL20.GL_VERTEX_SHADER); //loads up the vertex file
        fragmentShaderID = loadShader(fragmentFile,GL20.GL_FRAGMENT_SHADER); //loads up the fragment shader
        programID = GL20.glCreateProgram(); //creates the program
        GL20.glAttachShader(programID, vertexShaderID); //attach the vertex shader
        GL20.glAttachShader(programID, fragmentShaderID); //attach the fragment shader
        bindAttributes(); //binds the attributes to the VAO
        GL20.glLinkProgram(programID); // link it all together
        GL20.glValidateProgram(programID); // validate the program
        getAllUniformLocations(); // gets all the uniform locations
    }
    
    //used to make sure that all shader uniforms will have a method
    protected abstract void getAllUniformLocations();
    
    //gets the location of a uniform variable in shader code
    protected int getUniformLocation(String uniformName) {
    	return GL20.glGetUniformLocation(programID, uniformName);
    }
    
    public void start(){
        GL20.glUseProgram(programID);
    }
     
    public void stop(){
        GL20.glUseProgram(0);
    }
    
    public void cleanUp(){
        stop(); //checks that no program is currently running
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }
    
    //this method links up the inputs to the shader programs to one of the attributes of the VAO
    protected abstract void bindAttributes();
    
    //method to bind an attribute, it can't be done outside this class because of programID
    protected void bindAttribute(int attribute, String variableName){
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }
    
    //used to load up a float into an uniform
    protected void loadFloat(int location, float value) {
    	GL20.glUniform1f(location, value);
    }
    
    //used to load up a vector into an uniform
    protected void loadVector(int location, Vector3f vector) {
    	GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }
    
    //used to load up a boolean into an uniform
    //since there are no boolean values in shader code we will use either a 0 or a 1
    protected void loadBoolean(int location, boolean value) {
    	float toLoad = 0;
    	if(value) {
    		toLoad = 1;
    	}
    	GL20.glUniform1f(location, toLoad);
    }
    
    //used to load up a matrix into an uniform
    protected void loadMatrix(int location, Matrix4f matrix) {
    	matrix.store(matrixBuffer); //store the matrix into the float buffer
    	matrixBuffer.flip(); // prepare it for being read
    	GL20.glUniformMatrix4(location, false, matrixBuffer);
    }
    
    //used for loading shader source files
    //takes in the filename of the shader source file and an int that specifies if its a vertex or fragment shader
    //https://www.youtube.com/watch?v=4w7lNF8dnYw
    private static int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try{
        	InputStream input = Class.class.getResourceAsStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while((line = reader.readLine())!=null){
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }
}
