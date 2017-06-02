#version 400 core

in vec2 textureCoords;
out vec4 color;

//TEXTURES
uniform sampler2D textureSampler;

void main(void){
	color = texture(textureSampler, textureCoords);
	if(color.a < 0.5)
		discard;
}