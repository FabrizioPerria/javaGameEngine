package entities;

import java.io.ObjectOutputStream.PutField;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import toolbox.Keyboard;
import toolbox.Mouse;

import org.lwjgl.*;


public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	
	private Vector3f position = new Vector3f(0,0.2f,0);
	
	private float pitch=10;
	private float yaw;
	private float roll;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
	}

	public void move()
	{
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotation().y + angleAroundPlayer);
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public void invertPitch() {
		pitch = -pitch;
	}
	
	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance)
	{
		float theta = player.getRotation().y + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetY = 4.0f;
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance + offsetY;
	}
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom() {
		float zoomLimit = 4.0f;
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		if(distanceFromPlayer > zoomLevel + zoomLimit)
			distanceFromPlayer -= zoomLevel;		
		Mouse.update();
	}
	
	private void calculatePitch() {
		if(Mouse.isLeftButtonPressed()) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
 		}
	}
	
	private void calculateAngleAroundPlayer()
	{
		if(Mouse.isLeftButtonPressed()) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
	
}