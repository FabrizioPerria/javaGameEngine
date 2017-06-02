package particlesInstance;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Player;
import renderEngine.DisplayManager;

public class ParticleInstanced {
	private Vector3f position;
	private Vector3f velocity;	//speed (length) and direction(direction) of the particle
	private float weightEffect; // the lo0wer this value, the lighter the particle will be
	private float lifeLength;
	private Vector3f rotation;
	private Vector3f scale;
	
	private ParticleInstancedTexture texture;
	private Vector2f currentStageOffset;
	private Vector2f nextStageOffset;
	private float blendFactor;
	
	private float distanceFromCamera;
	
	private float elapsedTime;
	
	private Vector3f tmp = new Vector3f();

	public ParticleInstanced(ParticleInstancedTexture texture, Vector3f position, Vector3f velocity, float weightEffect, float lifeLength, Vector3f rotation, Vector3f scale) {
		currentStageOffset = new Vector2f();
		nextStageOffset = new Vector2f();
		blendFactor = 0;
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.weightEffect = weightEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.elapsedTime = 0;
		ParticleInstancedMaster.addParticle(this);
	}
	
	public float getDistanceFromCamera(){
		return distanceFromCamera;
	}
	
	public Vector2f getCurrentStageOffset() {
		return currentStageOffset;
	}

	public Vector2f getNextStageOffset() {
		return nextStageOffset;
	}

	public float getBlendFactor() {
		return blendFactor;
	}

	public ParticleInstancedTexture getTexture(){
		return texture;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getScale() {
		return scale;
	}
	
	public boolean update(Camera camera){
		velocity.y += Player.GRAVITY * weightEffect * DisplayManager.getFrameTimeSeconds();
		tmp.set(velocity);
		tmp.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(tmp, position, position);
		distanceFromCamera = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		updateTextures();
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		return elapsedTime < lifeLength;
	}
	
	private void updateTextures(){
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfrows() * texture.getNumberOfrows();
		float atlasProgression = lifeFactor * stageCount;
		int indexCurrent = (int)Math.floor((float)atlasProgression);
		int indexNext = indexCurrent < (stageCount - 1) ? indexCurrent + 1 : indexCurrent;
		blendFactor = atlasProgression % 1;
		setTextureOffset(currentStageOffset, indexCurrent);
		setTextureOffset(nextStageOffset, indexNext);
	}
	
	private void setTextureOffset(Vector2f offset, int index){
		int column = index % texture.getNumberOfrows();
		int row = index / texture.getNumberOfrows();
		offset.x = (float)column / texture.getNumberOfrows();
		offset.y = (float)row / texture.getNumberOfrows();
	}
}
