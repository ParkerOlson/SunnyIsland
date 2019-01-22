#version 400

layout(location = 0) in vec3 vertex_pos;

out vec2 coord;

void main(void) {
	gl_Position = vec4(vertex_pos, 1.0);

	coord = vec2((vertex_pos.xy + 1.0) / 2.0);
}