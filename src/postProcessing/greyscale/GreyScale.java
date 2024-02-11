package postProcessing.greyscale;

import postProcessing.PostProcessingFilter;

public class GreyScale extends PostProcessingFilter {
	
	public GreyScale(){
		super();
		shader = new GreyScaleShader();
	}
	
	public GreyScale(int targetFboWidth, int targetFboHeight){
		super(targetFboWidth, targetFboHeight);
		shader = new GreyScaleShader();
	}	
}
