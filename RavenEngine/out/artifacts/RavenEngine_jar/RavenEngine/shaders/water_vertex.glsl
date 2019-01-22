#version 400

layout (std140) uniform DirectionalLight
{
    mat4 view;
    mat4 projection;
    vec3 color;
    float intensity;
    vec3 direction;
    vec3 ambient;
} light;

layout (std140) uniform Matrices
{
    mat4 model;
    mat4 view;
    mat4 reflect_view;
    mat4 inverse_view;
    mat4 projection;
    mat4 inverse_projection;
    mat4 inverse_projection_view;
} matrix;

layout(location = 0) in vec3 vertex_pos;
layout(location = 1) in vec3 vertex_normal;
layout(location = 2) in vec3 vertex_color;

out vec3 water_color, camera_vector;

out vec2 coord;

void main(void)
{
    vec4 world_pos = matrix.model * vec4(vertex_pos, 1.0);
	gl_Position = matrix.projection * matrix.view * world_pos;

    float NdotL = dot(normalize((matrix.view * vec4(0.0, 1.0, 0.0, 0.0)).xyz), normalize(matrix.view * vec4(light.direction, 0.0)).xyz);

    water_color = vertex_color * light.ambient + max(vec3(0.0), vertex_color * max(0.0, NdotL) * light.color * light.intensity);

    camera_vector = matrix.inverse_view[3].xyz - world_pos.xyz;
    coord = (world_pos).xz;
}