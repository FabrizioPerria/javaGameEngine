package normalMappingRenderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import ambient.Fog;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.MasterRenderer;
import renderEngine.VertexAttrib;
import shadows.ShadowBox;
import shadows.ShadowMapMasterRenderer;
import textures.ModelTexture;
import toolbox.Maths;

public class NormalMappingRenderer {

	private NormalMappingShader shader;

	public NormalMappingRenderer(NormalMappingShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadAmbientLightAmount();
		shader.loadFog();
		shader.loadShadowProperties(ShadowBox.SHADOW_DISTANCE, 10f, ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<Entity>> entities, Matrix4f toShadowMapSpace) {
		shader.loadToShadowMapSpace(toShadowMapSpace);
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getModel();
		GL30.glBindVertexArray(rawModel.getVAO());
		GL20.glEnableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		GL20.glEnableVertexAttribArray(VertexAttrib.TEXTURE_COORDINATE.ordinal());
		GL20.glEnableVertexAttribArray(VertexAttrib.NORMAL.ordinal());
		GL20.glEnableVertexAttribArray(VertexAttrib.TANGENT.ordinal());
		
		ModelTexture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumberofRows());
		
		if (texture.hasTransparency()) {
			MasterRenderer.disableFaceCulling();
		}
		
		shader.loadFakeLight(texture.doFakeLight());
		
		shader.loadSpecularAttributes(texture.getReflectivity(), texture.getShineDamper());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTBO());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
		
		if(texture.hasSpecularMap()){
			GL13.glActiveTexture(GL13.GL_TEXTURE6);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getSpecularMap());
		}
			
	}

	private void unbindTexturedModel() {
		MasterRenderer.enableFaceCulling();
		GL20.glDisableVertexAttribArray(VertexAttrib.POSITION.ordinal());
		GL20.glDisableVertexAttribArray(VertexAttrib.TEXTURE_COORDINATE.ordinal());
		GL20.glDisableVertexAttribArray(VertexAttrib.NORMAL.ordinal());
		GL20.glDisableVertexAttribArray(VertexAttrib.TANGENT.ordinal());
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadTextureOfsets(new Vector2f(entity.getTextureXOffset(), entity.getTextureYOffset()));
	}
}
