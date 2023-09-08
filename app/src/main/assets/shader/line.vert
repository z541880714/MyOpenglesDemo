#version 300 es
layout (location = 0) in vec4 a_position;
out vec4 v_position;
void main() {
    v_position = a_position;
    gl_PointSize = 2.0;
    gl_Position = a_position;
}