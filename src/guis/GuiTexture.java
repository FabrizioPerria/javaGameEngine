package guis;

import org.lwjgl.util.vector.Vector2f;

public class GuiTexture {
	private int _TBO;
	private Vector2f _position;
	private Vector2f _scale;
	
	public GuiTexture(int TBO, Vector2f position, Vector2f scale) {
		_TBO = TBO;
		_position = position;
		_scale = scale;
	}

	public int getTBO() {
		return _TBO;
	}

	public Vector2f getPosition() {
		return _position;
	}

	public Vector2f getScale() {
		return _scale;
	}
	
	
}
