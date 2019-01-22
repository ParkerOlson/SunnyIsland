#version 400
#define NUM_SAMPLES 4

layout (std140) uniform DirectionalLight
{
    vec3 color;
    float intensity;
    vec3 direction;
} light;

layout (std140) uniform Matrices
{
    mat4 model;
    mat4 view;
    mat4 reflect_view;
    mat4 inverse_view;
    mat4 projection;
    mat4 inverse_projection;
} matrix;

layout(location = 0) out vec3 frag_light;

uniform sampler2DMS colorTexture;
uniform sampler2DMS normalTexture;
uniform sampler2DMS depthTexture;

in vec2 coord;

ivec2 int_coord = ivec2(gl_FragCoord.xy);

void main(void) {
    vec4 ct = texelFetch(colorTexture, int_coord, 0).rgba;
    vec3 color = ct.rgb;

    vec4 nt = texelFetch(normalTexture, int_coord, 0).rgba;
    vec3 normal = nt.xyz * 2.0 - 1.0;

    float depth = texelFetch(depthTexture, int_coord, 0).r;

	float NdotL = dot(normalize((matrix.view * vec4(normal, 0.0)).xyz), normalize(matrix.view * vec4(light.direction, 0.0)).xyz);

    frag_light = color * max(0.0, NdotL) * light.color;

//    frag_light = vec3(0);
}