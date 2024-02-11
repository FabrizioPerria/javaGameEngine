package terrains;

import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import models.RawModel;
import renderEngine.MasterRenderer;
import renderEngine.VertexAttrib;
import toolbox.Maths;

public class TerrainRenderer {
	private TerrainShader _shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix){
		_shader = shader;

		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadAmbientLightAmount();
		shader.loadFog();
		shader.connectTextureUnits();
		shader.stop();
	}
	
	public void render(List<Terrain>terrains, Matrix4f toShadowMapSpace){
		_shader.loadToShadowMapSpace(toShadowMapSpace);
		for(Terrain terrain : terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}
	
	private void prepareTerrain(Terrain terrain){
		RawModel model = terrain.getModel();
		GL30.glBindVertexArray(model.getVAO());
		GL20.glEnableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		GL20.glEnableVertexAttribArray(VertexAttrib.TEXTURE_COORDINATE.ordinal());
		GL20.glEnableVertexAttribArray(VertexAttrib.NORMAL.ordinal());
		
		bindTextures(terrain);
		
		_shader.loadSpecularAttributes(0,1);
	}
	
	private void bindTextures(Terrain terrain) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getTexturePack().getBackgroundTexture().getID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getTexturePack().getRedTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getTexturePack().getGreenTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getTexturePack().getBlueTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getID());
	}
	
	private void unbindTexturedModel(){
		MasterRenderer.enableFaceCulling();
		GL20.glDisableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		GL20.glDisableVertexAttribArray(VertexAttrib.TEXTURE_COORDINATE.ordinal());
		GL20.glDisableVertexAttribArray(VertexAttrib.NORMAL.ordinal());
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix (Terrain terrain){
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), new Vector3f(0,0,0), new Vector3f(1,1,1));
		_shader.loadTransformationMatrix(transformationMatrix);
	}

}
