#version 400
#define NUM_SAMPLES 4

layout(location = 0) out vec4 frag_color;
layout(location = 1) out vec4 frag_normal;
layout(location = 2) out vec3 frag_id;
layout(location = 3) out float frag_complex;

uniform vec3 id;

in vec3 color, normal;

const int sampleMask = (1 << NUM_SAMPLES) - 1;

void main(void) {
    frag_color = vec4(color, 0.0);
    frag_normal = vec4((normalize(normal).xyz) * .5 + .5, 0.0);

	frag_id = id;

    frag_complex = (gl_SampleMaskIn[0] != sampleMask) ? 1.0 : 0.0;
}