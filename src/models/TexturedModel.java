package models;

import textures.ModelTexture;

public class TexturedModel {
	private RawModel _model;
	private ModelTexture _texture;
	
	public TexturedModel(RawModel model, ModelTexture texture){
		_model = model;
		_texture = texture;
	}

	public RawModel getModel() {
		return _model;
	}

	public ModelTexture getTexture() {
		return _texture;
	}
}
