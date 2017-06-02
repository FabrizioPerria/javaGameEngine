package models;

public class RawModel {
	private int _VAO;
	private int _vertexCount;

	public RawModel(int VAO, int numVertices){
		_VAO = VAO;
		_vertexCount = numVertices;
	}
	
	public int getVAO() {
		return _VAO;
	}

	public int getVertexCount() {
		return _vertexCount;
	}
}
