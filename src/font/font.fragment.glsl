#version 400 core

in vec2 passTexCoord;

out vec4 color;

// TEXT ATTRIBUTES
uniform vec3 fontColor;

// FONT ATLAS
uniform sampler2D fontAtlas;

// DISTANCE FIELDS
uniform float width;
uniform float edgeTransition;

// TEXT EFFECTS
uniform float borderWidth;
uniform float borderEdge;
uniform vec3 outlineColor;

uniform vec2 offsetForShadow;

void main(void){
	vec4 textureColor = texture(fontAtlas, passTexCoord);
	float distance = 1.0 - textureColor.a;
	float alpha = 1.0 - smoothstep(width, width + edgeTransition, distance); 

	textureColor = texture(fontAtlas, passTexCoord + offsetForShadow);
	float distance2 = 1.0 - textureColor.a;
	float alphaOutline = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2); 
	
	float overallAlpha = alpha + (1-alpha) * alphaOutline;
	vec3 overallColor = mix(outlineColor, fontColor, alpha / overallAlpha);
	
	color = vec4(overallColor, overallAlpha);
}