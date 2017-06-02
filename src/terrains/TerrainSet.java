package terrains;

import java.util.ArrayList;
import java.util.List;

import renderEngine.MasterRenderer;

public class TerrainSet {
	private List<Terrain> terrains;
	
	public TerrainSet(){
		terrains = new ArrayList<Terrain>();
	}
	
	public void loadTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public int getNumTerrains(){
		return terrains.size();
	}
	
	public Terrain getTerrainByCoord(float worldX, float worldZ){
		int xCoord = (int)(worldX / Terrain.SIZE);
		int zCoord = (int)(worldZ / Terrain.SIZE);
		
		if(worldX < 0)
			xCoord--;
		if(worldZ < 0)
			zCoord--;
		
		for(Terrain terrain : terrains){
			if(xCoord == terrain.getGridX() && zCoord == terrain.getGridZ())
				return terrain;
		}
		return null;
	}
	
	public float getHeight(float worldX, float worldZ){
		return getTerrainByCoord(worldX, worldZ).getTerrainHeight(worldX, worldZ);
	}
	
	public Terrain getTerrainByIndex(int index){
		return terrains.get(index);
	}
}
