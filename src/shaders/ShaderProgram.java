package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class ShaderProgram {	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private int geometryShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public ShaderProgram(String Filename){
		programID = GL20.glCreateProgram();
		try {
			vertexShaderID = loadShader("src/" + Filename + ".vertex.glsl", GL20.GL_VERTEX_SHADER);
			GL20.glAttachShader(programID, vertexShaderID);
			
			fragmentShaderID = loadShader("src/" + Filename + ".fragment.glsl", GL20.GL_FRAGMENT_SHADER);
			GL20.glAttachShader(programID, fragmentShaderID);
		} catch (IOException e){
			//vertex and fragment shader are mandatory
			e.printStackTrace();
			System.exit(-1);
		}

		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}

    protected abstract void bindAttributes();
    
    protected void bindAttribute(int attribute, String variableName){
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }
    
    protected void bindFragmentOutput(int location, String varname){
    	GL30.glBindFragDataLocation(programID, location, varname);
    }
	
	private static int loadShader(String file, int shaderType) throws IOException{
		StringBuilder shaderSource = new StringBuilder();

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine())!=null){
			shaderSource.append(line).append("//\n");
		}
		reader.close();
        
        int shaderID = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;	
	}
	
    public void start(){
        GL20.glUseProgram(programID);
    }
     
    public void stop(){
        GL20.glUseProgram(0);
    }
    
    protected int getUniformLocation(String variableName){
    	return GL20.glGetUniformLocation(programID, variableName);
    }
    
    protected abstract void getAllUniformLocations();
	
    public void cleanUp(){
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, geometryShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(geometryShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }
    
    protected void loadFloat(int location, float value){
    	GL20.glUniform1f(location, value);
    }
    
	protected void loadInt(int location, int value){
    	GL20.glUniform1i(location, value);
    }

 	protected void loadVector2f(int location, Vector2f value){
    	GL20.glUniform2f(location, value.x, value.y);
    }   

	protected void loadVector3f(int location, Vector3f value){
    	GL20.glUniform3f(location, value.x, value.y, value.z);
    }
   
	protected void loadVector4f(int location, Vector4f value){
    	GL20.glUniform4f(location, value.x, value.y, value.z, value.w);
    }
	
	protected void loadBoolean(int location, boolean value){
    	GL20.glUniform1f(location, value == false ? 0 : 1);
    }
	
	protected void loadMatrix4f(int location, Matrix4f matrix){
		matrix.get(matrixBuffer);
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}
}
