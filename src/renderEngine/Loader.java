package renderEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.*;
//import org.newdawn.slick.opengl.Texture;
//import org.newdawn.slick.opengl.TextureLoader;

//import de.matthiasmann.twl.utils.PNGDecoder;
//import de.matthiasmann.twl.utils.PNGDecoder.Format;
import models.RawModel;
import textures.Texture;
//import textures.TextureData;
import textures.TextureData;

public class Loader {
	private List<Integer> _vaos = new ArrayList<Integer>();
	private List<Integer> _vbos = new ArrayList<Integer>();
	private List<Integer> _tbos = new ArrayList<Integer>();
	
	public RawModel loadToVAO(float[] positions, float[] texCoords, float[] normals, int[] indices){
		int VAO = createVAO();
		bindIBO(indices);
		storeDataInAttributeList(VertexAttrib.POSITION.ordinal(), 3, positions);
		storeDataInAttributeList(VertexAttrib.TEXTURE_COORDINATE.ordinal(), 2, texCoords);
		storeDataInAttributeList(VertexAttrib.NORMAL.ordinal(), 3, normals);
		unbindVAO();
		return new RawModel(VAO, indices.length);
	}
	
	public RawModel loadToVAO(float[] positions, int numDimensions){
		int VAO = createVAO();
		storeDataInAttributeList(VertexAttrib.POSITION.ordinal(), numDimensions,  positions);
		unbindVAO();
		return new RawModel(VAO, positions.length / numDimensions);
	}

	public RawModel loadToVAO(float[] positions, float[] texCoords, float[] normals, float[] tangents, int[] indices){
		int VAO = createVAO();
		bindIBO(indices);
		storeDataInAttributeList(VertexAttrib.POSITION.ordinal(), 3, positions);
		storeDataInAttributeList(VertexAttrib.TEXTURE_COORDINATE.ordinal(), 2, texCoords);
		storeDataInAttributeList(VertexAttrib.NORMAL.ordinal(), 3, normals);
		storeDataInAttributeList(VertexAttrib.TANGENT.ordinal(), 3, tangents);
		unbindVAO();
		return new RawModel(VAO, indices.length);
	}
	
	public int loadToVAO(float[] positions, float[] texCoords){
		int VAO = createVAO();

		storeDataInAttributeList(VertexAttrib.POSITION.ordinal(), 2, positions);
		storeDataInAttributeList(VertexAttrib.TEXTURE_COORDINATE.ordinal(), 2, texCoords);

		unbindVAO();
		return VAO;
	}	
	
	public RawModel loadToVAO(ModelData modelData) {
	    return loadToVAO(modelData.getVertices(), modelData.getTextureCoords(), modelData.getNormals(), modelData.getIndices());
	}
	
	public int createEmptyVBO(int floatCount){
		int VBO = GL15.glGenBuffers();
		_vbos.add(VBO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		return VBO;
	}
	
	public void addInstancedAtteribute(int VAO, int VBO, int attribute, int dataSize, int stride, int offset){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL30.glBindVertexArray(VAO);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, stride * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}

	public void updateVBO(int VBO, float[] data, FloatBuffer buffer){
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public int loadCubeMap(String[] textureFilenames){
		int TBO = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, TBO);
		
		for(int i = 0; i < textureFilenames.length; ++i){
			TextureData data = decodeTexture("res/" + textureFilenames[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		_tbos.add(TBO);
		
		return TBO;
	}
	
	private TextureData decodeTexture(String path) {
	    int width;
	    int height;
	    int[] pixels;
	    try {
	        BufferedImage image = ImageIO.read(new File(path));
	        width = image.getWidth();
	        height = image.getHeight();
	        pixels = new int[width * height];
	        image.getRGB(0, 0, width, height, pixels, 0, width);
	    } catch (IOException e) {
	        System.err.println("Failed to load texture file: " + path);
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    }
	    return new TextureData(width, height, pixels);
	}
	
	public int loadTexture(String filename){
		Texture texture = null;
		try {
			texture = new Texture("res/" + filename + ".png");
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			} else {
				System.out.println("Anisotropic filtering not supported");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int TBO = texture.getTextureID();
		
		_tbos.add(TBO);
		

		
		return TBO;
	}
	
//	public int loadTextFontAtlas(String filename){
//		Texture texture = null;
//		try {
//			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + filename + ".png"));
//			
//			//enable mipmapping
//			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
//			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
//			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		int TBO = texture.getTextureID();
//		
//		_tbos.add(TBO);
//		
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
//		
//		return TBO;
//	}
	
	private int createVAO(){
		int VAO = GL30.glGenVertexArrays();
		
		_vaos.add(VAO);
		
		GL30.glBindVertexArray(VAO);
		
		return VAO;
	}
	
	private void storeDataInAttributeList(int attributeID, int dimensions, float[] data){
		int VBO = GL15.glGenBuffers();
		
		_vbos.add(VBO);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		
		FloatBuffer buffer = toFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
		GL20.glVertexAttribPointer(attributeID, dimensions, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private void bindIBO(int[] indices){
		int VBO = GL15.glGenBuffers();
		_vbos.add(VBO);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VBO);
		
		IntBuffer buffer = toIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer,  GL15.GL_STATIC_DRAW);
	}

	private IntBuffer toIntBuffer(int[] data){
		IntBuffer fb = BufferUtils.createIntBuffer(data.length);
		fb.put(data);
		fb.flip();
		
		return fb;
	}
	
	private FloatBuffer toFloatBuffer(float[] data){
		FloatBuffer fb = BufferUtils.createFloatBuffer(data.length);
		fb.put(data);
		fb.flip();
		
		return fb;
	}
	
	public void cleanUp(){
		for(Integer TBO : _tbos){
			GL11.glDeleteTextures(TBO);
		}
		_tbos.clear();
		
		for(Integer VBO : _vbos){
			GL15.glDeleteBuffers(VBO);
		}
		_vbos.clear();
		
		for(Integer VAO : _vaos){
			GL30.glDeleteVertexArrays(VAO);
		}
		_vaos.clear();
	}
}
