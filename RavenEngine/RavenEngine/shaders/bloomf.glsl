#version 400

layout(location = 0) out vec3 frag_glow;

uniform sampler2DMS glowTexture;

uniform vec2 bloomStep;

in vec2 coord;

void main(void) {
	vec3 glow = texelFetch(glowTexture, ivec2(coord), 0).rgb;
	vec3 bloomGlow = glow / 3.0;

	//for (int i = 1; i < 5; i++) {
		//bloomGlow += (texelFetch(glowTexture, coord + bloomStep * i * 2, 0).rgb +
		 //             texelFetch(glowTexture, coord - bloomStep * i* 2, 0).rgb) / (4 * i);
	//}

    frag_glow = glow; // bloomGlow;
}