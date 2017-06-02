package postProcessing.passThrough;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import postProcessing.ImageRenderer;
import postProcessing.gaussianBlur.horizontal.HorizontalBlurShader;
import postProcessing.greyscale.GreyScaleShader;

public class PassThrough {
	private ImageRenderer imageRenderer;
	private PassThroughShader shader;
	
	public PassThrough(){
		shader = new PassThroughShader();
		imageRenderer = new ImageRenderer();	//render to screen
	}
	
	public PassThrough(int targetFboWidth, int targetFboHeight){
		shader = new PassThroughShader();
		imageRenderer = new ImageRenderer(targetFboWidth, targetFboHeight);
	}	
	
	public void render(int TBO){
		shader.start();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TBO);
		
		imageRenderer.renderQuad();
		
		shader.stop();
	}
	
	public int getOutputTexture(){
		return imageRenderer.getOutputTexture();
	}	
	
	public void cleanUp(){
		imageRenderer.cleanUp();
		shader.cleanUp();
	}
}
