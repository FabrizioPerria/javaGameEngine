#version 400 core

#define NUM_LIGHTS 4

in vec3 inPosition;
in vec2 inTexCoord;
in vec3 inNormal;
in vec3 inTangent;

out vec2 outTexCoord;
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
	
	mat4 modelViewMatrix = viewMatrix * transformationMatrix;
	vec4 positionCameraSpace = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionCameraSpace;
	
	outTexCoord = (inTexCoord / numberOfRows) + offsetTexture;
	
	vec3 outNormal = (modelViewMatrix * vec4(inNormal,0.0)).xyz;
	
	vec3 norm = normalize(outNormal);
	vec3 tang = normalize((modelViewMatrix * vec4(inTangent, 0.0)).xyz);
	vec3 biTang = normalize(cross(norm, tang));
	
	mat3 toTangentSpace = mat3(
		tang.x, biTang.x, norm.x,
		tang.y, biTang.y, norm.y,
		tang.z, biTang.z, norm.z
	);
	
	for(int i = 0; i < NUM_LIGHTS; i++){
		outToLightVector[i] = toTangentSpace * (lightPosition[i] - positionCameraSpace.xyz);
	}
	outToCameraVector = toTangentSpace * (-positionCameraSpace.xyz);
	
	float distanceFromCamera = length(positionCameraSpace.xyz);
	visibilityFog = clamp(exp(-pow(distanceFromCamera * density, gradient)), 0, 1);
	
	distanceFromCamera = distanceFromCamera - (shadowDistance - transitionDistance);
	distanceFromCamera = distanceFromCamera / transitionDistance;
	shadowCoords.w = clamp(1.0 - distanceFromCamera, 0.0,1.0);
}