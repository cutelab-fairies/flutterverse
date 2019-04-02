#version 330 core

layout (location = 0) in vec3 vert_pos;
layout (location = 1) in vec2 vert_uv;

out vec2 iUV;
out vec3 iLocalPosition; // local space vertex pos
out vec4 iWorldPosition; // local space vertex pos

uniform mat4 model; // model pos/rot
uniform mat4 view; // camera pos/rot
uniform mat4 projection; // perspective

void main() {
	iUV = vert_uv;
	iLocalPosition = vert_pos; // local space

	mat4 worldCamera = projection*view;
	vec4 iWorldPosition = model*vec4(iLocalPosition,1);

	// float polygonJitter = 128;
	// iWorldPosition.xyz *= polygonJitter;
	// iWorldPosition.xyz = round(iWorldPosition.xyz);
	// iWorldPosition.xyz /= polygonJitter;

	gl_Position = worldCamera*iWorldPosition;
}