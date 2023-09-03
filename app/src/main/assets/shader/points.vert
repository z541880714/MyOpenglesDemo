#version 300 es
layout (location = 0) in vec4 a_position;
layout (location = 1) in vec4 a_color;
out vec4 v_color;
out vec4 v_position;
void main() {
    v_color = a_color;
    v_position = a_position;
    gl_PointSize = 10.0;
    gl_Position = a_position;
}