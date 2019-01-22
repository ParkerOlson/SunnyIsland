#version 400

layout(location = 0) out vec4 frag_color;

uniform sampler2D highlight;

in vec2 coord;

void main() {
    frag_color = texture2D(highlight, coord).rgba;
}
