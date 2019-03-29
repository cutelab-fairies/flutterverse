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

	//gl_Position = vec4(vert_pos,1);
	gl_Position = projection*view*model*vec4(vert_pos,1);
}