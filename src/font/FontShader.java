package font;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.VertexAttrib;
import shaders.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String filename = "font/font";
	
	private int location_translation;
	private int location_fontColor;
	private int location_fontAtlas;
	private int location_width;
	private int location_edgeTransition;
	private int location_borderWidth;
	private int location_borderEdge;
	private int location_offsetForShadow;
	private int location_outlineColor;
	
	public FontShader() {
		super(filename);
	}

	@Override
	protected void getAllUniformLocations() {
		location_translation = super.getUniformLocation("translation");
		location_fontColor = super.getUniformLocation("fontColor");
		location_fontAtlas = super.getUniformLocation("fontAtlas");
		location_width = super.getUniformLocation("width");
		location_edgeTransition = super.getUniformLocation("edgeTransition");
		location_borderWidth = super.getUniformLocation("borderWidth");
		location_borderEdge = super.getUniformLocation("borderEdge");
		location_offsetForShadow = super.getUniformLocation("offsetForShadow");
		location_outlineColor = super.getUniformLocation("outlineColor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(VertexAttrib.POSITION.ordinal(), "inPosition");
		super.bindAttribute(VertexAttrib.TEXTURE_COORDINATE.ordinal(), "inTexCoord");
	}

	public void loadFontAtlas(){
		super.loadInt(location_fontAtlas, 0);
	}
	
	public void loadEdgeParameters(float width, float edge){
		super.loadFloat(location_width, width);
		super.loadFloat(location_edgeTransition, edge);
	}
	
	public void loadOutline(Vector3f color, Vector2f offsetShadow, float borderWidth, float borderEdge){
		super.loadVector3f(location_outlineColor, color);
		super.loadVector2f(location_offsetForShadow, offsetShadow);
		super.loadFloat(location_borderWidth, borderWidth);
		super.loadFloat(location_borderEdge, borderEdge);
	}
	
	public void loadFontColor(Vector3f color){
		super.loadVector3f(location_fontColor, color);
	}
	
	public void setTranslation(Vector2f translation){
		super.loadVector2f(location_translation, translation);
	}
}
