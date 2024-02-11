package postProcessing.combineFilter;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import postProcessing.PostProcessingFilter;

public class CombineFilter extends PostProcessingFilter {
	
	private int highlightTexture;

	public CombineFilter() {
		super();
		shader = new CombineShader();
		setupShader((CombineShader) shader);
	}
	
	public void setHighlightTexture(int highlightTexture) {
		this.highlightTexture = highlightTexture;
	}
	
	private void setupShader(CombineShader shader) {
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
	}
	
	@Override
	public void render(int colourTexture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		imageRenderer.renderQuad();
		shader.stop();
	}
}
