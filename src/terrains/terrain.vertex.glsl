#version 400 core

#define NUM_LIGHTS 7

in vec3 inPosition;
in vec2 inTexCoord;
in vec3 inNormal;

out vec2 passTextureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[NUM_LIGHTS];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPosition[NUM_LIGHTS];

uniform float density;
uniform float gradient;

uniform vec4 clipPlane;

uniform mat4 toShadowMapSpace;
const float shadowDistance = 150.0;
const float transitionDistance = 10.0;

void main(void)  {
	vec4 worldPosition = transformationMatrix * vec4(inPosition, 1.0);

	shadowCoords = toShadowMapSpace * worldPosition;
	
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	
	vec4 positionCameraSpace = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionCameraSpace;
	
    passTextureCoords = inTexCoord;
    
    surfaceNormal = (transformationMatrix * vec4(inNormal, 0)).xyz;
    for(int i = 0; i < NUM_LIGHTS; i++) {
    	toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }
    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
        
    float distance = length(positionCameraSpace.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
    
    distance = distance - (shadowDistance - transitionDistance);
    distance = distance / transitionDistance;
    
    shadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);
}
