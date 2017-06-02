#version 150

#define KERNEL_SIZE 11

in vec2 position;

out vec2 blurTextureCoords[KERNEL_SIZE];

uniform float targetHeight;

void main(void){
	gl_Position = vec4(position, 0.0, 1.0);
	vec2 centerTexCoords = position * 0.5 + 0.5;
	float pixelSize = 1.0 / targetHeight;
	
	for(int i = -5; i <= 5; ++i){
		blurTextureCoords[i + 5] = centerTexCoords + vec2(0, pixelSize * i);
	}
}