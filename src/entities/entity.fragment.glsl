#version 400 core

#define NUM_LIGHTS 7

in vec3 surfaceNormal;
in vec3 toLightVector[NUM_LIGHTS];
in vec3 toCameraVector;
in vec2 passTextureCoords;
in float visibility;
out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor[NUM_LIGHTS];
uniform vec3 attenuationCoefficients[NUM_LIGHTS];

uniform float shineDamper;
uniform float reflectivity;

uniform float ambientLightAmount;

uniform vec3 skyColor;

void main(void) {
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitCameraVector = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for (int i = 0; i < NUM_LIGHTS;i++){
		float distance = length(toLightVector[i]);
		float attenuationFactor = attenuationCoefficients[i].x + (attenuationCoefficients[i].y * distance) + (attenuationCoefficients[i].z * distance * distance);
		vec3 unitLightVector = normalize(toLightVector[i]);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLight = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLight, unitCameraVector);
		specularFactor = max(specularFactor, 0.0);
		
		float dampedFactor = pow(specularFactor, shineDamper) * reflectivity;
		
		float nDotL = dot(unitNormal, unitLightVector);
		float brightness = max(nDotL, 0.0);

		totalSpecular = totalSpecular + (dampedFactor * lightColor[i])/attenuationFactor;	
		totalDiffuse = totalDiffuse + (brightness * lightColor[i])/attenuationFactor;
	}
	
	totalDiffuse = max(totalDiffuse, ambientLightAmount);
	
	vec4 textureColor = texture(textureSampler, passTextureCoords);
	if(textureColor.a < 0.5)
		discard;
	
    out_Color = vec4(totalDiffuse,1.0) * textureColor + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(skyColor,1.0), out_Color, visibility);
}
