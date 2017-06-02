#version 140

in vec2 position;
in mat4 modelViewMatrix;
in vec4 AtlasOffsets;	//xy are current, zw are next
in float blendFactor;

out vec2 texCoordsCurrent;
out vec2 texCoordsNext;
out float blendFactorOut;

// MATRICES
uniform mat4 projectionMatrix; 

// TEXTURE INFO
uniform float numberOfRows;

void main(void){
	vec2 texCoords = position + vec2(0.5);
	texCoords.y = 1.0 - texCoords.y;
	texCoords /= numberOfRows;
	
	texCoordsCurrent = texCoords + AtlasOffsets.xy;
	texCoordsNext = texCoords + AtlasOffsets.zw;
	
	blendFactorOut = blendFactor;
	
	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}