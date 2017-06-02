#version 140

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

uniform float contrastAmount;

void main(void){

	out_Colour = texture(colourTexture, textureCoords);
	
	float red = 0.299 * out_Colour.r;
	float green = 0.587 * out_Colour.g;
	float blue = 0.114 * out_Colour.b;
	
	float total = red + green + blue;
	
	out_Colour = vec4(total, total, total, 1);
}