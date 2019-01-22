#version 400

layout (std140) uniform Matrices
{
    mat4 model;
    mat4 view;
    mat4 reflect_view;
    mat4 inverse_view;
    mat4 projection;
    mat4 inverse_projection;
} matrix;

layout(location = 0) in vec3 vertex_pos;
layout(location = 1) in vec3 vertex_normal;
layout(location = 2) in vec3 vertex_color;

out vec3 color, normal;

void main(void)
{
	gl_Position = matrix.projection * matrix.view * matrix.model * vec4(vertex_pos, 1.0);

	color = vertex_color;
	normal = normalize((matrix.model * vec4(vertex_normal, 0.0)).xyz);
}