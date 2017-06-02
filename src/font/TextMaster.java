package font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontMeshCreator.TextMeshData;
import renderEngine.Loader;

public class TextMaster {
	private static Loader _loader;
	private static Map<FontType, List<GUIText>> _texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer _renderer;
	
	public static void init(Loader loader){
		_renderer = new FontRenderer();
		_loader = loader;
	}
	
	public static void loadText(GUIText text){
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int VAO = _loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(VAO, data.getVertexCount());
		
		List<GUIText> batch = _texts.get(font);
		if(batch == null){
			batch = new ArrayList<>();
			_texts.put(font, batch);
		}
		
		batch.add(text);
	}
	
	public static void updateText(GUIText text){
		FontType font = text.getFont();
		List<GUIText> batch = _texts.get(text.getFont());
		batch.remove(text);
		TextMeshData data = font.loadText(text);
		int VAO = _loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(VAO, data.getVertexCount());
		batch.add(text);
	}
	
	public static void removeText(GUIText text){
		List<GUIText> batch = _texts.get(text.getFont());
		if(batch != null){
			batch.remove(text);
			if(batch.isEmpty()){
				_texts.remove(batch);
			}
		}
	}

	public static void render(){
		_renderer.render(_texts);
	}
	
	public static void cleanUp(){
		_renderer.cleanUp();
	}
}
