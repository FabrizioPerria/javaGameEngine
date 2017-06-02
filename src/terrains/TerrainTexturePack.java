package terrains;

import textures.TerrainTexture;

public class TerrainTexturePack {
	private TerrainTexture backgroundTexture;
	private TerrainTexture redTexture;
	private TerrainTexture greenTexture;
	private TerrainTexture blueTexture;
	
	public TerrainTexturePack(TerrainTexture bgTexture, TerrainTexture rTexture, TerrainTexture gTexture, TerrainTexture bTexture){
		backgroundTexture = bgTexture;
		redTexture = rTexture;
		greenTexture = gTexture;
		blueTexture = bTexture;
	}

	public TerrainTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public TerrainTexture getRedTexture() {
		return redTexture;
	}

	public TerrainTexture getGreenTexture() {
		return greenTexture;
	}

	public TerrainTexture getBlueTexture() {
		return blueTexture;
	}
}
