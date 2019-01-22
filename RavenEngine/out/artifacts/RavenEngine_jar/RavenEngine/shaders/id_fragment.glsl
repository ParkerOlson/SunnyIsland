#version 400

layout(location = 0) out vec3 frag_id;

uniform sampler2DMS idTexture;

uniform ivec2 coord;

void main(void) {
	frag_id = texelFetch(idTexture, coord, 0).rgb;
}