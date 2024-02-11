package renderEngine;

import org.lwjgl.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import toolbox.Keyboard;
import toolbox.Mouse;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class DisplayManager {
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS = 60;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static long window;
	
    private static Keyboard keyboard = new Keyboard();

	
	public static void createDisplay(){
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW");
        }
        
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "3D Game engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Could not create the window handle");
        }
        
        GLFW.glfwSetKeyCallback(window, keyboard);
        Mouse.createCallbacks();
        
        try (MemoryStack stack = stackPush()) {
            var pWidth = stack.mallocInt(1);
            var pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            var vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2
            );
        } 

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(0);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL13.GL_MULTISAMPLE);
        
        lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay(){
		GLFW.glfwSwapBuffers(window);
		glFlush();
        GLFW.glfwPollEvents();
        long currentTimeFrame = getCurrentTime();
        delta = (currentTimeFrame - lastFrameTime) / 1000.0f;
        lastFrameTime = currentTimeFrame;
	}
	
    public static int getWindowWidth() {
        var  w = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetWindowSize(window, w, null);
        return w.get(0);
    }

    public static int getWindowHeight() {
        var h = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetWindowSize(window, null, h);
        return h.get(0);
    }

    public static void closeDisplay() { 
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
    
    public static long getCurrentTime() {
        return (long) (GLFW.glfwGetTime() * 1000);
    }
    
    public static float getFrameTimeSeconds() {
    	return delta;
    }
    
    public static long getWindow() {
    	return window;
    }
}
