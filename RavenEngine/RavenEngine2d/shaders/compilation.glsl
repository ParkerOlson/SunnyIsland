#version 410

layout(location = 0) out vec4 frag_color;
layout(location = 1) out vec3 frag_id;

uniform sampler2D colorTexture;
uniform sampler2D idTexture;
uniform sampler2D depthTexture;

in vec2 texture_coords;

void main() {
    vec4 color = texture(colorTexture, texture_coords);
    vec3 id = texture(idTexture, texture_coords).rgb;
    float depth = texture(depthTexture, texture_coords).r;

    if (color.a <= 0)
        discard;

    gl_FragDepth = depth;
    frag_color = color;
//    frag_color = vec4(vec3(color.a), 1);
    frag_id = id;
}