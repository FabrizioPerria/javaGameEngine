#version 140

in vec2 texCoordsCurrent;
in vec2 texCoordsNext;
in float blendFactorOut;

out vec4 outColor;

// PARTICLE TEXTURE
uniform sampler2D particleTexture;

void main(void){

	vec4 colorCurrent = texture(particleTexture, texCoordsCurrent);
	vec4 colorNext = texture(particleTexture, texCoordsNext);

	outColor = mix(colorCurrent, colorNext, blendFactorOut);

}