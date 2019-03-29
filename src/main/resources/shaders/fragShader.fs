#version 330 core

in vec2 iUV;
in vec3 iPosition;

out vec4 frag_color;

uniform float iTime;
uniform sampler2D iTexture;

// <reference path="shaders/utils.glsl"/> 

void main() {
	// cute snoise
	//float r = snoise(vec3(iPosition.xy*4, iTime*0.5));
	//r = r*0.5 + 0.5;
	//vec3 color = hsv2rgb(vec3(r*4,1,1));

	// texture
	vec2 uv = iUV;
	uv.y *= -1;
	uv.y += 1;

	vec3 color = texture(iTexture, uv).rgb;

	frag_color = vec4(color,1);
}