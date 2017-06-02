package particlesInstance;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import renderEngine.InstancedAttrib;
import shaders.ShaderProgram;

public class ParticleInstancedShader extends ShaderProgram {

	private static final String filename = "particlesInstance/particleInstanced";

	private int location_numberOfRows;
	private int location_projectionmatrix;
	private int location_particleTexture;
	
	public ParticleInstancedShader() {
		super(filename);
	}

	@Override
	protected void getAllUniformLocations() {
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_projectionmatrix = super.getUniformLocation("projectionMatrix");
		location_particleTexture = super.getUniformLocation("particleTexture");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(InstancedAttrib.POSITION.ordinal(), "position");
		super.bindAttribute(InstancedAttrib.MV_COL1.ordinal(), "modelViewMatrix");
		super.bindAttribute(InstancedAttrib.TEXTURE_OFFSETS.ordinal(), "AtlasOffsets");
		super.bindAttribute(InstancedAttrib.BLEND_FACTOR.ordinal(), "blendFactor");
	}

	public void connectTextureAtlas(){
		super.loadInt(location_particleTexture, 0);
	}
	
	public void loadNumberOfRows(float numberOfRows){
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	public void loadProjectionMatrix(Matrix4f projectionMatrix){
		super.loadMatrix4f(location_projectionmatrix, projectionMatrix);
	}
}
