#version 400

layout (std140) uniform DirectionalLight
{
    mat4 view;
    mat4 projection;
    vec3 color;
    float intensity;
    vec3 direction;
    vec3 ambient;
} sunLight;

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

out vec3 light, lightGlow, color, normal;
out float gl_ClipDistance[1];

void main(void)
{
	gl_Position = matrix.projection * matrix.reflect_view * matrix.model * vec4(vertex_pos, 1.0);

	float ambiantLight = .2;

	normal = normalize((matrix.model * vec4(vertex_normal, 0.0)).xyz);

//	vec3 n = normalize(max(vec3(normal.x, 0, normal.y), normal));

	float NdotL = dot(normalize((matrix.reflect_view * matrix.model * vec4(normal, 0.0)).xyz), normalize(matrix.reflect_view * vec4(sunLight.direction, 0.0)).xyz);
	float light_magnitude = max(0.0, NdotL) * .8;

	light = ambiantLight + light_magnitude * sunLight.color * sunLight.intensity;

	color = vertex_color * light;


    gl_ClipDistance[0] = dot(matrix.model * vec4(vertex_pos, 1.0), vec4(0, 1, 0, 0));
}