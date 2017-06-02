package particlesInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import renderEngine.Loader;

public class ParticleInstancedMaster {
	private static Map<ParticleInstancedTexture, List<ParticleInstanced>> particles = new HashMap<>();
	private static ParticleInstancedRenderer renderer;
	
	public static void init(Loader loader, Matrix4f projectionMatrix){
		renderer = new ParticleInstancedRenderer(loader, projectionMatrix);
	}
	
	public static void update(Camera camera){
		Iterator<Entry<ParticleInstancedTexture, List<ParticleInstanced>>> mapIterator = particles.entrySet().iterator();
		while(mapIterator.hasNext()){
			Entry<ParticleInstancedTexture, List<ParticleInstanced>> entry = mapIterator.next();
			List<ParticleInstanced> list = entry.getValue();
			Iterator<ParticleInstanced> iterator = list.iterator();
			while(iterator.hasNext()){
				ParticleInstanced p = iterator.next();
				boolean stillAlive = p.update(camera);
				if(!stillAlive){
					iterator.remove();
					if(list.isEmpty())
						mapIterator.remove();
				}
			}
			if(!entry.getKey().doAdditiveBlending())
				InsertionSort.sortHighToLow(list);
		}

	}
	
	public static void renderParticles(Camera camera){
		renderer.render(particles, camera);
	}
	
	public static void cleanUp(){
		renderer.cleanUp();
	}
	
	public static void addParticle(ParticleInstanced particle){
		List<ParticleInstanced> list = particles.get(particle.getTexture());
		if(list == null){
			list = new ArrayList<>();
			particles.put(particle.getTexture(), list);
		}
		list.add(particle);
	}
}
