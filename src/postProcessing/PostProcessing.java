//package postProcessing;
//
//import org.lwjgl.opengl.Display;
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GL20;
//import org.lwjgl.opengl.GL30;
//
//import models.RawModel;
//import postProcessing.brightFilter.BrightFilter;
//import postProcessing.combineFilter.CombineFilter;
//import postProcessing.contrast.ContrastChanger;
//import postProcessing.gaussianBlur.horizontal.HorizontalBlur;
//import postProcessing.gaussianBlur.vertical.VerticalBlur;
//import postProcessing.greyscale.GreyScale;
//import postProcessing.passThrough.PassThrough;
//import renderEngine.Loader;
//
//public class PostProcessing {
//	
//	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
//	private static RawModel quad;
//	private static PassThrough passthrough;
//	private static ContrastChanger contrastChanger;
//	private static GreyScale greyscaler;
//	private static HorizontalBlur horizontalBlur;
//	private static VerticalBlur verticalBlur;
//	private static HorizontalBlur horizontalBlur2;
//	private static VerticalBlur verticalBlur2;
//	private static BrightFilter brightFilter;
//	private static CombineFilter combineFilter;
//
//	public static void init(Loader loader){
//		quad = loader.loadToVAO(POSITIONS, 2);
//		passthrough = new PassThrough();
//		
//		contrastChanger = new ContrastChanger();		//the contrast is the last one so it renders to screen
//		
//		greyscaler = new GreyScale(Display.getWidth(), Display.getHeight());
//		
//		horizontalBlur = new HorizontalBlur(Display.getWidth()/2, Display.getHeight()/2);
//		verticalBlur = new VerticalBlur(Display.getWidth()/2, Display.getHeight()/2);
//		horizontalBlur2 = new HorizontalBlur(Display.getWidth()/4, Display.getHeight()/4);
//		verticalBlur2= new VerticalBlur(Display.getWidth()/4, Display.getHeight()/4);
//		
//		brightFilter = new BrightFilter(Display.getWidth() / 2, Display.getHeight() / 2);
//		combineFilter = new CombineFilter();
//	}
//	
//	public static void doPostProcessing(int colourTexture, int brightTexture){
//		start();
////		passthrough.render(colourTexture);
////		brightFilter.render(colourTexture);
//		horizontalBlur2.render(brightTexture);
//		verticalBlur2.render(horizontalBlur2.getOutputTexture());
//		combineFilter.render(colourTexture, verticalBlur2.getOutputTexture());
//		
////		horizontalBlur.render(colourTexture);
////		verticalBlur.render(horizontalBlur.getOutputTexture());
////		horizontalBlur2.render(verticalBlur.getOutputTexture());
////		verticalBlur2.render(horizontalBlur2.getOutputTexture());
////		greyscaler.render(verticalBlur2.getOutputTexture());
////		contrastChanger.render(greyscaler.getOutputTexture());
//		end();
//	}
//	
//	public static void cleanUp(){
//		brightFilter.cleanUp();
//		passthrough.cleanUp();
//		contrastChanger.cleanUp();
//		greyscaler.cleanUp();
//		horizontalBlur.cleanUp();
//		verticalBlur.cleanUp();
//	}
//	
//	private static void start(){
//		GL30.glBindVertexArray(quad.getVAO());
//		GL20.glEnableVertexAttribArray(0);
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
//	}
//	
//	private static void end(){
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
//		GL20.glDisableVertexAttribArray(0);
//		GL30.glBindVertexArray(0);
//	}
//
//
//}
