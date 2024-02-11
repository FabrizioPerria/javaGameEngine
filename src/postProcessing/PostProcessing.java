package postProcessing;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import models.RawModel;
import postProcessing.brightFilter.BrightFilter;
import postProcessing.combineFilter.CombineFilter;
import postProcessing.contrast.ContrastChanger;
import postProcessing.gaussianBlur.horizontal.HorizontalBlur;
import postProcessing.gaussianBlur.vertical.VerticalBlur;
import postProcessing.greyscale.GreyScale;
import postProcessing.passThrough.PassThrough;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static PassThrough passthrough;
	private static PassThrough finalPass;
	private static ContrastChanger contrastChanger;
	private static GreyScale greyscaler;
	private static HorizontalBlur horizontalBlur;
	private static VerticalBlur verticalBlur;
	private static HorizontalBlur horizontalBlur2;
	private static VerticalBlur verticalBlur2;
	private static BrightFilter brightFilter;
	private static CombineFilter combineFilter;

	private static List<PostProcessingFilter> filters = new ArrayList<PostProcessingFilter>();
	
	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		passthrough = new PassThrough(DisplayManager.getWindowWidth(), DisplayManager.getWindowHeight());
		
		contrastChanger = new ContrastChanger(DisplayManager.getWindowWidth(), DisplayManager.getWindowHeight());		//the contrast is the last one so it renders to screen
		
		greyscaler = new GreyScale(DisplayManager.getWindowWidth(), DisplayManager.getWindowHeight());
		
		horizontalBlur = new HorizontalBlur(DisplayManager.getWindowWidth()/2, DisplayManager.getWindowHeight()/2);
		verticalBlur = new VerticalBlur(DisplayManager.getWindowWidth()/2, DisplayManager.getWindowHeight()/2);
		horizontalBlur2 = new HorizontalBlur(DisplayManager.getWindowWidth()/4, DisplayManager.getWindowHeight()/4);
		verticalBlur2= new VerticalBlur(DisplayManager.getWindowWidth()/4, DisplayManager.getWindowHeight()/4);
		
		brightFilter = new BrightFilter(DisplayManager.getWindowWidth() / 2, DisplayManager.getWindowHeight() / 2);
		combineFilter = new CombineFilter();
		combineFilter.setHighlightTexture(brightFilter.getOutputTexture());
		finalPass = new PassThrough();
		
		// filters.add(horizontalBlur);
		// filters.add(verticalBlur);
		// filters.add(horizontalBlur2);
		// filters.add(verticalBlur2);
		// filters.add(brightFilter);
		// filters.add(combineFilter);
		filters.add(greyscaler);
		filters.add(contrastChanger);
	}
	
	public static void doPostProcessing(int colourTexture, int brightTexture){
		start();
		
		for (PostProcessingFilter filter : filters) {
			filter.render(colourTexture);
			colourTexture = filter.getOutputTexture();
		}

		finalPass.render(colourTexture);
		end();
		// finalPass.cleanUp();
	}
	
	public static void cleanUp(){
		for (PostProcessingFilter filter : filters) {
			filter.cleanUp();
		}
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVAO());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
