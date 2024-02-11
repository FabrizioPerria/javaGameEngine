  package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import ambient.Fog;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiShader;
import guis.GuiTexture;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import normalMappingRenderer.NormalMappingShader;
import shadows.ShadowMapMasterRenderer;
import terrains.Terrain;
import terrains.TerrainShader;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import entities.EntityRenderer;
import entities.EntityShader;
import skybox.SkyboxRenderer;
import skybox.SkyboxShader;

import terrains.TerrainRenderer;
import terrains.TerrainSet;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MasterRenderer {
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 10000;
	
	private Matrix4f projectionMatrix;

	private EntityShader entityShader;
	private EntityRenderer entityRenderer;
	
	private TerrainShader terrainShader;
	private TerrainRenderer terrainRenderer;
	
	private GuiShader guiShader;
	private GuiRenderer guiRenderer;
	
	private SkyboxShader skyboxShader;
	private SkyboxRenderer skyboxRenderer;
	
	private Map<TexturedModel, List<Entity>> entities;
	private List<Terrain> terrains;
	private List<GuiTexture> guis;
	private Map<TexturedModel, List<Entity>> normalMapEntities;
	
	private WaterShader waterShader;
	private WaterFrameBuffers waterFBOs;
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
		
		guiShader = new GuiShader();
		guiRenderer = new GuiRenderer(loader, guiShader);
		guis = new ArrayList<>();
		
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
	
	private void processGui(GuiTexture gui) {
		guis.add(gui);
	}
	
	public void renderScene(Player player, List<Entity> entities, List<Entity> normalMapEntities, TerrainSet terrains,  List<WaterTile> water, WaterFrameBuffers waterFBO, List<Light> lights, Camera camera, Vector4f clipPlane){
		processEntity(player);
		
		for(Entity entity : entities)
			processEntity(entity);
		
		for(Entity entity : normalMapEntities)
			processNormalMapEntity(entity);

		for(int i = 0; i < terrains.getNumTerrains(); ++i)
			processTerrain(terrains.getTerrainByIndex(i));
		
		render(lights, camera, clipPlane);
	}
	
	public void renderGui(List<GuiTexture> guis) {
		for(GuiTexture gui : guis)
			processGui(gui);
		
		guiRenderer.render(this.guis);
		guis.clear();
	}

	public void renderWater(List<WaterTile> water, WaterFrameBuffers waterFBO, List<Light> lights, Camera camera) {

		if(water != null){
			waterShader.start();
			waterRenderer.render(water, camera, lights, waterFBO);
			waterShader.stop();
		}
	}
	
	private void render(List<Light> lights, Camera camera, Vector4f clipPlane){
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
		
		/* SKYBOX */
		skyboxShader.start();
		skyboxShader.loadViewMatrix(camera);
		skyboxShader.loadFogAttributes(Fog.LOWER_LIMIT, Fog.UPPER_LIMIT, Fog.FOG_COLOR);

		skyboxRenderer.render(camera);
		skyboxShader.stop();
		
		/* NORMAL MAP ENTITIES */
		normalMapShader.start();
		normalMapShader.loadSkyColor(Fog.FOG_COLOR);
		normalMapShader.loadViewMatrix(camera);
		normalMapShader.loadLights(lights, camera);
		normalMapShader.loadClipPlane(clipPlane);
		normalMapRenderer.render(normalMapEntities);//, shadowMapRenderer.getToShadowMapSpaceMatrix());
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
		guiShader.cleanUp();
		skyboxShader.cleanUp();
		waterShader.cleanUp();
		normalMapShader.cleanUp();
		shadowMapRenderer.cleanUp();
	}

	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL11.glClearColor(Fog.FOG_COLOR.x, Fog.FOG_COLOR.y,Fog.FOG_COLOR.z, 1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapRenderer.getShadowMap());	
		
	}
	
	private void createProjectionMatrix() {
		float windowWidth = (float) DisplayManager.getWindowWidth();
		float windowHeight = (float) DisplayManager.getWindowHeight();
	    projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(FOV), windowWidth / windowHeight, NEAR_PLANE, FAR_PLANE);
	}
	
	public Matrix4f getProjectionMatrix(){
		return projectionMatrix;
	}
}
