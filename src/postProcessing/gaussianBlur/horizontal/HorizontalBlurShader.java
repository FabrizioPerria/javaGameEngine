package postProcessing.gaussianBlur.horizontal;

import shaders.ShaderProgram;

public class HorizontalBlurShader extends ShaderProgram {

	private static final String filename = "postProcessing/gaussianBlur/horizontal/gaussianBlur";
	
	private int location_targetWidth;
	
	protected HorizontalBlurShader() {
		super(filename);
	}

	protected void loadTargetWidth(float width){
		super.loadFloat(location_targetWidth, width);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_targetWidth = super.getUniformLocation("targetWidth");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}
