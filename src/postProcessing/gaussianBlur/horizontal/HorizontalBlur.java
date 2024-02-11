package postProcessing.gaussianBlur.horizontal;

import postProcessing.PostProcessingFilter;

public class HorizontalBlur extends PostProcessingFilter{
	
	public HorizontalBlur(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight);
		shader = new HorizontalBlurShader();
		setupShader((HorizontalBlurShader)shader, targetFboWidth);
	}
	
	private void setupShader(HorizontalBlurShader shader, int targetFboWidth){
		shader.start();
		shader.loadTargetWidth(targetFboWidth);
		shader.stop();
	}
}
