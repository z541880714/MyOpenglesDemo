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

uniform float u_time;

out vec2 v_position;
out vec2 v_velocity;
out float v_size;
out float v_curtime;
out float v_lifetime;

void main() {
    float seed = u_time;
    float lifetime = a_curtime + a_lifetime - u_time;

    if (lifetime <= 0.0) {
        v_position = vec2(0.0, -0.9);
        v_velocity = a_velocity;
        v_size = 20.;
        v_curtime = u_time;
        v_lifetime = 5000.0;
    } else {
        v_position = a_position + a_velocity * 0.1;
        v_velocity = a_velocity;
        v_size = a_size;
        v_curtime = a_curtime;
        v_lifetime = a_lifetime;
    }

    gl_Position = vec4(v_position, 0.0, 1.0);
}