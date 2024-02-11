package engineTester;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJFileLoader;
import terrains.Terrain;
import terrains.TerrainSet;
import terrains.TerrainTexturePack;
import textures.ModelTexture;
import textures.TerrainTexture;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import font.TextMaster;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particlesInstance.ParticleInstancedMaster;
import particlesInstance.ParticleInstancedSystem;
import particlesInstance.ParticleInstancedTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		
		Loader loader= new Loader();
		
		RawModel playerModel = loader.loadToVAO(OBJFileLoader.loadOBJ("person"));
		ModelTexture playerTexture = new ModelTexture(loader.loadTexture("playerTexture"));
		playerTexture.setShineDamper(10);
		playerTexture.setReflectivity(2);
		TexturedModel person = new TexturedModel(playerModel, playerTexture);
		Player player = new Player(person, new Vector3f(-280,0,-640), new Vector3f(),  new Vector3f(0.6f, 0.6f, 0.6f));

		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera); 
		
		TextMaster.init(loader);
		
		FontType font = new FontType("candara", loader);
		GUIText text = new GUIText("Yo! I am a text rendered in the game loop", 1, font, new Vector2f(0, 0), 1f, true);
		text.setColour(1.0f, 0, 0);
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		TerrainTexturePack terrainTextures = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		TerrainSet terrains = new TerrainSet();
		for (int i = -4; i < 4; i++) {
			for(int j = 1; j < 5; j++) {
				terrains.loadTerrain(new Terrain(i,-j,loader, terrainTextures, blendMap, "heightmapNew"));
			}
		}

		RawModel treeModel = loader.loadToVAO(OBJFileLoader.loadOBJ("tree"));
		ModelTexture treeTexture = new ModelTexture(loader.loadTexture("tree"));
		treeTexture.setShineDamper(1);
		treeTexture.setReflectivity(0);
		TexturedModel tree = new TexturedModel(treeModel, treeTexture);
		
		RawModel pineModel = loader.loadToVAO(OBJFileLoader.loadOBJ("pine"));
		ModelTexture pineTexture = new ModelTexture(loader.loadTexture("pine"));
		pineTexture.setShineDamper(1);
		pineTexture.setReflectivity(0);
		TexturedModel pine = new TexturedModel(pineModel, pineTexture);
		
		RawModel cherryModel = loader.loadToVAO(OBJFileLoader.loadOBJ("cherry"));
		ModelTexture cherryTexture = new ModelTexture(loader.loadTexture("cherry"));
		cherryTexture.setShineDamper(1);
		cherryTexture.setReflectivity(0);
		TexturedModel cherry = new TexturedModel(cherryModel, cherryTexture);
		
		RawModel grassModel = loader.loadToVAO(OBJFileLoader.loadOBJ("grassModel"));
		ModelTexture grassTexture = new ModelTexture(loader.loadTexture("grassTexture"));
		grassTexture.setShineDamper(1);
		grassTexture.setReflectivity(0);
		grassTexture.setTransparency(true);
		grassTexture.setFakeLightning(true);
		TexturedModel grass = new TexturedModel(grassModel, grassTexture);
		
		RawModel fernModel = loader.loadToVAO(OBJFileLoader.loadOBJ("fern"));
		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
		fernTexture.setNumberOfRows(2);
		fernTexture.setShineDamper(1);
		fernTexture.setReflectivity(0);
		fernTexture.setTransparency(true);
		fernTexture.setFakeLightning(true);
		TexturedModel fern = new TexturedModel(fernModel, fernTexture);
		
		RawModel lampModel = loader.loadToVAO(OBJFileLoader.loadOBJ("lamp"));
		ModelTexture lampTexture = new ModelTexture(loader.loadTexture("lamp"));
		lampTexture.setShineDamper(1);
		lampTexture.setReflectivity(0);
		lampTexture.setTransparency(true);
		lampTexture.setFakeLightning(true);
		TexturedModel lamp = new TexturedModel(lampModel, lampTexture);
		
		RawModel lanternModel = loader.loadToVAO(OBJFileLoader.loadOBJ("lantern"));
		ModelTexture lanternTexture = new ModelTexture(loader.loadTexture("lantern"));
		lanternTexture.setShineDamper(1);
		lanternTexture.setReflectivity(0);
		lanternTexture.setTransparency(true);
		lanternTexture.setFakeLightning(true);
		TexturedModel lantern = new TexturedModel(lanternModel, lanternTexture);
		
		Vector3f rotation = new Vector3f(0,0,0);
		Vector3f scaleT3 = new Vector3f(3,3,3);
		Vector3f scaleT1 = new Vector3f(1,1,1);
		Vector3f scaleT06 = new Vector3f(0.6f, 0.6f, 0.6f);
		Vector3f scaleT02 = new Vector3f(0.2f, 0.2f, 0.2f);

		List<Entity> entities = new ArrayList<Entity>();
		entities.add(player);

		entities.add(new Entity(lamp, new Vector3f(-281, terrains.getHeight(-281, -680), -680), new Vector3f(), scaleT1));
		entities.add(new Entity(lamp, new Vector3f(-350, terrains.getHeight(-350, -700), -700), new Vector3f(), scaleT1));
		entities.add(new Entity(lamp, new Vector3f(-300, terrains.getHeight(-300, -705), -705), new Vector3f(), scaleT1));
		
		entities.add(new Entity(lamp, new Vector3f(-340, terrains.getHeight(-340, -720), -720), new Vector3f(), scaleT1));
		entities.add(new Entity(lamp, new Vector3f(-460, terrains.getHeight(-460, -250), -250), new Vector3f(), scaleT1));
		
		Entity lampEntity = new Entity(lamp, new Vector3f(360, terrains.getHeight(360, -255), -255), new Vector3f(), scaleT1);
		entities.add(lampEntity);
		
		Random random = new Random();

		for(int i=0;i<100;i++){
			entities.add(new Entity(cherry, getNextPosition(random, terrains), rotation, scaleT3));
			entities.add(new Entity(pine, getNextPosition(random, terrains), rotation, scaleT1));
			
			for(int j = 0; j < 20; j++) {
				entities.add(new Entity(grass, getNextPosition(random, terrains), rotation, scaleT1));
				entities.add(new Entity(fern, random.nextInt(4), getNextPosition(random, terrains), rotation, scaleT06));
			}
		}
		
		List<Entity> normalMapEntities = new ArrayList<Entity>();

		ModelTexture barrelTexture = new ModelTexture(loader.loadTexture("barrel"));
		barrelTexture.setShineDamper(10);
		barrelTexture.setReflectivity(0.5f);
		barrelTexture.setNormalMap(loader.loadTexture("barrelNormal"));
		TexturedModel barrel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), barrelTexture);
		
		ModelTexture crateTexture = new ModelTexture(loader.loadTexture("crate"));
		crateTexture.setShineDamper(10);
		crateTexture.setReflectivity(0.5f);
		crateTexture.setNormalMap(loader.loadTexture("crateNormal"));
		TexturedModel crate = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader), crateTexture);
		
		normalMapEntities.add(new Entity(barrel, new Vector3f(-240, 20, -600), new Vector3f(), scaleT3));
		normalMapEntities.add(new Entity(crate, new Vector3f(-360, 30, -650), new Vector3f(), scaleT02));

		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(1000,10000,-30000), new Vector3f(0.8f,0.8f,0.8f))); // SUN
		
		lights.add(new Light(new Vector3f(-281,terrains.getHeight(-281, -680)+14,-680), new Vector3f(1,0,0), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(-350,terrains.getHeight(-350, -700)+14, -700), new Vector3f(0,1,1), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(-300,terrains.getHeight(-300, -705)+14,-705), new Vector3f(0,1,0), new Vector3f(1, 0.01f, 0.002f)));
		
		lights.add(new Light(new Vector3f(-340,terrains.getHeight(-340, -720)+14,-720), new Vector3f(1,0,0), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(460,terrains.getHeight(460, -250)+14, -250), new Vector3f(0,1,1), new Vector3f(1, 0.01f, 0.002f)));
		Light light = new Light(new Vector3f(360,terrains.getHeight(360, -255)+14,-255), new Vector3f(0,1,0), new Vector3f(1, 0.01f, 0.002f));
		lights.add(light);
		
		ParticleInstancedMaster.init(loader, renderer.getProjectionMatrix());
		
		List<GuiTexture> guis = new ArrayList<>();
		guis.add(new GuiTexture(loader.loadTexture("health"), new Vector2f(0.8f, -0.9f), new Vector2f(0.15f, 0.25f)));
		
		// GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapID(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		//guis.add(shadowMap);
		
		List<WaterTile> waterTiles = new ArrayList<>();
		waterTiles.add(new WaterTile(-280,-475, -1.5f));
		WaterFrameBuffers waterFBOs = new WaterFrameBuffers();
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

		Vector4f reflectionClippingPlane = new Vector4f(0,1,0,-waterTiles.get(0).getHeight()+1);
		Vector4f refractionClippingPlane = new Vector4f(0,-1,0,waterTiles.get(0).getHeight());
		Vector4f sceneClippingPlane = new Vector4f(0,-1,0,500);
		ParticleInstancedTexture starTexture = new ParticleInstancedTexture(loader.loadTexture("particleStar"), 1);
		ParticleInstancedTexture fireTexture = new ParticleInstancedTexture(loader.loadTexture("particleAtlas"), 4);

		ParticleInstancedSystem particleSystemFire = new ParticleInstancedSystem(fireTexture, 500, 25, 0.3f, 1, 1);
		particleSystemFire.randomizeRotation();
		particleSystemFire.setDirection(new Vector3f(0,1,0), 0.1f);
		particleSystemFire.setLifeError(0.1f);
		particleSystemFire.setSpeedError(0.4f);
		particleSystemFire.setScaleError(0.8f);
		ParticleInstancedSystem particleSystemStar = new ParticleInstancedSystem(starTexture, 5000, 25, 0.3f, 4, 1);
		particleSystemStar.randomizeRotation();
		particleSystemStar.setDirection(new Vector3f(0,1,0), 0.1f);
		particleSystemStar.setLifeError(0.1f);
		particleSystemStar.setSpeedError(0.4f);
		particleSystemStar.setScaleError(0.8f);
		Vector3f starPosition = new Vector3f(-300, 50, -700);
		Vector3f firePosition = new Vector3f(-350, terrains.getHeight(-350, -750), -750);
		
		Fbo outputFbo = new Fbo(2 * DisplayManager.getWindowWidth(), 2 *DisplayManager.getWindowHeight(), Fbo.DEPTH_RENDER_BUFFER);
		PostProcessing.init(loader);
		
		while (!GLFW.glfwWindowShouldClose(DisplayManager.window)) {
			camera.move();
			player.move(terrains);
			picker.update();
			particleSystemStar.generateParticles(starPosition);

			particleSystemFire.generateParticles(firePosition);
			ParticleInstancedMaster.update(camera);
			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			if (terrainPoint != null) {
				//System.out.println(terrainPoint);
				lampEntity.setPosition(terrainPoint);
				light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 14, terrainPoint.z));
			}
			renderer.renderShadowMap(player, entities, normalMapEntities, lights.get(0));

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			waterFBOs.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waterTiles.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(player, entities, normalMapEntities, terrains, waterTiles, waterFBOs, lights, camera,
					reflectionClippingPlane);

			camera.getPosition().y += distance;
			camera.invertPitch();
			waterFBOs.bindRefractionFrameBuffer();
			renderer.renderScene(player, entities, normalMapEntities, terrains, waterTiles, waterFBOs, lights, camera,
					refractionClippingPlane);

			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			waterFBOs.unbindCurrentFrameBuffer();

			outputFbo.bindFrameBuffer();
			renderer.renderScene(player, entities, normalMapEntities, terrains, waterTiles, waterFBOs, lights, camera,
					sceneClippingPlane);
			renderer.renderWater(waterTiles, waterFBOs, lights, camera);
			ParticleInstancedMaster.renderParticles(camera);
			outputFbo.unbindFrameBuffer();
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(), 0);//, outputFbo.getBrightTexture());
			renderer.renderGui(guis);

			TextMaster.render();
			DisplayManager.updateDisplay();
		}
		
		PostProcessing.cleanUp();
		outputFbo.cleanUp();
		ParticleInstancedMaster.cleanUp();
		TextMaster.cleanUp();
		waterFBOs.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();

	}
	
	private static Vector3f getNextPosition(Random random, TerrainSet terrains) {
		float wX = random.nextFloat()*2 * Terrain.SIZE - Terrain.SIZE;
		float wZ = random.nextFloat() * -Terrain.SIZE;
		float wY = terrains.getHeight(wX, wZ);
		return new Vector3f(wX, wY, wZ);

	}
}
