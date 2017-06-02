package font;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import renderEngine.VertexAttrib;

public class FontRenderer {

	private FontShader _shader;

	public FontRenderer() {
		_shader = new FontShader();
	}

	public void cleanUp(){
		_shader.cleanUp();
	}
	
	private void prepare(){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		_shader.start();
	}
	
	private void renderText(GUIText text){
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		GL20.glEnableVertexAttribArray(VertexAttrib.TEXTURE_COORDINATE.ordinal());
		_shader.loadFontColor(text.getColour());
		_shader.setTranslation(text.getPosition());

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());

		GL20.glDisableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		GL20.glDisableVertexAttribArray(VertexAttrib.TEXTURE_COORDINATE.ordinal());
		GL30.glBindVertexArray(0);
	}
	
	private void endRendering(){
		_shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public void render(Map<FontType, List<GUIText>> texts){
		prepare();
		for(FontType font : texts.keySet()){
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for(GUIText text : texts.get(font)){
				_shader.loadEdgeParameters(0.5f, 0.1f);
				_shader.loadOutline(new Vector3f(0,0,0), new Vector2f(0.006f, 0.006f), 0.6f, 0.1f);
				renderText(text);
			}
		}
		endRendering();
	}
	
}
