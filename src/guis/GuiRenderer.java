package guis;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.RawModel;
import renderEngine.Loader;
import renderEngine.VertexAttrib;
import toolbox.Maths;

public class GuiRenderer {
	private final RawModel quad;
	private GuiShader _shader;
	
	public GuiRenderer(Loader loader, GuiShader shader){
		float[] quadPositions = { 	-1,  1,
									-1, -1,
									 1,  1,
									 1, -1 };
		
		quad = loader.loadToVAO(quadPositions, 2);	
		_shader = shader;
	}
	
	public void render(List<GuiTexture> guis){
		_shader.start();
		GL30.glBindVertexArray(quad.getVAO());
		GL20.glEnableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		
		for(GuiTexture gui : guis){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTBO());
			
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
			_shader.loadTransformation(transformationMatrix);
			
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		GL20.glDisableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		GL30.glBindVertexArray(0);
		_shader.stop();
	}
	
	public void cleanUp(){
		_shader.cleanUp();
	}
}
