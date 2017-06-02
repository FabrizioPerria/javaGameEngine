package textures;

public class TerrainTexture {
	
	private int TBO;

	public TerrainTexture(int textureID) {
		this.TBO = textureID;
	}

	
	public int getID() {
		return TBO;
	}

	
	public void setID(int textureID) {
		this.TBO = textureID;
	}
	

}
