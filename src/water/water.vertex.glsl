#version 400 core

#define NUM_LIGHTS 7

in vec2 position;

out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector;
out vec3 fromLightVector[NUM_LIGHTS];

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;
uniform vec3 lightPosition[NUM_LIGHTS];

const float tiling = 6.0;

void main(void) {
	vec4 worldPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);

	clipSpace = projectionMatrix * viewMatrix * worldPosition;
	gl_Position = clipSpace;

 	textureCoords = ((position.xy/2) + 0.5) * tiling;
 	
 	toCameraVector = cameraPosition - worldPosition.xyz;
 	
 	for(int i = 0; i < NUM_LIGHTS; i++) {
    	fromLightVector[i] = worldPosition.xyz- lightPosition[i];
    }
}