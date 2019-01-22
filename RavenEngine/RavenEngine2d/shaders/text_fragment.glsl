#version 410

layout(location = 0) out vec4 frag_color;

uniform sampler2D spriteSheet;

in vec2 texture_coords;
in float depth;

void main() {
    vec4 sprite = texture(spriteSheet, texture_coords);

    if (sprite.a <= 0)
        discard;

    frag_color = sprite;
}