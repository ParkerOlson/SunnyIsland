#version 400
#define NUM_SAMPLES 4

layout(location = 0) out vec3 frag_light;

uniform sampler2D complexTexture;
uniform sampler2DMS normalTexture;
uniform sampler2DMS depthTexture;

in vec2 coord;

ivec2 int_coord = ivec2(gl_FragCoord.xy);

void main(void) {
    if (texture(complexTexture, coord).r > .002)
        discard;
}