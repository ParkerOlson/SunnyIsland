#version 400

layout(location = 0) out vec3 frag_color;

uniform sampler2D colorTexture;

vec2 coord2 = gl_FragCoord.xy / vec2(1920, 1080);

void main(void) {
    frag_color = texture(colorTexture, coord2).rgb;
}