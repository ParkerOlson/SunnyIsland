#version 400

layout(location = 0) out vec4 frag_color;
layout(location = 1) out vec3 frag_id;

uniform vec4 color;

const uniform bool useText = false;
uniform sampler2D text;
uniform vec3 id;

in vec2 texture_coords;

void main(void) {
    if (useText) {
        frag_color = texture(text, texture_coords);
    } else {
        frag_color = color;
    }

    frag_id = id;
}