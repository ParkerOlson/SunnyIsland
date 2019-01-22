#version 400

layout(location = 0) in vec3 vertex_pos;

void main(void) {
	gl_Position = vec4(vertex_pos, 1.0);
}