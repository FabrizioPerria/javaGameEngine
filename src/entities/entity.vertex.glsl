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

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPosition[NUM_LIGHTS];

uniform float fakeLight;

uniform float density;
uniform float gradient;

uniform int numberOfRows;
uniform vec2 offsetTexture;

uniform vec4 clipPlane;

void main(void)  {
	vec4 worldPosition = transformationMatrix * vec4(inPosition, 1.0);
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	vec4 positionCameraSpace = viewMatrix * worldPosition;
	
	gl_Position = projectionMatrix * positionCameraSpace;
	
    passTextureCoords = (inTexCoord/numberOfRows) + offsetTexture;
    
    vec3 finalNormal = inNormal;
    if (fakeLight > 0.5)
    	finalNormal = vec3(0,1,0);
    
    surfaceNormal = (transformationMatrix * vec4(finalNormal, 0)).xyz;
    for(int i = 0; i < NUM_LIGHTS; i++) {
    	toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }
    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
    
    float distance = length(positionCameraSpace.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}
