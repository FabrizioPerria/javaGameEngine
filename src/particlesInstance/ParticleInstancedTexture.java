package particlesInstance;

public class ParticleInstancedTexture {
	private int TBO;
	private int numberOfrows;
	
	private boolean additiveBlending;
	
	public ParticleInstancedTexture(int tBO, int numberOfrows) {
		this(tBO, numberOfrows, true);
	}
	
	public ParticleInstancedTexture(int tBO, int numberOfrows, boolean additiveBlending) {
		this.additiveBlending = additiveBlending;
		TBO = tBO;
		this.numberOfrows = numberOfrows;
	}
	
	public int getTBO() {
		return TBO;
	}
	public int getNumberOfrows() {
		return numberOfrows;
	}
	
	public boolean doAdditiveBlending(){
		return additiveBlending;
	}
}
