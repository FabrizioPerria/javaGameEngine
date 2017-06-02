package textures;

import org.lwjgl.glfw.GLFW;

import toolbox.Keyboard;

public class ModelTexture {

	private int textureID;
	private int normalMap;
	
	private float shineDamper = 1;
	private float reflectivity = 1;
	
	private boolean _hasTransparency = false;
	private boolean _useFakeLightning = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int id) {
		this.textureID = id;
	}
		
	public boolean hasTransparency() {
		return _hasTransparency;
	}

	public void setTransparency(boolean hasTransparency) {
		this._hasTransparency = hasTransparency;
	}

	public int GetID()
	{
		return this.textureID;
	}
	
	public void setNormalMap(int id)
	{
		normalMap = id;
	}
	
	public int getNormalMap()
	{
		return this.normalMap;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}	
	
	public void setLightResource()
	{
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_9))
		{
			shineDamper += 0.1f;
		}
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_6))
		{
			shineDamper -= 0.1f;
		}
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_7))
		{
			reflectivity += 0.1f;
		}
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_4))
		{
			reflectivity -= 0.1f;
		}
	}

	public boolean useFakeLightning() {
		return _useFakeLightning;
	}

	public void setFakeLightning(boolean useFakeLightning) {
		this._useFakeLightning = useFakeLightning;
	}
	
	public void setNumberOfRows(int rows) {
		numberOfRows = rows;
	}
	
	public int getNumberOfRows() {
		return numberOfRows;
	}
}
