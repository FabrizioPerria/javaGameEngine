#version 400 core

#define NUM_LIGHTS 4

in vec2 outTexCoord;
in vec3 outNormal;
in vec3 outToLightVector[NUM_LIGHTS];
in vec3 outToCameraVector;
in float visibilityFog;
in vec4 shadowCoords;

out vec4 color;
out vec4 brightColor;

//TEXTURES
uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

//SHADOW
uniform sampler2D shadowMap;
uniform float shadowMapSize;
const int AREA_PCF = 2;
const float numTexels = (AREA_PCF * 2.0 + 1.0) * (AREA_PCF * 2.0 + 1.0);

//LIGHT PARAMETERS
uniform vec3 lightColor[NUM_LIGHTS];
uniform float reflectivity;
uniform float shineDamper;
uniform float ambientLightAmount;
uniform vec3 attenuationCoefficients[NUM_LIGHTS];	//AF.x * d^2 + AF.y * d + AF.z

//SKY PARAMETERS
uniform vec3 skyColor;

void main(void){
	float texelSize = 1.0 / shadowMapSize;
	float totalSamplesInShadow = 0;
	
	for(int x = -AREA_PCF; x <= AREA_PCF; ++x){
		for(int y = -AREA_PCF; y <= AREA_PCF; ++y){
			float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x,y) * texelSize).r;
			if(shadowCoords.z > objectNearestLight){
				totalSamplesInShadow++;
			}
		}
	}
	
	totalSamplesInShadow /= numTexels;

	float lightFactor = 1.0 - (totalSamplesInShadow * shadowCoords.w);

	vec4 blendMapColor = texture(blendMap, outTexCoord);
	float backgroundAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);	//what to render when the blend map is black
	
	vec2 tiledCoords = outTexCoord * 40.0;
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backgroundAmount;
	vec4 redTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
	vec4 greenTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
	vec4 blueTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;

	vec4 totalColor = backgroundTextureColor + redTextureColor + greenTextureColor + blueTextureColor;

	vec3 unitNormal = normalize(outNormal);
	vec3 unitToCameraVector = normalize(outToCameraVector);
	
	vec3 totalDiffuse = vec3(0);
	vec3 totalSpecular = vec3(0);	
	
	for(int i = 0; i < NUM_LIGHTS; ++i){
		float distance = length(outToLightVector[i]);
		float attenuationFactor = attenuationCoefficients[i].x * distance * distance + 
								  attenuationCoefficients[i].y * distance +
								  attenuationCoefficients[i].z;
								  
		vec3 unitToLightVector = normalize(outToLightVector[i]);
		
		float brightness = max(dot(unitNormal, unitToLightVector),0.0);
		vec3 lightDirection = -unitToLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		
		float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector), 0.0);
		float dampFactor = pow(specularFactor, shineDamper);
		totalDiffuse += (brightness * lightColor[i]) / attenuationFactor;
		totalSpecular += (dampFactor * reflectivity * lightColor[i]) / attenuationFactor;
	}
	
	totalDiffuse = max(totalDiffuse * lightFactor, ambientLightAmount);
	
	color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 1.0);
	
	color = mix(vec4(skyColor, 1.0), color, visibilityFog);
	
	brightColor = vec4(0);
}