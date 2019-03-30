#version 330 core

in vec2 iUV;
in vec3 iPosition;

out vec4 frag_color;

uniform float iTime;
uniform sampler2D iTexture;

void main() {
	// texture
	vec2 uv = iUV;
	uv.y *= -1;
	uv.y += 1;

	//uv *= 128;
	//uv = round(uv);
	//uv /= 128;

	vec3 color = texture(iTexture, uv).rgb;

	frag_color = vec4(color,1);
}