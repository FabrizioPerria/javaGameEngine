package postProcessing.gaussianBlur.vertical;

import postProcessing.PostProcessingFilter;

public class VerticalBlur extends PostProcessingFilter {
	public VerticalBlur(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight);
		shader = new VerticalBlurShader();
		setupShader((VerticalBlurShader)shader, targetFboHeight);
	}
	
	private void setupShader(VerticalBlurShader shader, int targetFboHeight){
		shader.start();
		shader.loadTargetHeight(targetFboHeight);
		shader.stop();
	}
}
