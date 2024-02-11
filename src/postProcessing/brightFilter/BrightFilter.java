package postProcessing.brightFilter;

import postProcessing.PostProcessingFilter;

public class BrightFilter extends PostProcessingFilter{

	public BrightFilter(int width, int height){
		super(width, height);
		shader = new BrightFilterShader();
	}
	
}
