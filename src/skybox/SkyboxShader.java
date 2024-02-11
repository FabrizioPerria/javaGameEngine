package skybox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import entities.Camera;
import renderEngine.DisplayManager;
import shaders.ShaderProgram;
import toolbox.Maths;

public class SkyboxShader extends ShaderProgram {
	private static final String filename = "skybox/skybox";
		
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lowerLimit;
    private int location_upperLimit;
    private int location_fogColor; 
    private int location_cubeMapDay;
    private int location_cubeMapNight;
    private int location_blendFactor;
    
    private static final float ROTATE_SPEED = 0.5f;
    private static final Vector3f yAXIS = new Vector3f(0,1,0);
    private float currentRotation = 0;
    
	public SkyboxShader() {
		super(filename);
	}

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix4f(location_projectionMatrix, matrix);
    }
 
    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.set(3,0,0);
        matrix.set(3,1,0);
        matrix.set(3,2,0);
        currentRotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
        matrix.rotate((float)Math.toRadians(currentRotation), yAXIS);
        super.loadMatrix4f(location_viewMatrix, matrix);
    }
    
    public void loadFogAttributes(float lowerLimit, float UpperLimit, Vector3f fogColor){
    	super.loadFloat(location_lowerLimit, lowerLimit);
    	super.loadFloat(location_upperLimit, UpperLimit);
    	super.loadVector3f(location_fogColor, fogColor);
    }
     
    public void loadBlendFactor(float value){
    	super.loadFloat(location_blendFactor, value);
    }
    
    public void connectCubeMaps(){
    	super.loadInt(location_cubeMapDay, 0);
    	super.loadInt(location_cubeMapNight, 1);
    }
    
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lowerLimit = super.getUniformLocation("lowerLimit");
        location_upperLimit = super.getUniformLocation("upperLimit");
        location_fogColor = super.getUniformLocation("fogColor");
        location_cubeMapDay = super.getUniformLocation("cubeMapDay");
        location_cubeMapNight = super.getUniformLocation("cubeMapNight");
        location_blendFactor = super.getUniformLocation("blendFactor");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
