package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import postProcessing.ImageRenderer;
import shaders.ShaderProgram;

public abstract class PostProcessingFilter {
	protected ImageRenderer imageRenderer;
	protected ShaderProgram shader;
	
	public PostProcessingFilter(){
		imageRenderer = new ImageRenderer();	//render to screen
	}
	
	public PostProcessingFilter(int targetFboWidth, int targetFboHeight){
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
