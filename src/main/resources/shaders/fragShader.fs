#version 330 core

in vec2 iUV;

out vec4 frag_color;

void main() {

	//vec3 color = vec3(1);
	vec3 color = vec3(iUV,0);

	frag_color = vec4(color,1);

}