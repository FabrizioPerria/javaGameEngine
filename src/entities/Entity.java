package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity {
	private TexturedModel _model;
	private Vector3f _position;
	private Vector3f _rotation;
	private Vector3f _scale;
	
	private int _textureIndex;
	
	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale){
		this(model, 0, position, rotation, scale);
	}
	
	public Entity(TexturedModel model, int textureIndex, Vector3f position, Vector3f rotation, Vector3f scale){
		_model = model;
		_position = position;
		_rotation = rotation;
		_scale = scale;
		_textureIndex = textureIndex;
	}

	public TexturedModel getTexturedModel() {
		return _model;
	}

	public Vector3f getPosition() {
		return _position;
	}

	public Vector3f getRotation() {
		return _rotation;
	}

	public Vector3f getScale() {
		return _scale;
	}
	
	public void setTexturedModel(TexturedModel model) {
		_model = model;
	}

	public void setPosition(Vector3f position) {
		_position = position;
	}

	public void setRotation(Vector3f rotation) {
		_rotation = rotation;
	}

	public void setScale(Vector3f scale) {
		_scale = scale;
	}
	
	public void increasePosition(Vector3f delta){
		Vector3f.add(_position, delta, _position);
	}
	
	public void increaseRotation(Vector3f delta){
		Vector3f.add(_rotation, delta, _rotation);
	}
	
	public void increaseScale(Vector3f delta){
		Vector3f.add(_scale, delta, _scale);
	}
	
	public float getTextureXOffset(){
		int column = _textureIndex % _model.getTexture().getNumberofRows();
		return (float)column / (float)_model.getTexture().getNumberofRows();
	}
	
	public float getTextureYOffset(){
		int row = _textureIndex % _model.getTexture().getNumberofRows();
		return (float)row / (float)_model.getTexture().getNumberofRows();
	}
}
