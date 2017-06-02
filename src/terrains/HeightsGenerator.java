package terrains;

import java.util.Random;

public class HeightsGenerator {
	private static final float AMPLITUDE = 70f;
	private static final int OCTAVES = 3;
	private static final float ROUGHNESS = 0.3f;
	
	private Random random;
	private int xOffset;
	private int zOffset;
	
	private int seed;
	
	public HeightsGenerator(int gridX, int gridZ, int vertexCount, int seed){
		random = new Random();
		this.seed = seed;
		xOffset = gridX * (vertexCount - 1);
		zOffset = gridZ * (vertexCount - 1);
	}
	
	public float generateHeight(int x, int z){
		float total = 0;
		float d = (float)Math.pow(2, OCTAVES - 1);
		for(int i = 0; i < OCTAVES; ++i){
			float frequency = (float) (Math.pow(2,  i) / d);
			float amplitude = (float)Math.pow(ROUGHNESS, i) * AMPLITUDE;
			
			total += getInterpolatedNoise((x + xOffset) * frequency,  (z + zOffset) * frequency) * amplitude;
		}
		
		return total;
	}
	
	private float getInterpolatedNoise(float x, float z){
		int intX = (int)x;
		int intZ = (int)z;
		float fractX = x - intX;
		float fractZ = z - intZ;
		
		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		
		float i1 = interpolate(v1, v2, fractX);
		float i2 = interpolate(v3, v4, fractX);
		
		return interpolate(i1, i2, fractZ);
	}
	
	private float interpolate(float a, float b, float blend){
		double theta = blend * Math.PI;
		float cosFactor = (1f - (float)Math.cos(theta)) * 0.5f;
		return a * (1f - cosFactor) + b * cosFactor;
	}
	
	private float getSmoothNoise(int x, int z){
		float corners = (getNoise(x-1, z-1) + getNoise(x+1, z-1) + getNoise(x-1, z+1) + getNoise(x+1, z+1)) / 16f;
		float sides = (getNoise(x-1, z) + getNoise(x+1, z) + getNoise(x, z-1) + getNoise(x, z+1)) / 8f;
		float center = getNoise(x,z) / 4f;
		
		return corners + sides + center;
	}

	private float getNoise(int x, int z){
		random.setSeed(x * 49632 + z * 325176 + seed);
		return random.nextFloat() * 2f - 1f;
	}
}
