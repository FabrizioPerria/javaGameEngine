package postProcessing.contrast;

import shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String fileName = "postProcessing/contrast/contrast";
	
	private int location_constrastAmount;
	
	public ContrastShader() {
		super(fileName);
	}

	@Override
	protected void getAllUniformLocations() {	
		location_constrastAmount = super.getUniformLocation("contrastAmount");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadContrastAmount(float amount){
		super.loadFloat(location_constrastAmount, amount);
	}

}
