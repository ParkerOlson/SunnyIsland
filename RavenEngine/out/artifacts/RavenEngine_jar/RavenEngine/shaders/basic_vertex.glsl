#version 120

attribute vec3 vertex_pos;
attribute vec3 vertex_normal;
attribute vec3 vertex_color;

uniform mat4 M;
uniform mat4 V;
uniform mat4 P;

uniform vec3 light_color = vec3(1.0, .8, .6);
uniform float light_intensity = 1.0;
uniform vec3 light_direction = vec3(1.0, .8, .6);

varying vec3 color;
varying vec3 light;

float light_ambiant = .2;

void main() {
    gl_Position = P * V * M * vec4(vertex_pos, 1.0);

	float NdotL = dot(normalize((V * M * vec4(vertex_normal, 0.0)).xyz), normalize(V * vec4(light_direction, 0.0)).xyz);
	float light_magnitude = max(0.0, NdotL) * (1.0 - light_ambiant);

	light = light_ambiant + light_magnitude * light_color * light_intensity;

	color = vertex_color * light;
}