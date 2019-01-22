#version 410

layout(location = 0) out vec4 frag_color;
layout(location = 1) out vec3 frag_id;

uniform sampler2D spriteSheet;
uniform vec3 id;
uniform float z;
uniform vec4 highlight;

in vec2 texture_coords;

void main() {
    vec4 sprite = texture(spriteSheet, texture_coords);

    if (sprite.a <= 0)
        discard;

    gl_FragDepth = z; // TODO get world coords

    float part = min(1, dot(sprite.xyz, vec3(.6)));

    vec3 color = mix(sprite.rgb, highlight.xyz, highlight.a * part);

    frag_color = vec4(color, sprite.a);

    if (id != vec3(0))
        frag_id = id;
    else
        frag_id = vec3(1);
}