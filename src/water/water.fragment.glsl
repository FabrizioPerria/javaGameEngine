#version 400 core

#define NUM_LIGHTS 7

in vec4 clipSpace; 
in vec2 textureCoords;
in vec3 toCameraVector;
in vec3 fromLightVector[NUM_LIGHTS];

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform float moveFactor;
uniform sampler2D normalMap;
uniform sampler2D depthMap;

uniform vec3 lightColor[NUM_LIGHTS]; 
uniform vec3 attenuationCoefficients[NUM_LIGHTS];

const float shineDamper = 20.0;
const float reflectivity = 0.5;
const float waveStrength = 0.04;

const float near = 0.1;
const float far = 1000.0;

void main(void) {
	vec2 normalClipSpace = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
	
	vec2 refractTextureCoords = vec2(normalClipSpace.x, normalClipSpace.y);
	vec2 reflectTextureCoords = vec2(normalClipSpace.x, -normalClipSpace.y);
	
	float depth = texture(depthMap, refractTextureCoords).r;
	float floorDistance = 2.0 * far * near / (far + near - (2.0 * depth -1.0) * (far - near)); 
	
	depth = gl_FragCoord.z;
	float waterDistance = 2.0 * far * near / (far + near - (2.0 * depth -1.0) * (far - near)); 
	
	float waterDepth = floorDistance - waterDistance;

	vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg*0.1;
	distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+moveFactor);
	vec2 distortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth/20.0, 0, 1.0);
	
	refractTextureCoords = refractTextureCoords + distortion;
	refractTextureCoords = clamp(refractTextureCoords, 0.001,0.999);
	
	reflectTextureCoords = reflectTextureCoords + distortion;
	reflectTextureCoords.x = clamp(reflectTextureCoords.x, 0.001,0.999);
	reflectTextureCoords.y = clamp(reflectTextureCoords.y, -0.999,-0.001);
	
	vec4 reflectionColor = texture(reflectionTexture, reflectTextureCoords);
	vec4 refractionColor = texture(refractionTexture, refractTextureCoords);
	
	vec4 normalMapColor = texture(normalMap, distortedTexCoords);
	vec3 normal = normalize(vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3.0, normalMapColor.g * 2.0 - 1.0));
	
	// FRESNEL
	vec3 view = normalize(toCameraVector);
	float refractiveFactor = dot(view, normal);
	refractiveFactor = pow(refractiveFactor, 0.5);
	
	vec3 totalSpecular = vec3(0.0);
	for (int i = 0; i < NUM_LIGHTS;i++){
		float distance = length(fromLightVector[i]);
		float attenuationFactor = attenuationCoefficients[i].x + (attenuationCoefficients[i].y * distance) + (attenuationCoefficients[i].z * distance * distance);
		
		vec3 reflectedLight = reflect(normalize(fromLightVector[i]), normal);
		float specularFactor = dot(reflectedLight, view);
		specularFactor = max(specularFactor, 0.0);
		
		float dampedFactor = pow(specularFactor, shineDamper) * reflectivity;

		totalSpecular = totalSpecular + (dampedFactor * lightColor[i])/attenuationFactor;
	}
	
	totalSpecular = totalSpecular * clamp(waterDepth/5.0, 0, 1.0);
	
	out_Color = mix(reflectionColor, refractionColor, refractiveFactor);
	out_Color = mix(out_Color, vec4(0.0,0.3,0.5,1.0), 0.2) + vec4(totalSpecular, 0.0);
	
	out_Color.a = clamp(waterDepth/5.0, 0, 1.0);
}