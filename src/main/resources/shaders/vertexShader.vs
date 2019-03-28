#version 330 core

layout (location = 0) in vec3 vert_pos;
layout (location = 1) in vec2 vert_uv;

out vec2 iUV;

uniform vec3 iWorldPosition;

void main() {
	
	iUV = vert_uv;

	vec3 pos = vert_pos; 
	
	// position offset
	pos += iWorldPosition;

	// perspective
	gl_Position = vec4(pos, 1);
}