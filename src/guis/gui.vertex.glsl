#version 400 core

in vec2 inPosition;

out vec2 textureCoords;

//MATRICES
uniform mat4 transformationMatrix;

void main(void){
	gl_Position = transformationMatrix * vec4(inPosition, 0.0, 1.0);
	textureCoords = vec2((inPosition.x + 1.0) / 2.0, 1 - (inPosition.y + 1.0) / 2.0);
}