package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import ambient.Fog;
import entities.Camera;
import entities.Entity;
import entities.EntityRenderer;
import entities.EntityShader;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import normalMappingRenderer.NormalMappingShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import skybox.SkyboxShader;
import terrains.Terrain;
import terrains.TerrainRenderer;
import terrains.TerrainSet;
import terrains.TerrainShader;
import water.WaterFrameBuffer;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MasterRenderer {
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;

	private EntityShader entityShader;
	private EntityRenderer entityRenderer;
	
	private TerrainShader terrainShader;
	private TerrainRenderer terrainRenderer;
	
	private SkyboxShader skyboxShader;
	private SkyboxRenderer skyboxRenderer;
	
	private Map<TexturedModel, List<Entity>> entities;
	private List<Terrain> terrains;
	private Map<TexturedModel, List<Entity>> normalMapEntities;
	
	private WaterShader waterShader;
	private WaterFrameBuffer waterFBOs;
	private WaterRenderer waterRenderer;
	
	private NormalMappingShader normalMapShader;
	private NormalMappingRenderer normalMapRenderer;
	
	private ShadowMapMasterRenderer shadowMapRenderer; 
	
	public MasterRenderer(Loader loader, Camera camera){
		enableFaceCulling();
		createProjectionMatrix();
		
		entityShader = new EntityShader();
		entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
		entities = new HashMap<>();
		
		terrainShader = new TerrainShader();
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		terrains = new ArrayList<>();
		
		waterShader = new WaterShader();
		waterRenderer = new WaterRenderer(loader, waterShader, projectionMatrix);
		
		skyboxShader = new SkyboxShader();
		skyboxRenderer = new SkyboxRenderer(loader, skyboxShader, projectionMatrix);
		
		normalMapShader = new NormalMappingShader();
		normalMapRenderer = new NormalMappingRenderer(normalMapShader, projectionMatrix);
		normalMapEntities = new HashMap<>();
		
		shadowMapRenderer = new ShadowMapMasterRenderer(camera);
	}
	
	public static void enableFaceCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableFaceCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	private void processEntity(Entity entity){
		TexturedModel model = entity.getTexturedModel();
		List<Entity> instances = entities.get(model);
		if(instances != null){
			instances.add(entity);
		} else {
			List<Entity> newList = new ArrayList<>();
			newList.add(entity);
			entities.put(model, newList);
		}
	}
	
	private void processNormalMapEntity(Entity entity){
		TexturedModel model = entity.getTexturedModel();
		List<Entity> instances = normalMapEntities.get(model);
		if(instances != null){
			instances.add(entity);
		} else {
			List<Entity> newList = new ArrayList<>();
			newList.add(entity);
			normalMapEntities.put(model, newList);
		}
	}
	
	private void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void renderScene(Player player, List<Entity> entities, List<Entity> normalMapEntities, TerrainSet terrains, List<WaterTile> water, WaterFrameBuffer waterFBO, List<Light> lights, Camera camera, Vector4f clipPlane){
		processEntity(player);
		
		for(Entity entity : entities)
			processEntity(entity);
		
		for(Entity entity : normalMapEntities)
			processNormalMapEntity(entity);
		
		for(int i = 0; i < terrains.getNumTerrains(); ++i)
			processTerrain(terrains.getTerrainByIndex(i));
		
		//rendering
		render(water, waterFBO, lights, camera, clipPlane);
	}
	
	private void render(List<WaterTile> water, WaterFrameBuffer waterFBO, List<Light> lights, Camera camera, Vector4f clipPlane){
		prepare();
		/* ENTITY */
		entityShader.start();
		entityShader.loadSkyColor(Fog.FOG_COLOR);
		entityShader.loadLights(lights);
		entityShader.loadViewMatrix(camera);
		entityShader.loadClipPlane(clipPlane);
		entityRenderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
		entityShader.stop();
		entities.clear();
		
		/* TERRAIN */
		terrainShader.start();
		terrainShader.loadSkyColor(Fog.FOG_COLOR);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadClipPlane(clipPlane);
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		terrains.clear();
		
		/* WATER */
		if(water != null){
			waterShader.start();
			waterShader.loadSkyColor(Fog.FOG_COLOR);
			waterShader.loadCameraParameters(camera);
			waterRenderer.render(water, camera, lights, waterFBO);
			waterShader.stop();
		}
		
		/* SKYBOX */
		skyboxShader.start();
		skyboxShader.loadViewMatrix(camera);
		skyboxShader.loadFogAttributes(Fog.LOWER_LIMIT, Fog.UPPER_LIMIT, Fog.FOG_COLOR);

		skyboxRenderer.render(camera);
		skyboxShader.stop();
		
		/* NORMAL MAP ENTITIES */
		normalMapShader.start();
		normalMapShader.loadSkyColor(Fog.FOG_COLOR);
		normalMapShader.loadLights(lights, camera);
		normalMapShader.loadViewMatrix(camera);
		normalMapShader.loadClipPlane(clipPlane);
		normalMapRenderer.render(normalMapEntities, shadowMapRenderer.getToShadowMapSpaceMatrix());
		normalMapShader.stop();
		normalMapEntities.clear();
	}

	public void renderShadowMap(Player player, List<Entity> shadowEntities, List<Entity> shadowNormalentities, Light sun){
		processEntity(player);
		
		for(Entity entity : shadowEntities){
			processEntity(entity);
		}
		
		for(Entity entity : shadowNormalentities){
			processEntity(entity);
		}
		
		shadowMapRenderer.render(entities, sun);
		entities.clear();
	}
	
	public int getShadowMapID(){
		return shadowMapRenderer.getShadowMap();
	}
	
	public void cleanUp(){
		entityShader.cleanUp();
		terrainShader.cleanUp();
		skyboxShader.cleanUp();
		normalMapShader.cleanUp();
		shadowMapRenderer.cleanUp();
	}

	private void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapRenderer.getShadowMap());	
		
	}
	
	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
 
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	public Matrix4f getProjectionMatrix(){
		return projectionMatrix;
	}
}
