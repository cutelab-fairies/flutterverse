#version 330 core

in vec2 iUV;
in vec3 iPosition;

out vec4 frag_color;

uniform float iTime;

// <reference path="shaders/utils.glsl"/> 

void main() {

	//vec3 color = vec3(1);
	float r = snoise(vec3(iPosition.xy*4, iTime));
	vec3 color = vec3(r*0.5 + 0.5);

	frag_color = vec4(color,1);

}