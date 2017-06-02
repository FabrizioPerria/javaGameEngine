#version 400 core

in vec3 textureCoords;
out vec4 out_Color;

// SKY CUBEMAPS
uniform samplerCube cubeMapDay;
uniform samplerCube cubeMapNight;

// DAY/NIGHT CYCLE
uniform float blendFactor;

// FOG PARAMETERS
uniform float lowerLimit;
uniform float upperLimit;
uniform vec3 fogColor;

void main(void){
	vec4 dayTexture = texture(cubeMapDay, textureCoords);
    vec4 nightTexture = texture(cubeMapNight, textureCoords);
	
	vec4 final_Color = mix(dayTexture, nightTexture, blendFactor);
	
	float visibility = clamp((textureCoords.y - lowerLimit ) / (upperLimit - lowerLimit), 0.0, 1.0);
	
	out_Color = mix(vec4(fogColor, 1.0), final_Color, visibility);
}