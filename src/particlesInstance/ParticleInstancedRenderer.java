package particlesInstance;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import entities.Camera;
import models.RawModel;
import renderEngine.InstancedAttrib;
import renderEngine.Loader;
import renderEngine.VertexAttrib;
import toolbox.Maths;

public class ParticleInstancedRenderer {
	
	private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	public static final int MAX_INSTANCES = 10000;
	private static final int INSTANCE_DATA_LENGTH = 21;
	
	private RawModel quad;
	private ParticleInstancedShader shader;

	private Loader loader;
	private int VBO;
	private int pointer;
	
	private static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH * 4);
	
	protected ParticleInstancedRenderer(Loader loader, Matrix4f projectionMatrix){
		this.loader = loader;
		VBO = loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		
		quad = loader.loadToVAO(VERTICES, 2);
		
		//attribute 0 is the position
		loader.addInstancedAtteribute(quad.getVAO(), VBO, InstancedAttrib.MV_COL1.ordinal(), 4, INSTANCE_DATA_LENGTH, 0);
		loader.addInstancedAtteribute(quad.getVAO(), VBO, InstancedAttrib.MV_COL2.ordinal(), 4, INSTANCE_DATA_LENGTH, 4);
		loader.addInstancedAtteribute(quad.getVAO(), VBO, InstancedAttrib.MV_COL3.ordinal(), 4, INSTANCE_DATA_LENGTH, 8);
		loader.addInstancedAtteribute(quad.getVAO(), VBO, InstancedAttrib.MV_COL4.ordinal(), 4, INSTANCE_DATA_LENGTH, 12);
		loader.addInstancedAtteribute(quad.getVAO(), VBO, InstancedAttrib.TEXTURE_OFFSETS.ordinal(), 4, INSTANCE_DATA_LENGTH, 16);
		loader.addInstancedAtteribute(quad.getVAO(), VBO, InstancedAttrib.BLEND_FACTOR.ordinal(), 1, INSTANCE_DATA_LENGTH, 20);
		
		shader = new ParticleInstancedShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureAtlas();
		shader.stop();
	}
	
	protected void render(Map<ParticleInstancedTexture, List<ParticleInstanced>> particles, Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		prepare();
		for(ParticleInstancedTexture texture : particles.keySet()){
			bindTexture(texture);
			List<ParticleInstanced> list = particles.get(texture);
			pointer = 0;
			float[] vboData = new float[list.size() * INSTANCE_DATA_LENGTH];
			for(ParticleInstanced particle : list){
				updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), viewMatrix, vboData);
				updateTexCoordInfo(particle, vboData);
			}
			loader.updateVBO(VBO, vboData, floatBuffer);
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), list.size());
		}
		finishRendering();
	}
	
	private void bindTexture(ParticleInstancedTexture texture){
		//bind texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTBO());
		if(texture.doAdditiveBlending())
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);	//additive blending
		else
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);	//alpha blending
		
		shader.loadNumberOfRows(texture.getNumberOfrows());
	}
	
	private void updateTexCoordInfo(ParticleInstanced particle, float[] data){
		data[pointer++] = particle.getCurrentStageOffset().x; 
		data[pointer++] = particle.getCurrentStageOffset().y; 
		data[pointer++] = particle.getNextStageOffset().x; 
		data[pointer++] = particle.getNextStageOffset().y; 
		data[pointer++] = particle.getBlendFactor(); 
	}

	private void updateModelViewMatrix(Vector3f position, Vector3f rotation, Vector3f scale, Matrix4f viewMatrix, float[] vboData){
	    Matrix4f modelMatrix = new Matrix4f();
	    modelMatrix.identity();
	    modelMatrix.translate(position);	
	    modelMatrix.m00(viewMatrix.m00());
	    modelMatrix.m01(viewMatrix.m10());
	    modelMatrix.m02(viewMatrix.m20());
	    modelMatrix.m10(viewMatrix.m01());
	    modelMatrix.m11(viewMatrix.m11());
	    modelMatrix.m12(viewMatrix.m21());
	    modelMatrix.m20(viewMatrix.m02());
	    modelMatrix.m21(viewMatrix.m12());
	    modelMatrix.m22(viewMatrix.m22());
	    
	    modelMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1));
	    modelMatrix.scale(scale);
	    
	    Matrix4f modelViewMatrix = new Matrix4f(viewMatrix).mul(modelMatrix);
	    storeMatrixData(modelViewMatrix, vboData);
	}

	private void storeMatrixData(Matrix4f modelViewMatrix, float[] data){
	    data[pointer++] = modelViewMatrix.m00();
	    data[pointer++] = modelViewMatrix.m01();
	    data[pointer++] = modelViewMatrix.m02();
	    data[pointer++] = modelViewMatrix.m03();
	    data[pointer++] = modelViewMatrix.m10();
	    data[pointer++] = modelViewMatrix.m11();
	    data[pointer++] = modelViewMatrix.m12();
	    data[pointer++] = modelViewMatrix.m13();
	    data[pointer++] = modelViewMatrix.m20();
	    data[pointer++] = modelViewMatrix.m21();
	    data[pointer++] = modelViewMatrix.m22();
	    data[pointer++] = modelViewMatrix.m23();
	    data[pointer++] = modelViewMatrix.m30();
	    data[pointer++] = modelViewMatrix.m31();
	    data[pointer++] = modelViewMatrix.m32();
	    data[pointer++] = modelViewMatrix.m33();
	}

	protected void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(){
		shader.start();
		GL30.glBindVertexArray(quad.getVAO());
		GL20.glEnableVertexAttribArray(InstancedAttrib.POSITION.ordinal());
		GL20.glEnableVertexAttribArray(InstancedAttrib.MV_COL1.ordinal());
		GL20.glEnableVertexAttribArray(InstancedAttrib.MV_COL2.ordinal());
		GL20.glEnableVertexAttribArray(InstancedAttrib.MV_COL3.ordinal());
		GL20.glEnableVertexAttribArray(InstancedAttrib.MV_COL4.ordinal());
		GL20.glEnableVertexAttribArray(InstancedAttrib.TEXTURE_OFFSETS.ordinal());
		GL20.glEnableVertexAttribArray(InstancedAttrib.BLEND_FACTOR.ordinal());
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
	}
	
	private void finishRendering(){
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(InstancedAttrib.POSITION.ordinal());
		GL20.glDisableVertexAttribArray(InstancedAttrib.MV_COL1.ordinal());
		GL20.glDisableVertexAttribArray(InstancedAttrib.MV_COL2.ordinal());
		GL20.glDisableVertexAttribArray(InstancedAttrib.MV_COL3.ordinal());
		GL20.glDisableVertexAttribArray(InstancedAttrib.MV_COL4.ordinal());
		GL20.glDisableVertexAttribArray(InstancedAttrib.TEXTURE_OFFSETS.ordinal());
		GL20.glDisableVertexAttribArray(InstancedAttrib.BLEND_FACTOR.ordinal());
		GL30.glBindVertexArray(0);	
		shader.stop();
	}

}
