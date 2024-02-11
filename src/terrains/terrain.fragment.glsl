#version 400 core

#define NUM_LIGHTS 7

in vec3 surfaceNormal;
in vec3 toLightVector[NUM_LIGHTS];
in vec3 toCameraVector;
in vec2 passTextureCoords;
in float visibility;
in vec4 shadowCoords;

out vec4 out_Color;

uniform sampler2D bgTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform sampler2D shadowMap;

uniform vec3 lightColor[NUM_LIGHTS];
uniform vec3 attenuationCoefficients[NUM_LIGHTS];
uniform float shineDamper;
uniform float reflectivity;

uniform float ambientLightAmount;

uniform vec3 skyColor;

const int pcfCount = 2;
const float totalTexels = (2 * pcfCount + 1) * (2 * pcfCount + 1);

void main(void) {
	
	float mapSize = 4096.0;
	float texelSize = 1.0 / mapSize;
	float amountInLight = 0.0;

	for(int x = -pcfCount; x <= pcfCount; x++) {
		for(int y = -pcfCount; y <= pcfCount; y++) {
			vec2 offset = vec2(x, y) * texelSize;
			float objectNearestLight = texture(shadowMap, shadowCoords.xy + offset).r;
			if(shadowCoords.z > objectNearestLight + 0.002) {
				amountInLight += 1.0;
			}
		}
	}

	amountInLight /= totalTexels;

	float lightFactor = 1.0 - (amountInLight * shadowCoords.w);

	vec4 blendMapColor = texture(blendMap, passTextureCoords);
	
	float bgTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoords = passTextureCoords * 40.0;
	
	vec4 bgTextureColor = texture(bgTexture, tiledCoords) * bgTextureAmount;
	vec4 rTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
	vec4 gTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
	vec4 bTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;
	
	vec4 totalColor = bgTextureColor + rTextureColor + gTextureColor + bTextureColor;

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitCameraVector = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for (int i = 0; i < NUM_LIGHTS; i++){
		float distance = length(toLightVector[i]);
		float attenuationFactor = attenuationCoefficients[i].x + (attenuationCoefficients[i].y * distance) + (attenuationCoefficients[i].z * distance * distance);
	
		vec3 unitLightVector = normalize(toLightVector[i]);
		
		vec3 lightDirection = -unitLightVector;
		
		vec3 reflectedLight = reflect(lightDirection, unitNormal);
		
		float specularFactor = dot(reflectedLight, unitCameraVector);
		specularFactor = max(specularFactor, 0.0);
		
		float dampedFactor = pow(specularFactor, shineDamper) * reflectivity;
		totalSpecular = totalSpecular + (dampedFactor * lightColor[i])/attenuationFactor;
		
		float nDotL = dot(unitNormal, unitLightVector);
		float brightness = max(nDotL, 0.0);
	
		totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenuationFactor;
	}
	totalDiffuse = max(totalDiffuse, ambientLightAmount) * lightFactor;
	
    out_Color = vec4(totalDiffuse,1) * totalColor + vec4(totalSpecular,1);
    out_Color = mix(vec4(skyColor,1.0), out_Color, visibility);
}
