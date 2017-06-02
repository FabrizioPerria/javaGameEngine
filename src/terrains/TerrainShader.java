package terrains;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import ambient.AmbientLight;
import ambient.Fog;
import ambient.LightDefinition;
import entities.Camera;
import entities.Light;
import renderEngine.VertexAttrib;
import shaders.ShaderProgram;
import toolbox.Maths;

public class TerrainShader extends ShaderProgram {

	private static final String filename = "terrains/terrain";
	
	private int location_transformationMatrix; 
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_reflectivity;
	private int location_shineDamper;
	private int location_ambientLightAmount;
	private int location_density;
	private int location_gradient;
	private int location_skyColor;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_attenuationCoefficients[];	
	private int location_clipPlane;
//	private int location_toShadowMapSpace;
//	private int location_shadowMap;
//	private int location_shadowDistance;
//	private int location_transitionDistance;
//	private int location_shadowMapSize;
//	
	public TerrainShader() {
		super(filename);
	}

	@Override
	protected void bindAttributes() {
		super.bindFragmentOutput(0, "color");
		super.bindFragmentOutput(1, "brightColor");
		super.bindAttribute(VertexAttrib.POSITION.ordinal(), "inPosition");
		super.bindAttribute(VertexAttrib.TEXTURE_COORDINATE.ordinal(), "inTexCoord");
		super.bindAttribute(VertexAttrib.NORMAL.ordinal(), "inNormal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_shineDamper = super.getUniformLocation("shineDamper");
		
		location_ambientLightAmount = super.getUniformLocation("ambientLightAmount");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		location_skyColor = super.getUniformLocation("skyColor");
		location_backgroundTexture = super.getUniformLocation("bgTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");
		location_clipPlane = super.getUniformLocation("clipPlane");
//		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
//		location_shadowMap = super.getUniformLocation("shadowMap");
//		location_shadowDistance = super.getUniformLocation("shadowDistance");
//		location_transitionDistance = super.getUniformLocation("transitionDistance");
//		location_shadowMapSize = super.getUniformLocation("shadowMapSize");
		
		location_attenuationCoefficients = new int[LightDefinition.NUM_LIGHTS];
		location_lightPosition = new int[LightDefinition.NUM_LIGHTS];
		location_lightColor = new int[LightDefinition.NUM_LIGHTS];
		
		for(int i = 0; i < LightDefinition.NUM_LIGHTS; ++i){
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuationCoefficients[i] = super.getUniformLocation("attenuationCoefficients[" + i + "]");
		}
	}

	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix4f(location_transformationMatrix, matrix);
	}
	
	public void loadAmbientLightAmount(){
		super.loadFloat(location_ambientLightAmount, AmbientLight.AMBIENT_LIGHT);
	}

	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix4f(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix4f(location_viewMatrix, matrix);
	}
	
	public void loadLights(List<Light> lights){
		for(int i = 0; i < LightDefinition.NUM_LIGHTS; ++i){
			if(i < lights.size()){
				super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector3f(location_lightColor[i], lights.get(i).getColor());
				super.loadVector3f(location_attenuationCoefficients[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector3f(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector3f(location_lightColor[i], new Vector3f(0,0,0));
				super.loadVector3f(location_attenuationCoefficients[i], new Vector3f(1,0,0));
			}
		}
	}
	
	public void loadSpecularAttributes(float reflectivity, float shineDamper){
		super.loadFloat(location_reflectivity, reflectivity);
		super.loadFloat(location_shineDamper, shineDamper);
	}
	
	public void loadFog(){
		super.loadFloat(location_density, Fog.DENSITY);
		super.loadFloat(location_gradient, Fog.GRADIENT);
	}
	
	public void loadSkyColor(Vector3f skyColor){
		super.loadVector3f(location_skyColor, skyColor);
	}
	
	public void connectTextureUnits(){
		super.loadInt(location_backgroundTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
//		super.loadInt(location_shadowMap, 5);
	}
	
	public void loadClipPlane(Vector4f clipPlane){
		super.loadVector4f(location_clipPlane, clipPlane);
	}
	
	protected void loadToShadowMapSpace(Matrix4f matrix){
//		super.loadMatrix4f(location_toShadowMapSpace, matrix);
	}
	
	public void loadShadowProperties(float shadowDistance, float transitionDistance, float mapSize){
//		super.loadFloat(location_shadowMapSize, mapSize);
//		super.loadFloat(location_shadowDistance, shadowDistance);
//		super.loadFloat(location_transitionDistance, transitionDistance);
	}
}
