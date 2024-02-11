package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.joml.Vector2f;
import org.joml.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import toolbox.Maths;

public class Terrain {
	public static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	private static int seed;
	
	private float _x;
	private float _z;
	
	private RawModel _model;
	
	private TerrainTexturePack _textures;
	private TerrainTexture _blendMap;
	
	private HeightsGenerator heightGenerator;
	
	private float[][] heights;
	
	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack textures, TerrainTexture blendMap, String heightMap){
		_textures = textures;
		_blendMap = blendMap;
		_x = gridX * SIZE;
		_z = gridZ * SIZE;
		_model = generateTerrain(loader, heightMap);
	}
	
	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack textures, TerrainTexture blendMap){
		Random random = new Random();
		seed = random.nextInt(10000000);	
		heightGenerator = new HeightsGenerator(gridX, gridZ, (int)SIZE, seed);
		_textures = textures;
		_blendMap = blendMap;
		_x = gridX * SIZE;
		_z = gridZ * SIZE;
		_model = generateTerrain(loader);
	}
	
	public float getGridX(){
		return _x / SIZE;
	}
	
	public float getGridZ(){
		return _z / SIZE;
	}
	
	private RawModel generateTerrain(Loader loader){
		int vertexCount = 128;
		
		heights = new float[vertexCount][vertexCount];

		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
		int vertexPointer = 0;
		for(int i=0;i<vertexCount;i++){
			for(int j=0;j<vertexCount;j++){
				vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * SIZE;
				heights[j][i] = getHeight(heightGenerator, j, i);
				vertices[vertexPointer*3+1] = heights[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * SIZE;
				Vector3f normal = calculateNormal(heightGenerator, j, i);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<vertexCount-1;gz++){
			for(int gx=0;gx<vertexCount-1;gx++){
				int topLeft = (gz*vertexCount)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*vertexCount)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private RawModel generateTerrain(Loader loader, String heightMap){
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int vertexCount = image.getHeight();
		
		heights = new float[vertexCount][vertexCount];

		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
		int vertexPointer = 0;
		for(int i=0;i<vertexCount;i++){
			for(int j=0;j<vertexCount;j++){
				vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * SIZE;
				heights[j][i] = getHeight(image, j, i);
				vertices[vertexPointer*3+1] = heights[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * SIZE;
				Vector3f normal = calculateNormal(image, j, i);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<vertexCount-1;gz++){
			for(int gx=0;gx<vertexCount-1;gx++){
				int topLeft = (gz*vertexCount)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*vertexCount)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	public float getX() {
		return _x;
	}

	public float getZ() {
		return _z;
	}

	public RawModel getModel() {
		return _model;
	}

	public static float getSize() {
		return SIZE;
	}

	public TerrainTexturePack getTexturePack() {
		return _textures;
	}

	public TerrainTexture getBlendMap() {
		return _blendMap;
	}

	private float getHeight(HeightsGenerator heightGenerator, int x, int z){
		return heightGenerator.generateHeight(x, z);
	}
	
	private float getHeight(BufferedImage image, int x, int z){
		if(x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight())
			return 0;
		
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= MAX_HEIGHT;
		
		return height;
	}
	
	public float getTerrainHeight(float worldX, float worldZ){
		float terrainX = worldX - _x;
		float terrainZ = worldZ - _z;
		
		float gridSquareSize = SIZE / (float)(heights.length - 1);
		
		int gridX = (int)Math.floor(terrainX / gridSquareSize);
		int gridZ = (int)Math.floor(terrainZ / gridSquareSize);
		
		if(gridX < 0 || gridX >= (heights.length - 1) || gridZ < 0 || gridZ >= (heights.length - 1))
			return 0;
		
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

		float res = 0;
		
		if(xCoord <= (1 - zCoord)){
			res = Maths.baryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1, 
							heights[gridX + 1][gridZ], 0), new Vector3f(0, 
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			res = Maths.baryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		
		return res;
	}
	
	private Vector3f calculateNormal(HeightsGenerator heightGenerator, int x, int z){
		float heightL = getHeight(heightGenerator, x - 1, z);
		float heightR = getHeight(heightGenerator, x + 1, z);
		float heightU = getHeight(heightGenerator, x, z + 1);
		float heightD = getHeight(heightGenerator, x, z - 1);
		
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}
	
	private Vector3f calculateNormal(BufferedImage image, int x, int z){
		float heightL = getHeight(image, x - 1, z);
		float heightR = getHeight(image, x + 1, z);
		float heightU = getHeight(image, x, z + 1);
		float heightD = getHeight(image, x, z - 1);
		
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}
}
