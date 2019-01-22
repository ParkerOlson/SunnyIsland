#version 400

#define BLOCKER_SEARCH_NUM_SAMPLES 8 // redundent
#define BLOCKER_SEARCH_NUM_SAMPLES 16
#define PCF_NUM_SAMPLES 16
#define LIGHT_WORLD_SIZE .035
#define LIGHT_FRUSTUM_WIDTH 40
#define LIGHT_FRUSTUM_HEIGHT light.length
#define LIGHT_SIZE_UV LIGHT_WORLD_SIZE / vec2(LIGHT_FRUSTUM_WIDTH, LIGHT_FRUSTUM_HEIGHT)

layout (std140) uniform DirectionalLight
{
    mat4 view;
    mat4 projection;
    vec3 color;
    float intensity;
    vec3 direction;
    float length;
    vec3 ambient;
    float shadow_transparency;
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

layout(location = 0) out vec3 frag_light;

uniform sampler2D colorTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

uniform sampler2D shadowTexture;

in vec2 coord;

int shadowSampleCount = 4;

const vec2 poissonDisk[16] = vec2[16](
    vec2( -0.94201624, -0.39906216 ),
    vec2( 0.94558609, -0.76890725 ),
    vec2( -0.094184101, -0.92938870 ),
    vec2( 0.34495938, 0.29387760 ),
    vec2( -0.91588581, 0.45771432 ),
    vec2( -0.81544232, -0.87912464 ),
    vec2( -0.38277543, 0.27676845 ),
    vec2( 0.97484398, 0.75648379 ),
    vec2( 0.44323325, -0.97511554 ),
    vec2( 0.53742981, -0.47373420 ),
    vec2( -0.26496911, -0.41893023 ),
    vec2( 0.79197514, 0.19090188 ),
    vec2( -0.24188840, 0.99706507 ),
    vec2( -0.81409955, 0.91437590 ),
    vec2( 0.19984126, 0.78641367 ),
    vec2( 0.14383161, -0.14100790 )
);

float PenumbraSize(float zReceiver, float zBlocker) //Parallel plane estimation
{
    return (zReceiver - zBlocker) / zBlocker;
}

void FindBlocker(out float avgBlockerDepth, out float numBlockers, vec2 uv, float zReceiver )
{
    //This uses similar triangles to compute what
    //area of the shadow map we should search
//    vec2 searchWidth = LIGHT_SIZE_UV * (zReceiver - NEAR_PLANE) / zReceiver;
    vec2 searchWidth = LIGHT_SIZE_UV * (zReceiver) / zReceiver;
    float blockerSum = 0;
    numBlockers = 0;
    for( int i = 0; i < BLOCKER_SEARCH_NUM_SAMPLES; ++i )
    {
        float shadowMapDepth = texture(shadowTexture, uv + poissonDisk[i] * searchWidth).z;

        if ( shadowMapDepth < zReceiver ) {
            blockerSum += shadowMapDepth;
            numBlockers++;
        }
    }

    avgBlockerDepth = blockerSum / numBlockers;
}

float PCF_Filter(vec2 uv, float zReceiver, vec2 filterRadiusUV)
{
    float sum = 0.0f;
    for ( int i = 0; i < PCF_NUM_SAMPLES; ++i )
    {
        vec2 offset = poissonDisk[i] * filterRadiusUV;
        sum += texture(shadowTexture, uv + offset).z > zReceiver ? 1.0 : 0.0;
    }
    return sum / PCF_NUM_SAMPLES;
}

float PCSS (sampler2D shadowMapTex, vec4 coords)
{
    vec2 uv = coords.xy;
    float zReceiver = coords.z; // Assumed to be eye-space z in this code

    // STEP 1: blocker search
    float avgBlockerDepth = 0;
    float numBlockers = 0;
    FindBlocker( avgBlockerDepth, numBlockers, uv, zReceiver );
    if( numBlockers < 1 )
        //There are no occluders so early out (this saves filtering)
        return 1.0f;

    // STEP 2: penumbra size
    float penumbraRatio = PenumbraSize(zReceiver, avgBlockerDepth);

//    vec2 filterRadiusUV = penumbraRatio * LIGHT_SIZE_UV * NEAR_PLANE / coords.z;
    vec2 filterRadiusUV = penumbraRatio * LIGHT_SIZE_UV / coords.z;

    // STEP 3: filtering
    return PCF_Filter( uv, zReceiver, filterRadiusUV );
}
// http://developer.download.nvidia.com/whitepapers/2008/PCSS_Integration.pdf

void main(void) {
    // unpack data
    vec4 ct = texture(colorTexture, coord).rgba;
    vec3 color = ct.rgb;

    vec4 nt = texture(normalTexture, coord).rgba;
    vec3 normal = nt.xyz * 2.0 - 1.0;

    float depth = texture(depthTexture, coord).r;
    vec3 coord2 = vec3(coord, depth) * 2.0 - 1.0;
    vec4 projectedPos = vec4(coord2, 1f);

    vec4 worldPos = matrix.inverse_projection_view * projectedPos;
    worldPos = worldPos.xyzw / worldPos.w;

    vec4 shadowCoord = (light.projection * light.view * worldPos.xyzw) * .5 + .5;

    // check if lit
    float percentage = PCSS(shadowTexture, shadowCoord);

    // light
	float NdotL = max(0.0,
	    dot(
	        normalize((matrix.view * vec4(normal, 0.0)).xyz),
	        normalize(matrix.view * vec4(light.direction, 0.0)).xyz));

    percentage = mix(NdotL, percentage, 1.0 - NdotL * light.shadow_transparency * 2.0);

    frag_light = max(vec3(0.0), color * (light.ambient + light.color * light.intensity * NdotL * percentage));
}