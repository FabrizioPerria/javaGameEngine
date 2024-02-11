#version 400 core

#define NUM_LIGHTS 7

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in vec3 tangent;

out vec2 pass_textureCoordinates;
out vec3 toLightVector[NUM_LIGHTS];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPositionEyeSpace[NUM_LIGHTS];

uniform float numberOfRows;
uniform vec2 offset;

const float density = 0;
const float gradient = 5.0;

uniform vec4 plane;

uniform mat4 toShadowMapSpace;
const float shadowDistance = 150.0;
const float transitionDistance = 10.0;

void main(void){
	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	shadowCoords = toShadowMapSpace * worldPosition;
	gl_ClipDistance[0] = dot(worldPosition, plane);
	mat4 modelViewMatrix = viewMatrix * transformationMatrix;
	vec4 positionRelativeToCam = modelViewMatrix * vec4(position,1.0);
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	pass_textureCoordinates = (textureCoordinates/numberOfRows) + offset;
	
	vec3 surfaceNormal = normalize((modelViewMatrix * vec4(normal,0.0)).xyz);
	vec3 norm = normalize(surfaceNormal);
	vec3 tang = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
	vec3 bitang = normalize(cross(norm, tang));
	
	mat3 toTangSpace = mat3(
		tang.x, bitang.x, norm.x,
		tang.y, bitang.y, norm.y,
		tang.z, bitang.z, norm.z
	);
	
	for(int i=0;i<NUM_LIGHTS;i++){
		toLightVector[i] = toTangSpace * (lightPositionEyeSpace[i] - positionRelativeToCam.xyz);
	}
	toCameraVector = toTangSpace * (-positionRelativeToCam.xyz);
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density),gradient));
	visibility = clamp(visibility,0.0,1.0);

    distance = distance - (shadowDistance - transitionDistance);
    distance = distance / transitionDistance;
    
    shadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);
	
}