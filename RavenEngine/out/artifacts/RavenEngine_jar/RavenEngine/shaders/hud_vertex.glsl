#version 400

layout(location = 0) in vec3 vertex_pos;

const uniform vec2 size = vec2(1920, 1080);
const uniform vec2 scale = vec2(0);
const uniform vec2 offset = vec2(0);
const uniform float z = 0;
const uniform int style = 0; // 0 center, 1 bottom,

out vec2 texture_coords;

void main(void) {
    texture_coords = vec2(vertex_pos.xy + 1) * vec2(1, -1) / 2;

    switch (style) {
    case 1: // bottom
        gl_Position = vec4(vertex_pos.xy * scale / size - vec2(0, 1) + offset / size, z, 1.0);
        break;
    case 2: // left
        texture_coords += .5 / scale * vec2(1, -1); // not sure why this is needed...
        gl_Position = vec4(vertex_pos.xy * scale / size - vec2(1, 0) + offset * 2.0  / size, z, 1.0);
        break;
    case 0: // center
    default:
        texture_coords += .5 / scale * vec2(1, -1); // not sure why this is needed...
        gl_Position = vec4(vertex_pos.xy * scale / size + offset * 2.0 / size, z, 1.0);
        break;
    }
}