#version 300 es

#define ATTRIBUTE_POSITION      0
#define ATTRIBUTE_VELOCITY      1
#define ATTRIBUTE_SIZE          2
#define ATTRIBUTE_CURTIME       3
#define ATTRIBUTE_LIFETIME      4

layout (location = ATTRIBUTE_POSITION) in vec2 a_position;
layout (location = ATTRIBUTE_VELOCITY) in vec2 a_velocity;
layout (location = ATTRIBUTE_SIZE) in float a_size;
layout (location = ATTRIBUTE_CURTIME) in float a_curtime;
layout (location = ATTRIBUTE_LIFETIME) in float a_lifetime;

void main() {
    gl_PointSize = 3.;
    gl_Position = vec4(a_position, 0.0, 1.0);
}