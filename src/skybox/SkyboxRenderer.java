package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;

import entities.Camera;
import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.VertexAttrib;

public class SkyboxRenderer {
	private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	
	private final String[] DAY_SKY = {"right", "left", "top", "bottom", "back", "front" };
	private final String[] NIGHT_SKY = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront" };
	
	
	private RawModel _cube;
	private int _TBO_day;
	private int _TBO_night;
	private SkyboxShader _shader;
	
	private float time = 0;
	
	public SkyboxRenderer(Loader loader, SkyboxShader shader, Matrix4f projectionMatrix){
		_cube = loader.loadToVAO(VERTICES, 3);
		_TBO_day = loader.loadCubeMap(DAY_SKY);
		_TBO_night = loader.loadCubeMap(NIGHT_SKY);
		
		_shader = shader;
		_shader.start();
		_shader.loadProjectionMatrix(projectionMatrix);
		_shader.connectCubeMaps();
		_shader.stop();
	}
	
	public void render(Camera camera){
		GL30.glBindVertexArray(_cube.getVAO());
		GL20.glEnableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		
		dayNightCycle();
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, _cube.getVertexCount());
		
		GL20.glDisableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		GL30.glBindVertexArray(0);
	}
	
	private void dayNightCycle(){
		time += DisplayManager.getFrameTimeSeconds() * 1000;
		time %= 24000;
		int texture1;
		int texture2;
		float blendFactor;		
		if(time >= 0 && time < 5000){
			texture1 = _TBO_night;
			texture2 = _TBO_night;
			blendFactor = (time - 0)/(5000 - 0);
		}else if(time >= 5000 && time < 8000){
			texture1 = _TBO_night;
			texture2 = _TBO_day;
			blendFactor = (time - 5000)/(8000 - 5000);
		}else if(time >= 8000 && time < 21000){
			texture1 = _TBO_day;
			texture2 = _TBO_day;
			blendFactor = (time - 8000)/(21000 - 8000);
		}else{
			texture1 = _TBO_day;
			texture2 = _TBO_night;
			blendFactor = (time - 21000)/(24000 - 21000);
		}
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		
		_shader.loadBlendFactor(blendFactor);
	}
}
