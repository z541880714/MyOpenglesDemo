#version 300 es
precision mediump float;

in vec4 v_position;
layout (location = 0) out vec4 o_fragColor;
void main() {
    o_fragColor = vec4(0.5, 0.3, 0.4, 1.);
}