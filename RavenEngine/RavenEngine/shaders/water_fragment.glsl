#version 400
#define PI 3.141592653589793238462643383279502884197169399375105820974

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

layout(location = 0) out vec3 frag_color;

uniform sampler2D terrainTexture;
uniform sampler2D reflectTexture;
uniform sampler2D depthTexture;

uniform float time = 0.0;

in vec3 water_color, camera_vector;

in vec2 coord;

vec2 coord2 = gl_FragCoord.xy / vec2(1920, 1080);

void main(void) {
    // create a uv map for the water ripples
    float distance_smooth = (1.0 - pow(gl_FragCoord.z, 5));

    float c = cos(-time * 500.0 + coord.y * 1) * .5 + .5;
    float a = cos(time * 2000.0 + coord.y * 10);
    float b = sin(time * 2000.0 + coord.y * 10);
    float f = cos(-time * 500.0 + coord.x * 1) * .5 + .5;
    float d = cos(time * 1000.0 + coord.x * .2);
    float e = sin(time * 1000.0 + coord.x * .2);

    vec4 unprojected = matrix.inverse_projection * vec4(0, 0, gl_FragCoord.z, 1.0);
    float water_camera_depth = (unprojected / unprojected.w).z;

    vec4 depthTextureData = vec4(coord2, texture(depthTexture, coord2).r, 1.0);
    unprojected = matrix.inverse_projection * vec4(0, 0, texture(depthTexture, coord2).r, 1.0);
    float ground_camera_depth = (unprojected / unprojected.w).z;

    float ground_water_depth = water_camera_depth - ground_camera_depth;
//    float ripple_mag = min((camera_depth) * .275, .4);
    float ripple_mag = min((ground_water_depth) * 1.675, .5);

    vec3 water_uv = normalize(vec3(
        mix(d, e, f) * .03 * distance_smooth * ripple_mag,
        2,
        mix(a, b, c) * .06 * distance_smooth * ripple_mag));

    float refract_depth = texture(depthTexture, coord2 + water_uv.xz * ripple_mag).r;
    vec3 refract_color;
    vec4 rful = texture(reflectTexture, coord2 - water_uv.xz * ripple_mag).rgba;

    vec3 reflect_color = mix(water_color, rful.rgb, rful.a / 3);

    if (refract_depth < gl_FragCoord.z) {
        refract_depth = depthTextureData.z;
        refract_color = texture(terrainTexture, coord2).rgb;
//        frag_color = vec3(1.0);
    } else {
        refract_color =
            texture(terrainTexture, coord2 + water_uv.xz * ripple_mag).rgb;
//        frag_color = vec3(0.0);
    }

    vec4 projectedPos = vec4(coord2, refract_depth, 1.0) * 2.0 - 1.0;
    vec4 worldPos = matrix.inverse_projection_view * projectedPos;
    worldPos = worldPos.xyzw / worldPos.w;

    float depth = -worldPos.y;

    refract_color = mix(refract_color, reflect_color, clamp(pow(depth, .5) * .8 + .2, 0.0, 1.0));

    frag_color = (refract_color);

    float waterShininess = 2000;

    vec3 reflected_light_vector = reflect(-light.direction,  water_uv);
    float cosAngle = max(0.0, dot(normalize(camera_vector), reflected_light_vector));
    float specularCoefficient = pow(cosAngle, waterShininess);

    vec3 specularComponent = specularCoefficient * light.color * light.intensity;

    frag_color += max(vec3(0.0), specularComponent);
//    frag_color = vec3(rnful);
}