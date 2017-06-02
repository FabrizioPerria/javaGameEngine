package ambient;

import org.joml.Vector3f;

public class Fog {
	public static float DENSITY = 0.0005f;
	public static float GRADIENT = 90.0f;
	public static Vector3f FOG_COLOR = new Vector3f(0.5f, 0.5f, 0.5f);
	public static float LOWER_LIMIT = 0.0f;
	public static float UPPER_LIMIT = 10.0f;
}
