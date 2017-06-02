#version 150

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

void main(void){
	vec4 color = texture(colourTexture, textureCoords);
	float brightness = (color.r * 0.2126) + (color.g * 0.7152) + (color.b * 0.0722);	//LUMA conversion
	
	out_Colour = color * brightness;
	
	/*float threshold = 0.7;
	if(brightness > threshold)
		out_Colour = color;
	else
		out_Colour = vec4(0);*/
}