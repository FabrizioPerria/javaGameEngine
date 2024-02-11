package postProcessing.contrast;

import postProcessing.PostProcessingFilter;

public class ContrastChanger extends PostProcessingFilter{
	
	public ContrastChanger(){
		super();
		shader = new ContrastShader();
		setupShader((ContrastShader)shader);
	}
	
	public ContrastChanger(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight);
		shader = new ContrastShader();
		setupShader((ContrastShader)shader);
	}
	
	private void setupShader(ContrastShader shader){
		shader.start();
		shader.loadContrastAmount(0.3f);
		shader.stop();
	}
}
