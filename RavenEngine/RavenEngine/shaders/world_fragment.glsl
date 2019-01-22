#version 400

layout(location = 0) out vec4 frag_color;
layout(location = 1) out vec4 frag_normal;
layout(location = 2) out vec3 frag_id;

uniform vec3 id;

in vec3 color, normal;

void main(void) {
    frag_color = vec4(color, 1.0);
    frag_normal = vec4((normalize(normal).xyz) * .5 + .5, 0.0);
	frag_id = id;
}