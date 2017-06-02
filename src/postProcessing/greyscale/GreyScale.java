//package postProcessing.greyscale;
//
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GL13;
//
//import postProcessing.ImageRenderer;
//import postProcessing.gaussianBlur.horizontal.HorizontalBlurShader;
//
//public class GreyScale {
//	private ImageRenderer imageRenderer;
//	private GreyScaleShader shader;
//	
//	public GreyScale(){
//		shader = new GreyScaleShader();
//		imageRenderer = new ImageRenderer();	//render to screen
//	}
//	
//	public GreyScale(int targetFboWidth, int targetFboHeight){
//		shader = new GreyScaleShader();
//		imageRenderer = new ImageRenderer(targetFboWidth, targetFboHeight);
//	}	
//	
//	public void render(int TBO){
//		shader.start();
//		
//		GL13.glActiveTexture(GL13.GL_TEXTURE0);
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TBO);
//		
//		imageRenderer.renderQuad();
//		
//		shader.stop();
//	}
//	
//	public int getOutputTexture(){
//		return imageRenderer.getOutputTexture();
//	}	
//	
//	public void cleanUp(){
//		imageRenderer.cleanUp();
//		shader.cleanUp();
//	}
//}
