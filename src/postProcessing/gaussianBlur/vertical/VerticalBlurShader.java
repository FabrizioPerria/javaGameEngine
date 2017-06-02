//package postProcessing.gaussianBlur.vertical;
//
//import shaders.ShaderProgram;
//
//public class VerticalBlurShader extends ShaderProgram{
//
//	private static final String filename = "postProcessing/gaussianBlur/vertical/gaussianBlur";
//	
//	private int location_targetHeight;
//	
//	protected VerticalBlurShader() {
//		super(filename);
//	}
//	
//	protected void loadTargetHeight(float height){
//		super.loadFloat(location_targetHeight, height);
//	}
//
//	@Override
//	protected void getAllUniformLocations() {	
//		location_targetHeight = super.getUniformLocation("targetHeight");
//	}
//
//	@Override
//	protected void bindAttributes() {
//		super.bindAttribute(0, "position");
//	}
//}
