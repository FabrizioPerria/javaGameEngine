package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.TerrainSet;

public class Player extends Entity {

	private static final float RUN_SPEED = 40;		// units/sec
	private static final float TURN_SPEED = 160;	//	degrees/sec
	public static final float GRAVITY =  -50;
	private static final float JUMP_POWER = 18;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;
	
	public Player(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(model, position, rotation, scale);
	}

	public void move(TerrainSet terrains){
		checkInputs();
		super.increaseRotation(new Vector3f(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0));
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float)(distance * Math.sin(Math.toRadians(super.getRotation().y)));
		float dz = (float)(distance * Math.cos(Math.toRadians(super.getRotation().y)));

		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();

		super.increasePosition(new Vector3f(dx, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), dz));
	
		try {
			Terrain terrain = terrains.getTerrainByCoord(super.getPosition().x, super.getPosition().z);
			float terrainHeight = terrain.getTerrainHeight(super.getPosition().x, super.getPosition().z);
			if(super.getPosition().y < terrainHeight){
				isInAir = false;
				upwardsSpeed = 0;
				super.getPosition().y = terrainHeight;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void jump(){
		if(!isInAir){
			upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	private void checkInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			currentSpeed = RUN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			currentSpeed = -RUN_SPEED;
		} else {
			currentSpeed = 0;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			currentTurnSpeed = -TURN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			currentTurnSpeed = TURN_SPEED;
		} else {
			currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
	}
}
