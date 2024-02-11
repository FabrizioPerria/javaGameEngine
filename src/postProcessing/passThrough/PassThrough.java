package postProcessing.passThrough;

import postProcessing.PostProcessingFilter;

public class PassThrough extends PostProcessingFilter {
	public PassThrough(){
		super();
		shader = new PassThroughShader();
	}
	
	public PassThrough(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight);
		shader = new PassThroughShader();
	}	
}
