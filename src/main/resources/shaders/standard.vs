#version 330 core

layout (location = 0) in vec3 vert_pos;
layout (location = 1) in vec2 vert_uv;

out vec2 iUV;
out vec3 iPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
	iUV = vert_uv;
	iPosition = vert_pos; 

	//float polygonJitter = 8;
	//iPosition.xyz *= polygonJitter;
	//iPosition.xyz = round(iPosition.xyz);
	//iPosition.xyz /= polygonJitter;

	gl_Position = projection*view*model*vec4(iPosition,1);
}