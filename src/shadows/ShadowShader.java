package shadows;

import org.lwjgl.util.vector.Matrix4f;

import renderEngine.VertexAttrib;
import shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String fileName = "shadows/shadow";
	
	private int location_mvpMatrix;

	protected ShadowShader() {
		super(fileName);
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix4f(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(VertexAttrib.POSITION.ordinal(), "in_position");
		super.bindAttribute(VertexAttrib.TEXTURE_COORDINATE.ordinal(), "in_textureCoords");
	}

}
