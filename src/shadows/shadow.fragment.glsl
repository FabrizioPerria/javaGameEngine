#version 330

in vec2 texCoord;

out vec4 out_colour;

uniform sampler2D modelTexture;

void main(void){
	float alpha = texture(modelTexture, texCoord).a;
	if(alpha < 0.5)
		discard;
	
	out_colour = vec4(1.0);
	
}