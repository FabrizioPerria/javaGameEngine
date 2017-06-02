#version 400 core

in vec2 inPosition;
in vec2 inTexCoord;

out vec2 passTexCoord;

// POSITION OF QUAD
uniform vec2 translation;

void main(void){
	gl_Position = vec4(inPosition + translation * vec2(2.0, -2.0), 0, 1);
	
	passTexCoord = inTexCoord;
}