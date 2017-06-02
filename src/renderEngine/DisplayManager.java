package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS = 60;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay(){
		try {
			ContextAttribs attribs = new ContextAttribs(3, 3)
			.withForwardCompatible(true)
			.withProfileCore(true);
			
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat().withDepthBits(24), attribs);
			Display.setTitle("Game Engine");
			
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		//use the whole display
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTimeInMS();
	}
	
	public static void updateDisplay(){
		Display.sync(FPS);
		Display.update();
		long currentFrameTime = getCurrentTimeInMS();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds(){
		return delta;
	}
	
	private static long getCurrentTimeInMS(){
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
	
	public static void closeDisplay(){
		Display.destroy();
	}
}
