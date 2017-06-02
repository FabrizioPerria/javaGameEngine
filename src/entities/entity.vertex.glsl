#version 400 core

#define NUM_LIGHTS 4

in vec3 inPosition;
in vec2 inTexCoord;
in vec3 inNormal;

out vec2 outTexCoord;
out vec3 outNormal;
out vec3 outToLightVector[NUM_LIGHTS];
out vec3 outToCameraVector;
out float visibilityFog;
out vec4 shadowCoords;

//MATRICES
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

//LIGHT PARAMETERS
uniform vec3 lightPosition[NUM_LIGHTS];
uniform float fakeLight;

//FOG PARAMETERS
uniform float density;
uniform float gradient;

//TEXTURE ATLAS
uniform float numberOfRows;
uniform vec2 offsetTexture;

//CLIP_PLANE
uniform vec4 clipPlane;

//SHADOWMAP
uniform mat4 toShadowMapSpace;
uniform float shadowDistance;
uniform float transitionDistance;

void main(void){

	vec4 worldPosition = transformationMatrix * vec4(inPosition, 1.0);

	shadowCoords = toShadowMapSpace * worldPosition;

	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	
	vec4 positionCameraSpace = viewMatrix * worldPosition;
	
	gl_Position = projectionMatrix * positionCameraSpace;
	
	outTexCoord = (inTexCoord / numberOfRows) + offsetTexture;	
	
	vec3 normal = inNormal;
	if(fakeLight > 0.5){
		normal = vec3(0,1,0);
	}
	
	outNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	for(int i = 0; i < NUM_LIGHTS; ++i)
		outToLightVector[i] = lightPosition[i] - worldPosition.xyz;
	
	outToCameraVector = (inverse(viewMatrix) * vec4(0,0,0,1)).xyz - worldPosition.xyz;
	
	float distanceFromCamera = length(positionCameraSpace.xyz);
	
	visibilityFog = clamp(exp(-pow(distanceFromCamera * density, gradient)), 0, 1);
	
	distanceFromCamera = distanceFromCamera - (shadowDistance - transitionDistance);
	distanceFromCamera = distanceFromCamera / transitionDistance;
	shadowCoords.w = clamp(1.0 - distanceFromCamera, 0.0,1.0);
}