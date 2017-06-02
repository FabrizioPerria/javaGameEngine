package textures;

import java.nio.ByteBuffer;

public class TextureData {
	private int width;
	private int height;
	private int[] buffer;
	
	public TextureData(int width, int height, int[] pixels){
		this.buffer = pixels;
		this.width = width;
		this.height = height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int[] getBuffer(){
		return buffer;
	}
}
