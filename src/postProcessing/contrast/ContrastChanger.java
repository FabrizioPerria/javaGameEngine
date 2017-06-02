//package postProcessing.contrast;
//
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GL13;
//
//import postProcessing.ImageRenderer;
//
//public class ContrastChanger {
//	private ImageRenderer imageRenderer;
//	private ContrastShader shader;
//	
//	public ContrastChanger(){
//		shader = new ContrastShader();
//		shader.start();
//		shader.loadContrastAmount(0.3f);
//		shader.stop();
//		imageRenderer = new ImageRenderer();	//render to screen
//	}
//	
//	public ContrastChanger(int targetFboWidth, int targetFboHeight){
//		shader = new ContrastShader();
//		shader.start();
//		shader.loadContrastAmount(0.3f);
//		shader.stop();
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
