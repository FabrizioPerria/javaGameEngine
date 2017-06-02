package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {
	private Vector3f _position;
	private Vector3f _color;
	private Vector3f _attenuationCoefficients;//in the equation AF.x * d^2 + AF.y * d + AF.z
																		
	
	public Light(Vector3f position, Vector3f color) {
		//0,0,1 means constant attenuation to 1.....so no attenuation at all
		this(position, color, new Vector3f(0,0,1));
	}
	
	public Light(Vector3f position, Vector3f color, Vector3f attenuationCoefficients) {
		_position = position;
		_color = color;
		_attenuationCoefficients = attenuationCoefficients;
	}

	public Vector3f getPosition() {
		return _position;
	}

	public void setPosition(Vector3f position) {
		_position = position;
	}

	public Vector3f getColor() {
		return _color;
	}

	public void setColor(Vector3f color) {
		_color = color;
	}

	public Vector3f getAttenuation(){
		return _attenuationCoefficients;
	}
	
}
