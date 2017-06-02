package normalMappingRenderer;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import ambient.AmbientLight;
import ambient.Fog;
import ambient.LightDefinition;
import entities.Camera;
import entities.Light;
import renderEngine.VertexAttrib;
import shaders.ShaderProgram;
import toolbox.Maths;

public class NormalMappingShader extends ShaderProgram{
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String filename = "normalMappingRenderer/normalMap";
	
	private int location_transformationMatrix; 
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColor;
	private int location_reflectivity;
	private int location_shineDamper;
	private int location_fakeLight;
	private int location_ambientLightAmount;
	private int location_density;
	private int location_gradient;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offsetTexture;
	private int[] location_attenuationCoefficients;
	private int location_clipPlane;
	private int location_textureSampler;
	private int location_normalMap;
	private int location_toShadowMapSpace;
	private int location_shadowMap;
	private int location_shadowDistance;
	private int location_transitionDistance;
	private int location_shadowMapSize;	
	private int location_specularMap;
	private int location_hasSpecularMap;

	public NormalMappingShader() {
		super(filename);
	}

	@Override
	protected void bindAttributes() {
		super.bindFragmentOutput(0, "color");
		super.bindFragmentOutput(1, "brightColor");
		super.bindAttribute(VertexAttrib.POSITION.ordinal(), "inPosition");
		super.bindAttribute(VertexAttrib.TEXTURE_COORDINATE.ordinal(), "inTexCoord");
		super.bindAttribute(VertexAttrib.NORMAL.ordinal(), "inNormal");
		super.bindAttribute(VertexAttrib.TANGENT.ordinal(), "inTangent");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_fakeLight = super.getUniformLocation("fakeLight");
		location_ambientLightAmount = super.getUniformLocation("ambientLightAmount");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offsetTexture = super.getUniformLocation("offsetTexture");
		location_clipPlane = super.getUniformLocation("clipPlane");
		location_textureSampler = super.getUniformLocation("texturesampler");
		location_normalMap = super.getUniformLocation("normalMap");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_shadowDistance = super.getUniformLocation("shadowDistance");
		location_transitionDistance = super.getUniformLocation("transitionDistance");
		location_shadowMapSize = super.getUniformLocation("shadowMapSize");	
		location_specularMap = super.getUniformLocation("specularMap");
		location_hasSpecularMap = super.getUniformLocation("hasSpecularMap");
		
		location_attenuationCoefficients = new int[LightDefinition.NUM_LIGHTS];
		location_lightPosition = new int[LightDefinition.NUM_LIGHTS];
		location_lightColor = new int[LightDefinition.NUM_LIGHTS];
		
		for(int i = 0; i < LightDefinition.NUM_LIGHTS; ++i){
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuationCoefficients[i] = super.getUniformLocation("attenuationCoefficients[" + i + "]");
		}
	}
	
	public void connectTextureUnits(){
		super.loadInt(location_textureSampler, 0);
		super.loadInt(location_normalMap, 1);
		super.loadInt(location_shadowMap, 5);
		super.loadInt(location_specularMap, 6);
	}
	
	public void loadClipPlane(Vector4f clipPlane){
		super.loadVector4f(location_clipPlane, clipPlane);
	}
	
	public void loadNumberOfRows(int numberOfRows){
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	public void loadTextureOfsets(Vector2f offset){
		super.loadVector2f(location_offsetTexture, offset);
	}
	
	public void loadSkyColor(Vector3f skyColor){
		super.loadVector3f(location_skyColor, skyColor);
	}
	
	public void loadSpecularAttributes(float reflectivity, float shineDamper){
		super.loadFloat(location_reflectivity, reflectivity);
		super.loadFloat(location_shineDamper, shineDamper);
	}
	
	public void loadFakeLight(boolean useFakeLight){
		super.loadBoolean(location_fakeLight, useFakeLight);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix4f(location_transformationMatrix, matrix);
	}
	
	public void loadLights(List<Light> lights, Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		for(int i=0;i<MAX_LIGHTS;i++){
			if(i<lights.size()){
				super.loadVector3f(location_lightPosition[i], getEyeSpacePosition(lights.get(i), viewMatrix));
				super.loadVector3f(location_lightColor[i], lights.get(i).getColor());
				super.loadVector3f(location_attenuationCoefficients[i], lights.get(i).getAttenuation());
			}else{
				super.loadVector3f(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector3f(location_lightColor[i], new Vector3f(0, 0, 0));
				super.loadVector3f(location_attenuationCoefficients[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix4f(location_viewMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix4f(location_projectionMatrix, projection);
	}
	
	private Vector3f getEyeSpacePosition(Light light, Matrix4f viewMatrix){
		Vector3f position = light.getPosition();
		Vector4f eyeSpacePos = new Vector4f(position.x,position.y, position.z, 1f);
		Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
		return new Vector3f(eyeSpacePos);
	}
	
	public void loadFog(){
		super.loadFloat(location_density, Fog.DENSITY);
		super.loadFloat(location_gradient, Fog.GRADIENT);
	}
	
	public void loadAmbientLightAmount(){
		super.loadFloat(location_ambientLightAmount, AmbientLight.AMBIENT_LIGHT);
	}
	
	protected void loadToShadowMapSpace(Matrix4f matrix){
		super.loadMatrix4f(location_toShadowMapSpace, matrix);
	}
	
	public void loadShadowProperties(float shadowDistance, float transitionDistance, float mapSize){
		super.loadFloat(location_shadowMapSize, mapSize);
		super.loadFloat(location_shadowDistance, shadowDistance);
		super.loadFloat(location_transitionDistance, transitionDistance);
	}
	
	public void loadUseSpecularMap(boolean hasSpecularMap){
		super.loadBoolean(location_hasSpecularMap, hasSpecularMap);
	}
	
}
