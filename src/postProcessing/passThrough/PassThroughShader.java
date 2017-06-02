package postProcessing.passThrough;

import shaders.ShaderProgram;

public class PassThroughShader extends ShaderProgram {

	private static final String fileName = "postProcessing/passthrough/passthrough";
	
	public PassThroughShader() {
		super(fileName);
	}

	@Override
	protected void getAllUniformLocations() {	
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}
