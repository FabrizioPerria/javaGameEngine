#version 400 core

#define NUM_LIGHTS 4

in vec2 outTexCoord;
in vec3 outToLightVector[NUM_LIGHTS];
in vec3 outToCameraVector;
in float visibilityFog;
in vec4 shadowCoords;

out vec4 color;
out vec4 brightColor;

//TEXTURES
uniform sampler2D textureSampler;
uniform sampler2D normalMap;

//LIGHT PARAMETERS
uniform vec3 lightColor[NUM_LIGHTS];
uniform float reflectivity;
uniform float shineDamper;
uniform float ambientLightAmount;
uniform vec3 attenuationCoefficients[NUM_LIGHTS];	//AF.x * d^2 + AF.y * d + AF.z

//SKY PARAMETERS
uniform vec3 skyColor;

//SHADOW
uniform sampler2D shadowMap;
uniform float shadowMapSize;
const int AREA_PCF = 2;
const float numTexels = (AREA_PCF * 2.0 + 1.0) * (AREA_PCF * 2.0 + 1.0);
const float ANTIACNE_OFFSET = 0.002;

// SPECULAR LIGHT
uniform sampler2D specularMap;
uniform float hasSpecularMap;

void main(void){
	
	vec4 texColor = texture(textureSampler, outTexCoord);
	if(texColor.a < 0.5)
		discard;
	
	float texelSize = 1.0 / shadowMapSize;
	float totalSamplesInShadow = 0;
	
	for(int x = -AREA_PCF; x <= AREA_PCF; ++x){
		for(int y = -AREA_PCF; y <= AREA_PCF; ++y){
			float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x,y) * texelSize).r;
			if(shadowCoords.z > objectNearestLight + ANTIACNE_OFFSET){
				totalSamplesInShadow++;
			}
		}
	}
	
	totalSamplesInShadow /= numTexels;

	float lightFactor = 1.0 - (totalSamplesInShadow * shadowCoords.w);
	
	vec4 normalMapSample = 2.0 * texture(normalMap, outTexCoord) - 1.0;
	
	vec3 unitNormal = normalize(normalMapSample.rgb);
	vec3 unitVectorToCamera = normalize(outToCameraVector);
	
	vec3 totalDiffuse = vec3(0);
	vec3 totalSpecular = vec3(0);
	
	for(int i = 0; i < NUM_LIGHTS; i++){
		float distance = length(outToLightVector[i]);
		float attenuationFactor = attenuationCoefficients[i].x * distance * distance + 
								  attenuationCoefficients[i].y * distance +
								  attenuationCoefficients[i].z;
		
		vec3 unitToLightVector = normalize(outToLightVector[i]);
		
		float brightness = max(dot(unitNormal, unitToLightVector), 0.0);
		vec3 lightDirection = -unitToLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		
		float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor,shineDamper);
		totalDiffuse += (brightness * lightColor[i]) / attenuationFactor;
		totalSpecular += (dampedFactor * reflectivity * lightColor[i]) / attenuationFactor;
	}
	totalDiffuse = max(totalDiffuse * lightFactor, ambientLightAmount);

	if(hasSpecularMap > 0.5){
		vec4 specularInfo = texture(specularMap, outTexCoord);
		totalSpecular *= specularInfo.r;
		if(specularInfo.g > 0.5){
			totalDiffuse = vec3(1.0);
		}
	}

	color =  vec4(totalDiffuse,1.0) * texColor + vec4(totalSpecular,1.0);
	color = mix(vec4(skyColor,1.0),color, visibilityFog);
	
	brightColor = vec4(0);
}